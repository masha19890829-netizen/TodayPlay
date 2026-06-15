# Screenshot QA Addendum - v0.9.29 / versionCode 47 - 2026-06-11

## Baseline

- Project: `D:\AppStore\nemu\real`
- Frozen version: `0.9.29 / versionCode 47`
- Package: `com.todayplay.app`
- Current debug APK: `app\build\outputs\apk\debug\app-debug.apk`, modified `2026-06-11 11:33:31`
- Current signed release AAB: `app\build\outputs\bundle\release\app-release.aab`, modified `2026-06-11 11:33:48`
- Submission gate status for screenshots: `warn`
- This addendum only updates screenshot QA baseline notes. It does not change code, generate screenshots, rebuild packages, or increment versions.

## Why The Existing Screenshots Cannot Be Submitted

The current `playstore\screenshots` folder contains four PNG screenshots:

| File | Size | Modified | Submission issue |
| --- | --- | --- | --- |
| `phone_01_home_route_picker.png` | 1080x1920 | `2026-06-08 16:04:16` | Stale against the `0.9.29 / 47` APK and AAB; draft/mock phone frame; old English-first home route picker; does not prove the current localized home UI, language selector, or current navigation state. |
| `phone_02_route_result.png` | 1080x1920 | `2026-06-08 16:04:16` | Stale; draft/mock frame; visible `?` separator risk in `3 stops ? 2h 30m ? ?100-300 per person`; does not prove current generated route result, map fallback, or current copy. |
| `phone_03_checkin_completion.png` | 1080x1920 | `2026-06-08 16:04:17` | Stale; draft/mock frame; visible `?` separator risk in `3 stops completed ? 90 reward points`; placeholder photo preview does not prove current check-in, completion card, save, share, or permission behavior. |
| `phone_04_premium_shop.png` | 1080x1920 | `2026-06-08 16:04:18` | Stale; draft/mock frame; shows a subscription CTA that can imply live Google Play purchase even though the submission gate still has `BILLING_VERIFY_ENDPOINT=None`; must be replaced by current guarded billing UI unless backend verification and Play products are configured. |

Reasons to reject the current set for Play submission:

- They are older than the frozen `0.9.29 / 47` artifacts from `2026-06-11 11:33`.
- Submission gate already reports `staleAgainstLatestAab`, `draftSource=True`, and `visibleQuestionSeparatorReviewRequired=True`.
- They are store-material drafts rather than real screenshots captured from the current APK on a device or emulator.
- Two screenshots contain visible `?` separators that can look like mojibake, broken formatting, or unfinished UI.
- The set does not cover the current end-to-end app path with real tapping: home, input/generation, result, map/check-in, history/completion, privacy/shop.

## Required Recapture Set

Capture 4-6 fresh phone screenshots from the installed `0.9.29 / 47` APK on a connected Android device or already-available emulator.

1. `phone_01_home_current_locale.png` - Home screen after launch, showing brand/app name, current locale state, primary route CTA, and navigation to History, Shop, and Privacy.
2. `phone_02_create_route_inputs.png` - Route creation/card-flow screen with relationship, city, time, budget, transport, and preference inputs. Use neutral sample values and no personal data.
3. `phone_03_route_result_overview.png` - Successful generated route result with route summary, stops, time/cost/reward copy, and no `?` separators or mojibake.
4. `phone_04_map_and_checkin.png` - Stop detail or result section showing map action plus manual check-in or completed check-in state.
5. `phone_05_history_or_completion.png` - Either a saved/generated route in History, or a completion/share/save card after check-in.
6. `phone_06_shop_guarded_billing.png` - Shop/Premium screen showing the current guarded paid-entry state. Until backend purchase verification exists, the screenshot must not imply that live subscription entitlement is fully available.

If only four screenshots can be submitted in the first refresh, prioritize 1, 2, 3, and 6. Keep 4 and 5 as QA evidence if they are not uploaded to Play.

## Screenshot Pass Standards

