# Verification

This file separates release-blocking smoke coverage from the unverified behavior inventory.

- `[x]` records evidence observed on the named revision. A build, source inspection or CI run is not gameplay evidence.
- The six scenarios in **Prerelease smoke gate** are the only standing manual gate for an experimental prerelease. Automating a scenario is encouraged; mark it complete only after the automated or manual scenario actually passes on the current revision.
- Plain bullets in the inventory are unverified behaviors and parity questions, **not assigned tasks and not individual release blockers**. Use them when changing the corresponding subsystem or investigating a report.
- Test one representative of repetitive variants first. Exhaustive variant and 1.7.10 visual-parity passes are optional follow-up work unless a change specifically affects them.
- Until the inventory is covered by evidence, releases must retain the experimental prerelease warning and must not claim fully verified 1:1 behavior.

## Prerelease smoke gate

Automated prerequisite for the current revision:

```sh
gradlew.bat build
python -I tools/check_seams.py
python -I tools/check_assets.py
```

- [ ] **Boot:** launch a live client and a dedicated server, create or load a world, and complete a resource reload without TH crashes, mixin failures, missing models or missing textures.
- [ ] **Vat and Soul Jar:** form a Vat, capture an infused creature in a Soul Jar, complete a creature infusion, release the stored creature, and save/reload. Confirm entity identity and data survive without duplication or item loss.
- [ ] **Two-client infusions:** toggle Chameleon Skin and Spider Climb with two clients, including a late join, relog and death/respawn. Confirm visibility, armor/held-item rendering, key authorization and persisted state agree on both clients.
- [ ] **Pocket Plane:** create a plane from a stabilized Vortex, travel there and back, restart the world, repeat the round trip, and close the portal. Confirm return dimension/position, plane ID and SavedData survive, and teardown releases the portal without corrupting the frame.
- [ ] **Persistence and inventory:** exercise representative block-entity, item-component, entity and player-attachment state through save/reload. Use the Vat and Blood Infuser menus to move items and change settings; confirm no crashes, duplication, lost stacks or client/server disagreement.
- [ ] **Visual smoke:** inspect the Vat, one static machine, one custom entity, one Spike and the Syringe in world/inventory contexts. Catch invisible or obviously broken geometry and render-log errors; exhaustive UV, animation and 1.7.10 parity comparison is not required for the prerelease gate.

Passing this gate supports an **experimental prerelease**, not a stable or exhaustively verified 1:1 release.

## Historical evidence and unverified behavior inventory

## Cloud blocks — PR #2, code and CI only

- Place all ten cloud item variants; confirm each places the matching cloud texture/type and remains correct after save/reload.
- Without planarClouds, confirm wand use does not remove the cloud or consume vis. With the research and at least 100 AIR vis, confirm one right-click removes the cloud, consumes exactly 100 AIR vis and drops the matching invulnerable cloud item.
- Confirm each picked-up variant can be placed again without changing its type; test multiplayer pickup and ensure no duplicate item.
- Test precipitation and entity effects in Overworld rain, including warm/cold biomes, fire/acid clouds, growth clouds and rain pause/resume. Confirm the synchronized weather columns appear/disappear with rain; save/reload while raining.
- Test periodic lightning from thunderclouds and Animus clouds; confirm Animus clouds only convert eligible blocks and spawn temporary berserk TH golems.
- Test alloy nugget and meat drops, primal aspect orbs, XP orbs, cow-to-mooshroom conversion and soil-specific growth results.
- Test acidic-cloud Thaumcraft dissolve damage and verify the full entity column is processed; test fire, enderman, blaze and firebat effects.
- In a live client, verify long-column rain/snow/ember rendering, variant tints, UV scrolling, warm/cold biome texture selection, rain start/stop synchronization, column-depth updates and chunk unload/reload persistence.


## Spike blocks — source and CI coverage only

- Place metal, wood and tooth spikes on all six sturdy support faces; confirm they orient away from the support and survive save/reload.
- Remove each support block; confirm the spike drops exactly one matching block item and disappears.
- Walk through/stand against each spike orientation; confirm the collision bounds match the old 1/16 inset and entity contact applies 1 cactus damage per collision tick.
- Check the OBJ block render and all three material textures from every direction. Confirm the metal, wood and tooth BlockItems render the same 3D mesh with the matching texture in GUI/inventory, on the ground, in first-person and third-person hands; check scale, orientation, clipping, UVs and missing-model/render logs. This branch contains the special-model renderer; this checklist is still unchecked.

## Injector projectiles — source and CI coverage only

