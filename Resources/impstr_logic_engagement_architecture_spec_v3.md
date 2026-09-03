# IMPSTR Android Game - Refined Game Logic & Technical Architecture

Version: 2.2 (Stealth Update)\
Status: Production-grade architecture sync\
Purpose: Define strongly typed, deterministic game logic and state-machine-driven architecture.

---

## 1. Data Model Sync

The project uses immutable Jetpack Compose state models to ensure thread safety and predictable recomposition.

### PlayerState
```kotlin
data class PlayerState(
    val id: String,                 // UUID from GameViewModel
    val name: String,
    val isImposter: Boolean = false,
    val isEliminated: Boolean = false,
    val isHost: Boolean = false
)
```

### GameState
```kotlin
data class GameState(
    val phase: GamePhase = GamePhase.SETUP,
    val players: List<PlayerState> = emptyList(),
    val imposterCount: Int = 1,
    val category: String = "Random Words",
    val secretWord: String = "",
    val imposterWord: String = "",         // Decoy Word for Stealth Mode
    val isStealthMode: Boolean = false,    // Persisted game mode
    val winner: String? = null             // "Crewmates" or "Imposters"
)
```

---

## 2. Deterministic State Machine Architecture

All transitions are handled by the `GameViewModel`, acting as a central State Reducer.

| Event | Input | Output State (`GamePhase`) |
|-------|-------|----------------------------|
| `startGame()` | Lobby Settings | `ROLE_REVEAL` |
| `startDiscussion()` | Reveal Complete | `DISCUSSION` |
| `startVoting()` | Discussion End | `VOTING` |
| `castVote()` | Selected Suspects | `VOTING_RESULTS` |
| `completeElimination()` | Post-Vote Check | `DISCUSSION` or `RESULT` |

---

## 3. UI Layer: The Design System Architecture

To minimize visual drift, the app utilizes a centralized **Design Token** architecture. This decouples visual styling from screen logic.

### Design System Highlights:
- **`DesignSystem.kt`**: Contains `Dimens`, `Corners`, `Anim`, and `GameColors`.
- **Consistency**: Reusable components (`SectionCard`, `HelpDialog`) consume these tokens via the standard Material 3 theme.
- **Glassmorphism**: Subtle tonal elevation and transparency applied to the HomeScreen header.

> [!NOTE]
> For detailed specifications on typography and colors, refer to [DESIGN.md](file:///Users/knownassurajit/Documents/Codes/GitHub/impstr/Resources/DESIGN.md).

---

## 4. Exception Handling & Recovery

### 4.1 Process Death Recovery
Managed via Hilt-injected `SavedStateHandle` + `SharedPreferences`.
- `GameViewModel` persists `isStealthMode` and player configurations.
- Current game phase remains in-memory but can be restored by re-initializing the view model.

### 4.2 Release Build Safety
R8/ProGuard rules are strictly defined to prevent accidental stripping of `com.google.errorprone` annotations which are transitive dependencies for `EncryptedSharedPreferences`.

---

## 5. Winning Logic Flow
The win condition check is deterministic:
1.  **Crewmates Win**: `players.count { it.isImposter && !it.isEliminated } == 0`
2.  **Imposters Win**: `activeImposters >= activeCrewmates`

---

## 6. Performance Benchmarks
- **Frame Rate**: Targeting consistent 60/120 FPS via `@Stable` state annotations.
- **State Collection**: Uses `collectAsStateWithLifecycle()` to halt CPU work when backgrounded.
- **Memory**: ProGuard rules optimized to strip unused code, reducing APK size significantly.
