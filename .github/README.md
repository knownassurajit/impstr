# CI/CD (`impstr/.github`)

```text
.github/
├── dependabot.yml
├── workflows/ci-cd.yml
├── workflows/build.yml
├── workflows/play-release.yml
└── README.md
```

## Branching

- `develop` — integration
- `master` — production

## Jobs

| Job | Trigger | Purpose |
|---|---|---|
| `test` | push to develop/master, all PRs | unit tests + lint |
| `dependency-review` | PRs | high-severity advisory gate |
| `debug-release` | push to develop | unsigned debug APK pre-release |
| `pr-summary` | PRs into master | sticky summary + changelog |
| `stable-release` | push to master | signed AAB + APK, GitHub release, Play internal AAB (`com.knownassurajit.impstr_game.app`, release name `impstr`) when `PLAY_CONSOLE_JSON` is set |

`play-release.yml` is manual (`workflow_dispatch`) so master does not upload twice. `build.yml` runs on `develop` only.

Dependencies live in `gradle/libs.versions.toml`. Actions are SHA-pinned.
