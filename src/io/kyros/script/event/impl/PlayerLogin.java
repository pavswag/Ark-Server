package io.kyros.script.event.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.script.event.PlayerEvent;

public class PlayerLogin extends PlayerEvent {
    public PlayerLogin(Player player) {
        super(player);
    }
}
