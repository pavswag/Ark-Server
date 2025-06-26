package io.kyros.model.entity.player.lock;

import io.kyros.model.entity.player.Player;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 27/02/2024
 */
public class ItemPickupLock implements PlayerLock {
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
        return true;
    }
}
