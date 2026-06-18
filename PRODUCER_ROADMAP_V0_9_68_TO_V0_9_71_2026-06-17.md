# TodayPlay Producer Roadmap V0.9.68 - V0.9.71

Date: 2026-06-17
Producer: Project Lead / Version Coordination

## 0. Producer Reflection

TodayPlay is moving in the right direction, but it still has three core gaps:

1. Personalization must feel alive.
   The user should feel that one sentence changes the route logic, not only the title. Candidate cards, stop choice, reasons, and result page must all reflect the intent.

2. The cinematic promise must become a repeatable product system.
   Time-cinema should not be a single pretty card. It needs a content taxonomy, route map, scene rhythm, source status, save/share ticket, and completion ritual.

3. The app needs more emotional feedback.
   Opening, generation, route reveal, current stop, completion, and sharing should each have a small animation moment. The motion should be calm and useful, not flashy.

## 1. Skills Used And Team Workflow

This roadmap uses the current team workflow plus the newer skill stack:

- `mobile-app-product-team-iteration`: role-based iteration and release discipline.
- `product-apk-iteration`: product-review-to-build loop.
- `formal-android-release-assistant`: APK, signing, adaptive UI, and release truthfulness.
- `imagegen`: project-local visual asset generation for original app art.

Any generated or local sample asset must remain clearly marked as project-generated or local sample content. No real film IP, stills, posters, logos, ratings, UGC, or official filming-location claims are used without verification.

## 2. V0.9.68 - Director Motion And State Retention

Goal:
Make the existing time-cinema path feel more like a living route experience and fix resize/orientation state loss.

Shipped scope:

- Preserve current screen and locale across foldable/landscape size changes.
- Add original `tp_art_time_cinema_route.png` as the time-cinema result visual.
- Add a director film strip to the result page.
- Add Shanghai/Tokyo time-cinema sample POIs.
- Add regression guards for state retention, new asset, and director strip.

Acceptance:

- Rotate or resize after entering result page should not reset to the splash screen.
- Time-cinema result page should show ticket, director strip, route map, and Act rows.
- New cinema POIs stay in same-city candidate selection.
- No fake source claims.

## 3. V0.9.69 - Personalized Route Memory

Goal:
Make different users feel they receive different plans.

Planned scope:

- Show the parsed intent as editable chips after generation.
- Add visible route strategy labels: `少走路`, `更安静`, `更便宜`, `时光电影`, `室内优先`.
- Store last 3 successful intents locally.
- Let home prompt suggest one-tap continuation from the latest intent.
- Add audit that candidate card strategy is inherited by the result page.

Acceptance:

- Five different inputs should produce visibly different candidate cards and stop order.
- Result page must explain why the selected route fits the original sentence.
- Local fallback still works without AI gateway.

## 4. V0.9.70 - Shareable Movie Ticket

Goal:
Turn completion into a reason to save and share.

Planned scope:

- Add a time-cinema share-card variant.
- Add ticket-stamp completion animation.
- Add source badge per scene: `本地样例`, `电影感灵感`, later `已核验`.
- Add a screenshot-ready ticket page with short copy and no text overflow.

Acceptance:

- After completing a stop, the ticket visibly changes.
- Share card does not claim official filming relationships.
- Small-screen share screenshot has no orphan characters.

## 5. V0.9.71 - Map And Scene Replacement

Goal:
Make route editing feel like directing today's episode.

Planned scope:

- Add `换一幕` action for each scene.
- Show replacement preview directly on the map sketch.
- Add indoor/rainy replacement mode.
- Add route map two-pane layout for foldables.

Acceptance:

- Replacing a scene should keep city, budget, and relationship constraints.
- Undo restore should keep previous stop and reward state.
- Foldable result should show map and scene list without returning to splash.

## 6. Product North Star

TodayPlay should become:

```text
Say one sentence.
Get several playable ways to spend today.
Choose one.
Play it like a small movie.
Save the ticket.
```

The app should not feel like a static route catalog. It should feel like a quiet AI producer that understands the user's mood, city, relationship, time, budget, and energy.

