package io.kyros.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.sql.dailytracker.DailyDataTracker;
import io.kyros.sql.dailytracker.TrackerType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: ArkCane
 * Social: Discord: ArkCane
 * Website: www.arkcane.net
 * Since: 18/03/2024
 */
public class DataStorage {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private static final Path DATA_FILE = Paths.get(Server.getDataDirectory() + "/cfg/data.json");
    private static final Map<String, JsonElement> dataMap = new HashMap<>();

    public static void saveData(String category, Object data) {
        dataMap.put(category, gson.toJsonTree(data));
        saveToFile();
    }

    public static <T> T loadData(String category, Class<T> dataType) {
        loadFromFile();
        JsonElement jsonElement = dataMap.get(category);
        if (jsonElement != null) {
            return gson.fromJson(jsonElement, dataType);
        }
        return null;
    }

    private static void saveToFile() {
        new Thread(() -> {
            String jsonData = gson.toJson(dataMap);
            try {
                Files.write(DATA_FILE, jsonData.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void loadFromFile() {
        if (!Files.exists(DATA_FILE)) {
            return;
        }

        try {
            byte[] jsonData = Files.readAllBytes(DATA_FILE);
            String jsonString = new String(jsonData);
            Type type = new TypeToken<Map<String, JsonElement>>() {}.getType();
            Map<String, JsonElement> dataObjectMap = gson.fromJson(jsonString, type);

            // Clear the existing data map
            dataMap.clear();
            dataMap.putAll(dataObjectMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostInit
    public static void loadDataSpecific() {
        DailyDataTracker.today = loadData("today", LocalDate.class);
        for (TrackerType trackerType : TrackerType.values()) {
            Integer trackerData = loadData(trackerType.name(), Integer.class);
            if (trackerData != null) {
                trackerType.setTrackerData(trackerData);
            }
        }
    }
}
