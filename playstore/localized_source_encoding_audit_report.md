# Localized Source Encoding Audit

Project: `D:\AppStore\nemu\real`
Overall: **pass**

| Check | Status | Detail |
| --- | --- | --- |
| Required localized tokens in app/src/main/java/com/todayplay/app/localization/TodayPlayLocale.kt | pass | 简体中文, 繁體中文, 日本語, 한국어, Español |
| Required localized tokens in app/src/main/java/com/todayplay/app/localization/PrivacyCopy.kt | pass | 隐私与本地数据, 隱私與本機資料, プライバシー, 개인정보, Privacidad |
| Required localized tokens in playstore/privacy_site/index.html | pass | 简体中文, 繁體中文, 日本語, 한국어, Español |
| UTF-8 decoding | pass | All checked files decode as UTF-8. |
| No common mojibake markers | pass | No common mojibake markers found. |
| Checked file coverage | pass | Checked 30 localized/source/listing files. |

## Scope
- `app/src/main/java/com/todayplay/app/localization/*.kt`
- `app/src/main/java/com/todayplay/app/ui/screens/*.kt`
- `app/src/main/res/values*/strings.xml`
- `playstore/localized/*/store_listing.md`
- `playstore/privacy_site/index.html`

## Notes
- This audit validates source encoding and obvious mojibake markers; it does not replace native-speaker translation review.
