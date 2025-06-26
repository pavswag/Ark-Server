package io.kyros.content.boosts.xp;

import io.kyros.content.bonus.DoubleExperience;
import io.kyros.content.boosts.PlayerSkillWrapper;

public class BonusWeekendBoost extends ExperienceBooster {
    @Override
    public String getDescription() {
        return "+50% XP Bonus Weekend";
    }

    @Override
    public boolean applied(PlayerSkillWrapper playerSkillWrapper) {
        return DoubleExperience.isDoubleExperience();
    }
}
