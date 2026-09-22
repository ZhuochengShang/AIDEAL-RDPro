# AIDEAL–RDPro: five measured source conditions

This repository holds the **RDPro/Beast library versions** used in the 2026-09-21 GPT-5.3-Codex study. The shared AIDEAL controller, portable results and harness live in [ZhuochengShang/AIDEAL](https://github.com/ZhuochengShang/AIDEAL/tree/evaluation/2026-09-21-rdpro-gpt-5.3-codex-five-conditions). There is **one RDPro repository with five condition branches**, not five separate forks.

`main` is a navigation-only orphan branch. It is intentionally separate from the measured source history; this landing page adds no commit to any of the five measured heads. [study.json](study.json) records full commit/tree IDs, artifact hashes and links.

## Open a measured version

| Condition | Dated source branch | Exact commit | Treatment files |
|---|---|---|---|
| Original | [original](https://github.com/ZhuochengShang/AIDEAL-RDPro/tree/study/2026-09-21-gpt-5.3-codex/original) | [`547f7f912131`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/547f7f912131a8032f6b5d26991415a5faf05cef) | No additive treatment; pinned baseline |
| Generated README only | [readme-only](https://github.com/ZhuochengShang/AIDEAL-RDPro/tree/study/2026-09-21-gpt-5.3-codex/readme-only) | [`7ab580c744db`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/7ab580c744dbf4d02a0ced29bd81b6cc4d0fbb72) | [selected README](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/7ab580c744dbf4d02a0ced29bd81b6cc4d0fbb72/.aideal/treatments/README.md) |
| Aliases only | [alias-only](https://github.com/ZhuochengShang/AIDEAL-RDPro/tree/study/2026-09-21-gpt-5.3-codex/alias-only) | [`dcaff7fbe2ce`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/dcaff7fbe2ce704707556b16d3b3e9e6a17a78e1) | [Scala wrappers](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/dcaff7fbe2ce704707556b16d3b3e9e6a17a78e1/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/AidealAliases0001.scala) · [alias interface](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/dcaff7fbe2ce704707556b16d3b3e9e6a17a78e1/.aideal/treatments/ALIASES.md) |
| Error hints only | [error-hints-only](https://github.com/ZhuochengShang/AIDEAL-RDPro/tree/study/2026-09-21-gpt-5.3-codex/error-hints-only) | [`bc55f6b312c0`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/bc55f6b312c048bd6110749e8def2fcaece74968) | [error hints](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/bc55f6b312c048bd6110749e8def2fcaece74968/.aideal/treatments/error_hints.json) |
| Combined | [combined](https://github.com/ZhuochengShang/AIDEAL-RDPro/tree/study/2026-09-21-gpt-5.3-codex/combined) | [`0db05e0f5a49`](https://github.com/ZhuochengShang/AIDEAL-RDPro/commit/0db05e0f5a49d6fdc835db7ec771fe8855dd041a) | [selected README](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/0db05e0f5a49d6fdc835db7ec771fe8855dd041a/.aideal/treatments/README.md) · [Scala wrappers](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/0db05e0f5a49d6fdc835db7ec771fe8855dd041a/cg/src/main/scala/edu/ucr/cs/bdlab/beast/geolite/AidealAliases0001.scala) · [alias interface](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/0db05e0f5a49d6fdc835db7ec771fe8855dd041a/.aideal/treatments/ALIASES.md) · [error hints](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/0db05e0f5a49d6fdc835db7ec771fe8855dd041a/.aideal/treatments/error_hints.json) |

Every condition starts from baseline **`547f7f912131a8032f6b5d26991415a5faf05cef`**. README-only and hints-only retain the original library code. Aliases-only adds forwarding wrappers without editing baseline source files; Combined uses the same wrappers, selected README and hints. The separate source-refactor experiment is excluded.

### Why the root README looks the same

The root `README.md` is unchanged at every measured head (Git blob `2ea9f8d6abd2e0e35efb41a5f383a4e2df0680f5`). The generated documentation is **`.aideal/treatments/README.md`**, selected explicitly by the AIDEAL evaluation configuration for README-only and Combined. Opening a branch homepage therefore shows the original library README; use the selected-README links above to inspect the actual treatment. The other conditions use the original evaluation-document snapshot. That snapshot is identified by hash in the shared package but omitted there because it contains machine-specific home paths.

The selected Generated README is an exact reused historical artifact, not a new README authored by this Codex proposal and not a repaired replacement. The same bytes are used in README-only and Combined.

## Shared setup, harness and audited results

The study package is pinned to AIDEAL commit **[`969f1dd78962`](https://github.com/ZhuochengShang/AIDEAL/commit/969f1dd7896226952669a45199bbf1efa2f17d39)** on branch `evaluation/2026-09-21-rdpro-gpt-5.3-codex-five-conditions`:

- [Study guide](https://github.com/ZhuochengShang/AIDEAL/blob/969f1dd7896226952669a45199bbf1efa2f17d39/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/README.md) and [audited results](https://github.com/ZhuochengShang/AIDEAL/blob/969f1dd7896226952669a45199bbf1efa2f17d39/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/RESULTS.md).
- [Protocol/model limits and controller hashes](https://github.com/ZhuochengShang/AIDEAL/blob/969f1dd7896226952669a45199bbf1efa2f17d39/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/protocol.json).
- [Shared trusted harness and offline test instructions](https://github.com/ZhuochengShang/AIDEAL/blob/969f1dd7896226952669a45199bbf1efa2f17d39/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/harness/README.md).
- [Public task contracts](https://github.com/ZhuochengShang/AIDEAL/blob/969f1dd7896226952669a45199bbf1efa2f17d39/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/public_tasks.json) and [artifact identities](https://github.com/ZhuochengShang/AIDEAL/blob/969f1dd7896226952669a45199bbf1efa2f17d39/studies/rdpro/2026-09-21-gpt-5.3-codex-five-conditions/artifact_manifest.json).

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

For exact source comparison or preparation of a new local evaluation, create separate named worktrees at the measured SHAs. AIDEAL checks each configured branch and commit:

```sh
git worktree add -b reproduce/original ../rdpro-original 547f7f912131a8032f6b5d26991415a5faf05cef
git worktree add -b reproduce/readme-only ../rdpro-readme-only 7ab580c744dbf4d02a0ced29bd81b6cc4d0fbb72
git worktree add -b reproduce/alias-only ../rdpro-alias-only dcaff7fbe2ce704707556b16d3b3e9e6a17a78e1
git worktree add -b reproduce/error-hints-only ../rdpro-error-hints-only bc55f6b312c048bd6110749e8def2fcaece74968
git worktree add -b reproduce/combined ../rdpro-combined 0db05e0f5a49d6fdc835db7ec771fe8855dd041a
```

These commands only create source views; they do not run the benchmark. Full repetition still needs the omitted private benchmark/oracle/reference inputs, the original evaluation-document bytes and pinned runtime dependencies. The shared harness describes those requirements. Rebuild and freeze a new local configuration; absolute runtime paths are part of the historical identity, so cloning alone does not reproduce the old freeze.

## Provenance and licensing

This is an independent GitHub experiment copy that preserves the measured RDPro/Beast source history. It does not assert membership in an upstream GitHub fork network. The five measured branch heads preserve their existing source/license history and [Apache 2.0 LICENSE](https://github.com/ZhuochengShang/AIDEAL-RDPro/blob/547f7f912131a8032f6b5d26991415a5faf05cef/LICENSE). The same upstream license is also provided as [LICENSE](LICENSE) on this navigation branch. Existing copyright/license headers remain with the source. The navigation-only `main` does not rewrite those trees. No API credentials, private expected answers, runtime binaries or local absolute study paths are included in this navigation metadata.
