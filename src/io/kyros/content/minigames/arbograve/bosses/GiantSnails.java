package io.kyros.content.minigames.arbograve.bosses;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.arbograve.ArbograveBoss;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.entity.player.Position;

import java.util.function.Function;

public class GiantSnails extends ArbograveBoss {
    public GiantSnails(Position position, InstancedArea instancedArea) {
        super(5630, new Position(position.getX(), position.getY(), instancedArea.getHeight()), instancedArea);
        revokeWalkingPrivilege = false;
        walkingType = 1;
        facePosition(1685, 4269);
        setAttacks();
    }

    @Override
    public void process() {
        setAttacks();
        super.process();
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(3723))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(1)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setHitDelay(2)
                        .setPoisonDamage(4)
                        .setMaxHit(36)
                        .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                            @Override
                            public Double apply(NPCCombatAttack npcCombatAttack) {
                                return 0.35;
                            }
                        })
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setAttackDelay(4)
                        /*.setProjectile(new ProjectileBaseBuilder().setProjectileId(156).setCurve(16).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {
                            if (attack.getCombatHit().getDamage() > 0) {
                                Player target = attack.getVictim().asPlayer();
                                target.startGraphic(new Graphic(78, Graphic.GraphicHeight.LOW));//85 if fail 140 is hit
                            } else {
                                attack.getVictim().startGraphic(new Graphic(85, Graphic.GraphicHeight.MIDDLE));//85 if fail 140 is hit
                            }
                        })*/
                        .createNPCAutoAttack()

        ));
    }
}
