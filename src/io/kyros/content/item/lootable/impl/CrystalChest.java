package io.kyros.content.item.lootable.impl;

import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.event.eventcalendar.EventChallenge;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrystalChest implements Lootable {

	private static final int KEY = 989;
	private static final int DRAGONSTONE = 1631;
	private static final int KEY_HALVE1 = 985;
	private static final int KEY_HALVE2 = 987;
	private static final int ANIMATION = 881;

	    @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

    private static Map<LootRarity, Integer> rates = new HashMap<>();

	public static void loadItems() {
		if (!items.isEmpty())
			items.clear();

		YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("CrystalChest");
items = loadedBox.getItems();
rates = loadedBox.getRates();
	}

	private static GameItem randomChestRewards(Player c) {
		int random = Misc.random(100);
		int rareChance = 90;
		if (c.getItems().playerHasItem(21046)) {
			rareChance = 85;
			c.getItems().deleteItem(21046, 1);
			c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
			c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
		}
		List<GameItem> itemList = random < rareChance ? items.get(LootRarity.COMMON) : items.get(LootRarity.RARE);
		return Misc.getRandomItem(itemList);
	}

	public static void makeKey(Player c) {
		if (c.getItems().playerHasItem(KEY_HALVE1, 1) && c.getItems().playerHasItem(KEY_HALVE2, 1)) {
			c.getItems().deleteItem(KEY_HALVE1, 1);
			c.getItems().deleteItem(KEY_HALVE2, 1);
			c.getItems().addItem(KEY, 1);
		}
	}

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

	@Override
	public void roll(Player c) {
		if (!c.getItems().playerHasItem(KEY)) {
			c.sendMessage("@blu@The chest is locked, it won't budge!");
			return;
		}
		int amt = c.getItems().getInventoryCount(KEY);
		if (amt > 100) {
			amt = 100;
		}
		c.getItems().deleteItem(KEY, amt);
		c.startAnimation(ANIMATION);
		for (int i = 0; i < amt; i++) {
			c.getItems().addItemToBankOrDrop(DRAGONSTONE, 1);
			GameItem reward = randomChestRewards(c);
			c.getItems().addItemUnderAnyCircumstance(reward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount()));
			Achievements.increase(c, AchievementType.LOOT_CRYSTAL_CHEST, 1);
		}
	}

}