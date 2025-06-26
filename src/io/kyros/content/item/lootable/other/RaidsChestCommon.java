package io.kyros.content.item.lootable.other;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.impl.RaidsChestItems;
import io.kyros.content.minigames.raids.Raids;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.items.GameItem;
import io.kyros.sql.logging.RareLootLog;
import io.kyros.util.Misc;

public class RaidsChestCommon implements Lootable {

    private static final int KEY = Raids.COMMON_KEY;
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
        List<GameItem> itemList = RaidsChestItems.getItems().get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return RaidsChestItems.getItems();
    }

    @Override
    public void roll(Player c) {
        int twistedhornsroll = Misc.random(120);
        if (twistedhornsroll == 1) {
            c.getItems().addItem(24466, 1);
            PlayerHandler.executeGlobalMessage("@bla@[@blu@RAIDS@bla@] "+ c.getDisplayName() +"@pur@ has just received twisted horns.");
        }
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem reward =  randomChestRewards();
            GameItem reward2 = randomChestRewards();
            GameItem reward3 = randomChestRewards();

            c.getItems().addItem(reward.getId(),  (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ?reward.getAmount() * 2:reward.getAmount())); //potentially gives the loot 3 times.
            c.getItems().addItem(reward2.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ?reward2.getAmount() * 2:reward2.getAmount())); //potentially gives the loot 3 times.
            c.getItems().addItem(reward3.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ?reward3.getAmount()* 2:reward3.getAmount())); //potentially gives the loot 3 times.
            c.sendMessage("@blu@You received a common item out of the storage unit.");
        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
