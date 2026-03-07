<div align="center">
  <img src="Resources/IMPSTR.svg" width="100%" alt="IMPSTR Logo">
</div>

<div align="center">
  <em>The title "IMPSTR" represents the players. The letter 'O' has been eliminated from the game, and we are highlighting 'M' because it is the imposter.</em>
</div>

<br>

<div align="center">

**A modern, offline-first social deduction game for Android built with Material Design 3**

[![Material Design 3](https://img.shields.io/badge/Material%20Design-3-blue)](https://m3.material.io/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack-Compose-brightgreen)](https://developer.android.com/jetpack/compose)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple)](https://kotlinlang.org/)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-31-orange)](https://developer.android.com/about/versions/12/behavior-changes-12)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-green)](https://developer.android.com/about/versions/)
[![Version](https://img.shields.io/badge/Version-1.0.0.1-blue)](#)

</div>

---

## 📑 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [How to Play](#-how-to-play)
- [Technology Stack](#-technology-stack)
- [Dependencies](#-dependencies)
- [Requirements](#-requirements)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [State Management](#-state-management)
- [Game Mechanics](#-game-mechanics)
- [Theme & Design System](#-theme--design-system)
- [Getting Started](#-getting-started)
- [Build & Deployment](#-build--deployment)
- [Testing](#-testing)
- [License](#-license)
- [Developer Info](#-developer-info)

---

## 📖 Overview

**Imposter** is a local multiplayer social deduction game inspired by Mafia and Among Us. Players pass a single Android device around; one player is secretly assigned as the **Imposter** while others receive a secret word. Through strategic discussion and voting, players must identify the imposter before they blend in and win!

### ✨ Key Features

- 🎮 **Pass-and-Play Multiplayer** — Single device shared among 3–10 players
- 🔒 **Fully Offline** — No internet connection required
- 🎨 **Material Design 3** — Modern, polished UI with smooth spring-physics animations
- 📂 **24 Word Categories** — Animals, Food, Movies, Space, and more
- ⚡ **Optimized Performance** — State managed with `StateFlow` and coroutine timers
- 🌙 **Dark Theme** — Beautiful dark-mode design optimised for mobile
- 🎯 **Strategic Gameplay** — Discussion timer, voting mechanics, win-condition tracking
- 💾 **Persistent Config** — Player names and settings saved via `SharedPreferences`
- 📱 **Wide Compatibility** — Android 12+ (API 31)

---

## 🎯 How to Play

### Game Setup
1. **Configure Players** — Add 3–10 player names
2. **Select Category** — Choose from 24 word categories
3. **Set Imposter Count** — 1–3 imposters recommended
4. **Start Game** — Tap **Start Game**

### Game Phases

#### Phase 1: Role Assignment
Each player privately reveals their role on the device, then hands it to the next player.

#### Phase 2: Discussion (3 minutes)
All players discuss without revealing the secret word. Skip the timer to vote early.

#### Phase 3: Voting
The host selects the player(s) most suspected of being the imposter.

#### Phase 4: Voting Results
Eliminated players' roles are revealed (imposter or crewmate).

#### Phase 5: Game Results
Final outcome shown with elimination history and total game duration.

### Win Conditions
- **Crewmates Win** — All imposters are eliminated
- **Imposters Win** — Imposters equal or outnumber crewmates

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Kotlin | 2.0.21 |
| **UI Framework** | Jetpack Compose (Material 3) | BOM 2024.10.01 |
| **Architecture** | MVVM + Unidirectional Data Flow | — |
| **State Management** | StateFlow + Coroutines | Kotlin 2.0+ |
| **Dependency Injection** | Hilt | 2.51.1 |
| **Navigation** | Navigation Compose | 2.8.3 |
| **Storage** | SharedPreferences | AndroidX |
| **Build System** | Gradle Kotlin DSL | 8.7.3 |
| **AGP** | Android Gradle Plugin | 8.7.3 |
| **Min / Target SDK** | Android 12 / Android 16 | API 31 / 36 |
| **JVM Target** | Java | 17 |

---

## 📦 Dependencies

### AndroidX & Lifecycle
- **androidx.core:core-ktx** (1.15.0)
- **androidx.lifecycle:lifecycle-runtime-ktx** (2.8.7)
- **androidx.activity:activity-compose** (1.9.3)

### Jetpack Compose & UI
- **androidx.compose:compose-bom** (2024.10.01)  
  Includes: Material 3, Material Icons Extended, Foundation, Runtime & Tooling

### Navigation & Routing
- **androidx.navigation:navigation-compose** (2.8.3)
- **androidx.hilt:hilt-navigation-compose** (1.2.0)

### Dependency Injection
- **com.google.dagger:hilt-android** (2.51.1)
- **com.google.dagger:hilt-android-compiler** (2.51.1) via KSP

### UI Utilities
- **org.burnoutcrew.composereorderable:reorderable** (0.9.6) — drag-and-drop player reordering

### Testing
- **junit:junit** (4.13.2)
- **org.mockito:mockito-core** (5.21.0)
- **org.mockito.kotlin:mockito-kotlin** (6.2.3)
- **org.jetbrains.kotlinx:kotlinx-coroutines-test** (1.10.2)
- **androidx.test.ext:junit** (1.3.0)
- **androidx.test.espresso:espresso-core** (3.7.0)

---

## 📱 Requirements

- **Android Version**: 12 (API 31) or higher
- **Storage**: ~15 MB for APK installation
- **RAM**: 512 MB minimum (1 GB+ recommended)
- **Network**: None — fully offline
- **Device**: Any Android phone/tablet with a touchscreen

---

## 🏗️ Architecture

### MVVM with Unidirectional Data Flow

```
┌──────────────────────────────────────────────────┐
│  UI Layer (Jetpack Compose)                      │
│  SplashActivity, HomeScreen, RoleRevealScreen,   │
│  DiscussionScreen, VotingScreen,                 │
│  VotingResultsScreen, ResultScreen               │
└────────────────┬─────────────────────────────────┘
                 │ collects StateFlow<GameState>
                 │ calls ViewModel.method()
                 ▼
┌──────────────────────────────────────────────────┐
│  ViewModel Layer                                 │
│  GameViewModel (Hilt Singleton)                  │
│  - Manages GameState (StateFlow)                 │
│  - Implements all game logic & phase transitions │
│  - Drives discussion/game timers (coroutines)    │
│  - Persists config via SharedPreferences         │
└────────────────┬─────────────────────────────────┘
                 │ reads
                 ▼
┌──────────────────────────────────────────────────┐
│  Data Layer                                      │
│  WordRepository (Kotlin object singleton)        │
│  - 24 word categories with random selection      │
│  ShufflePlayersUseCase                           │
│  - Randomly assigns imposter roles               │
└──────────────────────────────────────────────────┘
```

### Design Patterns
1. **MVVM** — Clear separation of UI, business logic, and data
2. **Unidirectional Data Flow** — Event → ViewModel → State → UI recompose
3. **Hilt DI** — `@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`
4. **Single Activity** — One `MainActivity` hosts all Compose screens via `NavHost`
5. **Use Cases** — `ShufflePlayersUseCase` encapsulates role-assignment logic

---

## 📂 Project Structure

```
impstr/
├── .gitignore                          # Root ignore rules
├── README.md                           # This file
│
├── Resources/
│   ├── README.md                       # Internal docs index
│   └── icon_play_store.png             # Play Store icon asset
│
└── Imposter/                           # Android project root
    ├── build.gradle.kts                # Project-level plugin versions
    ├── settings.gradle.kts
    ├── gradle.properties
    ├── gradlew / gradlew.bat
    ├── gradle/
    │   └── libs.versions.toml
    │
    └── app/
        ├── build.gradle.kts            # App-level dependencies & config
        └── src/
            ├── main/
            │   ├── AndroidManifest.xml
            │   └── java/com/game/impstr/
            │       ├── ImposterApp.kt              # @HiltAndroidApp entry point
            │       ├── MainActivity.kt             # NavHost + HelpDialog composables
            │       ├── SplashActivity.kt           # Splash screen
            │       │
            │       ├── data/
            │       │   └── WordRepository.kt       # 24 categories, random word selection
            │       │
            │       ├── domain/usecase/
            │       │   └── ShufflePlayersUseCase.kt
            │       │
            │       ├── ui/
            │       │   ├── components/
            │       │   │   ├── Components.kt       # Shared UI components
            │       │   │   ├── Dialogs.kt          # HelpDialog & other dialogs
            │       │   │   ├── FlipCard.kt         # Card-flip animation component
            │       │   │   ├── KeepScreenOn.kt     # Screen-wake utility
            │       │   │   └── TimerDisplay.kt     # Circular countdown timer
            │       │   │
            │       │   ├── screens/
            │       │   │   ├── HomeScreen.kt       # Player setup & lobby
            │       │   │   ├── RoleRevealScreen.kt # Pass-and-play role reveal
            │       │   │   ├── DiscussionScreen.kt # 3-min timed discussion
            │       │   │   ├── VotingScreen.kt     # Host voting panel
            │       │   │   ├── VotingResultsScreen.kt # Elimination results
            │       │   │   └── ResultScreen.kt     # Final game outcome
            │       │   │
            │       │   ├── theme/
            │       │   │   ├── Color.kt            # Material 3 color tokens
            │       │   │   ├── Motion.kt           # Animation specs
            │       │   │   ├── Shapes.kt           # Material 3 shape tokens
            │       │   │   ├── Theme.kt            # ImposterTheme setup
            │       │   │   └── Type.kt             # Typography scale
            │       │   │
            │       │   └── viewmodel/
            │       │       └── GameViewModel.kt    # Core game logic & StateFlow
            │       │
            │       └── res/
            │           ├── drawable/               # Icons, launcher & splash assets
            │           ├── font/                   # Custom typefaces
            │           ├── mipmap-*/               # Launcher icons (all densities)
            │           ├── values/                 # strings.xml, colors.xml
            │           └── xml/                    # Backup rules, data extraction rules
            │
            └── test/
                └── java/com/game/impstr/ui/viewmodel/
                    └── GameViewModelTest.kt        # JUnit 4 unit tests (6 tests)
```

---

## 🧠 State Management

### GameState

```kotlin
data class GameState(
    val phase: GamePhase = GamePhase.SETUP,
    val players: List<PlayerState> = emptyList(),
    val category: String = "Random Words",
    val secretWord: String = "",
    val imposterNames: List<String> = emptyList(),
    val imposterCount: Int = 1,
    val elapsedTime: Long = 0,             // Seconds elapsed in current phase
    val currentRoundNumber: Int = 1,
    val roundStartTime: Long = 0,          // Timestamp when current phase started
    val totalGameTime: Long = 0,           // Total seconds since game start
    val winner: String? = null,            // "Crewmates" | "Imposters" | null
    val currentRevealPlayerIndex: Int = 0,
    val isCardRevealed: Boolean = false,
    val isCardFlipping: Boolean = false,
    val lastEliminatedPlayer: PlayerState? = null,
    val eliminatedInCurrentRound: List<PlayerState> = emptyList(),
    val eliminationHistory: List<EliminationRecord> = emptyList(),
)
```

### GamePhase Enum

```kotlin
enum class GamePhase {
    SETUP,          // Player & category configuration
    ROLE_REVEAL,    // Each player privately sees their role
    DISCUSSION,     // 3-minute timed discussion phase
    HOST_VOTING,    // Host selects player(s) to eliminate
    VOTING_RESULTS, // Shows eliminated player(s) and their roles
    RESULT,         // Final game outcome
}
```

### PlayerState

```kotlin
data class PlayerState(
    val id: String,           // UUID — used for vote tracking
    val name: String,
    val isImposter: Boolean = false,
    val isReady: Boolean = false,
    val hasVoted: Boolean = false,
    val isEliminated: Boolean = false,
)
```

### Reactive Data Flow

```
User Action → Screen calls viewModel.method()
           → ViewModel updates MutableStateFlow<GameState>
           → StateFlow emits new GameState
           → Composable collects & recomposes
           → User sees updated UI
```

---

## 🎮 Game Mechanics

### Phase Flow

```
START
  ↓
SETUP (Player & Category Config)
  ↓ startGame()
ROLE_REVEAL (Pass-and-play, each player privately sees role)
  ↓ startDiscussion()
DISCUSSION (3-minute countdown)
  ↓ startVoting() [timer ends or host skips]
HOST_VOTING (Host selects who to eliminate)
  ↓ castVote()
VOTING_RESULTS (Show eliminated players & roles)
  ↓ checkWinConditions()
  ├── Win → RESULT (Final outcome)
  └── Continue → DISCUSSION (Next round, round counter + 1)
```

### Win Conditions

| Condition | Winner |
|-----------|--------|
| All imposters eliminated | **Crewmates** |
| Active imposters ≥ active crewmates | **Imposters** |
| ≤ 2 total active players remaining with an imposter | **Imposters** |

### Voting Logic

- `castVote(listOf("SKIP"))` — Skip this round; no elimination
- `castVote(listOf(id1, id2,...))` — Eliminate up to `activeImpostersCount` players
- Eliminated players are excluded from future votes
- `EliminationRecord` logs round number + global elimination order for the results screen

---

## 🎨 Theme & Design System

### Material Design 3

The app uses a full custom Material 3 colour scheme with dark-mode focus:

| Token | Colour | Usage |
|-------|--------|-------|
| **Primary** | `#9DB2BF` | Main interaction colour |
| **Secondary** | `#9BCBCB` | Secondary actions |
| **Tertiary** | `#BDB2D0` | Accent colour |
| **Error** | `#B3261E` | Error states |

### Animation Specs (Motion.kt)

- Default transitions: **200ms** spring with `DampingRatioMediumBouncy`
- Card flip (RoleReveal): **400ms** Y-axis rotation + opacity swap
- Vote selection: **200ms** scale + colour transition
- Screen entry/exit: slide + fade via `NavHost` `AnimatedContentTransition`

### Typography (Type.kt)

Custom font family applied to the full Material 3 type scale — Display through Label sizes.

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug or later (recommended)
- **JDK 17**
- **Android SDK** with API 31+ installed
- Gradle is bundled via the wrapper (`gradlew`)

### Clone & Open

```bash
git clone https://github.com/knownassurajit/impstr.git
cd impstr
```

Open **Android Studio** → `File → Open` → navigate to `impstr/Imposter`

Android Studio will sync Gradle automatically.

### Build & Run (CLI)

```bash
cd Imposter

# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Install on connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch app
adb shell am start -n com.game.impstr/.SplashActivity
```

---

## 🏗️ Build & Deployment

### Build Variants

| Variant | Minification | Use |
|---------|-------------|-----|
| **debug** | Off | Development & testing |
| **release** | R8 (minify + shrink) | Production distribution |

### APK Configuration

| Setting | Value |
|---------|-------|
| **App ID** | `com.game.impstr` |
| **Version Name** | 1.0.0.1 |
| **Version Code** | 2 |
| **Min SDK** | 31 (Android 12) |
| **Target SDK** | 36 (Android 16) |
| **Compile SDK** | 36 |

### Release Build

```bash
./gradlew bundleRelease   # AAB for Play Store
./gradlew assembleRelease # APK for sideloading
```

> **Note**: Configure signing in `app/build.gradle.kts` or via Android Studio's signing wizard before a release build.

---

## 🧪 Testing

### Unit Tests

**Framework**: JUnit 4 + Mockito  
**Location**: `Imposter/app/src/test/java/com/game/impstr/ui/viewmodel/GameViewModelTest.kt`

**6 test cases covering**:

| Test | Validates |
|------|-----------|
| `initial state is correct` | Default `GameState` values |
| `updatePlayerCount respects minimums and imposter limits` | Player count boundaries (min 3, imposter ceiling) |
| `startGame assigns roles correctly` | Correct imposter count assigned randomly |
| `voting eliminates players` | `castVote()` marks player eliminated, transitions to `VOTING_RESULTS` |
| `win condition - crewmates win` | Eliminating last imposter → `winner = "Crewmates"` |
| `win condition - imposters win` | Imposters reaching parity → `winner = "Imposters"` |

```bash
cd Imposter
./gradlew test   # All 6 tests pass ✅
```

### Manual Test Scenarios

1. **Basic Flow**: 4 players → pick category → start → all reveal → discuss → vote → results
2. **Crewmate wins**: Vote out imposter on first round
3. **Imposter wins**: Consistently vote out crewmates until parity
4. **Skip vote**: Use skip during voting to continue without elimination
5. **Multiple rounds**: Verify `currentRoundNumber` increments and `eliminationHistory` grows
6. **Play again**: After result screen → lobby resets player states cleanly

---

## 📝 License

This project is for educational and personal use. All rights reserved © Surajit Das.

---

## 👨‍💻 Developer

**Surajit Das**  
Made with 💙 for friends and fellow gamers.

---

<div align="center">

**Enjoy the game! 🎭**

</div>
