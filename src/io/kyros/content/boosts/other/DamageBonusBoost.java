package io.kyros.content.boosts.other;

import io.kyros.Configuration;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class DamageBonusBoost extends GenericBoost {
    @Override
    public String getDescription() {
        return "+10% Damage Bonus (" + Misc.cyclesToDottedTime((int) Hespori.ENHANCED_DAMAGE_TIMER) + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.ENHANCED_DAMAGE_TIMER > 0;
    }
}
