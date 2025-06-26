package io.kyros.content.upgrade;

import com.google.common.base.Preconditions;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.definitions.ItemStats;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static io.kyros.content.upgrade.UpgradeMaterials.UpgradeType.*;

@Getter
public enum UpgradeMaterials {


    /* Weapons */
    //Dragon Scimitar			    //Dragon Scimitar (or)
    DRAGON_SCIMITAR(WEAPON,60, new GameItem(4587, 1), new GameItem(20000, 1), 250_000, 50, 100,false),
    //Abyssal Whip			        //Abyssal Whip (or)
    ABYSSAL_WHIP(WEAPON, 70, new GameItem(4151, 1), new GameItem(26482, 1), 500_000, 50, 100,false),

    ABYSSAL_WHIP_ORN(WEAPON, 75, new GameItem(26482, 1), new GameItem(12774, 1), 2_500_000, 35, 100,true),

    ABYSSAL_TENTACLE(WEAPON, 80, new GameItem(12006, 1), new GameItem(26484, 1), 3_000_000, 35, 250,true),

    ARMADYL_GODSWORD(WEAPON, 85, new GameItem(11802, 1), new GameItem(20368, 1), 10_000_000, 50, 250,true),

    SARADOMIN_GODSWORD(WEAPON, 85, new GameItem(11806, 1), new GameItem(20372, 1), 10_000_000, 50, 250,true),

    ZAMORAK_GODSWORD(WEAPON, 85, new GameItem(11808, 1), new GameItem(20374, 1), 10_000_000, 50, 250,true),

    BANDOS_GODSWORD(WEAPON, 85, new GameItem(11804, 1), new GameItem(20370, 1), 10_000_000, 50, 250,true),

    Dragon_CLAWS(WEAPON, 60, new GameItem(20784, 1), new GameItem(26708, 1), 7_500_000, 40, 100,true),

    DRAGON_WARHAMMER(WEAPON, 60, new GameItem(13576, 1), new GameItem(26710, 1), 10_000_000, 40, 100,true),

    HEAVY_BALLISTA(WEAPON, 70, new GameItem(19481, 1), new GameItem(26712, 1), 5_000_000, 50, 150,true),

    Rune_Crossbow(WEAPON, 41, new GameItem(9185, 1), new GameItem(26486, 1), 500_000, 50, 75,false),

    DRAGON_HUNTER_CROSSBOW(WEAPON, 61, new GameItem(21012, 1), new GameItem(25916, 1), 5_000_000, 65, 250,false),

    DRAGON_HUNTER_CROSSBOW_T(WEAPON, 71, new GameItem(25916, 1), new GameItem(25918, 1), 10_000_000, 45, 200,true),

    ZARYTE_CROSSBOW(WEAPON, 99, new GameItem(26374, 1), new GameItem(33206, 1), 250_000_000, 25, 10000, true),

    Twisted_bow(WEAPON, 85, new GameItem(20997, 1), new GameItem(33058, 1), 250_000_000, 15, 10000,true),

    Ghrazi_Rapier(WEAPON, 85, new GameItem(22324, 1), new GameItem(25734, 1), 25_000_000, 40, 5000,true),

    OSMUMTEN_FANG(WEAPON, 99, new GameItem(26219, 1), new GameItem(27246, 1), 500_000_000, 25, 10000,true),

    KERIS_PARTISAN(WEAPON, 99, new GameItem(25979, 1), new GameItem(27287, 1), 500_000_000, 25, 10000,true),

    SCYTHE_OF_VITUR(WEAPON, 90, new GameItem(22325, 1), new GameItem(25736, 1), 150_000_000, 50, 10000,true),

    HOLY_SCYTHE_OF_VITUR(WEAPON, 99, new GameItem(25736, 1), new GameItem(25739, 1), 300_000_000, 40, 15000,true),

    SANGUINGE_SCYTHE(WEAPON, 99, new GameItem(25739, 1), new GameItem(33184, 1), 500_000_000, 20, 10000,true),

    ANCIENT_STAFF(WEAPON, 45, new GameItem(4675, 1), new GameItem(27624, 1), 50_000_000, 40, 10000, true),

    Sanguinesti_Staff(WEAPON, 90, new GameItem(22323, 1), new GameItem(25731, 1), 250_000_000, 40, 5000,true),

