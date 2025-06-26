package io.kyros.content.bosses.yama;

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

public class YamaNPC extends NPC {

    public YamaNPC(int npcId, Position position) {
        super(npcId, position);
        setAttacks(this);
    }

    public static void setAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(5)
                        .setMultiAttack(true)
                        .setHitDelay(2)
                        .setMinHit(25)
                        .setMaxHit(61)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)
                        .setIgnoreProjectileClipping(true)
                        .setAnimation(new Animation(64))
                        .setAttackDelay(6)
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(5)
                        .setMultiAttack(true)
                        .setHitDelay(2)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)
                        .setAnimation(new Animation(69))
                        .setIgnoreProjectileClipping(true)
                        .setMinHit(35)
                        .setMaxHit(54)
                        .setAttackDelay(2)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(-1).setCurve(0).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(85, Graphic.GraphicHeight.HIGH));
                        })
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setSelectAutoAttack(attack -> Misc.trueRand(2) == 0)
                        .setCombatType(CombatType.RANGE)
                        .setDistanceRequiredForAttack(5)
                        .setMultiAttack(true)
                        .setHitDelay(2)
                        .setIgnoreProjectileClipping(true)
                        .setAttackDelay(2)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)
                        .setAnimation(new Animation(9043))
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(-1).setCurve(0).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(2723, Graphic.GraphicHeight.LOW));
                        })
                        .setMinHit(65)
                        .setMaxHit(65)
                        .createNPCAutoAttack()
        ));
    }

    @Override
    public boolean hasBlockAnimation() {
        return false; // Stops flinching
    }
}
