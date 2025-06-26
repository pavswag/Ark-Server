package io.kyros.content.battlepass;

import io.kyros.model.Items;
import io.kyros.model.items.GameItem;
import lombok.Getter;

@Getter
public class RewardList {
    @Getter
    public enum NormalRewardList {

        // 500,000 Nomad Value Items (Duplicates for increased count)
        I1(new GameItem(22326, 1)), // Justiciar - 500k
        I2(new GameItem(22327, 1)), // Justiciar - 500k
        I3(new GameItem(22328, 1)), // Justiciar - 500k
        I4(new GameItem(21018, 1)), // Ancestral - 500k
        I5(new GameItem(21021, 1)), // Ancestral - 500k
        I6(new GameItem(21024, 1)), // Ancestral - 500k
        I7(new GameItem(12899, 1)), // Trident of swamp - 500k
        I8(new GameItem(Items.THIRD_AGE_PLATESKIRT, 1)), // Third age plateskirt - 500k
        I9(new GameItem(Items.THIRD_AGE_BOW, 1)), // Third age bow - 500k
        I10(new GameItem(Items.THIRD_AGE_DRUIDIC_ROBE_TOP, 1)), // Third age druidic robe top - 500k
        I11(new GameItem(Items.THIRD_AGE_DRUIDIC_CLOAK, 1)), // Third age druidic cloak - 500k
        I12(new GameItem(Items.THIRD_AGE_DRUIDIC_ROBE_BOTTOMS, 1)), // Third age druidic robe bottoms - 500k
        I13(new GameItem(Items.THIRD_AGE_DRUIDIC_STAFF, 1)), // Third age druidic staff - 500k
        I14(new GameItem(Items.THIRD_AGE_LONGSWORD, 1)), // Third age longsword - 500k
        I15(new GameItem(Items.THIRD_AGE_AXE, 1)), // Third age axe - 500k
        I16(new GameItem(Items.THIRD_AGE_PICKAXE, 1)), // Third age pickaxe - 500k
        I17(new GameItem(12821, 1)), // Spectral spirit shield - 500k
        I18(new GameItem(12825, 1)), // Arcane spirit shield - 500k
        I19(new GameItem(21012, 1)), // Dragon hunter crossbow - 500k
        I20(new GameItem(12422, 1)), // 3rd age wand - 500k
        I21(new GameItem(12437, 1)), // 3rd age cape - 500k
        I22(new GameItem(12600, 1)), // Druidic wreath - 500k
        I23(new GameItem(22326, 1)), // Justiciar - 500k
        I24(new GameItem(22327, 1)), // Justiciar - 500k
        I25(new GameItem(22328, 1)), // Justiciar - 500k
        I26(new GameItem(21018, 1)), // Ancestral - 500k
        I27(new GameItem(21021, 1)), // Ancestral - 500k
        I28(new GameItem(21024, 1)), // Ancestral - 500k
        I29(new GameItem(12899, 1)), // Trident of swamp - 500k
        I30(new GameItem(Items.THIRD_AGE_PLATESKIRT, 1)), // Third age plateskirt - 500k
        I31(new GameItem(Items.THIRD_AGE_BOW, 1)), // Third age bow - 500k
        I32(new GameItem(Items.THIRD_AGE_DRUIDIC_ROBE_TOP, 1)), // Third age druidic robe top - 500k
        I33(new GameItem(Items.THIRD_AGE_DRUIDIC_CLOAK, 1)), // Third age druidic cloak - 500k
        I34(new GameItem(Items.THIRD_AGE_DRUIDIC_ROBE_BOTTOMS, 1)), // Third age druidic robe bottoms - 500k
        I35(new GameItem(Items.THIRD_AGE_DRUIDIC_STAFF, 1)), // Third age druidic staff - 500k
        I36(new GameItem(Items.THIRD_AGE_LONGSWORD, 1)), // Third age longsword - 500k
        I37(new GameItem(Items.THIRD_AGE_AXE, 1)), // Third age axe - 500k
        I38(new GameItem(Items.THIRD_AGE_PICKAXE, 1)), // Third age pickaxe - 500k

