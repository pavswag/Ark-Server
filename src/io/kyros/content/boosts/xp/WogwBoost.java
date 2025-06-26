package io.kyros.content.boosts.xp;

import io.kyros.content.boosts.PlayerSkillWrapper;
import io.kyros.content.wogw.Wogw;
import io.kyros.util.Misc;

public class WogwBoost extends ExperienceBooster {
    @Override
    public String getDescription() {
        return "+50% XP Rate (" + Misc.cyclesToDottedTime((int) Wogw.EXPERIENCE_TIMER) + ")";
    }

    @Override
    public boolean applied(PlayerSkillWrapper playerSkillWrapper) {
        return Wogw.EXPERIENCE_TIMER > 0;
    }
}
