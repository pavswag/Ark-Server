package io.kyros.content.bosses;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.bosspoints.BossPoints;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.Npcs;
import io.kyros.model.ProjectileBaseBuilder;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.ClientGameTimer;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PhantomBaBa extends LegacySoloPlayerInstance {

    public static final Boundary BOUNDARY = Boundary.BABA_ZONE;

    public PhantomBaBa(Player player) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, BOUNDARY);
    }

    public void enter(Player player, PhantomBaBa intance) {

        if (InstancedArea.isPlayerInSameInstanceType(player, this.getClass())) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }
        NPC baba = NPCSpawning.spawnNpc(11775, 2717, 5793, intance.getHeight(), 1, 45);
        baba.getBehaviour().setRespawn(true); // Don't respawn
        baba.getBehaviour().setAggressive(true); // Attack anyone
        baba.getCombatDefinition().setAggressive(true); // Attack anyone
        baba.setMultiAttackDistance(30); // Set attack distance
        baba.getHealth().setMaximumHealth(15000); // Set attack distance
//        baba.getCombatDefinition().setAttackSpeed(5);

        intance.add(baba);
//        setupAutoAttack(baba);

        player.moveTo(new Position(2726, 5788,  intance.getHeight()));
        intance.add(player);
    }

    public static void setupAutoAttack(NPC npc) {// Single Target
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()// THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                        .setSelectPlayersForMultiAttack(npcCombatAttack -> Server.getPlayers().nonNullStream().filter(plr -> npc.getInstance().getPlayers().contains(plr) &&
                                !plr.isIdle && !plr.isDead && Boundary.isIn(plr, BOUNDARY) && plr.getHeight() == npc.getHeight() && plr.distance(npc.getPosition()) <= 30).collect(Collectors.toList()))
                        .setMultiAttack(false)
                        .setAnimation(new Animation(9747))// Attack Animation
                        .setCombatType(CombatType.MELEE)// Attack Style
                        .setDistanceRequiredForAttack(35)// Distance npc is from the player when attacking
                        .setMinHit(15)
                        .setMaxHit(52)// Max Damage
                        .setIgnoreProjectileClipping(true)
                        .setAccuracyBonus(npcCombatAttack -> 50.0)// Accuracy boost
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.1)// Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setOnHit(npcCombatAttackHit -> {
                            if (npcCombatAttackHit.getCombatHit().isSuccess()) {
                                if (npcCombatAttackHit.getVictim() != null) {
                                    Player p = npcCombatAttackHit.getVictim().asPlayer();
                                    if (Misc.random(0, 50) == 1) {
                                        if (p.hasOverloadBoost) {
                                            p.getPotions().resetOverload();
                                            p.getPA().sendGameTimer(ClientGameTimer.OVERLOAD, TimeUnit.SECONDS, 1);
                                        }
                                    }
                                }
                            }
                        })
                        .setPoisonDamage(12)// Poison
                        .setAttackDelay(4)// Delay between attacks
                        .setHitDelay(3)
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()// THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                        .setSelectPlayersForMultiAttack(npcCombatAttack -> Server.getPlayers().nonNullStream().filter(plr -> npc.getInstance().getPlayers().contains(plr) && !plr.isIdle && !plr.isDead && plr.getHeight() == npc.getHeight() && Boundary.isIn(plr, BOUNDARY) && plr.distance(npc.getPosition()) <= 30).collect(Collectors.toList()))
                        .setMultiAttack(false)
                        .setAnimation(new Animation(9745))// Attack Animation
                        .setCombatType(CombatType.RANGE)// Attack Style
                        .setDistanceRequiredForAttack(35)// Distance npc is from the player when attacking
                        .setMinHit(15)
                        .setMaxHit(52)// Max Damage
                        .setIgnoreProjectileClipping(true)
                        .setAccuracyBonus(npcCombatAttack -> 50.0)// Accuracy boost
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.1)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(2242).setCurve(0).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(npcCombatAttackHit -> {
                            if (npcCombatAttackHit.getCombatHit().isSuccess()) {
                                if (npcCombatAttackHit.getVictim() != null) {
                                    Player p = npcCombatAttackHit.getVictim().asPlayer();
                                    if (Misc.random(0, 50) == 1) {
                                        if (p.hasOverloadBoost) {
                                            p.getPotions().resetOverload();
                                            p.getPA().sendGameTimer(ClientGameTimer.OVERLOAD, TimeUnit.SECONDS, 1);
                                        }
                                    }
                                }
                            }
                        })
                        .setPoisonDamage(12)// Poison
                        .setAttackDelay(4)// Delay between attacks
                        .setHitDelay(3)
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()// THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                        .setSelectPlayersForMultiAttack(npcCombatAttack -> Server.getPlayers().nonNullStream().filter(plr -> npc.getInstance().getPlayers().contains(plr) && !plr.isIdle && !plr.isDead && plr.getHeight() == npc.getHeight() && Boundary.isIn(plr, BOUNDARY) && plr.distance(npc.getPosition()) <= 30).collect(Collectors.toList()))
                        .setMultiAttack(false)
                        .setAnimation(new Animation(9748))// Attack Animation
                        .setCombatType(CombatType.MAGE)// Attack Style
                        .setDistanceRequiredForAttack(35)// Distance npc is from the player when attacking
                        .setMinHit(15)
                        .setMaxHit(52)// Max Damage
                        .setIgnoreProjectileClipping(true)
                        .setOnHit(npcCombatAttackHit -> {
                            if (npcCombatAttackHit.getCombatHit().isSuccess()) {
                                if (npcCombatAttackHit.getVictim() != null) {
                                    Player p = npcCombatAttackHit.getVictim().asPlayer();
                                    if (Misc.random(0, 50) == 1) {
                                        if (p.hasOverloadBoost) {
                                            p.getPotions().resetOverload();
                                            p.getPA().sendGameTimer(ClientGameTimer.OVERLOAD, TimeUnit.SECONDS, 1);
                                        }
                                    }
                                }
                            }
                        })
                        .setAccuracyBonus(npcCombatAttack -> 50.0)// Accuracy boost
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.1)// Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setPoisonDamage(12)// Poison
                        .setAttackDelay(4)// Delay between attacks
                        .setHitDelay(3)
                        .createNPCAutoAttack()
        ));
    }
}
