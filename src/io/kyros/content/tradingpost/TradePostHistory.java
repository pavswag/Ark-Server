package io.kyros.content.tradingpost;

import io.kyros.model.items.GameItem;
import lombok.Getter;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 09/04/2024
 */
public class TradePostHistory {

    @Getter
    private final String buyer;
    @Getter
    private final String seller;
    @Getter
    private final GameItem item;
    @Getter
    private final long timestamp;
    @Getter
    private final boolean nomad;
    @Getter
    private final long cost;


    public TradePostHistory(String buyer, String seller, GameItem item, long timestamp, boolean nomad, long cost) {
        this.buyer = buyer;
        this.seller = seller;
        this.item = item;
        this.timestamp = timestamp;
        this.nomad = nomad;
        this.cost = cost;
    }
}
