#!/usr/bin/env python3
"""Audit release-time public configuration wiring.

This project keeps production endpoints empty by default. The audit verifies
that release_config.properties can supply public URLs/contact values for CI or
local release builds without committing secrets or fake launch claims.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path


REQUIRED_KEYS = [
    "BILLING_VERIFY_ENDPOINT",
    "TRAVEL_CONTENT_BASE_URL",
    "PRIVACY_POLICY_URL",
    "SUPPORT_EMAIL",
]


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8-sig")
    except FileNotFoundError:
        return ""


def status(value: bool) -> str:
    return "pass" if value else "fail"


def parse_properties(text: str) -> dict[str, str]:
    props: dict[str, str] = {}
    for raw_line in text.splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        props[key.strip()] = value.strip()
    return props


def valid_optional_https(value: str) -> bool:
    return not value or value.startswith("https://")


def valid_optional_email(value: str) -> bool:
    return not value or bool(re.fullmatch(r"[^@\s]+@[^@\s]+\.[^@\s]+", value))


def main() -> int:
    root = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path.cwd().resolve()
    template = root / "release_config.template.properties"
    local_config = root / "release_config.properties"
    gitignore = root / ".gitignore"
    build_gradle = root / "app/build.gradle.kts"
    privacy_screen = root / "app/src/main/java/com/todayplay/app/ui/screens/PrivacyScreen.kt"
    privacy_copy = root / "app/src/main/java/com/todayplay/app/localization/PrivacyCopy.kt"

    template_text = read_text(template)
    local_text = read_text(local_config)
    local_props = parse_properties(local_text)
    build_text = read_text(build_gradle)
    privacy_screen_text = read_text(privacy_screen)
    privacy_copy_text = read_text(privacy_copy)
    gitignore_text = read_text(gitignore)

    configured_values = {key: local_props.get(key, "") for key in REQUIRED_KEYS}

    checks = [
        (
            "Release config template",
            template.exists() and all(f"{key}=" in template_text for key in REQUIRED_KEYS),
            str(template),
            "Provide a committed template for public release URLs and support contact values.",
        ),
        (
            "Local release config ignored",
            "/release_config.properties" in gitignore_text,
            str(gitignore),
            "Do not commit local or CI release configuration values.",
        ),
        (
            "Gradle reads optional release config",
            "release_config.properties" in build_text
            and "releaseConfigValue" in build_text
            and "buildConfigString" in build_text
            and all(f'"{key}"' in build_text for key in REQUIRED_KEYS),
            str(build_gradle),
            "Read public release configuration from release_config.properties with safe empty defaults.",
        ),
        (
            "BuildConfig exposes public launch fields",
            all(f'buildConfigField("String", "{key}"' in build_text for key in REQUIRED_KEYS),
            str(build_gradle),
            "Expose Billing endpoint, travel-content endpoint, privacy URL, and support email through BuildConfig.",
        ),
        (
            "Privacy screen shows configured support fields",
            "BuildConfig.SUPPORT_EMAIL" in privacy_screen_text
            and "BuildConfig.PRIVACY_POLICY_URL" in privacy_screen_text
            and "ContactLine" in privacy_screen_text
            and "TextOverflow.Ellipsis" in privacy_screen_text,
            str(privacy_screen),
            "Show support email and privacy policy URL in the app without breaking small screens.",
        ),
        (
            "Privacy support links are actionable",
            "LocalUriHandler.current" in privacy_screen_text
            and "uriHandler.openUri" in privacy_screen_text
            and "mailto:$it" in privacy_screen_text
            and 'startsWith("https://")' in privacy_screen_text
            and "enabled = configuredSupportEmail != null" in privacy_screen_text
            and "enabled = configuredPrivacyPolicyUrl != null" in privacy_screen_text,
            str(privacy_screen),
            "When configured, users should be able to contact support and open the privacy policy without extra permissions.",
        ),
        (
            "Support copy localized",
            privacy_copy_text.count("supportTitle =") >= 6
            and privacy_copy_text.count("supportEmailLabel =") >= 6
            and privacy_copy_text.count("privacyPolicyLabel =") >= 6
            and privacy_copy_text.count("contactSupport =") >= 6
            and privacy_copy_text.count("openPrivacyPolicy =") >= 6
            and privacy_copy_text.count("notConfigured =") >= 6,
            str(privacy_copy),
            "Localize public support/configuration copy for all launch locales.",
        ),
        (
            "Configured URLs are HTTPS when present",
            valid_optional_https(configured_values["BILLING_VERIFY_ENDPOINT"])
            and valid_optional_https(configured_values["TRAVEL_CONTENT_BASE_URL"])
            and valid_optional_https(configured_values["PRIVACY_POLICY_URL"]),
            str(local_config) if local_config.exists() else "release_config.properties absent",
            "If local release_config.properties is present, URL values must be HTTPS or empty.",
        ),
        (
            "Configured support email is syntactically valid when present",
            valid_optional_email(configured_values["SUPPORT_EMAIL"]),
            str(local_config) if local_config.exists() else "release_config.properties absent",
            "If SUPPORT_EMAIL is present, it must look like a real email address.",
        ),
    ]

    overall = "pass" if all(item[1] for item in checks) else "fail"
    print("# Release Config Audit")
    print()
    print(f"- Project: `{root}`")
    print(f"- Local config present: `{local_config.exists()}`")
    print(f"- Overall: `{overall}`")
    print()
    print("| Check | Status | Evidence | Required action |")
    print("| --- | --- | --- | --- |")
    for name, ok, evidence, action in checks:
        print(f"| {name} | `{status(ok)}` | {evidence} | {action} |")

    return 0 if overall == "pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
