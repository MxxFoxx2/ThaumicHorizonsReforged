# Verification

Manual gates. Headless builds and the seam checker do not replace launching the client.

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
- [ ] Transduction Amplifier overload: use an energized node with the TC4R Node Transducer above it and TH amplifiers on the four horizontal sides. Verify redstone already applied before placement activates an amplifier on its first tick. With two active amplifiers, stand living targets within 10 blocks and confirm a node-bolt visual plus 4 magic damage every 50 ticks. With three, confirm the 1-in-4 overload pulse spills full-strength Flux Gas when the random Y offset is positive and Flux Goo otherwise, at a random offset in the surrounding 16×16×16 area. With all four active, confirm the energized node becomes a Vortex exactly once, its aura aspects are preserved, and the overlying TC4R Node Transducer is stopped (`status = -1`) rather than later reverting or disturbing the Vortex. Save/reload and repeat; watch for duplicated blocks or wiped Vortex aspects.
- [x] Vortex: absorbs blocks in 5×5×5, hungry-node pull works, stabilizer unhungrifies *(5×5×5 absorption reproduced (surrounding layer+floor removed, centre kept); hungry pull reproduced (armor stands dragged in and consumed). Stabilizer unhungrify reproduced after raycast fix: 6 powered stabilizers lock a cheat vortex (beams=6, all hasTarget=1, target=vortex); breaking one drops beams to 5 (reHungrify); re-placing + re-powering restores beams=6. Root cause was COLLIDER raycast passing through the collision-less vortex; now OUTLINE — see fidelity.md)*
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


## Pocket Plane verification (source commit `e38d16a`, data fix `7eb9b8c`)

The Pocket Plane classes and data JSONs are tracked, and the sources compile against MC 26.3 / NeoForge 26.3. Build, seam, dedicated-server startup, and dimension load pass; the gameplay checks below remain open. The source Vortex now consumes a primordial_pearl item after stabilization, creates the pocket plane, and records the source dimension + return coordinates. The generated vortex and source vortex both use the same saved plane ID for bidirectional player travel. When the source vortex collapses, its generated pocket vortex is removed after the original 25-tick delay. CI validates compilation and JAR contents only; no live-world travel has been marked tested. Portal source endpoints now persist a dimension identifier, coordinates and frame orientation. Portal blocks implement both entry into the Pocket Plane and return to the recorded source world; Keystone binding/insertion/extraction and wand-triggered slot opening are wired. The gateway geometry check now validates the 3×3 opening and 15 frame blocks. These are code/CI checks only; no live-world portal travel has been claimed as tested.

- [x] `gradlew.bat build --no-configuration-cache` passes after adapting `PocketPlaneChunkGenerator` to the 26.3 ChunkGenerator API.
- [x] `python tools/check_seams.py` passes with the `thaumcraft.platform.Teleport` seam.
- [x] `thaumichorizons:planarconduit` is registered exactly once in `PocketRegistry`; the dedicated server starts with the mod loaded.
- [x] Pocket dimension loads on a dedicated server; dimension, dimension type and biome JSONs resolve.
- [ ] PocketPlaneGenerator produces the intended layers/caves/pockets; test several seeds and verify deterministic chunk generation.
- [ ] Keystone item creates/binds the intended pocket plane and preserves its plane id through inventory, drop/pickup and save/reload.
- [ ] Slot gateway frame detection works in both orientations; only the 3×3 opening is empty, all 15 perimeter blocks are present, and wand activation consumes the expected vis from the wand in hand.
- [ ] Keystone bind at a Pocket Plane vortex, insert into a Slot, extract with an empty hand, inventory-full drop, and save/reload all preserve the exact plane ID.
- [ ] Portal teardown restores the frame and clears only the matching saved portal entry; destroying the Slot or removing its Keystone does not leave a stale entrance.
- [ ] Create four portals linked to one Pocket Plane and verify each surface/metadata mapping (A/C/B/D) enters and returns through the correct side.
- [ ] Drop a TC4R Primordial Pearl (thaumcraft:primordial_pearl; legacy Eldritch Object metadata 3) into a source Vortex with six stabilizing beams. Confirm exactly one Pocket Plane is generated, the pearl is consumed only on success, the source vortex records the plane ID, and the pocket vortex is created at (0, 129, 256 × plane ID).
- [ ] Stand on the source Vortex and confirm transfer to its Pocket Plane after the vortex is created; confirm the Pocket Plane vortex returns to the recorded source dimension and original vortex coordinates.
- [ ] Save/restart the world, repeat both directions, and confirm the saved source dimension and return coordinates are restored.
- [ ] Travel from Overworld and a non-Overworld source dimension into the Pocket Plane and back reaches the correct source dimension/position and exit orientation, preserves player state, and safely handles missing/unloaded destinations.
- [ ] PocketPlanes SavedData survives server restart and world save/reload.
- [ ] Remove or disable the source vortex's stabilizer beams until it collapses; after the 25-tick collapse delay, confirm only the matching pocket-plane vortex at (0, 129, 256 × plane ID) is removed, and confirm the other plane data and portals remain intact.
- [ ] Portal, Gateway and Slot blocks have complete blockstates, models, textures and no missing-renderer/model log.
