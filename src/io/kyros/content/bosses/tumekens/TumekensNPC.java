package io.kyros.content.bosses.tumekens;

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

public class TumekensNPC extends NPC {

    public TumekensNPC(int npcId, Position position) {
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
                        .setHitDelay(6)
                        .setMinHit(50)
                        .setMaxHit(75)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                        .setAnimation(new Animation(9660))
                        .setAttackDelay(6)
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(5)
                        .setMultiAttack(true)
                        .setHitDelay(6)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                        .setAnimation(new Animation(9661))//SPECIAL ANIMATION
                        .setMinHit(20)
                        .setMaxHit(40)
                        .setAttackDelay(4)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(2445).setCurve(0).setSpeed(50).setSendDelay(2).createProjectileBase())
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(85, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                        })
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(2446, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                        })
                        .createNPCAutoAttack()
        ));
    }

    @Override
    public boolean hasBlockAnimation() {
        return false; //Stops flinching
    }
}