    KODAI_WAND(WEAPON, 90, new GameItem(21006, 1), new GameItem(33149, 1), 250_000_000, 40, 10000,true),

    NOXIOUS_STAFF(WEAPON, 99, new GameItem(33149, 1), new GameItem(27275, 1), 500_000_000, 20, 10000,true),

    STARLIGHT_SWORD(WEAPON, 99, new GameItem(33202, 1), new GameItem(33329, 1), 5_000_000_000L, 5, 50000,true),

    /* Armour */

    VOID_MAGE_HELM(ARMOUR, 45, new GameItem(11663, 1), new GameItem(24183, 1), 500_000, 100, 200,false),

    VOID_RANGER_HELM(ARMOUR, 45, new GameItem(11664, 1), new GameItem(24184, 1), 500_000, 100, 200,false),

    VOID_MELEE_HELM(ARMOUR, 45, new GameItem(11665, 1), new GameItem(24185, 1), 500_000, 100, 200,false),

    ELITE_VOID_TOP(ARMOUR, 45, new GameItem(13072, 1), new GameItem(26463, 1), 500_000, 100, 100,false),

    ELITE_VOID_BOTTOM(ARMOUR, 45, new GameItem(13073, 1), new GameItem(26465, 1), 500_000, 100, 100,false),

    VOID_KNIGHT_GLOVES(ARMOUR, 45, new GameItem(8842, 1), new GameItem(24182, 1), 500_000, 100, 100,false),

    VOID_MAGE_HELM_I(ARMOUR, 50, new GameItem(24183, 1), new GameItem(26473, 1), 10_000_000, 100, 300,true),

    VOID_RANGER_HELM_I(ARMOUR, 50, new GameItem(24184, 1), new GameItem(26475, 1), 10_000_000, 100, 300,true),

    VOID_MELEE_HELM_I(ARMOUR, 50, new GameItem(24185, 1), new GameItem(26477, 1), 10_000_000, 100, 300,true),

    ELITE_VOID_TOP_OR(ARMOUR, 50, new GameItem(26463, 1), new GameItem(26469, 1), 10_000_000, 100, 100,true),

    ELITE_VOID_ROBE_OR(ARMOUR, 50, new GameItem(26465, 1), new GameItem(26471, 1), 10_000_000, 100, 100,true),

    VOID_KNIGHT_GLOVES_I(ARMOUR, 50, new GameItem(24182, 1), new GameItem(26467, 1), 10_000_000, 100, 200,true),

    ELIDINIS_WARD(ARMOUR, 99, new GameItem(25985, 1), new GameItem(27251, 1), 250_000_000, 40, 7500,true),

    ELIDINIS_WARD_F(ARMOUR, 99, new GameItem(27251, 1), new GameItem(27253, 1), 500_000_000, 25, 15000,true),

    DINHS_BULWARK(ARMOUR, 99, new GameItem(21015, 1), new GameItem(28682, 1), 250_000_000, 15, 25000,true),
//Mage
    ANCESTRAL_HAT(ARMOUR, 85, new GameItem(21018, 1), new GameItem(24664, 1), 10_000_000, 35, 2500,true),

    ANCESTRAL_ROBE_TOP(ARMOUR, 85, new GameItem(21021, 1), new GameItem(24666, 1), 10_000_000, 35, 2500,true),

    ANCESTRAL_ROBE_BOTTOM(ARMOUR, 85, new GameItem(21024, 1), new GameItem(24668, 1), 10_000_000, 35, 2500,true),

    ANCIENT_MASK(ARMOUR, 99, new GameItem(26225, 1), new GameItem(33141, 1), 100_000_000, 20, 15000,true),

    ANCIENT_BODY(ARMOUR, 99, new GameItem(26221, 1), new GameItem(33142, 1), 100_000_000, 20, 15000,true),

    ANCIENT_BOTTOMS(ARMOUR, 99, new GameItem(26223, 1), new GameItem(33143, 1), 100_000_000, 20, 15000,true),

    REVERIE_HELM(ARMOUR, 99, new GameItem(20128, 1), new GameItem(33308, 1), 5_000_000_000L, 5, 50000, true),

    REVERIE_BODY(ARMOUR, 99, new GameItem(20131, 1), new GameItem(33309, 1), 5_000_000_000L, 5, 50000, true),

