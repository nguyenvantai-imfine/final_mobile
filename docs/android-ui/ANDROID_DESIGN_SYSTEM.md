# TDT BANK MOBILE BANKING — SOVEREIGN ELITE
# ANDROID DESIGN SYSTEM

**Document ID:** TDT-ANDROID-DS-001  
**Version:** 1.0  
**Status:** PROJECT OWNER APPROVED BASELINE  
**Owner:** Project Owner  
**Coordinator:** Agent 00 — Master Project Coordinator  
**Platform:** Android Native Views / XML  
**Last updated:** 2026-11-27

---

## 1. Document Purpose

This document defines the approved baseline Android Design System for TDT Bank Mobile Banking — Sovereign Elite. It consolidates the accepted Form 02 resource audit, Project Owner decisions, and existing repository specifications.

This document is the shared design reference for subsequent UI forms and implementation tasks. It does not authorize unrestricted changes to existing repository resources. Any implementation must follow the task scope, resource ownership, and approval process defined by the project governance documents.

### 1.1 Authority and status labels

- **APPROVED:** Explicitly approved by the Project Owner.
- **EXISTING:** Verified in the audited repository baseline.
- **BASELINE:** Existing specification adopted as the current shared starting point; it must not be silently changed.
- **PENDING:** Insufficient evidence or no Project Owner decision yet.
- **DEFERRED:** Deliberately postponed to a later form; not a blocker for using the approved baseline.
- **LEGACY CANDIDATE:** Preserve as-is; do not use for new design unless separately approved.

Where a value is marked PENDING or DEFERRED, agents must not invent a replacement value.

## 2. Project and Technical Baseline

| Item | Standard |
|---|---|
| UI technology | Android Native Views/XML only |
| Language baseline | Java 17 |
| UI framework | Material Components / Material 3 |
| Theme mode | Light Mode only |
| Base theme | `Theme.Material3.Light.NoActionBar` (project theme: `Theme.TDTBankSovereign`) |
| Main layout system | ConstraintLayout; LinearLayout and FrameLayout may supplement where appropriate |
| Binding | View Binding enabled |
| Compose | Not permitted |
| Dark theme/night resources | Not part of the approved UI scope |

The audited repository baseline is:

- **Repository:** `nguyenvantai-imfine/final_mobile`
- **Branch:** `layout`
- **Commit:** `4641efb3255fd60c516c90bf9a152fa194b4d8c8`
- **Git tree:** `f3a2c55f756a62e9a71ad1e1c3d341f39c23fed5`

This is a point-in-time audit baseline, not a claim that the branch has not changed since the audit.

## 3. Color System

### 3.1 Approved brand colors

| Semantic role | Resource | HEX | Status | Usage rule |
|---|---|---:|---|---|
| Primary Brand | `tdt_brand_primary` | `#A50064` | **APPROVED — PO-F02-01** | Official primary brand color for new design |
| Secondary | `tdt_secondary` | `#B70071` | **APPROVED — PO-F02-02** | Official secondary color for new design |

The current theme maps `colorPrimary` to `tdt_brand_primary` and `colorSecondary` to `tdt_secondary`.

### 3.2 Approved foundation colors

The Project Owner accepted the current Theme-facing foundation tokens as the baseline for app background, surfaces, text, and outline.

| Semantic role | Resource | HEX | Status |
|---|---|---:|---|
| App Background | `tdt_neutral_bg` | `#F5F5F7` | **APPROVED — PO-F02-04** |
| Surface | `tdt_neutral_surface` | `#FFFFFF` | **APPROVED — PO-F02-04** |
| Primary Text | `tdt_text_primary` | `#222222` | **APPROVED — PO-F02-04** |
| Secondary Text | `tdt_text_secondary` | `#757575` | **APPROVED — PO-F02-04** |
| Outline / Border | `tdt_neutral_border` | `#E0E0E5` | **APPROVED — PO-F02-04** |

