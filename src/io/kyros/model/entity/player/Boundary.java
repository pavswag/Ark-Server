package io.kyros.model.entity.player;

import com.google.common.base.Preconditions;
import io.kyros.Server;
import io.kyros.model.collisionmap.doors.Location;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.world.objects.GlobalObject;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Mar 2, 2014
 */
public class Boundary {

	public static final Boundary DUKE_VISION = new Boundary(3036, 6435, 3042, 6451);
	public static final Boundary WHISPERER_PILLAR = new Boundary(2645, 6358, 2667, 6379);
	public static final Boundary WHISPERER_BOUNDARY = new Boundary(2636, 6348, 2677, 6385);
	public static final Boundary REV_CAVE1 = new Boundary(3136, 10048, 3199, 10111);
	public static final Boundary REV_CAVE2 = new Boundary(3200, 10048, 3263, 10111);
	public static final Boundary REV_CAVE3 = new Boundary(3136, 10112, 3199, 10175);
	public static final Boundary REV_CAVE4 = new Boundary(3200, 10112, 3263, 10175);
	public static final Boundary REV_CAVE5 = new Boundary(3136, 10176, 3199, 10239);
	public static final Boundary REV_CAVE6 = new Boundary(3200, 10176, 3263, 10239);
	public static final Boundary WINTERTODT = new Boundary(1600, 3968, 1663, 4031);
	public static final Boundary AFK_ZONE = new Boundary(2112, 5504, 2175, 5567);
	public static final Boundary AFK_ZONE_BOSS = new Boundary(2116, 5508, 2124, 5515);

	public static final Boundary SARACHNIS = new Boundary(1820, 9883, 1855, 9919);
	public static final Boundary KBD = new Boundary(2245, 4675, 2300, 4718);
	public static final Boundary WILDYMAN_DONORZONE = new Boundary(3008, 4480, 3071, 4543);
	public static final Boundary VOTE_BOSS = new Boundary(3712, 3904, 3775, 4031);
	public static final Boundary HOT_DROP = new Boundary(2880, 5056, 2943, 5119);
	public static final Boundary HALLOWEEN_BOSS = new Boundary(2304, 3648, 2367, 3711);
	public static final Boundary ARAXXOR_BOSS = new Boundary(3584, 9792, 3647, 9855);
    public static final Boundary YAMA_ZONE = new Boundary(3355, 9733, 3390, 9764);

	public static final Boundary PYRAMID_PLUNDER = new Boundary(1920, 4416, 1983, 4479);
	public static final Boundary GROOT_BOSS = new Boundary(2511, 3158, 2546, 3178);
	public static final Boundary HALLOWEEN = new Boundary(2560, 4800, 2623, 4863);
	public static final Boundary STAFF_ZONE = new Boundary(3584, 3648, 3647, 3711);
	public static final Boundary EVIL_SNOWMAN = new Boundary(2560, 3904, 2623, 3967);
	public static final Boundary NORTH_POLE = new Boundary(1920, 6208, 1983, 6271);
	public static final Boundary PERK_ZONE = new Boundary(3328, 9600, 3391, 9663);

	public static final Boundary BABA_ZONE = new Boundary(2688, 5760, 2751, 5823);
	public static final Boundary CHAOTIC_ZONE = new Boundary(3840, 4352, 3903, 4415);

	public static final Boundary BALD_EAGLE = new Boundary(3191, 3428, 3200, 3445);

	public static final Boundary SPIDER_CAVE = new Boundary(3584, 9792, 3711, 9855);

	public static final Boundary XMAS_ZONE_1 = new Boundary(2816, 3840, 2879, 3903);
	public static final Boundary XMAS_ZONE_2 = new Boundary(2880, 3840, 2943, 3903);
	public static final Boundary XMAS_ZONE_3 = new Boundary(2880, 3904, 2943, 3967);
	public static final Boundary XMAS_ZONE_4 = new Boundary(2816, 3904, 2879, 3967);

	public static final Boundary[] XMAS_ZONES = {XMAS_ZONE_1, XMAS_ZONE_2, XMAS_ZONE_3, XMAS_ZONE_4};

	public static final Boundary Ferox1 = new Boundary(3129, 3610, 3140, 3643);
	public static final Boundary Ferox2 = new Boundary(3124, 3616, 3128, 3643);
	public static final Boundary Ferox3 = new Boundary(3141, 3615, 3146, 3645);
	public static final Boundary Ferox4 = new Boundary(3147, 3622, 3152, 3646);
	public static final Boundary Ferox5 = new Boundary(3118, 3616, 3123, 3639);
	public static final Boundary Ferox6 = new Boundary(3153, 3626, 3156, 3646);
	public static final Boundary Ferox7 = new Boundary(3129, 3601, 3140, 3609);
	public static final Boundary Ferox8 = new Boundary(3124, 3615, 3128, 3615);
	public static final Boundary Ferox9 = new Boundary(3128, 3615, 3128, 3605);
	public static final Boundary Ferox10 = new Boundary(3128, 3605, 3125, 3605);
	public static final Boundary Ferox11 = new Boundary(3125, 3603, 3128, 3603);
	public static final Boundary Ferox12 = new Boundary(3129, 3646, 3140, 3644);
	public static final Boundary Ferox13 = new Boundary(3125, 3605, 3125, 3603);

	public static final Boundary[] Ferox_boundaries = new Boundary[] {Ferox1,Ferox2,
			Ferox3,
			Ferox4,
			Ferox5,
			Ferox6,
			Ferox7,
			Ferox8,
			Ferox9,
			Ferox10,
			Ferox11,
			Ferox12,
			Ferox13 };

