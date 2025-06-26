package io.kyros.util.logging.player;

import java.util.Set;

import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import io.kyros.util.logging.PlayerLog;

public class TradingPostBuyLog extends PlayerLog {

    private final GameItem bought;
    private final int price;
    private final String boughtFrom;

    public TradingPostBuyLog(Player username, GameItem bought, int price, String boughtFrom) {
        super(username);
        this.bought = bought;
        this.price = price;
        this.boughtFrom = boughtFrom;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("items_received_trading_post", "items_received");
    }

    @Override
    public String getLoggedMessage() {
        return "Bought item " + bought + " from " + boughtFrom + " for " + Misc.formatCoins(price) + " coins";
    }
}
