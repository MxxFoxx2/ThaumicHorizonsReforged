# Verification

Manual gates. Headless builds and the seam checker do not replace launching the client.

## Gate 1 — after machines + creatures (V-phase)

```sh
./gradlew build --no-configuration-cache
python -I tools/check_seams.py
./gradlew runClient
```

- [ ] Mod loads alongside TC4R, no mixin/attachment errors in log
- [ ] Creative tab shows TH items, blocks, machines
- [ ] Thaumometer scans TH items and creatures; aspects present
- [ ] Research browser: `ThaumicHorizons` category opens, entries visible, page text renders
- [ ] Machines: place VisDynamo near elements → vis net joins; EssentiaDynamo above jar → drains; NodeMonitor on node → redstone
- [ ] SyntheticNode: wand tap → node created with SYNTHETIC type
- [ ] Vortex: absorbs blocks in 5×5×5, hungry-node pull works, stabilizer unhungrifies
- [ ] Alchemite: ignite → fuse → explosion, chain reaction
- [ ] Creatures: `/summon thaumichorizons:chromaticsheep` etc. spawn, behave (color cycle, shear, eat stone)
- [ ] `/thaumichorizons chameleon true` → see Invisibility checklist below

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
