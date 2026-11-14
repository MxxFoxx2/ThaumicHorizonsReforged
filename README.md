# Thaumic Horizons: Reforged

Unofficial port of **Thaumic Horizons** (Minecraft 1.7.10, by Kentington; maintained by [GTNewHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)) for **Minecraft 26.3** on **NeoForge**, built as an add-on for [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged).

The goal is to recreate the original gameplay 1:1: creature infusion and morphing, player self-infusions, the Vat, souls and the blood system, TH golems, arcane lenses, and the pocket plane. Logic, numbers, recipes, and texts are ported from the original sources; intentional deviations are documented in [docs/fidelity.md](docs/fidelity.md) — most notably the player-invisibility infusion is reimplemented without the original sync/render bug.

> **Personal use only.** Thaumic Horizons assets belong to Kentington and the GTNH team; Thaumcraft assets belong to Azanor. This repository contains ported logic and assets and is not intended for redistribution.

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
- **Pocket plane.** Per-player, per-aspect themed dimension *(in flight — deferred to the next session, see the roadmap below)*.

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

Ported and building: registration, aspects, the full research tree (74 entries), five mob effects, simple content, eight essentia/vis machines, twenty creatures with AI and infusion recipes, golems, lenses and foci, the souls/blood/Vat system with all eight menus. In flight: client renderers. Deferred to the next session: pocket plane (60% done, preserved in `.deferred-pocket/`), remaining entities, crafting recipes, FX polish — see the roadmap section in `docs/fidelity.md`.

## Authors and license

- **Kentington** — original Thaumic Horizons (MIT).
- **GTNewHorizons** — the maintained 1.7.10 fork this port references (LGPL-3.0-or-later for their changes).
- **Azanor** — original Thaumcraft.
- **overseas / notlikethiss** — [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged).
- **MxxFoxx2** — this port, developed with AI agents under human direction (MIT for the port's original code; the legacy license notice of the 1.7.10 fork is kept below).

## Links

- **Releases**: [GitHub Releases](../../releases)
- **Upstream host**: [Thaumcraft 4: Reforged](https://github.com/notlikethiss/thaumcraft-4-reforged)
- **Original Thaumic Horizons**: [GTNewHorizons/ThaumicHorizons](https://github.com/GTNewHorizons/ThaumicHorizons)

---

# Thaumic Horizons: GregTech New Horizons Fork (legacy)

Curseforge link: https://www.curseforge.com/minecraft/mc-mods/thaumic-horizons

GTNH Changes are dual-licensed:
For Kentington - MIT
For everyone else, this code is licensed LGPL v3.0 or later.

Original code Copyright (c) 2015 Kentington

Explict permission to modify given here:
https://discord.com/channels/181078474394566657/1029918255932
![license-support--1](license-support--1.png)
![license-support-0](license-support-0.png)
![license-support-1](license-support-1.png)
![license-support-2](license-support-2.png)
