# TodayPlay V0.9.39 Update Report - 2026-06-11

## Review Lens

Project lead coordination with:
- Product Manager / Tester: result-page execution mode acceptance and state-sync risks.
- Product Planning / Market Research: minimum viable execution mode inspired by itinerary and route products.

Decision: ship a lightweight live route mode in the result page. Keep risky route-editing decisions inside stop cards, as PM requested.

## Problems Found

- The result page already had rich route details, but users still had to scroll and interpret the route before knowing the immediate next step.
- Current stop, next action, reward, and route-stop progress were spread across overview and stop cards.
- Putting swap, replacement, and restore into a top execution bar would make the page too dense and increase accidental edits.

## Changes Shipped

- Bumped Android version to `0.9.39 / versionCode 57`.
- Added `RouteExecutionCard` near the top of the result page.
- The card automatically selects the first route stop that is not completed or skipped.
- The card shows:
  - current stop
  - next action
  - available check-in points
  - stop progress
  - next unresolved stop
- The card exposes only low-risk actions:
  - open map
  - complete or undo current stop check-in
- Swap, replacement preview, confirmed replacement, and restore previous stop remain inside the full stop card.
- Added app regression audit coverage for `Result page live route mode`.

## Validation

- `assembleDebug`: pass.
- `lintDebug bundleRelease`: pass.
- APK metadata: `com.todayplay.app`, `0.9.39`, `57`.
- Release AAB signature: `META-INF/TODAYPLA.SF`, `META-INF/TODAYPLA.RSA`.
- `playstore/app_regression_audit.py`: pass, including new live route mode guard.
- `playstore/google_play_submission_gate.py`: expected overall fail from external launch items only.

## Artifacts

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
  - Size: `17,073,905` bytes.
  - Built: `2026-06-11 14:52:37`.
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`
  - Size: `9,377,071` bytes.
  - Built: `2026-06-11 14:56:27`.

## Remaining Blockers

- No Android device or emulator is attached, so real-device smoke and fresh screenshots are still not verified.
- Play Console support email, public HTTPS privacy URL, release `SUPPORT_EMAIL`, release `PRIVACY_POLICY_URL`, and real Billing verify endpoint are still missing.

## Recommended Next Iteration

V0.9.40 should either expand live mode with a compact step timeline or improve history retention with a favorite/replay template, depending on PM review of whether the execution card is clear enough in real use.
