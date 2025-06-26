package io.kyros.runescript;

import io.kyros.annotate.PostInit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptLoader {

    private static final String BASE_DIRECTORY = "./data/runescript";
    private static final Map<String, Config> itemConfigs = new HashMap<>();
    private static final Map<String, Config> objectConfigs = new HashMap<>();
    private static final Map<String, List<String>> scripts = new HashMap<>();
    private static final Map<String, List<String>> globalFunctions = new HashMap<>();
    private static final Map<String, AnimationConfig> animationConfigs = new HashMap<>();

    @PostInit
    public static void load() throws IOException {
        Files.walk(Paths.get(BASE_DIRECTORY))
                .filter(Files::isDirectory)
                .forEach(ScriptLoader::loadDirectory);
    }

    private static void loadDirectory(Path path) {
        try {
            Path configsPath = path.resolve("configs");
            if (Files.exists(configsPath)) {
                Files.walk(configsPath)
                        .filter(Files::isRegularFile)
                        .forEach(ScriptLoader::parseConfigFile);
            }

            Path scriptsPath = path.resolve("scripts");
            if (Files.exists(scriptsPath)) {
                Files.walk(scriptsPath)
                        .filter(Files::isRegularFile)
                        .forEach(ScriptLoader::parseScriptFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseConfigFile(Path path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            String identifier = null;
            String name = null;
            int id = -1;
            int time = -1;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("[")) {
                    identifier = line.substring(1, line.indexOf(']'));
                } else if (line.startsWith("name=")) {
                    name = line.substring(5);
                } else if (line.startsWith("id=")) {
                    id = Integer.parseInt(line.substring(3).trim());
                } else if (line.startsWith("time=")) {
                    time = Integer.parseInt(line.substring(5).trim());
                }
            }

            if (identifier != null && id != -1) {
                if (path.toString().endsWith(".obj")) {
                    Config config = new Config(name, id);
                    itemConfigs.put(identifier, config);
                    System.out.println("Loaded item config: " + identifier + " with name: " + name + " and id: " + id);
                } else if (path.toString().endsWith(".loc")) {
                    Config config = new Config(name, id);
                    objectConfigs.put(identifier, config);
                    System.out.println("Loaded object config: " + identifier + " with name: " + name + " and id: " + id);
                } else if (path.toString().endsWith(".seq")) {
                    AnimationConfig config = new AnimationConfig(id, time);
                    animationConfigs.put(identifier, config);
                    System.out.println("Loaded animation config: " + identifier + " with id: " + id + " and time: " + time);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseScriptFile(Path path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            List<String> actions = new ArrayList<>();
            String eventType = null;
            String eventIdentifier = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("[")) {
                    if (!actions.isEmpty()) {
                        if (eventType != null && eventIdentifier != null) {
                            storeScriptOrFunction(eventType, eventIdentifier, actions);
                        }
                        actions.clear();
                    }
                    if (line.contains(",")) {
                        eventType = line.substring(1, line.indexOf(','));
                        eventIdentifier = line.substring(line.indexOf(',') + 1, line.indexOf(']'));
                    }
                } else if (!line.isEmpty()) {
                    actions.add(line);
                }
            }

            if (!actions.isEmpty() && eventType != null && eventIdentifier != null) {
                storeScriptOrFunction(eventType, eventIdentifier, actions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void storeScriptOrFunction(String eventType, String eventIdentifier, List<String> actions) {
        if (eventType.equals("label")) {
            globalFunctions.put(eventIdentifier, new ArrayList<>(actions));
            System.out.println("Loaded global function: " + eventIdentifier);
        } else {
            scripts.put(eventType + "," + eventIdentifier, new ArrayList<>(actions));
            System.out.println("Loaded script for event: " + eventType + "," + eventIdentifier);
        }
    }

    public static List<String> getScriptLinesForEvent(String eventType, String eventIdentifier) {
        return scripts.get(eventType + "," + eventIdentifier);
    }

    public static List<String> getGlobalFunction(String functionName) {
        return globalFunctions.get(functionName);
    }

    public static Map<String, List<String>> getGlobalFunctions() {
        return globalFunctions;
    }

    public static Config getItemConfig(String name) {
        return itemConfigs.get(name);
    }

    public static Config getObjectConfig(String name) {
        return objectConfigs.get(name);
    }

    public static Map<String, Config> getItemConfigs() {
        return itemConfigs;
    }

    public static Map<String, Config> getObjectConfigs() {
        return objectConfigs;
    }

    public static AnimationConfig getAnimationConfig(String name) {
        return animationConfigs.get(name);
    }

    public static Map<String, AnimationConfig> getAnimationConfigs() {
        return animationConfigs;
    }
}
