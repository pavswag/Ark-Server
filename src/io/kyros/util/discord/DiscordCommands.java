package io.kyros.util.discord;

import io.kyros.util.discord.impl.*;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Getter
public enum DiscordCommands {

    GIVE_ITEM("giveitem", "Give an item to a player", new GiveItem(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE}),
    DELETE_TP("tpdelete", "Removes a player's Trading post listing", new TpDelete(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE}),
    BAN("ban", "Ban's a player", new Ban(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    UNBAN("unban", "unban's a player", new UnBan(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    MUTE("mute", "mute's a player", new Mute(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE}),
    JAIL("jail", "jail's a player", new Jail(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE}),
    UNMUTE("unmute", "unmute's a player", new UnMute(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    UNJAIL("unjail", "unjail's a player", new UnJail(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    XMAS("xmas", "xmas check for player", new xmas(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    GROOT("groot", "spawns groot", new Groot(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    VOTEBOSS("voteboss", "spawns vote boss", new VoteBoss(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE}),
    DONORBOSS("donorboss", "spawns donor boss", new DonorBoss(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    OFFLINEREWARDS("of", "gives offline reward", new OfflineReward(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE}),
    COMMANDS("commands", "Show's all commands", new Commands(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE, Discord.GLOBAL_MOD_ROLE, Discord.SUPPORT_ROLE}),
    ADDWHITELIST("addip", "Add's a specific IP to the whitelist", new AddWhitelist(), new String[]{Discord.OWNER_ROLE, Discord.DEVELOPER_ROLE, Discord.MANAGER_ROLE, Discord.ADMIN_ROLE})
    ;

    private final String command, description;
    private final ListenerAdapter adapter;
    private final String[] rolesCanUse;

    public static final DiscordCommands[] VALUES = DiscordCommands.values();

    public static String prefix = Discord.PREFIX;

    DiscordCommands(String command, String description, ListenerAdapter adapter, String[] rolesCanUse) {
        this.command = command;
        this.description = description;
        this.adapter = adapter;
        this.rolesCanUse = rolesCanUse;
    }

    public static DiscordCommands isCommand(GuildMessageReceivedEvent e) {
        String text = e.getMessage().getContentRaw().toLowerCase();
        for (DiscordCommands command : DiscordCommands.VALUES) {
            if (text.contains(prefix + command.getCommand())) {
                for (Role roles : e.getMember().getRoles()) {
                    if (command.getRolesCanUse() == null) {
                        return command;
                    }
                    for (String role : command.getRolesCanUse()) {
                        if (roles.getId().contains(role)) {
                            return command;
                        }
                    }
                }
            }
        }
        return null;
    }
}
