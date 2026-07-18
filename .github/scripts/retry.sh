#!/usr/bin/env bash
# Runs a command and retries it on failure, to ride out transient network
# flakes (interrupted maven downloads, brief mirror outages, and the like).
#
#   retry.sh <command> [args...]
#
# RETRY_ATTEMPTS (default 3) sets the total number of attempts; the pause
# between attempts grows with each failure.
set -uo pipefail

attempts="${RETRY_ATTEMPTS:-3}"
code=0
for ((i = 1; i <= attempts; i++)); do
	"$@"
	code=$?
	[ "$code" -eq 0 ] && exit 0
	if ((i < attempts)); then
		delay=$((i * 15))
		echo "::warning::Attempt ${i}/${attempts} of '$*' failed with exit code ${code}; retrying in ${delay}s"
		sleep "$delay"
	fi
done
echo "::error::All ${attempts} attempts of '$*' failed"
exit "$code"
