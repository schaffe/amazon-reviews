package com.dzidzoiev.amazonreviews;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.tuple.Tuple3;
import akka.routing.Broadcast;
import akka.routing.RoundRobinPool;
import com.dzidzoiev.amazonreviews.analyzer.ItemAnalyzer;

import java.io.Serializable;

/**
 * Main application dispatcher.
 *
 * Responsible for managing application lifecycle, actors state and dataflow
 * between submodules.
 */
public class Kernel extends UntypedActor {

    private static Registry registry;

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final ActorRef reader = getContext().actorOf(Props.create(CSVFileReader.class), "reader");
    private final ActorRef resultAggregator = getContext().actorOf(Props.create(ItemAnalyzer.class), "resultAggregator");
    private final ActorRef analyzers;
    private final ActorRef resultHandler = getContext().actorOf(Props.create(ResultHandler.class), "resultHandler");
    private final ActorRef translator = getContext().actorOf(Props.create(TranslatorEngine.class), "translator");

    private int availableAnalyzers;

    public Kernel(StartProcessingMessage init) {
        availableAnalyzers = init.parallelism;
        analyzers = getContext().actorOf(new RoundRobinPool(init.parallelism).props(Props.create(ItemAnalyzer.class)), "analyzers");
        registry = new Registry(getSelf().path(), resultAggregator.path(), resultHandler.path(), translator.path());
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
            resultAggregator.tell(message, getSender());
            if (--availableAnalyzers == 0)
                resultAggregator.tell(ItemAnalyzer.EVENTS.START_AGGREGATING, getSelf());
        } else if (message == EVENTS.FINISH) {
            //STUB
            //now iterate over file again and call translator

            context().system().shutdown();
        }

        unhandled(message);

    }

    static class StartProcessingMessage implements Serializable {
        final String filename;
        final int itemsCount;
        final int parallelism;
        final boolean translate;


        StartProcessingMessage(Tuple3<String, Integer, Boolean> argumets, int cores) {
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

    public enum EVENTS {
        FINISH;
    }
}
