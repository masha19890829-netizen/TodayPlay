# TodayPlay V0.9.74 Android 交接稿

日期：2026-06-24

阶段：设计过审前的开发拆解

## 本轮不开发

V0.9.74 当前阶段只交付设计和交互逻辑。Android 开发必须等设计图和 QA 红线过审后进入。

## 目标组件

### 1. `SilentRoutePosterHome`

职责：

- 承载首页默认瀑布流。
- 默认 chip 不超过 5 个。
- 小屏首屏完整露出首卡按钮。
- 横屏/折叠屏使用 rail 或最大卡宽，不把卡片横向拉满。

约束：

- 首页非卡片说明文不超过 28 个中文字符。
- 320dp 宽度单列。
- 360dp 到 600dp 可双列，但单张卡最小宽度不低于 160dp。
- 折叠屏和 tablet 最大内容宽度 980dp。

### 2. `SilentRoutePosterCard`

职责：

- 代替当前信息型 `QuietRouteCard`。
- 卡片正面只显示 5 个信息点。
- 整张卡可点击。

正面字段：

- image
- title
- cityDuration
- stopPreview1
- stopPreview2
- fitTag
- compactStartButton

不得出现：

- contentStatus 全文
- sourceStatus 全文
- 3 个等权操作按钮
- 完整理由句
- 第三站

### 3. `OneLineRewriteSheet`

职责：

- 点 `一句话改` 后上浮。
- 背景瀑布流降透明，不清空。
- 输入最多 30 个中文字符。
- 快捷 chip 最多 3 个。

发送后：

- 生成 3 张 `AiCandidatePosterCard`。
- 不直接进结果页。
- 候选卡必须携带 `routeIntentId`。

### 4. `AiCandidatePosterCard`

职责：

- 显示 AI 生成的三种不同路线方向。
- 三张卡必须在标题、第一站、短标签上不同。
- 用户点中一张后，结果页继承该卡的 title、city、stopId 顺序、fitTag 和 routeIntentId。

### 5. `PosterToRouteTransition`

职责：

- 把选中卡扩展成路线票根。
- 三段动效：`听懂今天`、`筛同城点`、`成路线`。
- 正常 900-1500ms，最长 2200ms fallback。

### 6. `LiveRouteMapFirstScreen`

职责：

- 结果页首屏地图优先。
- 当前站和主动作同屏。

普通屏结构：

- 顶部返回、标题、保存。
- 地图高度最小 220dp。
- 当前站卡。
- 主动作 `打开地图`。
- 次动作最多 2 个。

小屏结构：

- 地图高度最小 168dp。
- 不显示长时间句。
- 不显示英文残片省略。

横屏/折叠屏结构：

- 左侧地图 58%。
- 右侧当前站 42%。
- 不把当前站按钮压到首屏外。

## 数据与状态

新增或确认字段：

- `routeIntentId`
- `posterTitle`
- `fitTag`
- `candidateSource = ai | localFallback | curatedSample`
- `selectedStopIds`
- `posterVisualRes`

规则：

- 选中候选卡后，结果页必须继承该卡内容。
- AI 失败时使用本地 fallback，但前台不显示技术错误。
- 所有样例 POI 继续标记为本地样例或待核验样例。

## 开发任务切片

1. 首页 chip 和卡片字段减法。
2. `SilentRoutePosterCard` 组件。
3. `OneLineRewriteSheet` 和 3 张候选卡。
4. `PosterToRouteTransition`。
5. `LiveRouteMapFirstScreen` 响应式布局。
6. 截图 QA 脚本更新。
7. V0.9.74 APK 构建和模拟器验收。

## 审计守护建议

新增 V0.9.74 守护：

- `SilentRoutePosterHome` 存在。
- `SilentRoutePosterCard` 存在。
- `OneLineRewriteSheet` 存在。
- `AiCandidatePosterCard` 存在。
- `PosterToRouteTransition` 存在。
- 首页默认 chip 不超过 5 个。
- 首页卡片正面不渲染完整 `contentStatus` 或 `sourceStatus`。
- 首卡按钮在小屏和横屏截图完整。
- `LiveRouteMapFirstScreen` 在长详情列表之前。
- 地图内长句不进入紧凑模式。
- `visual_qa_audit.py --version v0.9.74` 支持新截图命名。

## 不进入 V0.9.74

- Google 登录真实后端验签。
- 支付。
- Play Console 上架。
- 未授权真实 POI 热度、评分、UGC。
- 真实地图 SDK。

