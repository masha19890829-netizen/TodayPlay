# TodayPlay V0.9.69 Update Report

日期：2026-06-18  
版本：0.9.69 / versionCode 86  
交付 APK：`dist/TodayPlay-v0.9.69-release.apk`

## 本轮目标

把 TodayPlay 从“固定路线演示”继续推向“有个性化判断的 AI 玩法助手”，同时重点修正用户反馈的细节问题：窄屏、折叠屏和开屏文案中不能出现一个字单独换行、标题挤压、按钮被压成省略号。

## 团队分工与产出

- 项目负责人：把 V0.9.69 定为“个性化证据 + 排版适配修复”版本，收束范围，确保能输出可安装 APK。
- 产品经理：要求候选路线必须说明“为什么适合你”，而不是只换标题；结果页必须继承用户选中的候选卡策略。
- Android 开发工程师：实现候选卡策略、证据信号、取舍说明、来源说明、最近需求复用、结果页个性化理由展示。
- UI/动效设计师：压缩首页文字密度，窄屏隐藏装饰图，控制输入框和顶部操作区换行。
- QA 负责人：增加折叠屏/窄屏排版守护，重跑模拟器冷启动、首页、候选卡、结果页、签名和日志检查。

## 主要变更

### 1. 个性化路线证据

- `ItineraryPlan` 增加：
  - `personalizationStrategy`
  - `personalizationSignals`
  - `personalizationReasons`
  - `personalizationTradeoff`
  - `personalizationSourceNote`
- 候选卡现在展示：
  - 策略标签，例如“最贴合 / 更安静 / 更省钱 / 少走路”
  - 城市、关系、时间、预算、移动强度等证据信号
  - 取舍说明和来源说明
- 用户点击某一张候选卡后，结果页会继承该卡策略，不再重新变成另一条固定路线。

### 2. 首页体验细节

- 首页增加最近需求入口，允许复用本地历史需求。
- 当已经生成候选路线时，最近需求条不会挡住当前候选卡。
- 窄屏模式隐藏装饰插画，避免首屏被视觉素材挤压。
- 窄屏输入框示例改为“今晚两人少走路”，避免长句把单字挤到下一行。

### 3. 排版与适配修复

- 开屏副标题改为三条固定短句，避免“影。”这种单字/尾字独占一行。
- 开屏文案容器限制为 `300dp`，每行 `softWrap = false`，由我们主动控制换行。
- 窄屏顶部操作区改为两层：
  - 第一层：TodayPlay + 设置
  - 第二层：历史 + 收藏
- 修复后折叠屏首页不再出现“设置”被挤成 `...` 的状态。

### 4. 审计守护

- `playstore/app_regression_audit.py` 增加 V0.9.69 个性化、候选卡证据、结果页继承、开屏孤字防回归检查。
- `playstore/adaptive_ui_audit.py` 增加 chat-first 个性化文案和窄屏顶部操作检查。

## QA 结果

- `app_regression_audit.py`：通过
- `adaptive_ui_audit.py`：通过
- `testDebugUnitTest`：通过，无测试源时正常跳过
- `lintDebug`：通过
- `assembleRelease`：通过
- 模拟器安装：通过
- 冷启动：通过
- 首页截图：通过
- 候选卡截图：通过
- 结果页截图：通过
- 折叠屏/窄屏首页截图：通过
- 崩溃日志过滤：未发现 `FATAL EXCEPTION` 或 `AndroidRuntime`

## 交付文件

- APK：`dist/TodayPlay-v0.9.69-release.apk`
- 首页截图：`dist/TodayPlay-v0.9.69-qa-home.png`
- 候选卡截图：`dist/TodayPlay-v0.9.69-qa-candidates.png`
- 结果页截图：`dist/TodayPlay-v0.9.69-qa-result.png`
- 折叠屏首页截图：`dist/TodayPlay-v0.9.69-qa-foldable-home.png`

## APK 校验

- Package：`com.todayplay.app`
- versionName：`0.9.69`
- versionCode：`86`
- APK SHA-256：`746098520B8973750E91BACB4FB259F88D2E91F2062CEDA978444053BC524826`
- 签名方案：APK Signature Scheme v2 verified
- 签名证书 SHA-256：`2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695`

## 仍需继续优化

- 真实 Kimi/DeepSeek 网关仍需进入端到端验收；当前 APK 已有本地差异化候选路线与 AI 网关边界，但外部测试版仍应继续验证真实大模型输出质量。
- 首页视觉已经比旧版轻，但还没有达到“看截图就想下载”的最终商店级表现，下一轮需要继续补充打开动画、生成动画和时光电影路线视觉。
- 折叠屏顶部已修正基础排版，但后续可以进一步设计成更优雅的紧凑导航，而不是仅做安全分行。
