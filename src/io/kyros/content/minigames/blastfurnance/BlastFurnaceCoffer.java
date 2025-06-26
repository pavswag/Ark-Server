package io.kyros.content.minigames.blastfurnance;

import com.google.gson.annotations.Expose;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.Items;
import io.kyros.model.entity.player.Player;

public class BlastFurnaceCoffer {

    @Expose
    private int amountInCoffer;

    public void depositToCoffer(int amount) {
        amountInCoffer += amount;
    }

    public void withdrawFromCoffer(int amount) {
        amountInCoffer -= amount;
    }

    public static void use(Player player) {
        player.start(new DialogueBuilder(player).option("Your coffer contains " + player.getBlastFurnace().getCoffer().getCoinsInCoffer() + " coins.",
                new DialogueOption("Deposit coins.", plr -> {
                    if (getAmountOfCoinsInInventory(plr) <= 0) {
                        plr.start(new DialogueBuilder(plr).statement("You don't have any coins to deposit!"));
                        return;
                    }
                    plr.getPA().sendEnterAmount("Enter how many coins to deposit!", (plr1, amount) -> {
                        if (amount > plr1.getItems().getInventoryCount(995)) {
                            amount = plr1.getItems().getInventoryCount(995);
                        }
                        plr1.getBlastFurnace().getCoffer().depositToCoffer(amount);
                        plr1.getItems().deleteItem2(995, amount);
                        plr1.getPA().closeAllWindows();
                    });
                }), new DialogueOption("Nevermind.", plr -> plr.getPA().closeAllWindows())));
    }

    private static int getAmountOfCoinsInInventory(Player player) {
        return player.getItems().getInventoryCount(Items.COINS);
    }

    public int getCoinsInCoffer() {
        return amountInCoffer;
    }

}
