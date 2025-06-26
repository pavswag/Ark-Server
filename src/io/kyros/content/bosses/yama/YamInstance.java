package io.kyros.content.bosses.yama;

import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstanceConfigurationBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class YamInstance extends LegacySoloPlayerInstance {

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    public YamInstance(Player player) {
        super(CONFIGURATION, player, Boundary.NEW_INSTANCE_AREA);
    }

    public void enter(Player player, YamInstance instance) {

        if (InstancedArea.isPlayerInSameInstanceType(player, this.getClass())) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }
        try {
            player.YamInstanceKills = Misc.random(100,350);

            player.sendErrorMessage("You've instanced Judge Of Yama for " + player.YamInstanceKills + " Kills!");
            int minX = 1944;
            int minY = 3738;
            int maxX = 1962;
            int maxY = 3755;
            int availableWidth = maxX - minX;
            int availableHeight = maxY - minY;

            int maxNpcsWidth = availableWidth / (1 + NpcDef.forId(10936).getSize());
            int maxNpcsHeight = availableHeight / (1 + NpcDef.forId(10936).getSize());
            int maxNpcs = maxNpcsWidth * maxNpcsHeight;

            for (int i = 0; i < maxNpcs; i++) {
                int x = minX + (i % maxNpcsWidth) * (1 + NpcDef.forId(10936).getSize());
                int y = minY + (i / maxNpcsWidth) * (1 + NpcDef.forId(10936).getSize());

                NPC slayer_npc = NPCSpawning.spawnNpc(player, 10936, x, y, instance.getHeight(),0,0,false,false);
                slayer_npc.spawnedBy = player.getIndex();
                slayer_npc.getBehaviour().setRespawn(true);
                slayer_npc.getBehaviour().setRespawnWhenPlayerOwned(true);
                slayer_npc.getBehaviour().setAggressive(true);
                slayer_npc.getCombatDefinition().setAggressive(true);
                slayer_npc.getHealth().setMaximumHealth(10000);
                slayer_npc.getHealth().setCurrentHealth(10000);
                instance.add(slayer_npc);
            }

            player.moveTo(new Position(1953, 3736, instance.getHeight()));
            instance.add(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
