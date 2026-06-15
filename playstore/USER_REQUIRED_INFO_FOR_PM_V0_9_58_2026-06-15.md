# User Required Info Checklist for PM - V0.9.58

Date: 2026-06-15
Project: `D:\AppStore\nemu\real`
Audience: @项目负责人
Purpose: collect user-side release information without blocking V0.9.58 product experience iteration.

## PM Summary

V0.9.58 产品体验迭代不应被 Google Play 外部资料卡住。当前应继续推进首页转化、路线体验、结果页执行、分享、截图吸引力和小屏/折叠屏 QA。

以下资料需要 @项目负责人 开始向用户收集，但除非本轮目标改成“提交 Google Play 内部测试/正式审核”，否则它们不阻塞 V0.9.58 的产品体验开发：

- Google Play 开发者账号资料。
- 公开 HTTPS 隐私政策和真实支持邮箱。
- Google 登录 OAuth 配置和账号验 token 后端。
- Google Play Billing 商品、支付资料和服务端验单。
- 内测人员邮箱和测试设备信息。
- 素材版权/授权确认。

Do not ask the user to send passwords, identity documents, bank details, tax documents, keystore passwords, OAuth client secrets, service-account private keys, or one-time verification codes into chat or the repository.

## 1. Developer Account

Ask the user for:

| Item | Needed for | Blocks V0.9.58 experience iteration? | PM note |
| --- | --- | --- | --- |
| Google Play developer account exists? | Play Console app creation | No | Needed before Play internal testing or production submission. |
| Account type: personal or organization | Developer registration, verification, public profile | No | Organization may need company info/D-U-N-S; personal may need device verification. |
| Developer public name | Play store listing | No | Must be final before submission; affects public store identity. |
| Country/region | Account, payments, tax, release countries | No | User should fill sensitive proof only in Google systems. |
| Real support email | Play Console, app privacy/support page | No for V0.9.58; Yes for Play submission | Must be monitored and long-lived. |
| Developer website, if any | Optional Play listing/support trust | No | If absent, privacy policy URL can be a standalone legal page. |
| Release country/region plan | Play rollout scope | No | Helps choose compliance and language priorities. |

Acceptance standard before Play submission:

- Play Console account is active and verified.
- Real support email is available.
- Public developer identity matches privacy policy and store listing.

## 2. Privacy Policy And Data Safety

Ask the user for:

| Item | Needed for | Blocks V0.9.58 experience iteration? | PM note |
| --- | --- | --- | --- |
| Public HTTPS privacy policy URL | Required Play Console field | No for V0.9.58; Yes for Play submission | Current local package exists under `playstore/privacy_site`, but needs public hosting. |
| Publisher/developer legal display name | Privacy policy | No | Must be real before store submission. |
| Privacy/data request email | Privacy policy and support | No for V0.9.58; Yes for submission | Can be same as support email if monitored. |
| Data deletion path | Play Data Safety and privacy policy | No for local-only V0.9.58 | If backend accounts launch, a public account/data deletion route is required. |
| Analytics SDK decision | Data Safety | No | If Firebase/analytics is added later, Data Safety must be updated. |
| Ads SDK decision | Ads declaration and Data Safety | No | Current stance should remain "No ads" unless an ad SDK is added. |
| Location/photo/account/backend sync decision | Data Safety and privacy copy | No | Must match the exact build submitted to Play. |

Acceptance standard before Play submission:

- Public HTTPS privacy page opens without login.
- Privacy text matches actual app behavior.
- Data Safety answers are filled from final release behavior, not from a draft.

## 3. Google Sign-In OAuth

Ask the user for:

| Item | Needed for | Blocks V0.9.58 experience iteration? | PM note |
| --- | --- | --- | --- |
| Whether Google login is required in V0.9.58 | Scope decision | No | Current app can continue with local tester profile while OAuth is missing. |
| Google Cloud / Google Auth Platform project | OAuth setup | No | Needed only when enabling real Google sign-in. |
| Android OAuth client | Sign in with Google on Android | No | Must bind package `com.todayplay.app` and signing certificate fingerprint. |
| Web OAuth client ID | `GOOGLE_WEB_CLIENT_ID` | No | Public-ish client ID; safe to configure, not a secret. |
| Auth verify endpoint | `AUTH_VERIFY_ENDPOINT` | No for product iteration; Yes for trusted accounts | Must be HTTPS and verify Google ID tokens server-side. |
| Account deletion/support route | Account compliance | No unless real accounts launch | Required if backend account data is stored. |

Do not collect:

- Google account password.
- OAuth client secret.
- Service account private key.
- One-time verification codes.

Acceptance standard before claiming real login:

- Google button is configured with real Web OAuth client ID.
- Backend verifies ID token audience, issuer, expiry, and subject.
- App does not trust client-side token parsing as a durable account.

## 4. Payment, Billing, And Verification

Ask the user for:

| Item | Needed for | Blocks V0.9.58 experience iteration? | PM note |
| --- | --- | --- | --- |
| Paid feature decision: disabled, internal test, or production | Product and release scope | No | If disabled, keep purchase entry guarded and avoid "live membership" claims. |
| Payments profile country/region | Paid products | No | User fills banking/tax details only inside Google systems. |
| Product IDs and prices | Play Billing catalog | No unless testing purchases | Existing planned IDs can stay stable. |
| Subscription benefits | Store copy and entitlement design | No | Needed before creating product descriptions. |
| Billing verify URL | `BILLING_VERIFY_ENDPOINT` | No for V0.9.58 UX; Yes for real paid testing | Must be a real HTTPS `/billing/verify`. |
| Backend hosting choice | Billing verification | No | Existing skeleton can be deployed or replaced. |
| Google Play Developer API access | Server-side purchase verification | No | Needed before live entitlement verification. |
| Entitlement storage plan | Durable paid access | No | Needed before claiming cross-device paid benefits. |
| RTDN/Pub/Sub plan | Subscription lifecycle | No | Important before production subscription launch. |

