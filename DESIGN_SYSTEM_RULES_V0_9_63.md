# TodayPlay Design System Rules V0.9.63

Date: 2026-06-17
Owner: Design System Lead

## 0. Core Rule

TodayPlay is not a route catalog. It is an AI life-planning companion for one believable day.

The screen must answer one promise:

> Say one natural sentence, and TodayPlay turns today's mood, city, time, budget, and companion into a same-city route you can actually start.

If a design choice weakens the input box, same-city trust, or route truthfulness, reject it.

## 1. Product Soul

TodayPlay should feel like:

- A calm companion that helps decide what to do today.
- A lightweight AI planning surface.
- A route copy generator that produces something shareable and editable.
- A local sample route engine that clearly labels what is sample, AI-ranked, or unverified.

TodayPlay must not feel like:

- A static travel article feed.
- A map app replacement.
- A generic chatbot wrapper.
- A fake real-time ranking app.
- A social matching product.
- A store screenshot prototype with features the APK does not contain.

## 2. Home Screen

The home screen is chat-first.

Required hierarchy:

1. Minimal top bar: product name, city/status, history/settings.
2. Central input box: the strongest visual object on the page.
3. Short prompt: one line only.
4. Quick chips: companion, mood, time, budget, mobility.
5. Result area: appears after user intent, not before.

Rules:

- The input box is the hero.
- Do not lead with a static channel wall.
- Do not fill the first screen with product explanation.
- Do not force a form before a user can speak naturally.
- Do not show language, payment, long settings, or complex onboarding in the first path.
- Chips help expression; they do not replace the sentence input.

Preferred copy:

```text
说一句今天想怎么玩
```

Support copy:

```text
城市、和谁、时间、预算，说一点就够。
```

## 3. Visual Foundation

Direction:

- Premium paper-gray.
- Warm white surfaces.
- Restrained rose and black cherry accents.
- Large whitespace.
- Low-noise texture.
- Light editorial feeling.
- Calm AI console feeling, not cyber tech.

Avoid:

- Purple/blue AI gradients.
- Decorative blobs.
- Heavy shadows.
- Full-screen banners.
- Cheap realistic stock art.
- Repeated placeholder illustrations.
- Overly cute cartoon UI that weakens trust.

## 4. Palette

| Token | Hex | Use |
| --- | --- | --- |
| PaperWhite | `#F8F3EC` | Main background |
| GalleryWhite | `#FFFBF4` | Elevated panels |
| WarmCream | `#F6EFE7` | Secondary surface |
| MistRose | `#F2E6E6` | Quiet selected state |
| DustPink | `#D89A9A` | Soft accent |
| RoseGold | `#D4A79C` | Lines, icons, minor highlights |
| CherryPressed | `#B76267` | Primary action |
| BlackCherry | `#3B1B1F` | Emphasis text and active state |
| WarmGray | `#8B8580` | Secondary text |
| InkGray | `#282423` | Primary text |

Color rules:

- Use CherryPressed sparingly.
- Use BlackCherry for emphasis, not large background blocks.
- Keep trust labels low saturation.
- Never use color alone to show status.

## 5. Typography And Copy Density

Rules:

- Titles should be compact.
- Body text should be short.
- No negative letter spacing.
- No viewport-width font scaling.
- Keep first-screen text sparse enough to scan in 3 seconds.
- Route explanations should fit in 1-3 short bullets.

Copy should answer:

- Why this route fits the user.
- What to do next.
- What is sample, local, AI-ranked, or unverified.

Copy should not:

- Explain the whole app.
- Make emotional promises.
- Claim real-time facts.
- Claim official ranking, ratings, heat, queues, or opening status without data.

## 6. Signature Input Box

The input box is TodayPlay's signature component.

Anatomy:

- 16-20dp radius.
- Paper or warm white fill.
- 1dp subtle border.
- Placeholder no longer than one short sentence.
- Bottom row for city, preference, voice if available, and send.
- Submit becomes visually active only when intent exists.

States:

| State | Visual rule |
| --- | --- |
| Empty | Quiet border, low-emphasis placeholder |
| Focused | Thin rose/cherry border or glow |
| Typing | Submit activates |
| Understanding | Small scanning line or text highlight |
| Confirming | Compact intent summary |
| Generating | Input compresses into task status |
| Error | Inline short message |
| Success | Becomes compact follow-up composer |

Never turn the input into a dense form.

## 7. AI Generation States

AI generation must feel structured:

1. Understand the sentence.
2. Extract city, companion, time, budget, mood, mobility, indoor preference, and avoid items.
3. Select same-city candidate stops.
4. Arrange route alternatives.
5. Verify same-city and unsupported claims.
6. Reveal cards and route copy.

Good status copy:

```text
正在读懂今天的状态
正在挑同城候选点
正在按时间和体力排路线
正在确认没有编造地点
```

Bad status copy:

```text
正在搜索全网热门景点
已发现附近最火店铺
官方认证推荐
```

## 8. Candidate Cards

Candidate cards are AI route choices, not ads.

Each card needs:

- Title.
- Duration/stops/budget/mobility.
- 2-4 stop preview names.
- One fit reason.
- Source/trust state if needed.
- One clear action.

Card types:

- Best fit.
- Quieter.
- Livelier.
- Cheaper.
- Less walking.
- Indoor first.
- Small surprise.

Rules:

- No more than three visible tags.
- No full route essay on a card.
- No repeated identical image crops.
- No fake ratings or heat.
- No impossible mixed-city route.

## 9. Result Page

The result page must be executable on the first screen.

Required first-screen content:

1. Route title.
2. Duration, stops, budget level, mobility level.
3. Why it fits: up to three short bullets.
4. Current stop or first action.
5. Route preview or map-like summary.
6. Edit/refine actions.

The selected candidate's title, stops, order, and reason must carry into the result page. Do not re-randomize a different route after the user chooses a card.

## 10. Route Copy

Route copy should be concise and shareable.

Pattern:

```text
1. 19:00  武康路附近慢走
   先用低压力环境开场，15-20 分钟即可。
2. 19:30  安静咖啡/轻食点
   适合坐下聊天，预算控制在 100 内。
3. 20:20  可选收尾
   状态好再走一小段，累了可以直接结束。
```

Rules:

- Use time or sequence.
- Include stay length when useful.
- Include one graceful exit option.
- Keep claims humble.
- Label sample data.

## 11. Same-City Trust

Hard rules:

- Every route stays in one city.
- AI may only select from provided candidate stops.
- AI cannot invent POI names, addresses, ratings, reviews, queues, discounts, official partnerships, opening status, or heat.
- If data is sample/local/unverified, label it.
- If AI fails, local fallback still respects the user's input.

Trust labels:

```text
同城候选池
本地样例
待验证
AI 已按输入排序
本地兜底生成
```

Forbidden labels:

```text
附近最火
评分最高
营业中
官方推荐
全网精选
实时热度
朋友都在去
```

## 12. Android Adaptation

Foldable and narrow screens are design requirements, not afterthoughts.

Must support:

- Phone portrait.
- Small phone / foldable outer display.
- Foldable inner display.
- Phone landscape.
- Large font mode.

Rules:

- No clipped title.
- No clipped CTA.
- No replacement glyphs.
- No icon font that may fail on OEM devices.
- Chips wrap or scroll safely.
- Primary input and first action remain reachable.
- Wide screens should use max width or two-pane structure, not stretched phone columns.

The QA gate must include small phone, regular phone, foldable, and landscape screenshots.

## 13. Art Asset System

Assets must be produced as a set:

- Splash.
- Chat-first home.
- Candidate cards.
- Loading / AI thinking.
- Result page.
- Current stop / completion ticket.

Rules:

- Use project-generated or project-owned assets only.
- Do not imitate a specific living artist or studio.
- Do not use fake licensed material.
- Do not reuse one art image with different text for all channels.
- Every art asset must have a screen purpose.

## 14. Motion

Motion must explain state.

Allowed:

- Splash route line draw.
- Input focus lift.
- AI understanding stages.
- Candidate card stagger reveal.
- Route line connection.
- Current stop emphasis.
- Ticket stamp after completion.

Avoid:

- Generic spinning loaders.
- Particle storms.
- Endless breathing on multiple cards.
- Decorative motion that delays the first action.

Reduced-motion mode should preserve state changes with fade or instant layout updates.

## 15. Acceptance Checklist

Before the next UI implementation is accepted:

- Home clearly says "say one sentence".
- First screen is not a text wall.
- Input box is visually dominant.
- Candidate cards differ across different user inputs.
- Selected candidate persists into result page.
- Route stays same-city.
- Sample/unverified data is labeled.
- First action is visible on result page.
- Small phone, foldable, and landscape screenshots show no overflow.
- Motion has a reason and can be reduced.
- Store screenshots do not promise unavailable functions.

## 16. Final Rule

TodayPlay should feel like a quiet AI companion arranging one believable today.

If it starts to feel like a catalog, a demo, or a decorative mood board, the design has drifted.
