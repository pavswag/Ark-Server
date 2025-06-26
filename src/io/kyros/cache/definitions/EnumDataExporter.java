package io.kyros.cache.definitions;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Map;

enum Category {

    ;

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

public class EnumDataExporter {
    public static void main(String[] args) {
        exportEnumDataToJson();
    }

    private static <T extends Enum<T>> void exportEnumDataToJson() {
        EnumSet<T> enumSet = EnumSet.allOf((Class<T>) Category.class);

        StringBuilder jsonData = new StringBuilder("{\n");
        for (Enum<T> enumConstant : enumSet) {
            String enumName = enumConstant.name();
            String enumDescription = getEnumDescription(enumConstant);
            jsonData.append(String.format("  \"%s\": \"%s\",\n", enumName, enumDescription));
        }
        if (!enumSet.isEmpty()) {
            jsonData.deleteCharAt(jsonData.lastIndexOf(",")); // Remove the trailing comma
        }
        jsonData.append("}");

        try (FileWriter fileWriter = new FileWriter("enum_data.json")) {
            fileWriter.write(jsonData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T extends Enum<T>> String getEnumDescription(Enum<T> enumConstant) {
        try {
            Field descriptionField = enumConstant.getDeclaringClass().getDeclaredField(enumConstant.name());
            descriptionField.setAccessible(true);
            return descriptionField.get(enumConstant).toString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return "";
        }
    }

    static <T extends Enum<T>> void populateEnumWithData(EnumSet<T> enumSet, Map<Integer, Object> params, Class<T> enumClass) {
        for (Enum<T> enumConstant : enumSet) {
            int category = getCategoryValue(enumConstant, enumClass);
            if (params.containsKey(category)) {
                Object data = params.get(category);
                setEnumData(enumConstant, data);
            }
        }
    }

    private static <T extends Enum<T>> int getCategoryValue(Enum<T> enumConstant, Class<T> enumClass) {
        try {
            Field categoryField = enumClass.getDeclaredField(enumConstant.name());
            categoryField.setAccessible(true);
            return categoryField.getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }
    private static void setEnumData(Enum<?> enumConstant, Object data) {
        try {
            Field dataField = enumConstant.getDeclaringClass().getDeclaredField(enumConstant.name());
            dataField.setAccessible(true);
            dataField.set(enumConstant, data);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
