# Keybind Guard — experimental draft (NOT for publication yet)

> This is only an English description draft. The source project is currently primarily AI-generated and must not be publicly published on Modrinth under the site's August 2026 policy. See PUBLISHING.md.

**Keeps your modded keybinds from being erased during early startup on Minecraft 1.21.1 / NeoForge.**

On affected NeoForge versions, Minecraft can save `options.txt` before mods register their own keybinds. Keys for unknown mods may then disappear. Keybind Guard remembers those key lines when Minecraft loads and saves its settings, restores only missing lines, and reloads the options after mod registration.

**Features**
- Protects custom key mappings during early option-file saves.
- Never overrides a key line already present in `options.txt`.
- Client-side only, with no server installation or extra required mods.
- Does not collect or transmit personal data.

**Compatibility**: Minecraft 1.21.1, NeoForge 21.1.230–21.1.x, Java 21.

**Installation**: Back up `options.txt`, then add a tested release JAR to your client `mods` directory. Reconfigure keys already lost before installing.

**Limitations**: Not guaranteed to fix resets caused by other mods, file permission problems, or launcher profile synchronization. Report bugs with your relevant log and mod list.

**AI/content disclosure**: Please consult the current publication rules and supply accurate disclosures if a human-authored future version becomes eligible for publishing.
