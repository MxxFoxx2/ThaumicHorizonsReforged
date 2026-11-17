# Roadmap and current state

Snapshot updated on 2026-11-17. Build, seam-check, and dedicated-server facts are recorded separately from code presence and manual in-game verification in `verification.md`.

## Current foundation

- Target: Minecraft 26.3 / NeoForge 26.3, as an add-on for Thaumcraft 4: Reforged.
- Compatibility interfaces and the `TcReforgedPort` ServiceLoader adapter isolate TC4R APIs; version-specific seams live in `platform/` and `client/platform/`.
- The 74-entry research tree, aspects, English/Russian localization, player infusion attachment, Chameleon Skin redesign, Spider Climb and Warp Tumor are present.
- The main content pass includes 20 creature types, their AI and infusion recipes; golems, lenses, foci; machines; souls, blood and Vat systems.
- Nine menu types are registered: Case, Vat, Soul Sieve, Inspiratron, Soulforge, Blood Infuser, Injector, Fingers and Vis Dynamo.
- Entity renderers are registered for the 20 creature types plus AlchemitePrimed and the TH golem. Node Monitor has a dedicated block-entity renderer. Familiar has a dedicated cutout hat layer built from the original `ModelFamiliar` geometry. Several machine models are static JSON models with corrected atlas UVs; Vat still needs its special multiblock renderer/animation.
- The TC4R FX adapter `TcFx` delegates to the fork's `FxProxy`, but individual legacy effects still need parity and runtime checks.

## Recent content additions

- **Case menu and screen** registered and wired: [`c195288`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/c195288b6d28d5045ea6598a4688671faba30a63).
- **Focus items and recipes**, Solar Light and missing aspect tags: [`3878b55`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/3878b550e05306186a5c50fb917d1e7ccb36dece).
- **Planar Conduit** item and the `planarTheory`, `transductionAmplifier` and `vortexStabilizer` recipes: [`408e6cc`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/408e6cc49b87d9c9922442ad8e0de8b13174d9e2).
- **Incubated Egg** item, throwable entity and `alchIncubation` crucible recipe: [`5119c15`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/5119c1582e24a798a436ffa67f01fadb06304f64).
- **Soul Jar capture/release code**: creature NBT is serialized into the jar item; placement transfers its data to the BlockEntity; wand use attempts to restore the creature. [`6fd7614`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/6fd7614d00ceb22d282ab6009483ca1f0f14e6e1). The full cycle is not yet game-verified.
- **Mirror Amulet**: registered as a TC4R amulet bauble, linked to ordinary mirrors through the compatibility seam, and used by the player-death handler to transport inventory and bauble items through the linked mirror. Its infusion recipe and research page are present. [`8538a7f`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/8538a7f). Runtime behavior still needs an in-game check.
- **Void Putty repair and Illumination Focus dye recipes**: custom recipe serializers and recipes are registered. [`8512e83`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/8512e83). Both still need an in-game check.
- **Pocket Plane dimension data**: dimension type and biome definitions were migrated to the Minecraft 26.3 codecs. The dedicated server now loads `thaumichorizons:pocket_plane`. [`7eb9b8c`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/7eb9b8c).
- **Arcane Lens radial HUD**: V-key hold/release selection and Shift+V removal are implemented in `ArcaneLensHud` and `THKeyHandler`. This is code presence only; mouse capture, selection and lens-case behavior still require live-client verification. Commits [`ac8d4ab`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/ac8d4ab4158551ac04bda09e392efc467b3133b0) and [`75c9338`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/75c9338638fce89a94dc9d90b1e169890054d1d4).

## Pocket Plane — dimension loads, gameplay verification pending

Commit [`e38d16a`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/e38d16ae8e68544a3dc6d45627b00cc1cf19ce47) moved substantial Pocket Plane work into tracked source files:

- World data and generation: `PocketPlanes`, `PocketPlaneData`, `PocketPlaneGenerator`, `PocketPlaneChunkGenerator`, `PocketNoise` and `PocketDimension`.
- Blocks and portal support: `GatewayPortalBlock`, `PortalTHBlock`, `SlotBlock` and `SlotBlockEntity`.
- Registration and item: `PocketRegistry`, `KeystoneItem`, `PlanarConduitItem`; the `Teleport` TC4R seam is added to the allowlist.
- Data resources: dimension, dimension type and biome JSONs for `pocket_plane`.

