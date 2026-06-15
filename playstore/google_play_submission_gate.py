#!/usr/bin/env python3
"""Google Play submission gate for Android projects.

This is intentionally stricter than a build audit. It checks whether the
project can plausibly be submitted to Google Play internal testing, including
external prerequisites that local Gradle builds cannot prove.
"""

from __future__ import annotations

import argparse
import json
import re
import struct
import zipfile
from pathlib import Path
from typing import Any


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8-sig")
    except UnicodeDecodeError:
        return path.read_text(encoding="utf-8-sig", errors="replace")
    except FileNotFoundError:
        return ""


def latest_file(paths: list[Path]) -> Path | None:
    existing = [path for path in paths if path.exists()]
    if not existing:
        return None
    return max(existing, key=lambda path: path.stat().st_mtime)


def parse_gradle_value(text: str, key: str) -> str | None:
    match = re.search(rf"\b{re.escape(key)}\s*=\s*([^\n]+)", text)
    if not match:
        return None
    return match.group(1).strip().strip('"')


def check_keystore(root: Path) -> dict[str, Any]:
    path = root / "keystore.properties"
    text = read_text(path)
    try:
        raw = path.read_bytes()
    except FileNotFoundError:
        raw = b""
    has_utf8_bom = raw.startswith(b"\xef\xbb\xbf")
    required = ["storeFile", "storePassword", "keyAlias", "keyPassword"]
    present = {key: bool(re.search(rf"^{re.escape(key)}\s*=\s*\S+", text, re.MULTILINE)) for key in required}
    store_file_match = re.search(r"^storeFile\s*=\s*(\S+)", text, re.MULTILINE)
    store_file = store_file_match.group(1) if store_file_match else ""
    store_file_path = root / store_file if store_file else None
    store_file_exists = bool(store_file_path and store_file_path.exists())
    return {
        "path": str(path),
        "exists": path.exists(),
        "storeFile": store_file,
        "storeFileExists": store_file_exists,
        "hasUtf8Bom": has_utf8_bom,
        "requiredFieldsPresent": present,
        "ready": path.exists() and not has_utf8_bom and all(present.values()) and store_file_exists,
    }


def check_screenshots(root: Path, reference_artifact: Path | None = None) -> dict[str, Any]:
    screenshot_dir = root / "playstore" / "screenshots"
    screenshot_readme = read_text(screenshot_dir / "README.md")
    screenshot_plan = read_text(root / "playstore" / "screenshot_plan.md")
    screenshots = sorted(
        path for path in screenshot_dir.glob("*")
        if path.suffix.lower() in {".png", ".jpg", ".jpeg", ".webp"}
    ) if screenshot_dir.exists() else []
    dimensions = {path.name: png_size(path) for path in screenshots if path.suffix.lower() == ".png"}
    invalid_dimensions = [
        name for name, size in dimensions.items()
        if size is None or min(size) < 320 or max(size) > 3840 or max(size) / min(size) > 2.2
    ]
    reference_mtime = reference_artifact.stat().st_mtime if reference_artifact and reference_artifact.exists() else None
    stale_files = [
        path.name for path in screenshots
        if reference_mtime is not None and path.stat().st_mtime < reference_mtime
    ]
    draft_source = (
        "draft" in screenshot_readme.lower()
        or "no Android device or emulator" in screenshot_plan
        or "layout rehearsal" in screenshot_readme.lower()
    )
    separator_review_required = bool(stale_files) or draft_source
    return {
        "path": str(screenshot_dir),
        "count": len(screenshots),
        "files": [str(path) for path in screenshots],
        "dimensions": dimensions,
        "invalidDimensions": invalid_dimensions,
        "staleAgainstReference": stale_files,
        "draftSource": draft_source,
        "separatorReviewRequired": separator_review_required,
        "ready": len(screenshots) >= 2 and not invalid_dimensions,
    }


def png_size(path: Path) -> tuple[int, int] | None:
    try:
        with path.open("rb") as handle:
            header = handle.read(24)
    except FileNotFoundError:
        return None
    if len(header) < 24 or header[:8] != b"\x89PNG\r\n\x1a\n":
        return None
    return struct.unpack(">II", header[16:24])


def check_graphic_assets(root: Path) -> dict[str, Any]:
    graphics_dir = root / "playstore" / "graphics"
    app_icon = graphics_dir / "app_icon_512.png"
    feature_graphic = graphics_dir / "feature_graphic_1024x500.png"
    app_icon_size = png_size(app_icon)
    feature_size = png_size(feature_graphic)
    return {
        "path": str(graphics_dir),
        "appIcon": str(app_icon),
        "appIconSize": app_icon_size,
        "featureGraphic": str(feature_graphic),
        "featureGraphicSize": feature_size,
        "ready": app_icon_size == (512, 512) and feature_size == (1024, 500),
    }


