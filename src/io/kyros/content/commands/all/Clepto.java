package io.kyros.content.commands.all;

import io.kyros.Configuration;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Clepto extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.CleptNotification = !player.CleptNotification;
        player.sendMessage("[Clepto] Notifications are now: " + (player.CleptNotification ? "enabled" : "disabled") + ".");
    }
}
