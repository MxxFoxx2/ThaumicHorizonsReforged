---
name: port-content
description: Use when porting an item, block, entity, machine, recipe, research entry, effect, or other gameplay/content feature from the Thaumic Horizons 1.7.10 reference into this repository.
---

# Port content from 1.7.10

1. Find the original implementation and all related registration, recipe, research, aspect, localization, model and texture files under \`reference/1.7.10/\`. Trace callers and lifecycle; do not port only the named class.
2. Identify the modern TC4R equivalents using the sibling TC4R source and its \`tools/data/id_map.json\` when available. Do not guess item/block IDs or silently substitute a semantically similar object.
3. Inspect existing TH implementations and shared helpers before adding code. Reuse the current registry, compat, platform and rendering patterns.
4. Implement server-authoritative behavior first. Then wire client presentation, persistence, synchronization, drops, recipes, research and localization as applicable.
5. Preserve original constants, probabilities, aspect amounts, recipe ingredients, research gates and behavior unless a necessary deviation is justified. Record deviations in \`docs/fidelity.md\`.
6. Add JUnit coverage for deterministic transformations, codecs and data contracts where useful. Do not claim it replaces client, server or multiplayer checks.
7. Run relevant seam/build/asset checks. Add manual scenarios to \`docs/verification.md\` when behavior still needs an in-game test.
8. Report source/reference files consulted, implementation scope, checks run, unverified scenarios and intentional deviations.

Do not mark the feature fully ported merely because it compiles or appears in the creative tab.
