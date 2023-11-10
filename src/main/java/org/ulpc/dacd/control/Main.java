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
        Location caletaDeSebo = new Location(29.23036, -13.50536, "Caleta de Sebo");
        Location arrecife = new Location(28.966271, -13.545968, "Arrecife");
        Location puertoDelRosario = new Location(28.49998, -13.86823, "Puerto del Rosario");
        Location lasPalmas = new Location(28.12144, -15.43979, "Las Palmas de Gran Canaria");
        Location santaCruzTenerife = new Location(28.46140, -16.25852, "Santa Cruz de Tenerife");
        Location sanSebastian = new Location(28.09274, -17.11118, "San Sebastián de la Gomera");
        Location santaCruzLaPalma = new Location(28.68369, -17.76615, "Santa Cruz de la Palma");
        Location valverde = new Location(27.808402, -17.915343, "Valverde");


        SqliteWeatherStore sqliteWeatherStore = new SqliteWeatherStore();

        //List<Weather> weatherLaGraciosa = supplier.getWeather(caletaDeSebo, Instant.now());
        List<Weather> weatherLanzarote = supplier.getWeather(arrecife, Instant.now());
        List<Weather> weatherFuerteventura = supplier.getWeather(puertoDelRosario, Instant.now());
        List<Weather> weatherGranCanaria = supplier.getWeather(lasPalmas, Instant.now());
        List<Weather> weatherTenerife = supplier.getWeather(santaCruzTenerife, Instant.now());
        List<Weather> weatherLaGomera = supplier.getWeather(sanSebastian, Instant.now());
        List<Weather> weatherLaPalma = supplier.getWeather(santaCruzLaPalma, Instant.now());
        List<Weather> weatherElHierro = supplier.getWeather(valverde, Instant.now());

        //sqliteWeatherStore.createTable("La_Graciosa");
        sqliteWeatherStore.createTable("Lanzarote");
        sqliteWeatherStore.createTable("Fuerteventura");
        sqliteWeatherStore.createTable("Gran_Canaria");
        sqliteWeatherStore.createTable("Tenerife");
        sqliteWeatherStore.createTable("La_Gomera");
        sqliteWeatherStore.createTable("La_Palma");
        sqliteWeatherStore.createTable("El_Hierro");


        //sqliteWeatherStore.insertWeather("La_Graciosa", weatherLaGraciosa);
        sqliteWeatherStore.insertWeather("Lanzarote", weatherLanzarote);
        sqliteWeatherStore.insertWeather("Fuerteventura", weatherFuerteventura);
        sqliteWeatherStore.insertWeather("Gran_Canaria", weatherGranCanaria);
        sqliteWeatherStore.insertWeather("Tenerife", weatherTenerife);
        sqliteWeatherStore.insertWeather("La_Gomera", weatherLaGomera);
        sqliteWeatherStore.insertWeather("La_Palma", weatherLaPalma);
        sqliteWeatherStore.insertWeather("El_Hierro", weatherElHierro);
    }
}
