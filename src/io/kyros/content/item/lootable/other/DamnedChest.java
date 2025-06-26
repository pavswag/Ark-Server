package io.kyros.content.item.lootable.other;

import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.impl.DamnedChestItems;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.sql.logging.RareLootLog;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;
import java.util.Map;

public class DamnedChest implements Lootable {

    private static final int KEY = 28421;
    private static final int ANIMATION = 881;

    // Method to get a random common reward
    private static GameItem randomChestRewards() {
        List<GameItem> itemList = DamnedChestItems.getItems().get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);
    }

    // Method to get a random rare reward
    private static GameItem randomRareChestRewards() {
        List<GameItem> itemList = DamnedChestItems.getItems().get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    // Method to get a random very rare reward
    private static GameItem randomVeryRareChestRewards() {
        List<GameItem> itemList = DamnedChestItems.getItems().get(LootRarity.VERY_RARE);
        return Misc.getRandomItem(itemList);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return DamnedChestItems.getItems();
    }

    @Override
    public void roll(Player c) {
        int rareChance = 599; // Set base chance to 599 for a 1 in 600 chance of a very rare item

// Adjust the rareChance variable based on various conditions
        if (Hespori.activeKronosSeed || Hespori.activeEnhancedKronosSeed) {
            rareChance = 360; // Adjusted to maintain the same proportion, e.g., reducing by 40%
        }

        // Further reduce the rareChance if the player is a Discord booster
        if (Discord.jda != null) {
            Guild guild = Discord.jda.getGuildById(1001818107343556648L);

            if (guild != null) {
                for (Member booster : guild.getBoosters()) {
                    if (c.getDiscordUser() == booster.getUser().getIdLong()) {
                        rareChance -= 10;
                        break;
                    }
                }
            }
        }

        // Generate a random number to determine the rarity of the reward
        int chance = Misc.random(0, rareChance);

        // Check if the player has the required key to open the chest
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);

            // Determine the reward based on the generated chance
            GameItem reward;
            if (chance == 0) {
                // Highest priority, very rare reward
                reward = randomVeryRareChestRewards();
//                c.sendErrorMessage("Oh look, you got a VERY FUCKING RARE!!!!!");
            } else if (chance < 2) {
                // Second priority, rare reward
                reward = randomRareChestRewards();
            } else {
                // Default, common reward
                reward = randomChestRewards();
            }

// Adjust the reward amount based on player's perks and conditions
            int finalAmount = (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount());
            if (c.daily2xRaidLoot > 0) {
                c.sendErrorMessage("2x Raid loot activated!!!!");
                finalAmount *= 2;
            }

// Add the reward to the player's inventory
            c.getItems().addItemUnderAnyCircumstance(reward.getId(), finalAmount);

// Log the loot and announce if the item is rare or very rare
            new RareLootLog(c.getDisplayName(), reward.getDef().getName(), reward.getId(), finalAmount, "Damned Chest", Misc.getTime()).submit();

// Update collection log for both rare and very rare items
            if (DamnedChestItems.getItems().get(LootRarity.RARE).contains(reward) || DamnedChestItems.getItems().get(LootRarity.VERY_RARE).contains(reward)) {
                c.sendMessage("@blu@You have received a rare item out of the storage unit.");
                NPCDeath.announceKc(c, reward, c.damnedCompletions);
                c.getCollectionLog().handleDrop(c, 853, reward.getId(), reward.getAmount());
            }

        } else {
            // Inform the player if they do not have the key to open the chest
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
