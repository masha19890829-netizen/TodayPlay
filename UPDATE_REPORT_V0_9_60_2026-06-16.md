# TodayPlay V0.9.60 AI Gateway + Adaptive UI Update

日期：2026-06-16

## 版本结论

V0.9.60 已把主线推进到“本地 AI 大模型链路可跑通 + 首页/横屏/折叠屏适配改善”的状态。

本轮交付两个 APK：

- 本地 AI 联调包：`dist/TodayPlay-v0.9.60-ai-local-release.apk`
  - 用途：电脑 Android 模拟器 + 本机 AI 网关联调。
  - AI 网关：`http://10.0.2.2:8787`
  - SHA-256：`E0C65D178AA6937225586A837D4CC54B156B4D4400E79B5A8F0BA999500AA1BD`
- 外部测试安全包：`dist/TodayPlay-v0.9.60-external-fallback-release.apk`
  - 用途：发给外部用户安装体验主路径。
  - AI 状态：不写死本机网关，默认本地路线兜底。
  - SHA-256：`8CCF1675C72559CF9AFBDF4C8B0978E707DBE35E1A639C1E54EF0930D996F9C1`

两个包均为 `versionName=0.9.60`、`versionCode=78`，release 签名校验通过。

## 团队执行记录

- AI 接入工程：补强 `backend/ai-route-gateway`，加入 Kimi 真实 probe、同城候选点约束、超时控制、Android 本地调试授权、烟测。
- UI/适配 QA：修复首页短横屏、折叠屏、宽屏列宽、长中文按钮换行和底部导航紧凑模式。
- 制作人集成：合并两条线，修 Android cleartext 本地网关策略、客户端 AI 读取超时、Kimi 限流/超时兜底说明，并完成 APK、截图、审计、签名。

## AI 链路状态

已验证：

- 本地 Worker 健康检查通过：`GET /health`
- Kimi API Key 已被 Worker 读取，Android APK 内没有 Kimi Key。
- Android 模拟器能通过 `10.0.2.2:8787` 调到本机 Worker。
- App 首页 AI 输入 -> 理解确认 -> 加载页 -> 结果页跑通。
- 最终结果页出现“AI 已按你的输入改写路线文案；POI 数据仍为本地样例。”，证明 App 吃到了 AI 网关返回。

稳定性修复：

- Kimi route generation 增加 `max_tokens=520`，减少生成耗时。
- Worker 默认 Kimi 超时从 9 秒提升到 25 秒。
- Android 客户端 AI 网关读取超时从 10 秒提升到 28 秒。
- 429/timeout/网关失败会明确走本地兜底，避免白屏或崩溃。

重要边界：

- 当前机器没有 Cloudflare 登录态，`wrangler whoami` 显示未认证，所以本轮不能直接部署公开 HTTPS Worker。
- 外部手机不能访问 `10.0.2.2` 本机模拟器地址；要让外部测试也跑真实 AI，需要先部署公开 HTTPS AI 网关，并把 `AI_ROUTE_GATEWAY_URL` 配成该地址重新打包。

## UI 与适配

本轮修复：

- 首页继续保留图片 Hero、AI 一句话输入、频道、推荐卡，避免回到文字墙。
- 短横屏切到双栏布局，不再被底部导航压住。
- 折叠屏内屏使用双栏，左侧 AI/频道，右侧 Hero/快速入口。
- 按钮和频道 pill 支持两行/自适应高度，降低中文长标题被裁风险。
- 本地 HTTP 只允许 `10.0.2.2 / 127.0.0.1 / localhost`，生产仍要求 HTTPS。

仍需优化：

- 底部导航还在用“首/藏/史/设”这类文字图标，视觉上不够高级，后续应换成统一线性图标。
- 横屏双栏首屏可用，但左侧确认按钮仍接近底部，需要继续压缩 AI 卡片高度。
- 外部测试包目前 AI 只能兜底，公开网关部署前不要宣传“外部用户可用真实 AI”。

## 截图

- `dist/TodayPlay-v0.9.60-ai-home.png`
- `dist/TodayPlay-v0.9.60-ai-understand.png`
- `dist/TodayPlay-v0.9.60-ai-loading.png`
- `dist/TodayPlay-v0.9.60-ai-result.png`
- `dist/TodayPlay-v0.9.60-foldable-home.png`
- `dist/TodayPlay-v0.9.60-landscape-home.png`

## 验证结果

通过：

- `python playstore/app_regression_audit.py .`
- `python playstore/release_config_audit.py .`
- `npm test` in `backend/ai-route-gateway`
- `:app:assembleDebug`
- `:app:testDebugUnitTest`
- `:app:lintDebug`
- `:app:assembleRelease`
- `aapt dump badging`
- `apksigner verify --verbose --print-certs`
- 模拟器冷启动无闪退
- 首页 AI -> 理解确认 -> 加载 -> 结果页通过
- 折叠屏截图、横屏截图通过基本目检
- 最近日志未发现 TodayPlay 崩溃

注意：

- `npm audit` 对 Wrangler 本地开发依赖报 4 个 high severity 告警；该依赖不进入 APK。本轮尝试降级到 audit 建议版本 3.6.0，但 Windows 本机缺少 SDK 编译依赖导致失败。部署公开 Worker 前应重新评估 Wrangler 安全版本或在 CI/Linux 环境锁定安全依赖。

## 下一步建议

1. 部署公开 HTTPS AI Route Gateway。
2. 重新打包一个真正外部 AI 测试版 APK。
3. 加备用模型 provider，例如 DeepSeek，Kimi 429 时自动切换。
4. 把底部导航和顶部按钮替换为统一图标系统。
5. 继续压缩短横屏 AI 卡片，让“确认并生成”完整露出。
