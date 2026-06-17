# TodayPlay V0.9.64-V0.9.67 Producer Roadmap

Date: 2026-06-17
Owner: Project Lead / Producer

## 0. Producer Judgment

TodayPlay is moving in the right direction, but it is not yet a product with a strong soul.

The current app can generate routes, show candidate cards, and render a clean result page. The missing part is deeper personal difference: two users with different habits should not feel they are receiving the same route with different labels.

The next three to four versions should not chase more static pages. They should make the product feel like a living planning assistant:

```text
一句话需求 -> 理解状态 -> 生成不同方向 -> 选择一条 -> 继续改 -> 记住偏好 -> 下次更懂你
```

## 1. Team Meeting Summary

| Role | Conclusion |
| --- | --- |
| Chief Product Manager | The core promise must be "用一句话安排今天", not "浏览固定路线". The first two minutes should prove the app understands the user's city, relationship, energy, budget, and weather-like constraints. |
| AI Route Logic Lead | Candidate-card strategy must become route-selection pressure. V0.9.64 implemented this for local fallback; next step is memory and gateway-refined candidates. |
| UI / Motion Art Director | The app still needs emotional polish: opening animation, character/companion presence, card reveal, route-line animation, and ticket-like completion. |
| UX Writing Editor | Copy should be shorter and more conversational. Avoid explaining every feature; use one-line intent, short route reasons, and compact failure copy. |
| QA Device Lead | Normal phone, 360dp small phone, foldable-like portrait, and landscape are usable. Ultra-narrow split-window still needs a fallback layout. |
| Release Lead | V0.9.64 is valid for internal/external test APK delivery, but not yet a store-ready release. |

## 2. Version Plan

### V0.9.64 - Adaptive Strategy Release

Status: implemented.

Goal:

- Make candidate cards change the real route output.
- Protect foldable and wide layouts.
- Produce a signed APK for external testing.

Delivered:

- Strategy-aware scoring.
- Indoor candidate card.
- Adaptive generated-state home layout.
- Build, sign, emulator install, screenshots, and report.

Remaining risk:

- Generation takes around 10.5 seconds.
- Personalization is still session-level, not memory-level.

### V0.9.65 - AI Memory And Faster Feeling

Goal:

- Make TodayPlay feel different for different users.
- Reduce perceived wait time.

Product requirements:

- Add a local preference profile:
  - preferred city
  - relationship pattern
  - budget range
  - pace
  - indoor/outdoor preference
  - liked stop categories
  - disliked stop categories
- Use last route feedback to influence next candidate cards.
- Add "I don't want this type of stop" and "remember this preference".
- Show candidate cards earlier, even before full route generation completes.

Acceptance:

- Same prompt with different preference profiles should produce visibly different candidate cards.
- User choosing "更安静" twice should bias later routes toward quiet and indoor stops.
- User rejecting a stop category should reduce it in future local ranking.

### V0.9.66 - Soulful Visual And Motion Pass

Goal:

- Make the app feel calm, premium, and alive before it adds more functions.

Art requirements:

- Opening animation: paper/route ticket appears, soft companion silhouette, TodayPlay title settles.
- Home idle animation: subtle breathing companion or paper grain shimmer, no noisy loops.
- AI generation animation: three stages, "理解需求 / 排列路线 / 打印票根".
- Candidate reveal: cards appear one by one with route line drawing.
- Result page: selected route line connects into the map preview.
- Completion: current stop finished with stamp feedback.

Acceptance:

- First screenshot should communicate "AI private quest" without reading long copy.
- Motion must not block use; every animation should be under 1.2 seconds except route generation progress.
- No heavy purple/blue tech style, no cheap gradients, no random decorative blobs.

### V0.9.67 - Content And Gateway Expansion

Goal:

- Let AI planning rely on richer same-city options and safer gateway behavior.

Content requirements:

- Expand Shanghai and Shenzhen local POI pools first.
- Add at least 30 sample POIs per supported city before adding more cities.
- Label every POI as local sample, curated sample, or real source.
- Add city-specific route constraints to avoid impossible cross-city mixes.

AI requirements:

- Gateway receives user intent, preference memory, and same-city candidate POIs.
- Gateway may choose and explain stops, but cannot invent unsupported stop IDs.
- If gateway fails, local engine should still generate differentiated cards.

Acceptance:

- Offline/local fallback and gateway route should both obey same-city rules.
- Generated output should explain why this route fits the user, not just list stops.
- No fake heat, reviews, ratings, official partnerships, or real-time status.

## 3. Team Workflow For Next Iterations

1. Product Manager writes the target user story and first-2-minute acceptance test.
2. AI Route Lead defines the intent/memory data needed for that story.
3. UI/Motion Art Director creates the screen and animation spec before Android work starts.
4. UX Writing Editor compresses every visible line before implementation.
5. Android Engineer implements the smallest end-to-end slice.
6. QA runs:
   - source regression audit
   - adaptive UI audit
   - build/lint/release checks
   - emulator install/cold start
   - normal phone, 360dp small phone, foldable-like portrait, landscape screenshots
7. Producer approves only if screenshots and behavior show real user value, not just a successful build.

## 4. Current Blocking Questions

These are not blockers for V0.9.64 external testing, but they are blockers for a global-quality product:

- What cities are we willing to support first with real content depth?
- Should TodayPlay remember preferences locally only, or require account sync later?
- Does Kimi become the primary planner, or only a refinement/explanation layer above local candidate selection?
- Should the app position itself as private planning, date/friend social decision, or solo-life companion first?
- What is the signature visual character: route ticket, quiet companion, city diary, or all three as one system?

## 5. Producer Decision

Proceed with V0.9.65 next.

The priority order is:

1. Preference memory.
2. Faster AI-feeling generation.
3. Route differences that are obvious to testers.
4. Then the V0.9.66 visual/motion upgrade.

Do not add more static route packs until the app can prove it understands and remembers the user.

