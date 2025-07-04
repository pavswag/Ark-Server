package io.kyros.model.entity.player.packets.npcoptions;

import io.kyros.Server;
import io.kyros.content.skills.herblore.HerbloreDecantCleanUnfNpc;
import io.kyros.content.skills.hunter.impling.Impling;
import io.kyros.content.skills.runecrafting.ouriana.OurianaBanker;
import io.kyros.model.Npcs;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class NpcOptions {

    /**
     * Handles all options.
     */
    public static void handle(Player player, NPC npc, int option) {
        if (Server.getMultiplayerSessionListener().inAnySession(player))
            return;

        player.clickNpcType = 0;
        player.clickedNpcIndex = player.npcClickIndex;
        player.npcClickIndex = 0;

        if (player.getQuesting().handleNpcClick(npc, option))
            return;

        if (OurianaBanker.clickNpc(player, npc, option))
            return;

        if (HerbloreDecantCleanUnfNpc.clickNpc(player, npc, option))
            return;

        if (Impling.isImp(npc.getNpcId())) {
            player.interruptActions(false, false, true);
            if (player.lastImpling > System.currentTimeMillis())
                return;
            Impling.catchImpling(player, npc);
            return;
        }

        switch(npc.getNpcId()) {
            case Npcs.BOSS_POINT_SHOP:
                if (option == 2) {
                    player.getShops().openShop(121);
                    player.sendMessage("You currently have " + Misc.insertCommas(player.bossPoints) + " Boss points.");
                }
                break;
        }
    }

}
