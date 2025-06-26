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

public class RoomFiveEleMix extends ShadowcrusadeBoss {


    public RoomFiveEleMix(int npcId, Position position, InstancedArea instancedArea) {
        super(npcId, new Position(position.getX(), position.getY(), instancedArea.getHeight()), instancedArea);

        revokeWalkingPrivilege = false;
        walkingType = 1;
        facePosition(2792, 4314);
        setAttacks();

        int health = (getHealth().getCurrentHealth() / 3) * instancedArea.getPlayers().size();

        getHealth().setMaximumHealth(health);
        getHealth().setCurrentHealth(health);

        for (int i = 0; i < 20; i++) {
            int x = Misc.random(2794, 2803);
            int y = Misc.random(4308, 4319);
            int npcID = Misc.random(13463,13470);
            if (npcID == 13463 || npcID == 13464) {
                new EliteKnight(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            } else if (npcID == 13465 || npcID == 13466) {
                new EliteWarrior(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            } else if (npcID == 13467 || npcID == 13468) {
                new EliteRanger(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            } else if (npcID == 13469 || npcID == 13470) {
                new EliteMage(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            }
        }
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
