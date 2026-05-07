# BetterHUD - Agent Instructions

## Cursor Cloud specific instructions

### Project overview

BetterHUD is a client-side Minecraft Fabric mod that adds a customizable HUD overlay. It is a single-module Gradle project (not a monorepo) with no external services, databases, or backends.

### Prerequisites

- **JDK 25** is required (`sourceCompatibility = JavaVersion.VERSION_25` in `build.gradle`). The update script installs `openjdk-25-jdk` via apt.
- Set `JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64` before running Gradle commands.

### Common commands

| Task | Command |
|---|---|
| Build | `JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 ./gradlew build` |
| Check (lint + tests) | `JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 ./gradlew check` |
| Run Minecraft client | `JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 ./gradlew runClient` |
| Clean | `JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 ./gradlew clean` |

### Caveats

- There are no test sources in this project (`test NO-SOURCE`); `./gradlew check` still runs `validateAccessWidener` and compilation checks.
- `./gradlew runClient` launches a full Minecraft client with the mod loaded. On a headless VM it will produce ALSA audio errors (harmless) and requires a display server (Xvfb or similar) to render.
- The first Gradle invocation downloads Minecraft JARs, mappings, and dependencies (~30s). Subsequent builds are cached and fast (~1-2s).
- Build output JAR is at `build/libs/betterhud-<version>.jar`.
- Gradle wrapper (`./gradlew`) auto-downloads Gradle 9.4.1; no manual Gradle install needed.
