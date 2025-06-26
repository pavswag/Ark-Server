package io.kyros.content.itemskeptondeath.modifiers;

import io.kyros.content.items.OrnamentedItem;
import io.kyros.content.itemskeptondeath.DeathItemModifier;
import io.kyros.model.Items;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrnamentedDeathItem implements DeathItemModifier {

    private static final Set<Integer> ALL;

    static {
        ALL = new HashSet<>();

        // Add all ornamented items except slayer helmets
        Arrays.stream(OrnamentedItem.values())
                .filter(it -> it.getStandardItem() != Items.SLAYER_HELMET && it.getStandardItem() != Items.SLAYER_HELMET_I)
                .forEach(it -> ALL.add(it.getOrnamentedItem()));
    }

    @Override
    public Set<Integer> getItemIds() {
        return ALL;
    }

    @Override
    public void modify(Player player, GameItem gameItem, boolean kept, List<GameItem> keptItems, List<GameItem> lostItems) {
        if (kept)
            return;
        OrnamentedItem ornamentedItem = OrnamentedItem.forOrnamentedItem(gameItem.getId());
        if (ornamentedItem == null)
            return;

        lostItems.remove(gameItem);
        lostItems.add(new GameItem(ornamentedItem.getOrnamentKitItem()));
        lostItems.add(new GameItem(ornamentedItem.getStandardItem()));
    }
}
