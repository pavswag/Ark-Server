package io.kyros.content.bosses.hespori;

import io.kyros.content.QuestTab;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class EnhancedKronosBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeEnhancedKronosSeed = true;
        Hespori.ENHANCED_KRONOS_TIMER += TimeUnit.HOURS.toMillis(2) / 600;
        Discord.writeBugMessage("The Enhanced Kronos has sprouted and is granting 2 hour of double Raid Keys!! <@&1121099590507188367>");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted() + " @bla@planted a Kronos seed which" +
                " granted @red@2 hours of double Raid keys!");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeEnhancedKronosSeed = false;
        Hespori.ENHANCED_KRONOS_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {
        return Hespori.KRONOS_TIMER <= 0 && Hespori.ENHANCED_KRONOS_TIMER <= 0;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.ENHANCEDKRONOS;
    }
}
