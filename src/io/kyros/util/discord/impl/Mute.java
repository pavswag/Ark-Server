package io.kyros.util.discord.impl;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.save.PlayerAddresses;
import io.kyros.punishments.PunishmentType;
import io.kyros.util.dateandtime.TimeSpan;
import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class Mute extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
        if (string == null || string.length != 2) {
            user.openPrivateChannel().queue((channel) -> channel.sendMessage("Invalid entry").queue());
            return;
        }
        String name = string[1];

        Player player = PlayerHandler.getPlayerByDisplayName(name);
        if (player != null) {
            TimeSpan timeSpan = new TimeSpan(TimeUnit.DAYS, 1);
            PlayerAddresses addresses = player.getValidAddresses();
            Server.getPunishments().add(PunishmentType.NET_MUTE, timeSpan, addresses.getIp());
            if (addresses.getMac() != null)
                Server.getPunishments().add(PunishmentType.NET_MUTE, timeSpan, addresses.getMac());
            if (addresses.getUUID() != null)
                Server.getPunishments().add(PunishmentType.NET_MUTE, timeSpan, addresses.getUUID());

            player.sendMessage(player.getDisplayName() + " You have been muted by : " + user.getName());

            Discord.writeGiveLog("[Mute-Log] " + user.getName() + " Muted " + player.getDisplayName());
        } else {
            Discord.writeGiveLog("[Mute-Log] Oh you're a special one aren't you, Either they don't exist or they're offline.");
        }
    }

}

