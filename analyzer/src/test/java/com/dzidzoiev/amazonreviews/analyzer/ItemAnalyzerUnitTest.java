package com.dzidzoiev.amazonreviews.analyzer;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemAnalyzerUnitTest {
    @Test
    public void testSortByValuePositive() throws Exception {
        Map<String, Integer> test = new LinkedHashMap<>();
        test.put("b", 20);
        test.put("a", 30);
        test.put("d", 0);
        test.put("e", -10);
        test.put("c", 10);

        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("a", 30);
        expected.put("b", 20);
        expected.put("c", 10);
        expected.put("d", 0);
        expected.put("e", -10);

        Assert.assertEquals(expected, ItemAnalyzer.sortByValue(test, expected.size()));
    }

    @Test
    public void testSortByValueTrim() throws Exception {
        Map<String, Integer> test = new LinkedHashMap<>();
        test.put("d", 0);
        test.put("b", 20);
        test.put("e", -10);
        test.put("a", 30);
        test.put("c", 10);

        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("a", 30);
        expected.put("b", 20);
        expected.put("c", 10);

        Assert.assertEquals(expected, ItemAnalyzer.sortByValue(test, 3));
    }

    @Test
    public void testSortByValueEmpty() throws Exception {
        Map<String, Integer> test = new LinkedHashMap<>();

        Map<String, Integer> expected = new LinkedHashMap<>();

        Assert.assertEquals(expected, ItemAnalyzer.sortByValue(test, 10));
    }

    @Test
    public void testMergeMapsPositive() throws Exception {
        Map<String, Integer> testA = new HashMap<>();
        testA.put("a", 30);
        testA.put("b", 20);
        testA.put("c", 10);

        Map<String, Integer> testB = new HashMap<>();
        testB.put("d", 15);
        testB.put("b", 2);
        testB.put("c", 1);

        Map<String, Integer> expected = new HashMap<>();
        expected.put("a", 30);
        expected.put("b", 22);
        expected.put("c", 11);
        expected.put("d", 15);

        ItemAnalyzer.mergeMapSumming(testA, testB);

        Assert.assertEquals(expected, testA);
    }
}