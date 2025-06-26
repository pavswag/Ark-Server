package io.kyros.content.donor;

import io.kyros.Server;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 19/02/2024
 */
public class DonoSlayerInstances extends LegacySoloPlayerInstance {

    public static final Boundary boundary = new Boundary(1856, 4992, 1919, 5055);
    private static final Position spawn = new Position(1886, 5016);

    public DonoSlayerInstances(Player player, Boundary boundary) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }

    public static void enter(Player player, DonoSlayerInstances instances) {
        if (player.getSlayer().getTask().isEmpty()) {
            player.sendMessage("You need a slayer task before you can use this area!");
            return;
        }

        try {
            final int NPCID = getNpcid(player);

            if (NPCID == 0) {
                player.sendMessage("Unfortunately you don't have a task which is allowed here.");
                return;
            }

            int instanceSize = player.getSlayer().getTaskAmount();

            // Calculate available space within the boundary
            int minX = 1879;
            int minY = 5015;
            int maxX = 1895;
            int maxY = 5036;
            int availableWidth = maxX - minX;
            int availableHeight = maxY - minY;

            // Calculate the maximum number of NPCs that can fit based on NPC size and spacing
            int maxNpcsWidth = availableWidth / (1 + NpcDef.forId(NPCID).getSize());
            int maxNpcsHeight = availableHeight / (1 + NpcDef.forId(NPCID).getSize());
            int maxNpcs = maxNpcsWidth * maxNpcsHeight;
            // Spawn NPCs within the boundary with appropriate spacing
            for (int i = 0; i < maxNpcs; i++) {
                int x = minX + (i % maxNpcsWidth) * (1 + NpcDef.forId(NPCID).getSize());
                int y = minY + (i / maxNpcsWidth) * (1 + NpcDef.forId(NPCID).getSize());

                // Create and spawn NPC with appropriate size
                NPC slayer_npc = NPCSpawning.spawnNpc(player, NPCID, x,y,instances.getHeight(),0,0,false,false);
                slayer_npc.spawnedBy = player.getIndex();
                slayer_npc.getBehaviour().setRespawn(true);
                slayer_npc.getBehaviour().setRespawnWhenPlayerOwned(true);
                instances.add(slayer_npc);
            }

            player.moveTo(new Position(spawn.getX(), spawn.getY(), instances.getHeight()));
            instances.add(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getNpcid(Player player) {
        int NPCID = 0;

        for(NPC npc : Server.getNpcs().toNpcArray()) {
            if (npc == null)
                continue;
            if (npc.getDefinition() == null)
                continue;
            if (npc.getPosition().inWild())
                continue;
            if (npc.getDefinition().getName().replaceAll("_", " ").equalsIgnoreCase(player.getSlayer().getTask().get().getFormattedName()) ||
                    npc.getDefinition().getName().replaceAll("_", " ").equalsIgnoreCase(player.getSlayer().getTask().get().getPrimaryName())) {
                NPCID = npc.getNpcId();
                break;
            }
        }

        String[] Blocked_names = {"zulrah", "vorkath", "sarachnis", "cerberus", "alchemical hydra", "nightmare"};

        for (String blockedName : Blocked_names) {
            if (player.getSlayer().getTask().get().getPrimaryName().toLowerCase().equalsIgnoreCase(blockedName)
                    || player.getSlayer().getTask().get().getFormattedName().toLowerCase().equalsIgnoreCase(blockedName)) {
                NPCID = 0;
            }
        }

        return NPCID;
    }
}
