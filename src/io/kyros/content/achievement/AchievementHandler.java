package io.kyros.content.achievement;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.achievement.Achievements.Achievement;
import io.kyros.content.achievement.inter.AchieveV2;
import io.kyros.content.battlepass.Pass;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import io.kyros.util.logging.player.ClaimAchievementLog;

/**
 * @author Jason MacKeigan (http://www.rune-server.org/members/Jason)
 */
public class AchievementHandler {

    public static final String COLOR = "074091";

    private static final int START_BUTTON_ID = 54801;
    private static final int BUTTON_SEPARATION = 6;

    private static final int MAXIMUM_TIER_ACHIEVEMENTS = 100;
    private static final int MAXIMUM_TIERS = AchievementTier.values().length;

    private final int[][] amountRemaining = new int[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
    private final boolean[][] completed = new boolean[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
    private final boolean[][] claimed = new boolean[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];

    private final Player player;
    public int points;
    private boolean firstAchievementLoginJune2021;

    public AchievementHandler(Player player) {
        this.player = player;
    }

    public void onLogin() {
/*        fixKc(AchievementType.COX, player.totalRaidsFinished);
        fixKc(AchievementType.TOB, player.tobCompletions);
        fixKc(AchievementType.GROTESQUES, player.getNpcDeathTracker().getKc("grotesque guardians"));
        fixKc(AchievementType.NIGHTMARE, player.getNpcDeathTracker().getKc("the nightmare"));
        fixKc(AchievementType.HYDRA, player.getNpcDeathTracker().getKc("alchemical hydra"));
        fixKc(AchievementType.HUNLLEF, player.getNpcDeathTracker().getKc("crystalline hunllef"));
        fixKc(AchievementType.MIMIC, player.getNpcDeathTracker().getKc("the mimic"));
        fixKc(AchievementType.SLAY_CERB, player.getNpcDeathTracker().getKc("cerberus"));*/

        if (!player.hasAchieveFix) {
            fixKc(AchievementType.ARBO, player.arboCompletions);
            fixKc(AchievementType.COX, player.totalRaidsFinished);
            fixKc(AchievementType.TOB, player.tobCompletions);
            fixKc(AchievementType.SLAY_KRAKEN, player.getNpcDeathTracker().getKc("kraken"));
            fixKc(AchievementType.SLAY_SIRE, player.getNpcDeathTracker().getKc("abyssal sire"));
            fixKc(AchievementType.SLAY_KBD, player.getNpcDeathTracker().getKc("king black dragon"));
            fixKc(AchievementType.SLAY_CORP, player.getNpcDeathTracker().getKc("corporeal beast"));
            fixKc(AchievementType.SLAY_CERB, player.getNpcDeathTracker().getKc("cerberus"));
            fixKc(AchievementType.HYDRA, player.getNpcDeathTracker().getKc("alchemical hydra"));
            fixKc(AchievementType.SLAY_VORKATH, player.getNpcDeathTracker().getKc("vorkath"));
            fixKc(AchievementType.NIGHTMARE, player.getNpcDeathTracker().getKc("the nightmare"));
            fixKc(AchievementType.SLAY_NEX, player.getNpcDeathTracker().getKc("nex"));
            fixKc(AchievementType.SLAY_MANTICORE, player.getNpcDeathTracker().getKc("manticore"));
            fixKc(AchievementType.SLAY_JAVELIN_COLOSSUS, player.getNpcDeathTracker().getKc("javelin colossus"));
            fixKc(AchievementType.SLAY_BABA, player.getNpcDeathTracker().getKc("baba"));
            fixKc(AchievementType.SLAY_GROOT, player.getNpcDeathTracker().getKc("groot"));
            fixKc(AchievementType.SLAY_VBOSS, player.getNpcDeathTracker().getKc("vote boss"));
            fixKc(AchievementType.SLAY_DBOSS, player.getNpcDeathTracker().getKc("dono boss"));
            fixKc(AchievementType.SLAY_DURIAL, player.getNpcDeathTracker().getKc("durial"));
            fixKc(AchievementType.SLAY_AFK, player.getNpcDeathTracker().getKc("afk goblin"));
            fixKc(AchievementType.SLAY_SOL_HEREDIT, player.getNpcDeathTracker().getKc("sol heredit"));
            fixKc(AchievementType.SLAY_SHARATHTEERK, player.getNpcDeathTracker().getKc("sharathteerk"));
            fixKc(AchievementType.SLAY_TUMEKEN, player.getNpcDeathTracker().getKc("tumekens warden"));

            // Ultimate ironman achievements to autocomplete (but no rewards)
            if (player.getMode().getType() == ModeType.ULTIMATE_IRON_MAN) {
                AchievementType[] autocomplete = { AchievementType.PRESETS, AchievementType.TOURNAMENT };
                for (Achievement achievement : Achievement.values()) {
                    if (Arrays.stream(autocomplete).anyMatch(it -> achievement.getType() == it)) {
                        setComplete(achievement, true);
                        setClaimed(achievement, true);
                        setAmountRemaining(achievement, achievement.getAmount());
                    }
                }
            }

            // This is a fix for someone having a complete achievement marked as incomplete.
            for (Achievement achievement : Achievement.values()) {
                if (!isComplete(achievement)) {
                    int remaining = getAmountRemaining(achievement);
                    int total = achievement.getAmount();
                    if (remaining == total) {
                        setComplete(achievement, true);
                    }
                }
            }
            player.hasAchieveFix = true;
        }

    }

    public void print(BufferedWriter writer, int tier) {
        try {
            for (Achievement achievement : Achievement.ACHIEVEMENTS) {
                if (achievement.getTier().getId() == tier) {
                    if (amountRemaining[tier][achievement.getId()] > 0) {
                        writer.write(achievement.name().toLowerCase() + " = "
                                + amountRemaining[tier][achievement.getId()]
                                + "\t" + completed[tier][achievement.getId()]
                                + "\t" + claimed[tier][achievement.getId()]
                        );
                        writer.newLine();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If achievement is less than kc, tick achievement by kc.
     */
    private void fixKc(AchievementType type, int kc) {
        for (Achievement achievement : Achievement.values()) {
            if (achievement.getType() == type) {
                int amount = kc - getAmountRemaining(achievement);
                if (amount > 0) {
                    Achievements.increase(player, achievement.getType(), amount);
                }
            }
        }
    }

    public void readFromSave(String name, String[] data, AchievementTier tier) {
        int amount = Integer.parseInt(data[0]);
        boolean complete = Boolean.parseBoolean(data[1]);
        boolean claimed = data.length >= 3 ? Boolean.parseBoolean(data[2]) : complete; // Set to complete because it was auto claimed
        read(name, tier.getId(), amount, complete, claimed);
    }

    private void read(String name, int tier, int amount, boolean complete, boolean claimed) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getTier().getId() == tier) {
                if (achievement.name().toLowerCase().equals(name)) {
                    this.setComplete(tier, achievement.getId(), complete);
                    this.setAmountRemaining(tier, achievement.getId(), amount);
                    this.setClaimed(tier, achievement.getId(), claimed);
                    break;
                }
            }
        }
    }

    public void kill(NPC npc) {
        String name = npc.getNpcStats().getName();
        if (name == null || name.length() <= 0) {
            return;
        } else {
            name = name.toLowerCase().replaceAll("_", " ");
        }
        Achievements.increase(player, AchievementType.SLAY_ANY_NPCS, 1);
        if ((name.contains("dragon") || name.contains("vorkath")) && !name.contains("baby"))
            Achievements.increase(player, AchievementType.SLAY_DRAGONS, 1);
        List<String> checked = new ArrayList<>();
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (!achievement.getType().name().toLowerCase().contains("kill"))
                continue;
            if (achievement.getType().name().toLowerCase().replaceAll("_", " ").replaceAll("kill ", "").equalsIgnoreCase(name)) {
                if (checked.contains(achievement.getType().name().toLowerCase().replaceAll("_", " ").replaceAll("kill ", "")))
                    continue;
                Achievements.increase(player, achievement.getType(), 1);
                checked.add(achievement.getType().name().toLowerCase().replaceAll("_", " ").replaceAll("kill ", ""));
            }
        }
    }

    public boolean hasCompletedAll() {
        int amount = 0;
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (isComplete(achievement.getTier().getId(), achievement.getId()))
                amount++;
        }
        return amount == Achievements.getMaximumAchievements();
    }

    public boolean hasCompletedHalf() {
        int amount = 0;
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (isComplete(achievement.getTier().getId(), achievement.getId()))
                amount++;
        }
        return amount >= (Achievements.getMaximumAchievements()/2);
    }

    public boolean hasCompletedSeventyFivePercent() {
        int amount = 0;
        int totalAchievements = Achievements.getMaximumAchievements();
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (isComplete(achievement.getTier().getId(), achievement.getId())) {
                amount++;
            }
        }
        return amount >= (totalAchievements * 0.75);
    }

