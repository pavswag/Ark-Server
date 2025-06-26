package io.kyros.util.discord.impl;

import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Groot extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        io.kyros.content.activityboss.impl.Groot.spawnGroot();
        Discord.writeGiveLog("[Groot] " + user.getName() + " has spawned groot!");
    }

}