These are the default semantic roles for new shared UI. Component-specific needs may use other approved semantic tokens when their role is clearly documented.

### 3.3 Existing tokens that must be preserved

The following resources were present in the audited `colors.xml`. Their existence and current values are recorded here; inclusion in this table does not authorize deletion, renaming, merging, or HEX changes.

| Resource | HEX | Current status / instruction |
|---|---:|---|
| `tdt_primary` | `#7B0049` | EXISTING; preserve unchanged. It is not the approved Primary Brand token and is not currently mapped to `colorPrimary`. Further role/use remains PENDING. |
| `tdt_accent_active` | `#D82D8B` | EXISTING; preserve. Existing active/accent role; further component mapping PENDING. |
| `tdt_soft_highlight` | `#FCE4F1` | EXISTING; preserve. Existing highlight role. |
| `tdt_success` | `#2E9E5B` | EXISTING; preserve. Semantic success role. |
| `tdt_error_alert` | `#D93025` | EXISTING; currently mapped to `colorError`; preserve. |
| `tdt_warning` | `#F5A623` | EXISTING; preserve. Semantic warning role. |
| `tdt_surface` | `#F9F9FB` | EXISTING; preserve. Not the token currently mapped to Theme `colorSurface`. |
| `tdt_on_surface` | `#1A1C1D` | EXISTING; preserve. Not the token currently mapped to Theme `colorOnSurface`. |
| `tdt_error` | `#BA1A1A` | EXISTING; preserve. Not the token currently mapped to Theme `colorError`. |

Other existing semantic and Material-style tokens must also be preserved unless a separate approved resource-migration task explicitly authorizes a change.

### 3.4 Blue aliases — legacy policy

**Decision PO-F02-03 — APPROVED**

All existing blue aliases in the repository are classified as **LEGACY CANDIDATE**.

- Preserve every existing alias exactly as it is.
- Do not use these blue aliases for new design work.
- Do not delete, rename, merge, or modify their HEX values.
- Reconsider them only after new evidence or a further Project Owner decision.
- The classification is not proof that every alias is unused at runtime.

The group includes non-`tdt_` aliases identified in the audit, including `secondary = #0056B3` and other blue-valued aliases. Preserve the complete group, not only the examples listed here.

### 3.5 Color-token interpretation rules

The audit found duplicate HEX values and semantic role overlaps. These are not automatically runtime defects.

Agents must distinguish:

1. **Duplicate HEX:** multiple resource names share the same HEX value.
2. **Semantic overlap/divergence:** resources have related semantic names or purposes but different HEX values.
3. **Theme mapping:** the specific resource currently assigned to a Material theme attribute.
4. **Actual runtime usage conflict:** only confirm when actual component/layout usage demonstrates a conflict.

At the accepted audit baseline, **no actual runtime color usage conflict was confirmed**.

Do not merge resources merely because their HEX values match. Do not call two tokens redundant merely because their names are similar. Check semantic role and usage first.

### 3.6 Audited Theme color mapping

The following mapping was verified in `app/src/main/res/values/themes.xml` and `colors.xml` at the audit baseline:

| Theme attribute | Resource | HEX |
|---|---|---:|
| `colorPrimary` | `tdt_brand_primary` | `#A50064` |
| `colorOnPrimary` | `tdt_on_primary` | `#FFFFFF` |
| `colorPrimaryContainer` | `tdt_primary_container` | `#A50064` |
| `colorOnPrimaryContainer` | `tdt_on_primary_container` | `#FFB4D0` |
| `colorSecondary` | `tdt_secondary` | `#B70071` |
| `colorOnSecondary` | `tdt_on_secondary` | `#FFFFFF` |
| `android:colorBackground` | `tdt_neutral_bg` | `#F5F5F7` |
| `colorSurface` | `tdt_neutral_surface` | `#FFFFFF` |
| `colorOnSurface` | `tdt_text_primary` | `#222222` |
| `colorOnSurfaceVariant` | `tdt_text_secondary` | `#757575` |
| `colorOutline` | `tdt_neutral_border` | `#E0E0E5` |
| `colorError` | `tdt_error_alert` | `#D93025` |

