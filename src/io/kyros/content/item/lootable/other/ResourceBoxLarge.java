package io.kyros.content.item.lootable.other;

import com.google.common.collect.Lists;
import io.kyros.content.item.lootable.ItemLootable;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.model.Items;
import io.kyros.model.items.GameItem;

import java.util.List;
import java.util.Map;

public class ResourceBoxLarge extends ItemLootable {

    public static final int BOX_ITEM = 30_002;

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return Map.of(LootRarity.COMMON, Lists.newArrayList(
                new GameItem(3050, 5), //grimy lantadyme
                new GameItem(212, 5),  //grimy dwarf weed
                new GameItem(214, 5),  //grimy torstol

                new GameItem(454, 7),  //coal
                new GameItem(448, 7),  //mithril ore
                new GameItem(450, 7),  //adamantite ore
                new GameItem(452, 7),  //runite ore

                new GameItem(2360, 2),  //mithril bar
                new GameItem(2362, 2),  //adamantite bar
                new GameItem(2364, 2),  //runite bar

                new GameItem(1516, 7), //yew logs
                new GameItem(1514, 7), //magic logs
                new GameItem(19670, 7),//redwood logs

                new GameItem(1620, 5), //uncut ruby
                new GameItem(1618, 5), //uncut diamond
                new GameItem(1632, 5), //uncut dragonstone

                new GameItem(7945, 7), //raw monkfish
                new GameItem(3143, 7), //raw karamwban
                new GameItem(384, 7),  //raw shark
                new GameItem(390, 7),  //raw manta ray

                new GameItem(3050, 10), //grimy lantadyme
                new GameItem(212, 10),  //grimy dwarf weed
                new GameItem(214, 10),  //grimy torstol

                new GameItem(454, 14),  //coal
                new GameItem(448, 14),  //mithril ore
                new GameItem(450, 14),  //adamantite ore
                new GameItem(452, 14),  //runite ore

                new GameItem(2360, 4),  //mithril bar
                new GameItem(2362, 4),  //adamantite bar
                new GameItem(2364, 4),  //runite bar

                new GameItem(1516, 14), //yew logs
                new GameItem(1514, 14), //magic logs
                new GameItem(19670, 14),//redwood logs

                new GameItem(1620, 10), //uncut ruby
                new GameItem(1618, 10), //uncut diamond
                new GameItem(1632, 10), //uncut dragonstone

                new GameItem(7945, 14), //raw monkfish
                new GameItem(3143, 14), //raw karamwban
                new GameItem(384, 14),  //raw shark
                new GameItem(390, 14),  //raw manta ray

                new GameItem(Items.LIMPWURT_ROOT_NOTED, 5),
                new GameItem(Items.LIMPWURT_ROOT_NOTED, 10),
                new GameItem(Items.LIMPWURT_ROOT_NOTED, 15),
                new GameItem(Items.RED_SPIDERS_EGGS_NOTED, 10),
                new GameItem(Items.RED_SPIDERS_EGGS_NOTED, 20),
                new GameItem(Items.RED_SPIDERS_EGGS_NOTED, 30),
                new GameItem(Items.MORT_MYRE_FUNGUS_NOTED, 10),
                new GameItem(Items.MORT_MYRE_FUNGUS_NOTED, 20),
                new GameItem(Items.MORT_MYRE_FUNGUS_NOTED, 30),
                new GameItem(Items.CRUSHED_NEST_NOTED, 2),
                new GameItem(Items.CRUSHED_NEST_NOTED, 4),
                new GameItem(Items.CRUSHED_NEST_NOTED, 6)
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
