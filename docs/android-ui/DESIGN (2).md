---
name: TDT Bank Sovereign
colors:
  surface: '#f9f9fb'
  surface-dim: '#d9dadc'
  surface-bright: '#f9f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f5'
  surface-container: '#eeeef0'
  surface-container-high: '#e8e8ea'
  surface-container-highest: '#e2e2e4'
  on-surface: '#1a1c1d'
  on-surface-variant: '#574149'
  inverse-surface: '#2f3132'
  inverse-on-surface: '#f0f0f2'
  outline: '#8a7079'
  outline-variant: '#debec8'
  surface-tint: '#b3166e'
  primary: '#7b0049'
  on-primary: '#ffffff'
  primary-container: '#a50064'
  on-primary-container: '#ffb4d0'
  inverse-primary: '#ffb0ce'
  secondary: '#b70071'
  on-secondary: '#ffffff'
  secondary-container: '#fe4ea9'
  on-secondary-container: '#5c0036'
  tertiary: '#483943'
  on-tertiary: '#ffffff'
  tertiary-container: '#61505a'
  on-tertiary-container: '#dac3d0'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffd9e5'
  primary-fixed-dim: '#ffb0ce'
  on-primary-fixed: '#3e0022'
  on-primary-fixed-variant: '#8c0054'
  secondary-fixed: '#ffd9e5'
  secondary-fixed-dim: '#ffb0cf'
  on-secondary-fixed: '#3d0023'
  on-secondary-fixed-variant: '#8c0056'
  tertiary-fixed: '#f4dce9'
  tertiary-fixed-dim: '#d7c0cd'
  on-tertiary-fixed: '#251820'
  on-tertiary-fixed-variant: '#52424c'
  background: '#f9f9fb'
  on-background: '#1a1c1d'
  surface-variant: '#e2e2e4'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-lg:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  headline-sm:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.03em
  currency-display:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  currency-md:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: -0.01em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  margin-tablet: 1.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system establishes a high-trust, modern retail and wealth mobile banking experience rooted in Material 3 principles, refined specifically for Android platform idioms. The aesthetic balances regulatory security with consumer warmth through an expressive palette anchored by deep magenta.

### Design Principles
- **Assured Precision**: Financial figures, transaction states, and critical actions are rendered with immediate clarity and zero ambiguity.
- **Expressive Tactility**: Uses dynamic surface layering, soft ambient tints, and fluid micro-interactions to create a native Android experience that feels responsive and reassuring.
- **Shielded Authority**: Visual motifs subtly reinforce digital fortress-grade security through grounded card containers, clear authentication barriers, and structural symmetry.

## Colors

The palette leverages a commanding deep magenta primary for core navigation and key calls to action, paired with an energetic accent and warm highlight surfaces.

### Functional Color Applications
- **Primary Brand (`#A50064`)**: Core interactive elements, key action buttons, selected navigation items, and biometric authentication prompts.
- **Accent Active (`#D82D8B`)**: Secondary highlights, interactive toggles, active progress indicators, and quick-action chips.
- **Soft Highlight Surface (`#FCE4F1`)**: Container backgrounds for critical alerts, promotional banners, and active selected list items.
- **Canvas & Surfaces**: Base application canvas rests on Neutral Background (`#F5F5F7`), elevated onto pure Neutral Surface (`#FFFFFF`) for interactable cards and sheets.
- **Dividers & Strokes**: Outlines, row separators, and field boundaries use Neutral Border (`#E0E0E5`).
- **Typography Levels**: Headlines and primary data values use Text Primary (`#222222`); descriptive labels, metadata, and helper text use Text Secondary (`#757575`).
- **Semantic Channels**:
  - Success (`#2E9E5B`): Confirmed transfers, positive balances, security validation.
  - Error (`#D93025`): Failed transactions, validation errors, account freezes.
  - Warning (`#F5A623`): Approaching limits, pending clearing periods, required KYC renewals.

## Typography

The type hierarchy prioritizes scannability and absolute numerical precision using Inter.

### Numeric & Currency Formatting
All account balances, transaction ledgers, interest rates, and numeric inputs must enable OpenType tabular figures (`tnum`) and slashed zeros (`zero`) via CSS: `font-feature-settings: "tnum" 1, "zero" 1;`. This enforces vertical column alignment across transaction lists and balance comparisons.

### Mobile Scaling Rules
- Main portfolio and ledger figures scale down from `display-lg` to `display-lg-mobile` on screens below 380dp to avoid wrapping sensitive currency amounts.
- Secondary decimal digits in account summaries drop 2px from base balance size and map to `Text Secondary` for clean visual hierarchy.

## Layout & Spacing

The layout is built for native Android viewports adhering to an 8dp grid increment, utilizing a 4dp baseline subgrid for fine typography and icon positioning.

### Grid & Viewport Behavior
- **Mobile (up to 599dp)**: 4-column fluid layout with `margin: 1rem` (16dp) and `gutter: 1rem` (16dp).
- **Foldables & Small Tablets (600dp - 839dp)**: 8-column layout with `margin-tablet: 1.5rem` (24dp) and `gutter: 1rem` (16dp). Two-pane layout pattern splits account list (left) and transaction details (right).
- **Large Displays (840dp+)**: 12-column layout max-width constrained to 720dp centered for focused security-critical operations, or expanded into master-detail dashboards.

