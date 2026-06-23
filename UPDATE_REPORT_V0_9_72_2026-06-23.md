# TodayPlay V0.9.72 更新报告

日期：2026-06-23  
版本：`0.9.72 / versionCode 89`  
主线目标：把首页从“文字/输入框压迫感”继续收敛为外测可用的路线卡片首屏，并把 AI 网关等待时间压到可接受范围。

## 团队结论

| 工种 | 本轮结论 |
| --- | --- |
| 首席产品经理 | 外测用户先看到可点路线卡，再允许“自己说一句”；避免首屏重新变成大段说明或复杂表单。 |
| AI 路线逻辑负责人 | Kimi/网关不可让用户等 20 秒以上；客户端必须快速失败并走同城本地 fallback。 |
| 应用美术 UI 动效设计师 | 保留电影票、路线卡、纸感留白的视觉方向；底部输入入口不能悬浮遮挡卡片。 |
| QA 设备验证负责人 | 小屏和折叠屏重点看标题、按钮、底部入口是否溢出或盖住路线内容。 |

## 已完成改动

- 首页保留路线卡片瀑布流作为第一入口，`自己说一句` 改为内容流内的入口，不再作为底部悬浮按钮覆盖卡片。
- 小屏首屏只取前 2 张主卡，减少纵向拥挤；普通/宽屏继续展示更多卡片。
- 点击路线卡后会把卡片标题、场景、理由、策略、站点信号写入结构化 `TP_INTENT_*` 标记，结果页继承被点击的卡片，不再随机跳成另一条路线。
- AI 网关客户端超时改为快速 fallback：连接 `800ms`，读取 `2200ms`；Worker 默认 Kimi 超时改为 `3000ms`。
- 加载成功停留时间从长等待收短到 `500ms`，避免用户感觉卡住。
- 回归审计增加 V0.9.72 守护：快速 fallback、卡片继承、滚动内输入入口、小屏不遮挡。

## 交付物

- APK：`D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-release.apk`
- AAB：`D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-release.aab`
- APK SHA-256：`83E30EF07B55557D6BC472474D3FFD5DE267E6962C3D756712E166B2455E2EF6`
- AAB SHA-256：`902F5931E752403D87712AE754B9CA38C52712719E00D4DB374F12CD0A4999EC`
- 签名证书 SHA-256：`2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695`

## 截图证据

- 普通屏首页：`D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-qa-home.png`
- 小屏首页：`D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-qa-small-home.png`
- 折叠屏首页：`D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-qa-foldable-home.png`
- 加载页：`D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-qa-loading.png`
- 结果页：`D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-qa-result.png`
- 运行日志：`D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-qa-logcat.txt`

## 验证结果

- `app_regression_audit.py`：通过。
- `adaptive_ui_audit.py`：通过。
- `compileDebugKotlin / lintDebug / assembleRelease / bundleRelease / testDebugUnitTest`：通过；单元测试任务当前为 no-source。
- Android release audit：通过；lint 0 errors，5 个依赖版本提示。
- APK 元数据：`versionName=0.9.72`，`versionCode=89`。
- APK 签名：通过，v2 签名有效。
- 模拟器冷启动：通过。
- 路线卡点击生成：通过，日志显示 `generate_start` 到 `generate_complete` 约 2.74 秒。
- 崩溃日志：未发现 `FATAL EXCEPTION`、`ANR in com.todayplay.app` 或 TodayPlay 进程崩溃标记。

## 已知问题

- 尚未在你的 OPPO N6 / 折叠屏真机上安装验证；本轮是 Android API 35 模拟器验证。
- 当前 Kimi 网关仍是可选外部能力；如果外部网关慢或不可用，前台会走本地同城 fallback。
- Google 登录、支付、Play Console 上架仍不是本轮真实联调范围。
- 通用 localization audit 有一个旧口径要求“首页语言选择”，与当前产品决策“语言放设置”冲突；项目回归审计已明确守护语言不回首页。

## 下一轮建议

- V0.9.73：继续做“AI 生成感”可见化，让用户输入一句话后出现 3-4 张明显不同的候选卡，而不是只感觉本地模板在变。
- 补一轮 OPPO/折叠屏真机截图，重点看系统字体、状态栏、底部导航、长中文标题。
- 把商店截图脚本更新为 V0.9.72 的真实路径：路线卡首页、小屏不遮挡、结果页电影票、当前镜头导航。