Important observations:

- `colorPrimary` and `colorPrimaryContainer` currently resolve to the same HEX. This is an existing mapping, not an independently confirmed runtime defect.
- `tdt_primary = #7B0049` is not the resource currently mapped to `colorPrimary`.
- The Theme uses `tdt_text_primary`, `tdt_neutral_surface`, and `tdt_error_alert` for the mapped attributes shown above; it does not use the similarly named `tdt_on_surface`, `tdt_surface`, or `tdt_error` for those mappings.
- Preserve existing mappings unless a future design/resource task receives Project Owner approval to change them.

### 3.7 Accessibility audit data

The following contrast ratios were calculated from the audited HEX values. They are audit data, not certification of every screen or component.

| Foreground | Background | Contrast | Reference result |
|---|---|---:|---|
| `#FFFFFF` (`tdt_on_primary`) | `#A50064` (`tdt_brand_primary`) | 7.54:1 | Meets 4.5:1 reference |
| `#FFB4D0` (`tdt_on_primary_container`) | `#A50064` (`tdt_primary_container`) | 4.56:1 | Meets 4.5:1 reference; close to threshold |
| `#FFFFFF` (`tdt_on_secondary`) | `#B70071` (`tdt_secondary`) | 6.46:1 | Meets 4.5:1 reference |
| `#222222` (`tdt_text_primary`) | `#FFFFFF` (`tdt_neutral_surface`) | 15.91:1 | Meets 4.5:1 reference |
| `#757575` (`tdt_text_secondary`) | `#FFFFFF` (`tdt_neutral_surface`) | 4.61:1 | Meets 4.5:1 reference |
| `#222222` (`tdt_text_primary`) | `#F5F5F7` (`tdt_neutral_bg`) | 14.61:1 | Meets 4.5:1 reference |

For the outlined button, the audit verified brand-colored stroke and text but did not verify its rendered background. Its contrast against white is 7.54:1 only as a conditional calculation; do not treat white as the confirmed runtime background.

No color changes are authorized by these contrast calculations. Re-audit additional foreground/background combinations when their actual component usage is available.

## 4. Typography System

### 4.1 Existing typography

The audited repository contains `TextAppearance.TDT.Currency`:

- Parent: `TextAppearance.Material3.HeadlineMedium`.
- Font feature settings: `tnum, zero`.
- Text style: bold.
- Text color: `tdt_text_primary`.

No dedicated `res/font/` directory or custom font asset was found in the audited resource tree.

### 4.2 Current standard and deferred items

- Use the existing Material 3 text appearances as the framework baseline unless a screen-specific approved design reference requires otherwise.
- Preserve `TextAppearance.TDT.Currency` and its current behavior.
- **PENDING / DEFERRED:** official font family, complete typography scale, exact text sizes, weights, line heights, and letter spacing for all roles.
- Do not introduce a custom font asset or invent a new type scale without Project Owner approval or an approved design reference.

## 5. Spacing and Dimensions

- No `values/dimens.xml` was present in the audited repository tree.
- The audit did not have active screen layout XML from which to establish a complete spacing inventory.
- Do not claim that a global spacing scale already exists.
- Preserve existing component dimensions and any inline values encountered during subsequent screen work until a shared dimension policy is approved.
- **PENDING / DEFERRED:** official spacing scale, screen margins, section spacing, component gaps, standard padding, touch-target dimensions, and reusable dimension token names.
- Do not create a large collection of near-duplicate dimension resources.

## 6. Corner Radius and Shapes

The following shape definitions were verified in `app/src/main/res/values/shapes.xml` and are adopted as the current shared shape baseline:

