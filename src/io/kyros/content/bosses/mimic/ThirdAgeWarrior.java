package io.kyros.content.bosses.mimic;

import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;

import java.util.function.Function;

public class ThirdAgeWarrior implements Function<MimicNpc, NPCAutoAttack> {

    @Override
    public NPCAutoAttack apply(MimicNpc nightmare) {
        return new NPCAutoAttackBuilder()
                .setAnimation(new Animation(390))
                .setCombatType(CombatType.MELEE)
                .setMaxHit(18)
                .setHitDelay(2)
                .setAttackDelay(4)
                .setDistanceRequiredForAttack(1)
                .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                    @Override
                    public Double apply(NPCCombatAttack npcCombatAttack) {
                        return 0.2d;
                    }
                })
                .createNPCAutoAttack();
    }
}