This is **not a playable/verified dimension yet**. The Pocket Plane sources compile against Minecraft 26.3 / NeoForge 26.3: the ChunkGenerator contract, SavedData codec, block-removal hook, entity-inside hook, item tooltip API, registry lookup APIs, and block-entity ticker were migrated. The duplicate `thaumichorizons:planarconduit` registration was consolidated into `PocketRegistry`. The dimension type and biome definitions were migrated to the current codecs in `7eb9b8c`; a fresh dedicated-server run reached `Done` and loaded `thaumichorizons:pocket_plane`.

Still pending: generated terrain layers/caves/pockets, portal/slot in-game behavior, keystone binding, portal transfers/return positions, Vortex-collapse integration, saved-data restart behavior, and client assets/rendering for the new blocks. The historical `.deferred-pocket/` staging directory is no longer part of the public source state; the current public WIP is in `src/main/java` and `src/main/resources`.

## Remaining work — priority order

1. **Validate Pocket Plane runtime behavior.** Build, seam checks, server boot, and dimension load are green. Test generated terrain, slot/keystone, all portal states, portal teardown, transfers in both directions, saved data and Vortex-collapse integration.
2. **Finish Gate 2 verification.** Test Soul Jar capture/release, Containment Focus, curative Vat mode, creature infusion, Corpse Effigy cloning, syringe draw/inject, all nine menus and Inspiratron research completion.
3. **Restore missing world content.** Recombinator and Spike variants are still absent. Original `TileCloud` behavior is not ported; it includes weather interaction, block/entity effects and finite lightning, not just particles. Illumination `TileLight` color persistence and ambient FX are now present in code, pending visual verification. Slot and portal equivalents now exist as WIP source; they should no longer be described as wholly absent.
4. **Finish keybind and interaction verification.** M (Morphic Fingers), C/X (infusion toggles), and V (Arcane Lens radial HUD) are wired. V's HUD and selection logic are implemented; manual checks are open in `verification.md`.
5. **Port remaining entities/projectiles/vehicles:** HorseZombie, HorseSkeleton, NightmareTH, Greatwood Boat, Thaumium Boat, Syringe projectile and BlastPhial projectile. Incubated Egg and its projectile are present in code with the thrown-item renderer registered; they still need a gameplay check. Finite lightning belongs with the Cloud behavior and is not a separately registered mod entity in the original.
6. **Complete item/research and recipe gaps.** Mirror Amulet, its infusion recipe, link behavior, death transport, Void Putty repair, and Focus Illumination dyeing are present in code and need runtime verification. Planar Keystone has a WIP item class, but its recipe/research flow and actual use need completion. Planar Conduit, Planar Theory, Transduction Amplifier, Vortex Stabilizer and Incubated Egg recipes are present in code.
7. **Finish machine and compatibility behavior:** Recombinator; SyntheticNode wisp-essence refilling/drops; crystal-essence conversion; overloaded Transducer flux gas/goo and wisp spawning; Liquefaction nuggets upgrade; remaining TC4R taint-poison/blurred-vision and skull-variant compatibility.
8. **Client polish:** Vat multiblock renderer/animation, new Pocket Plane block/item assets, missing projectile rendering and parity checks for remaining FX. Do not redo existing creature renderers, Node Monitor renderer or completed static machine models.
9. **Release validation and backports:** clean-checkout build, `tools/check_seams.py`, dedicated-server boot, save/reload, full Gate 1/Gate 2 and two-client invisibility checks. Backport branches (`mc/26.1.2`, `mc/1.21.1`) only after 26.3 stabilizes.

## Verification / CI status

`docs/verification.md` is the manual test record. GitHub Actions CI is configured for pushes and pull requests to `main`; it runs the seam checker, Java 25 Gradle build with configuration/build caches, and distributable-JAR validation. The client audio checklist remains a manual test. The remaining Pocket Plane and Gate 2 gameplay checks are manual and open.

## Upstream

The `feat/addon-api` branch contains additive TC4R hooks used by this port (including creature-infusion research pages, FX extensions and golem type support). The add-on currently uses a local composite `includeBuild` for development; verify against the intended published TC4R artifact before release.
