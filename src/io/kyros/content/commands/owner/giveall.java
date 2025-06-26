package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 04/03/2024
 */
public class giveall extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] args = input.split("-");
            if (args.length != 2) {
                throw new IllegalArgumentException();
            }
            int itemID = Integer.parseInt(args[0]);
            int amount = Misc.stringToInt(args[1]);

            Set<String> uniqueUUIDs = new HashSet<>();
            ArrayList<Player> filteredPlayers = new ArrayList<>();

            for (Player player1 : Server.getPlayers()) {
                if (player1 != null) {
                    String UUIDAddress = player1.getUUID();
                    if (!uniqueUUIDs.contains(UUIDAddress)) {
                        filteredPlayers.add(player1);
                        uniqueUUIDs.add(UUIDAddress);
                    }
                }
            }

            if (player.debugMessage) {
                player.sendMessage("You have given " + uniqueUUIDs.size() + " / " + Server.getPlayers().size() + " player's " + amount + " x " + ItemDef.forId(itemID).getName());
            }

            for (Player filteredPlayer : filteredPlayers) {
                filteredPlayer.getItems().addItemUnderAnyCircumstance(itemID, amount);
            }

            PlayerHandler.executeGlobalMessage("[@red@LOOOT@bla@] @pur@Everyone has been given " + ItemDef.forId(itemID).getName() + " x " + amount + "!!");
        } catch (Exception e) {
            player.sendMessage("Error. Correct syntax: ::giveall-itemid-amount " + input.split("-").length);
        }
    }
}
