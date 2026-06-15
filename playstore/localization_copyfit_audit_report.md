# Localization Copy-Fit Audit

- Project: `D:\AppStore\nemu\real`
- Required locales: `zh-CN, zh-TW, en, ja, ko, es`
- Overall: `pass`

| Check | Status | Evidence | Required action |
| --- | --- | --- | --- |
| Android resources zh-CN | `pass` | D:\AppStore\nemu\real\app\src\main\res\values\strings.xml | Provide app_name and Android resource directory for this launch locale. |
| Store listing zh-CN | `pass` | D:\AppStore\nemu\real\playstore\localized\zh-CN\store_listing.md | Provide localized Play listing draft for this locale. |
| Android resources zh-TW | `pass` | D:\AppStore\nemu\real\app\src\main\res\values-zh-rTW\strings.xml | Provide app_name and Android resource directory for this launch locale. |
| Store listing zh-TW | `pass` | D:\AppStore\nemu\real\playstore\localized\zh-TW\store_listing.md | Provide localized Play listing draft for this locale. |
| Android resources en | `pass` | D:\AppStore\nemu\real\app\src\main\res\values-en\strings.xml | Provide app_name and Android resource directory for this launch locale. |
| Store listing en | `pass` | D:\AppStore\nemu\real\playstore\localized\en\store_listing.md | Provide localized Play listing draft for this locale. |
| Android resources ja | `pass` | D:\AppStore\nemu\real\app\src\main\res\values-ja\strings.xml | Provide app_name and Android resource directory for this launch locale. |
| Store listing ja | `pass` | D:\AppStore\nemu\real\playstore\localized\ja\store_listing.md | Provide localized Play listing draft for this locale. |
| Android resources ko | `pass` | D:\AppStore\nemu\real\app\src\main\res\values-ko\strings.xml | Provide app_name and Android resource directory for this launch locale. |
| Store listing ko | `pass` | D:\AppStore\nemu\real\playstore\localized\ko\store_listing.md | Provide localized Play listing draft for this locale. |
| Android resources es | `pass` | D:\AppStore\nemu\real\app\src\main\res\values-es\strings.xml | Provide app_name and Android resource directory for this launch locale. |
| Store listing es | `pass` | D:\AppStore\nemu\real\playstore\localized\es\store_listing.md | Provide localized Play listing draft for this locale. |
| In-app locale registry | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\localization\TodayPlayLocale.kt | Expose all launch locales in the app language selector. |
| Localized core screens | `pass` | HomeScreen.kt, SplashScreen.kt, ShopScreen.kt, HistoryScreen.kt, ShareCardScreen.kt | Core entry, monetization, history, and share screens should use localized string registries. |
| Localized quick-start screen copy | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\localization\QuickStartCopy.kt | Quick-start route ideas and CTA copy should be localized and protected against long translated labels. |
| Localized result screen shell copy | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\localization\ResultCopy.kt | Result page labels, route-card controls, feedback chips, and availability notices should follow the selected launch locale. |
| Localized route POI display metadata | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\generator\LocalItineraryGenerator.kt | Generated route content should localize selected POI display fields, tags, source labels, and compliance notes, not only the page shell. |
| Clean generated route display copy | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\generator\LocalItineraryGenerator.kt | Visible generated route text should use the clean display-copy adapter instead of legacy mojibake-prone literals. |
| Localized privacy screen copy | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\localization\PrivacyCopy.kt | Privacy and local-data disclosure should exist for every launch locale. |
| Localized history screen copy | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\localization\HistoryCopy.kt | History/library labels and empty state should be localized for every launch locale. |
| Localized share screen copy | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\localization\ShareCopy.kt | Share page, system share text, and completion-card labels should be localized for every launch locale. |
| Localized system toasts | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\localization\SystemCopy.kt | Map fallback, payment status, and required-field Toasts should follow the selected launch locale. |
| Button copy fit | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\components\TodayPlayComponents.kt | Repeated buttons, chips, and top-bar actions need single-line overflow guards. |
| Shop copy fit | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\ShopScreen.kt | Product names, subscription notes, and localized purchase buttons should avoid horizontal squeeze. |
| Result copy fit | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\QuestResultScreen.kt | Long city, route, and POI names should clamp safely on small screens. |
