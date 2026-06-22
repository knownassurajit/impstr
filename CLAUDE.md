# CLAUDE.md

## IMPSTR

Offline-first Android social deduction game.  
Stack: Kotlin 2.0.21 · Compose · MD3 · Hilt · MVVM/UDF  
Pkg: `com.knownassurajit.impstr_game.app` · MinSDK 31 · TargetSDK 36

---

## Core Rules

- Prefer SOLID, Clean Architecture, DRY, KISS.
- Use immutable state only.
- Every feature must include:
  - tests
  - logging
  - docs
  - lint-clean build
- Keep diffs minimal, modular, reviewable.
- Never commit secrets, generated binaries, local configs.

---

## Commands

```bash
# Build
./gradlew clean assembleDebug
./gradlew assembleRelease

# Quality
./gradlew lintDebug
./gradlew detekt
./gradlew ktlintCheck

# Tests
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest

# Coverage
./gradlew koverHtmlReport
```

---

## Architecture

```text
UI(Compose)
 → ViewModel
 → StateFlow<GameState>
 → UseCases
 → Repository
 → DataSource
```

- Pattern: MVVM + UDF
- UI observes via `collectAsStateWithLifecycle()`
- State mutation only via:
  ```kotlin
  updateState { it.copy(...) }
  ```
- Never mutate collections/state directly.

---

## Game Flow

```text
SETUP
→ ROLE_REVEAL
→ DISCUSSION
→ HOST_VOTING
→ VOTING_RESULTS
→ RESULT
```

Loop voting until win condition.

---

## State Standards

- `GameState` + `PlayerState` remain `@Stable`
- Prefer sealed UI events/actions
- One source of truth
- No business logic in composables
- Use `SavedStateHandle` for recovery
- Cancel coroutines before relaunching timers/jobs

---

## Compose Standards

- One composable = one responsibility
- Extract reusable UI
- Use previews
- No hardcoded dimensions/colors/strings
- Use:
  - Material3 tokens
  - `DesignSystem.kt`
  - string resources
- Avoid unnecessary recomposition:
  - `remember`
  - `derivedStateOf`
  - immutable params

---

## Kotlin Standards

### Required

- Explicit visibility modifiers
- Max file size: ~500 LOC
- Prefer expression bodies
- Prefer `val`
- Use extension functions for reuse
- Use data/sealed classes appropriately

### Avoid

- God classes
- Deep nesting
- Unsafe null handling
- Magic numbers
- Global mutable state

---

## Naming

```text
Screen      → HomeScreen
ViewModel   → GameViewModel
State       → GameUiState
Event       → GameEvent
UseCase     → ShufflePlayersUseCase
Repository  → WordRepository
```

---

## Dependency Injection

- Hilt only
- Constructor injection preferred
- Avoid service locators/singletons unless justified

Annotations:
- `@HiltAndroidApp`
- `@AndroidEntryPoint`
- `@HiltViewModel`

---

## Persistence

- `EncryptedSharedPreferences`
- AES256-GCM only
- Never store secrets in plaintext
- Persist:
  - settings
  - session-safe state
  - user prefs

---

## Logging

Use Timber.

Rules:
- No sensitive data in logs
- Log:
  - crashes
  - state failures
  - network/storage failures
  - navigation errors
- Remove debug spam before release

Example:
```kotlin
Timber.e(exception, "Vote submission failed")
```

---

## Error Handling

- Use sealed `Result`
- Fail gracefully
- Never silently swallow exceptions
- Surface user-safe UI messages

---

## Testing

### Unit
- JUnit4/5
- Mockito/MockK
- Coroutines test APIs

### UI
- Compose UI tests
- Espresso only if required

### E2E
Cover:
- full game lifecycle
- rotation/process death
- stealth mode
- voting logic
- persistence recovery

Naming:
```text
method_condition_expectedResult
```

Target:
- critical logic coverage ≥80%

---

## Lint / Static Analysis

Required:
- ktlint
- detekt
- Android Lint

CI must fail on:
- lint errors
- test failures
- formatting violations

---

## Repo Structure

```text
app/
 ├─ ui/
 ├─ domain/
 ├─ data/
 ├─ di/
 ├─ testing/
 └─ util/

docs/
scripts/
.github/
```

---

## Git Standards

### Branches

```text
main
develop
feature/*
fix/*
release/*
hotfix/*
```

### Commit Format

```text
feat:
fix:
refactor:
test:
docs:
build:
ci:
chore:
```

### PR Rules

- squash before merge
- no direct push to main
- PR must pass CI
- require review approval

---

## .gitignore

Ignore:
```text
/build
/.gradle
/local.properties
*.keystore
*.jks
*.apk
/secrets.properties
.idea/
```

Track empty dirs using:
```text
.gitkeep
```

---

## CI/CD

Pipeline stages:

```text
lint
→ unit-test
→ ui-test
→ coverage
→ assemble
→ sign
→ release
```

Required:
- GitHub Actions
- version tagging
- artifact retention
- signed releases
- rollback-ready artifacts

Secrets:
```text
SIGNING_KEY
ALIAS
KEY_STORE_PASSWORD
KEY_PASSWORD
```

---

## Documentation Rules

Update `README.md` after EVERY:
- feature
- architecture change
- dependency change
- setup change
- CI/CD change

README must contain:
- setup
- architecture
- screenshots
- build steps
- release flow
- testing flow
- contribution guide

Maintain:
```text
CHANGELOG.md
/docs
```

---

## Security

- Enable R8 in release
- Obfuscate release builds
- Validate all external input
- No hardcoded secrets/tokens
- Use least-privilege permissions

---

## Performance

- Avoid blocking main thread
- Prefer coroutines + Flow
- Use baseline profiles if needed
- Optimize recomposition
- Profile before micro-optimizing

---

## Release Checklist

Before release:
- all tests pass
- lint clean
- changelog updated
- README updated
- version bumped
- release notes generated
- signed APK/AAB verified
- crash reporting enabled

---

## Done Criteria

Feature is complete only if:
- implementation finished
- tests added
- docs updated
- CI passes
- reviewed
- release-safe
