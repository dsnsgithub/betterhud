#!/usr/bin/env bash
# Collects the launch-test screenshots uploaded as artifacts by every matrix
# job of the current workflow run, normalizes their names, and commits them to
# the ci-screenshots branch so they can be embedded (as raw.githubusercontent
# URLs) in a PR comment and in the job summary.
#
# Leaves the normalized screenshots in screenshots-publish/ for the comment
# step: <sha>/mc-<version>-survival.png, <sha>/mc-<version>-settings.png and
# <sha>/mc-<version>-title.png.
#
# Required env: GITHUB_TOKEN, GITHUB_REPOSITORY, GITHUB_RUN_ID, GITHUB_SHA
set -euo pipefail

api() {
	curl -sSf -H "Authorization: Bearer $GITHUB_TOKEN" -H "Accept: application/vnd.github+json" "$@"
}

# --- Download every launch-screenshots-mc* artifact of this run -------------
mkdir -p artifact-zips shots
api "https://api.github.com/repos/$GITHUB_REPOSITORY/actions/runs/$GITHUB_RUN_ID/artifacts?per_page=100" > artifacts.json

while IFS=: read -r id name; do
	mc="${name#launch-screenshots-mc}"
	echo "Downloading $name"
	curl -sSfL -H "Authorization: Bearer $GITHUB_TOKEN" \
		-o "artifact-zips/$id.zip" \
		"https://api.github.com/repos/$GITHUB_REPOSITORY/actions/artifacts/$id/zip"
	mkdir -p "shots/$mc"
	unzip -oq "artifact-zips/$id.zip" -d "shots/$mc"
done < <(jq -r '.artifacts[] | select(.name | startswith("launch-screenshots-mc")) | "\(.id):\(.name)"' artifacts.json)

# --- Normalize names (gametest screenshots carry a timestamp prefix) --------
PUBLISH="screenshots-publish/$GITHUB_SHA"
mkdir -p "$PUBLISH"

for dir in shots/*/; do
	mc="$(basename "$dir")"
	for kind in survival-world settings-menu title-screen; do
		src="$(find "$dir" -name "*betterhud-$kind.png" -print -quit)"
		if [ -n "$src" ]; then
			cp "$src" "$PUBLISH/mc-$mc-${kind%%-*}.png"
		fi
	done
done

count="$(find "$PUBLISH" -name '*.png' | wc -l)"
echo "Collected $count screenshots"
if [ "$count" -eq 0 ]; then
	echo "No screenshots to publish, skipping branch update"
	exit 0
fi

# --- Commit to the ci-screenshots branch ------------------------------------
REMOTE="https://x-access-token:${GITHUB_TOKEN}@github.com/${GITHUB_REPOSITORY}.git"
if ! git clone --quiet --depth 1 --branch ci-screenshots "$REMOTE" ci-screenshots-branch 2>/dev/null; then
	mkdir -p ci-screenshots-branch
	git -C ci-screenshots-branch init --quiet -b ci-screenshots
	git -C ci-screenshots-branch remote add origin "$REMOTE"
fi

mkdir -p "ci-screenshots-branch/$GITHUB_SHA"
cp "$PUBLISH"/*.png "ci-screenshots-branch/$GITHUB_SHA/"
cd ci-screenshots-branch
git add .
if git diff --cached --quiet; then
	echo "Screenshots already published for $GITHUB_SHA"
	exit 0
fi
git -c user.name="github-actions[bot]" -c user.email="41898282+github-actions[bot]@users.noreply.github.com" \
	commit --quiet -m "CI screenshots for $GITHUB_SHA"

# Retry the push in case a concurrent run updated the branch first.
for _ in 1 2 3; do
	if git push --quiet origin ci-screenshots; then
		echo "Published screenshots to ci-screenshots/$GITHUB_SHA"
		exit 0
	fi
	git pull --quiet --rebase origin ci-screenshots || true
done
echo "::error::Could not push screenshots to the ci-screenshots branch" >&2
exit 1