    public void resetAll() {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            setClaimed(achievement.getTier().getId(), achievement.getId(), false);
            setComplete(achievement.getTier().getId(), achievement.getId(), false);
            setAmountRemaining(achievement, 0);
        }
    }

    public boolean clickButton(int buttonId) {
        if (buttonId >= 100_005 && buttonId <= 101_199) {
            if (player.achievementPage == 0) {
                int index = (buttonId - 100_005) / 6;

                List<Achievement> achievements = Arrays.stream(Achievement.values()).filter(a -> a.getTier() == AchievementTier.TIER_1 || a.getTier() == AchievementTier.STARTER).collect(Collectors.toList());
                if (isClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                    player.sendMessage(Misc.colorWrap(COLOR, "You've already claimed this achievement!"));
                    return true;
                }

                if (!isComplete(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                    int amountRequired = achievements.get(index).getAmount();
                    if (getAmountRemaining(achievements.get(index)) >= amountRequired) {
                        setComplete(achievements.get(index), true);
                    } else {
                        player.sendMessage(Misc.colorWrap(COLOR, "You haven't completed this achievement yet!"));
                        return true;
                    }
                }

                Pass.addExperience(player, 1);
                Achievements.addReward(player, achievements.get(index));
                setClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId(), true);
                AchieveV2.Update(player, player.achievementPage);
                player.sendMessage("<col=" + COLOR + ">Claimed the " + achievements.get(index).getTier().getName().toLowerCase()
                        + " achievement '" + achievements.get(index).getFormattedName() + "'.</col>");
                Discord.jda.getTextChannelById(1227065946469044244L).sendMessage(player.getDisplayName() + " has just completed " + achievements.get(index).getFormattedName()).queue();
                Server.getLogging().write(new ClaimAchievementLog(player, achievements.get(index)));
                return true;
            } else if (player.achievementPage == 1) {
                int index = (buttonId - 100_005) / 6;

                List<Achievement> achievements = Arrays.stream(Achievement.values()).filter(a -> a.getTier() == AchievementTier.TIER_2).collect(Collectors.toList());
                if (isClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                    player.sendMessage(Misc.colorWrap(COLOR, "You've already claimed this achievement!"));
                    return true;
                }
                if (!isComplete(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                    int amountRequired = achievements.get(index).getAmount();

                    if (getAmountRemaining(achievements.get(index)) >= amountRequired) {
                        setComplete(achievements.get(index), true);
                    } else {
                        player.sendMessage(Misc.colorWrap(COLOR, "You haven't completed this achievement yet!"));
                        return true;
                    }
                }

                Pass.addExperience(player, 2);
                Achievements.addReward(player, achievements.get(index));
                setClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId(), true);
                AchieveV2.Update(player, player.achievementPage);
                player.sendMessage("<col=" + COLOR + ">Claimed the " + achievements.get(index).getTier().getName().toLowerCase()
                        + " achievement '" + achievements.get(index).getFormattedName() + "'.</col>");
                if (!Configuration.DISABLE_DISCORD_MESSAGING) {
                    Discord.jda.getTextChannelById(1227065946469044244L).sendMessage(player.getDisplayName() + " has just completed " + achievements.get(index).getFormattedName()).queue();
                }
                Server.getLogging().write(new ClaimAchievementLog(player, achievements.get(index)));
                return true;
            } else if (player.achievementPage == 2) {
                int index = (buttonId - 100_005) / 6;

                List<Achievement> achievements = Arrays.stream(Achievement.values()).filter(a -> a.getTier() == AchievementTier.TIER_3).collect(Collectors.toList());
                if (isClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                    player.sendMessage(Misc.colorWrap(COLOR, "You've already claimed this achievement!"));
                    return true;
                }
                if (!isComplete(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                    int amountRequired = achievements.get(index).getAmount();

                    if (getAmountRemaining(achievements.get(index)) >= amountRequired) {
                        setComplete(achievements.get(index), true);
                    } else {
                        player.sendMessage(Misc.colorWrap(COLOR, "You haven't completed this achievement yet!"));
                        return true;
                    }
                }

                Pass.addExperience(player, 3);
                Achievements.addReward(player, achievements.get(index));
                setClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId(), true);
                AchieveV2.Update(player, player.achievementPage);
                player.sendMessage("<col=" + COLOR + ">Claimed the " + achievements.get(index).getTier().getName().toLowerCase()
                        + " achievement '" + achievements.get(index).getFormattedName() + "'.</col>");
                if (!Configuration.DISABLE_DISCORD_MESSAGING) {
                    Discord.jda.getTextChannelById(1227065946469044244L).sendMessage(player.getDisplayName() + " has just completed " + achievements.get(index).getFormattedName()).queue();
                }
                Server.getLogging().write(new ClaimAchievementLog(player, achievements.get(index)));
                return true;
            } else if (player.achievementPage == 3) {
                int index = (buttonId - 100_005) / 6;

                List<Achievement> achievements = Arrays.stream(Achievement.values()).filter(a -> a.getTier() == AchievementTier.TIER_4).collect(Collectors.toList());
                if (isClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                    player.sendMessage(Misc.colorWrap(COLOR, "You've already claimed this achievement!"));
                    return true;
                }
                if (!isComplete(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                    int amountRequired = achievements.get(index).getAmount();
                    if (getAmountRemaining(achievements.get(index)) >= amountRequired) {
                        setComplete(achievements.get(index), true);
                    } else {
                        player.sendMessage(Misc.colorWrap(COLOR, "You haven't completed this achievement yet!"));
                        return true;
                    }
                }

                Pass.addExperience(player, 4);
                Achievements.addReward(player, achievements.get(index));
                setClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId(), true);
                AchieveV2.Update(player, player.achievementPage);
                player.sendMessage("<col=" + COLOR + ">Claimed the " + achievements.get(index).getTier().getName().toLowerCase()
                        + " achievement '" + achievements.get(index).getFormattedName() + "'.</col>");
                if (!Configuration.DISABLE_DISCORD_MESSAGING) {
                    Discord.jda.getTextChannelById(1227065946469044244L).sendMessage(player.getDisplayName() + " has just completed " + achievements.get(index).getFormattedName()).queue();
                }
                Server.getLogging().write(new ClaimAchievementLog(player, achievements.get(index)));
                return true;
            }
        }
        return false;
    }

    public boolean claimAll(int buttonID, Player c) {
        if (buttonID == 54765) {
            List<Achievement> achievements = Arrays.stream(Achievement.values()).filter(a -> !isClaimed(a.getTier().getId(), a.getId()) && isComplete(a.getTier().getId(), a.getId())).collect(Collectors.toList());
            for (Achievement achievement : achievements) {
                Achievements.addReward(c, achievement);
                setClaimed(achievement.getTier().getId(), achievement.getId(), true);
                c.sendMessage("<col=" + COLOR + ">Claimed the " + achievement.getTier().getName().toLowerCase()
                        + " achievement '" + achievement.getFormattedName() + "'.</col>");
                if (!Configuration.DISABLE_DISCORD_MESSAGING) {
                    Discord.jda.getTextChannelById(1227065946469044244L).sendMessage(player.getDisplayName() + " has just completed " + achievement.getFormattedName()).queue();
                }

                Server.getLogging().write(new ClaimAchievementLog(c, achievement));
            }
            AchieveV2.Update(player, player.achievementPage);
            return true;
        }
        return false;
    }

    public boolean isComplete(Achievement achievement) {
        return isComplete(achievement.getTier().getId(), achievement.getId());
    }

    public boolean isComplete(int tier, int index) {
        return completed[tier][index];
    }

    public boolean setComplete(Achievement achievement, boolean state) {
        return setComplete(achievement.getTier().getId(), achievement.getId(), state);
    }

    public boolean setComplete(int tier, int index, boolean state) {
        return this.completed[tier][index] = state;
    }

    public int getAmountRemaining(Achievement achievement) {
        return getAmountRemaining(achievement.getTier().getId(), achievement.getId());
    }

    public int getAmountRemaining(int tier, int index) {
        return amountRemaining[tier][index];
    }

    public void setAmountRemaining(Achievement achievement, int amountRemaining) {
        setAmountRemaining(achievement.getTier().getId(), achievement.getId(), amountRemaining);
    }

    public void setAmountRemaining(int tier, int index, int amountRemaining) {
        this.amountRemaining[tier][index] = amountRemaining;
    }

    public boolean isClaimed(Achievement achievement) {
        return isClaimed(achievement.getTier().getId(), achievement.getId());
    }

    public boolean isClaimed(int tier, int index) {
        return claimed[tier][index];
    }

    public boolean setClaimed(Achievement achievement, boolean state) {
        return setClaimed(achievement.getTier().getId(), achievement.getId(), state);
    }

    public boolean setClaimed(int tier, int index, boolean state) {
        return this.claimed[tier][index] = state;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isFirstAchievementLoginJune2021() {
        return firstAchievementLoginJune2021;
    }

    public void setFirstAchievementLoginJune2021(boolean firstAchievementLoginJune2021) {
        this.firstAchievementLoginJune2021 = firstAchievementLoginJune2021;
    }
}
