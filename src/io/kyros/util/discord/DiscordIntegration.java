package io.kyros.util.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.kyros.Configuration;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.save.PlayerSave;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DiscordIntegration {

    public static Map<String, Long> connectedAccounts = new HashMap<>();
    public static ArrayList<Long> disableMessage = new ArrayList<>();

    public static Map<String, Long> idForCode = new HashMap<>();

    public static String generateCode(int length) {
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = lowerCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for (int i = 4; i < length; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return new String(password);
    }


    public static void sendPrivateMessage(User user, TextChannel c, String content) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return;
        }

        ErrorHandler handler = new ErrorHandler().handle(ErrorResponse.CANNOT_SEND_TO_USER, (error) -> {
            c.sendMessage(user.getAsMention() + " You must enable your private messages first!").queue();
        });

        user.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(content).queue(null, handler);
        });
    }

    public static void sendPMS(String content) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return;
        }
        System.out.println("sending discord pms");

        Guild guild = Discord.jda.getGuildById(1001818107343556648L);
        for (Map.Entry<String, Long> entry : DiscordIntegration.connectedAccounts.entrySet()) {
            Player player = PlayerHandler.getPlayerByLoginName(entry.getKey());
            if (player == null)
                continue;
            Member member = guild.getMemberById(entry.getValue());
            if (member == null) {
                continue;
            }
            if (disableMessage.contains(entry.getValue()))
                continue;

            User user = member.getUser();

            if (user != null) {
                ErrorHandler handler = new ErrorHandler().handle(ErrorResponse.CANNOT_SEND_TO_USER, (error) -> {
                    // c.sendMessage(user.getAsMention() + " You must enable your private messages first!").queue();
                });

                user.openPrivateChannel().queue((channel) -> {
                    channel.sendMessage("A new update has just released on Kyros!").queue(null, handler);
                });
                user.openPrivateChannel().queue((channel) -> {
                    channel.sendMessage("https://Paradise-network.net/").queue(null, handler);
                });
            }
        }
    }

    public static void integrateAccount(Player player, String code) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return;
        }

        if (connectedAccounts == null) {
            loadConnectedAccounts();
        }

        if (player.getDiscordUser() > 0) {
            player.sendMessage("You already have a connected discord account!");
        }

        if (!idForCode.containsKey(code)) {
            player.sendMessage("You have entered an invalid code! Try again.");
            return;
        }

        long userId = idForCode.get(code);

        idForCode.remove(code);

        if (connectedAccounts.containsValue(userId) && connectedAccounts.get(player.getLoginName()) !=  userId) {
            player.sendMessage("This discord account is already linked to another player!");
            return;
        }

        String name = Discord.jda.getUserById(userId).getAsTag();

        player.sendMessage("You have connected the discord account '" + name + "'.");
        connectedAccounts.put(player.getLoginName(), userId);
        player.setDiscordUser(userId);
        player.setDiscordTag(name);
        updateDiscordInterface(player);

        Discord.writeServerSyncMessage(player.getDisplayName()  + " : " + name + " : " + player.getIpAddress() + " : " + player.getMacAddress() + " : " + player.getUUID());

        if (!player.getDiscordlinked() && player.getDiscordPoints() <= 10) {
            player.amDonated += 20;
            player.updateRank();
            player.sendMessage("@mag@You received $20 to your total donated amount for linking your Discord account!");
            player.setDiscordlinked(true);
            player.setDiscordPoints(player.getDiscordPoints() + 10);
        }

        PlayerSave.saveGame(player);
        //TODO Announce player syncing discord to notify others
    }


    public static void setIntegration(Player player) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return;
        }
        if (player.getDiscordUser() > 0
                && player.getDiscordTag() != null) {
            connectedAccounts.put(player.getLoginName(), player.getDiscordUser());
            player.setDiscordUser(player.getDiscordUser());
            player.setDiscordTag(player.getDiscordTag());
        }
    }

    public static void loadConnectedAccounts() {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return;
        }
        File file = new File("./save_files/discord/discordConnectedAccounts.json");

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder().create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);
            if (reader.has("connectedAccounts")) {
                Map<String, Long> accounts = builder.fromJson(reader.get("connectedAccounts"),
                        new TypeToken<Map<String, Long>>() {
                        }.getType());

                connectedAccounts = accounts;
            }

            if (reader.has("disableMessage")) {
                Long[] pricesData = builder.fromJson(reader.get("disableMessage"), Long[].class);
                for (Long data : pricesData) {
                    disableMessage.add(data);
                }
            }

            System.out.println("Loaded Discord Connected Accounts!");
        } catch (Exception e) {
            System.out.println("Error Loading Discord Connected Accounts!");
        }
    }

    public static void saveConnectedAccounts() {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return;
        }
        File file = new File("./save_files/discord/discordConnectedAccounts.json");
        try (FileWriter writer = new FileWriter(file)) {
            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            JsonObject object = new JsonObject();

            object.add("connectedAccounts", builder.toJsonTree(connectedAccounts));

            object.add("disableMessage", builder.toJsonTree(disableMessage));


            writer.write(builder.toJson(object));
            writer.close();
            System.out.println("Saved Discord Connected Accounts!");
        } catch (Exception e) {
            System.out.println("Error Saving Discord Connected Accounts!");
        }
    }

    public static void updateDiscordInterface(Player player) {
        if (player.getDiscordUser() <= 0) {
            player.getPA().sendString(37507, "@red@Inactive");
        } else {
            player.getPA().sendString(37507,  "@whi@" + player.getDiscordTag());
        }

        if (disableMessage.contains(player.getDiscordUser())) {
            player.getPA().sendString(37508, "@whi@Active");
        } else {
            player.getPA().sendString(37508,  "@red@Inactive");
        }

        if (Discord.jda != null) {
            Guild guild = Discord.jda.getGuildById(1001818107343556648L);

            if (guild != null) {
                for (Member booster : guild.getBoosters()) {
                    if (player.getDiscordUser() == booster.getIdLong()) {
                        player.getPA().sendString(37509,  "@whi@Boosting!");
                        player.getPA().sendString(37510,  "@whi@Receiving 10% Damage Boost!" +
                                "\\n@whi@Receiving 10% Rare rewards from raids!" +
                                "\\n@whi@Receiving 10% Chance double achievement gain!");
                        break;
                    } else {
                        player.getPA().sendString(37509,  "@red@Inactive");
                        player.getPA().sendString(37510,  "@red@Inactive");
                    }
                }
            }
        }

        player.getPA().sendString(37511,  "@whi@"+player.getDiscordPoints());
    }

    public static void buttonClick(Player player) {
        if (player.getDiscordTag() != null && player.getDiscordUser() > 0) {
            if (disableMessage.contains(player.getDiscordUser())) {
                disableMessage.remove(player.getDiscordUser());
            } else {
                disableMessage.add(player.getDiscordUser());
            }
        } else {
            player.sendMessage("You need to link your account first.");
        }
        updateDiscordInterface(player);
    }

    public static void syncUser(Player player) {
        player.getPA().sendEnterString("Enter the code from the Discord Bot.", DiscordIntegration::integrateAccount);
    }

    public static void disconnectUser(Player player) {
        Iterator<Map.Entry<String, Long>> iterator = connectedAccounts.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (entry.getKey().equalsIgnoreCase(player.getLoginName())) {
                iterator.remove();  // Use iterator's remove method
                player.setDiscordlinked(false);
                player.setDiscordTag("");
                player.setDiscordUser(0);
                player.sendMessage("Your discord account has been removed from your account.");
                break;
            }
        }
        Discord.writeServerSyncMessage("[DISCORD] " + player.getDisplayName() + " has disconnected their account.");
        updateDiscordInterface(player);
    }


    public static void sendMessage(String message, long channel) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return;
        }
        Discord.jda.getTextChannelById(channel).sendMessage(message).queue();
    }

    public static long delay;

    public static long processTime = 0;
    public static void givePoints() {
        long start = System.currentTimeMillis();
        if (delay > System.currentTimeMillis()) {
            return;
        }
        if (Discord.jda != null) {
            Guild guild = Discord.jda.getGuildById(1001818107343556648L);

            for (Map.Entry<String, Long> entry : DiscordIntegration.connectedAccounts.entrySet()) {
                Player player = PlayerHandler.getPlayerByLoginName(entry.getKey());
                if (player == null)
                    continue;

                Member member = guild.getMemberById(entry.getValue());
                if (member == null) {
                    continue;
                }

                CheckDonor(player, member.getRoles());

                boolean containsStatus = false;
                boolean boosting = false;

                for (Activity a : member.getActivities()) {
                    String status = a.getName().toLowerCase();
                    if (status.contains("kyros")) {
                        containsStatus = true;
                        break;
                    }
                }

                for (Member booster : guild.getBoosters()) {
                    if (member == booster && player.getDiscordboostlastClaimed() < System.currentTimeMillis()) {
                        player.getItems().addItemUnderAnyCircumstance(696, 100);
                        player.getItems().addItemUnderAnyCircumstance(12588, 30);
                        player.getItems().addItemUnderAnyCircumstance(33358, 10);
                        player.getItems().addItemUnderAnyCircumstance(33359, 10);
                        player.getItems().addItemUnderAnyCircumstance(25537, 5);
                        player.sendErrorMessage("You have be given your Discord Boost Items This week! check your bank!");
                        player.sendErrorMessage("You have be given your Discord Boost Items This week! check your bank!");
                        player.sendErrorMessage("You have be given your Discord Boost Items This week! check your bank!");
                        player.setDiscordboostlastClaimed(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
                    }

                    if (member == booster) {
                        boosting = true;
                    }
                }

                delay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15);
                if (containsStatus) {
                    player.increaseDiscordPoints(3000 + (boosting ? 3000 : 0));
                } else {
                    player.increaseDiscordPoints(1000 + (boosting ? 3000 : 0));
                }
            }
        }
        processTime = System.currentTimeMillis() - start;
    }


    public static void CheckDonor(Player player, List<Role> roles) {
/*        if (player.amDonated >= 20 && player.amDonated < 50) {
            Role rl = (Role) roles.stream().filter(role -> role.getIdLong() == 2L);
        } else if (player.amDonated >= 50 && player.amDonated < 100) {

        } else if (player.amDonated >= 100 && player.amDonated < 250) {

        } else if (player.amDonated >= 250 && player.amDonated < 500) {

        } else if (player.amDonated >= 500 && player.amDonated < 750) {

        } else if (player.amDonated >= 750 && player.amDonated < 1000) {

        } else if (player.amDonated >= 1000 && player.amDonated < 1500) {

        } else if (player.amDonated >= 1500 && player.amDonated < 2000) {

        } else if (player.amDonated >= 2000 && player.amDonated < 3000) {

        } else if (player.amDonated >= 3000) {

        }*/
    }
}
