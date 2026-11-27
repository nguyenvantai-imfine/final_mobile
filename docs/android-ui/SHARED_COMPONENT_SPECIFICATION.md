# SHARED UI COMPONENT SPECIFICATION
## TDT BANK MOBILE BANKING — SOVEREIGN ELITE

- Repository: `nguyenvantai-imfine/final_mobile`
- Integration Branch: `layout`
- Responsible: Agent 00 — Master Project Coordinator
- Authority: Project Owner
- Baseline: Form 01 Governance + Form 02 Android Design System v1.0
- Status: **SUBMITTED FOR PROJECT OWNER REVIEW — NOT YET APPROVED**
- Inspected commit: `4641efb3255fd60c516c90bf9a152fa194b4d8c8`

---

## 1. Purpose and restrictions

This document defines the shared UI component catalog, records existing resources, identifies missing resources, and prepares the implementation blueprint for Form 04.

It does not re-audit Form 01 or Form 02.

All unspecified properties are marked **PENDING**. Existing resource declarations do not automatically mean that a component has been approved for use.

No Java, XML, Manifest, drawable, selector, or Android resource changes are authorized by Form 03.

## 2. Verified baseline

Repository configuration:

- Native Android XML.
- Java 17.
- Material 3 Light.
- View Binding.
- `Theme.Material3.Light.NoActionBar`.
- `compileSdk = 37`.
- `targetSdk = 37`.

### Existing shared styles

| Resource | Verified properties | Classification |
|---|---|---|
| `Widget.TDT.Button.Primary` | 48dp height, `#A50064` background, white text, Small shape | REUSE EXISTING |
| `Widget.TDT.Button.Outlined` | 48dp height, brand stroke/text, Small shape | REUSE EXISTING |
| `Widget.TDT.TextInputLayout` | Material 3 OutlinedBox, 12dp corners, brand/error stroke | REUSE EXISTING |
| `Widget.TDT.Card` | White surface, 1dp stroke, Medium shape | REUSE EXISTING |
| `Widget.TDT.BottomSheet` | Neutral surface, BottomSheet shape | REUSE EXISTING |
| `ThemeOverlay.TDT.BottomSheet` | Material 3 BottomSheetDialog overlay | REUSE EXISTING |
| `TextAppearance.TDT.Currency` | HeadlineMedium parent, bold, tabular numeric features | REUSE EXISTING |

### Existing shape resources

| Shape | Value |
|---|---|
| `ShapeAppearance.TDT.Small` | 12dp |
| `ShapeAppearance.TDT.Medium` | 16dp |
| `ShapeAppearance.TDT.BottomSheet` | Top 24dp, bottom 0dp |
| `ShapeAppearance.TDT.Pill` | 999dp |

Pill usage has not been verified.

### Existing resource files

- `res/values/colors.xml`
- `res/values/shapes.xml`
- `res/values/themes.xml`
- `res/values/strings.xml`
- Launcher drawable and mipmap assets.

No active `res/layout/`, `values/dimens.xml`, `values/styles.xml`, shared in-app vector icon library, or component selector resources were verified at the inspected baseline.

---

## 3. Component Catalog

### C01 — Button System

| ID | Component | Existing style | Confirmed | Pending | Strategy |
|---|---|---|---|---|---|
| C01.01 | Primary | `Widget.TDT.Button.Primary` | 48dp, brand background, white text, 12dp | Width, padding, typography, icon, states | REUSE |
| C01.02 | Secondary | None | None | All visual properties | NEW — PENDING |
| C01.03 | Outlined | `Widget.TDT.Button.Outlined` | 48dp, brand stroke/text, 12dp | Padding, typography, states | REUSE |
| C01.04 | Text | None | None | All visual properties | NEW — PENDING |
| C01.05 | Icon | None | None | Icon, dimensions, touch target | NEW — PENDING |
| C01.06 | Destructive | None | Error tokens exist | Foreground/background pairing, states | NEW — PENDING |
| C01.07 | Full-width CTA | Primary style available | Primary baseline | Width and margins | ADAPT — PENDING |
| C01.08 | Loading | None | None | Progress, label, disabled behavior | ADAPT/NEW — PENDING |

