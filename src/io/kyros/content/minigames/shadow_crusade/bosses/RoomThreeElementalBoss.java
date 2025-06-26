package io.kyros.content.minigames.shadow_crusade.bosses;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeBoss;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeRoom;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.ProjectileBaseBuilder;
import io.kyros.model.entity.player.Position;

import java.util.function.Function;

public class RoomThreeElementalBoss extends ShadowcrusadeBoss {

    public RoomThreeElementalBoss(int npcId, Position position, InstancedArea instancedArea) {
        super(13528, new Position(position.getX(), position.getY(), instancedArea.getHeight()), instancedArea);

        revokeWalkingPrivilege = false;
        walkingType = 1;
        facePosition(1685, 4269);
        setAttacks();

        int health = (getHealth().getCurrentHealth() / 3) * instancedArea.getPlayers().size();

        getHealth().setMaximumHealth(health);
        getHealth().setCurrentHealth(health);
    }


    @Override
    public void process() {
        setAttacks();
        super.process();
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(11348))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(17)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setHitDelay(2)
                        .setPoisonDamage(4)
                        .setMaxHit(36)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(2890).setStartHeight(45).setSendDelay(2).createProjectileBase())
                        .setEndGraphic(new Graphic(2892))
                        .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                            @Override
                            public Double apply(NPCCombatAttack npcCombatAttack) {
                                return 0.35;
                            }
                        })
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setAttackDelay(4)
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(11354))
                        .setCombatType(CombatType.RANGE)
                        .setDistanceRequiredForAttack(17)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setHitDelay(2)
                        .setPoisonDamage(4)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(2881).setStartHeight(45).setSendDelay(2).createProjectileBase())
                        .setMaxHit(36)
                        .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                            @Override
                            public Double apply(NPCCombatAttack npcCombatAttack) {
                                return 0.35;
                            }
                        })
                        .setEndGraphic(new Graphic(2882))
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setAttackDelay(4)
                        .createNPCAutoAttack()

        ));
    }
}
