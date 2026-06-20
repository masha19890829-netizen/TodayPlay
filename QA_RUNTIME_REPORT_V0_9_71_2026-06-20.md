# TodayPlay V0.9.71 Runtime QA Report

测试日期：2026-06-20  
测试包：`dist/TodayPlay-v0.9.71-release.apk`  
设备：Android Emulator `sdk_gphone64_x86_64` / Android 15  
默认尺寸：1080x2400 / density 420

## 结论

V0.9.71 release APK 已完成模拟器安装、冷启动、首页、筛选、生成转场、结果页、小屏和折叠屏模拟截图验证。核心目标成立：首屏不再是语言选择或文字墙，而是路线卡瀑布流；卡片能进入票根 loading，并最终进入结果页。

仍未覆盖真实物理设备，尤其是用户提到的 OPPO 折叠屏/窄屏机型，需要下一轮真机补测。

## 通过项

- 安装：`adb install -r dist/TodayPlay-v0.9.71-release.apk` 成功。
- 冷启动：首页可见 `TodayPlay · 同城`、场景 chips、两列路线卡和浮动 `自己说一句`。
- 首页无旧首屏：未出现首页语言选择、长说明、`生成全球路线副本` 等旧路径。
- 朋友筛选：点击 `朋友` 后路线卡变为朋友局内容，不是同一内容换标题。
- 生成转场：点击卡片 `开始` 后显示票根打印 loading，阶段为 `理解需求 / 筛选同城镜头 / 剪成路线副本 / 打印今日票根`。
- 结果页：等待后进入 `路线副本`，标题为 `广州老街小吃探秘之旅`，继承 `广州 / 朋友 / 2 小时` 和站点 `永庆坊老街小吃线 -> 沙面岛低强度散步线`。
- 样例边界：结果页显示 `来源：本地样例 POI + 个性化排序；不伪造真实热度、评分或营业状态。`
- 小屏：320x640 / density 160 下首页可读，标题、chips、按钮没有单字掉行或明显溢出。
- 折叠屏模拟：1768x2208 / density 320 下首页为两列路线卡，卡片未膨胀成单张大 banner。
- 日志：未发现 TodayPlay `FATAL EXCEPTION`、`ANR in com.todayplay.app` 或 `Process: com.todayplay.app` 崩溃记录。日志中出现的是模拟器网络时间与 Google 连通性探测失败。

## 运行态截图

- 首页默认：`dist/TodayPlay-v0.9.71-qa-home.png`
- 首页默认 XML：`dist/TodayPlay-v0.9.71-qa-home.xml`
- 朋友筛选：`dist/TodayPlay-v0.9.71-qa-friend-filter.png`
- 朋友筛选 XML：`dist/TodayPlay-v0.9.71-qa-friend-filter.xml`
- 生成转场：`dist/TodayPlay-v0.9.71-qa-loading.png`
- 结果页最终：`dist/TodayPlay-v0.9.71-qa-result-final.png`
- 结果页最终 XML：`dist/TodayPlay-v0.9.71-qa-result-final.xml`
- 小屏首页：`dist/TodayPlay-v0.9.71-qa-small-home.png`
- 小屏首页 XML：`dist/TodayPlay-v0.9.71-qa-small-home.xml`
- 折叠屏首页：`dist/TodayPlay-v0.9.71-qa-foldable-home.png`
- 折叠屏首页 XML：`dist/TodayPlay-v0.9.71-qa-foldable-home.xml`
- 日志：`dist/TodayPlay-v0.9.71-qa-logcat.txt`

## 已知问题

- 从卡片点击 `开始` 到结果页最终出现等待偏长，本轮实测需要额外等待约 25 秒。可接受为外测风险，但 V0.9.72 应优化 AI 网关/本地 fallback 超时，目标是 3-6 秒内进入结果。
- 小屏首页底部浮动 `自己说一句` 会覆盖第二张卡片的下半部分。主路径仍可用，但 V0.9.72 应让浮动按钮避开卡片内容或缩成底部输入条。
- 折叠屏只做了模拟尺寸验证，没有真机转轴/窗口分屏验证。
- 没有完成 OPPO 真机截图，不能声明已覆盖用户的 OPPO 折叠屏环境。

## 下一轮 QA 建议

- 使用 OPPO 折叠屏或同类真机补：冷启动、首页、朋友筛选、结果页、小屏外屏、横屏、日志。
- 将生成 fallback 超时加入自动化审计或埋点，防止结果页等待超过 8 秒。
- 对浮动按钮增加小屏避让规则，避免覆盖路线卡主体内容。
