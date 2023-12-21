package org.ulpc.dacd.control;
import org.ulpc.dacd.model.Location;

public class Main {

    public static void main(String[] args) {
        String apiKey = args[0];
        OpenWeatherMapSupplier supplier = new OpenWeatherMapSupplier(apiKey);

        Location caletaDeSebo = new Location(29.23036, -13.50536, "La Graciosa");
        Location arrecife = new Location(28.966271, -13.545968, "Lanzarote");
        Location puertoDelRosario = new Location(28.49998, -13.86823, "Fuerteventura");
        Location lasPalmas = new Location(28.12144, -15.43979, "Gran Canaria");
        Location santaCruzTenerife = new Location(28.46140, -16.25852, "Tenerife");
        Location sanSebastian = new Location(28.09274, -17.11118, "La Gomera");
        Location santaCruzLaPalma = new Location(28.68369, -17.76615, "La Palma");
        Location valverde = new Location(27.808402, -17.915343, "La Gomera");

        String brokerURL = "tcp://localhost:61616";
        String topicName = "prediction.weather";

        JmsWeatherStore jmsWeatherStore = new JmsWeatherStore();
        WeatherController weatherController = new WeatherController(supplier, jmsWeatherStore, brokerURL, topicName);
        weatherController.execute(caletaDeSebo, arrecife, puertoDelRosario, lasPalmas, santaCruzTenerife, sanSebastian, santaCruzLaPalma, valverde);
    }


}