- Load a seven-slot injector with ordinary syringe ammunition and phial ammunition. Confirm Shift-right-click opens the loader and that the first non-empty slot cycles into the firing position.
- Hold the injector for short, medium and full charges. Confirm the legacy draw curve/threshold, one durability point per successful shot, and server-side ammunition rotation without duplicate client entities.
- Fire an ordinary syringe at a living target. Confirm its stored PotionContents effects are applied only after a successful hit; hit a block and recover the embedded syringe, then confirm its potion data is intact. Check pickup restrictions for non-player shooters and despawn behavior.
- Enchant the injector with Power, Punch and Flame. Confirm Power increases dart damage, Punch applies knockback, and Flame ignites a hit target for five seconds. Confirm a full charge produces the critical-arrow effect.
- Fire phial ammunition from the injector and use a phial directly. Confirm each creates one splash projectile with the same effect list and splash behavior as the source syringe item, and that survival consumption/creative preservation matches the original.
- Test both projectiles with two clients and through save/reload while embedded/in flight. Check that projectile state and potion payload remain synchronized.
- Inspect the custom 3D syringe projectile in flight from all angles; confirm its four OBJ parts, yaw/pitch alignment, translucent potion-coloured body, white needle/plungers, scale and visibility at range. Confirm Blast Phial still uses its intended vanilla splash-potion model and check both projectile renderers for missing-model/log errors.
- Validate the custom 3D Syringe item renderer for empty, blood, blood-sample and injection variants in inventory/ground/first-person/third-person contexts. The item JSONs now use the shared four-part OBJ renderer; check the white empty body (50% alpha), red blood/sample body, PotionContents tint and 75% alpha for filled/injection variants, scale, hand transforms, clipping and missing-renderer logs.
- Validate the ModelBiped-derived 3D Corpse Effigy item renderer in inventory/ground/first-person/third-person contexts. The item now uses the Effigy OBJ mesh and texture; check its lying orientation, scale, hand transforms, UVs, clipping and missing-renderer logs. The Vat's normal/revived previews are separate render paths.

## Vat auxiliary previews — source implementation, client acceptance pending

- In Vat mode 3, verify the normal Corpse Effigy preview uses the original texture and a correctly aligned ModelBiped silhouette. In mode 4 and mode 2 with clone recipe type 1, verify the revived texture/model is selected.
- Put each supported sample item in the Vat sample slot and verify the item entity floats at the legacy position with the same gentle bob; confirm item changes update without stale previews and removing the sample clears the render.
- Verify the contained-creature preview still takes precedence over the effigy/sample previews and does not create a second world entity. In mode 1, confirm the legacy alpha curve: progress 800 starts fully transparent and progress 0 reaches fully opaque, with no flicker or alpha leakage to other entities.
- Verify all preview paths after save/reload, chunk unload/reload and on a second client; inspect model UVs, scale, pose and missing-resource logs.

## Undead horses and NightmareTH — source and CI coverage only

- Spawn `thaumichorizons:horse_zombie`, `thaumichorizons:horse_skeleton` and `thaumichorizons:nightmare`; confirm each entity type, display name, collision size, and renderer load without model/texture errors.
- Use all three spawn eggs on valid ground in survival and creative. Confirm each egg creates only its matching entity and consumes only in survival.
- Confirm HorseZombie is treated as undead and has the vanilla zombie-horse base attributes plus exactly +20 max health. Confirm HorseSkeleton is undead and has exactly +0.1 movement speed. Save/reload and verify modifiers do not stack.
- Mount and saddle all three mounts; test adult/foal state, save/reload, rider dismount and multiplayer tracking. Check Zombie/Skeleton equipment layers and Nightmare's four armor textures plus saddle layer.
- Endersteed equipment layers: equip a saddle and iron/gold/diamond horse armor and confirm both render as separate `SimpleEquipmentLayer` overlays on adult and foal, oriented and aligned with the body, without z-fighting against the baked armor texture switch. Remove each piece and confirm the overlay disappears; save/reload and check two-client tracking.
- Complete `nightmareInfusion` in the Vat. Confirm the output is `thaumichorizons:nightmare`, not `thaumichorizons:endersteed`, and its Thaumometer aspects match Beast 3, Eldritch 4, Fire 4 and Travel 4.
- With a rider, test low/medium Nightmare jump charges and confirm short-range Endersteed blink. In the Overworld and Nether, use a full jump charge and confirm travel to the other dimension, rider remains mounted, orientation is preserved, and arrival is collision-free with no liquid overlap. Confirm other dimensions never invoke cross-dimensional travel.
- Confirm Nightmare is fire immune, can rise through lava without accumulating fall damage, turns grass underfoot into dirt, burns legacy plant/web/leaves blocks at body/head level, and is not slowed by cobwebs. Confirm the source explosion appears only on successful dimension travel.
- Test repeated dimension trips, death/respawn, server restart, dismount before full jump, and destination with no safe landing column. Check for duplicate Nightmare entities, detached riders, or lost passengers.

## Greatwood and Thaumium Boats — source and CI coverage only

- Craft each boat through its Thaumcraft arcane-research page; verify Greatwood costs 5 ORDER / 5 EARTH / 5 WATER and uses five Greatwood Logs, and Thaumium costs 10 FIRE / 10 WATER / 10 ORDER / 10 EARTH with four Water Shards, four Thaumium Ingots and one Greatwood Boat.
- Place both boats on water using the item; confirm survival consumption / creative preservation, passenger mounting, steering, dismounting, pickup and correct item drops.
- Confirm Greatwood Boat does not work as a lava boat. Confirm Thaumium Boat floats and can be steered in source and flowing lava, does not burn, and remains present after save/reload.
- Ride Thaumium Boat through lava with a player and a living mob; confirm passengers are extinguished and receive/retain Fire Resistance while aboard in lava, and that the effect expires normally after leaving.
- Test dispensers with each boat above water; test Thaumium Boat over lava and Greatwood Boat over lava (the latter should not place). Check blocked placement and stack consumption.
- Test boat collisions, damage/pickup, passenger synchronization with two clients, dedicated-server behavior, and custom model/texture loading from multiple angles. Compare movement speed with the original; legacy 1.7.10 speed multipliers are not assumed to match modern paddle physics.

