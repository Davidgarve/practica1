package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Weather;

public interface WeatherEventPublisher {
     void sendWeatherToBroker(Weather weather, String brokerURL, String queueName);
}