    public static final Boundary BLAST_FURNACE = new Boundary(1920, 4928, 1983, 4991);
    public static final Boundary VOTE_ENTRY = new Boundary(1152, 9792, 1215, 9855);
	public static final Boundary COLOSSEUM = new Boundary(1804, 3087, 1841, 3125);
	public static Boundary Castle_Wars = new Boundary(2368, 3072, 2431, 3135);
	public static Boundary ZAMMY_BOUNDS = new Boundary(2406, 9512, 2431, 9535);
	public static Boundary SARA_BOUNDS = new Boundary(2368, 9472, 2392, 9498);

	/**
	 * Calculates the lowest/highest position based on the positions provided.
	 *
	 * @return {@link Boundary}
	 */
	public static Boundary calculateBoundary(Position a, Position b, int height) {
		return calculateBoundary(a.getX(), a.getY(), b.getX(), b.getY(), height);
	}

	/**
	 * Calculates the lowest/highest position based on the positions provided.
	 *
	 * @return {@link Boundary}
	 */
	public static Boundary calculateBoundary(int x1, int y1, int x2, int y2, int height) {
		return new Boundary(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2), height);
	}

	public static int getWildernessLevel(int x, int y) {
		int modY = x > 6400 ? y - 6400 : y;
		return (((modY - 3520) / 8) + 1);
	}

	int minX, minY, highX, highY;
	int height;

	public Boundary(Position low, Position high) {
		this(low.getX(), low.getY(), high.getX(), high.getY());
	}

	/**
	 * @param minX  The south-west x coordinate
	 * @param minY  The south-west y coordinate
	 * @param highX The north-east x coordinate
	 * @param highY The north-east y coordinate
	 */
	public Boundary(int minX, int minY, int highX, int highY) {
		this.minX = minX;
		this.minY = minY;
		this.highX = highX;
		this.highY = highY;
		height = -1;
	}

	public Boundary(Position low, Position high, int height) {
		this(low.getX(), low.getY(), high.getX(), high.getY(), height);
	}

	/**
	 * @param minX   The south-west x coordinate
	 * @param minY   The south-west y coordinate
	 * @param highX  The north-east x coordinate
	 * @param highY  The north-east y coordinate
	 * @param height The height of the boundary
	 */
	public Boundary(int minX, int minY, int highX, int highY, int height) {
		this.minX = minX;
		this.minY = minY;
		this.highX = highX;
		this.highY = highY;
		this.height = height;
	}


    /**
	 * Check if this boundary intersects with another boundary.
	 */
	public boolean intersects(Boundary boundary) {
		int x1 = getMinimumX();
		int y1 = getMinimumY();
		int x2 = getMaximumX();
		int y2 = getMaximumY();
		int x3 = boundary.getMinimumX();
		int y3 = boundary.getMinimumY();
		int x4 = boundary.getMaximumX();
		int y4 = boundary.getMaximumY();
		return (x1 < x4) && (x3 < x2) && (y1 < y4) && (y3 < y2);
	}

	public int getMinimumX() {
		return minX;
	}

	public int getMinimumY() {
		return minY;
	}

	public int getMaximumX() {
		return highX;
	}

	public int getMaximumY() {
		return highY;
	}

	// No-argument constructor
	public Boundary() {}

	public boolean in(Entity entity) {
		return isIn(entity, this);
	}

	/**
	 * @param entity     The entity object
	 * @param boundaries The array of Boundary objects
	 * @return
	 */
	public static boolean isIn(Entity entity, Boundary... boundaries) {
		Preconditions.checkState(boundaries.length > 0, "No boundaries specified.");
		return isIn(entity.getPosition(), boundaries);
	}

	public static boolean isIn(Position position, Boundary... boundaries) {
		for (Boundary b : boundaries) {
			if (b.height >= 0) {
				if (position.getHeight() != b.height) {
					continue;
				}
			}
			if (position.getX() >= b.minX && position.getX() <= b.highX && position.getY() >= b.minY && position.getY() <= b.highY) {
				return true;
			}
		}

		return false;
	}
	public static boolean isIn(GlobalObject obj, Boundary area) {
		return isIn(obj.getPosition(), area);
	}

	/**
	 * @param player     The player object
	 * @param boundaries The boundary object
	 * @return
	 */
	public static boolean isIn(Player player, Boundary boundaries) {
		if (boundaries.height >= 0) {
			if (player.heightLevel != boundaries.height) {
				return false;
			}
		}
		return player.absX >= boundaries.minX && player.absX <= boundaries.highX && player.absY >= boundaries.minY && player.absY <= boundaries.highY;
	}

	/**
	 * @param npc        The npc object
	 * @param boundaries The boundary object
	 * @return
	 */
	public static boolean isIn(NPC npc, Boundary boundaries) {
		if (boundaries.height >= 0) {
			if (npc.heightLevel != boundaries.height) {
				return false;
			}
		}
		return npc.absX >= boundaries.minX && npc.absX <= boundaries.highX && npc.absY >= boundaries.minY && npc.absY <= boundaries.highY;
	}

	public static boolean isIn(NPC npc, Boundary... boundaries) {
		for (Boundary boundary : boundaries) {
			if (boundary.height >= 0) {
				if (npc.heightLevel != boundary.height) {
					return false;
				}
			}
			if (npc.absX >= boundary.minX && npc.absX <= boundary.highX && npc.absY >= boundary.minY && npc.absY <= boundary.highY) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInSameBoundary(Player player1, Player player2, Boundary[] boundaries) {
		Optional<Boundary> boundary1 = Arrays.asList(boundaries).stream().filter(b -> isIn(player1, b)).findFirst();
		Optional<Boundary> boundary2 = Arrays.asList(boundaries).stream().filter(b -> isIn(player2, b)).findFirst();
		if (!boundary1.isPresent() || !boundary2.isPresent()) {
			return false;
		}
		return Objects.equals(boundary1.get(), boundary2.get());
	}

	public static int getPlayersInBoundary(Boundary boundary) {
		AtomicInteger i = new AtomicInteger();
		Server.getPlayers().forEachFiltered(player -> isIn(player, boundary), (p) -> i.getAndIncrement());
		return i.get();
	}

	/**
	 * Returns the centre point of a boundary as a {@link Coordinate}
	 *
	 * @param boundary The boundary of which we want the centre.
	 * @return The centre point of the boundary, represented as a {@link Coordinate}.
	 */
	public static Coordinate centre(Boundary boundary) {
		int x = (boundary.minX + boundary.highX) / 2;
		int y = (boundary.minY + boundary.highY) / 2;
		if (boundary.height >= 0) {
			return new Coordinate(x, y, boundary.height);
		} else {
			return new Coordinate(x, y, 0);
		}
	}

	public static final Boundary XMAS_CASTLE = new Boundary(2909, 3933, 2924, 3948);

	/**
	 * Diary locations
	 */
	public static final Boundary VARROCK_BOUNDARY = new Boundary(3136, 3349, 3326, 3519);
	public static final Boundary ARDOUGNE_BOUNDARY = new Boundary(2432, 3259, 2690, 3380);
	public static final Boundary ARDOUGNE_ZOO_BRIDGE_BOUNDARY = new Boundary(2611, 3270, 2614, 3280);
	public static final Boundary FALADOR_BOUNDARY = new Boundary(2935, 3310, 3066, 3394);
	public static final Boundary CRAFTING_GUILD_BOUNDARY = new Boundary(2925, 3274, 2944, 3292);
	public static final Boundary TAVERLY_BOUNDARY = new Boundary(2866, 3388, 2938, 3517);
	public static final Boundary LUMRIDGE_BOUNDARY = new Boundary(3142, 3139, 3265, 3306);
	public static final Boundary DRAYNOR_DUNGEON_BOUNDARY = new Boundary(3084, 9623, 3132, 9700);
	public static final Boundary AL_KHARID_BOUNDARY = new Boundary(3263, 3136, 3388, 3328);
	public static final Boundary DRAYNOR_MANOR_BOUNDARY = new Boundary(3074, 3311, 3131, 3388);
	public static final Boundary DRAYNOR_BOUNDARY = new Boundary(3065, 3216, 3136, 3292);
	public static final Boundary KARAMJA_BOUNDARY = new Boundary(2816, 3139, 2965, 3205);
	public static final Boundary BRIMHAVEN_BOUNDARY = new Boundary(2683, 3138, 2815, 3248);
	public static final Boundary BRIMHAVEN_DUNGEON_BOUNDARY = new Boundary(2624, 9402, 2752, 9604);
	public static final Boundary TZHAAR_CITY_BOUNDARY = new Boundary(2368, 5056, 2495, 5183);
	public static final Boundary FOUNTAIN_OF_RUNE_BOUNDARY = new Boundary(3367, 3888, 3380, 3899);
	public static final Boundary DEMONIC_RUINS_BOUNDARY = new Boundary(3279, 3879, 3294, 3893);
	public static final Boundary WILDERNESS_GOD_WARS_BOUNDARY = new Boundary(3008, 10112, 3071, 10175);
	public static final Boundary RESOURCE_AREA_BOUNDARY = new Boundary(3173, 3923, 3197, 3945);
	public static final Boundary CANIFIS_BOUNDARY = new Boundary(3471, 3462, 3516, 3514);
	public static final Boundary CATHERBY_BOUNDARY = new Boundary(2767, 3392, 2864, 3521);
	public static final Boundary SEERS_BOUNDARY = new Boundary(2574, 3393, 2766, 3517);
	public static final Boundary RELLEKKA_BOUNDARY = new Boundary(2590, 3597, 2815, 3837);
	public static final Boundary GNOME_STRONGHOLD_BOUNDARY = new Boundary(2369, 3398, 2503, 3550);
	public static final Boundary LLETYA_BOUNDARY = new Boundary(2314, 3153, 2358, 3195);
	public static final Boundary BANDIT_CAMP_BOUNDARY = new Boundary(3156, 2965, 3189, 2993);
	public static final Boundary DESERT_BOUNDARY = new Boundary(3136, 2880, 3517, 3122);
	public static final Boundary SLAYER_TOWER_BOUNDARY = new Boundary(3399, 3527, 3454, 3581);
	public static final Boundary APE_ATOLL_BOUNDARY = new Boundary(2691, 2692, 2815, 2812);
	public static final Boundary FELDIP_HILLS_BOUNDARY = new Boundary(2474, 2880, 2672, 3010);
	public static final Boundary YANILLE_BOUNDARY = new Boundary(2531, 3072, 2624, 3126);
	public static final Boundary ZEAH_BOUNDARY = new Boundary(1402, 3446, 1920, 3972);
	public static final Boundary LUNAR_ISLE_BOUNDARY = new Boundary(2049, 3844, 2187, 3959);
	public static final Boundary FREMENNIK_ISLES_BOUNDARY = new Boundary(2300, 3776, 2436, 3902);
	public static final Boundary WATERBIRTH_ISLAND_BOUNDARY = new Boundary(2495, 3711, 2559, 3772);
	public static final Boundary MISCELLANIA_BOUNDARY = new Boundary(2493, 3835, 2628, 3921);
	public static final Boundary WOODCUTTING_GUILD_BOUNDARY = new Boundary(1608, 3479, 1657, 3516);
	public static final Boundary HUNLLEF_BOSS_ROOM = new Boundary(1156, 9922, 1186, 9948);
	public static final Boundary LUMBRIDGE_OUTLAST_LOBBY = new Boundary(3399, 4807, 3448, 4848);
	public static final Boundary LUMBRIDGE_OUTLAST_AREA = new Boundary(3136, 4928, 3199, 4991);
	public static final Boundary OUTLAST_AREA = new Boundary(3263, 4927, 3330, 4992);
	public static final Boundary OUTLAST = new Boundary(3263, 4927, 3330, 4992);


	public static final Boundary FALLY_OUTLAST = new Boundary(3456, 4736, 3519, 4783);
	public static final Boundary LUMBRIDGE_OUTLAST = new Boundary(3400, 4808, 3447, 4847);
	public static final Boundary SNOW_OUTLAST = new Boundary(3392, 4864, 3455, 4927);
	public static final Boundary SWAMP_OUTLAST = new Boundary(3392, 4928, 3455, 4991);
	public static final Boundary FOREST_OUTLAST = new Boundary(3392, 4992, 3455, 5055);
	public static final Boundary ROCK_OUTLAST = new Boundary(3392, 5056, 3455, 5119);
	public static final Boundary WG_Boundary = new Boundary(1856, 4224, 1919, 4287);

	public static final Boundary OUTLAST_LOBBY = new Boundary(3321, 4939, 3325, 4979);
	public static final Boundary OUTLAST_HUT = new Boundary(3106, 3475, 3112, 3480);
	public static final Boundary TOURNAMENT_LOBBIES_AND_AREAS = new Boundary(3264, 4672, 3647, 5119);
	public static final Boundary DEMONIC_GORILLAS = new Boundary(2071, 5613, 2192, 5702);
	public static final Boundary CRYSTAL_CAVE_STAIRS = new Boundary(3222, 12441, 3229, 12448);
	public static final Boundary CRYSTAL_CAVE_ENTRANCE = new Boundary(3268, 6050, 3278, 6056);
	public static final Boundary CRYSTAL_CAVE_AREA = new Boundary(3131, 12346, 3265, 12482);

	public static final Boundary UNICOW_AREA = new Boundary(2820, 5060, 2875, 5115);
	public static final Boundary PERK_BOSS = new Boundary(3328, 9600, 3391, 9663,1);

	public static final Boundary TzHaar_Foundry = new Boundary(2048, 4864, 2111, 4927);

	public static final Boundary FLOWER_POKER_AREA = new Boundary(1766, 3561, 1788, 3582);
	public static final Boundary FLOWER_POKER_AREA_EAST = new Boundary(3365, 3298,3371,3321);
	public static final Boundary FLOWER_POKER_AREA_WEST = new Boundary(3355,3297,3371,3322);
	public static final Boundary FP_LANE_1 = new Boundary(1776, 3564, 1777, 3568);
	public static final Boundary FP_LANE_2 = new Boundary(1781, 3564, 1782, 3568);
	public static final Boundary FP_LANE_3 = new Boundary(1776, 3575, 1777, 3579);
	public static final Boundary FP_LANE_4 = new Boundary(1781, 3575, 1782, 3579);
	public static final Boundary FP_LANE_5 = new Boundary(3366,3315,3370,3316);
	public static final Boundary FP_LANE_6 = new Boundary(3366,3319,3370,3320);

	public static final Boundary FP_LANE_7 = new Boundary(3356,3299,3360,3300);
	public static final Boundary FP_LANE_8 = new Boundary(3356,3303,3360,3304);
	public static final Boundary FP_LANE_9 = new Boundary(3356,3307,3360,3308);
	public static final Boundary FP_LANE_10 = new Boundary(3356,3311,3360,3312);
	public static final Boundary FP_LANE_11 = new Boundary(3356,3315,3360,3316);
	public static final Boundary FP_LANE_12 = new Boundary(3356,3319,3360,3320);
	public static final Boundary[] FP_LANES = {FP_LANE_1, FP_LANE_2, FP_LANE_3, FP_LANE_4};
	public static final Boundary MAGE_ARENA = new Boundary(3092, 3912, 3117, 3954);
	/**
	 * 3118 3923
	 * 3128 3942
	 *
	 * 3082 3922
	 * 3095 3940
	 */
//3082 3921
	//3217 3942
	public static final boolean isInMageArena(Entity e) {
		return e.getX() >= 3095 && e.getX() <= 3117 && e.getY() >= 3912 && e.getY() <= 3954//center
		|| e.getX() >= 3082 && e.getX() <= 3217 && e.getY() >= 3921 && e.getY() <= 3942//west


				;
	}

	/**
	 * Essence Mine
	 */
    public static final Boundary ABYSS_RC = new Boundary(3024, 4818, 3053, 4847);
	public static final Boundary ESSENCE_MINE = new Boundary(2918, 4807, 2938, 4826);
	public static final Boundary HUNLLEF_CHEST = new Boundary(3268, 6047, 3277, 6062);
	public static final Boundary MAX_ISLAND = new Boundary(3828, 3888, 3834, 3897);

	public static final Boundary GRAND_EXCHANGE = new Boundary(3095, 3493, 3098, 3496);
	/**
	 * Konar Locations
	 */
	public static final Boundary TAVERLY_DUNGEON = new Boundary(2802, 9715, 2959, 9858);

	public static final Boundary OSRS_GRAND_EXCHANGE = new Boundary(3142, 3468, 3189, 3516);


	public static final Boundary SARACHNIS_LAIR = new Boundary(1830,9893,1850,9911);
	public static final Boundary MIMIC_LAIR = new Boundary(2708,4309,2731,4328);
	public static final Boundary GROTESQUE_LAIR = new Boundary(1689, 4567,1704, 4582);
	/**
	 * Halloween
	 */
	public static final Boundary HALLOWEEN_ORDER_MINIGAME = new Boundary(2591, 4764, 2617, 4786);

	/**
	 * skilling island
	 */
	public static final Boundary SKILLING_ISLAND = new Boundary(3072, 3456, 3135, 3519);
	public static final Boundary SKILLING_ISLAND_BANK = new Boundary(3072, 3456, 3135, 3519);
	public static final Boundary HOME = new Boundary(3072, 3456, 3135, 3519);

	/**
	 * mosleharmless cave
	 */
	public static final Boundary MOS_LEHARMLESS_CAVE1 = new Boundary(3712, 9344, 3775, 9407);
	public static final Boundary MOS_LEHARMLESS_CAVE2 = new Boundary(3712, 9408, 3775, 9471);
	public static final Boundary MOS_LEHARMLESS_CAVE3 = new Boundary(3776, 9408, 3839, 9471);


	/*
	 *
	 */

	public static final Boundary SLAYER_REV_CAVE_1 = new Boundary(3224, 10192, 3251, 10218);
	public static final Boundary SLAYER_REV_CAVE_2 = new Boundary(3146, 10128, 3227, 10232);
	public static final Boundary SLAYER_REV_CAVE_3 = new Boundary(3126, 10041, 3240, 10150);
	public static final Boundary REV_CAVE = new Boundary(3121, 10038, 3276, 10245);
	/**
	 * Hunter
	 */
	public static final Boundary HUNTER_JUNGLE = new Boundary(1486, 3392, 1685, 3520);
	public static final Boundary HUNTER_LOVAK = new Boundary(1468, 3840, 1511, 3890);
	public static final Boundary HUNTER_DONATOR = new Boundary(2124, 4917, 2157, 4946);
	public static final Boundary HUNTER_LZ_DONATOR = new Boundary(2856, 5069, 2868, 5081);
	public static final Boundary HUNTER_WILDERNESS = new Boundary(3128, 3755, 3172, 3796);
	public static final Boundary PURO_PURO = new Boundary(2561, 4289, 2623, 4351);




	public static final Boundary RESOURCE_HUNTER = new Boundary(3174, 3925, 3202, 3944);
	public static final Boundary[] HUNTER_BOUNDARIES = {HUNTER_JUNGLE, HUNTER_WILDERNESS, HUNTER_LOVAK, HUNTER_DONATOR, HUNTER_LZ_DONATOR, RESOURCE_HUNTER};

	public static final Boundary LAVA_DRAGON_ISLE = new Boundary(3174, 3801, 3233, 3855);

	public static final Boundary ABYSSAL_SIRE = new Boundary(2942, 4735, 3136, 4863);

	public static final Boundary COMBAT_DUMMY = new Boundary(2846, 2960, 2848, 2962);

	public static final Boundary SAFEPKSAFE = new Boundary(3027, 3516, 3329, 3536);

	public static final Boundary TELEGRAB_WILDYEDGE = new Boundary(2945, 3520, 3329, 3537);
	/**
	 * Raids bosses
	 */

	public static final Boundary DZ_BOSS = new Boundary(3722, 2817, 3798, 2833);


	public static final Boundary ALTAR = new Boundary(3223, 3603, 3255, 3633);
	public static final Boundary FORTRESS = new Boundary(2993, 3615, 3024, 3648);
	public static final Boundary DEMONIC = new Boundary(3236, 3852, 3275, 3884);
	public static final Boundary ROGUES = new Boundary(3293, 3919, 3320, 3950);
	public static final Boundary DRAGONS = new Boundary(3293, 3655, 3320, 3682);
	public static final Boundary[] PURSUIT_AREAS = {ALTAR, FORTRESS, DEMONIC, ROGUES, DRAGONS};

	/**
	 * Kalphite Queen
	 */
	public static final Boundary KALPHITE_QUEEN = new Boundary(3457, 9472, 3514, 9527);

	public static final Boundary CATACOMBS = new Boundary(1590, 9980, 1731, 10115);


	public static final Boundary CLAN_WARS = new Boundary(3272, 4759, 3380, 4852);
	public static final Boundary CLAN_WARS_SAFE = new Boundary(3263, 4735, 3390, 4761);

	public static final Boundary NEW_INSTANCE_AREA = new Boundary(1920, 3712, 1983, 3775);

	/**
	 * Cerberus spawns
	 */
	public static final Boundary CERBERUS_ROOM_WEST = new Boundary(1224, 1225, 1259, 1274);
	public static final Boundary CERBERUS_ROOM_NORTH = new Boundary(1290, 1288, 1323, 1338);
	public static final Boundary CERBERUS_ROOM_EAST = new Boundary(1354, 1224, 1387, 1274);
	public static final Boundary CERB_BOUNDARY2 = new Boundary(1234, 1246, 1246, 1256);
	public static final Boundary[] CERBERUS_BOSSROOMS = {CERBERUS_ROOM_NORTH, CERBERUS_ROOM_WEST, CERBERUS_ROOM_EAST};


	public static final Boundary SKOTIZO_BOSSROOM = new Boundary(2240, 5632, 2303, 5695);

	public static final Boundary GODWARS_MAIN = new Boundary(2847, 5346, 2912, 5280);
	public static final Boundary GODWARS_AREA = new Boundary(2815, 5245, 2954, 5377);

	public static final Boundary GODWARS_ARMADYL_ROOM = new Boundary(2847, 5346, 2912, 5280);
	public static final Boundary GODWARS_BANDOS_ROOM = new Boundary(2819, 5372, 2860, 5311);
	public static final Boundary GODWARS_SARA_ROOM = new Boundary(2910, 5308, 2944, 5252);
	public static final Boundary GODWARS_ZAMMY_ROOM = new Boundary(2880, 5371, 2940, 5335);

	public static final Boundary[] GODWARS_OTHER_ROOMS = {GODWARS_MAIN, GODWARS_ARMADYL_ROOM, GODWARS_BANDOS_ROOM, GODWARS_SARA_ROOM, GODWARS_ZAMMY_ROOM};

	public static final Boundary BANDOS_GODWARS = new Boundary(2864, 5351, 2876, 5369);
	public static final Boundary ARMADYL_GODWARS = new Boundary(2824, 5296, 2842, 5308);
	public static final Boundary ZAMORAK_GODWARS = new Boundary(2918, 5318, 2936, 5331);
	public static final Boundary SARADOMIN_GODWARS = new Boundary(2884, 5257, 2907, 5276);
	public static final Boundary[] GODWARS_BOSSROOMS = {BANDOS_GODWARS, ARMADYL_GODWARS, ZAMORAK_GODWARS, SARADOMIN_GODWARS};
	public static final Boundary NEX = new Boundary(2909, 5187, 2943, 5220);
	public static final Boundary DUKE_INSTANCE = new Boundary(3008, 6400, 3071, 6463);

	public static final Boundary JORMUNGANDS_PRISON = new Boundary(2383, 10360, 2506, 10477);

	public static final Boundary KARUULM_DUNGEON = new Boundary(1217, 10126, 1415, 10301);

	public static final Boundary DEATH_PLATEAU = new Boundary(2841, 3576, 2886, 3606);

	public static final Boundary BRINE_RAT_CAVERN = new Boundary(2685, 10111, 2748, 10153);


	public static final Boundary SMOKE_DEVIL_BOUNDARY = new Boundary(2338, 9409, 2433, 9472);
	public static final Boundary LITHKREN_VAULT = new Boundary(1530, 5050, 1608, 5124);
	public static final Boundary ANCIENT_CAVERN = new Boundary(1595, 5311, 1798, 5380);
	public static final Boundary STRONGHOLD_CAVE = new Boundary(2373, 9760, 2501, 9842);

	public static final Boundary OBOR_AREA = new Boundary(3072, 9792, 3110, 9818);

	public static final Boundary BRYOPHYTA_ROOM = new Boundary(3198, 9918, 3239, 9945);

	public static final Boundary WATERBIRTH_CAVES_1 = new Boundary(2424, 10097, 2569, 10185);
	public static final Boundary WATERBIRTH_CAVES_2 = new Boundary(1778, 4316, 2047, 4420);
	public static final Boundary WATERBIRTH_BOSS_CAVE = new Boundary(2876, 4354, 2942, 4475);
	public static final Boundary[] WATERBIRTH_CAVES = {WATERBIRTH_CAVES_1, WATERBIRTH_CAVES_2, WATERBIRTH_BOSS_CAVE };


	public static final Boundary FIGHT_ROOM = new Boundary(1671, 4690, 1695, 4715);
	public static final Boundary[] SAFE_ZONE_BLACK_KNIGHTS_FORTRESS = {
			new Boundary(3002, 3535, 3026, 3539),
			new Boundary(3005, 3540, 3025, 3542),
			new Boundary(3006, 3543, 3023, 3543),
			new Boundary(2997, 3525, 3032, 3528),
			new Boundary(2994, 3529, 3028, 3534)
	};


	public static final Boundary Wilderness_Slayer = new Boundary(3328, 10048, 3455, 10175);
	public static final	Boundary AOEInstance = new Boundary(2880, 5440, 2943, 5503);
	public static final Boundary WILDERNESS = new Boundary(2941, 3525, 3392, 3968);
	public static final Boundary WILDERNESS_UNDERGROUND = new Boundary(2980, 10048, 3473, 10373);
	public static final Boundary[] WILDERNESS_PARAMETERS = {WILDERNESS, WILDERNESS_UNDERGROUND, WILDERNESS_GOD_WARS_BOUNDARY, REV_CAVE, Wilderness_Slayer};
	public static final Boundary[] DEEP_WILDY_CAVES = {WILDERNESS_UNDERGROUND, WILDERNESS_GOD_WARS_BOUNDARY, REV_CAVE, Wilderness_Slayer};

	//public static final Boundary WILDERNESS = new Boundary(2941, 3525, 3392, 3968); // (2941, 3525, 3392, 3968);
    //public static final Boundary WILDERNESS = new Boundary(1486, 1444, 3767, 3730);
	//public static final Boundary ZEAH_WILDERNESS = new Boundary(1420, 3730, 1600, 4060);
	//public static final Boundary WILDERNESS_UNDERGROUND = new Boundary(2941, 9918, 3392, 10366);
	//public static final Boundary[] WILDERNESS_PARAMETERS = { WILDERNESS, ZEAH_WILDERNESS, WILDERNESS_UNDERGROUND, WILDERNESS_GOD_WARS_BOUNDARY };

	/*
	 * Hydra Dungeon
	 */
	public static final Boundary HYDRA_DUNGEON = new Boundary(1297, 10215, 1397, 10283);
	public static final Boundary HYDRA_DUNGEON2 = new Boundary(1248, 10144, 1302, 10210);
	public static final Boundary HYDRA_BOSS_ROOM = new Boundary(1355, 10253, 1380, 10281);
	public static final Boundary[] HYDRA_ROOMS = {HYDRA_DUNGEON, HYDRA_DUNGEON2, HYDRA_BOSS_ROOM};

	public static final Boundary HESPORI = new Boundary(2432, 3520, 2495, 3583);
	public static final Boundary HESPORI_EXIT = new Boundary(3066, 3497, 3067, 3501);
	public static final Boundary HESPORI_ENTRANCE = new Boundary(3069, 3497,3072, 3501);
	public static final Boundary INFERNO = new Boundary(2256, 5328, 2286, 5359);




	public static final Boundary EDGE_BANK = new Boundary(3091, 3488, 3098, 3499);
	public static final Boundary EDGE_TRADING_AREA = new Boundary(3088, 3502, 3108, 3520);
	public static final Boundary EDGE_NORTH_BUILDING = new Boundary(3088, 3502, 3108, 3520);
	public static final Boundary EDGE_DUNG_LADDER = new Boundary(3083, 9966, 3098, 9978);
	public static final Boundary EDGE_DUNG_ENTRANCE_LADDER = new Boundary(3092, 9863, 3102, 9870);
	public static final Boundary FOE_DUNGEON = new Boundary(2545, 9947, 2556, 9956);

	public static final Boundary PRESET_SKILLING = new Boundary(3807, 3551, 3108, 3520);
	public static final Boundary PRESET_HOME_MAIN_BANK = new Boundary(3088, 3481, 3097, 3484);
	public static final Boundary PRESET_HOME_TRADING_POST = new Boundary(3093, 3491, 3100, 3498);
	public static final Boundary PRESET_GAMBLING_BANK = new Boundary(3118, 3504, 3121, 3509);
	public static final Boundary DONATOR_ZONE = new Boundary(1920, 5312, 1983, 5375);
	public static final Boundary DONATOR_ZONE_NEW = new Boundary(2368, 3776, 2431, 3839);

	public static final Boundary[] ALLOWED_PRESET_LOAD_AREAS = {EDGE_NORTH_BUILDING, PRESET_SKILLING, PRESET_HOME_MAIN_BANK,PRESET_HOME_TRADING_POST,PRESET_GAMBLING_BANK,DONATOR_ZONE};

	public static final Boundary WILDY_CHAOS_HUT = new Boundary(2942, 3804, 2964, 3831);
	public static final Boundary WILDY_CHAOS_INSIDE_HUT= new Boundary(2948, 3819, 2957, 3824);
	public static final Boundary WILDY_CHAOS_INSIDE_HUT2= new Boundary(2949, 3817, 2952, 3824);
	public static final Boundary ICE_PATH = new Boundary(2837, 3786, 2870, 3821);
	public static final Boundary ICE_PATH_TOP = new Boundary(2822, 3806, 2837, 3813);
	public static final Boundary SEERS = new Boundary(2689, 3455, 2734, 3500);
	public static final Boundary VARROCK = new Boundary(3178, 3390, 3243, 3423);
	public static final Boundary ARDOUGNE = new Boundary(2644, 3288, 2678, 3323);
	public static final Boundary[] ROOFTOP_COURSES = {SEERS_BOUNDARY, VARROCK_BOUNDARY, ARDOUGNE_BOUNDARY};
	public static final Boundary ONYX_ZONE = new Boundary(2778, 4842, 2788, 4851);
	public static final Boundary LEGENDARY_ZONE = new Boundary(2842, 5091, 2853, 5103);
	public static final Boundary DONATOR_ZONE_BLOODY = new Boundary(1956, 5319, 1974, 5338);
	public static final Boundary DONATOR_ZONE_BOSS = new Boundary(1920, 5312,1937,5375);
	public static final Boundary DONATOR_ZONE_NPC = new Boundary(1922, 5319,1940, 5338);
	public static final Boundary REGULAR_DZ_HUNTER = new Boundary(1937,5363,1952,5374);
	public static final Boundary SKILLING_ISLAND_HUNTER = new Boundary(3814, 3543, 3829, 3555);
	public static final Boundary HUNTER_AREA = new Boundary(3531, 3974, 3598, 4036);

	public static final Boundary CORPOREAL_BEAST_LAIR = new Boundary(2972, 4370, 2999, 4397);
	public static final Boundary DAGANNOTH_KINGS = new Boundary(2891, 4428, 2934, 4469);
	public static final Boundary SCORPIA_LAIR = new Boundary(3216, 10329, 3248, 10354);
	public static final Boundary KRAKEN_CAVE = new Boundary(2240, 9984, 2303, 10047);
	public static final Boundary KRAKEN_BOSS_ROOM = new Boundary(2268, 10022, 2294, 10046);
	public static final Boundary DAGANNOTH_MOTHER_HFTD = new Boundary(2501, 4630, 2553, 4678);
	public static final Boundary MONKEY_MADNESS_DEMON = new Boundary(2643, 4546, 2687, 4605);
	public static final Boundary RFD = new Boundary(1888, 5344, 1911, 5367);
	public static final Boundary RESOURCE_AREA = new Boundary(3174, 3924, 3196, 3944);
	public static final Boundary KBD_AREA = new Boundary(2251, 4675, 2296, 4719);
	public static final Boundary PEST_CONTROL_AREA = new Boundary(2622, 2554, 2683, 2675);
	public static final Boundary FIGHT_CAVE = new Boundary(2365, 5052, 2429, 5119);
	public static final Boundary LAKE_MOLCH = new Boundary(1353, 3621, 1386, 3645);
	public static final Boundary HUNLLEF_CAVE = new Boundary(1161, 9924, 1183, 9946);
	public static final Boundary EDGEVILLE_PERIMETER = new Boundary(3072, 3451, 3138, 3520);
	public static final Boundary WILDY_BONE_ALTAR = new Boundary(2945, 3813, 2960, 3827);
	public static final Boundary[] DUEL_ARENA = {new Boundary(3335, 3247, 3350, 3255), new Boundary(3371, 3247, 3386, 3255)};
	public static final Boundary EMPTY = new Boundary(0, 0, 0, 0);

	/*
	 * Minigame Lobbys
	 */
	public static final Boundary LOBBY = new Boundary(3010, 9921, 3070, 9982);
	//South Side
	public static final Boundary RAIDS_LOBBY = new Boundary(3295,5184,3327,5204);
	public static final Boundary RAIDS_LOBBY_ENTRANCE = new Boundary(1216, 3547, 1250, 3583);
	public static final Boundary XERIC_LOBBY = new Boundary(3217, 4817, 3247, 4851);
	public static final Boundary XERIC_LOBBY_ENTRANCE = new Boundary(1216, 3547, 1250, 3583);

	//north side
	public static final Boundary THEATRE_LOBBY = new Boundary(3052, 9961, 3067, 9979);
	public static final Boundary THEATRE_LOBBY_ENTRANCE = new Boundary(3058, 9952, 3061, 9960);
	public static final Boundary TOURNY_LOBBY = new Boundary(3032, 9961, 3047, 9979);
	public static final Boundary TOB_VERZIK = new Boundary(3150, 4293, 3184, 4329);

	/**
	 * Raids bosses
	 */
	public static final Boundary RAID_MAIN = new Boundary(3295, 5152, 3359, 5407, 0);
	public static final Boundary RAID_F1 = new Boundary(3295, 5152, 3359, 5407, 1);
	public static final Boundary RAID_F2 = new Boundary(3295, 5152, 3359, 5407, 2);
	public static final Boundary RAID_F3 = new Boundary(3295, 5152, 3359, 5407, 3);

	public static final Boundary OLM = new Boundary(3197, 5708, 3270, 5780);
	public static final Boundary RAIDS = new Boundary(3212, 5119, 3367, 5763);
	public static final Boundary TEKTON = new Boundary(3305,5412,3320,5426);
	public static final Boundary TEKTON_ATTACK_BOUNDARY = new Boundary(3305,5412,3320,5426);
	public static final Boundary SHAMAN_BOUNDARY = new Boundary(3305, 5257, 3320, 5269);
	public static final Boundary SKELETAL_MYSTICS = new Boundary(3298, 5249, 3325, 5275);
	public static final Boundary ICE_DEMON = new Boundary(3297, 5343, 3325, 5374);
	public static final	Boundary VESPULA = new Boundary(3293, 5463, 3329, 5504);
	public static final Boundary[] RAIDROOMS = {OLM, RAIDS, RAID_MAIN, RAID_F1, RAID_F2, RAID_F3, TEKTON, TEKTON_ATTACK_BOUNDARY, SKELETAL_MYSTICS, ICE_DEMON, SHAMAN_BOUNDARY, VESPULA};
	public static final Boundary FULL_RAIDS = new Boundary(3204, 5118, 3379, 5766);
	public static final Boundary XERIC = new Boundary(3217, 4817, 3247, 4851);
	public static final Boundary XERIC_JAIL = new Boundary(3033, 4405, 3044, 4414);

	public static final Boundary EDGEVILLE_EXTENDED = new Boundary(2944, 3436, 3207, 3549);
	public static final Boundary GODWARS_MAIN_AREA = new Boundary(2811, 5247, 2950, 5347);
	public static final Boundary SWAMP_AREA = new Boundary(3395, 3163, 3853, 3595);
	public static final Boundary WARRIORS_GUILD = new Boundary(2833, 3531, 2878, 3558);
	public static final Boundary ZULRAH = new Boundary(2248, 3059, 2283, 3084);
	public static final Boundary VARDORVIS = new Boundary(1088, 3392, 1151, 3455);
	public static final Boundary DEMONIC_GORILLA = new Boundary(2124, 5660, 2174, 5696);
	public static final Boundary THERMONUCLEARS = new Boundary(2399, 9434, 2439, 9474);
	public static final Boundary WATERBIRTH_DUNGEON = new Boundary(2442, 10147, 2562, 10181);
	public static final Boundary MITHRIL_DRAGONS = new Boundary(1603, 5309, 1803, 5381);
	public static final Boundary DAGGANOTH_MOTHER = new Boundary(2515, 4632, 2545, 4667);
	public static final Boundary DAGGANOTH_KINGS = new Boundary(2882, 4357, 2946, 4476);
	public static final Boundary CANNON_JAD = new Boundary(2365, 5053, 2558, 5187);
	public static final Boundary LZ_CAVE = new Boundary(2363, 10237, 2429, 10303);
	public static final Boundary CANNON_FREMNIK_DUNGEON = new Boundary(2685, 9945, 2815, 10048);
	public static final Boundary LIZARDMAN_CANYON = new Boundary(1468, 3674, 1567, 3709);
	public static final Boundary VORKATH = new Boundary(2257, 4052, 2288, 4079);
	public static final Boundary FORTHOS_DUNGEON = new Boundary(1781, 9875, 1866, 9994);

	public static final Boundary OURIANA_ALTAR = new Boundary(2995, 5559, 3092, 5645);
	public static final Boundary OURIANA_ALTAR_BANK = new Boundary(3010, 5618, 3026, 5630);

	public static final Boundary GROUP_IRONMAN_FORMING = new Boundary(new Position(3059, 3032, 0), new Position(3433, 4073, 0));

	public static final Boundary SUPER_DZ = new Boundary(3019, 2768, 3060, 2812);



	public static final Boundary BOUNTY_HUNTER_OUTLAST = new Boundary(3328, 3968, 3519, 4159);
	public static final Boundary ISLE_OF_THE_DAMNED_LOBBY = new Boundary(3413, 4054, 3519, 4159);

	public static Location centerAsLocation(Boundary boundary) {
		int x = (boundary.minX + boundary.highX) / 2;
		int y = (boundary.minY + boundary.highY) / 2;
		if (boundary.height >= 0) {
			return new Location(x, y, boundary.height);
		} else {
			return new Location(x, y, 0);
		}
	}

	public Location getMinLocation() {
		return new Location(minX, minY);
	}
}