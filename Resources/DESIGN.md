# IMPSTR --- Design Philosophy & System

This document defines the visual language, user experience goals, and technical implementation of the IMPSTR Design System.

## 1. Design Vision: "The Modern Party Classic"

IMPSTR aims to bridge the gap between "Analog Party Games" and "Expressive Digital Experiences." The goal is a UI that feels **tense, responsive, and premium.**

### Core Pillars:
- **Expressive Motion**: Every action (card flip, vote selection) should have a physical, weighted feel.
- **Tension through Contrast**: High-contrast dark modes and vibrant semantic colors (Imposter Red vs. Crewmate Green) to heighten the emotional stakes.
- **Micro-Consistency**: Identical corner radii and spacing ensure the app feels like a single, cohesive unit rather than a collection of screens.

---

## 2. Technical Architecture: The Token System

To avoid "visual drift," the app uses a centralized **Design Token** architecture defined in `ui/theme/DesignSystem.kt`.

### 2.1 Spacing & Layout (`Dimens`)
- **Screen Padding**: Standardized to `24.dp` for all primary screens (`ScreenHorizontal`).
- **Button Heights**: Standardized to `56.dp` for primary actions to ensure accessibility and consistent tap targets.
- **Timer Visuals**: Defined proportions for `TimerCircleOuter` (280dp) and `TimerCircleInner` (240dp).

### 2.2 Shape Language (`Corners`)
- **Bottom Bars**: Using `RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)` to create a grounded, physical presence for navigation and primary actions.
- **Badges**: Unified rounding for status labels (e.g., "ELIMINATED", "STEALTH").
- **Buttons/Cards**: Standardized on MD3 `shapes.medium` (16dp).

### 2.3 Motion & Animation (`Anim`)
- **Duration**: Standardized at `200ms` for immediate feedback.
- **Easing**: Custom `FastOutSlowInEasing` for natural momentum.

### 2.4 Semantic Color System (`GameColors`)
We prioritize **Semantic Naming** over hex codes to allow for easy theme iterations:
- `ImposterRed`: High-intensity red for elimination and imposter reveals.
- `CrewmateGreen`: Calming success state color.
- `WinGradientRedStart / End`: Specific tokens for the high-impact victory screen.

---

## 3. UI Patterns

### 3.1 The "Glass-Effect" Header
Used in `HomeScreen`, the header combines the logo and mode toggle on a subtle `Surface` with tonal elevation to create depth without clutter.

### 3.2 The Anchored Bottom Bar
Every critical action screen (`Voting`, `Discussion`, `Result`) uses a pinned bottom bar. This ensures the "Next" or "Proceed" button is always in the same location, reducing user cognitive load during high-tension gameplay.

### 3.3 Card Morphing
The `RoleCard` in the reveal phase uses 3D-like flip animations combined with spring physics (`DampingRatioMediumBouncy`) to make the role reveal feel like a physical reveal.

---

## 4. Accessibility & UX
- **Dynamic Content**: All text labels use Material 3 `Text` tokens to ensure support for system-level font scaling.
- **Contrast**: Backgrounds are optimized for AMOLED displays (Deep Navy/Black) to reduce battery drain and eye strain.
- **Mode Clarity**: The "Normal/Stealth" toggle explicitly states its state to ensure users never start a game in a mode they didn't intend.
