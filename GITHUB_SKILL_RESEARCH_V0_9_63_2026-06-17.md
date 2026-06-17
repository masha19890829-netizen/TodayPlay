# TodayPlay GitHub Skill Research V0.9.63

Date: 2026-06-17
Owner: Skill Research and Tool Admin
Producer review: pass after rewriting corrupted Chinese text

## 0. Conclusion

This round upgrades the team's design and QA capability. It does not change TodayPlay product code and does not install any third-party skill.

Adopted now:

- `figma-create-design-system-rules`
- `figma-generate-library`
- `figma-code-connect-components`
- `playwright-interactive`
- `screenshot`
- `gh-fix-ci`

Read-only references only:

- `anthropics/skills` frontend design guidance
- `content-designer/ux-writing-skill`
- `nextlevelbuilder/ui-ux-pro-max-skill`
- `VoltAgent/awesome-agent-skills`
- `nexu-io/open-design`
- `signerlabs/design-system-builder-skills`

The main decision is deliberate: TodayPlay does not need a pile of style recipes. It needs a small, strict workflow that keeps the app chat-first, calm, truthful, screenshot-ready, and adaptive across phones and foldables.

## 1. Installed Official Skills

All official skills below were installed from `openai/skills` into:

```text
C:\Users\44277\.codex\skills
```

Codex should be restarted after this version so the newly installed skills are fully picked up by the runtime.

| Skill | Status | Source | TodayPlay use |
| --- | --- | --- | --- |
| `figma-create-design-system-rules` | installed | `openai/skills` curated skill | Generate project-specific Figma-to-code rules before any large UI rewrite, so the team stops reinterpreting the visual direction every version. |
| `figma-generate-library` | installed | `openai/skills` curated skill | Once the V0.9.63 design rules are stable, build a Figma library for input box, chips, route cards, timeline, ticket, and state components. |
| `figma-code-connect-components` | installed | `openai/skills` curated skill | Later map Figma components to real Compose components, after naming and component boundaries stop moving. |
| `playwright-interactive` | installed | `openai/skills` curated skill | Use for web prototypes, screenshot review pages, store material previews, and responsive QA where a browser surface exists. |
| `screenshot` | installed | `openai/skills` curated skill | Use as the universal visual evidence fallback for emulator windows, Figma windows, browser prototypes, and comparison captures. |
| `gh-fix-ci` | installed | `openai/skills` curated skill / GitHub plugin skill | Use when GitHub Actions or PR checks fail after the project moves to CI-driven builds. |

## 2. Immediate SOP Adoption

Use these skills immediately in TodayPlay workflow:

- `figma-create-design-system-rules`: design system rules and handoff constraints.
- `screenshot`: visual proof collection.
- `playwright-interactive`: browser/prototype visual QA when applicable.
- `gh-fix-ci`: CI failure diagnosis after GitHub workflows are active.

Use these after the design direction is stable:

- `figma-generate-library`
- `figma-code-connect-components`

Reason: generating a library or code mapping too early will freeze temporary UI decisions. TodayPlay is still rebuilding its identity around "one sentence arranges today", so the design rules must come first.

## 3. Third-Party Review Matrix

| Source | Verified existence | Useful for | Main risk | Decision |
| --- | --- | --- | --- | --- |
| `anthropics/skills` frontend-design | GitHub source exists | Anti-template design thinking, stronger visual judgment, avoiding generic AI app surfaces | Written for a different agent ecosystem; should not override TodayPlay-specific rules | Read-only reference |
| `content-designer/ux-writing-skill` | GitHub source exists | Short UI copy, empty states, errors, onboarding, button language | Generic UX writing rules need localization and product-specific tone | Read-only reference, translate useful rules into TodayPlay guide |
| `nextlevelbuilder/ui-ux-pro-max-skill` | GitHub source exists | Broad UI/UX style taxonomy and examples | Large scope, possible installer/script complexity, risk of making TodayPlay feel like a style demo | Do not install now |
| `VoltAgent/awesome-agent-skills` | GitHub source exists | Discovery index for future skills | Index quality does not equal skill safety or relevance | Use only as search directory |
| `nexu-io/open-design` | GitHub source exists | Observing agentic design workspace direction | Too broad for this app; adopting it would change the whole toolchain | Do not install now |
| `signerlabs/design-system-builder-skills` | GitHub source exists | Design token/spec/preview generation patterns | Overlaps with official Figma skills; generated specs may create demo-like artifacts | Do not install now |

## 4. Risk Policy

Third-party skills must not be installed until the team records:

- Source URL.
- License.
- Last maintenance signal.
- Whether it runs scripts, downloads assets, or executes remote code.
- Whether it contains prompt instructions that could conflict with TodayPlay project rules.
- Why official OpenAI skills are insufficient.

Reject or hold any skill when:

- It requires opaque remote execution.
- It tries to replace the app's product direction.
- It encourages fake data, fake heat, fake rating, or fake authority.
- It makes the UI more decorative but less useful.
- It cannot be traced to a stable public source.

## 5. What We Borrow From External References

From frontend-design references:

- Avoid generic AI gradients and feature catalogs.
- Make every screen feel like the specific product, not a reusable template.
- Use screenshots as design evidence, not only implementation proof.

From UX writing references:

- Keep prompt, buttons, errors, and empty states short.
- Write the next action, not the product explanation.
- Avoid blame in failure states.
- Give one useful recovery option.

From design-system-builder references:

- Keep tokens, component rules, preview pages, and QA evidence together.
- Do not let token generation replace product judgment.

## 6. Team Role Changes

New permanent roles for TodayPlay:

| Role | Responsibility |
| --- | --- |
| Skill Research and Tool Admin | Search, audit, install, and record skill decisions. |
| Design System Lead | Own visual tokens, component rules, layout rules, and adaptation constraints. |
| Visual Aesthetic QA | Score screenshots against design, device, and store-readiness criteria. |
| Motion Director | Define opening, generation, route connection, current-stop, and completion feedback. |
| UX Writing Editor | Remove text walls and keep AI, route, error, and fallback copy short. |

## 7. Next Steps

1. Use the new design system rules before starting the next UI implementation round.
2. Capture six fixed visual QA screenshots for every APK candidate.
3. Run `playstore/visual_qa_audit.py` before any external-test package is shared.
4. Keep third-party skills as references until one passes a full source, license, and execution-risk review.

## 8. Sources

- OpenAI skills: https://github.com/openai/skills
- Figma design system rules: https://mcpservers.org/agent-skills/openai/figma-create-design-system-rules
- Playwright interactive skill: https://github.com/openai/skills/blob/main/skills/.curated/playwright-interactive/SKILL.md
- Anthropic frontend design skill: https://github.com/anthropics/skills/blob/main/skills/frontend-design/SKILL.md
- UI UX Pro Max skill: https://github.com/nextlevelbuilder/ui-ux-pro-max-skill
- Awesome Agent Skills: https://github.com/VoltAgent/awesome-agent-skills
- UX writing skill: https://github.com/content-designer/ux-writing-skill
- Open Design: https://github.com/nexu-io/open-design
- Design system builder skills: https://github.com/signerlabs/design-system-builder-skills
