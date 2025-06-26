package io.kyros.model.entity.npc.actions;

import io.kyros.content.battlepass.Pass;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.skills.fishing.ArielFishing;
import io.kyros.model.entity.npc.NPCAction;
import io.kyros.model.entity.player.PlayerHandler;

public class CustomActions {

    public static void loadActions() {
        ArielFishing.init();

        NPCAction.register(827, 1, (player, npc) -> {
            player.start(new DialogueBuilder(player).statement("WoooHooo registered handling for npcs!#1"));
            npc.forceChat("Force chat working");
            npc.startAnimation(9170);
        });
        NPCAction.register(827, 2, (player, npc) -> {
            player.start(new DialogueBuilder(player).statement("WoooHooo registered handling for npcs!#2"));
            npc.forceChat("#2 Force chat");
            npc.startAnimation(8532);
        });

        NPCAction.register(315, 1, (player, npc) -> {
            if (player.isMember()) {
                player.start(new DialogueBuilder(player).npc(315, "You're already a division pass holder, good luck with your pass!"));
            } else {
                player.start(new DialogueBuilder(player).npc(315, "Welcome, I am Steve. I hold all the passes for the division,", "If you're interested in buying a pass I can sell you one",
                        "For a low low price of 1Bill MadPoints.").option("Buy division Premium pass for 1Bill MadPoints?",
                        new DialogueOption("Yes", p -> {
                            if (p.foundryPoints <= 1_000_000_000) {
                                p.start(new DialogueBuilder(player).npc(315, "You don't have a enough MadPoints to purchase a pass!"));
                                return;
                            }
                            Pass.grantMembership(p);
                            p.foundryPoints -= 1_000_000_000;
                            p.getPA().closeAllWindows();
                        }), new DialogueOption("Nevermind.", p -> p.getPA().closeAllWindows())));
            }
        });


        NPCAction.register(7950, 1, (player, npc) -> {
            player.start(new DialogueBuilder(player).option("What would you like to upgrade?",
                    new DialogueOption("Devout Boots", p -> {
                        if (player.getItems().getInventoryCount(10556) < 10 || player.foundryPoints < 100_000_000 || player.getItems().getInventoryCount(10557) < 10) {
                            player.start(new DialogueBuilder(player).npc(7950,"You need to bring me", "10 Attacker Icons, 10 Collector Icons, 100M MadPoints!"));
//                            player.getPA().closeAllWindows();
                            return;
                        }

                        int peg_amount = player.getItems().getInventoryCount(13237) + player.getItems().getInventoryCount(13238);
                        int prim_amount = player.getItems().getInventoryCount(13239) + player.getItems().getInventoryCount(13240);
                        int eter_amount = player.getItems().getInventoryCount(13235) + player.getItems().getInventoryCount(13236);

                        if (peg_amount >= 10) {
                            player.getItems().deleteItem2(13237, peg_amount);
                            player.getItems().deleteItem2(13238, peg_amount);
                            player.getItems().deleteItem2(10556, 10);
                            player.getItems().deleteItem2(10557, 10);
                            player.foundryPoints -= 100_000_000;
                            player.getItems().addItemUnderAnyCircumstance(22954, 1);
//                            player.getPA().closeAllWindows();

                            player.start(new DialogueBuilder(player).npc(7950,"Congratulations you have obtained a pair of", "Devout boots!"));
                            PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" + player.getDisplayName() + " has just obtained Devout boots!");
                            return;
                        } else if (prim_amount >= 10) {
                            player.getItems().deleteItem2(13239, prim_amount);
                            player.getItems().deleteItem2(13240, prim_amount);
                            player.getItems().deleteItem2(10556, 10);
                            player.getItems().deleteItem2(10557, 10);
                            player.foundryPoints -= 100_000_000;
                            player.getItems().addItemUnderAnyCircumstance(22954, 1);
//                            player.getPA().closeAllWindows();

                            player.start(new DialogueBuilder(player).npc(7950,"Congratulations you have obtained a pair of", "Devout boots!"));
                            PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" + player.getDisplayName() + " has just obtained Devout boots!");
                            return;
                        } else if (eter_amount >= 10) {
                            player.getItems().deleteItem2(13235, eter_amount);
                            player.getItems().deleteItem2(13236, eter_amount);
                            player.getItems().deleteItem2(10556, 10);
                            player.getItems().deleteItem2(10557, 10);
                            player.foundryPoints -= 100_000_000;
                            player.getItems().addItemUnderAnyCircumstance(22954, 1);
//                            player.getPA().closeAllWindows();

                            player.start(new DialogueBuilder(player).npc(7950,"Congratulations you have obtained a pair of", "Devout boots!"));
                            PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" + player.getDisplayName() + " has just obtained Devout boots!");
                            return;
                        } else {
                            player.start(new DialogueBuilder(player).npc(7950, "You need one of the following:",
                                    "10 Pairs of Pegasian boots", "10 Pairs of Eternal Boots", "10 Pairs of Primordial boots"));
                        }
                    }),
                    new DialogueOption("Juan Gloves", p-> {
                        if (player.getItems().getInventoryCount(27112) < 5 || player.getItems().getInventoryCount(10559) < 10 || player.getItems().getInventoryCount(10558) < 10 || player.foundryPoints < 100_000_000) {
                            player.start(new DialogueBuilder(player).npc(7950,"You need to bring me", "5 Barrows gloves (wrapped), 10 Healer Icons", "10 Defender Icons, 100M MadPoints!"));
//                            player.getPA().closeAllWindows();
                            return;
                        }

                        player.getItems().deleteItem2(27112, 5);
                        player.getItems().deleteItem2(10559, 10);
                        player.getItems().deleteItem2(10558, 10);
                        player.foundryPoints -= 100_000_000;
                        player.getItems().addItemUnderAnyCircumstance(13372,1);
//                        player.getPA().closeAllWindows();
                        player.start(new DialogueBuilder(player).npc(7950, "Congratulations you have obtained a pair of", "Juan gloves!"));
                        PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" + player.getDisplayName() + " has just obtained Juan gloves!");
                    }),
                    new DialogueOption("Ghommal's Hilt 5", p -> {
                        if (player.getItems().getInventoryCount(22322) < 5 || player.foundryPoints < 50_000_000) {
                            player.start(new DialogueBuilder(player).npc(7950, "You need to bring me", "5 Avernic Defenders, 50M MadPoints"));
                            return;
                        }

                        player.getItems().deleteItem2(22322, 5);
                        player.foundryPoints -= 50_000_000;
                        player.getItems().addItemUnderAnyCircumstance(25934,1);
                        player.start(new DialogueBuilder(player).npc(7950, "Congratulations you have obtained a", " Ghommal's Hilt 5!"));
                    }),
                    new DialogueOption("Ghommal's Hilt 6", p -> {
                        if (player.getItems().getInventoryCount(22322) < 10 || player.getItems().getInventoryCount(27550) < 1 || player.foundryPoints < 150_000_000) {
                            player.start(new DialogueBuilder(player).npc(7950, "You need to bring me", "10 Avernic Defenders, 1 Ghommal's avernic defender 5, 150M MadPoints!"));
                            return;
                        }

                        player.getItems().deleteItem2(22322, 10);
                        player.getItems().deleteItem2(27550, 1);
                        player.foundryPoints -= 150_000_000;
                        player.getItems().addItemUnderAnyCircumstance(25936,1);
                        player.start(new DialogueBuilder(player).npc(7950, "Congratulations you have obtained a", " Ghommal's Hilt 6!"));
                        PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" + player.getDisplayName() + " has just obtained Ghommal's Hilt 6!");
                    })));
        });

    }
}