    REVERIE_LEGS(ARMOUR, 99, new GameItem(20137, 1), new GameItem(33310, 1), 5_000_000_000L, 5, 50000, true),
//Range
    MASORI_HELM(ARMOUR, 90, new GameItem(26714, 1), new GameItem(27226, 1), 25_000_000, 30, 5000,true),

    MASORI_BODY(ARMOUR, 90, new GameItem(26715, 1), new GameItem(27229, 1), 25_000_000, 30, 5000,true),

    MASORI_LEGS(ARMOUR, 90, new GameItem(26716, 1), new GameItem(27232, 1), 25_000_000, 30, 5000,true),

    MASORI_HELM_F(ARMOUR, 99, new GameItem(27226, 1), new GameItem(27235, 1), 100_000_000, 20, 15000,true),

    MASORI_BODY_F(ARMOUR, 99, new GameItem(27229, 1), new GameItem(27238, 1), 100_000_000, 20, 15000,true),

    MASORI_LEGS_F(ARMOUR, 99, new GameItem(27232, 1), new GameItem(27241, 1), 100_000_000, 20, 15000,true),

    PERNIX_HELM(ARMOUR, 99, new GameItem(27235, 1), new GameItem(33144, 1), 250_000_000, 15, 15000,true),

    PERNIX_BODY(ARMOUR, 99, new GameItem(27238, 1), new GameItem(33145, 1), 250_000_000, 15, 15000,true),

    PERNIX_LEGS(ARMOUR, 99, new GameItem(27241, 1), new GameItem(33146, 1), 250_000_000, 15, 15000,true),

    PLAGUE_HELM(ARMOUR, 99, new GameItem(33144, 1), new GameItem(33311, 1), 2_500_000_000L, 5, 15000,true),

    PLAGUE_BODY(ARMOUR, 99, new GameItem(33145, 1), new GameItem(33312, 1), 2_500_000_000L, 5, 15000,true),

    PLAGUE_LEGS(ARMOUR, 99, new GameItem(33146, 1), new GameItem(33313, 1), 2_500_000_000L, 5, 15000,true),
//Melee
    SANGUINE_HELM(ARMOUR, 99, new GameItem(33189, 1), new GameItem(28254, 1), 1_000_000_000, 20, 50000,true),

    SANGUINE_PLATE(ARMOUR, 99, new GameItem(33190, 1), new GameItem(28256, 1), 1_000_000_000, 20, 50000,true),

    SANGUINE_LEGS(ARMOUR, 99, new GameItem(33191, 1), new GameItem(28258, 1), 1_000_000_000, 20, 50000,true),

    EMBER_HELM(ARMOUR, 99, new GameItem(28254, 1), new GameItem(33343, 1), 2_500_000_000L, 10, 50000,true),

    EMBER_BODY(ARMOUR, 99, new GameItem(28256, 1), new GameItem(33344, 1), 2_500_000_000L, 10, 50000,true),

    EMBER_LEGS(ARMOUR, 99, new GameItem(28258, 1), new GameItem(33345, 1), 2_500_000_000L, 10, 50000,true),

    STARLIGHT_HELM(ARMOUR, 99, new GameItem(33343, 1), new GameItem(33324, 1), 5_000_000_000L, 5, 50000, true),

    STARLIGHT_BODY(ARMOUR, 99, new GameItem(33344, 1), new GameItem(33325, 1), 5_000_000_000L, 5, 50000, true),

    STARLIGHT_LEGS(ARMOUR, 99, new GameItem(33345, 1), new GameItem(33326, 1), 5_000_000_000L, 5, 50000, true),

    /* Accessories */

    FREEDOM_RING(ACCESSORY, 99, new GameItem(33392, 1), new GameItem(24731, 1), 5_000_000_000L, 50, 50_000, true),
    FREEDOM_BOOTS(ACCESSORY, 99, new GameItem(33393, 1), new GameItem(33403, 1), 5_000_000_000L, 50, 50_000, true),
    FREEDOM_GLOVES(ACCESSORY, 99, new GameItem(33394, 1), new GameItem(33402, 1), 5_000_000_000L, 50, 50_000, true),

