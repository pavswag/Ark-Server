package io.kyros.content.minigames.shadow_crusade.bosses;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeBoss;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.ProjectileBaseBuilder;
import io.kyros.model.entity.player.Position;

import java.util.function.Function;

public class EliteMage extends ShadowcrusadeBoss {

    public EliteMage(int npcId, Position position, InstancedArea instancedArea) {
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
                        .setAnimation(new Animation(711))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(17)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setHitDelay(2)
                        .setPoisonDamage(4)
                        .setEndGraphic(new Graphic(677))
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(676).setSendDelay(2).createProjectileBase())
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
