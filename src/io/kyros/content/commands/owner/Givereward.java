package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import io.kyros.util.offlinestorage.ItemCollection;

public class Givereward extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] args = input.split("-");
            if (args.length != 3) {
                throw new IllegalArgumentException();
            }
            String playerName = args[0];
            int itemID = Integer.parseInt(args[1]);
            int amount = Misc.stringToInt(args[2]);

            ItemCollection.add(playerName, new GameItem(itemID, amount));
            player.sendMessage("You have given " + playerName + " " + ItemDef.forId(itemID).getName() + " x " + amount + "!");
            Discord.writeOfflineRewardsMessage("[OFFLINE REWARDS] " + player.getDisplayName() + " has given " + playerName + " " + ItemDef.forId(itemID).getName() + " x " + amount + "!");

        } catch (Exception e) {
            player.sendMessage("Error. Correct syntax: ::givereward-player-itemid-amount");
        }
    }
}
