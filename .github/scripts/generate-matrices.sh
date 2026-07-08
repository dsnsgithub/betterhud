#!/usr/bin/env bash
# Derives the CI job matrices from supported-versions.json (the single source
# of truth for supported Minecraft versions) and writes them to GITHUB_OUTPUT:
#  - build:   one entry per build variant       -> { mc, java }
#  - launch:  one entry per launchable version  -> { mc, java, pregen_world }
#  - publish: one entry per build variant       -> { mc, java, game_versions, range }
# pregen_world marks versions whose Fabric API has no client gametest module;
# their launch test needs a pre-generated world (see generate-test-world.sh).
# game_versions is the newline-separated list of Minecraft versions the
# variant's jar covers (fed to mc-publish), range is its human-readable form
# for the release name (e.g. "1.21-1.21.5").
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
publish="[]"
while read -r variant; do
	java="$(java_for_variant "$variant")"
	build="$(jq -c --arg mc "$variant" --arg java "$java" \
		'. + [{mc: $mc, java: $java}]' <<<"$build")"
	game_versions="$(jq -r --arg v "$variant" \
		'[to_entries[] | select(.value.variant == $v) | .key] | join("\n")' "$JSON")"
	range="$(jq -r --arg v "$variant" \
		'[to_entries[] | select(.value.variant == $v) | .key]
		 | if length == 1 then .[0] else "\(first)-\(last)" end' "$JSON")"
	publish="$(jq -c --arg mc "$variant" --arg java "$java" --arg gv "$game_versions" --arg range "$range" \
		'. + [{mc: $mc, java: $java, game_versions: $gv, range: $range}]' <<<"$publish")"
done < <(jq -r '[.[].variant] | reduce .[] as $v ([]; if index($v) then . else . + [$v] end) | .[]' "$JSON")

echo "build=$build" >> "$GITHUB_OUTPUT"
echo "launch=$launch" >> "$GITHUB_OUTPUT"
echo "publish=$publish" >> "$GITHUB_OUTPUT"
echo "build matrix:   $build"
echo "launch matrix:  $launch"
echo "publish matrix: $publish"
