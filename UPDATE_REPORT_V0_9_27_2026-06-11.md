# V0.9.27 内部上架风险收口报告

## 基线

- 项目：`D:\AppStore\nemu\real`
- 包名：`com.todayplay.app`
- 当前版本：`0.9.27 / versionCode 45`
- 本轮原则：只收口内部可修复上架风险，不伪造外部 Play Console、隐私 URL、Billing 后端或真实截图状态。

## 本轮改动

- 加固 `playstore/google_play_submission_gate.py`：
  - 新增 release AAB 签名元数据检查，验证 `META-INF/MANIFEST.MF`、`.SF`、`.RSA/.DSA/.EC`。
  - 新增 `keystore.properties` UTF-8 BOM 检查，防止 Gradle 读取签名字段误判。
  - 新增商店截图尺寸、新鲜度和可见 `?` 分隔符/乱码风险提示。
  - 新增版本文档同步检查，确保发布计划、Play Console 字段清单、官方要求说明与 Gradle 版本对齐。
- 修复版本文档同步：
  - `GOOGLE_PLAY_RELEASE_PLAN_V0_8_1.md`
  - `playstore/play_console_submission_fields.md`
  - `playstore/google_play_official_requirements_2026.md`
- 新增 `playstore/app_regression_audit.py` 并接入 `playstore/run_release_preflight.ps1`：
  - 路线生成 clean 文案路径。
  - Billing endpoint 未配置时禁用购买。
  - 历史 upsert / clear / 打卡积分状态。
  - 高德地图、浏览器、剪贴板 fallback 可构造性。

## 验证结果

- `assembleDebug bundleRelease lintDebug`：通过。
- Android release audit：通过，版本 `0.9.27 / 45`。
- Lint：`0 errors / 5 dependency warnings`。
- App regression audit：通过。
- AAB 签名门禁：通过，检测到 `META-INF/TODAYPLA.SF` 和 `META-INF/TODAYPLA.RSA`。
- Upload key 门禁：通过，`hasUtf8Bom=False`。
- Version documentation sync：通过。
- Store screenshots：`warn`，当前 4 张截图是旧草稿，需要用当前 APK/AAB 重新实机或模拟器采集。
- Google Play submission gate：整体仍为 `fail`，原因是外部项未完成。

## 最新产物

- 本地测试 APK：`D:\AppStore\nemu\real\app\build\outputs\apk\debug\app-debug.apk`
  - 大小：`17,008,369` bytes
  - 时间：`2026-06-11 11:20:11`
- Google Play AAB：`D:\AppStore\nemu\real\app\build\outputs\bundle\release\app-release.aab`
  - 大小：`9,310,811` bytes
  - 时间：`2026-06-11 11:20:36`

## 仍需外部完成

- 设置真实 Play Console 支持邮箱。
- 部署 `playstore/privacy_site` 到公开 HTTPS URL，并填入 Play Console。
- 部署真实 HTTPS `/billing/verify`，配置 `BILLING_VERIFY_ENDPOINT`。
- 配置 Play Console 商品、订阅和内部测试轨道。
- 连接真实设备或模拟器，重新采集当前版本商店截图，并人工检查截图中是否有 `?` 分隔符或乱码。

## 暂缓项

- 依赖升级暂缓。当前 5 个 lint warning 都是依赖新版本提示，不是构建错误；升级 Compose/Activity/Lifecycle/Billing 应作为单独工具链升级任务处理。
- Android App 层 JVM 单测暂缓。当前先用脚本化回归审计做门禁；后续引入 test 依赖后再补真正的 repository/generator/billing/map 单测。
