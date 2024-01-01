package org.ulpgc.dacd.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.ulpgc.dacd.control.SQLiteEventStore;

public class TripPlannerWebUI extends Application {

    private final TripPlannerController tripPlannerController;

    public TripPlannerWebUI() {
        this.tripPlannerController = new TripPlannerController(new SQLiteEventStore());
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Trip Planner Web UI");

        TabPane tabPane = new TabPane();
        Tab predictionTab = createPredictionTab();
        Tab recommendationTab = createRecommendationTab();
        Tab hotelTab = createHotelTab();

        tabPane.getTabs().addAll(predictionTab, recommendationTab, hotelTab);

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab createPredictionTab() {
        Tab predictionTab = new Tab("Predicción del Tiempo");
        VBox predictionContent = tripPlannerController.createPredictionTabContent();
        predictionTab.setContent(predictionContent);
        return predictionTab;
    }

    private Tab createRecommendationTab() {
        Tab recommendationTab = new Tab("Recomendaciones de Viaje");
        VBox recommendationContent = tripPlannerController.createRecommendationTabContent();
        recommendationTab.setContent(recommendationContent);
        return recommendationTab;
    }

    private Tab createHotelTab() {
        Tab hotelTab = new Tab("Hoteles Disponibles");
        VBox hotelContent = tripPlannerController.createHotelTabContent();
        hotelTab.setContent(hotelContent);
        return hotelTab;
    }
}
