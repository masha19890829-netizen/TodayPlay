# Device & Store Screenshot QA Plan - 2026-06-11

## Scope

- Project: `D:\AppStore\nemu\real`
- Baseline: `versionName=0.9.27`, `versionCode=45`, package `com.todayplay.app`
- Current test APK: `app\build\outputs\apk\debug\app-debug.apk`, modified `2026-06-11 11:20:11`
- Current signed release AAB: `app\build\outputs\bundle\release\app-release.aab`, modified `2026-06-11 11:20:36`
- This QA pass is read-only except for this plan file. No app code changes, no generated screenshots, no version increment.

## Local Device And Capture Environment Check

| Item | Result | Evidence / note |
| --- | --- | --- |
| adb on PATH | Blocked | `adb.exe` is not available through PATH. |
| bundled adb path | Available | `D:\AppStore\.android-build-tools\sdk\platform-tools\adb.exe`, Android Debug Bridge `1.0.41`, version `37.0.0-14910828`. |
| connected adb devices | Blocked | `adb devices -l` returned an empty device list. |
| screenshot command | Blocked | `adb shell wm size` and `adb shell screencap` both failed with `no devices/emulators found`. |
| Android emulator binary | Not available | No emulator binary found at common SDK paths or `D:\AppStore\.android-build-tools\sdk\emulator\emulator.exe`. |
| third-party emulator processes | Not usable | Only an `adb` process was found. No visible/common emulator process such as Nox, MEmu, LDPlayer, BlueStacks, Android Emulator, or QEMU was available. |
| large component install | Not attempted | Per task boundary, no emulator image or large SDK component was installed. |

Current blocker: this workstation cannot perform live device tapping or fresh device screenshots until a real Android device or already-installed emulator is connected.

## Existing Screenshot Inventory

All current PNG files in `playstore\screenshots` are 1080x1920 and meet the basic phone portrait pixel size expectation. They do not meet the current-build evidence requirement because all were modified on `2026-06-08 16:04`, before the current `0.9.27 / 45` APK and AAB from `2026-06-11 11:20`.

| File | Size | Problems | Key path coverage |
| --- | --- | --- | --- |
| `phone_01_home_route_picker.png` | 1080x1920 | Stale against current build; draft/mock phone frame rather than live device capture; old English-first UI copy (`Where to go today?`) does not match the current localized app baseline where the default Simplified Chinese home copy is `今天去哪玩，和谁一起？`; does not show current language selector and current home navigation exactly as shipped. No visible `?` separator found in this image. | Partially covers home/route picker, but not enough for current-version submission evidence. |
| `phone_02_route_result.png` | 1080x1920 | Stale; draft/mock phone frame; visible bad separators in `3 stops ? 2h 30m ? ?100-300 per person`; old English mock route result; does not prove current generated route UI, map fallback, localized copy, or real result state. | Partially covers result overview and stop cards, but missing live generation proof and contains visible `?` risk. |
| `phone_03_checkin_completion.png` | 1080x1920 | Stale; draft/mock phone frame; visible bad separator in `3 stops completed ? 90 reward points`; uses placeholder photo preview art, not live app output; does not prove current manual check-in, completion-card save/share, permission, or local-record behavior. | Partially covers completion/check-in concept, but not enough for current current-build QA or Play submission. |
| `phone_04_premium_shop.png` | 1080x1920 | Stale; draft/mock phone frame; shows `Subscribe with Google Play`, which can mislead while `BILLING_VERIFY_ENDPOINT` is still missing and live entitlement verification is not configured; does not prove the current guarded shop state from the app. No visible `?` separator found in this image. | Partially covers shop concept, but should be replaced with current guarded billing UI until backend and Play products are live. |

Conclusion: replace all four screenshots before Play submission. They are acceptable only as layout/copy rehearsal drafts.

## Required Recapture Set

Capture 4-6 current, real screenshots from the installed current APK on a real device or already-available emulator. Recommended minimum set:

1. `phone_01_home_current_locale.png` - first launch/home screen showing app name, language selector or current locale state, primary route CTA, History/Shop/Privacy navigation.
2. `phone_02_create_route_flow.png` - route creation/card-flow screen showing relationship, city, time/budget/transport/preferences inputs without personal data.
3. `phone_03_route_result_overview.png` - successful route result showing route summary, stops, estimated time/cost text, and no `?` separators or mojibake.
4. `phone_04_stop_map_checkin.png` - a stop detail area showing open-map action and manual check-in state or completed check-in state.
5. `phone_05_history_or_completion_card.png` - either history after generating a route, or completion/share/save card after check-in.
6. `phone_06_shop_guarded_state.png` - shop screen showing the current billing guard/disclosure. Until backend verification and Play products are live, it must not imply a completed purchasable subscription path.

Optional spot checks for internal QA evidence, not necessarily all Play listing uploads:

- Privacy & Local Data screen with real configured support/privacy status or clear not-configured state.
- English, Japanese, Korean, Spanish, and Traditional Chinese home/result screens for copy fit.
- Small-screen 360dp and foldable/tablet screenshots for adaptive UI confidence.

## Capture Steps

Use the existing adb if a device appears:

```powershell
$adb = 'D:\AppStore\.android-build-tools\sdk\platform-tools\adb.exe'
& $adb devices -l
& $adb install -r 'D:\AppStore\nemu\real\app\build\outputs\apk\debug\app-debug.apk'
```

For each scenario:

```powershell
& $adb shell screencap -p /sdcard/todayplay_screen.png
& $adb pull /sdcard/todayplay_screen.png 'D:\AppStore\nemu\real\playstore\screenshots\<target_name>.png'
```

Before saving each final Play screenshot:

- Navigate by tapping the real app UI, not by composing a marketing mock.
- Avoid debug banners, system error dialogs, toasts, notification shade, keyboard overlays, and personal account data.
- Use the current target package and build, not an older installed APK.
- Prefer portrait phone captures for the store listing set.

## Dimension And Format Requirements

- File format: PNG or JPEG.
- Phone screenshot count: at least 2 for Play Console; recommended 4-6 for this app.
- Dimension: keep portrait phone screenshots around 1080x1920 when possible.
- Aspect ratio: valid phone screenshot ratio, no artificial device frame required.
- Quality: no blurred text, clipped controls, cropped status/nav bars that hide app content, or non-app marketing overlays.
- Freshness: screenshot modified time must be later than the target APK/AAB build time for the release being submitted.

## Pass Criteria

A screenshot set passes this QA gate when all of the following are true:

- Captured from a connected real device or already-installed emulator running the current target APK.
- Every uploaded screenshot is newer than the target APK/AAB build.
- No visible `?` separators, mojibake, placeholder glyphs, broken currency symbols, or truncated critical copy.
- Current UI matches the app source and target build: localized home, route generation, result, check-in/history, privacy/shop behavior.
- Billing screenshot is truthful: if backend verification is not configured, the UI must show a guarded or unavailable purchase state rather than implying live subscription entitlement.
- Map/open-map screenshot does not imply guaranteed official map data, live POI availability, or real social/platform heat unless those data sources are configured and disclosed.
- Key user path is covered: home -> input/generate -> result -> map/check-in -> history/completion -> shop/privacy.
- No personal data, unsupported claims, third-party copyrighted content, or unauthorized POI imagery is visible.

## Current Execution Blockers

- No connected adb device was available on `2026-06-11`.
- No usable emulator binary or AVD was present in the available SDK locations.
- No third-party emulator process was available for capture.
- Therefore this QA pass could not perform real tapping, screen recording, screenshot capture, map fallback verification, billing UI tapping, share sheet verification, or small-screen/foldable visual QA.

## Handoff Summary

The current screenshot folder is not ready for Play submission. Keep the four existing images only as drafts, then replace them after connecting a real Android device or already-installed emulator and installing the current `0.9.27 / 45` APK.
