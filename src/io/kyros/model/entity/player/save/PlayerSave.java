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
import io.kyros.content.pet.PetUtility;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.content.privatemessaging.FriendType;
import io.kyros.content.privatemessaging.FriendsListEntry;
import io.kyros.content.seasons.Christmas;
import io.kyros.content.skills.slayer.SlayerMaster;
import io.kyros.content.skills.slayer.SlayerUnlock;
import io.kyros.content.skills.slayer.Task;
import io.kyros.content.skills.slayer.TaskExtension;
import io.kyros.content.sms.SmsManager;
import io.kyros.content.teleportv2.inter.TeleportInterface;
import io.kyros.content.titles.Title;
import io.kyros.content.tradingpost.TradePostOffer;
import io.kyros.model.controller.ControllerRepository;
import io.kyros.model.entity.HealthBar;
import io.kyros.model.entity.player.*;
import io.kyros.model.entity.player.mode.ExpMode;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.entity.player.mode.group.ExpModeType;
import io.kyros.model.entity.player.mode.group.GroupIronmanRepository;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.bank.BankItem;
import io.kyros.model.items.bank.BankTab;
import io.kyros.util.Misc;
import io.kyros.util.PasswordHashing;
import io.kyros.util.Reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;
import java.util.Map.Entry;

import static io.kyros.Server.getPlayers;
import static io.kyros.model.entity.player.save.PlayerLoad.playerSaveEntryList;

public class PlayerSave {

    private static final Logger logger = LoggerFactory.getLogger(PlayerSave.class);

    public static String getSaveDirectory() {
        return Server.getSaveDirectory() + "/character_saves/";
    }

    public static File[] getAllCharacterSaves() {
        return new File(getSaveDirectory()).listFiles();
    }

    /**
     * Save all online users.
     * Don't call this from the main thread!
     */
    public static void saveAll() {
        long count = Server.getPlayers().nonNullStream().count();
        GroupIronmanRepository.serializeAllInstant();
//        SmsManager.saveDataToDatabase();
        Server.getPlayers().nonNullStream().forEach(plr -> {
            try {
                PlayerSave.saveGameInstant(plr);
            } catch (Exception e) {
                logger.error("Error while saving account during player save backup {}", plr, e);
                e.printStackTrace();
            }
        });
        logger.info("Saved " + count + " online users.");
    }

    /**
     * Tells us whether or not the player exists for the specified name.
     *
     * @param name
     * @return
     */
    public static boolean playerExists(String name) {
        Misc.createDirectory(getSaveDirectory());
        File file = new File(getSaveDirectory() + name + ".txt");
        return file.exists();
    }

    public static boolean saveGame(Player p) {
        new PlayerSaveExecutor(p).request();
        return true;
    }

