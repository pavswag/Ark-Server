package io.kyros.content.achievement;

import io.kyros.content.achievement.inter.TasksInterface;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.seasons.Halloween;
import io.kyros.model.Items;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ImmutableItem;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Jason http://www.rune-server.org/members/jason
 * @date Mar 26, 2014 Mystery & Super done
 */
public class Achievements {

    public enum Achievement {

        /**
         * Tier 1 Achievement Start
         */

        Voter("I Voted", 1, AchievementTier.STARTER, AchievementType.VOTER, "Vote %d Time", 1, 0,
                new GameItem(11739, 5), new GameItem(Items.VOTE_CRYSTAL, 5)),

        Questing("Complete Knowledge to ArkCane", 2, AchievementTier.STARTER, AchievementType.ARK_QUEST, "Complete Knowledge to ArkCane", 1,1,
                new GameItem (20790,1)),

        Crabby("Kill Rock Crabs %d times", 3, AchievementTier.STARTER, AchievementType.SLAY_ROCKCRAB, "Kill Rock Crabs %d times", 75, 1,
                new GameItem(20000, 1), new GameItem(26486, 1), new GameItem(4675, 1)),

        Daily("Dailyscape", 4, AchievementTier.STARTER, AchievementType.DAILY, "Collect %d Daily Reward", 1, 1,
                new GameItem(692, 3), new GameItem(6679, 25)),

        Presets("Save a Preset", 5, AchievementTier.STARTER, AchievementType.PRESETS, "Save %d Preset", 1, 0,
                new GameItem(693, 2), new GameItem(6679, 25)),

        Collector("Collector", 6, AchievementTier.STARTER, AchievementType.COLLECTOR, "Add %d item to your\\nCollection Log", 5, 0,
                new GameItem(691, 5), new GameItem(6679, 100)),

        Burner("Burn It", 7, AchievementTier.STARTER, AchievementType.FOE_POINTS, "Burn %d Exchange\\nPoints at FoE", 25000, 1,
                new GameItem(696, 2), new GameItem(6679, 50)),

        WOGW_Donation("The Giver", 8, AchievementTier.STARTER, AchievementType.WOGW, "Donate %d to\\nWell of Good Will", 2_500_000, 0,
                new GameItem(6679, 50)),

        The_Slayer("The Slayer", 9, AchievementTier.STARTER, AchievementType.SLAY, "Complete %d Slayer Tasks", 1, 1,
                new GameItem(13438, 1), new GameItem(7629, 3)),
//

        NEWB_VOTER("Democracy %d", 10, AchievementTier.TIER_1, AchievementType.VOTE_CHEST_UNLOCK, "Open %d Vote Chest", 3, 1,
                new GameItem(22093, 1), new GameItem(11739, 10), new GameItem(23933, 5)),

        CKey_Task("Crystal Clear %d", 11, AchievementTier.TIER_1, AchievementType.LOOT_CRYSTAL_CHEST, "Loot Crystal Chest %d Times", 50, 1,
                new GameItem(693, 20), new GameItem(6677, 25), new GameItem (989, 20)),

        ClueScroll_Task("Treasure Trails %d", 12, AchievementTier.TIER_1, AchievementType.CLUES, "Loot %d Clue Caskets", 50, 1,
                new GameItem(696, 10), new GameItem(6679, 100), new GameItem(10025, 10)),

        UNIQUE_DROP_NOVICE("Obtain %d unique drops", 13, AchievementTier.TIER_1, AchievementType.UNIQUE_DROPS, "Obtain %d unique drops", 50, 1,
                new GameItem(696, 20), new GameItem(6677, 25), new GameItem(6678, 10)),

        UPGRADE_ITEMS_NOVICE("Upgrade %d items", 14, AchievementTier.TIER_1, AchievementType.UPGRADE, "Upgrade %d items",  10, 1,
                new GameItem(696, 10), new GameItem(6679, 75), new GameItem(6677, 25)),

        Pc_Task("Pest Control %d", 15, AchievementTier.TIER_1, AchievementType.PEST_CONTROL_ROUNDS, "Complete Pest Control\\n%d Times", 50, 1,
                new GameItem(696, 10), new GameItem(11666, 1), new GameItem(8841)),

        Slayer_Task_I("Slayer %d", 16, AchievementTier.TIER_1, AchievementType.SLAY, "Complete %d Slayer Tasks", 50, 1,
                new GameItem(696, 30), new GameItem(13438, 5), new GameItem(7629, 5)),

        DRAGON_SLAYER_I("Dragon Hunter %d", 17, AchievementTier.TIER_1, AchievementType.SLAY_DRAGONS, "Kill %d Dragons", 25, 1,
                new GameItem(693, 5), new GameItem(6679, 50), new GameItem(22124, 10)),

        Barrows_Task_I("Barrows %d", 18, AchievementTier.TIER_1, AchievementType.BARROWS_KILLS, "Kill %d barrows npcs", 100, 1,
                new GameItem(693, 20), new GameItem(6677, 25)),

        Boss_Hunter_I(19, AchievementTier.TIER_1, AchievementType.SLAY_BOSSES, "Kill 100 Bosses", 100, 1,
                new GameItem(696, 20), new GameItem(6677, 100)),

        PvMer_I("Mob Killer %d", 20, AchievementTier.TIER_1, AchievementType.SLAY_ANY_NPCS, "Kill %d NPCs", 1000, 1,
                new GameItem(696, 10), new GameItem(6679 , 250), new GameItem(989, 10)),

        Jad_Task("Fight Caves %d", 21, AchievementTier.TIER_1, AchievementType.FIGHT_CAVES_ROUNDS, "Complete the Fight Caves", 1, 1,
                new GameItem(696, 10), new GameItem(6570, 1)),

        MANTICORE("Kill Manticore %d times", 22, AchievementTier.TIER_1, AchievementType.SLAY_MANTICORE, "Kill %d Manticore", 10, 1,
                new GameItem(13346, 25), new GameItem(19887, 10), new GameItem(696, 40)),

        JAVELIN_COLOSSUS("Kill JavelinColossus %d times", 37, AchievementTier.TIER_1, AchievementType.SLAY_JAVELIN_COLOSSUS, "Kill %d Javelin Colossus", 10, 1,
                new GameItem(13346, 25), new GameItem(19887, 10), new GameItem(696, 40)),

        BABA("Kill BaBa's Phantom %d times", 38, AchievementTier.TIER_1, AchievementType.SLAY_BABA, "Kill %d BaBa's Phantom", 10, 1,
                new GameItem(13346, 25), new GameItem(19887, 10), new GameItem(696, 40)),

        GROOT("Kill Groot %d times", 39, AchievementTier.TIER_1, AchievementType.SLAY_GROOT, "Kill %d Groot", 10, 1,
                new GameItem(33378, 2), new GameItem(33354, 1), new GameItem(6805, 5), new GameItem(696, 40)),

        VBOSS("Kill Vote Boss %d times", 40, AchievementTier.TIER_1, AchievementType.SLAY_VBOSS, "Kill %d Vote Boss", 10, 1,
                new GameItem(33378, 2), new GameItem(33354, 1), new GameItem(6805, 5), new GameItem(696, 40)),

        DBOSS("Kill Dono Boss %d times", 41, AchievementTier.TIER_1, AchievementType.SLAY_DBOSS, "Kill %d Dono Boss", 10, 1,
                new GameItem(33378, 2), new GameItem(33354, 1), new GameItem(6805, 5), new GameItem(696, 40)),

        DURIAL("Kill Durial %d times", 42, AchievementTier.TIER_1, AchievementType.SLAY_DURIAL, "Kill %d Durial", 10, 1,
                new GameItem(33378, 2), new GameItem(33354, 1), new GameItem(6805, 5), new GameItem(696, 40)),

        AFK("Kill AFK Goblin %d times", 43, AchievementTier.TIER_1, AchievementType.SLAY_AFK, "Kill %d AFK Goblin", 10, 1,
                new GameItem(33378, 2), new GameItem(33354, 1), new GameItem(6805, 5), new GameItem(696, 40)),

        COX_NOVICE( 23, AchievementTier.TIER_1, AchievementType.COX, "Complete %d Chamber\\nof Xeric runs (Raids 1)", 100, 4,
                new GameItem(696, 40), new GameItem(6678, 25), new GameItem(12585, 25)),

        TOB_NOVICE( 24, AchievementTier.TIER_1, AchievementType.TOB, "Complete %d Theatre\\nof Blood runs (Raids 2)", 100, 4,
                new GameItem(696, 40), new GameItem(6678, 25), new GameItem(19895, 25)),

        ARBOGRAVE_NOVICE(25, AchievementTier.TIER_1, AchievementType.ARBO, "Complete %d Arbograve\\nSwamp runs (Raids 3)",  10, 1,
                new GameItem(696, 10), new GameItem(2400, 5), new GameItem(6678, 10), new GameItem(6680, 10)),

        SHADOW_CRUSADE_NOVICE(44, AchievementTier.TIER_1, AchievementType.SHADOW_CRUSADE, "Complete %d Shadow Crusade Raids",  10, 1,
                new GameItem(696, 10), new GameItem(28416, 5), new GameItem(33360, 10), new GameItem(6678, 10)),

        Fishing_Task_I("Fishing %d", 26, AchievementTier.TIER_1, AchievementType.FISH, "Catch %d Fish", 1000, 1,
                new GameItem(696, 10), new GameItem(25527, 750)),

        Cooking_Task_I("Cooking %d", 27, AchievementTier.TIER_1, AchievementType.COOK, "Cook %d Fish", 1000, 1,
                new GameItem(696, 10), new GameItem(25527, 750)),

        Mining_Task_I("Mining %d", 28, AchievementTier.TIER_1, AchievementType.MINE, "Mine %d Rocks", 1000, 1,
                new GameItem(696, 10), new GameItem(25527, 750)),

