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

    private static final String DEFAULT_FILE = "analyzer/src/main/resources/Reviews.csv";
    private static final int DEFAULT_ITEMS = 500;

    public static void main(String[] args) throws IOException {
        Tuple3<String, Integer, Boolean> arguments = parseArgs(args);

        ActorSystem system = ActorSystem.create("amazon-reviews");

        int cores = Runtime.getRuntime().availableProcessors();
        Kernel.StartProcessingMessage startMessage = new Kernel.StartProcessingMessage(arguments, cores);

        final ActorRef kernel = system.actorOf(Props.create(Kernel.class, startMessage), "kernel");
        kernel.tell(startMessage, ActorRef.noSender());
    }

    private static Tuple3<String, Integer, Boolean> parseArgs(String[] args) {
        if (args.length == 1)
            return new Tuple3<>(args[0], DEFAULT_ITEMS, false);
        else if (args.length == 2)
            return new Tuple3<>(args[0], Integer.valueOf(args[1]), false);
        else if (args.length == 3)
            return new Tuple3<>(args[0], Integer.valueOf(args[1]), extractTranslateArgument(args[2]));
        else
            return new Tuple3<>(DEFAULT_FILE, DEFAULT_ITEMS, false);

    }

    private static boolean extractTranslateArgument(String arg) {
        String unquote = arg.replaceAll("\'|`|\"", "");
        String[] parts = unquote.split("=");
        if (parts.length < 2)
            throw new IllegalArgumentException("You shuould pass `translate=true` argument to turn on translation.");
        String boolString = parts[1];
        return Boolean.valueOf(boolString);
    }
}
