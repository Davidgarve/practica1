package org.ulpc.dacd.control;
import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import java.time.Instant;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String apiKey = "d41a06840247e2449a1ddc74dd6789da";
        OpenWeatherMapSupplier supplier = new OpenWeatherMapSupplier(apiKey);

        Location caletaDeSebo = new Location(29.23036, -13.50536, "Caleta de Sebo");
        Location arrecife = new Location(28.966271, -13.545968, "Arrecife");
        Location puertoDelRosario = new Location(28.49998, -13.86823, "Puerto del Rosario");
        Location lasPalmas = new Location(28.12144, -15.43979, "Las Palmas de Gran Canaria");
        Location santaCruzTenerife = new Location(28.46140, -16.25852, "Santa Cruz de Tenerife");
        Location sanSebastian = new Location(28.09274, -17.11118, "San Sebastián de la Gomera");
        Location santaCruzLaPalma = new Location(28.68369, -17.76615, "Santa Cruz de la Palma");
        Location valverde = new Location(27.808402, -17.915343, "Valverde");

        // Configurar la conexión con ActiveMQ
        String brokerURL = "tcp://localhost:61616"; // Ajusta según tu configuración
        String queueName = "prediction.weather"; // Ajusta el nombre de la cola

        // Crear instancias de WeatherController y JmsWeatherStore
        WeatherController weatherController = new WeatherController(supplier, new JmsWeatherStore());

        // Ejecutar el método execute con la ubicación de Arrecife
        weatherController.execute(arrecife);

    }


}
