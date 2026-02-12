# 🎭 Imposter Word Game – Android App Design & Development Document

## Overview

This document describes the architecture, workflow, and technical design for a **local multiplayer social deduction Android game**, inspired by “Imposter / Mafia / Among Us”-style mechanics.

- Single device, pass-and-play  
- Offline-first  
- Persistent local statistics  
- Polished UI (design handled separately)

---

## 1. Core Game Concept

- N players share one device.
- One common word per round.
- One randomly selected **Imposter**.
- All players except the imposter see the word.
- Imposter sees a blank or special message.
- Players discuss verbally.
- Players vote to identify the imposter.
- Correct vote → imposter revealed.
- Wrong vote → accused player eliminated.
- Game continues until win condition.
- History and statistics stored locally.

---

## 2. Technology Stack

- Language: Kotlin  
- UI: Jetpack Compose  
- Architecture: MVVM  
- Database: Room (SQLite)  
- Async: Kotlin Coroutines + StateFlow  
- Dependency Injection: Hilt  

---

## 3. Architecture Overview

UI (Compose)  
→ ViewModel (Game Logic)  
→ Repository (Data Access)  
→ Room Database (Local Storage)

---

## 4. Game Flow

1. Player Setup  
2. Round Initialization  
3. Word Reveal (pass-and-play)  
4. Discussion  
5. Voting  
6. Result Evaluation  
7. Persistence  
8. Next Round or Game Over  

---

## 5. Word Management

- Predefined word list bundled locally
- Random selection per round
- Same word for all non-imposters

---

## 6. Persistence Strategy

- Room entities for:
  - Game statistics
  - Round history
- Offline-first
- Append-only history

---

## 7. Future Enhancements

- Animations
- Sound effects
- Timers
- Player stats
- Online multiplayer

---

## 8. Play Store Readiness

- Signed App Bundle
- No dangerous permissions
- Local-only data
- Simple privacy policy

---

## Final Note

This project is ideal for mastering modern Android development with real-world architecture and game logic.
