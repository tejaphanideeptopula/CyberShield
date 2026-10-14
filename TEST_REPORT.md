# CyberShield verification report

## Environment and method

- Compiled all source files with the installed JDK 25 compiler using `javac -encoding UTF-8 -d .verify-bin ...`.
- Ran `com.cybershield.AlgorithmSelfTest` from the project root: **31 passed, 0 failed**.
- Rebuilt the Eclipse `bin` output from all current source files after finding stale classes, then ran dataset summary, case-insensitive CO1 KMP search, CO5 assignment evaluation, and each CO4–CO6 menu choice from that `bin` classpath with no unresolved-compilation errors.
- Ran the interactive `Main` through the CO4, CO5, and CO6 individual algorithm choices and returned to the main menu. The 3-SAT evaluator accepted three entered T/F values. Dataset startup and the other menu paths were also exercised in the prior integration run.
- Results below are the observed outputs from these runs. Runtime is one environment-specific sample, not a benchmark.

## Dataset inspection

| Observation | Actual result |
|---|---|
| CSV file | `dataset/final(2).csv` |
| Header columns | `Time, Protcol, Flag, Family, Clusters, SeddAddress, ExpAddress, BTC, USD, Netflow_Bytes, IPaddress, Threats, Port, Prediction` |
| Accepted records | 149,043 |
| Skipped malformed over-wide rows | 0 |
| Combined searchable corpus | 17,017,989 characters from all 149,043 loaded records; no fixed character cap |
| Load plus full search-text construction | 1,706,191,700 ns (about 1.706 s in this run; environment-dependent) |

The threat field frequency table printed from the actual records was: Blacklist 16,356; Bonet 16,523; DoS 12,731; NerisBonet 6,241; Port Scanning 15,910; SSH 24,726; Scan 12,568; Spam 23,207; UDP Scan 20,781. These are literal CSV values and are not independently verified labels. The CSV `Prediction` column was not treated as ground truth.

Reservoir sampling (seed `20261013`) returned these actual source rows:

```text
48,TCP,AF,Flyper,3,17dcMo4V,1DiCeTjB,30,125855,916,C,NerisBonet,5062,S
3,TCP,R,SamSam,1,1DA11mPS,1SYSTEMQ,22,18090,5086,C,Port Scanning,5066,S
21,UDP,ASF,APT,1,17dcMo4V,1DiCeTjB,3,591,899,A,Spam,5061,SS
```

## Regression checks

All checks below were executed by `com.cybershield.AlgorithmSelfTest` and passed.

| Area | Input / expected output | Actual result |
|---|---|---|
| Naive, KMP, Z, Rabin-Karp | `ba` in `ababa` → `[1,3]` | PASS; all returned `[1,3]` |
| KMP LPS | `ababaca` → `[0,0,1,2,3,0,1]` | PASS |
| Z separator edge | `$$` in `$a$$` → `[2]` | PASS |
| Rabin-Karp candidate check | `abd` in `abc` → no match | PASS |
| Matcher boundaries | longer-than-text and empty patterns | PASS; empty pattern returns no match |
| Aho-Corasick | `he`, `she`, `hers`, `his` in `ushers` | PASS; `she=1`, `he=1`, `hers=1` |
| Suffix array | `banana` → `[5,3,1,0,4,2]` | PASS |
| Kasai LCP | `banana`, LCP to next suffix → `[1,3,0,0,2,0]` | PASS |
| Suffix automaton | `banana` contains `ana`, not `apple` | PASS |
| Empty suffix input | suffix array of empty string → `[]` | PASS |
| Levenshtein | `kitten` → `sitting` = 3 | PASS |
| Damerau OSA | `ca` → `ac` = 1; `CA` → `ABC` = 3 | PASS; confirms restricted OSA semantics |
| Weighted edit distance | `abc` → `adc`, substitution cost 2 → 2 | PASS |
| Bitmask DP | minimum illustrative coverage cost | PASS; cost 10 |
| Ford-Fulkerson / Edmonds-Karp / Dinic | same six-node demo network | PASS; each max flow = 5 |
| Min-cut | same flow demo | PASS in console; edges `0->1(3)`, `0->2(2)`, capacity 5 |
| Bipartite matching | 3×3 compatibility example | PASS; matching size = 3 |
| 3-SAT | all-true assignment on the three-clause example | PASS; satisfied |
| 3-SAT enumeration and CLIQUE | same three clauses | PASS; satisfying assignment found, clique of size 3 exists |
| Clique/independent-set/cover relationship | clique `{0,1,2}` and complement graph | PASS; constructed cover verified |
| Vertex-cover approximation | illustrative dependency graph | PASS; actual cover `[0,1,2,3]`; every edge covered |
| Miller-Rabin | 2, 17, 21, 1,000,000,007 | PASS; `true, true, false, true` probable-prime results |
| Randomized QuickSort | fixed integer example vs `Arrays.sort` | PASS; `[1,2,3,4,5,6,7,8,9]`, arrays equal |
| Reservoir sampling | stream supplied CSV, k=3, fixed seed | PASS; three actual records returned |
| Parallel reduction | actual dataset record count vs sequential count | PASS; 149,043 equals 149,043 |
| Blelloch exclusive scan | `[3,1,7,0,4,1,6,3]` | PASS; `[0,3,4,11,11,15,16,22]` |
| Brent illustration | W=20, D=7, P=3 | PASS; lower bound `max(ceil(20/3),7)=7` |

