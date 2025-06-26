package io.kyros.content.bosses.hespori;

import io.kyros.content.QuestTab;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class CelastrusBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeCelastrusSeed = true;
        Hespori.CELASTRUS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
//        Discord.writeBugMessage("The Celastrus has sprouted and is granting 1 hour of 2x Brimstone keys! <@&1121099920011702313>");
//        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted()+ " @bla@planted a Celastrus seed which" +
//                " granted @red@1 hour of 2x Brimstone keys!");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeCelastrusSeed = false;
        Hespori.CELASTRUS_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {

        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.CELASTRUS;
    }
}
