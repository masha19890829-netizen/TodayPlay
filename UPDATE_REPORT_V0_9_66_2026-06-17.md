# TodayPlay V0.9.66 Time-Cinema Visual Map Release Report

Date: 2026-06-17
Producer: Project Lead / Team Coordination

## 0. Summary

V0.9.66 follows the approved design direction: TodayPlay should feel like a quiet, cinematic daily route app, not a text-heavy itinerary tool.

This release turns the new “时光电影” idea into a real APK path:

- Home has a visible `时光电影` chip.
- AI intent recognizes movie / scene / filming-location style needs.
- Result page adds a real “今日电影票” block.
- Result page includes an in-app route map sketch.
- The route is shown as Act 01 / Act 02 / Act 03.
- Source wording remains honest: current data is local sample / cinematic inspiration, not official film IP or verified filming claims.

## 1. Team Execution

| Role | Work Report |
| --- | --- |
| 项目负责人 / 版本统筹 | Set V0.9.66 scope: follow the approved concept board, add ticket + map + three-act result UI, and deliver a signed APK. |
| 首席产品经理 | Defined acceptance: user must input one cinematic need, choose a movie-ticket route, see a route map, finish the current scene, and want to save the ticket. |
| 应用美术 UI 动效设计师 | Specified the result page as “movie ticket + calm map + three scene cards”, with later foldable two-pane and ticket-stamp motion. |
| AI 路线逻辑与内容可信负责人 | Locked content boundary: current route is “电影感灵感 / 本地样例”; no fake filming locations, no movie stills, no official claims. |
| Android 开发工程师 | Implemented result page ticket/map/scene components and wired time-cinema detection into the real route result path. |
| QA 设备验证负责人 | Built, installed, cold-launched, generated a time-cinema route, captured screenshots, checked logs, and recorded adaptation issues. |

## 2. Product Changes

### Time-cinema home entry

- Changed the quick chip from `电影感` to `时光电影`.
- Added `时光电影` to the route intent recognizer.

### Ticket-style result page

Added `TimeCinemaTicketCard` to the result screen when the route is detected as a time-cinema route.

The card includes:

- `TODAY WAS PLAYED / 今日电影票`
- City / duration / compact budget / source status
- Clapper-board prompt
- Direct action: `导航当前镜头`
- Route map sketch
- Act 01 / Act 02 / Act 03 scene rows
- Source warning about local samples and unverified cinematic places

### In-app route map

Added `TimeCinemaRouteMap`, a local route sketch map:

- Low-noise paper map background.
- Three to four route nodes.
- Current stop pulse.
- Soft route line.
- City and best photo time labels.

This is an execution preview, not an official map-data claim.

## 3. Version And APK

| Item | Value |
| --- | --- |
| Version name | `0.9.66` |
| Version code | `83` |
| APK | `dist/TodayPlay-v0.9.66-release.apk` |
| APK SHA-256 | `42AF199EB740AD6E96219314101F241060B1D21217402D2D771FA512F309771A` |
| Signing | APK Signature Scheme v2 verified |
| Signer SHA-256 | `2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695` |

## 4. Validation

Passed:

```powershell
python playstore\app_regression_audit.py
python playstore\adaptive_ui_audit.py
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug assembleRelease
apksigner verify --verbose --print-certs dist\TodayPlay-v0.9.66-release.apk
```

Emulator QA:

- Installed `dist\TodayPlay-v0.9.66-release.apk`.
- Cold launch succeeded.
- Home shows `时光电影` chip in the first-screen path.
- Generated time-cinema route from home.
- Candidate understanding recognized `电影感打卡`.
- Result page showed ticket, clapper board, map route, and Act rows.
- No `FATAL EXCEPTION`, `AndroidRuntime`, or TodayPlay crash markers in recent logs.
- Generation timing in final run: about 10.5 seconds from `generate_start` to `generate_complete`.

## 5. Screenshot Evidence

| Moment | File |
| --- | --- |
| Final home | `dist/TodayPlay-v0.9.66-final-home.png` |
| Time-cinema selected and understood | `dist/TodayPlay-v0.9.66-final-candidates.png` |
| Candidate cards | `dist/TodayPlay-v0.9.66-final-cards.png` |
| Loading | `dist/TodayPlay-v0.9.66-final-loading.png` |
| Final result ticket/map | `dist/TodayPlay-v0.9.66-final-result.png` |
| Logcat | `dist/TodayPlay-v0.9.66-final-logcat.txt` |
| Small-screen resize capture | `dist/TodayPlay-v0.9.66-small-result.png` |
| Foldable resize capture | `dist/TodayPlay-v0.9.66-foldable-result.png` |
| Landscape resize capture | `dist/TodayPlay-v0.9.66-landscape-result.png` |

## 6. Known Issues

1. Rotating or resizing the emulator during QA can rebuild the activity back to the opening animation instead of preserving the current result page. This is not a crash, but it is a real adaptation state-retention issue.
2. The first candidate is still titled `电影感打卡局`; the dedicated `时光电影路线` candidate exists in the route logic but should be promoted or renamed so the path feels more intentional.
3. The route map is a local visual sketch, not a true map SDK preview. It helps first-screen understanding, but external map navigation still opens separately.
4. Movie-location content is still local sample / cinematic inspiration. Verified filming-location data requires a sourced content table and review workflow.
5. Generation still takes around 10 seconds on the emulator. The next version should show staged partial output sooner.

## 7. Next Release Direction

V0.9.67 should focus on:

- Preserve current route page across foldable/landscape resize.
- Promote “时光电影路线” as the primary movie-mode candidate.
- Add a saved/shareable “今日电影票” card.
- Add source badges per Scene: `电影感灵感`, `本地样例`, later `已核验`.
- Make wide/foldable result page two-pane: ticket + map on the left, Act rows on the right.

