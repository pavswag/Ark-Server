package io.kyros.content.bosses.mimic;

import io.kyros.model.Items;
import io.kyros.model.entity.player.Player;

public class StrangeCasketDialogue {

    public static void open(Player player) {
        if (!player.getItems().playerHasItem(Items.MIMIC)) {
            player.sendMessage("You seem to be missing The Mimic casket.");
            return;
        }
        player.getDH().sendStatement("The Mimic has... found you!", "Will you fight now?");
        player.nextChat = 2000;
    }
}
