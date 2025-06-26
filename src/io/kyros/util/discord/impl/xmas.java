package io.kyros.util.discord.impl;

import io.kyros.Server;
import io.kyros.content.questing.Quest;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.save.PlayerAddresses;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static io.kyros.model.entity.player.save.PlayerSave.getSaveDirectory;

public class xmas extends ListenerAdapter {

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
            PlayerAddresses addresses = player.getValidAddresses();

            Discord.writeXmasMessage("[XMAS]: " + player.getDisplayName() +
                    " MAC " + player.getMacAddress() +
                    " UUID " + player.getUUID() +
                    " IP " + player.getIpAddress() +
                    " has also completed the quest!"
            );


            List<Player> clientList = Server.getPlayers().nonNullStream().filter(p -> p.connectedFrom.equals(addresses.getIp())).collect(Collectors.toList());

            for (Player pz : clientList) {
                for (Quest quest : pz.getQuesting().getQuestList()) {
                    if (quest.getName().equalsIgnoreCase("santa's troubles") && quest.getStage() >= 17) {
                        Discord.writeXmasMessage("[XMAS]: " + pz.getDisplayName() +
                                " MAC " + pz.getMacAddress() +
                                " UUID " + pz.getUUID() +
                                " IP " + pz.getIpAddress() +
                                " has also completed the quest!"
                                );

                    }
                }
            }
        } else {
            printData(name);
        }
    }


    public void printData(String name) {
        String line = "";
        String token = "";
        String token2 = "";

        BufferedReader characterfile = null;
        boolean characterFileExists = false;

        try {
            characterfile = new BufferedReader(new FileReader(getSaveDirectory() + name.toLowerCase() + ".txt"));
            characterFileExists = true;
        } catch (FileNotFoundException ignored) { }

        if (!characterFileExists) {
            Discord.writeXmasMessage("[XMAS]: " + name + ", this account doesn't exist");
            return;
        }

        try {
            line = characterfile.readLine();
        } catch (IOException ioexception) {
            Misc.println(name + ": error loading file.");
            return;
        }

        String UUID = "";
        StringBuilder message = new StringBuilder();

        while (line != null) {
            line = line.trim();
            try {
                int spot = line.indexOf("=");
                if (spot > -1) {
                    token = line.substring(0, spot);
                    token = token.trim();
                    token2 = line.substring(spot + 1);
                    token2 = token2.trim();

                    switch (token) {
                        case "character-username":
                            message.append("Player-Name = ").append(token2).append(", ");
                            break;
                        case "character-uuid":
                            message.append("Player-Unique-User-ID = ").append(token2).append(", ");
                            UUID = token2;
                        case "Santa's Troubles":
                            message.append("Player-Xmas-Quest-Stage = ").append(token2);
                            break;
                    }
                }
                line = characterfile.readLine();
            } catch (Exception ez) {
                ez.printStackTrace();
            }
        }
        try {
            if (!UUID.isEmpty()) {
                SearchFiles(UUID);
            }
            characterfile.close();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public void printDataz(String name) {
        String line = "";
        String token = "";
        String token2 = "";

        BufferedReader characterfile = null;
        boolean characterFileExists = false;

        try {
            characterfile = new BufferedReader(new FileReader(getSaveDirectory() + name.toLowerCase() + ".txt"));
            characterFileExists = true;
        } catch (FileNotFoundException ignored) { }

        if (!characterFileExists) {
            Discord.writeXmasMessage("[XMAS]: " + name + ", this account doesn't exist");
            return;
        }

        try {
            line = characterfile.readLine();
        } catch (IOException ioexception) {
            Misc.println(name + ": error loading file.");
            return;
        }

        StringBuilder message = new StringBuilder();

        while (line != null) {
            line = line.trim();
            try {
                int spot = line.indexOf("=");
                if (spot > -1) {
                    token = line.substring(0, spot);
                    token = token.trim();
                    token2 = line.substring(spot + 1);
                    token2 = token2.trim();

                    switch (token) {
                        case "character-username":
                            message.append("Player-Name = ").append(token2).append(", ");
                            break;
                        case "character-mac-address":
                            message.append("Player-Mac-Address = ").append(token2).append(", ");
                            break;
                        case "character-uuid":
                            message.append("Player-Unique-User-ID = ").append(token2).append(", ");
                            break;
                        case "character-ip-address":
                            message.append("Player-IP-Address = ").append(token2).append(", ");
                            break;
                        case "Santa's Troubles":
                            message.append("Player-Xmas-Quest-Stage = ").append(token2);
                            break;
                    }
                }
                line = characterfile.readLine();
            } catch (Exception ez) {
                ez.printStackTrace();
            }
        }
        try {
            Discord.writeXmasMessage("[XMAS]: " + message);
            characterfile.close();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public void SearchFiles(String uuid) {
        String srcDir = getSaveDirectory();
        File folder = new File(srcDir);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null && listOfFiles.length > 0) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    Scanner a = null;
                    try {
                        a = new Scanner(new BufferedReader(new FileReader(srcDir + listOfFiles[i].getName())));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (a != null) {
                        while (a.hasNext()) {
                            String words = a.next();
                            if (words.equalsIgnoreCase(uuid)) {
                                printDataz(listOfFiles[i].getName().substring(0,listOfFiles[i].getName().lastIndexOf(".")).toLowerCase());
                            }
                        }
                    }
                }
            }
        }
    }
}
