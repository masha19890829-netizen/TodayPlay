# Google Play 瀹樻柟瑕佹眰瀵圭収琛紙2026-06-08锛?
姝ゆ枃浠剁敤浜庢妸褰撳墠椤圭洰鍜?Google Play 瀹樻柟涓婃灦瑕佹眰瀵归綈銆傛渶缁堟彁浜ゅ墠浠嶉渶鍦?Play Console 涓寜瀹炴椂鎻愮ず澶嶆牳銆?
## 瀹樻柟鏉ユ簮

- Target API level requirements: https://developer.android.com/google/play/requirements/target-sdk
- Android App Bundle overview: https://developer.android.com/guide/app-bundle
- Google Play Billing integration: https://developer.android.com/google/play/billing/integrate
- Google Play Billing Library deprecation FAQ: https://developer.android.com/google/play/billing/deprecation-faq
- Google Play Data Safety: https://support.google.com/googleplay/android-developer/answer/10787469

## 褰撳墠椤圭洰鐘舵€?
- 鍖呭悕锛歚com.todayplay.app`
- 鐗堟湰锛歚0.9.40 / versionCode 58`
- `compileSdk`锛歚35`
- `targetSdk`锛歚35`
- `minSdk`锛歚26`
- Google Play 浜х墿锛歚app-release.aab`
- 鏈湴娴嬭瘯浜х墿锛歚app-debug.apk`
- 涓诲姩澹版槑鏉冮檺锛歚android.permission.INTERNET`
- Billing 渚濊禆锛歚com.android.billingclient:billing:8.3.0`
- Billing 楠屽崟 endpoint锛歚BuildConfig.BILLING_VERIFY_ENDPOINT`锛岄€氳繃鏈彁浜ょ殑 `release_config.properties` 閰嶇疆锛涘綋鍓嶉粯璁や负绌哄崰浣嶃€?- Upload key锛氬凡鐢熸垚鏈湴涓婁紶瀵嗛挜锛岃 `playstore/upload_key_status.md`銆?
## 瑕佹眰瀵圭収

| 椤圭洰 | 褰撳墠鐘舵€?| 缁撹 |
| --- | --- | --- |
| 鏂板簲鐢?鏇存柊鐩爣 API | `targetSdk 35` | 婊¤冻 Android 15 / API 35 杩欎竴杞姹傘€傚悗缁嫢 Play Console 瑕佹眰 API 36锛岄渶瑕佸崌绾?Android SDK銆丄GP 鍜屼緷璧栥€?|
| Google Play 鍙戝竷鏍煎紡 | 宸茶兘鐢熸垚 `app-release.aab` | 婊¤冻 Play 鍙戝竷鏍煎紡锛汥ebug APK 鍙敤浜庢湰鍦版祴璇曘€?|
| 鐗堟湰鍙?| `versionCode 58` | 婊¤冻閫掑鍘熷垯锛涙瘡娆′笂浼犳柊 AAB 鍓嶇户缁€掑銆?|
| 绛惧悕 | 宸茬敓鎴愭湰鍦?upload key锛孏radle release build 宸插彲璇诲彇 `keystore.properties` | 鏈湴绛惧悕鍑嗗宸插畬鎴愶紱浠嶉渶鍦?Play Console 鍚敤 Play App Signing 骞朵笂浼?AAB銆?|
| 浠樿垂鎺ュ彛 | Billing 8.3.0銆佸晢鍝佹煡璇€佽喘涔板洖璋冦€乸urchaseToken銆丠TTPS 鍚庣楠屽崟璋冪敤楠ㄦ灦宸插瓨鍦紝endpoint 褰撳墠涓虹┖ | 瀹㈡埛绔鏋跺悎鏍硷紱姝ｅ紡浠樿垂杩樼己 Play Console 鍟嗗搧鍜岀湡瀹炲悗绔獙鍗?URL銆?|
| Billing Library 鏀寔鏈?| 褰撳墠涓?8.x | 褰撳墠涓嶆槸纭樆濉烇紱鍚庣画鍗囩骇鍒?Billing 9 闇€瑕佸厛鍗囩骇 Kotlin/AGP/SDK 宸ュ叿閾俱€?|
| 闅愮鏀跨瓥 | 宸叉湁 HTML 鑽夋锛汚pp 鍐呭凡鎺ュ叆 `PRIVACY_POLICY_URL` 涓?`SUPPORT_EMAIL` 閰嶇疆灞曠ず | 浠嶉渶鎵樼涓哄叕缃?URL锛屽苟鍦?`release_config.properties` 涓?Play Console 涓～鍐欑湡瀹炴敮鎸侀偖绠便€?|
| Data Safety | 宸叉湁濉啓搴曠 | 浠嶉渶鎸夋渶缁堜笂绾胯涓哄湪 Play Console 鎻愪氦銆?|
| 鏉冮檺鏀舵暃 | 鏈姹傚畾浣嶃€佺浉鍐屻€佹憚鍍忓ご銆侀€氳褰曠瓑鏁忔劅鏉冮檺 | 绗﹀悎褰撳墠鐗堟湰鏈€灏忔潈闄愮瓥鐣ャ€?|
| 鎴浘 | 宸叉湁鎴浘閲囬泦鏂规 | 浠嶉渶鐪熷疄璁惧鎴栨ā鎷熷櫒鎴浘銆?|