## Gate 1 archive — machines + creatures

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
- Soul Extractor Brain Jar feeding: place a TC4R Brain Jar directly above a supplied Soul Extractor; confirm the sieve animation/extracting state runs, AIR vis is drained, jar xp rises at roughly the legacy 1% chance per vis bit, stops at 2000, syncs to a second client and the comparator, and that a full jar is skipped while a Soul Receiver above still accepts soul bits.
- [x] SyntheticNode: infusion craft (`etherealShard`) → place → fills from vis net; wand tap extracts vis *(recipe 1:1 with reference — infusion output is the node block, wand tap extracts rather than creates; placed BE fills from the vis net (aspects ignis 0→20) — reproduced; wand-tap extraction reproduced: right-click series drained ignis 20→17 and the wand gained `thaumcraft:wand_vis {ignis: 300}` (3 taps × 1 vis); `getId()` = SYNTHETIC at code level; the infusion run itself not separately exercised)*
- SyntheticNode Wisp Essence: right-click the node with a Wisp Essence stack; confirm the stack is consumed and the first stored aspect's maximum capacity increases by 4 per essence. Break the node and confirm it drops one Wisp Essence per 4 units of maximum capacity, preserving the legacy item aspect payload (2 aspect per returned essence). Save/reload between insertion and break.
- Liquefaction Focus `Nuggets` upgrade: mine a smeltable ore with 1, 2 and 3 upgrade levels; confirm each level independently adds a 45% chance to spawn one matching TC4R bonus item (iron/gold/copper/tin/silver/lead nuggets or cinnabar quicksilver drop), and that blocks transformed by the focus's meltable rules do not produce the bonus. Confirm the bonus is server-side and survives multiplayer pickup without duplicating.
- Alchemite crystallized essentia: detonate near blocks with and without object aspect tags. Confirm tagged blocks yield one invulnerable `thaumcraft:crystallized_essence` stack per aspect with the expected amount and embedded aspect, without normal block drops; untagged blocks retain ordinary drops. For aura nodes, repeat enough times to observe the 25% essence/removal, 25% FADING and 50% UNSTABLE branches; confirm node essence matches its current aspects. Check multiplayer pickup and ensure no duplicate drops. The legacy `BlockAiry.explodify` and its exact sparkle are approximated by modern node removal and blocksplosion FX, so inspect the visual and node cleanup behavior too.
- Focus Disintegration: hold the focus on an aspect-tagged block until it is removed and confirm it yields the appropriate aspect-specific crystallized essence instead of the block; target an aspect-tagged dropped item stack and confirm its count is converted into the corresponding essence amounts and the original item entity is removed. Confirm the essence item is not consumed by subsequent focus targeting and that multiplayer has no duplicated conversion.
- Transduction Amplifier overload: use an energized node with the TC4R Node Transducer above it and TH amplifiers on the four horizontal sides. Verify redstone already applied before placement activates an amplifier on its first tick. With two active amplifiers, stand living targets within 10 blocks and confirm a node-bolt visual plus 4 magic damage every 50 ticks. With three, confirm the 1-in-4 overload pulse spills full-strength Flux Gas when the random Y offset is positive and Flux Goo otherwise, at a random offset in the surrounding 16×16×16 area. With all four active, confirm the energized node becomes a Vortex exactly once, its aura aspects are preserved, and the overlying TC4R Node Transducer is stopped (`status = -1`) rather than later reverting or disturbing the Vortex. Save/reload and repeat; watch for duplicated blocks or wiped Vortex aspects.
- [x] Vortex: absorbs blocks in 5×5×5, hungry-node pull works, stabilizer unhungrifies *(5×5×5 absorption reproduced (surrounding layer+floor removed, centre kept); hungry pull reproduced (armor stands dragged in and consumed). Stabilizer unhungrify reproduced after raycast fix: 6 powered stabilizers lock a cheat vortex (beams=6, all hasTarget=1, target=vortex); breaking one drops beams to 5 (reHungrify); re-placing + re-powering restores beams=6. Root cause was COLLIDER raycast passing through the collision-less vortex; now OUTLINE — see fidelity.md)*
- Vortex dropped items: throw `thaumcraft:void_metal_ingot` onto a normal Vortex and retrieve Void Putty with the wand; throw one and multiple `thaumcraft:salis_mundus` and confirm each yields 1–4 Wisps with a random Vortex aspect when aspects exist; throw Golem Powder with a player as thrower and confirm one owned temporary TH golem spawns per powder. Confirm ownerless Golem Powder is not consumed.
- Vortex inside the Pocket Plane: place a non-cheat vortex and verify it does not consume the surrounding 5×5×5 blocks, does not pull/damage entities as a hungry node, and does not start collapsing from its 2400-tick counter.
- Vortex ambient sound: hear one looping sound per nearby vortex; confirm it follows the block position, fades with distance, stops on block removal/collapse/chunk unload, and does not duplicate after dimension changes or resource reload.
- Familiar hat: spawn `thaumichorizons:familiar`; confirm the hat is aligned to the head, follows head pitch/yaw while moving/sitting, and does not z-fight or clip through the ears.
- [x] Creatures: `/summon thaumichorizons:chromaticsheep` etc. spawn, behave (color cycle, shear, eat stone) *(22 summons spawn/render without crash after the goal-constructor CCE fix; chromatic color cycle reproduced (Color 10→13); self-shear reproduced (SelfShearingSheep Sheared=1 + wool drop); manual shear with shears reproduced; eat-stone reproduced (OrePig consumed a cobblestone drop, percent 0→45 and excreted a thaumium nugget); eat-taint reproduced (TaintEaterPig removed a placed crusted_taint → air). Entity skins re-verified visually after the 64×32→64×64 UV pad (ore boar / taint feeder / chocolate cow) and the eyes-overlay tint/outline fix: both pigs render solid with correct eyes, no slabs or white border. Horse skins remapped per-face from legacy 128×128 to the modern 64×64 adult + baby layouts (`tools/remap_horse_skins.py`, 16 sheets, alpha-audited 0 holes): plain adult and foal render solid with the legacy purple-eye/green-mouth markers, and iron horse armour visibly swaps the body sheet on both adult and foal)*
- [x] `/thaumichorizons chameleon true` → see Invisibility checklist below *(single-client A/B verified: true hides body/armor/held item, false restores; two-client checklist still open)*

