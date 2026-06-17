# TodayPlay V0.9.67 Typography And Adaptive Detail Hotfix Report

Date: 2026-06-17
Producer: Project Lead / Team Coordination

## 0. Summary

V0.9.67 is a focused hotfix for the first-screen typography issue reported in review: the splash subtitle could wrap into a single orphan character, such as `影。`, on narrow or foldable layouts.

This was treated as a first-impression trust issue, not cosmetic noise. The fix also covers two related home-screen polish problems found during QA:

- Splash subtitle now uses a balanced semantic line break.
- The home companion intro copy is shorter and no longer leaves a short tail line.
- Compact home hides the decorative route sketch when it would overlap copy.
- Compact home uses a shorter input example.
- Compact home shows fewer, higher-value chips so the primary action remains visible.
- Regression audit now guards against reverting these details.

## 1. Team Review

| Role | Work Report |
| --- | --- |
| Project Lead / Version Producer | Classified the issue as a V0.9.67 hotfix because external testers need an unambiguous APK version. |
| Chief Product Manager | Confirmed the orphan character damages download intent because TodayPlay's movie-like promise depends on precise first-screen rhythm. |
| UI Motion / Visual Designer | Defined the rule: Chinese key copy must break by phrase, not by automatic character spill; splash subtitles should stay within 2 balanced lines. |
| QA Device Validation Owner | Added acceptance language for 320dp, normal phone, foldable, and landscape checks: no single-character line, no overlapping text, no mojibake. |
| Android Experience Engineer | Implemented the balanced splash copy, compact home copy, compact chip set, and overlap fix. |

## 2. Product And UI Changes

### Splash subtitle

Old behavior:

```text
把普通日子，剪成一小段只属于你们的电
影。
```

New behavior:

```text
把普通日子，剪成
只属于你们的一小段电影。
```

Traditional Chinese uses the same balanced strategy.

### Compact home

Changed the companion intro copy:

```text
说一句状态，路线会浮现。
```

Changed the input example:

```text
比如：今晚两个人，少走路
```

For compact screens, the quick chip set is reduced to:

```text
约会 / 朋友 / 独处 / 低预算 / 少走路 / 时光电影
```

The decorative mini route line is hidden on compact home cards so it cannot overlap text.

## 3. Version And APK

| Item | Value |
| --- | --- |
| Version name | `0.9.67` |
| Version code | `84` |
| APK | `dist/TodayPlay-v0.9.67-release.apk` |
| APK SHA-256 | `7E99E7AB6C7546F4AB2341C80212215C99F3782079ED12B824C3515718F061C4` |
| Signing | APK Signature Scheme v2 verified |
| Signer SHA-256 | `2b036b017099397aabc4aaf0ae1fe8fc9456dd18a07090d04a0ce596e999b695` |

## 4. Validation

Passed:

```powershell
python playstore\app_regression_audit.py
python playstore\adaptive_ui_audit.py
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug assembleRelease
apksigner verify --verbose --print-certs dist\TodayPlay-v0.9.67-release.apk
```

Installed on emulator:

```text
versionCode=84
versionName=0.9.67
```

Log check:

- No `FATAL EXCEPTION`.
- No `AndroidRuntime` crash marker.

## 5. Screenshot Evidence

| Moment | File |
| --- | --- |
| Normal home | `dist/TodayPlay-v0.9.67-home-final.png` |
| Small home final | `dist/TodayPlay-v0.9.67-home-small-final2.png` |
| Small splash / transition capture | `dist/TodayPlay-v0.9.67-splash-small-final2.png` |
| Foldable splash final | `dist/TodayPlay-v0.9.67-splash-foldable-final2.png` |
| Landscape splash final | `dist/TodayPlay-v0.9.67-home-landscape-final.png` |

QA result:

- Splash subtitle no longer has `影。` or other single-character orphan lines.
- Home companion card no longer has decorative dots overlapping text.
- Small home input example no longer leaves a single-character `路` line.
- Primary CTA is fully visible on the small-screen home capture.

## 6. Regression Guard Added

`playstore/app_regression_audit.py` now includes:

- `Splash tagline avoids orphan Chinese characters`
- `Home intro copy avoids short orphan tails`

These checks guard:

- Balanced Chinese splash line break.
- Maximum two-line splash copy.
- Short compact home helper copy.
- Compact chip reduction.
- No compact decorative route overlay.

## 7. Remaining Design QA Notes

1. Foldable and resize can still show the splash transition during relaunch. It is not a crash, but V0.9.68 should preserve screen state more cleanly across size changes.
2. Long localized text still needs a broader multi-language visual QA pass before public store screenshots.
3. Future movie-ticket/share-card screens should adopt the same orphan-line rule for all Chinese key copy.

