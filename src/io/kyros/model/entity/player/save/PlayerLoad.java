package io.kyros.model.entity.player.save;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementTier;
import io.kyros.content.achievement_diary.DifficultyAchievementDiary;
import io.kyros.content.achievement_diary.impl.*;
import io.kyros.content.bonus.BoostScrolls;
import io.kyros.content.combat.pvp.Killstreak;
import io.kyros.content.event.eventcalendar.EventCalendar;
import io.kyros.content.event.eventcalendar.EventChallengeKey;
import io.kyros.content.lootbag.LootingBag;
import io.kyros.content.lootbag.LootingBagItem;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.content.privatemessaging.FriendType;
import io.kyros.content.privatemessaging.FriendsListEntry;
import io.kyros.content.seasons.Christmas;
import io.kyros.content.skills.slayer.SlayerMaster;
import io.kyros.content.skills.slayer.SlayerUnlock;
import io.kyros.content.skills.slayer.Task;
import io.kyros.content.skills.slayer.TaskExtension;
import io.kyros.content.teleportv2.inter.TeleportInterface;
import io.kyros.content.titles.Title;
import io.kyros.content.tradingpost.TradePostOffer;
import io.kyros.model.controller.ControllerRepository;
import io.kyros.model.entity.HealthBar;
import io.kyros.model.entity.player.LoadGameResult;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.Right;
import io.kyros.model.entity.player.mode.ExpMode;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.entity.player.mode.group.ExpModeType;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.bank.BankItem;
import io.kyros.util.Misc;
import io.kyros.util.PasswordHashing;
import io.kyros.util.Reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;

public class PlayerLoad {

    private static final Logger logger = LoggerFactory.getLogger(PlayerLoad.class);

    public static String getSaveDirectory() {
        return Server.getSaveDirectory() + "/character_saves/";
    }


    public static List<PlayerSaveEntry> playerSaveEntryList = Lists.newArrayList();

