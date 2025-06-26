package io.kyros.content.advancedslayer;

import io.kyros.content.skills.Skill;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static io.kyros.content.advancedslayer.ADVSlayer.*;

@Getter
public enum Gear {

    GREEN_DHIDE(new Integer[]{857,1135,1065,1099}, 40, 40, Difficulty.ANY, Skill.RANGED),
    BLUE_DHIDE(new Integer[]{861,2487,2499,2493}, 50, 40, Difficulty.ANY, Skill.RANGED),
    RED_DHIDE(new Integer[]{861,2489,2501,2495}, 60, 40, Difficulty.ANY, Skill.RANGED),

    BRONZE_ARMOR(new Integer[]{1321,1155,1117,1075,1189}, 1, 1, Difficulty.ANY, Skill.ATTACK),
    IRON_ARMOR(new Integer[]{1323,1153,1115,1067,1191}, 1, 1, Difficulty.ANY, Skill.ATTACK),
    STEEL_ARMOR(new Integer[]{1325,1157,1119,1069,1193}, 5, 5, Difficulty.ANY, Skill.ATTACK),
    MITH_ARMOR(new Integer[]{1329,1159,1121,1071,1197}, 20, 20, Difficulty.ANY, Skill.ATTACK),
    ADAMANT_ARMOR(new Integer[]{1331,1161,1123,1073,1199}, 30, 30, Difficulty.ANY, Skill.ATTACK),
    RUNE_ARMOR(new Integer[]{1333,1163,1127,1079,1201}, 40, 40, Difficulty.ANY, Skill.ATTACK),

    //MELEE SETS
    OBSIDIAN_ARMOR(new Integer[]{21300,21301,21304}, 1, 60, Difficulty.ANY, Skill.ATTACK),
    DRAGON_ARMOR(new Integer[]{2513,4087,10828,11840}, 1, 60, Difficulty.ANY, Skill.ATTACK),
    BANDOS_ARMOR(new Integer[]{11832,11834,11836}, 1, 65, Difficulty.ANY, Skill.ATTACK),
    BANDOS_OR_ARMOR(new Integer[]{26718,26719,26720}, 1, 65, Difficulty.ANY, Skill.ATTACK),
    THIRD_AGE_ARMOR(new Integer[]{10346,10348,10350,10352}, 1, 40, Difficulty.ANY, Skill.ATTACK),
    GUTHANS_ARMOR(new Integer[]{4724,4726,4728,4730}, 1, 70, Difficulty.ANY, Skill.ATTACK),
    VERACS_ARMOR(new Integer[]{4753,4755,4757,4759}, 1, 70, Difficulty.ANY, Skill.ATTACK),
    TORAGS_ARMOR(new Integer[]{4745,4747,4749,4751}, 1, 70, Difficulty.ANY, Skill.ATTACK),
    DHAROKS_ARMOR(new Integer[]{4716,4718,4720,4722}, 1, 70, Difficulty.ANY, Skill.ATTACK),
    JUSTICIAR_ARMOR(new Integer[]{22326,22327,22328}, 1, 75, Difficulty.ANY, Skill.ATTACK),
    INQUISITORS_ARMOR(new Integer[]{24419,24420,24421}, 1, 30, Difficulty.ANY, Skill.ATTACK),
    TORVA_ARMOR(new Integer[]{26382,26384,26386}, 1, 80, Difficulty.ANY, Skill.ATTACK),
    DRAGON_GUARD_ARMOR(new Integer[]{33189,33190,33191}, 1, 60, Difficulty.ANY, Skill.ATTACK),
    STARLIGHT(new Integer[]{33324,33325,33326}, 1, 80, Difficulty.ANY, Skill.ATTACK),
    ARTORIAS(new Integer[]{33296, 33297, 33298}, 1, 85, Difficulty.ANY, Skill.ATTACK),

