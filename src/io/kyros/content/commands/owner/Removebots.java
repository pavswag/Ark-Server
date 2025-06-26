package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.util.discord.Discord;

import java.util.Optional;

public class Removebots extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (!player.getRights().contains(Right.STAFF_MANAGER)) {
            player.sendMessage("Only owners can use this command.");
            return;
        }

        player.sendMessage("Logging out bots.");
        Server.getPlayers().nonNullStream().forEach(plr -> {
            if (plr.isBot()) {

                Discord.writeSuggestionMessage(plr.getDisplayName() + " is a bot!");
                plr.forceLogout();
            }
        });
    }

    public Optional<String> getDescription() {
        return Optional.of("Create a new setup, needs a name too!");
    }
}
