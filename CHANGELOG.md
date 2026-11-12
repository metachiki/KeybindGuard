# Changelog

## 0.1.0-beta.2 — test build, not released

- Replace missing-line-only recovery with a fixed startup snapshot that also restores a saved key value when an early save overwrites it with a default.
- Limit restoration to the startup window, then reload after mod key registration and disable protection so normal in-game key changes save normally.
- Add regression coverage for overwritten keys, a fixed snapshot, profile isolation, and releasing protection after startup.
- Run the standalone regression suite in GitHub Actions and on pull requests.

## 0.1.0-beta.1 — prototype source, not released

- Introduce client-side early-save preservation of missing `key_` mapping lines.
- Reload saved options at the first client tick.
- Add standalone Java regression tests and reproducible GitHub Actions build.
- Add English/Japanese docs, MIT license, attribution, and publication-policy notes.

This is not a tested Minecraft release. No completed NeoForge JAR is bundled.
