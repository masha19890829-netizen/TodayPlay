# V0.9.36 Replacement Preview Update Report

Date: 2026-06-11
Version: `0.9.36 / versionCode 54`
Owner view: project lead coordinating product, market research, Android development, and release QA
Theme: make single-stop replacement safer by previewing the candidate before changing the route

## Background

V0.9.35 let users keep a route and replace only one disliked stop. That solved the "whole route redraw" problem, but the action was still abrupt: the user tapped once and immediately lost the current stop.

This iteration turns the flow into preview -> confirm. It keeps the current local/offline candidate algorithm and adds a clear decision moment before changing the route.

## Shipped

1. Replacement candidate preview model
   - Added `RouteStopReplacementPreview`.
   - Preview exposes the original stop, candidate stop, district, category, tags, stay minutes, same-category signal, matched tags, stay-time delta, and source label.

2. Shared candidate selection
   - `LocalItineraryGenerator` now uses one `selectReplacement` helper for both preview and confirmed replacement.
   - Ranking still prioritizes same city, unused POIs, same category, matching tags, and similar stay duration.

3. Repository and ViewModel wiring
   - `QuestRepository.previewRouteStopReplacement(...)` returns a preview without saving.
   - `TodayPlayViewModel.previewRouteStopReplacement(...)` logs `route_stop_replace_preview`.
   - The existing confirmed replacement path still persists the updated route and clears stale progress.

4. Result page UX
   - Route stop cards now show `Preview new stop` instead of immediately replacing.
   - The preview panel shows the candidate name, district, stay duration, category, tags, source, and match signals.
   - Users can confirm replacement or keep the current stop.
   - Confirmed replacement still keeps the rest of the route and clears old check-in state, feedback, and points for that stop.

5. Regression guard
   - `playstore/app_regression_audit.py` now includes `Route stop replacement preview path`.
   - The guard checks preview generation, repository/ViewModel wiring, MainActivity callback, UI preview panel, and confirm/cancel copy.

## Validation

- `assembleDebug`: pass
- `lintDebug`: pass
- `bundleRelease`: pass
- APK metadata: `com.todayplay.app`, `versionName=0.9.36`, `versionCode=54`
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
  - Size: `17,057,521` bytes
  - Built: `2026-06-11 13:10:37`
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`
  - Size: `9,357,859` bytes
  - Built: `2026-06-11 13:13:20`
  - Signature files: `META-INF/TODAYPLA.SF`, `META-INF/TODAYPLA.RSA`
- `playstore/app_regression_audit.py`: pass, including new preview path
- `playstore/google_play_submission_gate.py`: expected overall `fail`
  - Pass: versioning, target SDK, release AAB, AAB signature, artifact freshness, upload key, release docs sync, privacy site, local data deletion, map/mock guard, content API boundary, Billing purchase guard, backend skeleton/contract
  - Warn: debug APK is local-only, screenshots are stale/draft
  - Fail: real Play support email, public HTTPS privacy URL, release `SUPPORT_EMAIL`/`PRIVACY_POLICY_URL`, and real Billing verify endpoint are still missing
- Device smoke: not run, because `adb devices` shows no connected Android device or emulator.

## Product Notes

- PM and market research roles were dispatched again to review V0.9.35/V0.9.36 route editing and propose the next roadmap slice.
- The strongest next local iteration is likely either route-edit undo/history clarity or city theme cards that make route generation feel more curated.

## Artifacts

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`
- Regression audit report: `playstore/app_regression_audit_report.md`
- Submission gate report: `playstore/submission_gate_report.md`
