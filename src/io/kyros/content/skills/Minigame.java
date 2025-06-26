package io.kyros.content.skills;

import io.kyros.Configuration;
import io.kyros.annotate.PostInit;
import io.kyros.content.combat.HitMask;
import io.kyros.content.skills.mining.Pickaxe;
import io.kyros.content.skills.woodcutting.Hatchet;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.queue.RepeatingEntityTask;
import io.kyros.model.items.ItemAction;
import io.kyros.util.Misc;

public class Minigame {

    private static final int Mining_Smithing = 6562;
    private static final int Fishing_Cooking = 11225;
    private static final int Slayer_Thieving = 13426;
    private static final int Farming_Herblore = 13241;
    private static final int Runecrafting_Crafting = 5810;
    private static final int Woodcutting_Firemaking = 3653;
    private static final int Fletching_Hunter = 7559;

    public static boolean SpecialNPC(NPC npc) {
        return npc.getNpcId() == Mining_Smithing || npc.getNpcId() == Fishing_Cooking || npc.getNpcId() == Slayer_Thieving || npc.getNpcId() == Farming_Herblore || npc.getNpcId() == Runecrafting_Crafting || npc.getNpcId() == Woodcutting_Firemaking || npc.getNpcId() == Fletching_Hunter;
    }

    public static boolean handleSkillingNPC(Player player, NPC npc) {
        return DoCombatSkilling(player, npc);
    }

    private static boolean DoCombatSkilling(Player player, NPC npc) {
        int animation = 423;
        Skill skill;
        if (npc.getNpcId() == Mining_Smithing) {
            Pickaxe pickaxe = Pickaxe.getBestPickaxe(player);
            boolean hammer = player.getItems().hasItemOnOrInventory(2347);
            if (pickaxe == null && !hammer) {
                player.sendErrorMessage("You need a pickaxe/hammer to attack this monster.");
                return true;
            }
            if (pickaxe != null) {
                skill = Skill.MINING;
                animation = pickaxe.getAnimation();
            } else {
                skill = Skill.SMITHING;
                animation = 898;
            }
            handleNPCAttack(player, npc, animation, skill);
            return true;
        } else if (npc.getNpcId() == Fishing_Cooking) {
            boolean harpoon = player.getItems().hasItemOnOrInventory(311);
            boolean gauntlets = player.getItems().hasItemOnOrInventory(775);
            if (!harpoon && !gauntlets) {
                player.sendErrorMessage("You need a harpoon or cooking gauntlets.");
                return true;
            }
            if (harpoon) {
                animation = 618;
                skill = Skill.FISHING;
            } else {
                animation = 896;
                skill = Skill.COOKING;
            }

            handleNPCAttack(player, npc, animation, skill);
            return true;
        } else if (npc.getNpcId() == Slayer_Thieving) {
            boolean tool = player.getItems().hasItemOnOrInventory(2631);

            if (!tool && !player.getItems().isWearingAnyItem(player.SLAYER_HELMETS) && !player.getItems().isWearingAnyItem(player.IMBUED_SLAYER_HELMETS)) {
                player.sendErrorMessage("You need a slayer helm or highway man mask to deal damage to this monster.");
                return true;
            }
            if (tool) {
                animation = 881;
                skill = Skill.THIEVING;
            } else {
                animation = 10655;
                skill = Skill.SLAYER;
            }

            handleNPCAttack(player, npc, animation, skill);
            return true;
        } else if (npc.getNpcId() == Farming_Herblore) {
            boolean spade = player.getItems().hasItemOnOrInventory(952);
            boolean sec = player.getItems().hasItemOnOrInventory(7409,7410);
            if (!spade && !sec) {
                player.sendErrorMessage("You need a spade or secateurs to deal damage to this monster.");
                return true;
            }

            if (spade) {
                animation = 830;
                skill = Skill.FARMING;
            } else {
                animation = 363;
                skill = Skill.HERBLORE;
            }
            handleNPCAttack(player, npc, animation, skill);
            return true;
        } else if (npc.getNpcId() == Runecrafting_Crafting) {
            boolean tiara = player.getItems().hasItemOnOrInventory(5525,5527,5529,5531,5533,5535,5537,5539,5541,5543,5545,5547,5549,9106,22121);
            boolean chisel = player.getItems().hasItemOnOrInventory(1755);
            if (!tiara && !chisel) {
                player.sendErrorMessage("You need a chisel or a tiara to deal damage to this monster.");
                return true;
            }

            if (tiara) {
                animation = 791;
                skill = Skill.RUNECRAFTING;
            } else {
                animation = 886;
                skill = Skill.CRAFTING;
            }
            handleNPCAttack(player, npc, animation, skill);
            return true;
        } else if (npc.getNpcId() == Woodcutting_Firemaking) {
            boolean tinderbox = player.getItems().hasItemOnOrInventory(590);
            Hatchet hatchet = Hatchet.getBest(player);
            if (hatchet == null && !tinderbox) {
                player.sendErrorMessage("You need a hatchet or a tinderbox to attack this monster.");
                return true;
            }
            if (hatchet != null) {
                animation = hatchet.getAnimation();
                skill = Skill.WOODCUTTING;
            } else {
                animation = 733;
                skill = Skill.FIREMAKING;
            }
            handleNPCAttack(player, npc, animation, skill);
            return true;
        } else if (npc.getNpcId() == Fletching_Hunter) {
            boolean net = player.getItems().hasItemOnOrInventory(11259, 10010);
            boolean knife = player.getItems().hasItemOnOrInventory(946);
            if (!net && !knife) {
                player.sendErrorMessage("You need a butterfly net or a knife to attack this monster.");
                return true;
            }
            if (net) {
                animation = 6605;
                skill = Skill.HUNTER;
            } else {
                animation = 1248;
                skill = Skill.FLETCHING;
            }
            handleNPCAttack(player, npc, animation, skill);
            return true;
        }
        return false;
    }


