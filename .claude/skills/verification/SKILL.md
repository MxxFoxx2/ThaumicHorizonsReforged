---
name: verification
description: Use when validating a change, investigating whether a feature is complete, updating verification documentation, or preparing a release-readiness report.
---

# Verification procedure

## 1. Select checks based on the changed behavior

- Java/data-only deterministic logic: targeted JUnit tests, then Gradle build.
- Architecture or API-boundary changes: `python3 -I tools/check_seams.py`.
- Blockstate/model/item asset references: `python3 -I tools/check_assets.py`.
- Compilation, packaging and project tests: `./gradlew build` (CI additionally validates the distributable JAR).
- Runtime registration, datapack loading and server lifecycle: launch `runServer` and inspect logs.
- Rendering, input, GUI, particles, audio and client behavior: launch `runClient`.
- Networking, ownership, tracking, race conditions and persistence across players: use a dedicated server with at least two clients where applicable.

## 2. Report evidence precisely

Use these terms consistently:

| Evidence | What it proves |
|---|---|
| Source inspection | The implementation exists in source; behavior is not executed |
| JUnit passed | The named automated test cases passed |
| Seam check passed | The configured architecture/import rules passed |
| Asset check passed | Static resource references checked by the script resolved |
| Gradle build passed | Compilation, configured checks/tests and packaging passed |
| Dedicated server tested | The named server scenarios were actually exercised |
| Client tested | The named client/UI/render scenarios were actually exercised |
| Multiplayer tested | The named multi-client scenarios were actually exercised |

Never infer runtime success from compilation, CI or static inspection. A successful dedicated-server boot does not prove client rendering; one-client success does not prove tracking/synchronization to a second client.

## 3. Update the verification record

- Add or refine concrete steps in `docs/verification.md` for behavior that remains untested.
- Mark a checkbox complete only with reproducible evidence. Record date/build/world setup and concise observations for nontrivial manual checks.
- Keep implementation gaps separate from implemented-but-unverified behavior.
- Update `docs/fidelity.md` only for confirmed differences from the original or known incomplete behavior.
- Do not rewrite unrelated checklists or mark an entire subsystem complete after testing only one path.

## 4. Release gate

A release-readiness report must separately state:
- clean-checkout build and seam result;
- automated test result;
- distributable JAR validation result;
- dedicated-server startup and save/reload result;
- client visual/UI/input result;
- multiplayer result;
- unresolved fidelity deviations and unsupported targets.

List checks that were not run as **not verified**, not as passed.
