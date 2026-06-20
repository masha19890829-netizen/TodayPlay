# TodayPlay V0.9.71 Android Handoff

日期：2026-06-20  
阶段：设计过审后的开发交接，不代表已经开始编码  
目标：把“少字、瀑布流、票根纸感、简洁转场”拆成可实现任务

## 设计资产

整板：

- `todayplay-v0.9.71-design-board.png`
- `todayplay-v0.9.71-responsive-states.png`
- `screens/todayplay-v0.9.71-screen-handoff.png`

单屏：

- `screens/todayplay-v0.9.71-home-default.png`
- `screens/todayplay-v0.9.71-home-filtered.png`
- `screens/todayplay-v0.9.71-card-detail.png`
- `screens/todayplay-v0.9.71-input-focus.png`
- `screens/todayplay-v0.9.71-generation-keyframe.png`
- `screens/todayplay-v0.9.71-result-first-screen.png`

文档：

- `MOTION_INTERACTION_SPEC_V0_9_71_2026-06-20.md`
- `DESIGN_REVIEW_V0_9_71_2026-06-20.md`
- `DESIGN_INTERACTION_BLUEPRINT_V0_9_71_2026-06-20.md`

## 开发拆解

### 1. 首页默认态

参考：`screens/todayplay-v0.9.71-home-default.png`

必须实现：

- 顶部只保留 TodayPlay、当前城市、设置。
- 场景 chips 为推荐、约会、朋友、独处、雨天、低预算、少走路。
- 首屏至少露出 2 张路线卡。
- `自己说一句` 是浮动入口，不挤掉路线卡。
- 非卡片说明文不超过 40 个中文字符。

### 2. 首页筛选态

参考：`screens/todayplay-v0.9.71-home-filtered.png`

必须实现：

- chips 选中后瀑布流重排。
- 筛选态可以出现一张横向重点卡，但不能把所有卡变成同高列表。
- 卡片保留主图、短标题、城市/时间/预算、移动强度、3 站摘要。

### 3. 卡片详情态

参考：`screens/todayplay-v0.9.71-card-detail.png`

必须实现：

- 卡片点开后像票根详情。
- 展示 3 站预览、一句适合原因、来源状态。
- 主按钮为开始，次按钮为保存、邀约。
- 不展示长解释，不展示未核验评分和热度。

### 4. 输入聚焦态

参考：`screens/todayplay-v0.9.71-input-focus.png`

必须实现：

- 背景卡片变淡，输入纸片上浮。
- 输入框不是首屏主视觉，只是自由生成入口。
- chips 聚拢到输入区下方。
- 发送按钮可见，不被安全区或键盘遮挡。

### 5. 生成转场态

参考：`screens/todayplay-v0.9.71-generation-keyframe.png`

必须实现：

- 不使用转圈 loading。
- 采用纸张显影、路线点亮、票根打印语义。
- 阶段文案为理解、筛选、剪成、打印。
- 失败时回到原卡片详情，不清空首页和用户选择。

### 6. 结果页首屏

参考：`screens/todayplay-v0.9.71-result-first-screen.png`

必须实现：

- 继承用户点选的标题、城市和 3 站路线。
- 首屏显示地图草图、当前站、导航按钮。
- `换一幕`、更安静、更便宜等修改入口可见。
- 结果页不重新随机成另一条路线。

## 组件建议

- `WaterfallRouteCard`
- `RouteCardVisual`
- `RouteTicketDetail`
- `SelfPromptSheet`
- `PaperRevealTransition`
- `GeneratedRouteFirstScreen`

## 审计规则建议

V0.9.71 开发完成后应新增守护：

- 首页非卡片说明文数量。
- 首页瀑布流卡片字段完整性。
- `自己说一句` 不作为首屏唯一主路径。
- 生成页禁止转圈 loading。
- 结果页继承候选卡标题、城市、站点。
- 小屏和折叠屏单字换行截图验收。

## 不进入本轮

- Google 登录。
- 支付。
- 上架。
- 真实商家评分和热度。
- 真实营业状态。
- 陌生人社交。
