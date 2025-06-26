package io.kyros.content.boosts.other;

import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class ConsecrationBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "+5 PC Points (" + Misc.cyclesToDottedTime((int) Hespori.CONSECRATION_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.CONSECRATION_TIMER > 0;
    }
}
