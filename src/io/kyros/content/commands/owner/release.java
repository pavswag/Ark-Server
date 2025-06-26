package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.util.discord.Discord;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 05/03/2024
 */
public class release extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Server.ServerLocked = !Server.ServerLocked;
        player.sendMessage("@red@The server is now " + (!Server.ServerLocked ? "unlocked" : "locked") + "!");

        if (!Server.ServerLocked) {
            Discord.writeServerSyncMessage("Server is now online. <@everyone>");
            Discord.writeOnlineNotification("Server is now online. <@everyone>");
        }
    }
}
