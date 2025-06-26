package io.kyros.content.item.lootable.impl;

import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.event.eventcalendar.EventChallenge;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.model.Npcs;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HunllefChest implements Lootable {

    private static final int KEY = 23776;
    private static final int ANIMATION = 881;

        @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

    private static Map<LootRarity, Integer> rates = new HashMap<>();

public static void loadItems() {
        if (!items.isEmpty())
            items.clear();
        if (!rates.isEmpty())
            rates.clear();

        YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("HunllefChest");
items = loadedBox.getItems();
rates = loadedBox.getRates();
    }

    private static GameItem commonChestRewards() {

        List<GameItem> itemList = randomNumber() < 850 ? items.get(LootRarity.COMMON) : items.get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);

    }

    private static GameItem rareChestRewards(int rareChance) {

        List<GameItem> itemList = randomNumber() >= rareChance ? items.get(LootRarity.RARE) : items.get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);

    }

    public final static int randomNumber() {
        final int random = Misc.random(1000);
        return random;
    }

    public static void rolledCommon(Player c) {
        int crystalshardbonus = Misc.random(29) + 10;
        if (randomNumber() < 750) { //not a rare
            if (c.getItems().playerHasItem(KEY)) {
                c.getItems().deleteItem(KEY, 1);
                c.startAnimation(ANIMATION);
                GameItem commonreward = commonChestRewards();
                GameItem commonreward2 = commonChestRewards();
                GameItem commonreward3 = commonChestRewards();

                c.getItems().addItem(commonreward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ?commonreward.getAmount() * 2 : commonreward.getAmount()));
                c.getItems().addItem(commonreward2.getId(),(PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? commonreward2.getAmount() *2: commonreward2.getAmount()));
                c.getItems().addItem(commonreward3.getId(),(PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? commonreward3.getAmount()*2: commonreward3.getAmount()));
                c.getItems().deleteItem(21046, 1);
                c.getItems().addItem(23877, crystalshardbonus);
            } else if (!(c.getItems().playerHasItem(KEY))) {
                c.sendMessage("@blu@The chest is locked, it won't budge!");
            }
        }
    }
    public static void rolledRare(Player c, int rareChance) {
        int crystalshardbonus = Misc.random(29) + 10;
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem rarereward = rareChestRewards(rareChance);
            if (rarereward.getId() == 23757 && c.getItems().getItemCount(23757, false) == 0) {
                c.getCollectionLog().handleDrop(c, 5, 23757, 1);
            }
            c.getItems().addItem(rarereward.getId(),(PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? rarereward.getAmount()*2 :rarereward.getAmount()));
            if (c.getItems().playerHasItem(21046)) {
                c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
                c.getItems().deleteItem(21046, 1);
                c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
            }
            c.getItems().addItem(23877, crystalshardbonus);
            NPCDeath.announce(c, rarereward, Npcs.CRYSTALLINE_HUNLLEF);
            //PlayerHandler.executeGlobalMessage("@red@[Hunllef] @pur@" + c.playerName + " has just received a rare item from Hunllef's chest.");
        } else if (!(c.getItems().playerHasItem(KEY))) {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public void roll(Player c) {
        final int random = randomNumber();
        int rareChance = 850;
        if (c.getItems().playerHasItem(21046)) {
            rareChance = 830;
        }
        if (random < rareChance) {
            rolledCommon(c);
        } else if (random >= rareChance) {
            rolledRare(c, rareChance);
        }
    }
}
