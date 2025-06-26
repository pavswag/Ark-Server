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
import io.kyros.model.entity.player.Position;

public class SaradominInstance extends LegacySoloPlayerInstance {

    public SaradominInstance(Player player, Boundary boundary) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }

    public static void enter(Player player, SaradominInstance instance) {

        if (InstancedArea.isPlayerInSameInstanceType(player, SaradominInstance.class)) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }
        try {
            instance.add(player);

            for(NPC npc : Server.getNpcs().toNpcArray()) {
                if (npc != null && Boundary.isIn(npc, Boundary.SARADOMIN_GODWARS) && !instance.getNpcs().contains(npc) && npc.getHeight() == 0 && npc.getInstance() == null) {
                    int maxhit = new NPCHandler().getMaxHit(player, npc);
                    NPC sara = NPCSpawning.spawnNpc(instance, npc.getNpcId(), npc.getX(), npc.getY(), instance.getHeight(), 1, maxhit);
                instance.add(sara);
                }
            }

            player.moveTo(new Position(2907, 5265, instance.getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}