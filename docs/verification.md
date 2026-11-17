# Verification

Manual gates. Headless builds and the seam checker do not replace launching the client.

## Cloud blocks — PR #2, code and CI only

- [ ] Place all ten cloud item variants; confirm each places the matching cloud texture/type and remains correct after save/reload.
- [ ] Without planarClouds, confirm wand use does not remove the cloud or consume vis. With the research and at least 100 AIR vis, confirm one right-click removes the cloud, consumes exactly 100 AIR vis and drops the matching invulnerable cloud item.
- [ ] Confirm each picked-up variant can be placed again without changing its type; test multiplayer pickup and ensure no duplicate item.
- [ ] Test precipitation and entity effects in Overworld rain, including warm/cold biomes, fire/acid clouds, growth clouds and rain pause/resume. Confirm the synchronized weather columns appear/disappear with rain; save/reload while raining.
- [ ] Test periodic lightning from thunderclouds and Animus clouds; confirm Animus clouds only convert eligible blocks and spawn temporary berserk TH golems.
- [ ] Test alloy nugget and meat drops, primal aspect orbs, XP orbs, cow-to-mooshroom conversion and soil-specific growth results.
- [ ] Test acidic-cloud Thaumcraft dissolve damage and verify the full entity column is processed; test fire, enderman, blaze and firebat effects.
- [ ] In a live client, verify long-column rain/snow/ember rendering, variant tints, UV scrolling, warm/cold biome texture selection, rain start/stop synchronization, column-depth updates and chunk unload/reload persistence.


## Spike blocks — source and CI coverage only

- [ ] Place metal, wood and tooth spikes on all six sturdy support faces; confirm they orient away from the support and survive save/reload.
- [ ] Remove each support block; confirm the spike drops exactly one matching block item and disappears.
- [ ] Walk through/stand against each spike orientation; confirm the collision bounds match the old 1/16 inset and entity contact applies 1 cactus damage per collision tick.
- [ ] Check the OBJ render and all three textures from every direction; check block/item icons in inventory and hand. The current inventory/hand model is a generated 2D texture icon, not the old 3D inventory model.

## Injector projectiles — source and CI coverage only

- [ ] Load a seven-slot injector with ordinary syringe ammunition and phial ammunition. Confirm Shift-right-click opens the loader and that the first non-empty slot cycles into the firing position.
- [ ] Hold the injector for short, medium and full charges. Confirm the legacy draw curve/threshold, one durability point per successful shot, and server-side ammunition rotation without duplicate client entities.
- [ ] Fire an ordinary syringe at a living target. Confirm its stored PotionContents effects are applied only after a successful hit; hit a block and recover the embedded syringe, then confirm its potion data is intact. Check pickup restrictions for non-player shooters and despawn behavior.
- [ ] Enchant the injector with Power, Punch and Flame. Confirm Power increases dart damage, Punch applies knockback, and Flame ignites a hit target for five seconds. Confirm a full charge produces the critical-arrow effect.
- [ ] Fire phial ammunition from the injector and use a phial directly. Confirm each creates one splash projectile with the same effect list and splash behavior as the source syringe item, and that survival consumption/creative preservation matches the original.
- [ ] Test both projectiles with two clients and through save/reload while embedded/in flight. Check that projectile state and potion payload remain synchronized.
- [ ] Inspect projectile appearance in hand, flight and impact. The syringe currently uses a 2D syringe item sprite via ThrownItemRenderer; replace/verify against the legacy 3D model and check Blast Phial tint/scale/orientation.

## Undead horses and NightmareTH — source and CI coverage only

