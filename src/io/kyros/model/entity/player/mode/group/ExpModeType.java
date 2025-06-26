package io.kyros.model.entity.player.mode.group;

import io.kyros.content.skills.Skill;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 27/12/2023
 */
public enum ExpModeType {

    OneTimes,
    FiveTimes,
    TenTimes,
    TwentyFiveTimes;

    public double getExperienceRate(Skill skill) {
        switch (this) {
            case TwentyFiveTimes:
                return skill.getExperienceRate();
            case TenTimes:
                return 10d;
            case FiveTimes:
                return 5d;
            case OneTimes:
                return 1d;
            default:
                throw new IllegalStateException("No xp rate defined for " + toString());
        }
    }


    public String getFormattedName() {
        switch (this) {
            case OneTimes:
                return "1x";
            case FiveTimes:
                return "5x";
            case TenTimes:
                return "10x";
            case TwentyFiveTimes:
                return "25x/15x";
            default:
                throw new IllegalStateException("No format option for: " + this);
        }
    }

}
