# Google Play Screenshot Plan

Current limitation: no Android device or emulator is connected in this workspace, so live device screenshots cannot be captured in this turn.

Current progress: 4 first-pass 1080x1920 Play phone screenshot drafts have been generated in `playstore/screenshots`.

Generated files:

1. `phone_01_home_route_picker.png`
2. `phone_02_route_result.png`
3. `phone_03_checkin_completion.png`
4. `phone_04_premium_shop.png`

These files can be used for Play Console layout rehearsal and copy review. Before production submission, run the APK on a real device or emulator and replace any screenshot that does not match the live app behavior.

## Required screenshots

Google Play phone listing needs at least 2 phone screenshots. Recommended set:

1. Home screen: "今天去哪玩，和谁一起"
2. Card-flow input: relationship/city/preference selection
3. Route result: itinerary overview
4. Route stop card: map entry and check-in
5. Completion card: check-in count and points
6. Shop screen: Plus and paid route products

## Capture command plan

After connecting a device or emulator:

```powershell
$env:ANDROID_HOME='D:\AppStore\.android-build-tools\sdk'
$env:PATH="$env:ANDROID_HOME\platform-tools;$env:PATH"
adb devices
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell screencap -p /sdcard/todayplay-home.png
adb pull /sdcard/todayplay-home.png playstore\screenshots\todayplay-home.png
```

Repeat after navigating to each key screen. If the generated drafts are kept, compare them against live app screens for text, pricing, paid-state, map behavior, and compliance disclosures.

## Recommended dimensions

- Phone portrait: 1080x1920 or similar.
- Avoid screenshots with debug toasts, system error dialogs, or empty states.
- Use actual app UI rather than marketing mockups for required screenshots.

## Copy overlays

Do not add misleading claims such as "real global social-platform heat" or "live paid membership" until those backend/data integrations exist.
