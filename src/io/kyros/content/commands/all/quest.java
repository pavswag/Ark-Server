package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.content.questing.QuestInterfaceV2;
import io.kyros.model.entity.player.Player;

public class quest extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        QuestInterfaceV2.openInterface(player);
        player.getQuesting().handleHelpTabActionButton(666);
    }
}
