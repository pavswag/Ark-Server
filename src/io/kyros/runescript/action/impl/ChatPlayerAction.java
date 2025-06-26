package io.kyros.runescript.action.impl;

import io.kyros.content.dialogue.DialogueBuilder;
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
        System.out.println("Chatting to player " + player.getName() + ": " + message);
        player.start(new DialogueBuilder(player).player(message.split("\\|")));
        // Implement chat logic here
    }
}

