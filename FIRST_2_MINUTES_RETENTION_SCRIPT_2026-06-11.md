# TodayPlay First 2 Minutes Retention Script - 2026-06-11

## Project Lead Decision

The problem is not only onboarding. The real problem is the first-session loop after a new user enters the app.

TodayPlay should not ask a cold user to understand the whole product first. It should let the user see something worth doing, tap it, get a small reward, then decide whether to personalize more.

Core change for V0.9.46:

> Open app -> see attractive route cards -> pick one feeling -> save/start/invite -> then refine preferences.

## Review Roles

| Role | New Name | Responsibility |
| --- | --- | --- |
| Product lead | 首次体验产品经理 | Own the first 2 minutes, retention hook, and decision flow. |
| Market research | 竞品与用户动机研究员 | Study discovery, dating, local-life, and social apps. |
| UI / motion | 社交发现 UI 动效设计师 | Design feed cards, visual hierarchy, micro-interactions, and first-screen mood. |
| Android | 首页体验开发工程师 | Implement the feed-first home and direct route actions. |
| QA | 首次启动体验 QA | Test install-open-first-action flow on emulator and OPPO-like viewport. |

## Competitor Learning For The First 2 Minutes

- Instagram Explore teaches that discovery should show interesting photos, reels, accounts, and deeper interest paths first, not a setup form.
- Instagram Reels and TikTok-style feeds teach that a user should understand the content value by consuming, not by reading instructions.
- Tinder Explore teaches category tiles and interest-based experiences before deeper matching.
- Bumble / Bumble BFF teaches that different social intents should be separated into high-level modes, such as date, friends, and local groups, instead of a long mixed questionnaire.
- Local-life apps such as Dianping teach that route/place discovery needs concrete visual cards, category chips, and fast access to details.

Sources:

- Instagram Search & Explore: https://about.instagram.com/features/search-and-explore
- Instagram Reels: https://about.instagram.com/features/reels
- Tinder Explore: https://www.help.tinder.com/hc/en-us/articles/38094560737677-Explore
- Bumble BFF: https://bumble.com/the-buzz/what-exactly-is-bumble-bff
- Bumble For Friends / Hives: https://bumble.com/join-hives

## First 2 Minutes User Script

### 0-5 Seconds: First Impression

Goal: user immediately knows "this app gives me things to do today."

Screen should show:

- Small top bar: TodayPlay, current city, settings icon.
- A visual recommendation area, not a language selector or form.
- At least two image-led route cards above the fold.
- One clear primary action: "Start tonight" or "Try this route".

Do not show:

- Multi-language selection.
- Long product explanation.
- Full relationship questionnaire.
- Account login wall.

### 5-20 Seconds: Let User Choose A Feeling

Goal: user makes one lightweight choice without pressure.

Interaction:

- Horizontal chips: Recommended, Date, Friends, Solo, Family, Rainy Day, Low Budget.
- Tapping a chip reshuffles visible cards immediately.
- The app should not navigate away yet; it should reward the tap by changing content.

What the user learns:

- "This app understands scenarios."
- "I can browse without committing."
- "I can get value without filling a profile."

### 20-45 Seconds: Card Curiosity

Goal: user opens or expands one route card.

Card content:

- Image / route motif first.
- Short title, one line.
- Three small chips: time, budget, social type.
- One reason line: why this route fits tonight.
- Visible actions: Save, Start, Invite.

Expanded card:

- 3-stop route preview.
- Small map-like visual.
- "Why this is recommended" in one sentence.
- Safety or comfort hint only when relevant.

Do not bury the card in paragraphs. If a card needs explanation, the card is not good enough.

### 45-75 Seconds: First Reward

Goal: user gets a concrete result quickly.

Preferred reward options:

- Save the route and see a small saved state.
- Start the route and generate a ready plan.
- Invite friend and create share copy.

The fastest route:

1. User taps "Start".
2. App shows route generating for less than 2 seconds if local.
3. Result screen opens with visual route preview at top.
4. Result screen shows next action: open map, replace one stop, or share.

### 75-120 Seconds: Personalization After Value

Goal: only after the user sees value, ask for one useful preference.

Good prompts:

- "Want more like this?"
- "Tonight with who?"
- "Make it cheaper / calmer / more photo-friendly?"

Bad prompts:

- "Complete your profile."
- "Choose language."
- "Fill all preferences before continuing."
- "Sign in to continue."

Account login should appear as a benefit after the first reward:

- "Sign in to sync saved routes and share invites."
- Google login can be shown near Save / Invite, not before browsing.

## Home Page Interaction Logic

### Default State

The home page is a discovery feed. The app preloads local curated route cards.

Primary hierarchy:

1. Top bar.
2. Scenario chips.
3. Waterfall route feed.
4. Floating "Plan tonight" action.
5. Bottom navigation.

### Feed Ranking Before Backend

Until there is a real backend, ranking should use local rules:

- First card matches common high-intent use case: date night or friends meetup.
- Second card should be visually different: solo reset, family, rainy day, or low budget.
- Avoid showing too many same-city or same-emotion cards in a row.
- User taps a chip -> place matching cards first.
- User saves a card -> show more cards with similar mood/time/budget.

### Entry Paths

Path A: Browse first

- Open app.
- Scroll feed.
- Tap card.
- Save or start.

Path B: Scenario first

- Open app.
- Tap "Friends" or "Date".
- Feed changes.
- Start route.

Path C: Urgent plan

- Open app.
- Tap floating "Plan tonight".
- Choose one visual scenario.
- Generate route.

Path D: Returning user

- Open app.
- See "Continue last saved route" as a small strip.
- Feed remains visible below.

## UI / Motion Rules

- Cards enter with subtle fade/slide, no distracting heavy animation.
- Image area should take at least 45% of each card height.
- Card title max one line.
- Reason text max two lines.
- Chips must be compact and scannable.
- Save state must visibly change.
- Tap feedback should feel instant.
- Bottom navigation should use icons plus short labels.

## Content Rules

Each route card needs:

- A concrete scenario: "after-work date", "friends citywalk", "solo reset".
- A clear time range.
- A budget range.
- A relationship or group fit.
- A visual identity.
- One emotional reason.
- One direct action.

Avoid:

- Generic "recommended route".
- Fake user reviews.
- Fake heat labels.
- Long safety lectures.
- Empty map promises if maps are not connected.

## V0.9.46 Acceptance Criteria

- New user can understand the product promise in 5 seconds.
- New user can see at least 2 visual cards without scrolling on Pixel 6 size.
- New user can start a route from home in 2 taps.
- New user can save or invite from a card without account login.
- Login is optional and framed as sync/share benefit.
- Language and settings stay outside the main discovery path.
- First home screen has no dense paragraph block.
- QA records the path: install -> open -> choose chip -> open card -> start route -> back -> save/invite.

## Implementation Priority

1. Replace mobile home order with feed-first discovery.
2. Build route feed data with scenario, image, chips, reason, and actions.
3. Add two-column waterfall-like feed on normal mobile width.
4. Move old preference form below the feed or behind "Plan tonight".
5. Add optional login prompt only after save/invite/start.
6. Add QA checklist and visual-density audit.

## Internal Meeting Conclusion

The next version should not be judged by whether it explains TodayPlay better. It should be judged by whether a cold user wants to tap a card within the first 10 seconds and can get one useful route within 2 minutes.
