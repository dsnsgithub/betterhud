#!/usr/bin/env bash
# Derives the CI job matrices from supported-versions.json (the single source
# of truth for supported Minecraft versions) and writes them to GITHUB_OUTPUT:
#  - build:  one entry per build variant       -> { mc, java }
#  - launch: one entry per launchable version  -> { mc, java, pregen_world }
# pregen_world marks versions whose Fabric API has no client gametest module;
# their launch test needs a pre-generated world (see generate-test-world.sh).
set -euo pipefail

JSON=supported-versions.json

java_for_variant() {
	grep -oP '^java_version=\K.+' "versions/$1/gradle.properties"
}

launch="[]"
while IFS=$'\t' read -r mc variant gametest; do
	java="$(java_for_variant "$variant")"
	pregen=$([ "$gametest" = "false" ] && echo true || echo false)
	launch="$(jq -c --arg mc "$mc" --arg java "$java" --argjson pregen "$pregen" \
		'. + [{mc: $mc, java: $java, pregen_world: $pregen}]' <<<"$launch")"
done < <(jq -r 'to_entries[] | [.key, .value.variant, (.value.clientGametest | tostring)] | @tsv' "$JSON")

build="[]"
while read -r variant; do
	java="$(java_for_variant "$variant")"
	build="$(jq -c --arg mc "$variant" --arg java "$java" \
		'. + [{mc: $mc, java: $java}]' <<<"$build")"
done < <(jq -r '[.[].variant] | reduce .[] as $v ([]; if index($v) then . else . + [$v] end) | .[]' "$JSON")

echo "build=$build" >> "$GITHUB_OUTPUT"
echo "launch=$launch" >> "$GITHUB_OUTPUT"
echo "build matrix:  $build"
echo "launch matrix: $launch"
