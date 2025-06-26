package io.kyros.content.skills.agility;

import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.combat.HitMask;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.skills.Skill;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.ImmutableItem;
import io.kyros.util.Misc;

import java.util.Arrays;

public class PyramidPlunder {

    public static boolean handleObjects(Player player, int objectID) {
        switch (objectID) {
            case 26580: searchUrn26580(player); return true;
            case 26600: searchUrn26600(player); return true;
            case 26601: searchUrn26601(player); return true;
            case 26602: searchUrn26602(player); return true;
            case 26603: searchUrn26603(player); return true;
            case 26604: searchUrn26604(player); return true;
            case 26605: searchUrn26605(player); return true;
            case 26606: searchUrn26606(player); return true;
            case 26607: searchUrn26607(player); return true;
            case 26608: searchUrn26608(player); return true;
            case 26609: searchUrn26609(player); return true;
            case 26610: searchUrn26610(player); return true;
            case 26611: searchUrn26611(player); return true;
            case 26612: searchUrn26612(player); return true;
            case 26613: searchUrn26613(player); return true;
            case 26616: searchUrn26616(player); return true; //main chest
            case 26626: searchUrn26626(player); return true; //sarcophagus

            case 26624: startPyramid(player); return true;
            case 21280: disarmTrap(player); return true;
            case 20931: endMinigame(player); return true; //exit door
        }


        int[] doors = {26618, 26619, 26620, 26621};

        if (player.pyramidDoor == objectID) {
            nextRoomDoor(player); return true;
        } else if (Arrays.stream(doors).anyMatch(i -> i == objectID)) {
            player.sendMessage("This door does not lead anywhere. Try another one."); return true;
        }
        return false;
    }

    public static void nextRoomDoor(Player player) {
        if (player.nextPlunderRoomId == 8) {
            player.sendMessage("Use the exit door to leave the minigame.");
            return;
        }
        player.start(new DialogueBuilder(player).option("Advance to the next room? You will not be able to return to the current room!",
                new DialogueOption("Yes", p -> {
                    if (player.hitStandardRateLimit(true))
                        return;
                    player.getPA().closeAllWindows();
                    nextRoom(player);
                }),
                new DialogueOption("No", player1 -> player1.getPA().closeAllWindows())));
    }

    public static void nextRoom(Player player) {
        int[] doors = {26618, 26619, 26620, 26621};

        if (!player.loot26616) {
            player.start(new DialogueBuilder(player).statement("You need to progress this floor first!"));
            return;
        }

        player.pyramidDoor = doors[Misc.random(doors.length-1)];
        player.nextPlunderRoomId++;
        player.sendMessage("@cya@Current Room: " + player.nextPlunderRoomId);
        player.disarmedPlunderRoomTrap = false;
        player.loot26580 = false;
        player.loot26600 = false;
        player.loot26601 = false;
        player.loot26603 = false;
        player.loot26604 = false;
        player.loot26606 = false;
        player.loot26607 = false;
        player.loot26608 = false;
        player.loot26609 = false;
        player.loot26610 = false;
        player.loot26611 = false;
        player.loot26612 = false;
        player.loot26613 = false;
        player.loot26616 = false;
        player.loot26626 = false;
        if (player.nextPlunderRoomId == 2 && player.getLevel(Skill.THIEVING) >= 31) {
            player.moveTo(new Position(1954, 4476, 0));
        } else if (player.nextPlunderRoomId == 3 && player.getLevel(Skill.THIEVING) >= 41) {
            player.moveTo(new Position(1977, 4470, 0));
        } else if (player.nextPlunderRoomId == 4 && player.getLevel(Skill.THIEVING) >= 51) {
            player.moveTo(new Position(1927, 4452, 0));
        } else if (player.nextPlunderRoomId == 5 && player.getLevel(Skill.THIEVING) >= 61) {
            player.moveTo(new Position(1965, 4444, 0));
        } else if (player.nextPlunderRoomId == 6 && player.getLevel(Skill.THIEVING) >= 71) {
            player.moveTo(new Position(1927, 4424, 0));
        } else if (player.nextPlunderRoomId == 7 && player.getLevel(Skill.THIEVING) >= 81) {
            player.moveTo(new Position(1943, 4421, 0));
        } else if (player.nextPlunderRoomId == 8 && player.getLevel(Skill.THIEVING) >= 91) {
            player.moveTo(new Position(1974, 4420, 0));
        } else {
            player.sendMessage("search for treasure inside this room");
        }

    }

