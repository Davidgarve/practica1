package org.ulpgc.dacd.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.ulpgc.dacd.control.SQLiteEventStore;
import org.ulpgc.dacd.control.TripPlanner;
import org.ulpgc.dacd.control.TripPlannerHotel;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TripPlannerController {

    private final TripPlanner tripPlanner;
    private final TripPlannerHotel tripPlannerHotel;

    public TripPlannerController(SQLiteEventStore eventStore) {
        this.tripPlanner = new TripPlanner(eventStore);
        this.tripPlannerHotel = new TripPlannerHotel(eventStore);
    }

    public VBox createPredictionTabContent() {
        VBox predictionContent = new VBox(10);
        predictionContent.setPadding(new Insets(10));

        DatePicker predictionDatePicker = new DatePicker(LocalDate.now());
        ChoiceBox<String> predictionLocationChoiceBox = new ChoiceBox<>();
        Button predictButton = new Button("Predecir Condiciones");
        WebView predictionWebView = new WebView();
        WebEngine predictionWebEngine = predictionWebView.getEngine();

        updateAvailableLocations(predictionLocationChoiceBox);

        predictButton.setOnAction(e -> handlePredictionButtonClick(
                predictionLocationChoiceBox.getValue(),
                predictionDatePicker.getValue(),
                predictionWebEngine
        ));

        predictionContent.getChildren().addAll(
                new Label("Fecha de Predicción"), predictionDatePicker,
                new Label("Ubicación"), predictionLocationChoiceBox,
                predictButton, predictionWebView
        );

        return predictionContent;
    }

    public VBox createRecommendationTabContent() {
        VBox recommendationContent = new VBox(10);
        recommendationContent.setPadding(new Insets(10));

        DatePicker checkInDatePicker = new DatePicker(LocalDate.now());
        DatePicker checkOutDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        ChoiceBox<String> recommendationLocationChoiceBox = new ChoiceBox<>();
        Button recommendButton = new Button("Obtener Recomendaciones");
        WebView recommendationWebView = new WebView();
        WebEngine recommendationWebEngine = recommendationWebView.getEngine();

        updateAvailableLocations(recommendationLocationChoiceBox);

        recommendButton.setOnAction(e -> handleRecommendationButtonClick(
                recommendationLocationChoiceBox.getValue(),
                checkInDatePicker.getValue(),
                checkOutDatePicker.getValue(),
                recommendationWebEngine
        ));

        recommendationContent.getChildren().addAll(
                new Label("Fecha de Check-In"), checkInDatePicker,
                new Label("Fecha de Check-Out"), checkOutDatePicker,
                new Label("Ubicación"), recommendationLocationChoiceBox,
                recommendButton, recommendationWebView
        );

        return recommendationContent;
    }

    public VBox createHotelTabContent() {
        VBox hotelContent = new VBox(10);
        hotelContent.setPadding(new Insets(10));

        ChoiceBox<String> hotelLocationChoiceBox = new ChoiceBox<>();
        Button getHotelsButton = new Button("Obtener Hoteles Disponibles");
        DatePicker checkInDatePicker = new DatePicker(LocalDate.now());
        DatePicker checkOutDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        WebView hotelWebView = new WebView();
        WebEngine hotelWebEngine = hotelWebView.getEngine();

        updateAvailableLocations(hotelLocationChoiceBox);

        getHotelsButton.setOnAction(e -> handleGetHotelsButtonClick(
                hotelLocationChoiceBox.getValue(),
                checkInDatePicker.getValue(),
                checkOutDatePicker.getValue(),
                hotelWebEngine
        ));

        hotelContent.getChildren().addAll(
                new Label("Ubicación"), hotelLocationChoiceBox,
                new Label("Fecha de Check-In"), checkInDatePicker,
                new Label("Fecha de Check-Out"), checkOutDatePicker,
                getHotelsButton, hotelWebView
        );

        return hotelContent;
    }

    private void updateAvailableLocations(ChoiceBox<String> locationChoiceBox) {
        List<String> availableLocations = tripPlannerHotel.getAvailableHotelLocations();
        locationChoiceBox.getItems().clear();
        locationChoiceBox.getItems().addAll(availableLocations);
    }

    private void handlePredictionButtonClick(String selectedLocation, LocalDate selectedDate, WebEngine webEngine) {
        String formattedDate = selectedDate != null ? selectedDate.toString() : null;
        String predictionResult = tripPlanner.compareWeatherConditions(selectedLocation, formattedDate);

        if (predictionResult.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Sin datos", "No hay datos disponibles para la fecha seleccionada.");
        } else {
            String htmlContent = "<html><body>" + predictionResult + "</body></html>";
            webEngine.loadContent(htmlContent);
        }
    }

    private void handleRecommendationButtonClick(String selectedLocation, LocalDate checkInDate, LocalDate checkOutDate, WebEngine webEngine) {
        if (checkInDate == null || checkOutDate == null || checkInDate.isAfter(checkOutDate)) {
            showAlert(Alert.AlertType.ERROR, "Error de fechas", "Selecciona fechas de check-in y check-out válidas.");
            return;
        }

        webEngine.loadContent(""); // Limpiar contenido

        try {
            List<Map<String, Object>> weatherData = tripPlanner.getWeatherInfoInRange(selectedLocation, checkInDate, checkOutDate);
            List<Map<String, String>> weatherStatusList = Collections.singletonList(tripPlanner.getWeatherStatusForDate(weatherData));

            String recommendation = tripPlanner.generateWeatherRecommendation(weatherStatusList);

            showAlert(Alert.AlertType.INFORMATION, "Recomendación de viaje", recommendation);

            String htmlContent = "<html><body>" + recommendation + "</body></html>";
            webEngine.loadContent(htmlContent);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error. Por favor, inténtalo de nuevo.");
        }
    }

    private void handleGetHotelsButtonClick(String selectedLocation, LocalDate checkInDate, LocalDate checkOutDate, WebEngine webEngine) {
        if (checkInDate == null || checkOutDate == null || checkInDate.isAfter(checkOutDate)) {
            showAlert(Alert.AlertType.ERROR, "Error de fechas", "Selecciona fechas de check-in y check-out válidas.");
            return;
        }

        webEngine.loadContent(""); // Limpiar contenido

        try {
            List<Map<String, Object>> hotelData = tripPlannerHotel.getHotelInfoForDates(selectedLocation, checkInDate, checkOutDate);
            String htmlContent = TripPlannerHotel.buildHotelInfoHTML(hotelData);
            webEngine.loadContent(htmlContent);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error al obtener la información de hoteles. Por favor, inténtalo de nuevo.");
        }
    }



    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
