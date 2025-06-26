package io.kyros.content.itemskeptondeath.modifiers;

import io.kyros.content.bosses.wildypursuit.FragmentOfSeren;
import io.kyros.content.bosses.wildypursuit.TheUnbearable;
import io.kyros.content.itemskeptondeath.DeathItemModifier;
import io.kyros.content.minigames.bounty_hunter.BountyHunterEmblem;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Items contained in the {@link AlwaysLostDeathItem#getItemIds()} are always dropped on death even if they
 * would have otherwise been kept.
 */
public class AlwaysLostDeathItem implements DeathItemModifier {

    private static final Set<Integer> ALL;

    static {
        ALL = new HashSet<>();
        ALL.add(TheUnbearable.KEY);
        ALL.add(FragmentOfSeren.KEY);

        Arrays.stream(BountyHunterEmblem.values()).forEach(it -> ALL.add(it.getItemId()));
    }

    public static Set<Integer> items() {
        return ALL;
    }

    @Override
    public Set<Integer> getItemIds() {
        return ALL;
    }

    @Override
    public void modify(Player player, GameItem gameItem, boolean kept, List<GameItem> keptItems, List<GameItem> lostItems) {
        if (!kept)
            return;
        keptItems.remove(gameItem);
        lostItems.add(gameItem);
    }
}
