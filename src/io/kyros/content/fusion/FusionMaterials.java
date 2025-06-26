package io.kyros.content.fusion;

import io.kyros.model.items.GameItem;
import lombok.Getter;

import java.util.ArrayList;

import static io.kyros.content.fusion.FusionSystem.*;
import static io.kyros.content.fusion.FusionTypes.*;

@Getter
public enum FusionMaterials {

    DRAGON_SCIMITAR(FusionTypes.WEAPON,60, new GameItem[]{new GameItem(4587, 1), new GameItem(SKILLING_EASY, 5),
            new GameItem(PVM_EASY, 5), new GameItem(MISC_EASY, 5), new GameItem(FOUNDRY_MEDIUM, 100)},
            new GameItem(20000, 1), 2000,  6000,false),

    ABYSSAL_WHIP(FusionTypes.WEAPON, 70, new GameItem[]{new GameItem(4151, 1), new GameItem(SKILLING_MEDIUM, 10),
            new GameItem(PVM_MEDIUM, 10), new GameItem(MISC_MEDIUM, 10), new GameItem(FOUNDRY_MEDIUM, 400)}, new GameItem(26482, 1), 10000, 7000,false),

    ARMADYL_GODSWORD(FusionTypes.WEAPON, 85, new GameItem[]{new GameItem(11802, 1), new GameItem(SKILLING_MEDIUM, 50),
            new GameItem(PVM_MEDIUM, 50), new GameItem(MISC_MEDIUM, 50), new GameItem(FOUNDRY_HARD, 300)}, new GameItem(20368, 1), 250000 , 8500,true),

    SARADOMIN_GODSWORD(FusionTypes.WEAPON, 85, new GameItem[]{new GameItem(11806, 1), new GameItem(SKILLING_MEDIUM, 50),
            new GameItem(PVM_MEDIUM, 50), new GameItem(MISC_MEDIUM, 50), new GameItem(FOUNDRY_HARD, 300)}, new GameItem(20372, 1), 250000, 8500,true),

    ZAMORAK_GODSWORD(FusionTypes.WEAPON, 85, new GameItem[]{new GameItem(11808, 1), new GameItem(SKILLING_MEDIUM, 50),
            new GameItem(PVM_MEDIUM, 50), new GameItem(MISC_MEDIUM, 50), new GameItem(FOUNDRY_HARD, 300)}, new GameItem(20374, 1), 250000, 8500,true),

    BANDOS_GODSWORD(FusionTypes.WEAPON, 85, new GameItem[]{new GameItem(11804, 1), new GameItem(SKILLING_MEDIUM, 75),
            new GameItem(PVM_MEDIUM, 75), new GameItem(MISC_MEDIUM, 75), new GameItem(FOUNDRY_HARD, 300)}, new GameItem(20370, 1), 750000, 8500,true),

    ABYSSAL_WHIP_OR(FusionTypes.WEAPON, 75, new GameItem[]{new GameItem(26482, 1), new GameItem(SKILLING_MEDIUM, 25),
            new GameItem(PVM_MEDIUM, 25), new GameItem(MISC_MEDIUM, 25), new GameItem(FOUNDRY_HARD, 20)}, new GameItem(12773, 1), 150000, 7500,true),

    ABYSSAL_TENTACLE(FusionTypes.WEAPON, 80, new GameItem[]{new GameItem(12006, 1), new GameItem(SKILLING_MEDIUM, 50),
            new GameItem(PVM_MEDIUM, 50), new GameItem(MISC_MEDIUM, 50), new GameItem(FOUNDRY_HARD, 200)}, new GameItem(26484, 1), 500000, 8000,true),

    DRAGON_CLAWS(FusionTypes.WEAPON, 60, new GameItem[]{new GameItem(13652, 1), new GameItem(SKILLING_MEDIUM, 40),
            new GameItem(PVM_MEDIUM, 40), new GameItem(MISC_MEDIUM, 40), new GameItem(FOUNDRY_HARD, 40)}, new GameItem(26708, 1), 250000, 6000,true),

    DRAGON_WARHAMMER(FusionTypes.WEAPON, 60, new GameItem[]{new GameItem(13576, 1), new GameItem(SKILLING_MEDIUM, 40),
            new GameItem(PVM_MEDIUM, 40), new GameItem(MISC_MEDIUM, 40), new GameItem(FOUNDRY_HARD, 40)}, new GameItem(26710, 1), 250000, 6000,true),

    HEAVY_BALLISTA(FusionTypes.WEAPON, 70, new GameItem[]{new GameItem(19481, 1), new GameItem(SKILLING_EASY, 5),
            new GameItem(PVM_MEDIUM, 25), new GameItem(MISC_MEDIUM, 25), new GameItem(FOUNDRY_HARD, 150)}, new GameItem(26712, 1), 150000, 6500,true),