        Smithing_Task_I("Smithing %d", 29, AchievementTier.TIER_1, AchievementType.SMITH, "Smith %d Bars", 1000, 1,
                new GameItem(696, 10), new GameItem(25527, 750)),

        Farming_Task_I("Farming %d", 30, AchievementTier.TIER_1, AchievementType.FARM, "Harvest %d Crops", 500, 1,
                new GameItem(696, 10), new GameItem(25527, 750)),

        Herblore_Task_I("Herblore %d", 31, AchievementTier.TIER_1, AchievementType.HERB, "Create %d Potions", 500, 1,
                new GameItem(696, 10), new GameItem(25527, 750)),

        Woodcutting_Task_I("Woodcutting %d", 32, AchievementTier.TIER_1, AchievementType.WOODCUT, "Cut %d Trees", 1000, 1,
                new GameItem(696, 10), new GameItem(25527, 750)),

        Fletching_Task_I("Fletching %d", 33, AchievementTier.TIER_1, AchievementType.FLETCH, "Fletch %d Logs", 1000, 1,
                new GameItem(696, 10), new GameItem(25527, 750)),

        Firemaking_Task_I("Firemaking %d", 34, AchievementTier.TIER_1, AchievementType.FIRE, "Light %d Logs", 500, 1,
                new GameItem(696, 10), new GameItem(25527, 750)),

        Theiving_Task_I("Thieving %d", 35, AchievementTier.TIER_1, AchievementType.THIEV, "Steal %d Times", 500, 1,
                new GameItem(696, 10), new GameItem(25527, 750)),

        Agility_Task_I("Agility %d", 36, AchievementTier.TIER_1, AchievementType.AGIL, "Complete v Agility\\nCourse Laps", 100, 1,
                new GameItem(696, 10), new GameItem(11849, 80), new GameItem(12792, 1)),

        TREASURE_GAMES_NOVICE("Treasure Games %d", 45, AchievementTier.TIER_1, AchievementType.TREASURE_GAMES, "Completed Treasure Games %d times", 250, 1,
                new GameItem(6805, 2), new GameItem(33361, 25), new GameItem(696, 10), new GameItem(13346, 30)),

        SOL_HEREDIT("Sol Heredit %d", 46, AchievementTier.TIER_1, AchievementType.SLAY_SOL_HEREDIT, "Kill Sol Heredit %d times", 50, 1,
                new GameItem(13346, 50), new GameItem(19887, 10), new GameItem(6805, 2), new GameItem(696, 100)),

        ISLE_OF_THE_DAMNED("Isle Of The Damned %d", 47, AchievementTier.TIER_1, AchievementType.ISLE_OF_DAMNED, "Finish Isle Of The Damned %d times", 100, 1,
                new GameItem(33354, 1), new GameItem(19887, 10), new GameItem(6805, 2), new GameItem(696, 100)),

        SHARATHTEERK("Sharathteerk %d", 48, AchievementTier.TIER_1, AchievementType.SLAY_SHARATHTEERK, "Kill Sharathteerk %d times", 100, 1,
                new GameItem(33362, 1), new GameItem(19887, 10), new GameItem(6805, 2), new GameItem(696, 100)),

        LUKE_SLAYER_I("Kill 50% Luke %d times", 49, AchievementTier.TIER_1, AchievementType.SLAY_LUKE, "Kill %d 50% Luke", 10, 1,
                new GameItem(33378, 2), new GameItem(33354, 1), new GameItem(6805, 5), new GameItem(696, 40)),

        TUMEKENS_WARDEN("Kill Tumekens Warden %d times", 50, AchievementTier.TIER_1, AchievementType.SLAY_TUMEKEN, "Kill %d Tumekens Warden", 100, 1,
                new GameItem(33362, 2), new GameItem(33354, 1), new GameItem(6805, 5), new GameItem(696, 40)),

        ARAXXOR("Kill Araxxor %d times", 51, AchievementTier.TIER_1, AchievementType.SLAY_ARAXXOR, "Kill %d Arraxor", 100, 1,
                new GameItem(33362, 2), new GameItem(33354, 2), new GameItem(6805, 10), new GameItem(33428, 50)),

        YAMA("Kill Judge Of Yama %d times", 52, AchievementTier.TIER_1, AchievementType.SLAY_YAMA, "Kill %d Judge Of Yama", 100, 1,
                new GameItem(33362, 2), new GameItem(33354, 2), new GameItem(6805, 10), new GameItem(33428, 50)),

        VARDORVIS("Kill Vardorvis %d times", 53, AchievementTier.TIER_1, AchievementType.SLAY_VARDORVIS, "Kill %d Vardorvis", 100, 1,
                new GameItem(33362, 2), new GameItem(33354, 2), new GameItem(6805, 10), new GameItem(33428, 50)),

        XAMPHUR("Kill Xamphur %d times", 54, AchievementTier.TIER_1, AchievementType.SLAY_XAMPHUR, "Kill %d Xamphur", 100, 1,
                new GameItem(33362, 2), new GameItem(33354, 2), new GameItem(6805, 10), new GameItem(33428, 50)),

        WHISPERER("Kill The Whisperer %d times", 55, AchievementTier.TIER_1, AchievementType.SLAY_WHISPERER, "Kill %d The Whisperer", 100, 1,
                new GameItem(33362, 2), new GameItem(33354, 2), new GameItem(6805, 10), new GameItem(33428, 50)),

        MINOTAUR("Kill The Minotaur %d times", 56, AchievementTier.TIER_1, AchievementType.SLAY_MINOTAUR, "Kill %d The Minotaur", 100, 1,
                new GameItem(33362, 2), new GameItem(33354, 2), new GameItem(6805, 10), new GameItem(33428, 50)),

        DUKE("Kill Duke %d times", 57, AchievementTier.TIER_1, AchievementType.SLAY_DUKE, "Kill %d Duke", 100, 1,
                new GameItem(33362, 2), new GameItem(33354, 2), new GameItem(6805, 10), new GameItem(33428, 50)),

        /**
         * Tier 2 Achievement Start
         */


        ADVANCED_VOTER("Democracy %d", 19, AchievementTier.TIER_2, AchievementType.VOTE_CHEST_UNLOCK, "Open %d Vote Chests", 5, 2,
                new GameItem(22093, 2), new GameItem(11739, 15), new GameItem(23933, 10)),

        CHEST_LOOTER("Crystal Clear %d", 16, AchievementTier.TIER_2, AchievementType.LOOT_CRYSTAL_CHEST, "Loot Crystal Chest %d Times", 100, 2,
                new GameItem(696, 4), new GameItem(6677, 25), new GameItem(989, 20)),


        CLUE_SCROLLER("Treasure Trails %d", 18, AchievementTier.TIER_2, AchievementType.CLUES, "Loot %d Clue Caskets", 125, 2,
                new GameItem(696, 10), new GameItem(6677, 25), new GameItem(10025, 10)),

        UNIQUE_DROP_INTERMEDIATE("Obtain %d unique drops", 43, AchievementTier.TIER_2, AchievementType.UNIQUE_DROPS, "Obtain %d unique drops", 250, 1,
                new GameItem(696, 100), new GameItem(6678, 25), new GameItem(6805, 1)),

        UPGRADE_ITEMS_INTERMEDIATE("Upgrade %d items", 44, AchievementTier.TIER_2, AchievementType.UPGRADE, "Upgrade %d items",  100, 1,
                new GameItem(696, 40), new GameItem(6677, 100), new GameItem(6678, 25)),

        SLAYER_DESTROYER("Slayer %d", 15, AchievementTier.TIER_2, AchievementType.SLAY, "Complete %d Slayer Tasks", 75, 2,
                new GameItem(696, 30), new GameItem(13438, 10), new GameItem(7629, 5)),

        Dragon_Hunter_II("Dragon Hunter %d", 2, AchievementTier.TIER_2, AchievementType.SLAY_DRAGONS, "Kill %d Dragons", 350, 2,
                new GameItem(696, 20), new GameItem(6677, 50), new GameItem(22124, 25)),

        Barrows_Task_III("Barrows %d", 41, AchievementTier.TIER_2, AchievementType.BARROWS_KILLS, "Kill %d barrows npcs", 250, 1,
                new GameItem(696, 10), new GameItem(6679, 75), new GameItem(6677, 50)),

        Boss_Hunter_II(3, AchievementTier.TIER_2, AchievementType.SLAY_BOSSES, "Kill %d Bosses", 750, 2, new GameItem(696, 20),
                new GameItem(6678, 50), new GameItem(6677, 150)),

        PvMer_II("Mob Killer %d", 1, AchievementTier.TIER_2, AchievementType.SLAY_ANY_NPCS, "Kill %d NPCs", 3000, 2,
                new GameItem(696, 20), new GameItem(6677, 150), new GameItem(989, 20)),

        RED_OF_FURY("Fight Caves %d", 17, AchievementTier.TIER_2, AchievementType.FIGHT_CAVES_ROUNDS, "Complete Fight Caves %d Times", 10, 2,
                new GameItem(696, 10),  new GameItem(6570, 3)),

        INFERNO("Inferno %d", 42, AchievementTier.TIER_2, AchievementType.INFERNO, "Complete Inferno %d Times", 10, 1,
                new GameItem(696, 30), new GameItem(6678, 10), new GameItem (21295, 5)),

        COX_SLAYER( 39, AchievementTier.TIER_2, AchievementType.COX, "Complete %d Chamber\\nof Xeric runs (Raids 1)", 250, 4,
                new GameItem(696, 40), new GameItem(6678, 50), new GameItem(12585, 50)),

