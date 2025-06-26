package io.kyros.content.boosts.other;

import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class CelastrusBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "x2 Brimstone Keys (" + Misc.cyclesToDottedTime((int) Hespori.CELASTRUS_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.CELASTRUS_TIMER > 0;
    }
}
