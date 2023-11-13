package org.ulpc.dacd.control;
import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import java.time.*;
import java.util.List;

public class Main {
    //Responsabilidades:
    //Crear controladores
    //leer apiKey
    //Cargar localizaciones
    //Crear tarea perodica(cada 6 horas). EJECUTAR TAREA.
    //task()

    public static void main(String[] args) {
        OpenWeatherMapSupplier supplier = new OpenWeatherMapSupplier();
        SqliteWeatherStore sqliteWeatherStore = new SqliteWeatherStore();

        Location caletaDeSebo = new Location(29.23036, -13.50536, "Caleta de Sebo");
        Location arrecife = new Location(28.966271, -13.545968, "Arrecife");
        Location puertoDelRosario = new Location(28.49998, -13.86823, "Puerto del Rosario");
        Location lasPalmas = new Location(28.12144, -15.43979, "Las Palmas de Gran Canaria");
        Location santaCruzTenerife = new Location(28.46140, -16.25852, "Santa Cruz de Tenerife");
        Location sanSebastian = new Location(28.09274, -17.11118, "San Sebastián de la Gomera");
        Location santaCruzLaPalma = new Location(28.68369, -17.76615, "Santa Cruz de la Palma");
        Location valverde = new Location(27.808402, -17.915343, "Valverde");

        WeatherController weatherController = new WeatherController(supplier, sqliteWeatherStore);
        weatherController.execute(caletaDeSebo, arrecife, puertoDelRosario, lasPalmas, santaCruzTenerife, sanSebastian, santaCruzLaPalma, valverde);
    }
}
