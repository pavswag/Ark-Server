package io.kyros.content.item.lootable.impl;

import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ItemAssistant;
import io.kyros.util.Misc;

import java.util.*;

public class HerbBox implements Lootable {

    /**
     * The item id of the mystery box required to trigger the event
     */
    public static final int HerbBox = 11738;

    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
     */
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

public static void loadItems() {
        if (!items.isEmpty())
            items.clear();

        YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("HerbBox");
items = loadedBox.getItems();
    }

    public static CycleEvent getCycleEvent(final Player player) {
        return new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (player.isDisconnected() || Objects.isNull(player)) {
                    container.stop();
                    return;
                }

                int random = Misc.random(100);
                List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 80 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
                GameItem item = Misc.getRandomItem(itemList);
                GameItem itemDouble = Misc.getRandomItem(itemList);

                if (Misc.random(10) == 0) {
                    player.getItems().addItem(item.getId(), item.getAmount());
                    player.getItems().addItem(itemDouble.getId(), itemDouble.getAmount());
                    player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>.");
                    player.sendMessage("You receive <col=255>" + itemDouble.getAmount() + " x " + ItemAssistant.getItemName(itemDouble.getId()) + "</col>.");
                } else {
                    player.getItems().addItem(item.getId(), item.getAmount());
                    player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>.");
                }
                container.stop();
            }
        };
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    /**
     * Opens a mystery box if possible, and ultimately triggers and event, if possible.
     */
    public void roll(Player player) {
        if (System.currentTimeMillis() - player.lastMysteryBox < 600 * 4) {
            return;
        }
        if (player.getItems().freeSlots() < 2) {
            player.sendMessage("You need at least two free slots to open a herb box.");
            return;
        }
        if (!player.getItems().playerHasItem(HerbBox)) {
            player.sendMessage("You need a herb box to do this.");
            return;
        }
        player.getItems().deleteItem(HerbBox, 1);
        player.lastMysteryBox = System.currentTimeMillis();
        CycleEventHandler.getSingleton().stopEvents(this);
        CycleEventHandler.getSingleton().addEvent(this, getCycleEvent(player), 1);
    }
    public void openall(Player player) {
        int amount = player.getItems().getInventoryCount(11738);
        if (amount <1){
            return;
        }

        if (amount > 10000) {
            amount = 10000;
        }

        player.getItems().deleteItem(HerbBox, amount);

        List<GameItem> rewards = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            int random = Misc.random(100);
            List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 80 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
            GameItem item = Misc.getRandomItem(itemList);
            rewards.add(item);
        }
        if (rewards.isEmpty())
            return;

        rewards.forEach(items -> player.getItems().addItemUnderAnyCircumstance(items.getId(), items.getAmount()));
    }
}
