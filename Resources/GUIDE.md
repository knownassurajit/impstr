# 🎭 IMPSTR Android Game - Complete Development Guide

> **Complete roadmap from importing the project to Android Studio to building and testing the app on your Pixel phone**

---

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Project Overview](#project-overview)
3. [Importing Project to Android Studio](#importing-project-to-android-studio)
4. [Understanding the Project Structure](#understanding-the-project-structure)
5. [Building the App](#building-the-app)
6. [Testing on Pixel Phone](#testing-on-pixel-phone)
7. [Code Review & Architecture](#code-review--architecture)
8. [Common Issues & Troubleshooting](#common-issues--troubleshooting)
9. [Next Steps & Enhancements](#next-steps--enhancements)

---

## Prerequisites

### Required Software

1. **Android Studio** (Ladybug 2024.2.1 or later)
   - Download from: https://developer.android.com/studio
2. **Java Development Kit (JDK)**
   - JDK 17 is required.
3. **Android SDK**
   - Minimum SDK: API 31 (Android 12)
   - Target SDK: API 36

---

## Project Overview

### What is IMPSTR?

IMPSTR is a **local multiplayer social deduction game** for Android. Key features:

- 🎮 **Pass-and-play**: Single device shared among players.
- 🎭 **Stealth Mode (v1.1.0)**: Advanced logic where imposters receive decoy words.
- 🔒 **Offline-first**: No internet required.
- 🎨 **Modern UI**: Built with Jetpack Compose & Material 3.
- 🏗️ **Clean Architecture**: MVVM pattern with Hilt DI.

### Material Design 3 Implementation

The app features a complete **Material Design 3** implementation with a centralized token system:

#### Design System (`DesignSystem.kt`)
- **Color Scheme**: Semantic tokens like `ImposterRed` and `CrewmateGreen`.
- **Dimensions**: Unified spacing (`ScreenHorizontal = 24.dp`) and button heights (`56.dp`).
- **Shapes**: Consistent corner radii for bottom bars (28dp) and cards (16dp).
- **Animations**: Standardized 200ms transitions with spring physics.

---

## Understanding the Project Structure

### Directory Layout

```
IMPSTR/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/game/impstr/
│   │       │   ├── ui/
│   │       │   │   ├── theme/
│   │       │   │   │   ├── DesignSystem.kt     # DESIGN TOKENS
│   │       │   │   │   ├── Theme.kt            # MD3 Theme
│   │       │   │   ├── screens/                # Compose screens
│   │       │   │   └── viewmodel/              # Game logic
│   │       │   └── domain/                     # Use Cases
│   └── build.gradle.kts                        # App configuration
├── Resources/                                  # Documentation & Assets
└── README.md                                   # Root documentation
```

---

## Building the App

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build (Optimized with R8)
```bash
./gradlew assembleRelease
```
*Note: R8 minification is enabled and configured with specific ProGuard rules in `app/proguard-rules.pro` to handle transitive dependency annotations.*

---

## Testing the Game Flow

### Step 1: Lobby Setup
1. Open the app to the **HomeScreen**.
2. **Toggle Game Mode**: Use the switch in the top-right corner to choose between **Normal** and **Stealth** mode.
   - **Normal**: Imposters are explicitly told "YOU ARE THE IMPOSTER".
   - **Stealth**: Imposters receive a *decoy word* from the same category as the crewmates.
3. Tap **"Start Game"**.

### Step 2: Role Reveal
1. Pass the phone to each player.
2. Players tap the card to reveal their role/word.
3. In Stealth mode, avoid reacting to the word—even if it seems strange, you might be the imposter!

### Step 3: Discussion & Voting
1. Start the 3-minute discussion.
2. Transition to voting once the group is ready.
3. The host selects suspects based on the discussion.

### Step 4: Final Results
1. Check the winner screen.
2. In Stealth mode, the **Decoy Word** is revealed in Red next to the **Secret Word** in Green.

---

## Code Review & Architecture

### Stealth Mode Logic
The `GameViewModel` uses a `WordRepository` to fetch a **WordPair**.
- If `isStealthMode` is true, crewmates get `word1` and imposters get `word2`.
- Both words come from the same semantic category to maximize bluffing potential.

### UI Consistency
All screens now inherit their dimensions and shapes from `DesignSystem.kt`. This ensures that updating a single token (e.g., `Corners.BottomBar`) propagates across every screen in the app instantly.

---

## Common Issues & Troubleshooting

### R8/ProGuard Errors
If the release build fails with "Missing classes", ensure `app/proguard-rules.pro` contains the `-dontwarn` rules for `com.google.errorprone.annotations`.

### Font Crashes
Legacy `Inter` or `Roboto` font files in older versions caused issues. The app now uses **Poppins** as the primary typeface for stability across all Android versions.
