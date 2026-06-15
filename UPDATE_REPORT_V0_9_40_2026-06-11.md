# TodayPlay V0.9.40 Update Report - 2026-06-11

## Review Lens

Project lead coordination with:
- Product Manager / Test Lead: route cockpit acceptance, 360dp screen density, state-sync risks.
- Product Planning / Market Research: reusable route-pack mechanics and competitor-inspired execution reassurance.
- App Art UI Motion Designer: warm city-ticket visual direction, route-line cues, current/done/todo readability, screenshot appeal.

Decision: ship a UI-focused route cockpit iteration. The result page should help users immediately understand "where am I, what do I do next, and how far through the route am I" without turning the top area into a route editor.

## Problems Found

- V0.9.39 made the route actionable, but the live execution card still read like a dense information strip rather than a polished in-use cockpit.
- Current stop, next action, reward, progress, and next stop needed stronger visual grouping for small screens.
- The result page had no compact timeline near the active stop, so users could not quickly sense route order without scrolling through every stop.
- Home City Theme cards worked as route entry points, but visually they still felt closer to static content cards than themed route packs.
- PM and UI review both warned against duplicating swap, replacement, and restore controls in the top execution area.

## Changes Shipped

- Bumped Android version to `0.9.40 / versionCode 58`.
- Reworked `RouteExecutionCard` into a route cockpit:
  - stronger current-stop panel
  - next-action tile
  - reward tile
  - progress tile
  - next-stop tile
- Added `RouteProgressTimeline` and `TimelineDot` states around the active stop.
- Kept top-level actions focused on:
  - open map
  - complete or undo current stop check-in
- Kept swap, replacement preview, confirmed replacement, and restore previous stop inside the full stop cards.
- Added a small `CityThemeRouteMotif` to home City Theme cards, including route dots/line and up to three route tags.
- Expanded app regression audit coverage for:
  - route cockpit tokens
  - execution timeline tokens
  - City Theme route motif tokens
- Hardened release/documentation checks so V0.9.40 version sync remains visible in Play release materials.

## Validation

- `assembleDebug`: pass.
- `lintDebug bundleRelease`: pass.
- APK metadata: `com.todayplay.app`, `0.9.40`, `58`.
- Release AAB signature: `META-INF/TODAYPLA.SF`, `META-INF/TODAYPLA.RSA`.
- `playstore/app_regression_audit.py`: pass, including route cockpit and City Theme motif guards.
- `playstore/localization_copyfit_audit.py`: pass.
- `playstore/run_release_preflight.ps1`: pass for local/internal checks.
- `playstore/google_play_submission_gate.py`: expected overall fail from external launch items only.

## Artifacts

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
  - Size: `17,090,289` bytes.
  - Built: `2026-06-11 15:16:02`.
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`
  - Size: `9,392,453` bytes.
  - Built: `2026-06-11 15:23:33`.

## Remaining Blockers

- No Android device or emulator is attached, so real-device smoke and fresh screenshots are still not verified.
- Play Console support email, public HTTPS privacy URL, release `SUPPORT_EMAIL`, release `PRIVACY_POLICY_URL`, and real Billing verify endpoint are still missing.
- Current store screenshots are stale/draft and must be recaptured from the current build before upload.

## Recommended Next Iteration

V0.9.41 should focus on one of two tracks:
- Product retention: favorite/replay templates, richer City Theme packs, or collectible route-ticket history.
- Visual/store polish: recapture screenshots from V0.9.40, tune route cockpit density on small screens, and add completion feedback motion after device review.
