# Changelog

All notable changes to this project will be documented in this file.

## [2.2.0] - 2026-03-27

### 🎭 Stealth Mode Update & UI Polish

A major gameplay and visual overhaul introducing the **Stealth Mode** mechanic and a centralized, token-based **Design System**.

#### Added
- **Stealth Mode**: New gameplay logic where Imposters receive a "Decoy Word" from the same category as the "Secret Word", enhancing the bluffing experience.
- **Stealth Toggle**: Added a branded "Normal / Stealth" toggle on the `HomeScreen` with persistent state via `SharedPreferences`.
- **Design System (`DesignSystem.kt`)**: Implemented a centralized token system for architecture-wide UI consistency:
    - `Dimens`: Standardized spacing (24dp horizontal padding), button heights, and elevation.
    - `Corners`: Unified shape language for bottom bars (28dp top radius), buttons (16dp), and badges.
    - `GameColors`: Semantic color naming for game-specific states (Imposter Red, Crewmate Green, Victory Gradients).
    - `Anim`: Standardized animation durations (200ms) and easing functions.
- **DESIGN.md**: Comprehensive documentation on the project's visual philosophy and technical UI architecture.

#### Changed
- **ResultScreen Redesign**: The "Play Again" button is now pinned to a fixed bottom bar, ensuring accessibility regardless of scroll height.
- **Cross-Screen Audit**: Replaced all hardcoded spacing and corner values across all 5 primary screens with design system tokens.
- **Release Build Optimization**: Fully enabled and stabilized R8/ProGuard minification for production releases.

#### Fixed
- **ProGuard `dontwarn` rules**: Resolved missing class warnings for transitive Google Error Prone annotations during release builds.
- **Toggle State Persistence**: Ensured the game mode persists accurately between app sessions.

---

## [2.1.0] - 2026-02-20

### 🔄 Refactoring & Redesign

Complete codebase beautification and documentation pass, accompanied by key logic fixes and a Result Screen redesign.

#### Added
- **Documentation**: Comprehensive KDoc and inline comments added to `GameViewModel.kt` and other key components to improve developer experience.
- **Result Screen Redesign**: 
  - Extracted the total game duration to a dedicated top-middle surface.
  - Separated the Elimination History into its own distinct, elevated card to prevent UI clutter.
  - Dynamically adjusting UI elements based on the winning team and game state.

#### Fixed
- **KeepScreenOn Component**: Simplified `KeepScreenOn.kt` to flawlessly maintain the `FLAG_KEEP_SCREEN_ON` awake state during Discussion, Voting, and Voting Results phases, preventing unwanted screen dimming.
- **Game Timer Logic**: Clarified and ensured the total gameplay timer correctly encapsulates the full session without resetting, accurately reflecting round durations on the Result Screen.
- **Code Formatting**: Whole-project codebase beautification applying consistent Kotlin formatting via `ktlint`.


## [2.0.0] - 2026-02-15

### 🎨 Material Design 3 Redesign

Complete redesign of the application with Material Design 3 principles, modern UI components, and optimized animations.

#### Added

**Theme System**
- Complete Material Design 3 color scheme with semantic colors
  - Primary: `#9DB2BF` (Light Blue)
  - Secondary: `#9BCBCB` (Teal)
  - Tertiary: `#BDB2D0` (Purple)
  - Semantic colors: Success, Error, Warning, Info
- Full MD3 typography scale (Display, Headline, Title, Body, Label)
- Dynamic color support for Android 12+
- System theme preference support (dark/light mode)

**UI Components**
- `ModalBottomSheet` for player and category configuration (HomeScreen)
- `ElevatedCard` components with proper elevation and shadows
- `LinearProgressIndicator` for role reveal progress tracking
- `CircularProgressIndicator` for discussion timer visualization
- `FilledTonalButton` for primary actions
- `OutlinedButton` for secondary actions
- Animated selection states with spring physics (VotingScreen)

**Animation Enhancements**
- Optimized animation duration from 300ms to 200ms
- Spring-based animations with `DampingRatioMediumBouncy`
- `animateItemPlacement` for smooth list reordering
- Entrance animations with scale effects (ResultScreen)
- Smooth fade and scale transitions (RoleRevealScreen)
- Color transitions for low-time warnings (DiscussionScreen)

#### Changed

**Screen Redesigns**
- **HomeScreen.kt**: Complete redesign with `ModalBottomSheet`, `ElevatedCard`, and spring animations
- **RoleRevealScreen.kt**: Added `LinearProgressIndicator`, optimized reveal animations to 200ms
- **DiscussionScreen.kt**: Implemented `CircularProgressIndicator` timer, removed blur modifier
- **VotingScreen.kt**: Added animated selection states with spring physics, `Surface` for bottom bar
- **ResultScreen.kt**: Implemented entrance animations with scale effects, gradient backgrounds
- **SettingsScreen.kt**: Already Material 3 compliant, no changes needed

**Build Configuration**
- Updated `build.gradle.kts` with opt-in annotations for experimental Material 3 APIs
- Added `freeCompilerArgs` to suppress experimental API warnings
- Configured for Compose BOM 2023.08.00 compatibility

**API Compatibility Fixes**
- Replaced `surfaceContainer` with `surface` (not available in Compose BOM 2023.08.00)
- Updated `LinearProgressIndicator` API from lambda syntax to direct parameter
- Updated `CircularProgressIndicator` API from lambda syntax to direct parameter
- Replaced unavailable Material Icons:
  - `Timer` → `Info`
  - `Category` → `Info`
  - `AccessTime` → `Info`

#### Removed

**Code Cleanup**
- Removed `imposter_android_game_design.md` (old design document)
- Removed `stitch_design_t1.html` (old design mockup)
- Removed `java_pid6163.hprof` (heap dump file, 688MB)
- Removed `build_log.txt` (temporary build log)
- Removed blur modifier from DiscussionScreen (not compatible with minSdk 26)
- Removed Room Database (switched to SharedPreferences and in-memory state)

#### Fixed

**Build Issues**
- Fixed compilation errors related to newer Material 3 APIs
- Added global opt-in for experimental Material 3 APIs
- Fixed icon import issues (replaced unavailable icons)
- Resolved API compatibility issues with Compose BOM 2023.08.00

**Theme Issues**
- Fixed MainActivity to use system theme preference instead of forcing dark theme
- Added missing `Color` import in Theme.kt
- Removed unsupported `surfaceContainer` properties from color scheme

### 🔧 Technical Details

**Build Status**: ✅ Successful
```
BUILD SUCCESSFUL in 20s
39 actionable tasks: 14 executed, 25 up-to-date
```

**Warnings (Non-Critical)**:
- Variable 'uiState' is never used in MainActivity.kt (line 34)
- Variable 'borderWidth' is never used in VotingScreen.kt (line 215)

---

## [1.0.0] - 2026-02-14

- Initial release with Jetpack Compose support.
- Core game logic for civilian/imposter deduction.
- Pass-and-play mechanic.
