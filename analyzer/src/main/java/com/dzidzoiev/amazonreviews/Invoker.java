package com.dzidzoiev.amazonreviews;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.tuple.Tuple3;

import java.io.IOException;

/**
 * Main entrance point for the application
 * <p>
 * Is responsible for creating ActorSystem and sending initializing message
 */
public class Invoker {

    public static final String DEFAULT_FILE = "analyzer/src/main/resources/Reviews.csv";
    public static final int DEFAULT_ITEMS = 500_000;

    public static void main(String[] args) throws IOException {
        System.in.read();
        Tuple3<String, Integer, Boolean> argumets = parseArgs(args);

        ActorSystem system = ActorSystem.create("amazon-reviews");

        int cores = Runtime.getRuntime().availableProcessors();
        Kernel.StartProcessingMessage start = new Kernel.StartProcessingMessage(argumets, cores);
        final ActorRef kernel = system.actorOf(Props.create(Kernel.class, start), "kernel");
        kernel.tell(start, ActorRef.noSender());
    }

    private static Tuple3<String, Integer, Boolean> parseArgs(String[] args) {
        if (args.length == 1)
            return new Tuple3<>(args[0], DEFAULT_ITEMS, false);
        else if (args.length == 2)
            return new Tuple3<>(args[0], Integer.valueOf(args[1]), false);
        else if (args.length == 3) {
            Boolean translate = Boolean.valueOf(args[2].replaceAll("`", "").split("=")[1]);
            return new Tuple3<>(args[0], Integer.valueOf(args[1]), translate);
        } else
            return new Tuple3<>(DEFAULT_FILE, DEFAULT_ITEMS, false);

    }
}
