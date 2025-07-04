package io.kyros.content.minigames.fight_cave;

import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Oct 17, 2013
 */
public class Wave {

	public static final int TZ_KIH = 3116;
	public static final int TZ_KEK_SPAWN = 3118;
	public static final int TZ_KEK = 3120;
	public static final int TOK_XIL = 3121;
	public static final int YT_MEJKOT = 3123;
	public static final int KET_ZEK = 3125;
	public static final int TZTOK_JAD = 3127;
	public static int[][] SPAWN_DATA = { { 2403, 5079 }, { 2396, 5074 }, { 2387, 5072 }, { 2388, 5085 }, { 2389, 5096 }, { 2403, 5097 }, { 2410, 5087 } };

	public static int[][] getWaveForType(Player player) {
		switch(player.fightCavesWaveType){
			case 1:
				return LEVEL_1;
			case 2:
				return LEVEL_2;
			case 3:
				return LEVEL_3;
			default:
				return LEVEL_1;
		}
	}

	public static int getHp(int npc) {
		switch (npc) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 10;
		case TZ_KEK:
			return 20;
		case TOK_XIL:
			return 40;
		case YT_MEJKOT:
			return 80;
		case KET_ZEK:
			return 160;
		case TZTOK_JAD:
			return 255;
		}
		return 50 + Misc.random(50);
	}

	public static int getMax(int npc) {
		switch (npc) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 4;
		case TZ_KEK:
			return 7;
		case TOK_XIL:
			return 13;
		case YT_MEJKOT:
			return 20;
		case KET_ZEK:
			return 50;
		case TZTOK_JAD:
			return 97;
		}
		return 5 + Misc.random(5);
	}

	public static int getAtk(int npc) {
		switch (npc) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 30;
		case TZ_KEK:
			return 40;
		case TOK_XIL:
			return 80;
		case YT_MEJKOT:
			return 120;
		case KET_ZEK:
			return 150;
		case TZTOK_JAD:
			return 500;
		}
		return 50 + Misc.random(50);
	}

	public static int getDef(int npc) {
		switch (npc) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 20;
		case TZ_KEK:
			return 40;
		case TOK_XIL:
			return 80;
		case YT_MEJKOT:
			return 100;
		case KET_ZEK:
			return 120;
		case TZTOK_JAD:
			return 500;
		}
		return 50 + Misc.random(50);
	}

	public static final int[][] LEVEL_1 = { 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KIH, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK },
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KEK },
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TOK_XIL }, 
		{ KET_ZEK, YT_MEJKOT, YT_MEJKOT }, 
		{ KET_ZEK, KET_ZEK }, 
		{ TZTOK_JAD } 
	};

	public static final int[][] LEVEL_2 = { 
		{ KET_ZEK, TOK_XIL, TZ_KEK, TZ_KEK }, 
		{ KET_ZEK, TOK_XIL, TOK_XIL }, 
		{ KET_ZEK, YT_MEJKOT }, 
		{ KET_ZEK, YT_MEJKOT, TZ_KIH },
		{ KET_ZEK, YT_MEJKOT, TZ_KIH, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TZ_KEK }, 
		{ KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KIH, TZ_KIH },
		{ KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KEK }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KIH, TZ_KIH },
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH },
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KEK }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TOK_XIL }, 
		{ KET_ZEK, YT_MEJKOT, YT_MEJKOT }, { KET_ZEK, KET_ZEK }, { TZTOK_JAD } 
	};

	public static final int[][] LEVEL_3 = { 
		{ TZ_KIH }, 
		{ TZ_KIH, TZ_KIH }, 
		{ TZ_KEK }, 
		{ TZ_KEK, TZ_KIH }, 
		{ TZ_KEK, TZ_KIH, TZ_KIH }, 
		{ TZ_KEK, TZ_KEK }, 
		{ TOK_XIL },
		{ TOK_XIL, TZ_KIH }, 
		{ TOK_XIL, TZ_KIH, TZ_KIH }, 
		{ TOK_XIL, TZ_KEK }, 
		{ TOK_XIL, TZ_KEK, TZ_KIH }, 
		{ TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH }, 
		{ TOK_XIL, TZ_KEK, TZ_KEK },
		{ TOK_XIL, TOK_XIL }, 
		{ YT_MEJKOT }, 
		{ YT_MEJKOT, TZ_KIH }, 
		{ YT_MEJKOT, TZ_KIH, TZ_KIH }, 
		{ YT_MEJKOT, TZ_KEK }, 
		{ YT_MEJKOT, TZ_KEK, TZ_KIH },
		{ YT_MEJKOT, TZ_KEK, TZ_KIH, TZ_KIH }, 
		{ YT_MEJKOT, TZ_KEK, TZ_KEK }, 
		{ YT_MEJKOT, TOK_XIL },
		{ YT_MEJKOT, TOK_XIL, TZ_KIH }, 
		{ YT_MEJKOT, TOK_XIL, TZ_KIH, TZ_KIH },
		{ YT_MEJKOT, TOK_XIL, TZ_KEK }, 
		{ YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH }, 
		{ YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH }, 
		{ YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KEK },
		{ YT_MEJKOT, TOK_XIL, TOK_XIL }, 
		{ YT_MEJKOT, YT_MEJKOT }, 
		{ KET_ZEK }, 
		{ KET_ZEK, TZ_KIH }, 
		{ KET_ZEK, TZ_KIH, TZ_KIH }, 
		{ KET_ZEK, TZ_KEK },
		{ KET_ZEK, TZ_KEK, TZ_KIH }, 
		{ KET_ZEK, TZ_KEK, TZ_KIH, TZ_KIH }, 
		{ KET_ZEK, TZ_KEK, TZ_KEK }, 
		{ KET_ZEK, TOK_XIL }, 
		{ KET_ZEK, TOK_XIL, TZ_KIH },
		{ KET_ZEK, TOK_XIL, TZ_KIH, TZ_KIH }, 
		{ KET_ZEK, TOK_XIL, TZ_KEK }, 
		{ KET_ZEK, TOK_XIL, TZ_KEK, TZ_KIH }, 
		{ KET_ZEK, TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH },
		{ KET_ZEK, TOK_XIL, TZ_KEK, TZ_KEK }, 
		{ KET_ZEK, TOK_XIL, TOK_XIL }, 
		{ KET_ZEK, YT_MEJKOT }, 
		{ KET_ZEK, YT_MEJKOT, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TZ_KIH, TZ_KIH },
		{ KET_ZEK, YT_MEJKOT, TZ_KEK }, 
		{ KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KIH, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KEK },
		{ KET_ZEK, YT_MEJKOT, TOK_XIL }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KIH, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK },
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH }, 
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KEK },
		{ KET_ZEK, YT_MEJKOT, TOK_XIL, TOK_XIL }, 
		{ KET_ZEK, YT_MEJKOT, YT_MEJKOT }, 
		{ KET_ZEK, KET_ZEK }, 
		{ TZTOK_JAD } 
	};

}