- [ ] Spawn `thaumichorizons:horse_zombie`, `thaumichorizons:horse_skeleton` and `thaumichorizons:nightmare`; confirm each entity type, display name, collision size, and renderer load without model/texture errors.
- [ ] Use all three spawn eggs on valid ground in survival and creative. Confirm each egg creates only its matching entity and consumes only in survival.
- [ ] Confirm HorseZombie is treated as undead and has the vanilla zombie-horse base attributes plus exactly +20 max health. Confirm HorseSkeleton is undead and has exactly +0.1 movement speed. Save/reload and verify modifiers do not stack.
- [ ] Mount and saddle all three mounts; test adult/foal state, save/reload, rider dismount and multiplayer tracking. Check Zombie/Skeleton equipment layers and Nightmare's four armor textures plus saddle layer.
- [ ] Complete `nightmareInfusion` in the Vat. Confirm the output is `thaumichorizons:nightmare`, not `thaumichorizons:endersteed`, and its Thaumometer aspects match Beast 3, Eldritch 4, Fire 4 and Travel 4.
- [ ] With a rider, test low/medium Nightmare jump charges and confirm short-range Endersteed blink. In the Overworld and Nether, use a full jump charge and confirm travel to the other dimension, rider remains mounted, orientation is preserved, and arrival is collision-free with no liquid overlap. Confirm other dimensions never invoke cross-dimensional travel.
- [ ] Confirm Nightmare is fire immune, can rise through lava without accumulating fall damage, turns grass underfoot into dirt, burns legacy plant/web/leaves blocks at body/head level, and is not slowed by cobwebs. Confirm the source explosion appears only on successful dimension travel.
- [ ] Test repeated dimension trips, death/respawn, server restart, dismount before full jump, and destination with no safe landing column. Check for duplicate Nightmare entities, detached riders, or lost passengers.

## Greatwood and Thaumium Boats — source and CI coverage only

- [ ] Craft each boat through its Thaumcraft arcane-research page; verify Greatwood costs 5 ORDER / 5 EARTH / 5 WATER and uses five Greatwood Logs, and Thaumium costs 10 FIRE / 10 WATER / 10 ORDER / 10 EARTH with four Water Shards, four Thaumium Ingots and one Greatwood Boat.
- [ ] Place both boats on water using the item; confirm survival consumption / creative preservation, passenger mounting, steering, dismounting, pickup and correct item drops.
- [ ] Confirm Greatwood Boat does not work as a lava boat. Confirm Thaumium Boat floats and can be steered in source and flowing lava, does not burn, and remains present after save/reload.
- [ ] Ride Thaumium Boat through lava with a player and a living mob; confirm passengers are extinguished and receive/retain Fire Resistance while aboard in lava, and that the effect expires normally after leaving.
- [ ] Test dispensers with each boat above water; test Thaumium Boat over lava and Greatwood Boat over lava (the latter should not place). Check blocked placement and stack consumption.
- [ ] Test boat collisions, damage/pickup, passenger synchronization with two clients, dedicated-server behavior, and custom model/texture loading from multiple angles. Compare movement speed with the original; legacy 1.7.10 speed multipliers are not assumed to match modern paddle physics.

## Gate 1 — after machines + creatures (V-phase)

```sh
./gradlew build --no-configuration-cache
python -I tools/check_seams.py
./gradlew runClient
```

