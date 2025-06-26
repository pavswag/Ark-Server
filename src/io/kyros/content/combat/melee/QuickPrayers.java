package io.kyros.content.combat.melee;

import io.kyros.model.entity.player.Player;

import java.util.Arrays;

public class QuickPrayers {

	/**
	 * The normal quick prayers
	 */
	private final boolean[] normal = new boolean[29]; // Store selected quick prayers

	/**
	 * The config id
	 */
	public static final int CONFIG = 620;

	/**
	 * Checks if all prayers are deactivated
	 *
	 * @param player the player
	 * @return true if no prayers are active, false otherwise
	 */
	public static boolean noneActive(Player player) {
		return player.getCombatPrayer().arePrayersActive(); // Check via CombatPrayer instance
	}

	/**
	 * Toggles the quick prayers
	 *
	 * @param player the player
	 */
	public static void toggle(Player player) {
		if (player.isSelectingQuickprayers) {
			player.sendMessage(":prayerfalse:");
			player.sendMessage("Please finish setting your quick prayers before toggling them.");
			return;
		}
		if (player.playerLevel[5] <= 0) {
			player.sendMessage(":prayerfalse:");
			player.sendMessage("You don't have any prayer points!");
			return;
		}

		boolean found = false;
		for (int i = 0; i < player.getQuick().getNormal().length; i++) {
			if (player.getQuick().getNormal()[i]) {
				found = true;
				player.getCombatPrayer().activatePrayer(i); // Activate prayer via CombatPrayer
				player.sendMessage(":prayertrue:");
			}
		}

		if (noneActive(player)) {
			player.sendMessage(":prayerfalse:");
		}

		if (!found) {
			player.sendMessage(":prayerfalse:");
			player.sendMessage("You need to have some quick prayers selected to use quick prayers.");
		}
	}

	/**
	 * Handles button click for quick prayers
	 *
	 * @param player the player
	 * @param button the button clicked
	 * @return true if button click is handled, false otherwise
	 */
	public static boolean clickButton(Player player, int button) {
		int[] buttonIds = { 42057, 42058, 42059, 97180, 97182, 42060, 42061, 42062, 42063, 42064, 42065, 97184, 97186,
				42066, 42067, 42068, 42069, 42070, 42071, 97189, 97191, 22251, 22252, 22253, 174057, 97193, 97195, 174060,
				174063 };

		for (int i = 0; i < buttonIds.length; i++) {
			if (button == buttonIds[i]) {
				// Check if the index is within bounds of the normal array
				if (i < player.getQuick().getNormal().length) {
					activateNormal(player, i); // Activating quick prayers
				} else {
					player.sendMessage("Invalid prayer selection.");
				}
				return true;
			}
		}

		if (button == 67080) { // Confirm selection
			player.isSelectingQuickprayers = false;
			player.setSidebarInterface(5, 15608);
			return true;
		}
		return false;
	}

	/**
	 * Activates quick regular prayers
	 *
	 * @param player the player
	 * @param prayer the prayer id
	 */
	private static void activateNormal(Player player, int prayer) {
		// Ensure that the prayer ID is within bounds
		if (prayer < 0 || prayer >= player.getQuick().getNormal().length) {
			player.sendMessage("Invalid prayer.");
			return;
		}

		if (!canActivatePrayer(player, prayer)) {
			return;
		}

		if (!player.getQuick().getNormal()[prayer]) {
			// Turn off conflicting prayers
			for (int i : CombatPrayer.getTurnOff(Prayer.getPrayerById(prayer))) {
				if (i < player.getQuick().getNormal().length) {
					player.getQuick().getNormal()[i] = false;
					player.getPA().sendConfig(CONFIG + i, 0);
				}
			}
		}

		// Activate or deactivate the selected prayer
		player.getQuick().getNormal()[prayer] = !player.getQuick().getNormal()[prayer];
		player.getPA().setConfig(CONFIG + prayer, player.getQuick().getNormal()[prayer] ? 1 : 0);
	}

	/**
	 * Checks if the player can activate the given prayer
	 *
	 * @param player the player
	 * @param prayer the prayer id
	 * @return true if the prayer can be activated, false otherwise
	 */
	private static boolean canActivatePrayer(Player player, int prayerId) {
		// Find the corresponding Prayer object by ID
		Prayer prayer = Prayer.getPrayerById(prayerId);

		if (prayer == null) {
			player.sendMessage("This prayer doesn't exist.");
			return false;
		}

		// Check defense requirement for specific prayers
		if ((prayerId == Prayer.CHIVALRY.getId() && player.playerLevel[1] < 65) ||
				(prayerId == Prayer.PIETY.getId() && player.playerLevel[1] < 70)) {
			player.sendMessage("You must have a defence level of " + (prayerId == Prayer.CHIVALRY.getId() ? "65" : "70") + " to use this prayer.");
			for (int i : CombatPrayer.getTurnOff(Prayer.getPrayerById(prayerId))) {
				player.getQuick().getNormal()[i] = false;
				player.getPA().sendConfig(CONFIG + i, 0);
			}
			return false;
		}

		// Check prayer level requirement
		if (player.getLevelForXP(player.playerXP[5]) < prayer.getLevelRequirement()) {
			player.sendMessage("You don't have the required Prayer level to activate " + prayer.getName() + ".");
			return false;
		}

		return true;
	}

	/**
	 * Gets the normal quick prayers
	 *
	 * @return the normal quick prayers array
	 */
	public boolean[] getNormal() {
		return normal;
	}
}
