package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.content.worldevent.WorldEventInformation;
import io.kyros.model.entity.player.Player;

public class events extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        WorldEventInformation.openInformationInterface(player);
    }
}
