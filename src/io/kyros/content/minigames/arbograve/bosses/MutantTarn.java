package io.kyros.content.minigames.arbograve.bosses;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.arbograve.ArbograveBoss;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.entity.player.ClientGameTimer;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MutantTarn extends ArbograveBoss {


    private boolean Tarn1st = false, Tarn2nd = false, Tarn3rd = false;
    public MutantTarn(InstancedArea instancedArea) {
        super(6477, new Position(1681, 4232, instancedArea.getHeight()), instancedArea);

        Tarn1st = false;
        Tarn2nd = false;
        Tarn3rd = false;
        int healthNeeded = 6000;
        int totalMembers = instancedArea.getPlayers().size();
        if (totalMembers > 1) {
            double healthIncreasePercentage = 0.20;  // 20% increase per member
            double totalHealthMultiplier = 1 + (healthIncreasePercentage * totalMembers);

            healthNeeded = (int) (healthNeeded * totalHealthMultiplier);
        }
        asNPC().getHealth().setCurrentHealth(healthNeeded);
        asNPC().getHealth().setMaximumHealth(healthNeeded);

        facePosition(1681, 4243);
        setAttacks();
    }

    @Override
    public void process() {
        postDefend();
        int current_health = asNPC().getHealth().getCurrentHealth();
        int maximum_health = asNPC().getHealth().getMaximumHealth();

        double healthPercentage = (double) current_health / maximum_health * 100;

        if (healthPercentage <= 25 && !Tarn3rd) {
//            asNPC().isGodmode = true;
            Tarn3rd = true;
            asNPC().forceChat("Rise my Beasts!!!");
            spawnTerrorDogs(asNPC().getInstance());
            for (Player player : asNPC().getInstance().getPlayers()) {
                player.sendMessage("@red@Tarn is now immune to all attacks!!");
                if (player.usingInfPrayer) {
                    player.getPotions().resetInfPrayer();
                    player.getPA().sendGameTimer(ClientGameTimer.INF_PRAYER_POT, TimeUnit.SECONDS, 1);
                }
                if (player.hasOverloadBoost) {
                    player.getPotions().resetOverload();
                    player.getPA().sendGameTimer(ClientGameTimer.OVERLOAD, TimeUnit.SECONDS, 1);
                }
            }
        } else if (healthPercentage <= 50 && healthPercentage > 25 && !Tarn2nd) {
//            asNPC().isGodmode = true;
            Tarn2nd = true;
            asNPC().forceChat("Rise my Beasts!!!");
            spawnTerrorDogs(asNPC().getInstance());
            for (Player player : asNPC().getInstance().getPlayers()) {
                player.sendMessage("@red@Tarn is now immune to all attacks!!");
                if (player.usingInfPrayer) {
                    player.getPotions().resetInfPrayer();
                    player.getPA().sendGameTimer(ClientGameTimer.INF_PRAYER_POT, TimeUnit.SECONDS, 1);
                }
                if (player.hasOverloadBoost) {
                    player.getPotions().resetOverload();
                    player.getPA().sendGameTimer(ClientGameTimer.OVERLOAD, TimeUnit.SECONDS, 1);
                }
            }
        } else if (healthPercentage <= 75 && healthPercentage > 50 && !Tarn1st) {
//            asNPC().isGodmode = true;
            Tarn1st = true;
            asNPC().forceChat("Rise my Beasts!!!");
            spawnTerrorDogs(asNPC().getInstance());
            for (Player player : asNPC().getInstance().getPlayers()) {
                player.sendMessage("@red@Tarn is now immune to all attacks!!");
                if (player.usingInfPrayer) {
                    player.getPotions().resetInfPrayer();
                    player.getPA().sendGameTimer(ClientGameTimer.INF_PRAYER_POT, TimeUnit.SECONDS, 1);
                }
                if (player.hasOverloadBoost) {
                    player.getPotions().resetOverload();
                    player.getPA().sendGameTimer(ClientGameTimer.OVERLOAD, TimeUnit.SECONDS, 1);
                }
            }
        }


        setAttacks();
        super.process();
    }

    public void postDefend() {
        if (asNPC().isGodmode && asNPC().getNpcId() == 8298) {
            final int[] count = {0};

            asNPC().getInstance().getNpcs().forEach(npc -> {
                if (npc.getNpcId() == 8298) {
                    count[0]++;
                }
            });

            if (count[0] <= 0) {
                asNPC().isGodmode = false;
                for (Player player : asNPC().getInstance().getPlayers()) {
                    player.sendMessage("@red@Tarn's immunity has vanished!");
                }
            }
        }
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setAnimation(new Animation(5613))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(3)
                        .setMaxHit(52)
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                            @Override
                            public Double apply(NPCCombatAttack npcCombatAttack) {
                                return 0.75;
                            }
                        })
                        .setPoisonDamage(15)
                        .setAttackDelay(3)
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setSelectAutoAttack(attack -> Misc.trueRand(10) == 0 && !asNPC().isGodmode)
                        .setAnimation(new Animation(5613))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(3)
                        .setMaxHit(52)
                        .setAccuracyBonus(npcCombatAttack -> 15.0)
                        .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                            @Override
                            public Double apply(NPCCombatAttack npcCombatAttack) {
                                return 0.75;
                            }
                        })
                        .setPoisonDamage(15)
                        .setAttackDelay(3)
                        .createNPCAutoAttack()

        ));
    }

    private void spawnTerrorDogs(InstancedArea instance) {
        int dog_amount = (8 * instance.getPlayers().size());

        for (int i = 0; i < dog_amount; i++) {
            int x = Misc.random(1675,1686);
            int y = Misc.random(4231,4242);
            new TerrorDog(new Position(x,y), instance);
        }
    }
}