| Shape resource | Value | Status / usage evidence |
|---|---|---|
| `ShapeAppearance.TDT.Small` | 12dp | EXISTING / BASELINE; referenced by Theme and button styles |
| `ShapeAppearance.TDT.Medium` | 16dp | EXISTING / BASELINE; referenced by Theme and Card style |
| `ShapeAppearance.TDT.BottomSheet` | Top corners 24dp; bottom corners 0dp | EXISTING / BASELINE; referenced by BottomSheet widget style |
| `ShapeAppearance.TDT.Pill` | 999dp | EXISTING; usage not confirmed in the audited source |

Rules:

- Reuse the existing shape resources for matching shared components.
- Do not create duplicate shape definitions for the same role.
- Do not change the existing values without Project Owner approval.
- Pill remains available but its current usage is unverified.
- Additional shape roles or exceptions are PENDING and must be justified by a design reference.

## 7. Elevation and Shadow

Verified baseline:

- `Widget.TDT.Card` inherits from `Widget.Material3.CardView.Elevated`.
- The Card style specifies a white background, a 1dp stroke, and the Medium shape.
- No explicit `cardElevation` value was found in that TDT style.
- BottomSheet inherits from the Material 3 BottomSheet widget style.

Rules:

- Preserve the existing Material 3 component inheritance unless approved design work requires a change.
- Do not invent numeric elevation levels from the inheritance name alone.
- Do not add manual drawable shadows where a Material component elevation already provides the required behavior without checking the visual result.
- **PENDING / DEFERRED:** shared numeric elevation scale, dialog elevation, menu/dropdown elevation, and any custom shadow specification.

## 8. Button System

### 8.1 Existing shared styles

| Style | Verified baseline |
|---|---|
| `Widget.TDT.Button.Primary` | Inherits `Widget.Material3.Button`; height 48dp; background `tdt_brand_primary`; text `tdt_on_primary`; Small shape |
| `Widget.TDT.Button.Outlined` | Inherits `Widget.Material3.Button.OutlinedButton`; height 48dp; brand-colored stroke and text; Small shape |

### 8.2 Rules

- Use the existing Primary style for the primary call-to-action role.
- Use the existing Outlined style for a secondary/alternative action when appropriate.
- Preserve 48dp height and the existing shape/color mapping unless a separately approved component specification supersedes it.
- The audited styles do not directly define a complete custom state system. Do not assume pressed, loading, disabled, or focused states have been fully standardized.
- **PENDING / DEFERRED:** Text button, tonal/secondary filled button, destructive button, icon button, loading treatment, disabled treatment, state-specific colors, and exact icon/text spacing.

## 9. Input System

### 9.1 Existing shared style

`Widget.TDT.TextInputLayout` was verified to:

- Inherit `Widget.Material3.TextInputLayout.OutlinedBox`.
- Use 12dp corner radii on all four corners.
- Reference brand stroke and error stroke colors.

### 9.2 Rules

- Preserve the existing Outlined TextInputLayout as the current shared input baseline.
- Do not assume that every input mode or state is standardized merely because the base style exists.
- Preserve existing error styling.
- **PENDING / DEFERRED:** filled input variant, search, password, OTP, dropdown, amount/currency input, read-only input, helper/counter treatments, focused/disabled/success states, and standard input height/padding.

## 10. Cards and Surfaces

### 10.1 Existing shared Card style

`Widget.TDT.Card` was verified to:

- Inherit `Widget.Material3.CardView.Elevated`.
- Use a white card background.
- Use a 1dp stroke.
- Use `ShapeAppearance.TDT.Medium` (16dp).
- Not declare an explicit `cardElevation` value in the TDT style.

### 10.2 Rules

- Use the existing Card style as the baseline for shared cards where its behavior matches the design.
- Use `tdt_neutral_surface` (`#FFFFFF`) as the approved default surface token.
- Do not create screen-specific variants as shared styles unless the same reusable role is demonstrated.
- **PENDING / DEFERRED:** Standard/flat card, outlined-only card, financial summary card, information panel, card internal padding, and numeric elevation differences.