        TOB_SLAYER( 40, AchievementTier.TIER_2, AchievementType.TOB, "Complete %d Theatre\\nof Blood runs (Raids 2)", 250, 4,
                new GameItem(696, 40), new GameItem(6678, 50), new GameItem(19895, 50)),

        ARBOGRAVE_SLAYER(45, AchievementTier.TIER_2, AchievementType.ARBO, "Complete %d Arbograve\\nSwamp runs (Raids 3)",  250, 4,
                new GameItem(2400, 25), new GameItem(27285, 1), new GameItem(696, 80), new GameItem (6680, 25)),

        SHADOW_CRUSADE_SLAYER(54, AchievementTier.TIER_2, AchievementType.SHADOW_CRUSADE, "Complete %d Shadow Crusade Raids",  250, 4,
                new GameItem(28416, 25), new GameItem(26886, 1), new GameItem(696, 80), new GameItem (33360, 25)),

        NEX("Kill Nex %d Times", 20, AchievementTier.TIER_2, AchievementType.SLAY_NEX, "Kill Nex %d Times", 100, 2, new GameItem(696, 20),
                new GameItem(6678, 50), new GameItem(6677,50)),

        NIGHTMARE("Kill Nightmare %d Times", 21, AchievementTier.TIER_2, AchievementType.NIGHTMARE, "Kill Nightmare %d Times", 100, 2,
                new GameItem(696, 20), new GameItem(6678, 50), new GameItem(6677,50)),

        VORKATH("Kill Vorkath %d Times", 22, AchievementTier.TIER_2, AchievementType.SLAY_VORKATH, "Kill Vorkath %d Times", 100, 2,
                new GameItem(696, 20), new GameItem(6677, 50)),

        ZULRAH("Kill Zulrah %d Times", 23, AchievementTier.TIER_2, AchievementType.SLAY_ZULRAH, "Kill Zulrah %d Times", 100, 2,
                new GameItem (696, 20), new GameItem(6677, 50)),

        HYDRA("Kill Hydra %d Times", 24, AchievementTier.TIER_2, AchievementType.HYDRA, "Kill Hydra %d Times", 100, 2,
                new GameItem(696, 20), new GameItem(6678, 50), new GameItem(6677,50)),

        CERB("Kill Cerberus %d Times", 25, AchievementTier.TIER_2, AchievementType.SLAY_CERB, "Kill Cerberus %d Times", 100, 2,
                new GameItem(696, 20), new GameItem(6678, 50), new GameItem(6677,50)),

        CORP("Kill Corporeal Beast %d Times", 26, AchievementTier.TIER_2, AchievementType.SLAY_CORP, "Kill Corporeal Beast %d Times", 100, 2,
                new GameItem(696, 20), new GameItem(6678, 50)),

        KBD("Kill KBD %d Times", 27, AchievementTier.TIER_2, AchievementType.SLAY_KBD, "Kill KBD %d Times", 100, 2, new GameItem(696, 20),
                new GameItem(6678, 50), new GameItem(6677,50)),

        SIRE("Kill SIRE %d Times", 28, AchievementTier.TIER_2, AchievementType.SLAY_SIRE, "Kill Abyssal Sire %d Times", 100, 2,
                new GameItem(696, 20), new GameItem(6677, 50)),

        KRAKEN("Kill Kraken %d Times", 29, AchievementTier.TIER_2, AchievementType.SLAY_KRAKEN, "Kill Kraken %d Times", 100, 2,
                new GameItem(696, 20), new GameItem(6677, 50)),

        MANTICORE_1("Kill Manticore %d times", 46, AchievementTier.TIER_2, AchievementType.SLAY_MANTICORE, "Kill %d Manticore", 100, 2,
                new GameItem(13346, 50), new GameItem(19887, 20), new GameItem(696, 80)),

        JAVELIN_COLOSSUS_1("Kill Javelin Colossus %d times", 47, AchievementTier.TIER_2, AchievementType.SLAY_JAVELIN_COLOSSUS, "Kill %d Javelin Colossus", 100, 2,
                new GameItem(13346, 50), new GameItem(19887, 20), new GameItem(696, 80)),

        BABA_1("Kill BaBa's Phantom %d times", 48, AchievementTier.TIER_2, AchievementType.SLAY_BABA, "Kill %d BaBa's Phantom", 100, 2,
                new GameItem(13346, 50), new GameItem(19887, 20), new GameItem(696, 80)),

        GROOT_1("Kill Groot %d times", 49, AchievementTier.TIER_2, AchievementType.SLAY_GROOT, "Kill %d Groot", 100, 2,
                new GameItem(33378, 4), new GameItem(33354, 3), new GameItem(6805, 10), new GameItem(696, 80)),

        VBOSS_1("Kill Vote Boss %d times", 50, AchievementTier.TIER_2, AchievementType.SLAY_VBOSS, "Kill %d Vote Boss", 100, 2,
                new GameItem(33378, 4), new GameItem(33354, 3), new GameItem(6805, 10), new GameItem(696, 80)),

        DBOSS_1("Kill Dono Boss %d times", 51, AchievementTier.TIER_2, AchievementType.SLAY_DBOSS, "Kill %d Dono Boss", 100, 2,
                new GameItem(33378, 4), new GameItem(33354, 3), new GameItem(6805, 10), new GameItem(696, 80)),

        DURIAL_1("Kill Durial %d times", 52, AchievementTier.TIER_2, AchievementType.SLAY_DURIAL, "Kill %d Durial", 100, 2,
                new GameItem(33378, 4), new GameItem(33354, 3), new GameItem(6805, 10), new GameItem(696, 80)),

        AFK_1("Kill AFK Goblin %d times", 53, AchievementTier.TIER_2, AchievementType.SLAY_AFK, "Kill %d AFK Goblin", 100, 2,
                new GameItem(33378, 4), new GameItem(33354, 3), new GameItem(6805, 10), new GameItem(696, 80)),

        VETION("Kill Vet'ion %d Times", 30, AchievementTier.TIER_2, AchievementType.SLAY_VETION, "Kill Vet'ion %d Times", 100, 2,
                new GameItem(2996, 2500), new GameItem(4185, 10), new GameItem(6792, 10), new GameItem(6677, 50)),

        CALLISTO("Kill Callisto %d Times", 31, AchievementTier.TIER_2, AchievementType.SLAY_CALLISTO, "Kill Callisto %d Times", 100, 2,
                new GameItem(2996, 2500), new GameItem(4185, 10), new GameItem(6792, 10), new GameItem(6677, 50)),

        SCORPIA("Kill Scorpia %d Times", 32, AchievementTier.TIER_2, AchievementType.SLAY_SCORPIA, "Kill Scorpia %d Times", 100, 2,
                new GameItem(2996, 2500), new GameItem(4185, 10), new GameItem(6792, 10), new GameItem(6677, 50)),

        VENENATIS("Kill Venenatis %d Times", 33, AchievementTier.TIER_2, AchievementType.SLAY_VENENATIS, "Kill Venenatis %d Times", 100, 2,
                new GameItem(2996, 2500), new GameItem(4185, 10), new GameItem(6792, 10), new GameItem(6677, 50)),

        CHAOS_ELE("Kill Chaos Elemental %d Times", 34, AchievementTier.TIER_2, AchievementType.SLAY_CHAOSELE, "Kill Chaos Elemental %d Times", 100, 2,
                new GameItem(2996, 2500), new GameItem(4185, 10), new GameItem(6792, 10), new GameItem(6677, 50)),

        CHAOS_FANATIC("Kill Chaos Fanatic %d Times", 35, AchievementTier.TIER_2, AchievementType.SLAY_CHAOSFANATIC, "Kill Chaos Fanatic %d Times", 100, 2,
                new GameItem(2996, 2500), new GameItem(4185, 10), new GameItem(6792, 10), new GameItem(6677, 50)),

        CRAZY_ARCH("Kill Crazy Archaeologist %d Times", 36, AchievementTier.TIER_2, AchievementType.SLAY_ARCHAEOLOGIST, "Kill Crazy Archaeologist %d Times", 100, 2,
                new GameItem(2996, 2500), new GameItem(4185, 10), new GameItem(6792, 10), new GameItem(6677, 50)),

        SHADOW_ARA("Kill Shadow of Araphael %d Times", 37, AchievementTier.TIER_2, AchievementType.SLAY_SHADOWARAPHAEL, "Kill Shadow of Araphael %d Times", 100, 2,
                new GameItem(2996, 3000), new GameItem(4185, 10), new GameItem(6792, 10), new GameItem(6678, 50)),

        ARA("Kill Araphael %d Times", 38, AchievementTier.TIER_2, AchievementType.SLAY_ARAPHAEL, "Kill Araphael %d Times", 100, 2,
                new GameItem(2996, 3000), new GameItem(4185, 10), new GameItem(6792, 10), new GameItem(6678, 50)),

        INTERMEDIATE_FISHER("Fishing %d", 4, AchievementTier.TIER_2, AchievementType.FISH, "Catch %d Fish", 2500, 2,
                new GameItem(696, 30), new GameItem(25527, 1500)),

        INTERMEDIATE_CHEF("Cooking %d", 5, AchievementTier.TIER_2, AchievementType.COOK, "Cook %d Fish", 2500, 2,
                new GameItem(696, 30), new GameItem(25527, 1500)),

        INTERMEDIATE_MINER("Mining %d", 6, AchievementTier.TIER_2, AchievementType.MINE, "Mine %d Rocks", 2500, 2,
                new GameItem(696, 30), new GameItem(25527, 1500)),

        INTERMEDIATE_SMITH("Smithing %d", 7, AchievementTier.TIER_2, AchievementType.SMITH, "Smelt or Smith %d Bars", 2500, 2,
                new GameItem(696, 30), new GameItem(25527, 1500)),

        INTERMEDIATE_FARMER("Farming %d", 8, AchievementTier.TIER_2, AchievementType.FARM, "Harvest %d Crops", 2500, 2,
                new GameItem(696, 30), new GameItem(25527, 1500)),

