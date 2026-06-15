# 今天怎么玩 V0.6.0 数据闭环版更新报告

生成日期：2026-06-08  
APK：`D:\AppStore\nemu\real\app\build\outputs\apk\debug\app-debug.apk`

## 1. 修改摘要

本轮实现 V0.6 数据闭环版，不改大 UI、不接 AI、不接支付、不引入后端、不新增敏感权限。

核心变化：

- Quest 生成后会以 `QuestRecord` 写入本地历史。
- 任务完成、跳过、反馈会写入 `QuestProgress` 并持久化。
- App 重启后历史副本、任务状态、隐藏任务状态、反馈仍然存在。
- 通关卡读取真实完成数量和隐藏任务状态，不再写死“待完成”。
- 历史页读取持久化记录，点击历史可以回看真实状态。
- ProductEventLogger 收紧为白名单字段，不记录用户备注、任务全文、对话全文。
- AndroidManifest `allowBackup` 改为 `false`。

## 2. 文件清单

新增：

- `app/src/main/java/com/todayplay/app/data/QuestHistoryStore.kt`
- `app/src/main/java/com/todayplay/app/data/QuestRepository.kt`
- `app/src/main/java/com/todayplay/app/TodayPlayViewModel.kt`

修改：

- `app/src/main/java/com/todayplay/app/model/QuestModels.kt`
- `app/src/main/java/com/todayplay/app/generator/LocalQuestGenerator.kt`
- `app/src/main/java/com/todayplay/app/MainActivity.kt`
- `app/src/main/java/com/todayplay/app/ui/screens/QuestResultScreen.kt`
- `app/src/main/java/com/todayplay/app/ui/screens/ShareCardScreen.kt`
- `app/src/main/java/com/todayplay/app/ui/screens/HistoryScreen.kt`
- `app/src/main/java/com/todayplay/app/ui/components/TodayPlayComponents.kt`
- `app/src/main/java/com/todayplay/app/data/ProductEventLogger.kt`
- `app/src/main/AndroidManifest.xml`
- `app/build.gradle.kts`

## 3. 数据流说明

1. 首页、灵感页或开局卡选择 `QuestInput`。
2. `TodayPlayViewModel.startGeneration()` 暂存输入，只记录安全埋点字段。
3. Loading 完成后调用 `TodayPlayViewModel.generateFromLatestInput()`。
4. `QuestRepository.generate()` 调用 `LocalQuestGenerator` 生成 `Quest`。
5. `QuestHistoryStore` 把 `QuestRecord(quest, progress)` 写入 SharedPreferences JSON。
6. 结果页读取 `QuestRecord.progress` 展示打卡、跳过、反馈状态。
7. 用户点击打卡/跳过/反馈时，通过 ViewModel 调 Repository 更新本地记录。
8. 通关卡用 `CompletionCardData.from(record)` 生成展示数据。
9. 历史页读取 `viewModel.history`，点击后回看同一条持久化记录。

## 4. 模型变化

新增字段：

- `Quest.questId`
- `Quest.createdAt`
- `QuestTask.taskId`

新增模型：

- `TaskStatus`: `Pending` / `Completed` / `Skipped`
- `FeedbackReason`: `TooAwkward` / `TooExpensive` / `TooFar` / `TooTiring`
- `QuestProgress`: 保存任务状态、反馈原因、更新时间、完成时间
- `QuestRecord`: 将 `Quest` 与 `QuestProgress` 绑定
- `CompletionCardData`: 通关卡展示专用数据

生成器变化：

- 每次生成都会创建唯一 `questId`。
- 每个主线任务写入 `questId-act-1/2/3`。
- 隐藏任务写入 `questId-hidden`。

## 5. 持久化方案

当前使用 Android `SharedPreferences` + `org.json`：

- 存储名：`today_play_history`
- 键：`quest_records`
- 最大保留：50 条
- 存储内容：Quest、主线任务、隐藏任务、对话提示、通关文案、任务状态、反馈原因

本方案适合 V0.6 内测闭环。后续如果历史、图片、用户偏好明显变多，应迁移到 Room 或 DataStore。

## 6. 隐私安全处理

- 未新增相机、相册、定位、网络、存储等敏感权限。
- 未写 API Key。
- 未接真实支付。
- 未引入后端。
- `allowBackup=false`，降低关系记录被系统备份带走的风险。
- `ProductEventLogger` 只允许以下字段进入日志：
  - `source`
  - `relationship`
  - `time`
  - `budget`
  - `questId`
  - `taskId`
  - `status`
  - `reason`
  - `pack`
- 不记录用户 note、任务全文、对话全文、通关标题。

说明：APK 元数据里出现 `com.todayplay.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`，这是 AndroidX 自动生成的动态接收器保护权限，不是用户敏感权限。

## 7. 未完成事项

- 仍未实现高清通关卡图片导出和保存到相册。
- 仍未接真实 AI，`AiQuestGenerator` 继续保持占位。
- 仍未接真实支付，商店页仍是权益预览。
- 任务反馈已经持久化，但还没有反向影响下一次生成。
- 当前持久化未加密；如果后续保存更私密内容，应增加加密或减少存储字段。
- 当前未检测到连接中的真机或模拟器，因此未做真机点击测试。

## 8. 编译测试结果

- `assembleDebug`: 通过。
- `lintDebug`: 通过。
- APK 版本：
  - `versionCode`: 6
  - `versionName`: 0.6.0
- SDK：
  - `minSdk`: 26
  - `targetSdk`: 35
- APK 路径：
  - `D:\AppStore\nemu\real\app\build\outputs\apk\debug\app-debug.apk`
