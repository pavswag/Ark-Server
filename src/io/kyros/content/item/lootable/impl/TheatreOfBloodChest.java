package io.kyros.content.item.lootable.impl;

import com.google.common.collect.Lists;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.bosspoints.BossPoints;
import io.kyros.content.event.eventcalendar.EventChallenge;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.YamlLoader;
import io.kyros.content.leaderboards.LeaderboardType;
import io.kyros.content.leaderboards.LeaderboardUtils;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.model.Items;
import io.kyros.model.Npcs;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.npc.drops.DropManager;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.*;

public class TheatreOfBloodChest implements Lootable {

        @Getter
    private static Map<LootRarity, List<GameItem>> items = new HashMap<>();

    private static Map<LootRarity, Integer> rates = new HashMap<>();

public static void loadItems() {
        if (!items.isEmpty())
            items.clear();
        if (!rates.isEmpty())
            rates.clear();

        YamlLoader.LoadedBox loadedBox = YamlLoader.loadItems("TheatreOfBloodChest");
items = loadedBox.getItems();
rates = loadedBox.getRates();
    }

    public static ArrayList<GameItem> getRareDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if (drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                drops.add(f);
            }
        }
        return drops;
    }

    public static ArrayList<GameItem> getAllDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        items.forEach((lootRarity, gameItems) -> {
            gameItems.forEach(g -> {
                if (!drops.contains(g)) {
                    drops.add(g);
                }
            });
        });
        return drops;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    public static List<GameItem> getRandomItems(Player player, int size) {
        List<GameItem> rewards = Lists.newArrayList();
        int rareChance = 100;
        if (Hespori.activeKronosSeed || Hespori.activeEnhancedKronosSeed) {
            rareChance = 60;
        } else if (player.amDonated >= 100 && player.amDonated < 250) {
            rareChance = 90;
        } else if (player.amDonated >= 250 && player.amDonated < 500) {
            rareChance = 85;
        } else if (player.amDonated >= 500 && player.amDonated < 750) {
            rareChance = 80;
        } else if (player.amDonated >= 750 && player.amDonated < 1000) {
            rareChance = 75;
        } else if (player.amDonated >= 1000 && player.amDonated < 1500) {
            rareChance = 70;
        } else if (player.amDonated >= 1500 && player.amDonated < 2000) {
            rareChance = 65;
        } else if (player.amDonated >= 2000 && player.amDonated < 3000) {
            rareChance = 60;
        } else if (player.amDonated >= 3000) {
            rareChance = 45;
        }

        if (player.getMode().isOsrs()) {
            rareChance -= 5;
        }
        if (player.getMode().is5x()) {
            rareChance -= 3;
        }

        if (player.hasFollower && (player.petSummonId == 25350 || player.petSummonId == 30122)) {
            rareChance -= 20;
        }

        if (Discord.jda != null) {
            Guild guild = Discord.jda.getGuildById(1001818107343556648L);

            if (guild != null) {
                for (Member booster : guild.getBoosters()) {
                    if (player.getDiscordUser() == booster.getUser().getIdLong()) {
                        rareChance -= 10;
                        break;
                    }
                }
            }
        }

        rareChance = (int) (rareChance - (DropManager.getModifier1(player) < 10 ? DropManager.getModifier1(player) : DropManager.getModifier1(player) / 10));
        if (rareChance <= 0) {
            rareChance = 30;
        }
        int chance = Misc.random(1, rareChance);
        if (player.getItems().hasItemOnOrInventory(Items.BUCKET_OF_MILK)) {
            player.sendMessage("[@red@MommyMilkers@bla@] Your milk has been consumed and you feel a rush through your system!");
            player.getItems().deleteItem2(Items.BUCKET_OF_MILK, 1);
        }

        if (chance <= 10) {
            rewards.add(Misc.getRandomItem(items.get(LootRarity.RARE)));
        } else {
            for (int count = 0; count < 3; count++) {
                rewards.add(Misc.getRandomItem(items.get(LootRarity.COMMON)));
            }
        }
        return rewards;
    }

    public static boolean containsRare(List<GameItem> itemList) {
        return items.get(LootRarity.RARE).stream().anyMatch(rareItem -> itemList.stream().anyMatch(itemReward -> rareItem.getId() == itemReward.getId()));
    }

    /**
     * Reward items that are generated when the treasure room is initialised.
     */
    public static void rewardItems(Player player, List<GameItem> rewards) {
        BossPoints.addManualPoints(player, "theatre of blood");

        PetHandler.roll(player, PetHandler.Pets.LIL_ZIK);
        player.getEventCalendar().progress(EventChallenge.COMPLETE_TOB);
        LeaderboardUtils.addCount(LeaderboardType.TOB, player, 1);
        Achievements.increase(player, AchievementType.TOB, 1);
        if (Hespori.activeKronosSeed) {
            player.sendMessage("@red@The @gre@Kronos seed@red@ doubled your chances!" );
        }
        player.getItems().addItem(995, 500_000 + Misc.random(1_000_000));
        List<GameItem> rareItemList = items.get(LootRarity.RARE);
        for (GameItem reward : rewards) {
            if (rareItemList.stream().anyMatch(rareItem -> reward.getId() == rareItem.getId())) {
                if (!player.getDisplayName().equalsIgnoreCase("thimble") && !player.getDisplayName().equalsIgnoreCase("top hat")) {
                    PlayerHandler.executeGlobalMessage("@pur@" + player.getDisplayNameFormatted() + " received a drop: "
                            + ItemDef.forId(reward.getId()).getName() + " x " + reward.getAmount() + " from Theatre of Blood.");
                }
                player.getCollectionLog().handleDrop(player, Npcs.THE_MAIDEN_OF_SUGADINTI, rewards.get(0).getId(), 1);
            }
        }

        for (GameItem item : rewards) {
            int finalAmount = (PrestigePerks.hasRelic(player, PrestigePerks.DOUBLE_PC_POINTS) && (Misc.random(0, 100) == 2) ?
                    item.getAmount()* 2 : item.getAmount());
            if (player.daily2xRaidLoot > 0) {
                finalAmount *= 2;
            }
            player.getItems().addItemUnderAnyCircumstance(item.getId(), finalAmount);
//            player.getInventory().addAnywhere(new ImmutableItem(item.getId(), item.getAmount()));
        }

        player.getTobContainer().displayRewardInterface(rewards);
    }

    /**
     * To be removed but kept for now.
     */
    @Override
    public void roll(Player player) {}


    public static void main(String[] args) throws Exception {
        ItemDef.load();
        HashMap<GameItem, Integer> map = new HashMap<>();

        for (int i = 0; i < 10000; i++) {
            GameItem gameItem = (Misc.random(1,30) <= 3 ? Misc.getRandomItem(items.get(LootRarity.RARE)) : Misc.getRandomItem(items.get(LootRarity.COMMON)));
            int amount = map.getOrDefault(gameItem, 0);
            map.put(gameItem, amount + 1);
        }

        map.forEach((gameItem, amount) -> {
            String dropChance = String.format("%.2f", ((double) amount / 10000) * 100);
            String itemName = ItemDef.forId(gameItem.getId()).getName();
            System.out.println("Rolled a " + itemName + " " + amount + " times. " + dropChance);
        });

    }
}
