package io.kyros.content.item.lootable.impl;

import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.MysteryBoxLootable;
import io.kyros.content.item.lootable.MysteryBoxLootableNew;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class f2pDivisionBox  extends MysteryBoxLootableNew {
    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their LootRarity.
     */
        @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

    private static Map<LootRarity, Integer> rates = new HashMap<>();

public static void loadItems() {
        if (!items.isEmpty())
            items.clear();
        if (!rates.isEmpty())
            rates.clear();
        YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("f2pDivisionBox");
items = loadedBox.getItems();
rates = loadedBox.getRates();
    }

    /**
     * Constructs a new myster box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public f2pDivisionBox(Player player) {
        super(player);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public Map<LootRarity, Integer> getRates() {
        return rates;
    }

    @Override
    public int getItemId() {
        return 6831;
    }
}