        INTERMEDIATE_MIXER("Herblore %d", 9, AchievementTier.TIER_2, AchievementType.HERB, "Create %d Potions", 1000, 2,
                new GameItem(696, 30), new GameItem(25527, 1500)),

        INTERMEDIATE_CHOPPER("Woodcutting %d", 10, AchievementTier.TIER_2, AchievementType.WOODCUT, "Cut %d Trees", 2500, 2,
                new GameItem(696, 30), new GameItem(25527, 1500)),

        INTERMEDIATE_FLETCHER("Fletching %d", 11, AchievementTier.TIER_2, AchievementType.FLETCH, "Fletch %d Logs", 2500, 2,
                new GameItem(696, 30), new GameItem(25527, 1500)),

        INTERMEDIATE_PYRO("Firemaking %d", 12, AchievementTier.TIER_2, AchievementType.FIRE, "Light %d Logs", 1000, 2,
                new GameItem(696, 30), new GameItem(25527, 1500)),

        INTERMEDIATE_THIEF("Thieving %d", 13, AchievementTier.TIER_2, AchievementType.THIEV, "Steal %d Times", 1000, 2,
                new GameItem(696, 30), new GameItem(25527, 1500)),

        INTERMEDIATE_RUNNER("Agility %d", 14, AchievementTier.TIER_2, AchievementType.AGIL, "Complete %d Agility\\nCourse Laps", 250, 2,
                new GameItem(696, 30), new GameItem(11849, 90), new GameItem(12792, 2)),

        TREASURE_GAMES_INTERMEDIATE("Treasure Games %d", 55, AchievementTier.TIER_2, AchievementType.TREASURE_GAMES, "Completed Treasure Games %d times", 500, 1,
                new GameItem(6805, 5), new GameItem(33361, 50), new GameItem(696, 20), new GameItem(13346, 75)),

        CHAOTIC_DEATH_SPAWN_I("Kill Chaotic Death Spawn %d", 56, AchievementTier.TIER_2, AchievementType.SLAY_CHAOTIC, "Kill Chaotic Death Spawn %d times", 100, 1,
                new GameItem(33358, 15), new GameItem(6805, 2), new GameItem(696, 100), new GameItem(33361, 5)),

        SOL_HEREDIT_1("Kill Sol Heredit %d", 57, AchievementTier.TIER_2, AchievementType.SLAY_SOL_HEREDIT, "KillSol Heredit %d times", 250, 1,
                new GameItem(13346, 100), new GameItem(19887, 25), new GameItem(6805, 4), new GameItem(696, 200)),

        ISLE_OF_THE_DAMNED_1("Isle Of The Damned %d", 58, AchievementTier.TIER_2, AchievementType.ISLE_OF_DAMNED, "Finish Isle Of The Damned %d times", 250, 1,
                new GameItem(33354, 3), new GameItem(19887, 25), new GameItem(6805, 4), new GameItem(696, 200)),

        SHARATHTEERK_1("Sharathteerk %d", 59, AchievementTier.TIER_2, AchievementType.SLAY_SHARATHTEERK, "Kill Sharathteerk %d times", 250, 1,
                new GameItem(33362, 2), new GameItem(19887, 25), new GameItem(6805, 4), new GameItem(696, 200)),

        LUKE_SLAYER_II("Kill 50% Luke %d times", 60, AchievementTier.TIER_2, AchievementType.SLAY_LUKE, "Kill %d 50% Luke", 100, 2,
                new GameItem(33378, 4), new GameItem(33354, 3), new GameItem(6805, 10), new GameItem(696, 80)),

        TUMEKENS_WARDEN_1("Kill Tumekens Warden %d times", 61, AchievementTier.TIER_2, AchievementType.SLAY_TUMEKEN, "Kill %d Tumekens Warden", 250, 1,
                new GameItem(33362, 2), new GameItem(33354, 1), new GameItem(6805, 5), new GameItem(696, 40)),

        ARAXXOR_1("Kill Araxxor %d times", 62, AchievementTier.TIER_2, AchievementType.SLAY_ARAXXOR, "Kill %d Arraxor", 250, 1,
                new GameItem(33362, 4), new GameItem(33354, 4), new GameItem(6805, 20), new GameItem(33428, 100)),

        YAMA_1("Kill Judge Of Yama %d times", 63, AchievementTier.TIER_2, AchievementType.SLAY_YAMA, "Kill %d Judge Of Yama", 250, 1,
                new GameItem(33362, 4), new GameItem(33354, 4), new GameItem(6805, 20), new GameItem(33428, 100)),


        VARDORVIS_1("Kill Vardorvis %d times", 64, AchievementTier.TIER_2, AchievementType.SLAY_VARDORVIS, "Kill %d Vardorvis", 250, 1,
                new GameItem(33362, 4), new GameItem(33354, 4), new GameItem(6805, 20), new GameItem(33428, 100)),

        XAMPHUR_1("Kill Xamphur %d times", 65, AchievementTier.TIER_2, AchievementType.SLAY_XAMPHUR, "Kill %d Xamphur", 250, 1,
                new GameItem(33362, 4), new GameItem(33354, 4), new GameItem(6805, 20), new GameItem(33428, 100)),

        WHISPERER_1("Kill The Whisperer %d times", 66, AchievementTier.TIER_2, AchievementType.SLAY_WHISPERER, "Kill %d The Whisperer", 250, 1,
                new GameItem(33362, 4), new GameItem(33354, 4), new GameItem(6805, 20), new GameItem(33428, 100)),

        MINOTAUR_1("Kill The Minotaur %d times", 67, AchievementTier.TIER_2, AchievementType.SLAY_MINOTAUR, "Kill %d The Minotaur", 250, 1,
                new GameItem(33362, 4), new GameItem(33354, 4), new GameItem(6805, 20), new GameItem(33428, 100)),

        DUKE_1("Kill Duke %d times", 68, AchievementTier.TIER_2, AchievementType.SLAY_DUKE, "Kill %d Duke", 250, 1,
                new GameItem(33362, 4), new GameItem(33354, 4), new GameItem(6805, 20), new GameItem(33428, 100)),
        /**
         * Tier 3 Achievement Start
         */

        EXTREME_VOTER("Democracy %d", 20, AchievementTier.TIER_3, AchievementType.VOTE_CHEST_UNLOCK, "Open %d Vote Chests", 20, 3,
                new GameItem(22093, 10), new GameItem(11739, 20), new GameItem(23933, 15)),

        DIG_FOR_GOLD("Crystal Clear %d", 16, AchievementTier.TIER_3, AchievementType.LOOT_CRYSTAL_CHEST, "Loot Crystal Chest %d Times", 200, 3,
                new GameItem(696, 30), new GameItem(6678, 10), new GameItem(989, 20)),

        CLUE_CHAMP("Treasure Trails %d", 19, AchievementTier.TIER_3, AchievementType.CLUES, "Loot %d Clue Caskets", 250, 3,
                new GameItem(696, 20), new GameItem(10025, 15), new GameItem(6677, 50)),

        UNIQUE_DROP_EXPERT("Obtain %d unique drops", 44, AchievementTier.TIER_3, AchievementType.UNIQUE_DROPS, "Obtain %d unique drops", 500, 1,
                new GameItem(696, 150), new GameItem(6678, 50), new GameItem(20788, 1)),

        UPGRADE_ITEMS_EXPERT("Upgrade %d items", 45, AchievementTier.TIER_3, AchievementType.UPGRADE, "Upgrade %d items",  250, 1,
                new GameItem(696, 100), new GameItem(6677, 50), new GameItem(6678, 50)),

        SLAYER_EXPERT("Slayer %d", 15, AchievementTier.TIER_3, AchievementType.SLAY, "Complete %d Slayer Tasks", 150, 3,
                new GameItem(696, 30), new GameItem(13438, 10), new GameItem(7629, 5)),

        EXPERT_DRAGON_SLAYER("Dragon Hunter %d", 2, AchievementTier.TIER_3, AchievementType.SLAY_DRAGONS, "Kill %d Dragons", 950, 3,
                new GameItem(696, 40), new GameItem(6678, 50), new GameItem(22124, 250)),

        BARROWS_GOD("Barrows %d", 18, AchievementTier.TIER_3, AchievementType.BARROWS_KILLS, "Kill %d npcs at barrows", 750, 3,
                new GameItem(696, 20), new GameItem(6678, 50), new GameItem(6677, 100)),

        BOSS_SLAUGHTERER("Boss Hunter %d", 3, AchievementTier.TIER_3, AchievementType.SLAY_BOSSES, "Kill %d Bosses", 1500, 3,
                new GameItem(696, 40), new GameItem(6678, 100), new GameItem(6677, 100)),

        SLAUGHTERER("Mob Killer %d", 1, AchievementTier.TIER_3, AchievementType.SLAY_ANY_NPCS, "Kill %d NPCs", 10000, 3,
                new GameItem(696, 40), new GameItem(6678, 50), new GameItem(989, 150)),

        TZHAAR("Fight Caves %d", 17, AchievementTier.TIER_3, AchievementType.FIGHT_CAVES_ROUNDS, "Complete Fight Caves %d Times", 50, 3,
                new GameItem(696, 10), new GameItem(6678, 10), new GameItem(6570, 5)),

        INFERNO_NOVICE("Inferno %d", 43, AchievementTier.TIER_3, AchievementType.INFERNO, "Complete Inferno %d Times", 50, 1,
                new GameItem(696, 100), new GameItem(6678, 50), new GameItem(21295, 10)),

        COX_GUARDIAN( 41, AchievementTier.TIER_3, AchievementType.COX, "Complete %d Chamber\\nof Xeric runs (Raids 1)", 500, 4, new GameItem(696, 100),
                new GameItem(6678, 250), new GameItem(12585, 100)),

