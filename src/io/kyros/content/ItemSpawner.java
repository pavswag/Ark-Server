package io.kyros.content;

import io.kyros.Server;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;

public class ItemSpawner {

    public static final int INTERFACE_ID = 43214;
    public static final int CONTAINER_ID = 43218;
    private static final int TEXT_INTERFACE_ID = 43216;

    public static void open(Player player) {
        if (Server.isTest() || player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            player.getPA().sendFrame126("", TEXT_INTERFACE_ID, true);
            player.getPA().showInterface(INTERFACE_ID);
        }
    }

    public static void spawn(Player player, int itemId, int amount) {
        if (amount == -1) {
            player.getPA().sendEnterAmount("Enter the amount of items to spawn", (p, enteredAmount) -> spawn(p, itemId, enteredAmount));
            return;
        }

        if (player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            player.getItems().addItem(itemId, amount);
            player.sendMessage("@dre@Spawned x" + amount + " " + ItemDef.forId(itemId).getName() + ".");
        }
    }

}
