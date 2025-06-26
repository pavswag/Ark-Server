package io.kyros.content.commands;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosses.JusticarZachariah;
import io.kyros.content.bots.BasicBot;
import io.kyros.content.commands.admin.dboss;
import io.kyros.content.deals.AccountBoosts;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.donationcampaign.DonationCampaign;
import io.kyros.content.minigames.wanderingmerchant.FiftyCent;
import io.kyros.content.pet.PetManager;
import io.kyros.content.referral.EnterReferralDialogue;
import io.kyros.content.referral.ReferralCode;
import io.kyros.content.referral.ReferralSource;
import io.kyros.content.tournaments.TourneyManager;
import io.kyros.content.wildwarning.WildWarning;
import io.kyros.model.Graphic;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Right;
import io.kyros.model.entity.player.mode.ExpMode;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.entity.player.mode.group.ExpModeType;
import io.kyros.model.entity.player.mode.group.GroupIronmanGroup;
import io.kyros.model.entity.player.mode.group.GroupIronmanRepository;
import io.kyros.model.entity.player.save.PlayerSave;
import io.kyros.model.items.bank.BankItem;
import io.kyros.model.items.bank.BankTab;
import io.kyros.sql.refsystem.RefManager;
import io.kyros.util.Misc;
import io.kyros.util.PasswordHashing;
import io.kyros.util.task.TaskManager;
import org.reflections.Reflections;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CommandManager {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CommandManager.class.getName());
    public static final Map<String, Command> COMMAND_MAP = new TreeMap<>();
    public static final List<CommandPackage> COMMAND_PACKAGES = Lists.newArrayList(new CommandPackage("admin", Right.ADMINISTRATOR), new CommandPackage("owner", Right.STAFF_MANAGER), new CommandPackage("owner", Right.GAME_DEVELOPER), new CommandPackage("moderator", Right.MODERATOR), new CommandPackage("helper", Right.HELPER), new CommandPackage("donator", Right.Donator), new CommandPackage("all", Right.PLAYER));

    private static boolean hasRightsRequirement(Player c, Right rightsRequired) {
        if (rightsRequired == Right.Donator && c.getRights().hasStaffPosition()) {
            return true;
        }
        return c.getRights().isOrInherits(rightsRequired);
    }

    public static void execute(Player c, String playerCommand) {
        for (CommandPackage commandPackage : COMMAND_PACKAGES) {
            if (hasRightsRequirement(c, commandPackage.getRight()) && executeCommand(c, playerCommand, commandPackage.getPackagePath())) {
                return;
            }
        }
    }

    public static CommandPackage getPackage(Command command) {
        for (CommandPackage commandPackage : COMMAND_PACKAGES) {
            if (command.getClass().getPackageName().contains(commandPackage.getPackagePath())) {
                return commandPackage;
            }
        }
        return null;
    }

    private static String getPackageName(String packagePath) {
        String[] split = packagePath.split("\\.");
        return split[split.length - 2];
    }

    public static List<Command> getCommands(Player player, String... skips) {
        return COMMAND_MAP.entrySet().stream().filter(entry -> {
            for (CommandPackage commandPackage : COMMAND_PACKAGES) {
                if (getPackageName(entry.getKey().toLowerCase()).contains(commandPackage.getPackagePath())) {
                    if (Arrays.stream(skips).anyMatch(skip -> commandPackage.getPackagePath().toLowerCase().contains(skip))) {
                        continue;
                    }
                    if (hasRightsRequirement(player, commandPackage.getRight())) {
                        return true;
                    }
                }
            }
            return false;
        }).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public static boolean executeCommand(Player c, String playerCommand, String commandPackage) {
        if (playerCommand == null) {
            return true;
        }
        String commandName = Misc.findCommand(playerCommand);
        String commandInput = Misc.findInput(playerCommand);
        String className;
        if (commandName.length() <= 0) {
            return true;
        } else if (commandName.length() == 1) {
            className = commandName.toUpperCase();
        } else {
            className = Character.toUpperCase(commandName.charAt(0)) + commandName.substring(1).toLowerCase();
        }

        if(c.isInTradingPost()) {
            c.setSidebarInterface(3, 3213);
            c.inTradingPost = false;
            c.clickDelay = System.currentTimeMillis();
        }

        boolean outlast = TourneyManager.getSingleton().isInArenaBounds(c) || TourneyManager.getSingleton().isInLobbyBounds(c);

        if (outlast && c.getRights().isNot(Right.ADMINISTRATOR)) {
            c.sendMessage("You cannot use commands when in the tournament arena");
            return true;
        }

        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("discordbot")) {
//            Discord.init();
            c.sendMessage("Discord bot initialized!");
            return true;
        }

        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("changexp")) {
            String[] args = commandInput.split("-");
            try {
                String playerName = args[0];

                ExpMode expMode = null;
                if (args[1].equalsIgnoreCase("1")) {
                    expMode = new ExpMode(ExpModeType.OneTimes);
                } else if (args[1].equalsIgnoreCase("5")) {
                    expMode = new ExpMode(ExpModeType.FiveTimes);
                } else if (args[1].equalsIgnoreCase("10")) {
                    expMode = new ExpMode(ExpModeType.TenTimes);
                } else if (args[1].equalsIgnoreCase("25")) {
                    expMode = new ExpMode(ExpModeType.TwentyFiveTimes);
                }

                Player optionalPlayer = PlayerHandler.getPlayerByDisplayName(playerName);

                if (optionalPlayer != null) {
                    Player c2 = optionalPlayer;

                    c2.setExpMode(expMode);
                    c2.sendErrorMessage("Your expMode has been changed to " + expMode.getType().getFormattedName());
                    c.sendMessage("You have changed " + c2.getDisplayName() + "'s expMode to " +expMode.getType().getFormattedName());
                }
            } catch (Exception e) {
                c.sendErrorMessage("Format invalid ::changexp-name-(1,5,10,25)");
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("tradeban") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            String[] args = commandInput.split(" ");
            try {
                String playerName = args[0];
                Player optionalPlayer = PlayerHandler.getPlayerByDisplayName(playerName);

                if (optionalPlayer != null) {
                    Player c2 = optionalPlayer;

                    c2.tradeBanned = !c2.tradeBanned;

                    if (c2.tradeBanned) {
                        c2.sendErrorMessage("You have been trade banned by " + c.getDisplayName());
                        c.sendMessage("You have trade banned " + c2.getDisplayName());
                    } else {
                        c2.sendErrorMessage("You have been trade unbanned by " + c.getDisplayName());
                        c.sendMessage("You have trade unbanned " + c2.getDisplayName());
                    }
                }
            } catch (Exception e) {
                c.sendErrorMessage("Format invalid ::changexp-name-(1,5,10,25)");
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("restricteditems")) {

            System.out.println("Currently restricted item's are as follows: ");
            for (Integer restrictedItem : WildWarning.restrictedItems) {
                System.out.println(ItemDef.forId(restrictedItem).getName());
            }


            return true;
        }

        if (commandName.equalsIgnoreCase("scanitem") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            String[] args = commandInput.split("-");

            try {
                int itemId = Integer.parseInt(args[0]);

                for (Player player : Server.getPlayers()) {
                    if (player != null) {
                        if (player.getItems().getInventoryCount(itemId) > 0) {
                            c.sendErrorMessage("<shad=1>"+player.getDisplayName() + " has " + ItemDef.forId(itemId).getName() + " x " + player.getItems().getInventoryCount(itemId));
                        }
                        if (player.getBank().containsItem(itemId+1) && player.getBank().getItemCountInTabs(itemId+1) > 0) {
                            c.sendErrorMessage("<shad=1>"+player.getDisplayName() + " has " + player.getBank().getItemCountInTabs(itemId+1) + " x " + ItemDef.forId(itemId).getName() + " within there bank");
                        }
                        if (player.getItems().isWearingItem(itemId)) {
                            c.sendErrorMessage("<shad=1>"+player.getDisplayName() + " has " + ItemDef.forId(itemId).getName() + " equipped!");
                        }
                    }
                }
            } catch (Exception e) {
                c.sendErrorMessage("Format invalid ::scanitem-itemID");
            }
        }

        if (commandName.equalsIgnoreCase("takeall") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            try {
                String[] args = commandInput.split("-");
                if (args.length != 2) {
                    throw new IllegalArgumentException();
                }
                int itemID = Integer.parseInt(args[0]);
                int amount = Misc.stringToInt(args[1]);

                Set<String> uniqueUUIDs = new HashSet<>();
                ArrayList<Player> filteredPlayers = new ArrayList<>();

                for (Player player1 : Server.getPlayers()) {
                    if (player1 != null) {
                        String UUIDAddress = player1.getUUID();
                        if (!uniqueUUIDs.contains(UUIDAddress)) {
                            filteredPlayers.add(player1);
                            uniqueUUIDs.add(UUIDAddress);
                        }
                    }
                }

                if (c.debugMessage) {
                    c.sendMessage("You have removed " + uniqueUUIDs.size() + " / " + Server.getPlayers().size() + " player's " + amount + " x " + ItemDef.forId(itemID).getName());
                }

                for (Player filteredPlayer : filteredPlayers) {
                    filteredPlayer.getItems().deleteItem(itemID, amount);
                }

            } catch (Exception e) {
                c.sendMessage("Error. Correct syntax: ::takeall-itemid-amount");
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("playersonline") && c.getRights().isOrInherits(Right.MODERATOR)) {
            for (Player player : Server.getPlayers()) {
                if (player != null && !player.isIdle) {
                    player.forcedChat("I'm Online!!");
                    player.startAnimation(2106);
                }
            }

            return true;
        }

        if (commandName.equalsIgnoreCase("region") && c.getRights().isOrInherits(Right.STAFF_MANAGER)) {
            int regionX = c.getPosition().getX() >> 3;
            int regionY = c.getPosition().getY() >> 3;
            int regionId = ((regionX / 8) << 8) + (regionY / 8);
            int regionXx = regionId >> 8;
            int regionYy = regionId & 255;
            String name = "_" + regionXx + "_" + regionYy;
            c.sendErrorMessage("Current region is : " + regionId + " name: l" + name + " / m" + name);
            return true;
        }

        if (commandName.equalsIgnoreCase("giveiron") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            String[] args = commandInput.split("-");
            try {
                if (args.length < 1 || args.length > 1) {
                    throw new IllegalArgumentException();
                }
                String playerName = args[0];
                Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

                if (optionalPlayer.isPresent()) {
                    Player c2 = optionalPlayer.get();

                    c2.setMode(Mode.forType(ModeType.IRON_MAN));
                    c2.getRights().setPrimary(Right.IRONMAN);

                    c2.sendErrorMessage(c.getDisplayName() + " has just given you ironman mode!");
                    c.sendErrorMessage("You have just given " + c2.getDisplayName() + " ironman mode!");
                }

            } catch (Exception e) {
                c.sendErrorMessage("You clearly fucked up something the syntax is ::giveiron-name");
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("givecosmetic") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            String[] args = commandInput.split("-");
            try {
                if (args.length < 2 || args.length > 2) {
                    throw new IllegalArgumentException();
                }
                String playerName = args[0];
                int amount = Integer.parseInt(args[1]);
                Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

                if (optionalPlayer.isPresent()) {
                    Player c2 = optionalPlayer.get();

                    c2.cosmeticCredits += amount;
                    c2.sendErrorMessage(c.getDisplayName() + " has just given you " + amount + " cosmetic credits!");
                    c.sendErrorMessage("You have just given " + c2.getDisplayName() + ", " + amount + " cosmetic credits!");
                }

            } catch (Exception e) {
                c.sendErrorMessage("You clearly fucked up something the syntax is ::givecosmetic-name-amount");
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("fuckjdk14") && c.getRights().isOrInherits(Right.GAME_DEVELOPER)) {
            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    c.sendErrorMessage("Current cycle = " + container.getTotalExecutions() + " / " + System.currentTimeMillis());
                    long currentTimeMillis = System.currentTimeMillis();

                    // Convert to Instant
                    Instant instant = Instant.ofEpochMilli(currentTimeMillis);

                    // Convert to ZonedDateTime with system default time zone
                    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

                    // Format the date and time
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = zonedDateTime.format(formatter);
                    c.sendErrorMessage("The time is: " + formattedDateTime);
                    if (container.getTotalExecutions() == 50) {
                        container.stop();
                    }
                }
            }, 2);
            return true;
        }

        if (commandName.equalsIgnoreCase("reflist")) {
            RefManager.openInterface(c);
            return true;
        }

        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("changeuserpassword")) {
            String[] args = commandInput.split("-");
            try {
                if (args.length < 1 || args.length > 1) {
                    throw new IllegalArgumentException();
                }
                String playerName = args[0];

                String newPassword = generateRandomPassword();
                String hashedPassword = PasswordHashing.hash(newPassword);




                try {
                    updatePlayerSaveFile(playerName.toLowerCase(), hashedPassword);
                    c.sendErrorMessage(playerName + "'s new password is " + newPassword);
                } catch (IOException e) {
                    c.sendErrorMessage("Something royaly fucked up...");
                    System.out.println(e);
                }

            } catch (Exception e) {
                c.sendErrorMessage("You clearly fucked up something the syntax is ::changeuserpassword-name");
            }
            return true;
        }

        if (c.getRights().isOrInherits(Right.STAFF_MANAGER) && commandName.equalsIgnoreCase("gim2fix")) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            GroupIronmanGroup group = GroupIronmanRepository.getGroupForOffline(playerName).orElse(null);
            if (group == null) {
                c.sendMessage("No group that has player with login name '{}'.", playerName);
                return true;
            }

            group.setFinalized(false);
            GroupIronmanRepository.serializeAllInstant();
            return true;
        }

        if (c.getRights().isOrInherits(Right.STAFF_MANAGER) && commandName.equalsIgnoreCase("gimfix")) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            GroupIronmanGroup group = GroupIronmanRepository.getGroupForOffline(playerName).orElse(null);
            if (group == null) {
                c.sendMessage("No group that has player with login name '{}'.", playerName);
                return true;
            }

            group.setFinalized(false);
            GroupIronmanRepository.serializeAllInstant();
            return true;
        }

        if (commandName.equalsIgnoreCase("delnpc") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            int newNPC = Integer.parseInt(commandInput);
            if (newNPC > 0) {
                NPCHandler.despawn(newNPC, c.heightLevel);
                c.sendMessage("You despawn npc " + NpcDef.forId(newNPC).getName() + ", "+ newNPC);
            } else {
                c.sendMessage("No such NPC.");
            }
            return true;
        }


        if (commandName.equalsIgnoreCase("ref")) {
            String[] args = commandInput.split("-");
            String code = args[0];

            Optional<ReferralCode> referralCodeOptional = ReferralCode.getReferralCodes().stream().filter(ref -> ref.getCode().equalsIgnoreCase(code)).findFirst();
            referralCodeOptional.ifPresentOrElse(referralCode ->
                            EnterReferralDialogue.register(c, ReferralSource.YOUTUBE, code,
                                    referralCode.getRewards(), "Redeemed '" + code + "' referral!"),
                    () -> c.start(new DialogueBuilder(c).statement("No code found!").exit(p -> p.getPA().closeAllWindows())));

            return true;
        }

        if (commandName.equalsIgnoreCase("givecamp") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            int amount = Integer.parseInt(args[1]);

            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

            if (optionalPlayer.isPresent()) {
                Player p = optionalPlayer.get();

                DonationCampaign.addPurchaseClaim(p, p.getIpAddress(), System.currentTimeMillis(), amount);

                c.sendErrorMessage("You've given " + p.getDisplayName() + " $"+amount + " towards there campaign!");
                p.sendErrorMessage(c.getDisplayName() + " has given you $"+amount + " towards your campaign!");
            }
            return true;
        }

        if (c.getRights().isOrInherits(Right.STAFF_MANAGER) && commandName.equalsIgnoreCase("manti")) {
            c.queue(JusticarZachariah::spawnBoss);
            return true;
        }

        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("wealthcheck")) {
            List<String> lines = Lists.newArrayList();
            long coins = 0;
            long nomad = 0;
            long plat = 0;

            for (Player player : Server.getPlayers().toPlayerArray()) {
                if (player != null && !player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
                    coins += player.getItems().getInventoryCount(995);
                    plat += player.getItems().getInventoryCount(13204);
                    nomad += player.getItems().getInventoryCount(33237);
                    nomad += (player.getItems().getInventoryCount(691) * 10_000L);
                    nomad += (player.getItems().getInventoryCount(692) * 25_000L);
                    nomad += (player.getItems().getInventoryCount(693) * 50_000L);
                    nomad += (player.getItems().getInventoryCount(696) * 250_000L);

                    for (BankTab bankTab : player.getBank().getBankTab()) {
                        for (BankItem item : bankTab.getItems()) {
                            if (item.getId() == 995) {
                                coins += item.getAmount();
                            }
                            if (item.getId() == 13204) {
                                plat += item.getAmount();
                            }
                            if (item.getId() == 33237) {
                                nomad += (item.getAmount());
                            }
                            if (item.getId() == 691) {
                                nomad += (item.getAmount() * 10_000L);
                            }

                            if (item.getId() == 692) {
                                nomad += (item.getAmount() * 25_000L);
                            }

                            if (item.getId() == 693) {
                                nomad += (item.getAmount() * 50_000L);
                            }

                            if (item.getId() == 696) {
                                nomad += (item.getAmount() * 250_000L);
                            }
                        }
                    }
                    nomad += player.foundryPoints;

                    lines.add("User: " +player.getDisplayName() + "coins: " + Misc.formatCoins(coins) +", plat: " + Misc.formatCoins(plat) + ", nomad: " + Misc.formatCoins(nomad));
                }
            }

            c.getPA().openQuestInterface("Wealth checker", lines.stream().limit(149).collect(Collectors.toList()));
            return true;
        }

        if (commandName.equalsIgnoreCase("titles")) {
            c.getTitles().display();
            return true;
        }

        if (commandName.equalsIgnoreCase("sms")) {
            c.getPA().showInterface(24960);
            c.sendErrorMessage("We only accept US Phone Numbers!");
            c.sendErrorMessage("All phone numbers but start with +1!");
            c.sendErrorMessage("Phone Number Example: 123-456-7890 (+11234567890)");
            return true;
        }

        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("resetWeeklyDono")) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

            if (optionalPlayer.isPresent()) {
                Player c2 = optionalPlayer.get();

                c2.setWeeklyDonated(0);
                c2.sendErrorMessage(c.getDisplayName() + " has just reset your weekly donations, make sure to relog!");
                c.sendErrorMessage("You have just reset " + c2.getDisplayName() + "'s weekly donations!");
            }
            return true;
        }

        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("giveweeklydono")) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

            if (optionalPlayer.isPresent()) {
                Player c2 = optionalPlayer.get();

                c2.getDonationRewards().increaseDonationAmount(1);
                c2.sendErrorMessage(c.getDisplayName() + " has just increased your weekly donations!");
                c.sendErrorMessage("You have just increased " + c2.getDisplayName() + "'s weekly donations!");
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("delpet")) {
            PetManager.deletePet(c);
            return true;
        }

        if (commandName.equalsIgnoreCase("wiki")) {
            String[] args = commandInput.split(" ");
            c.getPA().openWebAddress("https://kyros.fandom.com/wiki/Kyros_Wiki");
            return true;
        }
        if (c.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("setmax")) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

            if (optionalPlayer.isPresent()) {
                Player c2 = optionalPlayer.get();

                for (int i = 0; i < 24; i++) {
                    c2.playerLevel[i] = 99;
                    c2.playerXP[i] = c2.getPA().getXPForLevel(99) + 1;
                    c2.getPA().refreshSkill(i);
                    c2.getPA().setSkillLevel(i, c2.playerLevel[i], c2.playerXP[i]);
                    c2.getPA().levelUp(i);
                }
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("setdono") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            int amount = Integer.parseInt(args[1]);
            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

            if (optionalPlayer.isPresent()) {
                Player c2 = optionalPlayer.get();

                c2.sendErrorMessage(c.getDisplayName() + " has set your donation amount to: " + amount + " !");
                c.sendErrorMessage("You have set " + c2.getDisplayName() + " donation amount to: " + amount + " !");

                c2.amDonated = amount;
                c2.updateRank();
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("givecampaign") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            int amount = Integer.parseInt(args[1]);
            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

            if (optionalPlayer.isPresent()) {
                Player c2 = optionalPlayer.get();
                DonationCampaign.addPurchaseClaim(c2, c2.getIpAddress(), System.currentTimeMillis(), amount);
            }
            return true;
        }


        if (commandName.equalsIgnoreCase("prophet")) {
            c.getPA().sendURL("https://www.youtube.com/@ProphetRSPS");
            return true;
        }

        if (commandName.equalsIgnoreCase("walkchaos")) {
            c.getPA().sendURL("https://www.youtube.com/@Walkchaos");
            return true;
        }

        if (commandName.equalsIgnoreCase("catherby")) {
            c.getPA().sendURL("https://www.youtube.com/@Catherbyrsps");
            return true;
        }

        if (commandName.equalsIgnoreCase("donocred")) {
            c.start(new DialogueBuilder(c).option("Would you like to convert your donor credits to points?", new DialogueOption("Yes", p -> {
                int amt = p.getItems().getInventoryCount(33251);
                if (amt <= 0) {
                    p.getPA().closeAllWindows();
                    p.sendErrorMessage("You don't have any donor credits!");
                    return;
                }
                p.getPA().closeAllWindows();
                p.getPA().sendEnterAmount("How many would you like to convert?", (plr, amount) -> {
                    int total_am = p.getItems().getInventoryCount(33251);

                    if (amount > total_am) {
                        amount = total_am;
                    }

                    plr.donatorPoints += amount;
                    plr.getItems().deleteItem2(33251, amount);
                });

            }), new DialogueOption("No thank you.", p->p.getPA().closeAllWindows())));
            return true;
        }

        if (commandName.equalsIgnoreCase("bj") && c.getDisplayName().equalsIgnoreCase("ark")) {
            c.getPA().showInterface(60950);
            return true;
        }



        if (commandName.equalsIgnoreCase("addgiminvite") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            String[] args = commandInput.split("-");
            String playerName = args[0];

            GroupIronmanGroup group = GroupIronmanRepository.getGroupForOffline(playerName).orElse(null);

            if (group != null) {
                if (group.getJoined() > 0)
                    group.setJoined(group.getJoined() - 1);
                c.sendErrorMessage("You have granted 1 extra invite to " + playerName + "!");
            }

            return true;
        }

        if (commandName.equalsIgnoreCase("giveperk") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            String[] args = commandInput.split("-");
            String playerName = args[0];
            int amount = Integer.parseInt(args[1]);
            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

            if (optionalPlayer.isPresent()) {
                Player c2 = optionalPlayer.get();

                c2.getCurrentPet().setSkillUpPoints((short) (c2.getCurrentPet().getSkillUpPoints() + amount));
                c2.sendErrorMessage(c.getDisplayName() + " has given you " + amount + " skill up points!");
                c.sendErrorMessage("You have given " + c2.getDisplayName() + " " + amount + " skill up points!");
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("fiftycent") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            FiftyCent.SpawnBoss();
            return true;
        }

        if (commandName.equalsIgnoreCase("removelevels") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            for (Player player : Server.getPlayers()) {
                if (player != null) {
                    if (player.getCurrentPet().getSkillUpPoints() > 100) {
                        player.getCurrentPet().setSkillUpPoints((short) 20);
                    }
                }
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("gfxfuck") && c.getDisplayName().equalsIgnoreCase("ark")) {
            TaskManager.submit(30, value -> {
                int gfx = Misc.random(20, 100);

                c.startGraphic(new Graphic(gfx));
            }, 2);
            return true;
        }

        if (commandName.equalsIgnoreCase("achievescan") && c.getRights().isOrInherits(Right.MODERATOR) || commandName.equalsIgnoreCase("whorehunt") && c.getRights().isOrInherits(Right.MODERATOR)) {
            List<String> lines = Lists.newArrayList();
            Server.getPlayers().stream().filter(Objects::nonNull).forEach(it -> {
                int counter = 0;
                for (Achievements.Achievement value : Achievements.Achievement.values()) {
                    if (it.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                        counter++;
                    }
                }
                lines.add(it.getDisplayName() + " completed achievements : " + counter+ " / " + Achievements.Achievement.values().length);
            });
            c.getPA().openQuestInterface("Accounts completed achievements", lines.stream().limit(149).collect(Collectors.toList()));
            return true;
        }

        if (commandName.equalsIgnoreCase("bp")) {
            Pass.openInterface(c);
            c.getQuesting().handleHelpTabActionButton(669);
            return true;
        }

        if (commandName.equalsIgnoreCase("giveboost") && c.getRights().isOrInherits(Right.ADMINISTRATOR)) {


            String[] args = commandInput.split("-");
            String playerName = args[0];
            int amount = Integer.parseInt(args[1]);
            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

            if (optionalPlayer.isPresent()) {
                Player c2 = optionalPlayer.get();

                AccountBoosts.addWeeklyDono(c2, (int) amount);
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("fuckit") && c.getDisplayName().equalsIgnoreCase("luke")) {
            BasicBot.startBots();
            return true;
        }

        if (commandName.equalsIgnoreCase("donoboss") && c.getRights().isOrInherits(Right.MODERATOR)) {
            dboss.spawnBoss();
            return true;
        }

        if (commandName.equalsIgnoreCase("perk")) {
            c.getPA().startTeleport(3363, 9640, 0,"modern", false);
            return true;
        }

        try {
            String path = "io.kyros.content.commands." + commandPackage + "." + className;
            if (COMMAND_MAP.get(path.toLowerCase()) != null) {
                COMMAND_MAP.get(path.toLowerCase()).execute(c, commandName, commandInput);
                return true;
            }
            return false;
        } catch (Exception e) {
            c.sendMessage("Error while executing the following command: " + playerCommand);
            e.printStackTrace();
            return true;
        }
    }

    private static void initialize(String path) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> commandClass = Class.forName(path);
        Object instance = commandClass.newInstance();
        if (instance instanceof Command) {
            Command command = (Command) instance;
            COMMAND_MAP.putIfAbsent(path.toLowerCase(), command);
            log.fine(String.format("Added command [path=%s] [command=%s]", path, command.toString()));
        }
    }

    public static void initializeCommands() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        if (Server.isDebug() || Server.isTest()) { // Important that this doesn't get removed
            COMMAND_PACKAGES.add(new CommandPackage("test", Right.PLAYER));
        }
        Reflections reflections = new Reflections("io.kyros.content.commands");
        for (Class<? extends Command> clazz : reflections.getSubTypesOf(Command.class)) {
            initialize(clazz.getName());
        }
        log.info("Loaded " + COMMAND_MAP.size() + " commands.");
    }

    private static String generateRandomPassword() {
        String characters = "abcdefghijklmnopqrstuvwxyz";
        int length = 10;
        Random random = new Random();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    private static void updatePlayerSaveFile(String username, String hashedPassword) throws IOException {
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
}
