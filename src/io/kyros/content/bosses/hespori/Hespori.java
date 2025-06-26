package io.kyros.content.bosses.hespori;

import com.google.common.collect.Lists;
import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.event.eventcalendar.EventChallenge;
import io.kyros.content.leaderboards.LeaderboardType;
import io.kyros.content.leaderboards.LeaderboardUtils;
import io.kyros.content.skills.Skill;
import io.kyros.model.Items;
import io.kyros.model.definitions.NpcStats;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.kyros.Server.getNpcs;
import static io.kyros.content.combat.HitMask.HIT;


public class Hespori {

	private static final List<HesporiBonus> bonuses = Lists.newArrayList(new AttasBonus(), new IasorBonus(), new KronosBonus(),
			new BuchuBonus(), new CelastrusBonus(), new GolparBonus(), new KeldaBonus(), new NoxiferBonus(), new ConsecrationBonus(),
			new AchieveBonus(), new EnhancedNoxifierBonus(), new EnhancedKronosBonus(), new EnhancedIasorBonus(), new EnhancedAchieveBonus(), new DamageBonus()
	);
	public static final int HESPORI_PLANTER_OBJECT = 33983;
	public static final int[] HESPORI_RARE_SEEDS = {
					HesporiBonusPlant.KRONOS.getItemId(),
					HesporiBonusPlant.IASOR.getItemId(),
					HesporiBonusPlant.ATTAS.getItemId(),
					HesporiBonusPlant.KELDA.getItemId(),
					HesporiBonusPlant.NOXIFER.getItemId(),
					HesporiBonusPlant.BUCHU.getItemId(),
					HesporiBonusPlant.CELASTRUS.getItemId(),
					HesporiBonusPlant.GOLPAR.getItemId(),
					HesporiBonusPlant.CONSECRATION.getItemId()
	};
	/**
	 * Variables
	 */
    public static final int[] HESPORI_GROW_PHASE_OBJECTS = {33726, 33727, 33728, 33729};
	public static final int FINAL_OBJECT_ID = 33730;
	public static final int KEY = 22374;

	public static final int NPC_ID = 8583;

	public static final int X = 2455;
	public static final int Y = 3545;
	public static final int SPAWN_ANIMATION = 8221;
	public static final int DEATH_ANIMATION = 8225;

	public static final int RANGE_ANIMATION = 8224;
	public static final int MAGIC_ANIMATION = 8223;

	public static final int RANGE_PROJECTILE = 1639;
	public static final int MAGIC_PROJECTILE = 1640;
	public static final int SPECIAL_PROJECTILE = 1642;

	public static final int SPECIAL_HIT_GFX = 179;

	public static final int ESSENCE_REQUIRED = 100;
	public static final int TOXIC_GEM_EFFECT = 100;

	public static int TOXIC_GEM_AMOUNT = 0;
	public static int HESPORI_DEFENCE = 3000;

	public static int TOTAL_ESSENCE_BURNED = 0;

	public static boolean ENOUGH_BURNED = false;
	public static boolean isWeak = false;


