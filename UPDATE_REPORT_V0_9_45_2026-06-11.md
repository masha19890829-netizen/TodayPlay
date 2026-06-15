# TodayPlay V0.9.45 Update Report - 2026-06-11

## Why This Iteration Happened

User feedback was correct: the app still felt too text-heavy, and putting language selection on the home screen made the first-run flow feel like setup rather than discovery.

The product rule after this round is simple:

- Home is for recommendations, images, quick decisions, and route generation.
- Settings is for language, privacy, support, and local data controls.
- Result pages should show a route preview and next action before long explanations.

## Competitor Research Notes

- Wanderlog positions itinerary and map together as a core planning value, and emphasizes attraction recommendations and map-assisted planning.
- Tripsy focuses on keeping activities, routes, reservations, sharing, and trip details organized in one place.
- Cobble focuses on reducing "what do you want to do?" friction through options, filters, guides, and quick save/share behavior.

Takeaway for TodayPlay: the first screen should not ask users to configure the app. It should give them appealing routes or ideas quickly, then let settings live behind a settings entry.

Sources:

- https://wanderlog.com/
- https://tripsy.app/
- https://www.trycobble.com/

## Product Changes

- Removed the full language selector from the home page.
- Renamed the old Privacy entry to Settings.
- Added language selection to Settings & Privacy.
- Kept the V0.9.44 visual-first home flow:
  - Today Picks appear before manual configuration.
  - Recommendation cards include image headers.
  - Recommendation cards include route-map style previews.
- Kept the result-page route preview:
  - Hero image first.
  - Visual route preview second.
  - Current navigation action visible before long detail cards.

## Engineering Changes

- Updated version to `0.9.45 / versionCode 63`.
- Updated `HomeScreen` so it no longer owns language selection.
- Updated `PrivacyScreen` into a Settings & Privacy surface with language controls.
- Updated `PrivacyCopy` for localized Settings labels.
- Updated `app_regression_audit.py` so language selection cannot regress back into the home flow.

## QA Result

- Build: pass
- Lint: pass
- Release APK build: pass
- Unit test task: pass, no unit test sources currently present
- App regression audit: pass
- Emulator install: pass
- Cold startup: pass, no AndroidRuntime crash
- Home first screen: pass, Today Picks visible and no language selector present
- Settings page: pass, Settings & Privacy visible and language selector present
- Language switch: pass, English setting updates the settings page copy
- Release APK signature: pass, APK Signature Scheme v2

## Final APK

- APK: `dist/TodayPlay-v0.9.45-release.apk`
- Package: `com.todayplay.app`
- Version: `0.9.45`
- Version code: `63`
- SHA-256: `B3F535524DD9B8F50E9AB7C699823E0A2C384401926FE0CB2FF24C96230BBB6A`

## Remaining Product Direction

The next PM/market research task should go deeper on the real discovery loop:

- city/category filters on the recommendation feed
- fewer manual form fields
- visible saved/favorite route packs
- stronger map preview with real provider data once licensed APIs are available
- bottom navigation or persistent tabs if the app grows beyond the current prototype structure
