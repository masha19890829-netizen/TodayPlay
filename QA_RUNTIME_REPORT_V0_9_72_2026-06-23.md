# TodayPlay V0.9.72 QA 运行报告

日期：2026-06-23  
设备：Android API 35 模拟器 `TodayPlay_QA_API35`  
测试包：`D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-release.apk`

## 测试范围

- 安装最终 release APK。
- 冷启动进入首页。
- 普通屏首页截图。
- 320 宽小屏首页截图。
- 折叠屏尺寸首页截图。
- 点击首页第一张路线卡进入生成流程。
- 捕获加载页和结果页。
- 检查最近日志中是否存在 TodayPlay 崩溃标记。

## 结果摘要

| 项目 | 结果 | 备注 |
| --- | --- | --- |
| APK 安装 | 通过 | `adb install -r` 成功。 |
| 冷启动 | 通过 | 进入路线卡首页。 |
| 普通屏首页 | 通过 | 可见路线卡瀑布流，不是文字墙。 |
| 小屏首页 | 通过 | 320 宽下标题、按钮、卡片未溢出。 |
| 折叠屏首页 | 通过 | 双列布局稳定，底部输入入口不再覆盖卡片。 |
| 生成路径 | 通过 | 点击“下班 90 分钟心动线”后进入结果页。 |
| 结果继承 | 通过 | 结果页继续显示“下班 90 分钟心动线 / 上海 / 90 分钟”。 |
| 生成耗时 | 通过 | 日志约 2.74 秒完成。 |
| 崩溃日志 | 通过 | `TODAYPLAY_CRASH_MARKERS=0`。 |

## 截图路径

- `D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-qa-home.png`
- `D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-qa-small-home.png`
- `D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-qa-foldable-home.png`
- `D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-qa-loading.png`
- `D:\AppStore\nemu\real\dist\TodayPlay-v0.9.72-qa-result.png`

## 日志证据

```text
06-23 13:24:54.225 D TodayPlayEvent: generate_start {source=home_instant, relationship=暧昧中}
06-23 13:24:56.968 D TodayPlayEvent: generate_complete {relationship=暧昧中, time=90 分钟, budget=100 元以内}
TODAYPLAY_CRASH_MARKERS=0
```

## 适配观察

- 之前折叠屏/小屏最明显的问题是底部 `自己说一句` 悬浮入口遮住路线卡，本轮已经改为滚动内容中的入口。
- 小屏截图中第二张卡只露出顶部，属于自然滚动，不是遮挡。
- 折叠屏截图中页面使用双列卡片；按钮宽度稳定，没有文字挤出边界。
- UIAutomator XML 在 PowerShell 默认读取时会出现假乱码；按 UTF-8 读取后首页和结果页关键文案均可命中。

## 风险与未覆盖

- OPPO N6 真机未连接，无法验证 OEM 字体、真实折叠姿态和系统导航栏差异。
- 模拟器曾因长时间运行出现 ADB offline，本轮已重启模拟器后重新安装并截图。
- Kimi/API 网关真实质量未作为本轮 QA 通过条件；当前验收重点是快速 fallback 和外测不长时间卡住。
