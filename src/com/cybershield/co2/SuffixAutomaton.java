package com.cybershield.co2;

import java.util.HashMap;
import java.util.Map;

public class SuffixAutomaton {

    static class State {

        int length;

        int link = -1;

        Map<Character, Integer> next =
                new HashMap<>();
    }

    private State[] states;

    private int size = 1;

    private int last = 0;

    public SuffixAutomaton(
            String text) {

        int maxStates =
                Math.max(
                        2,
                        2 * text.length()
                );

        states =
                new State[maxStates];

        states[0] =
                new State();

        for (char c :
                text.toCharArray()) {

            extend(c);
        }
    }

    private void extend(char c) {

        int current =
                size++;

        states[current] =
                new State();

        states[current].length =
                states[last].length + 1;

        int p = last;

        while (p != -1 &&
                !states[p]
                        .next
                        .containsKey(c)) {

            states[p]
                    .next
                    .put(
                            c,
                            current
                    );

            p =
                    states[p].link;
        }

        if (p == -1) {

            states[current].link =
                    0;

        } else {

            int q =
                    states[p]
                            .next
                            .get(c);

            if (states[p].length + 1
                    == states[q].length) {

                states[current].link =
                        q;

            } else {

                int clone =
                        size++;

                states[clone] =
                        new State();

                states[clone].length =
                        states[p].length + 1;

                states[clone].link =
                        states[q].link;

                states[clone].next.putAll(
                        states[q].next
                );

                while (p != -1 &&
                        states[p]
                                .next
                                .getOrDefault(
                                        c,
                                        -1
                                ) == q) {

                    states[p]
                            .next
                            .put(
                                    c,
                                    clone
                            );

                    p =
                            states[p].link;
                }

                states[q].link =
                        clone;

                states[current].link =
                        clone;
            }
        }

        last =
                current;
    }

    public boolean contains(
            String pattern) {

        if (pattern == null ||
                pattern.isEmpty()) {

            return true;
        }

        int current = 0;

        for (char c :
                pattern.toCharArray()) {

            if (!states[current]
                    .next
                    .containsKey(c)) {

                return false;
            }

            current =
                    states[current]
                            .next
                            .get(c);
        }

        return true;
    }

    public int getStateCount() {
        return size;
    }
}