# BetterHUD Fabric Mod
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/betterhudfabric?style=for-the-badge&logo=modrinth)
](https://modrinth.com/mod/betterhudfabric)

A simple HUD with essential information easily accessible for Minecraft Fabric. 

[Cloth Config](https://modrinth.com/mod/cloth-config), [Mod Menu](https://modrinth.com/mod/modmenu), and [Fabric API](https://modrinth.com/mod/fabric-api) are also required.

![Picture of HUD](https://cdn.modrinth.com/data/cached_images/4dd33c9ed510e100af5cc442eb93092624856ba5.png)

## Features

- **FPS**: Displays the current frames per second.
- **Ping**: Displays the player's ping to the server.
- **Momentum**: Displays the player's current momentum.
- **Coordinates**: Displays the player's current coordinates.
- **Biome**: Displays the current biome the player is in.
- **Facing**: Displays the direction the player is facing.
- **Time**: Displays the current real world time.

## Configuration (requires Mod Menu)
![General Settings](https://cdn.modrinth.com/data/cached_images/9c3ed350ef0c2206a7439d1e1c15b708aa22a332.png)
![Mod Settings](https://cdn.modrinth.com/data/cached_images/f89e15c258a5a24cfda8fe9398406a7ce16edbcc.png)
## Installation

1. Ensure you have Fabric Mod Loader installed.
2. Download the BetterHUD mod file.
3. Place the downloaded file in your Minecraft `mods` folder.

## Development

The mod is built for several Minecraft families from one codebase with
[Stonecutter](https://stonecutter.kikugie.dev/). Each build variant
(`versions/<variant>/gradle.properties`) produces one jar covering a range of
Minecraft versions.

[`supported-versions.json`](supported-versions.json) is the single source of
truth for every supported Minecraft version. It drives:

- the Stonecutter variant list (`settings.gradle`),
- the CI matrices — every push builds all variants and **launch-tests the mod
  on every supported version concurrently** (a real production client boots
  under XVFB, enters a survival world with the HUD active, screenshots it,
  and fails on any crash; the screenshots are posted as a grid on the PR),
- the per-version runtime mods used by those launch tests,
- the Modrinth upload guide (see below).

### Adding a new Minecraft version

1. Add an entry to `supported-versions.json` (keep the order oldest → newest):
   - `variant`: the build variant whose jar covers the new version. Reuse the
     newest variant if the mod still compiles and runs on it; create a new
     variant only when the new Minecraft breaks the current code.
   - `fabricApi`, `modmenu`, `cloth`: the newest releases built for that
     Minecraft version (check each project's maven or Modrinth page — the
     launch test will catch runtime-incompatible picks).
   - `clientGametest`: `true` (every version since 1.21.4 has the module).
2. Only if a new variant is needed: create `versions/<variant>/gradle.properties`
   (copy the newest one and adjust the versions and `mc_dep` range), cap the
   previous variant's `mc_dep`, and add any `//?` Stonecutter guards the new
   Minecraft requires in the sources.
3. Push. CI picks the new version up automatically and launch-tests it.

To launch-test one version locally:
`./gradlew :launchtest:runProductionClientGameTest -PtestMcVersion=<version>`

### Releasing to Modrinth

Run `./gradlew modrinthBundle` (or download the `modrinth-upload` artifact
from any CI run). It produces `build/modrinth/` containing every release jar
plus `UPLOAD.md`, which lists exactly which Minecraft versions to select for
each jar when creating the Modrinth versions.
