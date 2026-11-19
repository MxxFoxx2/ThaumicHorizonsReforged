# Building and checks

## Toolchain and dependency

Read \`gradle.properties\` for the active Java, Minecraft, NeoForge and mod versions. Do not assume versions in old notes are current.

The development settings use a composite build of the sibling TC4R repository:

\`\`\`gradle
includeBuild('../thaumcraft-4-reforged')
\`\`\`

The CI workflow checks out a specific TC4R commit. When changing or debugging integration APIs, compare the local sibling checkout with the CI pin in \`.github/workflows/ci.yml\`.

## Common commands

Run from the repository root.

| Purpose | Linux/macOS | Windows |
|---|---|---|
| Full build and configured checks/tests | \`./gradlew build\` | \`.\gradlew.bat build\` |
| Unit tests only | \`./gradlew test\` | \`.\gradlew.bat test\` |
| Architecture/API boundaries | \`python3 -I tools/check_seams.py\` | \`py -3 -I tools/check_seams.py\` |
| Static model/blockstate/item references | \`python3 -I tools/check_assets.py\` | \`py -3 -I tools/check_assets.py\` |
| Development client | \`./gradlew runClient\` | \`.\gradlew.bat runClient\` |
| Development server | \`./gradlew runServer\` | \`.\gradlew.bat runServer\` |

Use the wrapper checked into the repository. If a command is unavailable on the current OS, report that instead of claiming it ran.

## What CI proves

The current CI workflow runs:

1. \`python3 -I tools/check_seams.py\`;
2. Gradle \`build\` using Java 25 and the configured cache/parallel options;
3. verification that exactly the expected distributable JAR exists and contains required mod resources without development-only files.

Gradle's build lifecycle runs configured project checks and tests. The CI job does not launch a live client and does not run the manual gameplay checklist. The static asset-reference script is available for local use but is not a separate CI step in the current workflow.

Check \`.github/workflows/ci.yml\` for the exact current commands and pinned TC4R revision; this document describes intent, not a substitute for reading the workflow.

## Interpreting results

- Seam check success means the configured import/boundary rules passed.
- Asset check success means the references inspected by that script resolve; it does not prove texture appearance, UV correctness or renderer behavior.
- Gradle build success means the configured build/check/test/package tasks passed; it does not prove gameplay correctness.
- Server startup does not prove client rendering or multiplayer synchronization.
- Client single-player testing does not prove dedicated-server safety or two-client synchronization.

Record manual runtime results in \`docs/verification.md\`. Never mark unrun steps as passed.
