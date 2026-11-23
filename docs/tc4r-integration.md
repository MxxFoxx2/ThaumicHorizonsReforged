# TC4R integration

## Dependency and source of truth

The local development setup uses a composite build of the sibling repository through `includeBuild('../thaumcraft-4-reforged')` in `settings.gradle`. The supported host is the official TC4R 1.1.3 Minecraft 26.3 release, tag `26.3`, pinned to commit `99499a8ef6cf9af0efa6a93a058ea56a42a0e7b2`. CI checks out that exact public revision. Keep the sibling checkout on the same tag when building locally.

For old Thaumcraft item and block metadata, use TC4R's `tools/data/id_map.json` rather than semantic guesses.

## Choosing the integration point

1. Search for an existing compat capability and its TC4R implementation in `compat/tc/TcReforgedPort`.
2. If the call is a plain public TC4R API (allowlisted in `tools/check_seams.py`), call TC4R directly from gameplay code.
3. Keep TC4R-specific lookups, effects, blocks, internal objects and mixins in `compat/tc/`.
4. A direct import in gameplay code is allowed only if the type is intentionally exposed in `THAUMCRAFT_API` in `tools/check_seams.py`. Before adding an exception, explain why it is public add-on API and why an adapter is unsuitable.
5. TC4R is a required dependency, but optional host extensions must be capability-checked. Prefer a safe no-op or disabling only the dependent feature over linking directly against a fork-only method.

## TH golem compatibility

- Stock TC4R 1.1.3 exposes `GolemBase.getGolemStrength()`, `getCarryLimit()`, `configure()`, `aiStep()` and `fireImmune()`, but does not expose the fork-only `GolemStats`, `golemStats()` or `getGolemTexture()` contract.
- `EntityGolemTH` owns its `GolemMaterialTH` values and overrides the public host methods for strength, carry capacity, health, armor and fire immunity. `GolemMaterialStatsMixin` adapts movement speed, water behavior, upgrade-slot count and per-material regeneration delay on the base golem tick/configuration path.
- `GolemRendererTHMixin` applies the material texture and any additional TH upgrade slots to the official renderer. These mixins are scoped to TH entities and leave stock golems unchanged.

## Optional host extensions

- The official host has no `NodeTransducerBlockEntity.stopForVortexConversion()`. TH checks for this method reflectively; the automatic multi-transducer-to-Vortex conversion runs only when the host can safely stop the transducer. On stock TC4R 1.1.3 that one conversion path is disabled.
- The official host has no `FxProxy.machineBeam(...)`; the optional adapter is a no-op when the method is absent.
- The optional `ResearchPage.creatureInfusion(String)` page type is loaded reflectively. Current TH research definitions do not request it, so there is no missing active page in the supported build.
- `ResearchItem.requireInfusionResearch()` is not needed at runtime: TH reproduces its behavior in the adapter by adding the hidden `INFUSION` parent for research items with infusion pages.
- `Wands.isWand(ItemStack)` in stock TC4R only recognizes the built-in wand item. TH's TC4R compatibility mixin restores the add-on contract by recognizing any `WandItem` subclass, which includes the TH disposable wand.

## Host API details worth preserving

- **Focus upgrade IDs:** TC4R's `FocusUpgradeType` stores upgrades in an ID-indexed static table. Registering a second upgrade at an occupied ID logs an error and ignores the new registration. Check the host's existing IDs before assigning any TH/custom ID; otherwise an upgrade can silently be unavailable to gameplay despite the log entry.
- **Focus upgrade localization:** the host API constructs keys as `focus.upgrade.<name>.name` and `focus.upgrade.<name>.text`. Match these exact keys when defining translations; do not infer a different suffix convention from unrelated language entries.

These details are tied to the host API implementation. Recheck the pinned TC4R source if that implementation changes.

## Mixins and hooks

Until the host provides a required native hook, TH may implement it through a mixin in `compat/tc/` and the existing TC4R compatibility implementation.

- Pin and verify the supported TC4R version range.
- Validate target class, method descriptor, injection point, side and lifecycle against the pinned source.
- Keep mixin configuration and access transformers synchronized with the implementation.
- Avoid changing host behavior when TH is absent.
- When TC4R gains the equivalent native hook, replace the mixin with the native API while keeping TH gameplay interfaces stable where possible.

## Working with the TC4R fork

- Prefer additive-only API changes; preserve behavior when TH is absent.
- Document new add-on-facing API in the fork's `docs/ADDON_API.md`.
- Build against the official `26.3` tag in the local composite checkout and confirm CI pins the same revision.
- Changes to `notlikethiss/thaumcraft-4-reforged` upstream require explicit user direction.

## Current add-on hooks and public API

- Verified against official `notlikethiss/thaumcraft-4-reforged` tag `26.3` / commit `99499a8ef6cf9af0efa6a93a058ea56a42a0e7b2`.
- Public scan API used by Scholar lens: `ScanManager` and `ScanKind`. The TH `LensScanPayload` independently revalidates goggles/lens, range and target on the server; no TC4R client networking is imported.
- Public rendering helpers allowed for legacy machine renderers: `FacingQuads`, `ModelBoxGeometry`, `TcRenderTypes`, `EntityQuadSink`, `VertexQuadSink`, `WorldRendering` and `WardedRendering`. These are used only from `client/` and cover the original scanner, crystal, brain/jar and halo geometry without a second rendering convention.
- `WandsRechargeMixin` and `VisStorageRechargeMixin` reject positive recharge and return no room for TH disposable wands. They do not change base wand behavior or require TC4R edits.
- Client-only mixins live in `client/` and have separate `thaumichorizons-client.mixins.json` / `thaumichorizons-tc-client.mixins.json` packages; common mixins remain isolated in their own configs to avoid Mixin package ownership errors.

## Validation

Run `python3 -I tools/check_seams.py` and the relevant Gradle tests/build. Add JUnit coverage for fallback contracts or deterministic adapters where practical. Exercise the actual client/server path when registries, effects, menus, rendering or networking are involved.

When reporting the change, include the TC4R revision, public API allowlist changes, mixin/API changes, fallback behavior and unverified runtime scenarios.
