# TodayPlay V0.9.73 更新报告

日期：2026-06-24

## 版本结论

V0.9.73 已完成“安静瀑布流 + 结果页当前行动优先”的外部测试包收口。

- APK：`dist/TodayPlay-v0.9.73-release.apk`
- AAB：`dist/TodayPlay-v0.9.73-release.aab`
- versionName：`0.9.73`
- versionCode：`90`
- package：`com.todayplay.app`

## 团队分工与结论

- 项目负责人：将本轮目标锁定为减少首页文字负担、解决小屏/横屏结果页首屏行动露出问题。
- 产品与体验负责人：确认首页继续保留路线卡，但每张卡只展示城市、时长、2 个站点和一句适配理由；“自己说一句”后置成轻输入。
- UI/动效负责人：改为更安静的双列/单列瀑布流卡片，弱化长说明和多按钮，结果页先给路线现场、地图线和当前站。
- QA 设备验证负责人：发现普通首页截图曾被系统弹窗遮挡、横屏结果页按钮露出不足、小屏地图长文案被截断；均已修正并重新截图。
- 发布验收负责人：确认 APK/AAB 存在、版本元数据正确、签名通过、日志未发现 TodayPlay 崩溃关键词。

## 主要改动

1. 首页路线卡从信息堆叠改为 `QuietRouteCard`：
   - 每张卡只保留图片、城市/时间、2 个站点、一句理由和单一“开始”按钮。
   - 小屏阈值从 `318.dp` 提升到 `360.dp`，避免窄屏误入双列导致卡片挤压。
   - “自己说一句”改为 `OneSentencePromptSheet`，不再抢占瀑布流首屏。

2. 结果页新增 `LiveRouteFirstScreen`：
   - 结果页首屏先显示路线现场、地图线、当前站、打开地图、完成当前站。
   - 票根、时间线和长详情后置，避免一打开结果页仍像说明文。
   - 宽屏/折叠屏使用最大宽度约束，横屏使用紧凑高度模式。

3. 适配细节修正：
   - 横屏结果页压缩说明、地图和卡片间距，使主操作完整露出。
   - 小屏紧凑地图隐藏长时间文案，避免半截字和乱码感。
   - 审计脚本加入 V0.9.73 首页/结果页守护，防止回退到文字墙或旧结果页结构。

## 验收结果

- `app_regression_audit.py`：通过
- `adaptive_ui_audit.py`：通过
- `design_handoff_audit.py`：通过
- `compileDebugKotlin`：通过
- `lintDebug`：通过
- `assembleRelease`：通过
- `bundleRelease`：通过
- `testDebugUnitTest`：通过，当前无单元测试源
- 模拟器安装：通过
- 冷启动首页：通过
- 小屏首页/结果页：通过
- 横屏结果页：通过
- 最新日志：未发现 `FATAL EXCEPTION`、`AndroidRuntime`、`Process: com.todayplay.app`、TodayPlay Crash/Exception 或 TodayPlay ANR 关键词

## 发布指纹

APK SHA-256：

`FB27C62DDDD00D5255E116EE3CEC7ABCA71935F3F9335625654765B5CE3FAD2D`

AAB SHA-256：

`042F0A8A7081D93A1D19B977BFEBD1F92262037D562A985E914913CDF0D1B7BB`

签名证书 SHA-256：

`2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695`

## QA 截图

- `dist/TodayPlay-v0.9.73-qa-home.png`
- `dist/TodayPlay-v0.9.73-qa-small-home.png`
- `dist/TodayPlay-v0.9.73-qa-small-result.png`
- `dist/TodayPlay-v0.9.73-qa-landscape-result.png`
- `dist/TodayPlay-v0.9.73-qa-foldable-home.png`
- `dist/TodayPlay-v0.9.73-qa-foldable-result.png`
- `dist/TodayPlay-v0.9.73-qa-loading.png`
- `dist/TodayPlay-v0.9.73-qa-result.png`
- `dist/TodayPlay-v0.9.73-qa-logcat.txt`

## 仍未进入本轮

- 真实 Google 登录后端验签
- 真实支付与服务端验单
- 真实 Kimi/DeepSeek 线上网关验收
- 真实地图 SDK 和官方 POI 数据
- Play Console 上架材料与 Data Safety 表单

## 下一轮建议

V0.9.74 应继续做“AI 感与内容灵魂”：

- 首页卡片保留轻量，但让“自己说一句”生成的候选路线更明显不同。
- 加强时光电影路线的地图与电影场景表达，但继续标注“待核验样例”。
- 把截图 QA 固定成自动任务：普通手机、小屏、折叠屏、横屏四套必须每版更新。
