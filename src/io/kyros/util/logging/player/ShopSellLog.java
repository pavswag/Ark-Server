package io.kyros.util.logging.player;

import java.util.Set;

import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.logging.PlayerLog;

public class ShopSellLog extends PlayerLog {

    private final int shopId;
    private final String shopName;
    private final GameItem gameItem;

    public ShopSellLog(Player player, int shopId, String shopName, GameItem gameItem) {
        super(player);
        this.shopId = shopId;
        this.shopName = shopName;
        this.gameItem = gameItem;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("items_received_shop", "items_received");
    }

    @Override
    public String getLoggedMessage() {
        return "Sold " + gameItem + " from " + shopName + " with id " + shopId;
    }
}
