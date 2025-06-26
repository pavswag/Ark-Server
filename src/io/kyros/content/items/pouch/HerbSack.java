package io.kyros.content.items.pouch;

import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ItemAssistant;

import java.util.Arrays;

public class HerbSack extends Pouch {

	/**
	 * The herb sack id and boolean to set if we want to check if a player has a herb sack
	 */
	public static final int HERB_SACK_ID = 13226;

	/**
	 * The herb sack class
	 * @param player
	 */
	public HerbSack(Player player) {
		this.player = player;
	}
	
	/**
	 * Attempts to withdraw all herbs from the herb sack
	 */
	public void withdrawAll() {
		withdrawItems();
	}

	/**
	 * The id's of the herbs you are allowed to store in the herb sack
	 */
	private final int[] cleanHerbs = { 249, 251, 253, 255, 257, 2998, 259, 261, 263, 3000, 265, 2481, 267, 269 };
	private final int[] grimyHerbs = { 199, 213, 215, 201, 203, 217, 3051, 3049, 219, 205, 207, 209, 211, 219, 2485 };
	
	/**
	 * Attempts to fill the sack with the herbs a player has in their inventory
	 */
	public void fillSack() {
		for (int cleanHerb : cleanHerbs) {
			if (player.getItems().playerHasItem(cleanHerb, 1)) {
				addItemToHerbSack(cleanHerb, player.getItems().getItemAmount(cleanHerb));
			}
		}
		for (int grimyHerb : grimyHerbs) {
			if (player.getItems().playerHasItem(grimyHerb, 1)) {
				addItemToHerbSack(grimyHerb, player.getItems().getItemAmount(grimyHerb));
			}
		}
	}

	/**
	 * Attempts  to add the herbs chosen to the herb sack
	 * @param id
	 * @param amount
	 */
	public void addItemToHerbSack(int id, int amount) {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (amount >= 28) {
			amount = player.getItems().getItemCount(id, false);
		}
		if (id == HERB_SACK_ID) {
			player.sendMessage("Don't be silly.");
			return;
		}
		if (!isValidHerb(id)) {
			player.sendMessage("You can only store clean & grimy herbs in the herb sack.");
			return;
		}
		if (id <= 0 || amount <= 0) {
			return;
		}
		int maxAddableAmount = Math.min(amount, 650000 - countItems(id));
		if (maxAddableAmount <= 0) {
			return;
		}

		player.sendMessage("Filled the sack with x" + amount + " " + ItemAssistant.getItemName(id));
		for (int amt = 0; amt < amount; amount--) {
			player.getItems().deleteItem(id, 1);
			addItemToList(id + 1, 1);
		}
	}

	private boolean isValidHerb(int id) {
		return Arrays.stream(cleanHerbs).anyMatch(i -> i == id) || Arrays.stream(grimyHerbs).anyMatch(i -> i == id);
	}
	
	/**
	 * Checks the amount and of what herb you have stored in the sack
	 */
	public void check() {
		int frame = 8149;
		int totalAmount = 0;
		player.getPA().resetQuestInterface();
		player.getPA().sendFrame126("@dre@                   Herb Sack", 8144);
		player.getPA().sendFrame126("", 8145);
		player.getPA().sendFrame126("", 8148);
		for (int i = 0; i < 14; i++) {
			int id = 0;
			int amt = 0;

			if (i < items.size()) {
				GameItem item = items.get(i);
				if (item != null) {
					id = item.getId();
					amt = item.getAmount();
				}
				totalAmount += amt;
				player.getPA().sendFrame126("@red@Total Amount: "+totalAmount+"/700", 8147);
				player.getPA().sendFrame126("@blu@x" + amt + " " + ItemAssistant.getItemName(id) + "", frame);
				frame++;
			}
			player.getPA().openQuestInterface();
		}
	}

}
