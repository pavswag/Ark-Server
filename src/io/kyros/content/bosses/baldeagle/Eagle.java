package io.kyros.content.bosses.baldeagle;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.message.MessageBuilder;
import io.kyros.model.entity.player.message.MessageColor;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Eagle {

    private static final int NPC_ID = 13003;
    public static NPC eag = null;
    private static long timer = 0;
    public static HashMap<Player, Integer> damageCount = new HashMap<>();

    public static void tick() {
/*        if (eag == null && timer < System.currentTimeMillis()) {
            NPC eagle = NPCSpawning.spawnNpc(NPC_ID, 3194, 3437, 0, 1, 52);

            eagle.getBehaviour().setRespawn(false);
            eagle.getBehaviour().setAggressive(false);
            eagle.getCombatDefinition().setAggressive(false);
            eagle.getHealth().setMaximumHealth(85000);
            eagle.getHealth().setCurrentHealth(85000);
            eagle.getBehaviour().setWalkHome(true);


            eag = eagle;
            timer = (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(45));
            MessageBuilder builder = new MessageBuilder()
                    .color(MessageColor.RED)
                    .text("[BALD EAGLE]")
                    .color(MessageColor.BLUE)
                    .text(" has just spawned!")
                    .color(MessageColor.WHITE)
                    .text(" @ Varrock West Bank!");
            PlayerHandler.executeGlobalMessage(builder.build());
        }*/
    }

    public static void handleAutoAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(10948))//Attack Animation
                        .setCombatType(CombatType.RANGE)//Attack Style
                        .setMultiAttack(true)
                        .setSelectPlayersForMultiAttack(npcCombatAttack ->
                                Server.getPlayers().nonNullStream().filter(plr -> !plr.isIdle && plr.distance(npcCombatAttack.getVictim().getPosition()) <= 15).collect(Collectors.toList()))
                        .setDistanceRequiredForAttack(3)//Distance npc is from the player when attacking
                        .setMaxHit(32)//Max Damage
                        .setAccuracyBonus(npcCombatAttack -> 10.0)//Accuracy boost
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.50)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setDistanceRequiredForAttack(14)
                        .setAttackDelay(3)//delay between attacks
                        .createNPCAutoAttack()
        ));
        if (eag != npc) {
            eag = npc;
        }
    }

    public static void handleGlobalDrop() {
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
                            Discord.writeServerSyncMessage("[Bald Eagle] " + player.getDisplayName() + " has tried to take more than 2 account's there!");
                        }
                    }
                }
            }

            map.values().removeIf(integer -> integer > 1);

            damageCount.forEach((player, integer) -> {
                if (integer > 1 && map.containsKey(player.getUUID())) {
                    int amountOfDrops = 1;
                    if (NPCDeath.isDoubleDrops()) {
                        amountOfDrops++;
                    }
                    Pass.addExperience(player, 3);
                    Server.getDropManager().create(player, eag, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, NPC_ID);
                    player.getNpcDeathTracker().add(eag.getName(), eag.getDefinition().getCombatLevel(), 1);
                }
            });
            reset();
        }
    }

    private static void reset() {
        if (eag != null) {
            if (eag.getIndex() > 0) {
                eag.unregister();
            }
            eag = null;
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }

}