    Rune_Crossbow(FusionTypes.WEAPON, 41, new GameItem[]{new GameItem(9185, 1), new GameItem(SKILLING_MEDIUM, 10),
            new GameItem(PVM_MEDIUM, 10), new GameItem(MISC_MEDIUM, 10), new GameItem(FOUNDRY_MEDIUM, 200)}, new GameItem(26486, 1), 50000, 4500,false),

    SCYTHE_OF_VITUR(FusionTypes.WEAPON, 90, new GameItem[]{new GameItem(22325, 1), new GameItem(SKILLING_ELITE, 50),
            new GameItem(PVM_ELITE, 50), new GameItem(MISC_ELITE, 50), new GameItem(FOUNDRY_ELITE, 250)}, new GameItem(25736, 1), 10000000, 100000,true),

    HOLY_SCYTHE_OF_VITUR(FusionTypes.WEAPON, 99, new GameItem[]{new GameItem(25736, 1), new GameItem(SKILLING_ELITE, 100),
            new GameItem(PVM_ELITE, 100), new GameItem(MISC_ELITE, 100), new GameItem(FOUNDRY_ELITE, 500)}, new GameItem(25739, 1), 15000000, 150000,true),

    DRAGON_AXE(FusionTypes.WEAPON, 60, new GameItem[]{new GameItem(6739, 1), new GameItem(SKILLING_EASY, 80),
            new GameItem(PVM_EASY, 80), new GameItem(MISC_EASY, 80), new GameItem(FOUNDRY_HARD, 25)}, new GameItem(25378, 1), 12500, 6000,false),

    DRAGON_PICKAXE(FusionTypes.WEAPON, 60, new GameItem[]{new GameItem(11920, 1), new GameItem(SKILLING_EASY, 80),
            new GameItem(PVM_EASY, 80), new GameItem(MISC_EASY, 80), new GameItem(FOUNDRY_HARD, 25)}, new GameItem(25376, 1), 12500, 6000,false),

    DRAGON_HUNTER_CROSSBOW(FusionTypes.WEAPON, 61, new GameItem[]{new GameItem(21012, 1), new GameItem(SKILLING_HARD, 35),
            new GameItem(PVM_HARD, 35), new GameItem(MISC_HARD, 35), new GameItem(FOUNDRY_HARD, 50)}, new GameItem(25916, 1), 50000, 6000,false),

    DRAGON_HUNTER_CROSSBOW_T(FusionTypes.WEAPON, 71, new GameItem[]{new GameItem(25916, 1), new GameItem(SKILLING_EASY, 75),
            new GameItem(PVM_ELITE, 50), new GameItem(MISC_ELITE, 50), new GameItem(FOUNDRY_ELITE, 20)}, new GameItem(25918, 1), 100000, 7000,true),

    Dragon_harpoon(FusionTypes.WEAPON, 60, new GameItem[]{new GameItem(21028, 1), new GameItem(SKILLING_EASY, 80),
            new GameItem(PVM_EASY, 80), new GameItem(MISC_EASY, 80), new GameItem(FOUNDRY_HARD, 25)}, new GameItem(25373, 1), 500000, 6000,true),

    Infernal_harpoon(FusionTypes.WEAPON, 75, new GameItem[]{new GameItem(21031, 1), new GameItem(SKILLING_MEDIUM, 75),
            new GameItem(PVM_MEDIUM, 75), new GameItem(MISC_MEDIUM, 75), new GameItem(FOUNDRY_HARD, 25)}, new GameItem(25059, 1), 100000, 6000,true),

    Infernal_Pickaxe(FusionTypes.WEAPON, 75, new GameItem[]{new GameItem(13243, 1), new GameItem(SKILLING_MEDIUM, 75),
            new GameItem(PVM_MEDIUM, 75), new GameItem(MISC_MEDIUM, 75), new GameItem(FOUNDRY_HARD, 25)}, new GameItem(25063, 1), 100000, 6000,true),

    Infernal_Axe(FusionTypes.WEAPON, 75, new GameItem[]{new GameItem(13241, 1), new GameItem(SKILLING_MEDIUM, 75),
            new GameItem(PVM_MEDIUM, 75), new GameItem(MISC_MEDIUM, 75), new GameItem(FOUNDRY_HARD, 25)}, new GameItem(25066, 1), 100000, 6000,true),

    Sanguinesti_Staff(FusionTypes.WEAPON, 90, new GameItem[]{new GameItem(22323, 1), new GameItem(SKILLING_ELITE, 80),
            new GameItem(PVM_ELITE, 80), new GameItem(MISC_ELITE, 80), new GameItem(FOUNDRY_ELITE, 300)}, new GameItem(25731, 1), 15000000, 100000,true),

