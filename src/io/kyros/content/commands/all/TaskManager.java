package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class TaskManager extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getTaskMaster().showInterface();
        player.getQuesting().handleHelpTabActionButton(667);
    }
}
