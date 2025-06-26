package io.kyros.script.event.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.script.event.PlayerEvent;

public class PlayerVoted extends PlayerEvent {
    public PlayerVoted(Player player) {
        super(player);
    }
}
