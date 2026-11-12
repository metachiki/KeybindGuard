#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p build/core-test-classes
javac --release 21 -d build/core-test-classes \
  src/main/java/dev/keybindguard/client/KeybindFile.java \
  src/test/java/dev/keybindguard/client/KeybindFileTest.java
java -cp build/core-test-classes dev.keybindguard.client.KeybindFileTest
