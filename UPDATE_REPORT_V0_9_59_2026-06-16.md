# TodayPlay V0.9.59 AI 个性化路线外测版报告

日期：2026-06-16  
版本：0.9.59 / versionCode 77  
APK：`dist/TodayPlay-v0.9.59-release.apk`  
APK SHA-256：`8330A4035999C346DAB2F04D2CECA3715BB5580C8E44E0A378E798B2FF6912AA`

## 本轮目标

V0.9.59 的目标是把 TodayPlay 从固定路线目录推进到“能根据用户一句话生成路线”的外测版本。核心路径是：

1. 用户在首页输入一句“今天想怎么玩”。
2. App 先展示“我理解的是”，让用户确认城市、关系、时间、预算和倾向。
3. 确认后生成路线，结果页展示“为什么适合你”、路线预览、当前站和导航入口。
4. Kimi 不直连 APK，所有大模型调用必须走 TodayPlay AI Route Gateway；网关不可用时本地兜底，保证外测不白屏、不崩溃。

## 团队分工与汇报

- 项目负责人：版本统筹、关键路径决策、最终 QA 放行。
- Android 开发工程师：接入 `AiQuestGenerator`、首页一句话入口、收藏路线页、结果页解释卡。
- AI 网关工程师：新增 `backend/ai-route-gateway`，约束 Kimi 只能从同城候选站点中选择。
- 产品经理 & 内容负责人：确认首页主路径为“一句话 -> 理解确认 -> 生成”，避免回到文字墙。
- UI/动效设计：保留浪漫极简主视觉、启动动效、加载动效，并修正确认卡被底部导航遮挡的问题。
- 设备截图 QA：复核黑屏来源、模拟器冷启动、频道、结果页、折叠屏和横屏风险。
- 发布合规负责人：检查 API Key 边界、版本元数据、签名、审计和外测提示。

## 已完成内容

### AI 路线网关

新增 `backend/ai-route-gateway`：

- `GET /health`
- `GET /ai/provider/status`
- `POST /ai/route/generate`

网关读取 `KIMI_API_KEY`、`KIMI_BASE_URL`、`KIMI_MODEL`，但这些只存在服务端环境文件中，不进入 Android APK。网关会校验：

- App 传入的候选站点必须同城。
- Kimi 返回的 `selectedStopIds` 必须来自候选站点。
- 跨城、空结果、JSON 异常、超时、未配置 Key 都走 `usedFallback: true`。

### Android AI 边界

- APK 只读取 `BuildConfig.AI_ROUTE_GATEWAY_URL`。
- Android 源码不包含 Kimi Key，也不直连 Moonshot/Kimi 域名。
- 网关 URL 为空、非法或不可用时，App 自动使用本地路线生成。
- 本地兜底会在结果页明确标注“当前为本地兜底路线；POI 数据仍为本地样例。”

### 首页体验

- 新增“一句话今天想怎么玩”输入卡。
- 支持关系 chips 和偏好 chips：对象、暧昧、朋友、家人、低压力、想聊天、有点累、想拍照、100内。
- 新增“我理解的是”确认卡。
- 修复确认卡出现后按钮被底部导航遮住的问题：确认卡会自动滚入可点击区域。

### 结果页体验

- 新增“为什么适合你 / PERSONAL FIT”卡片。
- 结果页保留地图式路线预览、当前站、导航当前站。
- 中文环境下 AI/本地兜底说明已中文化。

### 收藏入口

- 底部“收藏”不再跳到旧的 QuickStart 模板。
- 新增 `SavedRoutesScreen`，读取本地历史路线，可打开或重跑。

## QA 结果

### 已通过

- AI gateway smoke test：通过。
- `playstore/app_regression_audit.py`：通过。
- `playstore/release_config_audit.py`：通过。
- `assembleDebug`：通过。
- `testDebugUnitTest`：通过，无本地单测源。
- `lintDebug`：通过，0 error，5 个依赖版本 warning。
- `assembleRelease`：通过。
- APK 元数据：`versionName=0.9.59`、`versionCode=77`。
- APK 签名校验：通过。
- 模拟器安装：通过。
- 冷启动：通过。
- 首页可见路线卡片，不是文字墙。
- 一句话确认卡：通过，按钮可见。
- Loading：通过。
- 结果页：通过，可见“为什么适合你”、路线预览、上海站点和导航当前站。
- 最近日志：未发现 TodayPlay `FATAL EXCEPTION` / ANR。

### 签名信息

- 证书 DN：`CN=TodayPlay Upload, OU=TodayPlay, O=TodayPlay, L=Shenzhen, ST=Guangdong, C=CN`
- 证书 SHA-256：`2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695`

### 截图产物

- `dist/TodayPlay-v0.9.59-qa-home.png`
- `dist/TodayPlay-v0.9.59-qa-date-channel.png`
- `dist/TodayPlay-v0.9.59-qa-friend-channel.png`
- `dist/TodayPlay-v0.9.59-qa-solo-channel.png`
- `dist/TodayPlay-v0.9.59-qa-understand.png`
- `dist/TodayPlay-v0.9.59-qa-loading.png`
- `dist/TodayPlay-v0.9.59-qa-result.png`
- `dist/TodayPlay-v0.9.59-qa-foldable-home.png`
- `dist/TodayPlay-v0.9.59-qa-foldable-understand.png`
- `dist/TodayPlay-v0.9.59-qa-landscape-home.png`

## QA 发现与处理

### 已处理

- 最初首页截图黑屏不是 App 渲染黑屏，而是模拟器灭屏状态下截图。已调整 QA 流程：先唤醒、解锁、冷启动、等待首帧，再截图。
- 首页确认卡在普通手机尺寸下会被底部导航遮挡。已修复为自动滚入可见区域。
- 结果页中文环境出现英文兜底说明。已改为中文。

### 未阻塞但需要下一轮优化

- 横屏首屏可用，但按钮区域在第一屏有轻微裁切感，需要下一轮做真正的横屏/折叠内屏布局。
- 当前 APK 未配置正式公网 AI Gateway URL，所以外部手机安装后默认走本地兜底。要让外部测试用户真正调用 Kimi，需要先部署 AI Route Gateway，并把 HTTPS 网关地址写入 `release_config.properties` 后重打包。
- POI 数据仍是本地样例，不是官方授权、实时热度或真实地图数据。

## 外部测试说明

这是外测 APK，不是 Google Play 正式上架包。测试用户需要知道：

- 不要输入身份证、电话、住址、密码、健康、财务等敏感信息。
- AI 或本地路线只是测试建议，不代表官方推荐或安全保证。
- 当前不请求 GPS 定位，不接真实地图 SDK，不承诺实时热度、评分或营业状态。
- 当前 Google 登录和支付仍是受配置保护的脚手架，不作为本轮外测主功能。
- 如果没有公网 AI Gateway，App 会使用本地兜底路线，仍然可以测试首页、确认卡、路线结果页和收藏流程。

## 下一步建议

1. 部署 TodayPlay AI Route Gateway 到 HTTPS 公网地址，接入 Kimi 后重打一个 `0.9.60` 真 AI 外测包。
2. 为 OPPO 折叠屏、横屏和大字体模式做专门布局，不再只是把手机布局拉宽。
3. 为 AI 路线结果增加“为什么这样排顺序”“换城市/换预算/更安静一点”的二次修改入口。
4. 继续扩充本地 POI 样例库，至少覆盖上海、深圳、广州、杭州的晚间、周末、独处、朋友局和心动路线。
