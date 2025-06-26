package io.kyros.content.skills.runecrafting;

import io.kyros.content.SkillcapePerks;
import io.kyros.content.achievement_diary.impl.*;
import io.kyros.content.skills.SkillHandler;
import io.kyros.content.taskmaster.TaskMasterKills;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.Misc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class Runecrafting extends SkillHandler {

	public enum RunecraftingData {
		AIR(25378, 556, 5, new int[] { 1, 11, 22, 33, 44, 55, 66, 77, 88, 99 }, 2500, 20667),
		MIND(25379, 558, 5.5, new int[] { 2, 14, 28, 42, 56, 70, 84, 98 }, 2500, 20669),
		WATER(25376, 555, 6, new int[] { 5, 19, 38, 57, 76, 88, 99 }, 2500, 20671),
		MIST(-1, -1, 8, new int[] { 6 }, 2500, -1),
		EARTH(24972, 557, 6.5, new int[] { 9, 26, 52, 78, 88, 96 }, 2500, 20673),
		DUST(-1, -1, 8.3, new int[] { 10 }, 2500, -1),
		MUD(-1, -1, 9.3, new int[] { 13 }, 2500, -1),
		FIRE(24971, 554, 7, new int[] { 14, 35, 70, 85, 95 }, 2500, 20665),
		SMOKE(-1, -1, 8.5, new int[] { 15 }, 2500, -1),
		STEAM(-1, -1, 9.3, new int[] { 19 }, 2500, -1),
		BODY(24973, 559, 7.5, new int[] { 20, 46, 75, 92 }, 2500, 20675),
		LAVA(-1, -1, 10, new int[] { 23 }, 2500, -1),
		COSMIC(24974, 564, 8, new int[] { 27, 59, 78 }, 2500, 20677),
		CHAOS(24976, 562, 8.5, new int[] { 35, 74, 86 }, 2500, 20679),
		ASTRAL(14911, 9075, 8.7, new int[] { 40, 82 }, 2500, 20689),
		NATURE(24975, 561, 9, new int[] { 44, 91, 95 }, 2500, 20681),
		LAW(25034, 563, 9.5, new int[] { 54, 79, 93 }, 2500, 20683),
		DEATH(25035, 560, 15, new int[] { 65, 84, 96 }, 2500, 20685),
		BLOOD(43848, 565, 17, new int[] { 77, 89, 94 }, 2500, 20691),
		WRATH(34772, 21880, 15, new int[] { 95 }, 2500, 20691),
		SOUL(25377, 566, 18, new int[] { 90, 95, 99 }, 2500, 20687),
		DEATH_DZ(43707, 560, 15, new int[] { 65, 84, 96 }, 2500, 20685),
		BLOOD_DZ(43708, 565, 17, new int[] { 77, 89, 94 }, 2500, 20691),
		CHAOS_DZ(43814, 562, 8.5, new int[] { 35, 74, 86 }, 2500, 20679),
		NATURE_DZ(43711, 561, 9, new int[] { 44, 91, 95 }, 2500, 20681);

		private final int objectId;
		private final int runeId;
		private final int petChance;
		private final int petId;
		private final double experience;
		private final int[] multiplier;

		public int getObjectId() {
			return objectId;
		}

		public int getRuneId() {
			return runeId;
		}

		public int getPetChance() {
			return petChance;
		}

		public int getPetId() {
			return petId;
		}

		public double getExperience() {
			return experience;
		}

		public int getLevelRequirement() {
			return multiplier[0];
		}

		RunecraftingData(int objectId, int runeId, double experience, int[] multiplier, int petChance, int petId) {
			this.objectId = objectId;
			this.runeId = runeId;
			this.experience = experience;
			this.multiplier = multiplier;
			this.petChance = petChance;
			this.petId = petId;
		}

		public static RunecraftingData forId(int id) {
			Optional<RunecraftingData> first = Arrays.stream(RunecraftingData.values()).filter(rune -> rune.runeId == id).findFirst();
			return first.orElse(null);
		}
	}
	public static final List<Integer> rc_ids =
			Arrays.asList(
					26858, 26860, 26862);
	public static int getRcEquipmentCount(Player player) {
		int count = 0;
		for (int i : rc_ids) {
			if (player.getItems().isWearingItem(i)) {
				count++;
			}
		}
		return count;
	}
	public static void execute(Player player, int objectId) {
		for (RunecraftingData data : RunecraftingData.values()) {

			String name = data.name().toLowerCase().replace("_dz","").replaceAll("_", " ");
			if (data.getObjectId() == objectId) {
				if (!hasRequiredLevel(player, 20, data.getLevelRequirement(), "runecrafting", "craft these runes")) {
					return;
				}
				if (Misc.random(10) == 0 && player.getInterfaceEvent().isExecutable()) {
					player.getInterfaceEvent().execute();
					return;
				}
				if (!player.getItems().playerHasItem(1436) && !player.getItems().playerHasItem(7936)) {
					player.sendMessage("You need some essence to craft runes!");
					return;
				}
				int multiplier = 1;
				for (int multiply = 0; multiply < data.multiplier.length; multiply++) {
					if (player.playerLevel[20] >= data.multiplier[multiply]) {
						multiplier = multiply + 1;
					}
				}
				int count = player.getItems().getItemAmount(7936) + player.getItems().getItemAmount(1436);
				int essence = player.getItems().getItemAmount(7936) + player.getItems().getItemAmount(1436);

				if (count > 20000 || essence > 20000) {
					count = 20000;
					essence = 20000;
				}
				int multiply = essence *= multiplier;
				if (name.equals("water")) {
					player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.CRAFT_WATER);
				}
				if (name.equals("death")) {
					player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.CRAFT_DEATH);
				}
				if (name.equals("mind")) {
					if (multiply > 100) {
						player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.CRAFT_MIND);
					}
				}
				if (name.equals("cosmic")) {
					if (multiply > 56) {
						player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.CRAFT_COSMIC);
					}
				}
				if (name.equals("earth")) {
					if (multiply > 150) {
						player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.ALOT_OF_EARTH);
					}
					player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.EARTH_RUNES);
				}
				if (name.equals("nature")) {
					if (multiply > 50) {
						player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.CRAFT_NATURES);
					}
				}
				for (TaskMasterKills taskMasterKills : player.getTaskMaster().taskMasterKillsList) {
					if (taskMasterKills.getDesc().equalsIgnoreCase("Make @whi@Runes")) {
						if (essence + taskMasterKills.getAmountKilled() > taskMasterKills.getAmountToKill()) {
							taskMasterKills.incrementAmountKilled(taskMasterKills.getAmountToKill());
						} else {
							taskMasterKills.incrementAmountKilled(essence);
						}
						player.getTaskMaster().trackActivity(player, taskMasterKills);
						break;
					}
				}

				player.getItems().deleteItem2(7936, essence);
				player.getItems().deleteItem2(1436, essence);
				player.gfx100(186);
				player.startAnimation(791);
				double percentOfXp = (data.getExperience() * count * 2.5) + getRcEquipmentCount(player);
				player.getPA().addSkillXPMultiplied((int) (((data.getExperience()) * count) + (player.getItems().isWearingItem(20008) ? percentOfXp : 0)), 20, true);
				player.getItems().addItem(data.getRuneId(), (SkillcapePerks.isWearingMaxCape(player) && Misc.isLucky(10) || SkillcapePerks.RUNECRAFTING.isWearing(player) && Misc.isLucky(10) ? multiply*2 : multiply));
				player.getPA().requestUpdates();
				player.getPA().sendSound(207);
				petRoll(player, data);
			}
		}
	}

	public static void petRoll(Player player, RunecraftingData data) {
		boolean hasGuardian = IntStream.range(20665, 20691).anyMatch(id -> player.getItems().hasAnywhere(id));

		int petRate = player.skillingPetRateScroll ? (int) (data.getPetChance() * .75) : data.getPetChance();

		if (Misc.random(1000) > 975 && !player.getItems().hasAnywhere(26899)) {
			PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + player.getDisplayName() + "</col> successfully crafted a <col=CC0000>Greatish Guardian</col> pet!");
			player.getItems().addItemUnderAnyCircumstance(26899, 1);
			player.getCollectionLog().handleDrop(player, 5, 26899, 1);
		}

		if (!hasGuardian) {
			if (Misc.random(1000) > 975) {
				PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + player.getDisplayName() + "</col> successfully crafted a <col=CC0000>Rift guardian</col> pet!");
				player.getItems().addItemUnderAnyCircumstance(data.getPetId(), 1);
				player.getCollectionLog().handleDrop(player, 5, data.getPetId(), 1);
			}
		}
	}
}