def check_release_aab_signature(aab_path: Path | None) -> dict[str, Any]:
    if not aab_path or not aab_path.exists():
        return {
            "path": str(aab_path) if aab_path else "",
            "exists": False,
            "hasManifest": False,
            "signatureFiles": [],
            "certificateFiles": [],
            "ready": False,
        }
    try:
        with zipfile.ZipFile(aab_path) as archive:
            names = archive.namelist()
    except zipfile.BadZipFile:
        return {
            "path": str(aab_path),
            "exists": True,
            "hasManifest": False,
            "signatureFiles": [],
            "certificateFiles": [],
            "ready": False,
            "error": "Not a valid ZIP/AAB archive.",
        }
    signature_files = [
        name for name in names
        if name.upper().startswith("META-INF/") and name.upper().endswith(".SF")
    ]
    certificate_files = [
        name for name in names
        if name.upper().startswith("META-INF/")
        and name.upper().endswith((".RSA", ".DSA", ".EC"))
    ]
    has_manifest = "META-INF/MANIFEST.MF" in [name.upper() for name in names]
    return {
        "path": str(aab_path),
        "exists": True,
        "hasManifest": has_manifest,
        "signatureFiles": signature_files,
        "certificateFiles": certificate_files,
        "ready": has_manifest and bool(signature_files) and bool(certificate_files),
    }


def read_json(path: Path) -> dict[str, Any]:
    text = read_text(path)
    if not text:
        return {}
    try:
        parsed = json.loads(text)
    except json.JSONDecodeError:
        return {}
    return parsed if isinstance(parsed, dict) else {}


def check_artifact_version_freshness(
    root: Path,
    latest_debug_apk: Path | None,
    latest_aab: Path | None,
    version_name: str | None,
    version_code: str | None,
    build_gradle: Path,
) -> dict[str, Any]:
    metadata_path = root / "app" / "build" / "outputs" / "apk" / "debug" / "output-metadata.json"
    metadata = read_json(metadata_path)
    elements = metadata.get("elements") if isinstance(metadata.get("elements"), list) else []
    first_element = elements[0] if elements and isinstance(elements[0], dict) else {}
    metadata_version_name = str(first_element.get("versionName", ""))
    metadata_version_code = str(first_element.get("versionCode", ""))
    metadata_output_file = str(first_element.get("outputFile", ""))
    expected_name = str(version_name or "")
    expected_code = str(version_code or "")

    stale_against_build_gradle: list[str] = []
    release_input_paths = [
        build_gradle,
        root / "release_config.properties",
        root / "keystore.properties",
    ]
    existing_release_inputs = [path for path in release_input_paths if path.exists()]
    newest_release_input_mtime = 0.0
    if existing_release_inputs:
        newest_release_input_mtime = max(path.stat().st_mtime for path in existing_release_inputs)
    stale_against_release_inputs: list[str] = []
    try:
        build_mtime = build_gradle.stat().st_mtime
    except FileNotFoundError:
        build_mtime = 0
    for artifact in [latest_debug_apk, latest_aab, metadata_path if metadata_path.exists() else None]:
        if artifact and artifact.exists() and build_mtime and artifact.stat().st_mtime < build_mtime:
            stale_against_build_gradle.append(str(artifact))
        if (
            artifact
            and artifact.exists()
            and newest_release_input_mtime
            and artifact.stat().st_mtime < newest_release_input_mtime
        ):
            stale_against_release_inputs.append(str(artifact))

    metadata_matches = (
        bool(expected_name)
        and bool(expected_code)
        and metadata_version_name == expected_name
        and metadata_version_code == expected_code
    )
    output_file_matches = bool(
        latest_debug_apk
        and latest_debug_apk.exists()
        and metadata_output_file
        and latest_debug_apk.name == metadata_output_file
    )
    ready = (
        metadata_path.exists()
        and metadata_matches
        and output_file_matches
        and bool(latest_debug_apk)
        and bool(latest_aab)
        and not stale_against_build_gradle
        and not stale_against_release_inputs
    )
    return {
        "metadataPath": str(metadata_path),
        "metadataExists": metadata_path.exists(),
        "expectedVersionName": expected_name,
        "expectedVersionCode": expected_code,
        "metadataVersionName": metadata_version_name,
        "metadataVersionCode": metadata_version_code,
        "metadataOutputFile": metadata_output_file,
        "debugApk": str(latest_debug_apk) if latest_debug_apk else "",
        "aab": str(latest_aab) if latest_aab else "",
        "metadataMatchesGradle": metadata_matches,
        "outputFileMatches": output_file_matches,
        "staleAgainstBuildGradle": stale_against_build_gradle,
        "releaseInputFiles": [str(path) for path in existing_release_inputs],
        "staleAgainstReleaseInputs": stale_against_release_inputs,
        "ready": ready,
    }


