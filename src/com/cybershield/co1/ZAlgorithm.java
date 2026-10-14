package com.cybershield.co1;

import java.util.ArrayList;
import java.util.List;

public class ZAlgorithm {

    public static List<Integer> search(
            String text,
            String pattern) {

        List<Integer> result =
                new ArrayList<>();

        if (text == null ||
                pattern == null ||
                pattern.isEmpty() ||
                pattern.length() > text.length()) {

            return result;
        }

        // Use -1 as a separator outside the UTF-16 char range, so a '$' (or
        // any other character) in either input cannot create a false boundary.
        int[] combined = new int[pattern.length() + 1 + text.length()];
        for (int i = 0; i < pattern.length(); i++) combined[i] = pattern.charAt(i);
        combined[pattern.length()] = -1;
        for (int i = 0; i < text.length(); i++) combined[pattern.length() + 1 + i] = text.charAt(i);

        int n = combined.length;
        int[] z = new int[n];

        int left = 0;
        int right = 0;

        for (int i = 1;
             i < n;
             i++) {

            if (i <= right) {

                z[i] =
                        Math.min(
                                right - i + 1,
                                z[i - left]
                        );
            }

            while (i + z[i] < n &&
                    combined[z[i]] == combined[i + z[i]]) {

                z[i]++;
            }

            if (i + z[i] - 1 > right) {

                left = i;

                right =
                        i + z[i] - 1;
            }
        }

        for (int i = 0;
             i < n;
             i++) {

            if (z[i] ==
                    pattern.length()) {

                result.add(
                        i - pattern.length() - 1
                );
            }
        }

        return result;
    }
}
