package io.kyros.content.minigames.shadow_crusade.bosses;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeBoss;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeRoom;
import io.kyros.model.*;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.function.Function;

public class RoomSixLucien extends ShadowcrusadeBoss {


    public RoomSixLucien(int npcId, Position position, InstancedArea instancedArea) {
        super(13527, new Position(position.getX(), position.getY(), instancedArea.getHeight()), instancedArea);

        int health = (getHealth().getCurrentHealth() / 3) * instancedArea.getPlayers().size();

        getHealth().setMaximumHealth(health);
        getHealth().setCurrentHealth(health);

        revokeWalkingPrivilege = false;
        walkingType = 1;
        facePosition(2799, 4325);
        setAttacks();

        for (int i = 0; i < instancedArea.getPlayers().size() + 8; i++) {
            new SkeletonWarriors(13498, new Position(Misc.random(2792, 2804), Misc.random(4325, 4333), instancedArea.getHeight()), instancedArea);
        }
    }

    @Override
    public void process() {
        setAttacks();
        super.process();
    }

    //MainBoss & Summon's Skeleton Warrior's

    //MainBoss (Melee/Magic Immue)
    //Skeleton's no immunity

    //Skeleton -> 13498
    //Lucien -> 13527

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(10143))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(3)
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
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(10123))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(17)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setHitDelay(3)
                        .setPoisonDamage(4)
                        .setMaxHit(36)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(2860).setSendDelay(2).createProjectileBase())
                        .setEndGraphic(new Graphic(2861))
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
