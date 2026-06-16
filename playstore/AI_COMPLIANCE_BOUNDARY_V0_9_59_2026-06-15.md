# V0.9.59 AI Route Compliance Boundary

Date: 2026-06-15
Role: 发布合规负责人
Project: `D:\AppStore\nemu\real`
Scope: V0.9.59 AI 个性化路线内测版

## Release Position

V0.9.59 is an external/internal test APK for AI-assisted personalized route generation. It is not a Google Play store submission, not a production launch, and not a payment/login/map-platform release.

This round explicitly does not include:

- Real payment or subscription.
- Play Console submission.
- Real Google login.
- Real map SDK or location permission.
- Multi-model switching UI.
- Unlimited AI generation.
- Claims about real nearby popularity, real ratings, official recommendations, or social heat.

## 1. Privacy Policy And Settings Page Notices

When AI route generation is enabled, the app and future privacy policy must clearly tell users:

- The app may send the user's route request text, selected city, relationship/scenario, mood tags, time budget, money budget, mobility preference, locale, and candidate route/stop IDs to the TodayPlay AI Route Gateway.
- The AI Route Gateway may call a third-party model provider, currently planned as Kimi/Moonshot, to generate route understanding, route copy, and explanation text.
- The app should not ask users to enter sensitive personal information, identity numbers, phone numbers, exact home/work address, private health information, financial information, passwords, or emergency information in the route request box.
- Generated route content is an AI-assisted suggestion based on current inputs and local candidate route data; it is not official, real-time, guaranteed safe, or guaranteed optimal.
- The app does not request precise location permission in this round.
- The app does not send the user's live GPS location in this round.
- The app opens external maps only when the user chooses a map action.
- AI generation may fail, time out, or fall back to local route samples.
- Users should verify opening hours, transport, safety, weather, and venue suitability themselves before going out.

Settings page copy should include a short version:

```text
AI route generation may send your route request, selected city, preferences, and candidate route information to TodayPlay's AI gateway. Do not enter sensitive personal information. Current AI results are suggestions, not real-time map, ranking, safety, or venue guarantees.
```

If a privacy page is updated for V0.9.59, add:

- Data sent: route request text and selected planning preferences.
- Purpose: generate personalized route suggestions and explanation copy.
- Processor/path: TodayPlay AI Route Gateway and configured model provider.
- Retention stance: user input should not be written to permanent logs by default.
- Deletion stance: local app history remains clearable in-app; backend/account deletion must be added if persistent backend accounts are introduced later.

## 2. External Test Package Promotion Boundaries

External test copy may say:

- "V0.9.59 tests AI-assisted route understanding."
- "Say one sentence about how you want to spend today, then check what the app understood."
- "The app generates a route suggestion from your city, relationship/scenario, budget, time, and energy level."
- "AI can fall back to local sample routes if the gateway fails."
- "Please test whether the result feels meaningfully affected by your input."

External test copy must not say:

- "Official store release."
- "Production AI travel planner."
- "Real-time best route."
- "Real nearby hot spots."
- "Real ratings, rankings, or user reviews."
- "Official map data."
- "Real GPS-based recommendation."
- "Guaranteed safe route."
- "Guaranteed successful date/social outcome."
- "Payment, subscription, or membership is live."
- "Google login or cloud sync is live."
- "Kimi/API access is inside the APK."
- "Unlimited AI generation."

Required label for all V0.9.59 external-test material:

```text
This is an external test APK. AI route output is a test feature and may use a backend model gateway. It is not a store release, not a payment build, and not a real-time map or location service.
```

## 3. User Input And Model Request Copy Boundary

The one-sentence input entry should guide users toward low-risk planning preferences:

Allowed prompt examples:

- "我在上海，下班后想和刚认识的人轻松聊一聊，预算 100 内，不想太累。"
- "我在广州，三个朋友晚上想随便吃点再散步，人均 80，不想排队。"
- "我在杭州，今天有点累，想一个人慢慢走，安静一点，少花钱。"

Input placeholder should avoid inviting sensitive data:

```text
说一句今天想怎么玩，例如城市、和谁、预算、时间、想轻松还是多走一点。不要输入身份证、电话、住址、密码等敏感信息。
```

Before generation, show an understanding confirmation card:

- City.
- Relationship/scenario.
- Mood or energy level.
- Time budget.
- Money budget.
- Mobility preference.
- Route tendency.

The confirmation card should include:

```text
这些信息会用于生成路线建议。请确认没有包含敏感个人信息。
```

Model output copy must stay bounded:

- Say "why this route may fit your input", not "why this route is objectively best".
- Say "suggested route", not "official recommendation".
- Say "local sample/candidate stops", not "live POI database".
- Say "open external map", not "real-time in-app navigation".
- Say "AI suggestion may be wrong", not "verified by AI".

AI must not:

- Invent stop IDs outside the candidate list.
- Cross city boundaries when the user named a city.
- Exceed the user's budget/time constraints without marking uncertainty.
- Create risky, overly intimate, illegal, discriminatory, or manipulative tasks.
- Claim real popularity, real ratings, official certification, live queue data, or user social activity.