## Gate 2 archive — souls, Vat and GUI

- Soul jar: wand-right-click an infused critter → jars it (name label), item survives inventory/drop/save/reload, placement preserves NBT, wand release restores the original entity. Capture/data transfer/release are implemented; release now reconstructs the entity before consuming the jar, so invalid serialized entity data must leave the jar intact. The full cycle has not yet been reproduced in-game.
- Soul Jar preview: place a jar holding an infused critter and confirm the creature is visible inside at 0.25 scale, follows its saved appearance after save/reload, and disappears when the jar is emptied or removed. Place a soul jar and confirm the animated `textures/misc/soul.png` quad renders at the jar centre. Check both sides in a live client, including chunk reload and a second player.
- Containment focus captures LostSoul. Also carry a filled/essentia-loaded TC4R Warded Jar before an empty Warded Jar: only the empty, component-matching jar may authorize capture and be consumed. Fill all main inventory slots with exactly one empty jar present and confirm its slot is reused for the filled jar. Verify both LostSoul and ordinary mob capture, and release a malformed jar / simulate a rejected entity spawn to confirm the jar remains intact.
- Familiar: confirm occasional Thaumcraft sparkles appear around it with normal particle settings and do not appear when particles are disabled/minimal.
- [x] Vat multiblock forms *(reproduced in-world 2026-11-16: blueprint converts to VAT blocks and vis is consumed server-side after trigger-registration fix)*
- Curative Vat mode cures with containment foci
- Creature infusion: jarred critter + components in vat → infusion runs → TH creature spawns
- Vat infusion matrix renderer: during active infusion, confirm eight cubes rotate around the center, wobble according to instability and show a pulsing emissive overlay. While `Vat.mode == 2`, confirm the additive halo appears, grows with `craftCount`, fades toward transparent ray tips and disappears outside infusion mode. Check texture orientation, brightness, halo bounds and visual parity in a live client.
- Vat structure renderer: confirm the single upper-centre master BE draws the complete 3×3 base/water/glass/top/hatch model from `textures/models/vat.png`; inspect every side and top, correct vertical alignment, UV mapping, transparency, light/shading, render bounds and chunk-edge visibility. Confirm the internal VAT/VAT_SOLID/VAT_INTERIOR blocks do not draw duplicate cubes.
- Vat contained-creature preview: capture/store a non-player critter, reload the Vat and confirm its saved NBT produces a visible preview inside the glass at the legacy offset. Confirm it is not added to the client world, does not duplicate the server entity, and disappears when the subject is removed. The mode-1 alpha curve, Corpse Effigy/revived-effigy and floating sample previews now have source render paths and need in-game visual checks.
- Vat blockstate selectors: confirm `vat` variants 10 and 11 and `vatsolid` variants 4–7 load without missing-state/model errors; the assembled shell is drawn by the master block-entity renderer rather than these block models.
- Corpse effigy + self-clone flow works
- Blood Infuser: select aspects and mode; run the syringe draw/inject cycle; save/reload with partially filled essentia and with empty/full output slots. Confirm empty slots remain usable after reload, output injections are not lost/duplicated, selection/mode controls update on the client, and an output slot frees correctly when extracted.
- All 9 registered menus (Case, Vat, Soul Sieve, Inspiratron, Soulforge, Blood Infuser, Injector, Fingers, Vis Dynamo) open; slots behave and VisDynamo aspect toggles persist
- Inspiratron: soul → research completion fires

## Additional content checks added after the original Gate 2 pass