   // RANGE SETS
    LEATHER_ARMOR(new Integer[]{1063,1095,1129}, 1, 1, Difficulty.ANY, Skill.RANGED),
    SNAKESKIN_ARMOR(new Integer[]{6322,6324,6326,6328}, 1, 30, Difficulty.ANY, Skill.RANGED),
    SPINED_ARMOR(new Integer[]{6131,6133,6135,6143,6149 }, 1, 40, Difficulty.ANY, Skill.RANGED),
    GREEN_DRAGONHIDE_ARMOR(new Integer[]{1065,1099,1135}, 1, 40, Difficulty.ANY, Skill.RANGED),
    BLUE_DRAGONHIDE_ARMOR(new Integer[]{2487,2493,2499}, 1, 50, Difficulty.ANY, Skill.RANGED),
    RED_DRAGONHIDE_ARMOR(new Integer[]{2489,2495,2501}, 1, 60, Difficulty.ANY, Skill.RANGED),
    BLACK_DRAGONHIDE_ARMOR(new Integer[]{2491,2497,2503}, 1, 70, Difficulty.ANY, Skill.RANGED),
    THIRD_AGE_RANGE_ARMOR(new Integer[]{10330,10332,10334,10336}, 1, 40, Difficulty.ANY, Skill.RANGED),
    CRYSTAL_ARMOR(new Integer[]{23971,23975,23979}, 1, 70, Difficulty.ANY, Skill.RANGED),
    CORRUPT_CRYSTAL_ARMOR(new Integer[]{23842,23845,23848}, 1, 70, Difficulty.ANY, Skill.RANGED),
    KARILS_ARMOR(new Integer[]{4732,4736,4738}, 1, 70, Difficulty.ANY, Skill.RANGED),
    ARMADYL_ARMOR(new Integer[]{11826,11828,11830}, 1, 70, Difficulty.ANY, Skill.RANGED),
    ARMADYL_OR_ARMOR(new Integer[]{26714,26715,26716}, 1, 70, Difficulty.ANY, Skill.RANGED),
    MASORI_ARMOR(new Integer[]{27226,27229,27232}, 1, 80, Difficulty.ANY, Skill.RANGED),
    MASORI_F_ARMOR(new Integer[]{27235,27238,27241}, 1, 80, Difficulty.ANY, Skill.RANGED),
    PERNIX(new Integer[]{33144,33145,33146}, 1, 80, Difficulty.ANY, Skill.RANGED),
    PLAGUE(new Integer[]{33311,33312,33313}, 1, 80, Difficulty.ANY, Skill.RANGED),
    ICE(new Integer[]{33292,33293,33294}, 1, 80, Difficulty.ANY, Skill.RANGED),

    //MAGIC SETS
    GHOSTLY_ARMOR(new Integer[]{6106,6107,6108,6109,6110,6111}, 1, 1, Difficulty.ANY, Skill.MAGIC),
    BLUE_MYSTIC_ARMOR(new Integer[]{4089,4091,4093,4095,4097}, 1, 40, Difficulty.ANY, Skill.MAGIC),
    RED_MYSTIC_ARMOR(new Integer[]{4099,4101,4103,4105,4107}, 1, 40, Difficulty.ANY, Skill.MAGIC),
    WHITE_MYSTIC_ARMOR(new Integer[]{4109,4111,4113,4115,4117}, 1, 40, Difficulty.ANY, Skill.MAGIC),
    ELDER_CHAOS_ARMOR(new Integer[]{20517,20520,20523}, 1, 40, Difficulty.ANY, Skill.MAGIC),
    ENCHANTED_ARMOR(new Integer[]{7398,7399,7400}, 1, 40, Difficulty.ANY, Skill.MAGIC),
    SPLITBARK_ARMOR(new Integer[]{3385,3387,3389,3391,3393}, 1, 40, Difficulty.ANY, Skill.MAGIC),
    SWAMPBARK_ARMOR(new Integer[]{25389,25392,25395,25398,25401}, 1, 50, Difficulty.ANY, Skill.MAGIC),
    BLOODBARK_ARMOR(new Integer[]{25404,25407,25410,25413,25416}, 1, 60, Difficulty.ANY, Skill.MAGIC),
    INFINITY_ARMOR(new Integer[]{6916,6918,6920,6922,6924}, 1, 50, Difficulty.ANY, Skill.MAGIC),
    THIRD_AGE_MAGIC_ARMOR(new Integer[]{10338,10340,10342}, 1, 65, Difficulty.ANY, Skill.MAGIC),
    DAGONHAI_ARMOR(new Integer[]{24288,24291,24294}, 1, 40, Difficulty.ANY, Skill.MAGIC),
    AHRIMS_ARMOR(new Integer[]{4708,4712,4714}, 1, 70, Difficulty.ANY, Skill.MAGIC),
    ANCESTRAL_ARMOR(new Integer[]{21018,21021,21024}, 1, 75, Difficulty.ANY, Skill.MAGIC),
    TWISTED_ANCESTRAL_ARMOR(new Integer[]{24664,24666,24668}, 1, 75, Difficulty.ANY, Skill.MAGIC),
    ROBES_OF_RUIN_ARMOR(new Integer[]{27428,27430,27432,27434,27436,27438}, 1, 75, Difficulty.ANY, Skill.MAGIC),
    VIRTUS_ARMOR(new Integer[]{33141,33142,33143}, 1, 75, Difficulty.ANY, Skill.MAGIC),
    ANCIENT_CEREMONIAL_ARMOR(new Integer[]{26225,26221,26223}, 1, 75, Difficulty.ANY, Skill.MAGIC),
    DARKNESS(new Integer[]{20128,20131,20137,20140,20134,20211}, 1, 75, Difficulty.ANY, Skill.MAGIC),
    REVERIE(new Integer[]{33308,33309,33310},1, 75, Difficulty.ANY, Skill.MAGIC),
    ELDER_OR(new Integer[]{27119, 27115, 27117}, 1, 75, Difficulty.ANY, Skill.MAGIC),


