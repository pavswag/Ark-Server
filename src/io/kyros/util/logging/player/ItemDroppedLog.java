package io.kyros.util.logging.player;

import java.util.Set;

import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.util.logging.PlayerLog;

public class ItemDroppedLog extends PlayerLog {

    private final GameItem gameItem;
    private final Position position;

    public ItemDroppedLog(Player player, GameItem gameItem, Position position) {
        super(player);
        this.gameItem = gameItem;
        this.position = position;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("item_lost_dropped", "item_lost");
    }

    @Override
    public String getLoggedMessage() {
        return "Dropped " + gameItem + " at " + position;
    }
}
