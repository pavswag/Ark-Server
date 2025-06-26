package io.kyros.model.multiplayersession.flowerpoker;

import io.kyros.model.Items;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.lock.CompleteLock;

public class FlowerPokerLock extends CompleteLock {
    @Override
    public boolean cannotClickItem(Player player, int itemId) {
        if (itemId == Items.MITHRIL_SEEDS)
            return false;
        return true;
    }
}
