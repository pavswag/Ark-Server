package io.kyros.util.logging.player;

import java.util.Set;

import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.util.logging.PlayerLog;

public class ItemDestroyedLog extends PlayerLog {

    private final GameItem gameItem;
    private final Position position;

    public ItemDestroyedLog(Player player, GameItem gameItem, Position position) {
        super(player);
        this.gameItem = gameItem;
        this.position = position;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("item_lost_destroyed", "item_lost");
    }

    @Override
    public String getLoggedMessage() {
        return "Destroyed " + gameItem + " at " + position;
    }
}
