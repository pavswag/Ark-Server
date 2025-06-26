package io.kyros.model.entity.player.lock;

import io.kyros.model.entity.player.Player;

public class TeleportLock implements PlayerLock {

    @Override
    public boolean cannotLogout(Player player) {
        return true;
    }

    @Override
    public boolean cannotInteract(Player player) {
        return true;
    }

    @Override
    public boolean cannotClickItem(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean cannotTeleport(Player player) {
        return false;
    }
}
