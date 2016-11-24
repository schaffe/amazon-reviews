package com.dzidzoiev.amazonreviews.analyzer;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class WordFrequencyAnalyzerTest {

    @Test
    public void analyzeWordFrequencyPositive() throws Exception {
        String test = "a b b c c c";
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        Assert.assertEquals(map, WordFrequencyAnalyzer.analyzeWordFrequency(test));
    }

    @Test
    public void analyzeWordFrequencyEmpty() throws Exception {
        String test = "";
        Map<String, Integer> map = new HashMap<>();

        Assert.assertEquals(map, WordFrequencyAnalyzer.analyzeWordFrequency(test));
    }

    @Test
    public void analyzeWordFrequencyWithTags() throws Exception {
        String test = "a b <br>b</br> c c c";
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        Assert.assertEquals(map, WordFrequencyAnalyzer.analyzeWordFrequency(test));
    }

    @Test
    public void analyzeWordFrequencyWithParenthesis() throws Exception {
        String test = "a b (b) (c c c)";
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        Assert.assertEquals(map, WordFrequencyAnalyzer.analyzeWordFrequency(test));
    }

    @Test
    public void testSplitWordsPositive() throws Exception {
        String test = "a b c";
        String[] expected = {"a", "b", "c"};

        Assert.assertArrayEquals(expected, WordFrequencyAnalyzer.splitWords(test));
    }

    @Test
    public void testSplitWordsWithWhitespaces() throws Exception {
        String test = "a         b   c";
        String[] expected = {"a", "b", "c"};

        Assert.assertArrayEquals(expected, WordFrequencyAnalyzer.splitWords(test));
    }

    @Test
    public void testSplitLongWords() throws Exception {
        String test = "afghjkl bdfghjklwwrwr cdfghjk";
        String[] expected = {"afghjkl", "bdfghjklwwrwr", "cdfghjk"};

        Assert.assertArrayEquals(expected, WordFrequencyAnalyzer.splitWords(test));
    }
}