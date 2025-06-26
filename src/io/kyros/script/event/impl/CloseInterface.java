package io.kyros.script.event.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.script.event.PlayerEvent;

public class CloseInterface extends PlayerEvent {
    public CloseInterface(Player player, int widget) {
        super(player);
        this.widget = widget;
    }

    public int getWidget() {
        return widget;
    }

    private final int widget;
}
