# Thaumic Horizons: Reforged

[![CI](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/actions/workflows/ci.yml/badge.svg?branch=26.1.2)](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/actions/workflows/ci.yml)
[![Latest release](https://img.shields.io/github/v/release/MxxFoxx2/ThaumicHorizonsReforged?include_prereleases&label=release)](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/releases)
[![License: LGPL-3.0-or-later](https://img.shields.io/badge/license-LGPL--3.0--or--later-blue)](LICENSE.txt)

Unofficial port of **Thaumic Horizons** (Minecraft 1.7.10, by Kentington; maintained by [GTNewHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)) targeting **Minecraft 26.1.2** on **NeoForge 26.1.2.122**, as an add-on for [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged).

The goal is to recreate the original gameplay 1:1: creature infusion and morphing, player self-infusions, the Vat, souls and the blood system, TH golems, arcane lenses, boats and mounts, clouds and spikes, and the pocket plane. Logic, numbers, recipes, and texts are ported from the original sources; intentional deviations are documented in [docs/fidelity.md](docs/fidelity.md) — most notably the player-invisibility infusion is reimplemented without the original sync/render bug. The feature inventory is inherited from the Minecraft 26.3 implementation and is not evidence that every feature works on 26.1.2.

> [!WARNING]
> **Minecraft 26.1.2 backport in progress.** This branch is configured to build with TC4R 1.1.3 tag `26.1.2`, commit [`8b953e6`](https://github.com/notlikethiss/thaumcraft-4-reforged/commit/8b953e69c3d625469006f688d964d9f2a7911590), from the sibling checkout `../thaumcraft-4-reforged-26.1.2`. The Java sources still come from the 26.3 line until API and resource incompatibilities are resolved separately.
>
> **No compatibility claim yet:** changing the build target does not prove that the sources compile or that gameplay, mixins, rendering, multiplayer and save/load behavior work with TC4R 26.1.2. See [docs/verification.md](docs/verification.md) for actual results and [docs/roadmap.md](docs/roadmap.md) for the remaining backport gates. Do not publish or install a release JAR for this branch until its build and runtime gates pass.
>
> **Historical baseline:** earlier verification on Minecraft 26.3 remains historical evidence only. Host-specific feature limitations documented for the 26.3 TC4R release must be rechecked against the pinned 26.1.2 source.
>
> **Experimental pre-release:** broad gameplay, multiplayer, save/reload and visual acceptance coverage is incomplete. Back up worlds before testing.

> **License scope.** Thaumic Horizons itself is open source — Kentington's original code is MIT and the GTNH fork's changes are LGPL-3.0-or-later — so this port's TH logic and assets are redistributable under those licenses (see Authors and license below). What stays restricted: **Thaumcraft** assets and logic belong to Azanor and reach this mod only through [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged), whose own personal-use policy applies to them.

| Minecraft | NeoForge | Thaumcraft 4: Reforged | Status |
| --- | --- | --- | --- |
| 26.1.2 | 26.1.2.122 | 1.1.3 (`26.1.2`, `8b953e6`) | current target; build/docs configured, source and runtime compatibility pending |
| 26.3 | 26.3.0.57-beta | 1.1.3 (`26.3`, `99499a8`) | inherited 26.3 baseline; not validation for this branch |
| 1.21.1 | 21.1.256 | 1.1.3 | planned |

## What the mod adds

The following inventory describes the inherited source implementation. It is not a list of features verified on Minecraft/TC4R 26.1.2.

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
- **Pocket plane.** Shared, aspect-themed pocket dimensions created through the Vortex; a Planar Keystone binds a player to one pocket plane (`thaumichorizons:pocket_plane` is registered and loaded on a dedicated server in the historical 26.3 baseline; terrain and portal gameplay still need 26.1.2 and in-game verification).

## Installation

A user-facing 26.1.2 package is not yet accepted. After the backport passes compilation and runtime gates, the target profile is:

1. **Minecraft 26.1.2** with **NeoForge 26.1.2.122**.
2. **TerraBlender 26.1.2.0.3** (required by TC4R; build coordinate `26.1.2-26.1.2.0.3`).
3. **Thaumcraft 4: Reforged 1.1.3 for Minecraft 26.1.2**, tag `26.1.2`, commit `8b953e69c3d625469006f688d964d9f2a7911590`.
4. A validated `thaumic-horizons-reforged-<mod_version>-26.1.2.jar` from `build/libs`.

The intended artifact name and host are checked by CI, but JAR validation does not certify runtime compatibility.

## Configuration

`config/thaumichorizons-startup.toml` (created on first launch): `warpedTumorValue` (warp points held by the tumor), `enablePocketPlane`, `alternateGolemBell`.

## Building and development

Java 25 and the sibling checkout at `../thaumcraft-4-reforged-26.1.2` are required. `settings.gradle` includes that path as a composite build. The checked-out TC4R revision must remain `8b953e69c3d625469006f688d964d9f2a7911590` (tag `26.1.2`); CI and the release workflow reject a different revision. From the repository root:

```sh
./gradlew build
./gradlew test
./gradlew runClient
./gradlew runServer
python3 -I tools/check_seams.py
python3 -I tools/check_assets.py
```

On Windows use `gradlew.bat` and `python` instead. Gradle uses Java 25, Minecraft 26.1.2 and NeoForge 26.1.2.122 from `gradle.properties`; `tc4r_version` is shared by the dependency and generated mod metadata. Read [AGENTS.md](AGENTS.md) for shared agent rules. [CLAUDE.md](CLAUDE.md) imports those rules for Claude Code. The architecture and TC4R integration boundaries are explained in [docs/architecture.md](docs/architecture.md) and [docs/tc4r-integration.md](docs/tc4r-integration.md). The 26.1.2 backport gate and historical verification are tracked in [docs/verification.md](docs/verification.md).

## Visual implementation

These rendering paths belong to the inherited 26.3 source baseline. They require fresh 26.1.2 compilation, client startup and visual checks; the historical acceptance notes are not proof of 26.1.2 rendering. Research-page image references are packaged as transparent markers and rendered by the client-side research-page mixins.

## Status and roadmap

The `0.1.0-beta.2` version currently identifies the inherited source baseline; it does not mean this branch has published a validated 26.1.2 build. The source inventory includes the 74-entry research tree, creatures and infusions, golems, machines, lenses/foci, souls/blood/Vat, boats and mounts, projectiles, clouds/spikes, utility items and Pocket Plane code. The immediate gate is compilation against TC4R 26.1.2, followed by runtime and gameplay acceptance. Historical 26.3 results must not be carried over as 26.1.2 evidence. See [docs/roadmap.md](docs/roadmap.md) and [docs/verification.md](docs/verification.md).

## Authors and license

This port derives from the GTNH fork of Thaumic Horizons, so the distributed port is licensed **LGPL-3.0-or-later** (full text in [LICENSE.txt](LICENSE.txt)); Kentington's original 1.7.10 code remains MIT, and the port's own modern-API code is contributed under the same LGPL terms. Attribution: **Kentington** (original Thaumic Horizons, MIT), **GTNewHorizons** (the maintained 1.7.10 fork), **Azanor** (original Thaumcraft), **overseas / notlikethiss** ([Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)), **MxxFoxx2** (this port, developed with AI agents under human direction). Kentington's explicit-modification permission for the GTNH fork is archived at `reference/1.7.10/license-support/`.

## Links

- **Releases**: [GitHub Releases](../../releases)
- **CI**: [GitHub Actions](.github/workflows/ci.yml)
- **Documentation**: [docs/](docs/)
- **Upstream host**: [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)
- **Original Thaumic Horizons**: [GTNewHorizons/ThaumicHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)
