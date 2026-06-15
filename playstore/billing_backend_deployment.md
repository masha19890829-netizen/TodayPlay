# Billing Backend Deployment Plan

Current status: backend skeleton exists, but no live endpoint is configured.

Backend path:

```text
backend/billing-verify-worker
```

## Why This Is Required

Google Play Billing purchase callbacks are not enough to grant durable paid benefits. The app must send purchase tokens to a trusted backend. The backend verifies them with Google Play Developer API, records entitlement state, and returns only verified entitlement keys to the app.

## Current Android State

- Client collects `packageName`, `productIds`, `purchaseToken`, `orderId`, `purchaseTime`, `purchaseState`, and `isAcknowledged`.
- Client posts to `BuildConfig.BILLING_VERIFY_ENDPOINT` when configured.
- Endpoint is intentionally empty now.
- Client does not grant durable entitlements while endpoint is empty.

## Backend Skeleton

The Worker implements:

- `GET /health`
- `POST /billing/verify`
- `GET /entitlements` placeholder
- `POST /billing/notifications` placeholder

Local contract tests now cover the conservative failure paths:

- package mismatch
- missing purchase token or product ID
- unknown product ID
- missing Google service account secrets
- `/entitlements` blocked until authenticated durable storage exists
- `/billing/notifications` blocked until Pub/Sub RTDN verification exists
- purchase-token logging and raw Google payload exposure guardrails

The verification endpoint calls:

- Google Play Developer API `purchases.products.get` for one-time products.
- Google Play Developer API `purchases.subscriptionsv2.get` for subscriptions.

## Required External Setup

1. Create or choose a Google Cloud project.
2. Enable Google Play Android Developer API.
3. Create a service account.
4. Grant the service account access to the app in Play Console.
5. Deploy the Worker or port the same code to the final backend platform.
6. Set secrets:
   - `GOOGLE_SERVICE_ACCOUNT_EMAIL`
   - `GOOGLE_PRIVATE_KEY`
   - `TODAYPLAY_PACKAGE_NAME=com.todayplay.app`
7. Create Play Console products:
   - `todayplay.plus.monthly`
   - `todayplay.itinerary.premium.once`
   - `todayplay.citypack.global`
   - `todayplay.photo.positions`
8. Test with a Play license tester on an internal testing track.
9. Configure Android:

```kotlin
buildConfigField("String", "BILLING_VERIFY_ENDPOINT", "\"https://YOUR_DOMAIN/billing/verify\"")
```

## Production Gaps After Skeleton

- Durable user identity.
- Durable entitlement database.
- RTDN Pub/Sub verification and processing.
- Refund, cancellation, chargeback, grace-period, pause, and expiry handling.
- Entitlement re-check on app start.
- Admin tooling for support and entitlement revocation.

## Security Rules

- Do not log purchase tokens.
- Do not return raw Google Play purchase payloads to the app.
- Do not store service account keys in Android.
- Do not grant entitlement on package mismatch.
- Do not mark paid features as live before internal-test purchase verification passes.