Current safe product stance:

- V0.9.58 may show planned paid areas only if copy is truthful.
- Do not launch purchase flow or grant paid benefits without HTTPS server verification.
- Do not call paid membership live until Play products, backend verification, and entitlement handling pass internal testing.

Do not collect:

- Bank account details.
- Tax forms.
- Google service account private key.
- Play Console password.
- Keystore password.

## 5. Testers And Devices

Ask the user for:

| Item | Needed for | Blocks V0.9.58 experience iteration? | PM note |
| --- | --- | --- | --- |
| Internal tester emails | Play internal testing | No | Needed once an internal testing track is created. |
| Primary test device models | QA planning | No | Useful now for layout, foldable, and performance priorities. |
| Android OS versions | Compatibility QA | No | Helps choose emulator/device coverage. |
| Region/language of testers | Localization and store listing QA | No | Current app has multilingual paths; user can prioritize languages. |
| Whether testers can install APK directly | Trial distribution | No | APK trial can continue before Play internal testing is ready. |

Acceptance standard before Play internal testing:

- Tester list is ready in Play Console.
- Testers install through Play when testing real Billing.
- Direct APK install is only used for product UX trial, not purchase verification.

## 6. Visual Assets And Copyright

Ask the user for:

| Item | Needed for | Blocks V0.9.58 experience iteration? | PM note |
| --- | --- | --- | --- |
| App icon approval | Store listing | No | Existing 512x512 icon can be reviewed later. |
| Feature graphic approval | Store listing | No | Existing 1024x500 graphic can be updated before submission. |
| Screenshot visual direction approval | Store conversion | No | Strongly useful for V0.9.58, but not a legal blocker. |
| Permission to use any user-provided images | Asset compliance | Only if those assets are shipped | Need written confirmation of ownership/license. |
| Third-party font/image/map/POI data licenses | Store and legal review | Only if included in app/store materials | Do not ship unlicensed assets or imply official data sources. |
| Claims allowed in marketing copy | Store listing and screenshots | No | Avoid claims like real social heat, official map rankings, live AI, or live paid membership unless true. |

Current safe asset stance:

- Project-local generated visual assets can support product iteration.
- Store screenshots should be recaptured from the current build before Play submission.
- Marketing copy must not imply real third-party data, official recommendations, or live paid entitlements unless those integrations exist and are licensed.

## 7. Not Blocking V0.9.58

These items should be tracked by @项目负责人 but should not pause V0.9.58 product experience iteration:

- Play Console developer verification.
- Payments profile, bank, and tax setup.
- Public privacy policy hosting.
- Data Safety and content rating forms.
- Google OAuth production setup.
- Auth verify backend.
- Billing verify backend.
- Play Billing product creation and pricing.
- RTDN/Pub/Sub subscription lifecycle handling.
- Internal testing track setup.
- Final store screenshots and feature graphic approval.
- Legal review of public store copy.

V0.9.58 can continue as long as:

- Missing OAuth keeps Google login disabled or clearly trial-safe.
- Missing Billing endpoint keeps purchase flow and paid entitlement disabled.
- Missing privacy URL/support email is not presented as completed launch metadata.
- Any mock/local route content remains labeled and does not claim real provider coverage.
- User-facing product copy avoids unsupported release claims.

## 8. PM Questions To Send User

Suggested message for @项目负责人:

```text
为了不阻塞 V0.9.58 的产品体验迭代，我们会先继续打磨 APK 体验；同时请你准备以下上架资料：

1. Google Play 开发者账号：个人还是组织？公开开发者名称、国家/地区、真实支持邮箱、计划发布地区。
2. 隐私政策：是否已有可公开访问的 HTTPS 域名/页面？发布者名称、隐私请求邮箱、是否要启用账号/后端同步/分析/广告/照片/定位。
3. Google 登录：本轮是否要启用？如果要，请准备 Google Cloud 项目、Android OAuth client、Web OAuth client ID、HTTPS token 验证后端。
4. 支付：本轮是否启用内购/订阅？如果要，请确认商品价格、权益说明、Payments profile、Billing verify HTTPS endpoint 和后端托管方案。
5. 测试人员：请提供内部测试邮箱、主要测试机型、Android 版本、语言/地区。
6. 素材版权：请确认用户提供或计划使用的图片、字体、地图/POI/内容来源是否有授权；未授权素材不会进入上架包或商店截图。

请不要把身份证件、银行卡、税务文件、Google 密码、Play Console 密码、keystore 密码、OAuth secret、服务账号私钥或一次性验证码发到聊天里；这些只应填写在 Google、银行、税务或云服务后台。
```

## 9. Release Ops Handling After User Replies

Once the user provides non-sensitive values, Release Ops can:

- Put public config into local ignored release config only when appropriate:
  - `SUPPORT_EMAIL`
  - `PRIVACY_POLICY_URL`
  - `GOOGLE_WEB_CLIENT_ID`
  - `AUTH_VERIFY_ENDPOINT`
  - `BILLING_VERIFY_ENDPOINT`
- Rebuild the APK/AAB when the release target requires it.
- Re-run release audits and submission gate.
- Update Play Console field checklist.
- Re-check Data Safety, privacy copy, review notes, and screenshots.

No code, Gradle, keystore, APK, or AAB changes were required to create this checklist.
