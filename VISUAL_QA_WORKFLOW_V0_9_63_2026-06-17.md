# TodayPlay Visual QA Workflow V0.9.63

Date: 2026-06-17
Owner role: Visual aesthetic QA and device adaptation
Scope: TodayPlay Android release candidates, Google Play screenshot readiness, and adaptive visual evidence in `dist/`.

This workflow is a release gate. It does not judge whether the app idea is good; it judges whether the current build looks intentional, usable, and truthful on the devices and screenshots that matter before external testing or store submission.

## Output Artifacts

Every visual QA pass must leave evidence in `dist/` with the current version in the filename, for example `TodayPlay-v0.9.63-final-home-art.png`.

Required evidence:

- 6 fixed aesthetic screenshots.
- Normal phone portrait evidence.
- Small phone evidence.
- Foldable evidence.
- Phone landscape evidence.
- Matching window XML evidence when available, especially for layout overlap diagnosis.
- A short QA note or update report when any screenshot is rejected.

## Six Fixed Aesthetic Screenshots

These six screenshots are stable review anchors. They should be captured for every release candidate, even when the UI change seems small.

| ID | Filename template | Screen state | What it must prove |
| --- | --- | --- | --- |
| 01 | `TodayPlay-v{version}-final-splash-art.png` | Launch / first visual impression | Brand atmosphere loads cleanly, no black frame, no debug UI, no awkward crop. |
| 02 | `TodayPlay-v{version}-final-home-art.png` | Chat-first home | The input-first promise is obvious within 3 seconds, primary action is reachable, and the page feels like the real product. |
| 03 | `TodayPlay-v{version}-candidate-cards-art.png` | AI candidate plan cards | AI output becomes selectable cards, hierarchy is clear, and cards do not look like static demo placeholders. |
| 04 | `TodayPlay-v{version}-final-loading-art.png` | AI thinking / route generation | Loading state has intentional motion or visual feedback, not a frozen blank page or fake spinner-only demo. |
| 05 | `TodayPlay-v{version}-final-result-motion.png` | Generated route result | Result screen shows a complete route or plan with clear next action, real-feeling context, and readable cards. |
| 06 | `TodayPlay-v{version}-current-stop-actions.png` | Current stop / action state | The user can understand what to do now: navigate, check in, continue, save, or adjust. |

Acceptance for all six:

- PNG opens successfully and reports non-zero dimensions.
- The app content fills the intended viewport; no accidental desktop background, emulator launcher, notification shade, crash dialog, or keyboard-only capture.
- Text is readable at normal phone viewing distance.
- No obvious overlap, clipping, broken icon, missing image, blank card, repeated placeholder, or system error toast.
- The screenshot communicates a specific product promise rather than generic app chrome.

## Device Acceptance Matrix

Capture real device or emulator evidence whenever possible. Generated mockups are allowed only for copy/layout rehearsal and must not be used as final release proof.

| Device class | Minimum evidence filename | Target viewport | Acceptance standard |
| --- | --- | --- | --- |
| Normal phone portrait | `TodayPlay-v{version}-final-home-art.png` | 360x800 dp class, portrait | Home content is centered, input is reachable, cards fit without horizontal scroll, and bottom actions do not cover content. |
| Small phone / narrow cover | `TodayPlay-v{version}-final-small-home-art.png` | 320x640 dp class or equivalent narrow screenshot | No clipped button text, no overlapping title/card/input, no unreachable primary CTA, and long Chinese/English mixed copy wraps safely. |
| Foldable inner / wide | `TodayPlay-v{version}-foldable-home-art.png` | 673+ dp width class or foldable inner display | Layout uses sensible max width or grouped panes; content is not stretched into long unreadable lines; card grouping remains visually coherent. |
| Phone landscape | `TodayPlay-v{version}-landscape-home-art.png` | Landscape phone | Primary input and key content remain visible; hero/art does not consume all height; there is no clipped bottom navigation or hidden CTA. |

Optional but recommended:

- `TodayPlay-v{version}-foldable-result-art.png` for wide result layout.
- `TodayPlay-v{version}-small-result-art.png` for narrow route cards.
- Dark mode screenshot if dark mode is supported.
- One long-copy screenshot with a long city name, long route title, and mixed Chinese/English copy.

## Scoring Rubric

Score out of 100. A release candidate passes visual QA only when it reaches at least 90 and has no blocking failure.

| Dimension | Points | Review question |
| --- | ---: | --- |
| First impression and brand craft | 15 | Does the opening visual feel deliberate, polished, and immediately recognizable as TodayPlay? |
| Chat-first clarity | 15 | Does the user instantly understand that they can ask for a plan in natural language? |
| Card hierarchy and scan speed | 15 | Are plan cards, stop cards, and action cards easy to compare without reading every line? |
| Device adaptation | 15 | Do normal phone, small phone, foldable, and landscape screenshots all keep the core workflow usable? |
| Typography and copy fit | 10 | Are Chinese, English, numbers, prices, and long labels readable without clipping or awkward compression? |
| Motion and state continuity | 10 | Do loading and result states feel connected, responsive, and alive rather than fake or frozen? |
| Product truthfulness | 10 | Does the UI avoid implying real maps, real social heat, paid entitlements, or live AI behavior that is not actually implemented? |
| Store screenshot readiness | 10 | Would the screenshot make sense to a new user in the store without looking staged, noisy, or misleading? |

Score bands:

- 90-100: Pass for external testing or store screenshot candidate.
- 80-89: Needs visual polish before store use; external testing allowed only if no blocker exists.
- 70-79: Product intent is visible but screenshots are not release-ready.
- Below 70: Fails visual QA; fix layout, hierarchy, or truthfulness before more capture work.

## Blocking Failure Conditions

Any of these fail the visual QA gate regardless of score:

- Missing any of the six fixed screenshots.
- Missing small-screen, foldable, or landscape evidence.
- PNG is corrupt, has zero dimensions, or cannot be parsed as PNG.
- Screenshot shows launcher, crash dialog, permission dialog, notification shade, debug overlay, raw emulator shell, or unrelated app.
- Primary action is hidden, clipped, or covered by navigation/keyboard/bottom bars.
- Text overlaps, is cut off, or becomes unreadable in any required device class.
- Cards resize unpredictably between states, causing visible layout jumps in captured evidence.
- AI output looks like a fixed demo: repeated template cards, placeholder text, impossible route, or no way to refine/continue.
- The screenshot claims real data, official map intelligence, live social heat, paid access, or backend verification that is not actually available.
- Foldable or landscape view merely stretches a phone column into an awkward wide canvas without max-width control.
- Small-screen view requires horizontal scrolling for normal copy or hides the input/CTA below unreachable content.

## Manual Review Checklist

For every screenshot, answer yes/no:

- Is this the intended app screen and version?
- Is the most important action visible without guessing?
- Is the visual focus clear within 3 seconds?
- Does the screenshot avoid misleading claims?
- Are spacing, card rhythm, and typography balanced?
- Are all interactive-looking controls visually enabled only when they can actually work?
- Would a user believe this is a real app state, not a prototype slide?

## Automated Audit

Run the companion script after capture:

```powershell
python playstore\visual_qa_audit.py .
```

Optional arguments:

```powershell
python playstore\visual_qa_audit.py . --version v0.9.63
python playstore\visual_qa_audit.py . --dist dist --json
```

The automated script checks only objective evidence:

- Required screenshot files are present for the selected version.
- PNG dimensions are valid.
- Small-screen evidence exists.
- Foldable evidence exists.
- Landscape evidence exists and is actually wider than tall.

The script does not replace human aesthetic scoring. It is the guardrail that catches missing or invalid evidence before manual review begins.

## Release Decision

Use this decision order:

1. Run automated audit.
2. If automation fails, fix capture or naming first.
3. Score the six fixed screenshots manually.
4. Review device evidence against the acceptance matrix.
5. Mark the release candidate as:
   - `visual-pass`
   - `visual-pass-with-notes`
   - `visual-fail`

Only `visual-pass` screenshots should be considered final store candidates.