## 鏈疆渚濊禆鍐崇瓥

淇濈暀鍗囩骇锛?
- `androidx.activity:activity-compose`锛歚1.9.3` -> `1.10.1`
- `com.android.billingclient:billing`锛歚8.0.0` -> `8.3.0`

宸茶ˉ寮猴細

- `BuildConfig.BILLING_VERIFY_ENDPOINT` 閰嶇疆鐐广€?- 璐拱鍥炶皟鍚庡紓姝ユ彁浜?`/billing/verify` 鐨勭綉缁滈獙鍗曢鏋躲€?- 闈?HTTPS endpoint 鎷掔粷銆?- endpoint 涓虹┖鏃朵笉鍙戠綉缁滆姹傘€佷笉鍙戞斁鏉冪泭銆?
鏆備笉鍗囩骇锛?
- Compose BOM 鍒?`2026.05.01`
- Activity 鍒?`1.13.0`
- Lifecycle 鍒?`2.10.0`
- Billing 鍒?`9.0.0`

鍘熷洜锛?
- 鏈€鏂?Activity/Compose 渚濊禆瑕佹眰 `compileSdk 36` 鍜?AGP `8.9.1+`銆?- 褰撳墠鏈満 SDK 鍙湁 Android 35锛岄」鐩?AGP 涓?`8.7.3`銆?- 寮鸿鍗囩骇浼氱牬鍧忓綋鍓?`assembleDebug`銆乣bundleRelease`銆乣lintDebug` 楠岃瘉閾俱€?
## 涓嬩竴姝ョ‖闂ㄦ

1. 鍦?Play Console 鍒涘缓搴旂敤鍜屽唴閮ㄦ祴璇曡建閬撱€?2. 寮€鍚?Play App Signing锛屽苟浣跨敤褰撳墠 upload key 涓婁紶 AAB銆?3. 鎵樼闅愮鏀跨瓥锛岃幏寰楀彲璁块棶 URL銆?4. 鍦?Play Console 鍒涘缓鍟嗗搧锛屽晢鍝?ID 涓庡鎴风淇濇寔涓€鑷淬€?5. 瀹炵幇鍚庣 `/billing/verify`銆乣/entitlements`銆乣/billing/notifications`锛屽苟鎶婂鎴风 `BILLING_VERIFY_ENDPOINT` 鎸囧悜鐪熷疄 HTTPS URL銆?6. 浣跨敤鐪熷疄璁惧鎴栨ā鎷熷櫒閲囬泦鍟嗗簵鎴浘銆?7. 鍦?Play Console 濉啓 Data Safety銆佸唴瀹瑰垎绾с€佸鏍歌鏄庡拰鍟嗗簵鏂囨銆?
## 2026-06-11 瀹樻柟瑕佹眰澶嶆牳

