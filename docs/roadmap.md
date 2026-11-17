# Roadmap and current state

Snapshot of the public `main` branch at `6fd7614` (2026-11-17). This is a source-level status report; a feature is not marked game-verified unless `verification.md` records an in-game check.

## Current foundation

- Target: Minecraft 26.3 / NeoForge 26.3, as an add-on for Thaumcraft 4: Reforged.
- Compatibility interfaces and the `TcReforgedPort` ServiceLoader adapter isolate TC4R APIs; version-specific seams live in `platform/` and `client/platform/`.
- The 74-entry research tree, aspects, English/Russian localization, player infusion attachment, Chameleon Skin redesign, Spider Climb and Warp Tumor are present.
- The main content pass includes 20 creature types, their AI and infusion recipes; golems, lenses, foci; machines; souls, blood and Vat systems.
- Nine menu types are registered: Case, Vat, Soul Sieve, Inspiratron, Soulforge, Blood Infuser, Injector, Fingers and Vis Dynamo.
- Entity renderers are registered for the 20 creature types plus AlchemitePrimed and the TH golem. Node Monitor has a dedicated block-entity renderer. Several machine models are now static JSON models with corrected atlas UVs; Vat still needs its special multiblock renderer/animation.
- The TC4R FX adapter `TcFx` calls the fork's `FxProxy` methods. This does not mean every original TH effect has been matched or visually verified.

## Newly completed since the earlier roadmap snapshot

- **Case menu and screen** registered and wired: [`c195288`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/c195288b6d28d5045ea6598a4688671faba30a63).
- **Focus items and recipes**, plus Solar Light and missing aspect tags: [`3878b55`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/3878b550e05306186a5c50fb917d1e7ccb36dece).
- **Planar Conduit** item and the `planarTheory`, `transductionAmplifier` and `vortexStabilizer` recipes: [`408e6cc`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/408e6cc49b87d9c9922442ad8e0de8b13174d9e2).
- **Incubated Egg** item, throwable entity and `alchIncubation` crucible recipe: [`5119c15`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/5119c1582e24a798a436ffa67f01fadb06304f64).
- **Soul Jar capture path** now serializes an infused creature into a jar item; jar placement restores the block-entity data, and wand use has a release implementation: [`6fd7614`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/6fd7614d00ceb22d282ab6009483ca1f0f14e6e1). This is source-level implementation only; the complete cycle remains unverified in-game.

## Remaining work — priority order

1. **Finish Gate 2 verification.** Test Soul Jar capture/release, Containment Focus, curative Vat mode, creature infusion, Corpse Effigy cloning, syringe draw/inject, all nine menus and Inspiratron research completion. Separate implemented code from verified behavior in `verification.md`.
2. **Restore missing world blocks and BlockEntities.** The modern registries still lack the original Cloud and Light BlockEntities, Recombinator, Spike variants, Slot, PortalTH and Gateway. In particular, `TileCloud` contains substantial gameplay behavior (weather interaction, block/entity effects and finite lightning), not just particles.
3. **Finish the two keybinds.** V (Arcane Lens radial HUD) and M (Morphic Fingers) are registered but their client tick handlers currently only consume the presses. C/X are wired to server payloads.
4. **Port the remaining entities/projectiles and vehicles:** HorseZombie, HorseSkeleton, NightmareTH, Greatwood Boat, Thaumium Boat, Syringe projectile and BlastPhial projectile. The Incubated Egg and its projectile are no longer outstanding. The finite lightning behavior belongs with the missing Cloud system; it is not a separately registered entity type in the original.
5. **Complete remaining item/research content:** Mirror Amulet and Planar Keystone; add the missing recipes/behaviors for those items. Revisit Void Putty repair and Focus Illumination dye recipes. Planar Conduit, Planar Theory, Transduction Amplifier, Vortex Stabilizer and Incubated Egg recipes are now present in code.
6. **Finish machine and compatibility behavior:** Recombinator; SyntheticNode wisp-essence refilling/drops; crystal-essence conversion; overloaded Transducer flux gas/goo and wisp spawning; Liquefaction nuggets upgrade; remaining TC4R taint-poison/blurred-vision and skull-variant compatibility.
7. **Pocket Plane.** Planar Conduit is now present, but Planar Keystone, portal/slot blocks and BlockEntities, dimension registration/data, and Vortex-collapse integration remain. The earlier `.deferred-pocket/` directory is git-ignored and is not present in the public `main` tree; its claimed ~60% local progress cannot be verified from GitHub. Preserve a local backup and publish a safe branch before resuming.
8. **Client polish:** Vat multiblock renderer/animation, Soul Jar and other special machine visuals as needed, VortexSound, Familiar hat layer, missing projectile rendering, and parity checks for remaining FX. Do not redo the existing creature renderers, Node Monitor renderer or completed static machine models.
9. **Release validation and backports:** clean-checkout build, `tools/check_seams.py`, dedicated-server boot, save/reload, full Gate 1/Gate 2 and two-client invisibility checks. Backport branches (`mc/26.1.2`, `mc/1.21.1`) only after 26.3 stabilizes.

## Verification / CI status

`docs/verification.md` is the manual test record. No GitHub Actions workflow is present in the public repository tree, so do not infer that the latest `main` passed CI. The current HEAD has not been independently built or launched as part of this documentation update.

## Upstream

The `feat/addon-api` branch contains additive TC4R hooks used by this port (including creature-infusion research pages, FX extensions and golem type support). The add-on currently uses a local composite `includeBuild` for development; verify against the intended published TC4R artifact before release.
