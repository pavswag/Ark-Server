package io.kyros.content.dialogue.impl;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.entity.player.Player;

public class AmethystChiselDialogue extends DialogueBuilder {


    public AmethystChiselDialogue(Player player) {
        super(player);
        setNpcId(5810);
        npc("Please select which Amethyst chisel", "method you would like to do.")
                .option(new DialogueOption("Bolt Tips.", p -> amethystBoltMethod(player)),
                        new DialogueOption("Arrow Tips.", p -> amethystArrowMethod(player)),
                        new DialogueOption("Javelin Heads", p -> amethystJavelinMethod(player)),
                        new DialogueOption("Dart Tips.", p -> amethystDartMethod(player)));

    }

    public void amethystBoltMethod(Player player ) {
        player.boltTips = true;
        player.arrowTips = false;
        player.javelinHeads = false;
        player.dartTips = false;
        player.sendMessage("Your Amethyst method is now bolt tips!");
        player.getPA().closeAllWindows();
        return;
    }

    public void amethystArrowMethod(Player player ) {
        player.boltTips = false;
        player.arrowTips = true;
        player.javelinHeads = false;
        player.dartTips = false;
        player.sendMessage("Your Amethyst method is now arrow tips!");
        player.getPA().closeAllWindows();
        return;
    }

    public void amethystJavelinMethod(Player player ) {
        player.boltTips = false;
        player.arrowTips = false;
        player.javelinHeads = true;
        player.dartTips = false;
        player.sendMessage("Your Amethyst method is now javelin heads!");
        player.getPA().closeAllWindows();
        return;
    }

    public void amethystDartMethod(Player player ) {
        player.boltTips = false;
        player.arrowTips = false;
        player.javelinHeads = false;
        player.dartTips = true;
        player.sendMessage("Your Amethyst method is now dart tips!");
        player.getPA().closeAllWindows();
        return;
    }

}