    HALLOWED_RING(ACCESSORY, 99, new GameItem(24731, 1), new GameItem(33406, 1), 10_000_000_000L, 40, 100_000, true),
    HALLOWED_BOOTS(ACCESSORY, 99, new GameItem(33403, 1), new GameItem(33408, 1), 10_000_000_000L, 40, 100_000, true),
    HALLOWED_GLOVES(ACCESSORY, 99, new GameItem(33402, 1), new GameItem(33409, 1), 10_000_000_000L, 40, 100_000, true),
    HALLOWED_AMULET  (ACCESSORY, 99, new GameItem(24725, 1), new GameItem(33407, 1), 10_000_000_000L, 40, 100_000, true),

    AMULET_OF_FURY(ACCESSORY,30, new GameItem(6585, 1), new GameItem(12436, 1), 250_000, 50, 1250,false),

    Berserker_necklace(ACCESSORY,5, new GameItem(11128, 1), new GameItem(23240, 1), 250_000, 60, 675,true),

    OCCULT_NECKLACE(ACCESSORY,50, new GameItem(12002, 1), new GameItem(19720, 1), 2_500_000, 35, 2500, true),

    AMULET_OF_TORTURE(ACCESSORY,75, new GameItem(19553, 1), new GameItem(20366, 1), 2_500_000, 35, 2500,true),

    NECKLACE_OF_ANGUISH(ACCESSORY,75, new GameItem(19547, 1), new GameItem(22249, 1), 2_500_000, 35, 2500,true),

    TORMENTED_BRACELET(ACCESSORY,75, new GameItem(19544, 1), new GameItem(23444, 1), 2_500_000, 35, 2500,true),

    RING_OF_WEALTH_i(ACCESSORY, 5, new GameItem(12785, 1), new GameItem(20790, 1), 500_000, 50, 5000,true),

    RING_OF_WEALTH_i_1(ACCESSORY, 10, new GameItem(20790, 1), new GameItem(20789, 1), 1_000_000, 35, 15000,true),

    RING_OF_WEALTH_i_2(ACCESSORY, 15, new GameItem(20789, 1), new GameItem(20788, 1), 3_000_000, 25, 25000,true),

    RING_OF_WEALTH_I_3(ACCESSORY, 20, new GameItem(20788, 1), new GameItem(20787, 1), 10_000_000, 15, 35000,true),

    RING_OF_WEALTH_I_4(ACCESSORY, 25, new GameItem(20787, 1), new GameItem(20786, 1), 25_000_000, 10, 50000,true),

    /* Miscellaneous */

    UNHOLY_BOOK(MISC, 5, new GameItem(3842, 1), new GameItem(26498, 1), 500_000, 35, 250,false),

    HOLY_BOOK(MISC, 5, new GameItem(3840, 1), new GameItem(26496, 1), 500_000, 35, 250,false),

    BOOK_OF_LAW(MISC, 5, new GameItem(12610, 1), new GameItem(26492, 1), 500_000, 35, 250,false),

    BOOK_OF_WAR(MISC, 5, new GameItem(12608, 1), new GameItem(26494, 1), 500_000, 35, 250,false),

    BOOK_OF_DARKNESS(MISC, 5, new GameItem(12612, 1), new GameItem(26490, 1), 500_000, 35, 250,false),

    BOOK_OF_BALANCE(MISC, 5, new GameItem(3844, 1), new GameItem(26488, 1), 500_000, 35, 250,false),

    CRUCIFEROUS_CODEX(MISC, 80, new GameItem(20716, 1), new GameItem(13681, 1), 50_000_000, 80, 20000,false),

    ARKCANE_GRIMORE(MISC, 80, new GameItem(13681, 1), new GameItem(26551, 1), 250_000_000, 45, 50000,true),

    DRAGON_AXE(MISC, 60, new GameItem(6739, 1), new GameItem(25378, 1), 1_000_000, 35, 250,true),

    DRAGON_PICKAXE(MISC, 60, new GameItem(11920, 1), new GameItem(25376, 1), 1_000_000, 35, 250,true),

    Dragon_harpoon(MISC, 60, new GameItem(21028, 1), new GameItem(25373, 1), 1_000_000, 35, 150,true),

    Monkey_nut(MISC, 99, new GameItem( 4012, 1), new GameItem( 33239, 1), 500_000_000, 1, 5000, true),

