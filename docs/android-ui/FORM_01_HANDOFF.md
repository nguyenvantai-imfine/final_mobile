# FORM 01 — PROJECT CONTEXT HANDOFF

## TDT BANK MOBILE BANKING — SOVEREIGN ELITE

### 1. PROJECT OVERVIEW

The project is rebuilding the TDT Bank Mobile Banking interface using Android Native XML.

Technical baseline:

- Android Native Views/XML only.
- Java 17.
- Material 3 Light Theme.
- Light Mode only. No Dark Mode.
- View Binding enabled.
- ConstraintLayout is the primary layout system.
- Do not use Jetpack Compose.

Current phase: UI Design System preparation.

The project is NOT ready for screen conversion.

### 2. FOUR-AGENT MODEL

- Agent 00: Master Project Coordinator. Coordinates tasks, maintains documentation, reviews submissions. Must not directly modify repository source code.
- Agent 01: IDE execution agent.
- Agent 02: IDE execution agent.
- Agent 03: IDE execution agent.
- Project Owner: Final authority for all design and project decisions.

IDE agents work independently. Do not allow multiple agents to edit shared resources simultaneously.

### 3. FORM 01 — GOVERNANCE RULES

Form 01 established rules C-01 through C-22:

- C-01: Android Native XML only.
- C-02: Java 17. UI phase must not modify business logic, Java architecture, Firebase, Repository, ViewModel, navigation or database.
- C-03: Light Mode only; use `Theme.Material3.Light.NoActionBar`.
- C-04: Backend and business logic are deferred.
- C-05: Four-agent collaboration model.
- C-06: Agent 00 coordinates and reviews; does not edit project code.
- C-07: IDE agents perform only assigned tasks.
- C-08: Project Owner has final approval authority.
- C-09: Unknown design specifications remain PENDING until approved.
- C-10: Reuse existing resources before creating new ones. Maintain a resource registry.
- C-11: Do not create duplicate Drawables.
- C-12: Semantic Design Tokens are the official color-system direction. Material 3 attributes must map to semantic tokens.
- C-13: Target `values/` structure: colors.xml, dimens.xml, strings.xml, styles.xml, themes.xml, shapes.xml.
- C-14: Use English lowercase snake_case for resource names. Shared components must not be named after individual screens.
- C-15: Audit legacy resources before removal. Do not bulk-delete.
- C-16: IDE agents use separate branches/worktrees and clearly defined file ownership.
- C-17: `layout` is the integration branch. `main`, `duc`, `huy`, `tai` are not the integration target.
- C-18: Workflow: Receive → Analyze → Resource Check → Assign → Execute → Submit → Review → Approve → Update.
- C-19: Submissions must report changed files, resources, reuse, checks and unresolved issues truthfully.
- C-20: QA includes XML syntax, references, constraints, IDs, consistency, reuse, Stitch fidelity, responsive behavior and resource compilation.
- C-21: Communicate transparently. Report errors and do not independently decide major design changes.
- C-22: No bulk screen conversion or screen assignment before the Design System and required standards are completed.

### 4. FORM 01 STATUS

Agent 00 previously reported that Form 01 was ready for Project Owner approval.

A closure instruction was prepared to record decisions Q-01 through Q-04 concerning:

- Branch naming and merge ownership.
- Java and View ID compatibility.
- Build and screenshot responsibilities.
- Documentation storage and commit ownership.

Do not assume these four decisions were recorded or accepted unless the latest official project documentation confirms them.

Check `MASTER_AGENT_INSTRUCTIONS.md` v1.1 and the latest Form 01 closure record for the actual status.

### 5. DOCUMENT LOCATION

Official documentation directory:

`docs/android-ui/`

Main document:
`MASTER_AGENT_INSTRUCTIONS.md`

Other planned documents:

- `ANDROID_DESIGN_SYSTEM.md`
- `DRAWABLE_RESOURCE_REGISTRY.md`
- `HTML_TO_XML_CONVERSION_RULES.md`
- `OUTPUT_AND_QA_RULES.md`
- `AGENT_COORDINATION.md`
- `SCREEN_CONVERSION_TRACKER.md`

Do not create competing versions of official standards.

### 6. CURRENT PROJECT PHASE

The next task is FORM 02 — ANDROID DESIGN SYSTEM.

Form 02 must audit existing colors, shapes, themes and resource definitions, then propose a unified design system.

It must cover:

- Color system
- Typography
- Spacing
- Corner radius
- Elevation and shadows
- Buttons
- Inputs
- Cards and surfaces
- Icons
- Component states
- Additional components
- System UI

The project already has existing color and shape resources. Audit them before proposing changes.

Known color values requiring investigation:

- `tdt_brand_primary = #A50064`
- `tdt_primary = #7B0049`
- `tdt_secondary = #B70071`

These values are not yet confirmed as the final official color mapping.

### 7. STRICT INSTRUCTIONS

- Never invent missing project decisions.
- Never assume a proposed value has been approved.
- Never modify resources without explicit authorization.
- Never start screen conversion during Form 02.
- Preserve the original Form 01 rules.
- Project Owner approval is mandatory before finalizing the Design System.

END OF HANDOFF.
