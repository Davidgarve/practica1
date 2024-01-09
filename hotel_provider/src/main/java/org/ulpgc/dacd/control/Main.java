package org.ulpgc.dacd.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;

public class Main {

    public static void main(String[] args) {
        String hotelFile = "hotels.txt";
        URL resource = Main.class.getClassLoader().getResource(hotelFile);

        if (resource != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    processHotelConfig(line);
                }
            } catch (IOException e) {
                System.err.println("Error reading configuration file: " + e.getMessage());
            }
        } else {
            System.err.println("File not found " + hotelFile + " in resources.");
        }
    }

    private static void processHotelConfig(String configLine) {
        String[] hotelConfig = configLine.split(",");
        if (hotelConfig.length >= 3) {
            String hotelName = hotelConfig[0].trim();
            String location = hotelConfig[1].trim();
            String hotelKey = hotelConfig[2].trim();

            String checkIn = Instant.now().toString().substring(0, 10);
            Instant checkOutInstant = Instant.now().plusMillis(5 * 24 * 60 * 60 * 1000);
            String checkOut = checkOutInstant.toString().substring(0, 10);

            XoteloSupplier xoteloAPISupplier = new XoteloSupplier(hotelKey);
            JmsHotelStore jmsHotelStore = new JmsHotelStore();
            String brokerURL = "tcp://localhost:61616";
            String topicName = "hotel.rates";
            long refreshFrequency = 6 * 60 * 60 * 1000;
            HotelController hotelController = new HotelController(xoteloAPISupplier, jmsHotelStore, brokerURL, topicName, refreshFrequency);
            hotelController.execute(hotelName, checkIn, checkOut, location, Instant.now());
        } else {
            System.err.println("Incorrect formatting on configuration line: " + configLine);
        }
    }
}