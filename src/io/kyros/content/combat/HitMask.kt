package io.kyros.content.combat

enum class HitMask(val id: Int, val tinted: Int = -1, val max: Int = -1) {
    /**
     * Corruption has a chance to apply on the target of the player who is under the effects of either Lesser Corruption or Greater Corruption during a successful hit.
     * Corruption which drains prayer points over a short period of time.
     */
    CORRUPTION(id = 0),

    /**
     * Used for ironman to show the hit is not yours
     */
    BLOCK_HIT(id = 1),

    /**
     * Poison hitsplat.png 	Poison, which damages players at set intervals, and decreases over time.
     * Can also indicate eating a poison karambwan as well as being harmed by Poison Gas. Green eggs fired at enemies in Barbarian Assault also show this.
     * Poison damages entities over time, lowering the damage by one every four hit splat cycles.
     */
    POISON(id = 68, tinted = 69, max = 70),
    POISON_TINTED(id = 69),
    POISON_MAX(id = 70),

    /**
     * Disease, which drains a player's stats at set intervals, excluding Hitpoints and Prayer.
     * Indicates the player being under the effects of a disease, which periodically drains stats.
     */
    DISEASE(id = 4),

    /**
     * Venom damages entities over time, increasing the damage by one every four hit splat cycles, capping out at 20.
     */
    VENOM(id = 5),

    /**
     * Used to represent an NPC healing its hitpoints, though it is mostly reserved for boss encounters.
     */
    NPC_HEAL(id = 6), // generally used for NPCs -> NO player distinction

    /**
     * Indicates a hit of zero damage.
     */
    BLOCK(id = 12, tinted = 13),
    BLOCK_TINTED(id = 13),
    MISS(id = 12, tinted = 13),
    MISS_TINTED(id = 13),

    /**
     * Indicates a successful hit that dealt damage. In the Nightmare Zone,
     * drinking an absorption potion will cause all monster-inflicted damage to be zero,
     * but will still use this red damage hit splat to indicate the successful hit.
     */
    HIT(id = 16, tinted = 17, max = 43),
    HIT_TINTED(id = 17),
    HIT_MAX(43),

    /**
     * Indicates damage dealt to Verzik Vitur’s, The Nightmare’s and Tempoross’ shields.
     * While the inactive icon is defined in the config, it seems to be a placeholder.
     */
    SHIELD(id = 18, tinted = 19, max = 44),
    SHIELD_TINTED(id = 19, tinted = 19, max = 44),
    SHIELD_MAX(id = 44, tinted = 19, max = 44),

    /**
     * Indicates damage dealt to Zalcano’s stone armour. The dynamic id is never actually used.
     * Armour Shown when damaging Zalcano's stone armour.
     */
    ARMOUR(id = 20, tinted = 21, max = 45),
    ARMOUR_TINTED(id = 21, tinted = 21, max = 45),
    ARMOUR_MAX(id = 45, tinted = 21, max = 45),

    /**
     * Indicates totems being healed while charging them during the fight against The Nightmare.
     * Shown when The Nightmare's totems are charged or "healed".
     */
    CHARGE(id = 22, tinted = 23, max = 46),
    CHARGE_TINTED(id = 23, tinted = 23, max = 46),
    CHARGE_MAX(id = 46, tinted = 23, max = 46),


    /**
     * Indicates totems being damaged while the parasites discharge them during the fight against The Nightmare.
     * The dynamic is never actually used.
     * Shown when The Nightmare's totems are uncharged or "damaged" by parasites.
     */
    UNCHARGE(id = 24, tinted = 25, max = 47),
    UNCHARGE_TINTED(id = 25, tinted = 25, max = 47),
    UNCHARGE_MAX(id = 47, tinted = 25, max = 47),

    /**
     * Dodged damage from a negated non-typeless attack. Currently unused.
     */
    POISE(id = 53, tinted = 38, max = 55),
    POISE_TINTED(id = 38, tinted = 38, max = 55),
    POISE_MAX(id = 55, tinted = 38, max = 55),

    /**
     * Alternate charge hitsplat representing the growth of the Palm of Resourcefulness,
     * as well as the restoration of Kephri's scarab shield within the Tombs of Amascut raid.
     */
    ALT_CHARGE(id = 39, tinted = 40),
    ALT_CHARGE_TINTED(id = 40, tinted = 40),

    /**
     * Alternate uncharge hitsplat representing the crocodile's damage towards the Palm of Resourcefulness.
     */
    ALT_UNCHARGE(id = 41, tinted = 42),
    ALT_UNCHARGE_TINTED(id = 42, tinted = 42),

    /**
     * Shown when draining the Phantom Muspah's prayer shield. Currently, the max hit variant is unused.
     */
    PRAYER_DRAIN(id = 59, tinted = 60, max = 61),
    PRAYER_DRAIN_TINTED(id = 60, tinted = 60, max = 61),
    PRAYER_DRAIN_MAX(id = 61, tinted = 60, max = 61),

    /**
     * Bleed attack indicating damage dealt over time from Vardorvis' Swinging Axes attack.
     */
    BLEED(id = 67),

    /**
     * Used to represent the restoration of the player's sanity.
     */
    RESTORE_SANITY(id = 72),

    /**
     * Sanity drain from Lost Souls and The Whisperer within the Shadow Realm.
     */
    DRAIN_SANITY(id = 71);


    companion object {
        val TINTED_HITMARK_VARBIT = 10236
        val values = values()
        fun get(id: Int): HitMask? {
            values.forEach {
                if (it.id == id) return it
            }
            return null
        }
    }
}