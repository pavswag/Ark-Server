package io.kyros.content.item.lootable.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.kyros.content.event.eventcalendar.EventChallenge;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.model.Items;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import lombok.Getter;

public class LarransChest implements Lootable {

	private static final int KEY = Items.LARRANS_KEY;
	private static final int ANIMATION = 881;

	    @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

	public static void loadItems() {
		if (!items.isEmpty())
			items.clear();

		YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("LarransChest");
items = loadedBox.getItems();
	}



    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

	@Override
	public void roll(Player c) {
		if (c.getItems().playerHasItem(KEY)) {
			c.getItems().deleteItem(KEY, 1);
			c.startAnimation(ANIMATION);
			int random = Misc.random(500);
			if (c.getItems().playerHasItem(21046)) {
				random = Misc.random(493);
				c.getItems().deleteItem(21046, 1);
				c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
				c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
			}
			List<GameItem> itemList = random == 0 ? items.get(LootRarity.RARE) : items.get(LootRarity.COMMON);
			GameItem reward = Misc.getRandomItem(itemList);


			if (random == 0) {
				if (!c.getDisplayName().equalsIgnoreCase("thimble") && !c.getDisplayName().equalsIgnoreCase("top hat")) {

					PlayerHandler.executeGlobalMessage("@pur@" + c.getDisplayNameFormatted() + " received a drop: " +
							"" + ItemDef.forId(reward.getId()).getName() + " x " + reward.getAmount() + " from Larran's chest.");
				}
			}
			c.getItems().addItem(reward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount()));
		} else {
			c.sendMessage("The chest is locked, it won't budge!");
		}
	}

}