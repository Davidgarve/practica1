package org.ulpgc.dacd.control;


public class Main {
    public static void main(String[] args) {
        SQLiteEventStore sqLiteEventStore = new SQLiteEventStore();
        sqLiteEventStore.createTables();
        VacationVisionarySubscriber subscriber = new VacationVisionarySubscriber();
        subscriber.start();
}
}
