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

Where the original used TC4 metadata items without a TC4R counterpart:
`itemResource` 3→quicksilver, 6→crystallized_essence, 7→enchanted_fabric, 10→quicksilver_drop, 14→wisp_essence, 16→void_seed, 1→thaumium_ingot; `blockMetalDevice:9`→alchemical_construct, `blockLifter`→arcane_bellows, `customPlant 4/5`→tainted_plant/ethereal_bloom, `blockTaint:2`→tainted_soil, `cosmeticSolid:0`→arcane_stone, `blockJar:3`→void_jar, `table:15`→arcane_worktable, `customPlant 1/2`→shimmerleaf/vishroom; wool meta 32767→white wool; potion stacks 8233/8269→plain `SPLASH_POTION` without effect data (data-only for now; the vat GUI refines this later).

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
