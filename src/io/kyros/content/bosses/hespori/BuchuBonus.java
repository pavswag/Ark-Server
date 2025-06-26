package io.kyros.content.bosses.hespori;

import io.kyros.content.QuestTab;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class BuchuBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeBuchuSeed = true;
        Hespori.BUCHU_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
//        Discord.writeBugMessage("The Buchu has sprouted and is granting 1 hour of 2x Boss points! <@&1121099874377674858>");
//        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted() + " @bla@planted a Buchu seed which" +
//                " granted @red@1 hour of 2x Boss points.");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeBuchuSeed = false;
        Hespori.BUCHU_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {

        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.BUCHU;
    }
}
