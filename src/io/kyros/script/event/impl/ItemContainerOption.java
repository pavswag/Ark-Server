package io.kyros.script.event.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.script.event.PlayerEvent;
import lombok.Getter;

@Getter
public class ItemContainerOption extends PlayerEvent {
    public ItemContainerOption(Player player, int widget, int slot, int itemId, int option) {
        super(player);
        this.option = option;
        this.widget = widget;
        this.slot = slot;
        this.item = itemId;
    }

    private final int option;
    private final int widget;
    private final int slot;
    private final int item;

    public int getItemId() {
        return item + 1;
    }
}
