# 🎮 Impstr App Module (`impstr/app`)

The `app` module contains the interactive Jetpack Compose game logic, sound synthesis engine, and UI screens for Impstr.

---

## 🏗️ Architecture & Component Layout

```text
app/
├── src/main/
│   ├── java/com/knownassurajit/impstr_game/
│   │   ├── MainActivity.kt        # Compose game host activity
│   │   ├── app/ui/
│   │   │   ├── components/        # Game logo, custom UI controls
│   │   │   └── screens/           # HomeScreen, GameScreen
│   └── res/                       # Game sound effects & drawables
└── build.gradle.kts               # Module dependencies
```

---

## ⚙️ Verification

```bash
./gradlew :app:testDebugUnitTest
```
