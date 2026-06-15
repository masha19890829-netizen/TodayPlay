# Localization Audit

- Project: `D:\AppStore\nemu\real`
- Required locales: `zh-CN, zh-TW, en, ja, ko, es`
- Overall: `pass`

| Check | Status | Evidence | Required action |
| --- | --- | --- | --- |
| Locale string registry | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\localization\TodayPlayLocale.kt | Create a typed locale/string registry for in-app text. |
| In-app locale zh-CN | `pass` | SimplifiedChinese / zh-CN | Add this locale to TodayPlayLocale and provide TodayPlayStrings. |
| Android app name zh-CN | `pass` | D:\AppStore\nemu\real\app\src\main\res\values\strings.xml: appNameLength=5 | Provide localized app_name in the Android resource directory. |
| Play listing zh-CN | `pass` | D:\AppStore\nemu\real\playstore\localized\zh-CN\store_listing.md: shortDescriptionLength=26 | Create localized Play listing text with short description, full description, and release notes. |
| In-app locale zh-TW | `pass` | TraditionalChinese / zh-TW | Add this locale to TodayPlayLocale and provide TodayPlayStrings. |
| Android app name zh-TW | `pass` | D:\AppStore\nemu\real\app\src\main\res\values-zh-rTW\strings.xml: appNameLength=5 | Provide localized app_name in the Android resource directory. |
| Play listing zh-TW | `pass` | D:\AppStore\nemu\real\playstore\localized\zh-TW\store_listing.md: shortDescriptionLength=26 | Create localized Play listing text with short description, full description, and release notes. |
| In-app locale en | `pass` | English / en | Add this locale to TodayPlayLocale and provide TodayPlayStrings. |
| Android app name en | `pass` | D:\AppStore\nemu\real\app\src\main\res\values-en\strings.xml: appNameLength=9 | Provide localized app_name in the Android resource directory. |
| Play listing en | `pass` | D:\AppStore\nemu\real\playstore\localized\en\store_listing.md: shortDescriptionLength=71 | Create localized Play listing text with short description, full description, and release notes. |
| In-app locale ja | `pass` | Japanese / ja | Add this locale to TodayPlayLocale and provide TodayPlayStrings. |
| Android app name ja | `pass` | D:\AppStore\nemu\real\app\src\main\res\values-ja\strings.xml: appNameLength=7 | Provide localized app_name in the Android resource directory. |
| Play listing ja | `pass` | D:\AppStore\nemu\real\playstore\localized\ja\store_listing.md: shortDescriptionLength=33 | Create localized Play listing text with short description, full description, and release notes. |
| In-app locale ko | `pass` | Korean / ko | Add this locale to TodayPlayLocale and provide TodayPlayStrings. |
| Android app name ko | `pass` | D:\AppStore\nemu\real\app\src\main\res\values-ko\strings.xml: appNameLength=10 | Provide localized app_name in the Android resource directory. |
| Play listing ko | `pass` | D:\AppStore\nemu\real\playstore\localized\ko\store_listing.md: shortDescriptionLength=40 | Create localized Play listing text with short description, full description, and release notes. |
| In-app locale es | `pass` | Spanish / es | Add this locale to TodayPlayLocale and provide TodayPlayStrings. |
| Android app name es | `pass` | D:\AppStore\nemu\real\app\src\main\res\values-es\strings.xml: appNameLength=9 | Provide localized app_name in the Android resource directory. |
| Play listing es | `pass` | D:\AppStore\nemu\real\playstore\localized\es\store_listing.md: shortDescriptionLength=63 | Create localized Play listing text with short description, full description, and release notes. |
| Composition local wiring | `pass` | MainActivity locale provider | Wrap app screens in the TodayPlay locale provider. |
| Home language selector | `pass` | HomeScreen card-flow language selector | Expose language selection without long-form settings screens. |
| Splash localized copy | `pass` | SplashScreen uses localized strings | Move splash copy to the string registry. |
| Shop localized copy | `pass` | ShopScreen uses localized strings | Move payment/shop copy to the string registry. |
| Privacy localized copy | `pass` | PrivacyScreen uses PrivacyStrings for zh-CN, zh-TW, en, ja, ko, and es. | Keep privacy/data deletion text localized whenever privacy behavior changes. |
| Generated route copy scope | `pass` | Route generator uses locale-aware copy for itinerary summaries, check-in tasks, risk tips, and compliance notes. | Keep expanding localized POI names and route-result UI labels as the production catalog grows. |
