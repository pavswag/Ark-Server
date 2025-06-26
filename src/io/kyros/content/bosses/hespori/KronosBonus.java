package io.kyros.content.bosses.hespori;

import io.kyros.content.QuestTab;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class KronosBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeKronosSeed = true;
        Hespori.KRONOS_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        Discord.writeBugMessage("The Kronos has sprouted and is granting 1 hour of double Raid keys! <@&1121099590507188367>");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted() + " @bla@planted a Kronos seed which" +
                " granted @red@1 hour of double Raid keys");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeKronosSeed = false;
        Hespori.KRONOS_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {
        return Hespori.KRONOS_TIMER <= 0 && Hespori.ENHANCED_KRONOS_TIMER <= 0;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.KRONOS;
    }
}
