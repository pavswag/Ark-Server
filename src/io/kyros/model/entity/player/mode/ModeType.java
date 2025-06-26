package io.kyros.model.entity.player.mode;

import io.kyros.content.skills.Skill;

public enum ModeType {
	STANDARD,
	IRON_MAN,
	ULTIMATE_IRON_MAN,
	OSRS,
	OSRS_IRONMAN,
	HC_IRON_MAN,
	ROGUE,
	ROGUE_HARDCORE_IRONMAN,
	ROGUE_IRONMAN,
	GROUP_IRONMAN,
	WILDYMAN,
	HARDCORE_WILDYMAN,
	EVENT_MAN;
	;

	public double getExperienceRate(Skill skill) {
		switch (this) {
			case STANDARD:
			case IRON_MAN:
			case ULTIMATE_IRON_MAN:
			case HC_IRON_MAN:
			case HARDCORE_WILDYMAN:
			case WILDYMAN:
				return skill.getExperienceRate();
			case OSRS:
			case OSRS_IRONMAN:
				return 1d;
			case ROGUE:
			case ROGUE_HARDCORE_IRONMAN:
			case ROGUE_IRONMAN:
				return 5d;
			case GROUP_IRONMAN:
				return 10d;
			default:
				throw new IllegalStateException("No xp rate defined for " + toString());
		}
	}
	public boolean isStandardRate(Skill skill) {
		switch (this) {
			case STANDARD:
			case IRON_MAN:
			case ULTIMATE_IRON_MAN:
			case HC_IRON_MAN:
			case GROUP_IRONMAN:
				return true;
			case ROGUE:
			case ROGUE_HARDCORE_IRONMAN:
			case ROGUE_IRONMAN:
				return false;
			default:
				return false;
		}
	}
	public String getFormattedName() {
		switch (this) {
			case STANDARD:
				return "Standard";
			case IRON_MAN:
				return "Ironman";
			case ULTIMATE_IRON_MAN:
				return "Ultimate Ironman";
			case HC_IRON_MAN:
				return "Hardcore Ironman";
			case GROUP_IRONMAN:
				return "Group Ironman";
			case WILDYMAN:
				return "Wildyman";
			case HARDCORE_WILDYMAN:
				return "Hardcore Wildyman";
			default:
				throw new IllegalStateException("No format option for: " + this);
		}
	}
}
