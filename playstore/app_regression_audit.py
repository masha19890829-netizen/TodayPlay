#!/usr/bin/env python3
"""Small app-layer regression audit for release preflight.

This is a source-level guard used until the Android app module has a proper JVM
or instrumentation test suite. It verifies that several high-risk behaviors are
still wired into the app code before packaging.
"""

from __future__ import annotations

import argparse
from pathlib import Path


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return path.read_text(encoding="utf-8", errors="replace")
    except FileNotFoundError:
        return ""


def has_all(text: str, tokens: list[str]) -> bool:
    return all(token in text for token in tokens)


def audit(project_root: Path) -> tuple[list[dict[str, str]], str]:
    root = project_root.resolve()
    app_root = root / "app" / "src" / "main" / "java" / "com" / "todayplay" / "app"

    generator = read_text(app_root / "generator" / "LocalItineraryGenerator.kt")
    route_interpreter = read_text(app_root / "generator" / "RouteIntentInterpreter.kt")
    billing = read_text(app_root / "billing" / "PlayBillingGateway.kt")
    shop = read_text(app_root / "ui" / "screens" / "ShopScreen.kt")
    repository = read_text(app_root / "data" / "QuestRepository.kt")
    store = read_text(app_root / "data" / "QuestHistoryStore.kt")
    map_nav = read_text(app_root / "navigation" / "MapNavigator.kt")
    result_screen = read_text(app_root / "ui" / "screens" / "QuestResultScreen.kt")
    home_screen = read_text(app_root / "ui" / "screens" / "HomeScreen.kt")
    saved_screen = read_text(app_root / "ui" / "screens" / "SavedRoutesScreen.kt")
    splash_screen = read_text(app_root / "ui" / "screens" / "SplashScreen.kt")
    loading_screen = read_text(app_root / "ui" / "screens" / "LoadingScreen.kt")
    ai_generator = read_text(app_root / "generator" / "AiQuestGenerator.kt")
    home_route_content = read_text(app_root / "data" / "HomeRouteContentCatalog.kt")
    components = read_text(app_root / "ui" / "components" / "TodayPlayComponents.kt")
    theme_colors = read_text(app_root / "ui" / "theme" / "Color.kt")
    anime_date_asset = root / "app" / "src" / "main" / "res" / "drawable" / "anime_date_invitation.xml"
    anime_evening_asset = root / "app" / "src" / "main" / "res" / "drawable" / "anime_evening_walk.xml"
    anime_cafe_asset = root / "app" / "src" / "main" / "res" / "drawable" / "anime_cafe_friends.xml"
    anime_ticket_asset = root / "app" / "src" / "main" / "res" / "drawable" / "anime_ticket_sky.xml"
    romantic_hero_asset = root / "app" / "src" / "main" / "res" / "drawable-nodpi" / "romantic_hero.png"
    romantic_date_asset = root / "app" / "src" / "main" / "res" / "drawable-nodpi" / "romantic_date.png"
    romantic_friend_asset = root / "app" / "src" / "main" / "res" / "drawable-nodpi" / "romantic_friend.png"
    romantic_solo_asset = root / "app" / "src" / "main" / "res" / "drawable-nodpi" / "romantic_solo.png"
    romantic_ticket_asset = root / "app" / "src" / "main" / "res" / "drawable-nodpi" / "romantic_ticket.png"
    tp_art_splash_asset = root / "app" / "src" / "main" / "res" / "drawable-nodpi" / "tp_art_splash_companion.webp"
    tp_art_home_asset = root / "app" / "src" / "main" / "res" / "drawable-nodpi" / "tp_art_home_companion.webp"
    tp_art_loading_asset = root / "app" / "src" / "main" / "res" / "drawable-nodpi" / "tp_art_loading_route.webp"
    tp_art_time_cinema_asset = root / "app" / "src" / "main" / "res" / "drawable-nodpi" / "tp_art_time_cinema_route.png"
    share_screen = read_text(app_root / "ui" / "screens" / "ShareCardScreen.kt")
    privacy_screen = read_text(app_root / "ui" / "screens" / "PrivacyScreen.kt")
    view_model = read_text(app_root / "TodayPlayViewModel.kt")
    main_activity = read_text(app_root / "MainActivity.kt")
    quest_models = read_text(app_root / "model" / "QuestModels.kt")
    auth_models = read_text(app_root / "model" / "AuthModels.kt")
    google_gateway = read_text(app_root / "auth" / "GoogleAccountGateway.kt")
    locale_copy = read_text(app_root / "localization" / "TodayPlayLocale.kt")
    privacy_copy = read_text(app_root / "localization" / "PrivacyCopy.kt")
    build_gradle = read_text(root / "app" / "build.gradle.kts")
    release_config_template = read_text(root / "release_config.template.properties")
    ai_worker = read_text(root / "backend" / "ai-route-gateway" / "worker.js")
    all_app_sources = "\n".join(
        read_text(path)
        for path in app_root.rglob("*.kt")
    )

    checks: list[dict[str, str]] = []

    def add(name: str, ok: bool, evidence: str, action: str) -> None:
        checks.append({
            "name": name,
            "status": "pass" if ok else "fail",
            "evidence": evidence,
            "action": action,
        })

    add(
        "V0.9.70 cinematic personalization and adaptive text version metadata",
        'versionName = "0.9.70"' in build_gradle and "versionCode = 87" in build_gradle,
        "expected versionName=0.9.70 and versionCode=87",
        "Keep every external-test APK versioned independently so testers never install an ambiguous build.",
    )

    add(
        "Screen and locale survive foldable resize",
        "rememberSaveable { mutableStateOf(AppScreen.Splash) }" in main_activity
        and "rememberSaveable { mutableStateOf(TodayPlayLocale.SimplifiedChinese) }" in main_activity,
        "App screen and locale use rememberSaveable instead of plain remember.",
        "Keep configuration changes from dropping users back to the opening screen during foldable, landscape, and split-screen QA.",
    )

    chat_first_tokens = [
        "ChatFirstHomeExperience",
        "今天想怎么玩？",
        "生成今天的玩法",
        "CandidateRouteCard",
        "RouteIntentInterpreter.interpret",
        "RouteIntentInterpreter.buildCandidateCards",
        "为你生成了",
        "再改一句",
        "RouteTuneCard",
        "onTuneRoute",
    ]
    add(
        "Chat-first home and route tuning path",
        has_all(home_screen + result_screen + main_activity, chat_first_tokens),
        f"tokens={chat_first_tokens}",
        "Keep the primary home path as a single AI-style prompt that generates candidate cards, then allows route tuning from the result page.",
    )

    personalized_route_tokens = [
        "ChatFirstPoiMockData.search",
        "generateDraft(input, questId)",
        "rankCandidatesForInput",
        "candidateScore",
        "stableSeedFor",
        "diversifyByCategory",
        "candidateStops",
        "AI selected from a same-city candidate pool",
    ]
    add(
        "Personalized candidate-pool route engine",
        has_all(generator + ai_generator + read_text(app_root / "data" / "TravelContentRepository.kt"), personalized_route_tokens),
        f"tokens={personalized_route_tokens}",
        "Keep AI and local fallback generation based on a same-city candidate pool plus input scoring, not a fixed first-N local route.",
    )

    v0969_personalization_tokens = [
        "personalizationStrategy",
        "personalizationSignals",
        "personalizationReasons",
        "personalizationTradeoff",
        "personalizationSourceNote",
        "TP_INTENT_STRATEGY_LABEL",
        "TP_INTENT_SIGNALS",
        "TP_INTENT_REASON",
        "TP_INTENT_TRADEOFF",
        "TP_INTENT_SOURCE",
        "strategyLabel",
        "evidenceSignals",
        "safePersonalizationStrategyFor",
        "safePersonalizationSignalsFor",
        "safePersonalizationTradeoffFor",
        "safePersonalizationSourceNote",
    ]
    v0969_ui_tokens = [
        "RecentIntentStrip",
        "home_recent_intent",
        "PersonalFitCardV2",
        "当前版本：$currentStrategy",
        "按这版生成",
        "card.tradeoff",
        "card.sourceNote",
        "stableSplashTaglineLines",
        "SimplifiedSplashLine3",
        "widthIn(max = 300.dp)",
        "compact: Boolean",
        "KawaiiChip(text = \"设置\", selected = false, onClick = onSettings)",
    ]
    add(
        "V0.9.69 candidate evidence, route inheritance, and orphan-text guard",
        has_all(generator + route_interpreter + store, v0969_personalization_tokens)
        and has_all(home_screen + result_screen + main_activity + splash_screen, v0969_ui_tokens),
        f"personalizationTokens={v0969_personalization_tokens}; uiTokens={v0969_ui_tokens}",
        "Keep route candidates and result pages visibly different by intent, preserve selected-card strategy into the result page, expose recent local intents, and prevent splash subtitles from leaving a single Chinese character alone.",
    )

    v0970_cinematic_tokens = [
        "DirectorCutPanel",
        "director-cut-motion",
        "director-cut-pulse",
        "TODAY WAS CUT",
        "TodayQuestTicketCover",
        "你的今日电影",
        "Act ${index + 1} · ${card.strategyLabel}",
        "fadeIn(tween(180)) + slideInVertically",
        "筛选同城镜头",
        "剪成路线副本",
        "打印今日票根",
        "因为你",
    ]
    add(
        "V0.9.70 cinematic candidate and result-ticket layer",
        has_all(home_screen + result_screen + loading_screen + route_interpreter, v0970_cinematic_tokens),
        f"tokens={v0970_cinematic_tokens}",
        "Keep the chat-first generation moment feeling like a personalized director cut: visible AI understanding, staggered candidate cards, and a ticket-style result cover that inherits the selected route strategy.",
    )

    ai_boundary_tokens = [
        "AI_ROUTE_GATEWAY_URL",
        "AiQuestGenerator(BuildConfig.AI_ROUTE_GATEWAY_URL)",
        "normalizedGatewayEndpoint",
        "requestGateway",
        "usedFallback",
        "Local fallback route",
    ]
    add(
        "AI route gateway client boundary",
        has_all(build_gradle + main_activity + ai_generator + release_config_template, ai_boundary_tokens)
        and "KIMI_API_KEY" not in all_app_sources
        and "api.moonshot.cn" not in all_app_sources,
        f"tokens={ai_boundary_tokens}; appHasKimiKey={'KIMI_API_KEY' in all_app_sources}",
        "Keep Kimi keys out of Android. The APK may only contain an AI gateway URL and must fallback locally when the gateway is absent or invalid.",
    )

    manifest = read_text(root / "app" / "src" / "main" / "AndroidManifest.xml")
    network_security = read_text(root / "app" / "src" / "main" / "res" / "xml" / "network_security_config.xml")
    local_ai_network_tokens = [
        'android:networkSecurityConfig="@xml/network_security_config"',
        '<base-config cleartextTrafficPermitted="false" />',
        '<domain includeSubdomains="false">10.0.2.2</domain>',
        '<domain includeSubdomains="false">127.0.0.1</domain>',
        '<domain includeSubdomains="false">localhost</domain>',
    ]
    add(
        "Local AI gateway network policy",
        has_all(manifest + network_security, local_ai_network_tokens),
        f"tokens={local_ai_network_tokens}",
        "Allow cleartext only for local emulator QA hosts; production external-test gateways must remain HTTPS.",
    )

    ai_home_tokens = [
        "AiIntentComposer",
        "说一句今天想怎么玩",
        "看看我理解得对不对",
        "我理解的是",
        "就按这个生成",
        "buildAiIntentInput",
        "inferCityFromText",
    ]
    add(
        "AI one-sentence home path",
        has_all(home_screen, ai_home_tokens),
        f"tokens={ai_home_tokens}",
        "Keep the home AI entry short: one-sentence input, chips, understanding confirmation, then generation.",
    )

    saved_route_tokens = [
        "SavedRoutesScreen",
        "AppScreen.Saved",
        "onSaved = { screen = AppScreen.Saved }",
        "收藏的路线",
        "records = viewModel.history",
        "onReplay",
    ]
    add(
        "Saved route tab opens route list",
        has_all(main_activity + home_screen + saved_screen, saved_route_tokens)
        and "onSaved = onQuickStart" not in home_screen,
        f"tokens={saved_route_tokens}; oldQuickStartSaved={'onSaved = onQuickStart' in home_screen}",
        "Keep the bottom Saved tab wired to a real local route list, not the old quick-start inspiration templates.",
    )

    ai_worker_tokens = [
        "/ai/route/generate",
        "KIMI_API_KEY",
        "candidateStops",
        "selectedStopIds",
        "candidate_city_mismatch",
        "selected_city_mismatch",
        "usedFallback",
    ]
    add(
        "AI gateway validates candidate stops",
        has_all(ai_worker, ai_worker_tokens),
        f"tokens={ai_worker_tokens}",
        "Keep server-side AI constrained to provided same-city candidate stops and fallback on invalid model output.",
    )

    clean_generation_tokens = [
        "copy.safeTitleFor",
        "copy.safeRouteSummaryFor",
        "copy.safeCoverageNoteFor",
        "copy.localizePoi",
        "ContentComplianceNote",
    ]
    add(
        "Route generation clean display path",
        has_all(generator, clean_generation_tokens),
        f"tokens={clean_generation_tokens}",
        "Keep generated visible route copy behind safe/localized copy helpers.",
    )

    billing_guard_ok = (
        'BuildConfig.BILLING_VERIFY_ENDPOINT.trim().startsWith("https://")' in shop
        and "enabled = paymentsEnabled" in shop
        and "paymentUnavailable" in shop
        and "isVerificationEndpointConfigured()" in billing
        and billing.find("isVerificationEndpointConfigured()") < billing.find("launchBillingFlow")
    )
    add(
        "Billing disabled without verify endpoint",
        billing_guard_ok,
        "Shop disables purchase buttons and gateway blocks launchBillingFlow unless HTTPS verification endpoint is configured.",
        "Do not allow paid entitlement purchase flow before backend verification endpoint exists.",
    )

    billing_query_split_tokens = [
        "TodayPlayProducts.all.groupBy",
        "billingProductType(product.kind)",
        "ProductType.SUBS",
        "ProductType.INAPP",
        "runCatching",
    ]
    add(
        "Billing product details query split by product type",
        has_all(billing, billing_query_split_tokens),
        f"tokens={billing_query_split_tokens}",
        "Query subscriptions and one-time products separately; BillingClient rejects mixed product types in one request.",
    )

    history_ok = has_all(repository, [
        "fun clearHistory()",
        "store.clearRecords()",
        "fun updateTaskStatus",
        "rewardPointsForStatus",
        "filterNot { it.quest.questId == record.quest.questId }",
    ]) and has_all(store, [
        "fun saveRecords",
        "fun loadRecords",
        "fun clearRecords",
        "remove(KEY_RECORDS)",
        "KEY_RECORDS",
    ])
    add(
        "History upsert, status, reward, and clear",
        history_ok,
        "Repository upserts records, updates task status/rewards, and clears persisted store records.",
        "Keep local history persistence and deletion behavior covered by release preflight.",
    )

    map_ok = has_all(map_nav, [
        'setPackage("com.autonavi.minimap")',
        "fallbackIntent",
        "ClipboardManager",
        "copyAddress",
        "mapUnavailableCopiedAddress",
    ])
    add(
        "Map fallback constructability",
        map_ok,
        "MapNavigator attempts AMap, then browser fallback, then clipboard fallback.",
        "Keep external navigation available without requesting sensitive location permission.",
    )

    stop_swap_tokens = [
        "StopSwapPanel",
        "stopSwapAction",
        "stopSwapSelected",
        "onUseSwap",
        "TaskStatus.Skipped",
        "isResolved()",
    ]
    add(
        "Route stop swap path",
        has_all(result_screen, stop_swap_tokens),
        f"tokens={stop_swap_tokens}",
        "Keep single-stop swap available so users can continue a route without regenerating the whole quest.",
    )

    route_edit_tokens = [
        "fun replaceStop",
        "filterNot { candidate -> candidate.poiId in usedPoiIds }",
        "copy.safeTitleFor",
        "taskStatuses = record.progress.taskStatuses - clearedTaskId",
    ]
    route_edit_wiring_tokens = [
        "fun replaceRouteStop",
        "generator.replaceRouteStop",
        "repository.replaceRouteStop",
        "onReplaceRouteStop",
        "stopRerollAction",
    ]
    route_edit_ok = has_all(generator, route_edit_tokens) and has_all(
        repository + view_model + result_screen,
        route_edit_wiring_tokens,
    )
    add(
        "Route stop replacement path",
        route_edit_ok,
        f"generatorTokens={route_edit_tokens}; wiringTokens={route_edit_wiring_tokens}",
        "Keep route editing able to replace one stop, persist the updated record, and clear stale stop progress/rewards.",
    )

    route_preview_generator_tokens = [
        "fun previewStopReplacement",
        "RouteStopReplacementPreview",
        "selectReplacement",
        "sameCategory",
        "matchedTags",
        "stayDeltaMinutes",
    ]
    route_preview_wiring_tokens = [
        "fun previewRouteStopReplacement",
        "generator.previewRouteStopReplacement",
        "route_stop_replace_preview",
        "onPreviewRouteStopReplacement",
        "ReplacementPreviewPanel",
        "stopRerollConfirmAction",
        "stopRerollCancelAction",
    ]
    route_preview_ok = has_all(generator, route_preview_generator_tokens) and has_all(
        repository + view_model + result_screen + main_activity,
        route_preview_wiring_tokens,
    )
    add(
        "Route stop replacement preview path",
        route_preview_ok,
        f"generatorTokens={route_preview_generator_tokens}; wiringTokens={route_preview_wiring_tokens}",
        "Keep single-stop replacement as a preview-and-confirm flow so users do not accidentally lose a liked stop.",
    )

    route_restore_model_tokens = [
        "RouteStopRestoreSnapshot",
        "lastRouteStopRestore",
        "previousStop: RouteStop",
        "restoredRewardPoints",
    ]
    route_restore_storage_tokens = [
        '"lastRouteStopRestore"',
        "toRouteStopRestoreSnapshot",
        "restoredFeedbackReasons",
        "restoredRewardPoints",
    ]
    route_restore_wiring_tokens = [
        "fun restoreStop",
        "lastRouteStopRestore = RouteStopRestoreSnapshot",
        "lastRouteStopRestore = null",
        "fun restoreRouteStop",
        "repository.restoreRouteStop",
        "route_stop_restore",
        "onRestoreRouteStop",
        "RestoreStopPanel",
        "stopRestoreAction",
    ]
    route_restore_ok = (
        has_all(quest_models, route_restore_model_tokens)
        and has_all(store, route_restore_storage_tokens)
        and has_all(generator + repository + view_model + main_activity + result_screen, route_restore_wiring_tokens)
    )
    add(
        "Route stop restore path",
        route_restore_ok,
        f"modelTokens={route_restore_model_tokens}; storageTokens={route_restore_storage_tokens}; wiringTokens={route_restore_wiring_tokens}",
        "Keep replacement undo able to restore the previous stop, status, feedback, and points from local history.",
    )

    city_theme_copy_tokens = [
        "HomeCityThemePack",
        "cityThemePacks",
        "mobilityPressure",
        "contentStatus",
        "sourceStatus",
        "stopNames",
        "fitCue",
        "cityThemesTitle",
        "cityThemesEnglish",
        "cityThemeGenerate",
        "Old-street snack hangout",
        "Lakeside solo reset",
        "Boundary-safe night walk",
    ]
    city_theme_home_tokens = [
        "CityThemeCards",
        "CityThemeCard",
        "CityThemeRouteMotif",
        'SectionHeader("02"',
        "HomeRouteContentCatalog.cityThemePacks",
        "item.mobilityPressure",
        "item.contentStatus",
        "item.sourceStatus",
        "strings.cityThemeGenerate",
        "RoseGold.copy",
        "LineBeige",
        "verticalScroll(rememberScrollState())",
    ]
    city_theme_ok = (
        has_all(home_route_content + locale_copy, city_theme_copy_tokens)
        and has_all(home_screen, city_theme_home_tokens)
        and "strings.homeCityThemes" not in home_screen
    )
    add(
        "Home city theme route entry",
        city_theme_ok,
        f"copyTokens={city_theme_copy_tokens}; homeTokens={city_theme_home_tokens}",
        "Keep city theme cards as a first-screen route-generation path that is local, curated, and independent of external APIs.",
    )

    store_conversion_tokens = [
        'const val CHANNEL_DATE = "date"',
        'const val CHANNEL_FRIEND_LOOP = "friend-loop"',
        'const val CHANNEL_SOLO_RESET = "solo-reset"',
        "dateRoutePacks",
        "friendLoopPacks",
        "soloResetPacks",
        "datePackChannels",
        "friendLoopChannels",
        "soloResetChannels",
        "allRoutePacks",
        "心动路线",
        "今晚组局",
        "慢慢走",
        "routePackItems(",
        '"date"',
        '"friend-loop"',
        '"solo-reset"',
    ]
    store_conversion_content_ok = (
        home_route_content.count("HomeCityThemePack(") >= 15
        and home_route_content.count("stopNames = listOf(") >= 15
        and home_route_content.count("contentStatus = ") >= 15
        and home_route_content.count("sourceStatus = ") >= 15
    )
    add(
        "Store conversion home channels",
        has_all(home_route_content + home_screen, store_conversion_tokens)
        and store_conversion_content_ok
        and 'HomeContentChannel(HomeRouteContentCatalog.CHANNEL_RELATIONSHIP' not in home_screen,
        f"tokens={store_conversion_tokens}; contentOk={store_conversion_content_ok}",
        "Keep the store-conversion home path focused on Today, Date, Friends, Solo, and City packs, with five real local sample route packs per strong channel.",
    )

    add(
        "External-test home first-screen de-clutter",
        "copy.cityChip" not in home_screen
        and "HomeDiscoveryTopBar" in home_screen
        and "HomeEmotionHeroCard" in home_screen
        and "HomeWaterfallFeed" in home_screen,
        "top bar no longer renders the city-pack pill; hero, channels, and feed remain present",
        "Keep Settings and discovery separate; do not put city packs, language, payment, or long setup controls back into the first top row.",
    )

    result_external_test_tokens = [
        ".height(178.dp)",
        ".height(132.dp)",
        "KawaiiChip(text = copy.executionOpenMap",
        "copy.executionCompleteStop(currentStop.checkInTask.rewardPoints)",
        "RouteProgressTimeline(",
    ]
    add(
        "External-test result first action exposure",
        has_all(result_screen, result_external_test_tokens)
        and result_screen.find("copy.executionCompleteStop(currentStop.checkInTask.rewardPoints)") < result_screen.find("RouteProgressTimeline("),
        f"tokens={result_external_test_tokens}",
        "Keep the result page compact enough that testers see the current stop and primary route action before timeline/detail overflow.",
    )

    sensory_home_tokens = [
        "HomeEmotionHeroCard",
        "romantic_hero",
        "romantic-home-hero",
        "heroBadge",
        "heroTitle",
        "heroSubtitle",
        "heroAction",
        "StopPreviewPill",
        "route-card-pulse",
        "item.chips.take(2)",
    ]
    add(
        "Sensory romantic home redesign",
        has_all(home_screen, sensory_home_tokens)
        and romantic_hero_asset.exists(),
        f"tokens={sensory_home_tokens}; heroAssetExists={romantic_hero_asset.exists()}",
        "Keep the first mobile impression image-led, romantic, animated, and lighter than the older text-heavy route cards.",
    )

    romantic_visual_tokens = [
        "romantic_hero",
        "romantic_date",
        "romantic_friend",
        "romantic_solo",
        "romantic_ticket",
        "PaperWhite = Color(0xFFF8F3EC)",
        "CherryPressed = Color(0xFFB76267)",
        "BlackCherry = Color(0xFF3B1B1F)",
    ]
    old_placeholder_refs_removed = all(
        token not in home_screen + result_screen + shop + locale_copy + loading_screen + components
        for token in [
            "anime_date_invitation",
            "anime_evening_walk",
            "anime_cafe_friends",
            "anime_ticket_sky",
            "hero_date_invitation",
            "hero_romantic_dusk",
            "hero_cozy_cafe",
            "hero_premium_tickets",
        ]
    )
    romantic_assets_exist = all(
        asset.exists()
        for asset in [
            romantic_hero_asset,
            romantic_date_asset,
            romantic_friend_asset,
            romantic_solo_asset,
            romantic_ticket_asset,
        ]
    )
    add(
        "Romantic minimalism visual direction",
        has_all(home_screen + result_screen + shop + locale_copy + loading_screen + components + theme_colors, romantic_visual_tokens)
        and old_placeholder_refs_removed
        and romantic_assets_exist,
        f"tokens={romantic_visual_tokens}; oldPlaceholderRefsRemoved={old_placeholder_refs_removed}; romanticAssetsExist={romantic_assets_exist}",
        "Keep the primary visual system on project-local cinematic romantic minimalism bitmap assets instead of developer-drawn placeholder vectors.",
    )

    motion_tokens = [
        "AnimatedContent(",
        "app-screen-transition",
        "SplashRouteMotion",
        "splash-intro-alpha",
        "RouteBuildAnimation",
        "route-loading-motion",
        "route-loading-draw",
    ]
    add(
        "Opening transition and loading motion",
        has_all(main_activity + splash_screen + loading_screen, motion_tokens),
        f"tokens={motion_tokens}",
        "Keep opening, inter-screen, and route-generation loading moments animated in the romantic cinematic visual style.",
    )

    splash_copy_tokens = [
        "balancedSplashTagline",
        "把普通日子，剪成\\n只属于你们的一小段电影。",
        "把普通日子，剪成\\n只屬於你們的一小段電影。",
        "maxLines = 2",
        "lineHeight = 28.sp",
    ]
    splash_copy_tokens = [
        "SplashTaglineText(",
        "text = strings.appEnglishName",
        "fontSize = 15.sp",
        "stableSplashTaglineLines",
        "SimplifiedSplashLine1",
        "SimplifiedSplashLine2",
        "SimplifiedSplashLine3",
        "TraditionalSplashLine1",
        "TraditionalSplashLine2",
        "TraditionalSplashLine3",
        "softWrap = false",
        "maxLines = 1",
        "widthIn(max = 300.dp)",
        "if (lines.size >= 3)",
    ]
    add(
        "Splash tagline avoids orphan Chinese characters",
        has_all(splash_screen, splash_copy_tokens),
        f"tokens={splash_copy_tokens}",
        "Keep splash subtitle line breaks manually balanced so the final Chinese character or punctuation never sits alone.",
    )

    home_orphan_copy_tokens = [
        "说一句状态，路线会浮现。",
        "比如：今晚两个人，少走路",
        "maxLines = if (compact) 1 else 2",
        "TextOverflow.Ellipsis",
        "if (!compact) {",
        'setOf("date", "friends", "solo", "less-walk", "movie", "low-budget")',
        "minLines = if (compact) 1 else 3",
    ]
    add(
        "Home intro copy avoids short orphan tails",
        has_all(home_screen, home_orphan_copy_tokens)
        and "路线会慢慢浮出来。" not in home_screen
        and "别太累，适合聊天" not in home_screen,
        f"tokens={home_orphan_copy_tokens}",
        "Keep first-screen helper copy short enough for narrow and foldable screens; avoid awkward one- or two-character tail lines.",
    )

    art_motion_tokens = [
        "tp_art_splash_companion",
        "SplashStampMark",
        "tp_art_home_companion",
        "ChatFirstCompanionIntro",
        "chat-companion-breath",
        "tp_art_loading_route",
        "LoadingStageRail",
        "LoadingStamp",
        "result-hero-breath",
        "route-current-pulse",
        "timeline-dot-pulse",
    ]
    art_assets_exist = all(
        asset.exists() and asset.stat().st_size < 2_000_000
        for asset in [tp_art_splash_asset, tp_art_home_asset, tp_art_loading_asset]
    )
    add(
        "V0.9.62 art and calm motion layer",
        has_all(splash_screen + home_screen + loading_screen + result_screen, art_motion_tokens)
        and art_assets_exist,
        f"tokens={art_motion_tokens}; artAssetsExistAndSmall={art_assets_exist}",
        "Keep splash, chat-first home, loading, and result pages visually alive with project-local lightweight art and restrained motion.",
    )

    adaptive_strategy_tokens = [
        "val wide = maxWidth >= 720.dp",
        "val compact = maxHeight < 720.dp",
        "val useWideLayout = wide && !shortLandscape",
        "contentPadding = PaddingValues(pagePadding)",
        "ChatFirstRoutePrinciples",
        "更多城市待验证",
    ]
    strategy_route_tokens = [
        'CardSpec("indoor"',
            "val expandedSpecs = specs.take(5) +",
        'input.intentMarker("TP_INTENT_STRATEGY")',
        '"indoor" -> {',
        'category.contains("indoor")',
    ]
    add(
        "V0.9.64 adaptive home and strategy-scored routes",
        has_all(home_screen, adaptive_strategy_tokens)
        and has_all(route_interpreter + generator, strategy_route_tokens),
        f"homeTokens={adaptive_strategy_tokens}; strategyTokens={strategy_route_tokens}",
        "Keep the active chat-first home aligned to adaptive breakpoints and ensure selected candidate-card strategy affects POI ranking.",
    )

    time_cinema_tokens = [
        'ChatQuickChip("movie", "时光电影")',
        '"cinema" -> BlackCherry',
        'wantsCinema',
        'CardSpec("cinema"',
        '"时光电影路线"',
        'TP_INTENT_CINEMA',
        '"cinema" -> {',
        'poiId = "sh-cinema-film-park"',
        'poiId = "tokyo-cinema-suga-steps"',
        'poiId = "sh-cinema-wukang-balcony"',
        'poiId = "sh-cinema-suzhou-creek-bridge"',
        'poiId = "tokyo-cinema-shimokitazawa-vinyl"',
        'poiId = "tokyo-cinema-daikanyama-book"',
        "真实取景地关系需来源核验",
    ]
    add(
        "V0.9.65 time-cinema quest path",
        has_all(home_screen + route_interpreter + generator + read_text(app_root / "data" / "ChatFirstPoiMockData.kt"), time_cinema_tokens)
        and "TIME_CINEMA_QUEST_PLAN_V0_9_65_2026-06-17.md" in "\n".join(path.name for path in root.glob("*.md")),
        f"tokens={time_cinema_tokens}",
        "Keep the movie-feeling route mode grounded in a visible chat chip, cinema strategy scoring, sample POIs, and explicit source-verification wording.",
    )

    time_cinema_visual_tokens = [
        "TimeCinemaTicketCard",
        "TimeCinemaDirectorStrip",
        "TimeCinemaRouteMap",
        "TimeCinemaSceneRow",
        "TODAY WAS PLAYED / 今日电影票",
        "DIRECTOR'S CUT / 导演剪辑",
        "tp_art_time_cinema_route",
        "场记板",
        "导航当前镜头",
        "MAP ROUTE",
        "Act 01",
        "sourceWarning",
        "来源状态会单独标注",
        "isTimeCinemaRoute",
    ]
    add(
        "V0.9.66 time-cinema ticket and route-map result UI",
        has_all(result_screen, time_cinema_visual_tokens)
        and tp_art_time_cinema_asset.exists()
        and tp_art_time_cinema_asset.stat().st_size < 3_000_000,
        f"tokens={time_cinema_visual_tokens}; timeCinemaAssetOk={tp_art_time_cinema_asset.exists()}",
        "Keep the time-cinema result page visually centered on a generated route background, director film strip, clapper-board cue, three acts, and a non-official in-app route map.",
    )

    adaptive_text_tokens = [
        "statusBarsPadding()",
        "navigationBarsPadding()",
        "bottomHomeIcon",
        "bottomSavedIcon",
        "bottomHistoryIcon",
        "bottomSettingsIcon",
        'settingsIcon = "设置"',
        'settingsIcon = "設定"',
        'settingsIcon = "Set"',
        ".widthIn(max = 210.dp)",
        "Modifier.weight(1f, fill = false)",
        "text = \"<\"",
    ]
    glyph_sensitive_sources = [
        home_screen,
        components,
        result_screen,
        read_text(app_root / "ui" / "screens" / "HistoryScreen.kt"),
        read_text(app_root / "ui" / "screens" / "PrivacyScreen.kt"),
        read_text(app_root / "ui" / "screens" / "QuickStartScreen.kt"),
        read_text(app_root / "ui" / "screens" / "ShareCardScreen.kt"),
        read_text(app_root / "ui" / "screens" / "ShopScreen.kt"),
        read_text(app_root / "ui" / "screens" / "SplashScreen.kt"),
        read_text(app_root / "ui" / "screens" / "LoadingScreen.kt"),
        read_text(app_root / "localization" / "TodayPlayLocale.kt"),
        read_text(app_root / "localization" / "ShareCopy.kt"),
    ]
    glyph_sensitive_text = "\n".join(glyph_sensitive_sources)
    risky_home_glyphs_removed = all(token not in home_screen for token in ['"⚙"', '"⌂"', '"↺"', '"♡"'])
    risky_app_glyphs_removed = all(token not in glyph_sensitive_text for token in ["♥", "♡", "⚙", "⌂", "↺", "‹", "✧"])
    add(
        "Foldable safe text and insets",
        has_all(home_screen + components, adaptive_text_tokens)
        and risky_home_glyphs_removed
        and risky_app_glyphs_removed,
        f"tokens={adaptive_text_tokens}; riskyHomeGlyphsRemoved={risky_home_glyphs_removed}; riskyAppGlyphsRemoved={risky_app_glyphs_removed}",
        "Keep foldable and narrow-screen layouts out of the status/navigation bars and avoid OEM-font-sensitive glyph icons in primary home navigation.",
    )

    route_execution_tokens = [
        "RouteExecutionCard",
        "currentRunnableStop",
        'SectionHeader("LIVE"',
        "executionCurrentStopLabel",
        "executionNextActionLabel",
        "ExecutionMetricTile",
        "RouteProgressTimeline",
        "TimelineDot",
        "executionTimelineLabel",
        "executionCompleteStop",
        "executionOpenMap",
    ]
    route_execution_ok = has_all(result_screen, route_execution_tokens) and "executionUseSwap" not in result_screen
    add(
        "Result page live route mode",
        route_execution_ok,
        f"tokens={route_execution_tokens}; excludesTopLevelSwap={'executionUseSwap' not in result_screen}",
        "Keep the result page focused on the current stop, next action, reward, and safe progress actions without duplicating replacement/restore decisions.",
    )

    account_tokens = [
        "AccountSession",
        "AccountProvider",
        "localTester",
        "idTokenForBackend",
    ]
    google_auth_tokens = [
        "CredentialManager.create",
        "GetGoogleIdOption.Builder",
        "BuildConfig.GOOGLE_WEB_CLIENT_ID",
        "GoogleIdTokenCredential.createFrom",
        "backendVerified = false",
        "clearCredentialState",
    ]
    account_ui_tokens = [
        "AccountAccessPanel",
        "Continue with Google",
        "Use local tester profile",
        "ShareAccountSection",
        "withShareAttribution",
        "accountSession.shareName",
    ]
    account_ok = (
        has_all(auth_models, account_tokens)
        and has_all(google_gateway, google_auth_tokens)
        and has_all(home_screen + share_screen + main_activity, account_ui_tokens)
    )
    add(
        "Account sign-in scaffold and share attribution",
        account_ok,
        f"modelTokens={account_tokens}; googleTokens={google_auth_tokens}; uiTokens={account_ui_tokens}",
        "Keep Google sign-in behind OAuth configuration, preserve local tester mode for trial APKs, and only use Google ID tokens after backend verification.",
    )

    visual_weight_tokens = [
        "HomeWaterfallFeed",
        "RouteFeedCard",
        "HomeScenarioChips",
        "HomeQuickPlanStrip",
        "HomeContentChannelRail",
        "homeContentChannels",
        "selectedChannel",
        "HomeRouteContentCatalog.CHANNEL_TODAY",
        "HomeRouteContentCatalog.CHANNEL_CITY",
        "HomeRouteContentCatalog.CHANNEL_DATE",
        "HomeRouteContentCatalog.CHANNEL_FRIEND_LOOP",
        "HomeRouteContentCatalog.CHANNEL_SOLO_RESET",
        "CityRoutePackPreview",
        "routeStopsFor",
        "routeProofFor",
        "homeDiscoveryFeed",
        "selectedScenario",
        "savedRouteKeys",
        "RouteMiniMap(",
        "RouteVisualPreviewCard",
        "RouteSketchMap",
        "RouteStopPill",
        "visualMapTitle",
        "FeedRouteProof",
        "同城校验",
        "maxLines = 2",
    ]
    add(
        "Feed-first recommendations and route preview",
        has_all(home_screen + result_screen + home_route_content, visual_weight_tokens)
        and "HomeBrandHeader(strings = strings" not in home_screen,
        f"tokens={visual_weight_tokens}",
        "Keep the mobile experience from regressing into dense text-first onboarding; home needs scenario chips, waterfall-like route cards, save/start actions, and result pages need a map-like route preview.",
    )

    same_city_route_tokens = [
        "resolveRouteCity(",
        "withRouteCity(routeCity)",
        "sameCityOnly(routeCity)",
        "city = routeCity",
        "candidateRouteCount = candidates.size",
    ]
    home_city_pack_tokens = [
        'city = "上海"',
        'city = "深圳"',
        'city = "广州"',
        'city = "杭州"',
        'cityChip = "城市路线包"',
    ]
    add(
        "Same-city route planning guard",
        has_all(generator, same_city_route_tokens)
        and has_all(home_screen, home_city_pack_tokens)
        and 'city = "全球热门城市"' not in home_screen
        and 'cityChip = "全球灵感"' not in home_screen,
        f"generatorTokens={same_city_route_tokens}; homeCityTokens={home_city_pack_tokens}",
        "Keep every generated route locked to one concrete city and prevent clickable home recommendations from using broad global city placeholders.",
    )

    language_settings_tokens = [
        "languageTitle",
        "TodayPlayLocale.entries.forEach",
        "onLocaleSelected(locale)",
        'navLabel = "设置"',
        'title = "隐私与本地数据"',
    ]
    language_in_settings_ok = (
        has_all(privacy_screen + privacy_copy, language_settings_tokens)
        and "TodayPlayLocale.entries.forEach" not in home_screen
        and "onLocaleSelected: (TodayPlayLocale) -> Unit" not in home_screen
    )
    add(
        "Language selector lives in settings",
        language_in_settings_ok,
        f"settingsTokens={language_settings_tokens}; homeHasLanguageSelector={'TodayPlayLocale.entries.forEach' in home_screen}",
        "Keep language selection out of the home recommendation flow; it belongs in Settings & Privacy.",
    )

    system_back_tokens = [
        "BackHandler(enabled = screen != AppScreen.Home && screen != AppScreen.Splash)",
        "AppScreen.Result -> screen = AppScreen.Create",
        "AppScreen.ShareCard -> screen = AppScreen.Result",
        "viewModel.cancelGeneration()",
        "AppScreen.Shop -> screen = AppScreen.Home",
    ]
    add(
        "System back navigation stays inside app",
        has_all(main_activity, system_back_tokens),
        f"tokens={system_back_tokens}",
        "Keep Android system back from exiting subpages directly; route, shop, privacy, history, and loading screens need explicit in-app destinations.",
    )

    overall = "pass" if all(check["status"] == "pass" for check in checks) else "fail"
    return checks, overall


def print_markdown(checks: list[dict[str, str]], overall: str, project_root: Path) -> None:
    print("# App Regression Audit")
    print()
    print(f"- Project: `{project_root.resolve()}`")
    print(f"- Overall: `{overall}`")
    print()
    print("| Check | Status | Evidence | Required action |")
    print("| --- | --- | --- | --- |")
    for check in checks:
        print(f"| {check['name']} | `{check['status']}` | {check['evidence']} | {check['action']} |")


def main() -> None:
    parser = argparse.ArgumentParser(description="Run small app-layer source regression audit.")
    parser.add_argument("project", nargs="?", default=".", help="Android project root")
    args = parser.parse_args()

    project_root = Path(args.project)
    checks, overall = audit(project_root)
    print_markdown(checks, overall, project_root)
    if overall != "pass":
        raise SystemExit(1)


if __name__ == "__main__":
    main()
