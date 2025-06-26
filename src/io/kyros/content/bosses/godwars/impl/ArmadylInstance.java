package io.kyros.content.bosses.godwars.impl;

import io.kyros.Server;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;

public class ArmadylInstance extends LegacySoloPlayerInstance {

    public ArmadylInstance(Player player, Boundary boundary) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }

    public static void enter(Player player, ArmadylInstance instance) {

        if (InstancedArea.isPlayerInSameInstanceType(player, ArmadylInstance.class)) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }
        try {
            instance.add(player);
            Server.getNpcs().forEachFiltered(npc -> Boundary.isIn(npc, Boundary.ARMADYL_GODWARS) && !instance.getNpcs().contains(npc) && npc.getHeight() == 2 && npc.getInstance() == null,
                    npc -> {
                        int maxhit = new NPCHandler().getMaxHit(player, npc);
                        NPC arma = NPCSpawning.spawnNpc(instance, npc.getNpcId(), npc.getX(), npc.getY(), instance.getHeight()+2, 1, maxhit);
                        instance.add(arma);
                    });

            player.getPA().movePlayer(2839, 5296, instance.getHeight()+2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}