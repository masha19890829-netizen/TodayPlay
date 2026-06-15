# TodayPlay V0.9.46 Update Report - 2026-06-11

## Why This Iteration Happened

User feedback was correct: the app was still too much like a setup form. A new social / local-discovery user should not open the app and face a wall of text, language choices, or preference work.

This round focused on the first 2 minutes:

- First 5 seconds: understand that TodayPlay gives playable route ideas.
- First 20 seconds: tap a scenario chip such as recommended, date, friends, family, or solo.
- First 45 seconds: open a visual route card and choose save, start, or invite.
- First 2 minutes: reach a generated route result before asking for deeper preferences or login.

## Product And UI Changes

- Rebuilt mobile home into a feed-first discovery page.
- Added compact top bar with app name, city/idea chip, and settings icon.
- Added scenario chips before any form:
  - recommended
  - dating / crush
  - friends
  - family
  - solo
- Added two-column waterfall-like route feed on normal mobile width.
- Route cards now lead with images and route-line overlays.
- Cards show short chips for time, budget, and relationship fit.
- Cards expose immediate actions:
  - Save
  - Start
  - Invite
- Added a persistent bottom navigation surface:
  - Home
  - Saved
  - Plan
  - History
  - Settings
- Moved the older preference-heavy entry panel below the feed so it no longer blocks first impression.

## Product Review Notes

The new home follows the updated team decision:

> Give the user something worth tapping first; ask for preferences later.

This is intentionally closer to discovery products such as Instagram Explore, Reels, Tinder Explore, Bumble BFF-style mode separation, and local-life card feeds. It does not claim fake social proof, fake nearby status, fake reviews, or fake popularity.

The matching backend is still local/mock content. That is acceptable for this iteration because the target was first-session interaction, not live map/social data.

## Engineering Changes

- Updated version to `0.9.46 / versionCode 64`.
- Updated `HomeScreen.kt` with:
  - `HomeDiscoveryTopBar`
  - `HomeScenarioChips`
  - `HomeWaterfallFeed`
  - `RouteFeedCard`
  - `HomeQuickPlanStrip`
  - `HomeBottomNavBar`
  - local feed state for scenario filtering and saved route markers
- Updated `playstore/app_regression_audit.py` so the audit now checks for feed-first home structure.

## QA Result

- App regression audit: pass
- Debug APK build: pass
- Lint debug: pass
- Unit test task: pass, no unit test sources currently present
- Release APK build: pass
- APK metadata: pass, `com.todayplay.app`, `0.9.46 / 64`
- APK signature: pass, APK Signature Scheme v2
- Emulator install: pass
- Cold startup: pass
- AndroidRuntime/Fatal crash check: pass
- Home visual check: pass, screenshot shows image-led two-column route cards, scenario chips, and bottom nav
- Home primary path: pass, tapping the first card Start opens the generated route result page
- Result visual check: pass, result page shows hero image, visual route preview, and live route mode

## QA Evidence

- Home screenshot: `dist/TodayPlay-v0.9.46-home.png`
- Result screenshot: `dist/TodayPlay-v0.9.46-result.png`
- UI dump: `dist/todayplay-v0.9.46-ui.xml`

## Final APK

- APK: `dist/TodayPlay-v0.9.46-release.apk`
- Package: `com.todayplay.app`
- Version: `0.9.46`
- Version code: `64`
- Size: `8,034,330` bytes
- SHA-256: `8060A217F74E2F02A0AF99C633465FFA896F02E271D0C56139421514375D6AC9`

## Recommended Next Iteration

- Make the Saved tab real instead of routing to Quick Start.
- Add a true card detail state before route generation.
- Replace mock global cards with licensed local content or backend-fed curated packs.
- Add one optional post-value login prompt after Save / Invite instead of showing account controls on home.
- Run the same QA path on an OPPO-style physical device when available.
