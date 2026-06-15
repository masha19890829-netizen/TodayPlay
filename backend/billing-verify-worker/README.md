# TodayPlay Billing Verify Worker

Minimal backend skeleton for Google Play purchase verification.

This worker is designed for the current Android package:

- Package name: `com.todayplay.app`
- Subscription product: `todayplay.plus.monthly`
- One-time products:
  - `todayplay.itinerary.premium.once`
  - `todayplay.citypack.global`
  - `todayplay.photo.positions`

Entitlement mapping:

| Product ID | Entitlement key |
| --- | --- |
| `todayplay.plus.monthly` | `plus_monthly` |
| `todayplay.itinerary.premium.once` | `premium_itinerary_once` |
| `todayplay.citypack.global` | `global_city_pack` |
| `todayplay.photo.positions` | `photo_position_pack` |

## What This Solves

The Android client must not grant durable paid entitlements by itself. After Google Play returns a purchase token, the app sends it to:

```text
POST /billing/verify
```

The backend verifies the token with the Google Play Developer API, maps the verified product to entitlement keys, and returns only the entitlement result to the app.

## Required Secrets

Set these as Worker secrets or platform environment variables. Do not commit them.

```text
GOOGLE_SERVICE_ACCOUNT_EMAIL
GOOGLE_PRIVATE_KEY
TODAYPLAY_PACKAGE_NAME=com.todayplay.app
```

The Google service account must have access to the app in Play Console and permission to call Android Publisher APIs.

## Endpoints

### `GET /health`

Returns a simple health payload.

### `POST /billing/verify`

Request body:

```json
{
  "packageName": "com.todayplay.app",
  "productIds": ["todayplay.plus.monthly"],
  "purchaseToken": "purchase-token-from-google-play",
  "orderId": "GPA....",
  "purchaseTime": 1710000000000,
  "purchaseState": 1,
  "isAcknowledged": false
}
```

Successful response:

```json
{
  "status": "verified",
  "entitlements": [
    {
      "key": "plus_monthly",
      "productId": "todayplay.plus.monthly",
      "source": "google_play"
    }
  ]
}
```

### `GET /entitlements`

Placeholder endpoint. A production version should require authenticated user identity and return current entitlements from a durable database.

### `POST /billing/notifications`

Placeholder for Google Play Real-time Developer Notifications. A production version should verify Pub/Sub authenticity, update subscription status, and revoke or renew entitlements.

## Deployment Checklist

1. Create a Google Cloud service account.
2. Grant that service account access to the Android app in Play Console.
3. Enable the Google Play Android Developer API.
4. Deploy this Worker or port the same logic to your backend platform.
5. Set `GOOGLE_SERVICE_ACCOUNT_EMAIL`, `GOOGLE_PRIVATE_KEY`, and `TODAYPLAY_PACKAGE_NAME` as secrets.
6. Test `/health`.
7. Configure Play Console products.
8. Upload the AAB to an internal test track.
9. Buy a test product with a license tester.
10. Set Android `BILLING_VERIFY_ENDPOINT` to the deployed HTTPS `/billing/verify` URL.

## Contract Tests

Run:

```text
npm test
```

The tests call the Worker directly and verify:

- `/health`
- `/billing/verify` validation failures
- package-name mismatch rejection
- unknown product rejection
- no fake success when Google secrets are absent
- `/entitlements` remains blocked until authenticated durable storage exists
- `/billing/notifications` remains blocked until Pub/Sub RTDN verification exists
- product IDs, Google Play API URLs, and token-safety boundaries remain present
- entitlement keys stay aligned with the Android product catalog

## Security Notes

- Do not log purchase tokens.
- Do not return raw Google purchase payloads to the client.
- Do not grant entitlements for package mismatches.
- Do not use this without durable storage in production; refunds, renewals, cancellations, and expiry must be reflected through database state.
- This skeleton intentionally does not fake successful verification when secrets are missing.