    /**
	 * Hespori rewards player
	 * @param eventCompleted
	 */
	public static void rewardPlayers(boolean eventCompleted) {
		TOTAL_ESSENCE_BURNED = 0;
		TOXIC_GEM_AMOUNT = 0;
		HESPORI_DEFENCE = 3000;
		ENOUGH_BURNED = false;
		isWeak = false;
		HesporiSpawner.despawn();
		Server.getGlobalObjects().add(new GlobalObject(FINAL_OBJECT_ID, X, Y,
				0, 1, 10, -1, -1)); // West - Empty Altar
		Server.getPlayers().nonNullStream().filter(p -> Boundary.isIn(p, Boundary.HESPORI))
		.forEach(p -> {
			if (!eventCompleted) {
				p.sendMessage("@blu@Hespori event was ended before she was killed!");
				p.canLeaveHespori = true;
				p.getPA().startTeleport2(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0);
				p.setHesporiDamageCounter(0);
				deleteEventItems(p);
			} else {
				if (p.getHesporiDamageCounter() >= 100) {
					p.sendMessage("@blu@Hespori has been killed!");
					p.sendMessage("@blu@Harvest Hespori with an axe to receive your reward!");
					p.canLeaveHespori = true;
					p.canHarvestHespori = true;
					p.setHesporiDamageCounter(0);
					Pass.addExperience(p,5);
					p.getEventCalendar().progress(EventChallenge.OBTAIN_X_HESPORI_EVENT_KEYS);
					LeaderboardUtils.addCount(LeaderboardType.HESPORI, p, 1);
					Achievements.increase(p, AchievementType.HESPORI, 1);
					p.getNpcDeathTracker().add("hespori", 300, 5);
					deleteEventItems(p);
				} else {
					p.sendMessage("@blu@You were not active enough to receive a reward.");
					p.canLeaveHespori = true;
					p.getPA().startTeleport2(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0);
					p.setHesporiDamageCounter(0);
					deleteEventItems(p);
				}
			}
		});
	}
	/**
	 * Hespori Boss Event Mechanics
	 */
	public static void useBurningRune(Player c) {//damages Hespori
		int amount = c.getItems().getItemAmount(9699);
		int setBurnAmount = Misc.random((TOXIC_GEM_AMOUNT * amount) * 15); //player max hit is equal to amount of used toxic gems
		NPC npc = getNpcs().get(c.npcClickIndex);
		if (npc != null && npc.getNpcId() == NPC_ID) {
			if (ENOUGH_BURNED) {
				if (TOXIC_GEM_AMOUNT < 80) {
					c.sendMessage("@blu@Hesporis defence is still very high!");
					c.appendDamage(npc, Misc.random(15), HIT);
				} else {
					c.appendDamage(npc, setBurnAmount, HIT);
					c.sendMessage("@blu@Your burning runes vanish as they damage Hespori.");
				}
			}
		}
	}

	public static void useToxicGem(Player c) {//lowers Hespori defence
		int amount = c.getItems().getInventoryCount(23783);
		int lowerDefBy = TOXIC_GEM_EFFECT * TOXIC_GEM_AMOUNT;
		int setNewDefence = HESPORI_DEFENCE - lowerDefBy;
		NPC npc = getNpcs().get(c.npcAttackingIndex);
		c.setHesporiDamageCounter(c.getHesporiDamageCounter() + (15 * amount));
		if (npc != null && npc.getNpcId() == NPC_ID) {
			TOXIC_GEM_AMOUNT += amount;
			c.getItems().deleteItem2(23783, amount);
			c.getPA().addSkillXPMultiplied(110 * amount, 12, true);//crafting
			c.getPA().addSkillXPMultiplied(20 * amount, 14, true);//mining
			npc.setNpcStats(NpcStats.builder().setDefenceLevel(setNewDefence).createNpcStats());
			Server.getPlayers().nonNullStream().filter(p -> Boundary.isIn(p, Boundary.HESPORI))
					.forEach(p -> {

						if (TOXIC_GEM_AMOUNT < 100) {
							p.sendMessage("@blu@Hespori's defence has been lowered but is still too high!");
						} else if (!isWeak) {
							p.sendMessage("@red@Hespori is now weak and vulnerable to attacks but can still be weakened.");
							isWeak = true;
						} else if (TOXIC_GEM_AMOUNT > 100) {
							p.sendMessage("@red@Hespori is now extremely weak!");
						}
					});
		}
	}

