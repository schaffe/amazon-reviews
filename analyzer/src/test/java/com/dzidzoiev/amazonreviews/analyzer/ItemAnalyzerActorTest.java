package com.dzidzoiev.amazonreviews.analyzer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import com.dzidzoiev.amazonreviews.ReviewItem;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ItemAnalyzerActorTest {

    private ItemAnalyzer analyzerItself;
    private static ActorSystem system;
    private TestActorRef<ItemAnalyzer> actor;

    @BeforeClass
    public static void init() throws Exception {
        system = ActorSystem.create("test-amazon-reviews");
    }

    @Before
    public void setUp() throws Exception {
        final Props props = Props.create(ItemAnalyzer.class);
        actor = TestActorRef.create(system, props);
        analyzerItself = actor.underlyingActor();

    }

    @Test
    public void testActorPositive() throws Throwable {
        ReviewItem item1 = new ReviewItem(0, "prod1", "profile1", "a bb ccc");
        ReviewItem item2 = new ReviewItem(0, "prod2", "profile1", "a bb cccd");
        ReviewItem item3 = new ReviewItem(0, "prod1", "profile2", "a bb ccc");

        actor.tell(item1, ActorRef.noSender());
        actor.tell(item2, ActorRef.noSender());
        actor.tell(item3, ActorRef.noSender());

        ItemAnalyzer.JobResult jobResult = analyzerItself.composeResult(10);

        Map<String,Integer> expectedUserActivity = new LinkedHashMap<>();
        expectedUserActivity.put("profile1", 2);
        expectedUserActivity.put("profile2", 1);

        assertEquals(expectedUserActivity, jobResult.userActivityMap);

        Map<String,Integer> commentsItemExpected = new LinkedHashMap<>();
        commentsItemExpected.put("prod1", 2);
        commentsItemExpected.put("prod2", 1);

        assertEquals(commentsItemExpected, jobResult.itemCommentsMap);

        Map<String,Integer> frequencyExpected = new HashMap<>();
        frequencyExpected.put("bb", 3);
        frequencyExpected.put("a", 3);
        frequencyExpected.put("ccc", 2);
        frequencyExpected.put("cccd", 1);

        assertEquals(frequencyExpected, jobResult.wordFrequency);
    }

    @Test
    @Ignore //STUB
    public void testActorShouldHandleDuplicates() throws Throwable {
        ReviewItem item1 = new ReviewItem(0, "prod1", "profile1", "a bb ccc");
        ReviewItem item2 = new ReviewItem(0, "prod2", "profile1", "a bb cccd");
        ReviewItem item3 = new ReviewItem(0, "prod1", "profile2", "a bb ccc");
        ReviewItem item3_duplicate = new ReviewItem(0, "prod1", "profile2", "a bb ccc");

        actor.tell(item1, ActorRef.noSender());
        actor.tell(item2, ActorRef.noSender());
        actor.tell(item3, ActorRef.noSender());
        actor.tell(item3_duplicate, ActorRef.noSender());

        ItemAnalyzer.JobResult jobResult = analyzerItself.composeResult(10);

        Map<String,Integer> expectedUserActivity = new LinkedHashMap<>();
        expectedUserActivity.put("profile1", 2);
        expectedUserActivity.put("profile2", 1);

        assertEquals(expectedUserActivity, jobResult.userActivityMap);

        Map<String,Integer> commentsItemExpected = new LinkedHashMap<>();
        commentsItemExpected.put("prod1", 2);
        commentsItemExpected.put("prod2", 1);

        assertEquals(commentsItemExpected, jobResult.itemCommentsMap);

        Map<String,Integer> frequencyExpected = new HashMap<>();
        frequencyExpected.put("bb", 3);
        frequencyExpected.put("a", 3);
        frequencyExpected.put("ccc", 2);
        frequencyExpected.put("cccd", 1);

        assertEquals(frequencyExpected, new HashMap<>(jobResult.wordFrequency));
    }
}