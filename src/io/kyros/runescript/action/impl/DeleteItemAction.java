package io.kyros.runescript.action.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;

public class DeleteItemAction implements Action {

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        player.getItems().deleteItem2(context.getIntVariable("selected_item_id"), 1);
    }
}

