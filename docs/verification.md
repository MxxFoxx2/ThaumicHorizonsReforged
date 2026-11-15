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
- [ ] Thaumometer scans TH items and creatures; aspects present *(aspects data verified via `/thaumcraft aspects item` for inert/charged wand; held thaumometer scan and creature scans pending)*
- [x] Research browser: `ThaumicHorizons` category opens, entries visible, page text renders
- [x] Machines: place VisDynamo near elements → vis net joins; EssentiaDynamo above jar → drains; NodeMonitor on node → redstone *(EssentiaDynamo jar drain reproduced (64→40→0, ticks active); NodeMonitor redstone A/B reproduced (lamp LIT on empty node → UNLIT on filled node); vis-net join reproduced (SyntheticNode attached to dynamo source and filled). VisDynamo wand-click join verified at code level (addSource), not separately exercised)*
- [x] SyntheticNode: wand tap → node created with SYNTHETIC type *(placed SyntheticNode BE fills from the vis net (aspects ignis 0→20) — reproduced; SYNTHETIC id confirmed at code level; wand-tap extraction needs a hold-click and is still pending)*
- [ ] Vortex: absorbs blocks in 5×5×5, hungry-node pull works, stabilizer unhungrifies *(5×5×5 absorption reproduced (surrounding layer+floor removed, centre kept); hungry pull reproduced (armor stands dragged in and consumed). Stabilizer unhungrify NOT yet reproduced: powered stabilizers stay hasTarget=0 because the unstabilized vortex collapses (~3 s) before a raycast locks it; needs a faster lock or a code-level look)*
- [x] Alchemite: ignite → fuse → explosion, chain reaction *(reproduced: redstone-signal ignite → fuse → explosion (crater + debris) → chain primed and consumed all 3 blocks; flint ignite→fuse exercised earlier, client move-crash fixed)*
- [ ] Creatures: `/summon thaumichorizons:chromaticsheep` etc. spawn, behave (color cycle, shear, eat stone) *(22 summons spawn/render without crash after the goal-constructor CCE fix; chromatic color cycle reproduced (Color 10→13) and self-shear reproduced (SelfShearingSheep Sheared=1 + wool drop); manual shear and eat-stone/taint spot-checks pending)*
- [x] `/thaumichorizons chameleon true` → see Invisibility checklist below *(single-client A/B verified: true hides body/armor/held item, false restores; two-client checklist still open)*

## Gate 2 — after souls/Vat/GUI phase

- [ ] Soul jar: wand-right-click an infused critter → jars it (name label), release works
- [ ] Containment focus captures LostSoul
- [ ] Vat multiblock forms; curative mode cures with containment foci
- [ ] Creature infusion: jarred critter + components in vat → infusion runs → TH creature spawns
- [ ] Corpse effigy + self-clone flow works
- [ ] Blood infuser: syringe draw/inject cycle
- [ ] All 8 menus open, slots behave, VisDynamo aspect toggles persist
- [ ] Inspiratron: soul → research completion fires

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
