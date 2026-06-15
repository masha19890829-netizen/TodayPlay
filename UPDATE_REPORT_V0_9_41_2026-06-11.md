# TodayPlay V0.9.41 Update Report - 2026-06-11

## Review Lens

Project lead coordination with:
- Product Manager / Test Lead: try the current core flow and find the next practical usability gap.
- Android Developer: ship a signed APK that the owner can install and test.
- Account / Sharing Planning: prepare Google sign-in without faking a configured OAuth/backend system.

Decision: ship an account-aware sharing slice before app-store submission work. The current APK should remain usable without OAuth credentials, while still making the future Google sign-in path real and explicit.

## Problems Found

- The share flow could send route invites and completion text, but the sender was anonymous.
- The app had no account session concept, which would make future cross-device history, paid entitlements, and share identity harder to add safely.
- A real Google sign-in integration cannot be finished without a Google Web OAuth client ID and backend token verification endpoint.
- The trial APK still needs to work today, before the external OAuth setup exists.

## Changes Shipped

- Bumped Android version to `0.9.41 / versionCode 59`.
- Added Credential Manager dependencies for future Google sign-in:
  - `androidx.credentials:credentials:1.6.0`
  - `androidx.credentials:credentials-play-services-auth:1.6.0`
  - `com.google.android.libraries.identity.googleid:googleid:1.2.0`
- Added `AccountSession`, `AccountProvider`, and `AccountSignInResult`.
- Added `GoogleAccountGateway` using Credential Manager and `GetGoogleIdOption`.
- Added release config fields:
  - `GOOGLE_WEB_CLIENT_ID`
  - `AUTH_VERIFY_ENDPOINT`
- Added home account card:
  - `Continue with Google` is disabled until OAuth is configured.
  - `Use local tester profile` lets the trial APK test account-aware sharing immediately.
  - Signed-in/tester state can be cleared with `Sign out`.
- Added share-page account section.
- Share invite and completion text now append `Shared from TodayPlay by ...` when an account/tester profile is active.
- Added regression audit coverage for account model, Google gateway, local tester fallback, and share attribution.
- Added setup documents:
  - `playstore/GOOGLE_SIGN_IN_SETUP_2026-06-11.md`
  - `playstore/USER_REQUIRED_ACCOUNT_INFO_2026-06-11.md`

## Validation

- `assembleDebug`: pass.
- `lintDebug assembleRelease`: pass.
- Lint result: `0 errors / 5 warnings`.
  - Remaining warnings are dependency update notices for Compose, Activity, Lifecycle, and Billing.
- `playstore/app_regression_audit.py`: pass, including the new account sign-in scaffold check.
- Release APK metadata:
  - package: `com.todayplay.app`
  - versionName: `0.9.41`
  - versionCode: `59`
- APK signing:
  - `apksigner verify`: pass.
  - APK Signature Scheme v2: true.
  - signer: `CN=TodayPlay Upload, OU=TodayPlay, O=TodayPlay, L=Shenzhen, ST=Guangdong, C=CN`.

## Artifacts

- Final trial APK: `dist/TodayPlay-v0.9.41-release.apk`
  - Size: `8,017,942` bytes.
  - Built: `2026-06-11 18:35:13`.
- Release APK source path: `app/build/outputs/apk/release/app-release.apk`
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`

## What Is Not Yet Verified

- No Android device or emulator is connected, so I could not install and tap through the APK locally.
- Google sign-in is not live in this APK because `GOOGLE_WEB_CLIENT_ID` is empty.
- Backend Google ID token verification is not implemented or configured.
- Account state is memory-only in this trial build; closing the app clears it.

## Recommended Owner Test

1. Install `dist/TodayPlay-v0.9.41-release.apk`.
2. Open the app and choose `Use local tester profile` on the home account card.
3. Generate a route.
4. Open Share.
5. Confirm the account section shows `TodayPlay tester`.
6. Share an invite or completion result and check that the text includes `Shared from TodayPlay by TodayPlay tester`.
7. Return home, sign out, and confirm sharing no longer adds attribution.

## Next Iteration

V0.9.42 should focus on a real-device pass:
- confirm first launch, route generation, result cockpit, share, history, privacy, shop guard, and map fallback on your phone
- capture screenshots of the current build
- tune any text overflow or button density issues found on the real device
- once OAuth info is ready, enable Google sign-in and add backend token verification
