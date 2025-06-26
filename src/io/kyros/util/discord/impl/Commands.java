package io.kyros.util.discord.impl;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class Commands extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        User user = e.getMessage().getAuthor();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Command List");

        eb.setColor(Color.red);
        eb.setColor(new Color(0xF4840C));
        eb.setColor(new Color(255, 136, 0));
        eb.addField("::giveitem-user-id-amount","Give's an item to a player",true);
        eb.addField("::tpdelete-user","Removes a player's Trading post listing",true);
        eb.addField("::ban-user","Network ban's a user",true);
        eb.addField("::unban-user","Removes Network ban from user",true);
        eb.addField("::Mute-user","Network mute's a user",true);
        eb.addField("::jail-user","Jails a user",true);
        eb.addField("::unmute-user","Unmute's a user",true);
        eb.addField("::unjail-user","Unjail's a user",true);
        eb.addField("::groot","Spawns Groot",true);
        eb.addField("::voteboss","Spawns Vote Boss",true);
        eb.addField("::donorboss","Spawns Donor Boss",true);
        eb.addField("::of-user-id-amount","Give's user there reward to offline box",true);
        eb.addField("::wealthplat","Check's the current online player's plat worth",true);
        eb.addField("::wealthcoins","Check's the current online player's coins worth",true);
        eb.addField("::wealthnomad","Check's the current online player's nomad worth",true);
        eb.addField("::addip-IPAddress", "Add's a player's IP Address to the white list!", true);
        eb.addField("::commands","Show's command list",true);

        eb.setAuthor("~Luke~");
        eb.setFooter("Paradise-network.net");

        e.getChannel().sendMessage(eb.build()).queue();
    }
}
