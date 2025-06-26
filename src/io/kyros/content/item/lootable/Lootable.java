package io.kyros.content.item.lootable;

import java.util.List;
import java.util.Map;

import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;

public interface Lootable {

    Map<LootRarity, List<GameItem>> getLoot();

    void roll(Player player);

}
