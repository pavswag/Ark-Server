package io.kyros.content.globalboss;

import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosspoints.BossPoints;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Location3D;

import java.util.HashMap;

public class KBD {

    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    public static void handleDeath(NPC npc) {
        HashMap<String, Integer> map = new HashMap<>();
        damageCount.forEach((p, i) -> {
            if (map.containsKey(p.getUUID())) {
                map.put(p.getUUID(), map.get(p.getUUID()) + 1);
            } else {
                map.put(p.getUUID(), 1);
            }
        });

        map.values().removeIf(integer -> integer > 1);

        damageCount.forEach((player, integer) -> {
            if (integer > 100 && map.containsKey(player.getUUID())) {
                int amountOfDrops = 1;
                if (NPCDeath.isDoubleDrops()) {
                    amountOfDrops++;
                }
                Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, npc.getNpcId());
                Achievements.increase(player, AchievementType.SLAY_KBD, 1);

                int bossPoints = BossPoints.getPointsOnDeath(npc);
                BossPoints.addPoints(player, bossPoints, false);

                if (NpcDef.forId(npc.getNpcId()).getCombatLevel() >= 1) {
                    player.getNpcDeathTracker().add(NpcDef.forId(npc.getNpcId()).getName(), NpcDef.forId(npc.getNpcId()).getCombatLevel(), bossPoints);
                }
                Pass.addExperience(player, 1);
                PetHandler.rollOnNpcDeath(player, npc);
                player.getBossTimers().death(npc);
            }
        });
        reset();
    }

    private static void reset() {
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }
}
