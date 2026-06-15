# PM Test Report Addendum - v0.9.29 / versionCode 47 - 2026-06-11

## Scope

This is a project-lead PM addendum for the frozen `0.9.29 / versionCode 47` baseline.

No app code, Gradle config, keystore, APK, or AAB was changed for this addendum. It updates product/test decision state only.

## Current Baseline

- Project: `D:\AppStore\nemu\real`
- Package: `com.todayplay.app`
- Frozen version: `0.9.29 / versionCode 47`
- Debug APK: `app\build\outputs\apk\debug\app-debug.apk`
  - Current output metadata: `0.9.29 / 47`
  - Last built: `2026-06-11 11:33:31`
- Signed release AAB: `app\build\outputs\bundle\release\app-release.aab`
  - Last built: `2026-06-11 11:33:48`
  - Signature files present: `META-INF/TODAYPLA.SF`, `META-INF/TODAYPLA.RSA`
- Current submission gate result: `fail`, correctly blocked by external launch items.

## Resolved From Earlier PM Report

### RESOLVED - Previous P0 Version / Artifact Mismatch

- Earlier PM report observed a mismatch between source version and built artifacts.
- Current evidence now resolves it:
  - Gradle version: `0.9.29 / 47`
  - Debug APK output metadata: `0.9.29 / 47`
  - `Artifact version freshness`: `pass`
  - `Version documentation sync`: `pass`
- Owner: Development / release gate
- Acceptance status: accepted for current frozen baseline.

## Current P0 Issues

### P0-01 - Missing Real Play Console Support Email

- User impact: Users and Google Play reviewers do not have a real monitored support contact.
- Current evidence: submission gate still reports `Play Console contact email` as `fail`.
- Verification method: run `python .\playstore\google_play_submission_gate.py D:\AppStore\nemu\real` and confirm the contact email row is `pass`.
- Owner: External release operations / account owner.
- Acceptance standard:
  - A real monitored developer support email is available.
  - The same email is entered in Play Console and local submission fields.
  - Submission gate contact email row passes.

### P0-02 - Missing Public HTTPS Privacy Policy URL

- User impact: Google Play cannot verify a public privacy policy, and users cannot inspect data handling before install.
- Current evidence: submission gate still reports `Play Console privacy URL` as `fail`.
- Verification method: deploy `playstore\privacy_site` to a public HTTPS URL, update local fields, and rerun submission gate.
- Owner: External release operations / domain hosting owner.
- Acceptance standard:
  - Public HTTPS privacy policy URL is live.
  - Page contains the current multi-language privacy site.
  - URL is entered in Play Console and local release fields.
  - Submission gate privacy URL row passes.

### P0-03 - Missing Real Billing Verification Endpoint

- User impact: Paid purchase flows cannot safely grant entitlements because purchase tokens are not verified by a deployed server.
- Current evidence: submission gate still reports `Billing backend endpoint` as `fail`, with `BILLING_VERIFY_ENDPOINT=None`.
- Verification method: deploy a real HTTPS `/billing/verify`, configure Google Play service-account secrets, set local release config, rebuild, and rerun full preflight.
- Owner: Billing backend lead + release operations.
- Acceptance standard:
  - `/billing/verify` is deployed on HTTPS.
  - Internal-track Google Play purchase token verification succeeds.
  - Client paid entry remains blocked unless endpoint is valid.
  - Submission gate Billing endpoint row passes.

### P0-04 - No Real Device / Emulator Smoke Evidence

- User impact: The team cannot prove that first launch, generation, result, history, privacy, shop guard, map fallback, and share/save work on an installed app.
- Current evidence: latest device checks still show no connected adb device or usable emulator.
- Verification method: install the frozen APK on a real Android device or already-available emulator and perform a full smoke pass.
- Owner: Product QA / Device QA.
- Acceptance standard:
  - Smoke test covers first launch, language switch, route creation, result page, map fallback, check-in/history, privacy clear, shop guard, and share/save.
  - Any crash, layout overlap, broken copy, or blocked core flow is filed and fixed or explicitly accepted.
  - Smoke evidence references the `0.9.29 / 47` APK.

### P0-05 - Store Screenshots Are Still Draft / Stale

- User impact: Play listing could show old UI, broken `?` separators, or paid states that are not currently live.
- Current evidence: screenshot gate remains `warn`; all four existing screenshots are stale against the latest AAB and include draft-source / visible-question-separator risks.
- Verification method: recapture 4-6 screenshots from the installed `0.9.29 / 47` build and rerun submission gate.
- Owner: Device Screenshot QA.
- Acceptance standard:
  - New screenshots are captured from a real device or emulator.
  - File modified time is later than current target build time.
  - No visible `?` separators, mojibake, clipped text, fake billing readiness, or old UI.

## Current P1 Issues

### P1-01 - Billing Backend Is Not A Complete Production Entitlement System

- User impact: Even after `/billing/verify` is deployed, subscriptions, cancellations, refunds, restore, and entitlement durability still need production handling.
- Current evidence: backend readiness report states `/entitlements`, RTDN, durable storage, acknowledge/consume, and lifecycle handling are still blocking production billing maturity.
- Owner: Billing backend lead.
- Acceptance standard:
  - Durable entitlement storage exists.
  - RTDN notifications are handled.
  - Subscription expiry, cancellation, refund, and restore paths are tested.

### P1-02 - Dependency Warnings Remain

- User impact: Low short-term risk, but dependencies should not drift too far before a store release train.
- Current evidence: lint reports `0 errors / 5 dependency warnings`.
- Owner: Development.
- Acceptance standard:
  - Dependency update decision is made before production release.
  - Updates are applied only if they do not destabilize Billing, Compose, or lifecycle behavior.

### P1-03 - Long-Locale Visual QA Still Needs Device Evidence

- User impact: Japanese, Korean, Spanish, Traditional Chinese, and long Chinese strings may fit in static audits but still overflow on real devices.
- Current evidence: localization and copy-fit audits pass, but real device screenshot evidence is missing.
- Owner: Product QA / Device QA.
- Acceptance standard:
  - At least one long-text locale is spot-checked on a small phone viewport.
  - No clipped primary buttons, chips, result summaries, billing guard copy, or navigation labels.

## Current P2 Issues

### P2-01 - Store Listing Final Copy Needs Human Review

- User impact: Listing copy can pass a gate but still sound too technical or undersell the product.
- Owner: Product manager / release operations.
- Acceptance standard:
  - Short description, full description, release notes, and review notes are reviewed in target store language.

### P2-02 - Future Product Data Sources Remain Mock / Curated

- User impact: Users may expect live POI, map, or social heat data if copy is not careful.
- Current evidence: gate passes mock/media guard and travel content boundary, but production content backend is not live.
- Owner: Product / backend.
- Acceptance standard:
  - Store copy and in-app copy remain truthful about mock/curated data until official content sources are deployed.

## PM Decision

The `0.9.29 / 47` build resolves the earlier version/artifact mismatch and is suitable as the current internal candidate baseline.

It is not Google Play submission-ready. The remaining blockers are real external launch requirements and device evidence: support email, public privacy URL, Billing verification endpoint, real screenshots, and real installed-app smoke testing.