	public static void burnEssence(Player c) {
		int amount = c.getItems().getItemAmount(9017);

		if (!HesporiSpawner.isSpawned()) {
			c.sendMessage("@red@You cannot do this right now.");
			return;
		}
		if (c.getItems().playerHasItem(9017, 1)) {
			c.getItems().deleteItem2(9017, amount);
			TOTAL_ESSENCE_BURNED += amount;
			int ESSENCE_LEFT = ESSENCE_REQUIRED - TOTAL_ESSENCE_BURNED;
			if (ESSENCE_LEFT <= 0 && ENOUGH_BURNED != true) {
				ENOUGH_BURNED = true;
				c.setHesporiDamageCounter(c.getHesporiDamageCounter() + (15 * amount));
				c.getPA().addSkillXPMultiplied(120 * amount, 11, true);

				PlayerHandler.executeGlobalMessage("@bla@[@gre@Hespori@bla@]@red@Enough essence has burned! @gre@Hespori @red@can now be attacked!");

			} else if (ESSENCE_LEFT >= 1) {
				c.sendMessage("@red@" + ESSENCE_LEFT + " essence are still required to be burned.");
				c.setHesporiDamageCounter(c.getHesporiDamageCounter() + (15 * amount));
				c.getPA().addSkillXPMultiplied(120 * amount, 11, true);
			}
		} else if (!c.getItems().playerHasItem(9698, 1)) {
			c.sendMessage("You have no essence to burn.");
		} else {
			c.sendMessage("You have no essence to burn. Right-click the fire to burn your runes.");
		}
	}
	public static void burnRunes(Player c) {
		int amount = c.getItems().getInventoryCount(9698);
		if (!HesporiSpawner.isSpawned()) {
			c.sendMessage("@red@You cannot do this right now.");
			return;
		}
/*		if (c.getLevelForXP(c.playerXP[20]) < 50 || c.getLevelForXP(c.playerXP[13]) < 50) {
			c.sendMessage("You need a Smithing and Runecrafting level of 50 to burn these.");
			return;
		}*/
		if (c.getItems().playerHasItem(9698, 1)) {
			c.getItems().deleteItem2(9698, amount);
			c.getItems().addItem(9699, amount);
			c.sendMessage("@red@Your runes become hot to the touch.");
			c.setHesporiDamageCounter(c.getHesporiDamageCounter() + (15 * amount));
		} else {
			c.sendMessage("@red@You have no runes to burn.");
		}
	}

	public static boolean useSeedOnPatch(Player player, int objectId, int itemId) {
		if (objectId == 33983 || Arrays.stream(HesporiBonusPlant.values()).anyMatch(plant -> plant.getObjectId() == objectId)) {
			List<HesporiBonus> list = bonuses.stream().filter(bonus -> bonus.getPlant().getItemId() == itemId).collect(Collectors.toList());

			if (list.isEmpty()) {
				player.sendMessage("You need a Hespori seed to plant here.");
			} else if (list.size() > 1) {
				player.sendMessage("You have too many @gre@Hespori seeds@bla@ please bring only 1 seed type.");
			} else {
				plant(player, list.get(0));
			}
			return true;
		}
		return false;
	}

	public static boolean clickObject(Player player, int objectId) {
		if (objectId == HESPORI_PLANTER_OBJECT || Arrays.stream(HesporiBonusPlant.values()).anyMatch(plant -> plant.getObjectId() == objectId)) {
			List<HesporiBonus> list = bonuses.stream().filter(bonus -> player.getItems().playerHasItem(bonus.getPlant().getItemId())).collect(Collectors.toList());

			if (!list.isEmpty()) {
				for (HesporiBonus hesporiBonus : list) {
					if (hesporiBonus.canPlant(player)) {
						plant(player, hesporiBonus);
					}
				}
			}
			return true;
		}

		return false;
	}

	public static boolean clickNpc(Player player, int npc) {
		if (npc == 6770) {
			List<HesporiBonus> list = bonuses.stream().filter(bonus -> player.getItems().playerHasItem(bonus.getPlant().getItemId())).collect(Collectors.toList());

			if (list.isEmpty()) {
				player.sendMessage("You need a Hespori seed to plant here.");
			} else if (list.size() > 1) {
				player.sendMessage("You have too many @gre@Hespori seeds@bla@ please bring only 1 seed type.");
			} else {
				plant(player, list.get(0));
			}
			return true;
		}
		return false;
	}

