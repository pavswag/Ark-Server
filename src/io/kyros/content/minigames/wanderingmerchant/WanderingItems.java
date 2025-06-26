package io.kyros.content.minigames.wanderingmerchant;

import io.kyros.model.Items;
import lombok.Setter;

public enum WanderingItems {

    // Defining items with their respective IDs, names, and updated costs
    WRAITH_SWORD(33430, "Wraith Sword", 2000),
    WRAITH_SCYTHE(33431, "Wraith Scythe", 2000),
    WRAITH_SPEAR(33432, "Wraith Spear", 2000),
    WRAITH_STAFF(33433, "Wraith Staff", 2000),
    WRAITH_BOW(33434, "Wraith Bow", 2000),
    WRAITH_CROSSBOW(33435, "Wraith Crossbow", 2000),
    ARTORIAS_HELM(33296, "Artorias Helm", 1500),
    ARTORIAS_BODY(33297, "Artorias Body", 1500),
    ARTORIAS_LEGS(33298, "Artorias Legs", 1500),
    ICE_HELM(33292, "Ice Helm", 800),
    ICE_BODY(33293, "Ice Body", 800),
    ICE_LEGS(33294, "Ice Legs", 800),
    CHAOS_ROBE_TOP(27115, "Chaos Robe (or) Top", 800),
    CHAOS_ROBE_BOTTOM(27117, "Chaos Robe (or) Bottom", 800),
    CHAOS_ROBE_HOOD(27119, "Chaos Robe (or) Hood", 800),
    PLAGUE_HELM(33311, "Plague Helm", 650),
    PLAGUE_BODY(33312, "Plague Body", 650),
    PLAGUE_LEGS(33313, "Plague Legs", 650),
    STARLIGHT_HELM(33324, "Starlight Helm", 800),
    STARLIGHT_BODY(33325, "Starlight Body", 800),
    STARLIGHT_LEGS(33326, "Starlight Legs", 800),
    STARLIGHT_GLOVES(33329, "Starlight Gloves", 800),
    REVERIE_HELM(33308, "Reverie Helm", 650),
    REVERIE_BODY(33309, "Reverie Body", 650),
    REVERIE_LEGS(33310, "Reverie Legs", 650),
    PURGING_STAFF(29594, "Purging Staff", 1000),
    CORRUPTED_DARK_BOW(29599, "Corrupted Dark Bow", 1000),
    HEREDIT_ITEMS_1(33406, "Heredit Items 1", 1000),
    HEREDIT_ITEMS_2(33407, "Heredit Items 2", 1000),
    HEREDIT_ITEMS_3(33408, "Heredit Items 3", 1000),
    HEREDIT_ITEMS_4(33409, "Heredit Items 4", 1000),
    HEREDIT_ITEMS_5(33410, "Heredit Items 5", 1000),
    HEREDIT_ITEMS_6(33411, "Heredit Items 6", 1000),
    EMBER_HELM(33343, "Ember Helm", 650),
    EMBER_BODY(33344, "Ember Body", 650),
    EMBER_LEGS(33345, "Ember Legs", 650),
    KRATOS_PET(30022, "Kratos Pet", 325),
    HALLOWED_RING(24725, "Hallowed Ring", 800),
    HALLOWED_AMULET(24731, "Hallowed Amulet", 800),
    HALLOWED_GLOVES(33402, "Hallowed Gloves", 800),
    HALLOWED_BOOTS(33403, "Hallowed Boots", 800),
    SANGUINE_TORVA_HELM(28254, "Sanguine Torva Helm", 500),
    SANGUINE_TORVA_BODY(28256, "Sanguine Torva Body", 500),
    SANGUINE_TORVA_LEGS(28258, "Sanguine Torva Legs", 500),
    PERNIX_HELM(33144, "Pernix Helm", 500),
    PERNIX_BODY(33145, "Pernix Body", 500),
    PERNIX_LEGS(33146, "Pernix Legs", 500),
    DARKNESS_HOOD(20128, "Darkness Hood", 500),
    DARKNESS_BODY(20131, "Darkness Body", 500),
    DARKNESS_LEGS(20137, "Darkness Legs", 500),
    DARKNESS_CAPE(20211, "Darkness Cape", 500),
    RING_OF_WEALTH_I5(21129, "Ring of Wealth (i5)", 100),
    ROC_PET(30021, "Roc Pet", 250),
    CORRUPT_BEAST(30020, "Corrupt Beast", 250),
    SCYTHE_OF_VITOR(22325, "Scythe of Vitor", 250),
    SANG_STAFF(22323, "Sanguine Staff", 250),
    TWISTED_BOW(20997, "Twisted Bow", 250),
    LIGHTBEARER_RING(25975, "Lightbearer Ring", 250),
    DARKNESS_BOOTS(20134, "Darkness Boots", 500),
    DARKNESS_GLOVES(20140, "Darkness Gloves", 500),
    ROW_I4(20787, "Ring of Wealth (i4)", 100),
    KLIK(30014, "Klik", 250),
    HOLY_GHRAZI(25734, "Holy Ghrazi", 250),
    VIRTUS_HELM(33141, "Virtus Helm", 250),
    VIRTUS_BODY(33142, "Virtus Body", 250),
    VIRTUS_LEGS(33143, "Virtus Legs", 250),
    NOMAD_10M(33429, "10m Nomad", 250),
    ZART_CBOW(26374, "Zart Crossbow", 100),
    NIGHTMARE_STAFF(24422, "Nightmare Staff", 100),
    FOUNDRY_MASTER(33092, "Foundry Master", 100),
    PURE_SKILLS(33122, "Pure Skills", 325),
    POT_OF_GOLD(33112, "Pot of Gold", 325),
    CLEPTO(33110, "Clepto", 325),
    GHRAZI_RAPIER(22324, "Ghrazi Rapier", 100),
    KARIS(25979, "Karis", 100),
    ELIDINIS_WARD(25985, "Elidinis' Ward", 100),
    ELDER_MAUL_OR(27100, "Elder Maul (or)", 100),
    T_HELM(26382, "T Helm", 100),
    T_CHEST(26384, "T Chest", 100),
    T_LEGS(26386, "T Legs", 100),
    ANCIENT_GDSW(26233, "Ancient Godsword", 100),
    ZART_VAMBS(26235, "Zart Vambraces", 100),
    ELYSIAN_SPIRIT_SHIELD(12817, "Elysian Spirit Shield", 100),
    SWEDISH_SWINDLE(33108, "Swedish Swindle", 250),
    ROW_I3(20788, "Ring of Wealth (i3)", 100),
    DWARF_OVERLOAD(33073, "Dwarf Overload", 100),
    VESTAS_LONGSWORD(Items.VESTAS_LONGSWORD, "Vesta's Longsword", 100),
    STATIUSS_WARHAMMER(Items.STATIUSS_WARHAMMER, "Statius's Warhammer", 100),
    VESTAS_CHAINBODY(Items.VESTAS_CHAINBODY, "Vesta's Chainbody", 100),
    VESTAS_PLATESKIRT(Items.VESTAS_PLATESKIRT, "Vesta's Plateskirt", 100),
    MORRIGANS_COIF(Items.MORRIGANS_COIF, "Morrigan's Coif", 100),
    MORRIGANS_LEATHER_BODY(Items.MORRIGANS_LEATHER_BODY, "Morrigan's Leather Body", 100),
    MORRIGANS_LEATHER_CHAPS(Items.MORRIGANS_LEATHER_CHAPS, "Morrigan's Leather Chaps", 100),
    VESTAS_SPEAR(Items.VESTAS_SPEAR, "Vesta's Spear", 100),
    ZURIELS_STAFF(Items.ZURIELS_STAFF, "Zuriel's Staff", 100),
    ARMA_HELM_OR(26714, "Armadyl Helm (or)", 100),
    ARMA_TORSO_OR(26715, "Armadyl Torso (or)", 100),
    ARMA_LEGS_OR(26716, "Armadyl Legs (or)", 100),
    ANCIENT_CERE_TOP(26221, "Ancient Ceremonial Top", 100),
    ANCIENT_CERE_LEGS(26223, "Ancient Ceremonial Legs", 100),
    ANCIENT_CERE_HELM(26225, "Ancient Ceremonial Helm", 100),
    ELDRITCH_ORB(24517, "Eldritch Orb", 100),
    HARMONISED_ORB(24511, "Harmonised Orb", 100),
    VOLATILE_ORB(24514, "Volatile Orb", 100),
    DRAGON_HUNTER_CROSSBOW_B(25918, "Dragon Hunter Crossbow (b)", 100),
    PC_PRO(33123, "PC Pro", 100),
    LUCKY_COIN(33120, "Lucky Coin", 100),
    CASKET_MASTER(33114, "Casket Master", 100),
    MAGIC_PAPER_CHANCE(33085, "Magic Paper Chance", 100),
    DEEPER_POCKETS(33083, "Deeper Pockets", 100),
    PRO_RANGER(33107, "Pro Ranger", 100),
    PRO_MAGICIAN(33106, "Pro Magician", 100),
    PRO_ZERK(33105, "Pro Zerk", 100),
    OSMUNTEN_FANG(26219, "Osmumten's Fang", 100),
    RAIDERS_LUCK(33109, "Raider's Luck", 100),
    INQUISITOR_MACE(24417, "Inquisitor's Mace", 100),
    CRYSTAL_BLADE(23995, "Crystal Blade", 100),
    INQUISITOR_HELM(24419, "Inquisitor's Helm", 100),
    INQUISITOR_PLATE(24420, "Inquisitor's Platebody", 100),
    INQUISITOR_SKIRT(24421, "Inquisitor's Skirt", 100),
    ZURIELS_HOOD(Items.ZURIELS_HOOD, "Zuriel's Hood", 100),
    ZURIELS_ROBE_BOTTOM(Items.ZURIELS_ROBE_BOTTOM, "Zuriel's Robe Bottom", 100),
    ZURIELS_ROBE_TOP(Items.ZURIELS_ROBE_TOP, "Zuriel's Robe Top", 100),
    STATIUSS_FULL_HELM(Items.STATIUSS_FULL_HELM, "Statius's Full Helm", 100),
    STATIUSS_PLATEBODY(Items.STATIUSS_PLATEBODY, "Statius's Platebody", 100),
    STATIUSS_PLATELEGS(Items.STATIUSS_PLATELEGS, "Statius's Platelegs", 100),
    DRAGON_HUNTER_LANCE(22978, "Dragon Hunter Lance", 100),
    TWISTED_ROBE_BOTTOM(24668, "Twisted Robe Bottom", 100),
    TWISTED_ROBE_TOP(24666, "Twisted Robe Top", 100),
    TWISTED_HAT(24664, "Twisted Hat", 100),
    ATTACKER_ICON(10556, "Attacker Icon", 100),
    COLLECTOR_ICON(10557, "Collector Icon", 100),
    DEFENDER_ICON(10558, "Defender Icon", 100),
    HEALER_ICON(10559, "Healer Icon", 100),
    BANDOS_BOOTS_OR(26720, "Bandos Boots (or)", 100),
    BANDOS_TASSETS_OR(26719, "Bandos Tassets (or)", 100),
    BANDOS_CHESTPLATE_OR(26718, "Bandos Chestplate (or)", 100),
    OVERLOAD_PROTECTION(33119, "Overload Protection", 100),
    CANNON_EXTENDER(33121, "Cannon Extender", 100),
    SLAYER_OVERRIDE(33078, "Slayer Override", 100),
    NOMAD_1M(33428, "1m Nomad", 100),
    CRAWS_BOW_U(22547, "Craw's Bow (u)", 100),
    CRAWS_BOW(22550, "Craw's Bow", 100),
    VIGGS_MACE_U(22542, "Viggora's Mace (u)", 100),
    VIGGS_MACE(22545, "Viggora's Mace", 100),
    THAMS_SCEPTRE_U(22552, "Thammaron's Sceptre (u)", 100),
    THAMS_SCEPTRE(22555, "Thammaron's Sceptre", 100),
    DRAGON_FIRE(33118, "Dragon Fire", 100),
    DRAGON_WARHAMMER_OR(26710, "Dragon Warhammer (or)", 100),
    DRAGON_CLAWS_OR(26708, "Dragon Claws (or)", 100),
    DRAGON_HUNTER_CROSSBOW_T(25916, "Dragon Hunter Crossbow (t)", 100),
    PK_MASTER(33074, "PK Master", 100),
    MAGIC_MASTER(33090, "Magic Master", 100),
    NOVICE_ZERK(33102, "Novice Zerk", 100),
    YIN_YANG(33091, "Yin Yang", 100),
    NOVICE_MAGICIAN(33103, "Novice Magician", 100),
    NOVICE_RANGER(33104, "Novice Ranger", 100),
    WILDY_SLAYER(33075, "Wilderness Slayer", 100);

    private final int id;
    private final String name;
    @Setter
    private int cost;

    WanderingItems(int id, String name, int cost) {
        this.id = id;
        this.name = name;
        this.cost = cost;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public static int getCost(int id) {
        for (WanderingItems value : WanderingItems.values()) {
            if (value.getId() == id) {
                return value.getCost();
            }
        }

        return 0;
    }
}