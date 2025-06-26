package io.kyros.content.boosts.xp;

import io.kyros.content.boosts.PlayerSkillWrapper;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.util.Misc;

public class AttasBoost extends ExperienceBooster {
    @Override
    public String getDescription() {
        return "+50% XP (" + Misc.cyclesToDottedTime((int) Hespori.ATTAS_TIMER) + ")";
    }

    @Override
    public boolean applied(PlayerSkillWrapper playerSkillWrapper) {
        return Hespori.ATTAS_TIMER > 0;
    }
}
