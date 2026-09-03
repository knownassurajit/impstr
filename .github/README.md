# CI/CD (`impstr/.github`)

```text
.github/
├── dependabot.yml
├── workflows/ci-cd.yml
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
| `stable-release` | push to master | signed APK, GitHub release, optional Play internal |

Dependencies live in `gradle/libs.versions.toml`. Actions are SHA-pinned.
