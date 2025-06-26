package io.kyros.content.bosses.hespori;

import io.kyros.content.QuestTab;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class EnhancedIasorBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeEnhancedIasorSeed = true;
        Hespori.ENHANCED_IASOR_TIMER += TimeUnit.HOURS.toMillis(2) / 600;
        Discord.writeBugMessage("The Iasor has sprouted and is granting 2 hour of double drops! <@&1121099662171062302>");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @blu@" + player.getDisplayNameFormatted() + " @bla@sprouted the Iasor and it is granting 2 hr of double drops!");
        QuestTab.updateAllQuestTabs();
    }

    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeEnhancedIasorSeed = false;
        Hespori.ENHANCED_IASOR_TIMER = 0;
    }

    @Override
    public boolean canPlant(Player player) {
        return Hespori.IASOR_TIMER <= 0 && Hespori.ENHANCED_IASOR_TIMER <= 0;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.IASORBONUS;
    }
}
