# Fidelity

 deviations from the 1.7.10 original, phase by phase. Everything not listed here is 1:1.

## Player infusions (Chameleon Skin) — intentional redesign

The original applied Chameleon Skin as a permanent non-curative `Potion.invisibility` with a one-shot `PacketPlayerInfusionSync` (sendToAll at infusion time). Players joining later never received the sync, armor and held items kept rendering, and the toggle state could desync from the actual potion effect. The port replaces the mechanism (community requirement: fix the invisibility bug):

- The single source of truth is the `CreatureInfusionData` attachment (copy-on-death, synced to all tracking clients + `PlayerEvent.StartTracking` + join). No potion is involved.
- The server applies the vanilla `setInvisible` flag from the attachment state each change.
- The client cancels `RenderPlayerEvent.Pre` for chameleon-active players via `AvatarRenderState.id` — hiding body, armor and held item (vanilla potion invisibility leaves armor visible; here nothing renders).
- Other self-infusion effects (1 = jump + speed, 3 = regeneration, 4 = resistance) keep the original approach: permanent non-curative `MobEffectInstance`, re-applied on join/tick.

## Creature entities — modern-parent substitutions

- Panther / Gravekeeper / Familiar extend `TamableAnimal` instead of the ocelot lineage (modern `Cat`/`Ocelot` carry variant and avoid-player AI baggage). Always-tamed semantics and TH owner AI preserved.
- Gravekeeper's eight per-class undead target selectors → one `EntityTypeTags.UNDEAD` goal selector (covers TC4R undead too).
- Slime variants reuse vanilla split logic through `getType()`.
- LostSoul follows the TC4R Wisp movement idiom (`travelFlying`, waypoint motion).

## Data mappings (TC4 meta → TC4R ids)

Authoritative source: the host's own table `../thaumcraft-4-reforged/tools/data/id_map.json` — trust it over semantic guesses (verified against TH reference usage; e.g. `blockWoodenDevice:6` is greatwood_planks, not bellows; `blockStoneDevice:2` is runic_matrix). Resolved `itemResource` metas as used by TH: 0→alumentum, 1→nitor, 2→thaumium_ingot, 3→quicksilver, 6→amber, 7→enchanted_fabric, 8→vis_filter, 9→knowledge_fragment, 14→salis_mundus, 15→primal_charm, 16→void_metal_ingot. Blocks: `blockMetalDevice:9`→alchemical_construct, `blockLifter`→arcane_bellows, `customPlant 4/5`→tainted_plant/ethereal_bloom, `blockTaint:2`→tainted_soil, `cosmeticSolid:0`→arcane_stone, `blockJar:3`→void_jar, `table:15`→arcane_worktable, `customPlant 1/2`→shimmerleaf/vishroom; wool meta 32767→white wool; potion stacks 8233/8269→plain `SPLASH_POTION` without effect data (data-only for now; the vat GUI refines this later).

