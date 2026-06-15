# GitHub Skill Research V0.9.57 - 2026-06-15

## 执行结果

本轮按用户要求补充设计 workflow skill。最初尝试使用 curated skill 列表时遇到 GitHub API `403`，随后改用 GitHub repo path 直装，不阻塞版本推进。

已安装到本地 Codex skills：

- `figma-use`
- `figma-implement-design`
- `figma-generate-design`

安装来源：

- GitHub repo: `openai/skills`
- Path:
  - `skills/.curated/figma-use`
  - `skills/.curated/figma-implement-design`
  - `skills/.curated/figma-generate-design`

说明：安装后如需在后续新线程中自动出现在 skill 列表，建议重启 Codex；本轮已经手动读取并应用其工作流。

## 学到的工作流

### Figma Implement Design

适用于把 Figma 设计转成真实工程代码。关键要求：

- 先获取设计上下文和截图，不凭感觉写 UI。
- 下载并使用设计源资产，不用占位图假装完成。
- 将 Figma token 映射到项目已有主题、组件和排版。
- 最后用截图做视觉对比，检查间距、字号、颜色、响应式和资产渲染。

### Figma Generate Design

适用于把现有页面或描述反向整理成 Figma 屏幕。关键要求：

- 先理解页面结构，再分区搭建。
- 优先复用设计系统组件、变量、文本样式和阴影样式。
- 每次只搭一个大区块，避免一口气堆完整页面导致错位难修。
- 每段完成后截图验证，重点看文字裁切、重叠和占位文案。

### Figma Use

适用于直接操作 Figma 文件。关键要求：

- 所有操作分步进行，并返回创建/修改节点 ID。
- 使用 0-1 色值、先加载字体、先 append 再设置 fill/hug。
- 失败后先读错误，不盲目重试。

## 本轮应用方式

当前项目没有可连接的 Figma 文件或 Figma URL，所以本轮没有调用 Figma MCP。我们把流程转化为工程内动作：

- 先确认用户参考稿的视觉主张：浪漫极简、电影日常、纸质票据、低文字负担。
- 生成并筛选 5 张项目内主视觉资产。
- 将资产复制到 Android `drawable-nodpi`，不引用 Codex 临时目录。
- 将首页、频道、加载页、结果页、分享卡和会员页接入新资产。
- 调整主题色 token，让代码视觉和参考稿方向一致。
- 更新 `app_regression_audit.py`，防止主路径回退到旧 `anime_*` 占位矢量图。

## 后续建议

- 如用户后续提供正式 Figma 文件，应按 `figma-implement-design` 工作流做 1:1 还原。
- 美术组应把本轮 `romantic_*` 资产作为 V0.9.57 临时主视觉基准，下一轮再细化为可持续设计系统。
- 若要做商店正式截图，应先在 Figma 或截图脚本中锁定 6 张截图版式，再由真实 APK 页面截图验证。
