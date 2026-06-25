# TodayPlay V0.9.74 动效规范

日期：2026-06-24

主题：Silent Route Poster Motion

## 动效原则

- 每个动效都回答一个问题：这张卡如何变成今天的路线。
- 不使用转圈 loading。
- 不做无意义漂浮装饰。
- 每段动效可中断、可降级。
- 系统关闭动画时直接显示最终帧。
- 低性能设备跳过背景图漂移，只保留路线线条 draw。

## 动效清单

| 状态 | 出现时机 | 时长 | 曲线 | 实现建议 | 风险 |
| --- | --- | --- | --- | --- | --- |
| 纸底淡入 | 冷启动 | 0-180ms | LinearOutSlowIn | alpha | 低 |
| 票根和品牌出现 | 冷启动 | 180-620ms | FastOutSlowIn | alpha + scale 0.98 到 1 | 低 |
| 票根接首页首卡 | 开屏结束 | 620-900ms | FastOutSlowIn | shared bounds 语义 | 中 |
| 首页标题进入 | 首页 | 120ms | LinearOutSlowIn | fadeIn | 低 |
| chips 进入 | 首页 | 180ms | FastOutSlowIn | fadeIn + slide 6dp | 低 |
| 首卡进入 | 首页 | 260ms | FastOutSlowIn | fadeIn + slide 12dp | 低 |
| 其余卡错峰 | 首页 | 每张 45ms，总计不超 420ms | LinearOutSlowIn | Lazy item fade | 低 |
| 卡内路线画出 | 卡片出现后 | 220ms | LinearOutSlowIn | Canvas path progress | 低 |
| 卡片按下 | 按下 | 80ms | Linear | scale 0.985 | 低 |
| 卡片松手 | 释放 | 220ms | FastOutSlowIn | scale 回弹 + 路线亮度 +8% | 低 |
| 输入纸片上浮 | 点一句话改 | 260ms | FastOutSlowIn | bottom sheet 上浮 24dp | 低 |
| 候选卡显影 | 输入发送后 | 单张 360ms，间隔 70ms | LinearOutSlowIn | skeleton 到图片 | 中 |
| 听懂今天 | 生成态 1 | 280ms | LinearOutSlowIn | 关键词淡入 | 低 |
| 筛同城点 | 生成态 2 | 420ms | LinearOutSlowIn | 地图点出现 | 低 |
| 成路线 | 生成态 3 | 520ms | FastOutSlowIn | 线条连接 + stamp alpha | 中 |
| 结果地图进入 | 进入结果页 | 520ms | LinearOutSlowIn | 地图卡 fade + route draw | 低 |
| 当前站上浮 | 地图后 | 延迟 120ms，时长 260ms | FastOutSlowIn | slide 12dp | 低 |
| 按钮淡入 | 当前站后 | 延迟 80ms，时长 180ms | LinearOutSlowIn | alpha | 低 |
| 当前点脉冲 | 结果页停留 | 1600ms 循环 | RepeatMode.Reverse | alpha 0.16-0.32 | 低 |

## 开屏

画面：

- Paper White 底。
- 票根轮廓。
- TodayPlay。
- 文案：`点一张，今天就走。`

时长：

- 总长 900ms。
- 超过 900ms 直接进入首页，不等待动画结束。

## 首页

进入顺序：

1. 顶部品牌和城市。
2. 默认 chips。
3. 首张路线海报卡。
4. 第二张卡露头。
5. `一句话改` 入口。

卡片 press：

- scale 0.985。
- 图片路线线条亮度提升。
- 不弹说明，不闪烁。

chip 重排：

- 220ms 内完成。
- 保持卡片 key，避免全列表跳动。

## 一句话输入

触发：

- 点 `一句话改`。

动画：

- 背景瀑布流降到 35% 透明。
- 输入纸片从底部上浮 24dp。
- chip 收为 3 个：少走路、低预算、更安静。

发送：

- 输入纸片收起到顶部小胶囊。
- 3 张候选卡在原瀑布流顶部显影。
- 不直接跳结果页。

## 生成态

三段：

- `听懂今天`
- `筛同城点`
- `成路线`

时间：

- 正常 900-1500ms。
- 最长 2200ms。
- 超时后展示 fallback 候选或结果，不露技术错误。

## 结果页

进入：

- 地图先出现。
- 当前站后出现。
- 按钮最后淡入。

循环：

- 只允许当前点轻脉冲。
- 其他内容不循环动。

## QA 验收

- 关闭系统动画后，页面仍能完整使用。
- 横屏生成态不挤掉返回按钮。
- 小屏首页首卡按钮完整露出。
- 结果页地图、当前站、打开地图、完成按钮同屏可见。
- 转场不阻塞 fallback 超过 2200ms。

