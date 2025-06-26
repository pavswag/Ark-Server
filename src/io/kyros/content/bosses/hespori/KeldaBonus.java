package io.kyros.content.bosses.hespori;

import io.kyros.content.QuestTab;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class KeldaBonus implements HesporiBonus {

    @Override
    public void activate(Player player) {
        Hespori.activeKeldaSeed = true;
        Hespori.KELDA_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
//        Discord.writeBugMessage("The Kelda has sprouted and is granting 1 hour of 2x Larren's keys! <@&1121099788465737819>");
//        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted() + " @bla@planted a Kelda seed which" +
//                " granted @red@1 hour of 2x Larren's keys.");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeKeldaSeed = false;
        Hespori.KELDA_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            return false;
        }
        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.KELDA;
    }
}