def check_version_documentation(root: Path, version_name: str | None, version_code: str | None) -> dict[str, Any]:
    docs = [
        root / "GOOGLE_PLAY_RELEASE_PLAN_V0_8_1.md",
        root / "playstore" / "play_console_submission_fields.md",
        root / "playstore" / "google_play_official_requirements_2026.md",
        root / "playstore" / "release_ops_action_plan_2026-06-11.md",
    ]
    missing: list[str] = []
    stale: list[str] = []
    stale_current_status: list[str] = []
    version_token = str(version_name)
    code_token = str(version_code)
    for path in docs:
        text = read_text(path)
        if not path.exists():
            missing.append(str(path))
            continue
        if not version_token or not code_token or version_token not in text or code_token not in text:
            stale.append(str(path))
            continue
        current_patterns = {
            "GOOGLE_PLAY_RELEASE_PLAN_V0_8_1.md": (
                rf"(?:褰撳墠鐗堟湰锛歚{re.escape(version_token)}`\s*/\s*`versionCode {re.escape(code_token)}`"
                rf"|Version advanced to `{re.escape(version_token)}\s*/\s*versionCode {re.escape(code_token)}`)"
            ),
            "play_console_submission_fields.md": rf"Current release checkpoint:\s*`{re.escape(version_token)}\s*/\s*versionCode {re.escape(code_token)}`",
            "google_play_official_requirements_2026.md": (
                rf"(?:-\s*鐗堟湰锛歚{re.escape(version_token)}\s*/\s*versionCode {re.escape(code_token)}`"
                rf"|Current release checkpoint:\s*`{re.escape(version_token)}\s*/\s*versionCode {re.escape(code_token)}`)"
            ),
            "release_ops_action_plan_2026-06-11.md": rf"Release baseline:\s*`{re.escape(version_token)}\s*/\s*versionCode {re.escape(code_token)}`",
        }
        pattern = current_patterns.get(path.name)
        if pattern and not re.search(pattern, "\n".join(text.splitlines()[:120])):
            stale_current_status.append(str(path))
    return {
        "files": [str(path) for path in docs],
        "missing": missing,
        "stale": stale,
        "staleCurrentStatus": stale_current_status,
        "ready": not missing and not stale and not stale_current_status,
    }


def check_localized_store_listings(root: Path) -> dict[str, Any]:
    required = ["zh-CN", "zh-TW", "en", "ja", "ko", "es"]
    listing_root = root / "playstore" / "localized"
    files: dict[str, str] = {}
    missing: list[str] = []
    incomplete: list[str] = []
    for locale in required:
        path = listing_root / locale / "store_listing.md"
        files[locale] = str(path)
        text = read_text(path)
        if not path.exists():
            missing.append(locale)
            continue
        if (
            "## Short Description" not in text
            or "## Full Description" not in text
            or "## Release Notes" not in text
            or has_todo(text)
            or "\ufffd" in text
        ):
            incomplete.append(locale)
    return {
        "path": str(listing_root),
        "required": required,
        "files": files,
        "missing": missing,
        "incomplete": incomplete,
        "ready": not missing and not incomplete,
    }


def check_billing_backend_skeleton(root: Path) -> dict[str, Any]:
    backend_dir = root / "backend" / "billing-verify-worker"
    worker = backend_dir / "worker.js"
    readme = backend_dir / "README.md"
    deployment = root / "playstore" / "billing_backend_deployment.md"
    text = "\n".join(read_text(path) for path in [worker, readme, deployment])
    return {
        "path": str(backend_dir),
        "workerExists": worker.exists(),
        "readmeExists": readme.exists(),
        "deploymentDocExists": deployment.exists(),
        "hasVerifyEndpoint": "/billing/verify" in text,
        "hasGoogleApiCall": "androidpublisher.googleapis.com" in text,
        "hasSecretsDoc": "GOOGLE_SERVICE_ACCOUNT_EMAIL" in text and "GOOGLE_PRIVATE_KEY" in text,
        "ready": worker.exists()
        and readme.exists()
        and deployment.exists()
        and "/billing/verify" in text
        and "androidpublisher.googleapis.com" in text
        and "GOOGLE_SERVICE_ACCOUNT_EMAIL" in text
        and "GOOGLE_PRIVATE_KEY" in text,
    }


def check_privacy_site_package(root: Path) -> dict[str, Any]:
    privacy_dir = root / "playstore" / "privacy_site"
    index = privacy_dir / "index.html"
    headers = privacy_dir / "_headers"
    readme = privacy_dir / "README.md"
    text = read_text(index)
    required_locales = {
        "en": ['id="en"', 'lang="en"'],
        "zh-CN": ['id="zh-CN"', 'lang="zh-CN"'],
        "zh-TW": ['id="zh-TW"', 'lang="zh-TW"'],
        "ja": ['id="ja"', 'lang="ja"'],
        "ko": ['id="ko"', 'lang="ko"'],
        "es": ['id="es"', 'lang="es"'],
    }
    mojibake_markers = [
        "\ufffd",
        "\u951f",
        "\u95bf",
        "Espa\u752f",
        "relaci\u8d10",
        "ubicaci\u8d10",
    ]
    locales_present = {
        locale: all(token in text for token in tokens)
        for locale, tokens in required_locales.items()
    }
    found_mojibake = [marker for marker in mojibake_markers if marker in text]
    has_billing_disclosure = (
        "Google Play Billing" in text
        and "purchase token" in text
        and "backend verification" in text
    )
    has_permission_disclosure = (
        "does not request precise location" in text
        and "does not force arrival verification" in text
    )
    has_content_source_disclosure = "does not scrape third-party platforms" in text
    has_placeholder_contact_warning = (
        "Production publisher name and support email must be inserted" in text
        and "Do not submit the app with placeholder contact information" in text
    )
    return {
        "path": str(privacy_dir),
        "indexExists": index.exists(),
        "headersExists": headers.exists(),
        "readmeExists": readme.exists(),
        "localesPresent": locales_present,
        "foundMojibake": found_mojibake,
        "hasBillingDisclosure": has_billing_disclosure,
        "hasNoSensitivePermissionDisclosure": has_permission_disclosure,
        "hasContentSourceDisclosure": has_content_source_disclosure,
        "hasPlaceholderContactWarning": has_placeholder_contact_warning,
        "ready": index.exists()
        and headers.exists()
        and readme.exists()
        and all(locales_present.values())
        and not found_mojibake
        and has_billing_disclosure
        and has_permission_disclosure
        and has_content_source_disclosure
        and has_placeholder_contact_warning
        and not has_todo(text),
    }


