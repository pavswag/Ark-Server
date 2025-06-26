package io.kyros.model;

import io.kyros.model.entity.player.Player;

public interface AmountInput {
    void handle(Player player, int amount);
}
