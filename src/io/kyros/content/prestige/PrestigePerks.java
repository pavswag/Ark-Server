package io.kyros.content.prestige;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public enum PrestigePerks {
    // 1x mode = 15k
    // 5x mode = 7500
    // standard mode = 1500
    DAMAGE_BONUS1(1, 3, 18934, 18935, 2406, 750, 22_500_000),//1.5% damage boost
    EXPERIENCE_BONUS1(1, 3, 18916, 18917, 2400, 750, 22_500_000),//1.5% xp Boost

    DOUBLE_PC_POINTS(1, 100, 18952, 18953, 2412, 500, 15_000_000),//2x Chest rewards

    BLOODY_MINIGAME(2, 100, 18919, 18920, 2401, 250, 7_500_000), // Double Bloodyminigame points!
    CANNON_EXTENDER(2, 100, 18937, 18938, 2407, 250, 7_500_000),// 150 more Cannonballs! 200 if 1x & 5x xp mode
    TRIPLE_HESPORI_KEYS(2, 10, 18955, 18956, 2413, 750, 22_500_000),//3x all keys 10 chance

    DAMAGE_BONUS2(3, 3, 18940, 18941, 2408, 1000, 30_000_000),//1.5% damage boost
    EXPERIENCE_BONUS2(3, 3, 18922, 18923, 2402, 1000, 30_000_000),//1.5% xp Boost
    RESTORE_FULL_PRAYER(3, 100, 18958, 18959, 2414, 250, 7_500_000),// 10% chance to restore prayer to full

    ATTUNE_PERKS(4, 3, 18943, 18944, 2409, 1000, 30_000_000),//10 % chance to save item / Nomad points when upgrading
    ZERK_RANGE_MAGE(4, 1, 18925, 18926, 2403, 1000, 30_000_000),//ZERK, Range & Mage perk's combined into one for bonus damage!
    HEAL_TO_MAX(4, 100, 18961, 18962, 2415, 250, 7_500_000),//10% chance to heal to max

    DAMAGE_BONUS3(5, 3, 18946, 18947, 2410, 1250, 37_500_000),//1.5% damage boost
    EXPERIENCE_BONUS3(5, 3, 18928, 18929, 2404, 1250, 37_500_000),//1.5% xp Boost
    NOMAD_PLUS_15(5, 15, 18964, 18965, 2416, 2500, 75_000_000),//15% more foundry when melting items!

    EXPERIENCE_DAMAGE_BONUS1(6, 6, 18949, 18950, 2411, 5000, 150_000_000),//3% damage boost & xp boost
    RAGE(6, 0, 18967, 18968, 2417, 5000, 150_000_000),//An item that gives you "Rage mode" -
    ONE_TIME_15_UMBS(6, 15, 18931, 18932, 2405, 2500, 75_000_000)//One time purchase of 10 umb's for normal or 20 for 5x and 1x xp modes.
    ;
    public int prestigeTier;
    public int percentage;
    public int sprite;
    public int button;
    public int config;
    public int cost;
    public int madCost;
    PrestigePerks(int prestigeTier, int percentage, int sprite, int button, int config, int cost, int madCost) {
        this.prestigeTier = prestigeTier;
        this.percentage = percentage;
        this.sprite = sprite;
        this.button = button;
        this.config = config;
        this.cost = cost;
        this.madCost = madCost;
    }

    public static void HandleButtons(Player player, PrestigePerks value) {
        if (player.prestigePerks.contains(value)) {
            return;
        }

        if (value.prestigeTier > handleTiers(player)) {
            player.sendMessage("You need to be tier "+ value.prestigeTier + " to obtain this relic!");
            return;
        }

        if (!handleTierPerk(player, value)) {
            player.sendMessage("You've not unlocked all the previous relics to obtain this perk!");
            return;
        }

        player.start(new DialogueBuilder(player).option("This relic costs " + value.madCost + " MadPoints & " + value.cost + " prestige pooints!",
                new DialogueOption("Buy relic", p -> {
                    if (value.madCost > player.foundryPoints) {
                        p.sendMessage("You need " + Misc.formatCoins(value.madCost) + " MadPoints to purchase this perk!");
                        p.getPA().closeAllWindows();
                        return;
                    }

                    if (p.prestigePoints < value.cost) {
                        p.sendMessage("You need " + value.cost + " prestige points to buy this relic!");
                        p.getPA().closeAllWindows();
                        return;
                    }


                    switch (value.button) {
                        case 18932:
                            p.getItems().addItemUnderAnyCircumstance(13346, 10);
                            break;
                        case 18968:
                            p.getItems().addItemUnderAnyCircumstance(11433, 1);
                            break;
                    }

                    p.foundryPoints -= value.madCost;
                    p.prestigePoints -= value.cost;
                    p.prestigePerks.add(value);
                    p.sendMessage("You have unlocked this relic!");
                    p.getPA().sendConfig(value.config, 1);
                    p.getPA().closeAllWindows();
                    p.getPA().showInterface(18910);
                }),
                new DialogueOption("Nevermind.", p -> {p.getPA().closeAllWindows();})));

    }

    public static void handleLoading(Player player, PrestigePerks value) {
        player.prestigePerks.add(value);
        player.getPA().sendConfig(value.config, 1);
    }

    public static int handleTiers(Player player) {
        switch (player.prestigeLevel[0]) {
            case 1:
            case 2:
                return 1;
            case 3:
            case 4:
                return 2;
            case 5:
            case 6:
                return 3;
            case 7:
            case 8:
                return 4;
            case 9:
                return 5;
            case 10:
                return 6;
        }
        return 0;
    }

    public static boolean hasRelic(Player player, PrestigePerks perk) {
        return (player.prestigePerks.contains(perk));
    }


    private static boolean handleTierPerk(Player player, PrestigePerks perks) {

        if (perks.equals(PrestigePerks.BLOODY_MINIGAME) ||
                perks.equals(PrestigePerks.CANNON_EXTENDER) ||
                perks.equals(PrestigePerks.TRIPLE_HESPORI_KEYS)) {
            //Tier 2 requires Tier1
            return player.prestigePerks.contains(PrestigePerks.DAMAGE_BONUS1) &&
                    player.prestigePerks.contains(PrestigePerks.EXPERIENCE_BONUS1) &&
                    player.prestigePerks.contains(PrestigePerks.DOUBLE_PC_POINTS);
        }

        if (perks.equals(PrestigePerks.DAMAGE_BONUS2) ||
                perks.equals(PrestigePerks.EXPERIENCE_BONUS2) ||
                perks.equals(PrestigePerks.RESTORE_FULL_PRAYER)) {
            //Tier 3 requires Tier2
            return player.prestigePerks.contains(PrestigePerks.BLOODY_MINIGAME) &&
                    player.prestigePerks.contains(PrestigePerks.CANNON_EXTENDER) &&
                    player.prestigePerks.contains(PrestigePerks.TRIPLE_HESPORI_KEYS);
        }

        if (perks.equals(PrestigePerks.ATTUNE_PERKS) ||
                perks.equals(PrestigePerks.ZERK_RANGE_MAGE) ||
                perks.equals(PrestigePerks.HEAL_TO_MAX)) {
            //Tier 4 requires Tier3
            return player.prestigePerks.contains(PrestigePerks.RESTORE_FULL_PRAYER) &&
                    player.prestigePerks.contains(PrestigePerks.EXPERIENCE_BONUS2) &&
                    player.prestigePerks.contains(PrestigePerks.DAMAGE_BONUS2);
        }

        if (perks.equals(PrestigePerks.DAMAGE_BONUS3) ||
                perks.equals(PrestigePerks.EXPERIENCE_BONUS3) ||
                perks.equals(PrestigePerks.NOMAD_PLUS_15)) {
            //Tier 5 requires Tier4
            return player.prestigePerks.contains(PrestigePerks.HEAL_TO_MAX) &&
                    player.prestigePerks.contains(PrestigePerks.ZERK_RANGE_MAGE) &&
                    player.prestigePerks.contains(PrestigePerks.ATTUNE_PERKS);
        }

        if (perks.equals(PrestigePerks.EXPERIENCE_DAMAGE_BONUS1) ||
                perks.equals(PrestigePerks.RAGE) ||
                perks.equals(PrestigePerks.ONE_TIME_15_UMBS)) {
            //Tier 6 requires Tier5
            return player.prestigePerks.contains(PrestigePerks.NOMAD_PLUS_15) &&
                    player.prestigePerks.contains(PrestigePerks.EXPERIENCE_BONUS3) &&
                    player.prestigePerks.contains(PrestigePerks.DAMAGE_BONUS3);
        }

        return perks.equals(PrestigePerks.DAMAGE_BONUS1) ||
                perks.equals(PrestigePerks.EXPERIENCE_BONUS1) ||
                perks.equals(PrestigePerks.DOUBLE_PC_POINTS);
    }
}
