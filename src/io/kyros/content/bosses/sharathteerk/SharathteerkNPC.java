package io.kyros.content.bosses.sharathteerk;

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

public class SharathteerkNPC extends NPC {

    public SharathteerkNPC(int npcId, Position position) {
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
                        .setHitDelay(1)
                        .setMinHit(40)
                        .setMaxHit(65)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                        .setAnimation(new Animation(10738))//magic attack
                        .setAttackDelay(6)
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(5)
                        .setMultiAttack(true)
                        .setHitDelay(4)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                        .setAnimation(new Animation(10739))//SPECIAL ANIMATION
                        .setMinHit(40)
                        .setMaxHit(65)
                        .setAttackDelay(4)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(2685).setCurve(0).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(85, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                        })
                        .setOnHit(attack -> {
                            attack.getVictim().asPlayer().startGraphic(new Graphic(2686, Graphic.GraphicHeight.HIGH));//85 if fail 140 is hit
                        })
                        .createNPCAutoAttack()
        ));
    }

    @Override
    public boolean hasBlockAnimation() {
        return false; //Stops flinching
    }
}
