# 🎭 Imposter - Android Social Deduction Game

<div align="center">

**A modern, offline-first social deduction game for Android with Material Design 3**

[![Material Design 3](https://img.shields.io/badge/Material%20Design-3-blue)](https://m3.material.io/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack-Compose-brightgreen)](https://developer.android.com/jetpack/compose)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.21-purple)](https://kotlinlang.org/)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-orange)](https://developer.android.com/about/versions/oreo)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34-green)](https://developer.android.com/about/versions/android-14)

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
- [Component Architecture](#-component-architecture)
- [Game Mechanics](#-game-mechanics)
- [Theme & Design System](#-theme--design-system)
- [Feature Screens](#-feature-screens)
- [Configuration Files](#-configuration-files)
- [Getting Started](#-getting-started)
- [Build & Deployment](#-build--deployment)
- [Testing](#-testing)
- [License](#-license)
- [Developer Info](#-developer-info)
- [Documentation](#-documentation)

---

## 📖 Overview

**Imposter** is a local multiplayer social deduction game inspired by Mafia and Among Us. Players pass a single Android device around, with one player secretly assigned as the "imposter" while others receive a secret word. Through strategic discussion and voting, players must identify the imposter before they blend in and win!

### ✨ Key Features

- 🎮 **Pass-and-Play Multiplayer** - Single device shared among 3-10 players
- 🔒 **Fully Offline** - No internet connection required
- 🎨 **Material Design 3** - Modern, polished UI with smooth animations (200ms spring physics)
- 📂 **24 Word Categories** - Diverse game content from Animals to 90s Nostalgia
- ⚡ **Optimized Performance** - Smooth animations with spring-based easing
- 🌙 **Dark Theme** - Beautiful dark mode design optimized for mobile
- 🎯 **Strategic Gameplay** - Discussion phase timer, voting mechanics, win condition tracking
- 📱 **Wide Compatibility** - Supports Android 8.0 (API 26) and above

---

## 🎯 How to Play

### Game Setup
1. **Configure Players** - Add 3-10 player names
2. **Select Category** - Choose from 24 word categories (Animals, Food, Movies, etc.)
3. **Set Imposter Count** - Decide how many imposters (1-3 recommended)
4. **Start Game** - Press "Start Game" button

### Game Phases

#### Phase 1: Role Assignment
Each player passes the device around privately. Both imposters and crewmates see their role, then card flips to hide identity.

#### Phase 2: Discussion (3 minutes)
All players discuss to identify suspicious behavior without revealing the secret word. Can skip timer to start voting early.

#### Phase 3: Voting
Players secretly select who they suspect is the imposter and confirm their vote.

#### Phase 4: Voting Results
Eliminated players are revealed along with their actual role (imposter or civilian).

#### Phase 5: Game Results
See final outcome and decide to play again or return to lobby.

### Win Conditions
- **Crewmates Win**: All imposters are eliminated
- **Imposters Win**: Imposters equal or outnumber crewmates at any point

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Kotlin | 1.9.21 |
| **UI Framework** | Jetpack Compose | Material 3 |
| **Architecture** | MVVM + Unidirectional Data Flow | - |
| **State Management** | StateFlow + Coroutines | Kotlin 1.7+ |
| **Dependency Injection** | Hilt | 2.50 |
| **Navigation** | Navigation Compose | 2.7.6 |
| **Storage** | SharedPreferences | AndroidX |
| **Build System** | Gradle (Kotlin DSL) | 8.3.2 |
| **Min/Target SDK** | Android 8.0 / Android 14 | API 26/34 |
| **JVM Target** | Java | 17 |

---

## 📦 Dependencies

### AndroidX & Lifecycle
- **androidx.core:core-ktx** (1.12.0) - Core Android utilities and Kotlin extensions
- **androidx.lifecycle:lifecycle-runtime-ktx** (2.7.0) - Lifecycle-aware coroutine scopes
- **androidx.activity:activity-compose** (1.8.2) - Activity integration with Compose

### Jetpack Compose & UI
- **androidx.compose:compose-bom** (2023.08.00) - Bill of Materials for Compose
  - Material 3 - Material Design 3 components and theming
  - Material Icons Extended - Comprehensive icon library
  - Foundation - Core layout and primitives
  - Runtime - Reactive composition engine

### Navigation & Routing
- **androidx.navigation:navigation-compose** (2.7.6) - Screen navigation and routing
- **androidx.hilt:hilt-navigation-compose** (1.1.0) - Hilt integration with Navigation

### Dependency Injection
- **com.google.dagger:hilt-android** (2.50) - Dependency injection framework
- **com.google.dagger:hilt-android-compiler** (2.50) - KSP compiler plugin

### Data Persistence
- **androidx.room:room-runtime** (2.6.1) - SQLite database abstraction
- **androidx.room:room-ktx** (2.6.1) - Room Kotlin extensions with coroutines
- **androidx.room:room-compiler** (2.6.1) - Room code generator

### UI Utilities
- **org.burnoutcrew.composereorderable:reorderable** (0.9.6) - Drag-and-drop reordering for lists

### Testing
- **junit:junit** (4.13.2) - JUnit testing framework
- **org.mockito:mockito-core** (5.10.0) - Mocking library
- **org.mockito.kotlin:mockito-kotlin** (5.2.1) - Kotlin DSL for Mockito
- **org.jetbrains.kotlinx:kotlinx-coroutines-test** (1.7.3) - Coroutine testing utilities
- **androidx.test.ext:junit** (1.1.5) - AndroidX JUnit extensions
- **androidx.test.espresso:espresso-core** (3.5.1) - UI testing framework

### Build Tools
- **Android SDK Build-Tools**: 34.0.0
- **Gradle Wrapper**: 8.3.2

---

## 📱 Requirements

- **Android Version**: 8.0 (API 26) or higher
- **Storage Space**: ~15 MB for APK installation
- **RAM**: 512 MB minimum (1 GB+ recommended)
- **Network**: None - fully offline capable
- **Device**: Any Android phone/tablet with a touchscreen

---

## 🏗️ Architecture

### Architecture Pattern: MVVM with Unidirectional Data Flow

```
┌─────────────────────────────────────────────────┐
│     UI Layer (Jetpack Compose)                  │
│  HomeScreen, RoleRevealScreen, DiscussionScreen│
│  VotingScreen, VotingResultsScreen, ResultScreen
└──────────────────┬──────────────────────────────┘
                   │ observes StateFlow<GameState>
                   │ calls ViewModel.method()
                   ▼
┌─────────────────────────────────────────────────┐
│     ViewModel Layer                             │
│     GameViewModel (Hilt Singleton)              │
│     - Manages GameState (StateFlow)             │
│     - Implements game logic                     │
│     - Handles player actions                    │
│     - Manages SharedPreferences persistence    │
└──────────────────┬──────────────────────────────┘
                   │ uses/reads
                   ▼
┌─────────────────────────────────────────────────┐
│     Data Layer                                  │
│     - WordRepository (Kotlin Singleton)         │
│     - SharedPreferences (player config)         │
│     - 24 word categories with random selection  │
└─────────────────────────────────────────────────┘
```

### Design Patterns Used

1. **MVVM Pattern**
   - Clear separation between UI, business logic, and data
   - ViewModel survives configuration changes
   - Reactive state management via StateFlow

2. **Unidirectional Data Flow (UDF)**
   - User Action → ViewModel → State Update → UI Recomposition
   - Single source of truth (GameState)
   - Deterministic UI based on state

3. **Dependency Injection with Hilt**
   - `@HiltAndroidApp` on ImposterApp - Initializes Hilt
   - `@AndroidEntryPoint` on MainActivity - Enables dependency injection
   - `@HiltViewModel` on GameViewModel - Marks ViewModel for injection
   - Automatic injection of ApplicationContext where needed

4. **Singleton Pattern**
   - GameViewModel - Single instance per application lifecycle
   - WordRepository - Kotlin `object` singleton for word categories

5. **State Management with StateFlow**
   - Reactive, coroutine-based state
   - `MutableStateFlow<GameState>` internally
   - Exposed as `StateFlow<GameState>` for read-only consumption
   - Proper scope management with `viewModelScope`

6. **Single Activity Architecture**
   - One MainActivity hosts all UI composables
   - Navigation handled by NavController and NavGraph
   - Leverages Activity lifecycle with ViewModelStore

---

## 📂 Project Structure

```
Imposter/
│
├── app/
│   ├── build.gradle.kts                          # App build configuration
│   │   ├── Dependencies definitions
│   │   ├── Compose configuration
│   │   └── KSP compiler configuration
│   │
│   ├── src/main/
│   │   ├── java/com/example/imposter/
│   │   │   ├── ImposterApp.kt                    # @HiltAndroidApp - DI entry point
│   │   │   │
│   │   │   ├── MainActivity.kt                   # Single @AndroidEntryPoint activity
│   │   │   │   ├── NavHost and NavGraph setup
│   │   │   │   ├── Navigation routes
│   │   │   │   └── Transition animations
│   │   │   │
│   │   │   ├── data/                             # Data Layer (Singleton)
│   │   │   │   └── WordRepository.kt
│   │   │   │       ├── 24 word categories
│   │   │   │       ├── Random word selection
│   │   │   │       └── Category management
│   │   │   │
│   │   │   ├── di/                               # Dependency Injection (if needed)
│   │   │   │   └── [Hilt modules]
│   │   │   │
│   │   │   ├── ui/
│   │   │   │   ├── screens/                      # Composable Screens
│   │   │   │   │   ├── HomeScreen.kt             # Player setup & lobby
│   │   │   │   │   ├── RoleRevealScreen.kt       # Pass-and-play role reveal
│   │   │   │   │   ├── DiscussionScreen.kt       # 3-min discussion phase
│   │   │   │   │   ├── VotingScreen.kt           # Player voting
│   │   │   │   │   ├── VotingResultsScreen.kt    # Elimination results
│   │   │   │   │   └── ResultScreen.kt           # Final game outcome
│   │   │   │   │
│   │   │   │   ├── components/                   # Reusable Components
│   │   │   │   │   └── Components.kt
│   │   │   │   │       ├── Player card components
│   │   │   │   │       ├── Role reveal cards
│   │   │   │   │       └── Vote buttons/selections
│   │   │   │   │
│   │   │   │   ├── theme/                        # Material Design 3 Theming
│   │   │   │   │   ├── Color.kt                  # Color tokens (30+ colors)
│   │   │   │   │   ├── Theme.kt                  # Light/Dark theme setup
│   │   │   │   │   └── Type.kt                   # Typography scale
│   │   │   │   │
│   │   │   │   └── viewmodel/                    # State Management
│   │   │   │       └── GameViewModel.kt          # Core game logic with StateFlow
│   │   │   │
│   │   │   └── res/                              # Android Resources
│   │   │       ├── values/
│   │   │       │   ├── strings.xml               # String resources
│   │   │       │   └── colors.xml                # Legacy color definitions
│   │   │       └── raw/                          # Raw asset files
│   │   │
│   │   └── AndroidManifest.xml                   # App manifest configuration
│   │
│   └── src/test/
│       └── java/com/example/imposter/
│           └── ui/viewmodel/
│               └── GameViewModelTest.kt          # ViewModel unit tests
│
├── build.gradle.kts                              # Project-level build configuration
├── gradle.properties                             # Gradle configuration
├── README.md                                     # Documentation
├── GUIDE.md                                      # Developer guide
└── CHANGELOG.md                                  # Version history

```

### Layer Organization

**Presentation Layer (ui/)**
- Composable screens handling user interaction
- Reactive UI updates based on state changes
- Material Design 3 components and theming

**ViewModel Layer (ui/viewmodel/)**
- SingleViewModel managing game state with StateFlow
- Business logic implementation
- SharedPreferences integration for persistence

**Data Layer (data/)**
- WordRepository singleton providing game content
- Category management and word selection logic

---

## 🧠 State Management

### GameViewModel Responsibilities

The `GameViewModel` class manages all game state and orchestrates game logic:

```kotlin
// Key state exposed as StateFlow
val gameState: StateFlow<GameState>

// Configuration management
fun updatePlayerCount(count: Int)
fun updatePlayerName(index: Int, name: String)
fun updateImposterCount(count: Int)
fun updateCategory(category: String)

// Game flow control
fun startGame()           // Initialize game with random assignments
fun startDiscussion()     // Begin 3-minute discussion phase
fun startVoting()         // Transition to voting phase
fun castVote(voterId: String, targetId: String)  // Record player vote
fun resetGame()           // Reset for new round
fun checkWinConditions()  // Determine if game is over
```

### GameState Data Model

```kotlin
data class GameState(
    val phase: GamePhase,                    // Current game phase
    val players: List<PlayerState>,          // All players in game
    val category: String,                    // Selected word category
    val secretWord: String,                  // The crewmates' secret word
    val imposterNames: List<String>,         // Names of all imposters
    val imposterCount: Int,                  // Number of imposters
    val elapsedTime: Long,                   // Time in current phase (ms)
    val roundStartTime: Long,                // Phase start timestamp
    val totalGameTime: Long,                 // Total game duration (ms)
    val winner: String?,                     // "CREWMATES" or "IMPOSTERS"
    val currentRevealPlayerIndex: Int,       // Which player is revealing role
    val isCardRevealed: Boolean,              // Is the current role visible?
    val isCardFlipping: Boolean,              // Flip animation in progress?
    val lastEliminatedPlayer: PlayerState?,  // Most recently voted out
    val eliminatedInCurrentRound: List<PlayerState>  // All eliminated this round
)
```

### GamePhase Enum

```kotlin
enum class GamePhase {
    SETUP,              // Player and category configuration
    ROLE_REVEAL,        // Each player privately reveals their role
    DISCUSSION,         // 3-minute timed discussion phase
    VOTING,             // Players vote for suspected imposters
    VOTING_RESULTS,     // Show eliminated players and their roles
    REVEAL,             // Transition phase
    RESULT              // Final game outcome display
}
```

### PlayerState Data Model

```kotlin
data class PlayerState(
    val id: String,             // Unique player identifier
    val name: String,           // Player display name
    val isImposter: Boolean,    // True if player is impostor
    val isReady: Boolean,       // Ready for next phase?
    val hasVoted: Boolean,      // Has voted in current voting phase?
    val isEliminated: Boolean   // Voted out of game?
)
```

### Reactive Data Flow

1. **User Action** → Screen calls `viewModel.method()` (e.g., `castVote()`)
2. **State Update** → ViewModel updates `MutableStateFlow`
3. **Emission** → StateFlow emits new `GameState`
4. **Observation** → All collecting composables receive new state
5. **Recomposition** → Jetpack Compose automatically updates UI
6. **Display** → User sees updated game state

---

## 🎨 Component Architecture

### UI Screens (Presentation Layer)

#### HomeScreen
**Responsibility**: Game setup and lobby

**Features**:
- Player list with add/remove/reorder/rename functionality
- Category selector (dropdown or grid of 24 categories)
- Imposter count configuration (1-3)
- Help dialog with game rules
- Settings access
- Start Game button

**State Dependencies**:
- `gameState.players` - Display player list
- `gameState.category` - Show selected category
- `gameState.imposterCount` - Display imposter configuration

**User Actions**:
- Add/remove/rename/reorder players
- Select category
- Change imposter count
- Start game

#### RoleRevealScreen
**Responsibility**: Pass-and-play role assignment

**Features**:
- Card flip animation (400ms with easing)
- Shows secret word for crewmates
- Shows "IMPOSTER" for imposters
- Displays category context
- Progress bar showing reveal progress
- Next button to proceed
- Prevents peeking at previous roles

**State Dependencies**:
- `gameState.currentRevealPlayerIndex` - Which player is revealing
- `gameState.isCardFlipped` - Animation state
- `gameState.secretWord` - Word to display
- `gameState.category` - Category context
- `gameState.imposterNames` - Imposter identification

**User Actions**:
- Swipe/tap to flip card
- Confirm reveal (Next button)

#### DiscussionScreen
**Responsibility**: Timed discussion phase management

**Features**:
- 3-minute countdown timer
- Circular progress indicator with color transitions
- Time remaining display
- "Start Voting" button to skip timer
- Background music/haptic feedback indicators
- Player names on screen for context

**State Dependencies**:
- `gameState.elapsedTime` - Timer progress
- `gameState.players` - Display all players

**User Actions**:
- Wait for timer or tap "Start Voting"
- Skip timer to begin voting early

#### VotingScreen
**Responsibility**: Collection and management of player votes

**Features**:
- Grid of player cards (selectable)
- Shows all non-eliminated players
- Animated selection states with spring physics
- Vote confirmation button
- Prevents voting for self
- Prevents voting for already-eliminated players

**State Dependencies**:
- `gameState.players` - Display voteable players
- `gameState.currentPlayer` - Prevent self-voting

**User Actions**:
- Select a player to vote for
- Confirm vote

#### VotingResultsScreen
**Responsibility**: Display voting outcome and elimination

**Features**:
- Shows eliminated player(s)
- Displays their role (imposter/crewmate)
- Category and secret word revelation
- Option to vote again (if multiple imposters)
- Option to end game

**State Dependencies**:
- `gameState.lastEliminatedPlayer` - Who was voted out
- `gameState.secretWord` - Reveal word after elimination
- `gameState.category` - Context for word

**User Actions**:
- Continue voting or end game

#### ResultScreen
**Responsibility**: Display final game outcome

**Features**:
- Large "IMPOSTERS WIN" or "CREWMATES WIN" headline
- Lists actual imposters
- Displays secret word
- Shows game statistics
- Entrance animations with scale effects
- Play again button (returns to SETUP)
- Return to home button

**State Dependencies**:
- `gameState.winner` - Determine outcome message
- `gameState.imposterNames` - Show who was imposter
- `gameState.secretWord` - Reveal word

**User Actions**:
- Play again or return home

### Reusable Components (ui/components/)

**Components.kt** contains:
- PlayerCard - Displays player name, avatar, role
- RoleCard - Flip animation wrapper for role reveal
- VoteButton - Animated vote selection state
- ProgressIndicators - Custom progress visualization
- TimerDisplay - Animated countdown timer
- ScaleIndicator - Visual timer representation

---

## 🎮 Game Mechanics

### Phase Transitions & State Flow

```
START
  ↓
SETUP (Player & Category Config)
  ↓
ROLE_REVEAL (Each player sees their role secretly)
  ↓
DISCUSSION (3-minute debate)
  ↓
[Timer Ends or Skip]
  ↓
VOTING (Players vote for imposter)
  ↓
VOTING_RESULTS (Show eliminated player)
  ↓
┌─────────────────────┐
│ Check Win Condition │
└────────┬────────────┘
         │
    ┌────┴─────┐
    │           │
  GAME OVER   CONTINUE
    │           │
    ↓           ↓
  RESULT    ROLE_REVEAL
    │       (Next round)
    │           │
    └───────────┘
            ↓
       HOME (New Game)
```

### Win/Lose Conditions

**Game ends when:**

1. **All Imposters Eliminated** → Crewmates Win
   - All players with `isImposter: true` have `isEliminated: true`
   - Crewmates achieve goal of identifying and eliminating all imposters

2. **Imposters ≥ Crewmates** → Imposters Win
   - `imposterCount >= (totalPlayers - imposterCount)`
   - Imposters gain majority or equality
   - Indicates imposters have successfully infiltrated

3. **Specific Voting Scenarios**:
   - If imposter is voted out → Check if all imposters eliminated
   - If crewmate is voted out → Check if imposters now have majority
   - If tie vote → Implement tiebreaker logic

### Player Elimination Logic

1. **During Voting Phase**:
   - Each player casts a vote via VotingScreen
   - Votes are tallied in ViewModel

2. **After Vote Tallying**:
   - Player with most votes is marked `isEliminated: true`
   - If multiple imposters, may eliminate multiple players

3. **Voting Results Display**:
   - Show who was eliminated
   - Reveal if they were imposter or crewmate
   - Display their role card

4. **Continuation Logic**:
   - If game not over, reset voting flags
   - Transition back to ROLE_REVEAL for next round
   - If game over, transition to RESULT phase

### Category Selection & Word Assignment

1. **24 Categories Available**:
   - Animals, Food & Drinks, Countries, Sports & Activities
   - Movies & TV, Professions, Celebrities, Historical Figures
   - World Cities, Famous Characters, Brands, Instruments
   - Technology, Nature, Mythology, Space & Astronomy
   - Vehicles, Education, Beach, Halloween, Christmas
   - Superheroes, 90s Nostalgia, Science & Lab

2. **Random Word Selection**:
   - Called via `WordRepository.getRandomWord(category)`
   - Returns random word from selected category
   - Same word assigned to all crewmates

3. **Imposter Assignment**:
   - Selected randomly from player list
   - Multiple imposters supported (1-3 recommended)
   - Cannot determine imposters from word knowledge

---

## 🎨 Theme & Design System

### Material Design 3 Implementation

The app implements Material Design 3 principles with a custom color scheme and complete component redesign.

### Color Palette

**Primary Colors**:
- Primary: `#9DB2BF` (Light Blue) - Main interaction color
- On Primary: `#FFFFFF` - Text/icons on primary background
- Primary Container: `#D5E4EB` - Secondary primary color
- On Primary Container: `#1B3F4F` - Text on primary container

**Secondary Colors**:
- Secondary: `#9BCBCB` (Teal) - Secondary action color
- On Secondary: `#FFFFFF` - Text on secondary
- Secondary Container: `#D0F8F7` - Light secondary background
- On Secondary Container: `#1F4747` - Text on secondary container

**Tertiary Colors**:
- Tertiary: `#BDB2D0` (Purple) - Accent color
- On Tertiary: `#FFFFFF` - Text on tertiary
- Tertiary Container: `#EFDEFF` - Light tertiary background
- On Tertiary Container: `#4A4161` - Text on tertiary container

**Semantic Colors**:
- Error: `#B3261E` (Red) - Error states, dangers
- On Error: `#FFFFFF` - Text on error backgrounds
- Error Container: `#F9DEDC` - Light error background
- Success: `#4CAF50` (Green) - Success states
- Warning: `#FF9800` (Orange) - Warning states
- Info: `#2196F3` (Blue) - Information states

**Surface Colors**:
- Surface: `#FFFBFE` - Default background
- On Surface: `#1C1B1F` - Default text
- Surface Variant: `#E7E0EC` - Secondary background
- On Surface Variant: `#49454E` - Secondary text

### Typography Scale

**Display Styles**:
- Display Large: 57sp, Regular weight
- Display Medium: 45sp, Regular weight
- Display Small: 36sp, Regular weight

**Headline Styles**:
- Headline Large: 32sp, Bold weight
- Headline Medium: 28sp, Bold weight
- Headline Small: 24sp, Bold weight

**Title Styles**:
- Title Large: 22sp, Bold weight
- Title Medium: 16sp, Medium weight
- Title Small: 14sp, Medium weight

**Body Styles**:
- Body Large: 16sp, Regular weight
- Body Medium: 14sp, Regular weight
- Body Small: 12sp, Regular weight

**Label Styles**:
- Label Large: 14sp, Medium weight
- Label Medium: 12sp, Medium weight
- Label Small: 11sp, Medium weight

### Material 3 Components Used

| Component | Usage |
|-----------|-------|
| **ElevatedCard** | Player cards, role cards, result displays |
| **FilledButton** | Primary actions (Start Game, Confirm) |
| **FilledTonalButton** | Secondary actions (Skip, Continue) |
| **OutlinedButton** | Tertiary actions (Help, Settings) |
| **ModalBottomSheet** | Configuration dialogs (players, category) |
| **LinearProgressIndicator** | Role reveal progress tracking |
| **CircularProgressIndicator** | Discussion timer visualization |
| **TopAppBar** | Screen headers and navigation |
| **Snackbar** | Transient notifications |

### Animation Specifications

**Default Animations**:
- Duration: 200ms (optimized from initial 300ms)
- Spring Physics: `DampingRatioMediumBouncy`
- Easing: Cubic Bezier for smooth transitions

**Specific Animations**:
- **Card Flip** (Role Reveal): 400ms with rotate + opacity
- **Vote Selection**: 200ms scale + color transition with spring
- **Player Elimination**: 300ms scale + fade combination
- **Screen Entry**: 200ms slide up + fade
- **List Items**: `animateItemPlacement` for reordering

**Color Transitions**:
- Timer green → yellow → red as time depletes
- Selection outline: primary → secondary on toggle
- Voted state: surface → secondary container

---

## 📱 Feature Screens - Detailed Breakdown

### HomeScreen - Detailed UX

**Layout Structure**:
1. **Top Bar** - "Imposter Game" title with settings icon
2. **Player Section**
   - Player list with reorder-capable cards
   - Each card shows: name, number, remove button
   - Add player button at bottom
   - Name input dialog for new/edited players

3. **Configuration Section**
   - Category selector (dropdown or expandable list)
   - Current category display
   - Imposter count selector (1-3 with +/- buttons)

4. **Action Bar**
   - Help button (opens game rules dialog)
   - Start Game button (primary, enabled when 3+ players)

**Transitions**:
- Slide up from bottom when entering from Result screen
- Fade out when starting game

**Data Validation**:
- Minimum 3 players required
- All players must have names
- Category must be selected
- Imposter count must be 1-3
- Imposter count cannot exceed player count - 1

---

### RoleRevealScreen - Detailed UX

**Layout Structure**:
1. **Top Progress**
   - Linear progress bar showing: 1/5, 2/5, etc.
   - Current player name in center

2. **Main Card Area**
   - Large center card with flip animation
   - Card starts face-down (hidden)
   - On swipe/tap: rotates 180° with flip effect
   - Shows role: "IMPOSTER!" or secret word + category

3. **Role Content**
   - If Imposter: Large bold "IMPOSTER" text
   - If Crewmate: Shows word and category for reference
   - Player name displayed below role

4. **Navigation**
   - Next button (bottom right)
   - Automatically hides previous role info
   - Last player triggers transition to Discussion

**Animations**:
- **Flip Animation**: 400ms duration
  - Y-axis rotation: 0° → 90° (at midpoint, swap content) → 180°
  - Opacity change during flip for visual effect
  - Spring physics for natural feel

**Data Security**:
- Only current player can see role
- Previous player roles hidden
- Prevents peeking by auto-hiding content

---

### DiscussionScreen - Detailed UX

**Layout Structure**:
1. **Top Section**
   - "Discussion Phase" heading
   - Player names reminder: "Discuss without revealing the word"

2. **Center Timer**
   - Large circular progress indicator (300dp)
   - Time remaining text: "MM:SS"
   - Color coding: Green (>1min) → Yellow (<1min) → Red (<10sec)

3. **Player List**
   - Scrollable list of all players
   - Each player shows name and role icon (if revealed)

4. **Bottom Actions**
   - "Start Voting" button (skip timer)
   - Auto-transitions when timer = 0

**Timer Behavior**:
- Countdown from 180 seconds (3 minutes)
- Updates every 100ms for smooth animation
- Color transitions are fluid

**Interruptions**:
- Players can skip timer with "Start Voting" button
- Back button confirms exit (prevents accidental exit)

---

### VotingScreen - Detailed UX

**Layout Structure**:
1. **Top Bar**
   - "Who is the Imposter?" heading

2. **Player Grid**
   - Displays all non-eliminated players
   - Each player as selectable card
   - Cards are tappable for selection
   - Selected card highlights with primary color

3. **Selection State**
   - Prior unselected: surface color, outline
   - Selected: primary container, filled, checkmark
   - Transition: 200ms spring animation

4. **Voting Controls**
   - "Confirm Vote" button (enabled when player selected)
   - Clear Vote button (secondary action)

**Safety Features**:
- Cannot vote for self
- Cannot vote for already-eliminated players
- Must confirm vote (prevents accidental submission)
- Visual feedback for selection

**Animations**:
- Scale: 1.0 → 1.05 on selection
- Color transition: surface → primary container
- Checkmark appears with animation

---

### VotingResultsScreen - Detailed UX

**Layout Structure**:
1. **Elimination Notice**
   - Large heading: "{PlayerName} was eliminated!"
   - Subheading shows role: "They were the IMPOSTER" or "They were a CREWMATE"

2. **Role Reveal Card**
   - Shows eliminated player's card details
   - Displays role with large icon

3. **Game Status**
   - Shows remaining players
   - Indicates if game is continuing or ending

4. **Actions**
   - "Continue" button (if game continues to next round)
   - "End Game" button (if game should finish after this)

**Transitions**:
- If game continues: Back to ROLE_REVEAL for next round
- If game ends: Transition to RESULT screen

---

### ResultScreen - Detailed UX

**Layout Structure**:
1. **Game Duration Header**
   - Central placement at the top displaying the total time taken and the number of rounds played.

2. **Victory/Defeat Card**
   - Large icon and background color indicating the winner (green for crewmates, red for imposters).
   - "The Imposter was..." text and dynamic list of actual imposters.
   - Section dedicated to displaying the Secret Word and Category.

3. **Elimination History Card**
   - A separate elevated card listing the chronological order of player eliminations and the rounds in which they occurred.

4. **Actions**
   - "Play Again" button → Resets the entire game state and navigates back to the Lobby / Setup phase.

**Animations**:
- **Entry Animation**: Scale up (0.8 → 1.0) + Fade in
- **Text Animation**: Each section slides in with stagger
- Duration: 200ms per element with 100ms delay between

---

## ⚙️ Configuration Files

### app/build.gradle.kts

**Purpose**: Defines app-level build configuration, dependencies, and compiler settings

**Key Sections**:

```
Plugins:
- com.android.application          # Android app plugin
- kotlin-android                   # Kotlin support
- kotlin-kapt / com.google.devtools.ksp.gradle-plugin  # Code generation for DI

Android Configuration:
- compileSdk: 34                   # Target Android 14
- minSdk: 26                       # Android 8.0 support
- targetSdk: 34                    # Latest Android 14

Compose:
- composeOptions.kotlinCompilerExtensionVersion = "1.5.6"  # MD3 support

Java/Kotlin:
- sourceCompatibility = JavaVersion.VERSION_17
- targetCompatibility = JavaVersion.VERSION_17
```

**Dependency Management**:
- Uses BOM (Bill of Materials) for Compose versions
- Hilt for dependency injection
- Room for data persistence
- KSP (Kotlin Symbol Processing) for faster compilation

### build.gradle.kts (Project Level)

**Purpose**: Defines shared versions for all plugins across the project

**Key Versions**:
- Android Gradle Plugin: 8.3.2
- Kotlin: 1.9.21
- Hilt: 2.50
- KSP: 1.9.21-1.0.15

**Plugin Definitions**:
- `android-application` (8.3.2)
- `kotlin-android` (1.9.21)
- `kotlin-kapt` (1.9.21)
- `com.google.dagger.hilt.android` (2.50)

---

### gradle.properties

**Purpose**: JVM and Gradle-level configuration

**Settings**:
```properties
android.useAndroidX=true           # Use AndroidX libraries
org.gradle.jvmargs=-Xmx2048m       # Max heap size for build process
org.gradle.caching=true            # Enable build caching
JAVA_HOME=/path/to/jdk-21          # Specify JDK 21 path
```

**Impact**:
- AndroidX: Ensures modern Android library compatibility
- JVM args: Prevents OutOfMemory during large builds
- Build caching: Speeds up incremental builds

### AndroidManifest.xml

**Purpose**: App configuration and system declaration

**Key Declarations**:

```xml
<application
    android:name=".ImposterApp"     <!-- Hilt-enabled Application class -->
    android:icon="@drawable/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/Theme.Imposter">

    <activity
        android:name=".MainActivity"
        android:exported="true">    <!-- Main entry point -->
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>

<!-- Permissions: None required (fully offline) -->
```

**Features**:
- No internet permissions required (offline game)
- No camera/microphone permissions (text-based game)
- Minimal system resource requirements

---

## 🚀 Getting Started

### For Players

1. **Download**: Get APK from releases
2. **Install**: Open APK and install on Android device
3. **Launch**: Tap icon to start playing
4. **Gather**: Invite 3-10 players with their own faces 😄
5. **Play**: Follow in-app instructions

### For Developers

**Prerequisites**:
- Android Studio (latest version)
- JDK 17 or higher
- Android SDK with API level 34 installed
- Gradle 8.3.2 (included in project)

**Setup Steps**:

1. **Clone Repository**:
   ```bash
   git clone https://github.com/knownassurajit/impstr.git
   cd impstr
   ```

2. **Open in Android Studio**:
   ```
   File → Open → Navigate to impstr/Imposter
   ```

3. **Sync Gradle**:
   - Android Studio automatically syncs dependencies
   - Wait for indexing to complete

4. **Configure Device**:
   - Plug in Android device (USB debugging enabled)
   - OR launch Android Emulator

5. **Build & Run**:
   ```bash
   cd Imposter
   ./gradlew assembleDebug
   ```

6. **Install APK**:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

7. **Launch App**:
   ```bash
   adb shell am start -n com.example.imposter/.MainActivity
   ```

### Quick Development Build

```bash
./gradlew runDebug          # Build and run directly
./gradlew assembleDebug     # Build debug APK
./gradlew spotlessApply     # Format code (if configured)
./gradlew test              # Run unit tests
```

---

## 🏗️ Build & Deployment

### Build Variants

**Debug Build**:
- Development-focused
- Includes debug symbols
- Faster builds
- Full logging enabled
- Used for testing and development

**Release Build**:
- Production-ready
- Minified with R8
- Optimized for size and performance
- Debug symbols stripped
- Signed with app signing key

### Build Process

```bash
# Generate APK
./gradlew assembleDebug

# Build output location
app/build/outputs/apk/debug/app-debug.apk

# With version
./gradlew assembleDebug -Pversion=1.0.0
```

### APK Configuration

| Setting | Value |
|---------|-------|
| **App ID** | com.example.imposter |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 34 (Android 14) |
| **Expected Size** | ~15 MB |
| **Languages** | English (extensible) |

### Device Compatibility

**Tested On**:
- Android 8.0-14 devices
- Phone and tablet form factors
- Various screen sizes (4" to 7")

**Minimum Requirements**:
- 512 MB RAM (1 GB recommended)
- 15 MB free storage
- Touch screen support
- Kotlin stdlib support

### Testing & QA

**Pre-Release Checklist**:
- [ ] All screens render correctly
- [ ] Game flow completes without crashes
- [ ] Navigation between screens works
- [ ] State persistence in SharedPreferences
- [ ] Animations smooth on target devices
- [ ] No memory leaks in long play sessions
- [ ] Device rotation handled gracefully

---

## 🧪 Testing

### Unit Tests

**Location**: `Imposter/app/src/test/java/com/example/imposter/`

**Test Framework**: JUnit 4 with Mockito

**GameViewModelTest.kt**:
Tests core game logic including:
- Player initialization
- Role assignment randomization
- Win condition checking
- State transitions
- Vote counting

**Running Tests**:
```bash
./gradlew test                          # Run all tests
./gradlew test --tests GameViewModelTest  # Specific test class
```

### Manual Testing Workflow

**Scenario 1: Basic Game Flow**
1. Launch app → HomeScreen appears
2. Add 4 players (3 crewmates, 1 imposter)
3. Select "Animals" category
4. Set 1 imposter
5. Tap "Start Game"

**Scenario 2: Role Reveal**
1. Player 1 swipes card → See "DOG"
2. Next Player → See "DOG"
3. Next Player → See "DOG"
4. If Imposter → See "IMPOSTER"

**Scenario 3: Discussion**
1. Discuss for 30 seconds (don't need full 3 minutes)
2. Tap "Start Voting"
3. Should transition to VotingScreen

**Scenario 4: Voting**
1. Each player selects suspected imposter
2. Confirm votes
3. Most voted player eliminated

**Scenario 5: Game Result**
1. See elimination results
2. If game continues → Back to role reveal
3. If game ends → See ResultScreen
4. Tap "Play Again" → Back to HomeScreen

### Debug Configuration

**Enable Logging**:
- BuildConfig.DEBUG check before logging
- Timber or Log wrapper for organized output

**Performance Profiling**:
- Use Android Studio Profiler
- Monitor memory, CPU, FPS during gameplay
- Check for jank during animations

**Device Testing Checklist**:
```
☐ Minimum API 26 device (Android 8.0)
☐ Latest API 34 device (Android 14)
☐ Emulator with standard and large screens
☐ Device with low RAM (1 GB)
☐ Device with large RAM for stress testing
```

---

## 📝 License

This project is for educational and personal use.

---

## 👨‍💻 Developer

**Surajit Das**

Made with ❤️ for MSD-BI-IN and friends

---

## 📚 Documentation

- **[GUIDE.md](GUIDE.md)** - Complete development guide and architecture deep-dive
- **[CHANGELOG.md](CHANGELOG.md)** - Detailed version history and updates
- **[Material Design 3](https://m3.material.io/)** - Official Material Design guidelines
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Compose documentation
- **[Hilt Documentation](https://dagger.dev/hilt/)** - Dependency injection framework
- **[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** - Async programming

---

## 🔄 Recent Updates

### v0.0.4 - Material Design 3 Redesign & Repository Cleanup (2026-02-15)

**Major Changes**:
- ✨ Complete Material Design 3 implementation across all screens
- 🎨 Modern color palette with semantic color tokens (primary, secondary, tertiary)
- ⚡ Optimized animations (300ms → 200ms) with spring-based easing
- 🧹 Comprehensive repository cleanup and file organization
- 📱 Improved responsive design for various screen sizes
- 🔧 Fixed API compatibility issues with latest Android versions

**Screens Updated**:
- HomeScreen: ModalBottomSheet for configuration dialogs
- RoleRevealScreen: Enhanced card flip animations, LinearProgressIndicator
- DiscussionScreen: CircularProgressIndicator with color transitions
- VotingScreen: Smooth selection animations with spring physics
- ResultScreen: Entrance animations with scale effects

**Technical Improvements**:
- Upgraded to Kotlin 1.9.21
- Updated Jetpack Compose to latest BOM
- Improved ViewModel state management
- Enhanced SharedPreferences persistence
- Optimized recomposition performance

See [CHANGELOG.md](CHANGELOG.md) for full version history.

---

<div align="center">

**Enjoy the game! 🎭**

Questions or suggestions? Feel free to reach out!

</div>