- 宸插鏍?Google Play 瀹樻柟 target API level requirement 椤甸潰锛氭埅鑷?2026-06-11锛屾櫘閫氭柊搴旂敤鍜屾洿鏂颁粛瑕佹眰浠?Android 15 / API 35 鎴栨洿楂樼増鏈负鐩爣銆?- 褰撳墠椤圭洰 `compileSdk = 35`銆乣targetSdk = 35`锛屾弧瓒虫湰杞?Google Play target SDK 瑕佹眰銆?- 褰撳墠涓嶅己琛屽崌绾у埌 API 36锛氭湰鏈?Android SDK銆丄GP銆丆ompose銆丄ctivity銆丩ifecycle銆丅illing 渚濊禆缁勫悎浠嶄互 API 35 鏋勫缓閾炬渶绋冲畾銆?- 鍚庣画濡傛灉 Play Console 鎴栧畼鏂规枃妗ｅ皢鏈€浣庤姹傛帹杩涘埌 API 36锛屽簲浣滀负鐙珛宸ュ叿閾惧崌绾т换鍔″鐞嗭細瀹夎 Android 36 SDK銆佸崌绾?AGP/Kotlin/Compose/Billing锛屽苟閲嶆柊璺戝畬鏁撮妫€銆?- 鏈疆鏂板 `playstore/privacy_site_audit.py`锛屽皢澶氳瑷€闅愮鏀跨瓥绔欑偣鐨?UTF-8 缂栫爜銆佸叚璇叆鍙ｃ€佷粯娆炬姭闇层€佸湴鍥捐烦杞姭闇层€乵ock/鎺堟潈鍐呭杈圭晫鍜屽崰浣嶈仈绯绘柟寮忚鍛婄撼鍏ュ彂甯冮妫€銆?- V0.9.22 灏嗗悓涓€濂楅殣绉佺珯鐐硅川閲忎俊鍙峰苟鍏?`playstore/google_play_submission_gate.py`锛岄伩鍏嶅彧璺?Google Play submission gate 鏃舵紡鎺夊叚璇殣绉侀〉銆佷贡鐮佸拰鍏抽敭鎶湶妫€鏌ャ€?- V0.9.23 鏂板 `playstore/localized_source_encoding_audit.py`锛屾妸 Kotlin 鏈湴鍖栨簮鐮併€佸睆骞曟簮鐮併€丄ndroid 瀛楃涓层€丳lay 澶氳瑷€鍟嗗簵鏂囨鍜岄殣绉佺珯鐐圭殑 UTF-8/涔辩爜妫€鏌ョ撼鍏ュ畬鏁撮妫€銆?- V0.9.24 灏?App 鍐呴殣绉侀〉鐨勬敮鎸侀偖绠卞拰闅愮鏀跨瓥 URL 鍙樹负鍙搷浣滃叆鍙ｏ細閰嶇疆鍚庡彲鎵撳紑 `mailto:` 鍜?HTTPS 闅愮鏀跨瓥锛涙湭閰嶇疆鏃舵寜閽鐢紝骞剁敱 `release_config_audit.py` 鑷姩妫€鏌ャ€?- V0.9.25 灏?Google Play submission gate 鐨勯殣绉佹斂绛栧彛寰勬敹鏁涘埌 `playstore/privacy_site` 澶氳瑷€绔欑偣鍖咃紝涓嶅啀鎶婃棫鐨?`privacy_policy_zh.html` 褰撲綔涓婚殣绉佹斂绛栨潗鏂欙紱鍏紑 HTTPS URL 鍜岀湡瀹炶仈绯绘柟寮忎粛鐢?Play Console 瀛楁闃诲銆?- V0.9.26 灏?Google Play submission gate 鐨?Play Console 澶栭儴瀛楁鎷嗗垎涓?`Play Console contact email` 鍜?`Play Console privacy URL`锛屽垎鍒獙璇佺湡瀹為偖绠辨牸寮忓拰鍏紑 HTTPS 闅愮鏀跨瓥 URL锛岄伩鍏嶄竴涓缁熷け璐ラ」闅愯棌鍏蜂綋闃诲銆?Current release checkpoint: `0.9.40 / versionCode 58`.


V0.9.40 keeps the release requirement stance unchanged while improving visual execution quality: the result page live route mode now has a route cockpit, metric tiles, and a lightweight timeline, while home City Theme cards gain a small route motif. No new permissions, external APIs, live data, or large asset library were added. External Play Console fields, Billing endpoint, and current real-device screenshots remain required before upload.

V0.9.39 keeps the release requirement stance unchanged while improving route-result usability: the result page now has a local live route mode that highlights the current stop and next safe action without adding new permissions, external APIs, or live data dependencies. External Play Console fields, Billing endpoint, and current real-device screenshots remain required before upload.

V0.9.38 keeps the release requirement stance unchanged while improving the first-screen product experience: the home screen now includes local City Themes for one-tap route generation, with no new external API, scraping, or third-party content dependency. External Play Console fields, Billing endpoint, and current real-device screenshots remain required before upload.

V0.9.37 keeps the release requirement stance unchanged while improving route-editing safety: replacing one stop now stores a local one-step restore snapshot so users can bring back the previous stop and its progress. External Play Console fields, Billing endpoint, and current real-device screenshots remain required before upload.

V0.9.36 keeps the release requirement stance unchanged while improving route-editing safety: single-stop replacement now uses a preview-and-confirm flow before changing the route. External Play Console fields, Billing endpoint, and current real-device screenshots remain required before upload.

V0.9.35 keeps the release requirement stance unchanged while adding a local route-editing step: users can replace one route stop with a same-city candidate while preserving the overall route. Replacement persists locally and clears stale stop progress/reward state. External Play Console fields, Billing endpoint, and current real-device screenshots remain required before upload.

V0.9.34 keeps the release requirement stance unchanged while improving route resilience: route stop cards now include a single-stop swap path. Swap usage resolves a stop for route progress but does not award check-in points, keeping mock/alternative actions separate from real check-ins. External Play Console fields, Billing endpoint, and current real-device screenshots remain required before upload.

V0.9.33 keeps the release requirement stance unchanged while improving the core route-result experience: the result screen now exposes a start playbook, safe swap information, and stop-level missions/rewards. External Play Console fields, Billing endpoint, and current real-device screenshots remain required before upload.

V0.9.27 adds stricter release-hardening checks based on the V0.9.25 test report: signed-AAB ZIP metadata, keystore UTF-8 BOM detection, screenshot dimension/freshness checks, and release-document version sync.