    Ghrazi_Rapier(FusionTypes.WEAPON, 85, new GameItem[]{new GameItem(22324, 1), new GameItem(SKILLING_ELITE, 50),
            new GameItem(PVM_ELITE, 80), new GameItem(MISC_ELITE, 80), new GameItem(FOUNDRY_ELITE, 150)}, new GameItem(25734, 1), 15000000, 10000,true),

    Twisted_bow(FusionTypes.WEAPON, 85, new GameItem[]{new GameItem(20997, 1), new GameItem(SKILLING_ELITE, 100),
            new GameItem(PVM_ELITE, 100), new GameItem(MISC_ELITE, 100), new GameItem(FOUNDRY_ELITE, 500)}, new GameItem(33058, 1), 15000000, 10000,true),

    /* Armour */

    MASORI_MASK(ARMOUR, 45, new GameItem[]{new GameItem(26714, 1),
            new GameItem(SKILLING_MEDIUM, 50),
            new GameItem(PVM_MEDIUM, 50),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_HARD, 200)},
            new GameItem(27226, 1), 1000000,  4500,false),

    MASORI_BOTTOM(ARMOUR, 45, new GameItem[]{new GameItem(26715, 1),
            new GameItem(SKILLING_MEDIUM, 50),
            new GameItem(PVM_MEDIUM, 50),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_HARD, 200)},
            new GameItem(27229, 1), 1000000,  4500,false),

    MASORI_ROBE(ARMOUR, 50, new GameItem[]{new GameItem(26716, 1),
            new GameItem(SKILLING_HARD, 50),
            new GameItem(PVM_ELITE, 40),
            new GameItem(MISC_HARD, 50),
            new GameItem(FOUNDRY_HARD, 200)},
            new GameItem(27232, 1), 1000000,  5000,true),

    MASORI_F_MASK(ARMOUR, 45, new GameItem[]{new GameItem(27226, 1),
            new GameItem(SKILLING_MEDIUM, 50),
            new GameItem(PVM_MEDIUM, 50),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_ELITE, 60)},
            new GameItem(27235, 1), 5000000,  4500,false),

    MASORI_F_BOTTOM(ARMOUR, 45, new GameItem[]{new GameItem(27229, 1),
            new GameItem(SKILLING_MEDIUM, 50),
            new GameItem(PVM_MEDIUM, 50),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_ELITE, 60)},
            new GameItem(27241, 1), 5000000,  4500,false),

    MASORI_F_ROBE(ARMOUR, 50, new GameItem[]{new GameItem(27232, 1),
            new GameItem(SKILLING_HARD, 50),
            new GameItem(PVM_ELITE, 40),
            new GameItem(MISC_HARD, 50),
            new GameItem(FOUNDRY_ELITE, 60)},
            new GameItem(27238, 1), 5000000,  5000,true),


    ELITE_VOID_TOP(ARMOUR, 45, new GameItem[]{new GameItem(13072, 1), new GameItem(SKILLING_MEDIUM, 50),
            new GameItem(PVM_MEDIUM, 50), new GameItem(MISC_MEDIUM, 50), new GameItem(FOUNDRY_HARD, 20)}, new GameItem(26463, 1), 100000,  4500,false),

    ELITE_VOID_BOTTOM(ARMOUR, 45, new GameItem[]{new GameItem(13073, 1), new GameItem(SKILLING_MEDIUM, 50),
            new GameItem(PVM_MEDIUM, 50), new GameItem(MISC_MEDIUM, 50), new GameItem(FOUNDRY_HARD, 20)}, new GameItem(26465, 1), 100000,  4500,false),

    ELITE_VOID_ROBE_OR(ARMOUR, 50, new GameItem[]{new GameItem(26465, 1), new GameItem(SKILLING_HARD, 50),
            new GameItem(PVM_ELITE, 40), new GameItem(MISC_HARD, 50), new GameItem(FOUNDRY_ELITE, 20)}, new GameItem(26471, 1), 250000,  5000,true),

    ELITE_VOID_TOP_OR(ARMOUR, 50, new GameItem[]{new GameItem(26463, 1), new GameItem(SKILLING_HARD, 50),
            new GameItem(PVM_ELITE, 40), new GameItem(MISC_HARD, 50), new GameItem(FOUNDRY_ELITE, 20)}, new GameItem(26469, 1), 250000,  5000,true),

    VOID_KNIGHT_GLOVES(ARMOUR, 45, new GameItem[]{new GameItem(8842, 1), new GameItem(SKILLING_MEDIUM, 25),
            new GameItem(PVM_MEDIUM, 25), new GameItem(MISC_MEDIUM, 25), new GameItem(FOUNDRY_HARD, 20)}, new GameItem(24182, 1), 50000,  4500,false),

    VOID_KNIGHT_GLOVES_I(ARMOUR, 50, new GameItem[]{new GameItem(24182, 1), new GameItem(SKILLING_HARD, 20),
            new GameItem(PVM_HARD, 20), new GameItem(MISC_HARD, 20), new GameItem(FOUNDRY_ELITE, 10)}, new GameItem(26467, 1), 100000,  5000,true),

    VOID_MAGE_HELM(ARMOUR, 45, new GameItem[]{new GameItem(11663, 1), new GameItem(SKILLING_MEDIUM, 40),
            new GameItem(PVM_MEDIUM, 40), new GameItem(MISC_MEDIUM, 40), new GameItem(FOUNDRY_HARD, 20)}, new GameItem(24183, 1), 150000,  4500,false),

    VOID_RANGER_HELM(ARMOUR, 45, new GameItem[]{new GameItem(11664, 1), new GameItem(SKILLING_MEDIUM, 40),
            new GameItem(PVM_MEDIUM, 40), new GameItem(MISC_MEDIUM, 40), new GameItem(FOUNDRY_HARD, 20)}, new GameItem(24184, 1), 150000,  4500,false),

    VOID_MELEE_HELM(ARMOUR, 45, new GameItem[]{new GameItem(11665, 1), new GameItem(SKILLING_MEDIUM, 40),
            new GameItem(PVM_MEDIUM, 40), new GameItem(MISC_MEDIUM, 40), new GameItem(FOUNDRY_HARD, 20)}, new GameItem(24185, 1), 75000, 5000,false),

    VOID_MAGE_HELM_I(ARMOUR, 50, new GameItem[]{new GameItem(24183, 1), new GameItem(SKILLING_HARD, 40),
            new GameItem(PVM_HARD, 40), new GameItem(MISC_HARD, 40), new GameItem(FOUNDRY_ELITE, 20)}, new GameItem(26473, 1), 100000,  5000,true),

    VOID_RANGER_HELM_I(ARMOUR, 50, new GameItem[]{new GameItem(24184, 1), new GameItem(SKILLING_HARD, 40),
            new GameItem(PVM_HARD, 40), new GameItem(MISC_HARD, 40), new GameItem(FOUNDRY_ELITE, 20)}, new GameItem(26475, 1), 100000,  5000,true),

    VOID_MELEE_HELM_I(ARMOUR, 50, new GameItem[]{new GameItem(24185, 1), new GameItem(SKILLING_HARD, 40),
            new GameItem(PVM_HARD, 40), new GameItem(MISC_HARD, 40), new GameItem(FOUNDRY_ELITE, 20)}, new GameItem(26477, 1), 100000,  5000,true),

    MYSTIC_HAT(ARMOUR, 30, new GameItem[]{new GameItem(4089, 1), new GameItem(SKILLING_EASY, 150),
            new GameItem(PVM_EASY, 150), new GameItem(MISC_EASY, 150), new GameItem(FOUNDRY_MEDIUM, 800)}, new GameItem(26531, 1), 25000,  3000,false),

    MYSTIC_BOOTS(ARMOUR, 30, new GameItem[]{new GameItem(4097, 1), new GameItem(SKILLING_EASY, 150),
            new GameItem(PVM_EASY, 150), new GameItem(MISC_EASY, 150), new GameItem(FOUNDRY_MEDIUM, 800)}, new GameItem(26539, 1), 25000,  3000,false),

    MYSTIC_GLOVES(ARMOUR, 30, new GameItem[]{new GameItem(4095, 1), new GameItem(SKILLING_EASY, 150),
            new GameItem(PVM_EASY, 150), new GameItem(MISC_EASY, 150), new GameItem(FOUNDRY_MEDIUM, 800)}, new GameItem(26537, 1), 25000,  3000,false),

    MYSTIC_ROBE_TOP(ARMOUR, 30, new GameItem[]{new GameItem(4091, 1), new GameItem(SKILLING_EASY, 150),
            new GameItem(PVM_EASY, 150), new GameItem(MISC_EASY, 150), new GameItem(FOUNDRY_MEDIUM, 800)}, new GameItem(26533, 1), 25000,  3000,false),

    MYSTIC_ROBE_BOTTOM(ARMOUR, 30, new GameItem[]{new GameItem(4093, 1), new GameItem(SKILLING_EASY, 150),
            new GameItem(PVM_EASY, 150), new GameItem(MISC_EASY, 150), new GameItem(FOUNDRY_MEDIUM, 800)}, new GameItem(26535, 1), 25000,  3000,false),

    ANCESTRAL_ROBE_TOP(ARMOUR, 85, new GameItem[]{new GameItem(21021, 1), new GameItem(SKILLING_ELITE, 40),
            new GameItem(PVM_ELITE, 40), new GameItem(MISC_ELITE, 40), new GameItem(FOUNDRY_HARD, 150)}, new GameItem(24666, 1), 500000,  19500,true),

    ANCESTRAL_ROBE_BOTTOM(ARMOUR, 85, new GameItem[]{new GameItem(21024, 1), new GameItem(SKILLING_ELITE, 40),
            new GameItem(PVM_ELITE, 40), new GameItem(MISC_ELITE, 40), new GameItem(FOUNDRY_HARD, 150)}, new GameItem(24668, 1), 500000,  19500,true),

    ANCESTRAL_HAT(ARMOUR, 85, new GameItem[]{new GameItem(21018, 1), new GameItem(SKILLING_ELITE, 40),
            new GameItem(PVM_ELITE, 40), new GameItem(MISC_ELITE, 40), new GameItem(FOUNDRY_HARD, 150)}, new GameItem(24664, 1), 500000,  19500,true),

    MALEDICTION_WARD(ARMOUR, 55, new GameItem[]{new GameItem(11924, 1), new GameItem(SKILLING_HARD, 75),
            new GameItem(PVM_HARD, 75), new GameItem(MISC_HARD, 75), new GameItem(FOUNDRY_ELITE, 5)}, new GameItem(12806, 1), 300000,  1250,false),

    ODIUM_WARD(ARMOUR, 55, new GameItem[]{new GameItem(11926, 1), new GameItem(SKILLING_HARD, 75),
            new GameItem(PVM_HARD, 75), new GameItem(MISC_HARD, 75), new GameItem(FOUNDRY_ELITE, 5)}, new GameItem(12807, 1), 300000,  1250,false),

    /* Accessories */

    AMULET_OF_FURY(ACCESSORY,30, new GameItem[]{new GameItem(6585, 1), new GameItem(SKILLING_MEDIUM, 125),
            new GameItem(PVM_HARD, 40), new GameItem(MISC_MEDIUM, 125), new GameItem(FOUNDRY_ELITE, 5)}, new GameItem(12436, 1), 25000,  1250,false),

    Berserker_necklace(ACCESSORY,5, new GameItem[]{new GameItem(11128, 1), new GameItem(SKILLING_MEDIUM, 60),
            new GameItem(PVM_MEDIUM, 60), new GameItem(MISC_MEDIUM, 60), new GameItem(FOUNDRY_HARD, 5)}, new GameItem(23240, 1), 25000,  675,true),

    OCCULT_NECKLACE(ACCESSORY,50,new GameItem[]{new GameItem(12002, 1), new GameItem(SKILLING_MEDIUM, 125),
            new GameItem(PVM_HARD, 40), new GameItem(MISC_MEDIUM, 125), new GameItem(FOUNDRY_ELITE, 5)}, new GameItem(19720, 1), 25000,  1250,true),

    AMULET_OF_TORTURE(ACCESSORY,75, new GameItem[]{new GameItem(19553, 1), new GameItem(SKILLING_MEDIUM, 125),
            new GameItem(PVM_HARD, 50), new GameItem(MISC_MEDIUM, 125), new GameItem(FOUNDRY_ELITE, 10)}, new GameItem(20366, 1), 25000,  2500,true),

    NECKLACE_OF_ANGUISH(ACCESSORY,75, new GameItem[]{new GameItem(19547, 1), new GameItem(SKILLING_MEDIUM, 125),
            new GameItem(PVM_HARD, 50), new GameItem(MISC_MEDIUM, 125), new GameItem(FOUNDRY_ELITE, 10)}, new GameItem(22249, 1), 25000,  2500,true),

    TORMENTED_BRACELET(ACCESSORY,75, new GameItem[]{new GameItem(19544, 1), new GameItem(SKILLING_MEDIUM, 90),
            new GameItem(PVM_HARD, 35), new GameItem(MISC_MEDIUM, 90), new GameItem(FOUNDRY_ELITE, 15)},new GameItem(23444, 1), 25000,  2500,true),

    RING_OF_WEALTH_i(ACCESSORY, 5, new GameItem[]{new GameItem(12785, 1), new GameItem(SKILLING_MEDIUM, 400),
            new GameItem(PVM_MEDIUM, 400), new GameItem(MISC_MEDIUM, 400), new GameItem(FOUNDRY_ELITE, 50)}, new GameItem(20790, 1), 50000,  5000,true),

    RING_OF_WEALTH_i_1(ACCESSORY, 10, new GameItem[]{new GameItem(20790, 1), new GameItem(SKILLING_HARD, 250),
            new GameItem(PVM_HARD, 250), new GameItem(MISC_HARD, 250), new GameItem(FOUNDRY_ELITE, 120)}, new GameItem(20789, 1), 150000,  15000,true),

    RING_OF_WEALTH_i_2(ACCESSORY, 15, new GameItem[]{new GameItem(20789, 1), new GameItem(SKILLING_HARD, 600),
            new GameItem(PVM_HARD, 600), new GameItem(MISC_HARD, 600), new GameItem(FOUNDRY_ELITE, 200)}, new GameItem(20788, 1), 500000,  35000,true),

    RING_OF_WEALTH_I_3(ACCESSORY, 20, new GameItem[]{new GameItem(20788, 1), new GameItem(SKILLING_ELITE, 300),
            new GameItem(PVM_ELITE, 300), new GameItem(MISC_ELITE, 300), new GameItem(FOUNDRY_ELITE, 500)}, new GameItem(20787, 1), 25000000, 150000,true),

    RING_OF_WEALTH_I_4(ACCESSORY, 25, new GameItem[]{new GameItem(20787, 1), new GameItem(SKILLING_ELITE, 600),
            new GameItem(PVM_ELITE, 600), new GameItem(MISC_ELITE, 600), new GameItem(FOUNDRY_ELITE, 1500)}, new GameItem(20786, 1), 50000000,  150000,true),

    /* Miscellaneous */

    UNHOLY_BOOK(MISC, 5, new GameItem[]{new GameItem(3842, 1), new GameItem(SKILLING_MEDIUM, 100),
            new GameItem(PVM_HARD, 60), new GameItem(MISC_MEDIUM, 100), new GameItem(FOUNDRY_MEDIUM, 1500)}, new GameItem(26498, 1), 50000,  15000,false),

    HOLY_BOOK(MISC, 5, new GameItem[]{new GameItem(3840, 1), new GameItem(SKILLING_MEDIUM, 100),
            new GameItem(PVM_HARD, 60), new GameItem(MISC_MEDIUM, 100), new GameItem(FOUNDRY_MEDIUM, 1500)}, new GameItem(26496, 1), 50000, 15000,false),

    BOOK_OF_LAW(MISC, 5, new GameItem[]{new GameItem(12610, 1), new GameItem(SKILLING_MEDIUM, 100),
            new GameItem(PVM_HARD, 60), new GameItem(MISC_MEDIUM, 100), new GameItem(FOUNDRY_MEDIUM, 1500)},new GameItem(26492, 1), 50000, 15000,false),

    BOOK_OF_WAR(MISC, 5, new GameItem[]{new GameItem(12608, 1), new GameItem(SKILLING_MEDIUM, 100),
            new GameItem(PVM_HARD, 60), new GameItem(MISC_MEDIUM, 100), new GameItem(FOUNDRY_MEDIUM, 1500)},new GameItem(26494, 1), 50000,  15000,false),

    BOOK_OF_DARKNESS(MISC, 5, new GameItem[]{new GameItem(12612, 1), new GameItem(SKILLING_MEDIUM, 100),
            new GameItem(PVM_HARD, 60), new GameItem(MISC_MEDIUM, 100), new GameItem(FOUNDRY_MEDIUM, 1500)}, new GameItem(26490, 1), 50000,  15000,false),

    BOOK_OF_BALANCE(MISC, 5, new GameItem[]{new GameItem(3844, 1), new GameItem(SKILLING_MEDIUM, 100),
            new GameItem(PVM_HARD, 60), new GameItem(MISC_MEDIUM, 100), new GameItem(FOUNDRY_MEDIUM, 1500)}, new GameItem(26488, 1), 50000,  15000,false),

    CRUCIFEROUS_CODEX(MISC, 80, new GameItem[]{new GameItem(20716, 1), new GameItem(SKILLING_HARD, 50),
            new GameItem(PVM_HARD, 85), new GameItem(MISC_HARD, 50), new GameItem(FOUNDRY_ELITE, 50)}, new GameItem(13681, 1), 150000,  1500000,false),


    /* Perk's */

    DWARF_OVERLOAD(MISC, 70, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_EASY, 250),
            new GameItem(PVM_HARD, 85),
            new GameItem(MISC_HARD, 50),
            new GameItem(FOUNDRY_ELITE, 50)},
            new GameItem(33073, 1), 500000,  125000,false),

    PK_MASTER(MISC, 65, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_ELITE, 100),
            new GameItem(PVM_ELITE, 100),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_HARD, 50)},
            new GameItem(33074, 1), 1000000,  150000,false),

    MAGIC_MASTER(MISC, 55, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_EASY, 100),
            new GameItem(PVM_ELITE, 50),
            new GameItem(MISC_ELITE, 50),
            new GameItem(FOUNDRY_ELITE, 15)},
            new GameItem(33090, 1), 1500000,  150000,false),

    YIN_YANG(MISC, 80, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_EASY, 50),
            new GameItem(PVM_ELITE, 100),
            new GameItem(MISC_HARD, 75),
            new GameItem(FOUNDRY_ELITE, 50)},
            new GameItem(33091, 1), 1500000,  150000,false),

    NOVICE_ZERK(MISC, 65, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_EASY, 50),
            new GameItem(PVM_HARD, 85),
            new GameItem(MISC_HARD, 50),
            new GameItem(FOUNDRY_ELITE, 15)},
            new GameItem(33102, 1), 1500000,  92500,false),

    NOVICE_MAGICIAN(MISC, 65, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_EASY, 50),
            new GameItem(PVM_HARD, 85),
            new GameItem(MISC_HARD, 50),
            new GameItem(FOUNDRY_ELITE, 15)},
            new GameItem(33103, 1), 1500000,  92500,false),

    NOVICE_RANGER(MISC, 65, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_EASY, 50),
            new GameItem(PVM_HARD, 85),
            new GameItem(MISC_HARD, 50),
            new GameItem(FOUNDRY_ELITE, 15)},
            new GameItem(33104, 1), 1500000,  92500,false),

    PRO_ZERK(MISC, 80, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_EASY, 150),
            new GameItem(PVM_HARD, 255),
            new GameItem(MISC_HARD, 150),
            new GameItem(FOUNDRY_ELITE, 30)},
            new GameItem(33105, 1), 3000000,  185000,false),

    PRO_MAGICIAN(MISC, 80, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_EASY, 150),
            new GameItem(PVM_HARD, 255),
            new GameItem(MISC_HARD, 150),
            new GameItem(FOUNDRY_ELITE, 30)},
            new GameItem(33106, 1), 3000000,  185000,false),

    PRO_RANGER(MISC, 80, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_EASY, 150),
            new GameItem(PVM_HARD, 255),
            new GameItem(MISC_HARD, 150),
            new GameItem(FOUNDRY_ELITE, 30)},
            new GameItem(33107, 1), 3000000,  185000,false),

    SWEDISH_SWINDLE(MISC, 99, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_HARD, 150),
            new GameItem(PVM_HARD, 150),
            new GameItem(MISC_ELITE, 100),
            new GameItem(FOUNDRY_ELITE, 100)},
            new GameItem(33108, 1), 5000000,  200000,false),

    MONK_HEALS(MISC, 99, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_HARD, 50),
            new GameItem(PVM_HARD, 85),
            new GameItem(MISC_HARD, 50),
            new GameItem(FOUNDRY_ELITE, 50)},
            new GameItem(33117, 1), 3000000,  185000,false),

    DRAGON_FIRE(MISC, 65, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_EASY, 50),
            new GameItem(PVM_ELITE, 100),
            new GameItem(MISC_ELITE, 75),
            new GameItem(FOUNDRY_HARD, 50)},
            new GameItem(33118, 1), 1750000,  150000,false),

    OVERLOAD_PROTECTION(MISC, 25, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_EASY, 100),
            new GameItem(PVM_MEDIUM, 150),
            new GameItem(MISC_HARD, 75),
            new GameItem(FOUNDRY_EASY, 250)},
            new GameItem(33119, 1), 2500000,  15000,false),

    CANNON_EXTENDER(MISC, 80, new GameItem[]{new GameItem(26547, 1),
            new GameItem(SKILLING_HARD, 50),
            new GameItem(PVM_ELITE, 50),
            new GameItem(MISC_ELITE, 25),
            new GameItem(FOUNDRY_ELITE, 50)},
            new GameItem(33121, 1), 5000000,  150000,false),

    RUNECRAFTER(MISC, 25, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33079, 1), 500000,  15000,false),

    PRO_FLETCHER(MISC, 15, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33080, 1), 500000,  15000,false),

    SKILLED_THIEF(MISC, 15, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33087, 1), 500000,  15000,false),

    CRAFTING_GURU(MISC, 15, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33088, 1), 500000,  15000,false),

    HOT_HANDS(MISC, 15, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33089, 1), 500000,  15000,false),

    DEMON_SLAYER(MISC, 75, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_HARD, 50),
            new GameItem(FOUNDRY_ELITE, 25)},
            new GameItem(33093, 1), 500000,  15000,false),

    SLAYER_MASTER(MISC, 35, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33094, 1), 500000,  15000,false),

    PYROMANIAC(MISC, 15, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33095, 1), 500000,  15000,false),

    SKILLED_HUNTER(MISC, 15, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33096, 1), 500000,  15000,false),

    MOLTEN_MINER(MISC, 15, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33097, 1), 500000,  15000,false),

    WOODCHIPPER(MISC, 15, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33098, 1), 500000,  15000,false),

    BARE_HANDS(MISC, 15, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33099, 1), 500000,  15000,false),

    BARE_HANDS_X3(MISC, 15, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33100, 1), 500000,  15000,false),

    PRAYING_RESPECTS(MISC, 15, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33101, 1), 500000,  15000,false),

    PURE_SKILLS(MISC, 30, new GameItem[]{new GameItem(26546, 1),
            new GameItem(SKILLING_ELITE, 150),
            new GameItem(PVM_EASY, 170),
            new GameItem(MISC_MEDIUM, 100),
            new GameItem(FOUNDRY_EASY, 2000)},
            new GameItem(33122, 1), 1000000,  15000,false),

    IRON_GIANT(MISC, 15, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33077, 1), 500000,  15000,false),

    SLAYER_OVERRIDE(MISC, 35, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 150),
            new GameItem(PVM_EASY, 200),
            new GameItem(MISC_MEDIUM, 650),
            new GameItem(FOUNDRY_EASY, 2000)},
            new GameItem(33078, 1), 500000,  15000,false),

    THE_FUSIONIST(MISC, 80, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_EASY, 250),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_ELITE, 75),
            new GameItem(FOUNDRY_ELITE, 50)},
            new GameItem(33072, 1), 15000000,  15000,false),

    WILDY_SLAYER(MISC, 35, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33075, 1), 500000,  15000,false),

    SNEAKY_SNEAKY(MISC, 15, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33076, 1), 150000,  15000,false),

    CHISEL_MASTER(MISC, 15, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33081, 1), 150000,  15000,false),

    AVAS_ACCOMPLICE(MISC, 40, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_EASY, 50),
            new GameItem(PVM_HARD, 150),
            new GameItem(MISC_EASY, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33082, 1), 150000,  15000,false),

    DEEPER_POCKETS(MISC, 99, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_HARD, 150),
            new GameItem(PVM_HARD, 150),
            new GameItem(MISC_ELITE, 100),
            new GameItem(FOUNDRY_ELITE, 100)},
            new GameItem(33083, 1), 5000000,  200000,false),

    RECHARGER(MISC, 80, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_EASY, 1000),
            new GameItem(PVM_ELITE, 100),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_HARD, 100)},
            new GameItem(33084, 1), 500000,  15000,false),

    MAGIC_PAPER_CHANCE(MISC, 99, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_HARD, 75),
            new GameItem(PVM_HARD, 75),
            new GameItem(MISC_ELITE, 50),
            new GameItem(FOUNDRY_ELITE, 50)},
            new GameItem(33085, 1), 250000,  100000,false),

    DRAGON_BAIT(MISC, 15, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33086, 1), 150000,  15000,false),

    FOUNDRY_MASTER(MISC, 80, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_HARD, 75),
            new GameItem(PVM_HARD, 75),
            new GameItem(MISC_ELITE, 50),
            new GameItem(FOUNDRY_ELITE, 50)},
            new GameItem(33092, 1), 2500000,  15000,false),

    CASKET_MASTER(MISC, 45, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33114, 1), 150000,  15000,false),

    VOTING_KING(MISC, 65, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33115, 1), 150000,  15000,false),

    PET_LOCATOR(MISC, 15, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33116, 1), 150000,  15000,false),

    LUCKY_COIN(MISC, 65, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_MEDIUM, 1000)},
            new GameItem(33120, 1), 150000,  15000,false),

    PC_PRO(MISC, 15, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33123, 1), 150000,  15000,false),

    SLAYER_GURU(MISC, 15, new GameItem[]{new GameItem(26548, 1),
            new GameItem(SKILLING_ELITE, 75),
            new GameItem(PVM_EASY, 85),
            new GameItem(MISC_MEDIUM, 50),
            new GameItem(FOUNDRY_EASY, 1000)},
            new GameItem(33124, 1), 150000,  15000,false);


    private FusionTypes type;
    private GameItem[] required;
    private GameItem reward;
    private int cost, levelRequired, xp;
    private boolean rare;


    FusionMaterials(FusionTypes type, int levelRequired, GameItem[] required, GameItem reward, int cost, int xp, boolean rare) {
        this.type = type;
        this.levelRequired = levelRequired;
        this.required = required;
        this.reward = reward;
        this.cost = cost;
        this.xp = xp;
        this.rare = rare;
    }

    public static ArrayList<FusionMaterials> getForType(FusionTypes type) {
        ArrayList<FusionMaterials> fusionMaterialsArrayListArrayList = new ArrayList<>();
        for (FusionMaterials upgradeables : values()){
            if (upgradeables.getType() == type){
                fusionMaterialsArrayListArrayList.add(upgradeables);
            }
        }
        return fusionMaterialsArrayListArrayList;
    }

    private FusionTypes getType() {
        return type;
    }
}
