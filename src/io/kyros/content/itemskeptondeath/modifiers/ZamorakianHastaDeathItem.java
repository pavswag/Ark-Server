package io.kyros.content.itemskeptondeath.modifiers;

import io.kyros.content.itemskeptondeath.DeathItemModifier;
import io.kyros.model.Items;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;

import java.util.List;
import java.util.Set;

public class ZamorakianHastaDeathItem implements DeathItemModifier {
    @Override
    public Set<Integer> getItemIds() {
        return Set.of(Items.ZAMORAKIAN_HASTA);
    }

    @Override
    public void modify(Player player, GameItem gameItem, boolean kept, List<GameItem> keptItems, List<GameItem> lostItems) {
        if (kept)
            return;
        lostItems.remove(gameItem);
        lostItems.add(new GameItem(Items.ZAMORAKIAN_SPEAR));
    }
}
