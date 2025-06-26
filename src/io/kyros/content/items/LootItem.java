package io.kyros.content.items;

import com.google.common.collect.Maps;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

import java.util.Map;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 02/02/2024
 */
public class LootItem {

    public final int id;

    public final int min, max;

    public final int weight;

    public Map<String, String> attributes;

    public LootItem(int id, int amount, int weight) {
        this(id, amount, amount, weight);
    }

    public LootItem(int id, int minAmount, int maxAmount, int weight) {
        this(id, minAmount, maxAmount, weight, null);
    }

    public LootItem(int id, int minAmount, int maxAmount, int weight, Map<String, String> attributes) {
        this.id = id;
        this.min = minAmount;
        this.max = maxAmount;
        this.weight = weight;
        if (attributes == null)
            this.attributes = Maps.newHashMap();
        else
            this.attributes = Maps.newHashMap(attributes);
    }

    public GameItem toItem() {
        GameItem item = new GameItem(id, min == max ? min : Misc.get(min, max));
        return item;
    }

    public String getName() {
        ItemDef def = ItemDef.forId(id);
        if(def.isNoted())
            return ItemDef.forId(def.getNotedItemIfAvailable()) + " (noted)";
        return def.getName();
    }

}
