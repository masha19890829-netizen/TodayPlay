# TodayPlay V0.9.64 Adaptive AI Route Release Report

Date: 2026-06-17
Producer: Project Lead / Team Coordination

## 0. Summary

V0.9.64 is a focused external-test build.

This round does not try to redesign the whole app. It fixes the most damaging product gap from the previous AI route version: candidate cards now carry a real strategy into route generation. If the user selects "室内稳妥路线", the result page keeps that title, reasoning, and indoor-biased POI ranking instead of falling back to a generic fixed route.

The release also tightens home adaptation for small phones, foldable-like wide screens, and landscape. The APK is built, signed, installed on emulator, cold-launched, and tested through home -> candidate cards -> indoor strategy route result.

## 1. Team Execution

| Role | Work Report |
| --- | --- |
| Project Lead / Producer | Set V0.9.64 scope: make AI choices visibly different, protect foldable adaptation, produce a testable APK. |
| Chief Product Manager | Confirmed next iterations must move beyond static demos into user memory, clearer first-2-minute value, and richer route variants. |
| AI Route Logic Lead | Found the old problem: candidate-card labels were stronger than the route-ranking effect. Added strategy scoring to local fallback generation. |
| Android Engineer | Implemented adaptive home layout, reduced first-screen city choices, added indoor route candidate, and wired strategy into scoring. |
| QA Device Verification | Re-ran source audits, build checks, signed APK verification, emulator install/cold start, route generation, and screenshot evidence. |
| Visual QA | Reviewed normal phone, realistic small phone, foldable-like portrait, landscape, loading, cards, and result screenshots. |

## 2. Product Changes

### Candidate routes now change actual route logic

Added strategy-aware local ranking in `LocalItineraryGenerator`:

- `quiet`: boosts quiet cafes, libraries, indoor stops; reduces loud/night stops.
- `lively`: boosts food, public, photo, social stops.
- `budget`: boosts lower-cost stops and penalizes high-budget POIs.
- `short`: boosts shorter stays and lower movement.
- `indoor`: boosts indoor, museum, library, cafe stops and penalizes park/walk/night.
- `surprise`: boosts lane, photo, citywalk, and surprise-like categories.

### Indoor strategy added to the candidate set

Added "室内稳妥路线" as a visible candidate card:

- Title: `室内稳妥路线`
- Preview: `室内备选 / 坐下补给 / 雨天收尾`
- Tags: `室内优先 / 少走路 / 稳妥`

It is inserted into the first six candidate cards so external testers can actually see it.

### Adaptive home changes

- Home now treats `720dp+` as wide-capable, but avoids wide split layout on very short landscape windows.
- Compact-height threshold raised to `720dp`.
- Generated-state wide layout can show composer on the left and candidate results on the right.
- City chips are reduced to `上海 / 深圳 / 更多城市待验证` to avoid implying unverified cities are ready.
- Added a small principles card for wide generated layout: same-city only, selected strategy affects ranking, no fake popularity/rating/business status.

## 3. Version And APK

| Item | Value |
| --- | --- |
| Version name | `0.9.64` |
| Version code | `81` |
| APK | `dist/TodayPlay-v0.9.64-release.apk` |
| APK SHA-256 | `C3327B78149C0388F143827B36CDCED79C5FDD3FAD264D85AEEDB3708B7DE976` |
| Signing | APK Signature Scheme v2 verified |
| Signer SHA-256 | `2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695` |

## 4. Validation

Passed:

```powershell
python playstore\adaptive_ui_audit.py
python playstore\app_regression_audit.py
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug assembleRelease
apksigner verify --verbose --print-certs dist\TodayPlay-v0.9.64-release.apk
```

Emulator QA:

- Installed `dist/TodayPlay-v0.9.64-release.apk`.
- Cold launch succeeded.
- No `FATAL EXCEPTION`, `AndroidRuntime` crash, or TodayPlay process crash after launch and route generation.
- Generated candidate cards from the chat-first home.
- Confirmed "室内稳妥路线" appears in visible candidate cards.
- Selected "室内稳妥路线" and confirmed result page title and fit reasons inherit the selected strategy.
- Route generation completed in about 10.5 seconds on the emulator.

## 5. Screenshot Evidence

| Moment | File |
| --- | --- |
| Home | `dist/TodayPlay-v0.9.64-home.png` |
| Candidate summary | `dist/TodayPlay-v0.9.64-candidates.png` |
| Candidate cards | `dist/TodayPlay-v0.9.64-cards.png` |
| Indoor candidate evidence | `dist/TodayPlay-v0.9.64-cards-indoor-2.png` |
| Loading | `dist/TodayPlay-v0.9.64-loading.png` |
| Result | `dist/TodayPlay-v0.9.64-result.png` |
| Realistic 360dp small phone | `dist/TodayPlay-v0.9.64-small360dp-home.png` |
| Foldable-like portrait | `dist/TodayPlay-v0.9.64-foldable-home.png` |
| Landscape / wide | `dist/TodayPlay-v0.9.64-landscape-home.png` |
| Extreme narrow split-window warning | `dist/TodayPlay-v0.9.64-small-home.png` |

## 6. Known Issues

1. Route generation still feels too slow for a chat-first AI product. Emulator timing was about 10.5 seconds from `generate_start` to `generate_complete`. V0.9.65 should add faster staged feedback, earlier partial cards, or local-first immediate result before gateway refinement.
2. The UI is still not emotionally rich enough. It is cleaner than the old text wall, but the art direction still needs stronger opening animation, character presence, and card reveal rhythm.
3. Realistic 360dp small phone passes basic layout, but an extreme 180dp split-window width breaks the top row and truncates the home title. This should become a graceful ultra-narrow fallback, not a release blocker for normal phones.
4. The route pool is still local sample data. It is safer and now personalized by strategy, but global users need real city content and a better POI source pipeline.
5. Kimi/DeepSeek gateway value is not fully visible until more dynamic candidate generation, stronger intent parsing, and POI expansion are connected.

## 7. Next Release Direction

V0.9.65 should focus on "real AI feeling":

- Add user memory for budget, pace, relationship, liked/disliked stop types.
- Generate route differences from both intent and history, not just current chips.
- Shorten the waiting experience with streamed or staged output.
- Let the user modify one sentence after results and see card/route changes immediately.

V0.9.66 should focus on "app soul":

- Opening animation.
- Stronger character/companion visual.
- Candidate-card reveal animation.
- Route line connection animation.
- Ticket-stamp completion feedback.

V0.9.67 should focus on "content expansion":

- Bigger same-city POI candidate pool.
- City-specific route templates.
- Real source labels.
- Better fallback when gateway/API is unavailable.

