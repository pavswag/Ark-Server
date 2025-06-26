package io.kyros.content.minigames.xeric;

import io.kyros.util.Misc;
/**
 * 
 * @author Patrity
 * 
 */
public class XericWave {
 //spawn locations for monsters
	public static int[][] SPAWN_DATA = { { 3060, 4381 }, { 3051, 4393 },
		 { 3028, 4369 }, { 3051, 4369 }, { 3026, 4393 } };
//Npc ids
	public static final int RUNT = 7547, BEAST = 7548, RANGER = 7559, MAGE = 7560, SHAMAN = 7573, LIZARD = 7597,
			AIR_CRAB = 7576, FIRE_CRAB = 7577, EARTH_CRAB = 7578, WATER_CRAB = 7579, ICE_FIEND = 7586, VESPINE = 7538,
			VANGUARD = 7529,

			// BOSSES
			VESPULA = 7531, TEKTON = 7543, MUTTADILE = 7563, VASA = 7566,
					ICE_DEMON = 7585, GENERAL = 2215, KRIL = 3129,
			DAGPRIME = 6497, DAGREX = 6498, DAGSUP = 6496, KBD = 6502,
					JAD = 6506, CE = 6505, VENNY = 6504, KREE = 6492,
			COMZIL = 6493;

	public static final int[] wave = {VESPULA, TEKTON, MUTTADILE, VASA, ICE_DEMON, GENERAL, KRIL, DAGPRIME,
			DAGREX, DAGSUP, KBD, JAD, CE, VENNY, KREE, COMZIL};

		//get the npcs hp
	public static int getHp(int npc) {
		switch (npc) {
		case RUNT:
			return 120;
		case BEAST:
			return 180;
		case RANGER:
		case MAGE:
			return 180;
		case SHAMAN:
			return 2200;
		case LIZARD:
			return 90;
		case AIR_CRAB:
		case FIRE_CRAB:
		case EARTH_CRAB:
		case WATER_CRAB:
			return 180;
		case ICE_FIEND:
			return 1800;
		case VESPINE:
			return 250;
		case VANGUARD:
			return 2200;
		case ICE_DEMON:
			case JAD:
			case VENNY:
			return 1800;
		case VESPULA:
			return 3000;
		case MUTTADILE:
			return 1950;
		case VASA:
			return 3000;
		case TEKTON:
			return 3400;
			case DAGPRIME:
			case DAGREX:
			case DAGSUP:
				return 1400;
			case GENERAL:
			case KRIL:
			case COMZIL:
			case KREE:
				return 2250;
			case KBD:
			case CE:
				return 900;

		}
		return 50 + Misc.random(250);
	}
	//get the npcs max hit
	public static int getMax(int npc) {
		switch (npc) {
		case RUNT:
			return 20;
		case BEAST:
			return 22;
		case RANGER:
			return 24;
		case MAGE:
			return 35;
		case SHAMAN:
			return 38;
		case LIZARD:
			case KBD:
			case CE:
			return 12;
		case AIR_CRAB:
		case FIRE_CRAB:
		case EARTH_CRAB:
		case WATER_CRAB:
			case GENERAL:
			case KRIL:
			case COMZIL:
			case KREE:
			return 22;
		case ICE_FIEND:
			return 26;
		case VESPINE:
			return 28;
			case DAGPRIME:
			case DAGREX:
			case DAGSUP:
		case VANGUARD:
			return 24;
			case VENNY:
		case VESPULA:
			return 60;
		case TEKTON:
			return 70;
		case MUTTADILE:
			return 50;
		case VASA:
			return 60;
		case ICE_DEMON:
			return 45;
		}
		return 5 + Misc.random(5);
	}
// get the npcs attack lvl?
	public static int getAtk(int npc) {
		switch (npc) {
		case RUNT:
			return 100;
		case BEAST:
			return 115;
		case RANGER:
			return 125;
		case MAGE:
			case KBD:
			case CE:
			return 125;
		case SHAMAN:
			return 300;
		case LIZARD:
			case DAGPRIME:
			case DAGREX:
			case DAGSUP:
			return 100;
		case AIR_CRAB:
		case FIRE_CRAB:
		case EARTH_CRAB:
		case WATER_CRAB:
			case VENNY:
			return 100;
		case ICE_FIEND:
			return 115;
		case VESPINE:
			return 135;
		case VANGUARD:
			case GENERAL:
			case KRIL:
			case COMZIL:
			case KREE:
			return 120;

		case VESPULA:
			return 360;
		case TEKTON:
			return 500;
		case MUTTADILE:
			return 350;
		case VASA:
			return 350;
		case ICE_DEMON:
			return 400;
		}
		return 50 + Misc.random(50);
	}
//get the npcs def lvl
	public static int getDef(int npc) {
		switch (npc) {
		case RUNT:
			return 100;
		case BEAST:
			case DAGPRIME:
			case DAGREX:
			case DAGSUP:
			return 110;
		case RANGER:
			return 120;
		case MAGE:
			return 90;
		case SHAMAN:
			return 250;
		case LIZARD:
			case KBD:
			case CE:
			return 50;
		case AIR_CRAB:
		case FIRE_CRAB:
		case EARTH_CRAB:
		case WATER_CRAB:
			return 150;
		case ICE_FIEND:
			return 1200;
		case VESPINE:
			return 200;
		case VANGUARD:
			case VENNY:
			return 180;
		case VESPULA:
			case GENERAL:
			case KRIL:
			case COMZIL:
			case KREE:
			return 280;
		case TEKTON:
			return 450;
		case MUTTADILE:
			return 400;
		case VASA:
			return 300;
		case ICE_DEMON:
			return 350;
		}
		return 50 + Misc.random(50);
	} 
//sets the npcs to the level (wave) ie. line 1 = wave 1, line 2 = wave 2 etc etc
	public static final int[][] LEVEL = {
			{ RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER, RANGER },

			{ RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER },

			{ RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER },

			{ RANGER, RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER, RANGER, RANGER, RANGER },
			{ RANGER, RANGER, RANGER },

			{ RANGER, RANGER, RANGER }

	};

}
