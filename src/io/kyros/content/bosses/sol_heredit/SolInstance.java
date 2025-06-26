package io.kyros.content.bosses.sol_heredit;

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

public class SolInstance extends LegacySoloPlayerInstance {

    public static final Boundary boundary = new Boundary(1879, 5015, 1893, 5035);
    private static final Position spawn = new Position(1886, 5016,0);

    public SolInstance(Player player) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }

    public void enter(Player player, SolInstance intance) {

        if (InstancedArea.isPlayerInSameInstanceType(player, this.getClass())) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }
        try {
            player.SolInstanceKills = Misc.random(100,350);

            player.sendErrorMessage("You've instanced Sol Heredit for " + player.SolInstanceKills + " Kills!");
            int minX = 1880;
            int minY = 5016;
            int maxX = 1892;
            int maxY = 5034;
            int availableWidth = maxX - minX;
            int availableHeight = maxY - minY;

            int maxNpcsWidth = availableWidth / (1 + NpcDef.forId(12821).getSize());
            int maxNpcsHeight = availableHeight / (1 + NpcDef.forId(12821).getSize());
            int maxNpcs = maxNpcsWidth * maxNpcsHeight;

            for (int i = 0; i < maxNpcs; i++) {
                int x = minX + (i % maxNpcsWidth) * (1 + NpcDef.forId(12821).getSize());
                int y = minY + (i / maxNpcsWidth) * (1 + NpcDef.forId(12821).getSize());

                NPC slayer_npc = NPCSpawning.spawnNpc(player, 12821, x, y, intance.getHeight(),0,0,false,false);
                slayer_npc.spawnedBy = player.getIndex();
                slayer_npc.getBehaviour().setRespawn(true);
                slayer_npc.getBehaviour().setRespawnWhenPlayerOwned(true);
                slayer_npc.getBehaviour().setAggressive(false);
                slayer_npc.getCombatDefinition().setAggressive(false);
                slayer_npc.getHealth().setMaximumHealth(50000);
                slayer_npc.getHealth().setCurrentHealth(50000);
                intance.add(slayer_npc);
            }

            player.moveTo(new Position(spawn.getX(), spawn.getY(), intance.getHeight()));
            intance.add(player);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
