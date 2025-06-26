package io.kyros.model.shops;

import io.kyros.model.items.NamedItem;
import io.kyros.util.ItemConstants;

public class NamedShopItem extends NamedItem {

    private int price;

    public ShopItem toShopItem(ItemConstants itemConstants) {
        return new ShopItem(getId(itemConstants), getAmount(), price);
    }

    public NamedShopItem() {
    }

    public int getPrice() {
        return price;
    }
}
