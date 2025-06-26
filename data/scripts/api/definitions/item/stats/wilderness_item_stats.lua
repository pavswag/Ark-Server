require("ScriptLoader")

local itemStats = {
    [BANDOS_GODSWORD_OR] = {slot = WEAPON_SLOT, aslash = 182, acrush = 114, str = 184, prayer = 14, aspeed = 5},
    [ZAMORAK_GODSWORD_OR] = {slot = WEAPON_SLOT, aslash = 182, acrush = 114, str = 184, prayer = 14, aspeed = 5},
    [SARADOMIN_GODSWORD_OR] = {slot = WEAPON_SLOT, aslash = 182, acrush = 114, str = 184, prayer = 14, aspeed = 5},
    [ARMADYL_GODSWORD_OR] = {slot = WEAPON_SLOT, aslash = 182, acrush = 114, str = 184, prayer = 14, aspeed = 5},
    [TUMEKENS_SHADOW] = {slot = WEAPON_SLOT, amagic = 212, dmagic = 20, mdmg = 42, prayer = 1, aspeed = 3},
    [DEMON_X_STAFF] = {slot = WEAPON_SLOT, amagic = 200, dmagic = 50, mdmg = 58, prayer = 1, aspeed = 3},
    [SEREN_GODBOW] = {slot = WEAPON_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [DEMON_X_BOW] = {slot = WEAPON_SLOT, arange = 275, rstr = 250, aspeed = 3},
    [MASORI_MASK_F] = {slot = HAT_SLOT, arange = 57, dstab = 75, dslash = 75, dcrush = 75, dmagic = 75, drange = 75, rstr = 10, prayer = 1},
    [MASORI_BODY_F] = {slot = CHEST_SLOT, arange = 57, dstab = 75, dslash = 75, dcrush = 75, dmagic = 75, drange = 75, rstr = 20, prayer = 1},
    [MASORI_CHAPS_F] = {slot = LEGS_SLOT, arange = 57, dstab = 75, dslash = 75, dcrush = 75, dmagic = 75, drange = 75, rstr = 30, prayer = 1},
    [VIRTUS_MASK] = {slot = HAT_SLOT, amagic = 150, dmagic = 50, mdmg = 42, prayer = 1, aspeed = 3},
    [VIRTUS_ROBE_TOP] = {slot = CHEST_SLOT, amagic = 150, dmagic = 50, mdmg = 42, prayer = 1, aspeed = 3},
    [VIRTUS_ROBE_LEGS] = {slot = LEGS_SLOT, amagic = 150, dmagic = 50, mdmg = 42, prayer = 1, aspeed = 3},
    [DEMON_X_SWORD] = {slot = WEAPON_SLOT, amagic = 200, dmagic = 50, mdmg = 58, prayer = 1, aspeed = 3},
    [DEMON_X_SPEAR] = {slot = WEAPON_SLOT, amagic = 200, dmagic = 50, mdmg = 58, prayer = 1, aspeed = 3},
    [BLAZING_BLOWPIPE] = {slot = WEAPON_SLOT, amagic = 212, dmagic = 20, mdmg = 42, prayer = 1, aspeed = 3},
    [SANGUINE_TORVA_FULL_HELM] = {slot = HAT_SLOT, amagic = 200, dmagic = 50, mdmg = 58, prayer = 1, aspeed = 3},
    [SANGUINE_TORVA_PLATEBODY] = {slot = CHEST_SLOT, amagic = 200, dmagic = 50, mdmg = 58, prayer = 1, aspeed = 3},
    [SANGUINE_TORVA_PLATELEGS] = {slot = LEGS_SLOT, amagic = 200, dmagic = 50, mdmg = 58, prayer = 1, aspeed = 3},
    [ARKCANE_GRIMOIRE] = {slot = WEAPON_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [26551] = {slot = AMULET_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [SCRUBFOOT] = {slot = WEAPON_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [10556] = {slot = WEAPON_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [COLLECTORS_AMULET] = {slot = AMULET_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [CANDY_CANE_TWISTED_BOW] = {slot = WEAPON_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [CANDY_CANE_SCYTHE] = {slot = WEAPON_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [33162] = {slot = AMULET_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [SANGUINE_SCYTHE_OF_VITUR] = {slot = WEAPON_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [HOLY_SCYTHE_OF_VITUR] = {slot = WEAPON_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [DRAGON_CLAWS_OR] = {slot = WEAPON_SLOT, arange = 150, rstr = 85, aspeed = 3},
    [TWISTED_ANCESTRAL_HAT] = {slot = HAT_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [TWISTED_ANCESTRAL_ROBE_TOP] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [TWISTED_ANCESTRAL_ROBE_BOTTOM] = {slot = LEGS_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [DRAGON_HUNTER_CROSSBOW_B] = {slot = WEAPON_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [ABYSSAL_WHIP_OR] = {slot = WEAPON_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [ABYSSAL_TENTACLE_OR] = {slot = WEAPON_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [HOLY_GHRAZI_RAPIER] = {slot = WEAPON_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [RUNE_CROSSBOW_OR] = {slot = WEAPON_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [BLOOD_SCYTHE] = {slot = WEAPON_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [DEMON_X_SCYTHE] = {slot = WEAPON_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [ASCENSION_CROSSBOW] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [HOOD_OF_RUIN] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [ROBE_TOP_OF_RUIN] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [ROBE_BOTTOM_OF_RUIN] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [GLOVES_OF_RUIN] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [SOCKS_OF_RUIN] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [CLOAK_OF_RUIN] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [NOXIOUS_STAFF] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [VOID_MELEE_HELM_OR] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [VOID_RANGER_HELM_OR] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [VOID_MAGE_HELM_OR] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [HOLY_SANGUINESTI_STAFF] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [VOID_KNIGHT_GLOVES_OR] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [DRAGON_GUARD_HELM] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [DRAGON_GUARD_CHEST] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [DRAGON_GUARD_BOTTOMS] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [ELIDINIS_WARD_OR] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [CAPE_OF_MALAR] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [MASK_OF_MALAR] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [CHEST_OF_MALAR] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [BOTTOMS_OF_MALAR] = {slot = CHEST_SLOT, amagic = 62, arange = -2, dstab = 66, dslash = 54, dcrush = 69, dmagic = 43, mdmg = 20},
    [TRIDENT_OF_THE_SWAMP] = {slot = WEAPON_SLOT, aslash = 60, amagic = 50, dmagic = 60, mdmg = 25, prayer = 1, aspeed = 4},
    [UNCHARGED_TOXIC_TRIDENT] = {slot = WEAPON_SLOT, aslash = 60, amagic = 50, dmagic = 60, mdmg = 25, prayer = 1, aspeed = 4},
    [ELITE_VOID_TOP_OR] = {slot = WEAPON_SLOT, aslash = 60, amagic = 50, dmagic = 60, mdmg = 25, prayer = 1, aspeed = 4},
    [ELITE_VOID_ROBE_OR] = {slot = WEAPON_SLOT, aslash = 60, amagic = 50, dmagic = 60, mdmg = 25, prayer = 1, aspeed = 4},
    [CRUCIFEROUS_CODEX] = {slot = WEAPON_SLOT, aslash = 60, amagic = 50, dmagic = 60, mdmg = 25, prayer = 1, aspeed = 4},
    [ZARYTE_VAMBRACES] = {slot = WEAPON_SLOT, aslash = 60, amagic = 50, dmagic = 60, mdmg = 25, prayer = 1, aspeed = 4},
    [ANTISANTA_MASK] = {slot = WEAPON_SLOT, aslash = 60, amagic = 50, dmagic = 60, mdmg = 25, prayer = 1, aspeed = 4},
    [ANTISANTA_JACKET] = {slot = WEAPON_SLOT, aslash = 60, amagic = 50, dmagic = 60, mdmg = 25, prayer = 1, aspeed = 4},
    [ANTISANTA_PANTALOONS] = {slot = WEAPON_SLOT, aslash = 60, amagic = 50, dmagic = 60, mdmg = 25, prayer = 1, aspeed = 4},
    [ANTISANTA_GLOVES] = {slot = WEAPON_SLOT, aslash = 60, amagic = 50, dmagic = 60, mdmg = 25, prayer = 1, aspeed = 4},
    [ANTISANTA_BOOTS] = {slot = WEAPON_SLOT, aslash = 60, amagic = 50, dmagic = 60, mdmg = 25, prayer = 1, aspeed = 4},
    [HALLOWEEN_LEGS] = {slot = HAT_SLOT, amagic = 40, dmagic = 40},
    [HALLOWEEN_BODY] = {slot = CAPE_SLOT, amagic = 40, dmagic = 40},
    [HALLOWEEN_HEAD] = {slot = CHEST_SLOT, amagic = 40, dmagic = 40},
    [HALLOWEEN_BOOTS] = {slot = FEET_SLOT, amagic = 40, dmagic = 40},
    [HALLOWEEN_GLOVES] = {slot = HANDS_SLOT, amagic = 40, dmagic = 40},
    [HALLOWEEN_CAPE] = {slot = CAPE_SLOT, amagic = 40, dmagic = 40},
    [WITCH_HAT] = {slot = HAT_SLOT},
    [WITCH_TOP] = {slot = CHEST_SLOT},
    [WITCH_ROBES] = {slot = LEGS_SLOT},
    [WITCH_BOOTS] = {slot = FEET_SLOT},
    [WITCH_CAPE] = {slot = CAPE_SLOT}
}

local ItemStats = luajava.bindClass("io.kyros.model.definitions.ItemStats")
local itemCount = 0

for itemId, stats in pairs(itemStats) do
    ItemStats:addWildernessStat(
            itemId,
            stats.slot, stats.astab or 0, stats.aslash or 0, stats.acrush or 0,
            stats.amagic or 0, stats.arange or 0, stats.dstab or 0, stats.dslash or 0,
            stats.dcrush or 0, stats.dmagic or 0, stats.drange or 0, stats.str or 0,
            stats.rstr or 0, stats.mdmg or 0, stats.prayer or 0, stats.aspeed or 0
    )
    itemCount = itemCount + 1
end
print("Wilderness stat items size: ", itemCount)

function getItemStats(itemId)
    return wildernessStatsMap[itemId]
end

ScriptLoader.autoRegister()
