package io.kyros.content.items.item_combinations;

import java.util.List;
import java.util.Optional;

import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ItemCombination;

public class ArcaneSpiritShield extends ItemCombination {

	public ArcaneSpiritShield(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		super.items.forEach(item -> player.getItems().deleteItem2(item.getId(), item.getAmount()));
		player.getItems().addItem(super.outcome.getId(), super.outcome.getAmount());
		player.getDH().sendItemStatement("You combined the items and created the Arcane Spirit Shield.", 12825);
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("Once the sigil is combined with the blessed spirit shield", "there is no going back. The items cannot be reverted.");
	}

}
