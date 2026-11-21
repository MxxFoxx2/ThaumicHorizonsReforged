---
name: tc4r-compat
description: Use when changing integration with Thaumcraft 4: Reforged, public API usage, compat adapters, ServiceLoader, mixins, or the TC4R fork.
---

# TC4R integration procedure

1. Inspect the TC4R source revision used locally and the commit pinned in `.github/workflows/ci.yml`.
2. Search for an existing compat capability first. Prefer a narrow interface in `compat/` when behavior is optional or host-specific.
3. Keep TC4R implementation details and mixins in `compat/tc/`.
4. Direct imports from gameplay code are allowed only for classes already in `THAUMCRAFT_API` in `tools/check_seams.py`. Add an exception only for a verified public add-on API type, with a documented reason.
5. TC4R is a hard dependency: there are no ServiceLoader fallbacks or `isAvailable()` gates. Fail at load time if a required host API is missing rather than silently degrading.
6. For mixins, verify the exact target, descriptor, injection point, side and lifecycle against the pinned source.
7. Prefer additive-only changes to the fork. Preserve behavior when TH is absent and document add-on-facing API in the fork's `docs/ADDON_API.md`.
8. Changes to `notlikethiss/thaumcraft-4-reforged` upstream require explicit user direction.
9. Run seam and Gradle checks, add deterministic JUnit coverage where useful, and test the relevant runtime path when integration affects registries, effects, menus, rendering or networking.

Do not silently substitute missing items/effects or broaden the public API allowlist just to make compilation pass.
