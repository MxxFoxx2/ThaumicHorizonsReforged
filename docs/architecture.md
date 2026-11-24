# Architecture

## Compatibility boundaries

The project has two independent compatibility dimensions:

1. Minecraft/NeoForge version differences are isolated behind `platform/` and `client/platform/`.
2. Thaumcraft integration is isolated behind `dev.visfox.thaumichorizons.compat/` and its TC4R implementation.

Keep gameplay code independent of version-specific APIs and TC4R internals wherever an existing boundary provides the needed abstraction.

## Version seams

- `platform/` contains common/server abstractions for version-sensitive APIs.
- `client/platform/` contains version-sensitive client and rendering abstractions.
- Add a seam when an API is expected to vary between supported Minecraft/NeoForge versions.
- Do not leak client-only types through common interfaces or server-side registration.
- `tools/check_seams.py` is the executable contract for configured import boundaries. Change its policy only after reviewing the architectural implications.

## Thaumcraft compatibility

- TC4R (`../thaumcraft-4-reforged-26.1.2`, composite build, tag `26.1.2`, commit `8b953e69c3d625469006f688d964d9f2a7911590`) is the only integration target and a hard dependency: there is no ServiceLoader, no fallback backend and no availability gating.
- `compat/tc/TcReforgedPort` exposes the TC4R-backed capability implementations as public static fields; gameplay code calls them directly (`TcReforgedPort.fx.sparkle(...)`), or uses the TC4R public API straight away when the allowlist in `tools/check_seams.py` permits it.
- TC4R implementation details and mixins belong in `compat/tc/`.
- Gameplay code may directly import only the explicit public API allowlist in `tools/check_seams.py`. This is a reviewed exception, not general permission to import TC4R classes.

See [TC4R integration](tc4r-integration.md) before changing the host integration.

## Golem materials

- TH golems use `entity/GolemMaterialTH` — TH-owned materials, characteristics and `fromBlock` mapping, independent from TC4R's `GolemType` (which is stock Thaumcraft again).
- The material is a synced string (`TH_MATERIAL`) and is persisted as a stable string id (`GolemMaterialTH`) with a legacy ordinal mapping for old saves. The inherited 26.3 integration overrides the host golem contract; revalidate those overrides and mixin targets against TC4R 26.1.2 before treating them as compatible.

## Registration and state

- Content registration uses the existing DeferredRegister classes under `dev.visfox.thaumichorizons.registry/`.
- Player state uses NeoForge attachments; item state uses data components. Follow the existing copy, synchronization and serialization contracts.
- Critter/self-infusion recipes use the TH recipe system, not TC4R's `TcRecipes`.
- Do not create data-component-bearing ItemStacks during static initialization or registration constructors before `DefaultDataComponentsBoundEvent`; use lazy creation as established in the code.

## Client/server responsibilities

- The server owns authoritative gameplay mutations, inventory changes, entity spawning, persistent data and validation.
- The client owns presentation, input and prediction only where the server validates the final action.
- Common classes must not reference client renderers, screens, input or other client-only classes.
- Network payloads require correct direction, validation and ownership checks.
- Persistent block/entity/world data must account for save/load, chunk unload/reload and malformed or partial data.

## Chameleon Skin

Chameleon Skin is an intentional architectural redesign, not a normal potion effect. Its source of truth, server state, tracking synchronization and render cancellation are documented in [fidelity.md](fidelity.md). Read that section before modifying the infusion or player-render path; use the two-client checklist in [verification.md](verification.md) for runtime validation.

## Where changes belong

| Concern | Location |
|---|---|
| Gameplay behavior | Existing feature package under `dev.visfox.thaumichorizons` |
| Version-specific API | `platform/` or `client/platform/` |
| TC4R capability contract | `compat/` |
| TC4R implementation / mixins | `compat/tc/` |
| Client UI and renderers | `client/` |
| Registration | Existing `registry/` classes |
| Deterministic tests | Matching package under `src/test/java/` |
| Manual runtime checks | `docs/verification.md` |
| Fidelity deviations | `docs/fidelity.md` |