### C02 — Input System

| ID | Component | Existing style | Confirmed | Pending | Strategy |
|---|---|---|---|---|---|
| C02.01 | Standard | `Widget.TDT.TextInputLayout` | OutlinedBox, 12dp | Height, padding, typography | REUSE |
| C02.02 | Password | Same input style | 12dp container | Visibility icon, validation | ADAPT |
| C02.03 | Search | None | None | Container, icon, behavior | NEW — PENDING |
| C02.04 | OTP | None | None | Cell count, dimensions, spacing | NEW — PENDING |
| C02.05 | Dropdown | Input style available | 12dp container | Menu, trailing icon, states | ADAPT/NEW — PENDING |
| C02.06 | Amount/Currency | Currency TextAppearance | Currency text style | Input behavior, dimensions | ADAPT |
| C02.07 | Read-only | None | None | Background, border, behavior | NEW — PENDING |

### C03 — Card & Surface

| ID | Component | Existing style | Confirmed | Pending | Strategy |
|---|---|---|---|---|---|
| C03.01 | Standard Card | `Widget.TDT.Card` | White, 1dp stroke, 16dp | Padding, elevation | REUSE |
| C03.02 | Outlined Card | Same Card style | 1dp border, 16dp | Whether separate variant is required | ADAPT |
| C03.03 | Elevated Card | Elevated CardView parent | Parent confirmed | Numeric elevation | REUSE/ADAPT |
| C03.04 | Information Card | None | Semantic tokens exist | Icon, padding, mapping | NEW — PENDING |
| C03.05 | Financial Summary Card | Currency style exists | Currency typography | Hierarchy, spacing, states | NEW — PENDING |
| C03.06 | Section Container | None | Surface tokens exist | Padding, spacing | NEW — PENDING |
| C03.07 | Surface Container | Theme surface mapping | Existing theme mapping | Context-specific token mapping | REUSE |

### C04 — Icon System

| ID | Component | Existing resource | Pending | Strategy |
|---|---|---|---|---|
| C04.01 | Standard Icon | No shared icon set | Family, size, stroke/fill | NEW — PENDING |
| C04.02 | Navigation Icon | No shared icon set | Active/inactive states | NEW — PENDING |
| C04.03 | Action Icon | No shared icon set | Glyphs, size | NEW — PENDING |
| C04.04 | Status Icon | Semantic tokens exist | Glyphs and color mapping | NEW — PENDING |
| C04.05 | Icon Button | No dedicated style | Container and touch target | NEW — PENDING |
| C04.06 | Input Leading/Trailing Icon | Input style exists | Icon family and spacing | ADAPT/NEW — PENDING |

No icon library or vector naming convention is selected.

### C05 — Badge & Chip

| ID | Component | Existing resource | Pending | Strategy |
|---|---|---|---|---|
| C05.01 | Status Badge | Semantic tokens exist | Height, radius, color pairs | NEW — PENDING |
| C05.02 | Notification Badge | None | Dot/count dimensions | NEW — PENDING |
| C05.03 | Count Badge | None | Width, height, overflow | NEW — PENDING |
| C05.04 | Information Chip | None | Colors, typography, dimensions | NEW — PENDING |
| C05.05 | Filter Chip | None | Selected state and behavior | NEW — PENDING |
| C05.06 | Selection Chip | None | Selection style and behavior | NEW — PENDING |

`ShapeAppearance.TDT.Pill` is a possible reusable shape, not an approved badge/chip specification.

### C06 — Dialog & Bottom Sheet

