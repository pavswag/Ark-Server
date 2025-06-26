package io.kyros.content.playerinformation;

import io.kyros.cache.definitions.identifiers.NumberUtils;
import io.kyros.content.skills.Skill;
import io.kyros.model.entity.npc.drops.DropManager;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.concurrent.TimeUnit;

public class Interface {

    public static void Open(Player player) {
        Update(player);
        player.getPA().showInterface(55160);
    }

    public static void Update(Player player) {
        int start = 55164;
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.ATTACK));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.STRENGTH));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.DEFENCE));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.RANGED));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.PRAYER));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.MAGIC));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.RUNECRAFTING));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.HUNTER));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.HITPOINTS));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.AGILITY));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.HERBLORE));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.THIEVING));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.CRAFTING));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.FLETCHING));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.SLAYER));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.DEMON_HUNTER));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.MINING));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.SMITHING));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.FISHING));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.COOKING));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.FIREMAKING));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.WOODCUTTING));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.FARMING));
        player.getPA().sendString(start++, "@whi@"+player.getLevel(Skill.FORTUNE));
        player.getPA().sendString(start++, "Total Level: "+player.totalLevel);
        player.getPA().sendString(start++, "Donator: "+ player.getDisplayNameFormatted());
        player.getPA().sendString(start++, "D. Credits: "+ player.donatorPoints);
        player.getPA().sendString(start++, "Store: "+ player.getStoreDonated() + "/ Ingame: " +player.amDonated);

        int simp = 55202;
        player.getPA().sendString(simp++, "@cr1@@or1@ Player Information");simp++;

        long miliseconds = (long) player.playTime * 600;
        long days = TimeUnit.MILLISECONDS.toDays(miliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(miliseconds - TimeUnit.DAYS.toDays(days));
        String time = days + " days, " + hours + " hrs";
        player.getPA().sendString(simp++, "@or1@- TotalTime: @gre@"+time);simp++;
        double dr = DropManager.getModifier1(player);
        System.out.println("player has [" + dr + "] droprate / " + NumberUtils.formatOnePlace(dr));
        if (dr > 75) {
            player.getPA().sendString(simp++,"@or1@- DR: @gre@" + NumberUtils.formatOnePlace((DropManager.getModifier1(player) - (DropManager.getModifier1(player) - 75))));
        } else {
            player.getPA().sendString(simp++,"@or1@- DR: @gre@" + NumberUtils.formatOnePlace(DropManager.getModifier1(player)));
        }
        simp++;
        player.getPA().sendString(simp++, "@or1@- XP: @gre@" + player.getExpMode().getType().getFormattedName());
        simp++;
        player.getPA().sendString(simp++, "@or1@- Mode: @gre@" + player.getMode().getType().getFormattedName());
        simp++;
        if (player.killcount > 0) {
            if (player.deathcount > 0) {
                player.getPA().sendString(simp++,"@or1@- KDR: @gre@" + (player.killcount / player.deathcount) + " / (" +player.killcount+"/"+player.deathcount+")");
            } else {
                player.getPA().sendString(simp++,"@or1@- KDR: @gre@" + (player.killcount) + " / (" +player.killcount+")");
            }
            simp++;
        }
        player.getPA().sendString(simp++,"@or1@- Vote Points: @gre@" + player.votePoints);
        simp++;
        player.getPA().sendString(simp++,"@or1@- Vote Entries: @gre@" + player.VoteEntries);
        simp++;
        player.getPA().sendString(simp++,"@or1@- Premium Points: @gre@" + player.PremiumPoints);
        simp++;
        player.getPA().sendString(simp++,"@or1@- BaBa Points: @gre@" + player.BabaPoints);
        simp++;
        player.getPA().sendString(simp++,"@or1@- PK Points: @gre@" + player.pkp);
        simp++;
        player.getPA().sendString(simp++,"@or1@- AFK Points: @gre@" + Misc.formatCoins(player.getAfkPoints()));
        simp++;
        player.getPA().sendString(simp++,"@or1@- Arbo Points: @gre@" + Misc.formatCoins(player.arboPoints));
        simp++;
        player.getPA().sendString(simp++,"@or1@- Shadow Raid Points: @gre@" + Misc.formatCoins(player.shadowCrusadePoints));
        simp++;
        player.getPA().sendString(simp++,"@or1@- Damned Points: @gre@" + Misc.formatCoins(player.damnedPoints));
        simp++;
        player.getPA().sendString(simp++,"@or1@- Boss Points: @gre@" + player.bossPoints);
        simp++;
        player.getPA().sendString(simp++,"@or1@- Slayer Points: @gre@" + player.getSlayer().getPoints());
        simp++;
        player.getPA().sendString(simp++,"@or1@- PC Points: @gre@" + player.pcPoints);
        simp++;
        player.getPA().sendString(simp++,"@or1@- Nomad Points: @gre@" + Misc.formatCoins(player.foundryPoints));
        simp++;
        player.getPA().sendString(simp++,"@or1@- Bloody Points: @gre@" + player.getBloody_points());
        simp++;
        player.getPA().sendString(simp++,"@or1@- Seasonal Points: @gre@" + player.getSeasonalPoints());
        simp++;
        player.getPA().sendString(simp++,"@or1@- Discord Points: @gre@" + Misc.formatCoins(player.getDiscordPoints()));
        simp++;
        player.getPA().sendString(simp++,"@or1@- AOE Points: @gre@" + Misc.formatCoins(player.instanceCurrency));
        simp++;
        player.getPA().sendString(simp++,"@or1@- Fortune Spins: @gre@" + Misc.formatCoins(player.getFortuneSpins()));
    }
}
