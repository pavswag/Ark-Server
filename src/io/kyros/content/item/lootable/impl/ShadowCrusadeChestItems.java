package io.kyros.content.item.lootable.impl;

import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class ShadowCrusadeChestItems implements Lootable {

    @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

    private static Map<LootRarity, Integer> rates = new HashMap<>();

public static void loadItems() {
        if (!items.isEmpty())
            items.clear();
        if (!rates.isEmpty())
            rates.clear();

        YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("ShadowCrusadeChestItems");
items = loadedBox.getItems();
rates = loadedBox.getRates();
    }

    public static List<GameItem> getRareDrops() {
        return getUniqueItemsByRarity(LootRarity.RARE);
    }

    private static List<GameItem> getUniqueItemsByRarity(LootRarity rarity) {
        List<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(rarity);
        if (found != null) {
            Set<Integer> itemIds = new HashSet<>();
            for (GameItem f : found) {
                if (itemIds.add(f.getId())) {
                    drops.add(f);
                }
            }
        }
        return drops;
    }

    public static List<GameItem> getAllDrops() {
        List<GameItem> drops = new ArrayList<>();
        Set<Integer> itemIds = new HashSet<>();
        items.forEach((lootRarity, gameItems) -> {
            for (GameItem g : gameItems) {
                if (itemIds.add(g.getId())) {
                    drops.add(g);
                }
            }
        });
        return drops;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return getItems();
    }

    @Override
    public void roll(Player player) {
        // Implement the roll logic here
    }
}
