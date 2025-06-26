package io.kyros.content.bosses.hespori;

import io.kyros.content.QuestTab;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class DamageBonus implements HesporiBonus {

    @Override
    public void activate(Player player) {
        Hespori.activeEnhancedDamageSeed = true;
        Hespori.ENHANCED_DAMAGE_TIMER += TimeUnit.HOURS.toMillis(2) / 600;
        Discord.writeBugMessage("The Enhanced Damage Seed has sprouted and is granting 2 hours, 10% Bonus Damage!");
        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] The Enhanced Damage Seed has sprouted and is granting 2 hours, 10% Bonus Damage!");
        QuestTab.updateAllQuestTabs();
    }

    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeEnhancedDamageSeed = false;
        Hespori.ENHANCED_DAMAGE_TIMER = 0;
    }

    @Override
    public boolean canPlant(Player player) {
        if (Hespori.activeEnhancedDamageSeed) {
            player.sendMessage("This seed can't be planted while a seed is active.");
            return false;
        }
        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.DAMAGEBOOST;
    }


}
