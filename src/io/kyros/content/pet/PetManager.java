package io.kyros.content.pet;

import io.kyros.Server;
import io.kyros.content.bosses.nightmare.NightmareConstants;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerAssistant;
import io.kyros.util.Misc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages player pets, including data handling, UI updates, and interactions.
 */
public class PetManager {

    public static void open(Player player) {
        if (isRestrictedArea(player) || player.getInstance() != null || player.teleTimer > 0) {
            return;
        }
        updateInterface(player);
        player.getPA().showInterface(22731);
    }

    private static boolean isRestrictedArea(Player player) {
        return player.isBoundaryRestricted();
    }

    public static void updateInterface(Player player) {
        sendCurrentPetData(player);
        sendOverrideNames(player);
    }

    public static void deletePet(Player player) {
        player.start(new DialogueBuilder(player).statement("Are you sure you want to delete your pet!").option(
                new DialogueOption("Yes", p -> {
                    if (p.getCurrentPetIndex() == -1) {
                        return;
                    }
                    if (p.getCurrentPet().getNpcId() == PetHandler.Pets.KITTEN.npcId) {
                        return;
                    }
                    Pet currentPet = p.getCurrentPet();

                    if (currentPet == null) {
                        return;
                    }
                    p.sendErrorMessage("You have removed " + NpcDef.forId(currentPet.getNpcId()).getName() + " from your collection!");
                    p.getPetCollection().remove(currentPet);

                    Pet npc;
                    try {
                        npc = p.getPetCollection().get(0);
                    } catch (Exception e) {
                        return;
                    }
                    if (npc == null) return;

                    player.petPerkCost.replace(player.getCurrentPet().getNpcId(), new long[]{1_000_000, 1_000_000, 1_000_000, 1_000_000, 1_000_000});
                    p.setCurrentPetIndex(0);
                    p.setCurrentPet(npc);
                    p.currentPetNpc.requestTransform(p.getCurrentPet().getNpcId());
                    p.currentPetNpc.setFacePlayer(true);
                    p.currentPetNpc.facePlayer(p.getIndex());
                    Server.npcHandler.followPlayer(p.currentPetNpc, p.getIndex());
                    updateInterface(p);
                    p.getPA().closeAllWindows();
                }), new DialogueOption("nevermind", p -> p.getPA().closeAllWindows())
        ));
    }

    public static void addXp(Player player, int XP) {
        if (player.getCurrentPetIndex() == -1) {
            return;
        }
        int originalXp = player.getCurrentPet().getExperience();
        int newXp = originalXp + XP;

        player.getPetCollection().get(player.getCurrentPetIndex()).setExperience(newXp);

        if (PetUtility.getLevelForXP(originalXp) < PetUtility.getLevelForXP(newXp)) {
            player.getPetCollection().get(player.getCurrentPetIndex()).setLevel(PetUtility.getLevelForXP(newXp));
            player.setCurrentPet(player.getPetCollection().get(player.getCurrentPetIndex()));
            handlePetLevelUp(player, PetUtility.getLevelForXP(originalXp));
        } else {
            updateInterface(player);
        }

        player.getPA().addXpDrop(new PlayerAssistant.XpDrop(XP, 30));
        PetUtility.savePet(player);
    }

    public static void handlePetLevelUp(Player player, int originalLevel) {
        for (int i = originalLevel; i < player.getCurrentPet().getLevel(); i++) {
            if (i % 2 == 0) {
                player.getPetCollection().get(player.getCurrentPetIndex()).setSkillUpPoints((short) (player.getPetCollection().get(player.getCurrentPetIndex()).getSkillUpPoints() + 1));
            }
        }

        player.setCurrentPet(player.getPetCollection().get(player.getCurrentPetIndex()));
        updateInterface(player);
        PetUtility.savePet(player);
        player.sendMessage("<icon=9999> Congratulations, your pet has reached level " + player.getCurrentPet().getLevel() + "! Your pet now has " + player.getCurrentPet().getSkillUpPoints() + " skill up points available.");
    }

    public static void sendCurrentPetData(Player player) {
        player.getPA().sendString(22754, "Lv: " + Misc.formatCoins(player.getCurrentPet().getLevel()));
        player.getPA().sendString(22755, "Xp: " + Misc.formatCoins(player.getCurrentPet().getExperience()));
        player.getPA().sendString(22756, "Xp till lvl: " + Misc.formatCoins(PetUtility.getXPForLevel(player.getCurrentPet().getLevel() + 1) - player.getCurrentPet().getExperience()));
        player.getPA().sendString(22747, "<col=ff9933>" + player.getCurrentPet().getNpcDefinition().name);
        player.getPA().sendString(48854, player.getCurrentPet().getDescription());
        sendPerkLevels(player);
        player.getPA().runClientScript(13020, player.getCurrentPet().getNpcDefinition().id);
    }

