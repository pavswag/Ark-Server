package io.kyros.util.discord;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.afkzone.AfkBoss;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.model.entity.player.save.PlayerSave;
import io.kyros.model.items.bank.BankItem;
import io.kyros.model.items.bank.BankTab;
import io.kyros.util.Misc;
import io.kyros.util.PasswordHashing;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Discord extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Discord.class);
    private static final Map<String, TextChannel> channels = new ConcurrentHashMap<>();
    public static JDA jda = null;
    public static String PREFIX = "::";
    public static String OWNER_ROLE = "1001818107372908592";//Currently set to God Tier on beta.
    public static String MANAGER_ROLE = "1037136820321931306";
    public static String DEVELOPER_ROLE = "1001818107372908589";
    public static String ADMIN_ROLE = "1001818107372908586";
    public static String GLOBAL_MOD_ROLE = "1016647097749086229";
    public static String SUPPORT_ROLE = "1001818107356135575";


    /**
     * Write to a channel that contains misc. types of information about player activity.
     */
    public static void writeServerSyncMessage(String message, Object...args) {
        if (message.contains("[TRADE]")) {
            sendChannelMessage(1240060954537099265L, message, args);//Trade-Logs
        } else if (message.contains("[GAMBLE]")) {
            sendChannelMessage(1252164778659680367L, message, args);//Gamble-Logs
        } else if (message.contains("[REF]")) {
            sendChannelMessage(1255566668462034995L, message, args);//Gamble-Logs
        } else if (message.contains("[USE ON]")) {
            sendChannelMessage(1261968372246122546L, message, args);
        } else if (message.contains("[VOTE LOG]")) {
            sendChannelMessage(1269957206883696723L, message, args);
        } else if (message.contains("[Offline Storage]")) {
            sendChannelMessage(1269977816405901363L, message, args);
        } else if (message.contains("[NOMAD]")) {
            sendChannelMessage(1278016129343688775L, message, args); //Nomad dissolve logs
        } else {
            sendChannelMessage(1217970249480147057L, message, args);//Server-Logs
        }
    }

    public static void sendCriticalWarning(Player player, String time, long oldTotalNomad, long newTotalNomad, boolean nomad) {
        // Construct the message
        String wealthType = nomad ? "Nomad" : "Coins";
        String message = String.format("CRITICAL WARNING: Player %s has experienced a significant increase in %s wealth.\n" +
                        "Play time: %s\n" +
                        "%s wealth: %d => %d (Increase: %s)",
                player.getDisplayName(), wealthType, time, wealthType, oldTotalNomad, newTotalNomad, NumberFormat.getInstance().format(newTotalNomad - oldTotalNomad));

        Server.getIoExecutorService().submit(() -> {
            try {
                if (getJDA().getTextChannelById(1226277964707008612L) != null) {
                    getJDA().getTextChannelById(1226277964707008612L).sendMessage(message).queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void writeOfflineRewardsMessage(String message, Object...args) {
        sendChannelMessage(1217970340517515404L, message, args);//Offline-rewards -- 1151306936806031442L
    }

    public static void writeOnlineNotification(String message, Object...args) {
        sendChannelMessage(1217968808371949588L, message, args);//Bot-Information
    }

/*    public static void writeOfflineRewardsMessage(String message, Object...args) {
        sendChannelMessage(1151306936806031442L, message, args);//Offline-rewards
    }*/

    public static void writeBugMessage(String message, Object...args) {
        sendChannelMessage(1217968808371949588L, message, args);
    }

    public static void writePickupMessage(String message, Object...args) {
        sendChannelMessage(1217970299891486751L, message, args);//pickup-logs
    }

    public static void writeXmasMessage(String message, Object...args) {
        sendChannelMessage(1224876362683383838L, message, args);//xmas-logs
    }

    public static void writeSuggestionMessage(String message, Object...args) {
        sendChannelMessage(1217970235013857380L, message, args);//mod-comms
    }

    public static void writeFoeMessage(String message, Object...args) {
//        writeServerSyncMessage(message, args);
        sendChannelMessage(1240060954537099265L, message, args);//Bot-Information
    }

    public static void writeReferralMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
    }

    public static void writeCheatEngineMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
    }

    public static void writeDeathHandler(String message, Object...args) {
        sendChannelMessage(1217970272183914557L, message, args);
    }

    public static void writeDropHandler(String message, Object...args) {
        sendChannelMessage(1217970311795052634L, message, args);
    }

    public static void writeGiveLog(String message, Object...args) {
        sendChannelMessage(1217970249480147057L, message, args);
    }

    /**
     * Write to a channel that should not be ignored by staff.
     */
    public static void writeAddressSwapMessage(String message, Object...args) {
        writeServerSyncMessage(message, args);
//        sendChannelMessage("server-bot-notification", message, args);
    }
    private static void sendChannelMessage(long channelName, String message, Object...args) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            System.out.println("Retard set the config to false.");
            return;
        }
        Server.getIoExecutorService().submit(() -> {
            try {
                if (getJDA().getTextChannelById(channelName) != null) {
                    getJDA().getTextChannelById(channelName).
                            sendMessage(Misc.replaceBracketsWithArguments(message, args)).queue();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    private static TextChannel getChannel(String name) {
        if (Configuration.DISABLE_DISCORD_MESSAGING)
            return null;
        if (channels.containsKey(name))
            return channels.get(name);

        List<TextChannel> foundChannels = getJDA().getTextChannelsByName(name, true);
        if (foundChannels.isEmpty()) {
            logger.error("No discord channel found with name: " + name);
            return null;
        }

        TextChannel channel = foundChannels.get(0);
        channels.put(name, channel);
        return channels.get(name);
    }

    public static JDA getJDA() {
        return jda;
    }

    private static boolean enabled() {
        return !Configuration.DISABLE_DISCORD_MESSAGING;
    }

    public void init() {
        if (Configuration.DISABLE_DISCORD_MESSAGING)
            return;
        JDABuilder builder = JDABuilder.createDefault("MTAwMzY0MTM5ODY4OTUzMzk4Mg.GZNkEW.2OrBxoMUsdI-LbDi3phoOerw_CSTUaC4xtDG1s")
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableCache(CacheFlag.ACTIVITY)
                .setMemberCachePolicy(MemberCachePolicy.ONLINE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL);
        System.out.println("Loading Discord Bot!");
        try {
            jda = builder.build();
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching("Kyros 1.0 with you & others!"));
            jda.addEventListener(this);
            jda.getGuilds().forEach(Guild::loadMembers);
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            System.out.println("Retard set the config to false.");
            return;
        }

        if (e.getAuthor().isBot()) {
            return;
        }

        if (e.getChannel().getIdLong() == 1217968208783343707L && e.getMessage().getContentDisplay().equalsIgnoreCase("!connect")) {
            User user = e.getAuthor();

            if (DiscordIntegration.connectedAccounts.containsValue(user.getIdLong())) {
                DiscordIntegration.sendPrivateMessage(user, e.getChannel(), "This discord account is already connected to another in-game account!");
            }

            if (DiscordIntegration.idForCode.containsValue(user.getIdLong())) {
                String code = null;
                for (Map.Entry<String, Long> entry : DiscordIntegration.idForCode.entrySet()) {
                    if (entry.getValue() == user.getIdLong()) {
                        code = entry.getKey();
                    }
                }

                if (code == null) {
                    code = DiscordIntegration.generateCode(4);
                }

                DiscordIntegration.sendPrivateMessage(user, e.getChannel(), "Hello! You have already generated a code! Enter the following in the discord integration prompt:\n"
                        + code);
                return;
            }

            String code = DiscordIntegration.generateCode(4);

            while (DiscordIntegration.idForCode.containsKey(code)) {
                code = DiscordIntegration.generateCode(4);
            }

            DiscordIntegration.idForCode.put(code, e.getAuthor().getIdLong());

            DiscordIntegration.sendPrivateMessage(user, e.getChannel(),
                    "Hello! To connect your discord account to your in-game account, enter the following in the discord integration prompt when you click \"sync\":\n"
                            + code);
        } else if (e.getChannel().getIdLong() == 1217968208783343707L && e.getMessage().getContentDisplay().equalsIgnoreCase("!forgotpass")) {
            User user = e.getAuthor();
            long userID = user.getIdLong();

            if (DiscordIntegration.connectedAccounts.containsValue(userID)) {
                AtomicReference<String> username = new AtomicReference<>("");
                DiscordIntegration.connectedAccounts.forEach((s, aLong) -> {
                    if (aLong == userID) {
                        username.set(s);
                    }
                });

                String newPassword = generateRandomPassword();
                String hashedPassword = PasswordHashing.hash(newPassword);

                try {
                    updatePlayerSaveFile(username.get(), hashedPassword);
                    DiscordIntegration.sendPrivateMessage(user, e.getChannel(), "Your password has been reset. Your new password is: " + newPassword);
                } catch (IOException ex) {
                    DiscordIntegration.sendPrivateMessage(user, e.getChannel(), "An error occurred while updating your password. Please try again later.");
                    ex.printStackTrace();
                }
            } else {
                DiscordIntegration.sendPrivateMessage(user, e.getChannel(), "Hello sir, I am the Kyros Assistance Bot! It seems you haven't linked your Discord account. You'll need to create a ticket to get your password resolved!");
            }
        } else if (e.getChannel().getIdLong() == 1217970533040263348L) {
            DiscordCommands command = DiscordCommands.isCommand(e);

            String text = e.getMessage().getContentRaw().toLowerCase();
            if (text.contains("::uneventban")) {
                String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
                String username = string[1].trim();

                // Read existing data from eventbans.json
                JSONParser jsonParser = new JSONParser();
                String eventBansFileName = "eventbans.json";

                try (BufferedReader eventBansReader = new BufferedReader(new FileReader(Server.getSaveDirectory() + eventBansFileName))) {
                    Object obj = jsonParser.parse(eventBansReader);
                    JSONArray eventBansArray = (JSONArray) obj;

                    // Find and remove the entry associated with the specified username
                    JSONArray updatedEventBansArray = new JSONArray();
                    for (Object entry : eventBansArray) {
                        JSONObject eventBan = (JSONObject) entry;
                        String entryUsername = (String) eventBan.get("username");

                        if (!username.equalsIgnoreCase(entryUsername)) {
                            updatedEventBansArray.add(eventBan);
                        }
                    }

                    // Write the updated data back to eventbans.json
                    try (FileWriter eventBansWriter = new FileWriter(Server.getSaveDirectory() + eventBansFileName)) {
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String prettyJson = gson.toJson(updatedEventBansArray);
                        eventBansWriter.write(prettyJson);
                    }

                    Discord.writeXmasMessage("Player " + username + " has been unbanned from the event.");
                } catch (IOException | ParseException ignoredEvent) {
                    Discord.writeXmasMessage("No ban for the username: " + username + " exists!");
                }
                return;
            }
            if (text.contains("::eventban")) {
                String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
                String username = string[1].trim();
                String userFileName = username + ".txt";
                String eventBansFileName = "eventbans.json";

                try {
                    // Read existing data from eventbans.json
                    JSONParser jsonParser = new JSONParser();
                    JSONArray eventBansArray;
                    try (BufferedReader eventBansReader = new BufferedReader(new FileReader(Server.getSaveDirectory() + eventBansFileName))) {
                        Object obj = jsonParser.parse(eventBansReader);
                        eventBansArray = (JSONArray) obj;
                    } catch (FileNotFoundException ignored) {
                        // If the file doesn't exist, create a new array
                        eventBansArray = new JSONArray();
                    }

                    // Check if an entry for the specified username already exists
                    boolean entryExists = false;
                    for (Object entry : eventBansArray) {
                        JSONObject eventBan = (JSONObject) entry;
                        String entryUsername = (String) eventBan.get("username");

                        if (username.equalsIgnoreCase(entryUsername)) {
                            entryExists = true;
                            break;
                        }
                    }

                    if (!entryExists) {
                        // Read user information from the username.txt file
                        try (BufferedReader reader = new BufferedReader(new FileReader(PlayerSave.getSaveDirectory() + userFileName))) {
                            String uuid = "";
                            String ipAddress = "";
                            String macAddress = "";

                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] parts = line.split("=");
                                if (parts.length == 2) {
                                    String key = parts[0].trim();
                                    String value = parts[1].trim();

                                    if ("character-uuid".equals(key)) {
                                        uuid = value;
                                    } else if ("character-ip-address".equals(key)) {
                                        ipAddress = value;
                                    } else if ("character-mac-address".equals(key)) {
                                        macAddress = value;
                                    }
                                }
                            }

                            // Create JSON object with user information
                            JSONObject userJson = new JSONObject();
                            userJson.put("username", username);

                            JSONObject addressJson = new JSONObject();
                            addressJson.put("uuid", uuid);
                            addressJson.put("ip_address", ipAddress);
                            addressJson.put("mac_address", macAddress);

                            userJson.put("address", addressJson);

                            // Add the new user information to the existing eventbans.json data
                            eventBansArray.add(userJson);

                            // Write the updated data back to eventbans.json
                            try (FileWriter eventBansWriter = new FileWriter(Server.getSaveDirectory() + eventBansFileName)) {
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                String prettyJson = gson.toJson(eventBansArray);
                                eventBansWriter.write(prettyJson);
                            } catch (IOException ex) {
                                // Handle the IOException, log or print the error as needed
                                ex.printStackTrace();
                            }

                            // Log the success
                            Discord.writeXmasMessage("Event ban added successfully for " + username);
                        } catch (IOException ex) {
                            // Log the exception
                            Discord.writeXmasMessage("Error processing event ban for " + username + " / " + ex.getMessage());
                        }
                    } else {
                        Discord.writeXmasMessage("Player " + username + " is already event-banned.");
                    }
                } catch (Exception ex) {
                    // Log unexpected exceptions
                    Discord.writeXmasMessage("Unexpected error: " + ex.getMessage());
                }
                return;
            }

            if (text.contains("::wealthcoins")) {
                List<String> lines = new ArrayList<>();

                for (Player player : Server.getPlayers()) {
                    if (player != null && !player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
                        long coins = 0;

                        coins += player.getItems().getInventoryCount(995);

                        for (BankTab bankTab : player.getBank().getBankTab()) {
                            for (BankItem item : bankTab.getItems()) {
                                if (item.getId() - 1 == 995) {
                                    coins += item.getAmount();
                                }
                            }
                        }

                        lines.add("User: " + player.getDisplayName() + ", coins: " + Misc.formatCoins(coins));
                    }
                }

                List<String> messages = new ArrayList<>();
                StringBuilder currentMessage = new StringBuilder();
                for (String line : lines) {
                    if (currentMessage.length() + line.length() >= 1900) { // leaving some buffer
                        messages.add(currentMessage.toString());
                        currentMessage.setLength(0); // clear StringBuilder
                    }
                    currentMessage.append(line).append("\n");
                }
                if (currentMessage.length() > 0) {
                    messages.add(currentMessage.toString());
                }

                // Send messages
                for (String message : messages) {
                    Discord.writeXmasMessage(message);
                }
                return;
            }

            if (text.contains("::wealthvote")) {
                List<String> lines = new ArrayList<>();

                for (Player player : Server.getPlayers()) {
                    if (player != null && !player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
                        long coins = 0;

                        coins += player.getItems().getInventoryCount(23933);

                        for (BankTab bankTab : player.getBank().getBankTab()) {
                            for (BankItem item : bankTab.getItems()) {
                                if (item.getId() - 1 == 23933) {
                                    coins += item.getAmount();
                                }
                            }
                        }

                        coins += player.votePoints;

                        lines.add("User: " + player.getDisplayName() + ", vote crystals: " + Misc.formatCoins(coins));
                    }
                }

                List<String> messages = new ArrayList<>();
                StringBuilder currentMessage = new StringBuilder();
                for (String line : lines) {
                    if (currentMessage.length() + line.length() >= 1900) { // leaving some buffer
                        messages.add(currentMessage.toString());
                        currentMessage.setLength(0); // clear StringBuilder
                    }
                    currentMessage.append(line).append("\n");
                }
                if (currentMessage.length() > 0) {
                    messages.add(currentMessage.toString());
                }

                // Send messages
                for (String message : messages) {
                    Discord.writeXmasMessage(message);
                }
                return;
            }


            if (text.contains("::wealthplat")) {
                List<String> lines = new ArrayList<>();

                for (Player player : Server.getPlayers()) {
                    if (player != null && !player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
                        long plat = 0;

                        plat += player.getItems().getInventoryCount(13204);

                        for (BankTab bankTab : player.getBank().getBankTab()) {
                            for (BankItem item : bankTab.getItems()) {
                                if (item.getId() - 1 == 13204) {
                                    plat += item.getAmount();
                                }
                            }
                        }

                        lines.add("User: " + player.getDisplayName() + ", plat: " + Misc.formatCoins(plat));
                    }
                }

                List<String> messages = new ArrayList<>();
                StringBuilder currentMessage = new StringBuilder();
                for (String line : lines) {
                    if (currentMessage.length() + line.length() >= 1900) { // leaving some buffer
                        messages.add(currentMessage.toString());
                        currentMessage.setLength(0); // clear StringBuilder
                    }
                    currentMessage.append(line).append("\n");
                }
                if (currentMessage.length() > 0) {
                    messages.add(currentMessage.toString());
                }

                // Send messages
                for (String message : messages) {
                    Discord.writeXmasMessage(message);
                }
                return;
            }

// Command: ::wealthnomad
            if (text.contains("::wealthnomad")) {
                List<String> lines = new ArrayList<>();

                for (Player player : Server.getPlayers()) {
                    if (player != null && !player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
                        long nomad = 0;

                        nomad += player.getItems().getInventoryCount(33237);
                        nomad += (player.getItems().getInventoryCount(691) * 10_000L);
                        nomad += (player.getItems().getInventoryCount(692) * 25_000L);
                        nomad += (player.getItems().getInventoryCount(693) * 50_000L);
                        nomad += (player.getItems().getInventoryCount(696) * 250_000L);

                        for (BankTab bankTab : player.getBank().getBankTab()) {
                            for (BankItem item : bankTab.getItems()) {
                                if (item.getId() - 1 == 33237) {
                                    nomad += item.getAmount();
                                }
                                if (item.getId() - 1 == 691) {
                                    nomad += (item.getAmount() * 10_000L);
                                }
                                if (item.getId() - 1 == 692) {
                                    nomad += (item.getAmount() * 25_000L);
                                }
                                if (item.getId() - 1 == 693) {
                                    nomad += (item.getAmount() * 50_000L);
                                }
                                if (item.getId() - 1 == 696) {
                                    nomad += (item.getAmount() * 250_000L);
                                }
                            }
                        }

                        nomad += player.foundryPoints;

                        lines.add("User: " + player.getDisplayName() + ", nomad: " + Misc.formatCoins(nomad));
                    }
                }

                List<String> messages = new ArrayList<>();
                StringBuilder currentMessage = new StringBuilder();
                for (String line : lines) {
                    if (currentMessage.length() + line.length() >= 1900) { // leaving some buffer
                        messages.add(currentMessage.toString());
                        currentMessage.setLength(0); // clear StringBuilder
                    }
                    currentMessage.append(line).append("\n");
                }
                if (currentMessage.length() > 0) {
                    messages.add(currentMessage.toString());
                }

                // Send messages
                for (String message : messages) {
                    Discord.writeXmasMessage(message);
                }
                return;
            }
            if (text.contains("::voted")) {
                ArrayList<String> character_names = new ArrayList<>();
                for (Player player : Server.getPlayers()) {
                    if (AfkBoss.hasVoted(player)) {
                        character_names.add(player.getDisplayName());
                    }
                }

                Discord.writeXmasMessage("[Voted accounts] " + Arrays.toString(character_names.toArray()));
                return;
            }
            if (text.contains("::scan")) {
                String[] string = e.getMessage().getContentRaw().toLowerCase().split("-");
                String name = string[1];

                // Specify the regular expressions for matching character information lines
                String uuidPattern = "character-uuid\\s*=\\s*([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})";
                String macAddressPattern = "character-mac-address\\s*=\\s*([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})";
                String ipAddressPattern = "character-ip-address\\s*=\\s*([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)";

                // Compile the patterns
                Pattern uuidPatternCompiled = Pattern.compile(uuidPattern);
                Pattern macAddressPatternCompiled = Pattern.compile(macAddressPattern);
                Pattern ipAddressPatternCompiled = Pattern.compile(ipAddressPattern);

                try {
                    String uuid = null;
                    String macAddress = null;
                    String ipAddress = null;

                    File targetFile = new File(PlayerSave.getSaveDirectory(), name+".txt");

                    if (!targetFile.isFile()) {
                        System.out.println("Target file not found: " + name+".txt");
                        return;
                    }

                    // Read all lines from the target file
                    List<String> lines = Files.readAllLines(targetFile.toPath());

                    // Iterate through each line
                    for (String line : lines) {
                        // Check for character UUID
                        Matcher uuidMatcher = uuidPatternCompiled.matcher(line);
                        if (uuidMatcher.find()) {
                            uuid = uuidMatcher.group(1);
                        }

                        // Check for character MAC address
                        Matcher macAddressMatcher = macAddressPatternCompiled.matcher(line);
                        if (macAddressMatcher.find()) {
                            macAddress = line.replace("character-mac-address = ", "");
                        }

                        // Check for character IP address
                        Matcher ipAddressMatcher = ipAddressPatternCompiled.matcher(line);
                        if (ipAddressMatcher.find()) {
                            ipAddress = ipAddressMatcher.group(1);
                        }
                    }

                    // Output character information
                    if (uuid != null || macAddress != null || ipAddress != null) {
                        System.out.println("Found character information:");
                        if (uuid != null) {
                            System.out.println("UUID: " + uuid);
                        }
                        if (macAddress != null) {
                            System.out.println("MAC Address: " + macAddress);
                        }
                        if (ipAddress != null) {
                            System.out.println("IP Address: " + ipAddress);
                        }

                        // Scan the entire directory for files containing the same character information
                        scanDirectoryForCharacterInfo(PlayerSave.getSaveDirectory(), uuid, macAddress, ipAddress);
//                        checkForAlternativeAccountLoggedIn(uuid, macAddress, ipAddress);
                        Set<String> alternativeAccounts = new HashSet<>();

                        // Loop through all players currently on the server
                        for (Player player : Server.getPlayers()) {
                            if (player != null) {
                                String onlineUUID = player.getUUID();
                                String onlineMac = player.getMacAddress();
                                String onlineIP = player.getIpAddress();

                                // Check if player's UUID, MAC, or IP matches the given values
                                if ((uuid != null && uuid.equals(onlineUUID)) ||
                                        (macAddress != null && macAddress.equals(onlineMac)) ||
                                        (ipAddress != null && ipAddress.equals(onlineIP))) {

                                    String playerName = player.getDisplayName();

                                    // Add the player name to the set if it's not already there
                                    if (!alternativeAccounts.contains(playerName)) {
                                        alternativeAccounts.add(playerName);

                                        // Check if the player is online
                                        if (player.isOnline()) {
                                            System.out.println(playerName + " (alternative account) is currently online.");
                                        }
                                    }
                                }
                            }
                        }

                        // Output all found alternative accounts via Discord
                        if (!alternativeAccounts.isEmpty()) {
                            Discord.writeXmasMessage("Alternative accounts logged in: " + String.join(", ", alternativeAccounts));
                        } else {
                            Discord.writeXmasMessage("No alternative accounts are currently logged in.");
                        }
                    } else {
                        System.out.println("Character information not found in the target file.");
                    }
                } catch (IOException ex) {

                }
                return;
            }

            if (Objects.isNull(command)) {
                return;
            }

            command.getAdapter().onGuildMessageReceived(e);
        }
    }

    private String generateRandomPassword() {
        String characters = "abcdefghijklmnopqrstuvwxyz";
        int length = 10;
        Random random = new Random();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    private void updatePlayerSaveFile(String username, String hashedPassword) throws IOException {
        Path path = Path.of(PlayerSave.getSaveDirectory() + "/" +username+".txt");
        String content = new String(Files.readAllBytes(path));
        String oldPasswordLine = Files.lines(path)
                .filter(line -> line.startsWith("character-password = "))
                .findFirst()
                .orElseThrow(() -> new IOException("Password line not found"));

        String newPasswordLine = "character-password = " + hashedPassword;
        content = content.replace(oldPasswordLine, newPasswordLine);
        Files.write(path, content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }


    private static void scanDirectoryForCharacterInfo(String directoryPath, String uuid, String macAddress, String ipAddress) {
        File directory = new File(directoryPath);

        // Check if the specified path is a directory
        if (!directory.isDirectory()) {
            System.out.println("Specified path is not a directory.");
            return;
        }

        ArrayList<String> character_names = new ArrayList<>();

        // List all files in the directory
        File[] files = directory.listFiles();

        // Iterate through each file
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Process each file and check if it contains the same character information
                    try {
                        if (containsCharacterInfo(file.toPath(), uuid, macAddress, ipAddress)) {
                            if (!character_names.contains(file.getName().replace(".txt", ""))) {
                                character_names.add(file.getName().replace(".txt", ""));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Discord.writeXmasMessage(Arrays.toString(character_names.toArray()));
    }

    private static boolean containsCharacterInfo(Path filePath, String uuid, String macAddress, String ipAddress) throws IOException {
        // Read all lines from the file
        List<String> lines = Files.readAllLines(filePath);

        // Check if the file contains the same character information
        for (String line : lines) {
            if ((uuid != null && line.contains(uuid)) ||
                    (macAddress != null && line.contains(macAddress)) ||
                    (ipAddress != null && line.contains(ipAddress))) {
                return true;
            }
        }

        return false;
    }

}
