# Thaumic Horizons: Reforged

Unofficial port of **Thaumic Horizons** (Minecraft 1.7.10, by Kentington; maintained by [GTNewHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)) for **Minecraft 26.3** on **NeoForge**, built as an add-on for [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged).

The goal is to recreate the original gameplay 1:1: creature infusion and morphing, player self-infusions, the Vat, souls and the blood system, TH golems, arcane lenses, and the pocket plane. Logic, numbers, recipes, and texts are ported from the original sources; intentional deviations are documented in [docs/fidelity.md](docs/fidelity.md) — most notably the player-invisibility infusion is reimplemented without the original sync/render bug.

> **License scope.** Thaumic Horizons itself is open source — Kentington's original code is MIT and the GTNH fork's changes are LGPL-3.0-or-later — so this port's TH logic and assets are redistributable under those licenses (see Authors and license below). What stays restricted: **Thaumcraft** assets and logic belong to Azanor and reach this mod only through [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged), whose own personal-use policy applies to them.

| Minecraft | NeoForge | Thaumcraft 4: Reforged | Status |
| --- | --- | --- | --- |
| 26.3 | 26.3.0.57-beta | 1.1.3 | `main` — core systems playable, client pass in flight |
| 26.1.2 | 26.1.2.122 | 1.1.3 | planned |
| 1.21.1 | 21.1.256 | 1.1.3 | planned |

## What the mod adds

- **Creature infusion.** Jar a critter with the wand, drop it into the Vat with components, and get a TH creature: Chocolate Cow, Ore Pig, Endersteed, Seawolf, slimes, the Sheeder, and more — 30 critter recipes with vanilla parents replaced 1:1. Infused creatures gain permanent effects and replaced AI.
- **Player self-infusions.** Ten infusions from the Vat: swiftness, regeneration, resistance, morphic fingers, silver heart, synth skin, amphibious, warp tumor (siphons your warp into a reservoir), spider climb (C key), and Chameleon Skin (X key) — reimplemented without the original sync bug: attachment-driven, armor-including invisibility, verified by the checklist in [docs/verification.md](docs/verification.md).
- **The Vat.** A 1:1 multiblock with curative, incarnation, creature-infusion, and clone modes (corpse effigy → self-clone).
- **Souls.** Soul jars, the Soul Sieve, the Inspiratron (souls → completed research), the Soulforge, and the Soul Beacon; containment focus captures Lost Souls.
- **Blood.** The Blood Infuser and the syringe family: draw blood, inject potions and nutrients.
- **Machines.** Vis Dynamo, Essentia Dynamo, Node Monitor, Synthetic Node, Transduction Amplifier, Vortex and Vortex Stabilizer, Alchemite — all wired into the TC4R vis net and essentia transport.
- **Golems.** TH golem materials from grass to void salt, placed from blocks with golem powder, paired with the TH bell.
- **Lenses and foci.** Five arcane lenses with the lens case, containment/liquefaction/disintegration/illumination/animation foci, and the disposable crystal wand.
- **Pocket plane.** Shared, aspect-themed pocket dimensions created through the Vortex; a Planar Keystone binds a player to one pocket plane *(dimension loads on the dedicated server; terrain, portal, Keystone, and client-asset verification remain in flight)*.

## Installation

1. Create a **Minecraft 26.3** profile with **NeoForge 26.3.0.57-beta**.
2. Install **Thaumcraft 4: Reforged 1.1.3** (required) and **TerraBlender 26.3-26.3.0.0.9**.
3. Drop `thaumic-horizons-reforged-<version>-26.3.jar` into `mods`.

[Download Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged/releases/tag/26.3)

## Configuration

`config/thaumichorizons-startup.toml` (created on first launch): `warpedTumorValue` (warp points held by the tumor), `enablePocketPlane`, `alternateGolemBell`.

## Building and development

The port keeps 1.7.10 sources under `reference/1.7.10/` and enforces version seams (`platform/`, `compat/`) with a checker, mirroring the host project's discipline.

```sh
./gradlew build                        # composite build; includes Thaumcraft 4: Reforged
./gradlew runClient                    # dev client
./gradlew runServer                    # dev server
python3 tools/check_seams.py           # version/API boundary check (also runs locally)
```

Agent and contribution rules live in [CLAUDE.md](CLAUDE.md) and [AGENTS.md](AGENTS.md); the manual verification gates are in [docs/verification.md](docs/verification.md).

## Status and roadmap

Ported and building: registration, aspects, the full research tree (74 entries), five mob effects, simple content, eight essentia/vis machines, twenty creatures with AI and infusion recipes, golems, lenses and foci, the souls/blood/Vat system with nine menus, the Mirror Amulet with death-item transport, Void Putty repair and Illumination Focus dyeing. The Pocket Plane compiles and its dimension loads on the dedicated server; terrain generation, portal/Keystone behavior, remaining entities, client renderers, and FX polish remain in flight — see `docs/roadmap.md` and `docs/verification.md`.

## Authors and license

This port derives from the GTNH fork of Thaumic Horizons, so the distributed port is licensed **LGPL-3.0-or-later** (full text in [LICENSE.txt](LICENSE.txt)); Kentington's original 1.7.10 code remains MIT, and the port's own modern-API code is contributed under the same LGPL terms. Attribution: **Kentington** (original Thaumic Horizons, MIT), **GTNewHorizons** (the maintained 1.7.10 fork), **Azanor** (original Thaumcraft), **overseas / notlikethiss** ([Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)), **MxxFoxx2** (this port, developed with AI agents under human direction). Kentington's explicit-modification permission for the GTNH fork is archived at `reference/1.7.10/license-support/`.

## Links

- **Releases**: [GitHub Releases](../../releases)
- **Upstream host**: [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)
- **Original Thaumic Horizons**: [GTNewHorizons/ThaumicHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)
