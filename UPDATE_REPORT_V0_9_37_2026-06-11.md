# V0.9.37 Route Restore Update Report

Date: 2026-06-11
Version: `0.9.37 / versionCode 55`
Owner view: project lead coordinating product, market research, Android development, and release QA
Theme: add a one-step restore path after replacing a route stop

## Background

V0.9.36 made single-stop replacement safer by showing a candidate preview before confirming. The next product risk was edit anxiety: after replacing one stop, users still needed a way to recover if the new stop felt worse.

This iteration adds a light restore model rather than a heavy multi-version editor. It keeps one local restore snapshot for the latest replaced stop.

## Shipped

1. Local restore snapshot
   - Added `RouteStopRestoreSnapshot`.
   - `QuestProgress` now stores `lastRouteStopRestore`.
   - The snapshot stores the previous stop, prior task status, feedback reasons, reward points, replacement name, and timestamp.

2. History-compatible persistence
   - `QuestHistoryStore` now serializes and reads `lastRouteStopRestore`.
   - Old history records remain compatible because the new field is optional.

3. Restore logic
   - Confirmed replacement now saves the old stop snapshot before clearing stale stop progress.
   - Restore replaces the current stop with the previous stop.
   - Restore brings back prior check-in status, feedback, and reward points.
   - Restore clears the snapshot after use, keeping the model as one-step undo.

4. UI entry
   - The route stop card now shows a latest replacement panel when a previous stop can be restored.
   - The panel explains which stop was replaced and exposes `Restore previous stop`.
   - Restore uses the same local history path, so the restored route remains available when reopened from history.

5. Regression guard
   - `playstore/app_regression_audit.py` now includes `Route stop restore path`.
   - The guard checks the model snapshot, history serialization, generator restore logic, repository/ViewModel wiring, MainActivity callback, and result-page restore panel.

## Validation

- `assembleDebug`: pass
- `lintDebug`: pass
- `bundleRelease`: pass
- APK metadata: `com.todayplay.app`, `versionName=0.9.37`, `versionCode=55`
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
  - Size: `17,057,521` bytes
  - Built: `2026-06-11 13:24:18`
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`
  - Size: `9,363,669` bytes
  - Built: `2026-06-11 13:27:01`
  - Signature files: `META-INF/TODAYPLA.SF`, `META-INF/TODAYPLA.RSA`
- `playstore/app_regression_audit.py`: pass, including new restore path
- `playstore/google_play_submission_gate.py`: expected overall `fail`
  - Pass: versioning, target SDK, release AAB, AAB signature, artifact freshness, upload key, release docs sync, privacy site, local data deletion, map/mock guard, content API boundary, Billing purchase guard, backend skeleton/contract
  - Warn: debug APK is local-only, screenshots are stale/draft
  - Fail: real Play support email, public HTTPS privacy URL, release `SUPPORT_EMAIL`/`PRIVACY_POLICY_URL`, and real Billing verify endpoint are still missing
- Device smoke: not run, because `adb devices` shows no connected Android device or emulator.

## Product Notes

- PM and market research roles were dispatched to evaluate restore/undo and lightweight version history.
- The next local product iteration should likely move from route-edit safety to richer city theme cards, or expand the restore model into a visible edit-history strip if PM confirms users need more than one undo.

## Artifacts

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`
- Regression audit report: `playstore/app_regression_audit_report.md`
- Submission gate report: `playstore/submission_gate_report.md`