    private static void handleNPCAttack(Player player, NPC npc, int animation, Skill skill) {
        if (!player.skillingMinigame) {
            player.sendErrorMessage("You've not unlocked the secrets yet, try finishing slayer tasks.");
            player.getPA().startTeleport(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0, "modern", false);
            return;
        }

        if (player.underAttackByNpc > 0) {
            player.sendErrorMessage("Looks like you're already in combat!");
            return;
        }

        CycleEventHandler.getSingleton().stopEvents(player);

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (npc.getHealth().getCurrentHealth() <= 0 || npc.isDeadOrDying()) {
                    container.stop();
                    return;
                }
                npc.appendDamage(player, Misc.random(5,10), HitMask.HIT);
                player.getPA().addSkillXPMultiplied(270, skill.getId(), true);
                player.startAnimation(animation);
                npc.facePlayer(player.getIndex());
                npc.attackEntity(player);
                getEmblem(player, skill);
            }
        }, 2);
    }

    private static void getEmblem(Player player, Skill skill) {
        if (skill == Skill.MINING || skill == Skill.SMITHING || skill == Skill.FIREMAKING || skill == Skill.WOODCUTTING || skill == Skill.FISHING || skill == Skill.COOKING || skill == Skill.CRAFTING) {
            int[] emblems = {27997, 27999, 28001, 28003};
            if (Misc.random(0, 200) == 1) {
                int emblem = emblems[Misc.random(emblems.length - 1)];
                if (player.getItems().hasInBank(27997) ||
                        player.getItems().hasInBank(27999) ||
                        player.getItems().hasInBank(28001) ||
                        player.getItems().hasInBank(28003) ||
                        player.getItems().hasItemOnOrInventory(27997, 27999, 28001, 28003)) {
                    return;
                }
                player.getItems().addItemUnderAnyCircumstance(emblem, 1);
            }
        } else if (skill == Skill.RUNECRAFTING || skill == Skill.HUNTER || skill == Skill.FLETCHING || skill == Skill.FARMING || skill == Skill.HERBLORE || skill == Skill.SLAYER || skill == Skill.THIEVING) {
            int[] emblems = {28005, 28007, 28009, 28011};
            if (Misc.random(0, 200) == 1) {
                int emblem = emblems[Misc.random(emblems.length - 1)];
                if (player.getItems().hasInBank(28005) ||
                        player.getItems().hasInBank(28007) ||
                        player.getItems().hasInBank(28009) ||
                        player.getItems().hasInBank(28011) ||
                        player.getItems().hasItemOnOrInventory(28005, 28007, 28009, 28011)) {
                    return;
                }
                player.getItems().addItemUnderAnyCircumstance(emblem, 1);
            }
        }
    }

    @PostInit
    public static void handleItem() {
        ItemAction.registerInventory(11640, 1, (player, item) -> {
            if (player.skillingMinigame) {
                player.sendErrorMessage("You've already learned the secret of the Skilling Area!");
                return;
            }

            player.sendErrorMessage("You've have just learned about a secret Skilling Area!");
            player.skillingMinigame = true;
            player.gfx0(1880);
            player.getItems().deleteItem2(11640, 1);//5525
        });
    }
}
