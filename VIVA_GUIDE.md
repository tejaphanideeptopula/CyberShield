# CyberShield viva guide (CO1–CO6)

The platform demonstrates algorithms on selected UGRansome fields where text/data processing fits the method. CO4/CO5 graphs are small labelled examples. Nothing here establishes dependable production threat detection.

## CO1 — String algorithms

**Problem and connection.** Find a known field value or a list of categorical/identifier terms in the record text assembled by `SecurityRecord.toSearchText()`. This derived text includes Protocol, Flag, Family, SeedAddress, ExpAddress, IPaddress, Threats, and Port. Prediction and numeric fields are excluded.

**Choice and alternatives.** KMP is a good primary single-pattern matcher because it guarantees O(n+m) after the LPS preprocessing; Aho-Corasick is the choice when several terms are checked together. Naive matching is easiest as a baseline, Z is another linear single-pattern method, and Rabin-Karp is useful when hash-based screening is convenient but has O(nm) worst-case behavior. No matcher here determines whether a value is malicious.

**How it works.** Naive tries each text position. KMP follows its LPS table after mismatches instead of restarting. Z computes the common prefix between the pattern and suffixes of `pattern + separator + text`; the separator is an integer outside Java's `char` range. Rabin-Karp updates a rolling hash and checks actual characters on a hash match. Aho-Corasick builds a trie, failure links, and inherited outputs, then scans once.

**Example.** Searching `ba` in `ababa` returns UTF-16 character positions `[1, 3]`. Searching `$$` in `$a$$` returns `[2]` even though `$` used to be an unsafe separator.

**Code, complexity, tests, limits.** `NaiveSearch.search`, `KMP.search`/`buildLPS`, `ZAlgorithm.search`, `RabinKarp.search`, and `AhoCorasick.addPattern`/`build`/`search`. KMP/Z are O(n+m), auxiliary O(n+m); Naive is O(nm), Rabin-Karp average O(n+m)/worst O(nm), Aho-Corasick is linear in patterns, text, and reported matches. Tested overlapping matches, LPS, suffix outputs, empty/too-long patterns, separator collision, and Rabin-Karp verification. Positions count Java UTF-16 code units, not Unicode grapheme clusters. CO1 scans a single newline-separated searchable corpus made from every loaded record, with no fixed character cap.

## CO2 — Suffix structures

**Problem and connection.** Index a string once to inspect suffix order, common prefixes, and repeated substrings; use an automaton for repeated substring membership queries.

**Choice and alternatives.** Rank-doubling suffix array plus Kasai LCP is straightforward to inspect and verify. A suffix automaton is convenient for membership queries. The suffix-array code is a rank-doubling algorithm using comparison sorting, not SA-IS. No suffix tree or SA-IS implementation is claimed.

**How it works.** The suffix array repeatedly sorts suffix starts by their current rank and the rank one block later. Kasai uses the inverse rank and the previous common-prefix length to compute LCP values; this implementation stores each suffix's LCP with the *next* suffix, so the final slot is zero. A suffix automaton extends states and suffix links as each character arrives.

**Example.** For `banana`, suffix order is `[5,3,1,0,4,2]`; next-suffix LCP is `[1,3,0,0,2,0]`. The automaton contains `ana`, not `apple`.

**Code, complexity, tests, limits.** `SuffixArray.build`, `KasaiLCP.buildLCP`, `SuffixAutomaton.contains`. Rank-doubling is O(n log² n) here; Kasai O(n); automaton construction/query is expected linear with `HashMap` transitions. Tested `banana`, LCP, membership, and empty text. In the application, structures process the combined searchable fields from all loaded rows; the suffix array indexes one newline-separated string, not separate row objects. Removing the cap increases runtime and memory use for suffix-array construction.

## CO3 — Dynamic programming

**Problem and connection.** Quantify edits between short protocol/family/address labels, optionally weighting substitutions, or select a low-cost set of illustrative rules covering a small condition set.

**Choice and alternatives.** Levenshtein is the standard insert/delete/substitute distance. Damerau's local transposition option is useful when adjacent characters are swapped. This implementation is the optimal-string-alignment (OSA) restricted variant; it is not unrestricted Damerau-Levenshtein. Weighted edit distance is used only when edit costs are explicitly provided. Bitmask DP is suitable for few conditions; its exponential state space makes it unsuitable for large rule sets.

