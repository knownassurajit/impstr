# IMPSTR Android Game --- Complete Production Implementation Plan

Version: 2.2 (Stealth Update)\
Status: Milestone v1.1.0 COMPLETED\
Scope: Social Deduction Logic, UX Consistency, Release Build Stabilization

---

## 📋 Roadmap Completion Strategy

### ✅ Sprint 0 - Critical Lifecycle Stability (COMPLETED)
- [x] **KeepScreenOn** implementation across all game phases.
- [x] **Global Game Timer** in GameViewModel with SavedStateHandle persistence.
- [x] **PlayerCard UI** fixes and name overflow management.
- [x] **Shuffle players** logic for turn-order randomization.

### ✅ Sprint 1 - UX & Navigation Safety (COMPLETED)
- [x] **HelpDialog** reusable component for lobby and reveal screens.
- [x] **ExitConfirmationDialog** for all critical gameplay phases.
- [x] **Skip vote logic** improvements for better flow.

### ✅ Sprint 2 - Role Reveal & Design Polish (COMPLETED)
- [x] **RoleCard** 3D-flip animations and spring physics.
- [x] **Material 3 Tonal Progress Indicators**.
- [x] **ResultScreen** layout optimizations and game duration tracking.

### ✅ Sprint 3 - Design System & UI Consistency (COMPLETED)
- [x] **Centralized Token System** (`DesignSystem.kt`).
- [x] **Geometric Unification**: Corner radii (16dp/28dp) and Spacing (24dp) standardized across all screens.
- [x] **Semantic Colors**: Decoupled hex codes from logic.

### ✅ Sprint 4 - Stealth Mode Implementation (NEW / COMPLETED)
- [x] **Decoy Word Algorithm**: word-pair distribution logic.
- [x] **Stealth Mode Toggle**: Persistent game-mode state in SharedPreferences.
- [x] **R8/ProGuard Release Stabilization**: Custom rules for transitive Error Prone annotations.
- [x] **Documentation Sync**: Update all Resources and root README to v1.1.0.

---

## 🛠️ Future Roadmap

### Sprint 5 - State Persistence Scalability
- [ ] Replace SharedPreferences with Proto DataStore.
- [ ] Room Database for player statistics and game history.

### Sprint 6 - Immersion & Polish
- [ ] **Sound Effects**: Audio triggers for flips, votes, and victories.
- [ ] **Haptic Feedback**: Meaningful physical response for UI interactions.
- [ ] **Adaptive Layouts**: Support for tablets and foldables.

### Sprint 7 - Play Store Optimization
- [x] **R8 / Proguard Build Optimization**.
- [ ] Splash Screen API implementation for Android 12+.
- [ ] Firebase Analytics & Crashlytics integration.

---

## 📝 Revised Architecture Outcomes
This milestone ensures the app is **Release Ready** on a technical level. The UI is consistent, the logic is deterministic, and the release build is stabilized for the Play Store.

---
END OF DOCUMENT
