package io.kyros.content.bonus;

import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.concurrent.TimeUnit;

public enum BoostScrolls {

    DAMAGE_10MIN(6798, TimeUnit.MINUTES.toMillis(10), "DMG"),
    DAMAGE_30MIN(6799, TimeUnit.MINUTES.toMillis(30), "DMG"),
    DAMAGE_60MIN(6800, TimeUnit.MINUTES.toMillis(60), "DMG"),
    Slayer_Damage_10MIN(6801, TimeUnit.MINUTES.toMillis(10), "SLYDMG"),
    Slayer_Damage_30MIN(6802, TimeUnit.MINUTES.toMillis(30), "SLYDMG"),
    Slayer_Damage_60MIN(6803, TimeUnit.MINUTES.toMillis(60), "SLYDMG"),
    Double_Harvest_10MIN(6804, TimeUnit.MINUTES.toMillis(10), "HARV"),
    Double_Harvest_30MIN(6806, TimeUnit.MINUTES.toMillis(30), "HARV"),
    Double_Harvest_60MIN(6808, TimeUnit.MINUTES.toMillis(60), "HARV")
    ;

    public final long time;
    public final int itemID;
    public final String bonus;

    BoostScrolls(int itemID, long time, String bonus) {
        this.itemID = itemID;
        this.time = time;
        this.bonus = bonus;
    }

    public static boolean handleItemClick(Player player, int itemId) {
        for (BoostScrolls value : BoostScrolls.values()) {
            if (value.itemID == itemId) {
                if (!player.boostTimers.isEmpty()) {
                    for (BoostScrolls boostScrolls : player.boostTimers.keySet()) {
                        if (boostScrolls.bonus.equalsIgnoreCase(value.bonus)) {
                            player.sendMessage("You already have a bonus matching active!");
                            return true;
                        }
                    }
                }

                player.boostTimers.put(value, (System.currentTimeMillis() + value.time));
                player.sendMessage("You activate " + Misc.formatPlayerName(value.name().replace("_", " ")) + "utes scroll.");
                return true;
            }
        }
        return false;
    }

    public static void handleTimer(Player player) {
        if (player.boostTimers.isEmpty()) {
            return;
        }

        for (BoostScrolls boostScrolls : player.boostTimers.keySet()) {
            if (player.boostTimers.get(boostScrolls) < System.currentTimeMillis()) {
                player.sendMessage("Your " + Misc.formatPlayerName(boostScrolls.name().replace("_", " ") + "utes scroll has expired!"));
                player.boostTimers.remove(boostScrolls);
            }
        }
    }


    public static boolean checkSlayerBoost(Player player) {
        for (BoostScrolls boost : BoostScrolls.values()) {
            if (boost.name().equalsIgnoreCase("SLYDMG") && player.boostTimers.containsKey(boost)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkHarvestBoost(Player player) {
        for (BoostScrolls boost : BoostScrolls.values()) {
            if (boost.name().equalsIgnoreCase("HARV") && player.boostTimers.containsKey(boost)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkDamageBoost(Player player) {
        for (BoostScrolls boost : BoostScrolls.values()) {
            if (boost.name().equalsIgnoreCase("DMG") && player.boostTimers.containsKey(boost)) {
                return true;
            }
        }
        return false;
    }

}