## 4. Security Boundary: API Key, Logs, User Input, Location

### API Key

Rules:

- Kimi API Key must never be placed in Android code, `BuildConfig`, `release_config.properties`, assets, resources, or APK-bundled files.
- Kimi API Key must live only in backend environment variables, local `.dev.vars`, or deployment secrets.
- `backend/ai-route-gateway/.dev.vars` must stay ignored by version control.
- Do not paste Kimi API Key into chat, reports, issue comments, screenshots, or logs.
- Backend logs must redact key-like values and authorization headers.

Correct path:

```text
Android App -> TodayPlay AI Route Gateway -> Kimi API
```

Incorrect path:

```text
Android App -> Kimi API
```

### Logs

Rules:

- Do not log full API keys.
- Do not log full Authorization headers.
- Do not persist full user free-text input by default.
- If debugging is needed, log request IDs, provider status, latency, fallback status, schema validation errors, and coarse categories only.
- Redact or truncate user input in diagnostics.
- Do not include raw model output in permanent logs unless a reviewed debugging mode is explicitly enabled for a limited test.

Allowed log examples:

- `provider=kimi`
- `usedFallback=true`
- `validation=missing_selectedStopIds`
- `latencyMs=1234`
- `inputLength=42`
- `city=上海`

Avoid:

- Full route request text.
- Full model prompt.
- Full model response.
- API key or bearer token.
- Exact private address or contact details.

### User Input

Rules:

- Treat route free-text as user data.
- Minimize fields sent to the AI gateway.
- Do not ask for sensitive identity, contact, financial, health, or exact private-address data.
- If the user enters sensitive data, avoid displaying it back unnecessarily.
- Keep local clear-history behavior available.
- If backend persistence is added later, add account/data deletion handling before store submission.

### Location

Rules:

- This round must not request precise or approximate location permission.
- This round must not send GPS coordinates.
- City should be user-selected or user-typed, not inferred from live device location.
- Map preview should remain abstract or based on local route data, not a real map SDK.
- External map handoff is allowed only as a user-initiated action.

## 5. V0.9.59 External Test Notice

Suggested external test description:

```text
TodayPlay V0.9.59 is an external test APK for AI-assisted route personalization.

In this build, you can type one sentence about your city, who you are going out with, budget, time, and energy level. The app will show what it understood, then generate a route suggestion and explain why it may fit your input.

Please do not enter sensitive personal information such as phone numbers, exact home/work addresses, ID numbers, passwords, financial information, or private health details.

This build may send your route request and selected planning preferences to the TodayPlay AI Route Gateway, which may call a model provider to generate the suggestion. AI results are test suggestions and may be incomplete or wrong. Please verify real-world safety, opening hours, transport, weather, and venue suitability yourself.

This is not a store release. It does not include real payment/subscription, real Google login, real map SDK, GPS location, real-time popularity, official venue ratings, UGC, or live social data.
```

Suggested tester tasks:

- Try one date/heart route prompt.
- Try one friend route prompt.
- Try one solo route prompt.
- Check whether the understanding card matches your input.
- Check whether the result changed meaningfully based on your text.
- Check whether the route stays in the stated city.
- Check whether the result respects time, budget, and energy constraints.
- Trigger fallback if network/backend is unavailable, if possible.
- Report confusing, unsafe, overclaiming, or too-generic AI explanations.

Suggested feedback fields:

- Device model.
- Android version.
- Install success.
- Did the AI input box feel clear?
- Did the app warn you not to enter sensitive info?
- Prompt used.
- Did the understanding card match your intent?
- Did the route stay in the right city?
- Did it respect budget/time/energy?
- Did "why this route fits me" feel specific?
- Did any output feel unsafe, manipulative, fake, or overconfident?
- Did fallback work when AI failed?
- Screenshots or screen recording.
- One thing to keep.
- One thing to fix before the next APK.

## 6. Store And Play Console Status

V0.9.59 must stay outside Play submission until the following are ready:

- Public HTTPS privacy policy updated for AI gateway/model-provider processing.
- Support email and privacy contact.
- Data Safety updated for user input sent to backend/model provider.
- Review notes explaining AI route generation, fallback, no location permission, no real-time map data, no payment, and no login.
- Current-build screenshots that do not imply fake real-time data, ratings, popularity, UGC, login, or payment.
- Backend logging and API key handling reviewed.
- Submission gate rerun against the exact AAB if moving to Play internal testing.

## Release Compliance Decision

V0.9.59 can proceed as an AI route external test only if:

- Kimi API Key stays backend-only.
- User input is disclosed as being sent to the AI gateway/model provider.
- Logs avoid full user input and secrets.
- The app does not request location permission or claim real-time maps.
- External copy avoids payment, login, real map, UGC, real popularity, official rating, and production-store claims.

No code, Gradle, keystore, APK, or AAB changes were made for this compliance boundary document.
