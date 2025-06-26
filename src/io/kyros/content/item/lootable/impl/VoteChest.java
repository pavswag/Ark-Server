package io.kyros.content.item.lootable.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
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

public class VoteChest implements Lootable {

    public static final int KEY = 22093; //vote key heree
    private static final int ANIMATION = 881;

        @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

    private static Map<LootRarity, Integer> rates = new HashMap<>();

public static void loadItems() {
        if (!items.isEmpty())
            items.clear();
        if (!rates.isEmpty())
            rates.clear();

        YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("VoteChest");
items = loadedBox.getItems();
rates = loadedBox.getRates();
    }

    private static GameItem randomChestRewards(Player c, int chance) {
        int random = Misc.random(chance);
        int rareChance = 90;
        int uncommonChance = 50;
        if (c.getItems().playerHasItem(21046)) {
            rareChance = 89;
            c.getItems().deleteItem(21046, 1);
            c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
            c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
        }
        List<GameItem> itemList = random < uncommonChance ? items.get(LootRarity.COMMON) : random >= uncommonChance && random <= rareChance ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    private static void votePet(Player c) {
        int petchance = Misc.random(1500);
        if (petchance >= 1499) {
            c.getItems().addItem(21262, 1);
            c.getCollectionLog().handleDrop(c, 5, 21262, 1);
            PlayerHandler.executeGlobalMessage("@red@- "+ c.getDisplayName() +"@blu@ has just received the @red@Vote Genie Pet");
            c.sendMessage("@red@@cr10@You pet genie is waiting in your bank, waiting to serve you as his master.");
            c.gfx100(1028);
        }
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public void roll(Player c) {
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            if (c.playTime > Misc.toCycles(5, TimeUnit.HOURS)) {
                Achievements.increase(c, AchievementType.VOTE_CHEST_UNLOCK, 1);
            }

            if (c.playTime < Misc.toCycles(5, TimeUnit.HOURS)) {
                c.sendMessage("You have not earned towards your vote achievement, this requires 5hours playtime!", TimeUnit.MINUTES.toMillis(10));
            }
            c.startAnimation(ANIMATION);
            GameItem reward = randomChestRewards(c, 100);

            String name = ItemDef.forId(reward.getId()).getName();
            c.getItems().addItem(reward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount()));
//            PlayerHandler.executeGlobalMessage("@pur@["+ c.getDisplayName() +"]@blu@ has just opened the vote chest and received a " + name + "!");
            int random = 1 + Misc.random(5);
            c.votePoints+= random;
            c.sendMessage("You have received an extra "+random+" vote points from the chest.");
            votePet(c);
        } else {
            c.sendMessage("@blu@Use @red@::voterank @blu@to see when you'll get your next key!");
        }
    }
}
