# CyberShield – Enterprise Security Analytics Platform

An Eclipse Java console application for demonstrating string algorithms, suffix structures, dynamic programming, network flow, NP-completeness and approximation, randomized algorithms, and parallel reductions. Algorithms are educational demonstrations; results from the UGRansome CSV are not a claim of reliable real-world threat detection.

## Requirements and run

- Eclipse with JavaSE-25, or JDK 25 on the command line.
- No build tool or third-party dependency is required.
- Keep the supplied CSV at `dataset/final(2).csv` relative to the project root. Replace it there if needed; the loader does not use a machine-specific absolute path.
- In Eclipse, import the existing project and run `com.cybershield.Main` with the project root as the working directory.
- In PowerShell from the project root:

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)
java -cp bin com.cybershield.Main
java -cp bin com.cybershield.AlgorithmSelfTest
```

The application menu has dataset summary, CO1 through CO6, complexity notes, and exit. CO4, CO5, and CO6 provide a separate choice for every algorithm plus a run-all option. The 3-SAT assignment evaluator accepts three user-entered T/F values. Dataset summary prints the columns seen in the file, accepted/skipped counts, literal `Threats` category frequencies, and sample records.

## Architecture and module mapping

| Course outcome | Package / main classes | Contribution and selected use |
|---|---|---|
| CO1 | `co1.NaiveSearch`, `KMP`, `ZAlgorithm`, `RabinKarp`, `AhoCorasick` | Search selected categorical/identifier fields in a derived record string. KMP/Z provide deterministic linear single-pattern matching; Aho-Corasick handles several patterns. |
| CO2 | `co2.SuffixArray`, `KasaiLCP`, `SuffixAutomaton` | Order suffixes, inspect adjacent common prefixes, and test substring membership. The suffix array uses rank doubling with comparison sorting; it is **not SA-IS**. |
| CO3 | `co3.Levenshtein`, `DamerauLevenshtein`, `WeightedEditDistance`, `BitmaskDP` | Compare labels/strings and demonstrate small rule-coverage optimization. `DamerauLevenshtein` is the restricted optimal-string-alignment (OSA) variant. |
| CO4 | `co4.NetworkFlowModule` | Ford-Fulkerson, Edmonds-Karp, Dinic, residual min-cut, and bipartite matching on labelled illustrative capacities/compatibilities. |
| CO5 | `co5.ComplexityModule` | 3-SAT evaluation/enumeration, literal-occurrence compatibility graph and clique, complement relationships, and a 2-approximate vertex cover on small educational instances. |
| CO6 | `co6.RandomParallelModule` | Miller-Rabin probable-prime test, randomized QuickSort, streaming reservoir sampling, parallel count reduction, exclusive Blelloch scan, and a Brent lower-bound illustration. |
| Integration | `Main`, `DatasetLoader`, `SecurityRecord`, `AlgorithmSelfTest` | Menu, header-aware CSV mapping, the record model, and dependency-free regression checks. |

## Dataset notes

The checked-in file is a 14-column CSV. Its actual header is:

`Time, Protcol, Flag, Family, Clusters, SeddAddress, ExpAddress, BTC, USD, Netflow_Bytes, IPaddress, Threats, Port, Prediction`

The loader maps columns by normalized header name and explicitly handles the observed `Protcol` and `SeddAddress` misspellings. Empty/missing values become `N/A`; rows with more values than headers are skipped and reported. Quoted commas and escaped quotes are parsed. The data model retains all fields. CO1 derives searchable text only from Protocol, Flag, Family, SeedAddress, ExpAddress, IPaddress, Threats, and Port; it excludes numeric fields and `Prediction`. The `Threats` frequencies are literal source values, not independently validated labels. `Prediction` is displayed as a source field and is not taken as ground truth.

The current supplied file loaded **149,043 rows**, skipped **0 malformed over-wide rows**, and exposed the 14 headers above. CO1 and CO2 now build their combined searchable text from **every loaded record**, joining each record's selected descriptive fields with a newline. This is one concatenated text corpus; suffix structures do not represent separate per-record indexes. The corpus has no fixed character cap. Reservoir sampling separately reads the CSV data rows sequentially and uses fixed seed `20261013` for a reproducible example. Flow/matching/SAT graphs are intentionally demonstration-only rather than fabricated from CSV measurements.

## Algorithms and complexity

| Algorithm | Time | Auxiliary space | Application to this project |
|---|---:|---:|---|
| Naive matching | O(nm) | O(matches) | Simple baseline. |
| KMP / Z | O(n+m) | O(n+m) | Single-pattern search with predictable linear time. |
| Rabin-Karp | Average O(n+m), worst O(nm) | O(matches) | Hash filtering; verifies text when hashes agree. |
| Aho-Corasick | O(total pattern length + text + matches) expected with maps | O(trie states + outputs) | Multiple labels/signatures in a scan. |
| Rank-doubling suffix array | O(n log²n) in this comparison-sort implementation | O(n) | Suffix ordering; not SA-IS. |
| Kasai LCP | O(n) | O(n) | Adjacent suffix common-prefix lengths. |
| Suffix automaton | O(n) expected with hash-map transitions | O(n) states/transitions | Repeated substring membership queries. |
| Edit distance variants | O(nm) | O(nm) | Similarity between short identifiers/labels; OSA only allows local adjacent transposition. |
| Bitmask coverage DP | O(2^k r) | O(2^k) | Small rule/condition set only. |
| Ford-Fulkerson (integer capacities) | O(E·maxFlow) | O(V²) residual matrix | Demonstration communication capacity. |
| Edmonds-Karp | O(VE²) | O(V²) | BFS augmenting paths and stable polynomial bound. |
| Dinic (general bound) | O(V²E) | O(V²) | Level graph and blocking flow. |
| Bipartite DFS matching | O(VE) | O(V) beyond input | Analyst-to-alert compatibility demo. |
| 3-SAT enumeration | O(2^n·m) | O(n) | Exponential small-instance solver. |
| 3-SAT to CLIQUE construction | O(L²), L literal occurrences | O(L²) | Illustrates reduction, not direct threat detection. |
| 2-approx vertex cover | O(V²E) in this matrix implementation | O(V²) | Educational approximation on small dependency graphs. |
| Miller-Rabin | O(rounds·log³N) bit-operation scale | O(log N) | Probable-prime check; positive result is not proof. |
| Randomized QuickSort | Expected O(n log n), worst O(n²) | Expected O(log n) recursion | Random pivot changes running time, not sorted result. |
| Reservoir sampling | O(N) time, O(k) memory | O(k) | Uniform-size sample from a sequential record stream. |
| Parallel count reduction | O(N) total work | O(tasks) task metadata, besides records | Counts currently loaded dataset records and compares sequential total. |
| Blelloch exclusive scan | O(n) work, O(log n) depth | O(n) | Prefix sum demonstration; pads internally to a power of two. |

`N` is the number of streamed records, `n`/`m` are string or formula sizes as applicable, `k` is the sample size / DP condition count, `r` is the number of rules, and `V`/`E` are graph vertices/edges. Runtime measurements vary by hardware and JVM.

## Verification

Run `com.cybershield.AlgorithmSelfTest` for the dependency-free checks. On the supplied project/JDK, **29/29 checks passed**. The console integration run loaded 149,043 records, reported 0 skipped malformed rows, exercised the CO4–CO6 menus, and produced the outputs summarized in [`TEST_REPORT.md`](TEST_REPORT.md). Known OSA-vs-unrestricted edit-distance behavior, Z separator collision, Rabin-Karp collision verification, suffix order/LCP, and empty/longer patterns are covered.

## Viva material

See [`VIVA_GUIDE.md`](VIVA_GUIDE.md) for CO1–CO6 step-by-step explanations, rationale, alternatives, examples, complexity, class/method mapping, tests, and limitations.

## Suggested commit sequence

These are suggested future commits, not claims about existing Git history:

1. `fix: map UGRansome CSV by header and report malformed rows`
2. `fix: correct string matching edge cases and add regression checks`
3. `feat: add CO4 flow, min-cut, and matching demonstrations`
4. `feat: add CO5 satisfiability reductions and vertex-cover approximation`
5. `feat: add CO6 randomized and parallel algorithm examples`
6. `docs: add algorithm complexity, dataset notes, and viva guide`
