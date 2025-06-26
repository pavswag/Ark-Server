package io.kyros.content.skills.thieving;

import com.google.common.collect.Lists;
import io.kyros.content.SkillcapePerks;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.achievement_diary.impl.*;
import io.kyros.content.bonus.BoostScrolls;
import io.kyros.content.event.eventcalendar.EventChallenge;
import io.kyros.content.skills.Skill;
import io.kyros.content.taskmaster.TaskMasterKills;
import io.kyros.model.Items;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.queue.RepeatingEntityTask;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.bank.BankItem;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;
import org.apache.commons.lang3.RandomUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A representation of the thieving skill. Support for both object and npc actions will be supported.
 *
 * @author Jason MacKeigan
 * @date Feb 15, 2015, 7:12:14 PM
 */
public class Thieving {

	private static final int[] rogueOutfit = { 5553, 5554, 5555, 5556, 5557 };

	/**
	 * The managing player of this class
	 */
	private final Player player;

	/**
	 * The last interaction that player made that is recorded in milliseconds
	 */
	public long lastInteraction;

	/**
	 * The constant delay that is required inbetween interactions
	 */
	private static final long INTERACTION_DELAY = 1_500L;

	/**
	 * The stealing animation
	 */
	private static final int ANIMATION = 881;

	/**
	 * Constructs a new {@link Thieving} object that manages interactions between players and stalls, as well as players and non playable characters.
	 *
	 * @param player the visible player of this class
	 */
	public Thieving(final Player player) {
		this.player = player;
	}

	public void doStealStall(Stall stall, Location3D location) {
		/*int thieveAmount;

		if (player.getCurrentPet().hasPerk("uncommon_sticky_fingers")) {
			thieveAmount = (int) player.getCurrentPet().findPetPerk("uncommon_sticky_fingers").getValue();
			for (int i = 0; i < thieveAmount; i++) {
				steal(stall, location);
			}
			return;
		}


		player.getEntityQueue().repeat(new RepeatingEntityTask((task) -> {
			task.wait(2);
		}));*/


		steal(stall, location);
	}
	/**
	 * A method for stealing from a stall
	 *
	 * @param stall the stall being stolen from
	 * @param location the location of the stall
	 */
	private void steal(Stall stall, Location3D location) {
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			//player.sendMessage("You must wait a few more seconds before you can steal again.");
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.sendMessage("You need at least one free slot to steal from this.");
			return;
		}

		if (player.playerLevel[Skill.THIEVING.getId()] < stall.level) {
			player.sendMessage("You need a thieving level of " + stall.level + " to steal from this.");
			return;
		}
		if (Misc.random(200) == 0 && player.getInterfaceEvent().isExecutable()) {
			player.getInterfaceEvent().execute();
			return;
		}
		for (TaskMasterKills taskMasterKills : player.getTaskMaster().taskMasterKillsList) {
			if (taskMasterKills.getDesc().equalsIgnoreCase("Steal from @whi@stalls")) {
				taskMasterKills.incrementAmountKilled(1);
				player.getTaskMaster().trackActivity(player, taskMasterKills);
			}
		}
		player.getEventCalendar().progress(EventChallenge.THIEVE_X_STALLS);
		switch (stall) {
			case Food:
				player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.TEA_STALL);
				break;
			case Crafting:
				if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
					player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.STEAL_CAKE);
				}
				break;
			case Magic:
				if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
					player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.STEAL_GEM_ARD);
				}
				if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
					player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.STEAL_GEM_FAL);
				}
				break;
			case Scimitar:
				break;
			case Fur:
				if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
					player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.STEAL_FUR);
				}
				break;
			case Gold:
			default:
				break;
		}
		player.facePosition(location.getX(), location.getY());
