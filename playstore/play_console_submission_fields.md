# Play Console Submission Fields

Current release checkpoint: `0.9.40 / versionCode 58`.

Use this as the source checklist when filling Google Play Console.

## App Details

- App name: `今天怎么玩`
- Default language: `Chinese (Simplified) - zh-CN`
- App or game: `App`
- Free or paid: `Free`
- Contains ads: `No` unless an ad SDK is added later.
- Contact email: `TODO: developer support email`
- Privacy policy URL: `TODO: hosted playstore/privacy_site HTTPS URL`

## Store Listing

- Short description: see `google_play_store_listing_zh.md`.
- Full description: see `google_play_store_listing_zh.md`.
- App icon: `playstore/graphics/app_icon_512.png`.
- Feature graphic: `playstore/graphics/feature_graphic_1024x500.png`.
- Phone screenshots:
  - `playstore/screenshots/phone_01_home_route_picker.png`
  - `playstore/screenshots/phone_02_route_result.png`
  - `playstore/screenshots/phone_03_checkin_completion.png`
  - `playstore/screenshots/phone_04_premium_shop.png`
  - Status: first-pass store drafts; verify against a real device/emulator before production submission.
- Tablet screenshots: recommended if claiming tablet/foldable readiness.

## App Content

### Privacy Policy

Use the deployable multilingual privacy site at `playstore/privacy_site`, hosted on a public HTTPS URL.

`privacy_policy_zh.html` is a legacy single-language draft and should not be used as the primary Play Console privacy policy URL.

### Data Safety

Use `data_safety_draft.md` as the draft. Re-check after backend, analytics, maps SDK, account, or photo upload features are added.

### Content Rating

Suggested initial stance:

- No violence.
- No sexual content.
- No gambling.
- No user-generated public feed in current version.
- No precise location collection.
- Paid digital products via Google Play Billing.

### Target Audience

Suggested:

- Adults and general users.
- Not designed specifically for children.

If the product later targets children or family-specific audiences, update UX, privacy, ads, and Play Families policy handling.

## Internal Testing

- Create an internal testing release.
- Upload `app/build/outputs/bundle/release/app-release.aab`.
- Add tester emails.
- Add release notes:

