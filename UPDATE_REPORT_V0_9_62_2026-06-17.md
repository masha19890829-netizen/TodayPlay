# TodayPlay V0.9.62 Art Motion Update Report

## Version

- Version name: `0.9.62`
- Version code: `80`
- APK: `dist/TodayPlay-v0.9.62-art-motion-release.apk`
- APK SHA-256: `3EFF3CB70EE0A87F39801A40D2630134AE4C59C104C30DD9EE96FD1139A3F122`
- Signing: APK Signature Scheme v2 verified

## Goal

This iteration adds a calmer art and motion layer to the chat-first TodayPlay experience. The focus is not changing the product logic, but making the first impression feel less like a plain tool and more like a quiet daily quest app with visual presence.

## Team Decisions

- Project lead: ship a testable V0.9.62 APK with art, opening animation, loading motion, result motion, and device QA.
- UI motion design: keep the original romantic minimal direction, add local WebP art assets, use restrained breathing/pulse/stamp motion instead of heavy video or flashy loading.
- Android engineering: integrate assets into the real splash, home, loading, and result paths without rewriting the chat-first product flow.
- QA device validation: focus on cold start, no crash, small phone, foldable, landscape, route generation, and result page operations.

## Shipped Changes

- Added project-local WebP artwork:
  - `app/src/main/res/drawable-nodpi/tp_art_splash_companion.webp`
  - `app/src/main/res/drawable-nodpi/tp_art_home_companion.webp`
  - `app/src/main/res/drawable-nodpi/tp_art_loading_route.webp`
- Rebuilt the app splash into a visual opening scene with companion artwork, soft route line motion, and a `TODAY / READY` stamp.
- Added a companion visual card to the chat-first home screen with subtle drift and breathing motion.
- Added loading page background art, route drawing animation, four-stage progress rail, and success stamp.
- Added result page motion polish: hero breathing image, current route pulse, timeline pulse, and animated progress.
- Improved small-screen home layout so the main generation button is visible on a 320dp-class screen.
- Improved loading page adaptive behavior with safe insets, scroll support, compact sizing, and a 2x2 stage rail on narrow/short screens.
- Improved route map hint layout so the navigation chip can wrap instead of squeezing text.
- Updated regression audit guards for V0.9.62 art/motion assets and tokens.

## Validation

- `playstore/app_regression_audit.py`: pass
- `:app:assembleDebug`: pass
- `:app:testDebugUnitTest`: pass, no unit tests found in current module
- `:app:lintDebug`: pass
- `:app:assembleRelease`: pass
- Formal Android audit:
  - Errors: `0`
  - Remaining warnings: dependency update reminders only
  - Note: no AAB produced in this iteration; this APK is for external testing, not Play submission
- APK metadata:
  - `applicationId=com.todayplay.app`
  - `versionName=0.9.62`
  - `versionCode=80`
  - `minSdk=26`
  - `targetSdk=35`
- Emulator QA:
  - Release APK install: pass
  - Cold launch: pass
  - App splash: pass
  - Home screen: pass
  - Candidate route generation: pass
  - Loading animation: pass
  - Result page: pass
  - Small phone 320dp-class home: pass
  - Foldable home: pass
  - Landscape home: pass, scroll required
  - Recent logs: no TodayPlay crash/resource markers

## Screenshots

- `dist/TodayPlay-v0.9.62-final-splash-art.png`
- `dist/TodayPlay-v0.9.62-final-home-art.png`
- `dist/TodayPlay-v0.9.62-final-loading-art.png`
- `dist/TodayPlay-v0.9.62-final-result-motion.png`
- `dist/TodayPlay-v0.9.62-final-small-home-art.png`
- `dist/TodayPlay-v0.9.62-foldable-home-art.png`
- `dist/TodayPlay-v0.9.62-landscape-home-art.png`
- `dist/TodayPlay-v0.9.62-current-stop-actions.png`

## Remaining Product / Art Direction

- Candidate route cards still rely mostly on text and mini route motifs. The next art pass should add scene thumbnails or route-ticket visuals to the generated cards.
- Home companion art is currently one general character state. A later version should add separate visual states for date, friends, and solo modes, or shift toward more universal city/ticket imagery.
- Loading stage labels and stamp are currently Chinese-only in code. Future multilingual work should move those strings into localization.
- Result page still needs a stronger completion moment, such as a stamped ticket or point-light animation after a stop is completed.
- The current APK still uses local sample POI data and AI/local fallback routing. Real external data, Play AAB, OAuth production setup, billing verification, and store submission remain out of this iteration.
