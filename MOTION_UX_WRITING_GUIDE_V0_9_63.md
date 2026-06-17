# TodayPlay Motion And UX Writing Guide V0.9.63

Date: 2026-06-17
Owner: Motion Director and UX Writing Editor

## 0. Goal

Motion and copy exist for one purpose:

> Help the user feel that TodayPlay is turning today's real state into a believable route.

Motion is not decoration. Copy is not explanation. Every animation should clarify state, and every line of text should move the user one step forward.

## 1. Motion Rhythm

| Scene | Target duration | Rule |
| --- | ---: | --- |
| Cold splash | 900-1200 ms | Brand mood, paper surface, short route line. |
| Home input ready | 220-320 ms | Input gently settles into focus. |
| AI understanding | 1800-4200 ms | Show staged progress, then allow fallback if slow. |
| Candidate card reveal | 480-900 ms | Stagger cards so choices feel curated. |
| Route connection | 500-700 ms | Connect selected stops in order. |
| Current stop reveal | 260-420 ms | Lift current stop and reveal first action. |
| Ticket stamp | 360-520 ms | Completion feedback, not a blocking celebration. |

General rules:

- Use fade, small lift, line draw, and light stamp.
- Avoid bouncing, spinning, confetti, particle effects, and endless loops.
- Same screen should not have more than three continuous animations.
- Respect reduced-motion settings.
- Do not block input or navigation.

## 2. Splash

Use splash only on cold start.

Sequence:

1. Paper background appears.
2. TodayPlay mark fades in.
3. A short route line draws through 2-3 dots.
4. Home input receives the visual focus.

Rules:

- Tap or normal app flow can skip after 600 ms.
- Do not replay on simple resume.
- Do not use remote assets.
- Do not use video splash.

## 3. Home Input

Default prompt:

```text
说一句今天想怎么玩
```

Support line:

```text
城市、和谁、时间、预算，说一点就够。
```

Focus behavior:

- Border darkens slightly.
- Input lifts by about 4-8dp.
- Submit becomes active only after valid intent.
- Chips use a short press feedback, not loud animation.

Rules:

- Placeholder should not be a long example.
- If user text grows, input scrolls internally instead of pushing the whole page apart.
- Missing city can trigger one short question.

City question:

```text
你今天在哪个城市？
```

## 4. AI Understanding

AI thinking should be staged.

| Stage | Copy |
| --- | --- |
| 0-900 ms | 正在读懂今天的状态 |
| 900-2200 ms | 正在挑同城候选点 |
| 2200-4200 ms | 正在按时间和体力排路线 |
| 4200-6000 ms | 正在确认没有编造地点 |
| After 6000 ms | 先给你一版稳妥路线 |

Allowed exits:

```text
取消
先看稳妥版
```

Do not show:

```text
AI timeout
Kimi 429
JSON 解析失败
网关不可用
```

Fallback copy:

```text
先给你一版稳妥路线。
```

Small note:

```text
使用同城样例地点生成，出发前请确认实际营业和交通。
```

## 5. Candidate Cards

Cards should communicate several possible ways to spend today.

Reveal:

- Understanding summary fades in first.
- Best-fit card appears first.
- Other cards stagger by 60-90 ms.
- First screen should show up to three cards, not a dense wall.

Card copy structure:

1. Title: up to 14 Chinese characters.
2. Subtitle: up to 22 Chinese characters.
3. Stop preview: 2-4 short names.
4. Fit reason: up to 32 Chinese characters.

Example:

```text
下班低压聊天线
2 站，少走路，可随时结束
武康路 / 小河边咖啡
有点累，所以只排两站。
```

Avoid:

- More than four tags.
- Long explanation.
- Fake heat, rating, official status, or nearby claims.
- Over-intimate promises.

## 6. Route Connection

When a user selects a candidate, the result page must inherit that candidate.

Sequence:

1. Selected card presses down lightly.
2. Title carries into result page.
3. Stop dots light up in order.
4. Route line connects dots.
5. Current stop card appears.

