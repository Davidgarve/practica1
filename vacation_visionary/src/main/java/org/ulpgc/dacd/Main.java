package org.ulpgc.dacd;

import org.ulpgc.dacd.control.*;
import org.ulpgc.dacd.view.TripPlannerController;
import org.ulpgc.dacd.view.TripPlannerWebUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SQLiteEventStore eventStore = new SQLiteEventStore();
        eventStore.createTables();

        VacationVisionarySubscriber visionarySubscriber = new VacationVisionarySubscriber();
        visionarySubscriber.start();

        WeatherBusinessLogicInterface weatherBusinessLogic = new WeatherBusinessLogic(eventStore);
        HotelBusinessLogicInterface hotelBusinessLogic = new HotelBusinessLogic(eventStore);

        TripPlannerController tripPlannerController = new TripPlannerController(weatherBusinessLogic, hotelBusinessLogic);
        TripPlannerWebUI tripPlannerWebUI = new TripPlannerWebUI(tripPlannerController);

        SwingUtilities.invokeLater(() -> {
            tripPlannerWebUI.setVisible(true);
        });
    }
}
