# Repository Guidelines

## Project Overview

**omp-terminal** is a JetBrains IntelliJ Platform plugin (`com.github.ryans233.ompterminal`, v0.0.1) that adds **oh-my-pi** to the terminal predefined session dropdown (the ▼ arrow next to the **+** new-tab button). Selecting it opens a new terminal tab named "oh-my-pi" and runs the `omp` command. Scaffolded from the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).

## Architecture & Data Flow

Single-module Gradle project following the IntelliJ Platform plugin architecture:

```
plugin.xml (extension registry)
  └── OhMyPiPredefinedTerminalProvider  → implements OpenPredefinedTerminalActionProvider
        └── listOpenPredefinedTerminalActions()
              └── OhMyPiAction           → AnAction + DumbAware
                    └── actionPerformed()  → TerminalToolWindowManager.createNewSession(tabName="oh-my-pi", shellCommand=["omp"])
```

- **Extension point**: `org.jetbrains.plugins.terminal.openPredefinedTerminalProvider` — the terminal plugin collects actions from all registered providers and displays them in the predefined session dropdown.
- **Single source file**: `OhMyPiPredefinedTerminalProvider.kt` — contains the provider class and its inner `OhMyPiAction` class.
- **Terminal API**: Uses `TerminalToolWindowManager.createNewSession(workingDirectory, tabName, shellCommand, requestFocus, deferStart)` to create a named tab and execute the command in one call.
- **Action threading**: `getActionUpdateThread()` returns `BGT` (background thread); `update()` checks `e.project != null`.
- **No services, tool windows, startup activities, i18n, coroutines, or Compose**.

## Key Directories

```
src/
  main/
    kotlin/com/github/ryans233/ompterminal/
      OhMyPiPredefinedTerminalProvider.kt  # Sole source file — provider + action
    resources/
      META-INF/plugin.xml                  # Plugin descriptor (extension registration)
  test/
    kotlin/com/github/ryans233/ompterminal/
      OhMyPiActionTest.kt                  # Single test — provider registration verification
.github/workflows/
  build.yml                                # CI: build → test → verify → draft release
  release.yml                              # CD: publish to JetBrains Marketplace
.run/                                      # IntelliJ run configurations (runIde, check, verifyPlugin)
```

## Development Commands

| Task | Command |
|------|---------|
| Build plugin ZIP | `./gradlew buildPlugin` |
| Run tests | `./gradlew check` |
| Verify plugin structure | `./gradlew verifyPlugin` |
| Launch sandboxed IDE | `./gradlew runIde` |
| Update changelog | `./gradlew patchChangelog` |

Requires **Java 21** (Zulu recommended). Gradle wrapper is pinned to **9.6.0**.

## Code Conventions & Common Patterns

- **Language**: Kotlin (version 2.1.20), targeting JVM 21.
- **Package**: `com.github.ryans233.ompterminal` — flat structure, no subpackages.
- **Naming**: PascalCase classes matching file names; camelCase methods/properties.
- **Extension points**: Implement platform interfaces (e.g. `OpenPredefinedTerminalActionProvider`) and register in `plugin.xml` under the appropriate namespace.
- **Actions**: Extend `AnAction` + `DumbAware`. Override `getActionUpdateThread()` → `BGT`, `update()` for enable/disable logic, `actionPerformed()` for the main behavior.
- **Gradle**: Configuration cache and build cache enabled. Kotlin stdlib not bundled (relies on platform).

## Important Files

| File | Purpose |
|------|---------|
| `src/main/kotlin/.../OhMyPiPredefinedTerminalProvider.kt` | Sole source file — provider + inner action |
| `src/main/resources/META-INF/plugin.xml` | Plugin descriptor — extension registration, dependencies |
| `src/test/kotlin/.../OhMyPiActionTest.kt` | Provider registration test |
| `build.gradle.kts` | Dependencies, platform target, plugin config |
| `settings.gradle.kts` | Plugin versions (Kotlin 2.1.20, IntelliJ Platform 2.16.0, Changelog 2.5.0) |
| `gradle.properties` | Group, version, repository URL, build cache settings |
| `CHANGELOG.md` | Keep-a-Changelog format, updated by CI |
| `.github/workflows/release.yml` | Marketplace publishing (needs `PUBLISH_TOKEN` + certificate secrets) |

## Runtime & Tooling

- **JDK**: 21 (CI uses Zulu distribution)
- **Gradle**: 9.6.0 via wrapper (`gradlew`/`gradlew.bat`)
- **IntelliJ Platform**: 2025.2.6.2 (IDEA Ultimate)
- **IntelliJ Platform Gradle Plugin**: 2.16.0
- **Package manager**: Gradle with Maven Central + IntelliJ Platform repositories
- **CI**: GitHub Actions — `build.yml` (push/PR), `release.yml` (GitHub release events)
- **Dependency updates**: Dependabot daily for Gradle and GitHub Actions
- **No linting/formatting tools** configured (no ktlint, detekt, spotless, or checkstyle)

## Testing & QA

- **Framework**: JUnit 4.13.2 + IntelliJ Platform Test Framework (`BasePlatformTestCase`)
- **Run tests**: `./gradlew check`
- **Test location**: `src/test/kotlin/com/github/ryans233/ompterminal/OhMyPiActionTest.kt`
- **Pattern**: Tests extend `BasePlatformTestCase`, verify extension point registration via `OpenPredefinedTerminalActionProvider.EP_NAME`
- **CI**: Tests run on every push to `main` and PRs; reports uploaded on failure
- **Plugin verification**: `./gradlew verifyPlugin` runs IntelliJ Plugin Verifier for compatibility checks
- **Coverage**: No coverage tooling configured yet

## CI/CD Pipeline

```
Push to main / PR
  → Build plugin (buildPlugin)
  → Run tests (check)
  → Verify plugin (verifyPlugin)
  → Draft GitHub release (non-PR only)

GitHub release published
  → Patch CHANGELOG.md
  → Publish to JetBrains Marketplace
  → Upload release assets
  → Create changelog-update PR
```
