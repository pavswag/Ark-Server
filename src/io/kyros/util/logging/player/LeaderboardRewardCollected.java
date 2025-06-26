package io.kyros.util.logging.player;

import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.logging.PlayerLog;

import java.util.Set;

public class LeaderboardRewardCollected extends PlayerLog {

    private final GameItem gameItem;

    public LeaderboardRewardCollected(Player player, GameItem gameItem) {
        super(player);
        this.gameItem = gameItem;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("leaderboard_rewards");
    }

    @Override
    public String getLoggedMessage() {
        return "Collected " + gameItem;
    }
}
