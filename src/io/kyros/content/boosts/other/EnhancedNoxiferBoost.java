package io.kyros.content.boosts.other;

import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class EnhancedNoxiferBoost  extends GenericBoost {
    @Override
    public String getDescription() {
        return "x4 Slayer Points (" + Misc.cyclesToDottedTime((int) Hespori.ENHANCED_NOXIFER_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.ENHANCED_NOXIFER_TIMER > 0;
    }
}
