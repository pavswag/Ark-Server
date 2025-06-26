package io.kyros.content.item.lootable.other;

import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AOEChest implements Lootable {

    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

public static void loadItems() {
        if (!items.isEmpty())
            items.clear();

        YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("AOEChest");
    items = loadedBox.getItems();
    }
    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    private static GameItem randomChestRewards() {
        int rng = Misc.random(1000);
        List<GameItem> itemList = (rng > 950 ? getItems().get(LootRarity.RARE) : getItems().get(LootRarity.COMMON));
        return Misc.getRandomItem(itemList);
    }
    private static final int KEY = 13302;
    private static final int ANIMATION = 881;
    //Object ID = 43486;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 18; i++) {
            int pointz = 0;
            for (int ii = 0; ii < 32; ii++) {
                int rng = Misc.trueRand(1);
                int points = Misc.random(500, 2000);
                if (rng == 1) {
                    points /= 2;
                }
                pointz += points;
            }
            System.out.println(pointz);
        }
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return getItems();
    }

    @Override
    public void roll(Player c) {
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem reward = randomChestRewards();

            for (GameItem gameItem : getItems().get(LootRarity.RARE)) {
                if (gameItem.getId() == reward.getId()) {
                    PlayerHandler.executeGlobalMessage("@bla@[<col=7f0000>WILDY KEY@bla@] <col=990000>" + c.getDisplayName() + "@bla@ has just received a <col=990000>" + reward.getDef().getName() + ".");
                    break;
                }
            }
            c.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());

        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
