# V0.9.31 市场调研角色与留存体验更新报告

日期：2026-06-11  
版本：`0.9.31 / versionCode 49`  
负责人视角：项目负责人调配产品、市场调研、开发  
本轮主题：引入市场调研角色，并把可复用机制先落到本地体验闭环

## 新增团队角色

已新增“产品策划 + 市场调研负责人”角色，线程负责只读研究同类产品和相邻赛道，包括旅行行程规划、城市探索、本地活动推荐、date night、地图收藏、AI itinerary 和活动票务。

初步参考产品与类别：

- Google Maps Saved Lists
- Wanderlog
- TripIt
- Tripadvisor Trips / AI trip planning
- Time Out
- Atlas Obscura
- Eventbrite / Fever
- Meetup
- OpenTable
- Viator / GetYourGuide / Airbnb Experiences
- AllTrails

市场调研初步结论：

- TodayPlay 不应做成纯地图或纯旅行助手，差异点应放在“关系场景 + 今日可执行 + 任务互动 + 可分享副本”。
- 最值得复用的机制是：场景模板、一键生成、地图/列表/时间轴、收藏复用、任务化、城市主题卡、分享副本卡。
- 需要避开的坑是：内容授权不清、强制真实定位、AI 胡编不存在地点、暧昧/家庭任务冒犯、过早付费墙、分享只是普通截图。

## 本轮已交付

1. 首页今日灵感扩容
   - 今日灵感从 3 张扩展到 5 张。
   - 新增：
     - 朋友低预算笑点
     - 一个人放空半日
   - 每张灵感卡都映射到不同关系、城市、预算、时长和任务意图。

2. 历史副本支持重玩
   - 历史卡新增“打开”和“再开类似一局”两个明确操作。
   - “再开类似一局”会沿用旧副本的关系、城市、预算、时长、交通和关键词，再生成一条新的路线。
   - 这让历史从记录页变成下一次生成的入口，提高复玩价值。

3. 多语言文案补齐
   - 首页新增灵感补齐简中、繁中、英文文案。
   - 日文、韩文、西语继续继承英文 fallback，保证数组长度和功能稳定。

4. 版本与项目材料同步
   - App 版本升到 `0.9.31 / versionCode 49`。
   - 同步当前版本行到发布计划、官方要求对照、Play Console 字段清单和 Release Ops 行动计划。

## 验证结果

- `assembleDebug`：通过
- `lintDebug`：通过
- `bundleRelease`：通过
- APK metadata：`versionName=0.9.31`，`versionCode=49`
- `playstore/google_play_submission_gate.py`：整体仍为 fail，但版本、AAB 签名、产物新鲜度、版本文档同步均通过；失败项仍是外部支持邮箱、公开隐私 URL、release 支持/隐私配置、Billing endpoint。
- `playstore/app_regression_audit.py`：通过

## 产物

- Debug APK：`app/build/outputs/apk/debug/app-debug.apk`
- Release AAB：`app/build/outputs/bundle/release/app-release.aab`

## 下一轮建议

1. 产品策划继续把竞品研究收敛成“必须做 / 该做 / 可延后”的需求列表。
2. 开发优先做“分享副本卡强化”：区分未完成邀请卡和已完成通关卡。
3. 继续补“历史收藏 / 重玩 / 筛选”：让历史页更像用户的路线资产库。
4. 在真实设备可用后，优先验证首页 5 张灵感、快速开始 9 条模板、历史重玩、结果页任务完成和分享路径。
