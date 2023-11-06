package org.ulpc.dacd.control;


public class Main {
    //Responsabilidades:
    //Crear controladores
    //leer apiKey
    //Cargar localizaciones
    //Crear tarea perodica(cada 6 horas). EJECUTAR TAREA.
    //task()
    public static void main(String[] args) {
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=44.34&lon=10.99&appid=d41a06840247e2449a1ddc74dd6789da";  // Reemplaza con la URL de tu API
        OpenWeatherMapSupplier supplier = new OpenWeatherMapSupplier(apiUrl);
        supplier.fetchData();

    }
}
