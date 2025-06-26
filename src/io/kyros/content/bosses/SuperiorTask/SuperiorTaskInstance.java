package io.kyros.content.bosses.SuperiorTask;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class SuperiorTaskInstance extends LegacySoloPlayerInstance {

    public static final Boundary boundary = new Boundary(2507, 9285, 2535, 9309);
    private static final Position spawn = new Position(2530, 9294,0);

    public SuperiorTaskInstance(Player player) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }

    public static void handleDialogue(Player player, NPC npc) {
        if (player.getItems().playerHasItem(27997) || player.getItems().playerHasItem(28013)) {
            player.start(new DialogueBuilder(player).option("Emblems",
                    new DialogueOption("T9 Emblem", p -> {
                        if (!(p.getItems().playerHasItem(27997)
                                && p.getItems().playerHasItem(27999)
                                && p.getItems().playerHasItem(28001)
                                && p.getItems().playerHasItem(28003)
                                && p.getItems().playerHasItem(28005)
                                && p.getItems().playerHasItem(28007)
                                && p.getItems().playerHasItem(28009)
                                && p.getItems().playerHasItem(28011))) {
                            p.sendErrorMessage("You need T1 > T8 Emblems to make the T9 Emblem.");
                            p.getPA().closeAllWindows();
                            return;
                        }
                        p.getItems().deleteItem2(27997, 1);
                        p.getItems().deleteItem2(27999, 1);
                        p.getItems().deleteItem2(28001, 1);
                        p.getItems().deleteItem2(28003, 1);
                        p.getItems().deleteItem2(28005, 1);
                        p.getItems().deleteItem2(28007, 1);
                        p.getItems().deleteItem2(28009, 1);
                        p.getItems().deleteItem2(28011, 1);
                        p.getItems().addItemUnderAnyCircumstance(28013, 1);
                        p.sendMessage("You have made the T9 Emblem");
                        p.getPA().closeAllWindows();
                    }),
                    new DialogueOption("Never mind", p -> p.getPA().closeAllWindows())
            ));
        } else {
            if (!player.unlockedSpecialTasks) {
                player.start(new DialogueBuilder(player).option("Would you like to unlock the Champions?",
                        new DialogueOption("Yes", p -> {
                            if (!p.getItems().playerHasItem(28807)) {
                                p.getPA().closeAllWindows();
                                p.sendErrorMessage("You don't have the special item to unlock the Champions!");
                                return;
                            }
                            p.getItems().deleteItem2(28807, 1);
                            p.unlockedSpecialTasks = true;
                            p.getPA().closeAllWindows();
                        }),
                        new DialogueOption("Never mind.", p -> p.getPA().closeAllWindows())));
                return;
            }

            if (player.specialTaskNpc == -1 || player.specialTaskAmount <= 0) {
                player.start(new DialogueBuilder(player).option("Would you like a new task ?", new DialogueOption("Yes", p -> {
                    p.getPA().closeAllWindows();
                    p.start(new DialogueBuilder(player). option("Which task would you like?", new DialogueOption("Range Task", plr -> {
                        p.getPA().closeAllWindows();
                        p.specialTaskNpc = 3358;
                        p.specialTaskAmount = Misc.random(100,300);
                        p.start(new DialogueBuilder(p).statement("You have been assigned to kill " + NpcDef.forId(3358).getName() + " x " + p.specialTaskAmount, "Speak to me again to teleport to your task!"));
                    }), new DialogueOption("Mage Task", plr -> {
                        p.getPA().closeAllWindows();
                        p.specialTaskNpc = 3353;
                        p.specialTaskAmount = Misc.random(100,300);
                        p.start(new DialogueBuilder(p).statement("You have been assigned to kill " + NpcDef.forId(3353).getName() + " x " + p.specialTaskAmount, "Speak to me again to teleport to your task!"));
                    }), new DialogueOption("Nevermind.", plr -> plr.getPA().closeAllWindows())));

                }), new DialogueOption("Nevermind.", p -> p.getPA().closeAllWindows())));
            } else {
                player.start(new DialogueBuilder(player).option("Would like to begin your instance?", new DialogueOption("Yes", p -> {
                    p.getPA().closeAllWindows();
                    SuperiorTaskInstance superiorTaskInstance = new SuperiorTaskInstance(p);
                    enter(p, superiorTaskInstance);
                }), new DialogueOption("Nevermind.", p -> p.getPA().closeAllWindows())));
            }
        }
    }

    public static void enter(Player player, SuperiorTaskInstance instances) {

        if (InstancedArea.isPlayerInSameInstanceType(player, SuperiorTaskInstance.class)) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }
        if (player.specialTaskNpc == -1 || player.specialTaskAmount <= 0) {
            player.sendMessage("You need a Special task before you can use this area!");
            return;
        }

        try {
            final int NPCID = player.specialTaskNpc;

            if (NPCID == 0) {
                player.sendMessage("Unfortunately you don't have a task which is allowed here.");
                return;
            }

            int instanceSize = player.specialTaskAmount;

            // Calculate available space within the boundary
            int minX = 2513;
            int minY = 9290;
            int maxX = 2529;
            int maxY = 9303;
            int availableWidth = maxX - minX;
            int availableHeight = maxY - minY;

            // Calculate the maximum number of NPCs that can fit based on NPC size and spacing
            int maxNpcsWidth = availableWidth / (1 + NpcDef.forId(NPCID).getSize());
            int maxNpcsHeight = availableHeight / (1 + NpcDef.forId(NPCID).getSize());
            int maxNpcs = maxNpcsWidth * maxNpcsHeight;
            // Spawn NPCs within the boundary with appropriate spacing
            for (int i = 0; i < maxNpcs; i++) {
                int x = minX + (i % maxNpcsWidth) * (1 + NpcDef.forId(NPCID).getSize());
                int y = minY + (i / maxNpcsWidth) * (1 + NpcDef.forId(NPCID).getSize());

                // Create and spawn NPC with appropriate size
                NPC slayer_npc = NPCSpawning.spawnNpc(player, NPCID, x,y, instances.getHeight(),0,0,false,false);
                slayer_npc.spawnedBy = player.getIndex();
                slayer_npc.getBehaviour().setRespawn(true);
                slayer_npc.getBehaviour().setRespawnWhenPlayerOwned(true);
                instances.add(slayer_npc);
            }

            player.moveTo(new Position(spawn.getX(), spawn.getY(), instances.getHeight()));
            instances.add(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
