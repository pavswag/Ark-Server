package io.kyros.content.donor;

import io.kyros.Server;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.broadcasts.Broadcast;
import io.kyros.util.Location3D;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PremiumDonoMinotaur {

    private static final int common_key = 28416; //90%
    private static final int uncommon_key = 28417; //60%
    private static final int rare_key = 28418; //30%
    private static final int very_rare_key = 28419; //15%
    private static final int extremely_rare_key = 28420; //5%
    private static final int insanely_rare_key = 28421; // 1%
    //3163 9758 0
    //12812
    private static long Timer = 0;
    private static boolean alive = false;
    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    private static NPC MinotaurBoss;

    public static void Tick() {
        if (!alive && Timer < System.currentTimeMillis()) {
            alive = true;
            Timer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
            spawn();

            for (Player player : Server.getPlayers().toPlayerArray()) {
                if (player == null)
                    continue;

                if (player.storeDonated >= 500) {
                    Broadcast broadcast = new Broadcast("@bla@[@red@Minotaur@bla@] @red@Premium Donor Boss is alive!, head to the Premium donor zone to kill him!").addTeleport(new Position(2420, 3821, 0));
                    player.sendMessage(broadcast.getMessage());
                    player.getPA().sendBroadCast(broadcast);
                }
            }
        }
    }

    private static void spawn() {
        MinotaurBoss = NPCSpawning.spawnNpc(12812, 3163, 9758, 0, 1, 30);
        MinotaurBoss.getBehaviour().setAggressive(true);
        MinotaurBoss.getCombatDefinition().setAggressive(true);
        MinotaurBoss.needRespawn = false;
        MinotaurBoss.getBehaviour().setRespawn(false);
    }

    public static void handleRewards() {
        damageCount.forEach((player, integer) -> {
            if (player != null) {
                if (integer > 1) {
                    int amountOfDrops = 2;
                    if (NPCDeath.isDoubleDrops()) {
                        amountOfDrops++;
                    }
                    Pass.addExperience(player, 10);
                    Server.getDropManager().create(player, MinotaurBoss, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 12812);
                }
            }
        });
        despawn();
    }

    public static void despawn() {
        alive = false;
        if (MinotaurBoss != null) {
            if (MinotaurBoss.getIndex() > 0) {
                MinotaurBoss.unregister();
            }
            MinotaurBoss = null;
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }
}