    public static boolean saveGameInstant(Player p) {
        if (!p.saveCharacter) {
            return false;
        }
        if (p.getLoginName() == null || getPlayers().get(p.getIndex()) == null) {
            return false;
        }
        if (!p.isBot())
            logger.debug("Saving game for {}", p);


        PetUtility.savePet(p);

        Misc.createDirectory(getSaveDirectory());
        int tbTime = (int) (p.teleBlockStartMillis - System.currentTimeMillis() + p.teleBlockLength);
        if (tbTime > 300000 || tbTime < 0) {
            tbTime = 0;
        }
        try {
            p.getFarming().save();
        } catch (Exception e) {
            logger.error("Error while saving {}", p, e);
            e.printStackTrace();
        }
        BufferedWriter characterfile = null;
        try {
            characterfile = new BufferedWriter(new FileWriter(getSaveDirectory() + p.getLoginNameLower() + ".txt"));
            /* ACCOUNT */
            characterfile.write("[ACCOUNT]", 0, 9);
            characterfile.newLine();
            characterfile.write("character-username = ", 0, 21);
            characterfile.write(p.getLoginName(), 0, p.getLoginName().length());
            characterfile.newLine();

            characterfile.write("display-name = " + p.getDisplayName());
            characterfile.newLine();

            characterfile.write("character-password = ", 0, 21);
            String passToWrite = PasswordHashing.hash(p.playerPass);
            characterfile.write(passToWrite, 0, passToWrite.length());
            characterfile.newLine();
            characterfile.newLine();
            /* CHARACTER */
            characterfile.write("[CHARACTER]", 0, 11);
            characterfile.newLine();
            characterfile.write("character-rights = " + p.getRights().getPrimary().getValue());
            characterfile.newLine();
            StringBuilder sb = new StringBuilder();
            p.getRights().getSet().stream().forEach(r -> sb.append(r.getValue() + "\t"));
            characterfile.write("character-rights-secondary = " + sb.substring(0, sb.length() - 1));
            characterfile.newLine();
            characterfile.write("character-mac-address = " + p.getMacAddress());
            characterfile.newLine();
            characterfile.write("character-ip-address = " + p.getIpAddress());
            characterfile.newLine();
            characterfile.write("character-uuid = " + p.getUUID());
            characterfile.newLine();
            characterfile.write("collectorNecklace = " + p.collectNecklace);
            characterfile.newLine();
            characterfile.write("tradeBanned = " + p.tradeBanned);
            characterfile.newLine();
            characterfile.write("skillingMinigame = " + p.skillingMinigame);
            characterfile.newLine();
            characterfile.write("nexUnlocked = " + p.NexUnlocked);
            characterfile.newLine();
            characterfile.write("combatskillingUnlocked = " + p.CombatSkillingUnlocked);
            characterfile.newLine();
            characterfile.write("ragePotion = " + p.usingRage);
            characterfile.newLine();
            characterfile.write("ambitionPotion = " + p.usingAmbition);
            characterfile.newLine();
            characterfile.write("hweenUnlocked = " + p.halloweenGlobal);
            characterfile.newLine();
            characterfile.write("migration-version = " + p.getMigrationVersion());
            characterfile.newLine();

            characterfile.write("revert-option = " + p.getRevertOption());
            characterfile.newLine();
            characterfile.write("dropBoostStart = " + p.dropBoostStart);
            characterfile.newLine();
            if (p.getRevertModeDelay() > 0) {
                characterfile.write("revert-delay = " + p.getRevertModeDelay());
                characterfile.newLine();
            }
            if (p.getMode() != null) {
                characterfile.write("mode = " + p.getMode().getType().name());
                characterfile.newLine();
            }
            if (p.getExpMode() != null) {
                characterfile.write("expmode = " + p.getExpMode().getType().name());
                characterfile.newLine();
            }
            characterfile.write("character-height = ", 0, 19);
            characterfile.write(Integer.toString(p.heightLevel), 0, Integer.toString(p.heightLevel).length());
            characterfile.newLine();
            characterfile.write("character-hp = " + p.getHealth().getCurrentHealth());
            characterfile.newLine();
            characterfile.write("play-time = ", 0, 12);
            characterfile.write(Integer.toString(p.playTime), 0, Integer.toString(p.playTime).length());
            characterfile.newLine();


            characterfile.write("last-clan = ", 0, 12);
            characterfile.write(p.getLastClanChat(), 0, p.getLastClanChat().length());
            characterfile.newLine();

            characterfile.write("require-pin-unlock = " + p.isRequiresPinUnlock());
            characterfile.newLine();

            String teleportFavourites = "teleportfavourites = ";
            characterfile.write(teleportFavourites, 0, teleportFavourites.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (TeleportInterface.Teleport entry : p.getFavoriteTeleports())
                {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry);
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();

            characterfile.write("character-specRestore = ", 0, 24);
            characterfile.write(Integer.toString(p.specRestore), 0, Integer.toString(p.specRestore).length());
            characterfile.newLine();
            characterfile.write("character-posx = ", 0, 17);
            characterfile.write(Integer.toString(p.absX), 0, Integer.toString(p.absX).length());
            characterfile.newLine();
            characterfile.write("character-posy = ", 0, 17);
            characterfile.write(Integer.toString(p.absY), 0, Integer.toString(p.absY).length());
            characterfile.newLine();
            characterfile.write("bank-pin = " + p.getBankPin().getPin());
            characterfile.newLine();
            characterfile.write("bank-pin-cancellation = " + p.getBankPin().isAppendingCancellation());
            characterfile.newLine();
            characterfile.write("bank-pin-unlock-delay = " + p.getBankPin().getUnlockDelay());
            characterfile.newLine();
            characterfile.write("placeholders = " + p.placeHolders);
            characterfile.newLine();
            characterfile.write("bank-pin-cancellation-delay = " + p.getBankPin().getCancellationDelay());
            characterfile.newLine();
            characterfile.write("show-drop-warning = " + p.showDropWarning());
            characterfile.newLine();
            characterfile.write("show-alch-warning = " + p.isAlchWarning());
            characterfile.newLine();
            characterfile.write("hourly-box-toggle = " + p.getHourlyBoxToggle());
            characterfile.newLine();
            characterfile.write("enable-levelup-messages = " + p.enableLevelUpMessage);
            characterfile.newLine();
            characterfile.write("fractured-crystal-toggle = " + p.getFracturedCrystalToggle());
            characterfile.newLine();
            characterfile.write("accept-aid = " + p.acceptAid);
            characterfile.newLine();
            characterfile.write("did-you-know = " + p.didYouKnow);
            characterfile.newLine();
            characterfile.write("spectating-tournament = " + p.spectatingTournament);
            characterfile.newLine();
            characterfile.write("lootvalue = " + p.lootValue);
            characterfile.newLine();
            characterfile.write("infpot = " + p.InfAgroTimer);
            characterfile.newLine();
            characterfile.write("ragepot = " + p.RageTimer);
            characterfile.newLine();
            characterfile.write("ambitionpot = " + p.AmbitionTimer);
            characterfile.newLine();
            characterfile.write("raidPoints = " + p.getRaidPoints());
            characterfile.newLine();
            characterfile.write("raidCount = " + p.raidCount);
            characterfile.newLine();
            characterfile.write("tobCompletions = " + p.tobCompletions);
            characterfile.newLine();
            characterfile.write("arboCompletions = " + p.arboCompletions);
            characterfile.newLine();
            characterfile.write("experience-counter = " + p.getExperienceCounter());
            characterfile.newLine();
            characterfile.write("character-title-updated = " + p.getTitles().getCurrentTitle());
            characterfile.newLine();

            characterfile.write("receivedVoteStreakRefund = " + p.isReceivedVoteStreakRefund());
            characterfile.newLine();

            // EventCalendar
            Set<Entry<EventChallengeKey, Integer>> eventCalendarProgress = p.getEventCalendar().getEntries();
            characterfile.write(EventCalendar.SAVE_KEY + " = ");
            for (Entry<EventChallengeKey, Integer> entry : eventCalendarProgress) {
                characterfile.write(EventChallengeKey.toSerializedString(entry.getKey()));
                characterfile.write("\t");
                characterfile.write(String.valueOf(entry.getValue()));
                characterfile.write("\t");
            }


            characterfile.newLine();
            String[] removed = p.getSlayer().getRemoved();
            characterfile.write("removed-slayer-tasks = ");
            for (int index = 0; index < removed.length; index++) {
                characterfile.write(removed[index]);
                if (index < removed.length - 1) {
                    characterfile.write("\t");
                }
            }
            characterfile.newLine();
            List<TaskExtension> extensions = p.getSlayer().getExtensions();
            if (!extensions.isEmpty()) {
                characterfile.write("extended-slayer-tasks = ");
                for (int index = 0; index < extensions.size(); index++) {
                    characterfile.write(extensions.get(index).toString());
                    if (index < extensions.size() - 1) {
                        characterfile.write("\t");
                    }
                }
            }
            characterfile.newLine();
            List<SlayerUnlock> unlocks = p.getSlayer().getUnlocks();
            if (!unlocks.isEmpty()) {
                characterfile.write("slayer-unlocks = ");
                for (int index = 0; index < unlocks.size(); index++) {
                    characterfile.write(unlocks.get(index).toString());
                    if (index < removed.length - 1) {
                        characterfile.write("\t");
                    }
                }
            }
            characterfile.newLine();
            characterfile.write("rfd-round = ", 0, 12);
            characterfile.write(Integer.toString(p.rfdRound), 0, Integer.toString(p.rfdRound).length());
            characterfile.newLine();
            characterfile.newLine();

            for (int i = 0; i < p.historyItems.length; i++) {
                if (p.saleItems.size() > 0)
                    p.historyItems[i] = p.saleItems.get(i).intValue();
            }
            characterfile.write("character-historyItems = ", 0, 25);
            String toWrite = "";
            for (int i1 = 0; i1 < p.historyItems.length; i1++) {
                toWrite += p.historyItems[i1] + "\t";
            }
            characterfile.write(toWrite);
            characterfile.newLine();
            for (int i = 0; i < p.historyItemsN.length; i++) {
                if (p.saleItems.size() > 0) p.historyItemsN[i] = p.saleAmount.get(i).intValue();
            }
            characterfile.write("character-historyItemsN = ", 0, 26);
            String toWrite2 = "";
            for (int i1 = 0; i1 < p.historyItemsN.length; i1++) {
                toWrite2 += p.historyItemsN[i1] + "\t";
            }
            characterfile.write(toWrite2);
            characterfile.newLine();
            for (int i = 0; i < p.historyPrice.length; i++) {
                if (p.salePrice.size() > 0) p.historyPrice[i] = p.salePrice.get(i).intValue();
            }
            characterfile.write("character-historyPrice = ", 0, 25);
            String toWrite3 = "";
            for (int i1 = 0; i1 < p.historyPrice.length; i1++) {
                toWrite3 += p.historyPrice[i1] + "\t";
            }
            characterfile.write(toWrite3);
            characterfile.newLine();
            characterfile.write("lastLoginDate = ", 0, 16);
            characterfile.write(Integer.toString(p.lastLoginDate), 0, Integer.toString(p.lastLoginDate).length());
            characterfile.newLine();
            characterfile.write("has-npc = ", 0, 10);
            characterfile.write(Boolean.toString(p.hasFollower), 0, Boolean.toString(p.hasFollower).length());
            characterfile.newLine();
            characterfile.write("summonId = ", 0, 11);
            characterfile.write(Integer.toString(p.petSummonId), 0, Integer.toString(p.petSummonId).length());
            characterfile.newLine();
            characterfile.write("startPack = " + p.isCompletedTutorial());
            characterfile.newLine();
            characterfile.write("achieveFix = " + p.hasAchieveFix);
            characterfile.newLine();
            characterfile.write("unlockedUltimateChest = ", 0, 24);
            characterfile.write(Boolean.toString(p.unlockedUltimateChest), 0, Boolean.toString(p.unlockedUltimateChest).length());
            characterfile.newLine();
            characterfile.write("augury = ", 0, 9);
            characterfile.write(Boolean.toString(p.augury), 0, Boolean.toString(p.augury).length());
            characterfile.newLine();
            characterfile.write("rigour = ", 0, 9);
            characterfile.write(Boolean.toString(p.rigour), 0, Boolean.toString(p.rigour).length());
            characterfile.newLine();
            characterfile.write("crystalDrop = ", 0, 14);
            characterfile.write(Boolean.toString(p.crystalDrop), 0, Boolean.toString(p.crystalDrop).length());
            characterfile.newLine();
            characterfile.write("spawnedbarrows = ", 0, 17);
            characterfile.write(Boolean.toString(p.spawnedbarrows), 0, Boolean.toString(p.spawnedbarrows).length());
            characterfile.newLine();
            characterfile.write("collectCoins = ", 0, 15);
            characterfile.write(Boolean.toString(p.collectCoins), 0, Boolean.toString(p.collectCoins).length());
            characterfile.newLine();
            characterfile.write("printAttackStats = " + p.isPrintAttackStats());
            characterfile.newLine();
            characterfile.write("printDefenceStats = " + p.isPrintDefenceStats());
            characterfile.newLine();
            characterfile.write("absorption = ", 0, 13);
            characterfile.write(Boolean.toString(p.absorption), 0, Boolean.toString(p.absorption).length());
            characterfile.newLine();
            characterfile.write("announce = ", 0, 11);
            characterfile.write(Boolean.toString(p.announce), 0, Boolean.toString(p.announce).length());
            characterfile.newLine();
            characterfile.write("lootPickUp = ", 0, 13);
            characterfile.write(Boolean.toString(p.lootPickUp), 0, Boolean.toString(p.lootPickUp).length());
            characterfile.newLine();
            characterfile.write("breakVials = ", 0, 13);
            characterfile.write(Boolean.toString(p.breakVials), 0, Boolean.toString(p.breakVials).length());
            characterfile.newLine();
            characterfile.write("barbarian = ", 0, 12);
            characterfile.write(Boolean.toString(p.barbarian), 0, Boolean.toString(p.barbarian).length());
            characterfile.newLine();
            characterfile.write("membershipStartDate = ", 0, 22);
            characterfile.write(Integer.toString(p.startDate), 0, Integer.toString(p.startDate).length());
            characterfile.newLine();
            characterfile.write("XpScroll = ");
            characterfile.write(Boolean.toString(p.xpScroll));
            characterfile.newLine();
            characterfile.write("maxAttack = ");
            characterfile.write(Boolean.toString(p.maxAttack));
            characterfile.newLine();
            characterfile.write("maxStrength = ");
            characterfile.write(Boolean.toString(p.maxStrength));
            characterfile.newLine();
            characterfile.write("maxDefense = ");
            characterfile.write(Boolean.toString(p.maxDefense));
            characterfile.newLine();
            characterfile.write("maxRange = ");
            characterfile.write(Boolean.toString(p.maxRange));
            characterfile.newLine();
            characterfile.write("maxHealth = ");
            characterfile.write(Boolean.toString(p.maxHealth));
            characterfile.newLine();
            characterfile.write("maxMage = ");
            characterfile.write(Boolean.toString(p.maxMage));
            characterfile.newLine();
            characterfile.write("maxPrayer = ");
            characterfile.write(Boolean.toString(p.maxPrayer));
            characterfile.newLine();
            characterfile.write("XpScrollTime = ");
            characterfile.write(Long.toString(p.xpScrollTicks));
            characterfile.newLine();
            characterfile.write("halloweenCandy = ");
            characterfile.write(Long.toString(p.candyTimer));
            characterfile.newLine();
            characterfile.write("BonusDmg = ");
            characterfile.write(Boolean.toString(p.bonusDmg));
            characterfile.newLine();
            characterfile.write("BonusDmgTime = ");
            characterfile.write(Long.toString(p.bonusDmgTicks));
            characterfile.newLine();
            characterfile.write("fasterClueScroll = ");
            characterfile.write(Boolean.toString(p.fasterCluesScroll));
            characterfile.newLine();
            characterfile.write("fasterClueScrollTime = ");
            characterfile.write(Long.toString(p.fasterCluesTicks));
            characterfile.newLine();
            characterfile.write("skillingPetRateScroll = ");
            characterfile.write(Boolean.toString(p.skillingPetRateScroll));
            characterfile.newLine();
            characterfile.write("skillingPetRateTime = ");
            characterfile.write(Long.toString(p.skillingPetRateTicks));
            characterfile.newLine();
            characterfile.write("activeMageArena2BossId  = ");
            for (int i = 0; i < p.activeMageArena2BossId.length; i++) characterfile.write("" + p.activeMageArena2BossId[i] + ((i == p.activeMageArena2BossId.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("mageArena2SpawnsX  = ");
            for (int i = 0; i < p.mageArena2SpawnsX.length; i++) characterfile.write("" + p.mageArena2SpawnsX[i] + ((i == p.mageArena2SpawnsX.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("mageArena2SpawnsY  = ");
            for (int i = 0; i < p.mageArena2SpawnsY.length; i++) characterfile.write("" + p.mageArena2SpawnsY[i] + ((i == p.mageArena2SpawnsY.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("mageArenaBossKills  = ");
            for (int i = 0; i < p.mageArenaBossKills.length; i++) characterfile.write("" + p.mageArenaBossKills[i] + ((i == p.mageArenaBossKills.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("mageArena2Stages  = ");
            for (int i = 0; i < p.mageArena2Stages.length; i++) characterfile.write("" + p.mageArena2Stages[i] + ((i == p.mageArena2Stages.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("flamesOfZamorakCasts  = ");
            characterfile.write(Integer.toString(p.flamesOfZamorakCasts ));
            characterfile.newLine();
            characterfile.write("clawsOfGuthixCasts  = ");
            characterfile.write(Integer.toString(p.clawsOfGuthixCasts ));
            characterfile.newLine();
            characterfile.write("saradominStrikeCasts  = ");
            characterfile.write(Integer.toString(p.saradominStrikeCasts ));
            characterfile.newLine();
            characterfile.write("exchangeP = ", 0, 12);
            characterfile.write(Integer.toString(p.exchangePoints), 0, Integer.toString(p.exchangePoints).length());
            characterfile.newLine();
            characterfile.write("totalEarnedExchangeP = ");
            characterfile.write(Integer.toString(p.totalEarnedExchangePoints));
            characterfile.newLine();
            characterfile.write("usedFc = ", 0, 9);
            characterfile.write(Boolean.toString(p.usedFc), 0, Boolean.toString(p.usedFc).length());
            characterfile.newLine();
            characterfile.write("unlockedSpecialTasks = ", 0, 23);
            characterfile.write(Boolean.toString(p.unlockedSpecialTasks), 0, Boolean.toString(p.unlockedSpecialTasks).length());
            characterfile.newLine();
            characterfile.write("specialTaskAmount = ", 0, 20);
            characterfile.write(Integer.toString(p.specialTaskAmount), 0, Integer.toString(p.specialTaskAmount).length());
            characterfile.newLine();
            characterfile.write("BaBaInstanceKills = ", 0, 20);
            characterfile.write(Integer.toString(p.BaBaInstanceKills), 0, Integer.toString(p.BaBaInstanceKills).length());
            characterfile.newLine();
            characterfile.write("specialTaskNpc = ", 0, 17);
            characterfile.write(Integer.toString(p.specialTaskNpc), 0, Integer.toString(p.specialTaskNpc).length());
            characterfile.newLine();
            characterfile.write("enhancerCrystal = ", 0, 18);
            characterfile.write(Boolean.toString(p.enhancerCrystal), 0, Boolean.toString(p.enhancerCrystal).length());
            characterfile.newLine();
            characterfile.write("chaoticInstance = ", 0, 18);
            characterfile.write(Boolean.toString(p.unlockChaoticInstance), 0, Boolean.toString(p.unlockChaoticInstance).length());
            characterfile.newLine();
            characterfile.write("setPin = ", 0, 9);
            characterfile.write(Boolean.toString(p.setPin), 0, Boolean.toString(p.setPin).length());
            characterfile.newLine();
            characterfile.write("bigger-boss-tasks = " + p.getSlayer().isBiggerBossTasks());
            characterfile.newLine();
            characterfile.write("cerberus-route = " + p.getSlayer().isCerberusRoute());
            characterfile.newLine();
            characterfile.write("slayer-tasks-completed = " + p.slayerTasksCompleted);
            characterfile.newLine();
            characterfile.write("claimedReward = ", 0, 16);
            characterfile.write(Boolean.toString(p.claimedReward), 0, Boolean.toString(p.claimedReward).length());
            characterfile.newLine();
            characterfile.write("dragonfire-shield-charge = " + p.getDragonfireShieldCharge());
            characterfile.newLine();
            characterfile.write("rfd-gloves = " + p.rfdGloves);
            characterfile.newLine();
            characterfile.write("wave-id = " + p.waveId);
            characterfile.newLine();
            characterfile.write("wave-type = " + p.fightCavesWaveType);
            characterfile.newLine();
            characterfile.write("wave-info = " + p.waveInfo[0] + "\t" + p.waveInfo[1] + "\t" + p.waveInfo[2]);
            characterfile.newLine();

            characterfile.write("help-cc-muted = " + p.isHelpCcMuted());
            characterfile.newLine();

            characterfile.write("gamble-banned = " + p.isGambleBanned());
            characterfile.newLine();

            characterfile.write("usedReferral = " + p.usedReferral);
            characterfile.newLine();
            characterfile.write("master-clue-reqs = " + p.masterClueRequirement[0] + "\t" + p.masterClueRequirement[1] + "\t" + p.masterClueRequirement[2] + "\t" + p.masterClueRequirement[3]);
            characterfile.newLine();
            characterfile.write("counters = ");
            for (int i = 0; i < p.counters.length; i++) characterfile.write("" + p.counters[i] + ((i == p.counters.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("max-cape = ");
            for (int i = 0; i < p.maxCape.length; i++) characterfile.write("" + p.maxCape[i] + ((i == p.maxCape.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("zulrah-best-time = " + p.getBestZulrahTime());
            characterfile.newLine();
            characterfile.write("toxic-staff = " + p.getToxicStaffOfTheDeadCharge());
            characterfile.newLine();
            characterfile.write("toxic-pipe-ammo = " + p.getToxicBlowpipeAmmo());
            characterfile.newLine();
            characterfile.write("toxic-pipe-amount = " + p.getToxicBlowpipeAmmoAmount());
            characterfile.newLine();
            characterfile.write("toxic-pipe-charge = " + p.getToxicBlowpipeCharge());
            characterfile.newLine();
            characterfile.write("serpentine-helm = " + p.getSerpentineHelmCharge());
            characterfile.newLine();
            characterfile.write("trident-of-the-seas = " + p.getTridentCharge());
            characterfile.newLine();
            characterfile.write("trident-of-the-swamp = " + p.getToxicTridentCharge());
            characterfile.newLine();
            characterfile.write("arclight-charge = " + p.getArcLightCharge());
            characterfile.newLine();
            characterfile.write("sang-staff-charge = " + p.getSangStaffCharge());
            characterfile.newLine();

            characterfile.write("bryophyta-charge = " + p.bryophytaStaffCharges);
            characterfile.newLine();

            characterfile.write("slayerPoints = " + p.getSlayer().getPoints());
            characterfile.newLine();
            characterfile.write("LastLoginYear = ", 0, 16);
            characterfile.write(Integer.toString(p.LastLoginYear), 0, Integer.toString(p.LastLoginYear).length());
            characterfile.newLine();
            characterfile.write("LastLoginMonth = ", 0, 17);
            characterfile.write(Integer.toString(p.LastLoginMonth), 0, Integer.toString(p.LastLoginMonth).length());
            characterfile.newLine();
            characterfile.write("LastLoginDate = ", 0, 16);
            characterfile.write(Integer.toString(p.LastLoginDate), 0, Integer.toString(p.LastLoginDate).length());
            characterfile.newLine();
            characterfile.write("LoginStreak = ", 0, 14);
            characterfile.write(Integer.toString(p.LoginStreak), 0, Integer.toString(p.LoginStreak).length());
            characterfile.newLine();
            characterfile.write("crystal-bow-shots = ", 0, 20);
            characterfile.write(Integer.toString(p.crystalBowArrowCount), 0, Integer.toString(p.crystalBowArrowCount).length());
            characterfile.newLine();
            characterfile.write("skull-timer = ", 0, 14);
            characterfile.write(Integer.toString(p.skullTimer), 0, Integer.toString(p.skullTimer).length());
            characterfile.newLine();
            characterfile.write("afkTier = ", 0, 10);
            characterfile.write(Integer.toString(p.getAfkTier()), 0, Integer.toString(p.getAfkTier()).length());
            characterfile.newLine();
            characterfile.write("afkAttempts = ", 0, 14);
            characterfile.write(Integer.toString(p.getAfkAttempts()), 0, Integer.toString(p.getAfkAttempts()).length());
            characterfile.newLine();
            characterfile.write("magic-book = ", 0, 13);
            characterfile.write(Integer.toString(p.playerMagicBook), 0, Integer.toString(p.playerMagicBook).length());
            characterfile.newLine();
            characterfile.write("special-amount = ", 0, 17);
            characterfile.write(Double.toString(p.specAmount), 0, Double.toString(p.specAmount).length());
            characterfile.newLine();
            characterfile.write("prayer-amount = " + Double.toString(p.prayerPoint));
            characterfile.newLine();
            characterfile.write("KC = ", 0, 4);
            characterfile.write(Integer.toString(p.killcount), 0, Integer.toString(p.killcount).length());
            characterfile.newLine();
            characterfile.write("DC = ", 0, 4);
            characterfile.write(Integer.toString(p.deathcount), 0, Integer.toString(p.deathcount).length());
            characterfile.newLine();
            characterfile.write("total-hunter-kills = " + p.getBH().getTotalHunterKills());
            characterfile.newLine();
            characterfile.write("total-rogue-kills = " + p.getBH().getTotalRogueKills());
            characterfile.newLine();
            characterfile.write("target-time-delay = " + p.getBH().getDelayedTargetTicks());
            characterfile.newLine();
            characterfile.write("bh-penalties = " + p.getBH().getWarnings());
            characterfile.newLine();
            characterfile.write("bh-bounties = " + p.getBH().getBounties());
            characterfile.newLine();
            characterfile.write("statistics-visible = " + p.getBH().isStatisticsVisible());
            characterfile.newLine();
            characterfile.write("spell-accessible = " + p.getBH().isSpellAccessible());
            characterfile.newLine();
            characterfile.write("zerkAmount = ", 0, 13);
            characterfile.newLine();
            characterfile.write("autoRet = ", 0, 10);
            characterfile.write(Integer.toString(p.autoRet), 0, Integer.toString(p.autoRet).length());
            characterfile.newLine();
            characterfile.write("pkp = ", 0, 6);
            characterfile.write(Integer.toString(p.pkp), 0, Integer.toString(p.pkp).length());
            characterfile.newLine();
            characterfile.write("elvenCharge = ", 0, 14);
            characterfile.write(Integer.toString(p.elvenCharge), 0, Integer.toString(p.elvenCharge).length());
            characterfile.newLine();
            characterfile.write("slaughterCharge = ", 0, 18);
            characterfile.write(Integer.toString(p.slaughterCharge), 0, Integer.toString(p.slaughterCharge).length());
            characterfile.newLine();
            characterfile.write("tomeOfFirePages = ");
            characterfile.write(Integer.toString(p.getTomeOfFire().getPages()));
            characterfile.newLine();
            characterfile.write("tomeOfFireCharges = ");
            characterfile.write(Integer.toString(p.getTomeOfFire().getCharges()));
            characterfile.newLine();
            characterfile.write("ether = ", 0, 7);
            characterfile.write(Integer.toString(p.braceletEtherCount), 0, Integer.toString(p.braceletEtherCount).length());
            characterfile.newLine();
            characterfile.write("crawsbowCharge = ");
            characterfile.write(Integer.toString(p.getPvpWeapons().getCrawsBowCharges()));
            characterfile.newLine();
            characterfile.write("thammaronCharge = ");
            characterfile.write(Integer.toString(p.getPvpWeapons().getThammaronSceptreCharges()));
            characterfile.newLine();
            characterfile.write("viggoraCharge = ");
            characterfile.write(Integer.toString(p.getPvpWeapons().getViggoraChainmaceCharges()));
            characterfile.newLine();
            characterfile.write("tbowCharge = ");
            characterfile.write(Integer.toString(p.getChristmasWeapons().getBowChargesCharges()));
            characterfile.newLine();
            characterfile.write("scytheCharge = ");
            characterfile.write(Integer.toString(p.getChristmasWeapons().getScytheCharges()));
            characterfile.newLine();
            characterfile.write("whipCharge = ");
            characterfile.write(Integer.toString(p.getChristmasWeapons().getWhipCharges()));
            characterfile.newLine();
            characterfile.write("presentCounter = ");
            characterfile.write(Integer.toString(p.getPresentCounter()));
            characterfile.newLine();

            characterfile.write("bossPoints = ");
            characterfile.write(Integer.toString(p.bossPoints));
            characterfile.newLine();
            characterfile.write("bossPointsRefund = ");
            characterfile.write(Boolean.toString(p.bossPointsRefund));
            characterfile.newLine();


            characterfile.write("tWin = " + p.tournamentWins);
            characterfile.newLine();

            characterfile.write("tPoint = " + p.tournamentPoints);
            characterfile.newLine();
            characterfile.write("wgPoint = " + p.WGPoints);
            characterfile.newLine();
            characterfile.write("wgWin = " + p.WGWins);
            characterfile.newLine();

            characterfile.write("streak = " + p.streak);
            characterfile.newLine();

            characterfile.write("outlastKills = " + p.outlastKills);
            characterfile.newLine();

            characterfile.write("outlastDeaths = " + p.outlastDeaths);
            characterfile.newLine();

            characterfile.write("tournamentTotalGames = " + p.tournamentTotalGames);
            characterfile.newLine();


            characterfile.write("xpMaxSkills = ", 0, 14);
            characterfile.write(Integer.toString(p.xpMaxSkills), 0, Integer.toString(p.xpMaxSkills).length());
            characterfile.newLine();
            characterfile.write("RefU = ", 0, 6);
            characterfile.write(Integer.toString(p.referallFlag), 0, Integer.toString(p.referallFlag).length());
            characterfile.newLine();
            characterfile.write("LoyP = ", 0, 6);
            characterfile.write(Integer.toString(p.loyaltyPoints), 0, Integer.toString(p.loyaltyPoints).length());
            characterfile.newLine();
            characterfile.write("dayv = ", 0, 6);
            characterfile.write(Integer.toString(p.voteKeyPoints), 0, Integer.toString(p.voteKeyPoints).length());
            characterfile.newLine();
            characterfile.write("donP = ", 0, 6);
            characterfile.write(Integer.toString(p.donatorPoints), 0, Integer.toString(p.donatorPoints).length());
            characterfile.newLine();
            characterfile.write("voteEntry = ", 0, 12);
            characterfile.write(Integer.toString(p.VoteEntries), 0, Integer.toString(p.VoteEntries).length());
            characterfile.newLine();
            characterfile.write("bjwin = ", 0, 8);
            characterfile.write(Integer.toString(p.BjWins), 0, Integer.toString(p.BjWins).length());
            characterfile.newLine();
            characterfile.write("bjloss = ", 0, 9);
            characterfile.write(Integer.toString(p.BjLoss), 0, Integer.toString(p.BjLoss).length());
            characterfile.newLine();
            characterfile.write("bjpay = ", 0, 8);
            characterfile.write(Integer.toString(p.BjPay), 0, Integer.toString(p.BjPay).length());
            characterfile.newLine();
            characterfile.write("storetrans = ", 0, 13);
            characterfile.write(Boolean.toString(p.StoreTransfer), 0, Boolean.toString(p.StoreTransfer).length());
            characterfile.newLine();
            characterfile.write("spins = ", 0, 8);
            characterfile.write(Integer.toString(p.getFortuneSpins()), 0, Integer.toString(p.getFortuneSpins()).length());
            characterfile.newLine();
            characterfile.write("afk = ", 0, 6);
            characterfile.write(Integer.toString(p.getAfkPoints()), 0, Integer.toString(p.getAfkPoints()).length());
            characterfile.newLine();
            characterfile.write("cleptonoti = ", 0, 13);
            characterfile.write(Boolean.toString(p.CleptNotification), 0, Boolean.toString(p.CleptNotification).length());
            characterfile.newLine();
            characterfile.write("bloodyp = ", 0, 10);
            characterfile.write(Integer.toString(p.getBloody_points()), 0, Integer.toString(p.getBloody_points()).length());
            characterfile.newLine();
            characterfile.write("seasonal = ", 0, 11);
            characterfile.write(Integer.toString(p.getSeasonalPoints()), 0, Integer.toString(p.getSeasonalPoints()).length());
            characterfile.newLine();
            characterfile.write("donA = ", 0, 6);
            characterfile.write(Integer.toString(p.amDonated), 0, Integer.toString(p.amDonated).length());
            characterfile.newLine();
            characterfile.write("donB = ", 0, 6);
            characterfile.write(Long.toString(p.getStoreDonated()), 0, Long.toString(p.getStoreDonated()).length());
            characterfile.newLine();
            characterfile.write("donD = ", 0, 6);
            characterfile.write(Long.toString(p.getDailyDonated()), 0, Long.toString(p.getDailyDonated()).length());
            characterfile.newLine();
            characterfile.write("cflipRakeback = ", 0, 16);
            characterfile.write(Integer.toString(p.CoinFlipRakeBack), 0, Integer.toString(p.CoinFlipRakeBack).length());
            characterfile.newLine();
            characterfile.write("damnedpoints = ", 0, 15);
            characterfile.write(Long.toString(p.damnedPoints), 0, Long.toString(p.damnedPoints).length());
            characterfile.newLine();
            characterfile.write("nmzpoints = ", 0, 12);
            characterfile.write(Long.toString(p.NMZPoints), 0, Long.toString(p.NMZPoints).length());
            characterfile.newLine();
            characterfile.write("nmzgoldenboss = ", 0, 16);
            characterfile.write(Long.toString(p.NMZGoldenBoss), 0, Long.toString(p.NMZGoldenBoss).length());
            characterfile.newLine();
            characterfile.write("nmzhealing = ", 0, 12);
            characterfile.write(Long.toString(p.NMZHealing), 0, Long.toString(p.NMZHealing).length());
            characterfile.newLine();
            characterfile.write("nmzdoubledrop = ", 0, 15);
            characterfile.write(Long.toString(p.NMZDoubleDrop), 0, Long.toString(p.NMZDoubleDrop).length());
            characterfile.newLine();
            characterfile.write("nmzabsorption = ", 0, 15);
            characterfile.write(Long.toString(p.NMZAbsorption), 0, Long.toString(p.NMZAbsorption).length());
            characterfile.newLine();
            characterfile.write("tpNomad = ", 0, 10);
            characterfile.write(Long.toString(p.getTradePost().getNomadCoffer()), 0, Long.toString(p.getTradePost().getNomadCoffer()).length());
            characterfile.newLine();
            characterfile.write("tpPlat = ", 0, 9);
            characterfile.write(Long.toString(p.getTradePost().getCoinCoffer()), 0, Long.toString(p.getTradePost().getCoinCoffer()).length());
            characterfile.newLine();
            characterfile.write("donW = ", 0, 6);
            characterfile.write(Long.toString(p.getWeeklyDonated()), 0, Long.toString(p.getWeeklyDonated()).length());
            characterfile.newLine();
            characterfile.write("babapoints = ", 0, 13);
            characterfile.write(Long.toString(p.BabaPoints), 0, Long.toString(p.BabaPoints).length());
            characterfile.newLine();
            characterfile.write("premiumpoints = ", 0, 15);
            characterfile.write(Long.toString(p.PremiumPoints), 0, Long.toString(p.PremiumPoints).length());
            characterfile.newLine();
            characterfile.write("dailydmg = ", 0, 11);
            characterfile.write(Long.toString(p.dailyDamage), 0, Long.toString(p.dailyDamage).length());
            characterfile.newLine();
            characterfile.write("daily2xraid = ", 0, 14);
            characterfile.write(Long.toString(p.daily2xRaidLoot), 0, Long.toString(p.daily2xRaidLoot).length());
            characterfile.newLine();
            characterfile.write("daily2x = ", 0, 10);
            characterfile.write(Long.toString(p.daily2xXPGain), 0, Long.toString(p.daily2xXPGain).length());
            characterfile.newLine();
            characterfile.write("dailyddr = ", 0, 11);
            characterfile.write(Long.toString(p.doubleDropRate), 0, Long.toString(p.doubleDropRate).length());
            characterfile.newLine();
            characterfile.write("dailyAgro = ", 0, 12);
            characterfile.write(Long.toString(p.weeklyInfAgro), 0, Long.toString(p.weeklyInfAgro).length());
            characterfile.newLine();
            characterfile.write("dailyPray = ", 0, 12);
            characterfile.write(Long.toString(p.weeklyInfPot), 0, Long.toString(p.weeklyInfPot).length());
            characterfile.newLine();
            characterfile.write("dailyOverload = ", 0, 16);
            characterfile.write(Long.toString(p.weeklyOverload), 0, Long.toString(p.weeklyOverload).length());
            characterfile.newLine();
            characterfile.write("dailyRage = ", 0, 12);
            characterfile.write(Long.toString(p.weeklyRage), 0, Long.toString(p.weeklyRage).length());
            characterfile.newLine();
            characterfile.write("eliteboost = ", 0, 13);
            characterfile.write(Long.toString(p.EliteCentBoost), 0, Long.toString(p.EliteCentBoost).length());
            characterfile.newLine();
            characterfile.write("eliteboostc = ", 0, 14);
            characterfile.write(Long.toString(p.EliteCentCooldown), 0, Long.toString(p.EliteCentCooldown).length());
            characterfile.newLine();
            characterfile.write("cent = ", 0, 6);
            characterfile.write(Integer.toString(p.centurion), 0, Integer.toString(p.centurion).length());
            characterfile.newLine();
            characterfile.write("cosC = ", 0, 6);
            characterfile.write(Long.toString(p.getCosmeticCredits()), 0, Long.toString(p.getCosmeticCredits()).length());
            characterfile.newLine();
            characterfile.write("arboPoints = ", 0, 13);
            characterfile.write(Long.toString(p.arboPoints), 0, Long.toString(p.arboPoints).length());
            characterfile.newLine();
            characterfile.write("shadowPoints = ", 0, 15);
            characterfile.write(Long.toString(p.shadowCrusadePoints), 0, Long.toString(p.shadowCrusadePoints).length());
            characterfile.newLine();
            characterfile.write("shadowCompletions = ", 0, 20);
            characterfile.write(Integer.toString(p.shadowCrusadeCompletions), 0, Integer.toString(p.shadowCrusadeCompletions).length());
            characterfile.newLine();
            characterfile.write("foundry = ", 0, 10);
            characterfile.write(Long.toString(p.foundryPoints), 0, Long.toString(p.foundryPoints).length());
            characterfile.newLine();
            characterfile.write("blastcoffer = ", 0, 14);
            characterfile.write(Integer.toString(p.getBlastFurnace().getCoffer().getCoinsInCoffer()), 0, Integer.toString(p.getBlastFurnace().getCoffer().getCoinsInCoffer()).length());
            characterfile.newLine();
            characterfile.write("discordbooster = ", 0, 17);
            characterfile.write(Long.toString(p.getDiscordboostlastClaimed()), 0, Long.toString(p.getDiscordboostlastClaimed()).length());
            characterfile.newLine();
            characterfile.write("discordboosting = ", 0, 18);
            characterfile.write(Long.toString(p.getDiscordlastClaimed()), 0, Long.toString(p.getDiscordlastClaimed()).length());
            characterfile.newLine();
            characterfile.write("discord = ", 0, 10);
            characterfile.write(Boolean.toString(p.getDiscordlinked()), 0, Boolean.toString(p.getDiscordlinked()).length());
            characterfile.newLine();
            characterfile.write("discordpoints = ", 0, 16);
            characterfile.write(Integer.toString(p.getDiscordPoints()), 0, Integer.toString(p.getDiscordPoints()).length());
            characterfile.newLine();
            characterfile.write("discordtag = ", 0, 13);
            characterfile.write(p.getDiscordTag().toString(), 0, p.getDiscordTag().length());
            characterfile.newLine();
            characterfile.write("discorduser = ", 0, 14);
            characterfile.write(Long.toString(p.getDiscordUser()), 0, Long.toString(p.getDiscordUser()).length());
            characterfile.newLine();
            characterfile.write("donobosskc = ", 0, 13);
            characterfile.write(Integer.toString(p.getDonorBossKC()), 0, Integer.toString(p.getDonorBossKC()).length());
            characterfile.newLine();
            characterfile.write("donobosstime = ", 0, 15);
            characterfile.write(p.getDonorBossDate().toString(), 0, p.getDonorBossDate().toString().length());
            characterfile.newLine();
            characterfile.write("donobosskcx = ", 0, 14);
            characterfile.write(Integer.toString(p.getDonorBossKCx()), 0, Integer.toString(p.getDonorBossKCx()).length());
            characterfile.newLine();
            characterfile.write("donobosstimex = ", 0, 16);
            characterfile.write(p.getDonorBossDatex().toString(), 0, p.getDonorBossDatex().toString().length());
            characterfile.newLine();
            characterfile.write("donobosskcy = ", 0, 14);
            characterfile.write(Integer.toString(p.getDonorBossKCy()), 0, Integer.toString(p.getDonorBossKCy()).length());
            characterfile.newLine();
            characterfile.write("donobosstimey = ", 0, 16);
            characterfile.write(p.getDonorBossDatey().toString(), 0, p.getDonorBossDatey().toString().length());
            characterfile.newLine();
            characterfile.write("donobosskz = ", 0, 13);
            characterfile.write(Integer.toString(p.getDonorBossKCz()), 0, Integer.toString(p.getDonorBossKCz()).length());
            characterfile.newLine();
            characterfile.write("donobosstimez = ", 0, 16);
            characterfile.write(p.getDonorBossDatez().toString(), 0, p.getDonorBossDatez().toString().length());
            characterfile.newLine();
            characterfile.write("donobosskw = ", 0, 13);
            characterfile.write(Integer.toString(p.getDonorBossKCw()), 0, Integer.toString(p.getDonorBossKCw()).length());
            characterfile.newLine();
            characterfile.write("donobosstimew = ", 0, 16);
            characterfile.write(p.getDonorBossDatew().toString(), 0, p.getDonorBossDatew().toString().length());
            characterfile.newLine();
            characterfile.write("prestige-points = ", 0, 18);
            characterfile.write(Integer.toString(p.prestigePoints), 0, Integer.toString(p.prestigePoints).length());
            characterfile.newLine();
            characterfile.write("division-tier = ", 0, 16);
            characterfile.write(Integer.toString(p.getTier()), 0, Integer.toString(p.getTier()).length());
            characterfile.newLine();
            characterfile.write("division-xp = ", 0, 14);
            characterfile.write(Integer.toString(p.getXp()), 0, Integer.toString(p.getXp()).length());
            characterfile.newLine();
            characterfile.write("division-member = ", 0, 18);
            characterfile.write(Boolean.toString(p.isMember()), 0, Boolean.toString(p.isMember()).length());
            characterfile.newLine();
            characterfile.write("division-season = ", 0, 18);
            characterfile.write(Integer.toString(p.getCurrentSeason()), 0, Integer.toString(p.getCurrentSeason()).length());
            characterfile.newLine();
            characterfile.write("votePoints = ", 0, 13);
            characterfile.write(Integer.toString(p.votePoints), 0, Integer.toString(p.votePoints).length());
            characterfile.newLine();
            characterfile.write("bloodPoints = ", 0, 14);
            characterfile.write(Integer.toString(p.bloodPoints), 0, Integer.toString(p.bloodPoints).length());
            characterfile.newLine();
            characterfile.write("d1Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d1Complete), 0, Boolean.toString(p.d1Complete).length());
            characterfile.newLine();
            characterfile.write("d2Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d2Complete), 0, Boolean.toString(p.d2Complete).length());
            characterfile.newLine();
            characterfile.write("d3Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d3Complete), 0, Boolean.toString(p.d3Complete).length());
            characterfile.newLine();
            characterfile.write("d4Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d4Complete), 0, Boolean.toString(p.d4Complete).length());
            characterfile.newLine();
            characterfile.write("d5Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d5Complete), 0, Boolean.toString(p.d5Complete).length());
            characterfile.newLine();
            characterfile.write("d6Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d6Complete), 0, Boolean.toString(p.d6Complete).length());
            characterfile.newLine();
            characterfile.write("d7Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d7Complete), 0, Boolean.toString(p.d7Complete).length());
            characterfile.newLine();
            characterfile.write("d8Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d8Complete), 0, Boolean.toString(p.d8Complete).length());
            characterfile.newLine();
            characterfile.write("d9Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d9Complete), 0, Boolean.toString(p.d9Complete).length());
            characterfile.newLine();
            characterfile.write("d10Complete = ", 0, 14);
            characterfile.write(Boolean.toString(p.d10Complete), 0, Boolean.toString(p.d10Complete).length());
            characterfile.newLine();
            characterfile.write("d11Complete = ", 0, 14);
            characterfile.write(Boolean.toString(p.d11Complete), 0, Boolean.toString(p.d11Complete).length());
            characterfile.newLine();
            characterfile.write("achievement-points = " + p.getAchievements().getPoints());
            characterfile.newLine();
            characterfile.write("xpLock = ", 0, 9);
            characterfile.write(Boolean.toString(p.expLock), 0, Boolean.toString(p.expLock).length());
            characterfile.newLine();
            characterfile.write("teleblock-length = ", 0, 19);
            characterfile.write(Integer.toString(tbTime), 0, Integer.toString(tbTime).length());
            characterfile.newLine();
            //Varrock
            String varrockClaimed = "VarrockClaimedDiaries = ";
            characterfile.write(varrockClaimed, 0, varrockClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getVarrockDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Ardougne
            String ardougneClaimed = "ArdougneClaimedDiaries = ";
            characterfile.write(ardougneClaimed, 0, ardougneClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getArdougneDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Desert
            String desertClaimed = "DesertClaimedDiaries = ";
            characterfile.write(desertClaimed, 0, desertClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getDesertDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Falador
            String faladorClaimed = "FaladorClaimedDiaries = ";
            characterfile.write(faladorClaimed, 0, faladorClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getFaladorDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Fremennik
            String fremennikClaimed = "FremennikClaimedDiaries = ";
            characterfile.write(fremennikClaimed, 0, fremennikClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getFremennikDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Kandarin
            String kandarinClaimed = "KandarinClaimedDiaries = ";
            characterfile.write(kandarinClaimed, 0, kandarinClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getKandarinDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Karamja
            String karamjaClaimed = "KaramjaClaimedDiaries = ";
            characterfile.write(karamjaClaimed, 0, karamjaClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getKaramjaDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Lumbridge
            String lumbridgeClaimed = "LumbridgeClaimedDiaries = ";
            characterfile.write(lumbridgeClaimed, 0, lumbridgeClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getLumbridgeDraynorDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Morytania
            String morytaniaClaimed = "MorytaniaClaimedDiaries = ";
            characterfile.write(morytaniaClaimed, 0, morytaniaClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getMorytaniaDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Western
            String westernClaimed = "WesternClaimedDiaries = ";
            characterfile.write(westernClaimed, 0, westernClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getWesternDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Wilderness
            String wildernessClaimed = "WildernessClaimedDiaries = ";
            characterfile.write(wildernessClaimed, 0, wildernessClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getWildernessDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            String diary = "diaries = ";
            characterfile.write(diary, 0, diary.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                // Varrock
                for (VarrockDiaryEntry entry : p.getDiaryManager().getVarrockDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Ardougne
                for (ArdougneDiaryEntry entry : p.getDiaryManager().getArdougneDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Desert
                for (DesertDiaryEntry entry : p.getDiaryManager().getDesertDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Falador
                for (FaladorDiaryEntry entry : p.getDiaryManager().getFaladorDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Fremennik
                for (FremennikDiaryEntry entry : p.getDiaryManager().getFremennikDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Kandarin
                for (KandarinDiaryEntry entry : p.getDiaryManager().getKandarinDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Karamja
                for (KaramjaDiaryEntry entry : p.getDiaryManager().getKaramjaDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Lumbridge
                for (LumbridgeDraynorDiaryEntry entry : p.getDiaryManager().getLumbridgeDraynorDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Morytania
                for (MorytaniaDiaryEntry entry : p.getDiaryManager().getMorytaniaDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Western
                for (WesternDiaryEntry entry : p.getDiaryManager().getWesternDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Wilderness
                for (WildernessDiaryEntry entry : p.getDiaryManager().getWildernessDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            String partialDiary = "partialDiaries = ";
            //forEachPartial
            characterfile.write(partialDiary, 0, partialDiary.length()); //Saw that earlier but forgot lol, ahh ty
            {
                StringBuilder bldr = new StringBuilder();
                String prefix = "";
                //Varrock
                for (Entry<VarrockDiaryEntry, Integer> keyval : p.getDiaryManager().getVarrockDiary().getPartialAchievements().entrySet()) {
                    VarrockDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Ardougne
                for (Entry<ArdougneDiaryEntry, Integer> keyval : p.getDiaryManager().getArdougneDiary().getPartialAchievements().entrySet()) {
                    ArdougneDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Desert
                for (Entry<DesertDiaryEntry, Integer> keyval : p.getDiaryManager().getDesertDiary().getPartialAchievements().entrySet()) {
                    DesertDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Falador
                for (Entry<FaladorDiaryEntry, Integer> keyval : p.getDiaryManager().getFaladorDiary().getPartialAchievements().entrySet()) {
                    FaladorDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Fremennik
                for (Entry<FremennikDiaryEntry, Integer> keyval : p.getDiaryManager().getFremennikDiary().getPartialAchievements().entrySet()) {
                    FremennikDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Kandarin
                for (Entry<KandarinDiaryEntry, Integer> keyval : p.getDiaryManager().getKandarinDiary().getPartialAchievements().entrySet()) {
                    KandarinDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Karamja
                for (Entry<KaramjaDiaryEntry, Integer> keyval : p.getDiaryManager().getKaramjaDiary().getPartialAchievements().entrySet()) {
                    KaramjaDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Lumbridge
                for (Entry<LumbridgeDraynorDiaryEntry, Integer> keyval : p.getDiaryManager().getLumbridgeDraynorDiary().getPartialAchievements().entrySet()) {
                    LumbridgeDraynorDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Morytania
                for (Entry<MorytaniaDiaryEntry, Integer> keyval : p.getDiaryManager().getMorytaniaDiary().getPartialAchievements().entrySet()) {
                    MorytaniaDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Western
                for (Entry<WesternDiaryEntry, Integer> keyval : p.getDiaryManager().getWesternDiary().getPartialAchievements().entrySet()) {
                    WesternDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Wilderness
                for (Entry<WildernessDiaryEntry, Integer> keyval : p.getDiaryManager().getWildernessDiary().getPartialAchievements().entrySet()) {
                    WildernessDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            characterfile.write("pc-points = ", 0, 12);
            characterfile.write(Integer.toString(p.pcPoints), 0, Integer.toString(p.pcPoints).length());
            characterfile.newLine();
            characterfile.write("aoe-points = ", 0, 13);
            characterfile.write(Long.toString(p.instanceCurrency), 0, Long.toString(p.instanceCurrency).length());
            characterfile.newLine();
            characterfile.write("total-raids = ", 0, 14);
            characterfile.write(Integer.toString(p.totalRaidsFinished), 0, Integer.toString(p.totalRaidsFinished).length());
            characterfile.newLine();
            characterfile.write("killStreak = ", 0, 13);
            characterfile.write(Integer.toString(p.killStreak), 0, Integer.toString(p.killStreak).length());
            characterfile.newLine();
            characterfile.write("bonus-end = ", 0, 12);
            characterfile.write(Long.toString(p.bonusXpTime), 0, Long.toString(p.bonusXpTime).length());
            characterfile.newLine();
            characterfile.write("safety-end = ", 0, 13);
            characterfile.write(Long.toString(p.SafetyTimer), 0, Long.toString(p.SafetyTimer).length());
            characterfile.newLine();
            characterfile.write("island-end = ", 0, 13);
            characterfile.write(Long.toString(p.IslandTimer), 0, Long.toString(p.IslandTimer).length());
            characterfile.newLine();
            characterfile.write("jail-end = ", 0, 11);
            characterfile.write(Long.toString(p.jailEnd), 0, Long.toString(p.jailEnd).length());
            characterfile.newLine();
            characterfile.write("mute-end = ", 0, 11);
            characterfile.write(Long.toString(p.muteEnd), 0, Long.toString(p.muteEnd).length());
            characterfile.newLine();
            characterfile.write("last-yell = " + p.lastYell);
            characterfile.newLine();
            characterfile.write("splitChat = ", 0, 12);
            characterfile.write(Boolean.toString(p.splitChat), 0, Boolean.toString(p.splitChat).length());
            characterfile.newLine();
            characterfile.write("lastVote = " + p.getLastVote().toEpochDay());
            characterfile.newLine();
            characterfile.write("lastVotePanelPoint = " + p.getLastVotePanelPoint().toEpochDay());
            characterfile.newLine();

            if (p.getSlayer().getTask().isPresent()) {
                Task task = p.getSlayer().getTask().get();
                characterfile.write("slayer-task = " + task.getPrimaryName());
                characterfile.newLine();
                characterfile.write("slayer-task-amount = " + p.getSlayer().getTaskAmount());
                characterfile.newLine();
            }
            characterfile.write("last-task = " + p.lastTask);
            characterfile.newLine();
            characterfile.write("slayerPartner = " + p.slayerPartner);
            characterfile.newLine();
            characterfile.write("slayerParty = " + p.slayerParty);
            characterfile.newLine();
            characterfile.write("run-toggled = " + p.isRunningToggled());
            characterfile.newLine();
            characterfile.write("slayer-master = " + p.getSlayer().getMaster());
            characterfile.newLine();
            characterfile.write("konar-slayer-location = " + p.getKonarSlayerLocation());
            characterfile.newLine();
            characterfile.write("consecutive-tasks = " + p.getSlayer().getConsecutiveTasks());
            characterfile.newLine();
            characterfile.write("mage-arena-points = " + p.getArenaPoints());
            characterfile.newLine();
            characterfile.write("shayzien-assault-points = " + p.getShayPoints());
            characterfile.newLine();
            characterfile.write("flagged = ", 0, 10);
            characterfile.write(Boolean.toString(p.accountFlagged), 0, Boolean.toString(p.accountFlagged).length());
            characterfile.newLine();
            characterfile.write("keepTitle = ", 0, 12);
            characterfile.write(Boolean.toString(p.keepTitle), 0, Boolean.toString(p.keepTitle).length());
            characterfile.newLine();
            characterfile.write("killTitle = ", 0, 12);
            characterfile.write(Boolean.toString(p.killTitle), 0, Boolean.toString(p.killTitle).length());
            characterfile.newLine();
            characterfile.write("wave = ", 0, 7);
            characterfile.write(Integer.toString(p.waveId), 0, Integer.toString(p.waveId).length());
            characterfile.newLine();
            characterfile.write("privatechat = ", 0, 14);
            characterfile.write(Integer.toString(p.getPrivateChat()), 0, Integer.toString(p.getPrivateChat()).length());
            characterfile.newLine();
            characterfile.write("void = ", 0, 7);
            String toWrite55 = p.voidStatus[0] + "\t" + p.voidStatus[1] + "\t" + p.voidStatus[2] + "\t" + p.voidStatus[3] + "\t" + p.voidStatus[4];
            characterfile.write(toWrite55);
            characterfile.newLine();
            characterfile.write("quickprayer = ", 0, 14);
            String quick = "";
            for (int i = 0; i < p.getQuick().getNormal().length; i++) {
                quick += p.getQuick().getNormal()[i] + "\t";
            }
            characterfile.write(quick);
            characterfile.newLine();
            characterfile.write("pouch-rune = " + p.getRuneEssencePouch(0) + "\t" + p.getRuneEssencePouch(1) + "\t" + p.getRuneEssencePouch(2));
            characterfile.newLine();
            characterfile.write("pouch-pure = " + p.getPureEssencePouch(0) + "\t" + p.getPureEssencePouch(1) + "\t" + p.getPureEssencePouch(2));
            characterfile.newLine();
            // Looting bag deposit mode
            characterfile.write("looting_bag_deposit_mode = " + p.getLootingBag().getUseAction());
            characterfile.newLine();
            characterfile.write("district-levels = ");
            for (int i = 0; i < p.playerStats.length; i++) characterfile.write("" + p.playerStats[i] + ((i == p.playerStats.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("inDistrict = ", 0, 13);
            characterfile.write(Boolean.toString(p.pkDistrict), 0, Boolean.toString(p.pkDistrict).length());
            characterfile.newLine();
            characterfile.write("hideDonor = ", 0, 12);
            characterfile.write(Boolean.toString(p.hideDonor), 0, Boolean.toString(p.hideDonor).length());
            characterfile.newLine();
            characterfile.write("safeBoxSlots = ", 0, 15);
            characterfile.write(Integer.toString(p.safeBoxSlots), 0, Integer.toString(p.safeBoxSlots).length());
            characterfile.newLine();

            // Add new stuff below this line
            characterfile.write("serpHelmCombatTicks = ");
            characterfile.write(Long.toString(p.serpHelmCombatTicks));
            characterfile.newLine();
            characterfile.write("gargoyleStairsUnlocked = ");
            characterfile.write(Boolean.toString(p.gargoyleStairsUnlocked));
            characterfile.newLine();
            characterfile.write("firstAchievementLoginJune2021 = ");
            characterfile.write(Boolean.toString(p.getAchievements().isFirstAchievementLoginJune2021()));
            characterfile.newLine();
            characterfile.write("controller = ");
            characterfile.write(p.getController().getKey());
            characterfile.newLine();
            characterfile.write("joinedIronmanGroup = ");
            characterfile.write(p.isJoinedIronmanGroup() + "");
            characterfile.newLine();
            characterfile.write("receivedCalendarCosmeticJune2021 = ");
            characterfile.write(p.isReceivedCalendarCosmeticJune2021() + "");
            characterfile.newLine();
            if(p.getCompletionistCapeRe().getOverrides() != null && p.getCompletionistCapeRe().coloursNotDefault()) {
                characterfile.newLine();
                characterfile.write("comp-cape-colours = " + p.getCompletionistCapeRe().toString());
            }

            characterfile.newLine();

            // Don't add new stuff below this line

            for (PlayerSaveEntry entry : playerSaveEntryList) {
                for (String key : entry.getKeys(p)) {
                    try {
                        String encoded = entry.encode(p, key);
                        if (encoded != null) {
                            characterfile.write(key + " = " + entry.encode(p, key));
                            characterfile.newLine();
                        }
                    } catch (Exception e) {
                        logger.error("Error while saving player save entry class={}, key={}, player={}", entry.getClass(), key, p, e);
                        e.printStackTrace();
                    }
                }
            }
            characterfile.newLine();
            /* EQUIPMENT */
            characterfile.write("[EQUIPMENT]", 0, 11);
            characterfile.newLine();
            for (int i = 0; i < p.playerEquipment.length; i++) {
                characterfile.write("character-equip = ", 0, 18);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.playerEquipment[i]), 0, Integer.toString(p.playerEquipment[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.playerEquipmentN[i]), 0, Integer.toString(p.playerEquipmentN[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.newLine();
            }
            characterfile.newLine();
            /* RakeBackSystem */
            characterfile.write("[RAKEBACK]", 0, 10);
            characterfile.newLine();
            for (Map.Entry<Integer, Integer> entry : p.getRakeBackSystem().entrySet()) {
                characterfile.write("rake-back = ", 0, 12);
                characterfile.write(Integer.toString(entry.getKey()), 0, Integer.toString(entry.getKey()).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(entry.getValue()), 0, Integer.toString(entry.getValue()).length());
                characterfile.newLine();
            }
            characterfile.newLine();
            /* PetCosts */
            characterfile.write("[PETCOSTS]", 0, 10);
            characterfile.newLine();
            for (Map.Entry<Integer, long[]> entry : p.petPerkCost.entrySet()) {
                int index = entry.getKey();
                long[] values = entry.getValue();

                characterfile.write("petCost = " + index);
                for (long value : values) {
                    characterfile.write("\t" + value);
                }
                characterfile.newLine();
            }
            characterfile.newLine();
            /* COSMETICS */
            characterfile.write("[COSMETICS]", 0, 11);
            characterfile.newLine();
            for (int i = 0; i < p.playerEquipmentCosmetic.length; i++) {
                characterfile.write("character-cosmetic = ", 0, 21);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.playerEquipmentCosmetic[i]), 0, Integer.toString(p.playerEquipmentCosmetic[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.newLine();
            }
            /* COSMETIC BOOLEANS */
            characterfile.write("[COSMETICS-BOOLEANS]", 0, 20);
            characterfile.newLine();
            for (int i = 0; i < p.cosmeticOverrides.length; i++) {
                characterfile.write("character-cosmetic-boolean = ", 0, 29);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(String.valueOf(p.cosmeticOverrides[i]), 0, String.valueOf(p.cosmeticOverrides[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.newLine();
            }
            characterfile.newLine();
            /* LOOK */
            characterfile.write("[LOOK]", 0, 6);
            characterfile.newLine();
            for (int i = 0; i < p.playerAppearance.length; i++) {
                characterfile.write("character-look = ", 0, 17);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.playerAppearance[i]), 0, Integer.toString(p.playerAppearance[i]).length());
                characterfile.newLine();
            }
            characterfile.newLine();
            /* SKILLS */
            characterfile.write("[SKILLS]", 0, 8);
            characterfile.newLine();
            for (int i = 0; i < p.playerLevel.length; i++) {
                characterfile.write("character-skill = ", 0, 18);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.playerLevel[i]), 0, Integer.toString(p.playerLevel[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.playerXP[i]), 0, Integer.toString(p.playerXP[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Boolean.toString(p.skillLock[i]), 0, Boolean.toString(p.skillLock[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.prestigeLevel[i]), 0, Integer.toString(p.prestigeLevel[i]).length());
                characterfile.newLine();
            }
            characterfile.newLine();
            /* ITEMS */
            characterfile.write("[ITEMS]", 0, 7);
            characterfile.newLine();
            for (int i = 0; i < p.playerItems.length; i++) {
                if (p.playerItems[i] > 0) {
                    characterfile.write("character-item = ", 0, 17);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(p.playerItems[i]), 0, Integer.toString(p.playerItems[i]).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(p.playerItemsN[i]), 0, Integer.toString(p.playerItemsN[i]).length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            /* Perks */
            characterfile.write("[PRESTIGE]", 0, 10);
            characterfile.newLine();
            for (int i = 0; i < p.prestigePerks.size(); i++) {
                characterfile.write("prestige-perk = ", 0, 16);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(p.prestigePerks.get(i).name(), 0, p.prestigePerks.get(i).name().length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Long.toString(p.prestigePerks.get(i).ordinal()), 0, Long.toString(p.prestigePerks.get(i).ordinal()).length());
                characterfile.newLine();
            }
            characterfile.newLine();
            /* Perks */
            characterfile.write("[DISSOLVER]", 0, 11);
            characterfile.newLine();
            for (int i = 0; i < p.getRecentlyDissolvedItems().size(); i++) {
                characterfile.write("disolve-item = ", 0, 15);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.getRecentlyDissolvedItems().get(i)), 0, Integer.toString(p.getRecentlyDissolvedItems().get(i)).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Long.toString(p.getRecentlyDissolvedPrices().get(i)), 0, Long.toString(p.getRecentlyDissolvedPrices().get(i)).length());
                characterfile.newLine();
            }
            characterfile.newLine();
            /* Perks */
            characterfile.write("[PERKS]", 0, 7);
            characterfile.newLine();
            for (int i = 0; i < p.getPerkSytem().gameItems().size(); i++) {
                if (p.getPerkSytem().gameItems().get(i).getId() > 0) {
                    characterfile.write("perk-item = ", 0, 12);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(p.getPerkSytem().gameItems().get(i).getId()), 0, Integer.toString(p.getPerkSytem().gameItems().get(i).getId()).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(p.getPerkSytem().gameItems().get(i).getAmount()), 0, Integer.toString(p.getPerkSytem().gameItems().get(i).getAmount()).length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            /* XmasGifts */
            characterfile.write("[XMAS]", 0, 6);
            characterfile.newLine();
            if (!p.christmasGifts.isEmpty()) {
                for (int i = 0; i < p.christmasGifts.size(); i++) {
                    characterfile.write("gift-item = ", 0, 12);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(p.christmasGifts.get(i).name(), 0, p.christmasGifts.get(i).name().length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            /* BoostingScrolls */
            characterfile.write("[BOOSTINGSCROLLS]", 0, 17);
            characterfile.newLine();
            for (BoostScrolls boostScrolls : p.boostTimers.keySet()) {
                characterfile.write("scrollboost = ", 0, 14);
                characterfile.write(boostScrolls.name(), 0, boostScrolls.name().length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Long.toString(p.boostTimers.get(boostScrolls)), 0, Long.toString(p.boostTimers.get(boostScrolls)).length());
                characterfile.newLine();
            }
            characterfile.newLine();
            /* ITEMVALUES */
            characterfile.write("[RECHARGEITEMS]", 0, 15);
            characterfile.newLine();
            for (int itemId : p.getRechargeItems().getItemValues().keySet()) {
                int value = p.getRechargeItems().getChargesLeft(itemId);
                String itemIdString = Integer.toString(itemId);
                String valueString = Integer.toString(value);
                String lastUsed = p.getRechargeItems().getItemLastUsed(itemId);
                characterfile.write("item = ", 0, 7);
                characterfile.write("\t", 0, 1);
                characterfile.write(itemIdString, 0, itemIdString.length());
                characterfile.write("\t", 0, 1);
                characterfile.write(valueString, 0, valueString.length());
                characterfile.write("\t", 0, 1);
                characterfile.write(lastUsed, 0, lastUsed.length());
                characterfile.newLine();
            }
            characterfile.newLine();
            /* BANK */
            characterfile.write("[BANK]", 0, 6);
            characterfile.newLine();
            for (int bankTabIndex = 0; bankTabIndex < p.getBank().getBankTab().length; bankTabIndex++) {
                BankTab bankTab = p.getBank().getBankTab()[bankTabIndex];
                for (int index = 0; index < bankTab.getItems().size(); index++) {
                    BankItem item = bankTab.getItems().get(index);
                    if (item != null) {
                        characterfile.write("bank-tab = " + bankTabIndex + "\t" + item.getId() + "\t" + item.getAmount());
                        characterfile.newLine();
                    }
                }
            }
            characterfile.newLine();
            characterfile.newLine();
            /* LOOTBAG */
            characterfile.write("[LOOTBAG]", 0, 9);
            characterfile.newLine();
            for (int i = 0; i < p.getLootingBag().getLootingBagContainer().items.size(); i++) {
                if (p.getLootingBag().getLootingBagContainer().items.get(i).getId() > 0) {
                    characterfile.write("bag-item = ", 0, 11);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    int id = p.getLootingBag().getLootingBagContainer().items.get(i).getId();
                    int amt = p.getLootingBag().getLootingBagContainer().items.get(i).getAmount();
                    characterfile.write(Integer.toString(id), 0, Integer.toString(id).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(amt), 0, Integer.toString(amt).length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            /* RUNEPOUCH */
            characterfile.write("[RUNEPOUCH]", 0, 11);
            characterfile.newLine();
            for (int i = 0; i < p.getRunePouch().getItems().size(); i++) {
                if (p.getRunePouch().getItems().get(i).getId() > 0) {
                    characterfile.write("pouch-item = ", 0, 13);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    int id = p.getRunePouch().getItems().get(i).getId();
                    int amt = p.getRunePouch().getItems().get(i).getAmount();
                    characterfile.write(Integer.toString(id), 0, Integer.toString(id).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(amt), 0, Integer.toString(amt).length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            /* HERBSACK */
            characterfile.write("[HERBSACK]", 0, 10);
            characterfile.newLine();
            for (int i = 0; i < p.getHerbSack().getItems().size(); i++) {
                if (p.getHerbSack().getItems().get(i).getId() > 0) {
                    characterfile.write("sack-item = ", 0, 12);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    int id = p.getHerbSack().getItems().get(i).getId();
                    int amt = p.getHerbSack().getItems().get(i).getAmount();
                    characterfile.write(Integer.toString(id), 0, Integer.toString(id).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(amt), 0, Integer.toString(amt).length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            /* GEMBAG */
            characterfile.write("[GEMBAG]", 0, 8);
            characterfile.newLine();
            for (int i = 0; i < p.getGemBag().getItems().size(); i++) {
                if (p.getGemBag().getItems().get(i).getId() > 0) {
                    characterfile.write("bag-item = ", 0, 11);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    int id = p.getGemBag().getItems().get(i).getId();
                    int amt = p.getGemBag().getItems().get(i).getAmount();
                    characterfile.write(Integer.toString(id), 0, Integer.toString(id).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(amt), 0, Integer.toString(amt).length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            /* DeathStorage */
            characterfile.write("[DEATHSTORAGE]");
            characterfile.newLine();
            for (int i = 0; i < p.getDeathStorage().size(); i++) {
                characterfile.write("death-storage = ", 0, 16);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t",0,1);
                int id = p.getDeathStorage().get(i).getId();
                int amt = p.getDeathStorage().get(i).getAmount();
                characterfile.write(Integer.toString(id),0,Integer.toString(id).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(amt), 0, Integer.toString(amt).length());
                characterfile.newLine();
            }
            characterfile.newLine();
            /* TradingPost */
            characterfile.write("[TRADINGPOST]");
            characterfile.newLine();
            List<TradePostOffer> tradingPostOffers = p.getTradePost().tradePostOffers; // Assuming there's a method to get trading post offers
            for (int i = 0; i < tradingPostOffers.size(); i++) {
                characterfile.write("trading-post = "); // Write the offer identifier
                TradePostOffer offer = tradingPostOffers.get(i);
                int id = offer.getItem().getId();
                int amt = offer.getItem().getAmount();
                long pricePerItem = offer.getPricePerItem();
                boolean nomad = offer.isNomad();
                long timestamp = offer.getTimestamp();
                int totalSold = offer.getTotalSold();
                characterfile.write(Integer.toString(id)); // Write item ID
                characterfile.write("\t"); // Write a tab character as separator
                characterfile.write(Integer.toString(amt)); // Write item amount
                characterfile.write("\t"); // Write a tab character as separator
                characterfile.write(Long.toString(pricePerItem)); // Write price per item
                characterfile.write("\t"); // Write a tab character as separator
                characterfile.write(nomad ? "true" : "false"); // Write whether it's a nomad offer
                characterfile.write("\t"); // Write a tab character as separator
                characterfile.write(Integer.toString(totalSold)); // Write total amount sold
                characterfile.write("\t"); // Write a tab character as separator
                characterfile.write(Long.toString(timestamp)); // Write total amount sold
                characterfile.newLine(); // Move to the next line for the next offer
            }
            characterfile.newLine(); // Add an extra newline for separation

            /* Collection Log Claims */
            characterfile.write("[COLLOGCLAIMS]");
            characterfile.newLine();
            for (int i = 0; i < p.getClaimedLog().size(); i++) {
                characterfile.write("collect-log = ", 0, 14);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t",0,1);
                int id = p.getClaimedLog().get(i);
                characterfile.write(Integer.toString(id),0,Integer.toString(id).length());
                characterfile.newLine();
            }
            characterfile.newLine();

            characterfile.write("[DEGRADEABLES]");
            characterfile.newLine();
            characterfile.write("claim-state = ");
            for (int i = 0; i < p.claimDegradableItem.length; i++) {
                characterfile.write(Boolean.toString(p.claimDegradableItem[i]));
                if (i != p.claimDegradableItem.length - 1) {
                    characterfile.write("\t");
                }
            }
            characterfile.newLine();
            for (int i = 0; i < p.degradableItem.length; i++) {
                if (p.degradableItem[i] > 0) {
                    characterfile.write("item = " + i + "\t" + p.degradableItem[i]);
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            characterfile.newLine();

            // Achievement tiers
            for (AchievementTier tier : AchievementTier.values()) {
                characterfile.write("[ACHIEVEMENTS-TIER-" + (tier.getId() + 1) + "]");
                characterfile.newLine();
                p.getAchievements().print(characterfile, tier.getId());
                characterfile.newLine();
                characterfile.newLine();
            }


            characterfile.write("[PRESETS]");
            characterfile.newLine();
            characterfile.write("Names = ");
            characterfile.newLine();
            characterfile.write("[KILLSTREAKS]");
            characterfile.newLine();
            for (Entry<Killstreak.Type, Integer> entry : p.getKillstreak().getKillstreaks().entrySet()) {
                characterfile.write(entry.getKey().name() + " = " + entry.getValue());
                characterfile.newLine();
            }
            characterfile.newLine();
            characterfile.write("[TITLES]");
            characterfile.newLine();
            for (Title title : p.getTitles().getPurchasedList()) {
                characterfile.write("title = " + title.name());
                characterfile.newLine();
            }
            characterfile.newLine();
            characterfile.write("[NPC-TRACKER]");
            characterfile.newLine();
            for (Entry<String, Integer> entry : p.getNpcDeathTracker().getTracker().entrySet()) {
                if (entry != null) {
                    if (entry.getValue() > 0) {
                        characterfile.write(entry.getKey() + " = " + entry.getValue());
                        characterfile.newLine();
                    }
                }
            }
            characterfile.newLine();
            characterfile.write("[POLL-BOOTHS]");
            characterfile.newLine();
            for (Position pollBothObject : p.PollBothObjects) {
                if (pollBothObject != null) {
                    characterfile.write(pollBothObject.getX() + " = " + pollBothObject.getY());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            characterfile.write("[NMZ-BOSSES]");
            characterfile.newLine();
            for (Integer nmzBoss : p.NMZBosses) {
                characterfile.write("nmzboss = " + nmzBoss);
                characterfile.newLine();
            }
            characterfile.write("[EOF]", 0, 5);
            characterfile.newLine();
            characterfile.newLine();
            characterfile.close();
        } catch (Exception ioexception) {
            logger.error("Error while saving player {}", p, ioexception);
            ioexception.printStackTrace();
            return false;
        }
        return true;
    }
}