    ;

    public final Integer[] gameItems;
    public final int combatLevel;
    public final int defenseLevel;
    public final Difficulty difficulty;
    public final Skill skill;

    Gear(Integer[] gameItems, int combatLevel, int defenseLevel, Difficulty difficulty, Skill skill) {
        this.gameItems = gameItems;
        this.combatLevel = combatLevel;
        this.defenseLevel = defenseLevel;
        this.difficulty = difficulty;
        this.skill = skill;
    }

    public static ArrayList<Gear> Equipment(Player player, Difficulty difficulty) {
        ArrayList<Gear> RandoList = new ArrayList<>();
        for (Gear value : Gear.values()) {
            if (player.getLevel(value.skill) >= value.combatLevel && player.getLevel(Skill.DEFENCE) >= value.defenseLevel && (value.difficulty.equals(difficulty) || value.difficulty.equals(Difficulty.ANY))) {
                RandoList.add(value);
            }
        }
        return RandoList;
    }

    public static boolean hasAllEquipped(Player player) {
        int count = 0;
        for (Integer gameItem : player.getAdvGear().gameItems) {
            if (player.getItems().isWearingItem(gameItem)) {
                count++;
            }
        }
        System.out.println("gear counter " + count + " / " + player.getAdvGear().gameItems.length);
        return count == player.getAdvGear().gameItems.length;
    }

    public static void sendTasks(Player player) {
        getEasy = Arrays.stream(Tasks.values()).filter(tasks -> tasks.difficulty.equals(Difficulty.EASY)).collect(Collectors.toList());
        getNormal = Arrays.stream(Tasks.values()).filter(tasks -> tasks.difficulty.equals(Difficulty.NORMAL)).collect(Collectors.toList());
        getHard = Arrays.stream(Tasks.values()).filter(tasks -> tasks.difficulty.equals(Difficulty.HARD)).collect(Collectors.toList());

        taskEasy = getEasy.get(Misc.random(getEasy.size()));
        taskNormal = getNormal.get(Misc.random(getNormal.size()));
        taskHard = getHard.get(Misc.random(getHard.size()));

        easyGear = Equipment(player, taskEasy.difficulty);
        normalGear = Equipment(player, taskNormal.difficulty);
        hardGear = Equipment(player, taskHard.difficulty);

        if (easyGear.isEmpty() || normalGear.isEmpty() || hardGear.isEmpty()) {
            player.sendMessage("You need to get your stat's up before trying to get a task from here.");
            return;
        }



    }

    @Getter
    public enum Tasks {
        I("goblin", 50, 10, Difficulty.EASY),
        I1("cave crawler", 50, 10, Difficulty.EASY),
        I2("chasm crawler", 50, 10, Difficulty.EASY),
        I3("cave bug", 50, 10, Difficulty.EASY),
        I4("cockatrice", 50, 10, Difficulty.EASY),
        I5("rockslug", 50, 10, Difficulty.EASY),
        I6("hobgoblin", 50, 10, Difficulty.EASY),
        I7("possessed pickaxe", 50, 10, Difficulty.EASY),
        I8("magic axe", 50, 10, Difficulty.EASY),
        I9("cow", 50, 10, Difficulty.EASY),
        I10("rock crab", 50, 10, Difficulty.EASY),
        I11("crawling hand", 50, 10, Difficulty.EASY),
        I12("ghost", 50, 10, Difficulty.EASY),
        I13("hill giant", 50, 10, Difficulty.EASY),
        I14("skeleton", 50, 10, Difficulty.EASY),
        I15("chaos druid", 50, 10, Difficulty.EASY),
        I16("moss giant", 50, 10, Difficulty.EASY),
        I17("mammoth", 50, 10, Difficulty.EASY),
        I18("mountain troll", 50, 10, Difficulty.EASY),

