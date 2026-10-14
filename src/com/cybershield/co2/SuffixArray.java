package com.cybershield.co2;

import java.util.Arrays;

public class SuffixArray {

    public static int[] build(String text) {

        int n = text.length();

        if (n == 0) {
            return new int[0];
        }

        Integer[] suffixes = new Integer[n];

        int[] rank = new int[n];

        int[] newRank = new int[n];

        for (int i = 0; i < n; i++) {
            suffixes[i] = i;
            rank[i] = text.charAt(i);
        }

        for (int k = 1; k < n; k *= 2) {

            final int step = k;
            final int[] currentRank = rank;

            Arrays.sort(
                    suffixes,
                    (a, b) -> {

                        if (currentRank[a] != currentRank[b]) {

                            return Integer.compare(
                                    currentRank[a],
                                    currentRank[b]
                            );
                        }

                        int rankA =
                                a + step < n
                                        ? currentRank[a + step]
                                        : -1;

                        int rankB =
                                b + step < n
                                        ? currentRank[b + step]
                                        : -1;

                        return Integer.compare(
                                rankA,
                                rankB
                        );
                    }
            );

            newRank[suffixes[0]] = 0;

            for (int i = 1; i < n; i++) {

                int previous =
                        suffixes[i - 1];

                int current =
                        suffixes[i];

                int previousNext =
                        previous + step < n
                                ? rank[previous + step]
                                : -1;

                int currentNext =
                        current + step < n
                                ? rank[current + step]
                                : -1;

                if (rank[previous] != rank[current]
                        || previousNext != currentNext) {

                    newRank[current] =
                            newRank[previous] + 1;

                } else {

                    newRank[current] =
                            newRank[previous];
                }
            }

            int[] temp = rank;

            rank = newRank;

            newRank = temp;

            if (rank[suffixes[n - 1]] == n - 1) {
                break;
            }
        }

        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            result[i] = suffixes[i];
        }

        return result;
    }

    public static void print(
            String text,
            int limit) {

        int[] sa = build(text);

        System.out.println();
        System.out.println(
                "========== SUFFIX ARRAY =========="
        );

        int count =
                Math.min(
                        limit,
                        sa.length
                );

        for (int i = 0; i < count; i++) {

            int index = sa[i];

            String suffix =
                    text.substring(index);

            if (suffix.length() > 100) {
                suffix =
                        suffix.substring(0, 100)
                                + "...";
            }

            System.out.println(
                    i + " -> " +
                    index + " : " +
                    suffix
            );
        }

        System.out.println(
                "Displayed: " +
                count +
                " / " +
                sa.length
        );
    }
}