### Dimensions And File Quality

- Use real app screenshots, not marketing composites or artificial device frames.
- Recommended phone portrait size: `1080x1920` or the connected device's native portrait screenshot size.
- PNG is preferred; JPEG is acceptable only if text remains crisp.
- No blurred text, cropped buttons, clipped navigation, keyboard overlays, debug banners, notification shade, permission dialogs, crash dialogs, or transient toast overlays.
- Screenshot file modified time must be later than the target APK/AAB build time for the release being submitted.

### Copy And Encoding

- No visible `?` separators, mojibake, broken currency symbols, tofu boxes, placeholder glyphs, or truncated critical UI text.
- Time, cost, point, and stop-count separators should use normal localized punctuation or readable separators.
- Store screenshots must not claim live paid entitlement, live subscriptions, merchant perks, official map/POI coverage, or real social heat unless those services are configured and disclosed.
- Mock/sample content must remain truthful and must not appear as unauthorized third-party data.

### Status Bar And Device State

- Use a clean status bar: reasonable battery level, no low-battery warning, no active recording/privacy indicators unless unavoidable, no unrelated notifications.
- Prefer stable device time or normal current time; avoid obviously fake fixed UI if possible.
- Keep system font/display size at normal/default unless intentionally validating accessibility screenshots outside the Play upload set.
- Use portrait orientation for the main Play phone screenshot set.

### Language And Locale

- Primary upload set should match the target listing locale. For the Chinese listing, capture Simplified Chinese UI first.
- At minimum, spot-check English plus one long-text locale such as Japanese, Korean, Spanish, or Traditional Chinese for home/result/shop text fit.
- Language selector/current locale must match the visible copy; do not mix old English draft copy with Chinese store metadata.
- No clipped labels in chips, buttons, tab/navigation actions, card titles, or billing guard copy.

### Paid Entry / Billing

- Because `BILLING_VERIFY_ENDPOINT=None` is still a submission gate failure, paid-entry screenshots must show guarded or unavailable purchase behavior.
- Do not submit a screenshot that presents `Subscribe with Google Play` as a live working path unless Play Console products, internal test track, backend `/billing/verify`, entitlement delivery, cancellation, restore, and failure states have all been verified.
- If a shop screenshot is included before billing backend completion, it should communicate that paid functionality is reserved or not yet purchasable in this build.

## Device / Emulator Blocker

Current local environment check:

- adb exists at `D:\AppStore\.android-build-tools\sdk\platform-tools\adb.exe`.
- adb version: Android Debug Bridge `1.0.41`, version `37.0.0-14910828`.
- `adb devices -l` returned no connected devices.
- `adb shell wm size` failed with `no devices/emulators found`.
- No usable Android emulator binary was confirmed in the available SDK paths.
- Current visible related processes are adb processes only; no usable third-party emulator target was confirmed.
- No large emulator or SDK component installation was attempted.

Result: this workstation still cannot complete real device tapping, map fallback verification, billing button tapping, share/save verification, or screenshot recapture until a real Android device or an already-installed emulator is connected.

## Recapture Command Template

After connecting a device or already-available emulator:

```powershell
$adb = 'D:\AppStore\.android-build-tools\sdk\platform-tools\adb.exe'
& $adb devices -l
& $adb install -r 'D:\AppStore\nemu\real\app\build\outputs\apk\debug\app-debug.apk'
```

For each approved scene:

```powershell
& $adb shell screencap -p /sdcard/todayplay_screen.png
& $adb pull /sdcard/todayplay_screen.png 'D:\AppStore\nemu\real\playstore\screenshots\<target_name>.png'
```

After recapture, rerun the submission gate and confirm the screenshot row no longer reports stale files, draft source, or visible question-separator review risk.

## Handoff Conclusion

For `0.9.29 / versionCode 47`, the screenshot gate remains blocked by stale draft assets and lack of connected capture hardware. Replace all four existing screenshots with 4-6 current real-device captures before Play submission.
