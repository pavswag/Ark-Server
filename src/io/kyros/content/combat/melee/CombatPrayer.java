package io.kyros.content.combat.melee;

import io.kyros.content.skills.Skill;
import io.kyros.content.tournaments.TourneyManager;
import io.kyros.model.Bonus;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

import static io.kyros.content.combat.melee.Prayer.*;

public class CombatPrayer {

	@Getter
	private PrayerBook currentPrayerBook;
	private Player player;
	private static final int NORMALS_INTERFACE_ID = 15608; // Example interface ID for normals
	private static final int RUINOUS_INTERFACE_ID = 105000; // Example interface ID for ruinous


	public CombatPrayer(Player player) {
		this.currentPrayerBook = new PrayerBook("normals", Prayer.getPrayersByBook("normals"));
		this.player = player;
	}

	public boolean isPrayerActive(int prayerId) {
		return player.isPrayerActive(prayerId);
	}

	public void activatePrayer(int prayerId) {
		activatePrayer(prayerId, true);
	}

	public boolean ruinousButton(int button) {
		int baseButtonId = 105003;
		if (button >= baseButtonId && button <= 105075) {
			int prayerIndex = (button - baseButtonId) / 3;
			int prayerId = prayerIndex + 29;
			Prayer prayer = Prayer.getPrayerById(prayerId);

			if (prayer != null) {
				activatePrayer(prayer.getId());
				return true;
			}
		}
		return false;
	}



	public void activatePrayer(int prayerId, boolean shift) {
		if (prayerId < 0 || prayerId >= Prayer.values().length) {
			return; // Invalid prayer ID
		}

		// Shift protection prayers if needed
		if (shift && player.isProtectionPrayersShiftRight()) {
			prayerId = shiftProtectionPrayers(prayerId);
		}

		// Ensure that prayer points are sufficient
		if (player.playerLevel[Skill.PRAYER.getId()] <= 0) {
			player.sendMessage("You have run out of prayer points!");
			player.getPA().sendFrame36(Prayer.getPrayerById(prayerId).getGlowFrame(), 0);
			return;
		}

		// Ensure the player meets the requirements for the prayer
		Prayer prayer = currentPrayerBook.getPrayerById(prayerId);
		if (!canActivatePrayer(prayerId)) {
			player.getPA().sendFrame36(prayer.getGlowFrame(), 0);
			return;
		}

		// Check if the prayer is already active
		boolean isCurrentlyActive = player.isPrayerActive(prayerId);

		// Turn off conflicting prayers before enabling this one
		if (!isCurrentlyActive) {
			int[] conflicts = getTurnOff(Prayer.getPrayerById(prayerId));
			for (int conflictId : conflicts) {
				if (player.isPrayerActive(conflictId)) {
					player.setPrayerActive(conflictId, false);
					player.getPA().sendFrame36(Prayer.getPrayerById(conflictId).getGlowFrame(), 0);
				}
			}
		}

		// Toggle the prayer
		player.setPrayerActive(prayerId, !isCurrentlyActive);

		// Update the UI to reflect the new prayer state
		player.getPA().sendFrame36(prayer.getGlowFrame(), !isCurrentlyActive ? 1 : 0);

		// Handle head icon prayers (like protection prayers)
		if (prayer.getHeadIcon() != -1) {
			player.setHeadIcon(!isCurrentlyActive ? prayer.getHeadIcon() : -1);
			player.getPA().requestUpdates(); // Send head icon update
		}

		// Ensure the player still has enough prayer points after activation
		handlePrayerDrain();
	}

	private int shiftProtectionPrayers(int prayerId) {
		// Shift protection prayers logic
		if (prayerId == Prayer.PROTECT_FROM_MELEE.getId()) return Prayer.PROTECT_FROM_MAGIC.getId();
		if (prayerId == Prayer.PROTECT_FROM_MAGIC.getId()) return Prayer.PROTECT_FROM_MISSILES.getId();
		if (prayerId == Prayer.PROTECT_FROM_MISSILES.getId()) return Prayer.PROTECT_FROM_MELEE.getId();
		return prayerId;
	}

	private boolean isRestricted(int prayerId) {
		// Example: handle restricted areas and specific prayer rules
		return player.isInRestrictedZone() ||
				(prayerId == Prayer.PROTECT_FROM_MELEE.getId() && TourneyManager.getSingleton().isInArena(player));
	}

