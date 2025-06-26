package io.kyros.model.entity.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kyros.content.bosses.Durial321;
import io.kyros.content.bosses.SuperiorTask.SuperiorTaskInstance;
import io.kyros.content.bosses.godwars.Godwars;
import io.kyros.content.bosses.nightmare.NightmareConstants;
import io.kyros.content.bosses.xamphur.Xamphur;
import io.kyros.content.bosses.xamphur.XamphurInstance;
import io.kyros.content.donor.DonoSlayerInstances;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.content.minigames.tob.TobConstants;
import io.kyros.content.seasons.Christmas;
import io.kyros.model.Direction;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.collisionmap.Tile;
import io.kyros.model.entity.npc.NPCClipping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static io.kyros.model.entity.player.Boundary.XMAS_ZONES;

public final class Position {

	private final int x;
	
	private final int y;
	
	private final int height;
	
	public Position(int x, int y, int height) {
		this.x = x;
		this.y = y;
		this.height = height;
	}

	private Position() {
		x = y = height = 0;
	}

	public Position(int x, int y) {
		this(x, y, 0);
	}

	public Tile toTile() {
		return new Tile(x, y, height);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Position position = (Position) o;
		return x == position.x &&
				y == position.y &&
				height == position.height;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, height);
	}

	@Override
	public String toString() {
		return "Position{" +
				"x=" + x +
				", y=" + y +
				", z=" + height +
				'}';
	}

	public String getFormattedString() {
		return x + ", " + y + ", " + height;
	}

	public Position withHeight(int height) {
		return new Position(x, y, height);
	}

	public Position withX(int x) {
		return new Position(x, y, height);
	}

	public Position withY(int y) {
		return new Position(x, y, height);
	}

	public int getChunkX() {
		return (getX() >> 3);
	}

	public int getChunkY() {
		return (getY() >> 3);
	}

	/**
	 * Creates a deep copy of this object
	 * @return A deep copy of this object
	 */
	public Position deepCopy() {
		return new Position(this.x, this.y, height);
	}

	public Position translate(int x, int y) {
		return new Position(this.x + x, this.y + y, height);
	}

	public Position translate(Direction direction) {
		return new Position(this.x + direction.x(), this.y + direction.y(), height);
	}


	/**
	 * Deprecated because it rounds absolute double to integer, use {@link Position#getAbsDistance(Position)}.
	 * Use {@link Position#getManhattanDistance(Position)} to get distance in tiles.
	 */
	@Deprecated
	public int getDistance(Position dest) {
		return getDistance(this.getX(), this.getY(), dest.getX(), dest.getY());
	}


	/**
	 * Deprecated because it rounds absolute double to integer, use {@link Position#getAbsDistance(Position)}.
	 * Use {@link Position#getManhattanDistance(Position)} to get distance in tiles.
	 */
	@Deprecated
	public int getDistance(int destX, int destY) {
		return getDistance(this.getX(), this.getY(), destX, destY);
	}

	/**
	 * Deprecated because it rounds absolute double to integer, use {@link Position#getAbsDistance(Position)}.
	 * Use {@link Position#getManhattanDistance(Position)} to get distance in tiles.
	 */
	@Deprecated
	public int getDistance(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	public int getManhattanDistance(Position b) {
		return Math.abs(b.getX() - getX()) + Math.abs(b.getY() - getY());
	}

	public double getAbsDistance(Position b) {
		return Math.sqrt(Math.pow(b.getX() - getX(), 2) + Math.pow(b.getY() - getY(), 2));
	}

	public static double getAbsDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	public int region() {
		return ((x >> 6) << 8) | (y >> 6);
	}

	/**
	 * Creates a directional position to b in which x and y values are both between -1 and 1 inclusive.
	 */
	public Position toDirectional(Position b) {
		int xDiff = b.getX() - x;
		int yDiff = b.getY() - y;
		if (xDiff > 0)
			xDiff = 1;
		if (xDiff < 0) // TODO changed this from -1 to 0!
			xDiff = -1;
		if (yDiff > 0)
			yDiff = 1;
		if (yDiff < 0) // TODO changed this from -1 to 0!
			yDiff = -1;
		return new Position(xDiff, yDiff, getHeight());
	}

	public Position delta(Position b) {
		return new Position(b.getX() - getX(), b.getY() - getY());
	}

	public Position deltaAbsolute(Position b) {
		return new Position(Math.abs(b.getX() - getX()), Math.abs(b.getY() - getY()));
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	public Position getCenterPosition(int size) {
		if (size > 2) {
			int deltax = (int) Math.ceil((double) size / 3d);
			int deltay = (int) Math.ceil((double) size / 3d);

			return new Position(getX() + deltax, getY() + deltay, getHeight());
		}
		return this;
	}

	/**
	 * Checks if this position is within distance of another position.
	 * 
	 * @param position
	 *            the position to check the distance for.
	 * @param distance
	 *            the distance to check.
	 * @return true if this position is within the distance of another position.
	 */
	public boolean withinDistance(Position position, int distance) {
		if (this.height != position.height)
			return false;

		return Math.abs(position.getX() - this.getX()) <= distance && Math.abs(position.getY() - this.getY()) <= distance;
	}


	@JsonIgnore
	public boolean inWild() {
		if (Boundary.isIn(this, Boundary.Ferox1) ||
				Boundary.isIn(this, Boundary.Ferox2)||
				Boundary.isIn(this, Boundary.Ferox3)||Boundary.isIn(this, Boundary.Ferox4)||
				Boundary.isIn(this, Boundary.Ferox5)||Boundary.isIn(this, Boundary.Ferox6)||
				Boundary.isIn(this, Boundary.Ferox7)||Boundary.isIn(this, Boundary.Ferox8)||
				Boundary.isIn(this, Boundary.Ferox9)||Boundary.isIn(this, Boundary.Ferox10)||Boundary.isIn(this, Boundary.Ferox11)||
				Boundary.isIn(this, Boundary.Ferox12)||Boundary.isIn(this, Boundary.Ferox13)) {
			return false;
		}
		if (Boundary.isIn(this, Boundary.Castle_Wars)) {
			return false;
		}
		if (inClanWars())
			return false;
		if (Boundary.isIn(this, new Boundary(3328, 10048, 3455, 10175))) {
			return true;
		}
		if (Boundary.isIn(this, Boundary.BRYOPHYTA_ROOM))
			return false;

		if (Boundary.isIn(this, Boundary.LOBBY)) {
			return false;
		}
		if (Boundary.isIn(this, Boundary.SAFE_ZONE_BLACK_KNIGHTS_FORTRESS)) {
			return false;
		}
		if (Boundary.isIn(this, Boundary.WILDERNESS_UNDERGROUND))
			return true;

        return Boundary.isIn(this, Boundary.WILDERNESS_PARAMETERS);
    }

	@JsonIgnore
	public boolean inBank() {
		return Area(3071, 3110, 3480, 3514) || Area(3089, 3090, 3492, 3498) || Area(3248, 3258, 3413, 3428)
				|| Area(3179, 3191, 3432, 3448) || Area(2944, 2948, 3365, 3374) || Area(2942, 2948, 3367, 3374)
				|| Area(2944, 2950, 3365, 3370) || Area(3008, 3019, 3352, 3359) || Area(3017, 3022, 3352, 3357)
				|| Area(3203, 3213, 3200, 3237) || Area(3212, 3215, 3200, 3235) || Area(3215, 3220, 3202, 3235)
				|| Area(3220, 3227, 3202, 3229) || Area(3227, 3230, 3208, 3226) || Area(3226, 3228, 3230, 3211)
				|| Area(3227, 3229, 3208, 3226) || Area(3025, 3032, 3374, 3384) || Area(3806, 3820, 2840, 2848);
	}

	@JsonIgnore
	public boolean inOlmRoom() {//checks to see if player is in olm room
		return (getX() > 3200 && getX() < 3260 && getY() > 5710 && getY() < 5770);
	}

	@JsonIgnore
	public boolean inRaidLobby() {//checks to see if player is in the raid lobby
        return Boundary.isIn(this, new Boundary(3295, 5184, 3327, 5205));
    }

	@JsonIgnore
	public boolean inPcBoat() {
		return x >= 2660 && x <= 2663 && y >= 2638 && y <= 2643;
	}

	@JsonIgnore
	public boolean inPcGame() {
		return x >= 2624 && x <= 2690 && y >= 2550 && y <= 2619;
	}

	public boolean Area(final int x1, final int x2, final int y1, final int y2) {
		return (x >= x1 && x <= x2 && y >= y1 && y <= y2);
	}

	@JsonIgnore
	public boolean isInJail() {
        return x >= 3608 && x <= 3678 && y >= 3674 && y <= 3678;
    }

	@JsonIgnore
	public boolean inClanWars() {
        return x > 3272 && x < 3391 && y > 4759 && y < 4863;
    }

	@JsonIgnore
	public boolean inClanWarsSafe() {
        return x > 3263 && x < 3390 && y > 4735 && y < 4761;
    }

	@JsonIgnore
	public boolean inEdgeville() {
		return (x > 3040 && x < 3200 && y > 3460 && y < 3519);
	}

	@JsonIgnore
	public boolean inDuelArena() {
        return (x > 3322 && x < 3394 && y > 3195 && y < 3291)
                || (x > 3311 && x < 3323 && y > 3223 && y < 3248);
    }

	@JsonIgnore
	public boolean inGodwars() {
		return Boundary.isIn(this, Godwars.GODWARS_AREA);
	}

	@JsonIgnore
	public boolean inRevs() {
		return (getX() > 3143 && getX() < 3262 && getY() > 10053 && getY() < 10231);
	}

	@JsonIgnore
	public boolean inMulti() {
		// Define boundaries that return true
		Boundary[] trueBoundaries = {
				Boundary.Castle_Wars,
				Boundary.SPIDER_CAVE,
				XamphurInstance.XAMPHUR_ZONE,
				new Boundary(3264, 10176, 3327, 10239),
				Boundary.BOUNTY_HUNTER_OUTLAST,
				Boundary.BALD_EAGLE,
				Boundary.COLOSSEUM,
				Boundary.BABA_ZONE,
				Boundary.CHAOTIC_ZONE,
				Boundary.HOT_DROP,
				Boundary.WHISPERER_BOUNDARY,
				Boundary.VARDORVIS,
				Boundary.PERK_BOSS,
				Boundary.UNICOW_AREA,
				Boundary.ZULRAH,
				Boundary.ABYSSAL_SIRE,
				Boundary.CORPOREAL_BEAST_LAIR,
				Boundary.KRAKEN_CAVE,
				Boundary.SCORPIA_LAIR,
				Boundary.AOEInstance,
				Boundary.INFERNO,
				Boundary.SKOTIZO_BOSSROOM,
				Boundary.LIZARDMAN_CANYON,
				Boundary.BANDIT_CAMP_BOUNDARY,
				Boundary.COMBAT_DUMMY,
				Boundary.TEKTON,
				Boundary.SKELETAL_MYSTICS,
				Boundary.RAIDS,
				Boundary.OLM,
				Boundary.ICE_DEMON,
				Boundary.CATACOMBS,
				Boundary.DZ_BOSS,
				NightmareConstants.BOUNDARY,
				Boundary.OURIANA_ALTAR,
				Boundary.BRYOPHYTA_ROOM,
				Boundary.CANNON_FREMNIK_DUNGEON,
				Boundary.CRYSTAL_CAVE_AREA,
				Boundary.NEX,
				Boundary.DONATOR_ZONE_BLOODY,
				Boundary.DONATOR_ZONE_BOSS,
				Boundary.VOTE_BOSS,
				Boundary.XERIC,
				Boundary.STAFF_ZONE,
				Boundary.GROOT_BOSS,
				Boundary.KALPHITE_QUEEN,
				Boundary.SARACHNIS_LAIR,
				Boundary.MIMIC_LAIR,
				Boundary.GROTESQUE_LAIR,
				ArbograveConstants.ARBO_1st_ROOM,
				ArbograveConstants.ARBO_2nd_ROOM,
				ArbograveConstants.ARBO_3rd_ROOM,
				ArbograveConstants.ARBO_4th_ROOM,
				ArbograveConstants.ARBO_5th_ROOM,
			    Boundary.SUPER_DZ
		};


		if (Arrays.stream(Boundary.CERBERUS_BOSSROOMS).anyMatch(cerberusBossroom -> Boundary.isIn(this, cerberusBossroom))) {
			return true;
		}

		if (Arrays.stream(trueBoundaries).anyMatch(boundary -> Boundary.isIn(this, boundary))) {
			return true;
		}

		// Define boundaries with specific coordinates that return true
		Boundary[] specificBoundaries = {
				new Boundary(2752, 4224, 2815, 4351),
				new Boundary(2112, 5504, 2175, 5567),
				new Boundary(3392, 10176, 3455, 10239),
				new Boundary(1942, 3735, 1964, 3757), // New instancing area
				new Boundary(1373, 3816, 1391, 3831), // Shar boundary
				new Boundary(1413, 3726, 1459, 3689),
				new Boundary(1472, 3729, 1505, 3689),
				new Boundary(1495, 3719, 1522, 3683),
				new Boundary(1522, 3712, 1534, 3680),
				new Boundary(3328, 10048, 3455, 10175),
				new Boundary(3712, 3968, 3775, 4031),
				new Boundary(3712, 3904, 3775, 4031),
				new Boundary(2432, 10112, 2495, 10174),
				new Boundary(2496, 10112, 2559, 10174),
				new Boundary(2304, 3648, 2367, 3711)
		};

		// Check if in any of the specific boundaries
		for (Boundary boundary : specificBoundaries) {
			if (Boundary.isIn(this, boundary)) {
				return true;
			}
		}

		// Additional checks
		if (Boundary.isIn(this, SuperiorTaskInstance.boundary) ||
				Boundary.isIn(this, DonoSlayerInstances.boundary) ||
				(Christmas.isChristmas() && Boundary.isIn(this, XMAS_ZONES)) ||
				Arrays.stream(TobConstants.ALL_BOUNDARIES).anyMatch(boundary -> Boundary.isIn(this, boundary)) ||
				inRevs() ||
				(Durial321.alive && (RegionProvider.getGlobal().get(this.getX(), this.getY()).id() == 11828 ||
						RegionProvider.getGlobal().get(this.getX(), this.getY()).id() == 11829))) {
			return true;
		}

		// Coordinate checks
		int absX = getX();
		int absY = getY();
		return (absX >= 3136 && absX <= 3327 && absY >= 3519 && absY <= 3607) ||
				(absX >= 3190 && absX <= 3327 && absY >= 3648 && absY <= 3839) ||
				(absX >= 3200 && absX <= 3390 && absY >= 3840 && absY <= 3967) ||
				(absX >= 2992 && absX <= 3007 && absY >= 3912 && absY <= 3967) ||
				(absX >= 2946 && absX <= 2959 && absY >= 3816 && absY <= 3831) ||
				(absX >= 3008 && absX <= 3199 && absY >= 3856 && absY <= 3903) ||
				(absX >= 2824 && absX <= 2944 && absY >= 5258 && absY <= 5369) ||
				(absX >= 3008 && absX <= 3071 && absY >= 3600 && absY <= 3711) ||
				(absX >= 3072 && absX <= 3327 && absY >= 3608 && absY <= 3647) ||
				(absX >= 2624 && absX <= 2690 && absY >= 2550 && absY <= 2619) ||
				(absX >= 2371 && absX <= 2422 && absY >= 5062 && absY <= 5117) ||
				(absX >= 2896 && absX <= 2927 && absY >= 3595 && absY <= 3630) ||
				(absX >= 2892 && absX <= 2932 && absY >= 4435 && absY <= 4464) ||
				(absX >= 2256 && absX <= 2287 && absY >= 4680 && absY <= 4711) ||
				(absX >= 2962 && absX <= 3006 && absY >= 3621 && absY <= 3659) ||
				(absX >= 3155 && absX <= 3214 && absY >= 3755 && absY <= 3803) ||
				(absX >= 1889 && absX <= 1912 && absY >= 4396 && absY <= 4413) ||
				(absX >= 3717 && absX <= 3772 && absY >= 5765 && absY <= 5820) ||
				(absX >= 3341 && absX <= 3378 && absY >= 4760 && absY <= 4853) ||
				(absX >= 2377 && absX <= 2435 && absY >= 9411 && absY <= 9470);
	}


	public List<Position> getTiles(int size) {
		List<Position> positionList = new ArrayList<>();
		int index = 0;
		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < NPCClipping.SIZES[i].length; k++) {
				int x3 = getX() + NPCClipping.SIZES[i][k][0];
				int y3 = getY() + NPCClipping.SIZES[i][k][1];
				positionList.add(new Position(x3, y3, getHeight()));
				index++;
			}
		}
		return positionList;
	}

	public double distanceTo(Position other) {
		return Math.sqrt(Math.pow(this.x - other.x, 2) +
				Math.pow(this.y - other.y, 2) +
				Math.pow(this.height - other.height, 2));
	}
}