```text
V0.9.40 internal test:

- Upgrades the result page live route mode into a clearer route cockpit.
- Adds visual metric tiles for current stop, next action, reward points, route progress, and next stop.
- Adds a lightweight route timeline so users can understand the current/next stops at a glance.
- Upgrades home City Theme cards with a small route motif, making one-tap route generation more visually discoverable.
- Extends the regression audit to guard the route cockpit, timeline, and City Theme route motif.

V0.9.39 internal test:

- Adds a live route mode card near the top of the result page.
- The card highlights the current unresolved stop, next action, available points, route-stop progress, and next stop.
- Keeps complex route-editing actions such as swap, replacement, and restore inside the stop cards to reduce accidental edits.
- Adds a regression-gate check for the result-page live route mode.

V0.9.38 internal test:

- Adds City Themes on the home screen for curated one-tap route generation.
- Includes local theme cards for Guangzhou friend snacks, Hangzhou solo reset, and Taipei boundary-safe night walk.
- Each theme card shows city, scenario, tags, and a generate action without needing external APIs.
- Adds a regression-gate check for the home city theme route entry.

V0.9.37 internal test:

- Adds a one-step restore path after replacing a route stop.
- Replacement now keeps the previous stop, check-in status, feedback, and reward points as a local restore snapshot.
- The result page shows a restore panel for the latest replacement so users can go back without regenerating the route.
- Adds a regression-gate check for the route stop restore path.

V0.9.36 internal test:

- Changes single-stop replacement into a preview-and-confirm flow.
- Shows the same-city candidate name, district, stay duration, tags, source, and match signals before replacing the current stop.
- Confirming keeps the rest of the route and clears stale check-in status, feedback, and reward points for that stop.
- Adds a regression-gate check for the route stop replacement preview path.

V0.9.35 internal test:

- Adds single-stop reroll on route stop cards so users can keep the route and replace only the stop they dislike.
- Replacement uses same-city candidates and prioritizes similar category, matching tags, and close stay duration.
- Replacing a stop persists to local history and clears stale stop status, feedback, and reward points.
- Adds a regression-gate check for the route stop replacement path.

V0.9.34 internal test:

- Adds a single-stop swap path on route stop cards so users can continue a quest when one place is too far, crowded, rainy, or off-mood.
- Swap usage resolves that stop for route progress but does not award check-in points, keeping the difference between a real check-in and an alternative action clear.
- Adds a regression-gate check for the route stop swap path.

V0.9.33 internal test:

- Adds a route playbook card on the result page so users can immediately see the first stop, best photo timing, available points, safe swap plan, and route tags.
- Adds stop-level mission and reward copy to route stop cards, making each place feel like a playable check-in rather than only a location.
- Keeps Play launch blockers truthful: support email, public privacy URL, Billing verification endpoint, and fresh device screenshots are still required before submission.

V0.9.32 internal test:

- Improves the share loop so unfinished routes lead with an invite card while completed routes lead with a completion card.
- Adds system sharing for completed route quests, including completion title, task progress, stop check-ins, points, keywords, and summary.
- Keeps all share text localized across zh-CN, zh-TW, en, ja, ko, and es.

V0.9.31 internal test:

- Adds a market-research/product-planning role to track comparable products, reusable mechanics, and product risks.
- Expands home Today Picks from 3 to 5 cards, adding friends low-budget and solo reset scenarios.
- Adds a history replay action so users can generate a fresh route using a previous quest's relationship, city, budget, duration, and mood keywords.

V0.9.30 internal test:

- Expands Quick Start from 5 to 9 ready-made route templates, adding solo reset, friends low-budget, boundary-safe crush, and low-energy family scenarios.
- Adds local mock POI coverage for Guangzhou, Hangzhou, Taipei, and Xi'an, with more city suggestions for product testing.
- Adds more playable route content, including route voting, energy refill stops, comfort-level checks, low-energy permissions, photo missions, and completion summaries.

V0.9.29 internal test:

- Adds the release operations action plan to the submission gate's version-document sync check.
- Updates release operations materials so the baseline, internal-test release note prompt, and final upload condition match `0.9.29 / versionCode 47`.
- Keeps external blockers truthful: support email, hosted privacy URL, Billing verify endpoint, Play Console setup, and fresh real-device screenshots remain required.

V0.9.28 internal test:

- Tightens release-document version sync so the current-status section near the top of each release document must match Gradle's versionName/versionCode.
- Updates the release plan and official requirements note so their current project status no longer points at older versions.
- Keeps external Google Play blockers truthful: support email, hosted privacy URL, Billing verify endpoint, and fresh real-device screenshots still require external completion.

V0.9.27 internal test:

- Adds release gate checks for actual AAB signing files, keystore UTF-8 BOM risk, screenshot dimensions/freshness, and release-document version sync.
- The gate now verifies the generated AAB contains META-INF signature artifacts before Play upload.
- Store screenshots are flagged when they are older than the latest AAB, so production screenshots should be recaptured from the current build.
- This is a release-hardening update based on the V0.9.25 test report; app runtime behavior is unchanged.

V0.9.26 internal test:

- Splits the Google Play submission gate's external Play Console field check into separate contact-email and privacy-policy-URL checks.
- The contact email must be a real support email, not a TODO or example value.
- The privacy policy URL must be a public HTTPS URL for the deployed `playstore/privacy_site` package.
- This makes remaining release blockers more actionable without changing app runtime behavior.

V0.9.25 internal test:

- Aligns the Google Play submission gate with the deployable multilingual privacy site.
- The gate no longer treats the legacy `privacy_policy_zh.html` draft as the primary privacy policy material.
- The authoritative local privacy package is now `playstore/privacy_site`, while public HTTPS hosting and real support contact remain blocked through Play Console fields.
- Updates this Play Console checklist to point privacy policy hosting to `playstore/privacy_site`.

V0.9.24 internal test:

- Makes the in-app Privacy & Local Data support section actionable.
- When configured through release_config.properties, users can open the public HTTPS privacy policy and start a mailto support message directly from the privacy screen.
- Buttons remain disabled when support email or privacy policy URL are not configured, so local/internal builds do not pretend launch metadata is ready.
- Extends the release config audit to verify LocalUriHandler wiring, mailto support, HTTPS-only privacy URL opening, disabled states, and localized action labels.
- No new sensitive permissions are added.

V0.9.23 internal test:

- Adds a localized source encoding audit to the formal release preflight.
- The audit reads Kotlin localization files, UI screen files, Android string resources, localized Play listing drafts, and the deployable privacy site as UTF-8.
- It checks required launch-language tokens for Simplified Chinese, Traditional Chinese, English, Japanese, Korean, and Spanish, and blocks common mojibake/replacement-character markers before packaging.
- This does not replace native-speaker translation review, but it prevents corrupted multilingual copy from silently entering APK/AAB builds.

V0.9.22 internal test:

- Strengthens the Google Play submission gate with the same privacy-site quality checks used by the full release preflight.
- The submission gate now verifies zh-CN, zh-TW, en, ja, ko, and es privacy-site sections, language navigation, common UTF-8 mojibake markers, Billing purchase-token disclosure, no precise-location/forced-arrival disclosure, and no-scraping/source-policy disclosure.
- Keeps the app package, route behavior, payment guard, and content-provider boundaries unchanged.
- Keeps Play submission blocked until Play Console fields, public privacy-policy URL, real support contact, and Billing verification endpoint are configured.

V0.9.21 internal test:

- Adds a privacy-site audit to the formal release preflight.
- Verifies the deployable privacy site contains zh-CN, zh-TW, en, ja, ko, and es sections with valid UTF-8 text and language navigation.
- Verifies privacy copy includes local data handling, no precise-location permission, no forced arrival verification, map handoff behavior, Google Play Billing purchase-token handling, backend verification requirements, and mock/authorized-content boundaries.
- Verifies the privacy site still warns that real publisher identity, real support email, and a public HTTPS URL are required before Play Console submission.
- Rechecked official Google Play target API requirements on 2026-06-11: current targetSdk 35 still satisfies the Android 15 / API 35 requirement for this release track.
- Keeps Play submission blocked until Play Console fields, public privacy-policy URL, real support contact, and Billing verification endpoint are configured.

V0.9.20 internal test:

- Adds a generated-route display audit to the formal release preflight.
- The new gate verifies the active route generation flow uses the clean display-copy adapter and does not call legacy mojibake-prone display helpers.
- The audit separately checks selected POI display localization, safe copy adapter coverage, and obvious mojibake fragments inside the active safe copy block.
- No user-facing feature behavior changed in this round; the value is stronger release protection for multilingual generated results.

V0.9.19 internal test:

- Routes visible generated-result copy through a clean multilingual display-copy adapter.
- Replaces mojibake-prone legacy route result text for reward policy, compliance summary, limitations, route titles, route summaries, cost estimates, crowd/rain guidance, photo timing, check-in tasks, backup plans, group-fit explanations, POI names, POI recommendation reasons, POI risk tips, and source labels.
- Keeps route state, history, check-in, map actions, and backend/content boundaries unchanged.
- Extends localization copy-fit audit so the release gate verifies the result flow uses the clean display-copy adapter.

V0.9.18 internal test:

- Localizes the route content availability note shown on the result page for zh-CN, zh-TW, en, ja, ko, and es.
- Prevents the English repository `coverageNote` from leaking into non-English result pages when global live search is disabled or a remote content endpoint falls back to mock data.
- Keeps the compliance boundary explicit: local mock catalog for structure testing, production global search only through official APIs, licensed data, merchant partnerships, user-authorized links, or backend aggregation.
- Extends localization copy-fit audit so generated route content checks include coverage-note localization.

V0.9.17 internal test:

- Localizes generated route POI display metadata for zh-TW, en, ja, ko, and es.
- Adds selected-POI display localization for city, district, address fallback, tags, source labels, and source policy notes.
- Updates itinerary generation so non-Chinese route cards no longer rely only on a translated UI shell while showing raw Chinese POI tags.
- Keeps canonical mock matching fields stable; live global POI search still requires official APIs, licensed providers, merchant partnerships, or backend aggregation.
- Extends localization copy-fit audit to verify generated route content localization, not only screen labels.

V0.9.16 internal test:

- Localizes the Route Result page shell for zh-CN, zh-TW, en, ja, ko, and es.
- Adds `ResultCopy.kt` with localized top-bar copy, summary labels, route overview labels, data-availability chips, map/check-in/photo controls, feedback chips, hidden-task controls, dialogue header, backup-plan text, and completion actions.
- Updates `QuestResultScreen.kt` so visible route-result labels now follow the selected app language.
- Keeps generated POI names, recommendation reasons, and canonical mock route content stable for this round to avoid route-matching regressions.
- Updates localization copy-fit audit to verify result-page shell localization.
- Updates project-local Google Play submission gate and travel-content audit to recognize the localized result-page availability and photo-upload placeholders.

V0.9.15 internal test:

- Localizes the Quick Start route-idea screen for zh-CN, zh-TW, en, ja, ko, and es.
- Adds `QuickStartCopy.kt` with localized route idea titles, subtitles, tags, intro copy, and CTA text.
- Keeps the underlying generation inputs stable while localizing the visible route cards, so existing route matching remains intact.
- Adds copy-fit guards to Quick Start cards so long translated titles, subtitles, and tag rows clamp safely on small screens.
- Updates the localization copy-fit audit to verify QuickStartScreen uses localized copy and long-label guards.
- Keeps remaining known localization cleanup visible: route result labels and generated mock content still need follow-up internationalization.

V0.9.14 internal test:

- Localizes system-level Toast messages for zh-CN, zh-TW, en, ja, ko, and es.
- Adds `SystemCopy.kt` for map fallback, clipboard labels, card-flow required-field prompts, and Google Play Billing status messages.
- Updates map navigation fallback so browser-map fallback and copied-address fallback follow the selected app language.
- Updates the Billing gateway so product-query, purchase-cancelled, verification-pending, and payment-unavailable messages follow the selected app language.
- Adds a project-local Google Play submission gate that recognizes the multilingual map fallback implementation.
- Keeps remaining known localization cleanup visible: route result labels, quick-start presets, and generated mock content still need follow-up internationalization.

V0.9.13 internal test:

- Localizes the Share/Completion Card flow for zh-CN, zh-TW, en, ja, ko, and es.
- Adds `ShareCopy.kt` with localized share page copy, system share subject/body, chooser title, save-card toast, invite-card labels, completion-card labels, and hidden-task status text.
- Updates `ShareCompletionCard` to receive localized labels instead of hardcoded display text.
- Updates the localization copy-fit audit to verify ShareCardScreen and completion-card copy coverage.
- Keeps remaining known localization cleanup visible: route result details, quick-start presets, map fallback Toasts, and generated mock content still need follow-up internationalization.

V0.9.12 internal test:

- Localizes the History/Library screen for zh-CN, zh-TW, en, ja, ko, and es.
- Adds `HistoryCopy.kt` with localized history title, empty state, CTA, field labels, and replay hint.
- Updates the localization copy-fit audit to verify HistoryScreen uses localized copy and all launch locales are covered.
- Keeps remaining known localization cleanup visible: result page, share card page, quick-start presets, and map fallback Toasts still need follow-up internationalization.

V0.9.11 internal test:

- Keeps adaptive launcher icon resources in `mipmap-anydpi-v26` to preserve AAPT resource linking for `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`.
- Adds a path-scoped lint configuration for the known-safe launcher icon `ObsoleteSdkInt` warning.
- Reduces lint warnings from 6 to 5; remaining warnings are dependency update notices that require a separate dependency-upgrade regression round.
- No new permissions, no live paid entitlement, and no live global travel-content endpoint are enabled.

V0.9.10 internal test:

- Adds optional `release_config.properties` wiring for launch-time public configuration.
- Adds BuildConfig fields for Billing verification endpoint, travel-content backend base URL, privacy policy URL, and support email.
- Keeps local defaults empty so live paid products and live global content remain disabled until real HTTPS services are configured.
- Adds `release_config.template.properties` and ignores local `release_config.properties` to avoid committing environment-specific values.
- Updates the in-app privacy page to show support email and privacy policy URL configuration state.
- Localizes support/contact/privacy-policy labels for zh-CN, zh-TW, en, ja, ko, and es.
- Adds a release-config audit to preflight, covering template fields, ignored local config, BuildConfig exposure, privacy-screen display, HTTPS URL rules, and support-email syntax.

V0.9.9 internal test:

- Moves route generation into the ViewModel and runs generation on `Dispatchers.IO`, so future HTTPS travel-content calls do not block the main UI path.
- Adds explicit generation states for loading, success, failure, retry, and user cancellation.
- Updates the loading screen to observe real generation state instead of finishing after a fake timer.
- Adds retry and return-to-edit actions when generation fails.
- Localizes loading, failure, retry, and edit-copy for zh-CN, zh-TW, en, ja, ko, and es.
- Adds a generation-flow audit to release preflight, covering background generation, state handling, cancellation safety, async regeneration, non-fake loading, and localized loading copy.
- Keeps live global search disabled because `TRAVEL_CONTENT_BASE_URL` remains empty.
- Keeps paid products disabled because `BILLING_VERIFY_ENDPOINT` remains empty.

V0.9.8 internal test:

- Adds a real Android HTTPS client path for `/travel/poi/search` when `TRAVEL_CONTENT_BASE_URL` is configured.
- Remote POI responses must be `verified`, non-mock, and include source policy metadata before the app uses them.
- Remote failures, empty results, non-verified responses, or mock responses fall back to the local mock catalog with an explicit coverage note.
- Extends travel-content audit to check Android remote HTTPS calls, short timeouts, response safety, source-policy parsing, risk tips, and official-verification metadata.
- Keeps live global search disabled in this build because `TRAVEL_CONTENT_BASE_URL` remains empty.

V0.9.7 internal test:

- Adds local contract tests for the Billing verification Worker.
- Adds a Billing backend audit to release preflight, covering backend endpoints, Google Play Developer API usage, product ID alignment, server-side secret boundaries, no-fake-entitlement rules, purchase-token safety, and RTDN/durable-storage gaps.
- Verifies `/billing/verify` rejects package mismatches, missing tokens, unknown products, and missing Google service-account secrets instead of granting paid benefits.
- Verifies `/entitlements` and `/billing/notifications` remain blocked until authenticated durable storage and Pub/Sub RTDN verification are implemented.
- Keeps Android paid entry disabled while `BILLING_VERIFY_ENDPOINT` is empty; paid products are still not live.

V0.9.6 internal test:

- Adds a formal travel provider adapter contract for future official map APIs, licensed POI datasets, merchant catalogs, operator city packs, and user-authorized links.
- Adds a machine-readable provider adapter JSON schema covering ContentSource, SourcePolicy, POI, ImageAsset, RouteStop, ItineraryPlan, and ComplianceNote fields.
- Extends travel-content Worker contract tests to verify provider contract files, required production fields, supported launch locales, no-scraping policy, image-license metadata, and unsupported-claim boundaries.
- Extends the travel-content audit so missing provider contract documentation or schema fields fail release preflight.
- Keeps live global content search disabled until a real HTTPS backend, authorized providers, privacy review, source attribution, cache policy, and image-license review are complete.

V0.9.5 internal test:

- Improves route-result layout for narrow phones and foldable cover screens with responsive horizontal padding.
- Adds overflow protection for generated route titles and POI names, reducing risk from long global place names.
- Improves Premium Shop copy layout for localized product names, subscription notes, and purchase CTA labels.
- Adds a localization copy-fit audit covering zh-CN, zh-TW, en, ja, ko, and es resources, Play listing drafts, core localized screens, privacy copy, and long-copy UI guards.
- Keeps global content search, paid entitlements, and payment verification disabled until official data providers, Play Console products, and backend verification are configured.

V0.9.4 internal test:

- Improves the preference card-flow layout for narrow phones: choice cards become full-width single-column items below 360dp.
- Adds compact step-card padding and smaller title styling for small screens.
- Adds text overflow protection to primary and secondary buttons for long localized copy.
- Adds an Adaptive UI audit to the release preflight for home layout, card flow, button overflow, and typography stability.

V0.9.3 internal test:

- Splits the travel-content Worker into a provider-adapter boundary so future official map APIs, licensed datasets, merchant catalogs, and user-authorized links can be integrated safely.
- Adds no-dependency Worker contract tests for health, source policies, city search, POI search, itinerary planning, and authorized share-link import.
- Extends the travel-content audit to check provider adapters, contract tests, no-scraping rules, and authorized source terms.
- Keeps live global search disabled until a real HTTPS backend, provider contracts, auth, rate limits, privacy review, and source metadata are complete.

V0.9.2 internal test:

- Adds Android TRAVEL_CONTENT_BASE_URL configuration for future global route-content backend integration.
- Keeps global live search disabled while the backend endpoint is empty, so the app continues to use clearly labeled local mock POI data.
- Adds a TravelContentRepository factory and remote-content placeholder boundary for future official APIs or licensed data providers.
- Adds a travel-content backend Worker skeleton with health, source-policy, city search, POI search, itinerary planning, and authorized-link import endpoints.
- Adds a project travel-content audit to verify no scraping policy, authorized source terms, backend skeleton, and disabled live-search UI state.

V0.9.1 internal test:

- Adds a formal travel-content backend contract for global POI search, authorized content import, route planning, and source policy disclosure.
- Adds an Android TravelContentRepository boundary so the route generator no longer depends directly on the local mock POI catalog.
- Keeps local mock route generation working while making future official APIs, licensed datasets, merchant partnerships, and backend aggregation replaceable.
- Adds release-gate checks that prevent mock/global content from being treated as production-ready platform data.
- No client scraping, no new sensitive permissions, no real AI, no real payment entitlement, and no backend route-search endpoint are enabled yet.

V0.9.0 internal test:

- Disables shop purchase buttons when the backend Billing verification endpoint is not configured.
- Adds localized "payment not available yet" copy across zh-CN, zh-TW, en, ja, ko, and es.
- Keeps planned premium benefits visible while preventing accidental paid-flow entry before verification backend is live.
- Keeps route data availability notice, map fallback, local check-in, privacy deletion, and purchase guard intact.

V0.8.9 internal test:

- Adds a route-result data availability notice separating current local capabilities from production API requirements.
- Adds disabled chip state for unavailable controls.
- Marks photo upload as unavailable without changing check-in state.
- Keeps map fallback, manual check-in, local points, completion card, history, privacy deletion, and paid-purchase guard intact.

V0.8.8 internal test:

- Improves external map fallback: Amap first, browser map fallback, address copied only when no map target is available.
- Clarifies map fallback with user-facing toast messages.
- Changes photo upload mock control so it no longer marks a stop as checked in.
- Keeps manual check-in, local points, completion card, history, privacy deletion, and paid-purchase guard intact.

V0.8.7 internal test:

- Blocks Google Play purchase launch when the backend verification endpoint is not configured.
- Keeps paid products visible as planned offers, but prevents accidental unverified purchases.
- Removes premature shop "connecting" toast; Billing gateway now owns purchase status messages.
- Keeps in-app privacy/local data deletion and multilingual app shell.

V0.8.6 internal test:

- Adds an in-app Privacy & Local Data screen.
- Lets users clear locally stored route/history/check-in data inside the app.
- Keeps multilingual app shell and global route-planning prototype.
- No new sensitive permissions, no backend scraping, no real paid entitlement delivery yet.

V0.8.5 internal test:
- Global itinerary quest prototype.
- Card-flow preference input.
- Route result, map jump, local check-in points, history, completion card.
- Multilingual app shell and Play listing drafts for zh-CN, zh-TW, en, ja, ko, and es.
- Locale-aware itinerary generation for route summaries, check-in tasks, risk tips, POI mock copy, and compliance notes.
- First-pass Google Play screenshot drafts.
- Google Play Billing client flow with configurable HTTPS backend verification endpoint placeholder.
- Billing backend verification Worker skeleton and deployment checklist are included, but no live endpoint is configured yet.
```

## In-App Products

Create these products in Play Console before testing purchases:

- Subscription: `todayplay.plus.monthly`
- One-time product: `todayplay.itinerary.premium.once`
- One-time product: `todayplay.citypack.global`
- One-time product: `todayplay.photo.positions`

Do not mark paid features as live until `/billing/verify` is implemented and tested.

## Review Notes

Paste or adapt `google_play_review_notes.md`.
