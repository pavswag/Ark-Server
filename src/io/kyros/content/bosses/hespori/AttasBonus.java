package io.kyros.content.bosses.hespori;

import java.util.concurrent.TimeUnit;

import io.kyros.content.QuestTab;
import io.kyros.content.bonus.DoubleExperience;
import io.kyros.content.wogw.Wogw;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

public class AttasBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Wogw.EXPERIENCE_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
//        Discord.writeBugMessage("The Attas has sprouted and is granting 1 hours bonus xp! <@&1121099713995874345>");
//        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] The Attas has sprouted and is granting 1 hours bonus xp!");
        QuestTab.updateAllQuestTabs();
    }

    @Override
    public void deactivate() {
        Hespori.activeAttasSeed = false;
        Hespori.ATTAS_TIMER = 0;
        updateObject(false);
    }

    @Override
    public boolean canPlant(Player player) {
        if (DoubleExperience.isDoubleExperience()) {
            player.sendMessage("This seed can't be planted during bonus experience.");
            return false;
        }
        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.ATTAS;
    }
}
