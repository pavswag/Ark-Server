package io.kyros.util.discord.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UnJail extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
        if (string == null || string.length != 2) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String name = string[1];

        Player p = PlayerHandler.getPlayerByDisplayName(name);

        if (p != null) {
            p.getPA().movePlayer(3093, 3493, 0);
            p.jailEnd = 0;
            p.isStuck = false;
            p.sendMessage("You have been unjailed by " + user.getName() + ". Don't get jailed again!");
            Discord.writeGiveLog("[JAIL] " + user.getName() + " has unJailed " + p.getLoginName() + "/" + p.getDisplayName());
        } else {
            Discord.writeGiveLog("[JAIL] " + name + " has clearly fucking logged out, or you're an idiot and can't spell for shit, I'm telling Ark you fucking idiot.");
        }

    }

}

