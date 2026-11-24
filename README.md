# Thaumic Horizons: Reforged

[![CI](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/actions/workflows/ci.yml/badge.svg?branch=26.1.2)](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/actions/workflows/ci.yml)
[![Latest release](https://img.shields.io/github/v/release/MxxFoxx2/ThaumicHorizonsReforged?include_prereleases&label=release)](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/releases)
[![License: LGPL-3.0-or-later](https://img.shields.io/badge/license-LGPL--3.0--or--later-blue.svg)](LICENSE.txt)

Unofficial port of **Thaumic Horizons** for **Minecraft 26.1.2** on **NeoForge 26.1.2.122**, built as an add-on for [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged). The project aims to preserve the original gameplay; confirmed deviations are documented in [docs/fidelity.md](docs/fidelity.md).

> [!WARNING]
> **Experimental backport.** The build, JUnit, static checks and local client/server startup pass. Gameplay, multiplayer, save/reload and visual acceptance remain open. Do not treat this branch as a validated release.
>
> TC4R is pinned to tag `26.1.2`, commit [`8b953e6`](https://github.com/notlikethiss/thaumcraft-4-reforged/commit/8b953e69c3d625469006f688d964d9f2a7911590). The sibling repository is not modified.

## Installation

A user-facing package is not yet accepted. The target profile is:

1. **Minecraft 26.1.2** with **NeoForge 26.1.2.122**.
2. **TerraBlender 26.1.2.0.3**.
3. **Thaumcraft 4: Reforged 1.1.3** for Minecraft 26.1.2.
4. `thaumic-horizons-reforged-<mod_version>-26.1.2.jar` from `build/libs`.

## Configuration

`config/thaumichorizons-startup.toml`:

- `warpedTumorValue`
- `enablePocketPlane`
- `alternateGolemBell`

## Building

Java 25 and the sibling checkout `../thaumcraft-4-reforged-26.1.2` are required. Keep TC4R at commit `8b953e69c3d625469006f688d964d9f2a7911590`.

```sh
./gradlew build
./gradlew test
./gradlew runClient
./gradlew runServer
python3 -I tools/check_seams.py
python3 -I tools/check_assets.py
```

On Windows use `gradlew.bat` and `python`. See [AGENTS.md](AGENTS.md), [docs/architecture.md](docs/architecture.md), [docs/tc4r-integration.md](docs/tc4r-integration.md) and [docs/verification.md](docs/verification.md).

## Authors and license

This port derives from the GTNH fork of Thaumic Horizons and is licensed **LGPL-3.0-or-later**; Kentington's original 1.7.10 code remains MIT. Thaumcraft assets and logic remain the property of Azanor and are used through Thaumcraft 4: Reforged under its own license.

Attribution: **Kentington**, **GTNewHorizons**, **Azanor**, **overseas / notlikethiss**, **MxxFoxx2**.

## Links

- [Releases](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/releases)
- [CI](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/actions)
- [Documentation](docs/)
- [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)
- [Original Thaumic Horizons](https://github.com/GTNewHorizons/ThaumicHorizons)
