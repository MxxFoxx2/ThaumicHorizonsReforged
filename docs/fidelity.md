# Fidelity

Known deviations and incomplete port areas relative to the 1.7.10 reference. Unlisted behavior has not necessarily been exhaustively verified as 1:1.

## Player infusions (Chameleon Skin) — intentional redesign

The original applied Chameleon Skin as a permanent non-curative `Potion.invisibility` with a one-shot `PacketPlayerInfusionSync` (sendToAll at infusion time). Players joining later never received the sync, armor and held items kept rendering, and the toggle state could desync from the actual potion effect. The port replaces the mechanism (community requirement: fix the invisibility bug):

- The single source of truth is the `CreatureInfusionData` attachment (copy-on-death, synced to all tracking clients + `PlayerEvent.StartTracking` + join). No potion is involved.
- The server applies the vanilla `setInvisible` flag from the attachment state each change.
- The client cancels `RenderPlayerEvent.Pre` for chameleon-active players via `AvatarRenderState.id` — hiding body, armor and held item (vanilla potion invisibility leaves armor visible; here nothing renders).
- Other self-infusion effects (1 = jump + speed, 3 = regeneration, 4 = resistance) keep the original approach: permanent non-curative `MobEffectInstance`, re-applied on join/tick.

## Creature entities — modern-parent substitutions

- Panther / Gravekeeper / Familiar extend `TamableAnimal` instead of the ocelot lineage (modern `Cat`/`Ocelot` carry variant and avoid-player AI baggage). Always-tamed semantics and TH owner AI preserved.
- Familiar's wizard hat is restored as a dedicated cutout render layer. It ports the five hat parts and their original UV coordinates from `ModelFamiliar` on the existing `familiar.png` texture, using the modern Ocelot head pivot and applying pitch/yaw from `FelineRenderState`. In-game fit/occlusion still needs a visual check.
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
- FX compatibility is no longer entirely a vanilla fallback: `TcReforgedPort.TcFx` delegates wisp trails, bursts, node bolts, block sparkles, essentia trails, blocksplosions, containment capture, bubbles, dead-creature FX and Familiar's ambient sparkle to TC4R's `FxProxy`. Familiar now requests the legacy size-2, type-0 sparkle at a 3% chance per client tick (frame-rate independent); its in-game appearance and cadence still need a visual check.
- Vortex audio is now implemented as a client-side tickable looping sound, registered as `thaumichorizons:vortex`, streamed and limited to a 64-block range. Its position follows the block entity and its volume uses the legacy `1 / (1 + distance / 8)` curve; it stops when the block entity disappears or leaves the tracked nearby-chunk area. Confirm actual audibility, fade and teardown in a live client.
- VisDynamo aspect-toggle GUI deferred to the menu phase; toggles exist as BE fields.

## Souls / Vat / blood phase — documented deviations

- Soul-jar villager profession stored as a registry id string.
- TC4R taint-poison / blurred-vision effects omitted from blood-infuser and vat effect lists (no compat seam for them yet).
- Vat stabilizer symmetry scan counts only vanilla skulls (needs an InfusionCompat block predicate for TC4R skull variants).
- `resetStats` skipped (the port has no `IEntityInfusedStats` equivalent).
- Instilled-loyalty gate implemented via goalSelector priority instead of the original flag.
- Critter recipes 20/21/22 (undead horses, nightmare) exist but cannot produce their intended TH entities until HorseZombie, HorseSkeleton and NightmareTH are ported.
- Deferred: `EntitySyringe`/`EntityBlastPhial` projectiles (injector charge/fire logic works, projectile spawning is pending), brain-jar feeding seam, creative empty-soul-jar sub-item, and the remaining special/multiblock visuals. The static blockstates/models for the ten soul/vat blocks have been added; Node Monitor also has a dedicated renderer.
- Soul Jar capture/release code is now present: an infused creature is serialized into a jar item, placement copies the data into the BlockEntity, and wand use attempts to restore the entity. This was committed in `6fd7614`; the full capture → item persistence → placement → release cycle is not yet game-verified (see `verification.md`).
- `VatTrigger.register()` was dead code (never called); now registered from `FMLCommonSetupEvent` via `enqueueWork`. In-world formation verified 2026-11-16: the glass/pressure-plate/levitator blueprint converts to VAT/VAT_SOLID/VAT_INTERIOR on wand use, server-side replace with client-side gate.
- Vat formation pays crafting vis through the wand cap modifier (iron cap +10%, TC4 parity): 50 vis per aspect costs 5500 centivis with an iron cap, so a wand stocked with exactly 5000 per aspect legitimately fails to form the vat.

