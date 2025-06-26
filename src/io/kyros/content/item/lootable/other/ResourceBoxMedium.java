package io.kyros.content.item.lootable.other;

import com.google.common.collect.Lists;
import io.kyros.content.item.lootable.ItemLootable;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.model.Items;
import io.kyros.model.items.GameItem;

import java.util.List;
import java.util.Map;

public class ResourceBoxMedium extends ItemLootable {

    public static final int BOX_ITEM = 30_001;

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return Map.of(LootRarity.COMMON, Lists.newArrayList(
                new GameItem(3050, 2), //grimy toadflax
                new GameItem(212, 2),  //grimy avantoe
                new GameItem(214, 2),  //grimy kwuarm
                new GameItem(3052, 2), //grimy snapdragon
                new GameItem(216, 2),  //grimy cadantine
                new GameItem(454, 5),   //coal
                new GameItem(448, 5),   //mithril ore
                new GameItem(450, 5),   //adamantite ore
                new GameItem(452, 5),   //runite ore
                new GameItem(2360, 1),  //mithril bar
                new GameItem(2362, 1),  //adamantite bar
                new GameItem(2364, 1),  //runite bar
                new GameItem(1518, 5),  //maple logs
                new GameItem(1516, 5),  //yew logs
                new GameItem(1514, 5),  //magic logs
                new GameItem(1620, 2),  //uncut ruby
                new GameItem(1618, 2),  //uncut diamond
                new GameItem(378, 5),   //raw lobster
                new GameItem(372, 5),   //raw swordfish
                new GameItem(7945, 5),  //raw monkfish
                new GameItem(3143, 5),  //raw karamwban

                new GameItem(3050, 4), //grimy toadflax
                new GameItem(212, 4),  //grimy avantoe
                new GameItem(214, 4),  //grimy kwuarm
                new GameItem(3052, 4), //grimy snapdragon
                new GameItem(216, 4),  //grimy cadantine
                new GameItem(454, 10),   //coal
                new GameItem(448, 10),   //mithril ore
                new GameItem(450, 10),   //adamantite ore
                new GameItem(452, 10),   //runite ore
                new GameItem(2360, 2),  //mithril bar
                new GameItem(2362, 2),  //adamantite bar
                new GameItem(2364, 2),  //runite bar
                new GameItem(1518, 10),  //maple logs
                new GameItem(1516, 10),  //yew logs
                new GameItem(1514, 10),  //magic logs
                new GameItem(1620, 4),  //uncut ruby
                new GameItem(1618, 4),  //uncut diamond
                new GameItem(378, 10),   //raw lobster
                new GameItem(372, 10),   //raw swordfish
                new GameItem(7945, 10),  //raw monkfish
                new GameItem(3143, 10),  //raw karamwban

                new GameItem(Items.LIMPWURT_ROOT_NOTED, 3),
                new GameItem(Items.LIMPWURT_ROOT_NOTED, 6),
                new GameItem(Items.RED_SPIDERS_EGGS_NOTED, 5),
                new GameItem(Items.RED_SPIDERS_EGGS_NOTED, 10),
                new GameItem(Items.MORT_MYRE_FUNGUS_NOTED, 5),
                new GameItem(Items.MORT_MYRE_FUNGUS_NOTED, 10),
                new GameItem(Items.CRUSHED_NEST_NOTED, 5),
                new GameItem(Items.CRUSHED_NEST_NOTED, 10)
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