    Emblem(MISC, 99, new GameItem( 28013, 1), new GameItem( 28015, 1), 3_000_000_000L, 100, 250000, true),

    JEWEL_OF_THE_SUN(MISC, 99, new GameItem(27607, 1), new GameItem( 27289, 1), 2_500_000_000L, 50, 250000, true),
//    SHADOW_PET(MISC, 99, new GameItem(10198, 1), new GameItem( 10199, 1), 35_000_000_000L, 5, 750_000, true),
    ;


    private UpgradeType type;
    public GameItem required, reward;
    public long cost;
    private int successRate, levelRequired, xp;
    private boolean rare;

    UpgradeMaterials(UpgradeType type, int levelRequired, GameItem required, GameItem reward, long cost, int successRate, int xp, boolean rare) {
        this.type = type;
        this.levelRequired = levelRequired;
        this.required = required;
        this.reward = reward;
        this.cost = cost;
        this.successRate = successRate;
        this.xp = xp;
        this.rare = rare;
    }

    UpgradeMaterials(UpgradeType type, int levelRequired, GameItem required, GameItem reward, long cost, int successRate, int xp) {
        this.type = type;
        this.required = required;
        this.levelRequired = levelRequired;
        this.reward = reward;
        this.cost = cost;
        this.successRate = successRate;
        this.xp = xp;
        this.rare = false;
    }

    public static ArrayList<UpgradeMaterials> getForType(UpgradeType type) {
        ArrayList<UpgradeMaterials> upgradeablesArrayList = new ArrayList<>();
        for (UpgradeMaterials upgradeables : values()) {
            if (upgradeables.getType() == type){
                upgradeablesArrayList.add(upgradeables);
            }
        }
        return upgradeablesArrayList;
    }


    public enum UpgradeType {

        WEAPON, ARMOUR, ACCESSORY, MISC

    }

    private static final boolean DumpUpgradeItems = false;
    private static final boolean DumpUpgradeStats = false;
    static {
        if (DumpUpgradeItems) {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./temp/upgrade_items.json"));
                if (!new File("./temp/upgrade_items.json").exists()) {
                    Preconditions.checkState(new File("./temp/upgrade_items.json").mkdirs());
                }

                ArrayList<UpgradeMaterials> um = new ArrayList<>(Arrays.asList(UpgradeMaterials.values()));

                um.sort(Comparator.comparing(UpgradeMaterials::getLevelRequired));

                for (UpgradeMaterials upgradeMaterials : um) {
                    bufferedWriter.write("[TR][TD]"+ ItemDef.forId(upgradeMaterials.getRequired().getId()).getName() + "[/TD][TD]" + Misc.formatCoins(upgradeMaterials.getCost()) + "[/TD][TD]" + upgradeMaterials.getLevelRequired() + "[/TD][TD]" + upgradeMaterials.getXp() + "[/TD][TD]" + upgradeMaterials.getSuccessRate() + "%[/TD][TD]" + ItemDef.forId(upgradeMaterials.getReward().getId()).getName() + "[/TD][/TR]");
                    bufferedWriter.newLine();
                }


                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (DumpUpgradeStats) {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./temp/upgrade_items_stats.json"));
                if (!new File("./temp/upgrade_items_stats.json").exists()) {
                    Preconditions.checkState(new File("./temp/upgrade_items_stats.json").mkdirs());
                }

                ArrayList<UpgradeMaterials> um = new ArrayList<>(Arrays.asList(UpgradeMaterials.values()));

                um.sort(Comparator.comparing(UpgradeMaterials::getLevelRequired));

                for (UpgradeMaterials upgradeMaterials : um) {

                    bufferedWriter.write("[TR][TD]"+ ItemStats.forId(upgradeMaterials.getReward().getId()).getName() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getAstab() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getAslash() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getAcrush() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getAmagic() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getArange() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getDstab() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getDslash()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getDcrush()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getDmagic()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getDrange() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getStr()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getRstr()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getMdmg() + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getPrayer()  + "[/TD][TD]" +
                            ItemStats.forId(upgradeMaterials.getReward().getId()).getEquipment().getAttackSpeed() + "[/TD][TD]" +
                            "NONE[/TD][/TR]");

                    bufferedWriter.newLine();
                }


                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
