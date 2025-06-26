package io.kyros.util.discord.impl;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TpDelete extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
/*        User user = e.getMessage().getAuthor();
        String[] params = e.getMessage().getContentRaw().toLowerCase().split("-");
        if (params == null || params.length != 1) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String name = params[1];

        for (Sale sale : Listing.getSales(name)) {
            int quantity = sale.getQuantity() - sale.getTotalSold(), saleItem = sale.getId();
            sale.setHasSold(true);
            sale.setLastCollectedSold(0);
            Listing.save(sale);
            ItemCollection.add(name, new GameItem(saleItem, quantity));
        }
        Discord.writeGiveLog("[TP-Delete] " + user.getName() + " deleted " + Misc.capitalizeJustFirst(name) + " Trading post items!");*/

    }

}