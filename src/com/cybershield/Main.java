package com.cybershield;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import com.cybershield.co1.AhoCorasick;
import com.cybershield.co1.KMP;
import com.cybershield.co1.NaiveSearch;
import com.cybershield.co1.RabinKarp;
import com.cybershield.co1.ZAlgorithm;

import com.cybershield.co2.KasaiLCP;
import com.cybershield.co2.SuffixArray;
import com.cybershield.co2.SuffixAutomaton;

import com.cybershield.co3.BitmaskDP;
import com.cybershield.co3.DamerauLevenshtein;
import com.cybershield.co3.Levenshtein;
import com.cybershield.co3.WeightedEditDistance;
import com.cybershield.co4.NetworkFlowModule;
import com.cybershield.co5.ComplexityModule;
import com.cybershield.co6.RandomParallelModule;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static final String DATASET =
            "dataset/final(2).csv";

    static List<SecurityRecord> records =
            new ArrayList<>();

    static String corpus = "";

    public static void main(String[] args) {

        loadData();

        while (true) {

            System.out.println();
            System.out.println("==============================================");
            System.out.println("       CYBERSHIELD SECURITY ANALYTICS");
            System.out.println("==============================================");
            System.out.println("Dataset : UGRansome");
            System.out.println("Records : " + records.size());
            System.out.println("----------------------------------------------");
            System.out.println("1. Dataset loading and summary");
            System.out.println("2. CO1 - String Algorithms");
            System.out.println("3. CO2 - Suffix Structures");
            System.out.println("4. CO3 - Dynamic Programming");
            System.out.println("5. CO4 - Network Flow and Matching");
            System.out.println("6. CO5 - NP-Completeness and Approximation");
            System.out.println("7. CO6 - Randomized and Parallel Algorithms");
            System.out.println("8. Complexity comparison and test results");
            System.out.println("9. Exit");
            System.out.println("==============================================");

            int choice = inputInt("Enter choice: ");

            switch (choice) {

                case 1:
                    datasetInfo();
                    samples();
                    break;
                case 2:
                    co1Menu();
                    break;
                case 3:
                    co2Menu();
                    break;
                case 4:
                    co3Menu();
                    break;
                case 5:
                    co4Menu();
                    break;
                case 6:
                    co5Menu();
                    break;
                case 7:
                    co6Menu();
                    break;
                case 8:
                    complexitySummary();
                    break;
                case 9:
                    System.out.println(
                            "CyberShield closed."
                    );
                    return;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static List<String> datasetLines() {
        List<String> lines = new ArrayList<>();
        for (SecurityRecord r : records) lines.add(r.toSearchText());
        return lines;
    }

    private static void complexitySummary() {
        System.out.println("\nComplexity and test notes (space excludes returned result storage unless stated):");
        System.out.println("CO1: Naive O(nm); KMP/Z O(n+m); Rabin-Karp average O(n+m), worst O(nm); Aho-Corasick O(total pattern length + text + matches).");
        System.out.println("CO2: doubling suffix array O(n log^2 n) here (comparison sort each round); Kasai O(n); suffix automaton O(n) expected with HashMap transitions.");
        System.out.println("CO3: edit distances O(nm) time and O(nm) table space; bitmask rule coverage O(2^k*r).");
        System.out.println("CO4: see module results; capacities and compatibility are illustrative, not dataset-derived.");
        System.out.println("CO5: 3-SAT brute force O(2^n*m); 2-approx cover O(V^2*E) with adjacency matrix; graphs are illustrative.");
        System.out.println("CO6: Miller-Rabin O(rounds*log^3 N) bit-operation scale; reservoir O(k) memory; parallel demo verifies record count; Blelloch O(n) work.");
        System.out.println("Dataset integration: CSV summary, field-aware string search and reservoir sample use actual rows; flow/SAT examples are demonstrations.");
    }

    private static void co4Menu() {
        while (true) {
            System.out.println("\nCO4 NETWORK FLOW AND MATCHING");
            System.out.println("1. Ford-Fulkerson (DFS maximum flow)");
            System.out.println("2. Edmonds-Karp (BFS maximum flow)");
            System.out.println("3. Dinic's maximum flow");
            System.out.println("4. Bipartite analyst-alert matching");
            System.out.println("5. Minimum cut (after maximum flow)");
            System.out.println("6. Run all CO4 algorithms");
            System.out.println("7. Back");
            int choice = inputInt("Enter choice: ");
            if (choice == 7) return;
            if (choice == 6) NetworkFlowModule.run();
            else if (choice >= 1 && choice <= 5) NetworkFlowModule.runChoice(choice);
            else System.out.println("Invalid choice.");
        }
    }
    private static void co5Menu() {
        while (true) {
            System.out.println("\nCO5 NP-COMPLETENESS AND APPROXIMATION");
            System.out.println("1. Evaluate a supplied 3-SAT assignment (three T/F values)");
            System.out.println("2. Brute-force 3-SAT solver");
            System.out.println("3. 3-SAT to CLIQUE reduction");
            System.out.println("4. CLIQUE / INDEPENDENT SET / VERTEX COVER relationships");
            System.out.println("5. Vertex Cover 2-approximation");
            System.out.println("6. Run all CO5 algorithms");
            System.out.println("7. Back");
            int choice = inputInt("Enter choice: ");
            if (choice == 7) return;
            if (choice == 6) ComplexityModule.run();
            else if (choice == 1) {
                boolean[] assignment = {false, inputBoolean("x1 (T/F): "), inputBoolean("x2 (T/F): "), inputBoolean("x3 (T/F): ")};
                ComplexityModule.runEvaluation(assignment);
            }
            else if (choice >= 2 && choice <= 5) ComplexityModule.runChoice(choice);
            else System.out.println("Invalid choice.");
        }
    }
    private static void co6Menu() {
        while (true) {
            System.out.println("\nCO6 RANDOMIZED AND PARALLEL ALGORITHMS");
            System.out.println("1. Miller-Rabin primality test (Monte Carlo)");
            System.out.println("2. Randomized QuickSort (Las Vegas)");
            System.out.println("3. Reservoir sample actual CSV rows");
            System.out.println("4. Parallel reduction: count dataset records");
            System.out.println("5. Blelloch exclusive prefix scan");
            System.out.println("6. Brent's theorem illustration");
            System.out.println("7. Run all CO6 algorithms");
            System.out.println("8. Back");
            int choice = inputInt("Enter choice: ");
            if (choice == 8) return;
            if (choice == 7) RandomParallelModule.run(DATASET, datasetLines());
            else if (choice == 4) RandomParallelModule.runChoice(choice, DATASET, datasetLines());
            else if (choice >= 1 && choice <= 6) RandomParallelModule.runChoice(choice, DATASET, java.util.Collections.emptyList());
            else System.out.println("Invalid choice.");
        }
    }

    // =====================================================
    // DATASET
    // =====================================================

    static void loadData() {

        System.out.println();
        System.out.println(
                "Loading UGRansome dataset..."
        );

        long loadStart = System.nanoTime();
        records =
                DatasetLoader.load(DATASET);

        corpus =
                buildCorpus();
        long loadElapsed = System.nanoTime() - loadStart;

        System.out.println(
                "Records loaded : " +
                records.size()
        );

        System.out.println(
                "Corpus length  : " +
                corpus.length()
        );
        System.out.println("Load and search-text build time: " + loadElapsed + " ns (environment-dependent)");
    }

    static String buildCorpus() {

        StringBuilder sb =
                new StringBuilder();

        for (SecurityRecord r : records) {
            String text =
                    r.toSearchText();
            sb.append(text)
                    .append('\n');
        }

        return sb.toString();
    }

    // =====================================================
    // CO1
    // =====================================================

    static void co1Menu() {

        while (true) {

            System.out.println();
            System.out.println("================================");
            System.out.println("       CO1 STRING MATCHING");
            System.out.println("================================");
            System.out.println("1. Naive Search");
            System.out.println("2. KMP");
            System.out.println("3. Z Algorithm");
            System.out.println("4. Rabin-Karp");
            System.out.println("5. Aho-Corasick");
            System.out.println("6. Back");
            System.out.println("================================");

            int choice =
                    inputInt("Enter choice: ");

            switch (choice) {

                case 1:
                    singleSearch("Naive");
                    break;

                case 2:
                    singleSearch("KMP");
                    break;

                case 3:
                    singleSearch("Z");
                    break;

                case 4:
                    singleSearch("Rabin-Karp");
                    break;

                case 5:
                    ahoCorasick();
                    break;

                case 6:
                    return;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    static void singleSearch(
            String algorithm) {

        String pattern =
                inputText(
                        "Enter security pattern: "
                );

        if (pattern.isEmpty()) {
            System.out.println(
                    "Pattern cannot be empty."
            );
            return;
        }

        String searchText = corpus.toLowerCase(Locale.ROOT);
        pattern = pattern.toLowerCase(Locale.ROOT);
        long start =
                System.nanoTime();

        List<Integer> result;

        switch (algorithm) {

            case "Naive":
                result =
                        NaiveSearch.search(
                                searchText,
                                pattern
                        );
                break;

            case "KMP":
                result =
                        KMP.search(
                                searchText,
                                pattern
                        );
                break;

            case "Z":
                result =
                        ZAlgorithm.search(
                                searchText,
                                pattern
                        );
                break;

            default:
                result =
                        RabinKarp.search(
                                searchText,
                                pattern
                        );
        }

        long end =
                System.nanoTime();

        System.out.println();
        System.out.println(
                "========== RESULT =========="
        );
        System.out.println(
                "Algorithm : " + algorithm
        );
        System.out.println(
                "Pattern   : " + pattern
        );
        System.out.println(
                "Matches   : " + result.size()
        );
        System.out.println(
                "Time      : " +
                (end - start) +
                " ns"
        );

        if (!result.isEmpty()) {

            System.out.println(
                    "First positions:"
            );

            for (int i = 0;
                 i < Math.min(10, result.size());
                 i++) {

                System.out.println(
                        result.get(i)
                );
            }
        }
    }

    static void ahoCorasick() {
    String input = inputText("Enter patterns separated by comma: ");

    if (input.isEmpty()) {
        return;
    }

    AhoCorasick ac = new AhoCorasick();

    String[] patterns = input.split(",");

    List<String> cleanPatterns = new ArrayList<>();

    for (String p : patterns) {
        String pattern = p.trim().toLowerCase();

        if (!pattern.isEmpty()) {
            cleanPatterns.add(pattern);
            ac.addPattern(pattern);
        }
    }

    long start = System.nanoTime();

    ac.build();

    Map<String, Integer> totalResult = new java.util.LinkedHashMap<>();

    for (String pattern : cleanPatterns) {
        totalResult.put(pattern, 0);
    }

    for (SecurityRecord r : records) {
        String text = r.toSearchText().toLowerCase();

        Map<String, Integer> result = ac.search(text);

        for (Map.Entry<String, Integer> e : result.entrySet()) {
            totalResult.put(
                e.getKey(),
                totalResult.getOrDefault(e.getKey(), 0) + e.getValue()
            );
        }
    }

    long end = System.nanoTime();

    System.out.println();
    System.out.println("======= AHO-CORASICK =======");

    for (Map.Entry<String, Integer> e : totalResult.entrySet()) {
        System.out.println(e.getKey() + " -> " + e.getValue());
    }

    System.out.println("Records searched : " + records.size());
    System.out.println("Time: " + (end - start) + " ns");
}

    // =====================================================
    // CO2
    // =====================================================

    static void co2Menu() {

        while (true) {

            System.out.println();
            System.out.println("================================");
            System.out.println("   CO2 ADVANCED STRING ALGORITHMS");
            System.out.println("================================");
            System.out.println("1. Suffix Array");
            System.out.println("2. Kasai LCP");
            System.out.println("3. Suffix Automaton");
            System.out.println("4. Back");
            System.out.println("================================");

            int choice =
                    inputInt("Enter choice: ");

            switch (choice) {

                case 1:
                    suffixArray();
                    break;

                case 2:
                    kasai();
                    break;

                case 3:
                    suffixAutomaton();
                    break;

                case 4:
                    return;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    static void suffixArray() {

        System.out.println(
                "Building Suffix Array..."
        );

        long start =
                System.nanoTime();

        int[] sa =
                SuffixArray.build(corpus);

        long end =
                System.nanoTime();

        System.out.println();
        System.out.println(
                "========== SUFFIX ARRAY =========="
        );

        System.out.println(
                "Total suffixes: " +
                sa.length
        );

        int limit =
                Math.min(10, sa.length);

        for (int i = 0;
             i < limit;
             i++) {

            int index = sa[i];

            String suffix =
                    corpus.substring(index);

            if (suffix.length() > 80) {
                suffix =
                        suffix.substring(0, 80)
                                + "...";
            }

            System.out.println(
                    i + " -> " +
                    index + " : " +
                    suffix
            );
        }

        System.out.println(
                "Time: " +
                (end - start) +
                " ns"
        );
    }

    static void kasai() {

        System.out.println(
                "Building Suffix Array..."
        );

        int[] sa =
                SuffixArray.build(corpus);

        long start =
                System.nanoTime();

        int[] lcp =
                KasaiLCP.buildLCP(
                        corpus,
                        sa
                );

        long end =
                System.nanoTime();

        System.out.println();
        System.out.println(
                "========== KASAI LCP =========="
        );

        int limit =
                Math.min(10, sa.length);

        for (int i = 0;
             i < limit;
             i++) {

            System.out.println(
                    "SA[" + i + "] = " +
                    sa[i] +
                    "   LCP = " +
                    lcp[i]
            );
        }

        System.out.println(
                "Time: " +
                (end - start) +
                " ns"
        );
    }

    static void suffixAutomaton() {

        long start =
                System.nanoTime();

        SuffixAutomaton sam =
                new SuffixAutomaton(corpus);

        long end =
                System.nanoTime();

        String pattern =
                inputText(
                        "Enter pattern to search: "
                );

        boolean found =
                sam.contains(pattern);

        System.out.println();
        System.out.println(
                "======= SUFFIX AUTOMATON ======="
        );

        System.out.println(
                "States : " +
                sam.getStateCount()
        );

        System.out.println(
                "Pattern: " +
                pattern
        );

        System.out.println(
                "Found  : " +
                found
        );

        System.out.println(
                "Time   : " +
                (end - start) +
                " ns"
        );
    }

    // =====================================================
    // CO3
    // =====================================================

    static void co3Menu() {

        while (true) {

            System.out.println();
            System.out.println("================================");
            System.out.println("       CO3 DYNAMIC PROGRAMMING");
            System.out.println("================================");
            System.out.println("1. Levenshtein");
            System.out.println("2. Damerau-Levenshtein");
            System.out.println("3. Weighted Edit Distance");
            System.out.println("4. Bitmask DP");
            System.out.println("5. Back");
            System.out.println("================================");

            int choice =
                    inputInt("Enter choice: ");

            switch (choice) {

                case 1:
                    levenshtein();
                    break;

                case 2:
                    damerau();
                    break;

                case 3:
                    weighted();
                    break;

                case 4:
                    BitmaskDP.solve();
                    break;

                case 5:
                    return;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    static void levenshtein() {

        String a =
                inputText(
                        "Enter first security string: "
                );

        String b =
                inputText(
                        "Enter second security string: "
                );

        long start =
                System.nanoTime();

        int result =
                Levenshtein.calculate(a, b);

        long end =
                System.nanoTime();

        System.out.println();
        System.out.println(
                "========== LEVENSHTEIN =========="
        );

        System.out.println(
                "String 1 : " + a
        );

        System.out.println(
                "String 2 : " + b
        );

        System.out.println(
                "Distance : " + result
        );

        System.out.println(
                "Time     : " +
                (end - start) +
                " ns"
        );
    }

    static void damerau() {

        String a =
                inputText(
                        "Enter first security string: "
                );

        String b =
                inputText(
                        "Enter second security string: "
                );

        long start =
                System.nanoTime();

        int result =
                DamerauLevenshtein.calculate(
                        a,
                        b
                );

        long end =
                System.nanoTime();

        System.out.println();
        System.out.println(
                "====== DAMERAU-LEVENSHTEIN ======"
        );

        System.out.println(
                "Distance : " + result
        );

        System.out.println(
                "Time     : " +
                (end - start) +
                " ns"
        );
    }

    static void weighted() {

        String a =
                inputText(
                        "Enter first security string: "
                );

        String b =
                inputText(
                        "Enter second security string: "
                );

        int insertion =
                inputInt(
                        "Insertion cost: "
                );

        int deletion =
                inputInt(
                        "Deletion cost: "
                );

        int substitution =
                inputInt(
                        "Substitution cost: "
                );

        long start =
                System.nanoTime();

        int result =
                WeightedEditDistance.calculate(
                        a,
                        b,
                        insertion,
                        deletion,
                        substitution
                );

        long end =
                System.nanoTime();

        System.out.println();
        System.out.println(
                "======= WEIGHTED EDIT DISTANCE ======="
        );

        System.out.println(
                "Distance : " + result
        );

        System.out.println(
                "Time     : " +
                (end - start) +
                " ns"
        );
    }

    // =====================================================
    // DATASET INFORMATION
    // =====================================================

    static void datasetInfo() {

        System.out.println();
        System.out.println(
                "========== DATASET INFORMATION =========="
        );

        System.out.println(
                "Dataset : UGRansome"
        );

        System.out.println(
                "File    : " + DATASET
        );

        System.out.println(
                "Records : " + records.size()
        );

        System.out.println(
                "Corpus  : " + corpus.length() +
                " characters"
        );

        System.out.println("Skipped malformed rows: " + DatasetLoader.getSkippedRows());
        System.out.println("Columns: " + java.util.Arrays.toString(DatasetLoader.getColumns()));
        java.util.Map<String, Integer> threatCounts = new java.util.TreeMap<>();
        for (SecurityRecord r : records) threatCounts.put(r.getThreats(), threatCounts.getOrDefault(r.getThreats(), 0) + 1);
        System.out.println("Threat field categories (literal CSV values; not independently validated): " + threatCounts);
        System.out.println("Prediction is reported as a source column and is not treated as ground truth.");
    }

    // =====================================================
    // SAMPLE RECORDS
    // =====================================================

    static void samples() {

        System.out.println();
        System.out.println(
                "========== SAMPLE RECORDS =========="
        );

        int limit =
                Math.min(5, records.size());

        for (int i = 0;
             i < limit;
             i++) {

            System.out.println();
            System.out.println(
                    "Record " + (i + 1)
            );

            System.out.println(
                    records.get(i)
            );
        }
    }

    // =====================================================
    // DATASET SEARCH
    // =====================================================

    static void datasetSearch() {

        String keyword =
                inputText(
                        "Enter keyword: "
                );

        if (keyword.isEmpty()) {
            return;
        }

        int count = 0;

        System.out.println();
        System.out.println(
                "========== SEARCH RESULTS =========="
        );

        for (SecurityRecord r : records) {

            if (r.toSearchText()
                    .toLowerCase()
                    .contains(
                            keyword.toLowerCase()
                    )) {

                count++;

                if (count <= 10) {

                    System.out.println(
                            r
                    );
                }
            }
        }

        System.out.println();
        System.out.println(
                "Total matches: " +
                count
        );
    }

    // =====================================================
    // INPUT FUNCTIONS
    // =====================================================

    static String inputText(String message) {

        System.out.print(message);

        return sc.nextLine().trim();
    }

    static int inputInt(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Integer.parseInt(
                        sc.nextLine().trim()
                );

            } catch (Exception e) {

                System.out.println(
                        "Please enter a valid number."
                );
            }
        }
    }

    static boolean inputBoolean(String message) {
        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim();
            if (value.equalsIgnoreCase("T") || value.equalsIgnoreCase("TRUE")) return true;
            if (value.equalsIgnoreCase("F") || value.equalsIgnoreCase("FALSE")) return false;
            System.out.println("Please enter T or F.");
        }
    }
}
