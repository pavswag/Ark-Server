package io.kyros.util.logging.player;

import io.kyros.content.achievement.Achievements;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;
import io.kyros.util.logging.PlayerLog;

import java.util.Set;

public class ClaimAchievementLog extends PlayerLog {

    private final Achievements.Achievement achievement;

    public ClaimAchievementLog(Player player, Achievements.Achievement achievement) {
        super(player);
        this.achievement = achievement;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("claimed_achievement");
    }

    @Override
    public String getLoggedMessage() {
        return Misc.replaceBracketsWithArguments("Claimed achievement {}", achievement);
    }
}
