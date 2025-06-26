package io.kyros.script.event.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.script.event.PlayerEvent;

public class ButtonClick extends PlayerEvent {
    private final int buttonId;

    public ButtonClick(Player player, int buttonId) {
        super(player);
        this.buttonId = buttonId;
    }

    public int getButtonId() {
        return buttonId;
    }
}

