package io.kyros.content.bosses.hespori;

import io.kyros.content.QuestTab;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class GolparBonus implements HesporiBonus {
    @Override
    public void activate(Player player) {
        Hespori.activeGolparSeed = true;
        Hespori.GOLPAR_TIMER += TimeUnit.HOURS.toMillis(1) / 600;
//        Discord.writeBugMessage("The Consecration has sprouted and is granting 1 hour of 2x bonus loot including: Crystal keys, Coin bags, Resource boxes and Clues! <@&1121099960339927040>");
//        PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@] @red@" + player.getDisplayNameFormatted() + " @bla@planted a Golpar seed which" +
//                " granted @red@1 hour of 2x bonus loot including:");
//        PlayerHandler.executeGlobalMessage("@red@                   Crystal keys, Coin bags, Resource boxes and Clues!");
        QuestTab.updateAllQuestTabs();
    }


    @Override
    public void deactivate() {
        updateObject(false);
        Hespori.activeGolparSeed = false;
        Hespori.GOLPAR_TIMER = 0;

    }

    @Override
    public boolean canPlant(Player player) {

        return true;
    }

    @Override
    public HesporiBonusPlant getPlant() {
        return HesporiBonusPlant.GOLPAR;
    }
}
