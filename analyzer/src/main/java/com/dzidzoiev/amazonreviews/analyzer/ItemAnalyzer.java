package com.dzidzoiev.amazonreviews.analyzer;

import akka.actor.ActorPath;
import akka.actor.UntypedActor;
import com.dzidzoiev.amazonreviews.Kernel;
import com.dzidzoiev.amazonreviews.ReviewItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemAnalyzer extends UntypedActor {
    public static int DEFAULT_LIMIT = 1000;

    private final Map<String, Integer> userActivityMap = new HashMap<>();
    private final Map<String, Integer> itemCommentsMap = new HashMap<>();
    private final Map<String, Integer> wordFrequency = new HashMap<>();

    public enum EVENTS {
        FEED_RESULT,
        START_AGGREGATING
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof ReviewItem) {
            analyze((ReviewItem) message);
        } else if (message == EVENTS.FEED_RESULT) {
            tellResult(Kernel.getRegistry().kernel);
        } else if (message instanceof JobResult) {
            merge((JobResult) message);
        } else if (message == EVENTS.START_AGGREGATING) {
            tellResult(Kernel.getRegistry().resultSink);
        }
        unhandled(message);

    }

    JobResult composeResult(int limit) {
        return new JobResult(
                sortByValue(userActivityMap, limit),
                sortByValue(itemCommentsMap, limit),
                sortByValue(wordFrequency, limit));
    }

    private void merge(JobResult jobResult) {
        mergeMapSumming(itemCommentsMap, jobResult.itemCommentsMap);
        mergeMapSumming(userActivityMap, jobResult.userActivityMap);
        mergeMapSumming(wordFrequency, jobResult.wordFrequency);
    }

    private void analyze(ReviewItem item) {

        Map<String, Integer> itemWordFrequency = WordFrequencyAnalyzer.analyzeWordFrequency(item.getText());

//        if (isDuplicate(item, itemWordFrequency))
//            return;

        userActivityMap.compute(item.getProfileName(),
                (key, value) -> value == null ? 1 : value + 1);

        itemCommentsMap.compute(item.getProductId(),
                (key, value) -> value == null ? 1 : value + 1);

        mergeMapSumming(wordFrequency, itemWordFrequency);
    }

    private boolean isDuplicate(ReviewItem item, Map<String, Integer> itemWordFrequency) {
        return userActivityMap.containsKey(item.getProfileName())
                && itemCommentsMap.containsKey(item.getProductId())
                && wordFrequency.keySet().containsAll(itemWordFrequency.keySet());
    }

    static void mergeMapSumming(Map<String, Integer> baseMap, Map<String, Integer> mergingMap) {
        for (Map.Entry<String, Integer> wordItem : mergingMap.entrySet())
            baseMap.merge(wordItem.getKey(), wordItem.getValue(), Integer::sum);
    }

    private void tellResult(ActorPath path) {
        context().actorSelection(path).tell(composeResult(DEFAULT_LIMIT), getSelf());
        userActivityMap.clear();
        itemCommentsMap.clear();
        wordFrequency.clear();
    }

    static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, int limit) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static class JobResult {
        public final Map<String, Integer> userActivityMap;
        public final Map<String, Integer> itemCommentsMap;
        public final Map<String, Integer> wordFrequency;

        public JobResult(Map<String, Integer> userActivityMap, Map<String, Integer> itemCommentsMap, Map<String, Integer> wordFrequency) {
            this.userActivityMap = new HashMap<>(userActivityMap);
            this.itemCommentsMap = new HashMap<>(itemCommentsMap);
            this.wordFrequency = new HashMap<>(wordFrequency);
        }
    }
}
