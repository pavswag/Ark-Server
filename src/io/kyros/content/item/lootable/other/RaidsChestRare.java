package io.kyros.content.item.lootable.other;

import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.impl.RaidsChestItems;
import io.kyros.content.minigames.raids.Raids;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaidsChestRare implements Lootable {

    private static final int KEY = Raids.RARE_KEY;
    private static final int ANIMATION = 881;

    public static void main(String[] args) throws Exception {
        ItemDef.load();
        HashMap<GameItem, Integer> map = new HashMap<>();

        for (int i = 0; i < 10000; i++) {
            GameItem gameItem = randomChestRewards();
            int amount = map.getOrDefault(gameItem, 0);
            map.put(gameItem, amount + 1);
        }

        map.forEach((gameItem, amount) -> {
            String dropChance = String.format("%.2f", ((double) amount / 10000) * 100);
            String itemName = ItemDef.forId(gameItem.getId()).getName();
            System.out.println("Rolled a " + itemName + " " + amount + " times. " + dropChance);
        });

    }

    public static GameItem randomChestRewards() {
        List<GameItem> itemList = RaidsChestItems.getItems().get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    public static ArrayList<GameItem> getRareDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = RaidsChestItems.getItems().get(LootRarity.RARE);
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

    public static ArrayList<GameItem> getAllRaidsDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        RaidsChestItems.getItems().forEach((lootRarity, gameItems) -> {
            gameItems.forEach(g -> {
                if (!drops.contains(g)) {
                    drops.add(g);
                }
            });
        });
        return drops;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return RaidsChestItems.getItems();
    }

    @Override
    public void roll(Player c) {
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem reward =  randomChestRewards();

            c.getCollectionLog().handleDrop(c, 7554, reward.getId(), reward.getAmount());
            if (reward.getId() == 20851 && c.getItems().getItemCount(20851, false) == 0) {
                c.getCollectionLog().handleDrop(c, 5, 20851, 1);
            }
            c.getItems().addItem(reward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount())); //potentially gives the loot 3 times.
            c.sendMessage("@blu@You have received a rare item out of the storage unit.");
            if (reward.getId() == 20997 || reward.getId() == 20851) {
                NPCDeath.announceKc(c, reward, c.raidCount);
            }
        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
