#!/bin/bash

# Define the base directory and package name
BASE_DIR="./src/io/kyros/runescript"
ACTION_DIR="$BASE_DIR/action"
ACTION_IMPL_DIR="$ACTION_DIR/impl"
PACKAGE_NAME="package io.kyros.runescript;"
ACTION_PACKAGE_NAME="package io.kyros.runescript.action;"
ACTION_IMPL_PACKAGE_NAME="package io.kyros.runescript.action.impl;"

# Create the directory structure
mkdir -p $ACTION_IMPL_DIR

# Remove existing files
rm -rf $BASE_DIR/*.java $ACTION_DIR/*.java $ACTION_IMPL_DIR/*.java

# Define the files and their contents
declare -A files=(
    ["$ACTION_DIR/Action.java"]="$ACTION_PACKAGE_NAME

import io.kyros.runescript.ScriptContext;

public interface Action {
    void execute(ScriptContext context);
}
"
    ["$ACTION_IMPL_DIR/AnimAction.java"]="$ACTION_IMPL_PACKAGE_NAME

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;

public class AnimAction implements Action {
    private String animation;
    private int delay;

    public AnimAction(String animation, int delay) {
        this.animation = animation;
        this.delay = delay;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        System.out.println(\"Playing animation \" + animation + \" for player \" + player.getName() + \" with delay \" + delay);
        // Implement animation logic here
    }
}
"
    ["$ACTION_IMPL_DIR/AssignAction.java"]="$ACTION_IMPL_PACKAGE_NAME

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.ExpressionEvaluator;
import io.kyros.runescript.action.Action;

public class AssignAction implements Action {
    private String varName;
    private String expression;

    public AssignAction(String varName, String expression) {
        this.varName = varName;
        this.expression = expression;
    }

    @Override
    public void execute(ScriptContext context) {
        int value = ExpressionEvaluator.evaluate(expression, context);
        context.getVariables().put(varName, value);
        System.out.println(\"Assigned int \" + varName + \" = \" + value);
    }
}
"
    ["$ACTION_IMPL_DIR/ChatPlayerAction.java"]="$ACTION_IMPL_PACKAGE_NAME

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;

public class ChatPlayerAction implements Action {
    private String message;

    public ChatPlayerAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        System.out.println(\"Chatting to player \" + player.getName() + \": \" + message);
        // Implement chat logic here
    }
}
"
    ["$ACTION_IMPL_DIR/DamageSelfAction.java"]="$ACTION_IMPL_PACKAGE_NAME

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;

public class DamageSelfAction implements Action {
    private int damage;

    public DamageSelfAction(int damage) {
        this.damage = damage;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        System.out.println(\"Damaging player \" + player.getName() + \" for \" + damage + \" points);
        // Implement damage logic here
    }
}
"
    ["$ACTION_IMPL_DIR/DelayAction.java"]="$ACTION_IMPL_PACKAGE_NAME

import io.kyros.runescript.ScriptContext;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;
import io.kyros.runescript.ScriptInterpreter;
import io.kyros.runescript.action.Action;

public class DelayAction implements Action {
    private int cycles;

    public DelayAction(int cycles) {
        this.cycles = cycles;
    }

    @Override
    public void execute(ScriptContext context) {
        context.setPaused(true);
        TaskManager.submit(new Task(cycles) {
            @Override
            protected void execute() {
                context.setPaused(false);
                ScriptInterpreter.resumeScript(context);
            }
        });
    }
}
"
    ["$ACTION_IMPL_DIR/InvAddAction.java"]="$ACTION_IMPL_PACKAGE_NAME

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.Config;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.ScriptParser;
import io.kyros.runescript.action.Action;

public class InvAddAction implements Action {
    private String inventory;
    private int itemId;
    private int amount;

    public InvAddAction(String inventory, String itemName, int amount) {
        this.inventory = inventory;
        Config config = ScriptLoader.getItemConfig(itemName);
        this.itemId = config.getId();
        this.amount = amount;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        System.out.println(\"Adding \" + amount + \" of item ID \" + itemId + \" to \" + inventory + \" for player \" + player.getName());
        // Implement inventory add logic here
    }
}
"
    ["$ACTION_IMPL_DIR/MesAction.java"]="$ACTION_IMPL_PACKAGE_NAME

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;

public class MesAction implements Action {
    private String message;

    public MesAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        System.out.println(\"Message to player \" + player.getName() + \": \" + message);
        // Implement message logic here
    }
}
"
    ["$BASE_DIR/Config.java"]="$PACKAGE_NAME

public class Config {
    private String name;
    private int id;

    public Config(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
"
    ["$BASE_DIR/ExpressionEvaluator.java"]="$PACKAGE_NAME

import io.kyros.runescript.ScriptContext;
import io.kyros.util.Misc;

public class ExpressionEvaluator {
    public static int evaluate(String expression, ScriptContext context) {
        expression = expression.trim();
        if (expression.startsWith(\"random(\") && expression.endsWith(\")\")) {
            int bound = Integer.parseInt(expression.substring(7, expression.length() - 1).trim());
            return Misc.random(bound);
        }
        // Add more expression handling as needed
        return 0; // Default return value if expression is not recognized
    }
}
"
    ["$BASE_DIR/ScriptContext.java"]="$PACKAGE_NAME

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.action.Action;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ScriptContext {
    private Player player;
    private Map<String, Integer> variables = new HashMap<>();
    private Queue<Action> actionQueue = new LinkedList<>();
    private boolean paused = false;

    public ScriptContext(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Map<String, Integer> getVariables() {
        return variables;
    }

    public void addAction(Action action) {
        actionQueue.add(action);
    }

    public Action getNextAction() {
        return actionQueue.poll();
    }

    public boolean hasNextAction() {
        return !actionQueue.isEmpty();
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
"
    ["$BASE_DIR/ScriptInterpreter.java"]="$PACKAGE_NAME

import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.AnimAction;
import io.kyros.runescript.action.impl.AssignAction;
import io.kyros.runescript.action.impl.ChatPlayerAction;
import io.kyros.runescript.action.impl.DamageSelfAction;
import io.kyros.runescript.action.impl.DelayAction;
import io.kyros.runescript.action.impl.InvAddAction;
import io.kyros.runescript.action.impl.MesAction;
import java.util.List;

public class ScriptInterpreter {
    public void interpret(List<String> scriptLines, ScriptContext context) {
        for (String line : scriptLines) {
            Action action = parseLine(line, context);
            if (action != null) {
                context.addAction(action);
            }
        }
        executeNextAction(context);
    }

    public static void resumeScript(ScriptContext context) {
        if (context.isPaused()) {
            return;
        }
        executeNextAction(context);
    }

    private static void executeNextAction(ScriptContext context) {
        while (context.hasNextAction() && !context.isPaused()) {
            Action action = context.getNextAction();
            action.execute(context);
        }
    }

    private Action parseLine(String line, ScriptContext context) {
        line = line.trim();
        if (line.startsWith(\"anim(\")) {
            String[] parts = line.substring(5, line.length() - 1).split(\",\");
            return new AnimAction(parts[0].trim(), Integer.parseInt(parts[1].trim()));
        } else if (line.startsWith(\"p_delay(\")) {
            int cycles = Integer.parseInt(line.substring(8, line.length() - 1).trim());
            return new DelayAction(cycles);
        } else if (line.startsWith(\"def_int\")) {
            String[] parts = line.split(\" \");
            String varName = parts[1];
            String expression = line.substring(line.indexOf(\"=\") + 1).trim().replace(\";\", \"\");
            return new AssignAction(varName, expression);
        } else if (line.startsWith(\"~damage_self(\")) {
            int damage = Integer.parseInt(line.substring(13, line.length() - 1).trim());
            return new DamageSelfAction(damage);
        } else if (line.startsWith(\"~chatplayer(\")) {
            String message = line.substring(12, line.length() - 1).trim();
            return new ChatPlayerAction(message);
        } else if (line.startsWith(\"inv_add(\")) {
            String[] parts = line.substring(8, line.length() - 1).split(\",\");
            return new InvAddAction(parts[0].trim(), parts[1].trim(), Integer.parseInt(parts[2].trim()));
        } else if (line.startsWith(\"mes(\")) {
            String message = line.substring(4, line.length() - 1).trim();
            return new MesAction(message);
        }
        // Handle if, else if, else logic as well
        // ...
        return null;
    }
}
"
    ["$BASE_DIR/ScriptLoader.java"]="$PACKAGE_NAME

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

    private static final String BASE_DIRECTORY = \"./data/runescript\";
    private static final Map<String, Config> itemConfigs = new HashMap<>();
    private static final Map<String, Config> objectConfigs = new HashMap<>();
    private static final Map<String, List<String>> scripts = new HashMap<>();

    public static void load() throws IOException {
        Files.walk(Paths.get(BASE_DIRECTORY))
            .filter(Files::isDirectory)
            .forEach(ScriptLoader::loadDirectory);
    }

    private static void loadDirectory(Path path) {
        try {
            Files.walk(path.resolve(\"configs\"))
                .filter(Files::isRegularFile)
                .forEach(ScriptLoader::parseConfigFile);

            Files.walk(path.resolve(\"scripts\"))
                .filter(Files::isRegularFile)
                .forEach(ScriptLoader::parseScriptFile);
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

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(\"[\")) {
                    identifier = line.substring(1, line.indexOf(']'));
                } else if (line.startsWith(\"name=\")) {
                    name = line.substring(5);
                } else if (line.startsWith(\"id=\")) {
                    id = Integer.parseInt(line.substring(3).trim());
                }
            }

            if (identifier != null && name != null && id != -1) {
                Config config = new Config(name, id);
                if (path.toString().endsWith(\".obj\")) {
                    itemConfigs.put(identifier, config);
                } else if (path.toString().endsWith(\".loc\")) {
                    objectConfigs.put(identifier, config);
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
                if (line.startsWith(\"[\")) {
                    if (!actions.isEmpty() && eventType != null && eventIdentifier != null) {
                        scripts.put(eventType + \",\" + eventIdentifier, new ArrayList<>(actions));
                        actions.clear();
                    }
                    if (line.contains(\",\")) {
                        eventType = line.substring(1, line.indexOf(','));
                        eventIdentifier = line.substring(line.indexOf(',') + 1, line.indexOf(']'));
                    }
                } else if (!line.isEmpty()) {
                    actions.add(line);
                }
            }

            if (!actions.isEmpty() && eventType != null && eventIdentifier != null) {
                scripts.put(eventType + \",\" + eventIdentifier, new ArrayList<>(actions));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getScriptLinesForEvent(String eventType, String eventIdentifier) {
        return scripts.get(eventType + \",\" + eventIdentifier);
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
}
"
    ["$BASE_DIR/TaskManager.java"]="$PACKAGE_NAME

import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;

abstract class Task implements Runnable {
    private int cycles;

    public Task(int cycles) {
        this.cycles = cycles;
    }

    protected abstract void execute();

    @Override
    public void run() {
        execute();
    }

    public int getCycles() {
        return cycles;
    }
}

class TaskManager {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public static void submit(Task task) {
        scheduler.schedule(task, task.getCycles() * 600, TimeUnit.MILLISECONDS); // assuming 600ms per cycle
    }
}
"
    ["$BASE_DIR/Utility.java"]="$PACKAGE_NAME

import io.kyros.util.Misc;

public class Utility {
    public static int random(int bound) {
        return Misc.random(bound);
    }
}
"
    ["$BASE_DIR/InventoryType.java"]="$PACKAGE_NAME

public enum InventoryType {
    INVENTORY,
    BANK,
    EQUIPMENT
}
"
)

# Create the files with their respective content
for file in "${!files[@]}"; do
    echo "${files[$file]}" > $file
done

echo "Files created successfully."
