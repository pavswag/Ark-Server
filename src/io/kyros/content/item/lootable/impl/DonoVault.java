package io.kyros.content.item.lootable.impl;

import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import lombok.Getter;

import java.util.*;

public class DonoVault implements Lootable {
    @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

    private static Map<LootRarity, Integer> rates = new HashMap<>();

public static void loadItems() {
        if (!items.isEmpty())
            items.clear();
        if (!rates.isEmpty())
            rates.clear();

        YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("DonoVault");
items = loadedBox.getItems();
rates = loadedBox.getRates();
    }

    public static ArrayList<GameItem> getRareDrops() {
    ArrayList<GameItem> drops = new ArrayList<>();
    List<GameItem> found = items.get(LootRarity.RARE);
    for(GameItem f : found) {
        boolean foundItem = false;
        for(GameItem drop : drops) {
            if (drop.getId() == f.getId()) {
                foundItem = true;
                break;
            }
        }
        if (!foundItem) {
            drops.add(f);
        }
    }
    return drops;
    }



    public static ArrayList<GameItem> getRare() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if(drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if(!foundItem) {
                drops.add(f);
            }
        }
        return drops;
    }

    public static ArrayList<GameItem> getAllDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        items.forEach((lootRarity, gameItems) -> {
            gameItems.forEach(g -> {
                if (!drops.contains(g)) {
                    drops.add(g);
                }
            });
        });
        return drops;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() { return getItems(); }

    @Override
    public void roll(Player player) {

    }
}