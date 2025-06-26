package io.kyros.content.achievement.inter;

import io.kyros.Server;
import io.kyros.content.achievement.AchievementTier;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.bosses.nightmare.NightmareConstants;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

public class AchieveV2 {

    public static void Open(Player c) {
        if (c.isBoundaryRestricted()) {
            return;
        }
        Update(c, c.achievementPage);
        c.getPA().sendChangeSprite(54786, (byte) 1);
        c.getPA().showInterface(54760);
    }

    public static void reset(Player player) {
        player.getPA().runClientScript(3350);
        player.getPA().resetScrollBar(54795);
    }

    public static void Update(Player player, int page) {
        reset(player);
        int name = 100_002;
        int progress = 100_003;
        int rewards = 100_004;
        int complete = 54764;
        int totalA = 54763;
        int slot = 0;
        int total = 1;
        int amount = 0;
        int counter = 0;
        for (Achievements.Achievement value : Achievements.Achievement.values()) {
            if (value.getTier() == AchievementTier.STARTER && page == 0 || value.getTier() == AchievementTier.TIER_1 && page == 0) {
                int maxProgress = value.getAmount();
                int currentProgress = player.getAchievements().getAmountRemaining(value.getTier().getId(), value.getId());

                player.getPA().sendString(name, value.getDescription());
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @gre@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && !player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @cya@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else {
                    player.getPA().sendString(progress, "Current Progress: @red@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                }
                for (GameItem reward : value.getRewards()) {
                    player.getPA().itemOnInterface(reward.getId(),reward.getAmount(), rewards, slot++);
                }
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                    amount++;
                }

                player.getPA().sendString(complete, "Achievements completed: @whi@" + amount + "/" + total);
                name+=6;
                progress+=6;
                rewards+=6;
                slot = 0;
                total++;
            } else if (value.getTier() == AchievementTier.TIER_2 && page == 1) {
                int maxProgress = value.getAmount();
                int currentProgress = player.getAchievements().getAmountRemaining(value.getTier().getId(), value.getId());

                player.getPA().sendString(name, value.getDescription());
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @gre@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && !player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @cya@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else {
                    player.getPA().sendString(progress, "Current Progress: @red@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                }
                for (GameItem reward : value.getRewards()) {
                    player.getPA().itemOnInterface(reward.getId(),reward.getAmount(), rewards, slot++);
                }
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                    amount++;
                }

                player.getPA().sendString(complete, "Achievements completed: @whi@" + amount + "/" + total);
                name+=6;
                progress+=6;
                rewards+=6;
                slot = 0;
                total++;
            } else if (value.getTier() == AchievementTier.TIER_3 && page == 2) {
                int maxProgress = value.getAmount();
                int currentProgress = player.getAchievements().getAmountRemaining(value.getTier().getId(), value.getId());

                player.getPA().sendString(name, value.getDescription());
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @gre@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && !player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @cya@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else {
                    player.getPA().sendString(progress, "Current Progress: @red@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                }
                for (GameItem reward : value.getRewards()) {
                    player.getPA().itemOnInterface(reward.getId(),reward.getAmount(), rewards, slot++);
                }
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                    amount++;
                }

                player.getPA().sendString(complete, "Achievements completed: @whi@" + amount + "/" + total);
                name+=6;
                progress+=6;
                rewards+=6;
                slot = 0;
                total++;
            } else if (value.getTier() == AchievementTier.TIER_4 && page == 3) {
                int maxProgress = value.getAmount();
                int currentProgress = player.getAchievements().getAmountRemaining(value.getTier().getId(), value.getId());

                player.getPA().sendString(name, value.getDescription());
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @gre@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && !player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @cya@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else {
                    player.getPA().sendString(progress, "Current Progress: @red@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                }

                for (GameItem reward : value.getRewards()) {
                    player.getPA().itemOnInterface(reward.getId(),reward.getAmount(), rewards, slot++);
                }
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                    amount++;
                }

                player.getPA().sendString(complete, "Achievements completed: @whi@" + amount + "/" + total);
                name+=6;
                progress+=6;
                rewards+=6;
                slot = 0;
                total++;
            }
            if (player.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                counter++;
            }
            player.getPA().setScrollableMaxHeight(54795, (total - 1) * 36);
            player.getPA().sendString(totalA, "Achievement Diary @whi@(" + counter+ "/" + Achievements.Achievement.values().length + ")");
        }

        player.getPA().showInterface(54760);
    }

    public static boolean ButtonHandler(Player player, int buttonId) {
        if (buttonId == 54786) {
            Update(player, 0);
            player.achievementPage = 0;
            return true;
        }

        if (buttonId == 54787) {
            Update(player, 1);
            player.achievementPage = 1;
            return true;
        }

        if (buttonId == 54788) {
            Update(player, 2);
            player.achievementPage = 2;
            return true;
        }

        if (buttonId == 54789) {
            Update(player, 3);
            player.achievementPage = 3;
            return true;
        }

        return false;
    }

}
