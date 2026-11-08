#!/usr/bin/env bash
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"
rm -f .git/agent-ready

# Verify Java 21 toolchain inside the Codespace
java -version
javac -version

# Generate Gradle wrapper if gradle-wrapper.jar is not yet committed in the repository
if [[ ! -f gradle/wrapper/gradle-wrapper.jar ]]; then
  if command -v gradle >/dev/null 2>&1; then
    gradle wrapper --gradle-version 8.10.2 --no-daemon
  else
    echo "Neither gradle/wrapper/gradle-wrapper.jar nor system gradle found in Codespace." >&2
    exit 1
  fi
fi

chmod +x ./gradlew
./gradlew --version --no-daemon

# Marker is written ONLY after all setup checks succeed (§10 SKILL.md)
printf '%s\n' "$(git rev-parse HEAD)" > .git/agent-ready
