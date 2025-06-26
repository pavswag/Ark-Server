package io.kyros.content.minigames.nightmarezone;

import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.Misc;

public class NMZBossing {

    public static Boundary NMZONE = new Boundary(2240, 4672, 2303, 4735);

    public static void handleUnlockOnKill(Player player, NPC npc) {
        if (player.NMZBosses.contains(npc.getNpcId())) {
            return;
        }

        if (Misc.trueRand(10000) == 1) {
            player.NMZBosses.add(npc.getNpcId());
            player.sendMessage("[@red@NMZ Bosses@bla@] @red@You have just unlocked " + npc.getDefinition().getName() + " for NMZ!");
            PlayerHandler.executeGlobalMessage("[@red@NMZ Bosses@bla@] @red@"+ player.getDisplayName() +" have just unlocked " + npc.getDefinition().getName() + " for NMZ!");
        }
    }

    public static void handleGoldenBossOnKill(Player player, NPC npc) {
        if (player.getInstance() != null && Boundary.isIn(player, NMZONE)) {
         if (Misc.trueRand(1000) == 1) {
             //Spawn Golden Boss
         }
        }
    }
}