    public static void sendPerkLevels(Player player) {
        AtomicInteger startingWidget = new AtomicInteger(22742);
        player.getCurrentPet().getPetPerks().forEach(perk -> {
            player.getPA().sendString(startingWidget.getAndIncrement(), "<col=ff9933>" + perk.getPerkName() + "</col>\\n<col=ffffff>Level: " + perk.getLevel() + "</col>");
        });
        sendPerkDescriptions(player);
    }

    public static void sendPerkDescriptions(Player player) {
        int startingWidget = 39355;
        for (PetPerk perk : player.getCurrentPet().getPetPerks()) {
            player.getPA().sendString(startingWidget++, perk.getPerkName() + " - Level " + perk.getLevel());
            player.getPA().sendString(startingWidget++, perk.asString());
            startingWidget++;
            startingWidget++;
        }
    }

    public static void sendOverrideNames(Player player) {
        AtomicInteger startWidget = new AtomicInteger(25176);
        player.getPetCollection().forEach(npc -> {
            player.getPA().sendString(startWidget.getAndIncrement(), NpcDef.forId(npc.getNpcId()).getName());
        });
        player.getPA().setScrollableMaxHeight(24974, Math.max(200, 18 * player.getPetCollection().size()));
    }

    public static boolean handleButton(Player player, int buttonId) {
        if (handleCosmeticOverrideButton(player, buttonId)) return true;
        if (handlePetPerkRerollButton(player, buttonId)) return true;
        if (handlePerkLevelUpButton(player, buttonId)) return true;
        if (buttonId == 22757) {
            player.currentPetNpc.setInvisible(!player.currentPetNpc.isInvisible());
            player.sendMessage("Your pet is now " + (player.currentPetNpc.isInvisible() ? "hidden" : "visible") + ".");
            return true;
        }
        return false;
    }

    public static boolean handlePetPerkRerollButton(Player player, int buttonId) {
        if (buttonId >= 22761 && buttonId <= 22765) {
            int index = buttonId - 22761;

            if (!player.isInterfaceOpen(22731)) {
                return false;
            }

            if (!player.petPerkCost.containsKey(player.getCurrentPet().getNpcId())) {
                player.petPerkCost.put(player.getCurrentPet().getNpcId(), new long[]{1_000_000, 1_000_000, 1_000_000, 1_000_000, 1_000_000});
            }



            PetPerk newPerk = PetUtility.getRandomPetPerk(player);
            if (newPerk != null) {
                long rerollCost = player.petPerkCost.get(player.getCurrentPet().getNpcId())[index];
                if (player.foundryPoints < rerollCost || player.foundryPoints <= 0) {
                    player.sendErrorMessage("You don't have enough Nomad to re-roll this perk!");
                    player.sendErrorMessage("You need " + Misc.formatCoins(rerollCost) + " Nomad to re-roll!");
                    return true;
                }

                PetPerk currentPerk = player.getPetCollection().get(player.getCurrentPetIndex()).getPetPerks().get(index);
                int currentLevel = currentPerk.getLevel();

                PetPerk assignedPerk = new PetPerk(newPerk); // Deep copy the new perk
                assignedPerk.setLevel(1);
                player.getPetCollection().get(player.getCurrentPetIndex()).getPetPerks().set(index, assignedPerk);

                player.sendErrorMessage("You have just spent " + Misc.formatCoins(rerollCost) + " Nomad to re-roll your pet's perk!");

                if (currentLevel > 1) {
                    int totalSkillUpsRequired = calculateTotalSkillUpsRequired(currentLevel);
                    // Refund the exact number of skill-ups required for the current level
                    player.getPetCollection().get(player.getCurrentPetIndex()).setSkillUpPoints(
                            (short) (player.getCurrentPet().getSkillUpPoints() + totalSkillUpsRequired)
                    );
                    player.sendMessage("You have received a refund of " + totalSkillUpsRequired + " skill up points, giving you a new total of " + player.getCurrentPet().getSkillUpPoints() + " skill up points.");
                }

                updateInterface(player);
                player.foundryPoints -= rerollCost;
                player.petPerkCost.get(player.getCurrentPet().getNpcId())[index] = (long) (rerollCost * 1.10);

            } else {
                player.sendMessage("Failed to find a suitable pet perk. Try again");
            }
            return true;
        }
        return false;
    }

    public static boolean handlePerkLevelUpButton(Player player, int buttonId) {
        if (buttonId >= 22736 && buttonId <= 22740) {
            int index = buttonId - 22736;

            if (!player.isInterfaceOpen(22731)) {
                return false;
            }

            Pet currentPet = player.getPetCollection().get(player.getCurrentPetIndex());
            if (currentPet == null) {
                player.sendErrorMessage("You don't have a pet summoned.");
                return false;
            }

            PetPerk perk = currentPet.getPetPerks().get(index);
            if (perk == null) {
                player.sendErrorMessage("Invalid perk selection.");
                return false;
            }

            if (perk.levelUp(player, perk)) {
                currentPet.getPetPerks().set(index, perk); // Apply the level up only to this instance

                player.sendMessage("Congratulations! Your " + perk.getPerkName() + " has reached level " + perk.getLevel() + "!");
                player.sendMessage("You have " + currentPet.getSkillUpPoints() + " skill up points remaining!");

                player.setCurrentPet(currentPet);
                updateInterface(player);
            } else {
                player.sendErrorMessage("Unable to level up the perk. Ensure you have enough skill up points and the perk is not max level.");
            }
            return true;
        }
        return false;
    }