def check_in_app_data_deletion(root: Path) -> dict[str, Any]:
    privacy_screen = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "ui" / "screens" / "PrivacyScreen.kt"
    view_model = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "TodayPlayViewModel.kt"
    repository = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "data" / "QuestRepository.kt"
    store = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "data" / "QuestHistoryStore.kt"
    privacy_text = read_text(privacy_screen)
    view_model_text = read_text(view_model)
    repository_text = read_text(repository)
    store_text = read_text(store)
    return {
        "privacyScreenExists": privacy_screen.exists(),
        "viewModelHasClear": "clearLocalHistory" in view_model_text,
        "repositoryHasClear": "clearHistory" in repository_text,
        "storeHasClear": "clearRecords" in store_text and "KEY_RECORDS" in store_text,
        "uiCallsClear": "onClearLocalHistory" in privacy_text,
        "ready": privacy_screen.exists()
        and "onClearLocalHistory" in privacy_text
        and "clearLocalHistory" in view_model_text
        and "clearHistory" in repository_text
        and "clearRecords" in store_text
        and "KEY_RECORDS" in store_text,
    }


def check_billing_purchase_guard(root: Path) -> dict[str, Any]:
    gateway = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "billing" / "PlayBillingGateway.kt"
    shop_screen = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "ui" / "screens" / "ShopScreen.kt"
    locale_file = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "localization" / "TodayPlayLocale.kt"
    text = read_text(gateway)
    shop_text = read_text(shop_screen)
    locale_text = read_text(locale_file)
    has_shop_disabled_state = (
        "paymentsEnabled" in shop_text
        and "BuildConfig.BILLING_VERIFY_ENDPOINT" in shop_text
        and "enabled = paymentsEnabled" in shop_text
        and "paymentUnavailable" in shop_text
    )
    return {
        "path": str(gateway),
        "gatewayExists": gateway.exists(),
        "hasEndpointCheck": "isVerificationEndpointConfigured()" in text,
        "hasHttpsRequirement": 'startsWith("https://")' in text,
        "guardBeforeLaunch": text.find("isVerificationEndpointConfigured()") < text.find("launchBillingFlow") if "launchBillingFlow" in text else False,
        "hasShopDisabledState": has_shop_disabled_state,
        "hasPaymentUnavailableCopy": "paymentUnavailable" in locale_text,
        "ready": gateway.exists()
        and "isVerificationEndpointConfigured()" in text
        and 'startsWith("https://")' in text
        and text.find("isVerificationEndpointConfigured()") < text.find("launchBillingFlow")
        and has_shop_disabled_state
        and "paymentUnavailable" in locale_text,
    }


def check_map_and_mock_media_guard(root: Path) -> dict[str, Any]:
    map_navigator = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "navigation" / "MapNavigator.kt"
    result_screen = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "ui" / "screens" / "QuestResultScreen.kt"
    system_copy = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "localization" / "SystemCopy.kt"
    result_copy = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "localization" / "ResultCopy.kt"
    map_text = read_text(map_navigator)
    result_text = read_text(result_screen)
    system_text = read_text(system_copy)
    result_copy_text = read_text(result_copy)
    has_amap_first = "setPackage(\"com.autonavi.minimap\")" in map_text
    has_browser_fallback = "fallbackIntent" in map_text and "strings.mapOpenedInBrowser" in map_text
    has_clipboard_fallback = (
        "ClipboardManager" in map_text
        and "strings.mapUnavailableCopiedAddress" in map_text
        and "strings.destinationAddressLabel" in map_text
    )
    has_localized_map_copy = all(token in system_text for token in [
        "mapOpenedInBrowser",
        "mapUnavailableCopiedAddress",
        "destinationAddressLabel",
        "English",
        "Japanese",
        "Korean",
        "Spanish",
    ])
    has_photo_placeholder = "copy.photoUploadDisabled" in result_text
    has_data_availability_notice = (
        "DataAvailabilityNotice" in result_text
        and "copy.dataAvailabilityTitle" in result_text
        and "copy.needsLiveGlobalSearch" in result_text
        and "dataAvailabilityTitle" in result_copy_text
        and "needsLiveGlobalSearch" in result_copy_text
    )
    photo_placeholder_index = result_text.find("copy.photoUploadDisabled")
    following_text = result_text[photo_placeholder_index:photo_placeholder_index + 160] if photo_placeholder_index >= 0 else ""
    photo_does_not_check_in = "onCheckIn" not in following_text and "enabled = false" in following_text
    return {
        "mapNavigatorExists": map_navigator.exists(),
        "hasAmapFirst": has_amap_first,
        "hasBrowserFallback": has_browser_fallback,
        "hasClipboardFallback": has_clipboard_fallback,
        "hasLocalizedMapCopy": has_localized_map_copy,
        "hasPhotoPlaceholder": has_photo_placeholder,
        "hasDataAvailabilityNotice": has_data_availability_notice,
        "photoDoesNotCheckIn": photo_does_not_check_in,
        "ready": map_navigator.exists()
        and result_screen.exists()
        and has_amap_first
        and has_browser_fallback
        and has_clipboard_fallback
        and has_localized_map_copy
        and has_data_availability_notice
        and has_photo_placeholder
        and photo_does_not_check_in,
    }