- [x] Mod loads alongside TC4R, no mixin/attachment errors in log
- [x] Creative tab shows TH items, blocks, machines
- [x] Thaumometer scans TH items and creatures; aspects present *(held-thaumometer readouts reproduced: chromatic sheep (bestia/terra/sensus/permutatio) and ore pig (bestia/terra/sano/ordo) show aspect icons under the name — the hand renderer draws icons only for completed scans — plus server research-point award chat; meat nugget object scan and synthetic-node phenomenon scan (ignis ×20) reproduced; entity tag lists match the reference via `/thaumcraft aspects entity`)*
- [x] Research browser: `ThaumicHorizons` category opens, entries visible, page text renders
- [x] Machines: place VisDynamo near elements → vis net joins; EssentiaDynamo above jar → drains; NodeMonitor on node → redstone *(EssentiaDynamo jar drain reproduced (64→40→0, ticks active); NodeMonitor redstone A/B reproduced (lamp LIT on empty node → UNLIT on filled node); vis-net join reproduced (SyntheticNode attached to dynamo source and filled). VisDynamo wand-click join verified at code level (addSource), not separately exercised)*
- [x] SyntheticNode: infusion craft (`etherealShard`) → place → fills from vis net; wand tap extracts vis *(recipe 1:1 with reference — infusion output is the node block, wand tap extracts rather than creates; placed BE fills from the vis net (aspects ignis 0→20) — reproduced; wand-tap extraction reproduced: right-click series drained ignis 20→17 and the wand gained `thaumcraft:wand_vis {ignis: 300}` (3 taps × 1 vis); `getId()` = SYNTHETIC at code level; the infusion run itself not separately exercised)*
- [ ] SyntheticNode Wisp Essence: right-click the node with a Wisp Essence stack; confirm the stack is consumed and the first stored aspect's maximum capacity increases by 4 per essence. Break the node and confirm it drops one Wisp Essence per 4 units of maximum capacity, preserving the legacy item aspect payload (2 aspect per returned essence). Save/reload between insertion and break.
- [ ] Liquefaction Focus `Nuggets` upgrade: mine a smeltable ore with 1, 2 and 3 upgrade levels; confirm each level independently adds a 45% chance to spawn one matching TC4R bonus item (iron/gold/copper/tin/silver/lead nuggets or cinnabar quicksilver drop), and that blocks transformed by the focus's meltable rules do not produce the bonus. Confirm the bonus is server-side and survives multiplayer pickup without duplicating.
- [ ] Alchemite crystallized essentia: detonate near blocks with and without object aspect tags. Confirm tagged blocks yield one invulnerable `thaumcraft:crystallized_essence` stack per aspect with the expected amount and embedded aspect, without normal block drops; untagged blocks retain ordinary drops. For aura nodes, repeat enough times to observe the 25% essence/removal, 25% FADING and 50% UNSTABLE branches; confirm node essence matches its current aspects. Check multiplayer pickup and ensure no duplicate drops. The legacy `BlockAiry.explodify` and its exact sparkle are approximated by modern node removal and blocksplosion FX, so inspect the visual and node cleanup behavior too.
- [ ] Focus Disintegration: hold the focus on an aspect-tagged block until it is removed and confirm it yields the appropriate aspect-specific crystallized essence instead of the block; target an aspect-tagged dropped item stack and confirm its count is converted into the corresponding essence amounts and the original item entity is removed. Confirm the essence item is not consumed by subsequent focus targeting and that multiplayer has no duplicated conversion.
- [ ] Transduction Amplifier overload: use an energized node with the TC4R Node Transducer above it and TH amplifiers on the four horizontal sides. Verify redstone already applied before placement activates an amplifier on its first tick. With two active amplifiers, stand living targets within 10 blocks and confirm a node-bolt visual plus 4 magic damage every 50 ticks. With three, confirm the 1-in-4 overload pulse spills full-strength Flux Gas when the random Y offset is positive and Flux Goo otherwise, at a random offset in the surrounding 16×16×16 area. With all four active, confirm the energized node becomes a Vortex exactly once, its aura aspects are preserved, and the overlying TC4R Node Transducer is stopped (`status = -1`) rather than later reverting or disturbing the Vortex. Save/reload and repeat; watch for duplicated blocks or wiped Vortex aspects.
- [x] Vortex: absorbs blocks in 5×5×5, hungry-node pull works, stabilizer unhungrifies *(5×5×5 absorption reproduced (surrounding layer+floor removed, centre kept); hungry pull reproduced (armor stands dragged in and consumed). Stabilizer unhungrify reproduced after raycast fix: 6 powered stabilizers lock a cheat vortex (beams=6, all hasTarget=1, target=vortex); breaking one drops beams to 5 (reHungrify); re-placing + re-powering restores beams=6. Root cause was COLLIDER raycast passing through the collision-less vortex; now OUTLINE — see fidelity.md)*
- [ ] Vortex inside the Pocket Plane: place a non-cheat vortex and verify it does not consume the surrounding 5×5×5 blocks, does not pull/damage entities as a hungry node, and does not start collapsing from its 2400-tick counter.
- [ ] Vortex ambient sound: hear one looping sound per nearby vortex; confirm it follows the block position, fades with distance, stops on block removal/collapse/chunk unload, and does not duplicate after dimension changes or resource reload.
- [ ] Familiar hat: spawn `thaumichorizons:familiar`; confirm the hat is aligned to the head, follows head pitch/yaw while moving/sitting, and does not z-fight or clip through the ears.
- [x] Creatures: `/summon thaumichorizons:chromaticsheep` etc. spawn, behave (color cycle, shear, eat stone) *(22 summons spawn/render without crash after the goal-constructor CCE fix; chromatic color cycle reproduced (Color 10→13); self-shear reproduced (SelfShearingSheep Sheared=1 + wool drop); manual shear with shears reproduced; eat-stone reproduced (OrePig consumed a cobblestone drop, percent 0→45 and excreted a thaumium nugget); eat-taint reproduced (TaintEaterPig removed a placed crusted_taint → air). Entity skins re-verified visually after the 64×32→64×64 UV pad (ore boar / taint feeder / chocolate cow) and the eyes-overlay tint/outline fix: both pigs render solid with correct eyes, no slabs or white border. Horse skins remapped per-face from legacy 128×128 to the modern 64×64 adult + baby layouts (`tools/remap_horse_skins.py`, 16 sheets, alpha-audited 0 holes): plain adult and foal render solid with the legacy purple-eye/green-mouth markers, and iron horse armour visibly swaps the body sheet on both adult and foal)*
- [x] `/thaumichorizons chameleon true` → see Invisibility checklist below *(single-client A/B verified: true hides body/armor/held item, false restores; two-client checklist still open)*

