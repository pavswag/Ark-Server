package io.kyros.content.minigames.arbograve.bosses;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.arbograve.ArbograveBoss;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.entity.player.ClientGameTimer;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Leech extends ArbograveBoss {

    public Leech(Position position, InstancedArea instancedArea) {
        super(3233, new Position(position.getX(), position.getY(), instancedArea.getHeight()), instancedArea);
        revokeWalkingPrivilege = false;
        walkingType = 1;
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
                        .setAnimation(new Animation(1273))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(1)
                        .setMaxHit(0)
                        .setAccuracyBonus(npcCombatAttack -> 1.5)
                        .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                            @Override
                            public Double apply(NPCCombatAttack npcCombatAttack) {
                                return 0.3;
                            }
                        })
                        .setOnAttack(npcCombatAttackHit -> {
                            if (npcCombatAttackHit.getVictim().asPlayer().usingInfPrayer) {
                                npcCombatAttackHit.getVictim().asPlayer().getPotions().resetInfPrayer();
                                npcCombatAttackHit.getVictim().asPlayer().getPA().sendGameTimer(ClientGameTimer.INF_PRAYER_POT, TimeUnit.SECONDS, 1);
                            }
                            if (npcCombatAttackHit.getVictim().asPlayer().hasOverloadBoost) {
                                npcCombatAttackHit.getVictim().asPlayer().getPotions().resetOverload();
                                npcCombatAttackHit.getVictim().asPlayer().getPA().sendGameTimer(ClientGameTimer.OVERLOAD, TimeUnit.SECONDS, 1);
                            }
                            /*if (npcCombatAttackHit.getVictim().asPlayer().protectingMagic() || npcCombatAttackHit.getVictim().asPlayer().protectingMelee() || npcCombatAttackHit.getVictim().asPlayer().protectingRange()) {
                                CombatPrayer.resetOverHeads(npcCombatAttackHit.getVictim().asPlayer());
                            }*/
                            if (Misc.random(0, 10) == 1) {
                                int[] toDecrease = { 0, 1, 2, 4, 5, 6 };

                                for (int tD : toDecrease) {
                                    npcCombatAttackHit.getVictim().asPlayer().playerLevel[tD] -= 5;
                                    if (npcCombatAttackHit.getVictim().asPlayer().playerLevel[tD] <= 0)
                                        npcCombatAttackHit.getVictim().asPlayer().playerLevel[tD] = 1;
                                    npcCombatAttackHit.getVictim().asPlayer().getPA().refreshSkill(tD);
                                    npcCombatAttackHit.getVictim().asPlayer().getPA().setSkillLevel(tD, npcCombatAttackHit.getVictim().asPlayer().playerLevel[tD], npcCombatAttackHit.getVictim().asPlayer().playerXP[tD]);
                                }
                            }
                            npcCombatAttackHit.getVictim().asPlayer().startGraphic(new Graphic(1794));
                        })
                        .setHitDelay(8)
                        .setAttackDelay(8)
                        .createNPCAutoAttack()

        ));

    }
}
