# Release Ops Action Plan - Google Play Submission

Date: 2026-06-11
Project: `D:\AppStore\nemu\real`
Release baseline: `0.9.40 / versionCode 58`
Role: Release Operations Lead

## Executive Status

Current Google Play submission gate result: **fail**.

Local release preparation is largely complete:

- Versioning passes: `versionName=0.9.40`, `versionCode=58`.
- Release AAB exists: `app/build/outputs/bundle/release/app-release.aab`.
- Release AAB is signed and contains `META-INF/TODAYPLA.SF` and `META-INF/TODAYPLA.RSA`.
- Upload key configuration passes local checks.
- Store icon and feature graphic pass dimension checks.
- Local deployable privacy site package passes audit.
- Version documentation sync passes.
- Billing client is guarded so paid purchase flow is blocked until a real HTTPS verification endpoint exists.

Submission remains blocked by external launch items:

- TODO: real developer support email.
- TODO: public HTTPS privacy policy URL for `playstore/privacy_site`.
- TODO: real HTTPS Billing verify endpoint.
- TODO: current live-device or emulator screenshots, because existing screenshots are draft/stale against the latest AAB and require visible text review for `?` separators/mojibake.

No real support email, privacy URL, or Billing endpoint should be invented in local files or Play Console.

Account-owner intake checklist:

- Use `playstore/USER_REQUIRED_RELEASE_INFO_2026-06-11.md` to collect the real developer account, public support, privacy, Payments profile, Billing/backend, tester, and screenshot inputs.
- Do not request or store identity documents, bank details, tax forms, passwords, keystore passwords, one-time codes, or service-account private keys in the repository.
- Only copy non-sensitive public configuration into local release config: support email, public privacy URL, Billing verify URL, product IDs/prices, tester emails, and release notes.

## Source Review

Reviewed local release materials under `playstore` only:

- `submission_gate_report.md`
- `USER_REQUIRED_RELEASE_INFO_2026-06-11.md`
- `play_console_submission_fields.md`
- `privacy_policy_hosting.md`
- `privacy_site_audit_report.md`
- `billing_backend_deployment.md`
- `billing_backend_audit_report.md`
- `internal_test_checklist.md`
- `screenshot_plan.md`
- `google_play_review_notes.md`
- `google_play_official_requirements_2026.md`
- `release_config_audit_report.md`
- `upload_key_status.md`
- `app_regression_audit_report.md`
- `adaptive_ui_audit_report.md`
- `travel_content_audit_report.md`

## Ownership Model

| Area | Primary owner | Support | Current state | Completion signal |
| --- | --- | --- | --- | --- |
| Play Console app setup | Account owner | Release Ops | External TODO | App exists in Play Console with package `com.todayplay.app` and correct app identity fields. |
| Support email | Account owner | Product/Support | External TODO | Real monitored support email is entered in Play Console and local submission fields. |
| Privacy site hosting | Web/domain owner | Legal/Product, Release Ops | External TODO | `playstore/privacy_site` is deployed to a stable public HTTPS URL with real publisher/support identity. |
| Data Safety | Product/Legal | Release Ops, Engineering | Console TODO | Play Console Data Safety form submitted from real app behavior, not placeholder copy. |
| Content rating | Product/Legal | Release Ops | Console TODO | Questionnaire completed and rating accepted in Play Console. |
| Billing backend | Backend owner | Account owner, Android owner | External TODO | HTTPS `/billing/verify` verifies Google Play purchases with server-side credentials. |
| Play Billing products | Account owner | Product, Backend owner | Console TODO | Product IDs are created and testable in Play Console. |
| Internal testing track | Release Ops | QA, Account owner | Console TODO | Internal track has uploaded signed AAB, tester list, release notes, and rollout started. |
| Screenshots | QA/Design | Android owner, Release Ops | Needs recapture | At least 2 current phone screenshots from real build; recommended 4-6. No stale UI, mojibake, debug artifacts, or misleading paid/live claims. |
| AAB pre-upload | Release Ops | Android owner | Local pass, recheck before upload | Latest AAB passes submission gate immediately before upload. |
| Review notes | Product/Release Ops | Legal, Android owner | Draft exists | Review notes pasted/adapted in Play Console and match app behavior. |

