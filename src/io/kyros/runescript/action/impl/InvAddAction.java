package io.kyros.runescript.action.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.Config;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.ScriptLoader;
import io.kyros.runescript.action.Action;

public class InvAddAction implements Action {
    private String inventory;
    private int itemId;
    private int amount;

    public InvAddAction(String inventory, String itemName, int amount) {
        this.inventory = inventory;
        Config config = ScriptLoader.getItemConfig(itemName);
        if(config == null) {
            this.itemId = 1;
            System.out.println("could not find item config for item - " + itemName);
        } else {
            this.itemId = config.getId();
        }
        this.amount = amount;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if(inventory.equalsIgnoreCase("inv")) {
            player.getItems().addItem(itemId, amount);
        }
        // Implement inventory add logic here
    }
}

