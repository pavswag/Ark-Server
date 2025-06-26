package io.kyros.content.boosts.other;

import io.kyros.Configuration;
import io.kyros.model.entity.player.Player;

import java.util.Calendar;

public class DoublePCPoints extends GenericBoost {

    @Override
    public String getDescription() {
        return "2x PC Points";
    }

    @Override
    public boolean applied(Player player) {
        return Configuration.BONUS_PC || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
    }
}