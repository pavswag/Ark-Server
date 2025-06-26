package io.kyros.runescript.action.impl;

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
        System.out.println("Message to player " + player.getName() + ": " + message);
        player.sendMessage(message);
        // Implement message logic here
    }
}

