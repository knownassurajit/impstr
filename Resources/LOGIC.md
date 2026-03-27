# Game Logic Documentation (v1.1.0)

## Overview
IMPSTR is a multiplayer social deduction game. Players are assigned roles (Crewmate or IMPSTR), given a secret word (or a decoy word in Stealth Mode), and must deduce who the IMPSTR is through discussion and voting.

## Core Gameplay Logic

### 1. Game Setup
- **Players**: Minimum 3, Maximum 10.
- **IMPSTR Count**: Configurable, min 1, capped at `(playerCount / 2).coerceAtLeast(1)`.
- **Game Mode**: 
    - **Normal**: Imposters are told they are the imposter.
    - **Stealth**: Imposters receive a *decoy word* related to the crewmates' secret word.

### 2. Role & Word Assignment
Handled primarily in `GameViewModel.startGame()` and `ShufflePlayersUseCase`.

#### 2.1 Role Distribution
1.  **Shuffle**: A list of `PlayerState` is shuffled.
2.  **Assign**: The first `N` players in the shuffled list are flagged as `isImposter = true`.
3.  **Persistence**: Final states are stored to ensure consistency during orientation changes.

#### 2.2 Word Logic
- **Data Source**: `WordRepository` returns a `WordPair` (two related words from one category).
- **Crewmates**: Always receive `secretWord` (Word 1).
- **IMPSTRs**:
    - **Normal Mode**: Receive the string `null` (UI displays "YOU ARE THE IMPOSTER").
    - **Stealth Mode**: Receive the `imposterWord` (Word 2 from the pair). 

### 3. Game Flow (`GameViewModel`)

#### Phase 1: Setup (`GamePhase.SETUP`)
- Users configure players. `isStealthMode` toggle state is persisted in `SharedPreferences`.

#### Phase 2: Role Reveal (`GamePhase.ROLE_REVEAL`)
- Players flip cards to view roles. 
- In Stealth Mode, the card reveal animation and layout are identical for Crewmates and IMPSTRs to prevent accidental discovery.

#### Phase 3: Discussion (`GamePhase.DISCUSSION`)
- Starts a 3-minute `CountDownTimer`. 
- Logic: `totalGameTime` tracks the exact duration for the final results screen.

#### Phase 4: Voting (`GamePhase.VOTING`)
- **Suspect Selection**: The host selects up to `imposterCount` players.
- **Skip Logic**: If "SKIP" is chosen, no one is eliminated, and the round increases.

#### Phase 5: Voting Results (`GamePhase.VOTING_RESULTS`)
- Displays who was eliminated and their role. 
- **Elimination History**: Tracked in `eliminatedInCurrentRound` and added to a permanent history list.

#### Phase 6: Result (`GamePhase.RESULT`)
- **Imposter Wins**: `activeImposters >= activeCrewmates`.
- **Crewmates Win**: `activeImposters == 0`.
- **Stealth Reveal**: Under "Word Reveal", both the **Secret Word** (Green) and the **Decoy Word** (Red) are shown.

---

## Technical Data Structures

### PlayerState
```kotlin
data class PlayerState(
    val id: String,         // UUID for voting 
    val name: String,
    val isImposter: Boolean,
    val isEliminated: Boolean = false,
    val isHost: Boolean = false
)
```

### GameState
```kotlin
data class GameState(
    val phase: GamePhase = GamePhase.SETUP,
    val players: List<PlayerState> = emptyList(),
    val isStealthMode: Boolean = false,    // Persisted flag
    val secretWord: String = "",           // Word for Crewmates
    val imposterWord: String = "",         // Decoy Word for Stealth Mode
    val eliminatedInCurrentRound: List<PlayerState> = emptyList(),
    val winner: String? = null             // "Crewmates" or "Imposters"
)
```

---

## Winning Logic Algorithm
The `checkWinConditions()` method is executed after every elimination:
1.  Count `activeImposters` and `activeCrewmates`.
2.  If `activeImposters == 0`, set phase to `RESULT` and winner to "Crewmates".
3.  Else if `activeImposters >= activeCrewmates`, set phase to `RESULT` and winner to "Imposters".
4.  Otherwise, the game transitions back to `DISCUSSION` for a new round.
