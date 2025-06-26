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

public class BandosInstance extends LegacySoloPlayerInstance {

    public BandosInstance(Player player, Boundary boundary) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }

    public static void enter(Player player, BandosInstance instance) {

        if (InstancedArea.isPlayerInSameInstanceType(player, BandosInstance.class)) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }
        try {
            instance.add(player);

            for(NPC npc : Server.getNpcs().toNpcArray()) {
                if (npc != null && Boundary.isIn(npc, Boundary.BANDOS_GODWARS) && !instance.getNpcs().contains(npc)  && npc.getHeight() == 2 && npc.getInstance() == null) {
                    int maxhit = new NPCHandler().getMaxHit(player, npc);
                    NPC bandos = NPCSpawning.spawnNpc(instance, npc.getNpcId(), npc.getX(), npc.getY(),
                            instance.getHeight()+2, 1, maxhit);
                    instance.add(bandos);
                }
            }

            player.moveTo(new Position(2864, 5354, instance.getHeight()+2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
