package io.kyros.content.item.lootable.impl;

import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ItemAssistant;
import io.kyros.util.Misc;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */
public class VoteMysteryBox implements Lootable {

	/**
	 * The item id of the mystery box required to trigger the event
	 */
	public static final int VOTE_MYSTERY_BOX = 11739;

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	    @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

	public static void loadItems() {
		if (!items.isEmpty())
			items.clear();

		YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("VoteMysteryBox");
items = loadedBox.getItems();
	}

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

	/**
	 * Opens a mystery box if possible, and ultimately triggers and event, if possible.
	 */
	public void roll(Player player) {
		if (System.currentTimeMillis() - player.lastMysteryBox < 600) {
			return;
		}
		if (player.getItems().freeSlots() < 2) {
			player.sendMessage("You need at least two free slots to open a hourly box.");
			return;
		}
		if (!player.getItems().playerHasItem(VOTE_MYSTERY_BOX)) {
			player.sendMessage("You need a hourly box to do this.");
			return;
		}
		player.getItems().deleteItem(VOTE_MYSTERY_BOX, 1);
		player.lastMysteryBox = System.currentTimeMillis();
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
	}

}