## Gate 2 — after souls/Vat/GUI phase

- [ ] Soul jar: wand-right-click an infused critter → jars it (name label), item survives inventory/drop/save/reload, placement preserves NBT, wand release restores the original entity. Capture, data transfer and release code were added in `6fd7614`; the full cycle has not yet been reproduced in-game.
- [ ] Containment focus captures LostSoul
- [ ] Familiar: confirm occasional Thaumcraft sparkles appear around it with normal particle settings and do not appear when particles are disabled/minimal.
- [x] Vat multiblock forms *(reproduced in-world 2026-11-16: blueprint converts to VAT blocks and vis is consumed server-side after trigger-registration fix)*
- [ ] Curative Vat mode cures with containment foci
- [ ] Creature infusion: jarred critter + components in vat → infusion runs → TH creature spawns
- [ ] Corpse effigy + self-clone flow works
- [ ] Blood infuser: syringe draw/inject cycle
- [ ] All 9 registered menus (Case, Vat, Soul Sieve, Inspiratron, Soulforge, Blood Infuser, Injector, Fingers, Vis Dynamo) open; slots behave and VisDynamo aspect toggles persist
- [ ] Inspiratron: soul → research completion fires

## Additional content checks added after the original Gate 2 pass

- [ ] Incubated Egg: obtain via `alchIncubation`, throw it, confirm projectile renders and hatches a baby chicken on impact.
- [ ] Recombinator: place it directly above a node, power it with redstone and confirm activation after placement/reload. After the 50-tick warm-up, confirm it samples nearby unlocked aura nodes; verify the four combination priorities in order (primal+primal, source-primal, target-primal, any+any), random valid result selection, one source aspect consumed, one target aspect consumed, and a 1-in-3 chance to reduce the target's base amount. Confirm the 32-block node-zap effect and persistence of active/count state. Static block geometry and a dedicated moving-pearl/active-ring/underlying-node beam renderer are present; in-game visual parity remains to be checked.
- [ ] Recombinator renderer: with redstone power and a TC4R aura node directly below, confirm the pearl bobs vertically, the animated lightning ring appears above it, and a beam joins the machine to the node. Remove power and confirm the ring/beam stop; verify the pearl returns smoothly, and save/reload while powered. Compare texture orientation, ring animation speed, vertical offsets and alpha with the legacy renderer.
- [ ] Planar Conduit: craft using `planarTheory`; verify the Transduction Amplifier arcane recipe and Vortex Stabilizer infusion recipe against the reference ingredients/aspect costs.
- [ ] Confirm the thrown Incubated Egg renders in-game. `ThrownItemRenderer` is now registered for `ThaumicHorizons:eggincubated`; no missing-renderer log or invisible projectile in a live client.
- [ ] Mirror Amulet: craft it, link it to an ordinary TC4R mirror, die with inventory and bauble items, and confirm the items are transported through the mirror and the amulet is consumed.
- [ ] Void Putty repair: combine a damaged repairable item with Void Putty and confirm the repaired item is returned with damage reset.
- [ ] Focus Illumination dyeing: combine an Illumination Focus with one or more dyes and confirm the focus color changes to the legacy dye color.
- [ ] Focus Illumination placement: place normal and Solar lights using several focus colors; confirm the invisible block state preserves indices 0–15 after save/reload and its ambient sparkle uses the selected palette color. Confirm Solar light still ignites undead for five seconds.
- [ ] Arcane Lens radial HUD: wear Goggles of Revealing and keep one or more lenses in the inventory or a Lens Case; hold V, choose a lens by pointing at its icon, and release V to equip it. Confirm the current-lens center icon/tooltip, mouse capture restoration, and Shift+V removal. This implementation is not yet manually tested in a live client.

