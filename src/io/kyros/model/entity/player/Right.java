package io.kyros.model.entity.player;

import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.util.Misc;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The rights of a player determines their authority. Every right can be viewed with a name and a value. The value is used to separate each right from one another.
 *
 * @author Jason MacK
 * @date January 22, 2015, 5:23:49 PM
 */

public enum Right implements Comparator<Right> {
	PLAYER(0, "000000"),
	HELPER(11, "004080"),
	MODERATOR(1, "#DADADA", HELPER),
	ADMINISTRATOR(2, "F5FF0F", MODERATOR),
	STAFF_MANAGER(3, "4feb34", ADMINISTRATOR),
	UNKNOWN(4, "F5FF0F"),

	Donator(5, "1B1ABC"), // $20 - Purple
	Super_Donator(7, "118120", Donator), // $50 - Green
	Great_Donator(9, "6D0000", Super_Donator), // $100 - Red
	Extreme_Donator(17, "005C6D", Great_Donator), // $250 - blue
	Major_Donator(18, "4a4a4a", Extreme_Donator), // $500 - black
	Supreme_Donator(32, "e38400", Major_Donator), // $1250 - orange
	Gilded_Donator(8, "e3bd00", Supreme_Donator), // $2500 - Yellow
	Platinum_Donator(33, "ffffff", Gilded_Donator), // $4000 - White
	Apex_Donator(34, "fb00ff", Platinum_Donator), // $65000 - Transparent
	Almighty_Donator(35, "2fff00", Apex_Donator), // $15000 - Lime Green

	GUIDE_GURU(36, "4287f5", HELPER), //Guide Guru

	HITBOX(12, "437100"),
	IRONMAN(13, "3A3A3A"),
	ULTIMATE_IRONMAN(14, "717070"),
	YOUTUBER(15, "FE0018"),
	GAME_DEVELOPER(16, "eb9634", STAFF_MANAGER),
	OSRS(23, "437100"),
	OSRS_IRONMAN(31, "437100"),
	MEMBERSHIP(21, "437100"),
	ROGUE(25, "437100"),
	HC_IRONMAN(10, "60201f"),
	ROGUE_IRONMAN(26, "60201f"),
	ROGUE_HARDCORE_IRONMAN(27, "60201f"),
	GROUP_IRONMAN(28, "60201f"),
	EVENT_MAN(29, "60201f"),
	COMMUNITY_MANAGER(30, "27FA17", STAFF_MANAGER),
	WILDYMAN(93,"60201f"),
	HARDCORE_WILDYMAN(94, "60201f"),
	HEAD_ADMINISTRATOR(92, "fe7f89", STAFF_MANAGER);

	private static final Logger logger = LoggerFactory.getLogger(Right.class);

	/**
	 * Display groups. You can have one displayed right from the first group,
	 * the second group you can have as many until you reach 2 displayed groups.
	 */
	public static final EnumSet[] DISPLAY_GROUPS = {
			EnumSet.of(HELPER, GUIDE_GURU, MODERATOR, ADMINISTRATOR, COMMUNITY_MANAGER, HEAD_ADMINISTRATOR, GAME_DEVELOPER, STAFF_MANAGER, UNKNOWN, Donator, Super_Donator, Great_Donator, Extreme_Donator, Major_Donator, Supreme_Donator, Gilded_Donator,
					Platinum_Donator, Apex_Donator, Almighty_Donator, YOUTUBER),
			EnumSet.of(HITBOX, EVENT_MAN, IRONMAN, ULTIMATE_IRONMAN, OSRS,OSRS_IRONMAN, MEMBERSHIP, HC_IRONMAN, ROGUE,
					ROGUE_HARDCORE_IRONMAN, ROGUE_IRONMAN, GROUP_IRONMAN, WILDYMAN, HARDCORE_WILDYMAN)
	};

	/**
	 * Donator rights.
	 */
	public static final EnumSet<Right> DONATOR_SET = EnumSet.of(Donator, Super_Donator, Great_Donator, Extreme_Donator, Major_Donator, Supreme_Donator, Gilded_Donator,
			Platinum_Donator, Apex_Donator, Almighty_Donator);

	public static final EnumSet<Right> IRONMAN_SET = EnumSet.of(IRONMAN, ROGUE_HARDCORE_IRONMAN, HC_IRONMAN, ROGUE_IRONMAN, ULTIMATE_IRONMAN, GROUP_IRONMAN, OSRS_IRONMAN);

	/**
	 * An array of {@link Right} objects that represent the order in which some rights should be prioritized over others. The index at which a {@link Right} object exists
	 * determines it's priority. The lower the index the less priority that {@link Right} has over another. The list is ordered from lowest priority to highest priority.
	 * <p>
	 * An example of this would be comparing a {@link #MODERATOR} to a {@link #ADMINISTRATOR}. An {@link #ADMINISTRATOR} can be seen as more 'powerful' when compared to a
	 * {@link #MODERATOR} because they have more power within the community.
	 * </p>
	 */
	public static final Right[] PRIORITY = { PLAYER, WILDYMAN, HARDCORE_WILDYMAN, OSRS, OSRS_IRONMAN, EVENT_MAN, HC_IRONMAN, GROUP_IRONMAN, IRONMAN, ULTIMATE_IRONMAN,
			MEMBERSHIP, Donator, Super_Donator, Great_Donator, Extreme_Donator, Major_Donator, Supreme_Donator, Gilded_Donator,
			Platinum_Donator, Apex_Donator, Almighty_Donator, YOUTUBER, HITBOX, HELPER,
			GAME_DEVELOPER, MODERATOR, ADMINISTRATOR, COMMUNITY_MANAGER, STAFF_MANAGER, HEAD_ADMINISTRATOR, UNKNOWN};

