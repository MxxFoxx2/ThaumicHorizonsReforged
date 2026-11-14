# Roadmap and current state

Snapshot after the initial full-port sprint (see `git log` and [fidelity.md](fidelity.md) for details). Build green, `tools/check_seams.py` green, everything pushed to origin/main.

## Done (this sprint)

- Build skeleton: ModDevGradle, composite `includeBuild` of Thaumcraft 4: Reforged, mixin config placeholder, mods.toml.
- Compat layer: `thaumichorizons.compat` interfaces + `TcReforgedPort` (ServiceLoader), capability `isAvailable()` gates.
- Version seams `platform/` + `client/platform/` (DataKey attachments, StackKey data components, ModNetwork/ClientNetwork) + `tools/check_seams.py` with the THAUMCRAFT_API allowlist.
- Content: simple items/blocks, five mob effects, aspects, the full 74-entry research tree, lang en/ru.
- Machines: VisDynamo, EssentiaDynamo, NodeMonitor, SyntheticNode, TransductionAmplifier, Vortex, VortexStabilizer, Alchemite (+ primed entity + explosion).
- Creatures: 20 entities, 12 AI classes, 30 critter + 10 self-infusion recipes, EventHandlerEntity port.
- Player infusions: `CreatureInfusionData` attachment, chameleon skin without the original bug (render suppression + full sync), spider climb, warp tumor via `WarpCompat`, X/C keybinds, `/thaumichorizons chameleon`.
- Golems, lenses, foci, Case menu, disposable wand (`WandRegistry` hooks).
- Souls/blood/Vat: 10 BEs, 10 blocks, syringes, containment focus, 8 menus + 8 screens.
- Recipes: 43 TC4R registrations + 11 vanilla JSONs, keys 1:1 (wired via `ThRecipes.init()`).
- Client: renderers for all 20 creatures + AlchemitePrimed + golem, blockstates/models for the ten soul/vat blocks, item models.

## Deferred (next windows — 300M tokens/day, 18:00–04:00 MSK, after Nov 16)

1. **Pocket plane** (~60% done): in-flight files preserved in `.deferred-pocket/`, resume instructions in `.deferred-pocket/RESUME_MANIFEST.md`. Remaining: PocketRegistry, SlotBlockEntity, PortalTHBlock/SlotBlock, Keystone/PlanarConduit items, dimension JSONs, Vortex-collapse integration, seam allowlist for `thaumcraft.platform.Teleport`.
2. **Client polish**: FX (ClientFx real implementations instead of approximations), radial lens HUD (V key), VortexSound, Familiar hat layer, Endersteed armor variants, modmatrix texture.
3. **Entities**: HorseZombie, HorseSkeleton, NightmareTH, BoatGreatwood, BoatThaumium, BlastPhial projectile, Syringe projectile, EggIncubated + finite lightning.
4. **Recipes remainder**: entries blocked on unregistered items (mirror amulet, boats, planar conduit, void-putty repair, focus items already partly done); reconcile `docs/fidelity.md` data mappings with `../thaumcraft-4-reforged/tools/data/id_map.json` (the recipes phase corrected the table — research pages may need key fixes for suffixed variants, some already applied).
5. **Menus**: CASE menu registration + GuiCase (F's ContainerCase/InventoryCase exist unregistered — opening the lens case currently crashes; the item was hidden from the creative tab? no — it is registered, so either register the menu or hide the item until then).
6. **Recombinator** — needs TC4R node-internals API (NodeView allowlist or upstream hook).
7. **Verification**: run `docs/verification.md` Gate 1 + Gate 2 + the invisibility checklist with two clients. Nothing has been visually verified yet — this is the first thing a new session should drive the user through.
8. **Backports**: `mc/26.1.2`, `mc/1.21.1` after main stabilizes (TC4R backport procedure).

## Upstream

`feat/addon-api` branch (5 additive hooks: NodeType SYNTHETIC/ETHEREAL, ResearchPage.creatureInfusion, FxProxy extensions, VisCostModifiers, GolemType materials) is pushed to the `MxxFoxx2` fork. Open an upstream PR when the owner decides; until then TH-R builds through includeBuild and needs no mixins.
