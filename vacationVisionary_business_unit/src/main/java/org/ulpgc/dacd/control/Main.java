package org.ulpgc.dacd.control;

public class Main {public static void main(String[] args) {
    SQLiteEventStore.createTables();
    VacationVisionarySubscriber topicSubscriber = new VacationVisionarySubscriber();
    topicSubscriber.start();
}

}