## Golems / lenses phase — documented deviations

- `EnumGolemTHType` replaced by TC4R `GolemType` extension values (identical stats incl. `visCost`); golem material derived from the block via a modern block/tag mapping (modern MC removed `Material`). CLAY is still never produced, parity with the original.
- Restored blocks use `defaultBlockState()` (no numeric meta) — facing/rotation of golem-restored blocks resets.
- Wand-pairing (core/upgrade/decoration) goes through TC4R `GolemBase.mobInteract` `GolemPartItem` flow.
- Baubles support dropped from LensManager (no Baubles on NeoForge 26.3); lens cases work from inventory only.
- Lens night-vision detection uses a duration<=255 heuristic.
- Liquefaction "nuggets" upgrade is inert (TC4R removed the smelting-bonus registry); purity uses TC4R `SpecialMining.find`.
- Disposable-wand recharge-prevention hack unnecessary (TC4R nodes recharge only the base `Wands.isWand` item).

## Pocket plane — dimension loads, gameplay verification pending

- Commit [`e38d16a`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/e38d16ae8e68544a3dc6d45627b00cc1cf19ce47) moved the previous local work into tracked sources: `PocketPlanes` SavedData, `PocketPlaneData`, `PocketPlaneGenerator`, `PocketPlaneChunkGenerator`, `PocketNoise`, `PocketDimension`; `PocketRegistry`; `GatewayPortalBlock`, `PortalTHBlock`, `SlotBlock`, `SlotBlockEntity`; `KeystoneItem`; and dimension, dimension-type and biome JSONs. The `thaumcraft.platform.Teleport` seam was added to the allowlist.
- The Pocket Plane sources compile against Minecraft 26.3 / NeoForge 26.3. The ChunkGenerator contract, SavedData codec, block-removal hook, entity-inside hook, item tooltip API, registry lookup APIs, and block-entity ticker were migrated. The dimension type and biome definitions were migrated to the current codecs in `7eb9b8c`; a fresh dedicated-server run reached `Done` and loaded `thaumichorizons:pocket_plane`.
- The duplicate `thaumichorizons:planarconduit` registration was consolidated into `PocketRegistry`.
- Still pending: generated terrain layers/caves/pockets, portal/slot/keystone behavior, return-position and teleport validation, Vortex-collapse integration, saved-data restart behavior, and client assets/rendering. The historical `.deferred-pocket/` staging directory is no longer part of the public source state; the current WIP classes and JSONs are tracked under `src/main/`.
- Deviations already baked in: TC4 meta blocks mapped to named TC4R blocks (arcane_levitator, obsidian_tile, infused stones, crusted_taint...), biome assignment via `thaumcraft.taint.TaintBiomes.set`, dimension is data-driven and gated by `pocketPlaneEnabled` config, PocketNoise uses a different noise algorithm than 1.7.10 octaves.

## Phased deferrals (tracked, not lost)

## Remaining content and behavior gaps