| ID | Component | Existing resource | Confirmed | Pending | Strategy |
|---|---|---|---|---|---|
| C06.01 | Confirmation Dialog | No TDT dialog style | None | Size, padding, actions | NEW — PENDING |
| C06.02 | Alert Dialog | Error tokens exist | None beyond tokens | Color pairs, dimensions | NEW — PENDING |
| C06.03 | Information Dialog | No TDT dialog style | None | Icon, spacing, dimensions | NEW — PENDING |
| C06.04 | Action Bottom Sheet | Existing overlay/widget | Top 24dp, bottom 0dp | Height, drag handle, animation | REUSE |
| C06.05 | Selection Bottom Sheet | Same BottomSheet baseline | Same shape | Selected state, list spacing | ADAPT |

### C07 — Common Background & Decoration

| ID | Component | Existing resource | Confirmed | Pending | Strategy |
|---|---|---|---|---|---|
| C07.01 | Screen Background | `tdt_neutral_bg` | Theme background mapping | Screen exceptions | REUSE |
| C07.02 | Card Background | `tdt_neutral_surface` | Card white surface | Variant mapping | REUSE |
| C07.03 | Input Background | No explicit background mapping | OutlinedBox exists | Filled/transparent choice | PENDING |
| C07.04 | Modal Overlay | None | None | Color and opacity | NEW — PENDING |
| C07.05 | Divider | No dedicated style | Neutral border token exists | Thickness and inset | NEW — PENDING |
| C07.06 | Section Background | Surface tokens exist | Tokens available | Semantic mapping | REUSE/PENDING |
| C07.07 | Common Containers | No shared layout | Shapes exist | Padding and spacing | NEW — PENDING |

Do not create duplicate drawable backgrounds for identical functions.

### C08 — Component States

| State | Existing evidence | Status |
|---|---|---|
| Default | Base styles exist | PARTIAL |
| Pressed | `tdt_brand_primary_pressed` exists | Mapping PENDING |
| Focused | Input brand stroke exists | PARTIAL |
| Selected | No shared selector verified | PENDING |
| Disabled | No complete shared mapping | PENDING |
| Loading | No shared style | PENDING |
| Error | Input error stroke and error tokens exist | PARTIAL |
| Success | `tdt_success` exists | Mapping PENDING |
| Read-only | No shared style | PENDING |

No new HEX values or state colors are proposed.

### C09 — Feedback Components

| ID | Component | Existing resource | Pending | Strategy |
|---|---|---|---|---|
| C09.01 | Snackbar | Material 3 dependency | Style, duration, actions | NEW — PENDING |
| C09.02 | Toast | Platform capability | Whether to use platform Toast | PENDING |
| C09.03 | Progress Indicator | Material 3 dependency | Type, size, color | NEW — PENDING |
| C09.04 | Loading Overlay | None | Scrim, behavior | NEW — PENDING |
| C09.05 | Empty State | None | Illustration, copy, CTA | NEW — PENDING |
| C09.06 | Error State | Error tokens exist | Icon, text, retry behavior | NEW — PENDING |
| C09.07 | Success State | Success token exists | Icon, text, CTA | NEW — PENDING |

Screen-specific content should remain screen-owned while reusing approved shared primitives.

---

## 4. Existing Resource Reuse Matrix

| Existing resource | Intended reuse | Restriction |
|---|---|---|
| `tdt_brand_primary` | Primary Button, approved brand emphasis | No HEX change |
| `tdt_on_primary` | Approved foreground on primary | Do not assume for every surface |
| `tdt_brand_primary_pressed` | Candidate pressed state | Mapping requires approval |
| `tdt_accent_active` | Candidate active emphasis | Mapping requires approval |
| `tdt_neutral_bg` | Screen background | Preserve theme mapping |
| `tdt_neutral_surface` | Card/surface | Preserve theme mapping |
| `tdt_neutral_border` | Card/outline | Preserve theme mapping |
| `tdt_text_primary` | Primary text | Preserve theme mapping |
| `tdt_text_secondary` | Secondary text | Preserve theme mapping |
| `tdt_error_alert` | Error theme/input stroke | Do not infer missing foreground pair |
| `tdt_success`, `tdt_warning` | Candidate semantic feedback | Pairings PENDING |
| Small shape | 12dp | Do not change |
| Medium shape | 16dp | Do not change |
| BottomSheet shape | 24dp top / 0dp bottom | Do not change |
| Pill shape | Candidate badge/chip shape | Usage requires approval |
| Primary/Outlined Button styles | Existing button baselines | Preserve confirmed values |
| TextInputLayout style | Existing input baseline | Preserve 12dp |
| Card style | Existing card baseline | Preserve 1dp/16dp |
| BottomSheet styles | Existing sheet baseline | Preserve shape |
| Currency TextAppearance | Currency display | Not a general typography scale |