## 11. Icon System

The audited `res/drawable/` contained launcher vector assets only. No shared business/UI icon set or font icon resource was found in the active resource tree.

Rules:

- Do not treat launcher artwork as the app's business icon system.
- Do not introduce multiple competing icon families.
- **PENDING / DEFERRED:** official icon source/family, stroke-versus-fill policy, standard icon sizes, icon-button sizes, alignment, and interaction states.
- New icon assets require a specific task assignment and reuse check before creation.

## 12. Component States

The target shared state vocabulary is:

- Default
- Pressed
- Focused
- Selected
- Disabled
- Loading
- Error
- Success
- Read-only (where applicable)

Audit limitations:

- The audited TDT Button styles provide base styling but no complete custom state specification was confirmed.
- TextInputLayout has an error stroke reference; a complete success/focus/disabled state system was not confirmed.
- No dedicated color selector resource directory or state selector set was found in the audited `res/` tree.

Rules:

- Use Material component default state behavior where the component inherits it, unless a screen specification says otherwise.
- Do not invent project-wide state colors or selectors.
- **PENDING / DEFERRED:** exact state visuals, colors, opacity, transition behavior, loading indicators, and selected-state treatment.

## 13. Additional Components

Existing baseline:

- `Widget.TDT.BottomSheet` inherits from `Widget.Material3.BottomSheet` and references `ShapeAppearance.TDT.BottomSheet`.
- `ThemeOverlay.TDT.BottomSheet` connects the overlay to the TDT BottomSheet widget style.

Not verified as dedicated TDT shared styles in the audited `themes.xml`:

- Dialog
- Chip
- Badge
- Divider
- Tooltip
- Snackbar
- Dedicated icon button

These are **PENDING / DEFERRED**, not confirmed absent from all possible runtime usage. Define their shared variants only when a later form establishes their visual requirements.

## 14. System UI and Window Insets

### 14.1 Confirmed platform baseline

- `compileSdk = 37` — CONFIRMED at the audit baseline.
- `targetSdk = 37` — CONFIRMED at the audit baseline.
- Platform-enforced edge-to-edge behavior applies on Android 15+ for applications targeting API 35 or higher.

### 14.2 Existing WindowInsets handling

The audit found `applySystemBarInsets(View rootView)` in `AuthSessionHelper.java`. It registers a `ViewCompat.setOnApplyWindowInsetsListener`, reads status-bar and navigation-bar insets, adds those values to the root view's initial padding, and requests inset application.

The audit also found calls from `MainActivity.java` and `BaseSecureActivity.java`.

### 14.3 Important distinction

The existence of a WindowInsets listener does not prove that all screens correctly handle edge-to-edge or avoid overlap.

| Item | Status |
|---|---|
| Target/compile SDK 37 | CONFIRMED at audit baseline |
| Platform-enforced edge-to-edge on Android 15+ | CONFIRMED |
| Explicit edge-to-edge API configuration in source | PENDING VERIFICATION |
| WindowInsets handler exists | EXISTING — CONFIRMED |
| Status bar color/icon appearance | PENDING |
| Navigation bar color/icon appearance | PENDING |
| Layout overlap and inset correctness on devices | PENDING |
| Behavior on Android 24–14 | PENDING validation |

Do not claim runtime correctness without device/emulator evidence. System bar implementation changes require a separately assigned task.

## 15. Resource Organization and Naming

### 15.1 Target `values/` organization

The approved target resource organization is:

- `colors.xml`
- `dimens.xml`
- `strings.xml`
- `styles.xml`
- `themes.xml`
- `shapes.xml`

At the audited baseline, only `colors.xml`, `shapes.xml`, `strings.xml`, and `themes.xml` were present under `values/`. `dimens.xml` and `styles.xml` were not present at the audited paths.

This is a target organization, not authorization to create the missing files immediately.

### 15.2 Naming and reuse rules

