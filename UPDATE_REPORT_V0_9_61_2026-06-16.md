# TodayPlay V0.9.61 Chat-First Update Report

Date: 2026-06-16
Version: 0.9.61
VersionCode: 79
APK: `dist/TodayPlay-v0.9.61-chat-first-release.apk`
SHA-256: `7C101541A935FCC91200C45A7995C76E45BC80D4F88A3BB4EBE47E74F3CB46C2`

## Producer Summary

V0.9.61 moves TodayPlay from a static route-card product toward a chat-first AI planning assistant:

1. Cold start is now centered on one question: "今天想怎么玩？"
2. User intent is structured before route generation: city, relationship, time, budget, mobility, indoor preference, goals, and avoidances.
3. The app generates multiple candidate route cards first, then freezes the selected card into a route copy.
4. Result page now supports "继续调味": 更安静、少走路、更便宜、改室内、更热闹.
5. AI and local fallback both work from a same-city candidate pool instead of fixed first-N route content.

## Team Review

- 首席产品经理: Locked the product soul as "用一句话安排今天", with first 2 minutes focused on input, understanding, candidate cards, and route copy.
- 竞品与用户动机研究员: Reframed the home page away from browsing channels and toward AI assistant behavior.
- 应用美术 UI 动效设计师: Kept the romantic minimal interface but reduced the first screen to a console-like input, chips, and one primary action.
- AI 路线逻辑负责人: Added structured intent parsing, candidate route cards, same-city candidate POI pool, AI gateway boundary, and local fallback scoring.
- Android 开发工程师: Implemented the chat-first home, route candidate generation, result-page tuning, route-title inheritance, and version bump.
- QA 设备验证负责人: Tested cold start, candidate generation, route result, route tuning, release signing, and wide/landscape adaptation.

## Main Changes

- Added `RouteIntentInterpreter` for chat-first intent parsing and route-card generation.
- Added `ChatFirstPoiMockData` for clean local POI samples in Shanghai and Shenzhen.
- Updated `LocalItineraryGenerator` to rank candidates by user input and keep same-city boundaries.
- Updated `AiQuestGenerator` to send 8-12 same-city candidate stops to the AI gateway and fallback cleanly.
- Replaced the home entry with `ChatFirstHomeExperience`.
- Added result-page tuning chips through `RouteTuneCard`.
- Ensured selected card title and summary are inherited by the route copy, including AI gateway and fallback cases.
- Updated regression audit guards for chat-first, candidate pool generation, and route tuning.

## QA Evidence

- `playstore/app_regression_audit.py`: pass.
- `assembleDebug`: pass.
- `testDebugUnitTest`: pass, no source tests present.
- `lintDebug`: pass with 5 dependency-version warnings.
- `assembleRelease`: pass.
- APK metadata: `versionName=0.9.61`, `versionCode=79`.
- APK signing: v2 signature verified.
- Emulator cold start: pass.
- Home not a text wall: pass.
- Candidate cards visible after generation: pass.
- Selected card freezes into result page: pass.
- "更安静" tuning creates a new route direction: pass.
- Foldable/wide and landscape screenshots: no obvious overflow or mojibake.
- Recent logs: no `FATAL EXCEPTION` or `AndroidRuntime` crash.

## Screenshot Artifacts

- `dist/TodayPlay-v0.9.61-chat-first-home.png`
- `dist/TodayPlay-v0.9.61-chat-first-candidates.png`
- `dist/TodayPlay-v0.9.61-chat-first-candidates-scrolled.png`
- `dist/TodayPlay-v0.9.61-chat-first-result.png`
- `dist/TodayPlay-v0.9.61-chat-first-tuned-quiet.png`
- `dist/TodayPlay-v0.9.61-chat-first-foldable-home.png`
- `dist/TodayPlay-v0.9.61-chat-first-landscape-home.png`

## Known Limits

- This is still an external-test APK, not a Play submission AAB.
- POI data remains local sample data and must be verified before public launch.
- Kimi API key is not embedded in Android; Android only talks to the route gateway.
- Real Google login, payment verification, production map data, and store listing are outside this build.
- AI gateway response time can still feel long on tuning; next version should add a visible timeout/fallback threshold closer to 8-10 seconds.