def check_travel_content_api_boundary(root: Path) -> dict[str, Any]:
    contract = root / "playstore" / "travel_content_backend_contract.md"
    repository = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "data" / "TravelContentRepository.kt"
    generator = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app" / "generator" / "LocalItineraryGenerator.kt"
    contract_text = read_text(contract).lower()
    repository_text = read_text(repository)
    generator_text = read_text(generator)
    required_terms = [
        "no client-side scraping",
        "official api",
        "licensed",
        "contentsource",
        "sourcepolicy",
        "requiresofficialverification",
    ]
    terms_present = {term: term in contract_text for term in required_terms}
    return {
        "contractExists": contract.exists(),
        "repositoryExists": repository.exists(),
        "hasInterface": "interface TravelContentRepository" in repository_text,
        "hasLocalMockRepository": "LocalMockTravelContentRepository" in repository_text,
        "generatorUsesInterface": "TravelContentRepository" in generator_text
        and "contentRepository.searchPois" in generator_text,
        "termsPresent": terms_present,
        "ready": contract.exists()
        and repository.exists()
        and "interface TravelContentRepository" in repository_text
        and "LocalMockTravelContentRepository" in repository_text
        and "TravelContentRepository" in generator_text
        and "contentRepository.searchPois" in generator_text
        and all(terms_present.values()),
    }


def has_todo(text: str) -> bool:
    lowered = text.lower()
    return "todo:" in lowered or "support@example.com" in lowered or "example.com" in lowered


def parse_markdown_field(text: str, label: str) -> str:
    match = re.search(rf"^-\s*{re.escape(label)}:\s*`([^`]*)`", text, re.MULTILINE)
    return match.group(1).strip() if match else ""


def parse_java_string_literal(value: str) -> str:
    return (
        value
        .replace(r"\\", "\\")
        .replace(r"\"", '"')
        .replace(r"\n", "\n")
        .replace(r"\t", "\t")
    )


def read_release_build_config(root: Path) -> dict[str, Any]:
    known_path = (
        root / "app" / "build" / "generated" / "source" / "buildConfig"
        / "release" / "com" / "todayplay" / "app" / "BuildConfig.java"
    )
    candidates = [known_path]
    generated_root = root / "app" / "build" / "generated" / "source" / "buildConfig" / "release"
    if generated_root.exists():
        candidates.extend(generated_root.glob("**/BuildConfig.java"))
    path = latest_file(candidates)
    text = read_text(path) if path else ""
    values: dict[str, str] = {}
    for key, raw_value in re.findall(r'public static final String\s+([A-Z0-9_]+)\s*=\s*"((?:\\.|[^"\\])*)";', text):
        values[key] = parse_java_string_literal(raw_value)
    return {
        "path": str(path) if path else str(known_path),
        "exists": bool(path and path.exists()),
        "values": values,
    }


def is_real_email(value: str) -> bool:
    lowered = value.lower()
    return (
        bool(re.fullmatch(r"[^@\s`]+@[^@\s`]+\.[^@\s`]+", value))
        and "todo" not in lowered
        and "example." not in lowered
    )


def is_public_https_url(value: str) -> bool:
    lowered = value.lower()
    return value.startswith("https://") and "todo" not in lowered and "example." not in lowered


