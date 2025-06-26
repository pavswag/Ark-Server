package io.kyros.content.perky;

public enum Perks {
//26547
    DWARF_OVERLOAD(33073, PerkType.COMBAT), // Chance to auto reload cannon on kill -- Done
    PK_MASTER(33074, PerkType.COMBAT), // Double PK Points -- Done
    MAGIC_MASTER(33090, PerkType.COMBAT),//Chance to save rune's when casting spells -- Done
    YIN_YANG(33091, PerkType.COMBAT),//25% slower prayer drain -- Done
    NOVICE_ZERK(33102, PerkType.COMBAT),//Gain 5% Melee Damage Bonus -- Done
    NOVICE_MAGICIAN(33103, PerkType.COMBAT),//Gain 5% Magic Damage Bonus. -- Done
    NOVICE_RANGER(33104, PerkType.COMBAT),//Gain 5% Range Damage Bonus. -- Done
    PRO_ZERK(33105, PerkType.COMBAT),//Gain 10% Melee Damage Bonus -- Done
    PRO_MAGICIAN(33106, PerkType.COMBAT),//Gain 10% Magic Damage -- Done
    PRO_RANGER(33107, PerkType.COMBAT),//Gain 10% Range Damage Bonus. -- Done
    SWEDISH_SWINDLE(33108, PerkType.COMBAT),//10% Drop Rate  -- Done
    MONK_HEALS(33117, PerkType.COMBAT),//Chance to double health when using healer icon?  -- Done
    DRAGON_FIRE(33118, PerkType.COMBAT),//Permanent Anti-fire potion effect  -- Done
    OVERLOAD_PROTECTION(33119, PerkType.COMBAT),//Overload's no longer deal damage  -- Done
    CANNON_EXTENDER(33121, PerkType.COMBAT),//100 extra cannon ball's can be held in your cannon  -- Done
//26546
    RUNECRAFTER(33079, PerkType.SKILLING),//Player's will no longer receive rune's lower than or equal to level 20, (requires 99 runecrafting) -- Done
    PRO_FLETCHER(33080, PerkType.SKILLING),//Fletching logs & Stringing bows grant 50% more fletching exp-- Done
    SKILLED_THIEF(33087, PerkType.SKILLING),//Chance to double thieving xp-- Done
    CRAFTING_GURU(33088, PerkType.SKILLING),//Chance to double crafting xp-- Done
    HOT_HANDS(33089, PerkType.SKILLING),//Chance to double cooking xp-- Done
    DEMON_SLAYER(33093, PerkType.SKILLING),//Chance to double demon hunter xp-- Done
    SLAYER_MASTER(33094, PerkType.SKILLING),//Chance to double slayer xp-- Done
    PYROMANIAC(33095, PerkType.SKILLING),//Chance to double firemaking xp-- Done
    SKILLED_HUNTER(33096, PerkType.SKILLING),//Chance to double hunter xp-- Done
    MOLTEN_MINER(33097, PerkType.SKILLING),//Chance to double mining xp-- Done
    WOODCHIPPER(33098, PerkType.SKILLING),//Chance to double woodcutting xp-- Done
    BARE_HANDS(33099, PerkType.SKILLING),//Chance to double fishing xp-- Done
    BARE_HANDS_X3(33100, PerkType.SKILLING),//Chance to triple your fish when fishing-- Done
    PRAYING_RESPECTS(33101, PerkType.SKILLING),//Chance to double prayer xp-- Done
    PURE_SKILLS(33122, PerkType.SKILLING),//2x Skilling xp to all skills-- Done

    RAIDERS_LUCK(33109, PerkType.DONATION),//10% Chance to get an extra key $40-- Done
    CLEPTO_MANIAC(33110, PerkType.DONATION),//Collect all drop's $ 75-- Done
    MAGIC_PAPER(33111, PerkType.DONATION),//Note all drop's $ 50-- Done
    POT_OF_GOLD(33112, PerkType.DONATION),//20% increased drop rate $ 150-- Done
    MYSTERY_MADNESS(33113, PerkType.DONATION),//double reward's from any mystery box $ 50-- Done
//26548
    IRON_GIANT(33077, PerkType.MISC),//15% chance to smelt an extra bar when smelting bar's (extra item's will go to ground or bank if no inventory space)--done
    SLAYER_OVERRIDE(33078, PerkType.MISC),//Players will earn 100% more slayer point's upon completing a slayer task--done
    THE_FUSIONIST(33072, PerkType.MISC), // Chance to save Materials/Platinum when fusing items.--done
    WILDY_SLAYER(33075, PerkType.MISC), // Earn double slayer points while skulled in wilderness--done
    SNEAKY_SNEAKY(33076, PerkType.MISC), //Chance to double pickpocket loot when stealing from master farmer--done
    CHISEL_MASTER(33081, PerkType.MISC),//Chance to 50% more bolt tips per gem, arrow  tips, dart tips--done
    AVAS_ACCOMPLICE(33082, PerkType.MISC),//Chance to save ammo when using ranged weapon--done
    DEEPER_POCKETS(33083, PerkType.MISC),//Chance to collect drop's (like collector but on chance)--done
    RECHARGER(33084, PerkType.MISC),//charges have a 60% chance to be saved. (chargeable items only)--done
    MAGIC_PAPER_CHANCE(33085, PerkType.MISC),//chance to note drops--done
    DRAGON_BAIT(33086, PerkType.MISC),//50% increase hunter catch rate--done
    FOUNDRY_MASTER(33092, PerkType.MISC),//Chance to double Foundry Points when burning--done
    CASKET_MASTER(33114, PerkType.MISC),//Chance of an extra clue casket upon opening.--done
    VOTING_KING(33115, PerkType.MISC),//2 extra vote point's per vote--done
    PET_LOCATOR(33116, PerkType.MISC),//Extra Chance to find a pet--done
    LUCKY_COIN(33120, PerkType.MISC),//Chance to save 20% when donating to the well--done
    PC_PRO(33123, PerkType.MISC),//3x Pest control points--done
    SLAYER_GURU(33124, PerkType.MISC)//Double slayer task size.--done
    ;

    //33072 -> 33124
    public int itemID;
    public PerkType perkType;

    Perks(int itemID, PerkType perkType) {
        this.itemID = itemID;
        this.perkType = perkType;
    }

}