	private boolean canActivatePrayer(int prayerId) {
		Prayer prayer = currentPrayerBook.getPrayerById(prayerId);
		if (prayer == null) {
			return false;
		}

		// Check prayer level requirements
		if (player.getLevelForXP(player.playerXP[Skill.PRAYER.getId()]) < prayer.getLevelRequirement()) {
			return false;
		}

		// Check for prayer points
		if (player.playerLevel[Skill.PRAYER.getId()] <= 0) {
			player.sendMessage("You have run out of prayer points!");
			return false;
		}

		// Handle specific unlock requirements
		if ((prayer == Prayer.AUGURY && !player.augury) || (prayer == Prayer.RIGOUR && !player.rigour)) {
			player.sendMessage("You have not unlocked this prayer.");
			return false;
		}

		return true;
	}

	public void handlePrayerDrain() {
		if (player.usingInfPrayer && !Boundary.isIn(player, Boundary.ARAXXOR_BOSS)) {
			return;
		}
		boolean drain = false;

		if (player.isDead || player.getHealth().getCurrentHealth() <= 0)
			return;

		double drainAmount = 0.0;
		for (Prayer prayer : currentPrayerBook.getPrayers()) {
			if (player.isPrayerActive(prayer.getId())) {
				drainAmount += (prayer.getDrainRate() / 20);
				drain = true;
			}
		}

		if (!drain) {
			return;
		}

		if (drainAmount > 0) {
			drainAmount /= (1.5 + (0.035 * player.getItems().getBonus(Bonus.PRAYER)));
		}

		player.reducePrayerPoints(drainAmount);

		if (player.getPrayerPoints() <= 0) {
			player.setPrayerPoints(1.0 + player.getPrayerPoints());
			reducePrayerLevel();
		}
	}

	public void switchPrayerBook(String bookName) {
		if (!bookName.equalsIgnoreCase("normals") && !bookName.equalsIgnoreCase("ruinous")) {
			player.sendMessage("Invalid prayer book selection.");
			return;
		}

		// Reset active prayers before switching
		resetPrayers();

		// Set the current prayer book
		this.currentPrayerBook = new PrayerBook(bookName, Prayer.getPrayersByBook(bookName));

		// Update the interface based on the selected prayer book
		if (bookName.equalsIgnoreCase("normals")) {
			player.setSidebarInterface(5, NORMALS_INTERFACE_ID);
			player.sendMessage("You have switched to the Normals prayer book.");
		} else if (bookName.equalsIgnoreCase("ruinous")) {
			player.setSidebarInterface(5, RUINOUS_INTERFACE_ID);
			player.sendMessage("You have switched to the Ruinous Powers prayer book.");
		}

		// Request UI update
		player.getPA().requestUpdates();
	}

	public void resetPrayers() {
		for (Prayer prayer : currentPrayerBook.getPrayers()) {
			player.setPrayerActive(prayer.getId(), false);
			player.getPA().sendFrame36(prayer.getGlowFrame(), 0);
		}
		player.setHeadIcon(-1);
		player.getPA().requestUpdates();
	}

	public void reducePrayerLevel() {
		if (player.playerLevel[5] - 1 > 0) {
			player.playerLevel[5] -= 1;
		} else {
			player.sendMessage("You have run out of prayer points!", TimeUnit.MINUTES.toMillis(10));
			player.playerLevel[5] = 0;
			resetPrayers();
			player.prayerId = -1;
		}
		player.getPA().refreshSkill(5);
	}