def audit(project_root: Path) -> dict[str, Any]:
    root = project_root.resolve()
    build_gradle = root / "app" / "build.gradle.kts"
    build_text = read_text(build_gradle)
    playstore = root / "playstore"

    latest_aab = latest_file(list((root / "app" / "build" / "outputs" / "bundle" / "release").glob("*.aab")))
    latest_debug_apk = latest_file(list((root / "app" / "build" / "outputs" / "apk" / "debug").glob("*.apk")))
    submission_fields = playstore / "play_console_submission_fields.md"
    data_safety = playstore / "data_safety_draft.md"
    review_notes = playstore / "google_play_review_notes.md"
    billing_contract = playstore / "backend_billing_contract.md"
    release_build_config = read_release_build_config(root)
    release_build_values = release_build_config["values"]
    endpoint = release_build_values.get("BILLING_VERIFY_ENDPOINT", "")
    if not endpoint:
        endpoint_match = re.search(r'BILLING_VERIFY_ENDPOINT"\s*,\s*"\\"([^"]*)\\""', build_text)
        endpoint = endpoint_match.group(1) if endpoint_match else ""

    checks: list[dict[str, Any]] = []

    def add_check(name: str, status: str, evidence: str, action: str = "") -> None:
        checks.append({
            "name": name,
            "status": status,
            "evidence": evidence,
            "action": action,
        })

    version_name = parse_gradle_value(build_text, "versionName")
    version_code = parse_gradle_value(build_text, "versionCode")
    target_sdk = parse_gradle_value(build_text, "targetSdk")
    compile_sdk = parse_gradle_value(build_text, "compileSdk")

    add_check(
        "Versioning",
        "pass" if version_name and version_code else "fail",
        f"versionName={version_name}, versionCode={version_code}",
        "Set versionName and increment versionCode before every Play upload.",
    )
    add_check(
        "Target SDK",
        "pass" if target_sdk and int(target_sdk) >= 35 else "fail",
        f"compileSdk={compile_sdk}, targetSdk={target_sdk}",
        "Keep targetSdk aligned with current Google Play requirements.",
    )
    add_check(
        "Release AAB",
        "pass" if latest_aab else "fail",
        str(latest_aab) if latest_aab else "No release AAB found.",
        "Run bundleRelease and upload the AAB, not the debug APK.",
    )
    aab_signature = check_release_aab_signature(latest_aab)
    add_check(
        "Release AAB signature",
        "pass" if aab_signature["ready"] else "fail",
        (
            f"path={aab_signature['path']}, "
            f"hasManifest={aab_signature['hasManifest']}, "
            f"signatureFiles={aab_signature['signatureFiles']}, "
            f"certificateFiles={aab_signature['certificateFiles']}"
        ),
        "Verify the release AAB contains META-INF signature files before uploading to Play Console.",
    )
    add_check(
        "Debug APK",
        "warn" if latest_debug_apk else "pass",
        str(latest_debug_apk) if latest_debug_apk else "No debug APK found.",
        "Use debug APK only for local testing, not Play submission.",
    )
    artifact_freshness = check_artifact_version_freshness(
        root,
        latest_debug_apk,
        latest_aab,
        version_name,
        version_code,
        build_gradle,
    )
    add_check(
        "Artifact version freshness",
        "pass" if artifact_freshness["ready"] else "fail",
        (
            f"debugMetadataExists={artifact_freshness['metadataExists']}, "
            f"expected={artifact_freshness['expectedVersionName']}/{artifact_freshness['expectedVersionCode']}, "
            f"debugMetadata={artifact_freshness['metadataVersionName']}/{artifact_freshness['metadataVersionCode']}, "
            f"metadataOutputFile={artifact_freshness['metadataOutputFile']!r}, "
            f"outputFileMatches={artifact_freshness['outputFileMatches']}, "
            f"staleAgainstBuildGradle={artifact_freshness['staleAgainstBuildGradle']}, "
            f"releaseInputFiles={artifact_freshness['releaseInputFiles']}, "
            f"staleAgainstReleaseInputs={artifact_freshness['staleAgainstReleaseInputs']}"
        ),
        "Rebuild APK/AAB after version, signing, or release-config changes; debug output metadata must match Gradle version.",
    )

    keystore = check_keystore(root)
    add_check(
        "Upload key",
        "pass" if keystore["ready"] else "fail",
        (
            f"keystore.properties exists={keystore['exists']}, "
            f"storeFile={keystore['storeFile']!r}, storeFileExists={keystore['storeFileExists']}, "
            f"hasUtf8Bom={keystore['hasUtf8Bom']}, "
            f"fields={keystore['requiredFieldsPresent']}"
        ),
        "Create an upload key, configure keystore.properties without UTF-8 BOM, and enable Play App Signing.",
    )

    submission_text = read_text(submission_fields)
    contact_email = parse_markdown_field(submission_text, "Contact email")
    privacy_policy_url = parse_markdown_field(submission_text, "Privacy policy URL")
    app_support_email = release_build_values.get("SUPPORT_EMAIL", "")
    app_privacy_policy_url = release_build_values.get("PRIVACY_POLICY_URL", "")
    add_check(
        "Play Console contact email",
        "pass" if submission_fields.exists() and is_real_email(contact_email) else "fail",
        f"Contact email={contact_email!r}" if submission_fields.exists() else "Missing play_console_submission_fields.md",
        "Set a real developer support email in Play Console and play_console_submission_fields.md.",
    )
    add_check(
        "Play Console privacy URL",
        "pass" if submission_fields.exists() and is_public_https_url(privacy_policy_url) else "fail",
        f"Privacy policy URL={privacy_policy_url!r}" if submission_fields.exists() else "Missing play_console_submission_fields.md",
        "Host playstore/privacy_site on a public HTTPS URL and put that URL in Play Console.",
    )
    app_support_matches = (
        is_real_email(app_support_email)
        and is_real_email(contact_email)
        and app_support_email.lower() == contact_email.lower()
    )
    app_privacy_matches = (
        is_public_https_url(app_privacy_policy_url)
        and is_public_https_url(privacy_policy_url)
        and app_privacy_policy_url == privacy_policy_url
    )
    add_check(
        "App support and privacy config",
        "pass" if release_build_config["exists"] and app_support_matches and app_privacy_matches else "fail",
        (
            f"releaseBuildConfigExists={release_build_config['exists']}, "
            f"releaseBuildConfig={release_build_config['path']}, "
            f"SUPPORT_EMAIL={app_support_email!r}, "
            f"PRIVACY_POLICY_URL={app_privacy_policy_url!r}, "
            f"matchesPlayFields={{'supportEmail': {app_support_matches}, 'privacyPolicyUrl': {app_privacy_matches}}}"
        ),
        "Set SUPPORT_EMAIL and PRIVACY_POLICY_URL in release_config.properties to match Play Console fields, then rebuild.",
    )

    screenshots = check_screenshots(root, latest_aab)
    graphics = check_graphic_assets(root)
    localized_listings = check_localized_store_listings(root)
    version_documentation = check_version_documentation(root, version_name, version_code)
    billing_backend_skeleton = check_billing_backend_skeleton(root)
    privacy_site = check_privacy_site_package(root)
    in_app_data_deletion = check_in_app_data_deletion(root)
    billing_purchase_guard = check_billing_purchase_guard(root)
    map_and_mock_media_guard = check_map_and_mock_media_guard(root)
    travel_content_api_boundary = check_travel_content_api_boundary(root)
    add_check(
        "Store graphic assets",
        "pass" if graphics["ready"] else "fail",
        (
            f"app_icon_512.png={graphics['appIconSize']}, "
            f"feature_graphic_1024x500.png={graphics['featureGraphicSize']}"
        ),
        "Provide a 512x512 app icon and 1024x500 feature graphic for Play Console.",
    )
    add_check(
        "Store screenshots",
        "warn" if screenshots["ready"] and screenshots["staleAgainstReference"] else ("pass" if screenshots["ready"] else "fail"),
        (
            f"{screenshots['count']} screenshot(s) found in {screenshots['path']}, "
            f"invalidDimensions={screenshots['invalidDimensions']}, "
            f"staleAgainstLatestAab={screenshots['staleAgainstReference']}, "
            f"draftSource={screenshots['draftSource']}, "
            f"visibleQuestionSeparatorReviewRequired={screenshots['separatorReviewRequired']}"
        ),
        "Capture at least 2 current real phone screenshots for Google Play; recapture after meaningful UI changes and reject visible '?' separators or mojibake.",
    )
    add_check(
        "Version documentation sync",
        "pass" if version_documentation["ready"] else "fail",
        (
            f"missing={version_documentation['missing']}, "
            f"stale={version_documentation['stale']}, "
            f"staleCurrentStatus={version_documentation['staleCurrentStatus']}"
        ),
        "Keep release plan, Play submission fields, and official requirements notes aligned with Gradle versionName/versionCode.",
    )
    add_check(
        "Localized store listings",
        "pass" if localized_listings["ready"] else "fail",
        (
            f"required={localized_listings['required']}, "
            f"missing={localized_listings['missing']}, "
            f"incomplete={localized_listings['incomplete']}"
        ),
        "Provide Play listing drafts for zh-CN, zh-TW, en, ja, ko, and es.",
    )
    add_check(
        "Privacy policy site package",
        "pass" if privacy_site["ready"] else "fail",
        (
            f"indexExists={privacy_site['indexExists']}, "
            f"headersExists={privacy_site['headersExists']}, "
            f"readmeExists={privacy_site['readmeExists']}, "
            f"localesPresent={privacy_site['localesPresent']}, "
            f"foundMojibake={privacy_site['foundMojibake']}, "
            f"hasBillingDisclosure={privacy_site['hasBillingDisclosure']}, "
            f"hasPermissionDisclosure={privacy_site['hasNoSensitivePermissionDisclosure']}, "
            f"hasContentSourceDisclosure={privacy_site['hasContentSourceDisclosure']}"
        ),
        "Deploy playstore/privacy_site to a public HTTPS URL and replace publisher/support contact before Play submission.",
    )
    add_check(
        "In-app local data deletion",
        "pass" if in_app_data_deletion["ready"] else "fail",
        (
            f"privacyScreenExists={in_app_data_deletion['privacyScreenExists']}, "
            f"viewModelHasClear={in_app_data_deletion['viewModelHasClear']}, "
            f"repositoryHasClear={in_app_data_deletion['repositoryHasClear']}, "
            f"storeHasClear={in_app_data_deletion['storeHasClear']}"
        ),
        "Provide an in-app privacy/data screen that clears locally stored route history.",
    )
    add_check(
        "Map and mock media guard",
        "pass" if map_and_mock_media_guard["ready"] else "fail",
        (
            f"hasAmapFirst={map_and_mock_media_guard['hasAmapFirst']}, "
            f"hasBrowserFallback={map_and_mock_media_guard['hasBrowserFallback']}, "
            f"hasClipboardFallback={map_and_mock_media_guard['hasClipboardFallback']}, "
            f"hasDataAvailabilityNotice={map_and_mock_media_guard['hasDataAvailabilityNotice']}, "
            f"photoDoesNotCheckIn={map_and_mock_media_guard['photoDoesNotCheckIn']}"
        ),
        "Provide clear map fallback behavior and do not let mock photo upload trigger real check-in state.",
    )
    add_check(
        "Travel content API boundary",
        "pass" if travel_content_api_boundary["ready"] else "fail",
        (
            f"contractExists={travel_content_api_boundary['contractExists']}, "
            f"repositoryExists={travel_content_api_boundary['repositoryExists']}, "
            f"hasInterface={travel_content_api_boundary['hasInterface']}, "
            f"hasLocalMockRepository={travel_content_api_boundary['hasLocalMockRepository']}, "
            f"generatorUsesInterface={travel_content_api_boundary['generatorUsesInterface']}"
        ),
        "Keep global POI/search content behind an official/licensed backend boundary; never ship client scraping as production data.",
    )

    add_check(
        "Data Safety draft",
        "pass" if data_safety.exists() else "fail",
        str(data_safety) if data_safety.exists() else "Missing data_safety_draft.md",
        "Use the draft to fill the Play Console Data Safety form.",
    )
    add_check(
        "Review notes",
        "pass" if review_notes.exists() else "fail",
        str(review_notes) if review_notes.exists() else "Missing google_play_review_notes.md",
        "Provide clear notes about mock data, external maps, and Billing state.",
    )
    add_check(
        "Billing backend endpoint",
        "pass" if endpoint and endpoint.startswith("https://") else "fail",
        (
            f"BILLING_VERIFY_ENDPOINT={endpoint!r}, "
            f"source=release BuildConfig, releaseBuildConfigExists={release_build_config['exists']}"
        ),
        "Set a real HTTPS /billing/verify URL before live paid testing.",
    )
    add_check(
        "Billing purchase guard",
        "pass" if billing_purchase_guard["ready"] else "fail",
        (
            f"gatewayExists={billing_purchase_guard['gatewayExists']}, "
            f"hasEndpointCheck={billing_purchase_guard['hasEndpointCheck']}, "
            f"hasHttpsRequirement={billing_purchase_guard['hasHttpsRequirement']}, "
            f"guardBeforeLaunch={billing_purchase_guard['guardBeforeLaunch']}, "
            f"hasShopDisabledState={billing_purchase_guard['hasShopDisabledState']}"
        ),
        "Block launchBillingFlow while the server verification endpoint is missing or not HTTPS.",
    )
    add_check(
        "Billing backend skeleton",
        "pass" if billing_backend_skeleton["ready"] else "fail",
        (
            f"workerExists={billing_backend_skeleton['workerExists']}, "
            f"deploymentDocExists={billing_backend_skeleton['deploymentDocExists']}, "
            f"hasGoogleApiCall={billing_backend_skeleton['hasGoogleApiCall']}"
        ),
        "Keep backend skeleton deployable and configure it with Google Play service-account secrets.",
    )
    add_check(
        "Billing backend contract",
        "pass" if billing_contract.exists() else "fail",
        str(billing_contract) if billing_contract.exists() else "Missing backend_billing_contract.md",
        "Implement /billing/verify, /entitlements, and /billing/notifications.",
    )

    status_rank = {"pass": 0, "warn": 1, "fail": 2}
    overall = "pass"
    if any(check["status"] == "fail" for check in checks):
        overall = "fail"
    elif any(check["status"] == "warn" for check in checks):
        overall = "warn"

    return {
        "project": str(root),
        "overall": overall,
        "checks": checks,
    }


def print_markdown(result: dict[str, Any]) -> None:
    print("# Google Play Submission Gate")
    print()
    print(f"- Project: `{result['project']}`")
    print(f"- Overall: `{result['overall']}`")
    print()
    print("| Check | Status | Evidence | Required action |")
    print("| --- | --- | --- | --- |")
    for check in result["checks"]:
        print(
            f"| {check['name']} | `{check['status']}` | "
            f"{check['evidence']} | {check['action']} |"
        )


def main() -> None:
    parser = argparse.ArgumentParser(description="Check whether an Android app is ready for Google Play submission.")
    parser.add_argument("project", nargs="?", default=".", help="Android project root")
    parser.add_argument("--json", action="store_true", help="Print JSON output")
    args = parser.parse_args()

    result = audit(Path(args.project))
    if args.json:
        print(json.dumps(result, indent=2, ensure_ascii=False))
    else:
        print_markdown(result)


if __name__ == "__main__":
    main()
