# Thaumic Horizons: Reforged

[![CI](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/actions/workflows/ci.yml)
[![Latest release](https://img.shields.io/github/v/release/MxxFoxx2/ThaumicHorizonsReforged?include_prereleases&label=release)](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/releases)
[![License: LGPL-3.0-or-later](https://img.shields.io/badge/license-LGPL--3.0--or--later-blue)](LICENSE.txt)

Unofficial port of **Thaumic Horizons** (Minecraft 1.7.10, by Kentington; maintained by [GTNewHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)) for **Minecraft 26.3** on **NeoForge**, built as an add-on for [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged).

The goal is to recreate the original gameplay 1:1: creature infusion and morphing, player self-infusions, the Vat, souls and the blood system, TH golems, arcane lenses, boats and mounts, clouds and spikes, and the pocket plane. Logic, numbers, recipes, and texts are ported from the original sources; intentional deviations are documented in [docs/fidelity.md](docs/fidelity.md) — most notably the player-invisibility infusion is reimplemented without the original sync/render bug.

> [!WARNING]
> **Official host compatibility:** Thaumic Horizons: Reforged `0.1.0` targets the public [Thaumcraft 4: Reforged 1.1.3 release for Minecraft 26.3](https://github.com/notlikethiss/thaumcraft-4-reforged/releases/tag/26.3), pinned to commit [`99499a8`](https://github.com/notlikethiss/thaumcraft-4-reforged/commit/99499a8ef6cf9af0efa6a93a058ea56a42a0e7b2). No custom TC4R fork is required.
>
> **Known compatibility limitation:** on the official host, the Transduction Amplifier's automatic multi-transducer-to-Vortex conversion is disabled. Stock TC4R does not expose the API hook needed to safely stop the transducer before replacing its node. Other Transduction Amplifier behavior remains enabled.
>
> TH golem materials, material-specific attributes, upgrade slots, textures and regeneration intervals are adapted locally. Optional TC4R machine-beam FX and Creature Infusion research-page extensions are not available on stock TC4R; the current TH content tree does not use those extensions.
>
> **Early public release (pre-1.0):** this is a regular 0.x release, not the 1.0.0 stability milestone. Broad gameplay, multiplayer, save/reload and visual acceptance coverage is incomplete. Pocket Plane is especially not yet validated as a complete playable dimension. Back up worlds before testing.

> **License scope.** Thaumic Horizons itself is open source — Kentington's original code is MIT and the GTNH fork's changes are LGPL-3.0-or-later — so this port's TH logic and assets are redistributable under those licenses (see Authors and license below). What stays restricted: **Thaumcraft** assets and logic belong to Azanor and reach this mod only through [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged), whose own personal-use policy applies to them.

| Minecraft | NeoForge | Thaumcraft 4: Reforged | Status |
| --- | --- | --- | --- |
| 26.3 | 26.3.0.57-beta | 1.1.3 (official `26.3` release) | `0.1.0` — release configured, not yet published; one host-dependent feature disabled |
| 26.1.2 | 26.1.2.122 | 1.1.3 | `0.1.0` release prepared; build/JUnit/startup pass; gameplay acceptance open |
| 1.21.1 | 21.1.256 | 1.1.3 | `0.1.0` release prepared; build/static/startup pass; gameplay acceptance open |

## What the mod adds

- **Creature infusion.** Jar a critter with the wand, drop it into the Vat with components, and get a TH creature: Chocolate Cow, Ore Pig, Endersteed, Seawolf, slimes, the Sheeder, and more — 30 critter recipes with vanilla parents replaced 1:1. Infused creatures gain permanent effects and replaced AI. The research book renders creature and self-infusion recipe data in dedicated pages.
- **Player self-infusions.** Ten infusions from the Vat: swiftness, regeneration, resistance, morphic fingers (M key), silver heart, synth skin, amphibious, warp tumor (siphons your warp into a reservoir), spider climb (C key), and Chameleon Skin (X key) — reimplemented without the original sync bug, using attachment-driven full invisibility that hides armor. Two-client and persistence checks are tracked in [docs/verification.md](docs/verification.md).
- **The Vat.** A 1:1 multiblock with curative, incarnation, creature-infusion, and clone modes (corpse effigy → self-clone).
- **Souls.** Soul jars (filled jars render their stored creature as an item preview), the Soul Sieve, the Inspiratron (souls → completed research), the Soulforge, and the Soul Beacon; containment focus captures Lost Souls.
- **Blood and projectiles.** The Seroconverter (Blood Infuser) and the syringe family: draw blood, inject potions and nutrients, and fire syringe darts or blast phials from the seven-slot Injector; dispensers can launch filled syringes, blast phials and incubated eggs.
- **Machines.** Vis Dynamo, Essentia Dynamo, Node Monitor, Synthetic Node, Transduction Amplifier, Teleological Recombinator, Vortex and Vortex Stabilizer, Alchemite — all wired into the TC4R vis net and essentia transport. The Infernal Furnace also receives the original meat → meat-nugget bonus.
- **Golems.** TH golem materials from grass to void salt, placed from blocks with golem powder, paired with the TH bell.
- **Lenses and foci.** Five arcane lenses with the lens case and a V-key radial selector, containment/liquefaction/disintegration/illumination/animation foci, and the disposable crystal wand.
- **Mounts and boats.** Greatwood and Thaumium boats (the Thaumium boat can be steered through lava), the Endersteed, the Nightmare, and zombie/skeleton horses with TH attributes.
- **Clouds and spikes.** Ten cloud types with precipitation, lightning, growth and drop effects, plus metal, wood and tooth spike blocks with directional collision.
- **Field gear.** Mirrored Amulet (death inventory transfer through linked mirrors), Void Putty repair, Incubated Egg, and the Corpse Effigy used by the Vat clone mode.
- **Pocket plane.** Shared, aspect-themed pocket dimensions created through the Vortex; a Planar Keystone binds a player to one pocket plane (`thaumichorizons:pocket_plane` is registered and loads on a dedicated server; terrain and portal gameplay still need in-game verification).

## Installation

1. Create a **Minecraft 26.3** profile with **NeoForge 26.3.0.57-beta**.
2. Install **TerraBlender 26.3-26.3.0.0.9** (required by TC4R).
3. Install the official [Thaumcraft 4: Reforged 1.1.3 release for Minecraft 26.3](https://github.com/notlikethiss/thaumcraft-4-reforged/releases/tag/26.3). Use the published `thaumcraft-4-reforged-1.1.3-26.3.jar` from that release.
4. Drop `thaumic-horizons-reforged-0.1.0-26.3.jar` into the same `mods` folder.

The compatibility build is tested against official TC4R commit [`99499a8`](https://github.com/notlikethiss/thaumcraft-4-reforged/commit/99499a8ef6cf9af0efa6a93a058ea56a42a0e7b2). A custom TC4R build is no longer required.

## Configuration

`config/thaumichorizons-startup.toml` (created on first launch): `warpedTumorValue` (warp points held by the tumor), `enablePocketPlane`, `alternateGolemBell`.

## Building and development

Java 25 and a sibling checkout of the official TC4R repository are required. `settings.gradle` includes `../thaumcraft-4-reforged`; clone it next to this repository and check out the official `26.3` tag (commit `99499a8ef6cf9af0efa6a93a058ea56a42a0e7b2`) before building. From the repository root:

```sh
./gradlew build
./gradlew test
./gradlew runClient
./gradlew runServer
python3 -I tools/check_seams.py
python3 -I tools/check_assets.py
```

On Windows use `gradlew.bat` and `python` instead. Read [AGENTS.md](AGENTS.md) for shared agent rules. [CLAUDE.md](CLAUDE.md) imports those rules for Claude Code. The architecture and TC4R integration boundaries are explained in [docs/architecture.md](docs/architecture.md) and [docs/tc4r-integration.md](docs/tc4r-integration.md). The prerelease smoke gate and unverified behavior inventory are tracked in [docs/verification.md](docs/verification.md).

## Visual implementation

Syringes, the Corpse Effigy and the three spike items use per-context 3D transforms for inventory, dropped-item, first-person and third-person rendering. These are source-level corrections; the remaining visual acceptance checks are tracked in [docs/verification.md](docs/verification.md). Research-page image references are packaged as transparent markers and rendered by the client-side research-page mixins.

## Status and roadmap

The configured `0.1.0` release packages the current source implementation of the major Thaumic Horizons content systems: the full 74-entry research tree, creatures and infusions, golems, machines, lenses/foci, souls/blood/Vat, boats and mounts, projectiles, clouds/spikes, utility items and Pocket Plane code. This is an early pre-1.0 source milestone, not a claim that every mechanic has passed in-game acceptance. Pocket Plane travel/generation, several complete gameplay loops, multiplayer persistence and client visual parity remain unverified. Verification is limited to six risk-based smoke scenarios plus targeted regression checks when a subsystem changes; exhaustive variant and visual-parity passes are not release blockers. See [docs/roadmap.md](docs/roadmap.md) and [docs/verification.md](docs/verification.md).

## Authors and license

This port derives from the GTNH fork of Thaumic Horizons, so the distributed port is licensed **LGPL-3.0-or-later** (full text in [LICENSE.txt](LICENSE.txt)); Kentington's original 1.7.10 code remains MIT, and the port's own modern-API code is contributed under the same LGPL terms. Attribution: **Kentington** (original Thaumic Horizons, MIT), **GTNewHorizons** (the maintained 1.7.10 fork), **Azanor** (original Thaumcraft), **overseas / notlikethiss** ([Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)), **MxxFoxx2** (this port, developed with AI agents under human direction). Kentington's explicit-modification permission for the GTNH fork is archived at `reference/1.7.10/license-support/`.

## Links

- **Releases**: [GitHub Releases](../../releases)
- **CI**: [GitHub Actions](.github/workflows/ci.yml)
- **Documentation**: [docs/](docs/)
- **Upstream host**: [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)
- **Original Thaumic Horizons**: [GTNewHorizons/ThaumicHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)