- Use English lowercase `snake_case` for resource names where applicable.
- Shared resources must not be named after a specific screen.
- Prefer reuse of an existing resource over creating a new one.
- Check the resource registry before creating a shared Drawable or style.
- Do not create duplicate resources with the same purpose.
- Do not delete, rename, merge, or change the HEX of existing resources without an explicit approved task.
- Update the resource registry whenever an authorized resource task creates or changes shared resources.

## 16. Audit Baseline and Known Limitations

The accepted Form 02 audit was read-only and based on commit `4641efb3255fd60c516c90bf9a152fa194b4d8c8` on branch `layout`.

The audited active `res/` tree contained 20 Git-managed files. It had no active `res/layout/` directory, no `res/font/` directory, no `res/color/` selector directory, and no `values/dimens.xml` or `values/styles.xml` at the specified locations.

The Java source referenced nine layout resources and three input Drawables that were absent from the active resource tree. This is recorded as a **KNOWN BASELINE GAP** from the prior UI reset, not as a new Form 02 regression. The audit did not recreate these files.

The missing active layouts mean the audit could not fully verify:

- Actual use of every color alias and shape.
- Whether each TDT widget style is applied consistently in screens.
- Full typography and spacing usage.
- Actual component state rendering.
- Runtime screen overlap/inset correctness.

Governance files were not found in the audited tree at the expected `docs/android-ui/` paths in that commit. The Project Owner-provided Form 01 v1.2 status remains the governance context, but the absence in that particular commit must not be hidden or treated as proof that a file exists in the repository now.

## 17. Project Owner Decision Register — Form 02

| Decision ID | Decision | Status |
|---|---|---|
| PO-F02-01 | Primary Brand is `#A50064` (`tdt_brand_primary`). Keep `tdt_primary = #7B0049` unchanged; its further role remains unresolved. | **APPROVED** |
| PO-F02-02 | Secondary is `#B70071` (`tdt_secondary`). | **APPROVED** |
| PO-F02-03 | Preserve all existing blue aliases; classify as LEGACY CANDIDATE; do not use for new design; do not delete, rename, merge, or change HEX. | **APPROVED** |
| PO-F02-04 | Adopt `tdt_neutral_bg #F5F5F7`, `tdt_neutral_surface #FFFFFF`, `tdt_text_primary #222222`, `tdt_text_secondary #757575`, and `tdt_neutral_border #E0E0E5` as the approved foundation roles. | **APPROVED** |

Other detailed typography, spacing, elevation, icon, state, and System UI specifications remain PENDING/DEFERRED as stated in their sections. They must be resolved in the relevant subsequent form or by a specific Project Owner decision; agents must not silently fill those gaps.

## 18. Use in Subsequent Forms

For every subsequent UI form:

1. Read this document before proposing or implementing shared visual resources.
2. Use approved Primary, Secondary, background, surface, text, and outline tokens as defined here.
3. Preserve all legacy candidate aliases unchanged and do not use blue aliases for new design.
4. Reuse the existing shape and component baselines where they match the assigned design.
5. Treat PENDING/DEFERRED values as unresolved; do not fabricate specifications.
6. Record any required exception and obtain Project Owner approval before changing shared standards.
7. Keep implementation work within the assigned file ownership and branch rules.

## 19. Form 02 Release Record

**Resource Audit:** ACCEPTED BY PROJECT OWNER.  
**Design Decisions:** PO-F02-01 through PO-F02-04 APPROVED.  
**Document Status:** APPROVED BASELINE v1.0, with explicitly listed PENDING/DEFERRED specifications.  
**Repository write/commit:** Not performed by this document-generation step.  
**Official repository destination:** `docs/android-ui/ANDROID_DESIGN_SYSTEM.md`.

This file is prepared for Project Owner review and placement in the repository. Do not claim it has been committed or merged until that action is actually completed.

---

**END OF ANDROID DESIGN SYSTEM v1.0**
