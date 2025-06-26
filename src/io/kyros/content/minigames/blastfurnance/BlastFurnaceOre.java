package io.kyros.content.minigames.blastfurnance;

import io.kyros.model.Items;
import lombok.Getter;

@Getter
public enum BlastFurnaceOre {
    TIN(Items.TIN_ORE, 1000),
    COPPER(Items.COPPER_ORE, 1000),
    IRON(Items.IRON_ORE, 1000),
    SILVER(Items.SILVER_ORE, 1000),
    COAL(Items.COAL, 1000),
    GOLD(Items.GOLD_ORE, 1000),
    MITHRIL(Items.MITHRIL_ORE, 1000),
    ADAMANTITE(Items.ADAMANTITE_ORE, 1000),
    RUNITE(Items.RUNITE_ORE, 1000);

    private final int oreId;
    private final int capacity;

    BlastFurnaceOre(int oreId) {
        this(oreId, 28);
    }

    BlastFurnaceOre(int oreId, int capacity) {
        this.oreId = oreId;
        this.capacity = capacity;
    }

    public static BlastFurnaceOre forId(int oreId) {
        for (var ore : values()) {
            if (ore.getOreId() == oreId) {
                return ore;
            }
        }
        return null;
    }

    public String getName(){
        return name().replace(" Ore", "");
    }
}

