package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class tpdelete extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        /*try {
            String[] args = input.split("-");
            if (args.length != 1) {
                throw new IllegalArgumentException();
            }
            String playerName = args[0];

            for (Sale sale : Listing.getSales(playerName)) {
                int quantity = sale.getQuantity() - sale.getTotalSold(), saleItem = sale.getId();
                    sale.setHasSold(true);
                    sale.setLastCollectedSold(0);
                    Listing.save(sale);
                    ItemCollection.add(playerName, new GameItem(saleItem, quantity));
                    c.sendMessage("ItemRemoved from Trading Post " + ItemDef.forId(saleItem).getName());
            }
        } catch (Exception e) {
            c.sendMessage("Error. Correct syntax: ::tpdelete-name");
            e.printStackTrace();
        }*/
    }
}
