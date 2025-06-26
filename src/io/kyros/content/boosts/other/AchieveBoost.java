package io.kyros.content.boosts.other;

import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class AchieveBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "x2 Achieve Progress (" + Misc.cyclesToDottedTime((int) Hespori.ACHIEVE_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.ACHIEVE_TIMER > 0;
    }
}

