# Development

BetterHUD is built to support several Minecraft versions from one codebase with
[Stonecutter](https://stonecutter.kikugie.dev/). Each build variant
(`versions/<variant>/gradle.properties`) produces one jar covering the range of
Minecraft versions in its `mc_dep` property.

The `versions/` folder is the single source of truth for what is supported:

- CI expands each variant's `mc_dep` range against Mojang's version manifest to
  get the exact Minecraft versions to build, launch-test, and publish for.
- The launch tests resolve the latest Fabric API, Mod Menu, and Cloth Config
  releases for each Minecraft version from Modrinth — the same versions a
  launcher would install alongside the mod.

### Adding a new Minecraft version

- If the newest variant's `mc_dep` range already covers it (for example a new
  patch release), there is nothing to do — CI picks it up automatically.
- Otherwise create `versions/<variant>/gradle.properties` (copy the newest one
  and adjust the versions and the `mc_dep` range), cap the previous variant's
  `mc_dep`, and add any `//?` Stonecutter guards the new Minecraft requires in
  the sources.

To launch-test one version locally (generate the test world once per version):

```
bash .github/scripts/generate-test-world.sh <version>
./gradlew :launchtest:runLaunchTest -PtestMcVersion=<version>
```

### Releasing to Modrinth and CurseForge

Bump `mod_version` in `gradle.properties`, push, wait for CI to go green,
then run the **publish** workflow from the Actions tab (Run workflow).
The workflow asks for a changelog, a release channel (release/beta/alpha), and which platforms to publish to.

It needs two repository secrets: `MODRINTH_TOKEN` (a
[Modrinth PAT](https://modrinth.com/settings/pats) with the "Create versions"
scope) and `CURSEFORGE_TOKEN` (a
[CurseForge API token](https://legacy.curseforge.com/account/api-tokens)).

For a manual upload instead, run `./gradlew modrinthBundle` (or download the
`modrinth-upload` artifact from any CI run). It produces `build/modrinth/`
containing every release jar plus `UPLOAD.md`, which lists each jar's
Minecraft version range.
