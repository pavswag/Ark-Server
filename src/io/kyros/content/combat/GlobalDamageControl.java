package io.kyros.content.combat;

import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import lombok.Getter;

public enum GlobalDamageControl {

    // Enum constants for each boss with Name, ID, Min, Max
    EXPERIMENT_NO2("Experiment No 2", 5126, 1, 1000),

    JUSTICIAR_ZACHARIAH("Justiciar Zachariah", 12449, 1, 1000),

    DURIAL("Durial", 5169, 1, 1000),

    GROOT("Groot", 4923, 1, 1000),

    GALVEK("Galvek", 8096, 1, 1000),

    IRON_FIFTY_CENT("Iron Fifty Cent", 12784, 1, 1000),

    SCURRIUS("Scurrius", 7221, 1, 1000);

    // Fields for boss name, NPC ID, min, and max damage
    private final String bossName;
    @Getter
    private final int npcId;
    // Method to retrieve the minimum damage allowed for a boss
    @Getter
    private final int minDamage;
    // Method to retrieve the maximum damage allowed for a boss
    @Getter
    private final int maxDamage;

    // Constructor for the enum
    GlobalDamageControl(String bossName, int npcId, int minDamage, int maxDamage) {
        this.bossName = bossName;
        this.npcId = npcId;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    // Method to clamp damage between min and max for a specific boss
    public int clampDamage(int damage) {
        if (damage > getMaxDamage()) {
            return getMaxDamage();
        } else if (damage < getMinDamage()) {
            return getMinDamage();
        }
        return damage;
    }

    public static int handleNpcDamageControl(NPC npc, int damage) {
        // Identify which GlobalDamageControl enum applies based on the NPC
        GlobalDamageControl boss = getBossFromNpc(npc);

        if (boss != null) {
            // Apply damage clamping using the boss-specific rules
            damage = boss.clampDamage(damage);
        }

        // Return the adjusted damage
        return damage;
    }

    // This is a helper method to map an NPC to its corresponding boss in the enum
    private static GlobalDamageControl getBossFromNpc(NPC npc) {
        for (GlobalDamageControl boss : GlobalDamageControl.values()) {
            if (boss.getNpcId() == npc.getNpcId()) {
                return boss; // Return the boss enum if the NPC ID matches
            }
        }
        return null; // Return null if no matching boss is found
    }

}
