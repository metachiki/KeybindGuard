# Publication and validation checklist

**This is a prototype source kit, not a completed Modrinth release.**

## 1. Policy: read before publishing

Modrinth's Content Rules updated August 13, 2026:
https://modrinth.com/legal/rules

They prohibit publicly publishing projects whose content is primarily or entirely a product of generative-AI output, require appropriate disclosure of substantial AI-generated content, and forbid AI-generated artwork on project pages. This project currently falls into the primarily AI-generated category and **must not be submitted to Modrinth as-is**. A code-style pass, minor cosmetic edits, or a test run alone do not make it eligible. Substantial human-authored engineering and independent review are necessary, and eligibility must be judged under the policy in force at time of submission.

Even where permitted, disclose AI assistance honestly using the appropriate project disclosure. Artwork for the project page should be made without generative AI.

## 2. Before any release

- [ ] A human developer reviews the root cause and designs/implements meaningful code changes.
- [ ] The human developer reviews licenses and project credits.
- [ ] Java 21/Gradle 8.8 clean build succeeds and produces a real NeoForge JAR.
- [ ] `bash scripts/test-core.sh` passes on Java 21.
- [ ] Launch Minecraft 1.21.1 + NeoForge 21.1.259 with the built JAR.
- [ ] Verify: key set in a mod, game exit, restart, key preserved.
- [ ] Verify: new key change survives a second restart.
- [ ] Verify: no duplicate `key_` lines in `options.txt` after multiple restarts.
- [ ] Verify: Minecraft vanilla key changes are not overwritten.
- [ ] Verify: missing mod does not trigger a crash.
- [ ] Verify: no server-side dependency is required.
- [ ] Test coexistence with major mods and document any conflicts.
- [ ] Verify `neoforge.mods.toml`, the mixin config, and JAR manifest.
- [ ] Update version numbers, project authors, and credits honestly.
- [ ] Create a non-AI-generated icon if desired.

## 3. Project metadata (when independently eligible)

- Project name: Keybind Guard
- Project type: Mod
- Game: Minecraft: Java Edition
- Game version: 1.21.1
- Loader: NeoForge
- Environment: Client required; Server unsupported/not needed
- Required dependencies: NeoForge (Java 21 is a runtime requirement)
- License: MIT
- Categories: Utility / Optimization (bugfix; choose available current tags)
- Version number: 0.1.0-beta.2 for the experimental test build
- Release channel: Alpha (until tested)
- Source link: point to **your own public repository** once it exists
- Issues link: point to your own issue tracker once it exists
- AI disclosure: accurate disclosure as required, if the project eventually qualifies

Upload the **built `.jar`**, not this source ZIP, as the primary release file. This project has not been uploaded to Modrinth.

## 4. Source references

- NeoForge key mapping docs: https://docs.neoforged.net/docs/1.21.1/misc/keymappings/
- NeoForge ModDevGradle MDK: https://github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle
- Root-cause discussion: https://github.com/Rayou444/raykeyfix
- Related MIT implementation: https://github.com/TeamMangoMilk/Keybind-Fix
- Modrinth content rules: https://modrinth.com/legal/rules
