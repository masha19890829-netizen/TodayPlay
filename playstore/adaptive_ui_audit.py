#!/usr/bin/env python3
"""Static adaptive UI audit for TodayPlay Compose screens."""

from __future__ import annotations

import sys
from pathlib import Path


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8-sig")
    except FileNotFoundError:
        return ""


def status(value: bool) -> str:
    return "pass" if value else "fail"


def main() -> int:
    root = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path.cwd().resolve()
    ui_root = root / "app/src/main/java/com/todayplay/app/ui"
    home = ui_root / "screens/HomeScreen.kt"
    splash = ui_root / "screens/SplashScreen.kt"
    create = ui_root / "screens/CreateQuestScreen.kt"
    result = ui_root / "screens/QuestResultScreen.kt"
    shop = ui_root / "screens/ShopScreen.kt"
    components = ui_root / "components/TodayPlayComponents.kt"
    locale = root / "app/src/main/java/com/todayplay/app/localization/TodayPlayLocale.kt"
    type_file = ui_root / "theme/Type.kt"

    home_text = read_text(home)
    splash_text = read_text(splash)
    create_text = read_text(create)
    result_text = read_text(result)
    shop_text = read_text(shop)
    components_text = read_text(components)
    locale_text = read_text(locale)
    type_text = read_text(type_file)

    checks = [
        (
            "Splash tagline fixed line layout",
            "SplashTaglineText(" in splash_text
            and "stableSplashTaglineLines" in splash_text
            and "SimplifiedSplashLine3" in splash_text
            and "TraditionalSplashLine3" in splash_text
            and "softWrap = false" in splash_text
            and "maxLines = 1" in splash_text
            and "widthIn(max = 300.dp)" in splash_text
            and "if (lines.size >= 3)" in splash_text,
            str(splash),
            "Opening subtitle should use fixed short CJK lines instead of allowing a single Chinese character to fall onto its own line.",
        ),
        (
            "Home screen width classes",
            "BoxWithConstraints" in home_text
            and "val wide = maxWidth >= 720.dp" in home_text
            and ("val compact = maxHeight < 720.dp" in home_text or "val compact = maxWidth < 390.dp || maxHeight < 700.dp" in home_text),
            str(home),
            "Home should switch between phone, compact-height, and wide/tablet layouts.",
        ),
        (
            "Home scrollable compact layout",
            "LazyColumn" in home_text
            and ("contentPadding = PaddingValues(pagePadding)" in home_text or "contentPadding = PaddingValues(" in home_text)
            and "horizontalAlignment = Alignment.CenterHorizontally" in home_text,
            str(home),
            "Compact home layout must scroll instead of compressing content.",
        ),
        (
            "V0.9.71 route-card home avoids small-screen text wall",
            "V0971RouteCardHomeExperience" in home_text
            and "V0971WaterfallFeed" in home_text
            and "V0971RouteTicketCard" in home_text
            and "val useTwoColumns = forceTwoColumns && maxWidth >= 360.dp" in home_text
            and "TextOverflow.Ellipsis" in home_text
            and "自己说一句" in home_text
            and "换一幕" in home_text
            and "本地样例 POI" in home_text,
            str(home),
            "The new home should show tappable route cards before explanatory text, keep free-text input secondary, and clamp labels on phone, foldable, and landscape screens.",
        ),
        (
            "V0.9.72 prompt entry does not cover route cards",
            "V0972CompactPromptEntry" in home_text
            and "if (!promptOpen)" in home_text
            and "compact -> 26.dp" in home_text
            and "compact -> 2" in home_text
            and "TP_INTENT_CARD_ID" in home_text,
            str(home),
            "The one-sentence prompt should live inside the scroll feed instead of floating over card titles or actions on phone, foldable, and landscape screens.",
        ),
        (
            "V0.9.73 quiet cards and one-sentence sheet",
            "QuietRouteCard" in home_text
            and "OneSentencePromptSheet" in home_text
            and "item.routeStops.take(2)" in home_text
            and "V0.9.73 quiet-card-start" in home_text
            and "val useTwoColumns = forceTwoColumns && maxWidth >= 360.dp" in home_text,
            str(home),
            "Home route cards should stay visually light, preview only two stops, and use a one-sentence prompt entry without crowding narrow screens.",
        ),
        (
            "Card flow width classes",
            "BoxWithConstraints" in create_text
            and "maxWidth < 360.dp" in create_text
            and "LazyColumn" in create_text,
            str(create),
            "Preference card flow needs small-screen width handling and vertical scrolling.",
        ),
        (
            "Card flow compact choice layout",
            "Modifier.fillMaxWidth() else Modifier.widthIn(min = 132.dp, max = 196.dp)" in create_text
            and "ChoiceCard(" in create_text
            and "maxLines = 2" in create_text,
            str(create),
            "Choice cards should become single-column full-width cards on narrow screens and clamp long labels.",
        ),
        (
            "Step shell compact padding",
            "SoftCard(padding = if (compact) 16.dp else 22.dp)" in create_text
            and "style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium" in create_text,
            str(create),
            "Step cards should reduce padding and title size on narrow screens.",
        ),
        (
            "Button text overflow guards",
            components_text.count("overflow = TextOverflow.Ellipsis") >= 4
            and "maxLines = 1" in components_text,
            str(components),
            "Primary, ghost, chip, and top bar text should not overflow small screens or localized labels.",
        ),
        (
            "Result responsive route layout",
            "BoxWithConstraints" in result_text
            and "maxWidth < 360.dp" in result_text
            and "PaddingValues(horizontal = horizontalPadding" in result_text,
            str(result),
            "Route result list should reduce horizontal padding on narrow phones and foldable cover screens.",
        ),
        (
            "V0.9.73 live result max-width guard",
            "LiveRouteFirstScreen" in result_text
            and "resultMaxWidth" in result_text
            and "val resultCompactHeight = maxHeight < 640.dp" in result_text
            and "widthIn(max = resultMaxWidth)" in result_text
            and "RouteSketchMap(" in result_text
            and "currentRunnableStop" in result_text,
            str(result),
            "Result pages should open with map/current-stop action and constrain content width on foldables, tablets, and landscape screens.",
        ),
        (
            "Result long place text guards",
            "TextOverflow.Ellipsis" in result_text
            and "plan.title" in result_text
            and "stop.poi.name" in result_text
            and result_text.count("maxLines = 2") >= 3,
            str(result),
            "Route titles and POI names should clamp safely for multilingual place names.",
        ),
        (
            "Chat-first personalization text guards",
            "RecentIntentStrip" in home_text
            and "card.tradeoff" in home_text
            and "card.sourceNote" in home_text
            and "compact: Boolean" in home_text
            and "KawaiiChip(text = \"设置\", selected = false, onClick = onSettings)" in home_text
            and "PersonalFitCardV2" in result_text
            and "TodayQuestTicketCover" in result_text
            and "DirectorCutPanel" in home_text
            and "fadeIn(tween(180)) + slideInVertically" in home_text
            and "plan.personalizationReasons" in result_text
            and "currentStrategy" in result_text
            and result_text.count("overflow = TextOverflow.Ellipsis") >= 12,
            str(result),
            "Candidate cards, recent intents, result reasons, and tune labels should clamp safely on small phones and foldable cover screens.",
        ),
        (
            "Shop responsive payment layout",
            "BoxWithConstraints" in shop_text
            and "maxWidth < 360.dp" in shop_text
            and "PaddingValues(horizontal = horizontalPadding" in shop_text,
            str(shop),
            "Payment/shop content should reduce side padding on small screens.",
        ),
        (
            "Shop localized copy overflow guards",
            "TextOverflow.Ellipsis" in shop_text
            and "premiumHeroTitle" in shop_text
            and "PriceLine" in shop_text
            and "PaidPackCard" in shop_text
            and shop_text.count("maxLines = 2") >= 3,
            str(shop),
            "Long translated shop titles, product names, and price notes should not squeeze horizontally.",
        ),
        (
            "Launch locale coverage",
            all(token in locale_text for token in [
                'SimplifiedChinese("zh-CN"',
                'TraditionalChinese("zh-TW"',
                'English("en"',
                'Japanese("ja"',
                'Korean("ko"',
                'Spanish("es"',
            ]),
            str(locale),
            "The app should expose Simplified Chinese, Traditional Chinese, English, Japanese, Korean, and Spanish.",
        ),
        (
            "No viewport-scaled typography",
            "maxWidth.value" not in type_text
            and "LocalConfiguration" not in type_text
            and "letterSpacing = 0.sp" in type_text,
            str(type_file),
            "Typography should not scale font sizes directly from viewport width, and letter spacing should remain neutral.",
        ),
    ]

    overall = "pass" if all(item[1] for item in checks) else "fail"
    print("# Adaptive UI Audit")
    print()
    print(f"- Project: `{root}`")
    print(f"- Overall: `{overall}`")
    print()
    print("| Check | Status | Evidence | Required action |")
    print("| --- | --- | --- | --- |")
    for name, ok, evidence, action in checks:
        print(f"| {name} | `{status(ok)}` | {evidence} | {action} |")

    return 0 if overall == "pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