        // 750,000 Nomad Value Items (Some Duplicates)
        I39(new GameItem(26710, 1)), // Dragon warhammer (or) - 750k
        I40(new GameItem(26708, 1)), // Dragon claws (or) - 750k
        I41(new GameItem(25916, 1)), // Dragon hunter crossbow (t) - 750k
        I42(new GameItem(22547, 1)), // Craw's bow (u) - 750k
        I43(new GameItem(22550, 1)), // Craw's bow - 750k
        I44(new GameItem(22542, 1)), // Viggora's chainmace (u) - 750k
        I45(new GameItem(22545, 1)), // Viggora's chainmace - 750k
        I46(new GameItem(22552, 1)), // Thammaron's sceptre (u) - 750k
        I47(new GameItem(22555, 1)), // Thammaron's sceptre - 750k
        I48(new GameItem(26710, 1)), // Dragon warhammer (or) - 750k
        I49(new GameItem(26708, 1)), // Dragon claws (or) - 750k
        I50(new GameItem(25916, 1)), // Dragon hunter crossbow (t) - 750k
        I51(new GameItem(22547, 1)), // Craw's bow (u) - 750k
        I52(new GameItem(22550, 1)), // Craw's bow - 750k
        I53(new GameItem(22542, 1)), // Viggora's chainmace (u) - 750k
        I54(new GameItem(22545, 1)), // Viggora's chainmace - 750k

        // 1,000,000 Nomad Value Items
        I55(new GameItem(10556, 1)), // Attacker icon - 1M
        I56(new GameItem(10557, 1)), // Collector icon - 1M
        I57(new GameItem(10558, 1)), // Defender icon - 1M
        I58(new GameItem(10559, 1)), // Healer icon - 1M
        I59(new GameItem(26720, 1)), // Bandos boots (or) - 1M
        I60(new GameItem(26719, 1)), // Bandos tassets (or) - 1M
        I61(new GameItem(26718, 1)), // Bandos chestplate (or) - 1M
        I62(new GameItem(10556, 1)), // Attacker icon - 1M
        I63(new GameItem(10557, 1)), // Collector icon - 1M
        I64(new GameItem(10558, 1)), // Defender icon - 1M
        I65(new GameItem(10559, 1)), // Healer icon - 1M
        I66(new GameItem(26720, 1)), // Bandos boots (or) - 1M
        I67(new GameItem(26719, 1)), // Bandos tassets (or) - 1M

        // 1,250,000 Nomad Value Items
        I68(new GameItem(24417, 1)), // Inquisitor mace - 1.25M
        I69(new GameItem(23995, 1)), // Crystal blade - 1.25M
        I70(new GameItem(24419, 1)), // Inquisitor helm - 1.25M
        I71(new GameItem(24420, 1)), // Inquisitor plate - 1.25M
        I72(new GameItem(24421, 1)), // Inquisitor skirt - 1.25M
        I73(new GameItem(Items.ZURIELS_HOOD, 1)), // Zuriel's hood - 1.25M
        I74(new GameItem(Items.ZURIELS_ROBE_BOTTOM, 1)), // Zuriel's robe bottom - 1.25M
        I75(new GameItem(Items.ZURIELS_ROBE_TOP, 1)), // Zuriel's robe top - 1.25M
        I76(new GameItem(Items.STATIUSS_FULL_HELM, 1)), // Statius's full helm - 1.25M
        I77(new GameItem(Items.STATIUSS_PLATEBODY, 1)), // Statius's platebody - 1.25M
        I78(new GameItem(Items.STATIUSS_PLATELEGS, 1)), // Statius's platelegs - 1.25M
        I79(new GameItem(22978, 1)), // Dragon hunter lance - 1.25M
        I80(new GameItem(24668, 1)), // Twisted robe bottom - 1.25M
        I81(new GameItem(24666, 1)), // Twisted robe top - 1.25M
        I82(new GameItem(24664, 1)), // Twisted hat - 1.25M
        I83(new GameItem(24417, 1)), // Inquisitor mace - 1.25M
        I84(new GameItem(23995, 1)), // Crystal blade - 1.25M
        I85(new GameItem(24419, 1)), // Inquisitor helm - 1.25M

