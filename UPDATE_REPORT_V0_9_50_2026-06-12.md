# TodayPlay V0.9.50 更新报告 - 2026-06-12

## 1. 本轮目标

V0.9.49 已经把首页拆成 `今天可做 / 城市包 / 关系包` 三个频道，但路线卡片的站点预览和同城证明仍然写在首页 UI 文件里。

本轮目标是把“路线包内容运营资料”从 UI 组件中拆出来，让后续产品策划和内容运营可以持续补城市包，而不是每次都改首页展示代码。

## 2. 已完成改动

- 新增内容目录文件：`app/src/main/java/com/todayplay/app/data/HomeRouteContentCatalog.kt`
- 新增 `CityRoutePackPreview` 数据结构。
- 新增 `HomeRouteContentCatalog`：
  - 统一维护频道 key：`today`、`city`、`relationship`
  - 统一维护首页路线卡站点预览
  - 统一维护同城校验文案和关系场景安全提示
- 首页 `HomeScreen.kt` 不再维护 `feedRouteStops` 和 `feedRouteProof`。
- 首页现在通过 `HomeRouteContentCatalog.routeStopsFor()` 和 `HomeRouteContentCatalog.routeProofFor()` 读取内容。
- App regression audit 已更新为检查独立内容目录，而不是检查旧的首页内部函数。
- 版本升级到 `0.9.50 / versionCode 68`。

## 3. 当前内容目录包含

- 上海：武康路 Citywalk、外滩黄昏观景线
- 深圳：深圳湾公园日落段、华侨城创意文化园
- 深圳家庭场景：深圳博物馆历史民俗馆、深圳湾公园日落段
- 广州：永庆坊老街小吃线、沙面岛低强度散步线
- 杭州：西湖湖边独处慢走线、小河直街书店咖啡线
- 台北：大稻埕迪化街轻探店线、淡水河岸黄昏散步线

## 4. 验证结果

- App regression audit：通过。
- 源码结构检查：通过。
  - 首页使用 `HomeRouteContentCatalog.routeStopsFor`。
  - 首页使用 `HomeRouteContentCatalog.routeProofFor`。
  - 首页已移除旧的 `feedRouteStops`。
  - 首页已移除旧的 `feedRouteProof`。
  - 内容目录包含 `CityRoutePackPreview` 和 `同城校验`。
- Debug 构建：通过。
- Lint：通过。
- 单元测试任务：通过，但当前项目没有 JVM 单测源码。
- Release 构建：通过。
- APK 元数据：`versionName=0.9.50`，`versionCode=68`。
- APK 签名验证：通过，v2 签名有效。
- 模拟器安装：通过。
- 模拟器冷启动：TodayPlay 进程正常，未发现 TodayPlay 崩溃日志。
- 首页截图：频道入口和路线卡片保持正常。
- 城市包频道生成验证：
  - 广州=True
  - 永庆坊=True
  - 沙面=True
  - 上海=False
  - 深圳=False

## 5. 交付物

- APK：`dist/TodayPlay-v0.9.50-release.apk`
- 默认首页截图：`dist/TodayPlay-v0.9.50-home.png`
- 城市包频道截图：`dist/TodayPlay-v0.9.50-city-channel.png`
- 城市包结果页截图：`dist/TodayPlay-v0.9.50-result.png`
- 城市包结果页结构：`dist/TodayPlay-v0.9.50-result.xml`

APK SHA-256：

`5C9070277F3EEBDCF66F07A0E0256EBD064BD82E8DCC9C202094F357C6071C8C`

## 6. 下一轮建议

- 将 `homeCityThemes` 也从多语言文案文件中继续拆出，形成完整的运营内容包。
- 为每条路线包增加移动压力字段：同区、近距离、跨区、半日。
- 给城市包补内容状态：样例、待核验、运营精选、授权来源。
- QA 增加三频道各一条路线的自动检查，避免只测默认首页。
