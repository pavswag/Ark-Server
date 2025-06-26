package io.kyros.content.item.lootable.other;

import com.google.common.collect.Lists;
import io.kyros.content.item.lootable.ItemLootable;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.model.Items;
import io.kyros.model.items.GameItem;

import java.util.List;
import java.util.Map;

public class ResourceBoxSmall extends ItemLootable {

    public static final int BOX_ITEM = 30_000;

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return Map.of(LootRarity.COMMON, Lists.newArrayList(
                new GameItem(200, 1),  //grimy guam
                new GameItem(202, 1),  //grimy marrentill
                new GameItem(204, 1),  //grimy tarromin
                new GameItem(206, 1),  //grimy harralander
                new GameItem(208, 1),  //grimy ranarr weed
                new GameItem(441, 1),   //iron ore
                new GameItem(454, 1),   //coal
                new GameItem(448, 1),   //mithril ore
                new GameItem(2350, 1),  //bronze bar
                new GameItem(2352, 1),  //iron bar
                new GameItem(2354, 1),  //steel bar
                new GameItem(2360, 1),  //mithril bar
                new GameItem(1512, 1),  //logs
                new GameItem(1522, 1),  //oak logs
                new GameItem(1520, 1),  //willow logs
                new GameItem(1518, 1),  //maple logs
                new GameItem(1624, 1),  //uncut sapphire
                new GameItem(1622, 1),  //uncut emerald
                new GameItem(360, 1),   //raw tuna
                new GameItem(378, 1),   //raw lobster
                new GameItem(364, 1),   //raw bass
                new GameItem(372, 1),   //raw swordfish
                new GameItem(Items.LIMPWURT_ROOT_NOTED, 3),
                new GameItem(Items.RED_SPIDERS_EGGS_NOTED, 5),


                new GameItem(200, 2),  //grimy guam
                new GameItem(202, 2),  //grimy marrentill
                new GameItem(204, 2),  //grimy tarromin
                new GameItem(206, 2),  //grimy harralander
                new GameItem(208, 2),  //grimy ranarr weed
                new GameItem(441, 2),   //iron ore
                new GameItem(454, 2),   //coal
                new GameItem(448, 2),   //mithril ore
                new GameItem(2350, 2),  //bronze bar
                new GameItem(2352, 2),  //iron bar
                new GameItem(2354, 2),  //steel bar
                new GameItem(2360, 2),  //mithril bar
                new GameItem(1512, 2),  //logs
                new GameItem(1522, 2),  //oak logs
                new GameItem(1520, 2),  //willow logs
                new GameItem(1518, 2),  //maple logs
                new GameItem(1624, 2),  //uncut sapphire
                new GameItem(1622, 2),  //uncut emerald
                new GameItem(360, 2),   //raw tuna
                new GameItem(378, 2),   //raw lobster
                new GameItem(364, 2),   //raw bass
                new GameItem(372, 2),   //raw swordfish
                new GameItem(Items.LIMPWURT_ROOT_NOTED, 6),
                new GameItem(Items.RED_SPIDERS_EGGS_NOTED, 6)
        ));
    }

    @Override
    public int getLootableItem() {
        return BOX_ITEM;
    }

    @Override
    public int getRollCount() {
        return 3;
    }
}
