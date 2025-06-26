package io.kyros.content.bosses.sarachnis;

import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.ProjectileBase;
import io.kyros.model.ProjectileBaseBuilder;

import java.util.function.Function;

public class SarachnisMinionMage implements Function<SarachnisNpc, NPCAutoAttack> {

    private static ProjectileBase projectile() {
        return new ProjectileBaseBuilder()
                .setSendDelay(2)
                .setSpeed(25)
                .setStartHeight(40)
                .setProjectileId(1682)
                .createProjectileBase();
    }

    @Override
    public NPCAutoAttack apply(SarachnisNpc nightmare) {
        return new NPCAutoAttackBuilder()
                .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                .setAnimation(new Animation(4410))
                .setCombatType(CombatType.MAGE)
                .setMaxHit(11)
                .setHitDelay(3)
                .setAttackDelay(4)
                .setDistanceRequiredForAttack(24)
                .setMultiAttack(false)
                .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                    @Override
                    public Double apply(NPCCombatAttack npcCombatAttack) {
                        return 0.3;
                    }
                })
                .setProjectile(projectile())
                .createNPCAutoAttack();
    }
}
