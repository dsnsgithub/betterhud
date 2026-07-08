#!/usr/bin/env bash
set -euo pipefail

MC_VERSION="${1:?usage: generate-test-world.sh <minecraft-version>}"
DEST="$(pwd)/src/launchtest/run/saves/ci-world"

WORK="$(mktemp -d)"
cd "$WORK"

MANIFEST_URL="$(curl -sSf https://piston-meta.mojang.com/mc/game/version_manifest_v2.json \
	| jq -r --arg v "$MC_VERSION" '.versions[] | select(.id == $v) | .url')"
SERVER_URL="$(curl -sSf "$MANIFEST_URL" | jq -r '.downloads.server.url')"
curl -sSfo server.jar "$SERVER_URL"

echo "eula=true" > eula.txt
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