    public static boolean handleCosmeticOverrideButton(Player player, int buttonId) {
        if (buttonId >= 24975 && buttonId <= 25175) {
            if (!player.isInterfaceOpen(22731)) {
                return false;
            }
            int index = buttonId - 24975;
            Pet npc;
            try {
                npc = player.getPetCollection().get(index);
            } catch (Exception e) {
                return false;
            }
            if (npc == null) return false;
            player.setCurrentPetIndex(index);
            player.setCurrentPet(npc);
            player.currentPetNpc.requestTransform(player.getCurrentPet().getNpcId());
            player.currentPetNpc.setFacePlayer(true);
            player.currentPetNpc.facePlayer(player.getIndex());
            Server.npcHandler.followPlayer(player.currentPetNpc, player.getIndex());
            updateInterface(player);
            return true;
        }
        return false;
    }

    public static void summonOnLogin(Player player) {
        if (player.getCurrentPet() == null) {
            player.sendMessage("Pet is null");
            Pet pet = new Pet(5591);
            player.setCurrentPet(pet);
            player.getCurrentPet().addDefaultPerks();
            player.getPetCollection().add(pet);
            player.sendMessage("You have a new pet available @ ::pet");
        }

        player.hasPetSpawned = true;
        player.hasFollower = true;
        int offsetX = 0;
        int offsetY = 0;
        if (player.getRegionProvider().getClipping(player.getX() - 1, player.getY(), player.heightLevel, -1, 0)) {
            offsetX = -1;
        } else if (player.getRegionProvider().getClipping(player.getX() + 1, player.getY(), player.heightLevel, 1, 0)) {
            offsetX = 1;
        } else if (player.getRegionProvider().getClipping(player.getX(), player.getY() - 1, player.heightLevel, 0, -1)) {
            offsetY = -1;
        } else if (player.getRegionProvider().getClipping(player.getX(), player.getY() + 1, player.heightLevel, 0, 1)) {
            offsetY = 1;
        }
        player.currentPetNpc = NPCSpawning.spawnPet(player, player.getCurrentPet().getNpcDefinition().id, player.absX + offsetX, player.absY + offsetY,
                player.getHeight(), 0, true, false, true);
        player.currentPetNpc.getCombatDefinition().setAggressive(false);
        player.currentPetNpc.summoner = true;
        player.currentPetNpc.summonedBy = player.getIndex();
        player.hasFollower = true;

        for (int i = 25176; i <= 25376; i++) {
            player.getPA().sendString(i, "");
        }
    }

    private static int calculateTotalSkillUpsRequired(int level) {
        // Define the cumulative points required as per the provided level chart.
        int[] cumulativePoints = {
                0, 1, 2, 3, 4, 5, 7, 9, 11, 14, 17, 20, 24, 28, 32, 37, 42, 47, 53, 59,
                65, 72, 79, 86, 94, 102, 110, 119, 128, 137, 147, 157, 167, 178, 189, 200,
                212, 224, 236, 249, 262, 275, 289, 303, 317, 332, 347, 362, 378, 394, 410,
                427, 444, 461, 479, 497, 515, 534, 553, 572, 592, 612, 632, 653, 674, 695,
                717, 739, 761, 784, 807, 830, 854, 878, 902, 927, 952, 977, 1003, 1029,
                1055, 1082, 1109, 1136, 1164, 1192, 1220, 1249, 1278, 1307, 1337, 1367, 1397,
                1428, 1459, 1490, 1522, 1554, 1586
        };

        // Ensure the level is within the bounds of the array.
        if (level < 1 || level > cumulativePoints.length) {
            return 0;//If they've fucked there account they get nothing.
        }

        // Return the cumulative points required for the given level.
        return cumulativePoints[level - 1];
    }

    public static void HandlePetPrestige(Player player) {
        player.petPrestige.merge(player.getCurrentPet().getNpcId(), 1, Integer::sum);

        player.getCurrentPet().setLevel(1);
        player.getCurrentPet().setExperience(0);

        player.sendErrorMessage("You have successfully prestiged your pet, it has now been set back to level 1!");
        player.sendErrorMessage("Your current pet xp has been lowered due to prestiging your pet!");
        PetUtility.savePet(player);
    }

    public static void handleForcedSkillup(Player player) {
        short current = player.getCurrentPet().getSkillUpPoints();
        player.getCurrentPet().setSkillUpPoints((short) (current + 5));

        player.sendErrorMessage("You have just gained 5 skill up points for your pet!");
        PetUtility.savePet(player);
    }
}
