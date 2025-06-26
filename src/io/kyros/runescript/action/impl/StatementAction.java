package io.kyros.runescript.action.impl;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.model.entity.player.Player;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;

public class StatementAction implements Action {
    private String message;

    public StatementAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        player.start(new DialogueBuilder(player).statement(message.split("\\|")));
        // Implement chat logic here
    }
}