Rules:

- Max total motion: 700 ms.
- The selected stop order must not change unexpectedly.
- Do not add unselected stops during transition.
- Reduced motion uses simple fade.

## 7. Current Stop

The first result screen must show what to do now.

Current stop copy:

```text
当前站：武康路 Citywalk
先慢走 15 分钟，找一个自然开口。
```

Rules:

- Current stop explanation max one short sentence.
- Primary action should appear within the first screen.
- Do not bury navigation/check-in behind long route text.

## 8. Ticket Stamp And Completion

Completion should feel like a private route ticket, not a game level.

Single stop complete:

```text
已盖章，下一站准备好了。
```

Whole route complete:

```text
今天这条线完成了。
留一张票根，或发给同行的人。
```

Rules:

- Stamp does not block next action.
- Animation is short.
- No exaggerated congratulations.
- No "relationship guaranteed" language.

## 9. Performance And Accessibility

Performance:

- Aim for smooth 60fps on normal devices.
- Do not add large Lottie, remote animation, or video dependency in this round.
- Keep local bitmap assets compressed.
- Loading state should not wait for network images.

Accessibility:

- Reduced motion changes lift/scale into fade.
- State cannot rely on color alone.
- Large font mode may grow cards, but must not hide buttons.
- Foldable outer display must keep input, submit, and current stop action visible.

## 10. UX Writing Voice

Voice:

- Calm.
- Useful.
- Brief.
- Humble about data.
- Specific to today.

Use:

```text
你今天
建议
样例地点
可随时结束
先给你一版
```

Avoid:

```text
用户需求
保证
实时最火
官方认证
附近都在去
AI 已完全理解你
```

## 11. Copy Length Limits

| Surface | Limit |
| --- | ---: |
| Home title | 12 Chinese characters |
| Input placeholder | 18 Chinese characters |
| Support line | 24 Chinese characters |
| Candidate title | 14 Chinese characters |
| Candidate subtitle | 22 Chinese characters |
| Candidate reason | 32 Chinese characters |
| Result fit reasons | 3 bullets, 24 Chinese characters each |
| Error/fallback | 2 lines, 24 Chinese characters each |

## 12. Error And Fallback Copy

AI slow:

```text
先给你一版稳妥路线。
```

No city:

```text
你今天在哪个城市？
```

Too few local samples:

```text
这个城市样例还少。
先给你一条保守路线。
```

Network unavailable:

```text
现在用本地样例生成。
联网后可以再换一版。
```

Input too long:

```text
先抓重点就好。
城市、和谁、时间、预算最有用。
```

## 13. Result Page Copy

First screen should answer:

1. Why this route.
2. Where to go now.
3. What to check before leaving.

Fit reasons example:

```text
你说下班后有点累，所以只排 2 站。
你想轻松聊天，所以优先公开、好撤退的地点。
预算 100 内，所以减少高消费停留。
```

Source note:

```text
地点为本地样例，出发前请确认营业和交通。
```

AI success note:

```text
已按你的输入重排同城候选点。
```

Local fallback note:

```text
当前使用本地样例生成，仍按你的输入做了筛选。
```

## 14. QA Checklist

Motion:

- Splash under 1200 ms.
- Home input usable within 1 second.
- AI slow state has fallback after 6000 ms.
- Candidate cards are clickable immediately.
- Route connection under 700 ms.
- Current stop action is visible on first result screen.
- Ticket stamp does not block next action.
- Reduced motion works.

Writing:

- Home is not a text wall.
- Cards can be understood at a glance.
- Error states do not show technical codes.
- Result page first screen uses no more than three fit reasons.
- No unsupported real-time heat, rating, official, GPS, or social claims.
- Long copy does not push buttons out on foldable or large font screens.

## 15. Final Rule

If the user must read a long explanation before acting, the copy failed.

If animation is beautiful but does not clarify the route-building state, the motion failed.
