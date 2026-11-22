# Thaumic Horizons: Reforged

[![CI](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/actions/workflows/ci.yml)
[![Latest release](https://img.shields.io/github/v/release/MxxFoxx2/ThaumicHorizonsReforged?include_prereleases&label=release)](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/releases)
[![License: LGPL-3.0-or-later](https://img.shields.io/badge/license-LGPL--3.0--or--later-blue)](LICENSE.txt)

Unofficial port of **Thaumic Horizons** (Minecraft 1.7.10, by Kentington; maintained by [GTNewHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)) for **Minecraft 26.3** on **NeoForge**, built as an add-on for [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged).

The goal is to recreate the original gameplay 1:1: creature infusion and morphing, player self-infusions, the Vat, souls and the blood system, TH golems, arcane lenses, boats and mounts, clouds and spikes, and the pocket plane. Logic, numbers, recipes, and texts are ported from the original sources; intentional deviations are documented in [docs/fidelity.md](docs/fidelity.md) — most notably the player-invisibility infusion is reimplemented without the original sync/render bug.

> [!WARNING]
> **Temporary TC4R dependency for current releases.** The published `0.1.0-beta.1` JAR requires our custom [Thaumcraft 4: Reforged `feat/addon-api` fork](https://github.com/MxxFoxx2/thaumcraft-4-reforged/tree/feat/addon-api), built from the commit recorded in that release's notes ([`c5b7a0d`](https://github.com/MxxFoxx2/thaumcraft-4-reforged/commit/c5b7a0d99c2c23f097591c1343b8f6d3d3d8febb)). The current `main` branch and CI use the newer CI-tested snapshot [`388aebb`](https://github.com/MxxFoxx2/thaumcraft-4-reforged/commit/388aebb2ed9f7af45c580792f8f59976ea9a822b). The ordinary published TC4R `1.1.3` JAR does not contain all add-on hooks required by this build. Follow the custom-host installation steps below; do not substitute the stock upstream JAR. This notice is temporary and will be removed when the required APIs are included in the published TC4R artifact.
>
> **Experimental pre-release:** the major content systems are implemented in the source, but broad gameplay, multiplayer, save/reload and visual acceptance coverage does not exist. Releases use the six-scenario smoke gate in [docs/verification.md](docs/verification.md); the larger unverified behavior inventory is not a mandatory checklist. Pocket Plane is especially not yet validated as a playable dimension. Back up worlds before testing.

> **License scope.** Thaumic Horizons itself is open source — Kentington's original code is MIT and the GTNH fork's changes are LGPL-3.0-or-later — so this port's TH logic and assets are redistributable under those licenses (see Authors and license below). What stays restricted: **Thaumcraft** assets and logic belong to Azanor and reach this mod only through [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged), whose own personal-use policy applies to them.

| Minecraft | NeoForge | Thaumcraft 4: Reforged | Status |
| --- | --- | --- | --- |
| 26.3 | 26.3.0.57-beta | 1.1.3 + custom add-on API | `0.1.0-beta.1` — pre-release; gameplay/client acceptance in progress |
| 26.1.2 | 26.1.2.122 | 1.1.3 | planned |
| 1.21.1 | 21.1.256 | 1.1.3 | planned |

## What the mod adds

- **Creature infusion.** Jar a critter with the wand, drop it into the Vat with components, and get a TH creature: Chocolate Cow, Ore Pig, Endersteed, Seawolf, slimes, the Sheeder, and more — 30 critter recipes with vanilla parents replaced 1:1. Infused creatures gain permanent effects and replaced AI.
- **Player self-infusions.** Ten infusions from the Vat: swiftness, regeneration, resistance, morphic fingers (M key), silver heart, synth skin, amphibious, warp tumor (siphons your warp into a reservoir), spider climb (C key), and Chameleon Skin (X key) — reimplemented without the original sync bug, using attachment-driven full invisibility that hides armor. Two-client and persistence checks are tracked in [docs/verification.md](docs/verification.md).
- **The Vat.** A 1:1 multiblock with curative, incarnation, creature-infusion, and clone modes (corpse effigy → self-clone).
- **Souls.** Soul jars (filled jars render their stored creature as an item preview), the Soul Sieve, the Inspiratron (souls → completed research), the Soulforge, and the Soul Beacon; containment focus captures Lost Souls.
- **Blood and projectiles.** The Seroconverter (Blood Infuser) and the syringe family: draw blood, inject potions and nutrients, and fire syringe darts or blast phials from the seven-slot Injector.
- **Machines.** Vis Dynamo, Essentia Dynamo, Node Monitor, Synthetic Node, Transduction Amplifier, Teleological Recombinator, Vortex and Vortex Stabilizer, Alchemite — all wired into the TC4R vis net and essentia transport.
- **Golems.** TH golem materials from grass to void salt, placed from blocks with golem powder, paired with the TH bell.
- **Lenses and foci.** Five arcane lenses with the lens case and a V-key radial selector, containment/liquefaction/disintegration/illumination/animation foci, and the disposable crystal wand.
- **Mounts and boats.** Greatwood and Thaumium boats (the Thaumium boat can be steered through lava), the Endersteed, the Nightmare, and zombie/skeleton horses with TH attributes.
- **Clouds and spikes.** Ten cloud types with precipitation, lightning, growth and drop effects, plus metal, wood and tooth spike blocks with directional collision.
- **Field gear.** Mirrored Amulet (death inventory transfer through linked mirrors), Void Putty repair, Incubated Egg, and the Corpse Effigy used by the Vat clone mode.
- **Pocket plane.** Shared, aspect-themed pocket dimensions created through the Vortex; a Planar Keystone binds a player to one pocket plane (`thaumichorizons:pocket_plane` is registered and loads on a dedicated server; terrain and portal gameplay still need in-game verification).

## Installation

1. Create a **Minecraft 26.3** profile with **NeoForge 26.3.0.57-beta**.
2. Install **TerraBlender 26.3-26.3.0.0.9** (required by TC4R).
3. Build the custom TC4R host from the revision matching your Thaumic Horizons build. The current `main` / CI pin is:
   ```sh
   git clone --branch feat/addon-api https://github.com/MxxFoxx2/thaumcraft-4-reforged.git
   cd thaumcraft-4-reforged
   git checkout 388aebb2ed9f7af45c580792f8f59976ea9a822b
   ./gradlew build
   ```
   Put `build/libs/thaumcraft-4-reforged-1.1.3-26.3.jar` into `mods`. For the published `0.1.0-beta.1` JAR, use the revision in its release notes instead: `c5b7a0d99c2c23f097591c1343b8f6d3d3d8febb`.
4. Drop `thaumic-horizons-reforged-0.1.0-beta.1-26.3.jar` into the same `mods` folder.

The [published upstream TC4R 1.1.3 release](https://github.com/notlikethiss/thaumcraft-4-reforged/releases/tag/26.3) is **not sufficient for this pre-release** until the required add-on APIs are published there.

## Configuration

`config/thaumichorizons-startup.toml` (created on first launch): `warpedTumorValue` (warp points held by the tumor), `enablePocketPlane`, `alternateGolemBell`.

## Building and development

Java 25 and the sibling TC4R checkout are required. `settings.gradle` includes `../thaumcraft-4-reforged`, so clone TC4R next to this repository before building. From the repository root:

```sh
./gradlew build
./gradlew test
./gradlew runClient
./gradlew runServer
python3 -I tools/check_seams.py
python3 -I tools/check_assets.py
```

On Windows use `gradlew.bat` and `python` instead. Read [AGENTS.md](AGENTS.md) for shared agent rules. [CLAUDE.md](CLAUDE.md) imports those rules for Claude Code. The architecture and TC4R integration boundaries are explained in [docs/architecture.md](docs/architecture.md) and [docs/tc4r-integration.md](docs/tc4r-integration.md). The prerelease smoke gate and unverified behavior inventory are tracked in [docs/verification.md](docs/verification.md).

## Status and roadmap

The `0.1.0-beta.1` pre-release packages the current source implementation of the major Thaumic Horizons content systems: the full 74-entry research tree, creatures and infusions, golems, machines, lenses/foci, souls/blood/Vat, boats and mounts, projectiles, clouds/spikes, utility items and Pocket Plane code. Since that tag, `main` has restored the 3D Injector and dynamic Soul Jar item rendering. This is a source-implementation milestone, not a claim that every mechanic has passed in-game acceptance. Pocket Plane travel/generation, several complete gameplay loops, multiplayer persistence and client visual parity remain unverified. Verification is limited to six risk-based smoke scenarios plus targeted regression checks when a subsystem changes; exhaustive variant and visual-parity passes are not prerelease blockers. See [docs/roadmap.md](docs/roadmap.md) and [docs/verification.md](docs/verification.md).

## Authors and license

This port derives from the GTNH fork of Thaumic Horizons, so the distributed port is licensed **LGPL-3.0-or-later** (full text in [LICENSE.txt](LICENSE.txt)); Kentington's original 1.7.10 code remains MIT, and the port's own modern-API code is contributed under the same LGPL terms. Attribution: **Kentington** (original Thaumic Horizons, MIT), **GTNewHorizons** (the maintained 1.7.10 fork), **Azanor** (original Thaumcraft), **overseas / notlikethiss** ([Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)), **MxxFoxx2** (this port, developed with AI agents under human direction). Kentington's explicit-modification permission for the GTNH fork is archived at `reference/1.7.10/license-support/`.

## Links

- **Releases**: [GitHub Releases](../../releases)
- **CI**: [GitHub Actions](.github/workflows/ci.yml)
- **Documentation**: [docs/](docs/)
- **Upstream host**: [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)
- **Original Thaumic Horizons**: [GTNewHorizons/ThaumicHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)