        // 1,500,000 Nomad Value Items
        I86(new GameItem(Items.VESTAS_CHAINBODY, 1)), // Vesta's chainbody - 1.5M
        I87(new GameItem(Items.VESTAS_PLATESKIRT, 1)), // Vesta's plateskirt - 1.5M
        I88(new GameItem(Items.MORRIGANS_COIF, 1)), // Morrigan's coif - 1.5M
        I89(new GameItem(Items.MORRIGANS_LEATHER_BODY, 1)), // Morrigan's leather body - 1.5M
        I90(new GameItem(Items.MORRIGANS_LEATHER_CHAPS, 1)), // Morrigan's leather chaps - 1.5M
        I91(new GameItem(Items.VESTAS_SPEAR, 1)), // Vesta's spear - 1.5M
        I92(new GameItem(Items.ZURIELS_STAFF, 1)), // Zuriel's staff - 1.5M
        I93(new GameItem(26714, 1)), // Arma helm (or) - 1.5M
        I94(new GameItem(26715, 1)), // Arma torso (or) - 1.5M
        I95(new GameItem(26716, 1)), // Arma legs (or) - 1.5M
        I96(new GameItem(26221, 1)), // Ancient cere top - 1.5M
        I97(new GameItem(26223, 1)), // Ancient cere legs - 1.5M
        I98(new GameItem(26225, 1)), // Ancient cere helm - 1.5M
        I99(new GameItem(24517, 1)), // Eldritch orb - 1.5M
        I100(new GameItem(24511, 1)), // Harmonised orb - 1.5M
        I101(new GameItem(24514, 1)), // Volatile orb - 1.5M
        I102(new GameItem(25918, 1)), // Dragon hunter crossbow (b) - 1.5M

        // 2,000,000 Nomad Value Items
        I103(new GameItem(Items.VESTAS_LONGSWORD, 1)), // Vesta's longsword - 2M
        I104(new GameItem(Items.STATIUSS_WARHAMMER, 1)), // Statius's warhammer - 2M

        // 2,250,000 Nomad Value Items
        I105(new GameItem(26382, 1)), // T helm - 2.25M
        I106(new GameItem(26384, 1)), // T chest - 2.25M
        I107(new GameItem(26386, 1)), // T legs - 2.25M

        // 2,500,000 Nomad Value Items
        I108(new GameItem(20788, 1)), // Row (i3) - 2.5M

        // 3,750,000 Nomad Value Items
        I109(new GameItem(20787, 1)), // Row (i4) - 3.75M

        // 5,000,000 Nomad Value Items
        I110(new GameItem(24422, 1)), // Nightmare staff - 5M
        I111(new GameItem(22324, 1)), // Ghrazi rapier - 5M
        I112(new GameItem(25979, 1)), // Keris partisan - 5M
        I113(new GameItem(25985, 1)), // Elidinis' ward - 5M
        I114(new GameItem(27100, 1)), // Elder maul (or) - 5M

        // 8,000,000 Nomad Value Items
        I115(new GameItem(26374, 1)), // Zaryte crossbow - 8M

        // 10,000,000 Nomad Value Items
        I116(new GameItem(20786, 1)), // Row (i5) - 10M
        I117(new GameItem(3128, 1)), // Durial's green phat - 10M
        I118(new GameItem(30014, 1)), // K'klik - 10M
        I119(new GameItem(25734, 1)), // Holy Ghrazi rapier - 10M

        // 15,000,000 Nomad Value Items
        I120(new GameItem(25975, 1)), // Lightbearer ring - 15M
        I121(new GameItem(33346, 1)), // Ember boots - 15M

        // 20,000,000 Nomad Value Items
        I122(new GameItem(22325, 1)), // Scythe of vitur - 20M
        I123(new GameItem(22323, 1)), // Sanguinesti staff - 20M
        I124(new GameItem(20997, 1)), // Twisted bow - 20M

        // 25,000,000 Nomad Value Items
        I125(new GameItem(21129, 1)), // Rng of wealth (i5) - 25M
        I126(new GameItem(30021, 1)), // Roc pet - 25M
        I127(new GameItem(30020, 1)), // Corrupt beast - 25M
        I128(new GameItem(33299, 1)), // Artorias helm - 25M
        I129(new GameItem(33300, 1)), // Artorias body - 25M
        I130(new GameItem(33301, 1)), // Artorias legs - 25M

