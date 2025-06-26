package io.kyros.runescript.action.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;

public class ResetDailyTaskAction implements Action {
    private boolean resetScroll;

    public ResetDailyTaskAction(boolean resetScroll) {
        this.resetScroll = resetScroll;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if(resetScroll && !player.getItems().playerHasItem(20238)) {
            player.sendMessage("You do not have the required items to reset your daily tasks.");
            return;
        }
        player.getTaskMaster().taskMasterKillsList.clear();
        player.getTaskMaster().generateTasks(player, resetScroll);
    }
}

