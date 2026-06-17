# TodayPlay V0.9.63 Skill Upgrade And Visual QA Report

Date: 2026-06-17
Producer: Project Lead / Team Coordination

## 0. Summary

V0.9.63 is a team capability upgrade, not an APK release.

This round installed official design and QA skills, reviewed third-party skill sources, created TodayPlay's design system rules, created a motion and UX writing guide, created a visual QA workflow, and added an automated screenshot evidence audit.

No TodayPlay app feature code was changed in this round.

## 1. Team Execution

The team workflow was exercised with four role owners:

| Role | Delivered |
| --- | --- |
| Skill Research and Tool Admin | `GITHUB_SKILL_RESEARCH_V0_9_63_2026-06-17.md` |
| Design System Lead | `DESIGN_SYSTEM_RULES_V0_9_63.md` |
| Visual Aesthetic QA and Device Adaptation | `VISUAL_QA_WORKFLOW_V0_9_63_2026-06-17.md`, `playstore/visual_qa_audit.py` |
| Motion Director and UX Writing Editor | `MOTION_UX_WRITING_GUIDE_V0_9_63.md` |
| Producer | `TEAM_SKILL_UPGRADE_WORKFLOW_V0_9_63_2026-06-17.md`, this update report, final review and validation |

The team process now has named responsibilities and file-level outputs instead of one person silently making all decisions.

## 2. Installed Official Skills

Installed from OpenAI official skill sources:

- `figma-create-design-system-rules`
- `figma-generate-library`
- `figma-code-connect-components`
- `playwright-interactive`
- `screenshot`
- `gh-fix-ci`

Installed local skill root:

```text
C:\Users\44277\.codex\skills
```

Important: restart Codex after this report so the newly installed skills appear in future skill discovery automatically.

## 3. Third-Party Skill Decision

Third-party skill sources were reviewed as references only. None were installed.

| Source | Decision |
| --- | --- |
| `anthropics/skills` frontend design guidance | Read-only reference |
| `content-designer/ux-writing-skill` | Read-only reference |
| `nextlevelbuilder/ui-ux-pro-max-skill` | Do not install now |
| `VoltAgent/awesome-agent-skills` | Use only as discovery index |
| `nexu-io/open-design` | Do not install now |
| `signerlabs/design-system-builder-skills` | Do not install now |

Reason: TodayPlay should not import a broad external style system before its own chat-first product identity is stable.

## 4. New Documents

| File | Purpose |
| --- | --- |
| `GITHUB_SKILL_RESEARCH_V0_9_63_2026-06-17.md` | Skill installation, adoption, and risk matrix |
| `DESIGN_SYSTEM_RULES_V0_9_63.md` | TodayPlay-specific visual, AI, route, copy, and adaptation rules |
| `MOTION_UX_WRITING_GUIDE_V0_9_63.md` | Splash, AI thinking, card reveal, route connection, ticket stamp, and short-copy rules |
| `VISUAL_QA_WORKFLOW_V0_9_63_2026-06-17.md` | Six fixed screenshots, device matrix, scoring rubric, blockers |
| `TEAM_SKILL_UPGRADE_WORKFLOW_V0_9_63_2026-06-17.md` | Permanent team workflow and role responsibility map |
| `playstore/visual_qa_audit.py` | Automated screenshot evidence audit for dist screenshots |

## 5. Visual QA Script

New script:

```powershell
python playstore\visual_qa_audit.py .
```

Optional:

```powershell
python playstore\visual_qa_audit.py . --version v0.9.63
python playstore\visual_qa_audit.py . --dist dist --json
```

It checks:

- Six fixed screenshot moments exist.
- PNG files can be parsed.
- Dimensions are valid.
- Small-screen evidence exists.
- Foldable evidence exists.
- Landscape evidence exists and is actually landscape.

The script is intentionally objective. It does not replace human visual scoring; it prevents missing or invalid screenshot evidence from slipping through.

## 6. Validation Results

Passed:

```powershell
python playstore\visual_qa_audit.py .
python -m py_compile playstore\visual_qa_audit.py
python playstore\app_regression_audit.py
git diff --check
```

Visual QA audit auto-detected latest screenshot set as `v0.9.62` and passed all required screenshot evidence:

- Splash
- Chat-first home
- Candidate cards
- Loading
- Route result
- Current stop
- Normal phone
- Small phone
- Foldable
- Landscape

Known issue:

```powershell
python playstore\adaptive_ui_audit.py
```

Result: `fail`

The existing adaptive audit still reports that `HomeScreen` is not fully recognized as having phone, compact-height, and wide/tablet layout classes. This was not fixed in V0.9.63 because the scope was skill and workflow upgrade only. It should be a required implementation item for the next APK version.

## 7. New Release Gate

Future APK release candidates must pass:

1. Product promise check: one sentence can generate a personalized route.
2. Design system check: UI follows `DESIGN_SYSTEM_RULES_V0_9_63.md`.
3. Motion and copy check: follows `MOTION_UX_WRITING_GUIDE_V0_9_63.md`.
4. Screenshot evidence check: `playstore/visual_qa_audit.py`.
5. Visual manual score: at least 90/100 and no blocker from `VISUAL_QA_WORKFLOW_V0_9_63_2026-06-17.md`.
6. Android adaptive audit: must pass before external testers receive an APK.
7. App regression audit: must pass.

## 8. Next Version Recommendation

Recommended next implementation version:

```text
V0.9.64 Chat-First Adaptive Implementation
```

Priority order:

1. Fix `HomeScreen` adaptive layout so the adaptive audit passes.
2. Apply the V0.9.63 design system to the chat-first home.
3. Make AI candidate cards visibly change for different inputs.
4. Make selected candidate data persist into result page.
5. Add reduced-motion-aware route connection and ticket stamp polish.
6. Capture the six fixed screenshots again for the new version.
7. Build and sign a new APK only after the visual and adaptive gates pass.

## 9. Source Links

- OpenAI skills: https://github.com/openai/skills
- Figma design system rules reference: https://mcpservers.org/agent-skills/openai/figma-create-design-system-rules
- Playwright interactive skill: https://github.com/openai/skills/blob/main/skills/.curated/playwright-interactive/SKILL.md
- Anthropic frontend design skill: https://github.com/anthropics/skills/blob/main/skills/frontend-design/SKILL.md
- UI UX Pro Max skill: https://github.com/nextlevelbuilder/ui-ux-pro-max-skill
- Awesome Agent Skills: https://github.com/VoltAgent/awesome-agent-skills
- UX writing skill: https://github.com/content-designer/ux-writing-skill
- Open Design: https://github.com/nexu-io/open-design
