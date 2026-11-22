# TC4R integration

## Dependency and source of truth

The local development setup uses a composite build of the sibling repository through `includeBuild('../thaumcraft-4-reforged')` in `settings.gradle`. CI checks out a pinned TC4R commit; when changing or debugging an API, compare the local source with the revision pinned in `.github/workflows/ci.yml`.

For old Thaumcraft item and block metadata, use TC4R's `tools/data/id_map.json` rather than semantic guesses.

## Choosing the integration point

1. Search for an existing compat capability and its TC4R implementation in `compat/tc/TcReforgedPort`.
2. If the call is a plain public TC4R API (allowlisted in `tools/check_seams.py`), call TC4R directly from gameplay code.
3. Keep TC4R-specific lookups, effects, blocks, internal objects and mixins in `compat/tc/`.
4. A direct import in gameplay code is allowed only if the type is intentionally exposed in `THAUMCRAFT_API` in `tools/check_seams.py`. Before adding an exception, explain why it is public add-on API and why an adapter is unsuitable.
5. TC4R is a hard dependency: there are no fallback backends and no availability gates to honor.

## Golem stats

- `GolemBase.golemStats()` (overridable) is the TC4R extension point for golem characteristics; `EntityGolemTH` feeds it from its own `GolemMaterialTH`. `GolemBase.getGolemTexture()` is overridable for the model texture. `GolemType` stays stock Thaumcraft: no TH materials there.

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
- Build against the intended local composite checkout and confirm CI pins the tested revision.
- Changes to `notlikethiss/thaumcraft-4-reforged` upstream require explicit user direction.

## Current add-on hooks and public API

- Verified against local `feat/addon-api` `455a33f961ecd6b7a3dbf5fd7ff7bcbc3b923d9d`; CI pins the earlier `c5b7a0d99c2c23f097591c1343b8f6d3d3d8febb`, which also contains every API currently consumed.
- Public scan API used by Scholar lens: `ScanManager` and `ScanKind`. The TH `LensScanPayload` independently revalidates goggles/lens, range and target on the server; no TC4R client networking is imported.
- Public rendering helpers allowed for legacy machine renderers: `FacingQuads`, `ModelBoxGeometry`, `TcRenderTypes`, `EntityQuadSink`, `VertexQuadSink`, `WorldRendering` and `WardedRendering`. These are used only from `client/` and cover the original scanner, crystal, brain/jar and halo geometry without a second rendering convention.
- `AspectGrid` is the host's public research-page aspect grid. TH uses it only in the client research-page mixin so creature and self-infusion pages render the same aspect layout as the host; an adapter would duplicate the host's grid geometry and constants.
- `WandsRechargeMixin` and `VisStorageRechargeMixin` reject positive recharge and return no room for TH disposable wands. They do not change base wand behavior or require TC4R edits.
- Client-only mixins live in `client/` and have separate `thaumichorizons-client.mixins.json` / `thaumichorizons-tc-client.mixins.json` packages; common mixins remain isolated in their own configs to avoid Mixin package ownership errors.

## Validation

Run `python3 -I tools/check_seams.py` and the relevant Gradle tests/build. Add JUnit coverage for fallback contracts or deterministic adapters where practical. Exercise the actual client/server path when registries, effects, menus, rendering or networking are involved.

When reporting the change, include the TC4R revision, public API allowlist changes, mixin/API changes, fallback behavior and unverified runtime scenarios.