## Invisibility checklist (community requirement — run every phase touching infusions)

Two clients on the same server:

- [ ] `/thaumichorizons chameleon true` (or X key with infusion granted): player fully invisible **including armor and held item** on BOTH clients
- [ ] A player who logs in later sees the state correctly (no ghost/gap)
- [ ] State survives: relog, death/respawn, `/reload`
- [ ] `chameleon false`: player visible again, no lingering effects
- [ ] Chameleon without the infusion slot filled does nothing
- [ ] X/C keys toggle only when the infusion is present (server-verified)

## Final release gate

- [ ] Both `build` + `check_seams` green on a clean checkout
- [ ] `docs/fidelity.md` matches reality (spot-check 5 deviations)
- [ ] Full texture pass: no missing-model purple/black cubes
- [ ] Dedicated server boot + `/reload` + world save/reload
- [ ] Backport branches (26.1.2, 1.21.1) build and pass Gate 1


## Planar Keystone and Slot — source and CI coverage only

- [ ] Complete `pocketPlane` research and craft a blank Keystone. Verify central Planar Conduit, four Quartz, four Balanced Shards, and TRAVEL 32 / VOID 32 / EXCHANGE 16.
- [ ] Complete `planarKeystone` research and craft the alternate Keystone. Verify central Diamond, two Quicksilver, two Balanced Shards, and TRAVEL 24 / VOID 24 / ELDRITCH 12.
- [ ] Craft the Slot receptacle from its `planarKeystoneSlot` infusion page. Verify central Planar Conduit, four Gold Ingots, four Void Metal Ingots, and VOID 32 / TRAVEL 24 / MECHANISM 24 / ELDRITCH 24.
- [ ] Confirm Keystone and Planar Conduit appear in the creative tab; place the Slot item and inspect the static receptacle model and collision/interaction target.
- [ ] Right-click a valid generated Pocket Plane vortex with a blank Keystone. Confirm it binds to the correct plane, persists through inventory/world save and displays the bound tooltip; confirm invalid vortices and non-Pocket-Plane dimensions do not bind it.
- [ ] Insert a bound Keystone into an empty Slot; remove it with an empty hand; confirm the same plane ID is preserved. Test inventory-full drops, invalid plane IDs and multiplayer synchronization.
- [ ] Build both gateway orientations around the Slot using the original frame: four `thaumcraft:advanced_alchemical_construct` corners and eleven `thaumcraft:ancient_stone` border blocks. Confirm substituted/arbitrary blocks are rejected; activate with a wand, verify aspect cost, portal opening/closing, the four-portal limit, and frame restoration after teardown/reload.
- [ ] Check the legacy custom 5×5 PlanarGateway research-page visualization and the inlaid Keystone model separately: neither is claimed complete by the current static Slot model and normal Infusion page.

## Pocket Plane verification (source commit `e38d16a`, data fix `7eb9b8c`)

