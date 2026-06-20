# TodayPlay V0.9.71 Update Report

版本：0.9.71 / versionCode 88  
交付 APK：`dist/TodayPlay-v0.9.71-release.apk`  
补充 AAB：`dist/TodayPlay-v0.9.71-release.aab`

## 团队同步结论

- 首次体验产品经理：V0.9.71 必须让用户 5 秒内先看到可刷可点的路线卡，而不是先读说明或先输入表单。
- QA 与设备验证负责人：版本号、路线卡首页 token、小屏/折叠屏文字溢出、样例来源标注、生成转场文案都是阻断项。
- 项目负责人：本轮先把首页主路径切成“路线卡优先 + 自己说一句为次级入口”，并保留后续 AI 对话生成能力。

## 本轮改动

- 首页入口从旧的 chat-first 文字/输入优先页切到 `V0971RouteCardHomeExperience`。
- 首屏保留极简顶栏、场景 chips、瀑布流路线卡和浮动 `自己说一句`。
- 路线卡新增：
  - 主视觉缩略图与路线连线。
  - 城市、时间、预算、移动强度。
  - 3 个站点预览。
  - `开始 / 保存 / 邀约`。
  - `本地样例 POI / 待核验样例` 等来源状态。
- 新增 `换一幕`，让首页瀑布流可以轻量换序，不再像固定内容墙。
- 生成 loading 文案改为更符合票根转场的四段：理解需求、筛选同城镜头、剪成路线副本、打印今日票根。
- 版本升级到 `0.9.71 / 88`。
- 审计新增 V0.9.71 路线卡首页守护，防止回退到文字墙或隐藏样例来源。

## 设计与交接资产

- `DESIGN_INTERACTION_BLUEPRINT_V0_9_71_2026-06-20.md`
- `docs/design/v0.9.71/todayplay-v0.9.71-design-board.png`
- `docs/design/v0.9.71/todayplay-v0.9.71-responsive-states.png`
- `docs/design/v0.9.71/screens/todayplay-v0.9.71-screen-handoff.png`
- `docs/design/v0.9.71/screens/todayplay-v0.9.71-home-default.png`
- `docs/design/v0.9.71/screens/todayplay-v0.9.71-home-filtered.png`
- `docs/design/v0.9.71/screens/todayplay-v0.9.71-card-detail.png`
- `docs/design/v0.9.71/screens/todayplay-v0.9.71-input-focus.png`
- `docs/design/v0.9.71/screens/todayplay-v0.9.71-generation-keyframe.png`
- `docs/design/v0.9.71/screens/todayplay-v0.9.71-result-first-screen.png`

## 构建与校验

- `app_regression_audit.py`：通过。
- `adaptive_ui_audit.py`：通过。
- `design_handoff_audit.py`：通过。
- `compileDebugKotlin`：通过。
- `lintDebug`：通过，0 errors；剩余 5 个依赖新版本提醒，非本轮阻断。
- `assembleRelease`：通过。
- `bundleRelease`：通过。
- APK 元数据：`com.todayplay.app`，`versionName=0.9.71`，`versionCode=88`，minSdk 26，targetSdk 35。
- APK 签名：`apksigner verify` 通过，v2 签名。

## 哈希与签名

- APK SHA-256：`8E7661B2B0DD09E97EE9DFFA469BE7C075D71B45717BAB871933015690115D6C`
- AAB SHA-256：`30B4147C671A78802128A48BB4AF799CD42AE2A5BAF810DE1891A94814816037`
- 签名证书 SHA-256：`2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695`

## QA 结果

已通过：

- 源码审计确认：首页先进入 V0.9.71 路线卡瀑布流。
- 适配审计确认：首页、结果页、按钮、标题和多语言文本有溢出保护。
- 编码审计确认：核心本地化文件 UTF-8，无常见乱码标记。
- APK/AAB 构建和签名确认通过。
- 模拟器安装成功，冷启动首页、朋友筛选、生成转场、结果页、小屏首页、折叠屏模拟首页截图已完成。
- 日志未发现 TodayPlay `FATAL EXCEPTION`、`ANR in com.todayplay.app` 或 `Process: com.todayplay.app` 崩溃记录。

未完成：

- 未覆盖 OPPO 真折叠屏或真实物理设备。V0.9.71 已完成模拟器运行态 QA，但不能声明覆盖用户手机环境。
- 从 loading 到结果页等待偏长，本轮实测需要额外等待约 25 秒；V0.9.72 需要优化 AI 网关/本地 fallback 超时。
- 小屏首页底部浮动 `自己说一句` 会覆盖第二张卡片下半部分，需要下一版做避让。

## 外测说明

这个 APK 可以用于外部安装测试，但仍不是正式 Play 上架状态：

- Google 登录仍需要真实 OAuth 后端验签配置。
- Google Play Billing 客户端和后端骨架存在，但验单端点为空，付费入口被保护，不会真实开放购买。
- POI 与电影/路线内容仍是本地样例或待核验样例，不伪装真实热度、评分或官方授权。

## 下一版建议

V0.9.72 应优先做三件事：

- 把 V0.9.71 的路线卡首页继续打磨成更少字、更强图、更明显地图路线预览。
- 将 `自己说一句` 与 AI 网关实际联动验证，确保不同用户输入生成明显不同的路线候选。
- 使用 OPPO 折叠屏或同类真机补冷启动、首页、结果页、横屏和日志。