        I19("basilisk", 50, 10, Difficulty.NORMAL),
        I21("ice warrior", 50, 10, Difficulty.NORMAL),
        I22("ice giant", 50, 10, Difficulty.NORMAL),
        I23("baby blue dragon", 50, 10, Difficulty.NORMAL),
        I24("bloodveld", 50, 10, Difficulty.NORMAL),
        I25("cyclops", 50, 10, Difficulty.NORMAL),
        I26("ankou", 50, 10, Difficulty.NORMAL),
        I27("fire giant", 50, 10, Difficulty.NORMAL),
        I28("infernal mage", 50, 10, Difficulty.NORMAL),
        I29("jelly", 50, 10, Difficulty.NORMAL),
        I30("lesser demon", 50, 10, Difficulty.NORMAL),
        I31("pyrefiend", 50, 10, Difficulty.NORMAL),
        I32("cave horror", 50, 10, Difficulty.NORMAL),
        I33("dagannoth", 50, 10, Difficulty.NORMAL),
        I34("turoth", 50, 10, Difficulty.NORMAL),
        I35("black demon", 50, 10, Difficulty.NORMAL),
        I36("cave horror", 50, 10, Difficulty.NORMAL),
        I37("blue dragon", 50, 10, Difficulty.NORMAL),
        I38("brutal blue dragon", 50, 10, Difficulty.NORMAL),
        I39("black dragon", 50, 10, Difficulty.NORMAL),
        I40("brutal black dragon", 50, 10, Difficulty.NORMAL),
        I41("cave horror", 50, 10, Difficulty.NORMAL),
        I42("dust devil", 50, 10, Difficulty.NORMAL),
        I43("fire giant", 50, 10, Difficulty.NORMAL),
        I44("gargoyle", 50, 10, Difficulty.NORMAL),
        I45("greater demon", 50, 10, Difficulty.NORMAL),
        I46("hellhound", 50, 10, Difficulty.NORMAL),
        I47("elf warrior", 50, 10, Difficulty.NORMAL),
        I48("iron dragon", 50, 10, Difficulty.NORMAL),
        I49("kurask", 50, 10, Difficulty.NORMAL),
        I50("mithril dragon", 50, 10, Difficulty.NORMAL),
        I51("nechryael", 50, 10, Difficulty.NORMAL),
        I52("steel dragon", 50, 10, Difficulty.NORMAL),
        I53("twisted banshee", 50, 10, Difficulty.NORMAL),
        I54("warped jelly", 50, 10, Difficulty.NORMAL),
        I55("barrelchest", 50, 10, Difficulty.NORMAL),
        I56("giant mole", 50, 10, Difficulty.NORMAL),
        I57("lizardman shaman", 50, 10, Difficulty.NORMAL),
        I58("revenant", 50, 10, Difficulty.NORMAL),
        I59("ent", 50, 10, Difficulty.NORMAL),
        I60("dark warrior", 50, 10, Difficulty.NORMAL),
        I61("adamant dragon", 50, 10, Difficulty.NORMAL),
        I62("undead druid", 50, 10, Difficulty.NORMAL),
        I63("bronze dragon", 50, 10, Difficulty.NORMAL),
        I64("drake", 50, 10, Difficulty.NORMAL),
        I65("hydra", 50, 10, Difficulty.NORMAL),
        I66("wyrm", 50, 10, Difficulty.NORMAL),
        I67("Tree Spirit", 50, 10, Difficulty.NORMAL),

        I68("abyssal demon", 50, 10, Difficulty.HARD),
        I69("abyssal sire", 50, 10, Difficulty.HARD),
        I70("aviansie", 50, 10, Difficulty.HARD),
        I71("kree'arra", 50, 10, Difficulty.HARD),
        I72("demonic gorilla", 50, 10, Difficulty.HARD),
        I73("kraken", 50, 10, Difficulty.HARD),
        I74("dark beast", 50, 10, Difficulty.HARD),
        I75("k'ril tsutsaroth", 50, 10, Difficulty.HARD),
        I76("cerberus", 50, 10, Difficulty.HARD),
        I77("smoke devil", 50, 10, Difficulty.HARD),
        I78("thermonuclear smoke devil", 50, 10, Difficulty.HARD),
        I79("sarachnis", 50, 10, Difficulty.HARD),
        I80("vorkath", 50, 10, Difficulty.HARD),
        I81("the nightmare", 50, 10, Difficulty.HARD),
        I82("corporeal beast", 50, 10, Difficulty.HARD),
        I83("general graardor", 50, 10, Difficulty.HARD),
        I84("commander zilyana", 50, 10, Difficulty.HARD),
        I85("venenatis", 50, 10, Difficulty.HARD),
        I86("king black dragon", 50, 10, Difficulty.HARD),
        I87("alchemical hydra", 50, 10, Difficulty.HARD),
        I88("zulrah", 50, 10, Difficulty.HARD),
        I89("kalphite queen", 50, 10, Difficulty.HARD),
        I90("rune dragon", 50, 10, Difficulty.HARD),
        ;

        public final String npcName;
        public final int amount;
        public final int time;
        public final Difficulty difficulty;

        Tasks(String npcName, int amount, int time, Difficulty difficulty) {
            this.npcName = npcName;
            this.amount = amount;
            this.time = time;
            this.difficulty = difficulty;
        }
    }
}