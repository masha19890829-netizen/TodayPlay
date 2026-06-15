# TodayPlay V0.9.54 Foldable QA Report - 2026-06-12

## User Issue

用户反馈折叠屏手机上很多标题和文案出现超出、重叠或乱码感。本轮 QA 以折叠屏外屏窄宽度为优先目标，同时覆盖普通竖屏和折叠内屏大画布。

## QA Matrix

| Case | Simulated screen | Density | Result |
| --- | --- | --- | --- |
| Phone portrait | default emulator, 1080x2400 class | default | pass |
| Fold inner | 2208x1840 | 420 | pass |
| Fold outer | 884x2400 | 420 | pass |
| Fold outer result page | 884x2400 | 420 | pass |

## Reproduced Before Fix

- V0.9.53 窄外屏截图中，首页顶部内容距离系统状态栏不足，状态栏时间与 APP 标题区域发生视觉重叠。
- 首页首屏和底部导航存在特殊符号图标，存在不同 Android/OEM 字体显示异常风险。
- 背景漂浮装饰靠近顶部按钮区，在窄屏上容易被误认为文案挤压。

## Fixes Verified

- 首页、结果页和通用顶部栏均避开状态栏。
- 底部导航避开系统导航栏。
- 城市按钮、设置按钮、路线站点胶囊在窄屏上会省略或收缩，不再撑出容器。
- 全应用高风险装饰符号已清理，审计规则会防止回归。
- 窄外屏进入第一条路线结果页后，路线标题、地图预览、站点列表和当前导航按钮均可见。

## Text Dump Scan

Captured XML files:

- `dist/TodayPlay-v0.9.54-qa-phone.xml`
- `dist/TodayPlay-v0.9.54-qa-fold-inner.xml`
- `dist/TodayPlay-v0.9.54-qa-fold-outer.xml`
- `dist/TodayPlay-v0.9.54-qa-fold-outer-result.xml`

Scan result:

- Risky glyphs: `0`
- Negative bounds: `0`
- Replacement/tofu markers: `0`

## Screenshots

- Phone: `dist/TodayPlay-v0.9.54-qa-phone.png`
- Fold inner: `dist/TodayPlay-v0.9.54-qa-fold-inner.png`
- Fold outer: `dist/TodayPlay-v0.9.54-qa-fold-outer.png`
- Fold outer result: `dist/TodayPlay-v0.9.54-qa-fold-outer-result.png`

## Build And Package

- APK: `dist/TodayPlay-v0.9.54-release.apk`
- Package: `com.todayplay.app`
- Version: `0.9.54`
- Version code: `72`
- APK SHA-256: `B5492A61EA562BCF63E64409318BDEA1A1FAADA03B06B1222D4824764ACC1DD5`
- Signature: APK Signature Scheme v2, verified
- Signer certificate SHA-256: `2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695`

## Residual Risk

模拟器可以覆盖屏幕宽度、密度、冷启动和基础布局，但无法完全模拟 OPPO 折叠屏的真实系统字体、外屏圆角、铰链策略、分屏策略和用户字体缩放设置。建议用用户的 OPPO 真机再做一次：

- 默认字体大小
- 大号字体
- 外屏打开
- 内屏打开
- 从外屏切到内屏
- 从内屏切回外屏
