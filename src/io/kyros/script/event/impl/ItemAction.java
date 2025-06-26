package io.kyros.script.event.impl;

import io.kyros.Server;
import io.kyros.cache.definitions.ItemDefinition;
import io.kyros.model.entity.player.Player;
import io.kyros.runescript.Config;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.ScriptInterpreter;
import io.kyros.runescript.ScriptLoader;
import io.kyros.script.event.PlayerEvent;
import lombok.Getter;

import java.util.List;

@Getter
public class ItemAction extends PlayerEvent {
    public ItemAction(Player player, int itemId, int option) {
        super(player);
        this.item = itemId;
        this.option = option;
        execute();
    }

    private void execute() {
        int count = getPlayer().getItems().getInventoryCount(item);
        if (count <= 0) {
            return;
        }
        String eventType = "opheld" + option;
        String itemName = getItemNameById(item);
//        System.out.println("Attempting to get script lines for event: " + eventType + " and object: " + itemName);
        List<String> scriptLines = ScriptLoader.getScriptLinesForEvent(eventType, itemName);
        if (scriptLines != null) {
            ScriptContext context = new ScriptContext(getPlayer());
            context.setIntVariable("selected_item_id", item);
            ScriptInterpreter interpreter = new ScriptInterpreter();
            interpreter.interpret(scriptLines, context);
        } else {
//            System.out.println("No script found for event: " + eventType + " and item name: " + itemName + " itemId=" + item);
        }
    }
    private String getItemNameById(int itemName) {
        Config config = ScriptLoader.getItemConfig("obj_" + itemName);
        if (config != null) {
            return "obj_" + itemName;
        } else {
            config = ScriptLoader.getItemConfig(Server.definitionRepository.get(ItemDefinition.class, itemName).name.toLowerCase().replaceAll(" ", "_"));
            if(config == null) {
//                System.out.println("Returning default name, failure");
                return "obj_" + itemName;
            } else {
                return Server.definitionRepository.get(ItemDefinition.class, itemName).name.toLowerCase().replaceAll(" ", "_");
            }
        }
    }
    private final int option;
    private final int item;

}
