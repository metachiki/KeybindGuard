# Keybind Guard (NeoForge 1.21.1)

**Status: experimental source package / not yet a verified mod release.**

Keybind Guard is a client-side workaround for a startup problem in Minecraft 1.21.1 + NeoForge 21.1.x. Some mod-added key mappings are erased from `options.txt` when the game saves the file before all mod keys have been registered.

## What it does

- Intercepts option-file loading and saving in the Minecraft client.
- Takes one fixed snapshot of the `key_...` lines that existed before startup saves begin.
- During startup only, restores missing key mappings and user values replaced with defaults by an early save.
- Reloads options once at the first client tick, after mod key registrations should have completed, then permanently disables protection for the rest of the session.
- Does not send data to the internet and requires no server-side installation.

## Compatibility

- Minecraft **1.21.1 only**
- NeoForge **21.1.230+ within the 21.1 line** (built against **21.1.259**)
- Java **21**
- Client only; no optional library mods are required.

**Not yet verified in a running Minecraft client.** Build validation and real-world tests are required before distributing a playable release.

## Building

Install **JDK 21** and **Gradle 8.8**, then in this repository run:

```bash
gradle --no-daemon clean build
```

The intended artifact is `build/libs/keybindguard-1.21.1-0.1.0-beta.2.jar`.

Or upload this source to a GitHub repository and run the included **Actions → Build and test mod** workflow. The workflow installs Java/Gradle, runs unit tests, builds the NeoForge mod, checks its contents, and uploads the build artifact. A green run is *not* a substitute for testing in Minecraft.

A standalone test of the key-line preservation logic (no Minecraft dependencies) is included:

```bash
bash scripts/test-core.sh
```

On Windows: `scripts\test-core.bat`.

## Installing a verified build

1. Shut down Minecraft.
2. Back up your instance's `options.txt` (and keep a separate copy of the entire instance if possible).
3. Put the built JAR in the **client** instance's `mods` directory.
4. Temporarily disable other options/keybind restoration mods during diagnosis.
5. Start Minecraft, customize a **mod-added** key, quit normally, and start again.
6. Confirm the key mapping persists. If it does not, read `logs/latest.log` and file an issue with the relevant lines and mod list.

**Important:** The mod cannot reconstruct values already lost before installation. You may need to set affected keys once more.

## Known limitations

- Untested with the user's full modpack and some mods may store key settings outside `options.txt`.
- A mod that deliberately rewrites keybind settings after the first client tick may still override keys.
- Only `key_...` lines are protected, only before the first post-registration reload; other option values are left to Minecraft.
- This project does not claim to fix every reason `options.txt` can be reset (broken config file, permissions, profile sync, or conflicting mods).

## Licenses, origins, AI disclosure, and Modrinth

The code in this source kit was generated with substantial AI assistance. **Do not upload this project to Modrinth as-is.** As of August 13, 2026, Modrinth prohibits public projects primarily or entirely derived from generative-AI output, and requires appropriate disclosures even for projects with substantial AI involvement. Human authors must contribute substantial original development and testing, not superficial changes, before publication can be considered.

See [PUBLISHING.md](PUBLISHING.md) for the details and validation checklist.

The implementation approach is informed by public discussions and work on [TeamMangoMilk/Keybind-Fix](https://github.com/TeamMangoMilk/Keybind-Fix) (MIT), as well as NeoForge bug reports. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md). Project source in this package is under [MIT](LICENSE).

## Support

This is an initial prototype. File issues with the exact Minecraft, NeoForge, and mod versions and with reproduction steps. Include only relevant log sections, not tokens, passwords, or private server information.