	/**
	 * The level of rights that define this
	 */
	private final int right;

	/**
	 * The rights inherited by this right
	 */
	private final List<Right> inherited;

	/**
	 * The color associated with the right
     * -- GETTER --
     *  The color associated with the right
     *
     * @return the color

     */
	@Getter
    private final String color;

	/**
	 * Creates a new right with a value to differentiate it between the others
	 *
	 * @param right the right required
	 * @param color a color thats used to represent the players name when displayed
	 * @param inherited the right or rights inherited with this level of right
	 */
	Right(int right, String color, Right... inherited) {
		this.right = right;
		this.inherited = Arrays.asList(inherited);
		this.color = color;
	}

	public String getFormattedName() {
		return Misc.capitalizeEveryWord(name().toLowerCase().replace("_", " "));
	}

	public Mode getMode() {
		switch (this) {
			case IRONMAN:
				return Mode.forType(ModeType.IRON_MAN);
			case ULTIMATE_IRONMAN:
				return Mode.forType(ModeType.ULTIMATE_IRON_MAN);
			case HC_IRONMAN:
				return Mode.forType(ModeType.HC_IRON_MAN);
			case ROGUE_HARDCORE_IRONMAN:
				return Mode.forType(ModeType.ROGUE_HARDCORE_IRONMAN);
			case ROGUE_IRONMAN:
				return Mode.forType(ModeType.ROGUE_IRONMAN);
			case OSRS:
				return Mode.forType(ModeType.OSRS);
			case OSRS_IRONMAN:
				return Mode.forType(ModeType.OSRS_IRONMAN);
			case ROGUE:
				return Mode.forType(ModeType.ROGUE);
			case GROUP_IRONMAN:
				return Mode.forType(ModeType.GROUP_IRONMAN);
			case WILDYMAN:
				return Mode.forType(ModeType.WILDYMAN);
			case HARDCORE_WILDYMAN:
				return Mode.forType(ModeType.HARDCORE_WILDYMAN);
		}

		return Mode.forType(ModeType.STANDARD);
	}

	/**
	 * The rights of this enumeration
	 *
	 * @return the rights
	 */
	public int getValue() {
		return right;
	}

	/**
	 * Returns a {@link Right} object for the value.
	 *
	 * @param value the right level
	 * @return the rights object
	 */
	public static Right get(int value) {
		return RIGHTS.stream().filter(element -> element.right == value).findFirst().orElse(PLAYER);
	}

	/**
	 * A {@link Set} of all {@link Right} elements that cannot be directly modified.
	 */
	private static final Set<Right> RIGHTS = Collections.unmodifiableSet(EnumSet.allOf(Right.class));

    /**
	 * Determines if this level of rights inherited another level of rights
	 *
	 * @param right the level of rights we're looking to determine is inherited
	 * @return {@code true} if the rights are inherited, otherwise {@code false}
	 */
	public boolean isOrInherits(Right right) {
		/*if (this == right)
			return true;
		for (int i = 0; i < inherited.size(); i++) {
			if (this == inherited.get(i))
				return true;
		}
		System.out.println("inherited.size: "+inherited.size());
		return false;*/
		return this == right || !inherited.isEmpty() && inherited.stream().anyMatch(r -> r.isOrInherits(right));
	}

	/**
	 * Determines if the players rights equal that of {@link Right#MODERATOR}
	 * @return	true if they are of type moderator
	 */
	public boolean isModerator() {
		return equals(MODERATOR);
	}

	/**
	 * Determines if the players rights equal that of {@link Right#HELPER}
	 * @return	true if they are of type moderator
	 */
	public boolean isHelper() {
		return equals(HELPER) || equals(GUIDE_GURU);
	}

	/**
	 * Determines if the players rights equal that of {@link Right#ADMINISTRATOR}
	 * @return	true if they are of type administrator
	 */
	public boolean isAdministrator() {
		return equals(ADMINISTRATOR) || equals(COMMUNITY_MANAGER);
	}

	/**
	 * Determines if the players rights equal that of {@link Right#STAFF_MANAGER}
	 * @return	true if they are of type owner
	 */
	public boolean isOwner() {
		return equals(STAFF_MANAGER) || equals(HEAD_ADMINISTRATOR);
	}

	public boolean isDeveloper() {
		return equals(GAME_DEVELOPER);
	}

	/**
	 * Determines if the players right equal that of {@link Right#MODERATOR}, {@link Right#ADMINISTRATOR},
	 * and {@link Right#STAFF_MANAGER}
	 * @return	true if they are any of the predefined types
	 */
	public boolean isStaff() {
		return isHelper() || isModerator() || isAdministrator() || isOwner() || isDeveloper();
	}

	public boolean isManagement() {
		return isAdministrator() || isOwner()  || isDeveloper();
	}

	@Override
	public String toString() {
		return Misc.capitalizeJustFirst(name().replaceAll("_", " "));
	}

	@Override
	public int compare(Right arg0, Right arg1) {
		return 0;
	}

}