The Pocket Plane classes and data JSONs are tracked, and the sources compile against MC 26.3 / NeoForge 26.3. Build, seam, dedicated-server startup, and dimension load pass; the gameplay checks below remain open. The source Vortex now consumes a primordial_pearl item after stabilization, creates the pocket plane, and records the source dimension + return coordinates. The generated vortex and source vortex both use the same saved plane ID for bidirectional player travel. When the source vortex collapses, its generated pocket vortex is removed after the original 25-tick delay. CI validates compilation and JAR contents only; no live-world travel has been marked tested. Portal source endpoints now persist a dimension identifier, coordinates and frame orientation. Portal blocks implement both entry into the Pocket Plane and return to the recorded source world; Keystone binding/insertion/extraction and wand-triggered slot opening are wired. The gateway geometry check now validates the 3×3 opening and 15 frame blocks. These are code/CI checks only; no live-world portal travel has been claimed as tested.

- [x] `gradlew.bat build --no-configuration-cache` passes after adapting `PocketPlaneChunkGenerator` to the 26.3 ChunkGenerator API.
- [x] `python tools/check_seams.py` passes with the `thaumcraft.platform.Teleport` seam.
- [x] `thaumichorizons:planarconduit` is registered exactly once in `PocketRegistry`; the dedicated server starts with the mod loaded.
- [x] Pocket dimension loads on a dedicated server; dimension, dimension type and biome JSONs resolve.
- [ ] PocketPlaneGenerator produces the intended layers/caves/pockets; confirm the central 5×5 spawn platform uses `thaumcraft:arcane_stone`, test several seeds and verify deterministic chunk generation.
- [ ] Keystone item creates/binds the intended pocket plane and preserves its plane id through inventory, drop/pickup and save/reload.
- [ ] Slot gateway frame detection works in both orientations; only the 3×3 opening is empty, all 15 perimeter blocks are present, and wand activation consumes the expected vis from the wand in hand.
- [ ] Keystone bind at a Pocket Plane vortex, insert into a Slot, extract with an empty hand, inventory-full drop, and save/reload all preserve the exact plane ID.
- [ ] Portal teardown restores the frame and clears only the matching saved portal entry; destroying the Slot or removing its Keystone does not leave a stale entrance. Breaking a Slot with an inserted Keystone must drop the same plane-bound item, close the source and Pocket Plane portal ends, and release the portal index for reuse.
- [ ] Create four portals linked to one Pocket Plane and verify each surface/metadata mapping (A/C/B/D) enters and returns through the correct side.
- [ ] Drop a TC4R Primordial Pearl (thaumcraft:primordial_pearl; legacy Eldritch Object metadata 3) into a source Vortex with six stabilizing beams. Confirm exactly one Pocket Plane is generated, the pearl is consumed only on success, the source vortex records the plane ID, and the pocket vortex is created at (0, 129, 256 × plane ID).
- [ ] Stand on the source Vortex and confirm transfer to its Pocket Plane after the vortex is created; confirm the Pocket Plane vortex returns to the recorded source dimension and original vortex coordinates.
- [ ] Save/restart the world, repeat both directions, and confirm the saved source dimension and return coordinates are restored.
- [ ] Travel from Overworld and a non-Overworld source dimension into the Pocket Plane and back reaches the correct source dimension/position and exit orientation, preserves player state, and safely handles missing/unloaded destinations.
- [ ] PocketPlanes SavedData survives server restart and world save/reload. After joining the reloaded save, use a portal whose source-vortex chunk is not loaded and verify its endpoint and plane ID resolve correctly. In the same client process, switch from a save containing several Pocket Planes to a fresh save and confirm no prior plane IDs, coordinates or portal links leak into it.
- [ ] Remove or disable the source vortex's stabilizer beams until it collapses; after the 25-tick collapse delay, confirm only the matching pocket-plane vortex at (0, 129, 256 × plane ID) is removed, and confirm the other plane data and portals remain intact.
- [ ] Portal, Gateway and Slot blocks have complete blockstates, models, textures and no missing-renderer/model log.