## External Task Checklist

### 1. Play Console Fields

Local evidence:

- `play_console_submission_fields.md` exists.
- Store listing drafts and localized listing drafts exist.
- Submission gate fails contact email and privacy URL.

Tasks:

- TODO: Create or open the Play Console app for package `com.todayplay.app`.
- TODO: Confirm app name and default language in Play Console.
- TODO: Set app type as App.
- TODO: Set free/paid state according to launch plan. Current draft says Free.
- TODO: Set ads declaration. Current draft says No unless an ad SDK is added later.
- TODO: Enter real developer support email.
- TODO: Enter real public HTTPS privacy policy URL after deployment.
- TODO: Keep Play Console fields aligned with `playstore/play_console_submission_fields.md`.

Acceptance criteria:

- Contact email is a real monitored email, not `TODO`, `example.com`, or a placeholder.
- Privacy policy URL starts with `https://`, is public, requires no login, and serves the deployed privacy site.
- Submission gate no longer fails `Play Console contact email` or `Play Console privacy URL`.

### 2. Privacy Site Deployment

Local evidence:

- `playstore/privacy_site/index.html` exists.
- `playstore/privacy_site/_headers` exists.
- `playstore/privacy_site/README.md` exists.
- Privacy site audit passes for `en`, `zh-CN`, `zh-TW`, `ja`, `ko`, and `es`.
- Audit explicitly notes that local material still needs public HTTPS hosting and real publisher/support identity.

Tasks:

- TODO: Choose hosting: Cloudflare Pages, GitHub Pages, or an existing website.
- TODO: Deploy the full contents of `playstore/privacy_site`.
- TODO: Replace placeholder publisher/contact language with real developer identity and support email before production use.
- TODO: Confirm the public page is reachable without authentication.
- TODO: Confirm the page remains stable during review and after release.
- TODO: Update Play Console privacy policy URL.
- TODO: Update local submission field document with the same URL.

Acceptance criteria:

- Browser opens the exact public privacy URL over HTTPS.
- Page content matches current app behavior: local route/history data, no precise location permission, map handoff behavior, Billing purchase-token handling, backend verification requirement, mock/authorized-content boundary, and local deletion path.
- Public page contains real support contact and publisher identity.
- Legacy `privacy_policy_zh.html` is not used as the primary Play Console URL.

### 3. Data Safety

Local evidence:

- `data_safety_draft.md` exists and submission gate marks the draft present.
- The draft must be treated as a starting point only; final answers must match real release behavior.

Tasks:

- TODO: Reconfirm final app behavior before answering Play Console:
  - Account system: currently none unless added later.
  - Precise location collection: currently none.
  - Photo upload: currently not real upload.
  - Analytics SDK: currently not identified in gate materials.
  - Ads SDK: current store fields say no ads unless added later.
  - Backend sync: travel content and Billing endpoints remain disabled until real HTTPS services exist.
  - Google Play Billing: purchase callbacks and purchase tokens are relevant when paid testing is enabled.
- TODO: Complete the Data Safety form in Play Console.
- TODO: Re-review Data Safety after any backend, analytics, maps SDK, account, photo upload, or paid entitlement change.

Acceptance criteria:

- Submitted Data Safety answers match the exact AAB being uploaded.
- Financial/Billing handling does not claim verified paid entitlement until `/billing/verify` is live.
- Data deletion answer reflects the in-app local data deletion screen and any future account/backend deletion process.
- Legal/Product owner signs off before internal test or production submission.

### 4. Content Rating And Target Audience

Local evidence:

- Suggested stance exists in `play_console_submission_fields.md`.
- Current app is a travel/route planning app with local mock route content, external map handoff, and planned Google Play Billing.

Tasks:

- TODO: Complete Play Console content rating questionnaire.
- TODO: Declare no violence, sexual content, gambling, or public UGC feed unless final build changes.
- TODO: Declare paid digital products via Google Play Billing if Billing products are visible/tested.
- TODO: Set target audience to general/adult users, not designed specifically for children, unless product scope changes.

Acceptance criteria:

- Play Console accepts the content rating questionnaire.
- Target audience and content declarations match actual app copy, screenshots, and paid product state.
- If the app later targets children/families, privacy, ads, UX, and Families policy review are reopened.

### 5. Billing Verify Endpoint

Local evidence:

- Submission gate fails `Billing backend endpoint` with `BILLING_VERIFY_ENDPOINT=None`.
- Billing backend skeleton and audit pass locally.
- Client blocks purchase launch when endpoint is empty or non-HTTPS.
- Backend deployment plan documents required Google Play Developer API setup.

Tasks:

- TODO: Create or choose Google Cloud project.
- TODO: Enable Google Play Android Developer API.
- TODO: Create service account and grant app access in Play Console.
- TODO: Deploy `backend/billing-verify-worker` or equivalent production backend.
- TODO: Configure server-side secrets only:
  - `GOOGLE_SERVICE_ACCOUNT_EMAIL`
  - `GOOGLE_PRIVATE_KEY`
  - `TODAYPLAY_PACKAGE_NAME=com.todayplay.app`
- TODO: Implement/validate real `POST /billing/verify`.
- TODO: Keep `/entitlements` and `/billing/notifications` blocked or implement durable auth/storage and RTDN verification before claiming them live.
- TODO: Configure Android release with a real `https://.../billing/verify` endpoint only after backend is deployed and tested.

Acceptance criteria:

- Endpoint is HTTPS and publicly reachable by the app.
- Endpoint verifies one-time products and subscriptions through Google Play Developer API.
- Endpoint rejects package mismatches, missing purchase tokens, unknown products, missing service-account secrets, and invalid purchase states.
- Purchase tokens are not logged and raw Google purchase payloads are not returned to the app.
- Internal-test purchase with a Play license tester succeeds only after server verification.
- Submission gate no longer fails `Billing backend endpoint`.

### 6. Play Billing Products

Local evidence:

- Product IDs are documented:
  - `todayplay.plus.monthly`
  - `todayplay.itinerary.premium.once`
  - `todayplay.citypack.global`
  - `todayplay.photo.positions`

Tasks:

- TODO: Create subscription `todayplay.plus.monthly` in Play Console.
- TODO: Create one-time products:
  - `todayplay.itinerary.premium.once`
  - `todayplay.citypack.global`
  - `todayplay.photo.positions`
- TODO: Configure prices, availability, tax/payment requirements, and test status.
- TODO: Confirm product IDs match Android constants and backend catalog.

Acceptance criteria:

- Product query works for internal testers installed from Play.
- Product IDs match across Play Console, Android client, and backend.
- Paid features are not described as live until purchase verification and entitlement handling are proven.

### 7. Internal Testing Track

Local evidence:

- Signed release AAB exists.
- Internal test checklist exists.
- Release notes are drafted in `play_console_submission_fields.md`.

Tasks:

- TODO: Create internal testing track.
- TODO: Add tester email list.
- TODO: Upload `app/build/outputs/bundle/release/app-release.aab`.
- TODO: Paste/adapt release notes for `V0.9.40 internal test`.
- TODO: Ensure testers install through Play distribution for real Billing testing, not local debug APK.
- TODO: Run smoke test on the Play-distributed build.

Acceptance criteria:

- Internal testing release is accepted by Play Console.
- Tester opt-in link works.
- Testers can install the app from Play.
- Smoke test covers first launch, route generation, map handoff, manual check-in, history, completion card, privacy/local deletion, shop disabled state or verified Billing flow.

### 8. AAB Upload Pre-Check

Local evidence:

- Current gate passes versioning, target SDK, release AAB, AAB signature, upload key, graphics, localized listings, privacy site package, data deletion, map/mock guard, travel content boundary, Data Safety draft, review notes, Billing purchase guard, backend skeleton, and version documentation sync.

