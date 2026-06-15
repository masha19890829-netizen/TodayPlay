# TodayPlay V0.9.52 Update Report - 2026-06-12

## Goal

把 TodayPlay 从“可用路线工具”推进成更适合商店截图转化的主线版本：保留 `TodayPlay - 今天怎么玩`，并把 `心动路线 / 今晚组局 / 慢慢走` 做成首页三个强频道。

## Shipped Changes

- 首页频道升级为 `今天可做 / 心动路线 / 今晚组局 / 慢慢走 / 城市包`，语言和设置继续留在后置路径。
- 首页默认信息流优先展示三类强内容：约会心动线、朋友组局线、独处慢走线。
- `HomeRouteContentCatalog` 扩展为运营内容目录，新增 9 条本地/运营样例路线：
  - 心动路线 3 条：上海、深圳、台北。
  - 今晚组局 3 条：广州、上海、深圳。
  - 慢慢走 3 条：杭州、上海、台北。
- 每条路线包都带有城市、适合关系、移动压力、内容状态、来源状态、2 个站点预览和 `QuestInput`。
- 结果页仍沿用同城路线生成逻辑，首页路线包进入结果页后会显示地图预览、当前站和站点顺序。
- 首页频道栏改为横向滚动，避免小屏下把 `心动路线 / 今晚组局 / 慢慢走` 截成省略号。
- `playstore/app_regression_audit.py` 增加商店转化主线守护，要求三强频道、内容目录、站点预览和同城校验存在。
- 版本升级到 `0.9.52 / versionCode 70`。

## Validation

- `playstore/app_regression_audit.py`: pass
- `assembleDebug`: pass
- `testDebugUnitTest`: pass, no unit test sources currently present
- `lintDebug`: pass
- `assembleRelease`: pass
- APK metadata: `com.todayplay.app`, `versionName=0.9.52`, `versionCode=70`
- Release APK signature: pass, APK Signature Scheme v2
- Signer certificate SHA-256: `2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695`
- APK SHA-256: `4DACA3C5BAF13B9421CCBAF35DB7CECEB08FFFE72B51756D5047624964480F73`
- Emulator install and cold launch: pass
- Home visual QA: pass, first screen is card feed, not text wall
- Channel QA: pass, `心动路线 / 今晚组局 / 慢慢走` each shows matching route cards
- Result-page QA: pass, date route opens Shanghai result with Wukang Road and Bund route preview
- Recent log check: no `FATAL EXCEPTION` or TodayPlay crash marker found

## Deliverables

- APK: `dist/TodayPlay-v0.9.52-release.apk`
- Home screenshot: `dist/TodayPlay-v0.9.52-home.png`
- Date channel screenshot: `dist/TodayPlay-v0.9.52-date-channel.png`
- Friend channel screenshot: `dist/TodayPlay-v0.9.52-friend-channel.png`
- Solo channel screenshot: `dist/TodayPlay-v0.9.52-solo-channel.png`
- Result screenshot: `dist/TodayPlay-v0.9.52-result.png`
- Screenshot mapping: `APP_CONCEPTS_FOR_DOWNLOAD_CONVERSION_2026-06-12.md`

## Not Included This Round

- No real Google login wiring.
- No payment or Billing production configuration.
- No Kimi API integration.
- No Play Console submission.
