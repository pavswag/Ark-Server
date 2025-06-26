package io.kyros.content.bosses.minotaur;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.ProjectileBaseBuilder;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class MinotaurNPC extends NPC {

    public MinotaurNPC(int npcId, Position position) {
        super(npcId, position);
        setAttacks(this);
    }

    public static void setAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(5)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.60)
                        .setMultiAttack(true)
                        .setHitDelay(2)
                        .setMinHit(5)
                        .setMaxHit(20)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)
                        .setIgnoreProjectileClipping(true)
                        .setAnimation(new Animation(10843))
                        .setAttackDelay(6)
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                        .setCombatType(CombatType.MAGE)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.60)
                        .setDistanceRequiredForAttack(5)
                        .setMultiAttack(true)
                        .setHitDelay(2)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)
                        .setAnimation(new Animation(10844))
                        .setIgnoreProjectileClipping(true)
                        .setMinHit(5)
                        .setMaxHit(20)
                        .setAttackDelay(2)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(-1).setCurve(0).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(2761, Graphic.GraphicHeight.HIGH));
                        })
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setSelectAutoAttack(attack -> Misc.trueRand(2) == 0)
                        .setCombatType(CombatType.RANGE)
                        .setDistanceRequiredForAttack(5)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.60)
                        .setMultiAttack(true)
                        .setHitDelay(4)
                        .setIgnoreProjectileClipping(true)
                        .setAttackDelay(2)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)
                        .setAnimation(new Animation(10844))
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(-1).setCurve(0).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(2264, Graphic.GraphicHeight.LOW));
                        })
                        .setMinHit(5)
                        .setMaxHit(20)
                        .createNPCAutoAttack()
        ));
    }

    @Override
    public boolean hasBlockAnimation() {
        return false; // Stops flinching
    }
}
