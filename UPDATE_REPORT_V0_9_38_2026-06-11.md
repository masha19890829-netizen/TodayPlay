# TodayPlay V0.9.38 Update Report - 2026-06-11

## Review Lens

Project lead coordination with:
- Product Manager / Tester: target-user UX, next core-experience priority.
- Product Planning / Market Research: competitor-inspired local mechanism, avoiding external API and content licensing risk.

Decision: PM recommended result-page execution mode as the next deep UX step, while market planning recommended City Themes for content density. Because the account owner asked to prioritize version experience and content filling before listing details, V0.9.38 ships City Themes first.

## Problems Found

- The home screen had Today Picks, but it still felt closer to inspiration cards than a clear city-content module.
- Market research recommended learning from city-guide and itinerary products by presenting theme-led entry points, not long place lists.
- The wide home layout needed scroll protection before adding more first-screen content.

## Changes Shipped

- Bumped Android version to `0.9.38 / versionCode 56`.
- Added localized home City Theme model data:
  - Guangzhou old-street snack friend route.
  - Hangzhou lakeside solo reset route.
  - Taipei boundary-safe night walk route.
- Added a new home `City Themes` section after Today Picks.
- Each city theme card shows city, title, short scenario copy, tags, and a one-tap generate action.
- Home wide layout right column is now scrollable so extra content does not overflow on shorter desktop windows.
- Added app regression audit coverage for `Home city theme route entry`.

## Validation

- `assembleDebug`: pass.
- `lintDebug bundleRelease`: pass.
- APK metadata: `com.todayplay.app`, `0.9.38`, `56`.
- Release AAB signature: `META-INF/TODAYPLA.SF`, `META-INF/TODAYPLA.RSA`.
- `playstore/app_regression_audit.py`: pass, including new City Themes guard.
- `playstore/google_play_submission_gate.py`: expected overall fail from external launch items only.

## Artifacts

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
  - Size: `17,073,905` bytes.
  - Built: `2026-06-11 13:38:37`.
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`
  - Size: `9,369,550` bytes.
  - Built: `2026-06-11 13:44:14`.

## Remaining Blockers

- No Android device or emulator is attached, so real-device smoke and fresh screenshots are still not verified.
- Play Console support email, public HTTPS privacy URL, release `SUPPORT_EMAIL`, release `PRIVACY_POLICY_URL`, and real Billing verify endpoint are still missing.

## Recommended Next Iteration

V0.9.39 should likely follow the PM recommendation: add a lightweight result-page execution strip showing current stop, next action, expected reward, and safe progress actions. This would turn the route result from a rich plan into a more usable live play mode.
