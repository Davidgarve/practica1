package org.ulpgc.dacd.control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class EventStore {
    private static final String BASE_DIRECTORY = "eventstore";
    private static final String EVENT_DIRECTORY_PREFIX = "prediction.Weather";
    private static final String EVENT_FILE_EXTENSION = ".events";


    public void storeEvent(String jsonWeather) {
        try {
            String eventDirectoryName = BASE_DIRECTORY + File.separator +
                    EVENT_DIRECTORY_PREFIX + File.separator +
                    "OpenWeatherMap" + File.separator;

            String eventDirectoryPath = eventDirectoryName + File.separator;
            String eventFilePath = eventDirectoryPath + formatDate(Instant.now()) + EVENT_FILE_EXTENSION;

            File directory = new File(eventDirectoryName);
            File eventFile = new File(eventFilePath);

            // Crea el directorio si no existe
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Crea el archivo si no existe
            if (!eventFile.exists()) {
                eventFile.createNewFile();
            }

            // Abre el archivo en modo de anexar (append) y escribe el evento
            try (FileWriter writer = new FileWriter(eventFile, true)) {
                // Agrega una coma y un salto de línea si el archivo ya contiene eventos
                if (eventFile.length() > 0) {
                    writer.write(",\n");
                }

                writer.write(jsonWeather);
            }

            System.out.println("Evento almacenado en: " + eventFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatDate(Instant ts) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(Date.from(ts));
    }
}
