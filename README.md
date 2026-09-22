# AIDEAL–RDPro: five measured source conditions

This repository publishes **exact source snapshots** of the RDPro/Beast library versions used in the 2026-09-21 GPT-5.3-Codex study. The shared AIDEAL controller, portable results and harness live in [ZhuochengShang/AIDEAL](https://github.com/ZhuochengShang/AIDEAL/tree/evaluation/2026-09-21-rdpro-gpt-5.3-codex-five-conditions). There is **one RDPro repository with five condition branches**, not five separate forks.

`main` is a navigation-only orphan branch, separate from the five source snapshots. The published snapshots have new commit IDs but **identical Git trees** to the measured versions. Original local histories and frozen study evidence remain unchanged. [study.json](study.json) records both identities, tree IDs, artifact hashes and links.

## Open a source snapshot

| Condition | Dated source branch | Published snapshot commit | Treatment files |
|---|---|---|---|
| Original | [original](https://github.com/ZhuochengShang/AIDEAL-RDPro/tree/study/2026-09-21-gpt-5.3-codex/original) | [`5ee41a6dc37e`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/5ee41a6dc37ebe5f63bf7a3a0b74c5483460b9c8) | No additive treatment; snapshot baseline |
| Generated README only | [readme-only](https://github.com/ZhuochengShang/AIDEAL-RDPro/tree/study/2026-09-21-gpt-5.3-codex/readme-only) | [`41a302b39dd7`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/41a302b39dd7d9260842827d22bf7fa48dadcad7) | [selected README](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/41a302b39dd7d9260842827d22bf7fa48dadcad7/.aideal/treatments/README.md) |
| Aliases only | [alias-only](https://github.com/ZhuochengShang/AIDEAL-RDPro/tree/study/2026-09-21-gpt-5.3-codex/alias-only) | [`80ba93d32e9a`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/80ba93d32e9aff65fa12e730abb66a67663b9e35) | [Scala wrappers](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/80ba93d32e9aff65fa12e730abb66a67663b9e35/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/AidealAliases0001.scala) · [alias interface](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/80ba93d32e9aff65fa12e730abb66a67663b9e35/.aideal/treatments/ALIASES.md) |
| Error hints only | [error-hints-only](https://github.com/ZhuochengShang/AIDEAL-RDPro/tree/study/2026-09-21-gpt-5.3-codex/error-hints-only) | [`1f9e07195264`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/1f9e0719526455a6a89751869281df91eabeb5c0) | [error hints](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/1f9e0719526455a6a89751869281df91eabeb5c0/.aideal/treatments/error_hints.json) |
| Combined | [combined](https://github.com/ZhuochengShang/AIDEAL-RDPro/tree/study/2026-09-21-gpt-5.3-codex/combined) | [`cdb03c2e8343`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/cdb03c2e8343c80c48ab60d268c9ad3013e3570b) | [Scala wrappers](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/cdb03c2e8343c80c48ab60d268c9ad3013e3570b/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/AidealAliases0001.scala) · [alias interface](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/cdb03c2e8343c80c48ab60d268c9ad3013e3570b/.aideal/treatments/ALIASES.md) · [error hints](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/cdb03c2e8343c80c48ab60d268c9ad3013e3570b/.aideal/treatments/error_hints.json) · [selected README](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/cdb03c2e8343c80c48ab60d268c9ad3013e3570b/.aideal/treatments/README.md) |

Every measured condition starts from local baseline **`547f7f912131a8032f6b5d26991415a5faf05cef`**. The published baseline is **[`5ee41a6dc37e`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/5ee41a6dc37ebe5f63bf7a3a0b74c5483460b9c8)**; the other four published snapshots are its direct descendants. README-only and hints-only retain the original library code. Aliases-only adds forwarding wrappers without editing baseline source files; Combined uses the same wrappers, selected README and hints. The separate source-refactor experiment is excluded.

### Measured history versus publication snapshots

Full-history publication attempts failed. Local Git validation also found an inherited tree with duplicate entries, so this repository uses clean snapshot history instead of importing that ancestry. The table below maps the historical measured commits to GitHub snapshot commits. Identical tree IDs mean the tracked file bytes, paths and modes are unchanged; the commit ancestry and metadata are different. **The snapshots were not used to run a new experiment.**

| Condition | Measured local commit (provenance) | Published snapshot commit | Identical tree ID |
|---|---|---|---|
| Original | `547f7f912131a8032f6b5d26991415a5faf05cef` | [`5ee41a6dc37ebe5f63bf7a3a0b74c5483460b9c8`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/5ee41a6dc37ebe5f63bf7a3a0b74c5483460b9c8) | `757142fe21ba53b1388880d6abc7423aff844295` |
| Generated README only | `7ab580c744dbf4d02a0ced29bd81b6cc4d0fbb72` | [`41a302b39dd7d9260842827d22bf7fa48dadcad7`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/41a302b39dd7d9260842827d22bf7fa48dadcad7) | `957cbf1e741f3df545b3f82d69e6d62fbee3d905` |
| Aliases only | `dcaff7fbe2ce704707556b16d3b3e9e6a17a78e1` | [`80ba93d32e9aff65fa12e730abb66a67663b9e35`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/80ba93d32e9aff65fa12e730abb66a67663b9e35) | `fd7aeaa1abe5a0df5ad0752fdbe261bf88cb6dba` |
| Error hints only | `bc55f6b312c048bd6110749e8def2fcaece74968` | [`1f9e0719526455a6a89751869281df91eabeb5c0`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/1f9e0719526455a6a89751869281df91eabeb5c0) | `69a1977ec580e6ba598a9cab1710d63244863279` |
| Combined | `0db05e0f5a49d6fdc835db7ec771fe8855dd041a` | [`cdb03c2e8343c80c48ab60d268c9ad3013e3570b`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/cdb03c2e8343c80c48ab60d268c9ad3013e3570b) | `7f54895634b3bf85d5eb91c314f3c4068a6f431a` |

Measured commit IDs are retained as provenance, not as GitHub links: their original ancestry is not included in this repository. The historical frozen study continues to bind those measured IDs.

### Why the root README looks the same

The root `README.md` is unchanged at every measured head (Git blob `2ea9f8d6abd2e0e35efb41a5f383a4e2df0680f5`). The generated documentation is **`.aideal/treatments/README.md`**, selected explicitly by the AIDEAL evaluation configuration for README-only and Combined. Opening a branch homepage therefore shows the original library README; use the selected-README links above to inspect the actual treatment. The other conditions use the original evaluation-document snapshot. That snapshot is identified by hash in the shared package but omitted there because it contains machine-specific home paths.

The selected Generated README is an exact reused historical artifact, not a new README authored by this Codex proposal and not a repaired replacement. The same bytes are used in README-only and Combined.

## Shared setup, harness and audited results

The study package is pinned to AIDEAL commit **[`6f90fd658767`](https://github.com/ZhuochengShang/AIDEAL/commit/6f90fd658767d804cd232ba1cbf57148e7b87110)** on branch `evaluation/2026-09-21-rdpro-gpt-5.3-codex-five-conditions`:

- [Study guide](https://github.com/ZhuochengShang/AIDEAL/blob/6f90fd658767d804cd232ba1cbf57148e7b87110/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/README.md) and [audited results](https://github.com/ZhuochengShang/AIDEAL/blob/6f90fd658767d804cd232ba1cbf57148e7b87110/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/RESULTS.md).
- [Protocol/model limits and controller hashes](https://github.com/ZhuochengShang/AIDEAL/blob/6f90fd658767d804cd232ba1cbf57148e7b87110/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/protocol.json).
- [Shared trusted harness and offline test instructions](https://github.com/ZhuochengShang/AIDEAL/blob/6f90fd658767d804cd232ba1cbf57148e7b87110/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/harness/README.md).
- [Public task contracts](https://github.com/ZhuochengShang/AIDEAL/blob/6f90fd658767d804cd232ba1cbf57148e7b87110/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/public_tasks.json) and [artifact identities](https://github.com/ZhuochengShang/AIDEAL/blob/6f90fd658767d804cd232ba1cbf57148e7b87110/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/artifact_manifest.json).

The completed run resolved **40 audience units**, with **120 trusted controls** independently verified. Each condition has four microtasks and four puzzles. Within one repair, Original/Aliases/Combined passed 4/8; README-only/Hints-only passed 5/8. **No generated hint matched or was delivered**, so hints-only’s extra repaired success cannot be credited to hint content. Alias-name mentions do not prove wrapper execution. This single-trial pilot is not a broad effectiveness claim. The shared results distinguish first-attempt scores, repairs, context exposure and the $0.70922005 total usage-based cost.

## Review the source

Clone this repository, then switch to one dated condition branch. Authentication depends on the repository access settings; no visibility assumption is made here.

```sh
git clone https://github.com/ZhuochengShang/AIDEAL-RDPro.git
cd AIDEAL-RDPro
git switch --track origin/study/2026-09-21-gpt-5.3-codex/original
# To inspect another condition, for example:
git switch --track origin/study/2026-09-21-gpt-5.3-codex/readme-only
```

For exact source comparison or preparation of a new local evaluation, create separate named worktrees at the published snapshot SHAs. AIDEAL checks each configured branch and commit:

```sh
git worktree add -b reproduce/original ../rdpro-original 5ee41a6dc37ebe5f63bf7a3a0b74c5483460b9c8
git worktree add -b reproduce/readme-only ../rdpro-readme-only 41a302b39dd7d9260842827d22bf7fa48dadcad7
git worktree add -b reproduce/alias-only ../rdpro-alias-only 80ba93d32e9aff65fa12e730abb66a67663b9e35
git worktree add -b reproduce/error-hints-only ../rdpro-error-hints-only 1f9e0719526455a6a89751869281df91eabeb5c0
git worktree add -b reproduce/combined ../rdpro-combined cdb03c2e8343c80c48ab60d268c9ad3013e3570b
```

These commands only create source views; they do not run the benchmark. Full repetition still needs the omitted private benchmark/oracle/reference inputs, the original evaluation-document bytes and pinned runtime dependencies. The shared harness describes those requirements. Rebuild and freeze a new local configuration that binds these new branch/commit identities. Published snapshot IDs and local runtime paths differ from the historical freeze, so cloning alone cannot resume or reproduce that freeze.

## Provenance and licensing

This is an independent GitHub experiment copy of the measured source **trees**, not a republication of the complete upstream history or a claim of membership in an upstream GitHub fork network. All five snapshot trees retain their existing [Apache 2.0 LICENSE](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/5ee41a6dc37ebe5f63bf7a3a0b74c5483460b9c8/LICENSE), copyright files and source headers. The same upstream license is also provided as [LICENSE](LICENSE) on this navigation branch. Original local source histories remain unchanged. No API credentials, private expected answers, runtime binaries or local absolute study paths are included in this navigation metadata.
