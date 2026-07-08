# Development

BetterHUD is built for several Minecraft families from one codebase with
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
- the publish workflow's per-jar Minecraft version lists and the manual
  Modrinth upload guide (see below).

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

### Releasing to Modrinth and CurseForge

Bump `mod_version` in `gradle.properties`, push, wait for CI to go green,
then run the **publish** workflow from the Actions tab (Run workflow). It
builds every variant and uploads each release jar as its own version on
Modrinth and CurseForge, selecting exactly the Minecraft versions that jar
covers (from `supported-versions.json`). The workflow asks for a changelog,
a release channel (release/beta/alpha), and which platforms to publish to.

It needs two repository secrets: `MODRINTH_TOKEN` (a
[Modrinth PAT](https://modrinth.com/settings/pats) with the "Create versions"
scope) and `CURSEFORGE_TOKEN` (a
[CurseForge API token](https://legacy.curseforge.com/account/api-tokens)).

For a manual upload instead, run `./gradlew modrinthBundle` (or download the
`modrinth-upload` artifact from any CI run). It produces `build/modrinth/`
containing every release jar plus `UPLOAD.md`, which lists exactly which
Minecraft versions to select for each jar.
