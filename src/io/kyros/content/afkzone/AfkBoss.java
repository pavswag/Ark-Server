package io.kyros.content.afkzone;

import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.*;
import io.kyros.model.entity.player.broadcasts.Broadcast;
import io.kyros.sql.dailytracker.TrackerType;
import io.kyros.util.Location3D;
import io.kyros.util.discord.Discord;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 30/01/2024
 */

public class AfkBoss {

    private static int Goblin = 655;

    public static int GoblinSpawnAmount = 0;

    public static boolean GoblinActive = false;
    public static NPC goblinBoss;

    public static ArrayList<String> IPAddress = new ArrayList<>();
    public static HashMap<Player, Long> CurrentUserDate = new HashMap<>();
    public static ArrayList<String> MACAddress = new ArrayList<>();
    private static LocalDateTime checkDate = LocalDateTime.now();
    public static HashMap<Player, Integer> damageCount = new HashMap<>();

    public static void spawnGoblin() {
        if (GoblinActive) {
            return;
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }

        goblinBoss = NPCSpawning.spawnNpc(Goblin, 2118, 5510, 0, 0, 0);
        goblinBoss.getBehaviour().setRespawn(false);
        goblinBoss.getBehaviour().setAggressive(true);
        goblinBoss.getBehaviour().setRunnable(true);
        goblinBoss.getHealth().setMaximumHealth(125000);
        goblinBoss.getHealth().reset();

        GoblinActive = true;

        announce();
        Discord.writeBugMessage("[Goblin] has spawned!, ::afk to access him!");
        TrackerType.AFK_BOSS.addTrackerData(1);
    }

    public static void announce() {
        new Broadcast("[Goblin] has spawned!, ::afk to access him!").addTeleport(new Position(2120, 5519, 0)).copyMessageToChatbox().submit();
    }

    public static void tick() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(checkDate, currentDateTime);

        // Check if the duration is more than 24 hours
        if (duration.toHours() >= 24) {
            // Reset data and update checkDate
            IPAddress.clear();
            MACAddress.clear();
            checkDate = currentDateTime;
        }
    }

    public static void handleGoblinTick() {
        tick();
        GoblinSpawnAmount += 1;

        if (GoblinSpawnAmount >= (5000 + (500 * Server.getPlayers().toPlayerArray().length))) {
            spawnGoblin();
            GoblinSpawnAmount = 0;
            handleAfkAccounts(false);
        }
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
                    Server.getDropManager().create(player, goblinBoss, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 655);
                    Achievements.increase(player, AchievementType.SLAY_AFK, 1);

                }
            }
        });
        despawn();
    }

    public static void despawn() {
        GoblinActive = false;
        if (goblinBoss != null) {
            if (goblinBoss.getIndex() > 0) {
                goblinBoss.unregister();
            }
            goblinBoss = null;
        }
        handleAfkAccounts(true);
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }

    public static int getGoblinSpawnAmount() {
        return (((5000 + (500 * Server.getPlayers().toPlayerArray().length)) - AfkBoss.GoblinSpawnAmount));
    }

    public static boolean hasVoted(Player player) {
        if (true) {
            return true;
        }

        return IPAddress.contains(player.getIpAddress()) || MACAddress.contains(player.getMacAddress()) || player.getRights().isOrInherits(Right.STAFF_MANAGER);
    }

    private static void handleAfkAccounts(boolean death) {
        if (!death) {
            for (Player player : Server.getPlayers().toPlayerArray()) {
                if (player == null)
                    continue;
                if (!Boundary.isIn(player, Boundary.AFK_ZONE))
                    continue;
                if (!hasVoted(player))
                    continue;
                if (player.afk_position == null)
                    continue;

                CycleEventHandler.getSingleton().stopEvents(player);
                player.afk_position = player.getPosition();
                player.moveTo(new Position(2120, 5514, 0));
                player.attackEntity(goblinBoss);
            }
        } else {
            for (Player player : Server.getPlayers().toPlayerArray()) {
                if (player == null)
                    continue;
                if (!Boundary.isIn(player, Boundary.AFK_ZONE_BOSS))
                    continue;
                if (!hasVoted(player))
                    continue;
                if (player.afk_position == null)
                    continue;

                player.moveTo(player.afk_position);
                Afk.Start(player, new Location3D(player.afk_obj_position.getX(), player.afk_obj_position.getY(), 0), player.afk_object);
            }
        }
    }
}
