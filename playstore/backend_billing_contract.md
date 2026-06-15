# Backend Billing Contract Draft

The Android client now extracts purchase data but must not grant durable entitlements without backend verification.

## Android client configuration

The client has a `BuildConfig.BILLING_VERIFY_ENDPOINT` field.

Current release value:

```kotlin
buildConfigField("String", "BILLING_VERIFY_ENDPOINT", "\"\"")
```

This is intentionally empty for the current local build. With an empty endpoint, the app does not send purchase tokens over the network and does not unlock durable paid entitlements.

Before a live paid release:

- Set `BILLING_VERIFY_ENDPOINT` to a real HTTPS endpoint.
- Do not put API keys or Google service account credentials in the Android app.
- Keep Google Play Developer API credentials only on the backend.
- Return entitlements only after Google Play validates the purchase token.

## POST /billing/verify

Request:

```json
{
  "packageName": "com.todayplay.app",
  "productIds": ["todayplay.plus.monthly"],
  "purchaseToken": "<google-play-purchase-token>",
  "orderId": "<optional-order-id>",
  "purchaseTime": 1780000000000,
  "purchaseState": 1,
  "isAcknowledged": false,
  "userId": "<app-user-id>"
}
```

Response on success:

```json
{
  "status": "verified",
  "entitlements": [
    {
      "key": "plus_monthly",
      "productId": "todayplay.plus.monthly",
      "expiresAt": "2026-07-08T00:00:00Z"
    }
  ],
  "requiresAcknowledgement": true
}
```

Response on failure:

```json
{
  "status": "failed",
  "reason": "invalid_purchase_token"
}
```

## GET /entitlements

Returns currently active entitlements for the signed-in user.

## POST /billing/notifications

Endpoint for Google Play Real-time Developer Notifications.

Handle:

- Renewal
- Cancellation
- Expiry
- Refund
- Chargeback
- Grace period
- Account hold

## Client rule

The client may show "verification pending" but must only unlock durable premium features after the backend returns a verified entitlement.

The client rejects non-HTTPS verification endpoints.
