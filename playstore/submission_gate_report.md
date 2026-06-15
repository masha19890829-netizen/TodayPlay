# Google Play Submission Gate

- Project: `D:\AppStore\nemu\real`
- Overall: `fail`

| Check | Status | Evidence | Required action |
| --- | --- | --- | --- |
| Versioning | `pass` | versionName=0.9.40, versionCode=58 | Set versionName and increment versionCode before every Play upload. |
| Target SDK | `pass` | compileSdk=35, targetSdk=35 | Keep targetSdk aligned with current Google Play requirements. |
| Release AAB | `pass` | D:\AppStore\nemu\real\app\build\outputs\bundle\release\app-release.aab | Run bundleRelease and upload the AAB, not the debug APK. |
| Release AAB signature | `pass` | path=D:\AppStore\nemu\real\app\build\outputs\bundle\release\app-release.aab, hasManifest=True, signatureFiles=['META-INF/TODAYPLA.SF'], certificateFiles=['META-INF/TODAYPLA.RSA'] | Verify the release AAB contains META-INF signature files before uploading to Play Console. |
| Debug APK | `warn` | D:\AppStore\nemu\real\app\build\outputs\apk\debug\app-debug.apk | Use debug APK only for local testing, not Play submission. |
| Artifact version freshness | `pass` | debugMetadataExists=True, expected=0.9.40/58, debugMetadata=0.9.40/58, metadataOutputFile='app-debug.apk', outputFileMatches=True, staleAgainstBuildGradle=[], releaseInputFiles=['D:\\AppStore\\nemu\\real\\app\\build.gradle.kts', 'D:\\AppStore\\nemu\\real\\keystore.properties'], staleAgainstReleaseInputs=[] | Rebuild APK/AAB after version, signing, or release-config changes; debug output metadata must match Gradle version. |
| Upload key | `pass` | keystore.properties exists=True, storeFile='release/todayplay-upload.jks', storeFileExists=True, hasUtf8Bom=False, fields={'storeFile': True, 'storePassword': True, 'keyAlias': True, 'keyPassword': True} | Create an upload key, configure keystore.properties without UTF-8 BOM, and enable Play App Signing. |
| Play Console contact email | `fail` | Contact email='TODO: developer support email' | Set a real developer support email in Play Console and play_console_submission_fields.md. |
| Play Console privacy URL | `fail` | Privacy policy URL='TODO: hosted playstore/privacy_site HTTPS URL' | Host playstore/privacy_site on a public HTTPS URL and put that URL in Play Console. |
| App support and privacy config | `fail` | releaseBuildConfigExists=True, releaseBuildConfig=D:\AppStore\nemu\real\app\build\generated\source\buildConfig\release\com\todayplay\app\BuildConfig.java, SUPPORT_EMAIL='', PRIVACY_POLICY_URL='', matchesPlayFields={'supportEmail': False, 'privacyPolicyUrl': False} | Set SUPPORT_EMAIL and PRIVACY_POLICY_URL in release_config.properties to match Play Console fields, then rebuild. |
| Store graphic assets | `pass` | app_icon_512.png=(512, 512), feature_graphic_1024x500.png=(1024, 500) | Provide a 512x512 app icon and 1024x500 feature graphic for Play Console. |
| Store screenshots | `warn` | 4 screenshot(s) found in D:\AppStore\nemu\real\playstore\screenshots, invalidDimensions=[], staleAgainstLatestAab=['phone_01_home_route_picker.png', 'phone_02_route_result.png', 'phone_03_checkin_completion.png', 'phone_04_premium_shop.png'], draftSource=True, visibleQuestionSeparatorReviewRequired=True | Capture at least 2 current real phone screenshots for Google Play; recapture after meaningful UI changes and reject visible '?' separators or mojibake. |
| Version documentation sync | `pass` | missing=[], stale=[], staleCurrentStatus=[] | Keep release plan, Play submission fields, and official requirements notes aligned with Gradle versionName/versionCode. |
| Localized store listings | `pass` | required=['zh-CN', 'zh-TW', 'en', 'ja', 'ko', 'es'], missing=[], incomplete=[] | Provide Play listing drafts for zh-CN, zh-TW, en, ja, ko, and es. |
| Privacy policy site package | `pass` | indexExists=True, headersExists=True, readmeExists=True, localesPresent={'en': True, 'zh-CN': True, 'zh-TW': True, 'ja': True, 'ko': True, 'es': True}, foundMojibake=[], hasBillingDisclosure=True, hasPermissionDisclosure=True, hasContentSourceDisclosure=True | Deploy playstore/privacy_site to a public HTTPS URL and replace publisher/support contact before Play submission. |
| In-app local data deletion | `pass` | privacyScreenExists=True, viewModelHasClear=True, repositoryHasClear=True, storeHasClear=True | Provide an in-app privacy/data screen that clears locally stored route history. |
| Map and mock media guard | `pass` | hasAmapFirst=True, hasBrowserFallback=True, hasClipboardFallback=True, hasDataAvailabilityNotice=True, photoDoesNotCheckIn=True | Provide clear map fallback behavior and do not let mock photo upload trigger real check-in state. |
| Travel content API boundary | `pass` | contractExists=True, repositoryExists=True, hasInterface=True, hasLocalMockRepository=True, generatorUsesInterface=True | Keep global POI/search content behind an official/licensed backend boundary; never ship client scraping as production data. |
| Data Safety draft | `pass` | D:\AppStore\nemu\real\playstore\data_safety_draft.md | Use the draft to fill the Play Console Data Safety form. |
| Review notes | `pass` | D:\AppStore\nemu\real\playstore\google_play_review_notes.md | Provide clear notes about mock data, external maps, and Billing state. |
| Billing backend endpoint | `fail` | BILLING_VERIFY_ENDPOINT='', source=release BuildConfig, releaseBuildConfigExists=True | Set a real HTTPS /billing/verify URL before live paid testing. |
| Billing purchase guard | `pass` | gatewayExists=True, hasEndpointCheck=True, hasHttpsRequirement=True, guardBeforeLaunch=True, hasShopDisabledState=True | Block launchBillingFlow while the server verification endpoint is missing or not HTTPS. |
| Billing backend skeleton | `pass` | workerExists=True, deploymentDocExists=True, hasGoogleApiCall=True | Keep backend skeleton deployable and configure it with Google Play service-account secrets. |
| Billing backend contract | `pass` | D:\AppStore\nemu\real\playstore\backend_billing_contract.md | Implement /billing/verify, /entitlements, and /billing/notifications. |
