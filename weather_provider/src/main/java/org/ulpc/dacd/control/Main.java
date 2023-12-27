package org.ulpc.dacd.control;
import org.ulpc.dacd.model.Location;

public class Main {

    public static void main(String[] args) {
        String apiKey = args[0];
        OpenWeatherMapSupplier supplier = new OpenWeatherMapSupplier(apiKey);

        Location madrid = new Location(40.41581172250397, -3.6927121960696088,  "Madrid");
        Location barcelona = new Location(41.387120696877425, 2.1960018069652363, "Barcelona");
        Location valencia = new Location(39.47266732471288, -0.36124752700365187, "Valencia");
        Location oviedo = new Location(43.36293697609066, -5.8563961741044075, "Oviedo");
        Location marbella = new Location(36.50799477649729, -4.905502059451248, "Marbella");
        Location mallorca = new Location(39.565888338910476, 2.652656263464681, "Mallorca");
        Location bilbao = new Location(43.26725507112307, -2.9341964108977976, "Bilbao");
        Location tenerife = new Location(28.12094382231223, -16.774909594488363, "Tenerife");

        String brokerURL = "tcp://localhost:61616";
        String topicName = "prediction.weather";

        JmsWeatherStore jmsWeatherStore = new JmsWeatherStore();
        WeatherController weatherController = new WeatherController(supplier, jmsWeatherStore, brokerURL, topicName);
        weatherController.execute(madrid, barcelona, valencia, oviedo, marbella, mallorca, bilbao, tenerife);
    }


}
