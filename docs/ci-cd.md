# CI and releases

## Continuous integration

The `CI` workflow runs on pushes to `main`, pull requests targeting `main`, and manual dispatch. It builds with Java 25 and the Gradle Wrapper, runs the platform/API seam checker, and inspects the distributable JAR.

The project uses a composite Gradle build: `settings.gradle` includes `../thaumcraft-4-reforged` by default, so CI checks out both repositories as sibling directories (local overrides are possible with `-Ptc4rPath=...`). The add-on currently needs the `MxxFoxx2/thaumcraft-4-reforged` `feat/addon-api` implementation, pinned to commit `388aebb2ed9f7af45c580792f8f59976ea9a822b` and verified in `.github/workflows/ci.yml`. Bump this pin deliberately when the required API changes; remove the composite and the pin once the four remaining hooks ship in a published upstream release.

The build runs with Gradle parallel execution, the local build cache, and the configuration cache. `gradle/actions/setup-gradle` additionally caches Gradle User Home and validates the wrapper. ModDevGradle detects the CI environment and uses its CI compilation path, avoiding the normal Minecraft decompile/recompile pipeline.

For configuration-cache entries to be encrypted and reused across GitHub-hosted runners, add a repository Actions secret named `GRADLE_ENCRYPTION_KEY`. Generate it locally with:

```sh
openssl rand -base64 32
```

Without that secret, the Gradle dependency/build caches still work; configuration-cache compatibility is still checked by CI, but its entries cannot be reused between fresh hosted runners.

## Releases

A release is triggered only by a version tag beginning with `v`, for example `v0.1.0`. The tag version must exactly match `mod_version` in `gradle.properties`, and the tagged commit must be reachable from `main`. Legacy inherited tags such as `1.4.0` do not match the trigger.

The release workflow deliberately builds against the source commit for the currently published upstream TC4 Reforged `1.1.3` release, not against the development fork. It checks for the APIs currently used by this add-on (custom `WandItem` recognition, golem stats/texture hooks, `machineBeam`, transducer stop hook, and Synthetic/Ethereal node types) before it spends time building or publishing anything.

**Current release gate:** the public TC4 Reforged `26.3` / `1.1.3` release does not yet contain those four additive hooks. CI still uses the development fork's `feat/addon-api` branch; the production release workflow is intentionally blocked until the required API is published upstream. Do not bypass this check by switching the release build to the development fork: that would risk publishing a JAR that users cannot run with the dependency currently documented in the README.

After the dependency is published with the required API, update the pinned TC4 Reforged commit in `.github/workflows/release.yml` to the commit corresponding to that published release. Then update the dependency version/range in the mod metadata and documentation as appropriate, bump `mod_version`, and push a matching `v<mod_version>` tag from `main`.

The workflow uploads only the mod JAR produced by Gradle and its SHA-256 checksum. GitHub itself also exposes automatic source ZIP/TAR snapshots for the tag; the repository cannot remove those links from the release. The root `.gitattributes` uses `export-ignore` to keep those snapshots free of `.github/`, `docs/`, `reference/`, `tools/`, `AGENTS.md`, and `CLAUDE.md`, while retaining the actual source, build files, README, and license. Gradle caches, logs, and intermediate outputs are never attached as release assets. See [GitHub's source archive documentation](https://docs.github.com/en/repositories/working-with-files/using-files/downloading-source-code-archives).
