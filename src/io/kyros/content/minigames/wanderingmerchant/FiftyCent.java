package io.kyros.content.minigames.wanderingmerchant;

import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.collection_log.CollectionLog;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.seasons.Halloween;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.broadcasts.Broadcast;
import io.kyros.util.Location3D;
import io.kyros.util.discord.Discord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static io.kyros.model.Npcs.NEX;
import static io.kyros.model.Npcs.PHOSANIS_NIGHTMARE;

public class FiftyCent extends NPC {

    public static int ActivityPoints = 10000;

    public static boolean spawned;
    public static boolean alive;

    public static void handlePointIncrease(NPC npc, Player player) {
        if (alive || spawned) {
            return;
        }
        boolean thursday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY;
        int times = 1;

        if (thursday && Halloween.DoubleGroot) {
            times = 4;
        } else if (thursday || Halloween.DoubleGroot) {
            times = 2;
        }

        //Having lil' groot removes 2x activity requirement.
        if (CollectionLog.collectionNPCS.get(CollectionLog.CollectionTabType.WILDERNESS).contains(npc.getNpcId())) {
            ActivityPoints -= 5 * times;
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 10 * times;
            }
        } else if (npc.getNpcId() == NEX || npc.getNpcId() == PHOSANIS_NIGHTMARE || npc.getNpcId() == 9425) {
            ActivityPoints -= 3* times;
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 6* times;
            }
        } else if (npc.getNpcId() == 1101) {
            ActivityPoints -= 15* times; // arbograve
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 30* times;
            }
        } else if (npc.getNpcId() == 5169) {
            ActivityPoints -= 20* times; // durial
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 40* times;
            }
        } else if (npc.getNpcId() == 5126) {
            ActivityPoints -= 30* times; // vote
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 60* times;
            }
        } else if (npc.getNpcId() == 8096) {
            ActivityPoints -= 100 * times; // donor
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 200 * times;
            }
        } else if (CollectionLog.collectionNPCS.get(CollectionLog.CollectionTabType.BOSSES).contains(npc.getNpcId())) {
            ActivityPoints -= times; // all bosses in "bosses collectiong log"
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= times;
            }
        } else if (npc.getNpcId() == 3601) {
            ActivityPoints -= times; // Unicow's
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 2*times;
            }
        } else if (npc.getNpcId() == 8781 || npc.getNpcId() == 10531 || npc.getNpcId() == 10532) {
            ActivityPoints -= 10*times; // Unicow's
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 20*times;
            }
        } else {
            ActivityPoints -= times;
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= times;
            }
        }

        if (ActivityPoints <= 0) {
            SpawnBoss();
        }

    }

    public FiftyCent(int npcId) {
        super(npcId, new Position(3736, 3975, 0));
    }

    public static List<Player> targets = new ArrayList<>();
    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    public static final Boundary BOUNDARY = Boundary.VOTE_BOSS;
    private static NPC npc;

    public static void SpawnBoss() {
        ActivityPoints = 10000;
        npc = new FiftyCent(12784);
        npc.getBehaviour().setRespawn(false);
        npc.getBehaviour().setAggressive(true);
        npc.getBehaviour().setRunnable(true);
        npc.getHealth().setMaximumHealth(95000);
        npc.getHealth().reset();
        alive = true;
        spawned = true;
        announce();
        Discord.writeBugMessage("[FiftyCent] the Feather boss [FiftyCent] has spawned!, use ::vb or ::db <@&1282147201673461811>");
    }

    public static void announce() {
        new Broadcast("[FiftyCent] the Feather boss [FiftyCent] has spawned!, use ::vb or ::db").addTeleport(new Position(2974, 3405, 0)).copyMessageToChatbox().submit();
    }

    public static void handleRewards() {
        HashMap<String, Integer> map = new HashMap<>();
        damageCount.forEach((p, i) -> {
            if (map.containsKey(p.getUUID())) {
                map.put(p.getUUID(), map.get(p.getUUID()) + 1);
            } else {
                map.put(p.getUUID(), 1);
            }
        });

        for (String s : map.keySet()) {
            if (map.containsKey(s) && map.get(s) > 1) {
                for (Player player : Server.getPlayers().toPlayerArray()) {
                    if (player.getUUID().equalsIgnoreCase(s)) {
                        Discord.writeServerSyncMessage("[FiftyCent] "+player.getDisplayName() + " has tried to take more than 2 account's there!");
                    }
                }
            }
        }

        map.values().removeIf(integer -> integer > 1);

        damageCount.forEach((player, integer) -> {
            if (integer > 1 && map.containsKey(player.getUUID())) {
                int amountOfDrops = 2;
                if (NPCDeath.isDoubleDrops()) {
                    amountOfDrops++;
                }
                Pass.addExperience(player, 10);
                Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 12784);
                Achievements.increase(player, AchievementType.SLAY_LUKE, 1);
            }
        });
        PlayerHandler.executeGlobalMessage("@red@[FiftyCent]@blu@ the Feather boss [@red@FiftyCent@blu@] has been defeated!");

        if (!targets.isEmpty()) {
            targets.clear();
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
        if (npc != null) {
            npc = null;
        }
    }

    @Override
    public void onDeath() {
        alive = false;
        spawned = false;
    }

}
