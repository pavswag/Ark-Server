package io.kyros.content.skills;

import io.kyros.Configuration;
import io.kyros.util.Misc;

import java.util.stream.Stream;

public enum Skill {
	ATTACK(0, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	DEFENCE(1, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	STRENGTH(2, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	HITPOINTS(3, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	RANGED(4, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	PRAYER(5, 15),
	MAGIC(6, Configuration.DEFAULT_COMBAT_EXPERIENCE_RATE),
	COOKING(7, 15),
	WOODCUTTING(8, 15),
	FLETCHING(9, 15),
	FISHING(10, 15),
	FIREMAKING(11, 15),
	CRAFTING(12, 15),
	SMITHING(13, 15),
	MINING(14, 15),
	HERBLORE(15, 15),
	AGILITY(16, 50),//TODO Expand with more way to do agility and then turn XP down.
	THIEVING(17, 15),
	SLAYER(18, 15),
	FARMING(19, 15),
	RUNECRAFTING(20, 15),
	HUNTER(21, 50),//TODO Expand with more way to do hunter and then turn XP down.
	DEMON_HUNTER(22, 15),
	FORTUNE(23, 15);

	public static final int MAX_EXP = 200_000_000;

	public static int iconForSkill(Skill skill) {

		if (skill == null) {
			return 0;
		}

        return switch (skill) {
            case ATTACK -> 0;
            case STRENGTH -> 1;
            case DEFENCE -> 2;
            case RANGED -> 3;
            case PRAYER -> 4;
            case MAGIC -> 5;
            case RUNECRAFTING -> 6;
            case HITPOINTS -> 7;
            case AGILITY -> 8;
            case HERBLORE -> 9;
            case THIEVING -> 10;
            case CRAFTING -> 11;
            case FLETCHING -> 12;
            case MINING -> 13;
            case SMITHING -> 14;
            case FISHING -> 15;
            case COOKING -> 16;
            case FIREMAKING -> 17;
            case WOODCUTTING -> 18;
            case SLAYER -> 19;
            case FARMING -> 20;
            case HUNTER -> 21;
            case DEMON_HUNTER -> 22;
            case FORTUNE -> 23;
            default -> 0;
        };
	}

	public static Skill forId(int id) {
		return Stream.of(values()).filter(s -> s.id == id).findFirst().orElse(null);
	}

	public static int getIconId(Skill skill) {
		switch (skill) {
			case ATTACK:
				return 134;
			case STRENGTH:
				return 135;
			case DEFENCE:
				return 136;
			case RANGED:
				return 137;
			case PRAYER:
				return 138;
			case MAGIC:
				return 139;
			case RUNECRAFTING:
				return 140;
			case HITPOINTS:
				return 141;
			case AGILITY:
				return 142;
			case HERBLORE:
				return 143;
			case THIEVING:
				return 144;
			case CRAFTING:
				return 145;
			case FLETCHING:
				return 146;
			case MINING:
				return 147;
			case SMITHING:
				return 148;
			case FISHING:
				return 14;
			case COOKING:
				return 150;
			case FIREMAKING:
				return 151;
			case WOODCUTTING:
				return 152;
			case SLAYER:
				return 153;
			case FARMING:
				return 154;
			case HUNTER:
				return 155;
			case DEMON_HUNTER:
				return 156;
			case FORTUNE:
				return 157;
			default:
				return -1;
		}
	}

	public static Skill[] getCombatSkills() {
		return Stream.of(values()).filter(skill -> skill.getId() <= 6).toArray(Skill[]::new);
	}

	public static boolean isCombatSkill(int skill) {
		return skill <= 6;
	}

	public static Skill[] getNonCombatSkills() {
		return Stream.of(values()).filter(skill -> skill.getId() > 6).toArray(Skill[]::new);
	}

	public static final int MAXIMUM_SKILL_ID = 23;

	public static Stream<Skill> stream() {
		return Stream.of(values());
	}

	public static int length() {
		return values().length;
	}

	private final int id;
	private final int experienceRate;

	Skill(int id) {
		this(id, 1);
	}

	Skill(int id, int experienceRate) {
		this.id = id;
		this.experienceRate = experienceRate;
	}

	public int getId() {
		return id;
	}

	public double getExperienceRate() {
		return experienceRate;
	}

	@Override
	public String toString() {
		String name = name().toLowerCase();
		return Misc.capitalize(name);
	}
}

