package io.kyros.content.hotdrops;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.broadcasts.Broadcast;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;

import java.util.HashMap;
import java.util.stream.Collectors;

public class HotDrops {

    public static NPC npc = null;
    public static int DropNPCID = 1;
    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    public static final Boundary BOUNDARY = Boundary.HOT_DROP;
    private static long Timer = 0;

    private static boolean canHotDrop() {
        return npc == null;
    }

    public static void handleHotDrop(int npcID, boolean donationSpawn) {
        if (!canHotDrop()) {
            Timer = 0;
            reset();
            announce("[HOTDROP] The HotDrop Event has ended!");
            NPCHandler.despawn(7221, 0);
            return;
        }

        npc = NPCSpawning.spawnNpc(7221, 2908, 5086, 0, 1, 52);
        npc.getBehaviour().setRespawn(true);
        npc.getBehaviour().setAggressive(true);
        npc.getBehaviour().setRunnable(true);
        npc.getHealth().setMaximumHealth(500000);
        npc.getHealth().setCurrentHealth(500000);
        handleAutoAttacks(npc);
        if (donationSpawn) {
            Timer = 3000;
            DropNPCID = npcID;
            announce("[HOTDROP] A HotDrop boss spawned, you have 30 minutes of kills!, use ::HotDrop");
            Discord.writeBugMessage("[HOTDROP] A HotDrop boss spawned, you have 30 minutes of kills!, use ::HotDrop <@&1251546883965583360>");
            handleTimer();
        } else {
            Timer = 6000;
            DropNPCID = npcID;
            announce("[HOTDROP] A HotDrop boss spawned, you have 1 hour of kills!, use ::HotDrop");
            Discord.writeBugMessage("[HOTDROP] A HotDrop boss spawned, you have 1 hour of kills!, use ::HotDrop <@&1251546883965583360>");
            handleTimer();
        }
    }

    private static void handleTimer() {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (Timer > 0) {
                    Timer--;
                }

                if (Timer <= 0) {
                    announce("[HOTDROP] The HotDrop Event has ended!");
                    Timer = 0;
                    NPCHandler.despawn(7221, 0);
                    reset();
                    container.stop();
                }
            }
        }, 1);
    }

    public static void handleAutoAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(10692))//Attack Animation
                        .setCombatType(CombatType.MELEE)//Attack Style
                        .setMultiAttack(true)
                        .setSelectPlayersForMultiAttack(npcCombatAttack ->
                                Server.getPlayers().stream().filter(plr -> plr.distance(npcCombatAttack.getVictim().getPosition()) <= 15).collect(Collectors.toList()))
                        .setDistanceRequiredForAttack(3)//Distance npc is from the player when attacking
                        .setMaxHit(3)//Max Damage
                        .setAccuracyBonus(npcCombatAttack -> 10.0)//Accuracy boost
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.50)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setDistanceRequiredForAttack(14)
                        .setAttackDelay(4)//delay between attacks
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(10689))//Attack Animation
                        .setSelectAutoAttack(npcCombatAttack -> Misc.trueRand(5) == 0)
                        .setCombatType(CombatType.MELEE)//Attack Style
                        .setAttackDamagesPlayer(false)
                        .setOnAttack(npcCombatAttack -> {
                            int heal = Misc.random((npcCombatAttack.getNpc().getHealth().getCurrentHealth() / 20), (npcCombatAttack.getNpc().getHealth().getMaximumHealth() / 20));
                            heal = Math.max(heal, 50);
                            npcCombatAttack.getNpc().forceChat("Ooo a piece of cheese!");
//                            npcCombatAttack.getNpc().appendHeal(heal, HitMask.NPC_HEAL);
                        })
                        .setDistanceRequiredForAttack(14)
                        .setAttackDelay(5)
                        .createNPCAutoAttack()
        ));
    }

    public static void announce(String message) {
        new Broadcast(message).addTeleport(new Position(2908, 5086, 0)).copyMessageToChatbox().submit();
    }

    public static void handleGlobalHotDrop() {
        if (!damageCount.isEmpty()) {
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
                            Discord.writeServerSyncMessage("[HOT DROP] " + player.getDisplayName() + " has tried to take more than 2 account's there!");
                        }
                    }
                }
            }

            map.values().removeIf(integer -> integer > 1);

            damageCount.forEach((player, integer) -> {
                if (map.containsKey(player.getUUID())) {
                    int amountOfDrops = 1;
                    if (NPCDeath.isDoubleDrops()) {
                        amountOfDrops++;
                    }
                    Pass.addExperience(player, 3);
                    Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, DropNPCID);
                    player.getNpcDeathTracker().add(npc.getName(), npc.getDefinition().getCombatLevel(), 1);
                }
            });

            damageCount.clear();
            //reset();
        }
    }

    private static void reset() {
        if (npc != null) {
            if (npc.getIndex() > 0) {
                npc.unregister();
            }
            npc = null;
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }



}
