package io.kyros.util.discord.impl;

import io.kyros.model.definitions.ItemDef;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import io.kyros.util.offlinestorage.ItemCollection;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OfflineReward extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        String[] params = e.getMessage().getContentRaw().toLowerCase().split("-");
        if (params == null || params.length != 4) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String name = params[1].toLowerCase();
        int id = Integer.parseInt(params[2]);
        int amount = Integer.parseInt(params[3]);

        ItemCollection.add(name, new GameItem(id, amount));

        Discord.writeOfflineRewardsMessage("[OFFLINE REWARDS] " + user.getName() + " gave " + Misc.capitalizeJustFirst(name) + " Item: " + ItemDef.forId(id).getName() + " x " + amount + " (" + id +")");
    }

}
