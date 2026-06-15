# 今天怎么玩

「今天怎么玩」是一款情侣/朋友/家庭现实副本生成 App。输入今天的关系、心情、城市、预算和时间后，App 会在本地生成一场可以真实执行的“今日副本”，包括剧情设定、三段主线任务、隐藏任务、对话引导、拍照任务、结尾仪式和可截图分享的通关卡。

当前版本采用原生 Android 技术栈：Kotlin、Jetpack Compose、Material 3、Gradle。MVP 不依赖后端，也没有接入真实 AI API。

## 当前版本已实现

- Splash、首页、开局卡、加载页、今日副本结果页、通关卡页。
- 本地规则生成 `LocalQuestGenerator`，支持按关系、心情、预算、氛围和城市组合生成不同脚本。
- `QuestGenerator` 接口和 `AiQuestGenerator` 预留实现，方便后续接入 OpenAI / Gemini / 自建 API。
- 电影感、票根感、低饱和玫瑰色的 UI 风格。
- 重新生成同一输入下的新副本。
- 通关卡页可直接截图分享，保存图片功能 MVP 先用 Toast 提示。
- V0.2 新增首页直接开局预设、历史副本入口、结果页收藏动作。
- V0.3 新增首页电影感幻灯片、更多温馨美术资产、通关卡动态盖章和会员商店权益预览。
- V0.4 重构首页为单独入口页，拆出快速开局页，并优化小屏、折叠屏和宽屏布局。

## 如何运行

1. 用 Android Studio 打开本目录：`D:\AppStore\nemu\real`
2. 等待 Gradle 同步完成。
3. 连接模拟器或真机。
4. 点击 Run 运行 `app`。

本项目最低支持 `minSdk 26`，目标版本为 `targetSdk 35`。

## 如何打包 APK

在项目根目录执行：

```bash
./gradlew assembleDebug
```

Windows PowerShell 也可以执行：

```powershell
.\gradlew.bat assembleDebug
```

生成的 debug APK 路径：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 后续如何接入 AI API

代码已预留生成接口：

- `generator/QuestGenerator.kt`：统一生成接口。
- `generator/LocalQuestGenerator.kt`：当前本地规则生成。
- `generator/AiQuestGenerator.kt`：未来 AI 接入点。
- `model/QuestModels.kt`：输入和输出数据结构。

接入真实 AI 时建议：

1. 在 `AiQuestGenerator` 中调用后端代理服务，不要把 API Key 写入客户端。
2. 后端负责请求 OpenAI / Gemini / 自建模型。
3. 客户端只传 `QuestInput`，接收结构化 `Quest`。
4. 对 AI 返回内容做安全过滤，避免危险任务、强迫亲密、隐私窥探和高风险地点。

## 产品下一步路线图

### V0.1 当前版本

- 本地生成今日副本
- 情侣主题 UI
- 通关卡截图分享
- 重新生成

### V0.2

- 接入真实 AI 生成
- 增加城市地点推荐
- 增加情侣/朋友/家庭不同模板库
- 增加保存历史副本

### V0.3

- 增加付费高级副本（已加入权益预览）
- 增加纪念日特别局（已加入权益预览）
- 增加节日活动包
- 增加小红书风格分享卡（已加入权益预览）

### V0.4

- 增加双人共同编辑
- 增加完成打卡
- 增加关系值成长
- 增加连续副本

### V1.0

- 上线真实用户测试
- 支持 Android 正式发布
- 支持后端配置模板
- 支持 AI 个性化生成

## 项目结构

```text
app/src/main/java/com/todayplay/app/
  data/          文案和任务模板池
  generator/     本地生成器与未来 AI 生成器接口
  model/         QuestInput、Quest、QuestTask 数据模型
  ui/components/ 可复用 Compose 组件
  ui/screens/    页面
  ui/theme/      色彩、字体和主题
```
