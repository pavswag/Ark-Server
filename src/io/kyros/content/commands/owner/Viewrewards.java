package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.util.offlinestorage.ItemCollection;

public class Viewrewards extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
            try {
                String[] args = input.split("-");
                String playerName = args[0];
                ItemCollection.adminView(player, playerName);
            } catch (Exception e) {
                player.sendMessage("Error. Correct syntax: ::viewrewards-playername");
            }
    }
}
