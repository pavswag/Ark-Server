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
public class PvmCasket implements Lootable {

	/**
	 * The item id of the PvM Casket required to trigger the event
	 */
	public static final int PVM_CASKET = 405; //Casket

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	    @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();


	public static void loadItems() {
		if (!items.isEmpty())
			items.clear();

		YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("PvmCasket");
items = loadedBox.getItems();
	}



    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

	/**
	 * Opens a PvM Casket if possible, and ultimately triggers and event, if possible.
	 *
	 */
	@Override
	public void roll(Player player) {
		if (System.currentTimeMillis() - player.lastMysteryBox < 150 * 4) {
			return;
		}
		if (player.getItems().freeSlots() < 2) {
			player.sendMessage("You need at least two free slots to open a PvM Casket.");
			return;
		}
		if (!player.getItems().playerHasItem(PVM_CASKET)) {
			player.sendMessage("You need PvM Casket to do this.");
			return;
		}
		player.getItems().deleteItem(PVM_CASKET, 1);
		player.lastMysteryBox = System.currentTimeMillis();
		open(player);
	}

	public static void open(final Player player) {
		int coins = 50000 + Misc.random(15000);
		int coinsDouble = 100000 + Misc.random(50000);
		int random = Misc.random(100);
		List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON)
				: random <= 80 ? items.get(LootRarity.UNCOMMON)
				: items.get(LootRarity.RARE);
		GameItem item = Misc.getRandomItem(itemList);
		GameItem itemDouble = Misc.getRandomItem(itemList);

		if (Misc.random(10) == 0) {
			player.getItems().addItem(995, coins + coinsDouble);
			player.getItems().addItem(item.getId(), item.getAmount());
			player.getItems().addItem(itemDouble.getId(), itemDouble.getAmount());
			player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>, and <col=255>"
					+ Misc.insertCommas(Integer.toString(coins)) + "</col>GP.");
			player.sendMessage("You receive <col=255>" + itemDouble.getAmount() + " x " + ItemAssistant.getItemName(itemDouble.getId()) + "</col>, and <col=255>"
					+ Misc.insertCommas(Integer.toString(coins)) + "</col>GP.");
		} else {
			player.getItems().addItem(995, coins);
			player.getItems().addItem(item.getId(), item.getAmount());
			player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>, and <col=255>"
					+ Misc.insertCommas(Integer.toString(coins)) + "</col>GP.");
		}
	}

}