        TOB_GUARDIAN( 42, AchievementTier.TIER_3, AchievementType.TOB, "Complete %d Theatre\\nof Blood runs (Raids 2)", 500, 4, new GameItem(696, 100),
                new GameItem(6678, 250), new GameItem(19895, 100)),

        ARBOGRAVE_GUARDIAN(46, AchievementTier.TIER_3, AchievementType.ARBO, "Complete %d Arbograve\\nSwamp runs (Raids 3)",  500, 4,
                new GameItem(2400, 50), new GameItem(27285, 2), new GameItem(2403, 1), new GameItem (6680, 50)),

        SHADOW_CRUSADE_GUARDIAN(54, AchievementTier.TIER_3, AchievementType.SHADOW_CRUSADE, "Complete %d Shadow Crusade Raids",  500, 4,
                new GameItem(28416, 50), new GameItem(26886, 2), new GameItem(2403, 1), new GameItem (33360, 50)),

        NEX_MASTER("Kill Nex %d Times", 21, AchievementTier.TIER_3, AchievementType.SLAY_NEX, "Kill Nex %d Times", 1000, 2,
                new GameItem(696, 100), new GameItem(6677, 100), new GameItem(6678, 100)),

        NIGHTMARE_MASTER("Kill Nightmare %d Times", 22, AchievementTier.TIER_3, AchievementType.NIGHTMARE, "Kill Nightmare %d Times", 1000, 2,
                new GameItem(696, 100), new GameItem(6677, 100), new GameItem(6678, 100)),

        VORKATH_MASTER("Kill Vorkath %d Times", 23, AchievementTier.TIER_3, AchievementType.SLAY_VORKATH, "Kill Vorkath %d Times", 1000, 2,
                new GameItem(696, 80), new GameItem(6677, 100), new GameItem(6678, 100)),

        ZULRAH_MASTER("Kill Zulrah %d Times", 24, AchievementTier.TIER_3, AchievementType.SLAY_ZULRAH, "Kill Zulrah %d Times", 1000, 2,
                new GameItem(696, 80), new GameItem(6677, 100), new GameItem(6678, 100)),

        HYDRA_MASTER("Kill Hydra %d Times", 25, AchievementTier.TIER_3, AchievementType.HYDRA, "Kill Hydra %d Times", 1000, 2,
                new GameItem(696, 100), new GameItem(6677, 100), new GameItem(6678, 100)),

        CERB_MASTER("Kill Cerberus %d Times", 26, AchievementTier.TIER_3, AchievementType.SLAY_CERB, "Kill Cerberus %d Times", 1000, 2,
                new GameItem(696, 80), new GameItem(6677, 100), new GameItem(6678, 100)),

        CORP_MASTER("Kill Corporeal Beast %d Times", 27, AchievementTier.TIER_3, AchievementType.SLAY_CORP, "Kill Corporeal Beast %d Times", 1000, 2,
                new GameItem(696, 100), new GameItem(6677, 100), new GameItem(6678, 100)),

        KBD_MASTER("Kill KBD %d Times", 28, AchievementTier.TIER_3, AchievementType.SLAY_KBD, "Kill KBD %d Times", 1000, 2,
                new GameItem(696, 80), new GameItem(6677, 100), new GameItem(6678, 100)),

        SIRE_MASTER("Kill SIRE %d Times", 29, AchievementTier.TIER_3, AchievementType.SLAY_SIRE, "Kill Abyssal Sire %d Times", 1000, 2,
                new GameItem(696, 80), new GameItem(6677, 100), new GameItem(6678, 100)),

        KRAKEN_MASTER("Kill Kraken %d Times", 30, AchievementTier.TIER_3, AchievementType.SLAY_KRAKEN, "Kill Kraken %d Times", 1000, 2,
                new GameItem(696, 60), new GameItem(6677, 100), new GameItem(6678, 100)),

        MANTICORE_2("Kill Manticore %d times", 40, AchievementTier.TIER_3, AchievementType.SLAY_MANTICORE, "Kill %d Manticore", 1000, 2,
                new GameItem(13346, 100), new GameItem(19887, 50), new GameItem(696, 160)),

        JAVELIN_COLOSSUS_2("Kill Javelin Colossus %d times", 47, AchievementTier.TIER_3, AchievementType.SLAY_JAVELIN_COLOSSUS, "Kill %d Javelin Colossus", 1000, 2,
                new GameItem(13346, 100), new GameItem(19887, 50), new GameItem(696, 160)),

        BABA_2("Kill BaBa's Phantom %d times", 48, AchievementTier.TIER_3, AchievementType.SLAY_BABA, "Kill %d BaBa's Phantom", 1000, 2,
                new GameItem(13346, 100), new GameItem(19887, 50), new GameItem(696, 160)),

        GROOT_2("Kill Groot %d times", 49, AchievementTier.TIER_3, AchievementType.SLAY_GROOT, "Kill %d Groot", 500, 2,
                new GameItem(33378, 8), new GameItem(33354, 6), new GameItem(6805, 25), new GameItem(696, 160)),

        VBOSS_2("Kill Vote Boss %d times", 50, AchievementTier.TIER_3, AchievementType.SLAY_VBOSS, "Kill %d Vote Boss", 500, 2,
                new GameItem(33378, 8), new GameItem(33354, 6), new GameItem(6805, 25), new GameItem(696, 160)),

        DBOSS_2("Kill Dono Boss %d times", 51, AchievementTier.TIER_3, AchievementType.SLAY_DBOSS, "Kill %d Dono Boss", 500, 2,
                new GameItem(33378, 8), new GameItem(33354, 6), new GameItem(6805, 25), new GameItem(696, 160)),

        DURIAL_2("Kill Durial %d times", 52, AchievementTier.TIER_3, AchievementType.SLAY_DURIAL, "Kill %d Durial", 500, 2,
                new GameItem(33378, 8), new GameItem(33354, 6), new GameItem(6805, 25), new GameItem(696, 160)),

        AFK_2("Kill AFK Goblin %d times", 53, AchievementTier.TIER_3, AchievementType.SLAY_AFK, "Kill %d AFK Goblin", 500, 2,
                new GameItem(33378, 8), new GameItem(33354, 6), new GameItem(6805, 25), new GameItem(696, 160)),

        VETION_MASTER("Kill Vet'ion %d Times", 31, AchievementTier.TIER_3, AchievementType.SLAY_VETION, "Kill Vet'ion %d Times", 250, 2,
                new GameItem(13302, 3),  new GameItem(4185, 20), new GameItem(6792, 20), new GameItem(6678, 100)),

        CALLISTO_MASTER("Kill Callisto %d Times", 32, AchievementTier.TIER_3, AchievementType.SLAY_CALLISTO, "Kill Callisto %d Times", 250, 2,
                new GameItem(13302, 3),  new GameItem(4185, 20), new GameItem(6792, 20), new GameItem(6678, 100)),

        SCORPIA_MASTER("Kill Scorpia %d Times", 33, AchievementTier.TIER_3, AchievementType.SLAY_SCORPIA, "Kill Scorpia %d Times", 250, 2,
                new GameItem(13302, 3),  new GameItem(4185, 20), new GameItem(6792, 20), new GameItem(6678, 100)),

        VENENATIS_MASTER("Kill Venenatis %d Times", 34, AchievementTier.TIER_3, AchievementType.SLAY_VENENATIS, "Kill Venenatis %d Times", 250, 2,
                new GameItem(13302, 3),  new GameItem(4185, 20), new GameItem(6792, 20), new GameItem(6678, 100)),

        CHAOS_ELE_MASTER("Kill Chaos Elemental %d Times", 35, AchievementTier.TIER_3, AchievementType.SLAY_CHAOSELE, "Kill Chaos Elemental %d Times", 250, 2,
                new GameItem(13302, 3),  new GameItem(4185, 20), new GameItem(6792, 20), new GameItem(6678, 100)),

        CHAOS_FANATIC_MASTER("Kill Chaos Fanatic %d Times", 36, AchievementTier.TIER_3, AchievementType.SLAY_CHAOSFANATIC, "Kill Chaos Fanatic %d Times", 250, 2,
                new GameItem(13302, 3),  new GameItem(4185, 20), new GameItem(6792, 20), new GameItem(6678, 100)),

        CRAZY_ARCH_MASTER("Kill Crazy Archaeologist %d Times", 37, AchievementTier.TIER_3, AchievementType.SLAY_ARCHAEOLOGIST, "Kill Crazy Archaeologist %d Times", 250, 2,
                new GameItem(13302, 3),  new GameItem(4185, 20), new GameItem(6792, 20), new GameItem(6678, 100)),

        SHADOW_ARA_MASTER("Kill Shadow of Araphael %d Times", 38, AchievementTier.TIER_3, AchievementType.SLAY_SHADOWARAPHAEL, "Kill Shadow of Araphael %d Times", 250, 2,
                new GameItem(13302, 3),  new GameItem(4185, 20), new GameItem(6792, 20), new GameItem(6678, 100)),

        ARA_MASTER("Kill Araphael %d Times", 39, AchievementTier.TIER_3, AchievementType.SLAY_ARAPHAEL, "Kill Araphael %d Times", 250, 2,
                new GameItem(13302, 3),  new GameItem(4185, 20), new GameItem(6792, 20), new GameItem(6678, 100)),

        EXPERT_FISHER("Fishing %d", 4, AchievementTier.TIER_3, AchievementType.FISH, "Catch %d Fish", 5000, 3,
                new GameItem(696, 30), new GameItem(25527, 2750)),

        EXPERT_CHEF("Cooking %d", 5, AchievementTier.TIER_3, AchievementType.COOK, "Cook %d Fish", 5000, 3,
                new GameItem(696, 30), new GameItem(25527, 2750)),

