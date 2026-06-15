# Release Config Audit

- Project: `D:\AppStore\nemu\real`
- Local config present: `False`
- Overall: `pass`

| Check | Status | Evidence | Required action |
| --- | --- | --- | --- |
| Release config template | `pass` | D:\AppStore\nemu\real\release_config.template.properties | Provide a committed template for public release URLs and support contact values. |
| Local release config ignored | `pass` | D:\AppStore\nemu\real\.gitignore | Do not commit local or CI release configuration values. |
| Gradle reads optional release config | `pass` | D:\AppStore\nemu\real\app\build.gradle.kts | Read public release configuration from release_config.properties with safe empty defaults. |
| BuildConfig exposes public launch fields | `pass` | D:\AppStore\nemu\real\app\build.gradle.kts | Expose Billing endpoint, travel-content endpoint, privacy URL, and support email through BuildConfig. |
| Privacy screen shows configured support fields | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\PrivacyScreen.kt | Show support email and privacy policy URL in the app without breaking small screens. |
| Privacy support links are actionable | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\PrivacyScreen.kt | When configured, users should be able to contact support and open the privacy policy without extra permissions. |
| Support copy localized | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\localization\PrivacyCopy.kt | Localize public support/configuration copy for all launch locales. |
| Configured URLs are HTTPS when present | `pass` | release_config.properties absent | If local release_config.properties is present, URL values must be HTTPS or empty. |
| Configured support email is syntactically valid when present | `pass` | release_config.properties absent | If SUPPORT_EMAIL is present, it must look like a real email address. |
