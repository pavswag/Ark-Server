package io.kyros.content.item.lootable.impl;

import io.kyros.content.event.eventcalendar.EventChallenge;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnbearableChest implements Lootable {

    private static final int KEY = 4185;
    private static final int ANIMATION = 881;
        @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

    private static Map<LootRarity, Integer> rates = new HashMap<>();

public static void loadItems() {
        if (!items.isEmpty())
            items.clear();
        if (!rates.isEmpty())
            rates.clear();

        YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("UnbearableChest");
items = loadedBox.getItems();
rates = loadedBox.getRates();
    }

    private static GameItem randomChestRewardsYoutube(Player c, int chance) {
        int random = Misc.random(chance);
        int rareChance = 980;
        if (c.getItems().playerHasItem(21046)) {
            rareChance = 975;
            c.getItems().deleteItem(21046, 1);
            c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
            c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
        }
        List<GameItem> itemList = random <= rareChance ? items.get(LootRarity.COMMON) : items.get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    private static GameItem randomChestRewards(Player c, int chance) {
        int random = Misc.random(chance);
        int rareChance = 995;
        if (c.getItems().playerHasItem(21046)) {
            rareChance = 975;
            c.getItems().deleteItem(21046, 1);
            c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
            c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
        }
        List<GameItem> itemList;
        if (random <= rareChance) {
            itemList = items.get(LootRarity.COMMON);
            GameItem chooseRandomItem = Misc.getRandomItem(itemList);
            return chooseRandomItem;
        } else {
            itemList = items.get(LootRarity.RARE);
            GameItem chooseRandomItem = Misc.getRandomItem(itemList);
            ItemDef def = ItemDef.forId(chooseRandomItem.getId());
            if (!c.getDisplayName().equalsIgnoreCase("thimble") && !c.getDisplayName().equalsIgnoreCase("top hat")) {
                PlayerHandler.executeGlobalMessage("@bla@[<col=7f0000>UNBEARABLE@bla@] <col=990000>" + c.getDisplayName() + "@bla@ has just received a <col=990000>" + def.getName() + ".");
            }
            return chooseRandomItem;
        }

    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public void roll(Player c) {
        int pkpbonus = Misc.random(19) + 10;
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            c.pkp += pkpbonus;
            GameItem reward = randomChestRewards(c,1000);
            c.getItems().addItem(reward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount()));
            c.sendMessage("@blu@You also receive @red@" + pkpbonus + " @blu@pkp as a bonus for killing a wildy boss.");
        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
