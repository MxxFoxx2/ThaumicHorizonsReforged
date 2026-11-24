# CI and releases

## Target and source layout

The `26.1.2` branch targets Minecraft `26.1.2`, NeoForge `26.1.2.122`, Java 25 and TC4R `1.1.3` tag `26.1.2`, commit `8b953e69c3d625469006f688d964d9f2a7911590`. Versions come from `gradle.properties`; `tc4r_version` is used by both the Gradle dependency and generated mod metadata.

The project uses a composite Gradle build: `settings.gradle` includes `../thaumcraft-4-reforged-26.1.2`. Keep that sibling checkout at the pinned TC4R revision. CI and the release workflow check out the public repository into the same directory name and fail if the resolved commit differs.

## Continuous integration

The `CI` workflow runs on every branch push, on pull requests targeting `26.1.2`, and on manual dispatch. It:

1. verifies the pinned TC4R revision;
2. sets up Java 25 and the Gradle Wrapper;
3. runs `tools/check_seams.py` and `tools/check_assets.py`;
4. runs `gradlew build` with configuration/build caches and parallel execution;
5. checks the exact JAR name, required mod resources and absence of development-only files;
6. uploads the JAR and SHA-256 checksum as a workflow artifact.

The build runs with the local build cache and configuration cache. `gradle/actions/setup-gradle` additionally caches Gradle User Home and validates the wrapper. Configuration-cache writes are limited to pushes on `26.1.2` and manual dispatches from that branch; pull requests and other branches use read-only caches.

To reuse encrypted configuration-cache entries across GitHub-hosted runners, set the repository Actions secret `GRADLE_ENCRYPTION_KEY`. Generate it locally with:

```sh
openssl rand -base64 32
```

Without that secret, dependency/build caches still work, but configuration-cache entries cannot be reused between fresh hosted runners.

A green CI run proves the configured compile, static and JAR checks passed on the 26.1.2 source backport. It does not replace client, dedicated-server, multiplayer, persistence or visual acceptance testing.

## Releases

The release workflow is manual-only (`workflow_dispatch`). Run it from branch `26.1.2` or from a `v*` tag and explicitly set the required `release_authorized` input to confirm that the 26.1.2 source/runtime acceptance gate passed. The job refuses to run without that attestation or outside those refs. A tag version must exactly match `mod_version`, and a tagged commit must be reachable from `26.1.2`. Legacy inherited tags without the `v` prefix are not accepted.

The workflow builds against the pinned TC4R 26.1.2 commit and validates the JAR before publishing. Automation enforces the ref, attestation, host pin, build and JAR checks; a human remains responsible for the runtime evidence and release authorization. Updating this workflow is build preparation only and does not authorize a release. Do not bypass a failing build or host-revision check.

The workflow uploads only the mod JAR and its SHA-256 checksum. GitHub also exposes automatic source ZIP/TAR snapshots for a tag. The root `.gitattributes` uses `export-ignore` to keep those snapshots free of `.github/`, `docs/`, `reference/`, `tools/`, `AGENTS.md`, and `CLAUDE.md`, while retaining source, build files, README and license. Gradle caches, logs and intermediate outputs are never release assets. See [GitHub's source archive documentation](https://docs.github.com/en/repositories/working-with-files/using-files/downloading-source-code-archives).
