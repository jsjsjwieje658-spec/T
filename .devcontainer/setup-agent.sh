#!/usr/bin/env bash
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"
rm -f .git/agent-ready

java -version
chmod +x ./gradlew
./gradlew --version --no-daemon

printf '%s\n' "$(git rev-parse HEAD)" > .git/agent-ready
