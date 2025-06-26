package io.kyros.content.skills.mining;

import io.kyros.Server;
import io.kyros.content.SkillcapePerks;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.achievement_diary.impl.DesertDiaryEntry;
import io.kyros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.kyros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.kyros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.kyros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.kyros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.kyros.content.achievement_diary.impl.WildernessDiaryEntry;
import io.kyros.content.bonus.BoostScrolls;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.events.monsterhunt.ShootingStars;
import io.kyros.content.questing.Quest;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.smithing.Smelting;
import io.kyros.content.taskmaster.TaskMasterKills;
import io.kyros.content.wilderness.ActiveVolcano;
import io.kyros.model.cycleevent.Event;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;
import org.apache.commons.lang3.RandomUtils;

/**
 * Represents a singular event that is executed when a player attempts to mine.
 *
 * @author Jason MacKeigan
 * @date Feb 18, 2015, 6:17:11 PM
 */
public class MiningEvent extends Event<Player> {

	public static int[] prospectorOutfit = { 12013, 12014, 12015, 12016 };
	public static int[] GoldprospectorOutfit = { 25549, 25551, 25553, 25555 };

	/**
	 * The amount of cycles that must pass before the animation is updated
	 */
	private final int ANIMATION_CYCLE_DELAY = 3;

	/**
	 * The value in cycles of the last animation
	 */
	private int lastAnimation;

	/**
	 * The pickaxe being used to mine
	 */
	private final Pickaxe pickaxe;

	/**
	 * The mineral being mined
	 */
	private final Mineral mineral;

	/**
	 * The object that we are mning
	 */
	private int objectId;

	/**
	 * The location of the object we're mining
	 */
	private final Location3D location;

	/**
	 * The npc the player is mining, if any
	 */
	private NPC npc;

	/**
	 * Constructs a new {@link MiningEvent} for a single player
	 *
	 * @param attachment the player this is created for
	 * @param objectId the id value of the object being mined from
	 * @param location the location of the object being mined from
	 * @param mineral the mineral being mined
	 * @param pickaxe the pickaxe being used to mine
	 */
	public MiningEvent(Player attachment, int objectId, Location3D location, Mineral mineral, Pickaxe pickaxe, int time) {
		super("skilling", attachment, time);
		this.objectId = objectId;
		this.location = location;
		this.mineral = mineral;
		this.pickaxe = pickaxe;
	}

	/**
	 * Constructs a new {@link MiningEvent} for a single player
	 *
	 * @param attachment the player this is created for
	 * @param npc the npc being from from
	 * @param location the location of the npc
	 * @param mineral the mineral being mined
	 * @param pickaxe the pickaxe being used to mine
	 */
	public MiningEvent(Player attachment, NPC npc, Location3D location, Mineral mineral, Pickaxe pickaxe, int time) {
		super("skilling", attachment, time);
		this.npc = npc;
		this.location = location;
		this.mineral = mineral;
		this.pickaxe = pickaxe;
	}

