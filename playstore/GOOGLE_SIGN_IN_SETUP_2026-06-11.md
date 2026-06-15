# Google Sign-In Setup - 2026-06-11

Current app checkpoint: `0.9.41 / versionCode 59`.

## What V0.9.41 Includes

- A local account session model used by home and share screens.
- A trial-safe local tester profile so the APK can be tested before OAuth is ready.
- A Google Credential Manager gateway wired behind `GOOGLE_WEB_CLIENT_ID`.
- Share text attribution: signed-in or tester profiles append `Shared from TodayPlay by ...`.
- Google ID tokens are treated as pending backend verification and are not persisted locally.

## What You Need To Prepare

1. Google Cloud / Google Auth Platform project
   - App package name: `com.todayplay.app`
   - Android OAuth client using the upload/debug certificate SHA-1 or SHA-256 for the build you test.
   - Web OAuth client ID for Android Credential Manager Sign in with Google.

2. Local release config
   - Copy `release_config.template.properties` to `release_config.properties`.
   - Fill:
     - `GOOGLE_WEB_CLIENT_ID=<your web OAuth client id>`
     - `AUTH_VERIFY_ENDPOINT=<your HTTPS backend token-verification endpoint>`
   - Rebuild the APK after changing this file.

3. Backend verification
   - The app must send Google ID tokens to your backend.
   - Backend must verify the token audience, issuer, expiry, and user identity before creating a trusted account session.
   - Do not trust Google account identity or grant paid entitlements from client-side parsing alone.

4. Privacy and deletion updates before store release
   - Update the privacy policy for account sign-in.
   - Add a public account/data deletion path if backend accounts or synced history are enabled.
   - Re-check Play Data Safety after account login, backend sync, analytics identity, or share-link import is enabled.

## Current Trial Behavior

- If `GOOGLE_WEB_CLIENT_ID` is empty, the Google button is disabled and the APK remains usable.
- Users can choose `Use local tester profile` to test share attribution.
- Account state is in memory only for this trial build; closing the app clears it.