- Incubated Egg: obtain via `alchIncubation`, throw it, confirm projectile renders and hatches a baby chicken on impact.
- Recombinator: place it directly above a node, power it with redstone and confirm activation after placement/reload. After the 50-tick warm-up, confirm it samples nearby unlocked aura nodes; verify the four combination priorities in order (primal+primal, source-primal, target-primal, any+any), random valid result selection, one source aspect consumed, one target aspect consumed, and a 1-in-3 chance to reduce the target's base amount. Confirm the 32-block node-zap effect and persistence of active/count state. Static block geometry and a dedicated moving-pearl/active-ring/underlying-node beam renderer are present; in-game visual parity remains to be checked.
- Recombinator renderer: with redstone power and a TC4R aura node directly below, confirm the pearl bobs vertically, the animated lightning ring appears above it, and a beam joins the machine to the node. Remove power and confirm the ring/beam stop; verify the pearl returns smoothly, and save/reload while powered. Compare texture orientation, ring animation speed, vertical offsets and alpha with the legacy renderer.
- Planar Conduit: craft using `planarTheory`; verify the Transduction Amplifier arcane recipe and Vortex Stabilizer infusion recipe against the reference ingredients/aspect costs.
- Confirm the thrown Incubated Egg renders in-game. `ThrownItemRenderer` is now registered for `ThaumicHorizons:eggincubated`; no missing-renderer log or invisible projectile in a live client.
- Mirror Amulet: craft it, link it to an ordinary TC4R mirror, die with inventory and bauble items, and confirm the items are transported through the mirror and the amulet is consumed.
- Void Putty repair: combine a damaged repairable item with Void Putty and confirm the repaired item is returned with damage reset.
- Focus Illumination dyeing: combine an Illumination Focus with one or more dyes and confirm the focus color changes to the legacy dye color.
- Focus Illumination placement: place normal and Solar lights using several focus colors; confirm the invisible block state preserves indices 0–15 after save/reload and its ambient sparkle uses the selected palette color. Confirm Solar light still ignites undead for five seconds.
- Arcane Lens radial HUD: wear Goggles of Revealing and keep one or more lenses in the inventory or a Lens Case; hold V, choose a lens by pointing at its icon, and release V to equip it. Confirm the current-lens center icon/tooltip, mouse capture restoration, and Shift+V removal. This implementation is not yet manually tested in a live client.
- Lens `handleRender` effects: with Goggles of Revealing worn, equip the Fire lens and confirm night vision applies on land and is removed while submerged; equip the Water lens and confirm night vision applies only while submerged and is removed on leaving water. Confirm the effect duration refresh is not perceptibly flickering, and that removing goggles or the lens clears a lens-granted night vision effect. Not yet manually tested in a live client.

## Invisibility — targeted regression scenarios

Use these when changing infusions; the prerelease gate covers only the representative two-client scenario above.

- `/thaumichorizons chameleon true` (or X key with infusion granted): player fully invisible **including armor and held item** on BOTH clients
- A player who logs in later sees the state correctly (no ghost/gap)
- State survives: relog, death/respawn, `/reload`
- `chameleon false`: player visible again, no lingering effects
- Chameleon without the infusion slot filled does nothing
- X/C keys toggle only when the infusion is present (server-verified)

## Stable release criteria

A stable 26.3 release requires the prerelease smoke gate on a clean checkout, no known crash/world-loss/duplication defects in the covered core loops, and a dedicated-server/two-client pass. `docs/fidelity.md` must describe the intentional deviations and remaining unverified areas accurately. Broad parity testing remains risk-based rather than an exhaustive mandatory matrix.

Backport branches have independent release gates; they do not block a 26.3 prerelease.


## Planar Keystone and Slot — source and CI coverage only

- Complete `pocketPlane` research and craft a blank Keystone. Verify central Planar Conduit, four Quartz, four Balanced Shards, and TRAVEL 32 / VOID 32 / EXCHANGE 16.
- Complete `planarKeystone` research and craft the alternate Keystone. Verify central Diamond, two Quicksilver, two Balanced Shards, and TRAVEL 24 / VOID 24 / ELDRITCH 12.
- Craft the Slot receptacle from its `planarKeystoneSlot` infusion page. Verify central Planar Conduit, four Gold Ingots, four Void Metal Ingots, and VOID 32 / TRAVEL 24 / MECHANISM 24 / ELDRITCH 24.
- Confirm Keystone and Planar Conduit appear in the creative tab; place the Slot item and inspect the static receptacle model and collision/interaction target. Insert a Keystone and verify the central 8×8×8 cube appears tinted to the saved plane color on both clients; remove it and verify the cube disappears, then save/reload and confirm the tint persists.
- Right-click a valid generated Pocket Plane vortex with a blank Keystone. Confirm it binds to the correct plane, persists through inventory/world save and displays the bound tooltip; confirm invalid vortices and non-Pocket-Plane dimensions do not bind it.
- Insert a bound Keystone into an empty Slot; remove it with an empty hand; confirm the same plane ID is preserved. Test inventory-full drops, invalid plane IDs and multiplayer synchronization.
- Build both gateway orientations around the Slot using the original frame: four `thaumcraft:advanced_alchemical_construct` corners and eleven `thaumcraft:ancient_stone` border blocks. Confirm substituted/arbitrary blocks are rejected; activate with a wand, verify aspect cost, portal opening/closing, the four-portal limit, and frame restoration after teardown/reload. After closure, confirm the 3×3 opening is still air and the portal can be opened again without rebuilding the frame.
- Open the `pocketPlane` research page and verify the custom 5×5 PlanarGateway diagram appears on its own page. Confirm the four advanced-alchemical corner blocks, eleven ancient-stone edge blocks, Slot at the top centre and 3×3 portal aperture match `SlotBlockEntity` in both layout and item icons. Confirm localization, book-page alignment and no missing resources; then validate the dynamic inlaid Keystone renderer for alignment, receptacle-atlas UVs, aspect tint and disappearance after extraction.

