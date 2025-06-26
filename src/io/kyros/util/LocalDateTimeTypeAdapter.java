package io.kyros.util;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(formatter));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return LocalDateTime.parse(json.getAsString(), formatter);
        } else if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();

            JsonObject date = jsonObject.getAsJsonObject("date");
            int year = date.get("year").getAsInt();
            int month = date.get("month").getAsInt();
            int day = date.get("day").getAsInt();

            JsonObject time = jsonObject.getAsJsonObject("time");
            int hour = time.get("hour").getAsInt();
            int minute = time.get("minute").getAsInt();
            int second = time.get("second").getAsInt();
            int nano = time.get("nano").getAsInt();

            return LocalDateTime.of(year, month, day, hour, minute, second, nano);
        } else {
            throw new JsonParseException("Unexpected JSON type: " + json.getClass().getSimpleName());
        }
    }
}