**How it works.** Each DP cell chooses among deletion, insertion, and match/substitution. OSA also permits a one-step adjacent transposition from two earlier cells. Weighted edit distance changes operation costs. Bitmask DP stores the cheapest cost for each covered-condition mask and relaxes each rule.

**Example.** `kitten` → `sitting` has Levenshtein distance 3. `ca` → `ac` has OSA distance 1, while `CA` → `ABC` is 3 under OSA (the unrestricted result is 2). A substitution from `abc` to `adc` costs 2 when substitution cost is 2.

**Code, complexity, tests, limits.** `Levenshtein.calculate`, `DamerauLevenshtein.calculate`, `WeightedEditDistance.calculate`, and `BitmaskDP.solve`. Edit-distance methods use O(nm) time and table space; bitmask DP is O(2^k r) time / O(2^k) space. The first three cases above are in the test harness. Labels are compared literally and costs/condition examples are illustrative; there is no language-aware normalization.

## CO4 — Network flow and matching

**Problem and connection.** Demonstrate a maximum communication flow from a source node to a sink, a minimum cut, and assignment of analysts to compatible alerts.

**Choice and alternatives.** Ford-Fulkerson shows residual augmenting paths with DFS; Edmonds-Karp uses BFS for a polynomial bound; Dinic uses level graphs and blocking flow. The examples run the same capacity matrix through all three, making results comparable. Bipartite matching uses DFS augmenting paths because the compatibility example is small.

**How it works.** Residual capacity records unused forward capacity and reverse cancellation capacity. Each augmenting path adds its bottleneck to total flow. After max flow, nodes reachable from the source in the residual graph define the source side of a min cut. Matching recursively reroutes an earlier assignment when that allows a new analyst-alert pair.

**Example and actual output.** The labelled six-node demo yields max flow 5 from Ford-Fulkerson, Edmonds-Karp, and Dinic. The cut edges are `0->1(3)` and `0->2(2)`, capacity 5. The 3×3 compatibility matrix yields a matching of 3.

**Code, complexity, tests, limits.** `NetworkFlowModule.fordFulkerson`, `edmondsKarp`, `dinic`, `maximumMatching`; min-cut extraction is internal to `run`. Complexity: Ford-Fulkerson O(E·maxFlow) for integer capacities, Edmonds-Karp O(VE²), general Dinic O(V²E), matching O(VE). All use an adjacency/residual matrix in O(V²) space. Tests assert all three flows and the matching. Capacities and analyst compatibility are demonstrations, not dataset-derived.

## CO5 — NP-completeness and approximation

**Problem and connection.** Explain a small Boolean satisfiability instance, its compatibility-graph clique reduction, graph-complement relationships, and an approximate cover for a dependency graph.

**Choice and alternatives.** Brute-force 3-SAT makes exponential growth visible and is limited to small variable counts. The reduction illustrates a theoretical relationship; it is not a malware detection method. The edge-picking vertex-cover algorithm is easy to explain and guarantees a cover no larger than twice optimum. Exact vertex cover is computationally hard in general.

**How it works.** The menu accepts three T/F values and evaluates each 3-literal clause; brute force checks assignments. The reduction creates one vertex per literal occurrence and connects only non-contradictory literals in different clauses. A clique with one compatible choice per clause yields a satisfying assignment. A clique in G is an independent set in complement(G); complementing an independent set gives a vertex cover in that same graph. The approximation picks an uncovered edge, selects both ends, and repeats.

**Example and actual output.** The three-clause example is satisfied by `x1=x2=x3=true`; brute force finds a satisfying assignment and the compatibility graph has a clique of size three. The complement graph check is true. The illustrative 2-approx cover is `[0,1,2,3]`, and every edge is covered.

