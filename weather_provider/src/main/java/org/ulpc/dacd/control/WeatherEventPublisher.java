package org.ulpc.dacd.control;

import org.ulpc.dacd.model.Weather;

public interface WeatherEventPublisher {
     void sendWeatherToBroker(Weather weather, String brokerURL, String queueName);
}
