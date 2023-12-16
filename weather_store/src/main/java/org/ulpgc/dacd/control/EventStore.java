package org.ulpgc.dacd.control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class EventStore implements EventStoreBuilder {
    private static final String BASE_DIRECTORY = "eventstore";
    private static final String EVENT_DIRECTORY_PREFIX = "prediction.Weather";
    private static final String EVENT_FILE_EXTENSION = ".events";

    public void storeEvent(String jsonWeather) {
        try {
            JsonObject eventJson = JsonParser.parseString(jsonWeather).getAsJsonObject();
            String ts = eventJson.getAsJsonPrimitive("ts").getAsString();
            String ss = eventJson.getAsJsonPrimitive("ss").getAsString();

            String eventDirectoryName = BASE_DIRECTORY + File.separator +
                    EVENT_DIRECTORY_PREFIX + File.separator +
                    ss;

            String eventDirectoryPath = eventDirectoryName + File.separator;
            String eventFilePath = eventDirectoryPath + formatDate(Instant.parse(ts)) + EVENT_FILE_EXTENSION;
            File directory = new File(eventDirectoryName);
            File eventFile = new File(eventFilePath);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            if (!eventFile.exists()) {
                eventFile.createNewFile();
            }

            try (FileWriter writer = new FileWriter(eventFile, true)) {
                if (eventFile.length() > 0) {
                    writer.write("\n");
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
