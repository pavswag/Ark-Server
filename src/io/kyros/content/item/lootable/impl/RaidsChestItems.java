package io.kyros.content.item.lootable.impl;

import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.model.items.GameItem;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaidsChestItems {

    @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

    private static Map<LootRarity, Integer> rates = new HashMap<>();

public static void loadItems() {
        if (!items.isEmpty())
            items.clear();
        if (!rates.isEmpty())
            rates.clear();

        YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("RaidsChestItems");
items = loadedBox.getItems();
rates = loadedBox.getRates();
    }


}