	@Override
	public void update() {
		if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
			stop();
			return;
		}
		if (!attachment.getItems().playerHasItem(pickaxe.getItemId()) && !attachment.getItems().isWearingItem(pickaxe.getItemId())) {
			attachment.sendMessage("That is strange! The pickaxe could not be found.");
			stop();
			return;
		}
		if (attachment.getItems().freeSlots() == 0) {
			attachment.getDH().sendStatement("You have no more free slots.");
			stop();
			return;
		}
		if (RandomUtils.nextInt(1, 300) == 1 && attachment.getInterfaceEvent().isExecutable() && attachment.wildLevel < 30) {
			attachment.getInterfaceEvent().execute();
			stop();
			return;
		}
		if (objectId > 0) {
			if (Server.getGlobalObjects().exists(Mineral.EMPTY_VEIN, location.getX(), location.getY(), location.getZ()) && mineral.isDepletable()) {
				attachment.sendMessage("This vein contains no more minerals.");
				stop();
				return;
			}
		} else {
			if (npc == null || npc.isDead()) {
				attachment.sendMessage("This vein contains no more minerals.");
				stop();
				return;
			}
		}
		if (super.getElapsedTicks() - lastAnimation > ANIMATION_CYCLE_DELAY) {
			attachment.startAnimation(pickaxe.getAnimation());
			lastAnimation = super.getElapsedTicks();
			attachment.getPA().sendSound(432);
		}
	}
	private static void foeArtefact(Player player) {
		int chance = 250;
		int artefactRoll = Misc.random(100);
		if (Misc.random(chance) == 1) {
			if (artefactRoll <65) {//1/300
				player.getItems().addItemUnderAnyCircumstance(11180, 1);//ancient coin foe for 200
				player.sendMessage("You found a coin that can be dissolved, speak to Nomad!");
			} else if (artefactRoll >= 65 && artefactRoll < 99) {//1/600
				player.getItems().addItemUnderAnyCircumstance(681, 1);//anicent talisman foe for 300
				player.sendMessage("You found a talisman that can be dissolved, speak to Nomad!");
			} else if (artefactRoll > 99){//1/1000
				player.getItems().addItemUnderAnyCircumstance(9034, 1);//golden statuette foe for 500
				PlayerHandler.executeGlobalMessage("@bla@[@red@Mining@bla@]@blu@ " + player.getDisplayName() + " @red@just found a Golden statuette while mining!");
			}
		}
	}

	public static double xpBonus(Player player) {
		double multiplier = 1;
		for (int k : prospectorOutfit) {
			if (player.getItems().isWearingItem(k)) {
				multiplier += 0.25;
			}
		}
		for (int j : GoldprospectorOutfit) {
			if (player.getItems().isWearingItem(j)) {
				multiplier += 0.35;
			}
		}
		return multiplier;
	}

	@Override
	public void execute() {
		double osrsExperience = 0;
		int pieces = 1;
		for (int k : prospectorOutfit) {
			if (attachment.getItems().isWearingItem(k)) {
				pieces += 6;
			}
		}
		for (int j : GoldprospectorOutfit) {
			if (attachment.getItems().isWearingItem(j)) {
				pieces += 8;
			}
		}
		if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
			stop();
			return;
		}
		if (mineral.isDepletable()) {
			if (RandomUtils.nextInt(0, mineral.getDepletionProbability()) == 0
					|| mineral.getDepletionProbability() == 0) {
				if (objectId > 0) {
					Server.getGlobalObjects().add(new GlobalObject(Mineral.EMPTY_VEIN, location.getX(), location.getY(),
							location.getZ(), 0, 10, mineral.getRespawnRate(), objectId));
				} else {
					npc.setDead(true);
					npc.actionTimer = 0;
					npc.needRespawn = false;
				}
			}
		}
		for (TaskMasterKills taskMasterKills : attachment.getTaskMaster().taskMasterKillsList) {
			if (taskMasterKills.getDesc().equalsIgnoreCase("Mine @whi@Ores")) {
				taskMasterKills.incrementAmountKilled(1);
				attachment.getTaskMaster().trackActivity(attachment, taskMasterKills);
			}
		}
		attachment.facePosition(location.getX(), location.getY());
		Achievements.increase(attachment, AchievementType.MINE, 1);

		for (Quest quest : attachment.getQuesting().getQuestList()) {
			if (quest.getName().equalsIgnoreCase("Santa's Troubles") && quest.getStage() == 1 && attachment.getPresentCounter() <= 20) {
				if (Misc.isLucky(75)) {
					attachment.sendMessage("@red@You find a christmas present while mining");
					attachment.setPresentCounter(attachment.getPresentCounter() + 1);
					if (attachment.getPresentCounter() == 20) {
						quest.incrementStage();
						attachment.sendMessage("@red@It looks like you've found all the present in the area, you need to return to santa.");
					}
				}
				break;
			}
		}

		foeArtefact(attachment);
		if (Boundary.isIn(attachment, Boundary.RESOURCE_AREA)) {
			if (Misc.random(20) == 5) {
				int randomAmount = 1;
				attachment.sendMessage("You received " + randomAmount + " pkp while mining!");
				attachment.getItems().addItem(2996, randomAmount);
			}
		}

		/**
		 * Experience calculation
		 */
		osrsExperience = mineral.getExperience() + (mineral.getExperience() / 10) * pieces;

		if (attachment.getItems().playerHasItem(23760) || (attachment.hasFollower && (attachment.petSummonId == 23760))) {
			osrsExperience = osrsExperience + (osrsExperience*(10.0/100.0));
		}
		if (BoostScrolls.checkHarvestBoost(attachment)) {
			osrsExperience *= 1.15;
		}
		attachment.getPA().addSkillXPMultiplied((int) osrsExperience, Skill.MINING.getId(), true);
		switch (mineral) {
			case ADAMANT:
				break;
			case COAL:
				if (Boundary.isIn(attachment, Boundary.RELLEKKA_BOUNDARY)) {
					attachment.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.MINE_COAL_FREM);
				}
				break;
			case COPPER:
				break;
			case ESSENCE:
				attachment.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.MINE_ESSENCE);
				break;
			case GEM_DZ:
			case GEM:
				attachment.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.MINE_GEM_FAL);
				break;

			case GOLD:
				if (Boundary.isIn(attachment, Boundary.TZHAAR_CITY_BOUNDARY)) {
					attachment.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.MINE_GOLD_KAR);
				}
				if (Boundary.isIn(attachment, Boundary.RELLEKKA_BOUNDARY)) {
				}
				break;
			case IRON:
				if (attachment.getPosition().inWild()) {
					attachment.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.MINE_IRON_WILD);
				}
				if (Boundary.isIn(attachment, Boundary.VARROCK_BOUNDARY)) {
					attachment.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.MINE_IRON);
				}
				if (Boundary.isIn(attachment, Boundary.AL_KHARID_BOUNDARY)) {
					attachment.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.MINE_IRON_LUM);
				}
				break;
			case MITHRIL:
				if (attachment.getPosition().inWild()) {
					attachment.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.MINE_MITHRIL_WILD);
				}
				break;
			case TIN:
				break;
			case CLAY:
				attachment.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.MINE_CLAY);
				break;
			default:
				break;

		}
		int amount = SkillcapePerks.MINING.isWearing(attachment) || SkillcapePerks.isWearingMaxCape(attachment) ? 2 :
				attachment.getRechargeItems().hasItem(13104) && Misc.random(9) == 2 ? 2 :
						attachment.getRechargeItems().hasItem(13105) && Misc.random(8) == 2 ? 2 :
								attachment.getRechargeItems().hasItem(13106) && Misc.random(6) == 2 ? 2 :
										attachment.getRechargeItems().hasItem(13107) && Misc.random(4) == 2 ? 2 : 1;

		int itemId = mineral.getMineralReturn().generate();
		if ((SkillcapePerks.MINING.isWearing(attachment) || SkillcapePerks.isWearingMaxCape(attachment)) && attachment.getItems().freeSlots() < 2) {
			attachment.sendMessage("You have run out of inventory space.");
			stop();
			return;
		}


		if (mineral == Mineral.BOULDER) {
			if (!ActiveVolcano.progress) {
				stop();
				return;
			}
			ActiveVolcano.removeShards(1);
			if (attachment.getPosition().inWild()) {
				amount *= 3;
			}
			if (attachment.getItems().playerHasItem(23760) || (attachment.hasFollower && (attachment.petSummonId == 23760))) {
				amount *= 2;
			}
			if (ActiveVolcano.BOULDER_STABILITY <= 0) {
				ActiveVolcano.removeBoulder(true);
				stop();
				return;
			}
		}
		if (mineral == Mineral.SHOOTING_STAR) {
			if (!ShootingStars.progress) {
				stop();
				return;
			}
			ShootingStars.removeShards(1);
			ShootingStars.rockCheck();
			if (attachment.getPosition().inWild()) {
				amount *= 3;
			}
			if (attachment.getItems().playerHasItem(23760) || (attachment.hasFollower && (attachment.petSummonId == 23760))) {
				amount *= 2;
			}
			if (ShootingStars.METEORITE_REMAINING <= 0) {
				ShootingStars.removeStar(true);
				stop();
				return;
			}

		}

		if (attachment.playerEquipment[Player.playerWeapon] == 25112 || attachment.playerEquipmentCosmetic[Player.playerWeapon] == 25112) {
			attachment.getItems().addItemToBankOrDrop(itemId, amount);
		} else {
			attachment.getItems().addItem(itemId, amount);
		}

		attachment.sendSpamMessage("You just mined some " + mineral.name().toLowerCase() + ".");//restart that so i can do client side  once i got these done i can send u other skilling messages if needed

		if (mineral == Mineral.GEM || mineral == Mineral.GEM_DZ) {
			if (itemId == 6571) {
				PlayerHandler.executeGlobalMessage("@pur@" + attachment.getDisplayNameFormatted() + " received a drop: " +
						"" + ItemDef.forId(itemId).getName() + " x " + amount + " from a <col=E9362B>Gem Rock</col>@pur@.");
			}
		}

		if (!mineral.getBarName().equalsIgnoreCase("none")) {
			if (Misc.random(2) == 0) {
				if (attachment.getItems().hasItemOnOrInventory(13243) || attachment.getItems().hasItemOnOrInventory(25063)) {
					Smelting.startSmelting(attachment, mineral.getBarName(), "ONE", "INFERNAL");
					return;
				}
			}
		}
		int dropRate = 20;
		switch (mineral) {
			case ADAMANT:
			case COAL:
			case GEM_DZ:
			case GEM:
			case GOLD:
			case MITHRIL:
			case RUNE:
			case AMETHYST:
				dropRate = 60;
				break;
			case COPPER:
			case IRON:
			case TIN:
			case CLAY:
			case ESSENCE:
				dropRate = 10;
				break;
			default:
				break;
		}
		if (attachment.fasterCluesScroll) {
			dropRate = dropRate*2;
		}
		int clueAmount = 1;
		if (Hespori.activeGolparSeed) {
			clueAmount = 2;
		}
		if (Misc.random(mineral.getPetChance() / dropRate) == 10 ) {
			switch (Misc.random(2)) {
				case 0:
					attachment.getItems().addItemUnderAnyCircumstance(20358, clueAmount);
					break;

				case 1:
					attachment.getItems().addItemUnderAnyCircumstance(20360, clueAmount);
					break;
				case 2:
					attachment.getItems().addItemUnderAnyCircumstance(20362, clueAmount);
					break;

			}
			attachment.sendMessage("@blu@You appear to see a clue geode fall within the rock, and pick it up.");
		}

		if (Misc.random(mineral.getPetChance()) / dropRate == 10) {
			attachment.getItems().addItemUnderAnyCircumstance(20362, clueAmount);
			attachment.sendMessage("@blu@You appear to see a clue geode fall within the rock, and pick it up.");
		}

		int petRate = attachment.skillingPetRateScroll ? (int) (mineral.getPetChance() * .75) : mineral.getPetChance();
		if (Misc.random(petRate) == 2 && attachment.getItems().getItemCount(13321, false) == 0
				&& attachment.petSummonId != 7439) {
			PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + attachment.getDisplayName()
					+ "</col> mined a rock and formed the <col=CC0000>Rock golem</col> pet!");
			attachment.getCollectionLog().handleDrop(attachment, 5, 13321, 1);
			attachment.getItems().addItemUnderAnyCircumstance(13321, 1);
		}
	}

	@Override
	public void stop() {
		super.stop();
		if (attachment == null) {
			return;
		}
		attachment.stopAnimation();
	}
}
