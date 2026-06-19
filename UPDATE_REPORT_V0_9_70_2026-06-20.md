# TodayPlay V0.9.70 Update Report

日期：2026-06-20  
版本：0.9.70 / versionCode 87  
交付 APK：`dist/TodayPlay-v0.9.70-release.apk`

## 本轮目标

本轮把 V0.9.69 的“AI 候选路线”继续向“有电影剪辑感、能看出个性化理由、能被外部测试者理解”的方向推进。重点不是新增大功能，而是让用户更明确看到：

- 系统理解了我的城市、关系、时间、预算和状态。
- 多张候选卡不是同一条固定路线换标题。
- 我点中的候选卡会继承到结果页。
- 结果页更像一张今日电影票和路线副本，而不是一页说明书。

## 团队分工与结论

- 项目负责人：收束 V0.9.70 为“导演剪辑 + 票根结果页 + 适配守护”版本，保证能输出可安装 APK。
- 首次体验产品经理：要求候选路线前置“为什么适合你”，结果页要继承候选卡策略，减少用户怀疑“又是固定内容”。
- 应用美术 UI 动效设计师：要求候选卡出现时有轻量转场，结果页加入票根式视觉层，加载页改成电影剪辑语义。
- Android 开发工程师：实现导演剪辑面板、卡片渐入、票根封面、理由文案和审计守护。
- QA 设备验证负责人：验证普通手机、小屏、折叠屏、横屏、结果页、日志和 APK 签名。

## 主要变更

### 1. 首页候选区加入“导演剪辑”

文件：`app/src/main/java/com/todayplay/app/ui/screens/HomeScreen.kt`

- 新增 `DirectorCutPanel`。
- 展示当前 intent：城市、关系、目标。
- 将候选卡组织成 `Act 1 / Act 2 / Act 3` 的剪辑感结构。
- 明确提示“选中哪张，结果页就继承哪张卡的站点、顺序和理由”。
- 候选卡使用 `fadeIn + slideInVertically` 分段出现，减少静态页面感。

### 2. 结果页加入今日票根封面

文件：`app/src/main/java/com/todayplay/app/ui/screens/QuestResultScreen.kt`

- 新增 `TodayQuestTicketCover`。
- 结果页顶部展示 `TODAY WAS CUT`、版本号、城市、关系、时长、策略和第一条个性化理由。
- 票根内置简化路线线条，让用户一眼知道这是“已剪好的今日路线副本”。
- 结果页分段出现数量从 9 段扩展到 11 段，给票根和个性化信息留出独立转场。

### 3. 个性化理由更像 AI 解释

文件：`app/src/main/java/com/todayplay/app/generator/RouteIntentInterpreter.kt`

- 候选卡理由改为“因为你提到...”和“因为预算是...”这类证据句。
- 让路线差异来自用户输入和候选策略，而不是单纯换标题。

### 4. 加载页改为电影剪辑语义

文件：`app/src/main/java/com/todayplay/app/ui/screens/LoadingScreen.kt`

- 加载阶段从普通生成语义改为：
  - 理解需求
  - 筛选同城镜头
  - 剪成路线副本
  - 打印今日票根

### 5. 审计守护更新

文件：

- `playstore/app_regression_audit.py`
- `playstore/adaptive_ui_audit.py`

新增守护：

- V0.9.70 版本元数据。
- 导演剪辑面板存在。
- 候选卡转场存在。
- 结果页票根存在。
- 加载页电影剪辑阶段存在。
- 个性化理由必须包含“因为你”类证据表达。
- 语言选择仍留在设置页，不回到首页。

## QA 与构建结果

已通过：

- `python playstore/app_regression_audit.py`
- `python playstore/adaptive_ui_audit.py`
- `python playstore/generation_flow_audit.py`
- `python playstore/generated_route_display_audit.py`
- `python playstore/localized_source_encoding_audit.py`
- `python playstore/localization_copyfit_audit.py`
- `python playstore/release_config_audit.py`
- `python playstore/visual_qa_audit.py`
- `.\gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleRelease`
- `backend/ai-route-gateway` smoke test

模拟器验证：

- 安装 release APK 后冷启动正常。
- 首页不是崩溃或白屏。
- 点击生成后能出现候选卡。
- 进入结果页后能看到票根、地图预览、当前站操作和 LIVE 模块。
- 小屏、折叠屏、横屏截图已产出。
- 日志未发现 `FATAL EXCEPTION` 或 `AndroidRuntime` 崩溃标记。

## 交付文件

- APK：`dist/TodayPlay-v0.9.70-release.apk`
- 首页截图：`dist/TodayPlay-v0.9.70-qa-home.png`
- 候选区截图：`dist/TodayPlay-v0.9.70-qa-candidates-director.png`
- 加载页截图：`dist/TodayPlay-v0.9.70-qa-loading.png`
- 结果页截图：`dist/TodayPlay-v0.9.70-qa-result-friend.png`
- 当前站操作截图：`dist/TodayPlay-v0.9.70-qa-current-stop.png`
- 小屏截图：`dist/TodayPlay-v0.9.70-qa-small-home.png`
- 折叠屏截图：`dist/TodayPlay-v0.9.70-qa-foldable-home.png`
- 横屏截图：`dist/TodayPlay-v0.9.70-qa-landscape-home.png`
- 日志：`dist/TodayPlay-v0.9.70-qa-logcat.txt`

## APK 校验

- Package：`com.todayplay.app`
- versionName：`0.9.70`
- versionCode：`87`
- APK SHA-256：`8C4CC702885245DA80300BEA157668AB6142AA2B75A1C84CA526DBFB61ED95A6`
- 签名：APK Signature Scheme v2 verified
- 签名证书 SHA-256：`2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695`

## 仍需继续优化

V0.9.70 解决了“候选卡和结果页更像个性化路线”的问题，但还没有达到你要的最终产品状态。下一版必须回到更上层的设计问题：

- 首页文字仍偏多，需要进一步压缩成“输入一句话 + 卡牌瀑布流”的轻入口。
- 转场还只是基础淡入和滑入，需要设计成开屏、生成、卡牌浮现、路线冻结的完整动效系统。
- 瀑布流卡牌应该成为主要浏览资产，而不是生成之后才出现的辅助内容。
- 美术风格需要从代码局部效果升级为完整设计稿和交互规范。
- AI 网关已具备边界和 smoke test，但真实外部测试还需要持续验证不同输入是否稳定产出不同路线。
