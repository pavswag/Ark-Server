package io.kyros.util.discord.impl;

import io.kyros.Server;
import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AddWhitelist extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
        if (string == null || string.length != 2) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String ipAddress = string[1];

        if (!Server.whiteList.contains(ipAddress)) {
            Server.addIP(ipAddress);
            Discord.writeGiveLog("[White-List] Fuck me, you managed it! that ip is now whitelisted!");
        } else {
            Discord.writeGiveLog("[White-List] Well well well, looks like we have a new command, and you've fucked it up!!");
        }
    }
}
