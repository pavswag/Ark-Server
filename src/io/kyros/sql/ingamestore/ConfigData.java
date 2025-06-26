package io.kyros.sql.ingamestore;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 13/12/2023
 */
import lombok.Getter;

import java.util.List;

@Getter
public class ConfigData {
    private final List<Item> items;
    private final DiscountCode discountCode;

    public ConfigData(List<Item> items, DiscountCode discountCode) {
        this.items = items;
        this.discountCode = discountCode;
    }
}