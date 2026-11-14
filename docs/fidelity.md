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

## Phased deferrals (tracked, not lost)

- Client renderers for entities and special machine models — client phase.
- Spawn-egg items — skipped (vanilla summon works).
- Synthetic-node wisp-essence refilling and essence drops — needs aspect-container item API.
- Crystal-essence conversion of destroyed nodes/blocks — needs TC4R crystal-essence item API.
- Flux gas/goo spill from overloaded transducers, wisp spawning (salis mundus), crystal-wand void crafting — pending flux/wisp/golem systems.
- Warp tumor store caps use the config value (50) exactly like the original config default.
