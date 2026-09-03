# IMPSTR Android Game --- Implementation Plan & Screen Flow

Version: 2.2 (Stealth Update)\
Status: Complete v1.1.0 Roadmap Milestone\
Scope: Stealth Mode, Design Tokens, Release Stabilization

---

## 1. Roadmap Progress

### ✅ Completed: Phase 1 (Core Reliability)
- [x] **KeepScreenOn implementation**: Discussion, Voting, Results.
- [x] **Global Game Timer**: Count-up timer in GameViewModel surviving recreation.
- [x] **Player UI Polish**: Fix overflow and alignment in PlayerCards.
- [x] **Lobby Shuffle**: Randomizer for turn order.
- [x] **Host Management**: Visual badges for the device holder.

### ✅ Completed: Phase 2 (Architecture & Consistency)
- [x] **Help Components**: Reusable `HelpDialog`.
- [x] **Navigation Guard**: `ExitConfirmationDialog` for all critical screens.
- [x] **UI Token Migration**: Replaced magic numbers with `DesignSystem.kt` constants.
- [x] **Result Screen Redesign**: Pinned "Play Again" button and separation of History sections.

### 🚀 NEW: Phase 3 (Gameplay Depth - v1.1.0)
- [x] **Stealth Mode Logic**: Implement `WordPair` decoy word system in `WordRepository`.
- [x] **Mode Persistence**: State-aware `SharedPreferences` for game mode toggle.
- [x] **Release Build Optimization**: ProGuard rules for Error Prone annotations.
- [x] **Documentation Overhaul**: Sync all Resources and root README.

---

## 2. Reusable Component Matrix

| Component | Purpose | Status |
|-----------|---------|--------|
| **`DesignSystem.kt`** | **Central Style Provider** | **NEW / ACTIVE** |
| `KeepScreenOn` | Prevent screen sleep | Active |
| `SectionCard` | Unified layout container | Active |
| `ExitConfirmationDialog` | Safety during back-press | Active |
| `TimerDisplay` | Consistent time rendering | Active |
| `ImpstrLogo` | Context-aware help trigger | Active |

---

## 3. Revised Screen Flow (Stealth Update)

1.  **HomeScreen**: Setup mode (Normal/Stealth) + Shuffling.
2.  **RoleRevealScreen**: 3D Flip-card with decoy word support.
3.  **DiscussionScreen**: Progress circle timer for social deduction.
4.  **VotingScreen**: Host-driven suspicion selection.
5.  **VotingResultsScreen**: Elimination logs.
6.  **ResultScreen**: Victory reveal + Double-word reveal (Secret vs. Decoy).

---

## 4. Final Completion Status

This implementation plan is considered **CLOSED** for the v1.1.0 milestone. Future sprints will focus on:
- **Sound Effects & Haptics**: Immersive audio triggers.
- **Dynamic Word Downloads**: External category management.
- **Achievements System**: Competitive progression tracking.

---

END OF DOCUMENT