---

## 5. Missing Component List

No dedicated shared TDT resource was verified for:

- Secondary, Text, Icon, Destructive and Loading Buttons.
- Search, OTP, Dropdown and Read-only Input variants.
- Information and Financial Summary Cards.
- Shared in-app icon family.
- Badge and Chip variants.
- Confirmation, Alert and Information Dialogs.
- Modal Overlay and Divider.
- Complete state selectors and mappings.
- Snackbar, Progress Indicator, Loading Overlay, Empty, Error and Success states.

These are resource-presence findings, not claims that Material 3 cannot provide the underlying widgets.

---

## 6. Resource Implementation Blueprint — Form 04

| Resource type | Existing baseline | Form 04 proposal | Classification |
|---|---|---|---|
| `values/colors.xml` | TDT palette exists | Reuse approved tokens; no HEX changes | REUSE EXISTING |
| `values/dimens.xml` | Not present | Create only after exact dimensions approved | NEW — PENDING |
| `values/shapes.xml` | Four shape resources exist | Reuse; no baseline changes | REUSE EXISTING |
| `values/styles.xml` | Not present | Consider only if approved style organization requires it | PENDING |
| `values/themes.xml` | Theme and shared styles exist | Reuse; adaptations need approval | REUSE/ADAPT |
| `drawable/` | Launcher assets only | Approved backgrounds/illustrations only | NEW — PENDING |
| Selectors | No shared selectors verified | Create for approved state mappings | NEW — PENDING |
| Vector icons | No shared icon set verified | Create after icon family approval | NEW — PENDING |
| Component layouts | No active layout folder verified | Create approved reusable composite layouts | NEW — PENDING |
| Typography | Currency style exists | Additional text appearances only after scale approval | PENDING |
| Animation | No shared animation resource verified | No creation until motion reference approved | PENDING |

Implementation rules:

1. Prefer Material 3 widget inheritance and existing styles.
2. Reuse semantic tokens instead of raw HEX values.
3. Do not create duplicate backgrounds.
4. Do not rename or change existing resources.
5. Create selectors only after state mappings are approved.
6. Do not implement components whose critical properties remain unresolved.

---

## 7. Stitch Reference Checklist

| Batch | Group | Variants | Preserve | Pending |
|---|---|---|---|---|
| ST-01 | Buttons | All 8 variants | Primary/Outlined 48dp; Small 12dp; Primary #A50064 | Other dimensions, typography, states |
| ST-02 | Inputs | All 7 variants | Outlined input radius 12dp | Height, padding, icons, state visuals |
| ST-03 | Cards | All 7 variants | White, 1dp, Medium 16dp | Elevation, spacing, padding |
| ST-04 | Icons | All 6 variants | No icon family approved | Family, glyphs, sizes, stroke/fill |
| ST-05 | Badge/Chip | All 6 variants | Pill shape exists only | Dimensions and semantic pairings |
| ST-06 | Dialog/Sheet | All 5 variants | BottomSheet 24dp/0dp | Dialog dimensions, overlay, animation |
| ST-07 | Background | All 7 variants | Existing semantic tokens | Unmapped contexts, opacity, divider |
| ST-08 | States | All 9 states | Existing token/style evidence | State mapping and behavior |
| ST-09 | Feedback | All 7 variants | Existing semantic tokens | Dimensions, duration, icons, behavior |

### Stitch acceptance checklist

