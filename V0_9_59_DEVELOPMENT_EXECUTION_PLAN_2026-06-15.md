# TodayPlay V0.9.59 开发执行计划书

日期：2026-06-15  
版本目标：`0.9.59 / versionCode 77`  
制作人目标：让 TodayPlay 第一次具备“理解用户今天状态”的核心能力。

## 1. 本轮版本定义

V0.9.59 定义为：

```text
AI 个性化路线内测版
```

不是“多加几条路线”，也不是“大模型炫技版”。本轮必须让用户感知到：

```text
我说出的需求，会真实影响生成结果。
```

## 2. 成功标准

### 产品成功标准

用户输入：

```text
我在上海，和刚认识的人，下班后想轻松聊一聊，预算100内，不想太累。
```

App 必须表现出：

- 理解城市是上海。
- 理解关系是刚认识/心动。
- 理解低压力、预算 100 内、下班后、90 分钟左右。
- 生成的路线只在上海。
- 结果页说明“为什么适合我”。
- 不像固定模板换标题。

### 技术成功标准

- Android 不存 Kimi API Key。
- Kimi Key 只在 `backend/ai-route-gateway/.dev.vars` 或部署环境变量中。
- 后端能调用 Kimi。
- 后端输出结构化 JSON。
- Android 能调用 AI Route Gateway。
- AI 失败、超时、断网时自动本地兜底。
- `assembleDebug`、`lintDebug`、`assembleRelease` 通过。
- 模拟器冷启动、AI 生成、结果页、收藏、反馈入口通过。

## 3. 本轮 P0 范围

### P0-1：AI Route Gateway 后端

新增：

```text
backend/ai-route-gateway/
```

必须包含：

- Kimi provider。
- 本地 mock provider。
- `/ai/route/generate` endpoint。
- 请求 schema。
- 响应 schema。
- Kimi 输出 JSON 解析。
- 输出校验。
- 失败 fallback。
- 不记录完整 API Key。
- 不把用户输入写入永久日志。

### P0-2：首页一句话输入

首页新增轻输入入口：

```text
说一句今天想怎么玩
```

输入框不做长表单，只允许短句。保留快捷 chips：

- 关系：心动 / 朋友 / 家人 / 自己
- 状态：累了 / 想聊天 / 想拍照 / 想散步
- 强度：低压力 / 正常 / 多走点
- 时间：30 分钟 / 90 分钟 / 半天
- 预算：0 元 / 100 内 / 300 内

### P0-3：理解确认卡

生成前先显示：

```text
我理解的是
城市：
关系：
状态：
预算：
路线倾向：
```

按钮：

- `就按这个生成`
- `改一下`

### P0-4：个性化结果页

结果页新增：

```text
为什么适合我
```

必须基于用户输入生成 2-3 条短理由。

### P0-5：真收藏列表

修复当前问题：

```text
底部“收藏”不能再打开“灵感开局”
```

最低要求：

- 首页卡片点收藏后，收藏页能看到。
- 收藏页可再次开始路线。
- 收藏页可取消收藏。

### P0-6：外测反馈入口

设置页新增：

```text
外测反馈
```

最低要求：

- 能复制反馈模板。
- 或能打开邮箱/问卷。

### P0-7：文案修正

必须修：

- Loading 不再写“全球精选样例”。
- Quick Start 不再写 `mock`。
- 卡片进入结果页后标题保持连续。

## 4. 本轮明确不做

- 不接真实支付。
- 不接 Play Console。
- 不接真实 Google 登录。
- 不接真实地图 SDK。
- 不做多模型前台切换。
- 不开放无限 AI 生成。
- 不声称“真实附近热门推荐”。

## 5. 后端设计

### Endpoint

```text
POST /ai/route/generate
GET /ai/provider/status
```

### 请求字段

```json
{
  "locale": "zh-CN",
  "city": "上海",
  "freeText": "我在上海，和刚认识的人，下班后想轻松聊一聊，预算100内，不想太累。",
  "relationship": "date",
  "moods": ["累了", "想聊天"],
  "timeBudgetMinutes": 90,
  "moneyBudgetCny": 100,
  "mobility": "low",
  "candidateStops": []
}
```

### 响应字段

```json
{
  "status": "ready",
  "provider": "kimi",
  "usedFallback": false,
  "title": "下班 90 分钟低压力聊天线",
  "subtitle": "两站都在公开区域，适合轻松开口",
  "understanding": {
    "city": "上海",
    "relationship": "刚认识的心动对象",
    "mainNeed": "轻松聊天，不想太累",
    "constraints": ["100元以内", "90分钟", "低移动压力"]
  },
  "whyThisRoute": [
    "你说下班后有点累，所以只安排 2 站。",
    "第一站适合自然开口，第二站适合边走边聊。"
  ],
  "selectedStopIds": ["sh-wukang-road", "sh-bund-view"],
  "stopScripts": [],
  "inviteText": "今晚走这条？2站，90分钟，公开场所，轻松聊聊。",
  "safetyNote": "全程优先公开区域，累了可以提前结束。"
}
```

