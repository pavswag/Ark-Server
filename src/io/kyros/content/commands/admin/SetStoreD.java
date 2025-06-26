package io.kyros.content.commands.admin;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 31/01/2024
 */
public class SetStoreD extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] data = input.split("-");
            Player recipient = PlayerHandler.getPlayerByDisplayName(data[0]);
            int amount = Integer.parseInt(data[1]);
            boolean remove = Boolean.parseBoolean(data[2]);

            if (recipient == null) {
                player.sendMessage("No player online with name: " + data[0]);
                return;
            }

            if (amount <= 0) {
                player.sendMessage("You cannot assign a value of 0!");
                return;
            }

            if (remove) {
                recipient.setStoreDonated(recipient.getStoreDonated() + amount);
                player.sendMessage("You have increased " + recipient.getDisplayName() + " Store donation amount by " + amount +", total: " + recipient.getStoreDonated());
                recipient.sendMessage("Your store donation has been increase by " + amount + ", Total: " +recipient.getStoreDonated());
            } else {
                recipient.setStoreDonated(recipient.getStoreDonated() - amount);
                player.sendMessage("You have decreased " + recipient.getDisplayName() + " Store donation amount by " + amount +", total: " + recipient.getStoreDonated());
                recipient.sendMessage("Your store donation has been decreased by " + amount + ", Total: " +recipient.getStoreDonated());
            }

        } catch (Exception e) {
            player.sendMessage("Error occurred, usage: ::setstored-name-amount-true/false");
        }
    }
}
