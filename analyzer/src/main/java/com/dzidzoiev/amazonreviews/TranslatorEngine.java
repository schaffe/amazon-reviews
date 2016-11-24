package com.dzidzoiev.amazonreviews;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Pair;
import akka.routing.RoundRobinPool;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TranslatorEngine extends UntypedActor {

    public static final int TIMEOUT = 200;
    public static final int ALLOWED_CONNECTIONS = 100;
    public static final int CHARACTER_LIMIT = 1000;

    private final ActorRef workers = getContext().actorOf(new RoundRobinPool(ALLOWED_CONNECTIONS).props(Props.create(TranslatorWorker.class)));
    private Map<String, Pair<AtomicInteger, PriorityQueue<PartialJob>>> partialResults = new HashMap<>();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof TranslateMessage) {
            handleTranslateMessage(message);
            return;
        }
        if (message instanceof PartialJob) {
            acceptPartialResult((PartialJob) message)
                    //STUB!
                    //tell result to result handler
                    .ifPresent(System.out::println);

        }
        unhandled(message);
    }

    private void handleTranslateMessage(Object message) {
        // STUB!
        //split into chunks of CHARACTER_LIMIT < 1000 by whitespaces
        //construct partial result as set of promises

        int chunks = 1;
        String taskId = UUID.randomUUID().toString();
        partialResults.put(taskId, Pair.create(new AtomicInteger(chunks), new PriorityQueue<>()));
        workers.tell(message, getSender());
    }

    private Optional<String> acceptPartialResult(PartialJob p) {
        Pair<AtomicInteger, PriorityQueue<PartialJob>> pair = partialResults.get(p.id);
        if (pair.first().decrementAndGet() <= 0) {
            String result = pair.second().stream()
                    .map(part -> part.text)
                    .collect(Collectors.joining(" "));
            return Optional.of(result);
        } else {
            pair.second().add(p);
            return Optional.empty();
        }
    }

    static class TranslatorWorker extends UntypedActor {

        private PartialJob translateText(PartialJob text) throws Exception {

            // STUB!
            // translate here
            return new PartialJob(text.order, text.id, text.text);
        }

        @Override
        public void onReceive(Object message) throws Throwable {
            if (message instanceof PartialJob) {
                PartialJob partialJob = translateText((PartialJob) message);
                context().parent().tell(partialJob, getSelf());
                return;
            }
            unhandled(message);
        }
    }

    static class PartialJob implements Comparable<PartialJob>, Serializable {
        final int order;
        final String id;
        final String text;

        public PartialJob(int order, String id, String text) {
            this.order = order;
            this.id = id;
            this.text = text;
        }

        @Override
        public int compareTo(PartialJob o) {
            return Integer.compare(order, o.order);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PartialJob that = (PartialJob) o;
            return order == that.order &&
                    Objects.equals(text, that.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(order, text);
        }
    }

    static class TranslateMessage {
        private final String input_lang;
        private final String output_lang;
        private final String text;

        public TranslateMessage(String input_lang, String output_lang, String text) {
            this.input_lang = input_lang;
            this.output_lang = output_lang;
            this.text = text;
        }

        public String getInput_lang() {
            return input_lang;
        }

        public String getOutput_lang() {
            return output_lang;
        }

        public String getText() {
            return text;
        }
    }
}