## 6. 硬约束

AI 不能自由决定这些事情：

- 不能编造地点。
- 不能跨城市。
- 不能超预算。
- 不能超出用户时间。
- 不能安排危险或过度亲密任务。
- 不能声称真实热度、评分、官方认证。
- 不能写“附近最火”“全网推荐”这类未验证内容。

后端必须校验：

- JSON 可解析。
- stopId 来自候选列表。
- 城市一致。
- 文案长度不过长。
- 失败时本地兜底。

## 7. 团队任务分配

### 项目负责人 / 总调度

Owner：Codex 主线程  
交付：

- 冻结版本范围。
- 检查角色回报。
- 最终验收 APK。
- 保证 Key 不泄漏。

### 首次体验产品经理

Owner：产品经理 & 测试负责人线程  
交付：

- 一句话输入流程。
- 理解确认卡字段。
- 首次 2 分钟验收标准。
- V0.9.59 试用脚本。

### 竞品与用户动机研究员

Owner：产品策划 & 市场调研线程  
交付：

- AI 行程/活动推荐竞品机制。
- 可复用的用户输入范式。
- 应避雷的大模型体验问题。

### 关系场景内容策划

Owner：产品策划 & 市场调研线程兼任  
交付：

- date / friend / family / solo 的安全边界。
- Prompt 禁止项。
- 任务模板。
- 关系语气规范。

### 社交发现 UI 动效设计师

Owner：应用美术 UI 动效设计师线程  
交付：

- 首页一句话输入视觉方案。
- 理解确认卡设计。
- AI Loading 阶段动效。
- 结果页 `为什么适合我` 视觉。

### Android 体验开发工程师

Owner：Android 开发工程师线程  
交付：

- 版本升级到 `0.9.59 / 77`。
- 首页 AI 输入入口。
- 理解确认卡。
- 调用 AI gateway。
- 结果页显示个性化理由。
- 真收藏列表。

### 路线逻辑与 AI 接入工程师

Owner：Android 开发工程师线程兼任，必要时下轮拆独立线程  
交付：

- `backend/ai-route-gateway`。
- Kimi provider。
- mock provider。
- 请求/响应 schema。
- 输出校验。
- fallback。

### QA 与设备验证负责人

Owner：设备截图 QA 线程  
交付：

- 三组不同输入的生成差异验证。
- 断网/超时/fallback 验证。
- 普通屏、折叠屏竖屏、折叠屏宽屏截图。
- 日志崩溃检查。

### 发布合规负责人

Owner：发布上架负责人线程  
交付：

- 隐私文案更新：用户输入会发送到 AI 网关。
- 外测说明边界。
- 禁止商店文案夸大 AI 能力。

### Billing 后端验单负责人

Owner：Billing 线程当前连接异常，主线程暂代  
交付：

- 本轮确认支付不进入开发范围。
- 避免 AI 版和付费权益混在一起。

## 8. 验收测试输入

### Case A：心动

```text
我在上海，和刚认识的人，下班后想轻松聊一聊，预算100内，不想太累。
```

### Case B：朋友

```text
我在广州，三个朋友晚上想随便吃点再散步，预算人均80，不想排队。
```

### Case C：独处

```text
我在杭州，今天有点累，想一个人慢慢走，最好安静一点，花钱少。
```

### Case D：家人

```text
我在深圳，周末和爸妈出去，不想走太多，想舒服一点。
```

## 9. 交付物

- `dist/TodayPlay-v0.9.59-ai-preview.apk`
- `UPDATE_REPORT_V0_9_59_2026-06-15.md`
- `AI_ROUTE_GATEWAY_DESIGN_V0_9_59_2026-06-15.md`
- `PROMPT_AND_SAFETY_RULES_V0_9_59_2026-06-15.md`
- `PRODUCER_PLAYTEST_REPORT_V0_9_59_2026-06-15.md`
- QA 截图：
  - 首页 AI 输入
  - 理解确认
  - AI Loading
  - 心动结果
  - 朋友结果
  - 独处结果
  - 收藏列表
  - 设置反馈入口

## 10. 开工顺序

1. 产品/UI/内容先回报 30 分钟内可落地方案。
2. 后端先做 mock provider，再接 Kimi。
3. Android 先接 mock gateway，后切 Kimi。
4. QA 先写测试矩阵，不等开发完成。
5. 主线程收齐方案后开始代码实现。
