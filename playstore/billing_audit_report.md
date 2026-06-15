# Google Play Billing Audit

- Project: `D:\AppStore\nemu\real`

## Product IDs
- `todayplay.citypack.global`
- `todayplay.itinerary.premium.once`
- `todayplay.photo.positions`
- `todayplay.plus.monthly`

## Billing Files
- `D:\AppStore\nemu\real\app\build.gradle.kts`
- `D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\billing\PlayBillingGateway.kt`

## Signals
- hasProductDetailsQuery: `True`
- hasLaunchBillingFlow: `True`
- hasPurchaseListener: `True`
- hasPurchaseTokenHandling: `True`
- hasServerVerificationMarker: `True`
- hasBackendEndpointConfig: `True`
- hasEmptyBackendEndpoint: `True`
- hasPrePurchaseVerificationGuard: `True`
- hasNetworkVerificationCall: `True`
- hasHttpsEnforcement: `True`
- hasEntitlementTerms: `True`
- hasBackendSkeleton: `True`
- hasGoogleAndroidPublisherCall: `True`
- hasBackendSecretPlaceholders: `True`

## Findings
- BillingClient usage found in 2 file(s).
- Billing verification endpoint is configured as an empty placeholder. Set a real HTTPS /billing/verify URL before live paid release.
- Client purchase launch is guarded while the verification endpoint is empty, reducing accidental unverified purchase risk.
- Billing backend skeleton found. It still must be deployed, configured with Google Play service account secrets, and tested on an internal track.