Recipe-key conventions (TC4R's `TcRecipes` map requires unique keys): multi-variant recipes keep the bare original key for variant 0 plus `1..n` suffixes (`alcheponics1..4`, `meatGrowth1..2`, `fleshLeather1..2`, `shardTrans1..5`); the syringe arcane/infusion key collision resolved as `essentiaInjectionSyringe`; blast-phial variant data carried via a `TcStack.registerDataHandler/Matcher("phial", ...)` pair.

## Machines — documented deviations

- EssentiaDynamo: broken saves with `ticks > 0` but no stored aspect reset to 0 (original divided 12/0).
- Vortex: random aspects rolled server-side only + sync (original rolled on both sides = desync).
- Vortex creative variant (`cheat` meta) not exposed as an item variant.
- Transducer `status = -1` reset on TC4R transducers skipped (no public setter).
- Recombinator not ported yet: needs TC4-internal node lock/identity checks absent from the public API.
- Machine FX (alchemite FX, block zaps, hungry-node FX, vortex sound) routed through vanilla particles/sounds until the FX compat phase.
- VisDynamo aspect-toggle GUI deferred to the menu phase; toggles exist as BE fields.

## Souls / Vat / blood phase — documented deviations

- Soul-jar villager profession stored as a registry id string.
- TC4R taint-poison / blurred-vision effects omitted from blood-infuser and vat effect lists (no compat seam for them yet).
- Vat stabilizer symmetry scan counts only vanilla skulls (needs an InfusionCompat block predicate for TC4R skull variants).
- `resetStats` skipped (the port has no `IEntityInfusedStats` equivalent).
- Instilled-loyalty gate implemented via goalSelector priority instead of the original flag.
- Critter recipes 20/21/22 (undead horses, nightmare) complete but spawn nothing until those entities are ported.
- Deferred: `EntitySyringe`/`EntityBlastPhial` projectiles (injector charge/fire logic works, spawn pending), brain-jar feeding seam, block/blockstate/item models for the ten soul/vat blocks (client phase), containment beam FX, creative empty-soul-jar sub-item.

## Golems / lenses phase — documented deviations

- `EnumGolemTHType` replaced by TC4R `GolemType` extension values (identical stats incl. `visCost`); golem material derived from the block via a modern block/tag mapping (modern MC removed `Material`). CLAY is still never produced, parity with the original.
- Restored blocks use `defaultBlockState()` (no numeric meta) — facing/rotation of golem-restored blocks resets.
- Wand-pairing (core/upgrade/decoration) goes through TC4R `GolemBase.mobInteract` `GolemPartItem` flow.
- Baubles support dropped from LensManager (no Baubles on NeoForge 26.3); lens cases work from inventory only.
- Lens night-vision detection uses a duration<=255 heuristic.
- Liquefaction "nuggets" upgrade is inert (TC4R removed the smelting-bonus registry); purity uses TC4R `SpecialMining.find`.
- Disposable-wand recharge-prevention hack unnecessary (TC4R nodes recharge only the base `Wands.isWand` item).

## Pocket plane — deferred at ~60%

- Stopped mid-task by quota window; in-flight files preserved in `.deferred-pocket/` with a resume manifest (`RESUME_MANIFEST.md`). DONE: SavedData (`PocketPlanes`), PocketPlaneData schema, PocketPlaneGenerator (layers/caves/pockets/biomes 1:1), custom ChunkGenerator codec. TODO: PocketRegistry, SlotBlockEntity, PortalTHBlock/SlotBlock, Keystone/PlanarConduit items, dimension JSONs, Vortex-collapse integration. Resume in a 300M window.
- Deviations already baked in: TC4 meta blocks mapped to named TC4R blocks (arcane_levitator, obsidian_tile, infused stones, crusted_taint...), biome assignment via `thaumcraft.taint.TaintBiomes.set`, dimension is data-driven and gated by `pocketPlaneEnabled` config, PocketNoise uses a different noise algorithm than 1.7.10 octaves.

## Phased deferrals (tracked, not lost)

- Client renderers for entities and special machine models — client phase.
- Spawn-egg items — skipped (vanilla summon works).
- Synthetic-node wisp-essence refilling and essence drops — needs aspect-container item API.
- Crystal-essence conversion of destroyed nodes/blocks — needs TC4R crystal-essence item API.
- Flux gas/goo spill from overloaded transducers, wisp spawning (salis mundus), crystal-wand void crafting — pending flux/wisp/golem systems.
- Warp tumor store caps use the config value (50) exactly like the original config default.
- Entities still unported: HorseZombie, HorseSkeleton, NightmareTH, BoatGreatwood, BoatThaumium, BlastPhial, Syringe projectile, EggIncubated, finite LightningBolt, ItemEggIncubated logic.

## Asset pipeline & client-safety — documented deviations

- Textures use singular `textures/block/` + `textures/item/` (vanilla atlas auto-stitches these across namespaces, the Forbidden Magic idiom); the custom `assets/minecraft/atlases/blocks.json` is removed. Plural `blocks/`/`items/` are NOT auto-stitched. Item renders go through the `assets/thaumichorizons/items/` definition layer (MC 1.21.4+).
- Localization is JSON-only (`en_us`/`ru_ru`, full key parity); the 1.7.10 `.lang` format is not loaded by MC 26.3.
- Tamable TH mobs (panther/gravekeeper/familiar) register AI goals in `registerGoals()` (server-only). Building `BreedGoal` in the constructor cast `ClientLevel`→`ServerLevel` on the client and disconnected players on spawn; goals now never construct client-side.
- Block-entity item persistence uses `ItemStack.OPTIONAL_CODEC` (permits empty slots), matching TC4R; plain `ItemStack.CODEC` rejected air/0 and dropped inventories on save.

## Addon wand recognition & primed-alchemite client safety

- TC4R `Wands.isWand` matched only the base wand item (`stack.is(ModItems.WAND)`), so the TH disposable wand — a `WandItem` subclass mirroring 1.7.10 `ItemWandCastingDisposable extends ItemWandCasting` — was rejected by the arcane workbench, focus use, charging and the vis HUD. The fork seam (`feat/addon-api`) now accepts any `WandItem` subclass; without add-ons the only `WandItem` is the base wand, so vanilla behavior is unchanged. Documented in TC4R `docs/ADDON_API.md`.
- The disposable wand sets default `WandKeys.PARTS` (disposable cap/rod) and `WandKeys.VIS` (25000 per primal) in its `Item.Properties` operator, so creative/given stacks are full and correctly named. The records are built directly; creating an `ItemStack` during registration crashed with "Components not bound yet". Added `item.Wand.disposable.cap/rod` translations (ru/en); `WandParts`/`WandVis` added to the `check_seams.py` API allowlist. Verified in-game: vis HUD renders and the name reads "Одноразовый кристальный жезл".
- The crystal wand stays a bare crafting ingredient, exactly like the original `ItemCrystalWand`.
- `AlchemitePrimedEntity` overrides `getMoveSimulationType()` = `SERVER_AND_CLIENT` (vanilla `PrimedTnt` idiom); the NeoForge dev-only guard threw "logical side not permitted to simulate movement" when a primed alchemite ticked on the client.
- Known deviation: the disposable wand is currently rechargeable; the original returned an empty `getAspectsWithRoom` / 0 `getMaxVis` on tick to make it single-use. Restoring that needs a TC4R recharge seam (deferred).

## Functional-block static models (machine atlases)

- The machines shipped as `cube_all`/`cube_bottom_top` over the 1.7.10 *custom-model atlases* (e.g. `dynamobase` 64×32, `soulsieve` 128×64, `inspiratron`/`soulforge` 64×64 cross-nets). Vanilla cube parents stretch the whole atlas onto every face, so the blocks rendered garbled. Root cause: the reference draws these with `ModelRenderer` box models / TESRs, not cube faces.
- Added `tools/gen_machine_models.py`, which parses the reference `Model*.java` box specs (`addBox` + `setRotationPoint` + texture offset) and emits static JSON `elements` with per-face UVs into the atlas (standard box unwrap, scaled to the 16×16 JSON UV grid). The flipped-TESR transform `X=8+mx, Y=24−my, Z=8−mz` reproduces the reference stacking; the dynamo base slab uses the unflipped ground placement.
- Ported static models: `visdynamo`, `essentiadynamo` (base slab), `inspiratron`, `soulforge` (minus animated brine), `bloodinfuser`, `soulbeacon`, `soulsieve`, `vortexstabilizer`. Verified in-game: each renders as its intended box geometry with correctly mapped atlas tiles, not a stretched cube.
- Already correct (single square face): `synthnode` (32×32), `modmatrix` (`bone` 16×16), `transductionamplifier` (vanilla iron).
- Still deferred (TESR/multiblock, not cube-representable): `vat` (3×3 multiblock over a 512×512 atlas the port does not ship). Its animated renderer belongs to the machine-renderer client phase.

## Textures larger than 16×16 — no splitting

- MC 1.7.10 did **not** cut oversized block textures into 16×16 tiles for `cube_all` faces: the whole image was stretched onto the face. Verified against the host port (`thaumcraft-4-reforged`), where TC4's `alchemyblock` (32×32) is still used as a plain `cube_all` and renders as one coherent 32×32 image. The same holds for the TH textures `eyecluster`/`brain`/`leviathan`/`vortex`/`synthnode` (32×32, one coherent picture per face) — so they are **not** sliced into 16×16 sprites in this port. Only textures with an `.mcmeta` animation strip (`cloud` 16×128, `void` 64×512, `blockcrystaldeep` 16×576) are frame-stacks, and MC 26.3 derives their frame size the same way MC 1.7.10 did (`min(width, height)`).
- `cloud`/`cloudglowingth` now use `cube_bottom_top` with `cloudtop` on the top and bottom faces. The reference `BlockCloud.getIcon` returns `icontop[meta]` for sides 0 and 1 and `icon[meta]` otherwise, so the top/bottom faces use the dedicated `cloudtop` texture, not the animated cloud side texture.

## Node monitor — OBJ block-entity renderer

- The node monitor is drawn by `NodeMonitorRenderer` (block-entity renderer) from `thaumcraft:models/obj/scanner.obj` + `thaumichorizons:textures/models/hexagon.obj`, with the `nodemon.png` ring atlas and the `nodemonscreen`/`nodemonscreenactive` overlays. The block model is intentionally empty (no cube elements) so nothing is double-drawn; the blockstate rotations are harmless leftovers since the renderer applies its own facing transform.
- Fixed the ring texture path: the renderer looked for `textures/models/nodemon.png` while the file ships as `textures/block/nodemon.png`.
- The inventory item was invisible (empty block model parent). It now uses `minecraft:item/generated` with a new 16×16 `textures/item/nodemonitor.png` rendered offline from the scanner OBJ (`tools/preview_obj.py`) — the gold hexagonal ring.

## Collision shapes

- `visdynamo` and `essentiadynamo` are thin base plates in the reference (`ModelQuarterBlock`, 16×1×16), but 1.7.10 left them with the default full-block bounds. The port now gives both a `VoxelShape` of `Block.box(0,0,0,16,1,16)` — a deliberate deviation from the original, requested for the plates not to occupy a full block.
- `nodemonitor` uses `Block.box(2,0,2,14,2,14)` (a flat hexagonal ring footprint) instead of a full cube.

## Inert crystal wand recognition

- The inert crystal wand is a crafting ingredient in the original (`ItemCrystalWand extends Item`), not a wand; it is "recognized" in two ways, both of which were missing in the port.
- Scan: the original registers an object tag `CRYSTAL 6 + MAGIC 4`. `ConfigAspects` now registers `vitreus 6 + praecantatio 4` for `CRYSTAL_WAND`, so the thaumometer detects it (previously the scan returned nothing because `AspectRegistry.getObjectTags` had no entry). Verified in-game with `/thaumcraft aspects item thaumichorizons:crystalwand` → `Vitreus ×6, Praecantatio ×4`; the charged wand returns `Praecantatio ×64, Instrumentum ×64` via the `Wands.isWand` rod/cap path.
- Vortex: the original `TileVortex.handleVoidCrafting` converts a thrown inert wand into the charged crystalline wand (crystal rod/cap + 25000 vis per primal). `VortexBlockEntity.handleVoidCrafting` now does the same, producing `WAND_CASTING_DISPOSABLE` (whose defaults already carry the crystal parts and full vis). This is the survival path to the charged wand. Not yet exercised in-game (needs a stabilized vortex); covered by Gate 2.
- The remaining original object tags (amuletMirror, focusAnimation, syringes, nutrients, alchemite, vat, soulBeacon, vortex, portal, spikes, …) are still unregistered in the port; tracked separately.

## SyntheticNode vis-network seam (range-0 sources)

- Root cause: TC4R `VisNet.drainVis`/`calculateNearbyNodes` reach a source only within **`source.getRange()`**. The TH dynamos faithfully keep `getRange() == 0` (as in 1.7.10) and expose their vis through an overridden `consumeVis`, so a range-0 source is invisible to `drainVis`. The ported `SyntheticNodeBlockEntity` copied the reference `TileSyntheticNode.updateEntity` verbatim (`VisNetHandler.drainVis`, no network attach), which worked on TC4 1.7.10's reachability model but never attaches on TC4R — so it could not draw from dynamos at all.
- Fix: `SyntheticNodeBlockEntity.tick` now calls `tickNetwork(tickLevel)` and draws via the parent-chain `consumeVis(aspect, 10)` instead of `VisNet.drainVis`, matching the canonical TC4R consumer idiom (`EnergizedNodeBlockEntity`, `VisChargeRelayBlockEntity`). This attaches the node to nearby `VisNode` sources (including range-0 dynamos) and drains them through their `consumeVis` override. Behaviour (auto-fill from the vis net) is preserved; only the TC4R transport idiom changed.
- Not a regression for natural aura nodes: TC4R `NodeBlockEntity` is not a `VisNode` and never registered with `VisNet`, so `drainVis` could not reach it before either.
- Verified in-game: EssentiaDynamo over an ignis jar drains it (64→40→0, `ticks` active), registers as a source; SyntheticNode attaches and fills (`aspects` ignis 0→20); NodeMonitor redstone A/B flips (lamp LIT on empty node → UNLIT on filled node). Alchemite ignite→fuse→explosion→chain reproduced separately (redstone-signal ignite consumed all 3 blocks).
- Same seam may affect other ported `drainVis` consumers (Vat, SoulExtractor) against range-0 dynamos; they are Gate 2 scope and not changed here.