	public static int[] getTurnOff(Prayer id) {
		int[] turnOff = new int[0];
		switch (id) {
			// Normal prayers
			case THICK_SKIN:
				turnOff = new int[] { ROCK_SKIN.getId(), STEEL_SKIN.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), DECIMATE.getId(), ANNIHILATE.getId(), VAPORISE.getId() };
				break;
			case ROCK_SKIN:
				turnOff = new int[] { THICK_SKIN.getId(), STEEL_SKIN.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), DECIMATE.getId(), ANNIHILATE.getId(), VAPORISE.getId() };
				break;
			case STEEL_SKIN:
				turnOff = new int[] { THICK_SKIN.getId(), ROCK_SKIN.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), DECIMATE.getId(), ANNIHILATE.getId(), VAPORISE.getId() };
				break;
			case CLARITY_OF_THOUGHT:
				turnOff = new int[] { IMPROVED_REFLEXES.getId(), INCREDIBLE_REFLEXES.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), TRINITAS.getId() };
				break;
			case IMPROVED_REFLEXES:
				turnOff = new int[] { CLARITY_OF_THOUGHT.getId(), INCREDIBLE_REFLEXES.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), TRINITAS.getId() };
				break;
			case INCREDIBLE_REFLEXES:
				turnOff = new int[] { IMPROVED_REFLEXES.getId(), CLARITY_OF_THOUGHT.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), TRINITAS.getId() };
				break;
			case BURST_OF_STRENGTH:
				turnOff = new int[] { SUPERHUMAN_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), SHARP_EYE.getId(), MYSTIC_WILL.getId(), HAWK_EYE.getId(), MYSTIC_LORE.getId(), EAGLE_EYE.getId(), MYSTIC_MIGHT.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), DECIMATE.getId(), TRINITAS.getId() };
				break;
			case SUPERHUMAN_STRENGTH:
				turnOff = new int[] { BURST_OF_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), SHARP_EYE.getId(), MYSTIC_WILL.getId(), HAWK_EYE.getId(), MYSTIC_LORE.getId(), EAGLE_EYE.getId(), MYSTIC_MIGHT.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), DECIMATE.getId(), TRINITAS.getId() };
				break;
			case ULTIMATE_STRENGTH:
				turnOff = new int[] { SUPERHUMAN_STRENGTH.getId(), BURST_OF_STRENGTH.getId(), SHARP_EYE.getId(), MYSTIC_WILL.getId(), HAWK_EYE.getId(), MYSTIC_LORE.getId(), EAGLE_EYE.getId(), MYSTIC_MIGHT.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), DECIMATE.getId(), TRINITAS.getId() };
				break;
			case SHARP_EYE:
				turnOff = new int[] { MYSTIC_WILL.getId(), HAWK_EYE.getId(), MYSTIC_LORE.getId(), EAGLE_EYE.getId(), MYSTIC_MIGHT.getId(), BURST_OF_STRENGTH.getId(), SUPERHUMAN_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), CLARITY_OF_THOUGHT.getId(), IMPROVED_REFLEXES.getId(), INCREDIBLE_REFLEXES.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), ANNIHILATE.getId() };
				break;
			case HAWK_EYE:
				turnOff = new int[] { MYSTIC_WILL.getId(), SHARP_EYE.getId(), MYSTIC_LORE.getId(), EAGLE_EYE.getId(), MYSTIC_MIGHT.getId(), BURST_OF_STRENGTH.getId(), SUPERHUMAN_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), CLARITY_OF_THOUGHT.getId(), IMPROVED_REFLEXES.getId(), INCREDIBLE_REFLEXES.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), ANNIHILATE.getId() };
				break;
			case EAGLE_EYE:
				turnOff = new int[] { MYSTIC_WILL.getId(), HAWK_EYE.getId(), MYSTIC_LORE.getId(), SHARP_EYE.getId(), MYSTIC_MIGHT.getId(), BURST_OF_STRENGTH.getId(), SUPERHUMAN_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), CLARITY_OF_THOUGHT.getId(), IMPROVED_REFLEXES.getId(), INCREDIBLE_REFLEXES.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), ANNIHILATE.getId() };
				break;
			case MYSTIC_WILL:
				turnOff = new int[] { SHARP_EYE.getId(), HAWK_EYE.getId(), MYSTIC_LORE.getId(), EAGLE_EYE.getId(), MYSTIC_MIGHT.getId(), BURST_OF_STRENGTH.getId(), SUPERHUMAN_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), CLARITY_OF_THOUGHT.getId(), IMPROVED_REFLEXES.getId(), INCREDIBLE_REFLEXES.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), VAPORISE.getId() };
				break;
			case MYSTIC_LORE:
				turnOff = new int[] { MYSTIC_WILL.getId(), HAWK_EYE.getId(), SHARP_EYE.getId(), EAGLE_EYE.getId(), MYSTIC_MIGHT.getId(), BURST_OF_STRENGTH.getId(), SUPERHUMAN_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), CLARITY_OF_THOUGHT.getId(), IMPROVED_REFLEXES.getId(), INCREDIBLE_REFLEXES.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), VAPORISE.getId() };
				break;
			case MYSTIC_MIGHT:
				turnOff = new int[] { MYSTIC_WILL.getId(), HAWK_EYE.getId(), MYSTIC_LORE.getId(), EAGLE_EYE.getId(), SHARP_EYE.getId(), BURST_OF_STRENGTH.getId(), SUPERHUMAN_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), CLARITY_OF_THOUGHT.getId(), IMPROVED_REFLEXES.getId(), INCREDIBLE_REFLEXES.getId(), CHIVALRY.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), VAPORISE.getId() };
				break;
			case PROTECT_FROM_MAGIC:
				turnOff = new int[] { REDEMPTION.getId(), SMITE.getId(), RETRIBUTION.getId(), PROTECT_FROM_MISSILES.getId(), PROTECT_FROM_MELEE.getId(), DAMPEN_MAGIC.getId(), DAMPEN_MELEE.getId(), DAMPEN_RANGED.getId() };
				break;
			case PROTECT_FROM_MISSILES:
				turnOff = new int[] { REDEMPTION.getId(), SMITE.getId(), RETRIBUTION.getId(), PROTECT_FROM_MAGIC.getId(), PROTECT_FROM_MELEE.getId(), DAMPEN_MAGIC.getId(), DAMPEN_MELEE.getId(), DAMPEN_RANGED.getId() };
				break;
			case PROTECT_FROM_MELEE:
				turnOff = new int[] { REDEMPTION.getId(), SMITE.getId(), RETRIBUTION.getId(), PROTECT_FROM_MISSILES.getId(), PROTECT_FROM_MAGIC.getId(), DAMPEN_MAGIC.getId(), DAMPEN_MELEE.getId(), DAMPEN_RANGED.getId() };
				break;
			case RETRIBUTION:
				turnOff = new int[] { REDEMPTION.getId(), SMITE.getId(), PROTECT_FROM_MELEE.getId(), PROTECT_FROM_MISSILES.getId(), PROTECT_FROM_MAGIC.getId(), DAMPEN_MAGIC.getId(), DAMPEN_MELEE.getId(), DAMPEN_RANGED.getId() };
				break;
			case REDEMPTION:
				turnOff = new int[] { RETRIBUTION.getId(), SMITE.getId(), PROTECT_FROM_MELEE.getId(), PROTECT_FROM_MISSILES.getId(), PROTECT_FROM_MAGIC.getId(), DAMPEN_MAGIC.getId(), DAMPEN_MELEE.getId(), DAMPEN_RANGED.getId() };
				break;
			case SMITE:
				turnOff = new int[] { REDEMPTION.getId(), RETRIBUTION.getId(), PROTECT_FROM_MELEE.getId(), PROTECT_FROM_MISSILES.getId(), PROTECT_FROM_MAGIC.getId(), DAMPEN_MAGIC.getId(), DAMPEN_MELEE.getId(), DAMPEN_RANGED.getId() };
				break;
			case CHIVALRY:
				turnOff = new int[] { SHARP_EYE.getId(), MYSTIC_WILL.getId(), HAWK_EYE.getId(), MYSTIC_LORE.getId(), EAGLE_EYE.getId(), MYSTIC_MIGHT.getId(), BURST_OF_STRENGTH.getId(), SUPERHUMAN_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), CLARITY_OF_THOUGHT.getId(), IMPROVED_REFLEXES.getId(), INCREDIBLE_REFLEXES.getId(), PIETY.getId(), RIGOUR.getId(), AUGURY.getId(), THICK_SKIN.getId(), ROCK_SKIN.getId(), STEEL_SKIN.getId(), DECIMATE.getId(), ANNIHILATE.getId(), VAPORISE.getId(), TRINITAS.getId() };
				break;
			case PIETY:
				turnOff = new int[] { SHARP_EYE.getId(), MYSTIC_WILL.getId(), HAWK_EYE.getId(), MYSTIC_LORE.getId(), EAGLE_EYE.getId(), MYSTIC_MIGHT.getId(), BURST_OF_STRENGTH.getId(), SUPERHUMAN_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), CLARITY_OF_THOUGHT.getId(), IMPROVED_REFLEXES.getId(), INCREDIBLE_REFLEXES.getId(), CHIVALRY.getId(), THICK_SKIN.getId(), ROCK_SKIN.getId(), STEEL_SKIN.getId(), RIGOUR.getId(), AUGURY.getId(), DECIMATE.getId(), ANNIHILATE.getId(), VAPORISE.getId(), TRINITAS.getId() };
				break;
			case RIGOUR:
				turnOff = new int[] { SHARP_EYE.getId(), MYSTIC_WILL.getId(), HAWK_EYE.getId(), MYSTIC_LORE.getId(), EAGLE_EYE.getId(), MYSTIC_MIGHT.getId(), BURST_OF_STRENGTH.getId(), SUPERHUMAN_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), CLARITY_OF_THOUGHT.getId(), IMPROVED_REFLEXES.getId(), INCREDIBLE_REFLEXES.getId(), CHIVALRY.getId(), THICK_SKIN.getId(), ROCK_SKIN.getId(), STEEL_SKIN.getId(), PIETY.getId(), AUGURY.getId(), ANNIHILATE.getId(), DECIMATE.getId(), VAPORISE.getId() };
				break;
			case AUGURY:
				turnOff = new int[] { SHARP_EYE.getId(), MYSTIC_WILL.getId(), HAWK_EYE.getId(), MYSTIC_LORE.getId(), EAGLE_EYE.getId(), MYSTIC_MIGHT.getId(), BURST_OF_STRENGTH.getId(), SUPERHUMAN_STRENGTH.getId(), ULTIMATE_STRENGTH.getId(), CLARITY_OF_THOUGHT.getId(), IMPROVED_REFLEXES.getId(), INCREDIBLE_REFLEXES.getId(), CHIVALRY.getId(), THICK_SKIN.getId(), ROCK_SKIN.getId(), STEEL_SKIN.getId(), RIGOUR.getId(), PIETY.getId(), ANNIHILATE.getId(), DECIMATE.getId(), VAPORISE.getId() };
				break;

			// Ruinous prayers
			case DECIMATE:
				turnOff = new int[] { THICK_SKIN.getId(), ROCK_SKIN.getId(), STEEL_SKIN.getId(), PIETY.getId(), CHIVALRY.getId(), ULTIMATE_STRENGTH.getId() };
				break;
			case ANNIHILATE:
				turnOff = new int[] { SHARP_EYE.getId(), HAWK_EYE.getId(), EAGLE_EYE.getId(), RIGOUR.getId() };
				break;
			case VAPORISE:
				turnOff = new int[] { MYSTIC_WILL.getId(), MYSTIC_LORE.getId(), MYSTIC_MIGHT.getId(), AUGURY.getId() };
				break;
			case DAMPEN_MAGIC:
				turnOff = new int[] { PROTECT_FROM_MAGIC.getId(), PROTECT_FROM_MISSILES.getId(), PROTECT_FROM_MELEE.getId(), RETRIBUTION.getId(), REDEMPTION.getId(), SMITE.getId(), DAMPEN_MELEE.getId(), DAMPEN_RANGED.getId() };
				break;
			case DAMPEN_RANGED:
				turnOff = new int[] { PROTECT_FROM_MAGIC.getId(), PROTECT_FROM_MISSILES.getId(), PROTECT_FROM_MELEE.getId(), RETRIBUTION.getId(), REDEMPTION.getId(), SMITE.getId(), DAMPEN_MELEE.getId(), DAMPEN_MAGIC.getId() };
				break;
			case DAMPEN_MELEE:
				turnOff = new int[] { PROTECT_FROM_MAGIC.getId(), PROTECT_FROM_MISSILES.getId(), PROTECT_FROM_MELEE.getId(), RETRIBUTION.getId(), REDEMPTION.getId(), SMITE.getId(), DAMPEN_RANGED.getId(), DAMPEN_MAGIC.getId() };
				break;
			case TRINITAS:
				turnOff = new int[] { INCREDIBLE_REFLEXES.getId(), CLARITY_OF_THOUGHT.getId(), IMPROVED_REFLEXES.getId(), CHIVALRY.getId(), PIETY.getId() };
				break;
		}
		return turnOff;
	}




	public boolean arePrayersActive() {
		for (Prayer prayer : Prayer.values()) {
			if (player.isPrayerActive(prayer.getId())) {
				return true; // Return true if any prayer is active
			}
		}
		return false; // Return false if no prayers are active
	}

	/**
	 * Shifts protection prayers to the right.
	 * If Protect from Melee is active, it shifts to Protect from Magic.
	 *
	 * @param set Whether to activate or deactivate protection prayer shifting.
	 */
	public void shiftProtectionPrayersRight(boolean set) {
		player.setProtectionPrayersShiftRight(set);
		if (player.isProtectionPrayersShiftRight()) {
			shiftPrayersRight();
		} else {
			shiftPrayersLeft();
		}
	}

	/**
	 * Shift protection prayers to the right.
	 * Cycles from Melee → Magic → Missiles.
	 */
	private void shiftPrayersRight() {
		if (isPrayerActive(Prayer.PROTECT_FROM_MELEE.getId())) {
			activatePrayer(Prayer.PROTECT_FROM_MAGIC.getId(), false);
		} else if (isPrayerActive(Prayer.PROTECT_FROM_MAGIC.getId())) {
			activatePrayer(Prayer.PROTECT_FROM_MISSILES.getId(), false);
		} else if (isPrayerActive(Prayer.PROTECT_FROM_MISSILES.getId())) {
			activatePrayer(Prayer.PROTECT_FROM_MELEE.getId(), false);
		}
	}

	/**
	 * Shift protection prayers to the left.
	 * Cycles from Magic → Missiles → Melee.
	 */
	private void shiftPrayersLeft() {
		if (isPrayerActive(Prayer.PROTECT_FROM_MELEE.getId())) {
			activatePrayer(Prayer.PROTECT_FROM_MISSILES.getId(), false);
		} else if (isPrayerActive(Prayer.PROTECT_FROM_MISSILES.getId())) {
			activatePrayer(Prayer.PROTECT_FROM_MAGIC.getId(), false);
		} else if (isPrayerActive(Prayer.PROTECT_FROM_MAGIC.getId())) {
			activatePrayer(Prayer.PROTECT_FROM_MELEE.getId(), false);
		}
	}

	public void resetOverHeads() {
		// Loop through overhead prayers and deactivate them
		int[] overheadPrayers = {
				Prayer.PROTECT_FROM_MAGIC.getId(),
				Prayer.PROTECT_FROM_MISSILES.getId(),
				Prayer.PROTECT_FROM_MELEE.getId(),
				Prayer.RETRIBUTION.getId(),
				Prayer.REDEMPTION.getId(),
				Prayer.SMITE.getId()
		};

		// Deactivate each overhead prayer
		for (int prayerId : overheadPrayers) {
			if (player.isPrayerActive(prayerId)) {
				player.setPrayerActive(prayerId, false); // Deactivate the prayer
				player.getPA().sendFrame36(Prayer.getPrayerById(prayerId).getGlowFrame(), 0); // Update UI to turn off prayer glow
			}
		}

		// Reset the player's head icon if any overhead prayer was active
		player.setHeadIcon(-1);
		player.getPA().refreshSkill(5);
		player.getPA().requestUpdates(); // Update the player's status
	}

	public void resetPrayer(int prayerId) {
		if (player.isPrayerActive(prayerId)) {
			player.setPrayerActive(prayerId, false);  // Deactivate the prayer
			player.getPA().sendFrame36(Prayer.getPrayerById(prayerId).getGlowFrame(), 0);  // Update the prayer glow in the UI

			// If it's an overhead prayer, reset the head icon
			if (Prayer.getPrayerById(prayerId).getHeadIcon() != -1) {
				player.setHeadIcon(-1);  // Reset the head icon
			}

			player.getPA().refreshSkill(5);
			player.getPA().requestUpdates();  // Update the player's appearance
		}
	}

	public void drainPrayerPoints(int i) {
		player.playerLevel[5] -= Math.min(player.playerLevel[5], i);
		player.getPA().refreshSkill(5);
		reducePrayerLevel();
	}

	public void restorePrayerPoints(int i) {
		player.playerLevel[5] += i;
		if (player.playerLevel[5] > player.getLevelForXP(player.playerXP[5]))
			player.playerLevel[5] = player.getLevelForXP(player.playerXP[5]);
		player.getPA().refreshSkill(5);
	}
}
