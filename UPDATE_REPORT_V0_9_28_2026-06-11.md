# V0.9.28 发布材料同步收口报告

## 本轮目标

继续推进“最终可上线版本”的内部可修复项。本轮不堆新功能，专门修复发布材料当前版本口径和门禁过宽问题，避免旧文档误导上架交接。

## 已完成

- 版本推进到 `0.9.28 / versionCode 46`。
- 修复 `GOOGLE_PLAY_RELEASE_PLAN_V0_8_1.md` 顶部当前版本，从旧的 `0.9.20 / versionCode 38` 对齐到当前 Gradle 版本。
- 修复 `playstore/google_play_official_requirements_2026.md` 当前项目状态和版本号表格。
- 更新 `playstore/play_console_submission_fields.md`，新增 V0.9.28 内测说明。
- 收紧 `playstore/google_play_submission_gate.py` 的版本文档同步检查：
  - 发布计划必须有正确的 `当前版本` 行。
  - Play Console 字段清单必须有正确的 `Current release checkpoint` 行。
  - 官方要求说明必须有正确的 `版本` 行。

## 验证重点

- AAB 签名门禁继续保留。
- keystore BOM 检查继续保留。
- 截图旧草稿和可见 `?`/乱码复核风险继续保留为 `warn`。
- 外部项继续真实失败，不伪造上架完成。

## 本轮验证结果

- `assembleDebug bundleRelease lintDebug`：通过。
- Android release audit：通过，版本 `0.9.28 / 46`。
- Lint：`0 errors / 5 dependency warnings`。
- Google Play Billing audit：通过客户端骨架检查；真实付费仍需 Play Console 商品和后端验单。
- App regression audit：通过。
- Travel content worker contract tests：`7/7 pass`。
- Billing verify worker contract tests：`9/9 pass`。
- Google Play submission gate：整体仍为 `fail`，外部阻塞项保持真实暴露。

## 最新产物

- 本地测试 APK：`D:\AppStore\nemu\real\app\build\outputs\apk\debug\app-debug.apk`
  - 大小：`17,008,369` bytes
  - 时间：`2026-06-11 11:28:14`
- Google Play AAB：`D:\AppStore\nemu\real\app\build\outputs\bundle\release\app-release.aab`
  - 大小：`9,310,809` bytes
  - 时间：`2026-06-11 11:28:33`

## 仍需外部完成

- 真实 Play Console 支持邮箱。
- 公开 HTTPS 隐私政策 URL。
- 真实 HTTPS `/billing/verify`。
- Play Console 商品/订阅配置。
- 当前版本真实设备或模拟器截图。
