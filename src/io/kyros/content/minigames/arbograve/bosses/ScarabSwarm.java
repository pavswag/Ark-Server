package io.kyros.content.minigames.arbograve.bosses;

import com.google.common.collect.Lists;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.arbograve.ArbograveBoss;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 08/02/2024
 */
public class ScarabSwarm extends ArbograveBoss {
    public ScarabSwarm(InstancedArea instancedArea) {
        super(1782, new Position(Misc.random(1709, 1717), Misc.random(4264, 4273), instancedArea.getHeight()), instancedArea);

        getBehaviour().setAggressive(true);
        getCombatDefinition().setAggressive(true);
    }

    @Override
    public void process() {
        setAttacks();
        super.process();
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(1946))
                        .setCombatType(CombatType.RANGE)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setDistanceRequiredForAttack(1)
                        .setPoisonDamage(12)
                        .setMaxHit(0)
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setOnAttack(npcCombatAttack -> {

                            asNPC().getInstance().getNpcs().forEach(npc -> {
                                if (npc.getNpcId() == 1127) {
                                    npc.appendHeal(3, HitMask.NPC_HEAL);
                                }
                            });

                        })
                        .setHitDelay(4)
                        .setAttackDelay(4)
                        .createNPCAutoAttack()

        ));
    }
}
