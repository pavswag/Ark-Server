package io.kyros.content.boosts.xp;

import io.kyros.content.boosts.BoostType;
import io.kyros.content.boosts.Booster;
import io.kyros.content.boosts.PlayerSkillWrapper;

public abstract class ExperienceBooster implements Booster<PlayerSkillWrapper> {

    @Override
    public BoostType getType() {
        return BoostType.EXPERIENCE;
    }

}