        EXPERT_MINER("Mining %d", 6, AchievementTier.TIER_3, AchievementType.MINE, "Mine %d Rocks", 5000, 3,
                new GameItem(696, 30), new GameItem(25527, 2750)),

        EXPERT_SMITH("Smithing %d", 7, AchievementTier.TIER_3, AchievementType.SMITH, "Smelt or Smith %d Bars", 5000, 3,
                new GameItem(696, 30), new GameItem(25527, 2750)),

        EXPERT_FARMER("Farming %d", 8, AchievementTier.TIER_3, AchievementType.FARM, "Harvest %d Crops", 5000, 3,
                new GameItem(696, 30), new GameItem(25527, 2750)),

        EXPERT_MIXER("Herblore %d", 9, AchievementTier.TIER_3, AchievementType.HERB, "Create %d Potions", 5000, 3,
                new GameItem(696, 30), new GameItem(25527, 2750)),

        EXPERT_CHOPPER("Woodcutting %d", 10, AchievementTier.TIER_3, AchievementType.WOODCUT, "Cut %d Trees", 5000, 3,
                new GameItem(696, 30), new GameItem(25527, 2750)),

        EXPERT_FLETCHER("Fletching %d", 11, AchievementTier.TIER_3, AchievementType.FLETCH, "Fletch %d Logs", 5000, 3,
                new GameItem(696, 30), new GameItem(25527, 2750)),

        EXPERT_PYRO("Firemaking %d", 12, AchievementTier.TIER_3, AchievementType.FIRE, "Light %d Logs", 5000, 3,
                new GameItem(696, 30), new GameItem(25527, 2750)),

        EXPERT_THIEF("Thieving %d", 13, AchievementTier.TIER_3, AchievementType.THIEV, "Steal %d Times", 5000, 3,
                new GameItem(696, 30), new GameItem(25527, 2750)),

        EXPERT_RUNNER("Agility %d", 14, AchievementTier.TIER_3, AchievementType.ROOFTOP, "Complete %d Rooftop Agility\\nCourse Laps", 300, 3,
                new GameItem(696, 30), new GameItem(11849, 90), new GameItem(12792, 2)),

        TREASURE_GAMES_EXPTER("Treasure Games %d", 55, AchievementTier.TIER_3, AchievementType.TREASURE_GAMES, "Completed Treasure Games %d times", 1250, 1,
                new GameItem(6805, 10), new GameItem(33361, 100), new GameItem(696, 40), new GameItem(13346, 125)),

        CHAOTIC_DEATH_SPAWN_II("Kill Chaotic Death Spawn %d", 56, AchievementTier.TIER_3, AchievementType.SLAY_CHAOTIC, "Kill Chaotic Death Spawn %d times", 500, 1,
                new GameItem(33358, 30), new GameItem(6805, 4), new GameItem(696, 200), new GameItem(33361, 10)),

        SOL_HEREDIT_2("Kill Sol Heredit %d", 57, AchievementTier.TIER_3, AchievementType.SLAY_SOL_HEREDIT, "Kill Sol Heredit %d times", 500, 1,
                new GameItem(13346, 100), new GameItem(19887, 50), new GameItem(6805, 8), new GameItem(696, 300)),

        ISLE_OF_THE_DAMNED_2("Isle Of The Damned %d", 58, AchievementTier.TIER_3, AchievementType.ISLE_OF_DAMNED, "Finish Isle Of The Damned %d times", 500, 1,
                new GameItem(33354, 5), new GameItem(19887, 50), new GameItem(6805, 8), new GameItem(696, 300)),

        SHARATHTEERK_2("Sharathteerk %d", 59, AchievementTier.TIER_3, AchievementType.SLAY_SHARATHTEERK, "Kill Sharathteerk %d times", 500, 1,
                new GameItem(33362, 3), new GameItem(19887, 50), new GameItem(6805, 8), new GameItem(696, 300)),

        LUKE_SLAYER_III("Kill 50% Luke %d times", 60, AchievementTier.TIER_3, AchievementType.SLAY_LUKE, "Kill %d 50% Luke", 500, 2,
                new GameItem(33378, 8), new GameItem(33354, 6), new GameItem(6805, 25), new GameItem(696, 160)),

        TUMEKENS_WARDEN_2("Kill Tumekens Warden %d times", 61, AchievementTier.TIER_3, AchievementType.SLAY_TUMEKEN, "Kill %d Tumekens Warden", 500, 1,
                new GameItem(33362, 2), new GameItem(33354, 1), new GameItem(6805, 5), new GameItem(696, 40)),

        ARAXXOR_3("Kill Araxxor %d times", 62, AchievementTier.TIER_3, AchievementType.SLAY_ARAXXOR, "Kill %d Arraxor", 500, 1,
                new GameItem(33362, 8), new GameItem(33354, 8), new GameItem(6805, 40), new GameItem(33428, 200)),

        YAMA_2("Kill Judge Of Yama %d times", 63, AchievementTier.TIER_3, AchievementType.SLAY_YAMA, "Kill %d Judge Of Yama", 500, 1,
                new GameItem(33362, 8), new GameItem(33354, 8), new GameItem(6805, 40), new GameItem(33428, 200)),

        VARDORVIS_2("Kill Vardorvis %d times", 64, AchievementTier.TIER_3, AchievementType.SLAY_VARDORVIS, "Kill %d Vardorvis", 500, 1,
                new GameItem(33362, 8), new GameItem(33354, 8), new GameItem(6805, 40), new GameItem(33428, 200)),

        XAMPHUR_2("Kill Xamphur %d times", 65, AchievementTier.TIER_3, AchievementType.SLAY_XAMPHUR, "Kill %d Xamphur", 500, 1,
                new GameItem(33362, 8), new GameItem(33354, 8), new GameItem(6805, 40), new GameItem(33428, 200)),

        WHISPERER_2("Kill The Whisperer %d times", 66, AchievementTier.TIER_3, AchievementType.SLAY_WHISPERER, "Kill %d The Whisperer", 500, 1,
                new GameItem(33362, 8), new GameItem(33354, 8), new GameItem(6805, 40), new GameItem(33428, 200)),

        MINOTAUR_2("Kill The Minotaur %d times", 67, AchievementTier.TIER_3, AchievementType.SLAY_MINOTAUR, "Kill %d The Minotaur", 500, 1,
                new GameItem(33362, 8), new GameItem(33354, 8), new GameItem(6805, 40), new GameItem(33428, 200)),

        DUKE_2("Kill Duke %d times", 68, AchievementTier.TIER_3, AchievementType.SLAY_DUKE, "Kill %d Duke", 500, 1,
                new GameItem(33362, 8), new GameItem(33354, 8), new GameItem(6805, 40), new GameItem(33428, 200)),

        /**
         * Tier 4 Achievement Start
         */

        MAX( 10, AchievementTier.TIER_4, AchievementType.MAX, "Achieve level 99 in\\nall skills", 1, 4, new GameItem(696, 100),
                new GameItem(2528, 100), new GameItem(6678, 50)),

        UNIQUE_DROP_MASTER("Obtain %d unique drops", 31, AchievementTier.TIER_4, AchievementType.UNIQUE_DROPS, "Obtain %d unique drops", 1000, 1,
                new GameItem(696, 200), new GameItem(6678, 250), new GameItem(10557, 1)),

        UPGRADE_ITEMS("Upgrade %d items", 32, AchievementTier.TIER_4, AchievementType.UPGRADE, "Upgrade %d items",  500, 1,
                new GameItem(696, 200), new GameItem(6678, 500), new GameItem(6805, 3)),

        FIRE_OF_EXCHANGE(  7, AchievementTier.TIER_4, AchievementType.FOE_POINTS, "Dissolve 1 Billion Exchange\\nPoints", 1_000_000_000, 4,
                new GameItem(696, 400), new GameItem(6678, 250), new GameItem(6677, 250), new GameItem(6679, 1000)),

        MIMIC( 1, AchievementTier.TIER_4, AchievementType.MIMIC, "Kill %d Mimics", 20, 4,
                new GameItem(2714, 25), new GameItem(2802, 20), new GameItem(2775, 15), new GameItem(19841, 10)),

        HUNLLEF( 2, AchievementTier.TIER_4, AchievementType.HUNLLEF, "Kill %d Hunllefs", 25, 4,
                new GameItem(23776, 20), new GameItem(25894, 1), new GameItem(23995, 1)),

        WILDY_EVENT( 8, AchievementTier.TIER_4, AchievementType.WILDY_EVENT, "Finish %d\\nWildy Events", 50, 4,
                new GameItem(33175, 1)),

        MAGE_ARENA_II( 9, AchievementTier.TIER_4, AchievementType.MAGE_ARENA_II, "Complete %d\\nMage Arena II", 1, 4,
                new GameItem(2996,2500), new GameItem(21795, 1), new GameItem(21791, 1), new GameItem(21793, 1)),

        Jad_Task_EXPERT("Fight Caves %d", 3, AchievementTier.TIER_4, AchievementType.FIGHT_CAVES_ROUNDS, "Complete the Fight Caves", 250, 1,
                new GameItem(696, 75), new GameItem(6678, 50), new GameItem(6570, 20), new GameItem(10558, 1)),

        INFERNO_EXPERT("Inferno %d", 4, AchievementTier.TIER_4, AchievementType.INFERNO, "Complete Inferno %d Times", 250, 1,
                new GameItem(696, 75), new GameItem(6678, 50), new GameItem(21295, 20), new GameItem(10556, 1)),

        COX_CHAMPION( 5, AchievementTier.TIER_4, AchievementType.COX, "Complete %d Chamber\\nof Xeric runs (Raids 1)", 1000, 4,
                new GameItem(696, 200), new GameItem (6678, 500), new GameItem(2403, 1), new GameItem(12585, 250)),

