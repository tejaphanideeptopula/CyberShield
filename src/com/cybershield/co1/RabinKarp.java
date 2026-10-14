package com.cybershield.co1;

import java.util.ArrayList;
import java.util.List;

public class RabinKarp {

    private static final int BASE = 256;
    private static final int PRIME = 1000003;

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

        int n = text.length();
        int m = pattern.length();

        long patternHash = 0;
        long textHash = 0;
        long highestPower = 1;

        for (int i = 0;
             i < m - 1;
             i++) {

            highestPower =
                    (highestPower * BASE)
                            % PRIME;
        }

        for (int i = 0;
             i < m;
             i++) {

            patternHash =
                    (BASE * patternHash
                            + pattern.charAt(i))
                            % PRIME;

            textHash =
                    (BASE * textHash
                            + text.charAt(i))
                            % PRIME;
        }

        for (int i = 0;
             i <= n - m;
             i++) {

            if (patternHash == textHash) {

                boolean match = true;

                for (int j = 0;
                     j < m;
                     j++) {

                    if (text.charAt(i + j)
                            != pattern.charAt(j)) {

                        match = false;
                        break;
                    }
                }

                if (match) {
                    result.add(i);
                }
            }

            if (i < n - m) {

                textHash =
                        (BASE *
                                (textHash -
                                text.charAt(i)
                                * highestPower)
                                + text.charAt(i + m))
                                % PRIME;

                if (textHash < 0) {
                    textHash += PRIME;
                }
            }
        }

        return result;
    }
}