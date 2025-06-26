package io.kyros.content.boosts.other;

import io.kyros.Configuration;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class DoubleDropsBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "+100% Loot Rate (" + Misc.cyclesToDottedTime((int) Configuration.DOUBLE_DROPS_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Configuration.DOUBLE_DROPS_TIMER > 0;
    }
}
