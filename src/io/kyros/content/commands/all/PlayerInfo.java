package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.content.playerinformation.Interface;
import io.kyros.model.entity.player.Player;

public class PlayerInfo extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Interface.Open(player);
    }
}
