package org.ulpgc.dacd.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.ulpgc.dacd.control.TripPlanner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripPlannerWebUI extends Application {

    private final WeatherController weatherController;

    public TripPlannerWebUI() {
        this.weatherController = new WeatherController();
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

        tabPane.getTabs().addAll(predictionTab, recommendationTab);

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab createPredictionTab() {
        Tab predictionTab = new Tab("Predicción del Tiempo");
        VBox predictionContent = weatherController.createPredictionTabContent();
        predictionTab.setContent(predictionContent);
        return predictionTab;
    }

    private Tab createRecommendationTab() {
        Tab recommendationTab = new Tab("Recomendaciones de Viaje");
        VBox recommendationContent = weatherController.createRecommendationTabContent();
        recommendationTab.setContent(recommendationContent);
        return recommendationTab;
    }
}