    public static void startPyramid(Player player) {
        if (player.getLevel(Skill.THIEVING) < 21) {
            player.start(new DialogueBuilder(player).statement("You require 21 thieving to enter the Pyramid!"));
            return;
        }
        player.moveTo(new Position(1927, 4476, 0));
        player.nextPlunderRoomId++;
        int[] doors = {26618, 26619, 26620, 26621};

        player.pyramidDoor = doors[Misc.random(doors.length-1)];
        player.start(new DialogueBuilder(player).statement("You enter the Pyramid. You have 5 minutes to loot!"));
        player.inPyramidPlunder = true;
        handleTime(player);
    }

    public static void handleTime(Player player) {
        beginTimer(player);
    }

    private static void beginTimer(Player player) {
        CycleEventHandler.getSingleton().stopEvents(player, CycleEventHandler.Event.PYRAMID_PLUNDER);
        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.PYRAMID_PLUNDER, player, new CycleEvent() {

            @Override
            public void execute(CycleEventContainer b) {
                if (player == null) {
                    b.stop();
                    return;
                }
                ranOutOfTime(player);
            }

            @Override
            public void onStopped() {

            }
        }, 500); // 5 minutes
    }

    public static void ranOutOfTime(Player player) {
        player.moveTo(new Position(3288, 2786, 0));
        player.nextPlunderRoomId = 0;
        player.disarmedPlunderRoomTrap = false;
        player.start(new DialogueBuilder(player).statement("You've run out of time and the minigame has ended!"));
        player.inPyramidPlunder = false;
        player.totalPyramidPlunderGames++;
        endMinigame(player);
    }

    public static void endMinigame(Player player) {
        player.moveTo(new Position(3288, 2786, 0));
        player.nextPlunderRoomId = 0;
        player.disarmedPlunderRoomTrap = false;
        player.start(new DialogueBuilder(player).statement("You have left the minigame."));
        CycleEventHandler.getSingleton().stopEvents(player, CycleEventHandler.Event.PYRAMID_PLUNDER);
        player.inPyramidPlunder = false;
        player.totalPyramidPlunderGames++;
        player.lastTimeEnteredPlunder = 0;
        player.loot26580 = false;
        player.loot26600 = false;
        player.loot26601 = false;
        player.loot26603 = false;
        player.loot26604 = false;
        player.loot26606 = false;
        player.loot26607 = false;
        player.loot26608 = false;
        player.loot26609 = false;
        player.loot26610 = false;
        player.loot26611 = false;
        player.loot26612 = false;
        player.loot26613 = false;
        player.loot26616 = false;
        player.loot26626 = false;
    }

    public static void giveUrnLoot(Player player) {
        int amount = Misc.random(1000);
        amount *= (player.nextPlunderRoomId + 1);
        player.sendMessage("You search around inside the urn and find " + Misc.formatCoins(amount) + " coins!");
        player.getInventory().addAnywhere(new ImmutableItem(995, amount));

        if (!player.getAchievements().isComplete(Achievements.Achievement.MASTER_THIEF1)) {
            Achievements.increase(player, AchievementType.THIEV, 2);
        }
    }

    public static void giveChestLoot(Player player) {
        int amount = Misc.random(2500, 3000);
        amount *= (player.nextPlunderRoomId + 1);

        int experience = 500;
        experience += (player.nextPlunderRoomId * 100);

        player.getPA().addSkillXPMultiplied(experience, Skill.THIEVING.getId(), true);
        player.sendMessage("You search around inside the chest and find "+amount+" coins!");
        player.getInventory().addAnywhere(new ImmutableItem(995, amount));

        if (Misc.random(0, 100) > 90 && player.nextPlunderRoomId == 8) {
            player.getItems().addItem(23071, 1);
        }

        if (!player.getAchievements().isComplete(Achievements.Achievement.MASTER_THIEF1)) {
            Achievements.increase(player, AchievementType.THIEV, player.nextPlunderRoomId*4);
        }
    }

    public static void giveSarcophLoot(Player player) {
        int amount = Misc.random(1000, 1500);
        amount *= (player.nextPlunderRoomId + 1);

        int experience = 300;
        experience += (player.nextPlunderRoomId * 100);

        player.getPA().addSkillXPMultiplied(experience, Skill.THIEVING.getId(), true);
        player.sendMessage("You search around inside the sarcophagus and find " + Misc.formatCoins(amount) + " coins!");
        player.getInventory().addAnywhere(new ImmutableItem(995, amount));

        if (!player.getAchievements().isComplete(Achievements.Achievement.MASTER_THIEF1)) {
            Achievements.increase(player, AchievementType.THIEV, player.nextPlunderRoomId*2);
        }
    }

    public static void searchUrn26580 (Player player) {
            if (player.loot26580) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            if (Misc.random(1, 2) == 1) {
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                urnXp(player);
                player.loot26580 = true;
            } else {
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);
                player.loot26580 = true;
            }
    }
    public static void searchUrn26600 (Player player) {
            if (player.loot26600) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            if (Misc.random(1, 2) == 1) {
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                urnXp(player);
                player.loot26600 = true;
            } else {
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);
                player.loot26600 = true;
            }
    }
    public static void searchUrn26601 (Player player) {
            if (player.loot26601) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            if (Misc.random(1, 2) == 1) {
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                urnXp(player);
                player.loot26601 = true;
            } else {
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);
                player.loot26601 = true;
            }
    }
    public static void searchUrn26602 (Player player) {
            if (player.loot26602) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26602 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26602 = true;
            }
    }
    public static void searchUrn26603 (Player player) {
            if (player.loot26603) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26603 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26603 = true;
            }
    }
    public static void searchUrn26604 (Player player) {
            if (player.loot26604) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26604 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26604 = true;
            }
    }
    public static void searchUrn26605 (Player player) {
            if (player.loot26605) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26605 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26605 = true;
            }
    }
    public static void searchUrn26606 (Player player) {
            if (player.loot26606) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26606 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26606 = true;
            }
    }
    public static void searchUrn26607 (Player player) {
            if (player.loot26607) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26607 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26607 = true;
            }
    }
    public static void searchUrn26608 (Player player) {
            if (player.loot26608) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26608 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26608 = true;
            }
    }
    public static void searchUrn26609 (Player player) {
            if (player.loot26609) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26609 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26609 = true;
            }
    }
    public static void searchUrn26610 (Player player) {
            if (player.loot26610) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26610 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26610 = true;
            }
    }
    public static void searchUrn26611 (Player player) {
            if (player.loot26611) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26611 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26611 = true;
            }
    }
    public static void searchUrn26612 (Player player) {
            if (player.loot26612) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26612 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26612 = true;
            }
    }
    public static void searchUrn26613 (Player player) {
            if (player.loot26613) {
                player.sendMessage("You've already searched this urn.");
                return;
            }
            
            player.sendMessage("You search the urn..");
            player.startAnimation(4340);
            
            if (Misc.random(1, 2) == 1) {
                
                player.sendMessage("You find an artifact inside.");
                giveUrnLoot(player);
                player.startAnimation(4342);
                
                urnXp(player);

                player.loot26613 = true;
            } else {
                
                player.sendMessage("You find nothing inside of the urn.");
                player.getPA().addSkillXPMultiplied(5, Skill.THIEVING.getId(), true);



                player.loot26613 = true;
            }
    }
    public static void searchUrn26616 (Player player) {
            if (player.loot26616) {
                player.sendMessage("You've already searched this chest.");
                return;
            }
            
            player.startAnimation(4340);
            
            
            giveChestLoot(player);
            player.startAnimation(4342);


            if (player.nextPlunderRoomId == 8 && Misc.random(0,1000) > 995) {
            }
            if (player.nextPlunderRoomId == 8) {
                PetHandler.roll(player,PetHandler.Pets.GIANT_SQUIRREL);
                int random = Misc.random(200);
                if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33116) &&
                        (Misc.random(0, 100) > (((player.getItems().isWearingItem(10557)  || player.hasEquippedSomewhere(29489)) ||
                                player.getItems().isWearingItem(22954) || player.getItems().isWearingItem(33403)) ? 80 : 95)))  {
                    random = 1;
                }
                if (player.getItems().isWearingItem(10557) && Misc.random(100) >= 85) {
                    random = 1;
                } else if (player.getItems().isWearingItem(22954 ) || player.getItems().isWearingItem(33403) && Misc.random(100) >= 85) {
                    random = 1;
                }
                if (random == 1) {
                    player.getItems().addItemUnderAnyCircumstance(PetHandler.Pets.GIANT_SQUIRREL.getItemId(), 1);
                    player.getCollectionLog().handleDrop(player, 5, PetHandler.Pets.GIANT_SQUIRREL.getItemId(), 1);
                    PlayerHandler.executeGlobalMessage("@red@" + player.getDisplayNameFormatted()
                            + " has received a pet drop from Pyramid Plunder.");
                }
            }
            
            player.loot26616 = true;
    }
    public static void searchUrn26626 (Player player) {
            if (player.loot26626) {
                player.sendMessage("You've already searched this sarcophagus.");
                return;
            }
            
            player.startAnimation(4340);

            if (Misc.random(1, 2) == 1) {
                giveSarcophLoot(player);
                player.startAnimation(4342);
            } else {
                player.sendMessage("You trigger a trap.");
                player.appendDamage(player.getHealth().getCurrentHealth()-1, HitMask.HIT);
            }
        player.loot26626 = true;
    }

    public static void disarmTrap(Player player) {
        double chance = 0.5 + (double) (player.getLevel(Skill.THIEVING) - 10) * 0.01;
            if (player.disarmedPlunderRoomTrap) {
                player.sendMessage("This trap is already disabled.");
                return;
            }
            
            player.sendMessage("You start to disarm the trap..");
            player.startAnimation(4340);
            
            if (Misc.get() > Math.min(chance, 0.85)) {
                player.getPA().sendSound(2386);
                player.sendMessage("You slip and trigger the trap.");
                player.appendDamage(Misc.random(1, 6), HitMask.HIT);
                player.startAnimation(1113);
            } else {
                player.sendMessage("You successfully disarm the trap.");
                player.disarmedPlunderRoomTrap = true;
                player.getPA().sendSound(2387);
                player.startAnimation(4342);
                if (player.nextPlunderRoomId == 1) {
                    player.setForceMovement(player.getX(), player.getY()-3, 0, 100, "SOUTH", 762);
                    player.getPA().addSkillXPMultiplied(10, Skill.THIEVING.getId(), true);
                }
                if (player.nextPlunderRoomId == 2) {
                    player.setForceMovement(player.getX(), player.getY()-3, 0, 100, "SOUTH", 762);
                    player.getPA().addSkillXPMultiplied(12, Skill.THIEVING.getId(), true);
                }
                if (player.nextPlunderRoomId == 3) {
                    player.setForceMovement(player.getX(), player.getY()-3, 0, 100, "SOUTH", 762);
                    player.getPA().addSkillXPMultiplied(15, Skill.THIEVING.getId(), true);
                }
                if (player.nextPlunderRoomId == 4) {
                    player.setForceMovement(player.getX()+3, player.getY(), 0, 100, "EAST", 762);
                    player.getPA().addSkillXPMultiplied(18, Skill.THIEVING.getId(), true);
                }
                if (player.nextPlunderRoomId == 5) {
                    player.setForceMovement(player.getX()-3, player.getY(), 0, 100, "WEST", 762);
                    player.getPA().addSkillXPMultiplied(21, Skill.THIEVING.getId(), true);
                }
                if (player.nextPlunderRoomId == 6) {
                    player.setForceMovement(player.getX(), player.getY()+3, 0, 100, "NORTH", 762);
                    player.getPA().addSkillXPMultiplied(24, Skill.THIEVING.getId(), true);
                }
                if (player.nextPlunderRoomId == 7) {
                    player.setForceMovement(player.getX(), player.getY()+3, 0, 100, "NORTH", 762);
                    player.getPA().addSkillXPMultiplied(27, Skill.THIEVING.getId(), true);
                }
                if (player.nextPlunderRoomId == 8) {
                    player.setForceMovement(player.getX(), player.getY()+3, 0, 100, "NORTH", 762);
                    player.getPA().addSkillXPMultiplied(30, Skill.THIEVING.getId(), true);
                }
            }
    }

    public static void urnXp(Player player) {
        int amount = 50;
        amount += (player.nextPlunderRoomId * 100);
        player.getPA().addSkillXPMultiplied(amount, Skill.THIEVING.getId(), true);
    }
}
