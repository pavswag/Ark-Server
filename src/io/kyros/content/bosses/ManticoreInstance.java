package io.kyros.content.bosses;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.definitions.NpcStats;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.stats.NpcBonus;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.stream.Collectors;

public class ManticoreInstance extends LegacySoloPlayerInstance {

    public static Boundary boundary = new Boundary(1856, 5440, 1919, 5503); // Use this when doing things like "Multi"

    public ManticoreInstance(Player player, Boundary boundary) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }


    public void enter(Player player, ManticoreInstance manticoreInstance) {

        if (InstancedArea.isPlayerInSameInstanceType(player, this.getClass())) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }

        NPC manticore = NPCSpawning.spawnNpc(12818, 1886, 5473, manticoreInstance.getHeight(),1,30);
        manticore.getBehaviour().setRespawn(true);//Don't respawn
        manticore.getBehaviour().setAggressive(true);//Attack anyone
        manticore.getCombatDefinition().setAggressive(true);//Attack anyone
        manticore.getHealth().setMaximumHealth(12500);
        manticore.getHealth().setCurrentHealth(12500);
        manticore.setMultiAttackDistance(15);//Set's Attack Distance
        manticore.setNpcStats(NPCSpawning.getStats(12500, 5000, 20000));

        manticoreInstance.add(manticore);

        setupAutoAttack(manticore);

        player.moveTo(new Position(1887, 5464, manticoreInstance.getHeight()));
        manticoreInstance.add(player);
    }


    private static void setupAutoAttack(NPC npc) {//Single Target
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()//THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                        .setAnimation(new Animation(10869))//Attack Animation
                        .setCombatType(CombatType.MELEE)//Attack Style
                        .setDistanceRequiredForAttack(30)//Distance npc is from the player when attacking
                        .setMaxHit(52)//Max Damage
                        .setAccuracyBonus(npcCombatAttack -> 20.0)//Accuracy boost
                        .setIgnoreProjectileClipping(false)
                        .setAttackDamagesPlayer(true)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.30)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setPoisonDamage(20)//Poison
                        .setAttackDelay(2)//delay between attacks
                        .createNPCAutoAttack()
        ));
    }

    private static void setupAutoAttackMulti(NPC npc) {//Multi Target
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(10869))//Attack Animation
                        .setCombatType(CombatType.MELEE)//Attack Style
                        .setMultiAttack(true)
                        .setIgnoreProjectileClipping(false)
                        .setSelectPlayersForMultiAttack(npcCombatAttack ->
                                npcCombatAttack.getNpc().getInstance().getPlayers().stream().filter(plr -> plr.distance(npcCombatAttack.getVictim().getPosition()) <= 3)//Handle multi combat distance
                                        .collect(Collectors.toList()))
                        .setDistanceRequiredForAttack(3)//Distance npc is from the player when attacking
                        .setMaxHit(36)//Max Damage
                        .setAccuracyBonus(npcCombatAttack -> 10.0)//Accuracy boost
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setPoisonDamage(4)//Poison
                        .setAttackDelay(4)//delay between attacks
                        .createNPCAutoAttack()
        ));
    }

    private static void setupAutoAttackCombo(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()//Default attack
                        .setSelectPlayersForMultiAttack(npcCombatAttack ->
                                npcCombatAttack.getNpc().getInstance().getPlayers().stream().filter(plr -> plr.distance(npcCombatAttack.getVictim().getPosition()) <= 3)//Handle multi combat distance
                                        .collect(Collectors.toList()))
                        .setMultiAttack(true)
                        .setAnimation(new Animation(10869))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(3)
                        .setMaxHit(52)
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)
                        .setPoisonDamage(15)
                        .setAttackDelay(3)
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()//chance based attack
                        .setSelectPlayersForMultiAttack(npcCombatAttack ->
                                npcCombatAttack.getNpc().getInstance().getPlayers().stream().filter(plr -> plr.distance(npcCombatAttack.getVictim().getPosition()) <= 3)//Handle multi combat distance
                                        .collect(Collectors.toList()))
                        .setMultiAttack(true)
                        .setSelectAutoAttack(attack -> Misc.trueRand(10) == 0)
                        .setAnimation(new Animation(10869))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(3)
                        .setMaxHit(52)
                        .setAccuracyBonus(npcCombatAttack -> 15.0)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)
                        .setPoisonDamage(15)
                        .setAttackDelay(3)
                        .createNPCAutoAttack()
        ));
    }

    @Override
    public void onDispose() {

    }
}