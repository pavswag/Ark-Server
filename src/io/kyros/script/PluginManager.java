package io.kyros.script;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.message.MessageBuilder;
import io.kyros.model.entity.player.message.MessageColor;
import io.kyros.script.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PluginManager {
    private final Globals globals;
    private final List<LuaValue> scripts;
    private final Path scriptDirectory;
    private int totalScripts = 0;

    public PluginManager(String scriptDirectoryPath) {
        globals = JsePlatform.standardGlobals();
        scripts = new ArrayList<>();
        scriptDirectory = Paths.get(scriptDirectoryPath).toAbsolutePath();

        // Set Lua package path
        String packagePath = scriptDirectory.toString().replace("\\", "/") + "/?.lua;" + scriptDirectory.toString().replace("\\", "/") + "/?/init.lua;";
        globals.get("package").set("path", LuaValue.valueOf(packagePath + globals.get("package.path").tojstring()));

        loadScript(new File(scriptDirectory.toFile(), "ScriptLoader.lua"));
        loadScripts(new File(scriptDirectory.toFile(), "api"));

        loadRemainingScripts(scriptDirectory.toFile());
        globals.get("ScriptLoader").get("autoRegister").call();

        MessageBuilder messageBuilder = new MessageBuilder()
                .bracketed("PLUGINS", MessageColor.RED)
                .color(MessageColor.YELLOW)
                .text(" " + totalScripts + " ")
                .color(MessageColor.BLACK)
                .text("plugins have been reloaded.");
        PlayerHandler.executeGlobalStaffMessage(messageBuilder.build());
    }

    public static void start() {
        Server.pluginManager = new PluginManager("./data/scripts/");
    }

    private void loadScripts(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Script directory does not exist or is not a directory");
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    loadScripts(file); // Recursively load scripts from subdirectories
                } else if (file.isFile() && file.getName().endsWith(".lua") && !file.getName().equals("ScriptLoader.lua")) {
                    loadScript(file); // Load Lua scripts
                }
            }
        }
    }

    private void loadRemainingScripts(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Script directory does not exist or is not a directory");
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && !file.getName().equals("api")) {
                    loadScripts(file); // Recursively load scripts from subdirectories except api
                } else if (file.isFile() && file.getName().endsWith(".lua") && !file.getName().equals("ScriptLoader.lua")) {
                    loadScript(file); // Load Lua scripts
                }
            }
        }
    }

    private void loadScript(File file) {
        try {
            LuaValue chunk = globals.loadfile(file.getAbsolutePath());
            chunk.call();
            totalScripts++;
//            System.out.println("Script [" + file.getName() + "] loaded.");
        } catch (Exception e) {
            log.error("Failed to load script [{}]", file.getName(), e);
        }
    }

    public void triggerEvent(Event event) {
        String eventName = "on" + event.getClass().getSimpleName();
//        log.info("Sending event [{}]", eventName);
        LuaValue luaEvent = CoerceJavaToLua.coerce(event);
        globals.get("ScriptLoader").get("call").call(LuaValue.valueOf(eventName), luaEvent);
    }
}
