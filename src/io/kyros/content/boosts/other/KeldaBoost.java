package io.kyros.content.boosts.other;

import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.Calendar;

public class KeldaBoost extends GenericBoost {

    @Override
    public String getDescription() {
        return "x2 Larren's Keys (" + (Hespori.KELDA_TIMER > 0 ? Misc.cyclesToDottedTime((int) Hespori.KELDA_TIMER) : "Monday Bonus") + ")";
    }

    @Override
    public boolean applied(Player player) {
        return Hespori.KELDA_TIMER > 0 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
    }
}
