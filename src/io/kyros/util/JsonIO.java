package io.kyros.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public abstract class JsonIO {

    public static final Type HASH_STRING_INTEGER = new TypeToken<HashMap<String, Integer>>() {
    }.getType();

    public static final Gson GSON = new Gson();

    private String location;

    public String name;

    public JsonIO(String location, String name) {
        this.location = location;
        this.name = name;
    }

    public JsonIO(String location) {
        this(location, "");
    }

    public abstract void init(String name, Gson builder, JsonObject reader);

    public abstract JsonObject save(String name, Gson builder, JsonObject object);

    public void initAll() {
        File[] files = new File(location).listFiles();

        if (files.length == 0) {
            return;
        }

        for (File f : files) {
            if (f == null) {
                continue;
            }
            init(f.getName().replaceAll(".json", ""));
        }

        System.out.println("Init directory: " + location + ". Found: " + files.length + " files.");
    }

    public void init() {
        init(name);
    }

    public void init(String name) {

        final Path path = Paths.get(location + "" + name + ".json");
        final File file = path.toFile();

        if (!file.exists()) {
            return;
        }

        try (FileReader fileReader = new FileReader(file)) {
            final JsonParser fileParser = new JsonParser();
            final Gson builder = new GsonBuilder().create();
            final JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            init(name, builder, reader);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void saveFile(String file) {
        save(name + "/" + file);
    }

    public void save() {
        save(name);
    }

    public void save(String name) {
        final Path path = Paths.get(location + "" + name + ".json");
        final File file = path.toFile();

        if (!file.getParentFile().exists()) {
            try {
                file.getParentFile().mkdirs();
            } catch (final SecurityException e) {

            }
        }
        file.getParentFile().setWritable(true);
        try (FileWriter writer = new FileWriter(file)) {

            final Gson builder = new GsonBuilder().setPrettyPrinting().create();
            final JsonObject object = new JsonObject();

            writer.write(builder.toJson(save(name, builder, object)));
            writer.close();

        } catch (final Exception e) {
            System.out.println("An error has occured while saving " + location + " " + name + " file!");
        }
    }

    public String getFile() {
        return location + "" + name + ".json";
    }
}
