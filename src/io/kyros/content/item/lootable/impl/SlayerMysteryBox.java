package io.kyros.content.item.lootable.impl;

import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.MysteryBoxLootable;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import QuickUltra.Rarity;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */
public class SlayerMysteryBox extends MysteryBoxLootable {

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	    @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

	public static void loadItems() {
		if (!items.isEmpty())
			items.clear();

		YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("SlayerMysteryBox");
items = loadedBox.getItems();
	}

	/**
	 * Constructs a new myster box to handle item receiving for this player and this player alone
	 *
	 * @param player the player
	 */
	public SlayerMysteryBox(Player player) {
		super(player);
	}

	@Override
	public int getItemId() {
		return 13438;
	}

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}