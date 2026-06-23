# TodayPlay V0.9.73 Android Handoff

日期：2026-06-23  
阶段：设计过审后的开发交接草案  
目标：把 Quiet Waterfall 设计拆成可执行 Android 任务。

## 参考资产

- `DESIGN_INTERACTION_BLUEPRINT_V0_9_73_2026-06-23.md`
- `docs/design/v0.9.73/DESIGN_REVIEW_V0_9_73_2026-06-23.md`
- `docs/design/v0.9.73/MOTION_INTERACTION_SPEC_V0_9_73_2026-06-23.md`
- `docs/design/v0.9.73/todayplay-v0.9.73-quiet-waterfall-board.svg`

## 开发切片

### 1. 首页路线卡精简

候选组件：`QuietRouteCard`

实现要求：

- 首屏卡片只展示 6 个信息点。
- 去掉首屏卡片内的来源状态全文。
- 去掉首屏卡片内的保存/邀约等权按钮。
- 站点预览从 3 个减到 2 个。
- 卡片详情页再展示来源、预算、移动强度、保存、邀约。

### 2. 首页瀑布流节奏

候选组件：`QuietWaterfallHome`

实现要求：

- 320dp 宽度单列。
- 360dp 到 600dp 可双列，但单张卡最小宽度不低于 154dp。
- 折叠屏和 tablet 最大内容宽度 980dp。
- 首页非卡片说明文总量不超过 28 个中文字符。
- chips 默认最多 5 个，更多收进 `更多`。

### 3. 自由输入入口

候选组件：`OneSentencePromptSheet`

实现要求：

- 首页只显示一行入口：`自己说一句`。
- 点击后输入纸片上浮。
- 背景卡片变淡但不清空。
- 输入后先出 3 张候选卡，不直接跳结果。
- 候选卡必须带 `routeIntentId`。

### 4. 生成显影

候选组件：`PaperRouteRevealTransition`

实现要求：

- 不使用转圈 loading。
- 三段状态：听懂今天、筛同城站点、剪成路线。
- 网关慢时不超过 2200ms 进入 fallback。
- 失败不露技术错误。

### 5. 结果页首屏重排

候选组件：`LiveRouteFirstScreen`

实现要求：

- 第一屏先展示路线标题、城市时间、地图、当前站、导航按钮。
- 适配理由只显示一句。
- 保存、分享、来源状态、完整任务下滑后展示。
- 结果页必须继承 routeIntentId 对应的标题、城市、站点。
- 普通手机内容最大宽度 560dp，折叠屏/平板结果页内容最大宽度 760dp，不允许卡片横向拉满整个内屏。
- 横屏结果页优先显示地图和当前站，不让 hero 或封面把主动作推到首屏外。

## 审计规则建议

新增 V0.9.73 守护：

- `QuietRouteCard` 存在。
- `OneSentencePromptSheet` 存在。
- `PaperRouteRevealTransition` 存在。
- 首页卡片首屏不再渲染 `contentStatus` + `sourceStatus` 全文。
- 首页卡片不再有 3 个等权文字按钮。
- 站点预览首屏最多 2 个。
- 结果页 `LiveRouteFirstScreen` 在长任务列表之前。
- 结果页存在宽屏 `widthIn(max = ...)` 或等效最大宽度约束。
- V0.9.73 报告产物不得包含 `�`、`ï¿½`、`����`、可见坏 `?` 分隔符。

## 截图验收

开发完成后必须输出：

- `dist/TodayPlay-v0.9.73-qa-home.png`
- `dist/TodayPlay-v0.9.73-qa-prompt.png`
- `dist/TodayPlay-v0.9.73-qa-candidates.png`
- `dist/TodayPlay-v0.9.73-qa-loading.png`
- `dist/TodayPlay-v0.9.73-qa-result.png`
- `dist/TodayPlay-v0.9.73-qa-small-home.png`
- `dist/TodayPlay-v0.9.73-qa-small-result.png`
- `dist/TodayPlay-v0.9.73-qa-foldable-home.png`
- `dist/TodayPlay-v0.9.73-qa-foldable-result.png`
- `dist/TodayPlay-v0.9.73-qa-landscape-home.png`
- `dist/TodayPlay-v0.9.73-qa-landscape-result.png`
- 每张截图必须配套 XML，另附 logcat 崩溃检查。

## 不进入 V0.9.73

- Google 登录真实联调。
- 支付。
- Play Console 上架。
- 未授权真实 POI 热度、评分、UGC。
- 大规模重写路线引擎。
