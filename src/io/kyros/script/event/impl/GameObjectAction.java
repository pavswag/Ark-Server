package io.kyros.script.event.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.Config;
import io.kyros.script.event.PlayerEvent;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.ScriptInterpreter;
import io.kyros.runescript.ScriptLoader;
import lombok.Getter;

import java.util.List;

@Getter
public class GameObjectAction extends PlayerEvent {
    private final int option;
    private final int objectId;

    public GameObjectAction(Player player, int objectId, int option) {
        super(player);
        this.objectId = objectId;
        this.option = option;
        execute();
    }

    private void execute() {
        String eventType = "oploc" + option;
        String objectName = getObjectNameById(objectId);
//        System.out.println("Attempting to get script lines for event: " + eventType + " and object: " + objectName);
        List<String> scriptLines = ScriptLoader.getScriptLinesForEvent(eventType, objectName);
        if (scriptLines != null) {
            ScriptContext context = new ScriptContext(getPlayer());
            ScriptInterpreter interpreter = new ScriptInterpreter();
            interpreter.interpret(scriptLines, context);
        } else {
//            System.out.println("No script found for event: " + eventType + " and object: " + objectName + " objectId=" + objectId);
        }
    }

    private String getObjectNameById(int objectId) {
        Config config = ScriptLoader.getObjectConfig("loc_" + objectId);
        if (config != null) {
            return "loc_" + objectId;
        } else {
//            System.out.println("Returning default name, failure");
            return "loc_" + objectId;
        }
    }
}