        // Additional Duplicates to reach 162 items
        I131(new GameItem(24422, 1)), // Nightmare staff - 5M
        I132(new GameItem(22324, 1)), // Ghrazi rapier - 5M
        I133(new GameItem(25979, 1)), // Keris partisan - 5M
        I134(new GameItem(25985, 1)), // Elidinis' ward - 5M
        I135(new GameItem(27100, 1)), // Elder maul (or) - 5M
        I136(new GameItem(26374, 1)), // Zaryte crossbow - 8M
        I137(new GameItem(20786, 1)), // Row (i5) - 10M
        I138(new GameItem(3128, 1)), // Durial's green phat - 10M
        I139(new GameItem(30014, 1)), // K'klik - 10M
        I140(new GameItem(25734, 1)), // Holy Ghrazi rapier - 10M
        I141(new GameItem(25975, 1)), // Lightbearer ring - 15M
        I142(new GameItem(33346, 1)), // Ember boots - 15M
        I143(new GameItem(22325, 1)), // Scythe of vitur - 20M
        I144(new GameItem(22323, 1)), // Sanguinesti staff - 20M
        I145(new GameItem(20997, 1)), // Twisted bow - 20M
        I146(new GameItem(21129, 1)), // Rng of wealth (i5) - 25M
        I147(new GameItem(30021, 1)), // Roc pet - 25M
        I148(new GameItem(30020, 1)), // Corrupt beast - 25M
        I149(new GameItem(33299, 1)), // Artorias helm - 25M
        I150(new GameItem(33300, 1)), // Artorias body - 25M
        I151(new GameItem(33301, 1)), // Artorias legs - 25M
        I152(new GameItem(24422, 1)), // Nightmare staff - 5M
        I153(new GameItem(22324, 1)), // Ghrazi rapier - 5M
        I154(new GameItem(25979, 1)), // Keris partisan - 5M
        I155(new GameItem(25985, 1)), // Elidinis' ward - 5M
        I156(new GameItem(27100, 1)), // Elder maul (or) - 5M
        I157(new GameItem(26374, 1)), // Zaryte crossbow - 8M
        I158(new GameItem(20786, 1)), // Row (i5) - 10M
        I159(new GameItem(3128, 1)), // Durial's green phat - 10M
        I160(new GameItem(30014, 1)), // K'klik - 10M
        I161(new GameItem(25734, 1)), // Holy Ghrazi rapier - 10M
        I162(new GameItem(25975, 1)); // Lightbearer ring - 15M
        ;
        private final GameItem gameItem;
        NormalRewardList(GameItem gameItem) {
            this.gameItem = gameItem;
        }


    }

    @Getter
    public enum UltraRewardList {

        I1(new GameItem(25739, 1)),//Sanguine Scythe of Vitur
        I2(new GameItem(26269, 1)),//Dx Cbow
        I3(new GameItem(33311, 1)),//Plague helm
        I4(new GameItem(33312, 1)),//Plague Body
        I5(new GameItem(33313, 1)),//Plage Legs
        I6(new GameItem(25975, 1)),//Lightbearer
        I7(new GameItem(13681, 1)),//Cruciferous Codex
        I8(new GameItem(33308, 1)),//Reverie Helm
        I9(new GameItem(33309, 1)),//Reverie Body
        I10(new GameItem(33310, 1)),//Reverie Bottoms
        I12(new GameItem(2400, 25)),//Shadow Crusade Keys
        I13(new GameItem(27285, 10)),//Eyes of corruptor
        I15(new GameItem(12588, 200)),//Donator Mystery Box
        I16(new GameItem(13372, 1)),//Juan Gloves
        I17(new GameItem(22954, 1)),//Devout Boots
        I18(new GameItem(2403, 2)),//$100 scroll
        I19(new GameItem(7776, 5)),//2.5k Donor credits
        I20(new GameItem(33258, 2)),//Blue Coin Flip
        I21(new GameItem(33396, 5)),//Crusade Goodiebag
        I22(new GameItem(33389, 5)),//Freedom Goodiebag
        I23(new GameItem(25537, 5)),//Gear Goodiebag
        I24(new GameItem(33386, 5)),//BIS Goodiebag
        I25(new GameItem(33395, 5)),//Chaotic Goodiebag
        I26(new GameItem(33358, 10)),//Chaotic Box
        I27(new GameItem(33359, 10)),//Crusade Box
        I28(new GameItem(33357, 20)),//Epic Box
        I29(new GameItem(33354, 20)),//Wonder Box
        I30(new GameItem(33356, 5)),//Great Phantom Box
        I31(new GameItem(696, 4000)),//1Billion Nomad

        ;

        private final GameItem gameItem;
        UltraRewardList(GameItem gameItem) {
            this.gameItem = gameItem;
        }
    }
}
