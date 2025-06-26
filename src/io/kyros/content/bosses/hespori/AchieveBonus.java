package io.kyros.content.bosses.hespori;

import io.kyros.content.QuestTab;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class AchieveBonus implements HesporiBonus {

    @Override
    public void activate(Player player) {
        Hespori.activeAchieveSeed = true;
        Hespori.ACHIEVE_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
        Discord.writeBugMessage("The Hespori Seed has sprouted and is granting 1 hours, 2x achievement points! <@&1121100114652577882>");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] The Hespori Seed has sprouted and is granting 1 hours, 2x achievement points!");
        QuestTab.updateAllQuestTabs();
    }

    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeAchieveSeed = false;
        Hespori.ACHIEVE_TIMER = 0;
    }

    @Override
    public boolean canPlant(Player player) {
        if (Hespori.activeEnhancedAchieveSeed || Hespori.activeAchieveSeed) {
            player.sendMessage("This seed can't be planted during 2x Achievement points.");
            return false;
        }
        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.HESPORI;
    }


}