        TOB_CHAMPION( 6, AchievementTier.TIER_4, AchievementType.TOB, "Complete %d Theatre\\nof Blood runs (Raids 2)", 1000, 4,
                new GameItem(696, 200), new GameItem (6678, 500), new GameItem(2403, 1), new GameItem(19895, 250)),

        ARBOGRAVE_KING(33, AchievementTier.TIER_4, AchievementType.ARBO, "Complete %d Arbograve\\nSwamp runs (Raids 3)",  1000, 4,
                new GameItem(786, 1), new GameItem (696, 1000), new GameItem(6678, 500), new GameItem(6680, 250)),

        SHADOW_CRUSADE_KING(41, AchievementTier.TIER_4, AchievementType.SHADOW_CRUSADE, "Complete %d Shadow Crusade Raids",  1000, 4,
                new GameItem(786, 1), new GameItem (696, 1000), new GameItem(6678, 500), new GameItem(33360, 250)),

        NEX_GOD1("Kill Nex %d Times", 21, AchievementTier.TIER_4, AchievementType.SLAY_NEX, "Kill Nex %d Times", 2500, 2,
                new GameItem(696, 200), new GameItem(6677, 500), new GameItem(6678, 500), new GameItem(6805, 2)),

        NIGHTMARE_GOD1("Kill Nightmare %d Times", 22, AchievementTier.TIER_4, AchievementType.NIGHTMARE, "Kill Nightmare %d Times", 1500, 2,
                new GameItem(696, 200), new GameItem(6677, 500), new GameItem(6678, 500), new GameItem(6805, 2)),

        VORKATH_GOD1("Kill Vorkath %d Times", 23, AchievementTier.TIER_4, AchievementType.SLAY_VORKATH, "Kill Vorkath %d Times", 2500, 2,
                new GameItem(696, 160), new GameItem(6677, 500), new GameItem(6678, 500), new GameItem(6805, 1)),

        ZULRAH_GOD1("Kill Zulrah %d Times", 24, AchievementTier.TIER_4, AchievementType.SLAY_ZULRAH, "Kill Zulrah %d Times", 2500, 2,
                new GameItem(696, 160), new GameItem(6677, 500), new GameItem(6678, 500), new GameItem(6805, 1)),

        HYDRA_GOD1("Kill Hydra %d Times", 25, AchievementTier.TIER_4, AchievementType.HYDRA, "Kill Hydra %d Times", 1500, 2,
                new GameItem(696, 200), new GameItem(6677, 500), new GameItem(6678, 500), new GameItem(6805, 2)),

        CERB_GOD1("Kill Cerberus %d Times", 26, AchievementTier.TIER_4, AchievementType.SLAY_CERB, "Kill Cerberus %d Times", 2500, 2,
                new GameItem(696, 160), new GameItem(6677, 500), new GameItem(6678, 500), new GameItem(6805, 1)),

        CORP_GOD1("Kill Corporeal Beast %d Times", 27, AchievementTier.TIER_4, AchievementType.SLAY_CORP, "Kill Corporeal Beast %d Times", 1500, 2,
                new GameItem(696, 200), new GameItem(6677, 500), new GameItem(6678, 500), new GameItem(6805, 2)),

        KBD_GOD1("Kill KBD %d Times", 28, AchievementTier.TIER_4, AchievementType.SLAY_KBD, "Kill KBD %d Times", 2500, 2,
                new GameItem(696, 160), new GameItem(6677, 500), new GameItem(6678, 500), new GameItem(6805, 1)),

        SIRE_GOD1("Kill SIRE %d Times", 29, AchievementTier.TIER_4, AchievementType.SLAY_SIRE, "Kill Abyssal Sire %d Times", 2500, 2,
                new GameItem(696, 160), new GameItem(6677, 500), new GameItem(6678, 500), new GameItem(6805, 1)),

        KRAKEN_GOD1("Kill Kraken %d Times", 30, AchievementTier.TIER_4, AchievementType.SLAY_KRAKEN, "Kill Kraken %d Times", 2500, 2,
                new GameItem(696, 120), new GameItem(6677, 500), new GameItem(6678, 500), new GameItem(6805, 1)),

        MANTICORE_3("Kill Manticore %d times", 34, AchievementTier.TIER_4, AchievementType.SLAY_MANTICORE, "Kill %d Manticore", 2500, 2,
                new GameItem(13346, 250), new GameItem(19887, 250), new GameItem(696, 400)),

        JAVELIN_COLOSSUS_3("Kill Javelin Colossus %d times", 35, AchievementTier.TIER_4, AchievementType.SLAY_JAVELIN_COLOSSUS, "Kill %d Javelin Colossus", 2500, 2,
                new GameItem(13346, 250), new GameItem(19887, 250), new GameItem(696, 400)),

        BABA_3("Kill BaBa's Phantom %d times", 36, AchievementTier.TIER_4, AchievementType.SLAY_BABA, "Kill %d BaBa's Phantom", 2500, 2,
                new GameItem(13346, 250), new GameItem(19887, 250), new GameItem(696, 400)),

        GROOT_3("Kill Groot %d times", 37, AchievementTier.TIER_4, AchievementType.SLAY_GROOT, "Kill %d Groot", 1000, 2,
                new GameItem(33378, 15), new GameItem(33354, 10), new GameItem(6805, 100), new GameItem(696, 320)),

        VBOSS_3("Kill Vote Boss %d times", 38, AchievementTier.TIER_4, AchievementType.SLAY_VBOSS, "Kill %d Vote Boss", 1000, 2,
                new GameItem(33378, 15), new GameItem(33354, 10), new GameItem(6805, 100), new GameItem(696, 320)),

        DBOSS_3("Kill Dono Boss %d times", 39, AchievementTier.TIER_4, AchievementType.SLAY_DBOSS, "Kill %d Dono Boss", 1000, 2,
                new GameItem(33378, 15), new GameItem(33354, 10), new GameItem(6805, 100), new GameItem(696, 320)),

        AFK_3("Kill AFK Goblin %d times", 40, AchievementTier.TIER_4, AchievementType.SLAY_AFK, "Kill %d AFK Goblin", 1000, 2,
                new GameItem(33378, 15), new GameItem(33354, 10), new GameItem(6805, 100), new GameItem(696, 320)),

        MASTER_FISHER1("Fishing %d", 11, AchievementTier.TIER_4, AchievementType.FISH, "Catch %d Fish", 25000, 1,
                new GameItem(696, 100), new GameItem(25527, 6500), new GameItem(6678, 50), new GameItem(6807, 1)),

        MASTER_CHEF1("Cooking %d", 12, AchievementTier.TIER_4, AchievementType.COOK, "Cook %d Fish", 25000, 1,
                new GameItem(696, 100), new GameItem(25527, 6500), new GameItem(6678, 50), new GameItem(6807, 1)),

        MASTER_MINER1("Mining %d", 13, AchievementTier.TIER_4, AchievementType.MINE, "Mine %d Rocks", 25000, 1,
                new GameItem(696, 100), new GameItem(25527, 6500), new GameItem(6678, 50), new GameItem(6807, 1)),

        MASTER_SMITH1("Smithing %d", 14, AchievementTier.TIER_4, AchievementType.SMITH, "Smelt or Smith %d Bars", 25000, 1,
                new GameItem(696, 100), new GameItem(25527, 6500), new GameItem(6678, 50), new GameItem(6807, 1)),

        MASTER_FARMER1("Farming %d", 15, AchievementTier.TIER_4, AchievementType.FARM, "Harvest %d Crops", 25000, 1,
                new GameItem(696, 100), new GameItem(25527, 6500), new GameItem(6678, 50), new GameItem(6807, 1)),

        MASTER_MIXER1("Herblore %d", 16, AchievementTier.TIER_4, AchievementType.HERB, "Create %d Potions", 25000, 1,
                new GameItem(696, 100), new GameItem(25527, 6500), new GameItem(6678, 50), new GameItem(6807, 1)),

        MASTER_CHOPPER1("Woodcutting %d", 17, AchievementTier.TIER_4, AchievementType.WOODCUT, "Cut %d Trees", 25000, 1,
                new GameItem(696, 100), new GameItem(25527, 6500), new GameItem(6678, 50), new GameItem(6807, 1)),

        MASTER_FLETCHER1("Fletching %d", 18, AchievementTier.TIER_4, AchievementType.FLETCH, "Fletch %d Logs", 25000, 1,
                new GameItem(696, 100), new GameItem(25527, 6500), new GameItem(6678, 50), new GameItem(6807, 1)),

        MASTER_PYRO1("Firemaking %d", 19, AchievementTier.TIER_4, AchievementType.FIRE, "Light %d Logs", 25000, 1,
                new GameItem(696, 100), new GameItem(25527, 6500), new GameItem(6678, 50), new GameItem(6807, 1)),

        MASTER_THIEF1("Thieving %d", 20, AchievementTier.TIER_4, AchievementType.THIEV, "Steal %d Times", 25000, 1,
                new GameItem(696, 100), new GameItem(25527, 6500), new GameItem(6678, 50), new GameItem(6807, 1)),

        TREASURE_GAMES_MATER("Treasure Games %d", 42, AchievementTier.TIER_4, AchievementType.TREASURE_GAMES, "Completed Treasure Games %d times", 2500, 1,
                new GameItem(6805, 20), new GameItem(33361, 200), new GameItem(696, 80), new GameItem(13346, 250)),

        CHAOTIC_DEATH_SPAWN_III("Kill Chaotic Death Spawn %d", 43, AchievementTier.TIER_4, AchievementType.SLAY_CHAOTIC, "Kill Chaotic Death Spawn %d times", 1000, 1,
                new GameItem(33358, 50), new GameItem(6805, 8), new GameItem(696, 400), new GameItem(33361, 25)),

