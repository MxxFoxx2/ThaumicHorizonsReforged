# Architecture

## Project shape

The port has two independent compatibility dimensions:

1. Minecraft/NeoForge version differences, isolated behind `platform/` and `client/platform/`.
2. Thaumcraft integration, isolated behind `dev.visfox.thaumichorizons.compat/` and its TC4R implementation.

Keep gameplay code independent of version-specific APIs and TC4R internals wherever the existing boundaries provide an abstraction.

## Version seams

- `platform/` contains server/common abstractions for version-sensitive APIs.
- `client/platform/` contains version-sensitive client/render abstractions.
- Place a new API behind a seam when it is expected to vary between supported Minecraft/NeoForge versions.
- Avoid leaking client-only types through common interfaces or server-side registration.
- Use `tools/check_seams.py` as the executable contract. Update its policy only after reviewing the architectural implications.

## Thaumcraft compatibility

- `compat/` defines narrow capability interfaces such as aspect, research, warp, essentia, FX, potion, item, mirror and bauble capabilities.
- `TcReforgedPort` supplies the TC4R-backed implementation through Java ServiceLoader.
- Unavailable fallback interfaces must remain safe and expose availability explicitly. Code must check capability availability before invoking methods that are unsupported.
- Keep TC4R implementation classes and mixin code under `compat/tc/`.
- Gameplay code may directly import only the explicit TC4R public API allowlist in `tools/check_seams.py`. Treat this as a reviewed exception, not as a general permission to import TC4R classes.

See [TC4R integration](tc4r-integration.md) for change procedure.

## Registration and state

- Content registration is centralized in `dev.visfox.thaumichorizons.registry/` using the existing DeferredRegister pattern.
- Compat-dependent content must respect capability availability during registration and datapack lifecycle.
- Player state uses NeoForge attachments; item state uses data components. Follow existing copy, synchronization and serialization contracts.
- Critter/self-infusion recipes use the TH recipe system; do not move them into TC4R's `TcRecipes` without an explicit design reason.
- Do not construct data-component-bearing ItemStacks during static initialization or registration constructors before components are bound by `DefaultDataComponentsBoundEvent`. Use lazy suppliers/creation as established in the code.

## Client/server boundary

- Server owns authoritative gameplay mutations, inventory changes, entity spawning, persistent data and validation.
- Client code owns presentation, input and prediction only where the server validates the final action.
- Common classes must not reference client renderer, screen, input or other client-only classes.
- Network payloads require explicit direction, validation, ownership checks and synchronization behavior.
- For persistent world/block/entity data, consider save/load, chunk unload/reload and invalid or partially populated old data.

## Where to put changes

| Concern | Preferred location |
|---|---|
| Gameplay behavior | Existing feature package under `dev.visfox.thaumichorizons` |
| Version-specific API | `platform/` or `client/platform/` |
| TC4R capability contract | `compat/` |
| TC4R implementation / mixin | `compat/tc/` |
| Client UI and renderers | `client/` |
| Registration | Existing `registry/` classes |
| Deterministic automated tests | Matching package under `src/test/java/` |
| Manual runtime test steps | `docs/verification.md` |
| Fidelity deviation | `docs/fidelity.md` |
