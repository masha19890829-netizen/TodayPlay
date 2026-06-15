# Release Ops External Test Retro - V0.9.58

Date: 2026-06-15
Role: 发布上架负责人
Project: `D:\AppStore\nemu\real`

## Conclusion

V0.9.58 is an **external test APK**, not a Google Play store submission package and not a production launch.

The current delivery is suitable for external testers to evaluate product experience, visual direction, route-card clarity, result-page execution flow, loading motion, and foldable/small-screen readability. It must not be marketed as a live store release, a Play Console internal test release, or a production app with real login/payment/backend services.

## Verified External Test Artifact

| Item | Verified value |
| --- | --- |
| APK | `dist/TodayPlay-v0.9.58-external-test.apk` |
| Package | `com.todayplay.app` |
| Version | `0.9.58 / versionCode 76` |
| APK SHA-256 | `150DEB4CA547709650B193733A9D3B576C92E4CBA52400DD4B80482BB3BC78FF` |
| APK signature | APK Signature Scheme v2 verified |
| Signing certificate SHA-256 | `2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695` |
| Active permission declared by app | `android.permission.INTERNET` |
| BuildConfig external services | `GOOGLE_WEB_CLIENT_ID`, `AUTH_VERIFY_ENDPOINT`, `BILLING_VERIFY_ENDPOINT`, `TRAVEL_CONTENT_BASE_URL`, `PRIVACY_POLICY_URL`, and `SUPPORT_EMAIL` are empty |

## Source Reports Reviewed

- `UPDATE_REPORT_V0_9_58_2026-06-15.md`
- `EXTERNAL_TESTER_GUIDE_V0_9_58_2026-06-15.md`
- `TEAM_SYNC_V0_9_58_2026-06-15.md`
- `playstore/STORE_CONVERSION_COMPLIANCE_BOUNDARY_V0_9_58_2026-06-15.md`
- APK metadata and signing verification for `dist/TodayPlay-v0.9.58-external-test.apk`

## Promotion Boundary Review

The current report language is compliant for an external APK test:

- It states V0.9.58 is an external testing candidate.
- It lists the test target as first-screen appeal, route-card clarity, channel understanding, result-page next action, and foldable/screen readability.
- It explicitly says the build does not connect real Google login, real payment/subscription, Kimi/API planning, real map SDK, or Play Console release flow.
- It describes routes as local/operational samples rather than live platform data.
- It keeps Billing/payment claims disabled and does not promise purchase, subscription, or entitlement delivery.

Approved external-test claims:

- Test the new image-led home experience.
- Test the three channels: 心动路线, 今晚组局, 慢慢走.
- Test local sample route cards, route generation, loading motion, result page, current stop action, and map handoff.
- Test ordinary phone and foldable screen readability.
- Collect feedback on whether users understand what to do within the first two minutes.

Claims that remain prohibited:

- Google Play release is live.
- Google Play internal testing is already configured.
- Real Google login is connected.
- Real payment, subscription, membership, or paid entitlement is live.
- Real map SDK, real location, real-time navigation, real traffic, or official map data is connected.
- Real UGC, social heat, friend activity, ratings, rankings, or user reviews are present.
- Kimi/API/AI backend is live and making real recommendations.
- Store screenshots can imply fake ratings, fake popularity, fake social relationships, or unavailable paid/login features.

## Required External-Test Labeling

For testers, keep using labels like:

- "External test APK"
- "Local sample route content"
- "Current build does not include real Google login"
- "Payment/subscription is not enabled in this test"
- "Map actions open external map paths; the app does not provide real-time map data"
- "Please test first impression, route clarity, loading, result-page action, and screen compatibility"

Avoid labels like:

- "正式版"
- "已上架"
- "真实热门"
- "真实评分"
- "会员已开通"
- "Google 登录已上线"
- "AI 实时推荐"
- "官方地图路线"

## Materials Still Missing Before Formal Store Release

These are not blockers for the V0.9.58 external APK test, but they are required before a real Google Play submission.

### Play Console And Account

- Google Play developer account.
- Developer account type: personal or organization.
- Public developer name.
- Developer country/region.
- Real monitored support email.
- Release country/region plan.
- Play Console app record for `com.todayplay.app`.
- Internal testing track setup if moving from direct APK testing to Play distribution.

### Privacy And Compliance

- Public HTTPS privacy policy URL.
- Real publisher/developer identity in the privacy policy.
- Privacy/data request contact.
- Data Safety form answers matched to the exact release build.
- Content rating questionnaire.
- Target audience and ads declaration.
- Account/data deletion route if real backend accounts are enabled.

### Google Login

- Google Cloud / Google Auth Platform project.
- Android OAuth client bound to `com.todayplay.app` and the correct signing certificate fingerprints.
- Web OAuth client ID for `GOOGLE_WEB_CLIENT_ID`.
- HTTPS `AUTH_VERIFY_ENDPOINT`.
- Server-side Google ID token verification.
- Privacy/Data Safety update for account sign-in and any synced data.

### Payment And Entitlements

- Decision on whether paid products are disabled, internal-test only, or production-ready.
- Play Billing products and prices:
  - `todayplay.plus.monthly`
  - `todayplay.itinerary.premium.once`
  - `todayplay.citypack.global`
  - `todayplay.photo.positions`
- Real HTTPS `BILLING_VERIFY_ENDPOINT`.
- Google Play Developer API access.
- Backend service account configured only in backend secrets.
- Durable entitlement storage.
- RTDN/Pub/Sub handling for subscriptions before production subscription launch.
- Refund, cancellation, expiry, grace-period, and revocation handling.

### Store Assets And Review Materials

- Current-build Play screenshots, not draft or stale screenshots.
- Store feature graphic and icon final approval.
- Localized store listings review.
- Review notes explaining local sample content, external maps, disabled payment/login state, and data boundaries.
- Asset copyright/license confirmation for any image, font, POI, map, merchant, or third-party content used in store materials.

### Build And Submission

- AAB build for Play upload, separate from direct external-test APK.
- Fresh submission gate against the exact AAB.
- VersionCode not previously consumed by Play.
- Play App Signing enabled.
- Upload key handling confirmed.
- Final release notes for Play internal testing or production.

## Release Ops Decision

V0.9.58 may continue as an external APK test with the current boundary. The published reports are acceptable as long as distribution copy preserves the same limits:

- External test APK only.
- No store launch claim.
- No real login/payment/backend/map/UGC/AI claim.
- Testers are asked to evaluate current runnable paths.

Formal Google Play submission remains blocked until the missing external account, privacy, Data Safety, login, Billing, screenshot, and Play Console materials are provided and rechecked.

No code, Gradle, keystore, APK, or AAB changes were made for this retro.