### Vertical Rhythm
- Standard spacing between stacked content cards is `space-md` (16px).
- In-card row spacing defaults to `space-sm` (8px).
- Bottom edge scrolling clearance preserves `space-xl` (32px) above Android dynamic navigation bars.

## Elevation & Depth

Visual hierarchy employs a tonal elevation approach combined with a proprietary tinted shadow that integrates the brand’s magenta hue without visual noise.

### Shadow Characteristics
- **Standard Card Elevation**: `box-shadow: 0px 4px 16px rgba(165, 0, 100, 0.06);` provides a soft, warm lift that separates cards from the `#F5F5F7` canvas.
- **Floating Action & Dropdown**: `box-shadow: 0px 8px 24px rgba(165, 0, 100, 0.12);` used for persistent CTA bars, FABs, and floating transaction badges.
- **Modals & Dialogs**: `box-shadow: 0px 16px 32px rgba(34, 34, 34, 0.16);` paired with a 40% `#222222` backdrop scrim.

### Tonal Surface Hierarchy
1. **Level 0 (Canvas)**: Background tint `#F5F5F7`.
2. **Level 1 (Cards, List Groups)**: Pure `#FFFFFF` surface with low-contrast border `#E0E0E5` (1px) and ambient brand shadow.
3. **Level 2 (In-Card Highlights, Grouped Actions)**: Soft highlight `#FCE4F1` or secondary background `#FAFAFB` with zero shadow.
4. **Level 3 (Overlays, Bottom Sheets)**: Surface `#FFFFFF` with high-level ambient elevation and distinct structural top radius.

## Shapes

The shape system expresses security and modern mobile ergonomics using tailored radii matching distinct component classes:

- **12px (`rounded-md` equivalent)**: Interactive inputs, text fields, primary/secondary action buttons, dropdown trigger containers.
- **16px (`rounded-lg`)**: Content cards, modular dashboard widgets, notification prompts, modal dialogs.
- **24px (`rounded-xl`)**: Top borders of modal bottom sheets, transfer authorization trays, security keypads.
- **999px (Pill / Full Round)**: Account tag pills, status badges, chip filters, biometric icon containers.
- **TDT Shield Motif**: The shield geometry is restricted to branding elements, app header verification watermarks, and cryptographic confirmation seals. It uses a custom asymmetrical 16px curved corner radius terminating into a sharp base point.

## Components

### Buttons
- **Primary**: Solid `#A50064` background, `#FFFFFF` label, 12px border radius. Height: 48dp on mobile. Pressed state shifts to `#850050`. Focused state displays a 2px `#D82D8B` outline ring.
- **Secondary / Outlined**: 1px solid `#A50064` stroke on transparent or `#FFFFFF` surface. Text `#A50064`.
- **Text Button**: Zero border, `#A50064` label, 48dp min-target padding.
- **Disabled State**: Background `#E0E0E5`, text `#757575`, no shadow.

### Inputs & Form Controls
- **Text & Currency Inputs**: 12px border radius, 1px solid `#E0E0E5` border, `#FFFFFF` fill. Height: 56dp. Active focus shifts border to 2px solid `#A50064`.
- **Currency Field Variant**: Large tabular figure display (`24px`), currency symbol pinned to left in Text Secondary (`#757575`).
- **Error State**: Border shifts to 1.5px `#D93025`; helper error text rendered in `body-sm` `#D93025` with leading alert icon.

### Cards & Surfaces
- **Account Summary Card**: Surface `#FFFFFF`, 16px radius, 1px `#E0E0E5` stroke, soft brand shadow. Internal padding: 16px. Top accent edge optional with a 4px primary brand highlight bar.
- **Interactive Card**: Feedback ripple using `rgba(165, 0, 100, 0.08)`.

### Bottom Sheets
- Container surface `#FFFFFF`, top corners rounded to 24px (`rounded-top-xl`).
- Centered drag handle: 32px width by 4px height, `#E0E0E5` neutral fill, 12px top margin.
- Fixed bottom action bar anchored above the Android system navigation bar.

### Chips & Badges
- **Status Badges**: Pill-shaped (999px radius), horizontal padding 8px, vertical padding 4px.
  - Active/Success: `#2E9E5B` background at 10% opacity, solid `#2E9E5B` label.
  - Pending: `#F5A623` background at 12% opacity, solid `#B26A00` label.
  - Alert: `#D93025` background at 10% opacity, solid `#D93025` label.
- **Filter Chips**: 999px radius, 1px `#E0E0E5` border, `#FFFFFF` background. Selected state applies `#FCE4F1` fill and `#A50064` text.

### Selection Controls
- **Checkboxes & Radios**: 20dp hit area. Checked state fills `#A50064` with `#FFFFFF` check/dot mark. Unchecked state uses 1.5px `#757575` border.
- **Switches**: Track 32dp height, thumb 28dp circle. Active track uses `#A50064` with `#FFFFFF` thumb; inactive track uses `#E0E0E5` with `#FFFFFF` thumb.

### Financial Lists & Ledger Rows
- List item height: 72dp.
- Left slot: 40dp rounded circle or shield container holding category iconography in `#FCE4F1` with `#A50064` glyph.
- Center slot: Transaction counterparty title in `headline-sm`, timestamp and category below in `body-sm` (`#757575`).
- Right slot: Monospaced/tabular transaction amount in `currency-md`. Positive credits prefix `+` in `#2E9E5B`; negative debits render in `#222222`.