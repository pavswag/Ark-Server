package io.kyros.util.logging.player;

import io.kyros.content.minigames.tob.instance.TobInstance;
import io.kyros.model.entity.player.Player;
import io.kyros.util.logging.PlayerLog;

import java.util.Set;
import java.util.stream.Collectors;

public class DiedAtTobLog extends PlayerLog {

    private final TobInstance instance;

    public DiedAtTobLog(Player player, TobInstance instance) {
        super(player);
        this.instance = instance;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("died_at_tob");
    }

    @Override
    public String getLoggedMessage() {
        String players = "";
        if (instance != null) {
            players = instance.getPlayers().stream().map(Player::getLoginNameLower).collect(Collectors.joining(", "));
        }
        return "Died at tob with " + players;
    }
}
