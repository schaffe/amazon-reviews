package com.dzidzoiev.amazonreviews.analyzer;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class WordFrequencyAnalyzer {
    private static final Pattern wordsSplitPattern = Pattern.compile("[^a-zA-Z']");

    static Map<String, Integer> analyzeWordFrequency(CharSequence text) {
        String[] words = splitWords(text);

        return Arrays.stream(words)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.groupingBy(String::toLowerCase, Collectors.reducing(0, e -> 1, Integer::sum)));
    }

    static String[] splitWords(CharSequence text) {
        return wordsSplitPattern.split(text);
    }
}
