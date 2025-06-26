package io.kyros.content.minigames.shadow_crusade;

import io.kyros.Configuration;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.bosspoints.BossPoints;
import io.kyros.content.combat.HitMask;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.pet.PetManager;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.model.entity.Health;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class ShadowcrusadeBoss extends NPC {

    public ShadowcrusadeBoss(int npcId, Position position, InstancedArea instancedArea) {
        super(npcId, position);
        instancedArea.add(this);
        getBehaviour().setRespawn(false);
        getBehaviour().setAggressive(false);
        getCombatDefinition().setAggressive(false);
    }

    public void onDeath() {
        if (getInstance() != null) {
            if (this.asNPC() != null && this.asNPC().getNpcId() == 13527) {
                for (NPC npc : getInstance().getNpcs()) {
                    npc.appendDamage(npc.getHealth().getMaximumHealth(), HitMask.HIT);
                }
                for (Player player : getInstance().getPlayers()) {
                    if (player != null) {
                        player.sendMessage("You have completed Shadow Crusade!");
                        player.getBossTimers().death("Shadow Crusade");
                        Pass.addExperience(player, 2);
                        player.shadowCrusadeCompletions++;
                        PetManager.addXp(player, 1050);
                        if (Misc.random(0, 300) == 1) {
                            player.getItems().addItemUnderAnyCircumstance(26886, 1);
                            PlayerHandler.executeGlobalMessage("<shad=1>[ShadowCrusade] " + player.getDisplayName() + " has just gotten a Overcharged Cell From Shadow Crusade!");
                        }

                        int points = Misc.random(500, 2000);
                        int rng = Misc.trueRand(1);
                        if (rng == 1) {
                            points /= 2;
                        }

                        if (player.getCurrentPet().hasPerk("p2w_raiders_ruse")) {
                            points += (int) (points * 0.10);
                        }

                        player.shadowCrusadePoints += points;
                        player.sendMessage("You now have a total of " + player.shadowCrusadePoints + " Shadow Crusade Points!");

                        BossPoints.addManualPoints(player, "shadow crusade raid");
                        int keys = player.getShadowcrusadeContainer().lives;

                        if (keys > 0) {
                            if (PrestigePerks.hasRelic(player, PrestigePerks.TRIPLE_HESPORI_KEYS) && Misc.isLucky(10)) {
                                keys *= 3;
                            }

                            if (Hespori.KRONOS_TIMER > 0 && Misc.random(100) >= 95) {
                                keys *= 2;
                            }

                            if (player.getCurrentPet().getNpcId() == 2302) {
                                keys *= 2;
                            }

                            if (player.hasEquippedSomewhere(33394)) {
                                keys *= 2;
                            }

                            player.getItems().addItemUnderAnyCircumstance(28416, keys);//TODO Change key
                        } else {
                            player.sendMessage("You had no key's remaining so you get fuck all.");
                        }

                        player.moveTo(new Position(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0));
                        Achievements.increase(player, AchievementType.SHADOW_CRUSADE, 1);
                        player.tryPetRaidLoot();
                    }
                }
            }
        }
    }

}
