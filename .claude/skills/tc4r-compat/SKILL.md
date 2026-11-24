---
name: tc4r-compat
description: Use when changing integration with Thaumcraft 4: Reforged, public API usage, compat adapters, optional capabilities, mixins, or the pinned TC4R host.
---

# TC4R integration procedure

1. Inspect `../thaumcraft-4-reforged-26.1.2` at commit `8b953e69c3d625469006f688d964d9f2a7911590` and the same pin in `.github/workflows/ci.yml`. Contracts from the 26.3 line are not evidence for this host.
2. Search for an existing compat capability first. Prefer a narrow interface in `compat/` when behavior is optional or host-specific.
3. Keep TC4R implementation details and mixins in `compat/tc/`.
4. Direct imports from gameplay code are allowed only for classes already in `THAUMCRAFT_API` in `tools/check_seams.py`. Add an exception only for a verified public add-on API type, with a documented reason.
5. Preserve safe optional-capability fallbacks. Check availability before invoking an optional operation; TC4R itself remains a required dependency.
6. For mixins, verify the exact target, descriptor, injection point, side and lifecycle against the pinned source.
7. Do not modify the pinned host or upstream repository without explicit user direction. If a host change is authorized, prefer additive-only API, preserve behavior when TH is absent and document add-on-facing API in the host repository.
8. Changes to `notlikethiss/thaumcraft-4-reforged` upstream require explicit user direction.
9. Run seam and Gradle checks, add deterministic JUnit coverage where useful, and test the relevant runtime path when integration affects registries, effects, menus, rendering or networking.

Do not silently substitute missing items/effects or broaden the public API allowlist just to make compilation pass.
