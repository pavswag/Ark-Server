package io.kyros.content.item.lootable.other;

import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.Lootable;
import io.kyros.content.item.lootable.impl.ShadowCrusadeChestItems;
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

public class ShadowCrusadeChest implements Lootable {

    private static final int KEY = 28416;
    private static final int ANIMATION = 881;

    private static GameItem randomChestRewards() {
        List<GameItem> itemList = ShadowCrusadeChestItems.getItems().get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);
    }

    private static GameItem randomRareChestRewards() {
        List<GameItem> itemList = ShadowCrusadeChestItems.getItems().get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return ShadowCrusadeChestItems.getItems();
    }



    @Override
    public void roll(Player c) {
        int rareChance = 100;

        if (Hespori.activeKronosSeed || Hespori.activeEnhancedKronosSeed) {
            rareChance = 60;
        } else if (c.amDonated >= 100 && c.amDonated < 250) {
            rareChance = 90;
        } else if (c.amDonated >= 250 && c.amDonated < 500) {
            rareChance = 85;
        } else if (c.amDonated >= 500 && c.amDonated < 750) {
            rareChance = 80;
        } else if (c.amDonated >= 750 && c.amDonated < 1000) {
            rareChance = 75;
        } else if (c.amDonated >= 1000 && c.amDonated < 1500) {
            rareChance = 70;
        } else if (c.amDonated >= 1500 && c.amDonated < 2000) {
            rareChance = 65;
        } else if (c.amDonated >= 2000 && c.amDonated < 3000) {
            rareChance = 60;
        } else if (c.amDonated >= 3000) {
            rareChance = 45;
        }

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


        int chance = Misc.trueRand(rareChance);
        if (c.getItems().playerHasItem(KEY)) {
                c.getItems().deleteItem(KEY, 1);
                c.startAnimation(ANIMATION);
                GameItem reward = (chance < 2) ? randomRareChestRewards() : randomChestRewards();
                int finalAmount = (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount());
                if (c.daily2xRaidLoot > 0) {
                    finalAmount *= 2;
                }
            c.getItems().addItemUnderAnyCircumstance(reward.getId(), finalAmount);

            new RareLootLog(c.getDisplayName(), reward.getDef().getName(), reward.getId(), finalAmount, "Shadow Crusade Chest", Misc.getTime()).submit();
            if (ShadowCrusadeChestItems.getItems().get(LootRarity.RARE).contains(reward)) {
                    c.sendMessage("@blu@You have received a rare item out of the storage unit.");
                    NPCDeath.announceKc(c, reward, c.shadowCrusadeCompletions);
                    c.getCollectionLog().handleDrop(c, 13527, reward.getId(), reward.getAmount());
                }
        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }
}