/**		if (Misc.random(stall.depletionProbability) == 0) {
 GlobalObject stallObj = Server.getGlobalObjects().get(objectId, location.getX(), location.getY(), location.getZ());
 if (stallObj != null) {
 Server.getGlobalObjects().add(new GlobalObject(4797, location.getX(), location.getY(), location.getZ(), stallObj.getFace(), 10, 8, stallObj.getObjectId()));
 }
 }
 */
		GameItem item = stall.item;
		ItemDef definition = ItemDef.forId(item.getId());
		int petRate = player.skillingPetRateScroll ? (int) (stall.petChance * .75) : stall.petChance;
		if (Misc.random(petRate) == 20 && player.getItems().getItemCount(20663, false) == 0 && player.petSummonId != 20663) {
			PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" + player.getDisplayName() + "</col> now goes hand in hand with a <col=CC0000>Rocky</col> pet!");
			player.getItems().addItemUnderAnyCircumstance(20663, 1);
			player.getCollectionLog().handleDrop(player, 5, 20663, 1);
		}
		int amount = item.getAmount();

		if (player.getCurrentPet().hasPerk("uncommon_fast_hands")) {
			if (player.getCurrentPet().findPetPerk("uncommon_fast_hands").getValue() >= 5) {
				if (Misc.random(0,100) >= 90) {
					amount *= 2;
				}
			}
		}

		player.startAnimation(ANIMATION);
		if (player.getHeight() >= 1) {
			player.getItems().sendItemToAnyTabOrDrop(new BankItem(item.getId(), amount), player.getX(), player.getY());
		} else {
			player.getItems().addItem(item.getId(),amount);
		}

		player.getPA().addSkillXPMultiplied((BoostScrolls.checkHarvestBoost(player) ? ((stall.experience * (1 + (getRoguesPieces() * 0.12)))* 1.12) : (stall.experience * (1 + (getRoguesPieces() * 0.12)))), Skill.THIEVING.getId(), true);
		player.sendMessage("You steal a " + definition.getName() + " from the stall.");
		Achievements.increase(player, AchievementType.THIEV, 1);
		lastInteraction = System.currentTimeMillis();
	}

	public int getRoguesPieces() {
		int pieces = 0;
		for (int aRogueOutfit : rogueOutfit) {
			if (player.getItems().isWearingItem(aRogueOutfit)) {
				pieces++;
			}
		}
		return pieces;
	}

	public void doStealNpc(Pickpocket pickpocket, NPC npc) {
		/*npc.revokeWalkingPrivilege = true;
		AtomicInteger thieveAmount = player.getCurrentPet().findPetPerk("uncommon_sticky_fingers").getValue() > 0D ? new AtomicInteger((int) player.getCurrentPet().findPetPerk("uncommon_sticky_fingers").getValue()) : new AtomicInteger(1);
		player.getEntityQueue().repeat(new RepeatingEntityTask((task) -> {
			if(npc == null || npc.isDeadOrDying()) {
				task.finish();
				return;
			}
			thieveAmount.getAndDecrement();
			player.sendMessage("You will thieve " + thieveAmount.get() + " more times!");
			if(thieveAmount.get() == 0) {
				npc.revokeWalkingPrivilege = false;
				task.finish();
			}
			task.wait(3);
		}));*/


		steal(pickpocket, npc);
	}

	/**
	 * A method for pick pocketing npc's
	 *
	 * @param pickpocket the pickpocket type
	 * @param npc the npc being pick pocketed
	 */
	public void steal(Pickpocket pickpocket, NPC npc) {
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			//player.sendMessage("You must wait a few more seconds before you can steal again.");
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.sendMessage("You need at least one free slot to steal from this npc.");
			return;
		}
		if (player.playerLevel[Skill.THIEVING.getId()] < pickpocket.level) {
			player.sendMessage("You need a thieving level of " + pickpocket.level + " to steal from this npc.");
			return;
		}
		if (Misc.random(200) == 0 && player.getInterfaceEvent().isExecutable()) {
			player.getInterfaceEvent().execute();
			return;
		}
		/**
		 * Incorporate chance for failure
		 */
		switch (pickpocket) {
			case FARMER:
				if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
					player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.PICKPOCKET_ARD);
				}
				if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
					player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.PICKPOCKET_MASTER_FARMER_FAL);
				}
				if (Boundary.isIn(player, Boundary.DRAYNOR_BOUNDARY)) {
					player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.PICKPOCKET_FARMER_DRAY);
				}
				break;
			case MAN:
				if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
					player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.PICKPOCKET_MAN);
				}
				if (Boundary.isIn(player, Boundary.LUMRIDGE_BOUNDARY)) {
					player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.PICKPOCKET_MAN_LUM);
				}
				break;
			case GNOME:
				player.getDiaryManager().getWesternDiary().progress(WesternDiaryEntry.PICKPOCKET_GNOME);
				break;
			case HERO:
				player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.PICKPOCKET_HERO);
				break;
			case MENAPHITE_THUG:
				player.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.PICKPOCKET_THUG);
				break;
			default:
				break;

		}

		player.facePosition(npc.getX(), npc.getY());
		player.startAnimation(ANIMATION);
		GameItem item = pickpocket.getRandomItem();
		boolean maxCape = SkillcapePerks.THIEVING.isWearing(player) || SkillcapePerks.isWearingMaxCape(player);
		if (item != null) {
			int amt = maxCape ? item.getAmount() * 2 : item.getAmount();
			if (player.getPerkSytem().gameItems.stream().anyMatch(item1 -> item1.getId() == 33076) && Misc.random(0,100) <= 10) {
				amt *= 2;
			}
			player.getItems().addItem(item.getId(), amt);
		} else {
			player.sendMessage("You were unable to find anything useful.");
		}
		int petRate = player.skillingPetRateScroll ? (int) (pickpocket.petChance * .75) : pickpocket.petChance;
		if (Misc.random(petRate) == 20 && player.getItems().getItemCount(20663, false) == 0 && player.petSummonId != 20663) {
			PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr20@ <col=255>" + player.getDisplayName() + "</col> now goes hand in hand with a <col=CC0000>Rocky</col> pet!");
			player.getItems().addItemUnderAnyCircumstance(20663, 1);
			player.getCollectionLog().handleDrop(player, 5, 20663, 1);
		}
		Achievements.increase(player, AchievementType.THIEV, 1);
		player.getPA().addSkillXPMultiplied((int) (pickpocket.experience * (1 + (getRoguesPieces() * 0.65))), Skill.THIEVING.getId(), true);
		lastInteraction = System.currentTimeMillis();
	}

	private enum Rarity {
		ALWAYS(0), COMMON(5), UNCOMMON(10), RARE(15), VERY_RARE(25);

		/**
		 * The rarity
		 */
		private final int rarity;

		/**
		 * Creates a new rarity
		 *
		 * @param rarity the rarity
		 */
		Rarity(int rarity) {
			this.rarity = rarity;
		}
	}

	@SuppressWarnings("serial")
	public enum Pickpocket {
		MAN(1, 8, 2500, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.ALWAYS, Arrays.asList(new GameItem(995, 750), new GameItem(995, 1000), new GameItem(995, 1250)));
			}
		}),

		DRUNKEN_DWARF(75, 160, 2500, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.ALWAYS, Arrays.asList(
						new GameItem(995, 950),
						new GameItem(Items.COAL),
						new GameItem(Items.IRON_ORE),
						new GameItem(Items.BRONZE_BAR),
						new GameItem(Items.SEAWEED),
						new GameItem(Items.BEER),
						new GameItem(Items.BRONZE_PICKAXE),
						new GameItem(Items.RAW_COD)));

				put(Rarity.VERY_RARE, Arrays.asList(
						new GameItem(995, 7850),
						new GameItem(Items.RUNITE_ORE),
						new GameItem(Items.COAL_NOTED, 25),
						new GameItem(Items.RUNITE_BAR),
						new GameItem(Items.RUNE_PICKAXE)));
			}
		}),

		FARMER(38, 80, 2500, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.ALWAYS, Lists.newArrayList(
						new GameItem(Items.GUAM_SEED, 3), new GameItem(Items.MARRENTILL_SEED, 3),new GameItem(Items.TARROMIN_SEED, 3),
						new GameItem(Items.HARRALANDER_SEED, 3),

						new GameItem(Items.POTATO_SEED, 4),new GameItem(Items.ONION_SEED, 4),new GameItem(Items.CABBAGE_SEED, 4),
						new GameItem(Items.TOMATO_SEED, 4),new GameItem(Items.SWEETCORN_SEED, 4),new GameItem(Items.STRAWBERRY_SEED, 4),
						new GameItem(Items.WATERMELON_SEED, 4),new GameItem(Items.MARIGOLD_SEED, 4),new GameItem(Items.NASTURTIUM_SEED, 4),
						new GameItem(Items.WOAD_SEED, 4),new GameItem(Items.LIMPWURT_SEED, 4)
				));

				put(Rarity.UNCOMMON, Lists.newArrayList(
						new GameItem(Items.RANARR_SEED, 3),new GameItem(Items.TOADFLAX_SEED, 3),
						new GameItem(Items.IRIT_SEED, 3),new GameItem(Items.AVANTOE_SEED, 3),
						new GameItem(Items.KWUARM_SEED, 3),new GameItem(Items.SNAPDRAGON_SEED, 3),
						new GameItem(Items.CADANTINE_SEED, 3),new GameItem(Items.LANTADYME_SEED, 3),
						new GameItem(Items.DWARF_WEED_SEED, 3),new GameItem(Items.TORSTOL_SEED, 3)
				));
			}

		}), MENAPHITE_THUG(65, 75, 2500, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.ALWAYS, Arrays.asList(new GameItem(995, 1000), new GameItem(995, 800), new GameItem(995, 950)));
			}
		}), GNOME(75, 85, 2500, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.ALWAYS, Arrays.asList(new GameItem(995, 1200), new GameItem(995, 800), new GameItem(995, 1250)));
				put(Rarity.UNCOMMON, Arrays.asList(new GameItem(444), new GameItem(557), new GameItem(13431, 5)));
			}
		}), HERO(80, 180, 2500, new HashMap<Rarity, List<GameItem>>() {
			{
				put(Rarity.ALWAYS, Arrays.asList(new GameItem(995, 1500), new GameItem(995, 1800), new GameItem(995, 3500)));
				put(Rarity.UNCOMMON, Arrays.asList(new GameItem(560, 2), new GameItem(565), new GameItem(444), new GameItem(1601)));
			}
		});

		/**
		 * The level required to pickpocket
		 */
		private final int level;

		/**
		 * The experience gained from the pick pocket
		 */
		private final int experience;

		/**
		 * The chance of receiving a pet
		 */
		private final int petChance;

		/**
		 * The list of possible items received from the pick pocket
		 */
		private Map<Rarity, List<GameItem>> items = new HashMap<>();

		/**
		 * Creates a new pickpocket level requirement and experience gained
		 *
		 * @param level the level required to steal from
		 * @param experience the experience gained from stealing
		 */
		Pickpocket(int level, int experience, int petChance, Map<Rarity,List<GameItem>> items) {
			this.level = level;
			this.experience = experience;
			this.petChance = petChance;
			this.items = items;
		}

		GameItem getRandomItem() {
			if (this == DRUNKEN_DWARF && Misc.trueRand(5_000) == 0) {
				return new GameItem(Items.AMMO_MOULD);
			}

			for (Entry<Rarity, List<GameItem>> entry : items.entrySet()) {
				final Rarity rarity = entry.getKey();

				if (rarity == Rarity.ALWAYS) {
					continue;
				}
				final List<GameItem> items = entry.getValue();

				if (items.isEmpty()) {
					continue;
				}

				if (RandomUtils.nextInt(1, rarity.rarity) == 1) {
					return Misc.getItemFromList(items).randomizedAmount();
				}
			}

			List<GameItem> always = items.getOrDefault(Rarity.ALWAYS, Lists.newArrayList());

			if (!always.isEmpty()) {
				return Misc.getItemFromList(always).randomizedAmount();
			}

			return null;
		}
	}

	public enum Stall {
		Crafting(new GameItem(1893), 1, 16, 20, 2500),
		Silk(new GameItem(950), 25, 30, 10, 2500),
		Silver(new GameItem(2961), 50, 54, 10, 2500),
		Fur(new GameItem(6814), 65, 80, 10, 2500),
		Magic(new GameItem(1613), 90, 120, 10, 2500),
		Food(new GameItem(712), 25, 30, 10, 2500),
		General(new GameItem(2961), 50, 54, 10, 2500),
		Scimitar(new GameItem(1993), 90, 120, 10, 2500),
		Spice(new GameItem(2007), 50, 54, 10, 2500),
		Gold(new GameItem(4692), 95, 220, 10, 2500),
		LZ_GOLD(new GameItem(19473), 99, 120, 10, 2500);

		/**
		 * The item received from the stall
		 */
		private final GameItem item;

		/**
		 * The experience gained in thieving from a single stall thieve
		 */
		private final double experience;

		/**
		 * The probability that the stall will deplete
		 */
		private final int depletionProbability;

		/**
		 * The level required to steal from the stall
		 */
		private final int level;

		/**
		 * The chance of receiving a pet
		 */
		private final int petChance;

		/**
		 * Constructs a new {@link Stall} object with a single parameter, {@link GameItem} which is the item received when interacted with.
		 *
		 * @param item the item received upon interaction
		 */
		Stall(GameItem item, int level, int experience, int depletionProbability, int petChance) {
			this.item = item;
			this.level = level;
			this.experience = experience;
			this.depletionProbability = depletionProbability;
			this.petChance = petChance;
		}
	}

}