- [ ] Use a consistent frame size from the approved reference.
- [ ] Label each component and variant.
- [ ] Distinguish confirmed properties from proposed visuals.
- [ ] Do not invent colors, typography, icons, elevations, or dimensions.
- [ ] Use approved TDT palette and shape baselines only where mapping is known.
- [ ] Use mock placeholder data, not production data.
- [ ] Show states only when behavior and mapping are reviewed.
- [ ] Record Owner feedback for each component.
- [ ] Keep references focused on reusable components, not redesigning all screens.

---

## 8. Project Owner Decisions

| ID | Required decision | Affected groups | Status |
|---|---|---|---|
| PO-03-01 | Button hierarchy and width behavior | C01 | PENDING |
| PO-03-02 | Shared typography scale | C01–C09 | PENDING |
| PO-03-03 | Input dimensions and validation presentation | C02 | PENDING |
| PO-03-04 | Card elevation, padding and spacing | C03 | PENDING |
| PO-03-05 | Icon family, glyphs, size and stroke/fill | C04, C05, C06, C09 | PENDING |
| PO-03-06 | Badge/chip dimensions and semantic color pairs | C05 | PENDING |
| PO-03-07 | Dialog dimensions, overlay and sheet behavior | C06 | PENDING |
| PO-03-08 | State-to-token mappings | C01–C09 | PENDING |
| PO-03-09 | Feedback behavior and Toast policy | C09 | PENDING |
| PO-03-10 | Shared versus screen-specific component boundaries | C01–C09 | PENDING |
| PO-03-11 | Form 04 resource organization and creation list | Form 04 | PENDING |

No decision is approved by Agent 00.

---

## 9. Form 04 Implementation Scope

### In scope after Owner approval

1. Create or update approved shared XML styles and dimensions.
2. Reuse existing palette and shape resources.
3. Add approved selectors for component states.
4. Add vector icons after icon family approval.
5. Add reusable XML layouts for approved composite components.
6. Validate resource references, naming, theme inheritance and state behavior.
7. Record each resource as REUSE, ADAPT, or NEW.

### Out of scope

- Reworking Form 01 or Form 02.
- Changing approved colors, shape radii or theme parent.
- Implementing or redesigning application screens.
- Changing Java logic, navigation, APIs or database behavior.
- Creating unapproved icons, illustrations, animations or backgrounds.
- Using Stitch mock data as production data.
- Implementing variants that remain PENDING.

### Form 04 entry gate

Form 04 may start only after:

- Project Owner approves Form 03.
- Each selected component has an approved visual reference.
- Required dimensions, typography, semantic mappings, shapes, icons and states are resolved.
- Exact resource changes are listed and authorized.
- Implementation follows the `layout` branch governance.

**Efficiency clarification for incremental implementation:** The entry gate applies to each component selected for implementation, not to every variant in this catalog. Form 04 may begin with already-confirmed baseline components (Primary Button, Outlined Button, Standard Outlined Input, Standard Card and Bottom Sheet). Components or properties still marked PENDING remain out of scope until separately approved.

---

## 10. Completion Status

- [x] Nine component groups classified.
- [x] Existing and proposed resources separated.
- [x] Form 02 baselines carried forward.
- [x] Unknown values marked PENDING.
- [x] Form 04 blueprint prepared.
- [x] Stitch checklist prepared.
- [x] Owner decisions documented.
- [ ] Project Owner review completed.
- [ ] Project Owner approval received.
- [ ] File committed to repository `layout` branch.

**FORM 03 — SUBMITTED FOR PROJECT OWNER REVIEW — NOT YET APPROVED.**

**No Form 04 implementation authorization is implied until Project Owner approval.**

---

## Appendix — Baseline paths

- `app/build.gradle.kts`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/shapes.xml`
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/drawable/` (launcher assets only)
- `app/src/main/res/mipmap-*/` (launcher assets)

## Audit note

The report is based on the inspected baseline commit stated at the top. It records the state evidenced at that commit and does not imply that later commits have been inspected.

Agent 00 reported that its attempt to save `docs/android-ui/SHARED_COMPONENT_SPECIFICATION.md` to GitHub failed with `403 Resource not accessible by integration`. No repository or resource modifications were made as part of that attempt.
