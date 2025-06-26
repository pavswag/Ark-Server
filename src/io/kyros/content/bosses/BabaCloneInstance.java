package io.kyros.content.bosses;

import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class BabaCloneInstance extends LegacySoloPlayerInstance {

    public static final Boundary boundary = new Boundary(2507, 9285, 2535, 9309);
    private static final Position spawn = new Position(2530, 9294,0);

    public BabaCloneInstance(Player player) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }
    public void enter(Player player, BabaCloneInstance babaCloneInstance) {

        if (InstancedArea.isPlayerInSameInstanceType(player, this.getClass())) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }

        try {
            player.BaBaInstanceKills = Misc.random(100,350);
            player.sendErrorMessage("You've instanced BaBa for " + player.BaBaInstanceKills + " Kills!");
            int minX = 2513;
            int minY = 9290;
            int maxX = 2529;
            int maxY = 9303;
            int availableWidth = maxX - minX;
            int availableHeight = maxY - minY;

            // Calculate the maximum number of NPCs that can fit based on NPC size and spacing
            int maxNpcsWidth = availableWidth / (1 + NpcDef.forId(11775).getSize());
            int maxNpcsHeight = availableHeight / (1 + NpcDef.forId(11775).getSize());
            int maxNpcs = maxNpcsWidth * maxNpcsHeight;
            // Spawn NPCs within the boundary with appropriate spacing
            for (int i = 0; i < maxNpcs; i++) {
                int x = minX + (i % maxNpcsWidth) * (1 + NpcDef.forId(11775).getSize());
                int y = minY + (i / maxNpcsWidth) * (1 + NpcDef.forId(11775).getSize());

                // Create and spawn NPC with appropriate size
                NPC slayer_npc = NPCSpawning.spawnNpc(player, 11775, x, y, babaCloneInstance.getHeight(),0,0,false,false);
                slayer_npc.spawnedBy = player.getIndex();
                slayer_npc.getBehaviour().setRespawn(true);
                slayer_npc.getBehaviour().setRespawnWhenPlayerOwned(true);
                babaCloneInstance.add(slayer_npc);
            }

            player.moveTo(new Position(spawn.getX(), spawn.getY(), babaCloneInstance.getHeight()));
            babaCloneInstance.add(player);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
