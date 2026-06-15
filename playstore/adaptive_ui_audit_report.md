# Adaptive UI Audit

- Project: `D:\AppStore\nemu\real`
- Overall: `pass`

| Check | Status | Evidence | Required action |
| --- | --- | --- | --- |
| Home screen width classes | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\HomeScreen.kt | Home should switch between phone, compact-height, and wide/tablet layouts. |
| Home scrollable compact layout | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\HomeScreen.kt | Compact home layout must scroll instead of compressing content. |
| Card flow width classes | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\CreateQuestScreen.kt | Preference card flow needs small-screen width handling and vertical scrolling. |
| Card flow compact choice layout | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\CreateQuestScreen.kt | Choice cards should become single-column full-width cards on narrow screens and clamp long labels. |
| Step shell compact padding | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\CreateQuestScreen.kt | Step cards should reduce padding and title size on narrow screens. |
| Button text overflow guards | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\components\TodayPlayComponents.kt | Primary, ghost, chip, and top bar text should not overflow small screens or localized labels. |
| Result responsive route layout | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\QuestResultScreen.kt | Route result list should reduce horizontal padding on narrow phones and foldable cover screens. |
| Result long place text guards | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\QuestResultScreen.kt | Route titles and POI names should clamp safely for multilingual place names. |
| Shop responsive payment layout | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\ShopScreen.kt | Payment/shop content should reduce side padding on small screens. |
| Shop localized copy overflow guards | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\ShopScreen.kt | Long translated shop titles, product names, and price notes should not squeeze horizontally. |
| Launch locale coverage | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\localization\TodayPlayLocale.kt | The app should expose Simplified Chinese, Traditional Chinese, English, Japanese, Korean, and Spanish. |
| No viewport-scaled typography | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\theme\Type.kt | Typography should not scale font sizes directly from viewport width, and letter spacing should remain neutral. |