Tasks before each upload:

- TODO: Re-run the release preflight/submission gate on the exact AAB to upload.
- TODO: Confirm versionCode has not already been uploaded to Play.
- TODO: Confirm release AAB, not debug APK, is uploaded.
- TODO: Confirm `keystore.properties` has no UTF-8 BOM and is not committed.
- TODO: Confirm AAB contains signature files under `META-INF`.
- TODO: Confirm external fields are real and current.

Acceptance criteria:

- Latest submission gate is pass, or any remaining warning is explicitly accepted by Release Ops and Product.
- AAB path and version are recorded in release notes.
- No keystore, password, or local release secret is committed or uploaded outside Play/backend secret systems.

### 9. Screenshot Recapture

Local evidence:

- Four phone screenshots exist.
- Submission gate warns they are draft, stale against the latest AAB, and require visible `?` separator/mojibake review.
- Screenshot plan says no device/emulator was connected when drafts were generated.

Tasks:

- TODO: Install the current build on a real device or emulator.
- TODO: Capture at least 2 current phone screenshots; recommended set is 4-6:
  - Home / route picker.
  - Card-flow input.
  - Route result.
  - Route stop with map/check-in.
  - Completion card.
  - Shop screen with truthful paid-state messaging.
- TODO: Replace stale draft files or add clearly named current screenshots under `playstore/screenshots`.
- TODO: Review visible text for mojibake, replacement characters, stray `?` separators, clipped text, debug toasts, system dialogs, and misleading claims.

Acceptance criteria:

- Screenshots come from the same version intended for upload.
- Minimum Play requirement is met: at least 2 phone screenshots.
- No screenshot implies live paid membership, real global social-platform heat, unauthorized POI images, or production map/content provider data unless those integrations are real.
- Submission gate screenshot warning is cleared or formally accepted as non-blocking only for internal testing.

### 10. Review Notes

Local evidence:

- `google_play_review_notes.md` exists.
- It covers permissions, external map behavior, Billing verification requirement, and mock/authorized content boundaries.

Tasks:

- TODO: Paste or adapt review notes in Play Console.
- TODO: Make the notes readable and not mojibake when entered into Console.
- TODO: State that map buttons open external map apps/web maps and the app does not request current location.
- TODO: State that route/place/social-heat content is mock/sample unless official/licensed providers are configured.
- TODO: State that paid entitlement requires server-side Google Play verification and should not be treated as live until backend is configured.

Acceptance criteria:

- Review notes match the actual uploaded build and store screenshots.
- Notes do not overclaim real backend, real social data, real photo upload, or live paid entitlement.
- Product/Legal owner approves wording before submission.

## Completion Definition

Google Play "submittable" means all of the following are true:

- Latest local submission gate passes without fail.
- Real support email is configured in Play Console and local submission fields.
- Public HTTPS privacy policy URL is live, stable, and points to the multilingual privacy site with real contact/publisher identity.
- Data Safety form is submitted from real release behavior.
- Content rating and target audience forms are completed and accepted.
- Internal testing track is configured with testers and release notes.
- Current signed AAB for `0.9.40 / versionCode 58` is uploaded, or versionCode is incremented if Play already consumed `58`.
- Billing products and backend verification are either fully configured and tested, or paid entry remains visibly disabled and review notes explain the state.
- Store screenshots are recaptured from the current build and reviewed for stale UI, mojibake, `?` separator artifacts, and unsupported claims.
- Review notes are entered in Play Console and match the build.

## Do Not Do

- Do not invent a support email.
- Do not invent a privacy policy URL.
- Do not invent a Billing verification endpoint.
- Do not upload a debug APK to Play.
- Do not claim paid entitlements are live before server-side Google Play verification works.
- Do not claim real global/social/map/provider data unless official APIs, licensed data, merchant partnerships, user-authorized links, or backend providers are configured and reviewed.
- Do not modify Gradle, code, keystore, AAB, or APK as part of this action-plan-only round.
