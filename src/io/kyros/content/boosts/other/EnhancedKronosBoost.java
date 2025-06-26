package io.kyros.content.boosts.other;

import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class EnhancedKronosBoost  extends GenericBoost {
    @Override
    public String getDescription() {
        return "x2 Raid Keys (" + Misc.cyclesToDottedTime((int) Hespori.ENHANCED_KRONOS_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.ENHANCED_KRONOS_TIMER > 0;
    }
}
