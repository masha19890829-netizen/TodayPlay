# TodayPlay V0.9.51 更新报告 - 2026-06-12

## 1. 本轮目标

V0.9.50 已经把首页路线卡片的站点预览抽进 `HomeRouteContentCatalog`，但城市主题包仍有一部分停留在多语言文案层，不利于产品策划和内容运营持续补城市内容。

本轮目标是继续把“城市包”做成可运营的内容资产：每个城市包不仅有标题和标签，还要有移动压力、内容状态、来源状态和可直接生成路线的输入参数。这样后续团队可以围绕城市包扩内容，而不是继续在首页 UI 里堆文案。

## 2. 已完成改动

- 新增 `HomeCityThemePack` 数据结构，集中描述城市包内容。
- `HomeRouteContentCatalog.cityThemePacks` 现在维护广州、杭州、台北 3 个城市主题包。
- 每个城市主题包补充：
  - `mobilityPressure`：如同区轻走、近距离、短线步行。
  - `contentStatus`：如运营样例、待核验样例。
  - `sourceStatus`：如本地样例 POI。
  - `input`：可直接生成路线的完整 `QuestInput`。
- 首页 `CityThemeCards` 改为读取 `HomeRouteContentCatalog.cityThemePacks`。
- 首页不再依赖 `strings.homeCityThemes` 渲染城市包。
- 城市包卡片现在展示移动压力、内容状态和来源状态，方便用户知道这是不是一条轻负担、已运营、可体验的路线包。
- App regression audit 增加了城市包内容目录和状态字段检查，防止城市包退回纯 UI 文案。
- 版本升级到 `0.9.51 / versionCode 69`。

## 3. 产品负责人判断

这轮不是单纯“多放几条内容”，而是在给后续内容运营打底：

- 产品经理可以继续定义“朋友局、暧昧夜散步、独处疗愈、亲子轻任务”等内容品类。
- 市场/竞品调研角色可以把竞品中可复用的卡片机制转成结构字段，而不是只写建议。
- UI 美术可以基于内容状态设计视觉层级，比如“运营精选”“待核验样例”“本地 POI”。
- 开发可以继续接入本地 AI 或 Kimi API，但当前版本先保证无 API 依赖也能跑通核心路线体验。

## 4. 验证结果

- App regression audit：通过。
- 源码结构检查：通过。
  - 首页使用 `HomeRouteContentCatalog.cityThemePacks`。
  - 首页不再使用 `strings.homeCityThemes` 渲染城市包。
  - 内容目录包含 `HomeCityThemePack`。
  - 内容目录包含 `mobilityPressure`、`contentStatus`、`sourceStatus`。
- Debug 构建：通过。
- Lint：通过。
- 单元测试任务：通过；当前项目没有 JVM 单测源码，所以任务结果为 `NO-SOURCE`。
- Release 构建：通过。
- APK 元数据：`versionName=0.9.51`，`versionCode=69`。
- APK 签名验证：通过，v2 签名有效。
- 模拟器安装：通过。
- 模拟器冷启动：通过，应用进程正常，未发现 TodayPlay `FATAL EXCEPTION`。
- 首屏视觉 QA：通过，首页保持卡片流、图片路线预览、同城校验、收藏/开始/邀请入口。
- 城市包频道 QA：通过，广州/杭州/台北城市包可见，城市包不是设置项或说明页。
- 广州城市包生成结果 QA：通过。
  - 广州=True
  - 永庆坊=True
  - 沙面岛=True
  - 上海=False
  - 深圳=False

## 5. 交付物

- APK：`dist/TodayPlay-v0.9.51-release.apk`
- 首页截图：`dist/TodayPlay-v0.9.51-home.png`
- 城市包频道截图：`dist/TodayPlay-v0.9.51-city-channel.png`
- 城市包结果页截图：`dist/TodayPlay-v0.9.51-result.png`
- 城市包结果页结构：`dist/TodayPlay-v0.9.51-result.xml`

APK SHA-256：
`F8512DE5EF66CAF8F6F0C16144729EBECF572055F5755214EB20AF9D6F92F20C`

## 6. 下一轮建议

- 继续把旧的 `LocalizedHomeCityTheme` 从多语言文案文件中清理掉，避免内容入口有两套来源。
- 给城市包增加“适合谁”“避雷提醒”“可分享标题”，让用户前 2 分钟更快判断是否想开始。
- 增加更多城市包：上海、深圳、成都、重庆、南京、厦门，每个城市先做 3 条高质量本地样例。
- QA 增加三频道各一条路线的点击生成检查，避免只测默认首页。
- UI 美术下一轮重点优化城市包卡片的内容状态表达，让“运营精选”和“待核验样例”的可信度差异更清楚。
