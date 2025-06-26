package io.kyros.content.minigames.arbograve;

import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.combat.HitMask;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.arbograve.bosses.Leech;
import io.kyros.content.pet.PetManager;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

public class ArbograveBoss extends NPC {

    public ArbograveBoss(int npcId, Position position, InstancedArea instancedArea) {
        super(npcId, position);
        instancedArea.add(this);
        getBehaviour().setRespawn(false);
        getBehaviour().setAggressive(true);
        getCombatDefinition().setAggressive(true);
    }

    public void onDeath() {
        Entity killer = calculateKiller();
        if (getInstance() != null) {
            if (this.asNPC() != null && this.asNPC().getNpcId() == 6477) {
                for (NPC npc : getInstance().getNpcs()) {
                    npc.appendDamage(npc.getHealth().getMaximumHealth(), HitMask.HIT);
                }
                for (Player player : getInstance().getPlayers()) {
                    player.sendMessage("You have completed Abrograve!");
                    player.getBossTimers().death("Arbograve Swamp");
                    Pass.addExperience(player, 2);
                    player.arboCompletions++;
                    PetManager.addXp(player, 1050);
                    if (Misc.random(0,300) == 1) {
                        player.getItems().addItemUnderAnyCircumstance(26886, 1);
                        PlayerHandler.executeGlobalMessage("<shad=1>[ArboGrave] " + player.getDisplayName() + " has just gotten a Overcharged Cell From ArboGrave!");
                    }

                    int points = Misc.random(500,2000);
                    int rng = Misc.trueRand(1);
                    if (rng == 1) {
                        points /= 2;
                    }

                    if (player.getCurrentPet().hasPerk("p2w_raiders_ruse")) {
                        points += (int) (points * 0.10);
                    }

                    player.arboPoints += points;
                    player.sendMessage("You now have a total of " + player.arboPoints + " Arbograve Swamp Points!");
                    int keys = player.getArboContainer().lives;

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

                        player.getItems().addItemUnderAnyCircumstance(2400, keys);
                    } else {
                        player.sendMessage("You had no key's remaining so you get fuck all.");
                    }

                    player.moveTo(new Position(2834,3264,0));
                    Achievements.increase(player, AchievementType.ARBO, 1);
                    player.tryPetRaidLoot();
                }
            }
        }

        if (asNPC().getNpcId() != 3233 && asNPC().getNpcId() != 1782) {
            new Leech(new Position(asNPC().getX(), asNPC().getY()),asNPC().getInstance());

            for (Player player : asNPC().getInstance().getPlayers()) {
                Server.itemHandler.createGroundItem(player, new GameItem(6691,Misc.random(2,4)), new Position(asNPC().getX(), asNPC().getY(), player.getHeight()), 0);
            }
        }
    }
}
