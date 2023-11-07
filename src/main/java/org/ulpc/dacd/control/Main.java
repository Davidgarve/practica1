package org.ulpc.dacd.control;
import org.ulpc.dacd.model.Location;
import org.ulpc.dacd.model.Weather;

import java.time.*;

public class Main {
    //Responsabilidades:
    //Crear controladores
    //leer apiKey
    //Cargar localizaciones
    //Crear tarea perodica(cada 6 horas). EJECUTAR TAREA.
    //task()
    public static void main(String[] args) {
        OpenWeatherMapSupplier supplier = new OpenWeatherMapSupplier();
        //Location granCanaria = new Location(28.12144, -15.43979, "Las Palmas de Gran Canaria");
        Location arrecife = new Location(28.966271, -13.545968, "Arrecife");

        Instant actual = Instant.now();
        ZoneId husoHorarioCanarias = ZoneId.of("Atlantic/Canary");
        ZonedDateTime fechaHoraLasPalmas = actual.atZone(husoHorarioCanarias);
        ZonedDateTime fechaHora12PM = fechaHoraLasPalmas.with(LocalTime.of(12, 0, 0));
        actual = fechaHora12PM.toInstant();

        Weather weatherLanzarote = supplier.getWeather(arrecife, actual);
        System.out.println(weatherLanzarote);

        //SqliteWeatherStore sqliteWeatherStore = new SqliteWeatherStore();
        //sqliteWeatherStore.createTable("Lanzarote");
    }
}
