package io.kyros.runescript.action.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;

public class CloseWidgetsAction implements Action {

    public CloseWidgetsAction() {
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        player.getPA().closeAllWindows();
    }
}

