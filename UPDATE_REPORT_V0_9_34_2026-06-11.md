# V0.9.34 单点替换增强更新报告

日期：2026-06-11  
版本：`0.9.34 / versionCode 52`  
负责人视角：项目负责人调配产品经理、市场调研、开发与测试  
本轮主题：让用户在某个地点不合适时继续玩下去，而不是重抽整局

## 背景

V0.9.33 已经把结果页包装成更清晰的“开局指南”，但产品经理视角仍有一个高影响体验问题：如果某一站太远、太拥挤、下雨、不合适，用户只能手动忽略它或者重抽整条路线。竞品里的路线编辑、单点替换和备选动作都说明，这一步会明显影响路线完成率。

本轮不接入真实 POI 后端，也不伪造新的真实地点；先用当前每站已有的 `backupPlan` 做可验证的地点级替代玩法。

## 已交付

1. 地点卡新增“单点替代玩法”
   - 每个路线地点卡展示该站的替代方案。
   - 替代方案用于地点太远、天气不好、人流太多或氛围不合适的场景。
   - 用户不用重抽整局，可以继续当前路线。

2. 替代方案接入真实进度状态
   - 点击“改用替代方案”后，该站状态写入 `TaskStatus.Skipped`。
   - 该状态会推进本局进度，避免路线卡住。
   - 该状态不发放地点打卡积分，避免把替代动作误算成真实打卡。
   - 用户仍可切回真实手动打卡，状态会变为 `TaskStatus.Completed` 并获得对应积分。

3. 回归审计新增保护
   - `playstore/app_regression_audit.py` 新增 `Route stop swap path` 检查。
   - 后续预检会检查替代面板、替代按钮、`Skipped` 状态和进度解析仍然存在。

4. 版本与发布材料同步
   - App 版本推进到 `0.9.34 / versionCode 52`。
   - 同步更新发布计划、Play Console 字段清单、官方要求对照、Release Ops 行动计划和项目总控状态。
   - Play 内测说明新增 V0.9.34。

## 验证结果

- `assembleDebug`：通过
- `lintDebug`：通过
- `bundleRelease`：通过
- APK metadata：`versionName=0.9.34`，`versionCode=52`
- `playstore/app_regression_audit.py`：通过，含新增 `Route stop swap path`
- `playstore/google_play_submission_gate.py`：版本、AAB、签名、产物新鲜度、上传密钥、图形素材、本地数据删除、地图/内容边界、回归保护等通过；整体仍为 `fail`，原因仍是外部支持邮箱、公开隐私 URL、release 配置、Billing endpoint 和当前截图。
- 设备状态：当前未连接 Android 设备或模拟器，本轮未做真机点按/截图验证。

## 产物

- Debug APK：`app/build/outputs/apk/debug/app-debug.apk`
- Release AAB：`app/build/outputs/bundle/release/app-release.aab`

## 下一轮建议

1. 产品经理优先评审单点替代路径：替代入口是否足够明显，是否会被误解成真实打卡。
2. 下一轮可做“路线编辑”：允许用户保留喜欢的地点，只重抽不喜欢的地点。
3. 市场调研继续拆解路线收藏、地点替换和城市主题卡产品，优先找低风险可复用机制。
4. 真机可用后优先验证：手动打卡、改用替代方案、切回打卡、历史保存、通关分享里的进度是否符合预期。
