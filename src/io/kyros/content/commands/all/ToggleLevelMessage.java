package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * @author Adam
 * @discord thegururspsdev
 * @since 03/05/2024
 */
public class ToggleLevelMessage extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.enableLevelUpMessage = !player.enableLevelUpMessage;
        if(player.enableLevelUpMessage) {
            player.sendMessage("<icon=157> You will now receive messages when you level up.");
        } else {
            player.sendMessage("<icon=157> You will no longer receive messages when you level up.");
        }
    }
}