	private static void plant(Player player, HesporiBonus hesporiBonus) {
		if (!player.getItems().playerHasItem(hesporiBonus.getPlant().getItemId())) {
			player.sendMessage("You don't have the seed.");
			player.getPA().removeAllWindows();
			return;
		}

		 if (hesporiBonus.canPlant(player)) {
			player.getItems().deleteItem(hesporiBonus.getPlant().getItemId(), 1);
			hesporiBonus.activate(player);
			hesporiBonus.updateObject(true);

			 player.getPA().addSkillXPMultiplied(2000, Skill.FARMING.getId(), true);

			player.getItems().addItemUnderAnyCircumstance(Items.HESPORI_KEY, 3);
			player.getItems().addItemUnderAnyCircumstance(21046, 1);
		} else {
			 player.sendMessage("You can't plant while " + hesporiBonus.getPlant().name() + " is active!");
		 }
	}

	public static void deleteEventItems(Player c) {
		if (c.getItems().getInventoryCount(9698) > 0) {
			c.getItems().deleteItem2(9698, c.getItems().getInventoryCount(9698));
		}
		if (c.getItems().getInventoryCount(9699) > 0) {
			c.getItems().deleteItem2(9699, c.getItems().getInventoryCount(9699));
		}
		if (c.getItems().getInventoryCount(23778) > 0) {
			c.getItems().deleteItem2(23778, c.getItems().getInventoryCount(23778));
		}
		if (c.getItems().getInventoryCount(23783) > 0) {
			c.getItems().deleteItem2(23783, c.getItems().getInventoryCount(23783));
		}
		if (c.getItems().getInventoryCount(9017) > 0) {
			c.getItems().deleteItem2(9017, c.getItems().getInventoryCount(9017));
		}
	}

	/**
	 * Hespori Seeds Bonus Time Handling
	 */

	public static long ATTAS_TIMER, KRONOS_TIMER, IASOR_TIMER, GOLPAR_TIMER, BUCHU_TIMER,
			NOXIFER_TIMER, KELDA_TIMER, CELASTRUS_TIMER, CONSECRATION_TIMER, ACHIEVE_TIMER,
			ENHANCED_NOXIFER_TIMER, ENHANCED_KRONOS_TIMER, ENHANCED_IASOR_TIMER, ENHANCED_ACHIEVE_TIMER, ENHANCED_DAMAGE_TIMER;
	public static boolean activeAttasSeed = false;
	public static boolean activeKronosSeed = false;
	public static boolean activeIasorSeed = false;
	public static boolean activeBuchuSeed = false;
	public static boolean activeNoxiferSeed = false;
	public static boolean activeGolparSeed = false;
	public static boolean activeKeldaSeed = false;
	public static boolean activeCelastrusSeed = false;
	public static boolean activeConsecrationSeed = false;
	public static boolean activeAchieveSeed = false;
	public static boolean activeEnhancedNoxiferSeed = false;
	public static boolean activeEnhancedKronosSeed = false;
	public static boolean activeEnhancedIasorSeed = false;
	public static boolean activeEnhancedAchieveSeed = false;
	public static boolean activeEnhancedDamageSeed = false;

	public static String activeAnimaBonus() {
		if (Hespori.ATTAS_TIMER > 0) {
			return "Anima: @gre@Attas [Bonus XP]";
		}
		if (Hespori.KRONOS_TIMER > 0) {
			return "Anima: @gre@Kronos [x2 Raids 1 Keys]";
		}
		if (Hespori.IASOR_TIMER > 0) {
			return "Anima: @gre@Iasor [+10% DR]";
		}
		return "Anima: @red@None";
	}

}

