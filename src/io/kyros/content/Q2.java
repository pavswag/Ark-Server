package io.kyros.content;

import io.kyros.Server;
import io.kyros.content.achievement.inter.AchieveV2;
import io.kyros.content.combat.stats.MonsterKillLog;
import io.kyros.content.item.lootable.LootableInterface;
import io.kyros.content.playerinformation.Interface;
import io.kyros.model.entity.player.Player;

public class Q2 {

    public static boolean Open(Player player, int buttonId) {
        switch (buttonId) {
            case 10282:
                player.getCollectionLog().openInterface(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10283:
                Server.getDropManager().openDefault(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10284:
                LootableInterface.openInterface(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10285:
                MonsterKillLog.openInterface(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10286:
                Interface.Open(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
                return true;
            case 10287:
                AchieveV2.Open(player);
                player.getQuesting().handleHelpTabActionButton(buttonId);
            return true;
        }
        return false;
    }
}
