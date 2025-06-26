package io.kyros.content.bosses.godwars;

import io.kyros.Server;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerAssistant;
import io.kyros.util.Misc;

import java.util.HashMap;
import java.util.Map;

public class Godwars {

	private static int KC_REQUIRED;
	public static final int KEY_ID = 11942;

	public static final Boundary GODWARS_AREA = new Boundary(2819, 5255, 2942, 5375);

	private final Player player;
	private Map<God, Integer> killcount;

	public Godwars(Player player) {
		this.player = player;
		initialize();
	}

	/**
	 * Sets all killcount values to 0.
	 */
	public void initialize() {
		killcount = new HashMap<>();
		for (God god : God.values()) {
			killcount.put(god, 0);
		}
	}

	/**
	 * Handles entering a boss room.
	 *
	 * @param god The god to which the room belongs to.
	 */
	public void enterBossRoom(God god) {
		if (player.amDonated >= 0 && player.amDonated < 20) {//non donators
			KC_REQUIRED = 20;
		} else if (player.amDonated >= 20 && player.amDonated < 50) {//regular
			KC_REQUIRED = 15;
		} else if (player.amDonated >= 50 && player.amDonated < 100) { //extreme only
			KC_REQUIRED = 10;
		} else if (player.amDonated >= 100 && player.amDonated < 250) { //legendary
			KC_REQUIRED = 7;
		} else if (player.amDonated >= 250 && player.amDonated < 500) { //diamond club
			KC_REQUIRED = 5;
		} else if (player.amDonated >= 500 && player.amDonated < 750) { //onyx
			KC_REQUIRED = 1;
		}else if (player.amDonated >= 750&& player.amDonated < 1000) { //onyx
			KC_REQUIRED = 0;
		}else if (player.amDonated >= 1000 && player.amDonated < 1500) { //onyx
			KC_REQUIRED = 0;
		}else if (player.amDonated >= 1500 && player.amDonated < 2000) { //onyx
			KC_REQUIRED = 0;
		}else if (player.amDonated >= 2000) { //onyx
			KC_REQUIRED = 0;
		}
		if (killcount.get(god) >= KC_REQUIRED) {
			killcount.put(god, killcount.get(god) - KC_REQUIRED);
		} else if (player.getItems().playerHasItem(KEY_ID)) {
			player.getItems().deleteItem(KEY_ID, 1);
		} else {
			player.sendMessage("You need to kill " + (KC_REQUIRED - killcount.get(god)) + " more " + Misc.capitalizeJustFirst(god.name()) + " creatures before you can enter.");
			player.sendMessage("You can also buy a Ecumenical Key from the general store for quick entry!");
			return;
		}
		int previousHeight = player.heightLevel;

		switch (god) {
			case SARADOMIN:
				player.getPA().movePlayer(2907, 5265, getInstanceHeight());
				break;
			case ZAMORAK:
				player.getPA().movePlayer(2925, 5331, getInstanceHeight() + 2);
				break;
			case BANDOS:
				player.getPA().movePlayer(2864, 5354, getInstanceHeight() + 2);
				break;
			case ARMADYL:
				player.getPA().movePlayer(2839, 5296, getInstanceHeight() + 2);
				break;
		}
		if (player.heightLevel != previousHeight) {
			Server.itemHandler.reloadItems(player);
		}
	}

	/**
	 * Returns the height level of the instance which the player should be teleported to.
	 *
	 * @return The height level of the instance.
	 */
	private int getInstanceHeight() {
		if (player.getMode().isGroupIronman()) {
			return 8;
		} else if (player.getMode().isIronmanType()) {
			return 4;
		} else {
			return 0;
		}
	}

	/**
	 * Increases the amount of minions slain of a certain god.
	 *
	 * @param god The god of which the killcount should be increased.
	 */
	public void increaseKillcount(God god) {
		killcount.put(god, killcount.get(god) + 1);
	}

	public void increaseKillcountByTeleportationDevice(God god, int amount) {
		killcount.put(god, killcount.get(god) + amount);
	}

	/**
	 * Updates the killcount values on the interface.
	 */
	public void drawInterface() {
		PlayerAssistant assistant = player.getPA();
		assistant.sendFrame126(Integer.toString(killcount.get(God.ARMADYL)), 16218);
		assistant.sendFrame126(Integer.toString(killcount.get(God.BANDOS)), 16219);
		assistant.sendFrame126(Integer.toString(killcount.get(God.SARADOMIN)), 16220);
		assistant.sendFrame126(Integer.toString(killcount.get(God.ZAMORAK)), 16221);
		assistant.sendFrame126(Integer.toString(0), 16222);
	}
}
