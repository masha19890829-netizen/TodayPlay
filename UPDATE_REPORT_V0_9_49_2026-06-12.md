# TodayPlay V0.9.49 更新报告 - 2026-06-12

## 1. 本轮目标

V0.9.48 已经让卡片有路线证据，但首页仍然只有“关系筛选”。竞品调研后，本轮把首页进一步拆成轻频道，让用户先进内容意图，再按关系过滤。

本轮新增三个首页频道：

- 今天可做
- 城市包
- 关系包

## 2. 竞品学习结论

- 内容流产品先给可浏览内容，再通过点击、保存、继续看来理解兴趣。
- Explore 类产品用轻量频道/瓷砖降低选择成本。
- 熟人/朋友关系产品会把关系模式单独组织，而不是把所有内容混在一个大列表里。

本轮对应落地：

- 首页顶部新增内容频道，优先展示“今天可做”。
- 城市包频道只看城市路线包。
- 关系包频道聚合关系场景路线。
- 原来的对象、暧昧、朋友、家人、自己仍保留为第二层过滤。

## 3. 已完成改动

- 新增 `HomeContentChannelRail`。
- 新增 `HomeContentChannel`。
- 新增 `homeContentChannels()`：
  - 简体中文：今天可做 / 城市包 / 关系包
  - 繁体中文：今天可做 / 城市包 / 關係包
  - 其他语言：Today / City packs / People
- `RouteFeedItem` 新增 `channels` 字段。
- 首页瀑布流现在按 `selectedChannel` 先筛选，再按关系 `selectedScenario` 二次筛选。
- App regression audit 新增频道守门，防止首页退回单层关系筛选。
- 版本升级到 `0.9.49 / versionCode 67`。

## 4. 验证结果

- App regression audit：通过。
- Debug 构建：通过。
- Lint：通过。
- 单元测试任务：通过，但当前项目没有 JVM 单测源码。
- Release 构建：通过。
- APK 元数据：`versionName=0.9.49`，`versionCode=67`。
- APK 签名验证：通过，v2 签名有效。
- 模拟器安装：通过。
- 模拟器冷启动：TodayPlay 进程正常，未发现 TodayPlay 崩溃日志。
- 首页视觉验证：频道入口在第一屏可见，没有挤坏瀑布流卡片。
- 城市包频道验证：点击后首屏切换到广州、杭州、台北城市路线包。
- 城市包首卡生成验证：
  - 广州=True
  - 永庆坊=True
  - 沙面=True
  - 上海=False
  - 深圳=False

说明：第一次并行运行多个 Gradle 任务时损坏了 Kotlin 增量编译缓存。已执行 Gradle stop/clean 后按顺序重新运行，最终全部通过。该问题属于构建缓存冲突，不是应用代码错误。

## 5. 交付物

- APK：`dist/TodayPlay-v0.9.49-release.apk`
- 默认首页截图：`dist/TodayPlay-v0.9.49-home.png`
- 城市包频道截图：`dist/TodayPlay-v0.9.49-city-channel.png`
- 城市包结果页截图：`dist/TodayPlay-v0.9.49-result.png`
- 城市包结果页结构：`dist/TodayPlay-v0.9.49-result.xml`

APK SHA-256：

`C0198C147B96BAD7181502BFDBFF33F33D4031FFDE5529946234EF09778038EC`

## 6. 下一轮建议

- 内容运营把城市包拆成可独立维护的数据文件，不要继续写死在首页代码里。
- 美术继续优化频道切换动效和城市包卡片视觉，让城市路线包更像可收藏的“路线票”。
- 开发补“移动压力”字段：同区、近距离、跨区、半日。
- QA 为三个频道各生成一条路线，检查城市、站点、结果页一致性。
