# TC4R integration

## Dependency and source of truth

The local development setup includes the sibling repository through \`includeBuild('../thaumcraft-4-reforged')\` in \`settings.gradle\`. The CI workflow checks out a specific tested TC4R commit. Check both the local source state and the CI pin before relying on an API.

For old Thaumcraft metadata and item/block mappings, prefer TC4R's \`tools/data/id_map.json\` over semantic guesses.

## Choosing the integration point

1. Search for an existing compat capability and its TC4R implementation.
2. If the feature is optional or may differ between host implementations, expose the smallest useful contract through \`compat/\`.
3. Keep TC4R-specific registry lookups, effects, blocks, internal objects and mixins in \`compat/tc/\`.
4. A direct TC4R import in gameplay code is allowed only if the type is intentionally exposed in \`THAUMCRAFT_API\` in \`tools/check_seams.py\`. Before adding an exception, explain why it is a public add-on API type and why an adapter is not appropriate.
5. Keep unavailable implementations safe. Callers must check \`isAvailable()\` before using optional operations; do not disguise a missing registry entry as an unrelated item/effect without an explicit compatibility policy.

## Mixins and hooks

Until a required hook is provided by the host API, TH may implement it through a mixin in \`compat/tc/\` and the existing TC4R compatibility implementation.

- Pin and verify the supported TC4R version range.
- Validate exact target class, method descriptor, injection point, side and lifecycle against the pinned source.
- Keep mixin configuration and access transformers synchronized with the implementation.
- Avoid changing host behavior when TH is absent.
- When TC4R gains the equivalent native hook, replace the mixin with the native API while keeping TH gameplay interfaces stable where possible.

## Working with the TC4R fork

- Prefer additive-only API changes.
- A hook should not alter existing behavior for users without TH.
- Document new add-on-facing API in the fork's \`docs/ADDON_API.md\`.
- Build TH against the local composite build and confirm that CI uses the intended tested revision.
- Changes to the separate upstream repository \`notlikethiss/thaumcraft-4-reforged\` require explicit user direction; do not publish upstream changes automatically.

## Required validation and reporting

Run the seam checker and Gradle checks. Add JUnit tests for fallback contracts or deterministic adapters where practical. Exercise the relevant runtime path in client/server when host registries, effects, menus, rendering or networking are involved.

In the report, include the exact TC4R revision, any public API allowlist changes, mixin/API changes, fallback behavior and unverified runtime scenarios.
