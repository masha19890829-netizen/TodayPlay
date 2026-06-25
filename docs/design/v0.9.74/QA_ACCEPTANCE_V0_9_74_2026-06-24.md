# TodayPlay V0.9.74 QA 验收红线

日期：2026-06-24

## 一句话验收

V0.9.74 不是“能滚动就行”，而是小屏和横屏第一眼也要完整看见可点的路线卡，结果页第一眼就知道现在去哪一站。

## P0 红线

- 小屏首页首屏必须完整露出至少 1 张路线卡的标题、城市/时长、2 个站点、短标签、完整开始按钮。
- 横屏首页首屏必须完整露出至少 1 张路线卡的主动作，按钮不得被导航栏、安全区或截图底边切掉。
- 结果页普通屏、小屏、折叠屏、横屏都必须同时看到地图/路线线、当前站、打开地图、完成当前站。
- 地图内只放短标签，不放长句。
- 不允许出现单字孤行、乱码、半截字、中英残片省略。
- 不允许转圈 loading。

## 首页验收

### 文字密度

- 首屏非卡片说明文不超过 28 个中文字符。
- 默认 chip 不超过 5 个。
- 小屏 chip 最多 2 行。
- 卡片标题建议 10 字内，最多 12 字。
- fit tag 不超过 4 个中文字符。

### 卡片点击欲

- 主图清晰。
- 路线线条可见。
- 整卡可点。
- 主动作触控区不小于 48dp。
- 首屏卡片只保留一个主动作。

### 小屏

- 320x640 首屏露出 1 张完整卡和第二张卡露头。
- 第一张卡高度控制在 360-390dp。
- `一句话改` 不压住首卡按钮。

### 横屏/折叠屏

- 卡片最大宽 360dp。
- 不把卡片拉成 banner。
- 输入入口固定为窄票根条或顶部轻入口。

## 结果页验收

- 普通屏地图最小 220dp。
- 小屏地图最小 168dp。
- 横屏左地图右当前站。
- 当前站卡只保留站名、区域时间、打开地图、完成。
- 轻量修改可见但不得挤掉主行动。
- 长说明全部下沉。

## 动效验收

- 开屏总长 900ms。
- 首页卡片错峰总长不超 420ms。
- 生成转场正常 900-1500ms。
- 最长 2200ms fallback。
- 系统动画关闭时页面仍可用。
- 低性能设备不掉帧到不可交互。

## 截图清单

必须输出：

- `dist/TodayPlay-v0.9.74-qa-home.png`
- `dist/TodayPlay-v0.9.74-qa-prompt.png`
- `dist/TodayPlay-v0.9.74-qa-candidates.png`
- `dist/TodayPlay-v0.9.74-qa-loading.png`
- `dist/TodayPlay-v0.9.74-qa-result.png`
- `dist/TodayPlay-v0.9.74-qa-current-stop.png`
- `dist/TodayPlay-v0.9.74-qa-small-home.png`
- `dist/TodayPlay-v0.9.74-qa-small-result.png`
- `dist/TodayPlay-v0.9.74-qa-small-long-copy-home.png`
- `dist/TodayPlay-v0.9.74-qa-small-long-copy-result.png`
- `dist/TodayPlay-v0.9.74-qa-landscape-home.png`
- `dist/TodayPlay-v0.9.74-qa-landscape-loading.png`
- `dist/TodayPlay-v0.9.74-qa-landscape-result.png`
- `dist/TodayPlay-v0.9.74-qa-foldable-home.png`
- `dist/TodayPlay-v0.9.74-qa-foldable-result.png`

每张核心截图必须配套 XML，另附：

- `dist/TodayPlay-v0.9.74-qa-logcat.txt`
- `dist/TodayPlay-v0.9.74-visual-qa-report.md`

## 脚本门槛

- `adaptive_ui_audit.py` 通过。
- `design_handoff_audit.py` 通过。
- 编码审计通过。
- `visual_qa_audit.py --version v0.9.74` 支持新截图命名并通过。
- PNG、XML、logcat 必须同一验收窗口生成。

