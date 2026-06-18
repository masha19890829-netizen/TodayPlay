# TodayPlay V0.9.68 更新报告

## 版本目标

本轮把 TodayPlay 从普通路线结果推进到「时光电影路线」体验：一句话生成电影感候选卡，选中后进入票根、导演剪辑、地图路线和当前镜头结果页。

## 团队分工结论

- 首席产品经理：V0.9.68 应收敛成一个闭环，不继续堆更多路线卡。核心是「一句话生成一张可分享的时光电影路线」。
- 应用美术 UI 动效设计师：结果页要像安静、有编号、有路线的电影票；动效节奏应是出票根、画路线、点亮三幕，而不是普通弹窗动画。
- QA 设备验证负责人：小屏、折叠屏、横屏重点检查孤字、乱码、按钮遮挡、地图压缩和生成结果是否随输入变化。

## 已完成内容

- 版本升级为 `0.9.68 / versionCode 85`。
- 新增时光电影路线背景资产：`app/src/main/res/drawable-nodpi/tp_art_time_cinema_route.png`。
- 时光电影结果页新增导演剪辑胶片条、当前镜头说明和电影感路线背景。
- 上海、东京各补充 2 个电影感本地样例 POI，并继续标注来源边界，不伪装真实授权或官方取景地。
- App 屏幕状态和语言状态改为可保存，降低折叠屏/横竖屏切换时回到开屏的风险。
- 修复用户指出的开屏标语孤字问题：
  - 中文标语改为固定两行：「把普通日子，剪成 / 只属于你们的一小段电影。」
  - 英文副标题改为固定单行，避免 `QUEST` 单独掉行或被裁切。
  - `app_regression_audit.py` 和 `adaptive_ui_audit.py` 已加入守护项。

## QA 结果

- `playstore/app_regression_audit.py`：通过。
- `playstore/adaptive_ui_audit.py`：通过。
- `assembleDebug`：通过。
- `testDebugUnitTest`：通过。
- `lintDebug`：通过。
- `assembleRelease`：通过。
- release APK 签名校验：通过。
- 模拟器安装、冷启动、首页、候选卡、加载页、结果页：通过。
- 最近日志：未发现 TodayPlay 崩溃标记。

## APK

- APK：`dist/TodayPlay-v0.9.68-release.apk`
- APK SHA-256：`94DF7A2949B3B2E7BC2F541D061981C980D8D34630AE70F00998A4F9CF86D34D`
- 签名证书 SHA-256：`2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695`

## 截图

- 开屏普通屏：`dist/TodayPlay-v0.9.68-splash-late.png`
- 开屏窄屏最终版：`dist/TodayPlay-v0.9.68-splash-narrow-final.png`
- 首页：`dist/TodayPlay-v0.9.68-home-final.png`
- 候选卡：`dist/TodayPlay-v0.9.68-cards-final.png`
- 加载页：`dist/TodayPlay-v0.9.68-loading-final.png`
- 结果页：`dist/TodayPlay-v0.9.68-result-final2.png`

## 仍需下一版继续推进

- V0.9.69：加强个性化记忆，让不同关系、预算、体力和情绪生成明显不同路线。
- V0.9.70：强化地图路线和当前站执行体验，让用户不读长文也知道下一步去哪。
- V0.9.71：完成可分享电影票根卡，把路线结果变成用户愿意保存和转发的记忆卡。
