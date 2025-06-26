package io.kyros.util.logging.player;

import java.util.List;
import java.util.Set;

import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.logging.PlayerLog;

public class ItemTradeLog extends PlayerLog {

    private final String tradedWithLoginName;
    private final List<GameItem> received;
    private final List<GameItem> given;

    public ItemTradeLog(Player player, String tradedWithLoginName, List<GameItem> received, List<GameItem> given) {
        super(player);
        this.tradedWithLoginName = tradedWithLoginName;
        this.received = received;
        this.given = given;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("items_received_trade", "items_received");
    }

    @Override
    public String getLoggedMessage() {
        return String.format("Traded with [%s] (login name) Received [%s] Given [%s]", tradedWithLoginName, received, given);
    }
}
