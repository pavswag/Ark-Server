package io.kyros.model.items;

import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 04/03/2024
 */
public interface ItemAction {

    void handle(Player player, GameItem item);

    static void registerInventory(int itemId, int option, ItemAction action) {
        ItemDef def = ItemDef.forId(itemId);
        if(def.inventoryActions == null)
            def.inventoryActions = new ItemAction[5];
        def.inventoryActions[option - 1] = action;
    }

}
