package org.ulpgc.dacd.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.ulpgc.dacd.control.SQLiteEventStore;
import org.ulpgc.dacd.control.WeatherBusinessLogic;
import org.ulpgc.dacd.control.HotelBusinessLogic;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TripPlannerController {

    private final WeatherBusinessLogic weatherBusinessLogic;
    private final HotelBusinessLogic hotelBusinessLogic;
    private static final String STYLE_CLASS_VBOX = "vbox";
    private static final String STYLE_CLASS_BUTTON = "button";

    public TripPlannerController(SQLiteEventStore eventStore) {
        this.weatherBusinessLogic = new WeatherBusinessLogic(eventStore);
        this.hotelBusinessLogic = new HotelBusinessLogic(eventStore);
    }

    public VBox createPredictionTabContent() {
        VBox predictionContent = createStyledVBox();
        DatePicker predictionDatePicker = new DatePicker(LocalDate.now());
        ChoiceBox<String> predictionLocationChoiceBox = new ChoiceBox<>();
        Button predictButton = createStyledButton("Predecir Condiciones");
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
        VBox recommendationContent = createStyledVBox();
        DatePicker checkInDatePicker = new DatePicker(LocalDate.now());
        DatePicker checkOutDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        ChoiceBox<String> recommendationLocationChoiceBox = new ChoiceBox<>();
        Button recommendButton = createStyledButton("Obtener Recomendaciones");
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
        VBox hotelContent = createStyledVBox();
        ChoiceBox<String> hotelLocationChoiceBox = new ChoiceBox<>();
        Button getHotelsButton = createStyledButton("Obtener Hoteles Disponibles");
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
        List<String> availableLocations = hotelBusinessLogic.getAvailableHotelLocations();
        locationChoiceBox.getItems().clear();
        locationChoiceBox.getItems().addAll(availableLocations);
    }

    private void handlePredictionButtonClick(String selectedLocation, LocalDate selectedDate, WebEngine webEngine) {
        String formattedDate = selectedDate != null ? selectedDate.toString() : null;
        String predictionResult = weatherBusinessLogic.compareWeatherConditions(selectedLocation, formattedDate);

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

        // Verifica si hay datos disponibles para al menos una de las fechas
        if (!weatherBusinessLogic.isDataAvailableForDates(selectedLocation, checkInDate, checkOutDate)) {
            showAlert(Alert.AlertType.WARNING, "Datos insuficientes", "No hay datos disponibles para la(s) fecha(s) seleccionada(s).");
            return;
        }

        webEngine.loadContent(""); // Limpiar contenido

        try {
            List<Map<String, Object>> weatherData = weatherBusinessLogic.getWeatherInfoInRange(selectedLocation, checkInDate, checkOutDate);
            List<Map<String, String>> weatherStatusList = Collections.singletonList(weatherBusinessLogic.getWeatherStatusForDate(weatherData));
            String recommendation = weatherBusinessLogic.generateWeatherRecommendation(weatherStatusList);
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
            List<Map<String, Object>> hotelData = hotelBusinessLogic.getHotelInfoForDates(selectedLocation, checkInDate, checkOutDate);
            String htmlContent = HotelBusinessLogic.buildHotelInfoHTML(hotelData);
            webEngine.loadContent(htmlContent);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Se produjo un error al obtener la información de hoteles. Por favor, inténtalo de nuevo.");
        }
    }

    private VBox createStyledVBox() {
        VBox styledVBox = new VBox(10);
        styledVBox.getStyleClass().add(STYLE_CLASS_VBOX);
        styledVBox.setPadding(new Insets(10));
        return styledVBox;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add(STYLE_CLASS_BUTTON);
        return button;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
