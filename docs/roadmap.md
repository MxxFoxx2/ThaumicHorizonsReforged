# Roadmap and current state

Snapshot after the initial full-port sprint and the pocket-plane WIP session (see `git log` and [fidelity.md](fidelity.md) for details). Everything is pushed to origin/main.

## Done (build green)

- Build skeleton: ModDevGradle, composite `includeBuild` of Thaumcraft 4: Reforged, mixin config, mods.toml, license LGPL-3.0-or-later.
- Compat layer: `thaumichorizons.compat` interfaces + `TcReforgedPort` (ServiceLoader), capability `isAvailable()` gates, THAUMCRAFT_API allowlist in check_seams.
- Version seams `platform/` + `client/platform/` (DataKey attachments, StackKey data components, ModNetwork/ClientNetwork) + `tools/check_seams.py`.
- Content: simple items/blocks, five mob effects, aspects, the full 74-entry research tree, lang en/ru (382 keys each), loot tables for all 21 breakable blocks.
- Machines: VisDynamo, EssentiaDynamo, NodeMonitor, SyntheticNode, TransductionAmplifier, Vortex, VortexStabilizer, Alchemite (+ primed entity + explosion).
- Creatures: 20 entities, 12 AI classes, 30 critter + 10 self-infusion recipes, EventHandlerEntity port, creature infusion state attachment.
- Player infusions: `CreatureInfusionData` attachment, chameleon skin without the original bug, spider climb, warp tumor via `WarpCompat`, X/C keybinds, `/thaumichorizons chameleon`.
- Golems, lenses, foci, Case menu + GuiCase, disposable wand (`WandRegistry` hooks).
- Souls/blood/Vat: 10 BEs, 10 blocks, syringes, containment focus, 8 menus + 8 screens.
- Recipes: 43 TC4R registrations + 11 vanilla JSONs, keys 1:1 (wired via `ThRecipes.init()`).
- Client: renderers for all 20 creatures + AlchemitePrimed + golem, blockstates/models for the ten soul/vat blocks, item models, machine atlas-UV render polish (successor session).
- In-game verification: successor ran the client (main menu reached), fixed 6 first-launch bugs (lazy ItemStack, lowercase paths, lang suffixes, VariantProperty range, focus upgrade id clash), documented in `docs/fidelity.md` and `CLAUDE.md` pitfalls section.

## In flight / next (priority order)

1. **Pocket plane — WIP at `e38d16a`, needs ~30 min of API adaptation to compile.** Classes written (PocketRegistry, SlotBlockEntity, SlotBlock, PortalTHBlock, KeystoneItem, PlanarConduitItem, dimension JSONs, wiring, allowlist Teleport) but ~25 compile errors remain: `PocketPlaneChunkGenerator` vs 26.3 ChunkGenerator API (GenerationStep package move, addDebugScreenInfo, fillFromNoise signatures), `PocketPlaneData:73 getValue(int)`, `GatewayPortalBlock` private-field access on SlotBlockEntity, `KeystoneItem` API, `PocketRegistry:22` chunk-generator generics. **Fix all, get build+seams green, commit.** Then: pocket dimension is playable (portal, keystone, Vortex-collapse hook still pending — see G's RESUME_MANIFEST).
2. **Remaining entities (~8)**: HorseZombie, HorseSkeleton, NightmareTH, BoatGreatwood, BoatThaumium, BlastPhial, Syringe projectile, EggIncubated, finite LightningBolt. Server-side logic; renderers in the client pass. Recipes 20/21/22 go live when they land. Agent D's partial `EggIncubatedEntity` is already in `6fd7614` — verify and extend.
3. **Client polish**: FX (real `ClientFx` implementations instead of approximations), radial lens HUD (V key), VortexSound, Familiar hat layer, Endersteed armor variants, OBJ machine models (successor started `tools/preview_obj.py`).
4. **Recombinator** — needs TC4R node-internals API (NodeView allowlist or upstream hook).
5. **Verification**: user runs `docs/verification.md` Gate 1 + Gate 2 + the invisibility checklist with two clients. Nothing fully visually verified yet — successor verified wands and machine rendering only.
6. **Backports**: `mc/26.1.2`, `mc/1.21.1` after main stabilizes (TC4R backport procedure).
7. **Upstream**: `feat/addon-api` branch (5 additive hooks) is on the `MxxFoxx2` fork (TC4R repo). Open an upstream PR when the owner decides.

## Upstream

`feat/addon-api` branch (5 additive hooks: NodeType SYNTHETIC/ETHEREAL, ResearchPage.creatureInfusion, FxProxy extensions, VisCostModifiers, GolemType materials) is pushed to the `MxxFoxx2` fork of `notlikethiss/thaumcraft-4-reforged`. TH-R builds through includeBuild and needs no mixins.
