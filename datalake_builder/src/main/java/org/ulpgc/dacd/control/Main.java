package org.ulpgc.dacd.control;


public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Debe proporcionar la ruta base como argumento.");
            return;
        }

        String baseDirectory = args[0];
        TopicSubscriber topicSubscriber = new TopicSubscriber();
        topicSubscriber.start(baseDirectory);
    }
}