- Missing modern block registrations: Recombinator and Spike variants (iron/wood/tooth). Pocket Plane equivalents for Slot, PortalTH and Gateway now compile but remain runtime-unverified. Original BlockEntity behavior still missing includes TileCloud, TileLight, TileRecombinator and TileSpike; TileSlot has a counterpart in `SlotBlockEntity`. `TileCloud` is gameplay logic (weather, block/entity interactions and finite lightning), not merely a rendering task.
- Missing entities/projectiles/vehicles: HorseZombie, HorseSkeleton, NightmareTH, BoatGreatwood, BoatThaumium, BlastPhial and Syringe projectile. Incubated Egg and its projectile are now implemented in code (commits `5119c15` and `6fd7614`) with `ThrownItemRenderer` registered for the entity; gameplay still needs an in-game check. `EntityLightningBoltFinite` is spawned by the original cloud behavior and is not a separately registered mod entity.
- Mirror Amulet is now registered as a TC4R amulet bauble. Its infusion recipe and research page are present; linking uses TC4R's ordinary mirror block and its persisted link position. On player death, the handler offers inventory and bauble stacks to the linked mirror through the compatibility seam and drops a primordial pearl after transport. This behavior is code-complete but not yet game-verified. Planar Keystone still has a WIP `KeystoneItem` class in `PocketRegistry`, but its recipe/research path and runtime behavior remain incomplete/unverified. Planar Conduit and Incubated Egg are registered; the duplicate Planar Conduit id was consolidated into `PocketRegistry` (`ThItems` no longer re-registers it).
- Void Putty repair and Focus Illumination dye recoloring now have registered custom recipe serializers and recipes. Both still need an in-game check. Planar Theory, Transduction Amplifier, Vortex Stabilizer and Alch Incubation recipes are also present.
- Synthetic-node wisp-essence refilling/drops needs an aspect-container item seam; crystal-essence conversion needs a TC4R crystal-essence item seam; overloaded Transducer flux gas/goo and wisp spawning remain pending.
- Warp tumor store caps use the config value (50), matching the original config default.
- Render/FX polish still outstanding: Vat multiblock renderer/animation, VortexSound, Familiar hat layer and any missing special projectile renderers. The 20 creature renderers, Node Monitor renderer, and several static machine models are already present.

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
- The remaining original object tags are registered as of the focus-completion pass: syringes (`SYRINGE_EMPTY`, `SYRINGE_BLOOD_SAMPLE`, `SYRINGE_BLOOD`, `SYRINGE_INJECTION`), `NUTRIENTS`, `ALCHEMITE`, `VAT`, `VAT_SOLID`, `SOUL_BEACON`, `VORTEX`. `focusAnimation` and `itemEggIncubated` are registered. `amuletMirror` still has no explicit object-aspect registration even though the item, recipe, research page, mirror-link behavior, and death transport are implemented. `portal`, `spike`, `spikeWood`, and `spikeTooth` remain unregistered because their targets are not ported.

## SyntheticNode vis-network seam (range-0 sources)

- Root cause: TC4R `VisNet.drainVis`/`calculateNearbyNodes` reach a source only within **`source.getRange()`**. The TH dynamos faithfully keep `getRange() == 0` (as in 1.7.10) and expose their vis through an overridden `consumeVis`, so a range-0 source is invisible to `drainVis`. The ported `SyntheticNodeBlockEntity` copied the reference `TileSyntheticNode.updateEntity` verbatim (`VisNetHandler.drainVis`, no network attach), which worked on TC4 1.7.10's reachability model but never attaches on TC4R — so it could not draw from dynamos at all.
- Fix: `SyntheticNodeBlockEntity.tick` now calls `tickNetwork(tickLevel)` and draws via the parent-chain `consumeVis(aspect, 10)` instead of `VisNet.drainVis`, matching the canonical TC4R consumer idiom (`EnergizedNodeBlockEntity`, `VisChargeRelayBlockEntity`). This attaches the node to nearby `VisNode` sources (including range-0 dynamos) and drains them through their `consumeVis` override. Behaviour (auto-fill from the vis net) is preserved; only the TC4R transport idiom changed.
- Not a regression for natural aura nodes: TC4R `NodeBlockEntity` is not a `VisNode` and never registered with `VisNet`, so `drainVis` could not reach it before either.
- Verified in-game: EssentiaDynamo over an ignis jar drains it (64→40→0, `ticks` active), registers as a source; SyntheticNode attaches and fills (`aspects` ignis 0→20); NodeMonitor redstone A/B flips (lamp LIT on empty node → UNLIT on filled node). Alchemite ignite→fuse→explosion→chain reproduced separately (redstone-signal ignite consumed all 3 blocks).
- Same seam may affect other ported `drainVis` consumers (Vat, SoulExtractor) against range-0 dynamos; they are Gate 2 scope and not changed here.

## Vortex stabilizer raycast seam (COLLIDER vs outline)

