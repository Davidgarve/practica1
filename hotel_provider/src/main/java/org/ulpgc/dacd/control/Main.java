package org.ulpgc.dacd.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;

public class Main {

    private static final String CONFIG_FILE = "hotels.txt";
    private static final String CHECK_IN_DATE = "2023-12-22";
    private static final String CHECK_OUT_DATE = "2023-12-27";
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "hotel.rates";

    public static void main(String[] args) {
        URL resource = Main.class.getClassLoader().getResource(CONFIG_FILE);

        if (resource != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    processHotelConfig(line);
                }
            } catch (IOException e) {
                System.err.println("Error al leer el archivo de configuración: " + e.getMessage());
            }
        } else {
            System.err.println("No se encontró el archivo " + CONFIG_FILE + " en resources.");
        }
    }

    private static void processHotelConfig(String configLine) {
        String[] hotelConfig = configLine.split(",");
        if (hotelConfig.length >= 3) {
            String hotelName = hotelConfig[0].trim();
            String location = hotelConfig[1].trim();
            String hotelKey = hotelConfig[2].trim();

            XoteloSupplier xoteloAPISupplier = new XoteloSupplier(hotelKey);
            JmsHotelStore jmsHotelStore = new JmsHotelStore();
            HotelController hotelController = new HotelController(xoteloAPISupplier, jmsHotelStore, BROKER_URL, TOPIC_NAME);
            hotelController.execute(hotelName, CHECK_IN_DATE, CHECK_OUT_DATE, location, Instant.now());
        } else {
            System.err.println("Formato incorrecto en la línea de configuración: " + configLine);
        }
    }
}