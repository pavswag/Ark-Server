package io.kyros.content.bosses;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.CombatType;
import io.kyros.model.ProjectileBaseBuilder;
import io.kyros.model.definitions.NpcStats;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.stats.NpcBonus;
import io.kyros.model.entity.npc.stats.NpcCombatDefinition;
import io.kyros.model.entity.npc.stats.NpcCombatSkill;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;

public class ChaoticDeathSpawn extends LegacySoloPlayerInstance {

    public static final Boundary BOUNDARY = Boundary.CHAOTIC_ZONE;

    public ChaoticDeathSpawn(Player player) {
        super(player, BOUNDARY);
    }

    public void enter(Player player, ChaoticDeathSpawn intance) {

        if (InstancedArea.isPlayerInSameInstanceType(player, this.getClass())) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }
        NPC baba = NPCSpawning.spawnNpc(7649, 3864, 4383, intance.getHeight(), 0, 50);
        baba.getBehaviour().setRespawn(true); // Don't respawn
        baba.getBehaviour().setAggressive(true); // Attack anyone
        baba.getCombatDefinition().setAggressive(true); // Attack anyone
        baba.setMultiAttackDistance(30); // Set attack distance
        baba.getHealth().setMaximumHealth(7500);
        baba.getHealth().setCurrentHealth(7500);

        NpcCombatDefinition combatDef = baba.getCombatDefinition();

        combatDef.setDefenceBonus(NpcBonus.RANGE_BONUS, 100);
        combatDef.setDefenceBonus(NpcBonus.MAGIC_BONUS, 100);
        combatDef.setLevel(NpcCombatSkill.DEFENCE, 350);

        intance.add(baba);
        setupAutoAttack(baba);

        player.moveTo(new Position(3880, 4385,  intance.getHeight()));
        intance.add(player);
        player.getPA().closeAllWindows();
    }

    public static void setupAutoAttack(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()// THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                        .setCombatType(CombatType.MAGE)// Attack Style
                        .setDistanceRequiredForAttack(40)// Distance npc is from the player when attacking
                        .setMaxHit(50)// Max Damage
                        .setIgnoreProjectileClipping(false)
                        .setAccuracyBonus(npcCombatAttack -> 50.0)// Accuracy boost
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.1)// Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(396).setCurve(0).setStartHeight(43).setEndHeight(31).setSendDelay(3).setSpeed(51).createProjectileBase())
                        .setAttackDelay(6)// Delay between attacks
                        .setHitDelay(4)
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()// THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                        .setCombatType(CombatType.RANGE)// Attack Style
                        .setDistanceRequiredForAttack(40)// Distance npc is from the player when attacking
                        .setMaxHit(50)// Max Damage
                        .setIgnoreProjectileClipping(false)
                        .setAccuracyBonus(npcCombatAttack -> 50.0)// Accuracy boost
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.1)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(394).setCurve(0).setStartHeight(43).setEndHeight(31).setSendDelay(3).setSpeed(51).createProjectileBase())
                        .setAttackDelay(6)// Delay between attacks
                        .setHitDelay(4)
                        .createNPCAutoAttack()
        ));
    }
}
