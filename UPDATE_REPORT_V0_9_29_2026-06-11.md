# V0.9.29 发布运营材料同步收口报告

## 本轮目标

继续推进最终可上线版本的内部可控事项。本轮专门修复上架执行材料的版本漂移问题：`release_ops_action_plan_2026-06-11.md` 仍停留在旧版本，可能导致 Play Console 内测说明和最终上传判断使用过期版本号。

## 已完成

- 版本推进到 `0.9.29 / versionCode 47`。
- 更新 `playstore/release_ops_action_plan_2026-06-11.md`：
  - Release baseline。
  - Versioning passes。
  - 内测 release notes 提示。
  - 最终 AAB 上传准入条件。
- 更新 `playstore/play_console_submission_fields.md`，新增 V0.9.29 内测说明。
- 更新 `GOOGLE_PLAY_RELEASE_PLAN_V0_8_1.md` 和 `playstore/google_play_official_requirements_2026.md` 当前版本口径。
- `playstore/google_play_submission_gate.py` 的版本文档同步门禁新增 `release_ops_action_plan_2026-06-11.md`。

## 预期结果

- 如果发布运营计划再次停留在旧版本，Google Play submission gate 会在 `Version documentation sync` 中失败。
- 外部未完成项继续保持失败/警告，不伪造支持邮箱、隐私 URL、Billing endpoint 或真实截图。

## 验证结果

- `assembleDebug bundleRelease lintDebug`：通过。
- Android release audit：通过，版本 `0.9.29 / 47`。
- Lint：`0 errors / 5 dependency warnings`。
- Google Play Billing audit：通过客户端骨架检查；真实付费仍需 Play Console 商品和服务端验单。
- App regression audit：通过。
- Travel content worker contract tests：`7/7 pass`。
- Billing verify worker contract tests：`9/9 pass`。
- Google Play submission gate：整体仍为 `fail`，但内部门禁项按预期通过：
  - `Release AAB signature`：pass。
  - `Artifact version freshness`：pass，debug metadata 已对齐 `0.9.29 / 47`。
  - `Version documentation sync`：pass，包含 release ops action plan。

## 最新产物

- 本地测试 APK：`D:\AppStore\nemu\real\app\build\outputs\apk\debug\app-debug.apk`
  - 大小：`17,008,369` bytes
  - 时间：`2026-06-11 11:33:31`
- Google Play AAB：`D:\AppStore\nemu\real\app\build\outputs\bundle\release\app-release.aab`
  - 大小：`9,310,807` bytes
  - 时间：`2026-06-11 11:33:48`

## 仍需外部完成

- 真实 Play Console 支持邮箱。
- 公开 HTTPS 隐私政策 URL。
- 真实 HTTPS `/billing/verify`。
- Play Console 商品/订阅和内部测试轨道配置。
- 当前版本真实设备或模拟器截图重采。
