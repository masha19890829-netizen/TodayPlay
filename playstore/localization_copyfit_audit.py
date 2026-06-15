#!/usr/bin/env python3
"""Static copy-fit audit for TodayPlay launch locales.

This checks engineering readiness for long localized UI copy. It does not
certify translation quality; native-speaker review is still required.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path


REQUIRED_LOCALES = {
    "zh-CN": "values",
    "zh-TW": "values-zh-rTW",
    "en": "values-en",
    "ja": "values-ja",
    "ko": "values-ko",
    "es": "values-es",
}


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8-sig")
    except FileNotFoundError:
        return ""


def status(value: bool) -> str:
    return "pass" if value else "fail"


def string_count(xml: str) -> int:
    return len(re.findall(r"<string\s+name=", xml))


def main() -> int:
    root = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path.cwd().resolve()
    res_root = root / "app/src/main/res"
    ui_root = root / "app/src/main/java/com/todayplay/app/ui"
    locale_file = root / "app/src/main/java/com/todayplay/app/localization/TodayPlayLocale.kt"
    privacy_file = root / "app/src/main/java/com/todayplay/app/localization/PrivacyCopy.kt"
    history_file = root / "app/src/main/java/com/todayplay/app/localization/HistoryCopy.kt"
    share_file = root / "app/src/main/java/com/todayplay/app/localization/ShareCopy.kt"
    system_file = root / "app/src/main/java/com/todayplay/app/localization/SystemCopy.kt"
    quick_start_file = root / "app/src/main/java/com/todayplay/app/localization/QuickStartCopy.kt"
    result_file = root / "app/src/main/java/com/todayplay/app/localization/ResultCopy.kt"
    generator_file = root / "app/src/main/java/com/todayplay/app/generator/LocalItineraryGenerator.kt"

    locale_text = read_text(locale_file)
    privacy_text = read_text(privacy_file)
    history_copy_text = read_text(history_file)
    share_copy_text = read_text(share_file)
    system_copy_text = read_text(system_file)
    quick_start_copy_text = read_text(quick_start_file)
    result_copy_text = read_text(result_file)
    generator_text = read_text(generator_file)
    home_text = read_text(ui_root / "screens/HomeScreen.kt")
    splash_text = read_text(ui_root / "screens/SplashScreen.kt")
    shop_text = read_text(ui_root / "screens/ShopScreen.kt")
    history_text = read_text(ui_root / "screens/HistoryScreen.kt")
    share_text = read_text(ui_root / "screens/ShareCardScreen.kt")
    quick_start_text = read_text(ui_root / "screens/QuickStartScreen.kt")
    result_text = read_text(ui_root / "screens/QuestResultScreen.kt")
    create_text = read_text(ui_root / "screens/CreateQuestScreen.kt")
    components_text = read_text(ui_root / "components/TodayPlayComponents.kt")
    map_text = read_text(root / "app/src/main/java/com/todayplay/app/navigation/MapNavigator.kt")
    billing_text = read_text(root / "app/src/main/java/com/todayplay/app/billing/PlayBillingGateway.kt")
    main_text = read_text(root / "app/src/main/java/com/todayplay/app/MainActivity.kt")

    checks: list[tuple[str, bool, str, str]] = []

    for code, res_dir in REQUIRED_LOCALES.items():
        xml_path = res_root / res_dir / "strings.xml"
        xml_text = read_text(xml_path)
        checks.append((
            f"Android resources {code}",
            xml_path.exists() and 'name="app_name"' in xml_text and string_count(xml_text) >= 1,
            str(xml_path),
            "Provide app_name and Android resource directory for this launch locale.",
        ))

        listing_path = root / "playstore/localized" / code / "store_listing.md"
        listing_text = read_text(listing_path)
        checks.append((
            f"Store listing {code}",
            listing_path.exists()
            and "## Short Description" in listing_text
            and "## Full Description" in listing_text
            and "## Release Notes" in listing_text,
            str(listing_path),
            "Provide localized Play listing draft for this locale.",
        ))

    checks.extend([
        (
            "In-app locale registry",
            all(token in locale_text for token in [
                'SimplifiedChinese("zh-CN"',
                'TraditionalChinese("zh-TW"',
                'English("en"',
                'Japanese("ja"',
                'Korean("ko"',
                'Spanish("es"',
            ]),
            str(locale_file),
            "Expose all launch locales in the app language selector.",
        ),
        (
            "Localized core screens",
            all("LocalTodayPlayStrings" in text for text in [home_text, splash_text, shop_text])
            and "historyStrings(LocalTodayPlayLocale.current)" in history_text
            and "shareStrings(LocalTodayPlayLocale.current)" in share_text,
            "HomeScreen.kt, SplashScreen.kt, ShopScreen.kt, HistoryScreen.kt, ShareCardScreen.kt",
            "Core entry, monetization, history, and share screens should use localized string registries.",
        ),
        (
            "Localized quick-start screen copy",
            quick_start_file.exists()
            and all(token in quick_start_copy_text for token in [
                "SimplifiedChinese",
                "TraditionalChinese",
                "English",
                "Japanese",
                "Korean",
                "Spanish",
                "quickStartStrings",
                "QuickStartItemCopy",
            ])
            and "quickStartStrings(LocalTodayPlayLocale.current)" in quick_start_text
            and "copy.generate" in quick_start_text
            and "TextOverflow.Ellipsis" in quick_start_text
            and "maxLines = 2" in quick_start_text,
            str(quick_start_file),
            "Quick-start route ideas and CTA copy should be localized and protected against long translated labels.",
        ),
        (
            "Localized result screen shell copy",
            result_file.exists()
            and all(token in result_copy_text for token in [
                "SimplifiedChinese",
                "TraditionalChinese",
                "English",
                "Japanese",
                "Korean",
                "Spanish",
                "resultStrings",
                "ResultStrings",
                "feedbackLabel",
                "sourceLine",
            ])
            and "resultStrings(locale)" in result_text
            and "copy.dataAvailabilityTitle" in result_text
            and "copy.photoUploadDisabled" in result_text
            and "copy.feedbackRecorded(labels)" in result_text
            and "copy.intimacyFor(quest.relationship)" in result_text,
            str(result_file),
            "Result page labels, route-card controls, feedback chips, and availability notices should follow the selected launch locale.",
        ),
        (
            "Localized route POI display metadata",
            all(token in generator_text for token in [
                "fun localizePoi(poi: POI): POI",
                "safePoiName(poi)",
                "safeRecommendationFor(poi)",
                "safeRiskTipsFor(poi)",
                "localizedCityFor",
                "localizedDistrictFor",
                "localizedAddressFor",
                "localizedTagsFor",
                "safeLocalizedSourceLabel",
                "safeLocalizeSourcePolicy",
                "safeCoverageNoteFor",
                "poi.tags.take(2)",
                "tagNamesEn",
                "tagNamesJa",
                "tagNamesKo",
                "tagNamesEs",
            ]),
            str(generator_file),
            "Generated route content should localize selected POI display fields, tags, source labels, and compliance notes, not only the page shell.",
        ),
        (
            "Clean generated route display copy",
            all(token in generator_text for token in [
                "copy.safePlanTypeFor",
                "copy.safeTitleFor",
                "copy.safeRouteSummaryFor",
                "copy.safeEstimateCost",
                "copy.safeCrowdRiskFor",
                "copy.safeRainBackupFor",
                "copy.safeBestPhotoTimeFor",
                "copy.safeRewardPolicy",
                "copy.safeComplianceSummary",
                "copy.safeComplianceLimitations",
                "copy.safeCheckInTitle",
                "copy.safeCheckInDescriptionFor",
                "copy.safePhotoSuggestionFor",
                "copy.safeSpendingSuggestionFor",
                "copy.safeBackupPlanFor",
                "copy.safeWhyForGroup",
            ])
            and "fun safePoiName" in generator_text
            and "fun safeRecommendationFor" in generator_text
            and "fun safeRiskTipsFor" in generator_text,
            str(generator_file),
            "Visible generated route text should use the clean display-copy adapter instead of legacy mojibake-prone literals.",
        ),
        (
            "Localized privacy screen copy",
            all(token in privacy_text for token in [
                "SimplifiedChinese",
                "TraditionalChinese",
                "English",
                "Japanese",
                "Korean",
                "Spanish",
            ]),
            str(privacy_file),
            "Privacy and local-data disclosure should exist for every launch locale.",
        ),
        (
            "Localized history screen copy",
            history_file.exists()
            and all(token in history_copy_text for token in [
                "SimplifiedChinese",
                "TraditionalChinese",
                "English",
                "Japanese",
                "Korean",
                "Spanish",
            ])
            and all(token in history_text for token in [
                "copy.emptyTitle",
                "copy.relationshipLabel",
                "copy.stopCheckInLabel",
                "copy.openHint",
            ]),
            str(history_file),
            "History/library labels and empty state should be localized for every launch locale.",
        ),
        (
            "Localized share screen copy",
            share_file.exists()
            and all(token in share_copy_text for token in [
                "SimplifiedChinese",
                "TraditionalChinese",
                "English",
                "Japanese",
                "Korean",
                "Spanish",
            ])
            and "shareStrings(LocalTodayPlayLocale.current)" in share_text
            and "ShareCompletionCard(" in share_text
            and "labels = copy.completionCard" in share_text
            and all(token in components_text for token in [
                "CompletionCardStrings",
                "labels.completionTitleLabel",
                "labels.hiddenTaskStatus",
            ]),
            str(share_file),
            "Share page, system share text, and completion-card labels should be localized for every launch locale.",
        ),
        (
            "Localized system toasts",
            system_file.exists()
            and all(token in system_copy_text for token in [
                "SimplifiedChinese",
                "TraditionalChinese",
                "English",
                "Japanese",
                "Korean",
                "Spanish",
                "mapOpenedInBrowser",
                "paymentNotOpen",
                "missingRequired",
            ])
            and "MapNavigator.openInAmap(context, stop.navigationAction, systemCopy)" in result_text
            and "strings.mapOpenedInBrowser" in map_text
            and "strings.mapUnavailableCopiedAddress" in map_text
            and "systemStrings(localeProvider())" in billing_text
            and "systemCopy.currentPageCannotPurchase" in main_text
            and "systemCopy.missingRequired(missing)" in create_text,
            str(system_file),
            "Map fallback, payment status, and required-field Toasts should follow the selected launch locale.",
        ),
        (
            "Button copy fit",
            components_text.count("overflow = TextOverflow.Ellipsis") >= 4
            and components_text.count("maxLines = 1") >= 4,
            str(ui_root / "components/TodayPlayComponents.kt"),
            "Repeated buttons, chips, and top-bar actions need single-line overflow guards.",
        ),
        (
            "Shop copy fit",
            "BoxWithConstraints" in shop_text
            and "TextOverflow.Ellipsis" in shop_text
            and shop_text.count("maxLines = 2") >= 3
            and "PriceLine" in shop_text,
            str(ui_root / "screens/ShopScreen.kt"),
            "Product names, subscription notes, and localized purchase buttons should avoid horizontal squeeze.",
        ),
        (
            "Result copy fit",
            "BoxWithConstraints" in result_text
            and "TextOverflow.Ellipsis" in result_text
            and "stop.poi.name" in result_text
            and "plan.title" in result_text,
            str(ui_root / "screens/QuestResultScreen.kt"),
            "Long city, route, and POI names should clamp safely on small screens.",
        ),
    ])

    overall = "pass" if all(item[1] for item in checks) else "fail"
    print("# Localization Copy-Fit Audit")
    print()
    print(f"- Project: `{root}`")
    print(f"- Required locales: `{', '.join(REQUIRED_LOCALES)}`")
    print(f"- Overall: `{overall}`")
    print()
    print("| Check | Status | Evidence | Required action |")
    print("| --- | --- | --- | --- |")
    for name, ok, evidence, action in checks:
        print(f"| {name} | `{status(ok)}` | {evidence} | {action} |")

    return 0 if overall == "pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
