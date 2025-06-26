package io.kyros.content.tradingpost;

import io.kyros.model.items.GameItem;
import lombok.Getter;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 05/04/2024
 */
public class TradePostOffer {

    @Getter
    private String username;
    @Getter
    private final GameItem item;
    @Getter
    private final long pricePerItem;
    @Getter
    private final long timestamp;
    @Getter
    private final boolean nomad;
    @Getter
    private final int totalSold;

    public TradePostOffer(String username, GameItem item, long pricePerItem, long timestamp, boolean nomad, int totalSold) {
        this.username = username;
        this.item = item;
        this.pricePerItem = pricePerItem;
        this.timestamp = timestamp;
        this.nomad = nomad;
        this.totalSold = totalSold;
    }

    public void setUsername(String displayName) {
        this.username = displayName;
    }
}
