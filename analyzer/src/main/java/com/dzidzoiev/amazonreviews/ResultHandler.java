package com.dzidzoiev.amazonreviews;

import akka.actor.UntypedActor;
import com.dzidzoiev.amazonreviews.analyzer.ItemAnalyzer;

import java.util.TreeSet;

public class ResultHandler extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof ItemAnalyzer.JobResult) {
            ItemAnalyzer.JobResult jobResult = (ItemAnalyzer.JobResult) message;

            System.out.println(new TreeSet<>(jobResult.userActivityMap.keySet()));
            System.out.println(new TreeSet<>(jobResult.itemCommentsMap.keySet()));
            System.out.println(new TreeSet<>(jobResult.wordFrequency.keySet()));

            context().actorSelection(Kernel.getRegistry().kernel).tell(Kernel.EVENTS.FINISH, getSelf());
            return;
        }
        unhandled(message);
    }
}
