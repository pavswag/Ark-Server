package io.kyros.content.minigames.arbograve.bosses;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.arbograve.ArbograveBoss;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.ProjectileBaseBuilder;
import io.kyros.model.entity.player.Position;

import java.util.function.Function;

public class TarMonster extends ArbograveBoss {
    public TarMonster(Position position, InstancedArea instancedArea) {
        super(7804, new Position(position.getX(), position.getY(), instancedArea.getHeight()), instancedArea);
        facePosition(1711, 4253);
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
                        .setAnimation(new Animation(7678))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(17)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setMaxHit(52)
                        .setAccuracyBonus(npcCombatAttack -> 15.0)
                        .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                            @Override
                            public Double apply(NPCCombatAttack npcCombatAttack) {
                                return 0.70;
                            }
                        })
                        .setAttackDelay(3)
                        .setPoisonDamage(30)
                        .setProjectile(new ProjectileBaseBuilder()
                                .setSendDelay(1)
                                .setProjectileId(1735)
                                .setCurve(0)
                                .setStartHeight(0)
                                .setEndHeight(0)
                                .createProjectileBase())
                        .setEndGraphic(new Graphic(1736))
                        .createNPCAutoAttack()

        ));
    }
}
