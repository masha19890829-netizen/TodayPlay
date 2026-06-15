# Backend Billing Readiness - 2026-06-11

Owner view: Billing Backend Lead

Scope audited:

- `backend/billing-verify-worker/worker.js`
- `backend/billing-verify-worker/tests/contract.test.mjs`
- `backend/billing-verify-worker/README.md`
- `backend/billing-verify-worker/wrangler.toml.example`
- `playstore/backend_billing_contract.md`
- `playstore/billing_backend_deployment.md`
- `playstore/submission_gate_report.md`
- `playstore/billing_backend_audit_report.md`

## Executive Status

Current state: deployable verification slice, not a complete production Billing backend.

The Worker now has a real `/billing/verify` implementation path that signs a Google service-account JWT, obtains an Android Publisher access token, and calls Google Play Developer API purchase endpoints. It still cannot be treated as production-ready because durable user identity, entitlement storage, acknowledgement/consume policy, and RTDN processing are not implemented.

The Google Play submission gate should remain `fail` while `BILLING_VERIFY_ENDPOINT` is empty. That is the correct release posture: the Android client has a purchase guard and must not unlock durable paid benefits without a deployed HTTPS verifier.

## Current Endpoint State

| Endpoint | Current behavior | Readiness |
| --- | --- | --- |
| `GET /health` | Returns `{ "status": "ok" }`. | Ready for deployment smoke test. |
| `POST /billing/verify` | Validates package name and product IDs, rejects unknown products, requires service-account secrets, calls Google Play APIs for subscriptions and one-time products, returns entitlement keys only after verified purchase state. | Ready for internal-track token verification testing after secrets and Play products exist. Not enough for production entitlement lifecycle. |
| `GET /entitlements` | Returns `501 not_implemented`. | Blocked until authenticated user identity and durable entitlement storage exist. |
| `POST /billing/notifications` | Returns `501 not_implemented`. | Blocked until Google Play RTDN Pub/Sub authenticity verification and persistent entitlement updates exist. |

## Changes Made In This Pass

- Fixed backend entitlement key mapping to match Android catalog:
  - `todayplay.plus.monthly` -> `plus_monthly`
  - `todayplay.itinerary.premium.once` -> `premium_itinerary_once`
  - `todayplay.citypack.global` -> `global_city_pack`
  - `todayplay.photo.positions` -> `photo_position_pack`
- Updated Worker README with an explicit product-to-entitlement mapping table.
- Added a contract test that compares backend entitlement keys against the Android product catalog so this drift is caught early.

## Product And Package Contract

Package name:

```text
com.todayplay.app
```

Google Play product IDs:

```text
todayplay.plus.monthly
todayplay.itinerary.premium.once
todayplay.citypack.global
todayplay.photo.positions
```

Entitlement keys returned to Android:

```text
plus_monthly
premium_itinerary_once
global_city_pack
photo_position_pack
```

## Required Environment Variables And Secrets

Currently implemented:

| Name | Type | Required | Notes |
| --- | --- | --- | --- |
| `TODAYPLAY_PACKAGE_NAME` | Worker var | Yes | Default is `com.todayplay.app`; set explicitly in production. |
| `GOOGLE_SERVICE_ACCOUNT_EMAIL` | Secret | Yes | Service account must have Play Console access to this app. |
| `GOOGLE_PRIVATE_KEY` | Secret | Yes | Store as a secret; escaped `\n` private keys are normalized by the Worker. |

Needed before full production, not yet implemented:

| Need | Suggested env/storage | Why |
| --- | --- | --- |
| User identity verification | Auth issuer/JWKS or app backend session validation | `/billing/verify` and `/entitlements` must bind purchases to a real user. |
| Durable entitlement storage | D1/KV/R2/external DB binding | Required for renewals, refunds, expiry, support review, and app-start entitlement checks. |
| RTDN verification | Pub/Sub push auth config or signed-token audience | `/billing/notifications` must prove Google Pub/Sub authenticity before mutating entitlements. |
| Operational logging | Redacted structured logs | Needed for support and incident response without exposing purchase tokens. |

## Deployment Steps

