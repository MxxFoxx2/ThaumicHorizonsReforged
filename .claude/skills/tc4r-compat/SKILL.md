---
name: tc4r-compat
description: Use when adding or changing integration with Thaumcraft 4: Reforged, its public API, compat adapters, ServiceLoader implementation, mixins, or the TC4R fork.
---

# TC4R integration procedure

1. Inspect the exact TC4R revision used by this repository and the relevant source/API implementation. The local development setup uses the sibling \`../thaumcraft-4-reforged\` composite build; CI pins a tested revision in \`.github/workflows/ci.yml\`.
2. Decide whether the integration belongs behind an existing \`compat/\` interface, needs a new narrow capability interface, or is a genuinely public API type used by inheritance/registration. Prefer the narrow compat seam.
3. Keep TC4R implementation details in \`compat/tc/\`. Direct imports from gameplay code are allowed only when the class is already in \`THAUMCRAFT_API\` in \`tools/check_seams.py\`.
4. If a public API allowlist addition is unavoidable, verify the type is intended for add-on use, explain why a compat wrapper is unsuitable, update the checker and the API boundary documentation, and run seam checks.
5. Keep ServiceLoader registration and unavailable fallback behavior consistent. Optional capabilities must advertise availability; callers must not accidentally invoke unavailable methods.
6. Put mixin targets and TC4R implementation hooks in \`compat/tc/\` behind the project's TC4R compatibility implementation. Check exact descriptors, target version constraints and client/server side. Never scatter TC4R mixins through gameplay packages.
7. Prefer additive hooks in the fork: preserve behavior when TH is absent, avoid breaking existing APIs, and document the extension in the fork's \`docs/ADDON_API.md\`. Changes to the upstream \`notlikethiss/thaumcraft-4-reforged\` require explicit user direction.
8. Test both the TC4R-backed path and unavailable/fallback contract when practical. Run \`python3 -I tools/check_seams.py\`, JUnit/build, and the relevant runtime scenario.
9. Report the exact TC4R revision, changed API surface, fallback behavior and all unverified runtime cases.

Do not pin a moving branch when a tested commit SHA is required. Do not silently use a fallback item/effect or broaden the allowlist to make compilation pass.
