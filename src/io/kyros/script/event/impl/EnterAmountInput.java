package io.kyros.script.event.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.script.event.PlayerEvent;

public class EnterAmountInput extends PlayerEvent {
    public EnterAmountInput(Player player, int inputtedAmount, int widget) {
        super(player);
        this.inputtedAmount = inputtedAmount;
        this.widget = widget;
    }

    public int getWidget() {
        return widget;
    }

    private final int widget;
    private final int inputtedAmount;

    public int getInputtedAmount() {
        return this.inputtedAmount;
    }
}