**Code, complexity, tests, limits.** `ComplexityModule.evaluate`, `solve3Sat`, `buildLiteralCompatibilityGraph`, `hasClauseClique`, `complement`, `vertexCover2Approx`, and `coversAll`. Enumeration is O(2^n·m); the reduction graph uses O(L²) time/space; the matrix cover implementation is O(V²E) time and O(V²) space. Assignment, clique, and coverage checks passed. These are tiny educational graphs and do not analyze dataset malware.

## CO6 — Randomized and parallel algorithms

**Problem and connection.** Show probabilistic primality screening, a randomized but correct sorting algorithm, memory-light record sampling, parallel reduction, scan, and a parallel-time lower bound.

**Choice and alternatives.** Miller-Rabin is compact and works on `BigInteger`; a positive answer is probable, not proof. Randomized QuickSort keeps the correctness contract while randomizing pivot behavior; Java's sort is used as a comparison oracle. Reservoir sampling is preferred over loading all raw CSV rows to sample a stream. A simple executor count demonstrates reduction without complex shared state.

**How it works.** Miller-Rabin decomposes n−1 into an odd component times a power of two and checks repeated modular squaring for witness bases. QuickSort chooses a random pivot and partitions. Reservoir sampling keeps the first k records, then replaces a random slot with probability k/i for record i. Parallel count tasks return local counts, which are summed. Blelloch scan uses up-sweep/down-sweep for an exclusive prefix sum. Brent's lower bound says idealized parallel time is at least max(W/P,D), where W is work and D the critical path.

**Example and actual output.** Miller-Rabin reported 2, 17, and 1,000,000,007 probable prime and 21 composite at certainty 20. Randomized QuickSort returned `[1,2,3,4,5,6,7,8,9]`, matching `Arrays.sort`. With seed 20261013, reservoir sampling returned three actual CSV rows. Parallel count and sequential count both returned 149,043. Exclusive scan of `[3,1,7,0,4,1,6,3]` is `[0,3,4,11,11,15,16,22]`. For W=20, D=7, P=3, the lower bound is 7 idealized steps.

**Code, complexity, tests, limits.** `RandomParallelModule.isProbablePrime`, `quickSort`, `reservoirSample`, `parallelCount`, and `exclusiveBlelloch`; Brent's calculation is printed by `run`. Miller-Rabin uses O(rounds·log³N) bit-operation scale, QuickSort expected O(n log n)/worst O(n²), reservoir O(N) time/O(k) memory, scan O(n) work/O(log n) depth. All examples passed. Miller-Rabin's error bound assumes independent uniform bases; this demonstration is reproducibly seeded. Parallel tasks count loaded records held in memory; reservoir sampling alone rereads the raw CSV as a stream. Brent's bound assumes an idealized fork-join model.

## CO comparison at a glance

| CO | Selected algorithm(s) | Alternatives | Selection rationale | Complexity headline | CyberShield use |
|---|---|---|---|---|---|
| CO1 | KMP; Aho-Corasick for many patterns | Naive, Z, Rabin-Karp | Linear single-pattern guarantee; one scan for a pattern set | O(n+m) / O(text + patterns + matches) | Search selected categorical/identifier fields |
| CO2 | Rank-doubling suffix array + Kasai; suffix automaton | SA-IS and suffix trees are not present | Understandable sort/index and substring membership | O(n log²n), O(n), expected O(n) | Examine repeated substrings in bounded text |
| CO3 | Levenshtein/OSA; bitmask DP for few flags | Weighted distance where costs unknown | Standard edit distance; small state coverage demo | O(nm); O(2^k r) | Compare labels and model small rule selection |
| CO4 | Edmonds-Karp for predictable demo; all three compared | DFS Ford-Fulkerson, Dinic | Teach residual paths and compare methods | O(VE²), O(V²E) | Illustrative communication routing and assignment |
| CO5 | Brute-force 3-SAT, reduction, 2-approx cover | Exact exponential solvers | Expose complexity and a simple guarantee | O(2^n m); O(V²E) cover demo | Security dependency / compatibility teaching example |
| CO6 | Miller-Rabin, randomized QuickSort, reservoir, parallel count, scan | Deterministic primality proof/full in-memory sampling | Demonstrate probability, memory bounds, and parallel primitives | Probabilistic / expected / O(N) stream / O(n) scan work | Sample dataset and explain concurrent aggregation |
