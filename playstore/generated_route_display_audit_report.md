# Generated Route Display Audit

- Project: `D:\AppStore\nemu\real`
- Overall: `pass`

| Check | Status | Evidence | Required action |
| --- | --- | --- | --- |
| Generator file exists | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\generator\LocalItineraryGenerator.kt | Keep route generation in the expected Kotlin source file. |
| Active flow uses safe display copy | `pass` | all required safe calls found | Wire every visible generated route field through safe display-copy helpers. |
| Active flow avoids legacy display copy | `pass` | no forbidden legacy calls found | Do not call mojibake-prone legacy display-copy helpers from the active result flow. |
| POI localization uses safe display copy | `pass` | all required POI safe calls found | Route POI display fields should use safe copy helpers before cards are built. |
| POI localization avoids legacy display copy | `pass` | no forbidden POI legacy calls found | Do not use mojibake-prone legacy POI helpers in the selected POI display path. |
| Safe copy adapter coverage | `pass` | safe copy helpers present | Keep clean multilingual helpers for visible generated route result fields. |
| Safe copy block mojibake scan | `pass` | no obvious mojibake fragments in safe copy block | Replace corrupted text fragments in the active safe display-copy adapter. |
