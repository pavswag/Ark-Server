package io.kyros.script.event.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.script.event.PlayerEvent;

public class PlayerLogout extends PlayerEvent {
    public PlayerLogout(Player player) {
        super(player);
    }
}
