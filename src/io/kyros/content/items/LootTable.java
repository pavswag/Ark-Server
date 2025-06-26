package io.kyros.content.items;

import com.google.common.collect.Lists;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 02/02/2024
 */
public class LootTable {
   public LootItem[] guaranteed;

   public List<ItemsTable> tables;

    public double totalWeight;


    /**
     * Methods used for creating tables @ runtime.
     */

    public LootTable guaranteedItems(LootItem... items) {
        guaranteed = items;
        return this;
    }
    public List<LootItem> getLootItems() {
        List<LootItem> items = Lists.newArrayList();
        for (ItemsTable table : tables) {
            items.addAll(Arrays.asList(table.items));
        }
        return items;
    }

    public LootTable addTable(int tableWeight, LootItem... tableItems) {
        return addTable(null, tableWeight, tableItems);
    }

    public LootTable addTable(String tableName, int tableWeight, LootItem... tableItems) {
        if(tables == null)
            tables = new ArrayList<>();
        tables.add(new ItemsTable(tableName, tableWeight, tableItems));
        totalWeight += tableWeight;
        return this;
    }

    /**
     * Methods pretty much specifically for npc drop tables.
     */

    public LootTable combine(LootTable table) {
        LootTable newTable = new LootTable();

        List<LootItem> newGuaranteed = new ArrayList<>();
        if(guaranteed != null)
            Collections.addAll(newGuaranteed, guaranteed);
        if(table.guaranteed != null)
            Collections.addAll(newGuaranteed, table.guaranteed);
        newTable.guaranteed = newGuaranteed.isEmpty() ? null : newGuaranteed.toArray(new LootItem[0]);

        List<ItemsTable> newTables = new ArrayList<>();
        if(tables != null)
            newTables.addAll(tables);
        if(table.tables != null)
            newTables.addAll(table.tables);
        newTable.tables = newTables.isEmpty() ? null : newTables;

        return newTable;
    }

    public void calculateWeight() {
        totalWeight = 0;
        if(tables != null) {
            for(ItemsTable table : tables) {
                totalWeight += table.weight;
                table.totalWeight = 0;
                if(table.items != null) {
                    for(LootItem item : table.items)
                        table.totalWeight += item.weight;
                }
            }
        }
    }

    /**
     * Item selection
     */

    public GameItem rollItem() {
        List<GameItem> items = rollItems(false);
        return items == null ? null : items.get(0);
    }

    public List<GameItem> rollItems(boolean allowGuaranteed) {
        List<GameItem> items;
        if(allowGuaranteed && guaranteed != null) {
            items = new ArrayList<>(guaranteed.length + 1);
            for(LootItem item : guaranteed)
                items.add(item.toItem());
        } else {
            items = new ArrayList<>(1);
        }
        if(tables != null) {
            double tableRand = Misc.get() * totalWeight;
            for(ItemsTable table : tables) {
                if((tableRand -= table.weight) <= 0) {
                    if(table.items != null) {
                        double itemsRand = Misc.get() * table.totalWeight;
                        for(LootItem item : table.items) {
                            if(item.weight == 0) {
                                /* weightless item landed, add it and continue loop */
                                items.add(item.toItem());
                                continue;
                            }
                            if((itemsRand -= item.weight) <= 0) {
                                /* weighted item landed, add it and break loop */
                                items.add(item.toItem());
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        return items.isEmpty() ? null : items;
    }


    /**
     * A table of items unique to this table type.
     */

    public static final class ItemsTable {

        public final String name;

        public final int weight;

        public final LootItem[] items;

        public double totalWeight;

        public int rollChance;

        public ItemsTable(String name, int weight, LootItem[] items) {
            this(name, weight, items, 0);
        }

        public ItemsTable(String name, int weight, LootItem[] items, int rollChance) {
            this.name = name;
            this.weight = weight;
            this.items = items;
            for(LootItem item : items) {
                totalWeight += item.weight;
                if(ItemDef.forId(item.id) == null)
                    System.err.println("!!@@@@@@@@@@@@@@@@@@@@@@: " + item.id);
            }
            this.rollChance = rollChance;
        }


    }
}
