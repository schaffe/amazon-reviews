package com.dzidzoiev.amazonreviews;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.tuple.Tuple3;
import akka.routing.Broadcast;
import akka.routing.RoundRobinPool;
import com.dzidzoiev.amazonreviews.analyzer.ItemAnalyzer;
import com.dzidzoiev.amazonreviews.translator.TranslateMessage;
import com.dzidzoiev.amazonreviews.translator.TranslatorEngine;

import java.io.File;

public class Kernel extends UntypedActor {

    private static Registry registry;

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final ActorRef reader = getContext().actorOf(Props.create(CSVFileReader.class), "reader");
    private final ActorRef sink = getContext().actorOf(Props.create(ItemAnalyzer.class), "sink");
    private final ActorRef analyzers;
    private final ActorRef resultHandler = getContext().actorOf(Props.create(ResultHandler.class), "results");
    private final ActorRef translator = getContext().actorOf(Props.create(TranslatorEngine.class), "translator");

    private int analyzersCount;

    public Kernel(StartProcessingMessage init) {
        analyzersCount = init.parallelism;
        analyzers = getContext().actorOf(new RoundRobinPool(init.parallelism).props(Props.create(ItemAnalyzer.class)), "analyzers");
        registry = new Registry(getSelf().path(), sink.path(), resultHandler.path(), translator.path());
    }

    public static Registry getRegistry() {
        return registry;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof StartProcessingMessage)
            reader.tell(message, getSender());
        else if (message instanceof ReviewItem)
            analyzers.tell(message, getSender());
        else if (message == CSVFileReader.EVENTS.FILE_PROCESSING_FINISHED) {
            log.info("File processing finished.");
            analyzers.tell(new Broadcast(ItemAnalyzer.EVENTS.FEED_RESULT), getSelf());
        } else if (message instanceof ItemAnalyzer.JobResult) {
            sink.tell(message, getSender());
            if (--analyzersCount == 0)
                sink.tell(ItemAnalyzer.EVENTS.START_AGGREGATING, getSelf());
        } else if (message == EVENT.FINISH) {
            //STUB
            //now iterate over file again and call translator

            context().system().shutdown();
        }

        unhandled(message);

    }

    static class StartProcessingMessage {
        final String filename;
        final int itemsCount;
        final int parallelism;
        final boolean translate;


        public StartProcessingMessage(Tuple3<String, Integer, Boolean> argumets, int cores) {
            this.itemsCount = argumets.t2();
            this.filename = argumets.t1();
            this.parallelism = cores;
            this.translate = argumets.t3();
        }
    }

    public static class Registry {
        public final ActorPath kernel;
        public final ActorPath resultProcessor;
        public final ActorPath resultSink;
        public final ActorPath translator;

        public Registry(ActorPath kernel, ActorPath resultProcessor, ActorPath resultSink, ActorPath translator) {
            this.kernel = kernel;
            this.resultProcessor = resultProcessor;
            this.resultSink = resultSink;
            this.translator = translator;
        }
    }

    public enum EVENT {
        FINISH;
    }
}
