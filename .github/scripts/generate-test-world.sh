#!/usr/bin/env bash
# Generates a vanilla singleplayer world save for the given Minecraft version
# by briefly running that version's dedicated server, and places it where the
# launch test expects it (launchtest/run/saves/ci-world).
#
# Only needed for versions whose Fabric API has no client gametest module
# (before 1.21.4): their launch test cannot create a world in-game, so the
# client auto-joins this save via --quickPlaySingleplayer instead. Using the
# exact same Minecraft version to generate the world avoids any world-upgrade
# prompts when the client opens it.
set -euo pipefail

MC_VERSION="${1:?usage: generate-test-world.sh <minecraft-version>}"
DEST="$(pwd)/launchtest/run/saves/ci-world"

WORK="$(mktemp -d)"
cd "$WORK"

MANIFEST_URL="$(curl -sSf https://piston-meta.mojang.com/mc/game/version_manifest_v2.json \
	| jq -r --arg v "$MC_VERSION" '.versions[] | select(.id == $v) | .url')"
SERVER_URL="$(curl -sSf "$MANIFEST_URL" | jq -r '.downloads.server.url')"
curl -sSfo server.jar "$SERVER_URL"

echo "eula=true" > eula.txt
# A flat world keeps generation fast and renders instantly on the CI software
# renderer. The default gamemode (survival) matches the gametest-based runs.
cat > server.properties <<'EOF'
level-name=ci-world
level-type=minecraft\:flat
view-distance=4
simulation-distance=4
online-mode=false
spawn-protection=0
EOF

mkfifo console
java -Xmx1G -jar server.jar nogui < console > server-log.txt 2>&1 &
SERVER_PID=$!
exec 3> console

for _ in $(seq 1 300); do
	grep -q 'Done (' server-log.txt && break
	if ! kill -0 "$SERVER_PID" 2>/dev/null; then
		cat server-log.txt
		echo "::error::Dedicated server for $MC_VERSION exited before finishing world generation" >&2
		exit 1
	fi
	sleep 1
done
if ! grep -q 'Done (' server-log.txt; then
	cat server-log.txt
	echo "::error::Dedicated server for $MC_VERSION did not finish starting within 300s" >&2
	exit 1
fi

echo "stop" >&3
wait "$SERVER_PID"
exec 3>&-

mkdir -p "$(dirname "$DEST")"
cp -r ci-world "$DEST"
echo "Generated test world at $DEST"
