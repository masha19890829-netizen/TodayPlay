# TodayPlay Social Home Design Review - 2026-06-11

## Meeting Decision

The current app is still too form-first and text-heavy. A social / local-discovery app should not open with setup, language, or long explanatory copy. It should open with things worth tapping.

Decision: V0.9.46 should be a social-discovery homepage redesign, not another small copy patch.

## Roles In The Review

| Role | Responsibility | Decision |
| --- | --- | --- |
| 产品策划 & 市场调研 | Study social, dating, local-life, and discovery apps | Move from form-first to feed-first. |
| 应用美术 UI 动效设计师 | Define visual interaction and first-screen mood | Design a photo-led waterfall/card feed with lightweight motion. |
| 产品经理 & 测试负责人 | Define acceptance criteria | User should see attractive ideas in 3 seconds without reading instructions. |
| Android 开发工程师 | Judge implementation path | Build a Compose two-column waterfall-like feed from local route cards first. |
| QA | Validate on emulator and OPPO-style mobile viewport | Check first screen density, text overflow, tap targets, and scroll feel. |

## Competitor Learning

### Xiaohongshu / 小红书

Observed pattern: interest/community discovery first. Its app listing describes discovery, real user sharing, interest communities, and finding content that makes users feel "眼前一亮".

What to learn:

- Home should be a content stream, not a setup form.
- Cards should be visual, short, and emotionally specific.
- Interaction should invite save, like, comment, and share behavior.

Risk to avoid:

- Do not imply real community heat or user-generated content until backend/social data exists.
- Use mock/local cards clearly as curated route ideas.

Source: https://play.google.com/store/apps/details?hl=zh&id=com.xingin.xhs

### Instagram Explore / Reels

Observed pattern: discovery surfaces show recommendations such as photos and reels. Recent product direction also increases user control over feed topics.

What to learn:

- The first visible unit should be media-led.
- Feed filters should feel like interest controls, not a long questionnaire.
- Saved/favorites should become a primary loop.

Risk to avoid:

- Do not bury the main content under headers and prose.

Sources:

- https://about.instagram.com/features
- https://help.instagram.com/487224561296752
- https://www.theverge.com/tech/947898/meta-instagram-your-algorithm-main-feed-tell

### Douyin / TikTok Style Feed

Observed pattern: opens around abundant content and creation, not configuration. The App Store listing emphasizes large content supply and simple creation tools.

What to learn:

- Immediate content beats explanation.
- Motion and visual rhythm matter more than labels.
- "One more card" scrolling should be effortless.

Risk to avoid:

- TodayPlay is not a video app; copying full-screen video would be wrong. Borrow the "content first" principle, not the format.

Source: https://apps.apple.com/mo/app/%E6%8A%96%E9%9F%B3-douyin/id1142110895

### Tinder / Bumble / Hinge

Observed pattern: dating apps reduce choice into fast cards, modes, prompts, and compact actions.

What to learn:

- Tinder Explore uses categories and interest-based discovery.
- Bumble separates modes such as Date, BFF, and Bizz; mode choice happens at a high level, not through long forms.
- Hinge uses short prompts, photos, voice, video, and polls to make profiles feel human.

Risk to avoid:

- Swipe mechanics alone are shallow. TodayPlay needs "save route / invite friend / start now" actions, not just like/dislike.

Sources:

- https://tinder.com/
- https://techcrunch.com/2021/09/09/tinder-adds-a-new-home-for-interactive-social-features-with-launch-of-tinder-explore/
- https://bumble.com/en-us/
- https://bumble.com/en-us/the-buzz/what-exactly-is-bumble-bff
- https://hinge.co/newsroom/prompt-feedback

### Dianping / 大众点评

Observed pattern: local-life discovery is organized by categories, real reviews/photos, nearby places, deals, and travel/leisure use cases.

What to learn:

- TodayPlay needs category chips and concrete local-life cards.
- Cards should show image, place/route type, rough time, budget, social fit, and action.
- Map/detail comes after interest is created.

Risk to avoid:

- Do not show fake ratings, fake user reviews, or fake "hot" labels.

Source: https://apps.apple.com/cn/app/%E5%A4%A7%E4%BC%97%E7%82%B9%E8%AF%84-%E5%90%83%E5%96%9D%E7%8E%A9%E4%B9%90%E6%8C%87%E5%8D%97/id351091731

## New Product Principle

Old:

> Configure relationship, language, mood, city, budget, then generate a route.

New:

> Open app, see attractive social route cards, tap one, save/share/start, then optionally refine.

## Proposed V0.9.46 First Screen

1. Top app bar
   - Small logo / TodayPlay
   - City selector chip
   - Settings icon

2. Interest tabs
   - 推荐
   - 约会
   - 朋友
   - 一个人
   - 亲子
   - 雨天
   - 低预算

3. Waterfall feed
   - Two columns on mobile where safe, one column on very narrow screens.
   - Card image first.
   - 1-line title.
   - 2-3 chips: time, budget, relationship, mood.
   - One short reason line max.
   - Actions: Save, Start, Invite.

4. Floating action
   - "帮我安排今晚" as the generation shortcut.
   - Opens a lightweight bottom sheet, not a full form page.

5. Bottom navigation
   - 首页
   - 收藏
   - 生成
   - 历史
   - 设置

## Onboarding Redesign

Do not start with language, long value proposition, or empty form.

V0.9.46 onboarding should be:

1. Optional 2-card intro only on first install.
   - Card 1: "刷到今晚可以直接玩的路线"
   - Card 2: "保存、邀请朋友、打开地图"

2. One screen of soft preferences.
   - "今晚和谁？" with 4 visual buttons.
   - "想要什么感觉？" with chips.
   - Skip button always visible.

3. Land on feed immediately.

## Visual Direction

Move away from large serif text blocks and paper-note density.

Use:

- photo-led cards
- compact labels
- bolder route thumbnails
- bottom nav
- light motion on card entry
- heart/save/share icons
- map-preview chips
- fewer paragraphs

Keep:

- warm, romantic route mood
- clear safety/privacy boundary
- no fake social proof
- no unlicensed third-party images

Avoid:

- language selector on home
- long paragraphs above content
- card inside card feeling
- settings hidden under privacy wording
- fake "hot", "nearby", "review" data

## Acceptance Criteria For V0.9.46

- First screen shows at least 2 visual route cards without scrolling on a Pixel 6 size emulator.
- No full language selector on home.
- No paragraph longer than 2 lines on home cards.
- Waterfall or staggered feed feeling is visible.
- User can start a route directly from a feed card.
- Settings remains available but not dominant.
- Emulator smoke test covers startup, home feed, feed-card route generation, settings, and system back.
- App regression audit includes a guard for feed-first home.

## Implementation Plan

1. Build local route feed data from existing mock content.
2. Add a `RouteFeedCard` component with image, chips, and direct start action.
3. Replace current mobile home ordering with a feed-first layout.
4. Move old relation setup into a bottom-sheet-like secondary section or lower page.
5. Add bottom navigation scaffolding if feasible in one iteration.
6. Rebuild as V0.9.46 and test on emulator.

## Manager Decision

The team should stop polishing the current form-like homepage. The next APK should be judged by whether it feels like a social discovery product in the first 3 seconds.
