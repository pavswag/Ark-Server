package io.kyros.content.bosses;

import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.stats.NpcBonus;
import io.kyros.model.entity.npc.stats.NpcCombatDefinition;
import io.kyros.model.entity.npc.stats.NpcCombatSkill;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class ChaoticClone extends LegacySoloPlayerInstance {

    public static final Boundary boundary = new Boundary(2507, 9285, 2535, 9309);
    private static final Position spawn = new Position(2530, 9294,0);

    public ChaoticClone(Player player) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }

    public void enter(Player player, ChaoticClone intance) {

        if (InstancedArea.isPlayerInSameInstanceType(player, this.getClass())) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }
        try {
            player.ChaoticInstanceKills = Misc.random(100,350);

            player.sendErrorMessage("You've instanced Chaotic Death Spawn for " + player.ChaoticInstanceKills + " Kills!");
            int minX = 2513;
            int minY = 9290;
            int maxX = 2529;
            int maxY = 9303;
            int availableWidth = maxX - minX;
            int availableHeight = maxY - minY;

            int maxNpcsWidth = availableWidth / (1 + 1);
            int maxNpcsHeight = availableHeight / (1 + 1);
            int maxNpcs = maxNpcsWidth * maxNpcsHeight;

            for (int i = 0; i < maxNpcs; i++) {
                int x = minX + (i % maxNpcsWidth) * (1 + 1);
                int y = minY + (i / maxNpcsWidth) * (1 + 1);

                NPC slayer_npc = NPCSpawning.spawnNpc(player, 7649, x, y, intance.getHeight(),0,0,false,false);
                slayer_npc.spawnedBy = player.getIndex();
                slayer_npc.getBehaviour().setRespawn(true);
                slayer_npc.getBehaviour().setRespawnWhenPlayerOwned(true);
                slayer_npc.getBehaviour().setAggressive(false);
                slayer_npc.getCombatDefinition().setAggressive(false);



                slayer_npc.getHealth().setMaximumHealth(7500);
                slayer_npc.getHealth().setCurrentHealth(7500);

                NpcCombatDefinition combatDef = slayer_npc.getCombatDefinition();

                combatDef.setDefenceBonus(NpcBonus.RANGE_BONUS, 100);
                combatDef.setDefenceBonus(NpcBonus.MAGIC_BONUS, 100);
                combatDef.setLevel(NpcCombatSkill.DEFENCE, 350);
                intance.add(slayer_npc);
            }

            player.moveTo(new Position(spawn.getX(), spawn.getY(), intance.getHeight()));
            intance.add(player);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
