package io.kyros.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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
    }
}
