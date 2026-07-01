# TodayPlay - 一句话安排今天

> 用一句话安排今天的 AI 城市玩伴。
>
> 输入“今晚两个人，电影感，少走路”，TodayPlay 会理解城市、关系、时间、预算和体力，生成几张可选路线票；选中一张后，进入带地图、当前站和行动按钮的今日路线副本。

TodayPlay 不是传统旅游攻略列表，也不是社交信息流。它想解决的是一个很日常的问题：

**今天到底去哪、怎么走、和谁一起玩，才不累、也不无聊？**

## 核心体验

1. 说一句今天想怎么玩。
2. AI 或本地路线引擎理解你的需求。
3. 生成多张不同方向的路线票。
4. 选择一张，进入地图优先的路线副本。
5. 查看当前站、打开地图、完成打卡，也可以再改一句。

## 现在能做什么

- **一句话开始**：首页不再先让用户填长表单，而是从一句自然语言需求开始。
- **多张路线票**：根据输入生成“最贴合、时光电影、少走路、更安静、更热闹、低预算、室内优先”等不同玩法方向。
- **同城路线校验**：上海、东京、深圳等城市内容不会混在一条路线里。
- **地图优先结果页**：路线结果先展示地图预览、当前站、打开地图和完成当前站。
- **时光电影路线**：把城市地点组织成 Act 01 / Act 02 / Act 03 的电影感路线；当前内容以本地样例或待核验内容为主，不伪装官方取景地或授权素材。
- **可选 AI 网关**：可以接入 Kimi 或其他大模型代理；没有网关时，也会用本地评分引擎生成差异化路线。
- **移动端适配**：持续针对普通手机、小屏、横屏、折叠屏做截图和文字排版审查。

## 适合谁

- 下班后不想再做选择的人。
- 情侣、朋友、独处用户，希望快速得到一条今天能走的路线。
- 想要电影感、票根感、轻仪式感的城市探索体验。
- 想测试 AI 个性化路线生成体验的早期用户或合作者。

## 当前状态

项目当前处于 **V0.9.79 / versionCode 95 外测候选迭代阶段**，还不是正式商店上架版本。

近期重点：

- 减少文字墙，让首页更像“一句话安排今天”。
- 让不同输入真正得到不同路线，而不是固定内容换标题。
- 强化今日私人电影票视觉、地图首屏和打开动画。
- 修复小屏、横屏、折叠屏上的标题溢出、单字孤行和排版问题。
- 保持 AI 失败时的本地差异化路线能力。

暂未进入本轮：

- Google 登录。
- 支付和订阅。
- Play Console 正式上架。
- 真实 UGC、评分、热度或官方授权内容。

## 外测说明

当前 APK 不随源码仓库直接提交。外测包会通过测试负责人或后续 GitHub Release 分发。

外测时请重点看：

- 冷启动是否闪退。
- 首页是否能 5 秒内看懂“一句话安排今天”。
- 不同输入是否生成不同路线票。
- 选中路线票后，结果页是否继承所选路线的标题、地点和理由。
- 小屏、横屏、折叠屏是否有文字溢出、单字孤行或乱码。
- 地图预览、当前站、打开地图、完成当前站是否在首屏清楚可见。

反馈建议包含：

```text
设备型号：
系统版本：
App 版本：
输入内容：
实际结果：
期望结果：
截图或录屏：
是否闪退 / 卡死 / 乱码：
```

## 技术栈

- Kotlin
- Jetpack Compose
- Material 3
- Gradle / Android Gradle Plugin
- 本地路线生成器 + 可选 AI route gateway

## 本地运行

1. 用 Android Studio 打开项目根目录。
2. 等待 Gradle 同步完成。
3. 连接 Android 模拟器或真机。
4. 运行 `app` 模块。

命令行构建 debug APK：

```powershell
.\gradlew.bat :app:assembleDebug
```

生成路径：

```text
app/build/outputs/apk/debug/app-debug.apk
```

Release 打包需要本地签名配置，可参考：

```text
keystore.properties.template
release_config.template.properties
```

## AI 接入原则

客户端不保存 Kimi 或其他大模型 API Key。

推荐结构：

```text
Android App -> AI route gateway -> Kimi / other model provider
```

客户端只发送结构化路线需求和同城候选 POI；后端负责调用模型、校验输出、过滤跨城或不安全路线。

更多配置说明见 [KIMI_API_KEY_SETUP_2026-06-15.md](KIMI_API_KEY_SETUP_2026-06-15.md)。

不要提交以下文件或信息：

- Kimi、OpenAI 或其他模型 API Key。
- `keystore.properties`。
- `release_config.properties`。
- `.dev.vars`。
- Android release signing key。

## 项目结构

```text
app/src/main/java/com/todayplay/app/
  data/          本地内容、城市 POI 和路线样例
  generator/     路线意图解析、本地生成器、AI 网关生成器
  model/         Quest、RouteIntent、ItineraryPlan 等数据模型
  ui/components/ 通用 Compose 组件
  ui/screens/    首页、加载页、结果页、设置页等
  ui/theme/      票根视觉、色彩和字体主题

backend/
  ai-route-gateway/       AI 路线代理示例
  billing-verify-worker/  支付校验后端草案
```

## 重要说明

- 当前 POI、电影感地点和路线内容是本地样例或待核验内容。
- 不展示虚构热度、评分、UGC 或官方授权关系。
- 不把用户 API Key 写进 Android 客户端。
- 正式上架前还需要隐私政策、Data Safety、真实截图、账号登录和支付链路复核。
