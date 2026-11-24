# TC4R integration

## Dependency and source of truth

The `26.1.2` branch uses a composite build of `../thaumcraft-4-reforged-26.1.2` through `settings.gradle`. The supported build target is TC4R `1.1.3`, tag `26.1.2`, commit `8b953e69c3d625469006f688d964d9f2a7911590`, for Minecraft `26.1.2` and NeoForge `26.1.2.122`. CI checks out that exact public revision; keep the sibling checkout on the same commit when building locally.

For old Thaumcraft item and block metadata, use `../thaumcraft-4-reforged-26.1.2/tools/data/id_map.json` rather than semantic guesses. For host API and mixin targets, use the pinned source rather than contracts copied from the Minecraft 26.3 line.

The build configuration and documentation have been retargeted. The inherited Java sources still require compilation and runtime validation; the observations below are source-inspection evidence, not proof that the add-on is compatible.

## Choosing the integration point

1. Search for an existing compat capability and its TC4R implementation in `compat/tc/TcReforgedPort`.
2. If the call is a plain public TC4R API (allowlisted in `tools/check_seams.py`), call TC4R directly from gameplay code.
3. Keep TC4R-specific lookups, effects, blocks, internal objects and mixins in `compat/tc/`.
4. A direct import in gameplay code is allowed only if the type is intentionally exposed in `THAUMCRAFT_API` in `tools/check_seams.py`. Before adding an exception, explain why it is public add-on API and why an adapter is unsuitable.
5. Optional host extensions must be capability-checked. Prefer a safe no-op or disabling only the dependent feature over linking directly against a fork-only method.

## Pinned-host observations

These points were checked against TC4R commit `8b953e6` while preparing the 26.1.2 build:

- `Wands.isWand(ItemStack)` recognizes only the built-in `ModItems.WAND`; TH's recognition mixin must still compile and match the pinned method.
- `GolemBase` exposes `getGolemStrength()`, `getCarryLimit()`, `configure(GolemType, GolemSnapshot)`, `aiStep()` and `fireImmune()`. The 26.3 golem-stat/texture integration cannot be assumed to match; validate overrides, renderer state and mixin descriptors against this revision.
- `ResearchPage.Image(Identifier, String)` exists. No `ResearchPage.creatureInfusion(...)` or `ResearchItem.requireInfusionResearch()` method was found, so TH must not link directly to either extension.
- `NodeTransducerBlockEntity.stopForVortexConversion()` was not found. Keep the guarded transducer conversion disabled unless a validated safe hook exists.
- `FxProxy.machineBeam(...)` was not found. The machine-beam adapter must remain optional and must not link directly to that method.
- `ScanManager` and `ScanKind` exist. The TH Scholar-lens payload independently revalidates goggles/lens, range and target on the server.
- Rendering helper types found in the pinned source include `FacingQuads`, `ModelBoxGeometry`, `TcRenderTypes`, `EntityQuadSink`, `VertexQuadSink`, `WorldRendering` and `WardedRendering`; their packages and method descriptors still need compile-time and mixin validation.

## Host API details worth preserving

- **Focus upgrade IDs:** TC4R's `FocusUpgradeType` stores upgrades in an ID-indexed static table. Registering a second upgrade at an occupied ID logs an error and ignores the new registration. Check the host's existing IDs before assigning any TH/custom ID; otherwise an upgrade can silently be unavailable despite the log entry.
- **Focus upgrade localization:** recheck the exact localization-key convention in the pinned host before changing TH translations.
- **Mixin descriptors:** a class name match is insufficient. Revalidate target method descriptors, injection points, side, renderer state and lifecycle whenever the host revision changes.

## Mixins and hooks

Until the host provides a required native hook, TH may implement it through a mixin in `compat/tc/` and the existing compatibility implementation.

- Pin and verify the supported TC4R version range.
- Validate target class, method descriptor, injection point, side and lifecycle against the pinned 26.1.2 source.
- Keep mixin configuration and access transformers synchronized with the implementation.
- Avoid changing host behavior when TH is absent.
- When TC4R gains the equivalent native hook, replace the mixin with the native API while keeping TH gameplay interfaces stable where possible.

## Working with the host repository

- Prefer additive-only API changes; preserve behavior when TH is absent.
- Do not modify `notlikethiss/thaumcraft-4-reforged` or the local sibling checkout without explicit user direction.
- Build against tag `26.1.2` in the local composite checkout and confirm CI pins the same revision.
- A source backport is separate from build-target configuration. Do not present compile fixes as complete until the build actually passes.

## Validation

Run `python3 -I tools/check_seams.py`, `python3 -I tools/check_assets.py` and `gradlew.bat build` from the TH repository root. Add JUnit coverage for deterministic adapters where practical. Exercise the actual client/server path when registries, effects, menus, rendering or networking are involved.

When reporting the change, include the TC4R revision, public API allowlist changes, mixin/API changes, fallback behavior and unverified runtime scenarios. Historical results from Minecraft 26.3 are not 26.1.2 evidence.