## Pocket Plane verification (source commit `e38d16a`, data fix `7eb9b8c`)

The Pocket Plane classes and data JSONs are tracked, and the sources compile against MC 26.3 / NeoForge 26.3. Build, seam, dedicated-server startup, and dimension load pass; the gameplay checks below remain open. The source Vortex now consumes a primordial_pearl item after stabilization, creates the pocket plane, and records the source dimension + return coordinates. The generated vortex and source vortex both use the same saved plane ID for bidirectional player travel. When the source vortex collapses, its generated pocket vortex is removed after the original 25-tick delay. CI validates compilation and JAR contents only; no live-world travel has been marked tested. Portal source endpoints persist a dimension identifier, coordinates and frame orientation. Portal blocks implement entry into the Pocket Plane and return to the recorded source world; Keystone binding/insertion/extraction and wand-triggered Slot opening are wired. The Slot's occupied Keystone cube renderer and color synchronization are implemented, but need client validation. The gateway geometry check validates the 3×3 opening and 15 frame blocks. These are code/CI facts only; no live-world portal travel has been claimed as tested.

Static source audit (2026-11-18): the 1.7.10 `ChunkProviderPocketPlane` also returns empty chunks, because the plane surface/volume is generated by a separate one-time pass; the empty modern `PocketPlaneChunkGenerator` is intentional. The reference `drawRavines`, `drawClouds`, `drawSurfaceFeatures`, `drawUndergroundFeatures`, and `drawLeviathanBones` functions are empty stubs too. SavedData now reconciles `MaxID` against the dense `Data` list and ignores malformed/orphan return-position entries ([commit `73d329a`](https://github.com/MxxFoxx2/ThaumicHorizonsReforged/commit/73d329a3adf79f4e9db0f780b8ea49370ff6f3b3)). This is static inspection, not proof of generation, world restart or cross-save behavior.

- [x] `gradlew.bat build --no-configuration-cache` passes after adapting `PocketPlaneChunkGenerator` to the 26.3 ChunkGenerator API.
- [x] `python tools/check_seams.py` passes with the `thaumcraft.platform.Teleport` seam.
- [x] `thaumichorizons:planarconduit` is registered exactly once in `PocketRegistry`; the dedicated server starts with the mod loaded.
- [x] Pocket dimension loads on a dedicated server; dimension, dimension type and biome JSONs resolve.
- PocketPlaneGenerator produces the intended layers/caves/pockets; confirm the central 5×5 spawn platform uses `thaumcraft:arcane_stone`, the void rings use `ancient_stone`, the small decorative columns use `obsidian_totem` over `obsidian_tile`, the CRAFT feature uses `arcane_pedestal`, the flora/taint patches use the correct legacy TC4R block variants, and cold-biome snow surfaces have the original two-layer thickness; test several seeds and verify deterministic chunk generation.
- Keystone item creates/binds the intended pocket plane and preserves its plane id through inventory, drop/pickup and save/reload.
- Slot gateway frame detection works in both orientations; only the 3×3 opening is empty, all 15 perimeter blocks are present, and wand activation consumes the expected vis from the wand in hand.
- Keystone bind at a Pocket Plane vortex, insert into a Slot, extract with an empty hand, inventory-full drop, and save/reload all preserve the exact plane ID, display the saved Pocket Plane name, and tint the item with the plane's aspect-mixed color on a client with no local SavedData cache.
- Portal teardown restores the frame and clears only the matching saved portal entry even with Pocket Plane unavailable; destroying the Slot or removing its Keystone does not leave a stale entrance, and the portal index can be reused after the dimension is re-enabled. Confirm null, three-int/incomplete, invalid-dimension, invalid-orientation and four-int zero-coordinate portal records are treated as free after save/reload; opening a replacement portal must overwrite the stale endpoint and allow a working return trip. Breaking a Slot with an inserted Keystone must drop the same plane-bound item, close the source and Pocket Plane portal ends, and release the portal index for reuse.
- While a portal is open, break one of its Gateway frame blocks. Confirm teardown runs once without recursion/crash, restores the complete frame while leaving the 3×3 aperture empty, and clears only the matching Pocket Plane portal and saved endpoint.
- Create four portals linked to one Pocket Plane and verify each surface/metadata mapping (A/C/B/D) enters and returns through the correct side.
- Drop a TC4R Primordial Pearl (thaumcraft:primordial_pearl; legacy Eldritch Object metadata 3) into a source Vortex with six stabilizing beams. Confirm exactly one Pocket Plane is generated, a renamed pearl supplies the plane name, an ordinary pearl names it after the thrower using the localized suffix, the pearl is consumed only on success, the source vortex records the plane ID, and the pocket vortex is created at (0, 129, 256 × plane ID).
- Stand on the source Vortex and confirm transfer to its Pocket Plane after the vortex is created; confirm the Pocket Plane vortex returns to the recorded source dimension and original vortex coordinates.
- Save/restart the world, repeat both directions, and confirm the saved source dimension and return coordinates are restored.
- Travel from Overworld and a non-Overworld source dimension into the Pocket Plane and back reaches the correct source dimension/position and exit orientation, preserves player state, and safely handles missing/unloaded destinations.
- PocketPlanes SavedData survives server restart and world save/reload. After joining the reloaded save, use a portal whose source-vortex chunk is not loaded and verify its endpoint and plane ID resolve correctly. In the same client process, switch from a save containing several Pocket Planes to a fresh save and confirm no prior plane IDs, coordinates or portal links leak into it. Also test a deliberately inconsistent `MaxID`, malformed/orphan `Positions` entries, and saved radii below 32 or above 127: load must recover the next dense plane ID, clamp the radius to 32..127 without a crash or silently mapping a malformed return point to `(0,0,0)`, and the source Vortex plus generated Pocket Vortex must both reference that recovered ID.
- Remove or disable the source vortex's stabilizer beams until it collapses; after the 25-tick collapse delay, confirm only the matching pocket-plane vortex at (0, 129, 256 × plane ID) is removed, and confirm the other plane data and portals remain intact.
- Portal, Gateway and Slot blocks have complete blockstates, models, textures and no missing-renderer/model log.

## Second original-parity pass — 2026-11-20

> Verification boundary: the build, seam, asset, client-startup and dedicated-server checkmarks below describe the run recorded for the original-parity pass before the subsequent review fixes (portal synchronization/teardown and Earth Lens world scoping). Those later source changes have not yet been rebuilt or exercised in-game; rerun the checks before treating this revision as verified.

- [x] `gradlew.bat build` passes, including the existing JUnit suite.
- [x] `python -I tools/check_seams.py` passes for 307 files; the previous five client/common violations are resolved.
- [x] `python -I tools/check_assets.py` passes for 525 references; all 126 registered item IDs have item definitions.
- [x] The live client starts and completes a resource reload with no TH missing-texture, missing-model or mixin errors. Recombinator UVs were normalized after a real `FaceBakery` failure and the following reload is clean. Remaining model warnings are for TC4R blocks, not TH.
- [x] The dedicated server reaches `Done` and loads `thaumichorizons:pocket_plane`.
- Exercise Air, Earth, Scholar and Water lenses in a live world: entity markers, 3×3 x-ray including mining, auto-scan packet acceptance/rejection and underwater overlay/night vision. Check lens removal, two-client state, and a world/dimension transition while Earth Lens replacements are active.
- Compare all restored machine, Synthetic Node, Soul Sieve, Inspiratron, Soulforge, Blood Infuser, Soul Beacon, Vortex, Portal and stabilizer visuals against 1.7.10; check UVs, geometry, animation timing and render bounds.
- Trigger a lethal hit after binding a Soul Beacon above a mode-4 Vat: death is cancelled, inventory/baubles drop with flux, the player returns to the saved dimension, self-infusions return and the Vat resets. Repeat after restart and cross-dimension travel.
- Confirm Silver Heart cancels `thaumcraft:taint` damage without suppressing other damage types.
- Verify a disposable wand cannot recharge through aura taps, relays, pedestals, orbs, rods or direct calls, while its initial vis can still be spent and saved/reloaded.
- Animate/remove/re-place a directional block with a TH golem and confirm facing and all other state properties survive entity save/load and golem itemization.
- Craft chocolate ice cream and apply `AdvancedGolemTH` to a TH golem placer; verify the extra upgrade slot and persistence.
- Use all 23 spawn eggs and every creature/self-infusion and node debug item once; verify models, colors, names and no duplicate entity/infusion application.
- Let a source portal reach 250 ticks: both endpoints close, the frame is restored, the portal index is reusable, the saved endpoint is cleared and no reentrant teardown occurs. Repeat for all four directions and after restart.

## Golem material refactor — 2026-11-20

- Golem materials are owned by TH again: `entity/GolemMaterialTH` (16 materials, exact `EnumGolemTHType` stats, TH WOOD = 2 upgrade slots); TC4R `GolemType` is back to the 8 stock Thaumcraft materials (addon-material values and their placer items removed — TC4R's missing-item-model warnings for them are gone too). TC4R exposes `GolemBase.golemStats()`/`getGolemTexture()` as overridable extension points (`e490df0`).
- Persistence: synced string `TH_MATERIAL`, saved `GolemMaterialTH` string id, item tag `GolemMaterial`; old saves restore via the legacy `GolemType` ordinal mapping (8..21 → TH materials, 1/3/5/6 → overlapping stock materials) or from the saved block; safe fallback — VOID.
- JUnit: `GolemMaterialTHTest` (stats table for all 16, ids, legacy mapping incl. unknown → null, null-block → VOID, water behavior) — 65/65 TH tests green; TC4R 32/32; seams 295 ✓.
- Dedicated-server boot verified green (2026-11-22, dev `runServer` against the TC4R composite): mod loads (Thaumic Horizons: Reforged 0.1.0-beta.1), mixin subsystem binds in SERVER dist with client configs correctly skipped, `Done (5.081s)`, dimensions initialize (`thaumichorizons:pocket_plane`, `thaumcraft:outer_lands`), TC4R aspect tags register (1953 items / 173 entities), only a benign first-run `server.properties` error in the log. The client half of the Boot scenario still needs a live `runClient`.
- CI (`ci.yml`, pin `388aebb`): build, seams, JAR contents verified green. **Not yet game-verified (runClient/runServer required):** block→material for each of the 16 materials on real placement, core/upgrade/pane flows after the rework, bell and powder interactions, golem renderer texture (TH `textures/model/golem_<material>.png` now exist: generated by `tools/gen_golem_textures.py` — canonical block textures per material tiled over the TC4R template's UV mask, void reuses the designed 1.7.10 sheet; visual acceptance in a live client still pending), water pathfinding/fire immunity in world, old-save migration against a real save with ordinals 8..21.

## Third parity pass — confirmed-gap closure

- [x] `gradlew.bat build` passes after Baubles, exact potion components, Fortune and Vortex cheat restoration.
- [x] `python -I tools/check_seams.py` passes for 308 files.
- [x] `python -I tools/check_assets.py` passes for 526 references, including `vortexth_cheat`.
- Equip a Lens Case in each of the four bauble slots and verify radial discovery, selection and removal on two clients; compare with main-inventory behavior.
- Verify exact `PotionContents` matching for Regeneration II and Water Breathing in creature/self-infusion recipes, including a rejected default splash potion.
- Break Bone, Eyes and Flesh with Fortune I/II/III and without Fortune; verify the extra 0..level bonus while preserving the original 3–5 base range. *(Bone reproduced 2026-11-20: without Fortune, 15 bones from exactly 4 broken `boneth` blocks — each per-block drop inside the legacy 3–5 range; with Fortune III, 58 bones from exactly 8 blocks (column mined straight down; remaining positions probed via `/execute if block`) — above the no-Fortune ceiling of 40, so the 0..level bonus is live. Eyes: 28 spider eyes from 6 `eyeth` blocks with Fortune III is still inside the no-Fortune envelope (max 30), not conclusive; Flesh unmeasured; Fortune I/II not exercised.)*
- [x] Place `vortexth_cheat` and inspect `cheat` in the block entity; verify normal Vortex items do not set it, and confirm creative Vortex persists after restart. *(Reproduced 2026-11-20 in the live client: placing `vortexth_cheat` writes `cheat: 1b` into the block entity; placing the normal `thaumichorizons:vortexth` item writes `cheat: 0b`. After in-game save-and-quit + re-enter, the cheat vortex still reads `cheat: 1b` with `count` frozen at 50 — no eat, no collapse. Full client restart was not re-verified: the first cheat vortex was destroyed by an adjacent normal vortex, whose 5×5×5 eat + collapse (non-cheat path, incidentally re-confirmed) swallowed the neighbour — future tests must place the pair more than 5 blocks apart. Placement was keyboard-driven because this client's options.txt had attack/use rebound to U/J; mouse attack/use are restored since.)*
- Complete a Guardian Panther conversion in the Vat and confirm `resetStats` restores 24 max health and 0.5 movement speed after entity NBT is copied.
- Exercise orphan cleanup: remove/replace the source Slot while a portal is open, and repeat with part of the 5×5 source-frame footprint initially unloaded. Confirm cleanup does not overwrite unrelated blocks, waits for the complete footprint when necessary, restores the frame once loaded, closes the opposite endpoint and frees the portal index.

### Findings from the 2026-11-20 client session

- **Fixed: integrated-server crash on first damage.** Any damage event (observed via drowning) ran `InfusionEntityEvents.trySoulBeaconReturn`, whose first reference to `SoulBeaconKeys` class-initialized it *after* registry freeze: `DataKey$Builder.build → DataAttachments.register → DeferredRegister.register` threw `IllegalStateException: Cannot register new entries to DeferredRegister after RegisterEvent has been fired` and took the whole game down (crash report `crash-2026-11-20_18.24.38-server.txt`). Fix: `SoulBeaconKeys.initialize()` called from the mod constructor next to the other key classes (commit `d8c6b5b`); `build` incl. JUnit suite green, and two drowning deaths after the fix produced damage events with no crash.
- **Fixed: `ObjModel` `ConcurrentModificationException` during parallel model bake** (`ObjModel.java:32`, previously logged at every resource reload) failed baking of `thaumichorizons:corpseeffigy`, `spiketoothth` and `spikewoodth` item models → "Missing item model" warnings and invisible/purple items. Fixed by making the loader cache a `ConcurrentHashMap` (commit `3e79d85`); the 18:29 client reload after the fix bakes all three models with no TH bake failures and no CME — the corpse-effigy and spike item-renderer checklist items (Injector projectiles section) are unblocked for their in-game visual checks.
- Not a mod bug: the "dead mouse buttons" impression was caused by `options.txt` bindings `key_key.attack=key.keyboard.u`, `key_key.use=key.keyboard.j` (mouse buttons unbound from attack/use). Restored to mouse buttons.
