# TodayPlay V0.9.54 Update Report - 2026-06-12

## Review Lens

本轮按“OPPO 折叠屏真机反馈 + QA 适配复测”处理。用户反馈重点是文案标题在折叠屏上超出、顶到状态栏或出现乱码感，因此 V0.9.54 不新增业务功能，专门修折叠屏、窄屏、字体符号稳定性和截图可验证性。

## Problems Found

- V0.9.53 在窄外屏模拟尺寸下复现到首页标题区域靠近状态栏，状态栏时间会压到 APP 标题区域。
- 首页和底部导航使用了齿轮、空心心形、返回尖括号等特殊符号；不同厂商字体下存在显示成方块或问号的风险。
- 顶部城市按钮、设置按钮、站点预览胶囊在窄屏上缺少更明确的最大宽度和省略策略。
- 全局背景漂浮装饰在窄屏上可能靠近顶部按钮区，容易被误认为文案重叠或乱码。
- 结果页顶部栏也需要统一安全区处理，避免在折叠外屏、挖孔屏、状态栏高度变化时压住标题。

## Changes Shipped

- 首页和宽屏布局增加状态栏/导航栏安全区处理：
  - 首页移动端 `LazyColumn` 使用 `statusBarsPadding()`
  - 宽屏 Row 使用 `statusBarsPadding()` 和 `navigationBarsPadding()`
  - 底部导航使用 `navigationBarsPadding()`
- 通用顶部栏 `CuteTopBar` 增加状态栏安全区，返回按钮从特殊符号改为稳定 ASCII `<`。
- 首页顶部按钮适配窄屏：
  - 城市路线包按钮限制最大宽度并使用省略号。
  - 设置按钮从固定 40dp 改为可伸缩宽度。
- 路线站点预览胶囊增加最大宽度和文字权重，长站点名会收缩省略，不再把卡片撑开。
- 底部导航从特殊符号图标改成稳定短文字：
  - 中文：`首 / 藏 / + / 史 / 设`
  - 英文：`Home / Save / + / Hist / Set`
- 全应用去掉高风险装饰符号：`♥ / ♡ / ⚙ / ⌂ / ↺ / ‹ / ✧`。
- 去掉全局漂浮文字装饰，保留纸张纹理和真实图片，避免窄屏顶部按钮区被装饰干扰。
- `playstore/app_regression_audit.py` 增加 `Foldable safe text and insets` 守护，后续回归会检查安全区、底部导航短文字、站点预览收缩和高风险符号清理。
- 版本升级到 `0.9.54 / versionCode 72`。

## Validation

- `playstore/app_regression_audit.py`: pass
- `assembleDebug`: pass
- `testDebugUnitTest`: pass, no unit test sources currently present
- `lintDebug`: pass
- `assembleRelease`: pass
- APK metadata: `com.todayplay.app`, `versionName=0.9.54`, `versionCode=72`
- Release APK signature: pass, APK Signature Scheme v2
- Signer certificate SHA-256: `2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695`
- APK SHA-256: `B5492A61EA562BCF63E64409318BDEA1A1FAADA03B06B1222D4824764ACC1DD5`
- Emulator install and cold launch: pass
- Phone screenshot QA: pass
- Fold inner screenshot QA: pass
- Fold outer screenshot QA: pass
- Fold outer result-page smoke test: pass
- UI text dump scan: pass, `0` risky glyphs and `0` negative bounds across captured screens
- Recent log check: no `FATAL EXCEPTION` or TodayPlay crash marker found

## Deliverables

- APK: `dist/TodayPlay-v0.9.54-release.apk`
- Phone screenshot: `dist/TodayPlay-v0.9.54-qa-phone.png`
- Fold inner screenshot: `dist/TodayPlay-v0.9.54-qa-fold-inner.png`
- Fold outer screenshot: `dist/TodayPlay-v0.9.54-qa-fold-outer.png`
- Fold outer result screenshot: `dist/TodayPlay-v0.9.54-qa-fold-outer-result.png`
- Foldable QA report: `FOLDABLE_QA_REPORT_V0_9_54_2026-06-12.md`

## Not Included This Round

- No real Google login wiring.
- No payment or Billing production configuration.
- No Kimi API integration.
- No Play Console submission.
- No new route content pack expansion.

## Recommended Next Iteration

- 用真实 OPPO Find N 系列或用户手机复测字体缩放、分屏、内外屏切换。
- 增加系统字体大小 1.3x / 1.5x 的截图守护。
- 继续压缩结果页下半屏长文，把任务说明改成“当前一步 + 可跳过 + 可分享”的轻量卡片。
