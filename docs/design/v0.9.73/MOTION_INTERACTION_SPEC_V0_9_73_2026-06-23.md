# TodayPlay V0.9.73 Motion Interaction Spec

日期：2026-06-23  
状态：设计阶段动效规范

## 动效原则

- 让用户知道“我点的这张卡正在变成我的路线”。
- 每个动效都要可中断、可降级、低性能成本。
- 不用转圈 loading，不用无意义漂浮装饰。
- 验收口径：不使用转圈 loading；生成中只能使用路线点亮、纸片显影、票根打印这类语义动效。
- 动效总时长短，单段最好低于 700ms。

## 动效清单

| 状态 | 出现时机 | 时长 | 曲线 | 实现建议 | 性能风险 |
| --- | --- | --- | --- | --- | --- |
| 开屏票根出现 | 冷启动 | 900ms | FastOutSlowIn | alpha + scale 0.98 到 1 | 低 |
| 首页卡片错峰 | 首页进入 | 每张延迟 45ms，总计不超 360ms | LinearOutSlowIn | fadeIn + slideInY 12dp | 低 |
| chip 筛选重排 | 点场景 chip | 220ms | FastOutSlowIn | Crossfade + key 保持 | 低 |
| 卡片点开 | 点路线卡 | 320ms | FastOutSlowIn | shared bounds 语义，卡片扩成票根详情 | 中 |
| 输入纸片上浮 | 点自己说一句 | 260ms | FastOutSlowIn | bottom sheet + 背景淡化 | 低 |
| 候选卡显影 | 输入生成后 | 600-900ms | LinearOutSlowIn | 骨架线条到主图/标题 | 中 |
| 路线剪成票根 | 点候选卡开始 | 900-1500ms | FastOutSlowIn | route line draw + stamp alpha | 中 |
| 结果地图点亮 | 进入结果页 | 650ms | LinearOutSlowIn | Canvas path progress | 低 |
| 当前站脉冲 | 结果页首屏 | 1800ms 循环 | RepeatMode.Reverse | alpha 0.18-0.34 | 低 |

## 开屏

画面：

- 暖白纸底。
- 票根线条。
- TodayPlay。
- 一句短文案：`今天，先选一张路线。`

退出：

- 票根轻缩小并淡出。
- 首页第一张卡从同一方向进入。

## 首页瀑布流

卡片出现顺序：

1. 顶部品牌和城市。
2. 场景 chips。
3. 第一张大卡。
4. 其余卡片错峰。
5. 自由输入入口最后出现。

卡片 hover/press：

- press scale 0.985。
- 主图路线线条亮度 +8%。
- 不弹大型说明。

## 输入态

触发：

- 点 `自己说一句`。

动画：

- 背景瀑布流降到 32% 透明度。
- 输入纸片从底部上浮 24dp。
- 输入框聚焦后 chips 收拢到输入下方。

失败：

- 如果 AI 或网关失败，候选卡仍展示本地同城 fallback。
- 文案只说：`先给你一版稳妥路线。`

## 生成态

三段状态：

1. `听懂今天`：关键词浮现。
2. `筛同城站点`：地图点出现。
3. `剪成路线`：线条连接，票根盖章。

时间：

- 正常 900-1500ms。
- 最长不超过 2200ms。
- 低性能设备直接跳到最后关键帧。

## 结果页

进入：

- 顶部票根先出现。
- 地图线条 650ms 点亮。
- 当前站卡片从下方 12dp 进入。

第一屏动效：

- 只允许当前站有轻脉冲。
- 其他卡片不循环动。
- 下方内容用普通 fadeIn，不抢主线。

## Compose 命名建议

- `QuietWaterfallHome`
- `QuietRouteCard`
- `RouteCardDetailSheet`
- `OneSentencePromptSheet`
- `CandidateRevealStrip`
- `PaperRouteRevealTransition`
- `LiveRouteFirstScreen`
- `CurrentStopPulse`

## QA 必查

- 关闭动画或系统动画缩放时，页面仍能完整使用。
- 320dp 宽度下输入纸片不超过安全区。
- 折叠屏内屏卡片不拉宽成 banner。
- 生成转场不阻塞快速 fallback。
- 横屏 loading 不得把阶段文案和返回按钮挤出首屏。
- 结果页当前站 CTA 必须在普通屏、小屏、折叠屏、横屏首屏或首屏下缘可见。
