# Roadmap and current state

Snapshot of the public `main` branch at `5df0b4b` (2026-11-17). This is a source/documentation audit, not a new build or playtest. Code presence and in-game verification are tracked separately in `verification.md`.

## Current foundation

- Target: Minecraft 26.3 / NeoForge 26.3, as an add-on for Thaumcraft 4: Reforged.
- Compatibility interfaces and the `TcReforgedPort` ServiceLoader adapter isolate TC4R APIs; version-specific seams live in `platform/` and `client/platform/`.
- The 74-entry research tree, aspects, English/Russian localization, player infusion attachment, Chameleon Skin redesign, Spider Climb and Warp Tumor are present.
- The main content pass includes 20 creature types, their AI and infusion recipes; golems, lenses, foci; machines; souls, blood and Vat systems.
- Nine menu types are registered: Case, Vat, Soul Sieve, Inspiratron, Soulforge, Blood Infuser, Injector, Fingers and Vis Dynamo.
- Entity renderers are registered for the 20 creature types plus AlchemitePrimed and the TH golem. Node Monitor has a dedicated block-entity renderer. Several machine models are static JSON models with corrected atlas UVs; Vat still needs its special multiblock renderer/animation.
- The TC4R FX adapter `TcFx` delegates to the fork's `FxProxy`, but individual legacy effects still need parity and runtime checks.

## Recent content additions

- **Case menu and screen** registered and wired: [`c195288`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/c195288b6d28d5045ea6598a4688671faba30a63).
- **Focus items and recipes**, Solar Light and missing aspect tags: [`3878b55`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/3878b550e05306186a5c50fb917d1e7ccb36dece).
- **Planar Conduit** item and the `planarTheory`, `transductionAmplifier` and `vortexStabilizer` recipes: [`408e6cc`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/408e6cc49b87d9c9922442ad8e0de8b13174d9e2).
- **Incubated Egg** item, throwable entity and `alchIncubation` crucible recipe: [`5119c15`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/5119c1582e24a798a436ffa67f01fadb06304f64).
- **Soul Jar capture/release code**: creature NBT is serialized into the jar item; placement transfers its data to the BlockEntity; wand use attempts to restore the creature. [`6fd7614`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/6fd7614d00ceb22d282ab6009483ca1f0f14e6e1). The full cycle is not yet game-verified.

## Pocket Plane — committed WIP, not build-ready

Commit [`e38d16a`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/e38d16ae8e68544a3dc6d45627b00cc1cf19ce47) moved substantial Pocket Plane work into tracked source files:

- World data and generation: `PocketPlanes`, `PocketPlaneData`, `PocketPlaneGenerator`, `PocketPlaneChunkGenerator`, `PocketNoise` and `PocketDimension`.
- Blocks and portal support: `GatewayPortalBlock`, `PortalTHBlock`, `SlotBlock` and `SlotBlockEntity`.
- Registration and item: `PocketRegistry`, `KeystoneItem`, `PlanarConduitItem`; the `Teleport` TC4R seam is added to the allowlist.
- Data resources: dimension, dimension type and biome JSONs for `pocket_plane`.

This is **not a playable/verified dimension yet**. The follow-up roadmap commit [`ea2a04d`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/ea2a04d23a16a47415fb0079fb1d32e31a2c1df6) reports roughly 25 compile errors remaining in the WIP, including ChunkGenerator API mismatches, `PocketPlaneData` API usage, Keystone API usage and SlotBlockEntity access from `GatewayPortalBlock`. I have not independently run Gradle, so treat that count as the WIP author's report, not a fresh build result.

Additional source-level integration issue found during this audit: `ThItems` already registers `thaumichorizons:planarconduit`, while `PocketRegistry` registers the same item id again using `PlanarConduitItem`. Consolidate this to one canonical registration before expecting registry startup to work.

Still pending after the WIP compile fixes: portal/slot in-game behavior, dimension load and terrain generation, keystone binding, portal transfers/return positions, Vortex-collapse integration, and client assets/rendering for the new blocks. The old `.deferred-pocket/` directory remains ignored and absent; the current public WIP is now in `src/main/java` and `src/main/resources`.

## Remaining work — priority order

1. **Make Pocket Plane compile and validate registration.** Resolve the WIP API mismatches; consolidate the duplicate `planarconduit` item registration; run build + seam checker. Then test dimension load, terrain, slot/keystone, all portal states, portal teardown, transfers in both directions, saved data and Vortex-collapse integration.
2. **Finish Gate 2 verification.** Test Soul Jar capture/release, Containment Focus, curative Vat mode, creature infusion, Corpse Effigy cloning, syringe draw/inject, all nine menus and Inspiratron research completion.
3. **Restore missing world content.** Recombinator and Spike variants are still absent. Original `TileCloud` and `TileLight` behavior is not ported; `TileCloud` includes weather interaction, block/entity effects and finite lightning, not just particles. Slot and portal equivalents now exist as WIP source; they should no longer be described as wholly absent.
4. **Finish the two keybinds.** V (Arcane Lens radial HUD) and M (Morphic Fingers) are registered but their client tick handlers currently only consume the presses. C/X are wired to server payloads.
5. **Port remaining entities/projectiles/vehicles:** HorseZombie, HorseSkeleton, NightmareTH, Greatwood Boat, Thaumium Boat, Syringe projectile and BlastPhial projectile. Incubated Egg and its projectile are present in code; they still need a gameplay/render check. Finite lightning belongs with the Cloud behavior and is not a separately registered mod entity in the original.
6. **Complete item/research and recipe gaps.** Mirror Amulet remains missing. Planar Keystone now has a WIP item class, but its recipe/research flow and actual use need completion. Void Putty repair and Focus Illumination dye recipes remain absent. Planar Conduit, Planar Theory, Transduction Amplifier, Vortex Stabilizer and Incubated Egg recipes are present in code.
7. **Finish machine and compatibility behavior:** Recombinator; SyntheticNode wisp-essence refilling/drops; crystal-essence conversion; overloaded Transducer flux gas/goo and wisp spawning; Liquefaction nuggets upgrade; remaining TC4R taint-poison/blurred-vision and skull-variant compatibility.
8. **Client polish:** Vat multiblock renderer/animation, VortexSound, Familiar hat layer, new Pocket Plane block/item assets, missing projectile rendering and parity checks for remaining FX. Do not redo existing creature renderers, Node Monitor renderer or completed static machine models.
9. **Release validation and backports:** clean-checkout build, `tools/check_seams.py`, dedicated-server boot, save/reload, full Gate 1/Gate 2 and two-client invisibility checks. Backport branches (`mc/26.1.2`, `mc/1.21.1`) only after 26.3 stabilizes.

## Verification / CI status

`docs/verification.md` is the manual test record. No GitHub Actions workflow is present in the public repository tree. The Pocket Plane WIP author's roadmap reports compile errors; the current HEAD must not be described as build-green until a clean `./gradlew build --no-configuration-cache` and `python -I tools/check_seams.py` pass.

## Upstream

The `feat/addon-api` branch contains additive TC4R hooks used by this port (including creature-infusion research pages, FX extensions and golem type support). The add-on currently uses a local composite `includeBuild` for development; verify against the intended published TC4R artifact before release.
