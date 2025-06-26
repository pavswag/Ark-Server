package io.kyros.content.keyboard_actions;

import io.kyros.model.entity.player.Player;

@FunctionalInterface
public interface KeyboardStrategy {
    void execute(Player player);
}
