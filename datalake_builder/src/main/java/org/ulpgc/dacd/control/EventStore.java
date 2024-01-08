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
    private final String BASE_DIRECTORY;

    public EventStore(String baseDirectory) {
        this.BASE_DIRECTORY = baseDirectory;
    }

    private static final String EVENT_FILE_EXTENSION = ".events";

    public void storeEvent(String json, String topic) {
        try {
            JsonObject eventJson = JsonParser.parseString(json).getAsJsonObject();
            String ts = eventJson.getAsJsonPrimitive("ts").getAsString();
            String ss = eventJson.getAsJsonPrimitive("ss").getAsString();

            String eventDirectoryName = BASE_DIRECTORY + File.separator + topic + File.separator + ss;
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
                writer.write(json);
            }

            System.out.println("Event stored in: " + eventFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatDate(Instant ts) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(Date.from(ts));
    }
}