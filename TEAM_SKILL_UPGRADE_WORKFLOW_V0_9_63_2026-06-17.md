# TodayPlay V0.9.63 Team Skill Upgrade Workflow

Date: 2026-06-17

## Purpose

TodayPlay needs a repeatable design-upgrade workflow, not occasional style changes. V0.9.63 establishes the team roles, skill intake policy, visual QA gates, and handoff rules that every later UI, motion, art, screenshot, and Android adaptation pass must follow.

This version is a capability-building release. It does not ship a new APK by itself.

## Team Roles

| Role name | Owner responsibility | Required output |
| --- | --- | --- |
| Project Lead / Producer | Sets version goal, assigns roles, blocks scope drift, approves release evidence | Update report, known issues, next-version plan |
| Skill Research and Tool Admin | Searches official and third-party skills, checks source trust, records install decisions | `GITHUB_SKILL_RESEARCH_*` |
| Chief Product Manager | Keeps the core promise: one sentence arranges today | Product route, first-2-minute path, acceptance criteria |
| Competitive Research and User Motivation | Compares AI itinerary, map, social-decision, and lifestyle apps | Competitor teardown and borrow/avoid list |
| Design System Lead | Converts TodayPlay's art direction into reusable rules and tokens | `DESIGN_SYSTEM_RULES_*` |
| UI and Motion Art Director | Designs splash, chat-first home, candidate cards, loading, result, and completion feedback as a set | Motion guide, asset checklist, screen-by-screen art brief |
| UX Writing Editor | Compresses copy, removes text walls, defines prompt and fallback voice | Copy rules, examples, error and fallback messages |
| Android Engineer | Implements only after design rules and screenshot paths are accepted | Code changes, build evidence, APK |
| Visual QA and Device QA | Scores screenshots, checks small phone, regular phone, foldable, and landscape | Visual QA table, screenshot paths, unresolved risks |
| Release and Compliance | Verifies versioning, signing, privacy, billing, account/login boundaries | Release checklist and signing evidence |

## Skill Intake Policy

1. Official OpenAI skills may be installed when their purpose matches TodayPlay's workflow and the installer can verify source files from `openai/skills`.
2. Third-party skills are read-only references until the Skill Research Admin records source URL, license, maintenance state, prompt-injection risk, and whether scripts execute remote code.
3. No skill is allowed to install or run opaque remote scripts during TodayPlay production work.
4. Every installed or rejected skill must appear in the version skill research report with one of these decisions: `adopt now`, `read-only reference`, `hold`, or `reject`.
5. If a skill changes UI code, it must still pass TodayPlay's local design system, screenshot QA, and Android release audits.

## Version Workflow

1. Product defines the user promise and first-2-minute behavior.
2. Research compares competitor flows and identifies reusable patterns and traps to avoid.
3. Design System Lead updates rules before UI work begins.
4. UI and Motion Art Director produces screen and motion specs as a set, never isolated images.
5. UX Writing Editor trims text before Android implementation starts.
6. Android Engineer implements the approved slice.
7. Visual QA captures six required screenshots across the device matrix.
8. QA runs automated audits and records every unresolved issue.
9. Producer reviews evidence, then either approves the APK or opens a fix list.

## Required Screenshot Set

Every visual release must provide these six moments:

| Moment | Purpose |
| --- | --- |
| Splash / opening | Shows tone, brand, and first emotional impression |
| Chat-first home | Proves the app is centered on one-sentence planning |
| Candidate cards | Shows AI-generated options, not fixed identical content |
| Generation / loading | Shows useful, calm progress instead of generic spinning |
| Route result | Shows selected route, reason, map/stop summary, and edit actions |
| Current stop / completion | Shows in-use value and completion feedback |

The device evidence set must include small phone, regular phone, foldable portrait or outer screen, and landscape/wide screen.

## Visual QA Scorecard

Each required screenshot is scored from 1 to 5:

| Dimension | Passing target |
| --- | --- |
| Premium calm | At least 4; no cheap tech gradients, heavy shadows, or template-like visuals |
| Whitespace and hierarchy | At least 4; first glance should not feel crowded |
| Text density | At least 4; only short, useful copy on the first screen |
| Motion necessity | At least 3; animation must explain state or create emotional polish |
| Store appeal | At least 4; a user should understand why they might download from the screenshot alone |
| Device fit | 5 required; no clipped titles, overflow, gibberish, or unsafe inset collisions |

Any `Device fit` failure blocks the APK for external testing.

## Motion Rules

Motion must be calm, short, and purposeful:

| Motion | Target duration | Rule |
| --- | --- | --- |
| Splash entrance | 900-1800 ms | Brand mood only; must be skippable by normal app flow |
| Prompt focus | 180-280 ms | Subtle lift or glow, no bouncing |
| AI understanding | 1200-2200 ms | Show structured progress: understand, select, connect |
| Candidate cards | 260-420 ms stagger | Cards appear as curated options, not random tiles |
| Route line connection | 500-900 ms | Clarify stop order and same-city logic |
| Ticket stamp / completion | 300-600 ms | Reward moment without blocking navigation |

Reduced-motion users should still get clear state changes without decorative animation.

## Blocking Rules

The Producer must not approve a version when any of these are true:

- Homepage returns to a static channel wall or text-heavy introduction.
- Candidate routes do not change across user inputs.
- Results page changes the chosen candidate's stops or reason unexpectedly.
- Screenshots show overflowing text on small or foldable screens.
- Art assets look like placeholders, repeated crops, or fake licensed material.
- AI, rating, popularity, business status, or UGC claims are fabricated.
- New skill usage cannot be traced to an audited source.

## V0.9.63 Immediate Team Assignments

| Role | Assignment |
| --- | --- |
| Skill Research and Tool Admin | Produce the skill research and adoption matrix |
| Design System Lead | Produce TodayPlay-specific design rules |
| Visual QA and Device QA | Produce visual QA workflow and screenshot audit script |
| UI and Motion Art Director / UX Writing Editor | Produce motion and short-copy guide |
| Producer | Integrate outputs, run audits, commit and publish the workflow upgrade |