1. Create or choose the Google Cloud project that owns Android Publisher API access.
2. Enable Google Play Android Developer API.
3. Create a Google service account.
4. Grant that service account access to the `com.todayplay.app` app in Play Console with permission to view/manage orders/subscriptions as required.
5. Configure Play Console products for all four product IDs listed above.
6. Deploy `backend/billing-verify-worker` to the chosen Worker/account.
7. Set Worker vars/secrets:
   - `TODAYPLAY_PACKAGE_NAME=com.todayplay.app`
   - `GOOGLE_SERVICE_ACCOUNT_EMAIL`
   - `GOOGLE_PRIVATE_KEY`
8. Smoke test `GET /health` on the deployed HTTPS host.
9. Upload the current release AAB to an internal testing track.
10. Add license tester accounts and perform real Google Play test purchases.
11. Verify `/billing/verify` returns only the matching entitlement key for the purchased product.
12. Only after internal-track verification passes, set Android `BILLING_VERIFY_ENDPOINT` to the deployed HTTPS `/billing/verify` URL and rebuild release artifacts.

## Acceptance Tests

Local contract tests:

```text
npm.cmd test
```

Result on 2026-06-11: pass, 9/9 tests.

Backend audit:

```text
python playstore\billing_backend_audit.py D:\AppStore\nemu\real
```

Result on 2026-06-11: pass.

Required live acceptance before enabling paid release:

- Deployed `/health` returns 200.
- Missing or invalid request body never grants entitlements.
- Package mismatch never calls Google Play and never grants entitlements.
- Unknown product ID never calls Google Play and never grants entitlements.
- Missing service-account secrets fail closed.
- Real subscription purchase token verifies through `purchases.subscriptionsv2.get`.
- Real one-time product purchase token verifies through `purchases.products.get`.
- Expired, cancelled, refunded, pending, or wrong-package tokens do not grant durable entitlement.
- Returned entitlement key exactly matches the purchased product.
- Purchase token and raw Google payload are not returned to Android or logged.
- App start can refresh current entitlements from durable storage after `/entitlements` is implemented.
- RTDN can renew, revoke, expire, and restore entitlement state after `/billing/notifications` is implemented.

## Production Risks

- No durable user identity: a valid token is not yet tied to an authenticated app account.
- No durable entitlement database: the Worker response is transient and cannot reflect later refunds, renewals, chargebacks, account hold, grace period exit, or expiry.
- No RTDN processing: subscription lifecycle changes will not update the app backend.
- No acknowledgement/consume policy: current audited code does not acknowledge or consume purchases. Before launch, decide whether acknowledgement is handled by Android or backend, then implement and test it.
- No replay/idempotency store: repeated verification requests for the same token are not tracked.
- No support/admin tooling: support cannot inspect, revoke, or restore entitlements safely.
- Contract mismatch risk remains between `playstore/backend_billing_contract.md` and Worker response shape: the Play contract draft includes `expiresAt` and `requiresAcknowledgement`, but current Worker returns only `status` and `entitlements`.

## Launch Blockers

Paid launch remains blocked until all of the following are done:

- Real HTTPS Worker deployment exists.
- `BILLING_VERIFY_ENDPOINT` points to the deployed `/billing/verify` endpoint only after internal verification succeeds.
- Google Play service account is created, permissioned, and stored as Worker secrets.
- Play Console package `com.todayplay.app` and all four product IDs exist and are active on an internal testing track.
- License tester purchase plan passes for subscription and one-time products.
- Durable user identity and entitlement storage are implemented.
- `/entitlements` is implemented with authentication and durable state.
- `/billing/notifications` is implemented with RTDN Pub/Sub authenticity checks and lifecycle mutation logic.
- Purchase acknowledgement/consume behavior is implemented and verified.
- Refund, cancellation, expiry, grace period, account hold, and chargeback cases are tested.

## Handoff Conclusion

The backend is now a better deployable skeleton: it has real Google Play API verification code, fail-closed local tests, aligned entitlement keys, and clear secret boundaries. It is not yet the production Billing source of truth. Keep the Play submission gate failing on `BILLING_VERIFY_ENDPOINT=None` until the deployed endpoint, Play Console setup, durable entitlement system, RTDN handling, and live test purchases are complete.
