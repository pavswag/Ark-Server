package io.kyros.content.boosts.other;

import io.kyros.content.boosts.BoostType;
import io.kyros.content.boosts.Booster;
import io.kyros.model.entity.player.Player;

public abstract class GenericBoost implements Booster<Player> {
    @Override
    public BoostType getType() {
        return BoostType.GENERIC;
    }
}
