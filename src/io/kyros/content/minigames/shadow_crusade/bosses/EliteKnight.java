package io.kyros.content.minigames.shadow_crusade.bosses;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeBoss;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.entity.player.Position;

import java.util.function.Function;

public class EliteKnight extends ShadowcrusadeBoss {

    public EliteKnight(int npcId, Position position, InstancedArea instancedArea) {
        super(npcId, position, instancedArea);
        revokeWalkingPrivilege = false;
        walkingType = 1;

        getHealth().setMaximumHealth(3500);
        getHealth().setCurrentHealth(3500);
    }

    @Override
    public void process() {
        setAttacks();
        super.process();
    }


    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(7045))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(1)
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
                        .createNPCAutoAttack()

        ));
    }
}
