# CI and releases

## Continuous integration

The `CI` workflow runs on pushes to any branch, pull requests targeting `main`, and manual dispatch. On the `1.21.1` branch it builds with Java 21 and the Gradle Wrapper, runs the platform/API seam checker, the data-pack checker and the asset checker, smoke-tests a dedicated server through `Done`, and inspects the distributable JAR.

The project uses a composite Gradle build: `settings.gradle` includes the sibling TC4R checkout selected by the `tc4rPath` property. CI therefore checks out TC4R as a sibling directory and passes `-Ptc4rPath=../thaumcraft-4-reforged` to the Gradle invocation. On this branch the checkout is the public `notlikethiss/thaumcraft-4-reforged` branch `mc/1.21.1`, pinned to commit `03f354ac4ed91151ef10c9d9b2e4947789453472`; the workflow fails if the resolved commit differs. Locally, `gradle.properties` points `tc4rPath` at the sibling `../thaumcraft-4-reforged-1.21.1` worktree.

The build runs with Gradle parallel execution, the local build cache, and the configuration cache. `gradle/actions/setup-gradle` additionally caches Gradle User Home and validates the wrapper. ModDevGradle detects the CI environment and uses its CI compilation path, avoiding the normal Minecraft decompile/recompile pipeline.

For configuration-cache entries to be encrypted and reused across GitHub-hosted runners, add a repository Actions secret named `GRADLE_ENCRYPTION_KEY`. Generate it locally with:

```sh
openssl rand -base64 32
```

Without that secret, the Gradle dependency/build caches still work; configuration-cache compatibility is still checked by CI, but its entries cannot be reused between fresh hosted runners.

## Releases

A release is triggered only by a version tag beginning with `v`, for example `v0.1.0`. The tag version must exactly match `mod_version` in `gradle.properties`, and the tagged commit must be reachable from `main`. Legacy inherited tags such as `1.4.0` do not match the trigger.

The release workflow deliberately builds against the source commit for the currently published upstream TC4 Reforged `1.1.3` release, not against the development fork. It checks for the APIs currently used by this add-on (custom `WandItem` recognition, Creature Infusion research pages, and Synthetic/Ethereal node types) before it spends time building or publishing anything.

**Current release gate:** the public TC4 Reforged `26.3` / `1.1.3` release does not yet contain all of those APIs. The current add-on code uses the development fork's `feat/addon-api` branch. Consequently, the production release workflow is intentionally blocked until the required API is included in a published TC4 Reforged version. Do not bypass this check by switching the release build to the development fork: that would risk publishing a JAR that users cannot run with the dependency currently documented in the README. This gate describes the 26.3 release line; the `1.21.1` branch has no published release and no release workflow run yet.

After the dependency is published with the required API, update the pinned TC4 Reforged commit in `.github/workflows/release.yml` to the commit corresponding to that published release. Then update the dependency version/range in the mod metadata and documentation as appropriate, bump `mod_version`, and push a matching `v<mod_version>` tag from `main`.

The workflow uploads only the mod JAR produced by Gradle and its SHA-256 checksum. GitHub itself also exposes automatic source ZIP/TAR snapshots for the tag; the repository cannot remove those links from the release. The root `.gitattributes` uses `export-ignore` to keep those snapshots free of `.github/`, `docs/`, `reference/`, `tools/`, `AGENTS.md`, and `CLAUDE.md`, while retaining the actual source, build files, README, and license. Gradle caches, logs, and intermediate outputs are never attached as release assets. See [GitHub's source archive documentation](https://docs.github.com/en/repositories/working-with-files/using-files/downloading-source-code-archives).
