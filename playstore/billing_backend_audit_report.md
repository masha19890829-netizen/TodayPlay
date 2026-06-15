# Billing Backend Audit

- Project: `D:\AppStore\nemu\real`
- Overall: `pass`

| Check | Status | Evidence | Required action |
| --- | --- | --- | --- |
| Billing worker endpoints | `pass` | D:\AppStore\nemu\real\backend\billing-verify-worker\worker.js | Provide health, verification, entitlement, and RTDN endpoints. |
| Google Play verification APIs | `pass` | D:\AppStore\nemu\real\backend\billing-verify-worker\worker.js | Verify one-time products and subscriptions through Google Play Developer API. |
| Product catalog alignment | `pass` | D:\AppStore\nemu\real\backend\billing-verify-worker\worker.js; D:\AppStore\nemu\real\backend\billing-verify-worker\README.md | Keep backend product IDs aligned with Android product constants and Play Console setup. |
| Secret boundary | `pass` | D:\AppStore\nemu\real\backend\billing-verify-worker\worker.js; D:\AppStore\nemu\real\backend\billing-verify-worker\README.md; D:\AppStore\nemu\real\app\build.gradle.kts; D:\AppStore\nemu\real\release_config.template.properties | Keep Google service-account credentials server-side and the Android endpoint empty or HTTPS-configured through ignored release config. |
| No fake entitlements | `pass` | D:\AppStore\nemu\real\backend\billing-verify-worker\worker.js; D:\AppStore\nemu\real\backend\billing-verify-worker\README.md | Never return paid entitlements unless Google Play verifies the purchase. |
| Purchase token safety | `pass` | D:\AppStore\nemu\real\backend\billing-verify-worker\worker.js; D:\AppStore\nemu\real\backend\billing-verify-worker\README.md; D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\billing\PurchaseVerificationGateway.kt; D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\billing\PlayBillingGateway.kt | Do not log purchase tokens, redact long tokens in errors, and enforce HTTPS on the client. |
| Billing backend contract tests | `pass` | D:\AppStore\nemu\real\backend\billing-verify-worker\tests\contract.test.mjs | Keep local Worker tests for verification failure paths, RTDN placeholder, and token safety. |
| RTDN and durable storage gaps documented | `pass` | D:\AppStore\nemu\real\playstore\billing_backend_deployment.md | Keep remaining production Billing gaps visible until they are implemented. |
