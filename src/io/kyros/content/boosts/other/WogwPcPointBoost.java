package io.kyros.content.boosts.other;

import io.kyros.content.wogw.Wogw;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class WogwPcPointBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "+5 PC Points (" + Misc.cyclesToDottedTime((int) Wogw.PC_POINTS_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Wogw.PC_POINTS_TIMER > 0;
    }
}
