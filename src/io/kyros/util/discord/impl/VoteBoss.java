package io.kyros.util.discord.impl;

import io.kyros.content.commands.helper.vboss;
import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoteBoss extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();
        vboss.spawnBoss();
        Discord.writeGiveLog("[Vote Boss] " + user.getName() + " has spawned Vote Boss!");
    }

}