    /**
     * Reflect and collect {@link PlayerSaveEntry}
     */
    public static void loadPlayerSaveEntries() {
        Reflection.getSubClasses(PlayerSaveEntry.class).forEach(clazz -> {
            try {
                playerSaveEntryList.add((PlayerSaveEntry) clazz.getConstructors()[0].newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        playerSaveEntryList = Collections.unmodifiableList(playerSaveEntryList);
        logger.info("Loaded " + playerSaveEntryList.size() + " Player Save Entries.");
    }

    public static void login(Player player) {
        playerSaveEntryList.forEach(entry -> entry.login(player));
    }

    /**
     * Loading
     */
    public static LoadGameResult loadGame(Player player, String playerName, String playerPass, boolean passedCaptcha) {
        Misc.createDirectory(getSaveDirectory());
        String line = "";
        String token = "";
        String token2 = "";
        String[] token3 = new String[3];

        boolean EndOfFile = false;
        int ReadMode = 0;
        BufferedReader characterfile = null;
        boolean characterFileExists = false;

        try {
            characterfile = new BufferedReader(new FileReader(getSaveDirectory() + playerName.toLowerCase() + ".txt"));
            characterFileExists = true;
        } catch (FileNotFoundException ignored) {
            System.out.println(ignored.getMessage());
        }

        if (!characterFileExists) {
            return LoadGameResult.NEW_PLAYER;
        }

        try {
            line = characterfile.readLine();
        } catch (IOException ioexception) {
            logger.error("Error while loading {}", playerName, ioexception);
            Misc.println(playerName + ": error loading file.");
            return LoadGameResult.ERROR_OCCURRED;
        }
        try {
            player.getFarming().load();
        } catch (Exception e) {
            logger.error("Error while loading farming {}", playerName, e);
            e.printStackTrace();
        }

        // Migrating old accounts, if new launch this can be removed along with [FRIENDS]/[IGNORES] reading
        List<FriendsListEntry> friends = new ArrayList<>();

        main:
        while (EndOfFile == false && line != null) {
            line = line.trim();
            try {
                int spot = line.indexOf("=");
                if (spot > -1) {
                    token = line.substring(0, spot);
                    token = token.trim();
                    token2 = line.substring(spot + 1);
                    token2 = token2.trim();
                    token3 = token2.split("\t");
                    if (ReadMode == 2) {
                        for (PlayerSaveEntry entry : playerSaveEntryList) {
                            if (entry.getKeys(player).contains(token)) {
                                Preconditions.checkState(entry.decode(player, token, token2), "Failed to decode player save entry: " + entry.getClass() + ", token: " + token);
                                line = characterfile.readLine();
                                continue main;
                            }
                        }
                    }
                    switch (ReadMode) {
                        case 1:
                            if (token.equals("character-password")) {
                                try {
                                    if (PasswordHashing.check(player.playerPass, token2)) {
                                        playerPass = token2;
                                    } else {
                                        if (Server.isDebug()) {
                                            System.out.println("Invalid password but server is in debug mode so it\'s ignored.");
                                        } else return LoadGameResult.INVALID_CREDENTIALS;
                                    }
                                } catch (IllegalArgumentException e) {
                                    logger.error("Error while loading {}", playerName, e);
                                    e.printStackTrace();
                                    return LoadGameResult.ERROR_OCCURRED;
                                }
                            }
                            break;
                        case 2:
                            if (token.equals("character-height")) {
                                player.heightLevel = Integer.parseInt(token2);
                            } else if (token.equals("character-hp")) {
                                player.getHealth().setCurrentHealth(Integer.parseInt(token2));
                                if (player.getHealth().getCurrentHealth() <= 0) {
                                    player.getHealth().setCurrentHealth(10);
                                }
                            } else if (token.equals("character-mac-address")) {
                                if (!player.getMacAddress().equalsIgnoreCase(token2)) {
                                    if (!Configuration.DISABLE_CHANGE_ADDRESS_CAPTCHA && !passedCaptcha)
                                        return LoadGameResult.REQUIRE_CAPTCHA;
                                    player.setAddressChanged("mac", token2, player.getMacAddress(), true);
                                }
                            } else if (token.equals("character-ip-address")) {
                                if (!player.getIpAddress().equalsIgnoreCase(token2)) {
                                    player.setAddressChanged("ip", token2, player.getIpAddress(), false);
                                }
                            } else if (token.equals("character-uuid")) {
                                if (!player.getUUID().equalsIgnoreCase(token2)) {
                                    if (!Configuration.DISABLE_CHANGE_ADDRESS_CAPTCHA && !passedCaptcha)
                                        return LoadGameResult.REQUIRE_CAPTCHA;
                                    player.setAddressChanged("uuid", token2, player.getUUID(), true);
                                }
                            } else if (token.equals("play-time")) {
                                player.playTime = Integer.parseInt(token2);
                            } else if (token.equals("last-clan")) {
                                player.setLastClanChat(token2);
                            } else if (token.equals("require-pin-unlock")) {
                                boolean requiresPinUnlock = Boolean.parseBoolean(token2);
                                if (requiresPinUnlock) {
                                    player.setRequiresPinUnlock(requiresPinUnlock);
                                    player.addQueuedLoginAction(plr -> {
                                        if (!plr.getBankPin().hasBankPin()) {
                                            plr.setRequiresPinUnlock(false);
                                            return;
                                        }
                                        plr.sendMessage("@cr2@@dre@Your pin is required because you logged in from a different computer");
                                        plr.sendMessage("@cr2@@dre@and logged off without entering your account pin.");
                                        plr.sendMessage("@cr2@@red@If this wasn't you then you should secure your account!");
                                    });
                                }
                            } else if (token.equals("teleportfavourites")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    for (TeleportInterface.MONSTERS value : TeleportInterface.MONSTERS.values()) {
                                        if (value.name().equalsIgnoreCase(claim)) {
                                            TeleportInterface.Teleport monsters = TeleportInterface.MONSTERS.valueOf(claim);
                                            if (monsters != null) {
                                                player.getFavoriteTeleports().add(monsters);
                                            }
                                        }
                                    }
                                    for (TeleportInterface.BOSSES value : TeleportInterface.BOSSES.values()) {
                                        if (value.name().equalsIgnoreCase(claim)) {
                                            TeleportInterface.Teleport bosses = TeleportInterface.BOSSES.valueOf(claim);
                                            if (bosses != null) {
                                                player.getFavoriteTeleports().add(bosses);
                                            }
                                        }
                                    }
                                    for (TeleportInterface.MINIGAMES value : TeleportInterface.MINIGAMES.values()) {
                                        if (value.name().equalsIgnoreCase(claim)) {
                                            TeleportInterface.Teleport minigames = TeleportInterface.MINIGAMES.valueOf(claim);
                                            if (minigames != null) {
                                                player.getFavoriteTeleports().add(minigames);
                                            }
                                        }
                                    }
                                    for (TeleportInterface.DUNGEONS value : TeleportInterface.DUNGEONS.values()) {
                                        if (value.name().equalsIgnoreCase(claim)) {
                                            TeleportInterface.Teleport dungeons = TeleportInterface.DUNGEONS.valueOf(claim);
                                            if (dungeons != null) {
                                                player.getFavoriteTeleports().add(dungeons);
                                            }
                                        }
                                    }
                                    for (TeleportInterface.SKILLING value : TeleportInterface.SKILLING.values()) {
                                        if (value.name().equalsIgnoreCase(claim)) {
                                            TeleportInterface.Teleport skilling = TeleportInterface.SKILLING.valueOf(claim);
                                            if (skilling != null) {
                                                player.getFavoriteTeleports().add(skilling);
                                            }
                                        }
                                    }
                                    for (TeleportInterface.PK value : TeleportInterface.PK.values()) {
                                        if (value.name().equalsIgnoreCase(claim)) {
                                            TeleportInterface.Teleport pk = TeleportInterface.PK.valueOf(claim);
                                            if (pk != null) {
                                                player.getFavoriteTeleports().add(pk);
                                            }
                                        }
                                    }
                                }
                            } else if (token.equals("character-specRestore")) {
                                player.specRestore = Integer.parseInt(token2);
                            } else if (token.equals("character-posx")) {
                                player.setTeleportToX((Integer.parseInt(token2) == -1 ? Configuration.EDGEVILLE_X : Integer.parseInt(token2)));
                            } else if (token.equals("character-posy")) {
                                player.setTeleportToY((Integer.parseInt(token2) == -1 ? Configuration.EDGEVILLE_Y : Integer.parseInt(token2)));
                            } else if (token.equals("character-rights")) {
                                player.getRights().setPrimary(Right.get(Integer.parseInt(token2)));
                                if(player.getRights().contains(Right.GAME_DEVELOPER))
                                    player.healthBar = new HealthBar(player, 23);
                            } else if (token.equals("character-rights-secondary")) {
                                // sound like an activist group
                                Arrays.stream(token3).forEach(right -> player.getRights().add(Right.get(Integer.parseInt(right))));
                            } else if (token.equals("migration-version")) {
                                player.setMigrationVersion(Integer.parseInt(token2));
                            } else if (token.equals("revert-option")) {
                                player.setRevertOption(token2);
                            } else if (token.equals("revert-delay")) {
                                player.setRevertModeDelay(Long.parseLong(token2));
                            } else if (token.equals("dropBoostStart")) {
                                player.dropBoostStart = Long.parseLong(token2);
                            } else if (token.equals("mode")) {
                                ModeType type = null;
                                try {
                                    if (token2.equals("NONE")) {
                                        token2 = "REGULAR";
                                    }
                                    type = Enum.valueOf(ModeType.class, token2);
                                } catch (NullPointerException | IllegalArgumentException e) {
                                    e.printStackTrace();
                                    logger.error("Error while loading mode {}, type={}", playerName, token2, e);
                                    break;
                                }
                                Mode mode = Mode.forType(type);
                                player.setMode(mode);
                            } else if (token.equals("expmode")) {
                                ExpModeType type = null;
                                try {
                                    if (token2.equals("NONE")) {
                                        token2 = "TwentyFiveTimes";
                                    }
                                    type = Enum.valueOf(ExpModeType.class, token2);
                                } catch (NullPointerException | IllegalArgumentException e) {
                                    e.printStackTrace();
                                    logger.error("Error while loading expMode {}, type={}", playerName, token2, e);
                                    break;
                                }
                                player.setExpMode(new ExpMode(type));
                            } else if (token.equals("character-title-updated")) {
                                player.getTitles().setCurrentTitle(token2);
                            } else if (token.equals("receivedVoteStreakRefund")) {
                                player.setReceivedVoteStreakRefund(Boolean.parseBoolean(token2));
                            } else if (token.equals("experience-counter")) {
                                player.setExperienceCounter(Long.parseLong(token2));
                            } else if (token.equals("collectorNecklace")) {
                                player.collectNecklace = Boolean.parseBoolean(token2);
                            } else if (token.equals("tradeBanned")) {
                                player.tradeBanned = Boolean.parseBoolean(token2);
                            } else if (token.equals("skillingMinigame")) {
                                player.skillingMinigame = Boolean.parseBoolean(token2);
                            }  else if (token.equals("nexUnlocked")) {
                                player.NexUnlocked = Boolean.parseBoolean(token2);
                            }  else if (token.equals("combatskillingUnlocked")) {
                                player.CombatSkillingUnlocked = Boolean.parseBoolean(token2);
                            }  else if (token.equals("ragePotion")) {
                                player.usingRage = Boolean.parseBoolean(token2);
                            }  else if (token.equals("ambitionPotion")) {
                                player.usingAmbition = Boolean.parseBoolean(token2);
                            }  else if (token.equals("hweenUnlocked")) {
                                player.halloweenGlobal = Boolean.parseBoolean(token2);
                            } else if(token.equals("comp-cape-colours")) {
                                int[] colours = new int[4];
                                for (int i = 0; i < colours.length; i++) {
                                    colours[i] = Integer.parseInt(token3[i]);
                                }
                                player.getCompletionistCapeRe().setColours(colours[0], colours[1], colours[2], colours[3]);
                            } else if (token.equals("connected-from")) {
                                player.lastConnectedFrom.add(token2);
                            } else if (token.equals("printAttackStats")) {
                                player.setPrintAttackStats(Boolean.parseBoolean(token2));
                            } else if (token.equals("printDefenceStats")) {
                                player.setPrintDefenceStats(Boolean.parseBoolean(token2));
                            } else if (token.equals("collectCoins")) {
                                player.collectCoins = Boolean.parseBoolean(token2);
                            } else if (token.equals("horror-from-deep")) {
                                player.horrorFromDeep = Integer.parseInt(token2);
                            } else if (token.equals("breakVials")) {
                                player.breakVials = Boolean.parseBoolean(token2);
                            } else if (token.equals("absorption")) {
                                player.absorption = Boolean.parseBoolean(token2);
                            } else if (token.equals("announce")) {
                                player.announce = Boolean.parseBoolean(token2);
                            } else if (token.equals("lootPickUp")) {
                                player.lootPickUp = Boolean.parseBoolean(token2);
                            } else if (token.equals("barbarian")) {
                                player.barbarian = Boolean.parseBoolean(token2);
                            } else if (token.equals("run-energy")) {
                                player.setRunEnergy(Integer.parseInt(token2), false);
                            } else if (token.equals("bank-pin")) {
                                player.getBankPin().setPin(token2);
                            } else if (token.equals("bank-pin-cancellation")) {
                                player.getBankPin().setAppendingCancellation(Boolean.parseBoolean(token2));
                            } else if (token.equals("bank-pin-cancellation-delay")) {
                                player.getBankPin().setCancellationDelay(Long.parseLong(token2));
                            } else if (token.equals("bank-pin-unlock-delay")) {
                                player.getBankPin().setUnlockDelay(Long.parseLong(token2));
                            } else if (token.equals("placeholders")) {
                                player.placeHolders = Boolean.parseBoolean(token2);
                            } else if (token.equals("show-drop-warning")) {
                                player.setDropWarning(Boolean.parseBoolean(token2));
                            } else if (token.equals("show-alch-warning")) {
                                player.setAlchWarning(Boolean.parseBoolean(token2));
                            } else if (token.equals("hourly-box-toggle")) {
                                player.setHourlyBoxToggle(Boolean.parseBoolean(token2));
                            } else if (token.equals("enable-levelup-messages")) {
                                player.enableLevelUpMessage = Boolean.parseBoolean(token2);
                            } else if (token.equals("fractured-crystal-toggle")) {
                                player.setFracturedCrystalToggle(Boolean.parseBoolean(token2));
                            } else if (token.equals("accept-aid")) {
                                player.acceptAid = Boolean.parseBoolean(token2);
                            } else if (token.equals("did-you-know")) {
                                player.didYouKnow = Boolean.parseBoolean(token2);
                            } else if (token.equals("spectating-tournament")) {
                                player.spectatingTournament = Boolean.parseBoolean(token2);
                            } else if (token.equals("infpot")) {
                                player.InfAgroTimer = Long.parseLong(token2);
                            } else if (token.equals("ragepot")) {
                                player.RageTimer = Long.parseLong(token2);
                            } else if (token.equals("ambitionpot")) {
                                player.AmbitionTimer = Long.parseLong(token2);
                            } else if (token.equals("raidPoints")) {
                                player.setRaidPoints(Integer.parseInt(token2));
                            } else if (token.equals("raidCount")) {
                                player.raidCount = (Integer.parseInt(token2));
                            } else if (token.equals("tobCompletions")) {
                                player.tobCompletions = (Integer.parseInt(token2));
                            } else if (token.equals("arboCompletions")) {
                                player.arboCompletions = (Integer.parseInt(token2));
                            } else if (token.equals("lootvalue")) {
                                player.lootValue = Integer.parseInt(token2);
                            } else if (token.equals("startPack")) {
                                player.setCompletedTutorial(Boolean.parseBoolean(token2));
                            } else if (token.equals("achieveFix")) {
                                player.hasAchieveFix = Boolean.parseBoolean(token2);
                            } else if (token.equals("unlockedUltimateChest")) {
                                player.unlockedUltimateChest = Boolean.parseBoolean(token2);
                            } else if (token.equals("rigour")) {
                                player.rigour = Boolean.parseBoolean(token2);
                            } else if (token.equals("augury")) {
                                player.augury = Boolean.parseBoolean(token2);
                            } else if (token.equals("crystalDrop")) {
                                player.crystalDrop = Boolean.parseBoolean(token2);
                            } else if (token.equals("spawnedbarrows")) {
                                player.spawnedbarrows = Boolean.parseBoolean(token2);
                            } else if (token.equals("membershipStartDate")) {
                                player.startDate = Integer.parseInt(token2);
                            } else if (token.equals("XpScrollTime")) {//halloweenCandy =
                                player.xpScrollTicks = Long.parseLong(token2);
                            } else if (token.equals("halloweenCandy")) {//halloweenCandy =
                                player.candyTimer = Long.parseLong(token2);
                            } else if (token.equals("fasterClueScrollTime")) {
                                player.fasterCluesTicks = Long.parseLong(token2);
                            } else if (token.equals("skillingPetRateTime")) {
                                player.skillingPetRateTicks = Long.parseLong(token2);
                            } else if (token.equals("serpHelmCombatTicks")) {
                                player.serpHelmCombatTicks = Long.parseLong(token2);
                            } else if (token.equals("gargoyleStairsUnlocked")) {
                                player.gargoyleStairsUnlocked = Boolean.parseBoolean(token2);
                            } else if (token.equals("controller")) {
                                player.setLoadedController(ControllerRepository.get(token2));
                            } else if (token.equals("joinedIronmanGroup")) {
                                player.setJoinedIronmanGroup(Boolean.parseBoolean(token2));
                            } else if (token.equals("receivedCalendarCosmeticJune2021")) {
                                player.setReceivedCalendarCosmeticJune2021(Boolean.parseBoolean(token2));
                            } else if (token.equals("firstAchievementLoginJune2021")) {
                                player.getAchievements().setFirstAchievementLoginJune2021(Boolean.parseBoolean(token2));
                            } else if (token.equals("XpScroll")) {
                                player.xpScroll = Boolean.parseBoolean(token2);
                            } else if (token.equals("maxAttack")) {
                                player.maxAttack = Boolean.parseBoolean(token2);
                            } else if (token.equals("maxStrength")) {
                                player.maxStrength = Boolean.parseBoolean(token2);
                            } else if (token.equals("maxDefense")) {
                                player.maxDefense = Boolean.parseBoolean(token2);
                            } else if (token.equals("maxRange")) {
                                player.maxRange = Boolean.parseBoolean(token2);
                            } else if (token.equals("maxHealth")) {
                                player.maxHealth = Boolean.parseBoolean(token2);
                            } else if (token.equals("maxMage")) {
                                player.maxMage = Boolean.parseBoolean(token2);
                            } else if (token.equals("maxPrayer")) {
                                player.maxPrayer = Boolean.parseBoolean(token2);
                            } else if (token.equals("BonusDmg")) {
                                player.bonusDmg = Boolean.parseBoolean(token2);
                            } else if (token.equals("BonusDmgTime")) {
                                player.bonusDmgTicks = Long.parseLong(token2);
                            } else if (token.equals("skillingPetRateScroll")) {
                                player.skillingPetRateScroll = Boolean.parseBoolean(token2);
                            } else if (token.equals("fasterClueScroll")) {
                                player.fasterCluesScroll = Boolean.parseBoolean(token2);
                            }else if (token.equals("activeMageArena2BossId")) {
                                for (int i = 0; i < player.activeMageArena2BossId.length; i++) player.activeMageArena2BossId[i] = Integer.parseInt(token3[i]);
                            }else if (token.equals("mageArena2SpawnsX")) {
                                for (int i = 0; i < player.mageArena2SpawnsX.length; i++) player.mageArena2SpawnsX[i] = Integer.parseInt(token3[i]);
                            }else if (token.equals("mageArena2SpawnsY")) {
                                for (int i = 0; i < player.mageArena2SpawnsY.length; i++) player.mageArena2SpawnsY[i] = Integer.parseInt(token3[i]);
                            }else if (token.equals("mageArenaBossKills")) {
                                for (int i = 0; i < player.mageArenaBossKills.length; i++) player.mageArenaBossKills[i] = Boolean.parseBoolean(token3[i]);
                            }else if (token.equals("mageArena2Stages")) {
                                for (int i = 0; i < player.mageArena2Stages.length; i++) player.mageArena2Stages[i] = Boolean.parseBoolean(token3[i]);
                            }else if (token.equals("flamesOfZamorakCasts")) {
                                player.flamesOfZamorakCasts = (Integer.parseInt(token2));
                            }else if (token.equals("flamesOfZamorakCasts")) {
                                player.flamesOfZamorakCasts = (Integer.parseInt(token2));
                            }else if (token.equals("clawsOfGuthixCasts")) {
                                player.clawsOfGuthixCasts = (Integer.parseInt(token2));
                            }else if (token.equals("saradominStrikeCasts")) {
                                player.saradominStrikeCasts = (Integer.parseInt(token2));
                            }else if (token.equals("exchangeP")) {
                                player.exchangePoints = (Integer.parseInt(token2));
                            }else if (token.equals("totalEarnedExchangeP")) {
                                player.totalEarnedExchangePoints = (Integer.parseInt(token2));
                            } else if (token.equals("usedFc")) {
                                player.usedFc = Boolean.parseBoolean(token2);
                            } else if (token.equals("unlockedSpecialTasks")) {
                                player.unlockedSpecialTasks = Boolean.parseBoolean(token2);
                            } else if (token.equals("specialTaskNpc")) {
                                player.specialTaskNpc = Integer.parseInt(token2);
                            } else if (token.equals("specialTaskAmount")) {
                                player.specialTaskAmount = Integer.parseInt(token2);
                            } else if (token.equals("BaBaInstanceKills")) {
                                player.BaBaInstanceKills = Integer.parseInt(token2);
                            } else if (token.equals("enhancerCrystal")) {
                                player.enhancerCrystal = Boolean.parseBoolean(token2);
                            } else if (token.equals("chaoticInstance")) {
                                player.unlockChaoticInstance = Boolean.parseBoolean(token2);
                            } else if (token.equals("lastLoginDate")) {
                                player.lastLoginDate = Integer.parseInt(token2);
                            } else if (token.equals("summonId")) {
                                player.petSummonId = Integer.parseInt(token2);
                            } else if (token.equals("has-npc")) {
                                player.hasFollower = Boolean.parseBoolean(token2);
                            } else if (token.equals("setPin")) {
                                player.setPin = Boolean.parseBoolean(token2);
                            } else if (token.equals("hasBankpin")) {
                                player.hasBankpin = Boolean.parseBoolean(token2);
                            } else if (token.equals("rfd-gloves")) {
                                player.rfdGloves = Integer.parseInt(token2);
                            } else if (token.equals("wave-id")) {
                                player.waveId = Integer.parseInt(token2);
                            } else if (token.equals("wave-type")) {
                                player.fightCavesWaveType = Integer.parseInt(token2);
                            } else if (token.equals("wave-info")) {
                                for (int i = 0; i < player.waveInfo.length; i++) player.waveInfo[i] = Integer.parseInt(token3[i]);
                            } else if (token.equals("help-cc-muted")) {
                                player.setHelpCcMuted(Boolean.parseBoolean(token2));
                            } else if (token.equals("gamble-banned")) {
                                player.setGambleBanned(Boolean.parseBoolean(token2));
                            } else if (token.equals("usedReferral")) {
                                player.usedReferral = Boolean.parseBoolean(token2);
                            } else if (token.equals("counters")) {
                                for (int i = 0; i < player.counters.length; i++) player.counters[i] = Integer.parseInt(token3[i]);
                            } else if (token.equals("max-cape")) {
                                for (int i = 0; i < player.maxCape.length; i++) player.maxCape[i] = Boolean.parseBoolean(token3[i]);
                            } else if (token.equals("master-clue-reqs")) {
                                for (int i = 0; i < player.masterClueRequirement.length; i++) player.masterClueRequirement[i] = Integer.parseInt(token3[i]);
                            } else if (token.equals("quickprayer")) {
                                for (int j = 0; j < token3.length; j++) {
                                    player.getQuick().getNormal()[j] = Boolean.parseBoolean(token3[j]);
                                }
                            } else if (token.equals("zulrah-best-time")) {
                                player.setBestZulrahTime(Long.parseLong(token2));
                            } else if (token.equals("inferno-best-time")) {
                                player.setInfernoBestTime(Long.parseLong(token2));
                            } else if (token.equals("toxic-staff")) {
                                player.setToxicStaffOfTheDeadCharge(Integer.parseInt(token2));
                            } else if (token.equals("toxic-pipe-ammo")) {
                                player.setToxicBlowpipeAmmo(Integer.parseInt(token2));
                            } else if (token.equals("toxic-pipe-amount")) {
                                player.setToxicBlowpipeAmmoAmount(Integer.parseInt(token2));
                            } else if (token.equals("toxic-pipe-charge")) {
                                player.setToxicBlowpipeCharge(Integer.parseInt(token2));
                            } else if (token.equals("serpentine-helm")) {
                                player.setSerpentineHelmCharge(Integer.parseInt(token2));
                            } else if (token.equals("trident-of-the-seas")) {
                                player.setTridentCharge(Integer.parseInt(token2));
                            } else if (token.equals("trident-of-the-swamp")) {
                                player.setToxicTridentCharge(Integer.parseInt(token2));
                            } else if (token.equals("arclight-charge")) {
                                player.setArcLightCharge(Integer.parseInt(token2));
                            } else if (token.equals("sang-staff-charge")) {
                                player.setSangStaffCharge(Integer.parseInt(token2));
                            } else if (token.equals("bryophyta-charge")) {
                                player.bryophytaStaffCharges = Integer.parseInt(token2);
                            } else if (token.equals("crystal-bow-shots")) {
                                player.crystalBowArrowCount = Integer.parseInt(token2);
                            } else if (token.equals("skull-timer")) {
                                player.skullTimer = Integer.parseInt(token2);
                            } else if (token.equals("afkTier")) {
                                player.setAfkTier(Integer.parseInt(token2));
                            } else if (token.equals("afkAttempts")) {
                                player.setAfkAttempts(Integer.parseInt(token2));
                            } else if (token.equals("magic-book")) {
                                player.playerMagicBook = Integer.parseInt(token2);
                            } else if (token.equals("slayer-recipe") || token.equals("slayer-helmet")) {
                                // Backwards compat
                                if (Boolean.parseBoolean(token2)) {
                                    player.getSlayer().getUnlocks().add(SlayerUnlock.MALEVOLENT_MASQUERADE);
                                }
                            } else if (token.equals("bigger-boss-tasks")) {
                                player.getSlayer().setBiggerBossTasks(Boolean.parseBoolean(token2));
                            } else if (token.equals("cerberus-route")) {
                                player.getSlayer().setCerberusRoute(Boolean.parseBoolean(token2));
                            } else if (token.equals("superior-slayer")) {
                                // Backwards compat
                                if (Boolean.parseBoolean(token2)) {
                                    player.getSlayer().getUnlocks().add(SlayerUnlock.BIGGER_AND_BADDER);
                                }
                            } else if (token.equals("slayer-tasks-completed")) {
                                player.slayerTasksCompleted = Integer.parseInt(token2);
                            } else if (token.equals("claimedReward")) {
                                player.claimedReward = Boolean.parseBoolean(token2);
                            } else if (token.equals("special-amount")) {
                                player.specAmount = Double.parseDouble(token2);
                            } else if (token.equals("prayer-amount")) {
                                player.prayerPoint = Double.parseDouble(token2);
                            } else if (token.equals("dragonfire-shield-charge")) {
                                player.setDragonfireShieldCharge(Integer.parseInt(token2));
                            } else if (token.equals("autoRet")) {
                                player.autoRet = Integer.parseInt(token2);
                            } else if (token.equals("pkp")) {
                                player.pkp = Integer.parseInt(token2);
                            } else if (token.equals("elvenCharge")) {
                                player.elvenCharge = Integer.parseInt(token2);
                            } else if (token.equals("slaughterCharge")) {
                                player.slaughterCharge = Integer.parseInt(token2);
                            } else if (token.equals("pages")) {
                                int oldTomeOfFirePages = Integer.parseInt(token2) / 20;
                                player.getTomeOfFire().addPages(oldTomeOfFirePages);
                            } else if (token.equals("tomeOfFirePages")) {
                                int pages = Integer.parseInt(token2);
                                player.getTomeOfFire().setPages(pages);
                            } else if (token.equals("tomeOfFireCharges")) {
                                int charges = Integer.parseInt(token2);
                                player.getTomeOfFire().setCharges(charges);
                            } else if (token.equals("ether")) {
                                player.braceletEtherCount = Integer.parseInt(token2);
                            } else if (token.equals("bossPoints")) {
                                player.bossPoints = Integer.parseInt(token2);
                            } else if (token.equals("bossPointsRefund")) {
                                player.bossPointsRefund = Boolean.parseBoolean(token2);
                            } else if (token.equals("tWin")) {
                                player.tournamentWins = Integer.parseInt(token2);
                            } else if (token.equals("tPoint")) {
                                player.tournamentPoints = Integer.parseInt(token2);
                            }  else if (token.equals("wgPoint")) {
                                player.WGPoints = Integer.parseInt(token2);
                            } else if (token.equals("wgWin")) {
                                player.WGWins = Integer.parseInt(token2);
                            } else if (token.equals("streak")) {
                                player.streak = Integer.parseInt(token2);
                            } else if (token.equals("outlastKills")) {
                                player.outlastKills = Integer.parseInt(token2);
                            } else if (token.equals("outlastDeaths")) {
                                player.outlastDeaths = Integer.parseInt(token2);
                            } else if (token.equals("tournamentTotalGames")) {
                                player.tournamentTotalGames = Integer.parseInt(token2);
                            } else if (token.equals("xpMaxSkills")) {
                                player.xpMaxSkills = Integer.parseInt(token2);
                            } else if (token.equals("LastLoginYear")) {
                                player.LastLoginYear = Integer.parseInt(token2);
                            } else if (token.equals("LastLoginMonth")) {
                                player.LastLoginMonth = Integer.parseInt(token2);
                            } else if (token.equals("LastLoginDate")) {
                                player.LastLoginDate = Integer.parseInt(token2);
                            } else if (token.equals("LoginStreak")) {
                                player.LoginStreak = Integer.parseInt(token2);
                            } else if (token.equals("RefU")) {
                                player.referallFlag = Integer.parseInt(token2);
                            } else if (token.equals("LoyP")) {
                                player.loyaltyPoints = Integer.parseInt(token2);
                            } else if (token.equals("votePoints")) {
                                player.votePoints = Integer.parseInt(token2);
                            } else if (token.equals("dayv")) {
                                player.voteKeyPoints = Integer.parseInt(token2);
                            } else if (token.equals("bloodPoints")) {
                                player.bloodPoints = Integer.parseInt(token2);
                            } else if (token.equals("donP")) {
                                player.donatorPoints = Integer.parseInt(token2);
                            } else if (token.equals("voteEntry")) {
                                player.VoteEntries = Integer.parseInt(token2);
                            } else if (token.equals("spins")) {
                                player.FortuneSpins = Integer.parseInt(token2);
                            } else if (token.equals("donA")) {
                                player.amDonated = Integer.parseInt(token2);
                            }  else if (token.equals("donB")) {
                                player.setStoreDonated(Long.parseLong(token2));
                            }  else if (token.equals("cflipRakeback")) {
                                player.CoinFlipRakeBack = Integer.parseInt(token2);
                            }  else if (token.equals("damnedpoints")) {
                                player.damnedPoints = Long.parseLong(token2);
                            }else if (token.equals("nmzpoints")) {
                                player.NMZPoints = Long.parseLong(token2);
                            } else if (token.equals("nmzgoldenboss")) {
                                player.NMZGoldenBoss = Long.parseLong(token2);
                            } else if (token.equals("nmzhealing")) {
                                player.NMZHealing = Long.parseLong(token2);
                            } else if (token.equals("nmzdoubledrop")) {
                                player.NMZDoubleDrop = Long.parseLong(token2);
                            } else if (token.equals("nmzabsorption")) {
                                player.NMZAbsorption = Long.parseLong(token2);
                            } else if (token.equals("bjwin")) {
                                player.BjWins = Integer.parseInt(token2);
                            } else if (token.equals("bjloss")) {
                                player.BjLoss = Integer.parseInt(token2);
                            } else if (token.equals("bjpay")) {
                                player.BjPay = Integer.parseInt(token2);
                            } else if (token.equals("storetrans")) {
                                player.StoreTransfer = Boolean.parseBoolean(token2);
                            } else if (token.equals("tpNomad")) {
                                player.tempNomadCoffer = Long.parseLong(token2);
                            } else if (token.equals("tpPlat")) {
                                player.tempPlatCoffer = Long.parseLong(token2);
                            } else if (token.equals("donW")) {
                                player.setWeeklyDonated(Long.parseLong(token2));
                            } else if (token.equals("babapoints")) {
                                player.BabaPoints = (Long.parseLong(token2));
                            } else if (token.equals("premiumpoints")) {
                                player.PremiumPoints = (Long.parseLong(token2));
                            }  else if (token.equals("dailydmg")) {
                                player.dailyDamage = Long.parseLong(token2);
                            }  else if (token.equals("daily2xraid")) {
                                player.daily2xRaidLoot = Long.parseLong(token2);
                            }  else if (token.equals("daily2x")) {
                                player.daily2xXPGain = Long.parseLong(token2);
                            }  else if (token.equals("dailyddr")) {
                                player.doubleDropRate = Long.parseLong(token2);
                            }  else if (token.equals("dailyAgro")) {
                                player.weeklyInfAgro = Long.parseLong(token2);
                            }  else if (token.equals("dailyPray")) {
                                player.weeklyInfPot = Long.parseLong(token2);
                            }  else if (token.equals("dailyOverload")) {
                                player.weeklyOverload = Long.parseLong(token2);
                            }  else if (token.equals("dailyRage")) {
                                player.weeklyRage = Long.parseLong(token2);
                            }  else if (token.equals("cent")) {
                                player.centurion = Integer.parseInt(token2);
                            }  else if (token.equals("cosC")) {
                                player.cosmeticCredits = Long.parseLong(token2);
                            }  else if (token.equals("eliteboostc")) {
                                player.EliteCentCooldown = (Long.parseLong(token2));
                            } else if (token.equals("royalboostc")) {
                                player.RoyalCentCooldown = (Long.parseLong(token2));
                            } else if (token.equals("arboPoints")) {
                                player.arboPoints = Long.parseLong(token2);
                            } else if (token.equals("shadowPoints")) {
                                player.shadowCrusadePoints = Long.parseLong(token2);
                            } else if (token.equals("shadowCompletions")) {
                                player.shadowCrusadeCompletions = Integer.parseInt(token2);
                            } else if (token.equals("afk")) {
                                player.setAfkPoints(Integer.parseInt(token2));
                            } else if (token.equals("cleptonoti")) {
                                player.CleptNotification = Boolean.parseBoolean(token2);
                            } else if (token.equals("bloodyp")) {
                                player.setBloody_points(Integer.parseInt(token2));
                            } else if (token.equals("seasonal")) {
                                player.setSeasonalPoints(Integer.parseInt(token2));
                            } else if (token.equals("foundry")) {
                                player.foundryPoints = Long.parseLong(token2);
                            }  else if (token.equals("blastcoffer")) {
                                player.getBlastFurnace().getCoffer().depositToCoffer(Integer.parseInt(token2));
                            }  else if (token.equals("discordbooster")) {
                                player.setDiscordboostlastClaimed(Long.parseLong(token2));
                            }  else if (token.equals("discordboosting")) {
                                player.setDiscordlastClaimed(Long.parseLong(token2));
                            }  else if (token.equals("discord")) {
                                player.setDiscordlinked(Boolean.parseBoolean(token2));
                            }  else if (token.equals("discordpoints")) {
                                player.setDiscordPoints(Integer.parseInt(token2));
                            }  else if (token.equals("discordtag")) {
                                player.setDiscordTag(token2);
                            }  else if (token.equals("discorduser")) {
                                player.setDiscordUser(Long.parseLong(token2));
                            } else if (token.equals("donobosskc")) {
                                player.setDonorBossKC(Integer.parseInt(token2));
                            } else if (token.equals("donobosstime")) {
                                player.setDonorBossDate(LocalDate.parse(token2));
                            } else if (token.equals("donobosskcx")) {
                                player.setDonorBossKCx(Integer.parseInt(token2));
                            } else if (token.equals("donobosstimex")) {
                                player.setDonorBossDatex(LocalDate.parse(token2));
                            } else if (token.equals("donobosskcy")) {
                                player.setDonorBossKCy(Integer.parseInt(token2));
                            } else if (token.equals("donobosstimey")) {
                                player.setDonorBossDatey(LocalDate.parse(token2));
                            } else if (token.equals("donobosskcz")) {
                                player.setDonorBossKCz(Integer.parseInt(token2));
                            } else if (token.equals("donobosstimez")) {
                                player.setDonorBossDatez(LocalDate.parse(token2));
                            } else if (token.equals("donobosskcw")) {
                                player.setDonorBossKCw(Integer.parseInt(token2));
                            } else if (token.equals("donobosstimew")) {
                                player.setDonorBossDatew(LocalDate.parse(token2));
                            } else if (token.equals("prestige-points")) {
                                player.prestigePoints = Integer.parseInt(token2);
                            }  else if (token.equals("division-tier")) {
                                player.setTier(Integer.parseInt(token2));
                            }  else if (token.equals("division-xp")) {
                                player.setXp(Integer.parseInt(token2));
                            }  else if (token.equals("division-member")) {
                                player.setMember(Boolean.parseBoolean(token2));
                            }  else if (token.equals("division-season")) {
                                player.setCurrentSeason(Integer.parseInt(token2));
                            } else if (token.equals("xpLock")) {
                                player.expLock = Boolean.parseBoolean(token2);
                            } else if (line.startsWith("KC")) {
                                player.killcount = Integer.parseInt(token2);
                            } else if (line.startsWith("DC")) {
                                player.deathcount = Integer.parseInt(token2);
                            } else if (token.equals("pc-points")) {
                                player.pcPoints = Integer.parseInt(token2);
                            } else if (token.equals("aoe-points")) {
                                player.instanceCurrency = Long.parseLong(token2);
                            } else if (token.equals("total-raids")) {
                                player.totalRaidsFinished = Integer.parseInt(token2);
                            } else if (token.equals("total-rogue-kills")) {
                                player.getBH().setTotalRogueKills(Integer.parseInt(token2));
                            } else if (token.equals("total-hunter-kills")) {
                                player.getBH().setTotalHunterKills(Integer.parseInt(token2));
                            } else if (token.equals("target-time-delay")) {
                                player.getBH().setDelayedTargetTicks(Integer.parseInt(token2));
                            } else if (token.equals("bh-penalties")) {
                                player.getBH().setWarnings(Integer.parseInt(token2));
                            } else if (token.equals("bh-bounties")) {
                                player.getBH().setBounties(Integer.parseInt(token2));
                            } else if (token.equals("statistics-visible")) {
                                player.getBH().setStatisticsVisible(Boolean.parseBoolean(token2));
                            } else if (token.equals("spell-accessible")) {
                                player.getBH().setSpellAccessible(Boolean.parseBoolean(token2));
                            } else if (token.equals("killStreak")) {
                                player.killStreak = Integer.parseInt(token2);
                            } else if (token.equals("achievement-points")) {
                                player.getAchievements().setPoints(Integer.parseInt(token2));
                            } else if (token.equals("d1Complete")) { //Varrock claimed
                                player.d1Complete = Boolean.parseBoolean(token2);
                            } else if (token.equals("d2Complete")) {
                                player.d2Complete = Boolean.parseBoolean(token2);
                            } else if (token.equals("d3Complete")) {
                                player.d3Complete = Boolean.parseBoolean(token2);
                            } else if (token.equals("d4Complete")) {
                                player.d4Complete = Boolean.parseBoolean(token2);
                            } else if (token.equals("d5Complete")) {
                                player.d5Complete = Boolean.parseBoolean(token2);
                            } else if (token.equals("d6Complete")) {
                                player.d6Complete = Boolean.parseBoolean(token2);
                            } else if (token.equals("d7Complete")) {
                                player.d7Complete = Boolean.parseBoolean(token2);
                            } else if (token.equals("d8Complete")) {
                                player.d8Complete = Boolean.parseBoolean(token2);
                            } else if (token.equals("d9Complete")) {
                                player.d9Complete = Boolean.parseBoolean(token2);
                            } else if (token.equals("d10Complete")) {
                                player.d10Complete = Boolean.parseBoolean(token2);
                            } else if (token.equals("d11Complete")) {
                                player.d11Complete = Boolean.parseBoolean(token2);
                            } else if (token.equals("VarrockClaimedDiaries")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> player.getDiaryManager().getVarrockDiary().claim(diff));
                                }
                            } else if (token.equals("ArdougneClaimedDiaries")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> player.getDiaryManager().getArdougneDiary().claim(diff));
                                }
                            } else if (token.equals("DesertClaimedDiaries")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> player.getDiaryManager().getDesertDiary().claim(diff));
                                }
                            } else if (token.equals("FaladorClaimedDiaries")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> player.getDiaryManager().getFaladorDiary().claim(diff));
                                }
                            } else if (token.equals("FremennikClaimedDiaries")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> player.getDiaryManager().getFremennikDiary().claim(diff));
                                }
                            } else if (token.equals("KandarinClaimedDiaries")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> player.getDiaryManager().getKandarinDiary().claim(diff));
                                }
                            } else if (token.equals("KaramjaClaimedDiaries")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> player.getDiaryManager().getKaramjaDiary().claim(diff));
                                }
                            } else if (token.equals("LumbridgeClaimedDiaries")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> player.getDiaryManager().getLumbridgeDraynorDiary().claim(diff));
                                }
                            } else if (token.equals("MorytaniaClaimedDiaries")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> player.getDiaryManager().getMorytaniaDiary().claim(diff));
                                }
                            } else if (token.equals("WesternClaimedDiaries")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> player.getDiaryManager().getWesternDiary().claim(diff));
                                }
                            } else if (token.equals("WildernessClaimedDiaries")) {
                                String[] claimedRaw = token2.split(",");
                                for (String claim : claimedRaw) {
                                    DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> player.getDiaryManager().getWildernessDiary().claim(diff));
                                }
                            } else if (token.equals("diaries")) {
                                try {
                                    String raw = token2;
                                    String[] components = raw.split(",");
                                    for (String comp : components) {
                                        if (comp.isEmpty()) {
                                            continue;
                                        }
                                        // Varrock
                                        Optional<VarrockDiaryEntry> varrock = VarrockDiaryEntry.fromName(comp);
                                        if (varrock.isPresent()) {
                                            player.getDiaryManager().getVarrockDiary().nonNotifyComplete(varrock.get());
                                        }
                                        // Ardougne
                                        Optional<ArdougneDiaryEntry> ardougne = ArdougneDiaryEntry.fromName(comp);
                                        if (ardougne.isPresent()) {
                                            player.getDiaryManager().getArdougneDiary().nonNotifyComplete(ardougne.get());
                                        }
                                        // Desert
                                        Optional<DesertDiaryEntry> desert = DesertDiaryEntry.fromName(comp);
                                        if (desert.isPresent()) {
                                            player.getDiaryManager().getDesertDiary().nonNotifyComplete(desert.get());
                                        }
                                        // Falador
                                        Optional<FaladorDiaryEntry> falador = FaladorDiaryEntry.fromName(comp);
                                        if (falador.isPresent()) {
                                            player.getDiaryManager().getFaladorDiary().nonNotifyComplete(falador.get());
                                        }
                                        // Fremennik
                                        Optional<FremennikDiaryEntry> fremennik = FremennikDiaryEntry.fromName(comp);
                                        if (fremennik.isPresent()) {
                                            player.getDiaryManager().getFremennikDiary().nonNotifyComplete(fremennik.get());
                                        }
                                        // Kandarin
                                        Optional<KandarinDiaryEntry> kandarin = KandarinDiaryEntry.fromName(comp);
                                        if (kandarin.isPresent()) {
                                            player.getDiaryManager().getKandarinDiary().nonNotifyComplete(kandarin.get());
                                        }
                                        // Karamja
                                        Optional<KaramjaDiaryEntry> karamja = KaramjaDiaryEntry.fromName(comp);
                                        if (karamja.isPresent()) {
                                            player.getDiaryManager().getKaramjaDiary().nonNotifyComplete(karamja.get());
                                        }
                                        // Lumbridge
                                        Optional<LumbridgeDraynorDiaryEntry> lumbridge = LumbridgeDraynorDiaryEntry.fromName(comp);
                                        if (lumbridge.isPresent()) {
                                            player.getDiaryManager().getLumbridgeDraynorDiary().nonNotifyComplete(lumbridge.get());
                                        }
                                        // Morytania
                                        Optional<MorytaniaDiaryEntry> morytania = MorytaniaDiaryEntry.fromName(comp);
                                        if (morytania.isPresent()) {
                                            player.getDiaryManager().getMorytaniaDiary().nonNotifyComplete(morytania.get());
                                        }
                                        // Western
                                        Optional<WesternDiaryEntry> western = WesternDiaryEntry.fromName(comp);
                                        if (western.isPresent()) {
                                            player.getDiaryManager().getWesternDiary().nonNotifyComplete(western.get());
                                        }
                                        // Wilderness
                                        Optional<WildernessDiaryEntry> wilderness = WildernessDiaryEntry.fromName(comp);
                                        if (wilderness.isPresent()) {
                                            player.getDiaryManager().getWildernessDiary().nonNotifyComplete(wilderness.get());
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.error("Error while loading {}", playerName, e);
                                    e.printStackTrace();
                                }
                            } else if (token.equals("partialDiaries")) {
                                String raw = token2;
                                String[] components = raw.split(",");
                                try {
                                    for (String comp : components) {
                                        if (comp.isEmpty()) {
                                            continue;
                                        }
                                        String[] part = comp.split(":");
                                        int stage = Integer.parseInt(part[1]);
                                        //Varrock
                                        Optional<VarrockDiaryEntry> varrock = VarrockDiaryEntry.fromName(part[0]);
                                        if (varrock.isPresent()) {
                                            player.getDiaryManager().getVarrockDiary().setAchievementStage(varrock.get(), stage, false);
                                        }
                                        //Ardougne
                                        Optional<ArdougneDiaryEntry> ardougne = ArdougneDiaryEntry.fromName(part[0]);
                                        if (ardougne.isPresent()) {
                                            player.getDiaryManager().getArdougneDiary().setAchievementStage(ardougne.get(), stage, false);
                                        }
                                        //Desert
                                        Optional<DesertDiaryEntry> desert = DesertDiaryEntry.fromName(part[0]);
                                        if (desert.isPresent()) {
                                            player.getDiaryManager().getDesertDiary().setAchievementStage(desert.get(), stage, false);
                                        }
                                        //Falador
                                        Optional<FaladorDiaryEntry> falador = FaladorDiaryEntry.fromName(part[0]);
                                        if (falador.isPresent()) {
                                            player.getDiaryManager().getFaladorDiary().setAchievementStage(falador.get(), stage, false);
                                        }
                                        //Fremennik
                                        Optional<FremennikDiaryEntry> fremennik = FremennikDiaryEntry.fromName(part[0]);
                                        if (fremennik.isPresent()) {
                                            player.getDiaryManager().getFremennikDiary().setAchievementStage(fremennik.get(), stage, false);
                                        }
                                        //Kandarin
                                        Optional<KandarinDiaryEntry> kandarin = KandarinDiaryEntry.fromName(part[0]);
                                        if (kandarin.isPresent()) {
                                            player.getDiaryManager().getKandarinDiary().setAchievementStage(kandarin.get(), stage, false);
                                        }
                                        //Karamja
                                        Optional<KaramjaDiaryEntry> karamja = KaramjaDiaryEntry.fromName(part[0]);
                                        if (karamja.isPresent()) {
                                            player.getDiaryManager().getKaramjaDiary().setAchievementStage(karamja.get(), stage, false);
                                        }
                                        //Lumbridge
                                        Optional<LumbridgeDraynorDiaryEntry> lumbridge = LumbridgeDraynorDiaryEntry.fromName(part[0]);
                                        if (lumbridge.isPresent()) {
                                            player.getDiaryManager().getLumbridgeDraynorDiary().setAchievementStage(lumbridge.get(), stage, false);
                                        }
                                        //Morytania
                                        Optional<MorytaniaDiaryEntry> morytania = MorytaniaDiaryEntry.fromName(part[0]);
                                        if (morytania.isPresent()) {
                                            player.getDiaryManager().getMorytaniaDiary().setAchievementStage(morytania.get(), stage, false);
                                        }
                                        //Western
                                        Optional<WesternDiaryEntry> western = WesternDiaryEntry.fromName(part[0]);
                                        if (western.isPresent()) {
                                            player.getDiaryManager().getWesternDiary().setAchievementStage(western.get(), stage, false);
                                        }
                                        //Wilderness
                                        Optional<WildernessDiaryEntry> wilderness = WildernessDiaryEntry.fromName(part[0]);
                                        if (wilderness.isPresent()) {
                                            player.getDiaryManager().getWildernessDiary().setAchievementStage(wilderness.get(), stage, false);
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.error("Error while loading {}", playerName, e);
                                    e.printStackTrace();
                                }
                            } else if (token.equals("bonus-end")) {
                                player.bonusXpTime = Long.parseLong(token2);
                            } else if (token.equals("safety-end")) {
                                player.SafetyTimer = Long.parseLong(token2);
                            } else if (token.equals("island-end")) {
                                player.IslandTimer = Long.parseLong(token2);
                            } else if (token.equals("jail-end")) {
                                player.jailEnd = Long.parseLong(token2);
                            } else if (token.equals("mute-end")) {
                                player.muteEnd = Long.parseLong(token2);
                            } else if (token.equals("last-yell")) {
                                player.lastYell = Long.parseLong(token2);
                            } else if (token.equals("splitChat")) {
                                player.splitChat = Boolean.parseBoolean(token2);
                            } else if (token.equals("lastVote")) {
                                player.setLastVote(LocalDate.ofEpochDay(Long.parseLong(token2)));
                            } else if (token.equals("lastVotePanelPoint")) {
                                player.setLastVotePanelPoint(LocalDate.ofEpochDay(Long.parseLong(token2)));
                            } else if (token.equals("slayer-task")) {
                                Optional<Task> task = SlayerMaster.get(token2);
                                player.getSlayer().setTask(task);
                            } else if (token.equals("konar-slayer-location")) {
                                player.setKonarSlayerLocation(token2);
                            } else if (token.equals("last-task")) {
                                player.setLastTask(token2);
                            } else if (token.equals("slayerPartner")) {
                                player.slayerPartner = token2;
                            } else if (token.equals("slayerParty")) {
                                player.slayerParty = Boolean.parseBoolean(token2);
                            } else if (token.equals("run-toggled")) {
                                player.setRunningToggled(Boolean.parseBoolean(token2));
                            } else if (token.equals("slayer-master")) {
                                player.getSlayer().setMaster(Integer.parseInt(token2));
                            } else if (token.equals("konar-slayer-location")) {
                                player.setKonarSlayerLocation(token2);
                            } else if (token.equals("slayerPoints")) {
                                player.getSlayer().setPoints(Integer.parseInt(token2));
                            } else if (token.equals("slayer-task-amount")) {
                                player.getSlayer().setTaskAmount(Integer.parseInt(token2));
                            } else if (token.equals("consecutive-tasks")) {
                                player.getSlayer().setConsecutiveTasks(Integer.parseInt(token2));
                            } else if (token.equals("mage-arena-points")) {
                                player.setArenaPoints(Integer.parseInt(token2));
                            } else if (token.equals("shayzien-assault-points")) {
                                player.setShayPoints(Integer.parseInt(token2));
                            } else if (token.equals("flagged")) {
                                player.accountFlagged = Boolean.parseBoolean(token2);
                            } else if (token.equals("keepTitle")) {
                                player.keepTitle = Boolean.parseBoolean(token2);
                            } else if (token.equals("killTitle")) {
                                player.killTitle = Boolean.parseBoolean(token2);
                            } else if (token.equals("character-historyItems")) {
                                //System.err.println("Loading - Length of list="+token3.length+" saleSize="+player.historyItems.length);
                                for (int j = 0; j < token3.length; j++) {
                                    player.historyItems[j] = Integer.parseInt(token3[j]);
                                    player.saleItems.add(Integer.parseInt(token3[j]));
                                }
                            } else if (token.equals("character-historyItemsN")) {
                                for (int j = 0; j < token3.length; j++) {
                                    player.historyItemsN[j] = Integer.parseInt(token3[j]);
                                    player.saleAmount.add(Integer.parseInt(token3[j]));
                                }
                            } else if (token.equals("character-historyPrice")) {
                                for (int j = 0; j < token3.length; j++) {
                                    player.historyPrice[j] = Integer.parseInt(token3[j]);
                                    player.salePrice.add(Integer.parseInt(token3[j]));
                                }
                            } else if (token.equals(EventCalendar.SAVE_KEY)) {
                                if (token3.length >= 2) {
                                    for (int index = 0; index < token3.length; index += 2) {
                                        EventChallengeKey key = EventChallengeKey.fromString(token3[index]);
                                        if (key != null) {
                                            int value = Integer.parseInt(token3[index + 1]);
                                            player.getEventCalendar().set(key, value);
                                        }
                                    }
                                }
                            } else if (token.equals("removed-slayer-tasks")) {
                                String[] backing = Misc.nullToEmpty(player.getSlayer().getRemoved().length);
                                int index = 0;
                                for (; index < token3.length; index++) {
                                    backing[index] = token3[index];
                                }
                                player.getSlayer().setRemoved(backing);
                            } else if (token.equals("slayer-unlocks")) {
                                for (int index = 0; index < token3.length; index++) {
                                    try {
                                        SlayerUnlock unlock = SlayerUnlock.valueOf(token3[index]);
                                        if (unlock != null && !player.getSlayer().getUnlocks().contains(unlock)) {
                                            player.getSlayer().getUnlocks().add(unlock);
                                        }
                                    } catch (Exception e) {
                                        logger.error("Error while loading {}", playerName, e);
                                        e.printStackTrace();
                                    }
                                }
                            } else if (token.equals("extended-slayer-tasks")) {
                                for (int index = 0; index < token3.length; index++) {
                                    try {
                                        TaskExtension extension = TaskExtension.valueOf(token3[index]);
                                        if (extension != null && !player.getSlayer().getExtensions().contains(extension)) {
                                            player.getSlayer().getExtensions().add(extension);
                                        }
                                    } catch (Exception e) {
                                        logger.error("Error while loading {}", playerName, e);
                                        e.printStackTrace();
                                    }
                                }
                            } else if (token.startsWith("removedTask")) {
                                int value = Integer.parseInt(token2);
                                if (value > -1) {
                                    player.getSlayer().setPoints(player.getSlayer().getPoints() + 100);
                                }
                            } else if (token.equals("wave")) {
                                player.waveId = Integer.parseInt(token2);
                            } else if (token.equals("void")) {
                                for (int j = 0; j < token3.length; j++) {
                                    player.voidStatus[j] = Integer.parseInt(token3[j]);
                                }
                            } else if (token.equals("pouch-rune")) {
                                for (int j = 0; j < token3.length; j++) {
                                    player.setRuneEssencePouch(j, Integer.parseInt(token3[j]));
                                }
                            } else if (token.equals("pouch-pure")) {
                                for (int j = 0; j < token3.length; j++) {
                                    player.setPureEssencePouch(j, Integer.parseInt(token3[j]));
                                }
                            } else if (token.equals("looting_bag_deposit_mode")) {
                                try {
                                    LootingBag.LootingBagUseAction useAction = LootingBag.LootingBagUseAction.valueOf(token3[0]);
                                    if (useAction != null) {
                                        player.getLootingBag().setUseAction(useAction);
                                    }
                                } catch (Exception e) {
                                    logger.error("Error while loading {}", playerName, e);
                                    e.printStackTrace();
                                }
                            } else if (token.equals("privatechat")) {
                                player.setPrivateChat(Integer.parseInt(token2));
                            } else if (token.equals("inDistrict")) {
                                player.pkDistrict = Boolean.parseBoolean(token2);
                            } else if (token.equals("hideDonor")) {
                                player.hideDonor = Boolean.parseBoolean(token2);
                            } else if (token.equals("safeBoxSlots")) {
                                player.safeBoxSlots = Integer.parseInt(token2);
                            } else if (token.equals("district-levels")) {
                                for (int i = 0; i < player.playerStats.length; i++) player.playerStats[i] = Integer.parseInt(token3[i]);
                            } else if (token.equals("crawsbowCharge")) {
                                player.getPvpWeapons().setCrawsBowCharges(Integer.parseInt(token2));
                            } else if (token.equals("thammaronCharge")) {
                                player.getPvpWeapons().setThammaronSceptreCharges(Integer.parseInt(token2));
                            } else if (token.equals("viggoraCharge")) {
                                player.getPvpWeapons().setViggoraChainmaceCharges(Integer.parseInt(token2));
                            } else if (token.equals("tbowCharge")) {
                                player.getChristmasWeapons().setBowCharges(Integer.parseInt(token2));
                            } else if (token.equals("scytheCharge")) {
                                player.getChristmasWeapons().setScytheCharges(Integer.parseInt(token2));
                            } else if (token.equals("whipCharge")) {
                                player.getChristmasWeapons().setWhipCharges(Integer.parseInt(token2));
                            } else if (token.equals("presentCounter")) {
                                player.setPresentCounter(Integer.parseInt(token2));
                            }

                        case 3:
                            if (token.equals("character-equip")) {
                                player.playerEquipment[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                                player.playerEquipmentN[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
                            }
                            break;
                        case 67:
                            if (token.equals("character-cosmetic")) {
                                player.playerEquipmentCosmetic[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                            }
                            break;
                        case 68:
                            if (token.equals("character-cosmetic-boolean")) {
                                player.cosmeticOverrides[Integer.parseInt(token3[0])] = Boolean.parseBoolean(token3[1]);
                            }
                            break;
                        case 4:
                            if (token.equals("character-look")) {
                                player.playerAppearance[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                            }
                            break;
                        case 5:
                            if (token.equals("character-skill")) {
                                player.playerLevel[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                                player.playerXP[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
                                if (token3.length > 3) {
                                    player.skillLock[Integer.parseInt(token3[0])] = Boolean.parseBoolean(token3[3]);
                                    player.prestigeLevel[Integer.parseInt(token3[0])] = Integer.parseInt(token3[4]);
                                }
                            }
                            break;
                        case 6:
                            if (token.equals("character-item")) {
                                player.playerItems[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                                player.playerItemsN[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
                            }
                            break;
                        case 61:
                            if (token.equals("perk-item")) {
                                player.getPerkSytem().gameItems.add(new GameItem(Integer.parseInt(token3[1]), Integer.parseInt(token3[2])));
                            }
                            break;
                        case 63:
                            if (token.equals("disolve-item")) {
                                player.getRecentlyDissolvedItems().add(Integer.parseInt(token3[1]));
                                player.getRecentlyDissolvedPrices().add(Long.parseLong(token3[2]));
                            }
                            break;
                        case 64:
                            if (token.equals("prestige-perk")) {
                                PrestigePerks.handleLoading(player, PrestigePerks.values()[Integer.parseInt(token3[2])]);
                            }
                            break;
                        case 65:
                            if (token.equals("scrollboost")) {
                                for (BoostScrolls value : BoostScrolls.values()) {
                                    if (value.name().equalsIgnoreCase(token3[0])) {
                                        player.boostTimers.put(value, Long.parseLong(token3[1]));
                                    }
                                }
                            }
                            break;
                        case 66:
                            if (token.equals("gift-item")) {
                                for (Christmas.Gifts value : Christmas.Gifts.values()) {
                                    if (value.name().equalsIgnoreCase(token3[1])) {
                                        player.christmasGifts.add(value);
                                    }
                                }
                            }
                            break;
                        case 46:
                            if (token.equals("bag-item")) {
                                int id = Integer.parseInt(token3[1]);
                                int amt = Integer.parseInt(token3[2]);
                                player.getLootingBag().getLootingBagContainer().items.add(new LootingBagItem(id, amt));
                            }
                            break;
                        case 52:
                            if (token.equals("item")) {
                                int itemId = Integer.parseInt(token3[0]);
                                int value = Integer.parseInt(token3[1]);
                                String date = token3[2];
                                player.getRechargeItems().loadItem(itemId, value, date);
                            }
                            break;
                        case 55:
                            if (token.equals("pouch-item")) {
                                int id = Integer.parseInt(token3[1]);
                                int amt = Integer.parseInt(token3[2]);
                                player.getRunePouch().getItems().add(new GameItem(id, amt));
                            }
                            break;
                        case 56:
                            if (token.equals("sack-item")) {
                                int id = Integer.parseInt(token3[1]);
                                int amt = Integer.parseInt(token3[2]);
                                player.getHerbSack().getItems().add(new GameItem(id, amt));
                            }
                            break;
                        case 57:
                            if (token.equals("bag-item")) {
                                int id = Integer.parseInt(token3[1]);
                                int amt = Integer.parseInt(token3[2]);
                                player.getGemBag().getItems().add(new GameItem(id, amt));
                            }
                            break;
                        case 60:
                            if (token.equals("death-storage")) {
                                int id = Integer.parseInt(token3[1]);
                                int amt = Integer.parseInt(token3[2]);
                                player.getDeathStorage().add(new GameItem(id, amt));
                            }
                            break;
                        case 62:
                            if (token.equals("collect-log")) {
                                int id = Integer.parseInt(token3[1]);
                                player.getClaimedLog().add(id);
                            }
                            break;
                        case 69:
                            if (token.equals("trading-post")) {
                                int id = Integer.parseInt(token3[0]);
                                int amt = Integer.parseInt(token3[1]);
                                long pricePerItem = Long.parseLong(token3[2]);
                                boolean nomad = Boolean.parseBoolean(token3[3]);
                                int totalSold = Integer.parseInt(token3[4]);
                                long timestamp = Long.parseLong(token3[5]);

                                player.tempTradeOffers.add(new TradePostOffer(player.getDisplayName(),
                                        new GameItem(id, amt),
                                        pricePerItem,
                                        timestamp,
                                        nomad,
                                        totalSold));
                            }
                            break;
                        case 70:
                            if (token.equals("petCost")) {
                                int index = Integer.parseInt(token3[0]);
                                long[] values = new long[5];

                                for (int i = 0; i < 5; i++) {
                                    values[i] = Long.parseLong(token3[i + 1]);
                                }

                                player.petPerkCost.put(index, values);
                            }
                            break;
                        case  71:
                            if (token.equals("rake-back")) {
                                player.getRakeBackSystem().put(Integer.parseInt(token3[0]), Integer.parseInt(token3[1]));
                            }
                            break;

                        case 73:
                            if (token != null && token.length() > 0) {
                                player.PollBothObjects.add(new Position(Integer.parseInt(token), Integer.parseInt(token2)));
                            }
                            break;

                        case 74:
                            if (token2 != null && token2.length() > 0) {
                                player.NMZBosses.add(Integer.parseInt(token2));
                            }
                            break;

                        case 7:
                            if (token.equals("bank-tab")) {
                                int tabId = Integer.parseInt(token3[0]);
                                int itemId = Integer.parseInt(token3[1]);
                                int itemAmount = Integer.parseInt(token3[2]);
                                player.getBank().getBankTab()[tabId].add(new BankItem(itemId, itemAmount));
                            }
                            break;
                        case 8: // Legacy
                            if (token.equals("character-friend")) {
                                try {
                                    String name = Misc.convertLongToFixedName(Long.parseLong(token3[0]));
                                    friends.add(new FriendsListEntry(FriendType.FRIEND, name, ""));
                                } catch (NumberFormatException e) {
                                    logger.error("Error adding friend {} on {} friends list.", token3[0], player.getLoginName());
                                }
                            }
                            break;
                        case 12: // Legacy
                            if (token.equals("character-ignore")) {
                                try {
                                    String name = Misc.convertLongToFixedName(Long.parseLong(token3[0]));
                                    friends.add(new FriendsListEntry(FriendType.IGNORE, name, ""));
                                } catch (NumberFormatException e) {
                                    logger.error("Error adding ignore {} on {} ignore list.", token3[0], player.getLoginName());
                                }
                            }
                            break;

                        // Achievements
                        case 9:
                        case 10:
                        case 11:
                        case 19:
                        case 20:
                            if (token3.length < 2) continue; // Legacy condition
                            AchievementTier tier = ReadMode == 9 ? AchievementTier.TIER_1
                                    : ReadMode == 10 ? AchievementTier.TIER_2
                                    : ReadMode == 11 ? AchievementTier.TIER_3
                                    : ReadMode == 19 ? AchievementTier.TIER_4
                                    : ReadMode == 20 ? AchievementTier.STARTER
                                    : null;
                            if (tier == null)
                                throw new IllegalStateException("Unsupported achievement read mode: " + ReadMode);
                            player.getAchievements().readFromSave(token, token3, tier);
                            break;
                        case 14:
                            if (token.equals("item")) {
                                player.degradableItem[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                            } else if (token.equals("claim-state")) {
                                for (int i = 0; i < token3.length; i++) {
                                    player.claimDegradableItem[i] = Boolean.parseBoolean(token3[i]);
                                }
                            }
                            break;
                        case 16:
                            try {
                                Killstreak.Type type = Killstreak.Type.get(token);
                                int value = Integer.parseInt(token2);
                                player.getKillstreak().getKillstreaks().put(type, value);
                            } catch (NullPointerException | NumberFormatException e) {
                                logger.error("Error while loading {}", playerName, e);
                                e.printStackTrace();
                            }
                            break;
                        case 17:
                            try {
                                if (token2 != null && token2.length() > 0) {
                                    Title title = Title.valueOf(token2);
                                    if (title != null) {
                                        player.getTitles().getPurchasedList().add(title);
                                    }
                                }
                            } catch (Exception e) {
                                logger.error("Error while loading {}", playerName, e);
                                e.printStackTrace();
                            }
                            break;
                        case 18:
                            if (token != null && token.length() > 0) {
                                player.getNpcDeathTracker().getTracker().put(token, Integer.parseInt(token2));
                            }
                            break;
                    }
                } else {
                    if (line.equals("[ACCOUNT]")) {
                        ReadMode = 1;
                    } else if (line.equals("[CHARACTER]")) {
                        ReadMode = 2;
                    } else if (line.equals("[EQUIPMENT]")) {
                        ReadMode = 3;
                    } else if (line.equals("[COSMETICS]")) {
                        ReadMode = 67;
                    } else if (line.equals("[COSMETICS-BOOLEANS]")) {
                        ReadMode = 68;
                    } else if (line.equals("[TRADINGPOST]")) {
                        ReadMode = 69;
                    } else if (line.equals("[LOOK]")) {
                        ReadMode = 4;
                    } else if (line.equals("[SKILLS]")) {
                        ReadMode = 5;
                    } else if (line.equals("[ITEMS]")) {
                        ReadMode = 6;
                    } else if (line.equals("[PERKS]")) {
                        ReadMode = 61;
                    } else if (line.equals("[DISSOLVER]")) {
                        ReadMode = 63;
                    } else if (line.equals("[PRESTIGE]")) {
                        ReadMode = 64;
                    } else if (line.equals("[BOOSTINGSCROLLS]")) {
                        ReadMode = 65;
                    } else if (line.equals("[XMAS]")) {
                        ReadMode = 66;
                    } else if (line.equals("[LOOTBAG]")) {
                        ReadMode = 46;
                    } else if (line.equals("[RECHARGEITEMS]")) {
                        ReadMode = 52;
                    } else if (line.equals("[RUNEPOUCH]")) {
                        ReadMode = 55;
                    } else if (line.equals("[HERBSACK]")) {
                        ReadMode = 56;
                    } else if (line.equals("[GEMBAG]")) {
                        ReadMode = 57;
                    } else if (line.equals("[SAFEBOX]")) {
                        ReadMode = 58;
                    } else if (line.equals("[DEATHSTORAGE]")) {
                        ReadMode = 60;//DEATHSTORAGE
                    } else if (line.equals("[COLLOGCLAIMS]")) {
                        ReadMode = 62;//COLLOGCLAIMS
                    } else if (line.equals("[PETCOSTS]")) {
                        ReadMode = 70;
                    } else if (line.equals("[RAKEBACK]")) {
                        ReadMode = 71;
                    } else if (line.equals("[BANK]")) {
                        ReadMode = 7;
                    } else if (line.equals("[FRIENDS]")) { // Legacy
                        ReadMode = 8;
                    } else if (line.equals("[IGNORES]")) { // Legacy
                        ReadMode = 12;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-1]")) {
                        ReadMode = 9;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-2]")) {
                        ReadMode = 10;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-3]")) {
                        ReadMode = 11;
                    } else if (line.equals("[HOLIDAY-EVENTS]")) {
                        ReadMode = 13;
                    } else if (line.equals("[DEGRADEABLES]")) {
                        ReadMode = 14;
                    } else if (line.equals("[PRESETS]")) {
                        ReadMode = 15;
                    } else if (line.equals("[KILLSTREAKS]")) {
                        ReadMode = 16;
                    } else if (line.equals("[TITLES]")) {
                        ReadMode = 17;
                    } else if (line.equals("[NPC-TRACKER]")) {
                        ReadMode = 18;
                    } else if (line.equals("[POLL-BOOTHS]")) {
                        ReadMode = 73;
                    } else if (line.equals("[NMZ-BOSSES]")) {
                        ReadMode = 74;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-4]")) {
                        ReadMode = 19;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-5]")) {
                        ReadMode = 20;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-5]")) {
                        ReadMode = 59;
                    } else if (line.equals("[EOF]")) {
                        try {
                            characterfile.close();
                        } catch (IOException ioexception) {
                            logger.error("Error while loading {}", playerName, ioexception);
                            ioexception.printStackTrace();
                        }

                        player.getFriendsList().addFromSave(friends);
                        return LoadGameResult.SUCCESS;
                    }
                }
                line = characterfile.readLine();
            } catch (Exception e) {
                logger.error("Error while loading {} on line {}", playerName, line, e);
                e.printStackTrace();
                return LoadGameResult.ERROR_OCCURRED;
            }
        }

        try {
            characterfile.close();
        } catch (IOException ioexception) {
            logger.error("Error while loading {}", playerName, ioexception);
            ioexception.printStackTrace();
        }

        logger.error("Reached end of load method without reaching EOF, player logging in while save is executing or save file wiped, user={}", player);
        return LoadGameResult.ERROR_OCCURRED;
    }
}
