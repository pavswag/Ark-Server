package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.teleportv2.inter.TeleportInterface;
import io.kyros.model.entity.player.Player;

public class Telem extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        TeleportInterface.open(player);
    }
}