Self-test summary from execution: `Self-tests passed: 31 / 31`.

The per-algorithm menu integration run printed the expected flow values (all three maximum-flow choices returned 5; matching returned 3; min-cut capacity was 5), SAT assignment result `true`, a 3-clause clique result `true`, verified vertex cover, all six CO6 outputs, and cleanly returned to the main menu after each CO.

## Per-algorithm result, complexity, and data status

`Expected` is the standard output for the stated small input; `Observed` is from the actual checks or console run above. Auxiliary space excludes the returned match lists unless the output says otherwise.

| Algorithm and purpose | Input; expected → observed | Status | Time / auxiliary space | Data use |
|---|---|---|---|---|
| Naive matching — baseline field search | `ba` in `ababa`; `[1,3]` → `[1,3]` | PASS | O(nm) / O(matches) | Dataset-backed searchable fields |
| KMP — one field term with guaranteed linear scan | Same; `[1,3]` → `[1,3]`; LPS `ababaca` → `[0,0,1,2,3,0,1]` | PASS | O(n+m) / O(m + matches) | Dataset-backed searchable fields |
| Z — prefix-based one-term search | Same; `[1,3]` → `[1,3]`; separator case `[2]` → `[2]` | PASS | O(n+m) / O(n+m) | Dataset-backed searchable fields |
| Rabin-Karp — rolling-hash search | Same; `[1,3]` → `[1,3]`; nonmatch verified | PASS | Average O(n+m), worst O(nm) / O(matches) | Dataset-backed searchable fields |
| Aho-Corasick — multiple field terms | Four terms in `ushers`; three total reported matches → `she=1, he=1, hers=1` | PASS | O(P+n+occurrences) expected / O(trie+outputs) | Dataset-backed searchable fields |
| Suffix array — suffix ordering | `banana`; `[5,3,1,0,4,2]` → same | PASS on regression input; full-corpus suffix-array runtime not measured | O(n log²n) / O(n) | Full 17,017,989-character derived corpus when selected |
| Kasai LCP — adjacent suffix similarity | `banana`; successor LCP `[1,3,0,0,2,0]` → same | PASS on regression input; full-corpus LCP runtime not measured | O(n) / O(n) | Full corpus when selected, after suffix-array construction |
| Suffix automaton — substring membership | `banana`; contains `ana`, excludes `apple` → same | PASS on regression input; full-corpus construction not measured | O(n) expected build, O(pattern) query / O(n) | Full corpus when selected |
| Levenshtein — typo/label distance | `kitten` to `sitting`; 3 → 3 | PASS | O(nm) / O(nm) | User-supplied strings / derived field labels |
| Damerau OSA — adjacent-swap distance | `ca` to `ac`; 1 → 1; `CA` to `ABC`; 3 → 3 | PASS | O(nm) / O(nm) | User-supplied strings / derived field labels |
| Weighted edit distance — custom operation costs | `abc` to `adc`, sub cost 2; 2 → 2 | PASS | O(nm) / O(nm) | Demonstration costs |
| Bitmask DP — minimum rule coverage | Five condition demo; minimum cost 10 → 10 | PASS | O(2^k r) / O(2^k) | Demonstration rules |
| Ford-Fulkerson — DFS augmenting flow | Six-node capacity matrix; 5 → 5 | PASS | O(E·maxFlow) for integer capacities / O(V²) | Demonstration network |
| Edmonds-Karp — BFS augmenting flow | Same matrix; 5 → 5 | PASS | O(VE²) / O(V²) | Demonstration network |
| Dinic — level graph and blocking flow | Same matrix; 5 → 5 | PASS | O(V²E) general / O(V²) | Demonstration network |
| Min-cut — residual reachability | Same matrix; capacity 5, edges `0->1`, `0->2` → capacity 5, those edges | PASS | O(V²) after max flow / O(V²) | Demonstration network |
| Bipartite matching — analyst/alert assignment | 3×3 compatibility matrix; 3 → 3 | PASS | O(VE) / O(V) beyond matrix | Demonstration compatibility |
| 3-SAT evaluation — check supplied assignment | Three clauses, all variables true; satisfied → true | PASS | O(total literals) / O(1) additional | Demonstration formula |
| 3-SAT brute force — find assignment | Same formula; satisfiable → satisfying assignment found | PASS | O(2^n·m) / O(n) | Demonstration formula |
| 3-SAT to CLIQUE — reduction witness | Three clauses, 9 occurrence vertices; size-3 clique expected → found | PASS | O(L²) graph construction plus exponential small search / O(L²) | Demonstration formula |
| Clique/complement/independent set/cover — relationship check | Clique `{0,1,2}`; cover in complement expected verified → verified | PASS | O(V²) matrix operations / O(V²) | Demonstration graph |
| Vertex-cover 2-approx — cover every dependency edge | Small 4-node graph; valid cover expected → `[0,1,2,3]`, all edges covered | PASS | O(V²E) / O(V²) | Demonstration graph |
| Miller-Rabin — probable-prime screening | 2, 17, 21, 1,000,000,007; T,T,F,T → T,T,F,T | PASS | O(rounds·log³N) bit-operation scale / O(log N) | Demonstration integers |
| Randomized QuickSort — sort with randomized pivots | Nine integers; ascending order → `[1,2,3,4,5,6,7,8,9]`; agrees with Java sort | PASS | Expected O(n log n), worst O(n²) / expected O(log n) recursion | Demonstration integers |
| Reservoir sampling — bounded-memory random row sample | CSV stream, k=3, seed 20261013; three rows → three actual rows printed | PASS | O(N) / O(k) | Actual CSV rows, streamed |
| Parallel reduction — count records by chunks | Dataset count 149,043; match sequential → 149,043 = 149,043 | PASS | O(N) work / O(tasks) task metadata beyond loaded rows | Actual loaded dataset |
| Blelloch scan — exclusive prefix sums | `[3,1,7,0,4,1,6,3]`; expected `[0,3,4,11,11,15,16,22]` → same | PASS | O(n) work, O(log n) depth / O(n) | Demonstration array |
| Brent theorem — work/depth lower bound | W=20, D=7, P=3; expected 7 → 7 idealized steps | PASS | O(1) / O(1) | Demonstration calculation |

## Scope and limits

CO4 capacities, CO5 formulas/graphs, and CO6 small sorting/primality/scan inputs are labelled educational examples. Parallel reduction counts the records already loaded by the application; it compares against the sequential count. Reservoir sampling is a separate streaming pass over the CSV and holds only its k selected rows. Miller-Rabin reports probable primes, not proofs. Dataset-derived results are descriptive of this one provided file and do not validate a real-world security detector.

The full-corpus suffix array and suffix automaton were not benchmarked on the 17-million-character corpus. Their implementations have substantial memory/runtime cost at this size; only their correctness on the recorded regression strings was verified. The startup corpus build completed successfully.