        SOL_HEREDIT_3("Kill Sol Heredit %d", 44, AchievementTier.TIER_4, AchievementType.SLAY_SOL_HEREDIT, "Kill Sol Heredit %d times", 1000, 1,
                new GameItem(13346, 200), new GameItem(19887, 100), new GameItem(6805, 15), new GameItem(696, 400)),

        ISLE_OF_THE_DAMNED_3("Isle Of The Damned %d", 45, AchievementTier.TIER_4, AchievementType.ISLE_OF_DAMNED, "Finish Isle Of The Damned %d times", 1000, 1,
                new GameItem(33354, 10), new GameItem(19887, 100), new GameItem(6805, 15), new GameItem(696, 400)),

        SHARATHTEERK_3("Sharathteerk %d", 46, AchievementTier.TIER_4, AchievementType.SLAY_SHARATHTEERK, "Kill Sharathteerk %d times", 1000, 1,
                new GameItem(33362, 4), new GameItem(19887, 100), new GameItem(6805, 15), new GameItem(696, 400)),

        LUKE_SLAYER_IV("Kill 50% Luke %d times", 47, AchievementTier.TIER_4, AchievementType.SLAY_LUKE, "Kill %d 50% Luke", 1000, 2,
                new GameItem(33378, 15), new GameItem(33354, 10), new GameItem(6805, 100), new GameItem(696, 320)),

        TUMEKENS_WARDEN_3("Kill Tumekens Warden %d times", 48, AchievementTier.TIER_4, AchievementType.SLAY_TUMEKEN, "Kill %d Tumekens Warden", 1000, 1,
                new GameItem(33362, 2), new GameItem(33354, 1), new GameItem(6805, 5), new GameItem(696, 40)),

        ARAXXOR_4("Kill Araxxor %d times", 49, AchievementTier.TIER_4, AchievementType.SLAY_ARAXXOR, "Kill %d Arraxor", 1000, 1,
                new GameItem(33362, 16), new GameItem(33354, 16), new GameItem(6805, 100), new GameItem(33428, 350)),

        YAMA_3("Kill Judge Of Yama %d times", 50, AchievementTier.TIER_4, AchievementType.SLAY_YAMA, "Kill %d Judge Of Yama", 1000, 1,
                new GameItem(33362, 16), new GameItem(33354, 16), new GameItem(6805, 100), new GameItem(33428, 350)),

        VARDORVIS_4("Kill Vardorvis %d times", 51, AchievementTier.TIER_4, AchievementType.SLAY_VARDORVIS, "Kill %d Vardorvis", 1000, 1,
                new GameItem(33362, 16), new GameItem(33354, 16), new GameItem(6805, 100), new GameItem(33428, 350)),

        VARDORVIS_PERFECT_KILL("Achieve a perfect kill at\\n Vardorvis %d times", 52, AchievementTier.TIER_4, AchievementType.PERFECT_KILL, "Achieve a perfect kill at\\n Vardorvis %d times", 1, 1,
                new GameItem(33364, 10), new GameItem(33362, 25), new GameItem(33354, 50), new GameItem(33428, 500)),

        XAMPHUR_3("Kill Xamphur %d times", 53, AchievementTier.TIER_4, AchievementType.SLAY_XAMPHUR, "Kill %d Xamphur", 1000, 1,
                new GameItem(33362, 16), new GameItem(33354, 16), new GameItem(6805, 100), new GameItem(33428, 350)),

        WHISPERER_3("Kill The Whisperer %d times", 54, AchievementTier.TIER_4, AchievementType.SLAY_WHISPERER, "Kill %d The Whisperer", 1000, 1,
                new GameItem(33362, 16), new GameItem(33354, 16), new GameItem(6805, 100), new GameItem(33428, 350)),

        MINOTAUR_3("Kill The Minotaur %d times", 56, AchievementTier.TIER_4, AchievementType.SLAY_MINOTAUR, "Kill %d The Minotaur", 1000, 1,
                new GameItem(33362, 16), new GameItem(33354, 16), new GameItem(6805, 100), new GameItem(33428, 350)),

        DUKE_3("Kill Duke %d times", 57, AchievementTier.TIER_4, AchievementType.SLAY_DUKE, "Kill %d Duke", 1000, 1,
                new GameItem(33362, 16), new GameItem(33354, 16), new GameItem(6805, 100), new GameItem(33428, 350)),
        ;


        private String formattedName;
        private final AchievementTier tier;
        private final AchievementType type;
        private final String description;
        private final int amount;
        private final int identification;
        private final int points;
        private final GameItem[] rewards;

        Achievement(int identification, AchievementTier tier, AchievementType type,
                    String description, int amount, int points, GameItem... rewards) {
            this(null, identification, tier, type, description, amount, points, rewards);
        }

        Achievement(String formattedName, int identification, AchievementTier tier, AchievementType type,
                    String description, int amount, int points, GameItem... rewards) {
            this.formattedName = formattedName == null ? null : formattedName.replace("%d", tier.getTierText());
            this.identification = identification;
            this.tier = tier;
            this.type = type;
            this.description = description.replace("%d", Misc.insertCommas(amount));
            this.amount = amount;
            this.points = points;
            this.rewards = rewards;

            //format the items
            for (GameItem b : rewards) if (b.getAmount() == 0) b.setAmount(1);
        }

        @Override
        public String toString() {
            return "Achievement{" +
                    "formattedName='" + formattedName + '\'' +
                    ", tier=" + tier +
                    ", type=" + type +
                    ", description='" + description + '\'' +
                    ", amount=" + amount +
                    ", identification=" + identification +
                    ", points=" + points +
                    ", rewards=" + Arrays.toString(rewards) +
                    '}';
        }

        static {
            for (Achievement a : Achievement.values()) {
                for (Achievement b : Achievement.values()) {
                    if (a != b && a.getId() == b.getId() && a.getTier() == b.getTier()) {
                        throw new IllegalStateException(String.format("Achievements: %s and %s share the same id.", a.name(), b.name()));
                    }
                }
            }
        }

        public String getFormattedName() {
            if (formattedName == null) {
                formattedName = WordUtils.capitalize(name().toLowerCase().replace("_", " "))
                        .replace("Ii", "II")
                        .replace("Iii", "III")
                        .replace("Iv", "IV");
            }

            return formattedName;
        }

        public int getId() {
            return identification;
        }

        public AchievementTier getTier() {
            return tier;
        }

        public AchievementType getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public int getAmount() {
            return amount;
        }

        public int getPoints() {
            return points;
        }

        public GameItem[] getRewards() {
            return rewards;
        }

        public static final Set<Achievement> ACHIEVEMENTS = EnumSet.allOf(Achievement.class);
    }

    public static void increase(Player player, AchievementType type, int amount) {
        if (Discord.jda != null) {
            Guild guild = Discord.jda.getGuildById(1001818107343556648L);

            if (guild != null) {
                for (Member booster : guild.getBoosters()) {
                    if (player.getDiscordUser() == booster.getUser().getIdLong()) {
                        if (Misc.isLucky(10)) {
                            amount += 1;
                        }
                        break;
                    }
                }
            }
        }

        if (Halloween.DoubleAchieve) {
            amount += 1;
        }

        if (Hespori.ACHIEVE_TIMER > 0) {
            amount += 1;
        }  else if (Hespori.ENHANCED_ACHIEVE_TIMER > 0) {
            amount += 1;
        }

        if (player.usingRage) {
            amount += 1;
        }

        if (player.eggNogTimer > System.currentTimeMillis()) {
            amount += 1;
        }

        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getType() == type) {


                int currentAmount = player.getAchievements().getAmountRemaining(achievement.getTier().getId(), achievement.getId());
                int tier = achievement.getTier().getId();

                if (currentAmount < achievement.getAmount() && !player.getAchievements().isComplete(achievement.getTier().getId(), achievement.getId())) {
                    player.getAchievements().setAmountRemaining(tier, achievement.getId(), currentAmount + amount);
                    if ((currentAmount + amount) >= achievement.getAmount()) {
                        player.getAchievements().setAmountRemaining(tier, achievement.getId(), achievement.getAmount()); // Set to max amount in case they went over
                        player.getAchievements().setComplete(tier, achievement.getId(), true);
                        player.getAchievements().setPoints(achievement.getPoints() + player.getAchievements().getPoints());
                        player.sendMessage(Misc.colorWrap(AchievementHandler.COLOR, "<clan=6>You've completed the " + achievement.getTier().getName().toLowerCase()
                                + " achievement '" + achievement.getFormattedName() + "'!"));

                        if (player.getAchievements().hasCompletedAll()) {
                            PlayerHandler.executeGlobalStaffMessage(Misc.colorWrap(AchievementHandler.COLOR,
                                    "<clan=6> " + player.getDisplayNameFormatted() + " has completed all achievements!"));
                        }
                    }

                    updateProgress(player, type);
                }
            }
        }
    }

    private static void updateProgress(Player player, AchievementType type) {
        for (Achievement achievement : Achievement.values()) {
            if (achievement.getType() == type) {
                TasksInterface.updateProgress(player, "achievements", achievement);
            }
        }
    }

    public static void addReward(Player player, Achievement achievement) {
        for (GameItem item : achievement.getRewards()) {
            player.getInventory().addAnywhere(new ImmutableItem(item.getId(), item.getAmount()));
        }
    }

    public static void reset(Player player, AchievementType type) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getType() == type) {
                if (!player.getAchievements().isComplete(achievement.getTier().getId(), achievement.getId())) {
                    player.getAchievements().setAmountRemaining(achievement.getTier().getId(), achievement.getId(),
                            0);
                }
            }
        }
    }

    public static int getMaximumAchievements() {
        return Achievement.ACHIEVEMENTS.size();
    }
}
