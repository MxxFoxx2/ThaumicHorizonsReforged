# Thaumic Horizons: Reforged

Unofficial port of **Thaumic Horizons** (Minecraft 1.7.10, by Kentington; maintained by [GTNewHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)) for **Minecraft 26.3** on **NeoForge**, built as an add-on for [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged).

The goal is to recreate the original gameplay 1:1: creature infusion and morphing, player self-infusions, the Vat, souls and the blood system, TH golems, arcane lenses, and the pocket plane. Logic, numbers, recipes, and texts are ported from the original sources; intentional deviations are documented in [docs/fidelity.md](docs/fidelity.md) — most notably the player-invisibility infusion is reimplemented without the original sync/render bug.

> [!WARNING]
> **Official host compatibility:** Thaumic Horizons: Reforged `0.1.0-beta.2` targets the public [Thaumcraft 4: Reforged 1.1.3 release for Minecraft 26.3](https://github.com/notlikethiss/thaumcraft-4-reforged/releases/tag/26.3), pinned to commit [`99499a8`](https://github.com/notlikethiss/thaumcraft-4-reforged/commit/99499a8ef6cf9af0efa6a93a058ea56a42a0e7b2). No custom TC4R fork is required.
>
> **Known compatibility limitation:** on the official host, the Transduction Amplifier's automatic multi-transducer-to-Vortex conversion is disabled. Stock TC4R does not expose the API hook needed to safely stop the transducer before replacing its node. Other Transduction Amplifier behavior remains enabled.
>
> TH golem materials, material-specific attributes, upgrade slots, textures and regeneration intervals are adapted locally. Optional TC4R machine-beam FX and Creature Infusion research-page extensions are not available on stock TC4R; the current TH content tree does not use those extensions.
>
> **Experimental pre-release:** broad gameplay, multiplayer, save/reload and visual acceptance coverage is incomplete. Pocket Plane is especially not yet validated as a complete playable dimension. Back up worlds before testing.

> **License scope.** Thaumic Horizons itself is open source — Kentington's original code is MIT and the GTNH fork's changes are LGPL-3.0-or-later — so this port's TH logic and assets are redistributable under those licenses (see Authors and license below). What stays restricted: **Thaumcraft** assets and logic belong to Azanor and reach this mod only through [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged), whose own personal-use policy applies to them.

| Minecraft | NeoForge | Thaumcraft 4: Reforged | Status |
| --- | --- | --- | --- |
| 26.3 | 26.3.0.57-beta | 1.1.3 (official `26.3` release) | `0.1.0-beta.2` — experimental pre-release; one host-dependent feature disabled |
| 26.1.2 | 26.1.2.122 | 1.1.3 | planned |
| 1.21.1 | 21.1.256 | 1.1.3 | planned |

## What the mod adds

- **Creature infusion.** Jar a critter with the wand, drop it into the Vat with components, and get a TH creature: Chocolate Cow, Ore Pig, Endersteed, Seawolf, slimes, the Sheeder, and more — 30 critter recipes with vanilla parents replaced 1:1. Infused creatures gain permanent effects and replaced AI.
- **Player self-infusions.** Ten infusions from the Vat: swiftness, regeneration, resistance, morphic fingers, silver heart, synth skin, amphibious, warp tumor (siphons your warp into a reservoir), spider climb (C key), and Chameleon Skin (X key) — reimplemented without the original sync bug, using attachment-driven full invisibility that hides armor. Two-client and persistence checks are tracked in [docs/verification.md](docs/verification.md).
- **The Vat.** A 1:1 multiblock with curative, incarnation, creature-infusion, and clone modes (corpse effigy → self-clone).
- **Souls.** Soul jars, the Soul Sieve, the Inspiratron (souls → completed research), the Soulforge, and the Soul Beacon; containment focus captures Lost Souls.
- **Blood.** The Blood Infuser and the syringe family: draw blood, inject potions and nutrients.
- **Machines.** Vis Dynamo, Essentia Dynamo, Node Monitor, Synthetic Node, Transduction Amplifier, Vortex and Vortex Stabilizer, Alchemite — all wired into the TC4R vis net and essentia transport.
- **Golems.** TH golem materials from grass to void salt, placed from blocks with golem powder, paired with the TH bell.
- **Lenses and foci.** Five arcane lenses with the lens case, containment/liquefaction/disintegration/illumination/animation foci, and the disposable crystal wand.
- **Pocket plane.** Shared, aspect-themed pocket dimensions created through the Vortex; a Planar Keystone binds a player to one pocket plane (`thaumichorizons:pocket_plane` is registered and loads on a dedicated server; terrain and portal gameplay still need in-game verification).

## Installation

1. Create a **Minecraft 26.3** profile with **NeoForge 26.3.0.57-beta**.
2. Install **TerraBlender 26.3-26.3.0.0.9**.
3. Install the official [Thaumcraft 4: Reforged 1.1.3 release for Minecraft 26.3](https://github.com/notlikethiss/thaumcraft-4-reforged/releases/tag/26.3). Use the published `thaumcraft-4-reforged-1.1.3-26.3.jar` from that release.
4. Drop `thaumic-horizons-reforged-0.1.0-beta.2-26.3.jar` into the same `mods` folder.

The compatibility build is tested against official TC4R commit [`99499a8`](https://github.com/notlikethiss/thaumcraft-4-reforged/commit/99499a8ef6cf9af0efa6a93a058ea56a42a0e7b2). A custom TC4R build is no longer required.

## Configuration

`config/thaumichorizons-startup.toml` (created on first launch): `warpedTumorValue` (warp points held by the tumor), `enablePocketPlane`, `alternateGolemBell`.

## Building and development

The source port and development instructions are in the repository. From the repository root:

```sh
./gradlew build
./gradlew runClient
./gradlew runServer
python3 -I tools/check_seams.py
python3 -I tools/check_assets.py
```

Read [AGENTS.md](AGENTS.md) for shared agent rules. [CLAUDE.md](CLAUDE.md) imports those rules for Claude Code. The architecture and TC4R integration boundaries are explained in [docs/architecture.md](docs/architecture.md) and [docs/tc4r-integration.md](docs/tc4r-integration.md). The prerelease smoke gate and unverified behavior inventory are tracked in [docs/verification.md](docs/verification.md).

## Status and roadmap

The `0.1.0-beta.2` pre-release packages the current source implementation of the major Thaumic Horizons content systems: the full 74-entry research tree, creatures and infusions, golems, machines, lenses/foci, souls/blood/Vat, vehicles, projectiles, cloud/spike content and Pocket Plane code. This is a source-implementation milestone, not a claim that every mechanic has passed in-game acceptance. Pocket Plane travel/generation, several complete gameplay loops, multiplayer persistence and client visual parity remain unverified. Verification is limited to six risk-based smoke scenarios plus targeted regression checks when a subsystem changes; exhaustive variant and visual-parity passes are not prerelease blockers. See [docs/roadmap.md](docs/roadmap.md) and [docs/verification.md](docs/verification.md).

## Authors and license

This port derives from the GTNH fork of Thaumic Horizons, so the distributed port is licensed **LGPL-3.0-or-later** (full text in [LICENSE.txt](LICENSE.txt)); Kentington's original 1.7.10 code remains MIT, and the port's own modern-API code is contributed under the same LGPL terms. Attribution: **Kentington** (original Thaumic Horizons, MIT), **GTNewHorizons** (the maintained 1.7.10 fork), **Azanor** (original Thaumcraft), **overseas / notlikethiss** ([Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)), **MxxFoxx2** (this port, developed with AI agents under human direction). Kentington's explicit-modification permission for the GTNH fork is archived at `reference/1.7.10/license-support/`.

## Links

- **Releases**: [GitHub Releases](../../releases)
- **Upstream host**: [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)
- **Original Thaumic Horizons**: [GTNewHorizons/ThaumicHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)
