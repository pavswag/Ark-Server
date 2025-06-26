package io.kyros.content.bosses.hespori;

import io.kyros.content.QuestTab;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class EnhancedNoxifierBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeEnhancedNoxiferSeed = true;
        Hespori.ENHANCED_NOXIFER_TIMER += TimeUnit.HOURS.toMillis(2) / 600;
        Discord.writeBugMessage("The Enhanced Noxifer has sprouted and is granting 1 hour of 4x Slayer points! <@&1121099827506323666>");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted() + " @bla@planted a Noxifer seed which" +
                " granted @red@1 hour of 4x Slayer points.");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeEnhancedNoxiferSeed = false;
        Hespori.ENHANCED_NOXIFER_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {

        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.ENHANCEDNOXIFER;
    }
}