- Root cause: `VortexStabilizerBlockEntity` raycast with `ClipContext.Block.COLLIDER`, but the vortex deliberately has **no collision shape** (original `BlockVortex.getCollisionBoundingBoxFromPool` returns `null`; only the outline bounds 0.3–0.7 exist). A COLLIDER clip therefore rays straight through the vortex and hits whatever is behind it, so powered stabilizers never acquired a vortex target (`hasTarget=0`, `targetPos` = the opposite stabilizer at distance 6). The original 1.7.10 `World.rayTraceBlocks(from,to)` hits blocks without a collision box (otherwise it could not lock nodes either), so the port's COLLIDER choice was the seam — not a timing/collapse race.
- Fix: raycast with `ClipContext.Block.OUTLINE` (uses `getShape`, the vortex's 3–13 box) instead of COLLIDER. This restores the original ray behaviour **without** adding entity collision, keeping the vortex non-solid as upstream.
- Verified in-game (cheat vortex to isolate from collapse): 6 powered stabilizers around a vortex all report `hasTarget=1`, target = vortex, and vortex `beams=6`; breaking one stabilizer runs `reHungrifyTarget` and drops `beams` to 5; re-placing and re-powering it restores `beams=6`. Note a re-placed stabilizer only becomes `redstoned` after a neighbour update (its `neighborChanged`), matching vanilla block-update semantics.

## Taint feeder eyes overlay (translucent pulse, not opaque)

- Root cause: `TaintEaterPigRenderer`'s eyes layer submitted the full pig model with `RenderTypes.eyes` (emissive **cutout**). `taintfeeder_eyes.png` is an opaque cyan silhouette (same in the original), so a cutout pass painted the whole body solid ice-cyan and hid the green `taintfeeder.png` base.
- Original behaviour: `RenderTaintfeeder.shouldRenderPass` binds the eyes texture and blends it over the whole model at `glColor4f(1,1,1, 0.32 + 0.32*sin(ticksExisted%45 * 8°))` with fullbright lightmap — a pulsing translucent glow, not an opaque recolor.
- Fix (two seams): (1) the layer uses `entityTranslucent` + `LightCoordsUtil.FULL_BRIGHT` with a pulsing alpha `(0.32+0.32*sin(ageInTicks%45*8°))`, reproducing the original blend instead of an opaque cutout. (2) The first attempt passed that ARGB alpha as the **7th** argument of `SubmitNodeCollector.submitModel`, but the 7-arg overload's last parameter is `outlineColor` (it forces `tintedColor=-1`, fully opaque). That produced a white silhouette outline plus an opaque cyan body that hid the base and its eyes. The layer now uses the 9-arg overload `(..., tintedColor, uvMapping=null, outlineColor=state.outlineColor)`, so the alpha actually tints the overlay and no outline is drawn. Verified in-game: green base with its dark eyes shows through a subtle pulsing cyan glow, no white border; ore boar shows its red eyes.
- The ore boar (`OrePigRenderer`) is a plain `PigRenderer` texture swap, matching `RenderOreBoar` (no extra passes). Its stone-gray skin with red eyes is intended; the earlier "ice slab" appearance was **not** intended — it was the legacy-UV texture bug below, now fixed.

## Entity texture UV layout (legacy 64×32 on modern 64×64 models)

- Root cause: the ported entity skins are 1.7.10-era **64×32** PNGs, but the modern `PigModel`/`CowModel` bake their `LayerDefinition` at **64×64**. Model UVs are normalized by the layer's declared height (64), so on a 32-row image every `v` samples half its intended row — faces land on empty/wrong texels and the mob renders as holes/slabs. Confirmed against vanilla 26.3 `pig_temperate.png`: 64×64 with the legacy skin in the top 32 rows and a fully transparent bottom half, i.e. the modern layout is exactly the legacy layout padded to 64×64.
- Fix: padded `oreboar.png`, `taintfeeder.png`, `taintfeeder_eyes.png`, `chocolatecow.png` from 64×32 to 64×64 (transparent bottom half), preserving the legacy top-half UVs. Originals remain in git history and `reference/1.7.10`. Verified in-game: ore boar and taint feeder render as solid, correctly textured pigs (red / dark eyes respectively), no slabs or missing faces.
- Audited the rest: modern `ChickenModel`, `SlimeModel`, `SpiderModel`, `AdultWolfModel`, `AdultFelineModel` are still **64×32**, so `goldchicken`, `scholarchicken`, the slimes, `sheeder`, the wolves and the felines need no change. The horses (`HorseModel`/`AbstractEquineModel`, 64×64 with a different UV layout, legacy skins 128×128) needed a real per-face remap rather than a pad — done in the horse-skin section below.

## Wand focus completion (registration, recipes, solar light)

- The four focus classes (`FocusAnimationItem`, `FocusDisintegrationItem`, `FocusIlluminationItem`, `FocusLiquefactionItem`) were ported but never registered as items, so the reference's `focusAnimation` / `focusDisintegration` / `focusIllumination` / `focusLiquefaction` research entries had no obtainable target and their recipe pages rendered blank. All four are now registered in `ThItems` with `FocusItem.properties` (stacks to 1, rare), matching the reference's `ItemFocusBasic` base; item models and `en_us`/`ru_ru` names added.
- The infusion/crucible recipes from the reference were added with the same aspect costs and ingredients: `focusLiquefaction` (infusion, instability 3, diamond + focus_fire + salis_mundus + focus_excavation), `focusDisintegration` (infusion, instability 7, focus_excavation + 2× liquid_death_bucket + primal_charm + alumentum), `focusAnimation` (infusion, instability 10, primordial_pearl + 2× golem_core_blank + 2× syringe_blood + 6 golem upgrades), `focusIllumination` (crucible, glowstone), `incarnationVat` (crucible, sugar → nutrients).
- `lightsolar` did not exist as a block, so the SOLAR upgrade of the illumination focus resolved the id through `BuiltInRegistries.BLOCK.getValue` (a defaulted registry) and received **air**, deleting the targeted block. The reference `BlockLightSolar` is registered as `lightsolar`; the port now has `LightSolarBlock` (same properties as `light`) which additionally ignites undead entities that touch it, matching `BlockLightSolar.onEntityCollidedWithBlock`. The focus now uses the registered holders directly instead of a registry lookup.
- `FocusContainmentItem.SLOW` used the 2-argument `FocusUpgradeType` constructor, which defaults the icon to `thaumcraft:textures/foci/slow.png`; the texture ships as `thaumichorizons:textures/foci/slow.png`. Now passed explicitly, like the other six focus upgrades.
- The brain block drops the brain block itself; the reference `BlockBrain` drops 2–3 `thaumcraft:zombie_brain`. The loot table now does that.
- Remaining recipe/research gaps include `greatwoodBoat`, `thaumiumBoat`, `planarKeystone`, `recombinator` and `pocketPlane`. The `mirrorAmulet` infusion recipe and research page are now present. The previously missing `alchIncubation`, `planarTheory`, `transductionAmplifier` and `vortexStabilizer` recipes were added in commits `5119c15` and `408e6cc`; Void Putty repair and Focus Illumination dyeing were added in `8512e83`.

## Creature scan aspects (entity tags never registered)

- Root cause: the port registered object/block aspect tags but had no counterpart of the reference's `addAspectsToAllTheThings` entity assignments, so TH creatures carried an empty aspect list in the TC4R aspect registry. A held thaumometer therefore showed the creature name but never any aspect icons, and completing a scan awarded nothing — the scan pipeline (`ScanManager.completeScan`) reads the registry, not the renderer.
- Fix: `CreatureAspects` subscribes to `FMLCommonSetupEvent` and, guarded by `Compat.port().aspects().isAvailable()`, registers entity aspect data for the 16 ported creature types through the TC4R aspect registrar, 1:1 with the reference (e.g. chromatic sheep = bestia 2 / terra 2 / sensus 2 / permutatio 2, ore pig = bestia / terra / sano / ordo).
- Verified in-game: `/thaumcraft aspects entity` lists match the reference; held-thaumometer hold-scans on a chromatic sheep and an ore pig show the aspect icons under the name (the hand renderer draws icons only once `ScanManager.hasBeenScanned` is true) together with the server-side research-point award chat; a meat nugget object scan and a synthetic-node phenomenon scan (ignis ×20) reproduce the same readout path.
- Wand-tap extraction from a SyntheticNode reproduced end-to-end afterwards: a right-click series on the node drained `aspects` ignis 20→17 while the wand gained `thaumcraft:wand_vis {ignis: 300}` (3 taps × 1 vis, centivis storage), confirming the `WandInteractable` hold-tick seam works with default iron/wood wand parts.

## Horse skin remap (legacy 128×128 → modern 64×64, plus baby sheets)

- Root cause: `endersteed*` / `nightmare*` skins are 1.7.10 `ModelHorse` sheets at **128×128** with a multi-segment layout (3-segment legs and tail, separate muzzle/jaw, private ear block), while modern 26.3 `AbstractEquineModel` bakes a **64×64** single-box-per-part layout and `BabyHorseModel` uses a *different* 64×64 layout again (vanilla ships separate `*_baby.png`). Feeding the legacy sheet to the modern model sampled wrong texels → slabs/holes.
- Fix: `tools/remap_horse_skins.py` rebuilds all 8 adult sheets face-by-face (legacy UV rects → modern UV rects, NEAREST resample; legs/tail composed by stacking the legacy segment side faces) and generates 8 new `*_baby.png` sheets on the baby layout. The legacy tail art is a thin plume with transparent margins while the modern tail is a solid box, so tail faces are composited over opaque black (`solidify`) instead of carrying the margins through as see-through holes; the baby tail is horizontal (d=8, h=3) and maps from a single legacy segment rather than a vertical stack. An alpha audit over every model UV rect reports 0 transparent texels in all 16 sheets.
- `EndersteedRenderer.getTextureLocation` now reproduces the reference `RenderEndersteed.getEntityTexture` armour switch on the modern signal: `state.bodyArmorItem` iron/gold/diamond horse armour selects the matching body sheet, and `state.isBaby` selects the baby-layout variant of that same sheet (the reference scaled the armored adult sheet for foals; the port keeps the modern baby UVs but preserves the armored look). The legacy `ArmorItem` summon tag no longer exists in 26.3; armour lives in the equipment body slot (`armor.body` in commands).
- Verified in-game after client restarts with the regenerated sheets: plain adult renders as a solid black horse (body/neck/head/ears/legs/tail, no slabs or holes); a foal (`Age:-1`) renders solid on the baby sheet with the legacy markers intact (purple eye, green mouth); equipping iron horse armour visibly swaps to the iron body sheet on both the adult (grey plates on neck/legs, red harness on the body) and the foal (same plates on the baby layout), keeping the purple eye. Gold/diamond use the same switch branch as iron.
- Open seams (out of scope here): `EndersteedRenderer` adds no vanilla-style saddle/armour overlay layers (`HorseMarkingLayer` / `SimpleEquipmentLayer`), so armour is only the body-texture swap exactly like the reference; the `nightmare` entity itself is not ported yet, its 4 sheets + baby variants are ready for when it is.


## Additions on main through 2026-11-17

- `PlanarConduit` item plus `planarTheory`, `transductionAmplifier` and `vortexStabilizer` recipes: commit [`408e6cc`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/408e6cc49b87d9c9922442ad8e0de8b13174d9e2).
- `EggIncubatedItem`, `EggIncubatedEntity` and `alchIncubation` recipe: commit [`5119c15`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/5119c1582e24a798a436ffa67f01fadb06304f64). The thrown entity now has a registered `ThrownItemRenderer`; projectile and hatching behavior still need an in-game check.
- Soul Jar creature serialization/capture, item-data transfer on placement and wand-release implementation: commit [`6fd7614`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/6fd7614d00ceb22d282ab6009483ca1f0f14e6e1). This is code presence, not a passed gameplay test.
- Pocket Plane source and dimension data: commit [`e38d16a`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/e38d16ae8e68544a3dc6d45627b00cc1cf19ce47). The sources compile against MC 26.3 / NeoForge 26.3. Commit [`7eb9b8c`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/7eb9b8c) migrated the dimension type and biome definitions to current codecs; a fresh dedicated-server run reached `Done` and loaded `thaumichorizons:pocket_plane`. Terrain and portal behavior remain game-unverified.
- Mirror Amulet item, infusion recipe, research page, mirror-link behavior, and death-item transport: commit [`8538a7f`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/8538a7f). Runtime behavior still needs an in-game check.
- Void Putty repair and Focus Illumination dye recipe serializers and recipes: commit [`8512e83`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/8512e83). Both still need an in-game check.
