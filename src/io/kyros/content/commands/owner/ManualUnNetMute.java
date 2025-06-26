package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.punishments.PunishmentType;

public class ManualUnNetMute extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Server.getPunishments().removeWithMessage(player, PunishmentType.NET_MUTE, input);
    }
}
