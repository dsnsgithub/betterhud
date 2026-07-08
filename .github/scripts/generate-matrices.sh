#!/usr/bin/env bash
set -euo pipefail

releases="$(curl -sSf https://piston-meta.mojang.com/mc/game/version_manifest_v2.json \
	| jq -c '[.versions[] | select(.type == "release") | .id] | reverse')"

expand_mc_dep() {
	jq -nc --argjson releases "$releases" --arg dep "$1" '
		def key: split(".") | map(tonumber);
		[$releases[] | . as $id | select(
			all($dep | split(" ")[] | capture("(?<op>>=|<=|<|>)(?<ver>.+)");
				if .op == ">=" then ($id | key) >= (.ver | key)
				elif .op == "<=" then ($id | key) <= (.ver | key)
				elif .op == ">" then ($id | key) > (.ver | key)
				else ($id | key) < (.ver | key) end))]'
}

prop() {
	grep -oP "^$2=\K.+" "versions/$1/gradle.properties"
}

build="[]"
launch="[]"
publish="[]"
for variant in $(ls versions | sort -V); do
	java="$(prop "$variant" java_version)"
	mc_versions="$(expand_mc_dep "$(prop "$variant" mc_dep)")"

	build="$(jq -c --arg mc "$variant" --arg java "$java" \
		'. + [{mc: $mc, java: $java}]' <<<"$build")"
	launch="$(jq -c --argjson mcs "$mc_versions" --arg java "$java" \
		'. + [$mcs[] | {mc: ., java: $java}]' <<<"$launch")"

	game_versions="$(jq -r 'join("\n")' <<<"$mc_versions")"
	range="$(jq -r 'if length == 1 then .[0] else "\(first)-\(last)" end' <<<"$mc_versions")"
	publish="$(jq -c --arg mc "$variant" --arg java "$java" --arg gv "$game_versions" --arg range "$range" \
		'. + [{mc: $mc, java: $java, game_versions: $gv, range: $range}]' <<<"$publish")"
done

echo "build=$build" >> "$GITHUB_OUTPUT"
echo "launch=$launch" >> "$GITHUB_OUTPUT"
echo "publish=$publish" >> "$GITHUB_OUTPUT"
echo "build matrix:   $build"
echo "launch matrix:  $launch"
echo "publish matrix: $publish"
