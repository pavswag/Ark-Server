package io.kyros.content.bosses.sharathteerk;

import io.kyros.annotate.PostInit;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.items.ItemAction;

public class SharItems {

    @PostInit
    public static void RegisterItems() {
        ItemAction.registerInventory(26879, 1, (player, item) -> {
            if (player.getItems().playerHasItem(26879, 1)) { // Wraith Essence
                player.start(new DialogueBuilder(player)
                        .option("Which weapon would you like to upgrade?",
                                new DialogueOption("Demon X Staff (2b MadPoints & 1750x Wraith Essence)", p -> {
                                    if (p.getItems().getInventoryCount(33205) == 0) {
                                        p.sendMessage("@red@You don't have any Demon X Staffs to upgrade!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    if (p.getItems().getInventoryCount(26879) < 1750) {
                                        p.sendMessage("@red@You need 1750 Wraith Essences to upgrade your Demon X Staff!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    if (p.foundryPoints < 2_000_000_000) {
                                        p.sendMessage("@red@You require 2b MadPoints to upgrade your Demon X Staff!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    p.getItems().deleteItem2(33205, 1);
                                    p.getItems().deleteItem2(26879, 1750);
                                    p.foundryPoints -= 2_000_000_000;
                                    p.getItems().addItemUnderAnyCircumstance(33433, 1);
                                    String msg = "@blu@@cr18@[UPGRADE]@cr18@@red@ " + p.getDisplayName()
                                            + " Has successfully achieved Wraith Staff!";
                                    PlayerHandler.executeGlobalMessage(msg);
                                    p.getPA().closeAllWindows();
                                }),
                                new DialogueOption("Demon X Spear (2b MadPoints & 250x Wraith Essence)", p -> {
                                    if (p.getItems().getInventoryCount(33204) == 0) {
                                        p.sendMessage("@red@You don't have any Demon X Spears to upgrade!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    if (p.getItems().getInventoryCount(26879) < 250) {
                                        p.sendMessage("@red@You need 250 Wraith Essences to upgrade your Demon X Spear!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    if (p.foundryPoints < 2_000_000_000) {
                                        p.sendMessage("@red@You require 2b MadPoints to upgrade your Demon X Spear!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    p.getItems().deleteItem2(33204, 1);
                                    p.getItems().deleteItem2(26879, 250);
                                    p.foundryPoints -= 2_000_000_000;
                                    p.getItems().addItemUnderAnyCircumstance(33432, 1);
                                    String msg = "@blu@@cr18@[UPGRADE]@cr18@@red@ " + p.getDisplayName()
                                            + " Has successfully achieved Wraith Spear!";
                                    PlayerHandler.executeGlobalMessage(msg);
                                    p.getPA().closeAllWindows();
                                }),
                                new DialogueOption("Demon X Bow (2b MadPoints & 1750x Wraith Essence)", p -> {
                                    if (p.getItems().getInventoryCount(33207) == 0) {
                                        p.sendMessage("@red@You don't have any Demon X Bows to upgrade!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    if (p.getItems().getInventoryCount(26879) < 1750) {
                                        p.sendMessage("@red@You need 1750 Wraith Essences to upgrade your Demon X Bow!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    if (p.foundryPoints < 2_000_000_000) {
                                        p.sendMessage("@red@You require 2b MadPoints to upgrade your Demon X Bow!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    p.getItems().deleteItem2(33207, 1);
                                    p.getItems().deleteItem2(26879, 1750);
                                    p.foundryPoints -= 2_000_000_000;
                                    p.getItems().addItemUnderAnyCircumstance(33434, 1);
                                    String msg = "@blu@@cr18@[UPGRADE]@cr18@@red@ " + p.getDisplayName()
                                            + " Has successfully achieved Wraith Bow!";
                                    PlayerHandler.executeGlobalMessage(msg);
                                    p.getPA().closeAllWindows();
                                }),
                                new DialogueOption("Demon X Crossbow (2b MadPoints & 1750x Wraith Essence)", p -> {
                                    if (p.getItems().getInventoryCount(26269) == 0) {
                                        p.sendMessage("@red@You don't have any Demon X Crossbows to upgrade!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    if (p.getItems().getInventoryCount(26879) < 1750) {
                                        p.sendMessage("@red@You need 1750 Wraith Essences to upgrade your Demon X Crossbow!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    if (p.foundryPoints < 2_000_000_000) {
                                        p.sendMessage("@red@You require 2b MadPoints to upgrade your Demon X Crossbow!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    p.getItems().deleteItem2(26269, 1);
                                    p.getItems().deleteItem2(26879, 1750);
                                    p.foundryPoints -= 2_000_000_000;
                                    p.getItems().addItemUnderAnyCircumstance(33435, 1);
                                    String msg = "@blu@@cr18@[UPGRADE]@cr18@@red@ " + p.getDisplayName()
                                            + " Has successfully achieved Wraith Crossbow!";
                                    PlayerHandler.executeGlobalMessage(msg);
                                    p.getPA().closeAllWindows();
                                }),
                                new DialogueOption("Demon X Scythe (2b MadPoints & 1750x Wraith Essence)", p -> {
                                    if (p.getItems().getInventoryCount(33203) == 0) {
                                        p.sendMessage("@red@You don't have any Demon X Scythes to upgrade!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    if (p.getItems().getInventoryCount(26879) < 1750) {
                                        p.sendMessage("@red@You need 1750 Wraith Essences to upgrade your Demon X Scythe!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    if (p.foundryPoints < 2_000_000_000) {
                                        p.sendMessage("@red@You require 2b MadPoints to upgrade your Demon X Scythe!");
                                        p.getPA().closeAllWindows();
                                        return;
                                    }
                                    p.getItems().deleteItem2(33203, 1);
                                    p.getItems().deleteItem2(26879, 1750);
                                    p.foundryPoints -= 2_000_000_000;
                                    p.getItems().addItemUnderAnyCircumstance(33431, 1);
                                    String msg = "@blu@@cr18@[UPGRADE]@cr18@@red@ " + p.getDisplayName()
                                            + " Has successfully achieved Wraith Scythe!";
                                    PlayerHandler.executeGlobalMessage(msg);
                                    p.getPA().closeAllWindows();
                                })
                        )
                );
            } else {
                player.sendMessage("You do not have the required item.");
            }
        });
    }

}
