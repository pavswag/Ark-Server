package io.kyros.content.itemskeptondeath;

import io.kyros.content.combat.melee.CombatPrayer;
import io.kyros.content.combat.melee.Prayer;
import io.kyros.content.itemskeptondeath.modifiers.AlwaysLostDeathItem;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemsLostOnDeath {

    public static int getKeptItemAmount(Player player) {
        int amount = 3;
        if (player.isSkulled)
            amount -= 3;
        if (player.getCombatPrayer().isPrayerActive(Prayer.PROTECT_ITEM.getId()))
            amount += 1;
        if (player.getCombatPrayer().isPrayerActive(Prayer.PROTECT_ITEM_RUINOUS.getId()))
            amount += 2;
        if (player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)))
            amount = 0;
        return amount;
    }

    /**
     * Generates a {@link ItemsLostOnDeathList} for all the player's carried and equipped items.
     */
    public static ItemsLostOnDeathList generate(Player player) {
        Inventory keptItems = new Inventory(64);
        Inventory lostItems = new Inventory(64);

        DeathItemStack stack = new DeathItemStack();
        stack.create(player);

        stack.pop(getKeptItemAmount(player)).forEach(keptItems::add);
        stack.popRemaining().forEach(lostItems::add);

        List<GameItem> kept = keptItems.buildList();
        List<GameItem> lost = lostItems.buildList();

        // Remove always lost items here, I know.. I know. But I can't rewrite this shit again.
        Set<Integer> alwaysLost = AlwaysLostDeathItem.items();
        alwaysLost.forEach(item -> {
            List<GameItem> remove = kept.stream().filter(it -> it.getId() == item).collect(Collectors.toList());
            remove.forEach(it -> {
                kept.remove(it);
                lost.add(it);
            });
        });

        return new ItemsLostOnDeathList(kept, lost);
    }

    /**
     * Generates the {@link ItemsLostOnDeathList} and modifies all the items kept and lost,
     * i.e. calls the appropriate {@link DeathItemModifier} for all items.
     * Transforms lost untradeables into cash if applicable, otherwise deletes them.
     *
     * WARNING: This will permanently delete charges from all items that are lost.
     * Only call this on death when the items are actually dropped.
     * For anything else use the non-modified version: {@link ItemsLostOnDeath#generate(Player)}.
     */
    public static ItemsLostOnDeathList generateModified(Player player) {
        ItemsLostOnDeathList list = generate(player);
        for (GameItem keptItem : new ArrayList<>(list.getKept())) {
            DeathItemModifier modifier = DeathItemModifiers.get(keptItem.getId());
            if (modifier != null) {
                modifier.modify(player, keptItem, true, list.getKept(), list.getLost());
            }
        }

        for (GameItem lostItem : new ArrayList<>(list.getLost())) {
            DeathItemModifier modifier = DeathItemModifiers.get(lostItem.getId());
            if (modifier != null) {
                modifier.modify(player, lostItem, false, list.getKept(), list.getLost());
            }
        }

        return new ItemsLostOnDeathList(list.getKept(), list.getLost());
    }

    public static long calculateValueOfLostItems(Player player) {
        return generate(player).getLost().stream().mapToLong(item -> item.getDef().getShopValue()).sum();
    }
}
