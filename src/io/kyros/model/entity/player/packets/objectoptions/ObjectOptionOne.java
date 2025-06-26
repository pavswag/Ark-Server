package io.kyros.model.entity.player.packets.objectoptions;

import com.google.common.collect.Lists;
import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.Obelisks;
import io.kyros.content.SkillcapePerks;
import io.kyros.content.WeaponGames.WGMedPack;
import io.kyros.content.WeaponGames.WGPowerup;
import io.kyros.content.achievement_diary.impl.*;
import io.kyros.content.afkzone.Afk;
import io.kyros.content.afkzone.AfkBoss;
import io.kyros.content.bosses.Cerberus;
import io.kyros.content.bosses.DonorBoss;
import io.kyros.content.bosses.Kraken;
import io.kyros.content.bosses.Vorkath;
import io.kyros.content.bosses.araxxor.AraxxorInstance;
import io.kyros.content.bosses.dukesucellus.DukeInstance;
import io.kyros.content.bosses.dukesucellus.DukeSucellus;
import io.kyros.content.bosses.godwars.impl.ArmadylInstance;
import io.kyros.content.bosses.godwars.impl.BandosInstance;
import io.kyros.content.bosses.godwars.impl.SaradominInstance;
import io.kyros.content.bosses.godwars.impl.ZamorakInstance;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.bosses.hespori.HesporiSpawner;
import io.kyros.content.bosses.hydra.AlchemicalHydra;
import io.kyros.content.bosses.sharathteerk.SharathteerkInstance;
import io.kyros.content.bosses.tumekens.TumekensInstance;
import io.kyros.content.bosses.wintertodt.Wintertodt;
import io.kyros.content.bosses.wintertodt.WintertodtActions;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.weapon.WeaponDataConstants;
import io.kyros.content.commands.owner.Pos;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.dialogue.impl.CrystalCaveEntryDialogue;
import io.kyros.content.dialogue.impl.FireOfDestructionDialogue;
import io.kyros.content.dialogue.impl.OutlastLeaderboard;
import io.kyros.content.dialogue.impl.SkillingPortalDialogue;
import io.kyros.content.donor.DonoSlayerInstances;
import io.kyros.content.donor.DonorVault;
import io.kyros.content.donor.NomadVault;
import io.kyros.content.event.eventcalendar.EventChallenge;
import io.kyros.content.event_manager.EventManager;
import io.kyros.content.event_manager.EventStage;
import io.kyros.content.event_manager.EventType;
import io.kyros.content.event_manager.impl.DropPartyEvent;
import io.kyros.content.item.lootable.other.*;
import io.kyros.content.item.lootable.impl.*;
import io.kyros.content.leaderboards.LeaderboardInterface;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.content.minigames.castlewars.CastleWarsObjects;
import io.kyros.content.minigames.donationgames.TreasureHandler;
import io.kyros.content.minigames.dz.BloodyMinigame;
import io.kyros.content.minigames.isle_of_the_damned.IsleOfTheDamned;
import io.kyros.content.minigames.isle_of_the_damned.IsleOfTheDamnedParty;
import io.kyros.content.minigames.pest_control.PestControl;
import io.kyros.content.minigames.pk_arena.Highpkarena;
import io.kyros.content.minigames.pk_arena.Lowpkarena;
import io.kyros.content.minigames.raids.CoxParty;
import io.kyros.content.minigames.raids.Raids;
import io.kyros.content.skills.FlaxPicking;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.agility.AgilityHandler;
import io.kyros.content.skills.agility.PyramidPlunder;
import io.kyros.content.skills.agility.impl.rooftop.RooftopArdougne;
import io.kyros.content.skills.agility.impl.rooftop.RooftopSeers;
import io.kyros.content.skills.agility.impl.rooftop.RooftopVarrock;
import io.kyros.content.skills.crafting.BraceletMaking;
import io.kyros.content.skills.crafting.JewelryMaking;
import io.kyros.content.skills.hunter.Hunter;
import io.kyros.content.skills.runecrafting.Runecrafting;
import io.kyros.content.skills.smithing.CannonballSmelting;
import io.kyros.content.skills.thieving.Thieving.Stall;
import io.kyros.content.skills.woodcutting.Tree;
import io.kyros.content.skills.woodcutting.Woodcutting;
import io.kyros.content.teleportv2.inter.TeleportInterface;
import io.kyros.content.tournaments.ViewingOrb;
import io.kyros.content.wilderness.SpiderWeb;
import io.kyros.content.wildwarning.WildWarning;
import io.kyros.model.Items;
import io.kyros.model.collisionmap.ObjectDef;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.player.*;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.entity.player.mode.group.GroupIronmanBank;
import io.kyros.model.entity.player.mode.group.GroupIronmanGroup;
import io.kyros.model.entity.player.mode.group.GroupIronmanRepository;
import io.kyros.model.entity.player.packets.objectoptions.impl.DarkAltar;
import io.kyros.model.entity.player.packets.objectoptions.impl.Overseer;
import io.kyros.model.entity.player.packets.objectoptions.impl.RaidObjects;
import io.kyros.model.entity.player.packets.objectoptions.impl.TrainCart;
import io.kyros.model.entity.player.save.PlayerSave;
import io.kyros.model.items.EquipmentSet;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ImmutableItem;
import io.kyros.model.lobby.LobbyManager;
import io.kyros.model.lobby.LobbyType;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.duel.DuelSession;
import io.kyros.model.multiplayersession.duel.DuelSessionRules.Rule;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.objects.Doors;
import io.kyros.objects.DoubleDoors;
import io.kyros.script.event.impl.GameObjectAction;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 * @author Matt
 * Handles all first options for objects.
 */

public class ObjectOptionOne {

	static int[] barType = { 2363, 2361, 2359, 2353, 2351, 2349 };

	public static void handleOption(final Player c, int objectType, int obX, int obY) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}

		if (c.teleTimer > 0) {
			return;
		}

		GlobalObject object = new GlobalObject(objectType, obX, obY, c.heightLevel);

		if (c.getRights().isOrInherits(Right.STAFF_MANAGER) && c.debugMessage)
			c.sendMessage("Clicked Object Option 1:  "+objectType+"");

		Server.pluginManager.triggerEvent(new GameObjectAction(c, objectType, 1));

		c.getPA().resetVariables();
		c.clickObjectType = 0;
		c.facePosition(obX, obY);
		c.boneOnAltar = false;
		Tree tree = Tree.forObject(objectType);

		RaidObjects.clickObject1(c, objectType, obX, obY);


		if (CastleWarsLobby.isInCw(c) || CastleWarsLobby.isInCwWait(c)) {
			if (CastleWarsObjects.execute(c, objectType, obX, obY)) {
				return;
			}
		}

		if (tree != null) {
			Woodcutting.getInstance().chop(c, objectType, obX, obY);
			return;
		}

		if (c.treasureGames != null && TreasureHandler.isGameObject(c, object)) {
			if (!TreasureHandler.objectActivated(c, object)) {
				TreasureHandler.handleRandomItem(c);
				TreasureHandler.addObjectToPlayer(c, object);
			}
			return;
		}

		if (Boundary.isIn(c, Boundary.AFK_ZONE) && Afk.handleAFKObjectCheck(c, object)) {
			Afk.Start(c, new Location3D(obX, obY, c.heightLevel), objectType);
			return;
		}

		if (objectType == 41214) {
			c.start(new DialogueBuilder(c).option("Would you like to buy seeds? (25k MadPoint per seed)",
					new DialogueOption("Yes", p -> {
						p.getPA().closeAllWindows();
						p.getPA().sendEnterAmount("How many seeds would you like to buy? (25k per seed)", (pl, i) -> {
							if (pl.foundryPoints < (25000L * i)) {
								pl.getPA().closeAllWindows();
								pl.sendErrorMessage("You don't have enough point's for that many seeds!");
								return;
							}
							pl.getPA().closeAllWindows();

							pl.foundryPoints -= (25000L * i);
							pl.getItems().addItemUnderAnyCircumstance(299, i);
							pl.sendErrorMessage("You have purchased " + i + " mithril seeds for " + (25000*i) + "!");
						});
					}), new DialogueOption("Nope", p -> p.getPA().closeAllWindows())));
			return;
		}

		if (c.getBlastFurnace().handleObjects(c, objectType)) {
			return;
		}
		if (c.getBlastFurnace().getBarDispenser().handleObject(c, objectType)) {
			return;
		}

		if (PyramidPlunder.handleObjects(c, objectType)) {
			return;
		}

		if (object.getObjectId() == 11663) {
			c.rangingGuild.shootArrow(object);
			return;
		}

		if (object.getObjectId() == 31984) {
			c.sendMessage("Feed me bones to get bonus XP");
			return;
		}

		if (object.getObjectId() == 11726) {
			if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
				int pX = c.getX();
				int pY = c.getY();
				int yOffset = pY > obY ? -1 : 1;
				if (obX == 3190 && obY == 3957) {
					c.moveTo(c.getPosition().translate(0, yOffset));
				}
			} else {
				c.sendMessage("You're not a wildyman you cannot access this area!");
			}
			return;
		}

		if (Doors.getSingleton().handleDoor(object.getObjectId(), obX, obY, object.getHeight())) {
			return;
		}

		if (DoubleDoors.getSingleton().handleDoor(object.getObjectId(), obX, obY, object.getHeight())) {
			return;
		}

		if(EventManager.lastStartedEvent != null) {
			if(EventManager.lastStartedEvent.getEventType() == EventType.DROP_PARTY) {
				if(EventManager.lastStartedEvent.eventStage == EventStage.STARTED) {
					if (EventType.DROP_PARTY.getEvent().handleObject(c, object)) {
						return;
					}
				}
			}
		}

		if (object.getObjectId() == 10721) {
			long milliseconds = (long) c.playTime * 600;
			long days = TimeUnit.MILLISECONDS.toDays(milliseconds);

/*			if (days < 1) {
				c.sendMessage("@red@ You need to be at least 1 day old to stake.");
				c.sendMessage("@red@ This is to prevent our new players from getting cleaned.");
				c.sendMessage("@red@ Please enjoy all other aspects of the game though. Thanks.");
				return;
			}*/
			List<String> lines = Lists.newArrayList();
			lines.add("Welcome to Gambling on Kyros.");
			lines.add("We would like you warn you that when you start gambling,");
			lines.add("THIS IS AT YOUR OWN RISK!");
			lines.add("Our staff here at Kyros are not responsible,");
			lines.add("for you losing any items.");
			lines.add("Please be responsible when gambling,");
			lines.add("as we want you to have fun and enjoy your time here.");
			c.getPA().openQuestInterface("FlowerPoker Gambling", lines.stream().limit(149).collect(Collectors.toList()));
			if (object.getY() > c.getPosition().getY()) {
				PathFinder.getPathFinder().findRoute(c, 3363, 3298, false, 1, 1, true);
				c.facePosition(3363, 3299);
			} else {
				PathFinder.getPathFinder().findRoute(c, 3363, 3296, false, 1, 1, true);
				c.facePosition(3363, 3295);
			}
			return;
		}

		if (object.getObjectId() == 26461) {
			if (c.getX() >= 2851) {
				c.moveTo(new Position(2850, c.getY(), c.getHeight()));
			} else {
				c.moveTo(new Position(2851, c.getY(), c.getHeight()));
			}
		}

		if (object.getObjectId() == 31611) { //Super Dz Ladder
			c.moveTo(new Position(3050, 2785, 1));
		}

		if (object.getObjectId() == 4965) {
			c.moveTo(new Position(3050, 2785, 0));
		}

		if (object.getObjectId() == 22274) { //Island ladders up
			c.moveTo(new Position(3803, 2873, 1));
		}
		if (object.getObjectId() == 22275) { //Island ladders down
			c.moveTo(new Position(3803, 2873, 0));
		}
		if (object.getObjectId() == 22247 || object.getObjectId() == 22250 || object.getObjectId() == 22248) { //Island stairs up
			c.moveTo(new Position(3796, 2873, 2));
		}
		if (object.getObjectId() == 22253 || object.getObjectId() == 22252 || object.getObjectId() == 22254) { //Island stairs down
			c.moveTo(new Position(3799, 2873, 1));
		}

		if (object.getObjectId() == 4126) {
			if (c.getItems().getInventoryCount(2400) >= 1) {
				for (int i = 0; i < 1; i++) {
					new ArbograveChest().roll(c);
				}
			}
			return;
		}

		if (object.getObjectId() == 47461) {
			if (c.getItems().getInventoryCount(28421) >= 1) {
				for (int i = 0; i < 1; i++) {
					new DamnedChest().roll(c);
				}
			}
			return;
		}

		if (object.getObjectId() == 4130) {
			if (c.getItems().getInventoryCount(28416) >= 1) {
				for (int i = 0; i < 1; i++) {
					new ShadowCrusadeChest().roll(c);
				}
			}
			return;
		}

		if (WGPowerup.Claim(c, object)) {
			return;
		}

		if (WGMedPack.Claim(c, object)) {
			return;
		}

		if (object.getObjectId() == 39549) {
			c.getPA().sendScreenFade("", -1, 4);
			c.moveTo(new Position(3080, 3483, 0));
			return;
		}

		if (object.getObjectId() == 38426) {
			c.getPA().sendScreenFade("", -1, 4);
			c.moveTo(new Position(3171, 5726, 0));
			return;
		}
		if (object.getObjectId() == 29322) {
			if (c.getY() < obY) {
				if (c.playerLevel[Skill.FIREMAKING.getId()] < 50) {
					c.sendMessage("You require 50 firemaking to enter!");
					return;
				}
				c.getPA().movePlayer(1630, 3971);
			} else {
				Wintertodt.removeGameItems(c);
				c.getPA().movePlayer(1630, 3962);
			}
			return;
		}
		if (WintertodtActions.handleObjects(object, c, 1))
			return;

		if (OutlastLeaderboard.handleInteraction(c, objectType, 1))
			return;



		if (c.getGnomeAgility().gnomeCourse(c, objectType)) {
			return;
		}
		if (c.getWildernessAgility().wildernessCourse(c, objectType)) {
			return;
		}
		if (c.getBarbarianAgility().barbarianCourse(c, objectType)) {
			return;
		}
		if (c.getBarbarianAgility().barbarianCourse(c, objectType)) {
			return;
		}
		if (c.getAgilityShortcuts().agilityShortcuts(c, objectType)) {
			return;
		}

		if (RooftopSeers.execute(c, objectType)) {
			return;
		}
		if (c.getRooftopFalador().execute(c, objectType)) {
			return;
		}
		if (RooftopVarrock.execute(c, objectType)) {
			return;
		}
		if (RooftopArdougne.execute(c, objectType)) {
			return;
		}
		if (c.getRoofTopDraynor().execute(c, objectType)) {
			return;
		}
		if (c.getRooftopAlkharid().execute(c, objectType)) {
			return;
		}


		if (c.getRooftopPollnivneach().execute(c, objectType)) {
			return;
		}
		if (c.getRooftopRellekka().execute(c, objectType)) {
			return;
		}
		if (c.getLighthouse().execute(c, objectType)) {
			return;
		}
		ObjectDef def = ObjectDef.getObjectDef(objectType);

		if ((def != null ? def.name : null) != null && def.name.toLowerCase().contains("bank") && !Boundary.isIn(c, Boundary.OURIANA_ALTAR)) {
			c.getPA().c.itemAssistant.openUpBank();
			c.inBank = true;
			return;
		}
		final int[] HUNTER_OBJECTS = { 9373, 9377, 9379, 9375, 9348, 9380, 9385, 9344, 9345, 9383, 721 };
		if (IntStream.of(HUNTER_OBJECTS).anyMatch(id -> objectType == id)) {
			if (Hunter.pickup(c, object)) {
				return;
			}
			if (Hunter.claim(c, object)) {
				return;
			}
		}

		if (objectType == 46323) {
			if (AfkBoss.IPAddress.contains(c.getIpAddress()) || AfkBoss.MACAddress.contains(c.getMacAddress())) {
				c.sendErrorMessage("You've already completed this week trail of treasure!");
				return;
			}
			if (c.DirtSack == 0) {
				c.DirtSack = 1;
				c.sendErrorMessage("You've found a secret!");
			}
			return;
		}


		if (objectType == 354) {
			if (c.DirtSack == 1) {
				c.DirtSack = 2;
				c.sendErrorMessage("You've found a secret!");
			}
			return;
		}

		if (objectType == 12548) {
			if (AfkBoss.IPAddress.contains(c.getIpAddress()) || AfkBoss.MACAddress.contains(c.getMacAddress())) {
				c.sendErrorMessage("You've already completed this week trail of treasure!");
				return;
			}
			if (c.DirtSack == 2) {
				c.DirtSack = 3;
				AfkBoss.IPAddress.add(c.getIpAddress());
				AfkBoss.MACAddress.add(c.getMacAddress());
				c.getItems().addItemUnderAnyCircumstance(33359, 5);
				c.getItems().addItemUnderAnyCircumstance(33358, 5);
				c.getItems().addItemUnderAnyCircumstance(33391, 5);
				Discord.writeXmasMessage("[Trail Of Treasure] " + c.getDisplayName() + " has just found the weekly treasure!");
				c.sendErrorMessage("You've found some treasure!");
				PlayerHandler.executeGlobalMessage("[@red@Trail Of Treasure@bla@] @red@" + c.getDisplayName()+ " @bla@ has completed this weeks Trail Of Treasure, see info @ ::yt!");

			}
			return;
		}
		//Wrath Runes
		if (objectType == 34759 && c.objectX == 2335 && c.objectY == 4825) {
			c.getPA().startTeleport(3039, 4835, 0, "modern", false);
			return;
		}
		if (objectType == 34759 && c.objectX == 3040 && c.objectY == 4845) {
			c.getPA().startTeleport(2335, 4826, 0, "modern", false);
			return;
		}
		c.getMining().mine(objectType, new Location3D(obX, obY, c.heightLevel));
		Obelisks.get().activate(c, objectType);
		Runecrafting.execute(c, objectType);

		/*		DoorDefinition door = DoorDefinition.forCoordinate(c.objectX, c.objectY, c.getHeight());
		 */
		if (c.getRaidsInstance() != null && c.getRaidsInstance().handleObjectClick(c,objectType, obX, obY)) {
			c.objectDistance = 3;
			return;
		}


		Location3D location = new Location3D(obX, obY, c.heightLevel);
		switch (objectType) {

			case 35965:
				TumekensInstance.JoinInstance(c);
				break;

			case 4387:
				CastleWarsLobby.addToWaitRoom(c, 1);
				break;

			case 4388:
				CastleWarsLobby.addToWaitRoom(c, 2);
				break;

			case 4408:
				CastleWarsLobby.addToWaitRoom(c, Misc.random(1, 2));
				break;

			case 49138:
				if (c.getInstance() != null) {
					c.getInstance().dispose();
					c.moveTo(new Position(3039, 6432, 0));
				} else {
					DukeInstance instance = new DukeInstance();
					instance.enter(c);
				}
				break;
			case 47560:
				if (!c.getItems().hasItemOnOrInventory(233)) {
					c.getInventory().addOrDrop(new ImmutableItem(233, 1));//Pestle N mortar
				}
				break;
			case 47561:
				if (!c.getItems().hasItemOnOrInventory(1267)) {
					c.getInventory().addOrDrop(new ImmutableItem(1267, 1));//Pickaxe
				}
				break;
			case 47536:
				if (c.getInstance() != null) {
					DukeInstance instance = (DukeInstance) c.getInstance();
					if (instance != null) {
						instance.placeIngredientsInVats(c, object);
					}
				}
				break;
			case 42594:
				AgilityHandler.delayFade(c, "CRAWL", 3679, 9797, 0, "You crawl into the cave.",
						"and end up in a spiders nest.", 1);
				break;
			case 42595:
				AgilityHandler.delayFade(c, "CRAWL", 3656, 3407, 0, "You crawl out of the cave.",
						"and end up in a dark spooky forest.", 1);
				break;
			case 54161:
				AraxxorInstance.joinInstance(c);
				break;
			case 54274:
				AgilityHandler.delayFade(c, "CRAWL", 3659, 9816, 0, "You crawl out of the cave.",
						"and end up in another cave.", 1);
				break;
			case 47539:
				if (c.getInstance() != null) {
					DukeInstance instance = (DukeInstance) c.getInstance();
					if (instance != null) {
						instance.collectPotion(c, object);
					}
				}
				break;
			case 47524:
				c.startAnimation(2282);
				c.getInventory().addOrDrop(new ImmutableItem(28341, 1));
				break;
			case 47528:
				c.startAnimation(2282);
				c.getInventory().addOrDrop(new ImmutableItem(28345, 1));
				break;
			case 47336:
				if (object.getObjectId() == 47336) {
					if (AfkBoss.hasVoted(c)) {
						if (c.getY() < 5516) {
							c.getPA().walkTo3(object.getX() - c.getX(), 2);
						} else {
							c.getPA().walkTo3(object.getX() - c.getX(), -2);
						}
					} else {
						c.sendMessage("@red@You need to vote in order to access the AFK Boss!");
					}
				}
				break;
			case 48234:
				if (Boundary.isIn(object, DonorVault.area) && Boundary.isIn(c, DonorVault.area)) {
					if (c.DonorVaultObjects.size() >= 20) {
						c.moveTo(new Position(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0));
						c.sendMessage("@red@You have completed your personal vault! Thank you for supporting Kyros!");
						c.DonorVaultObjects.clear();
						return;
					}

					if (c.DonorVaultObjects.contains(object.getPosition())) {
						c.sendMessage("This object has already been looted.");
						return;
					}

					c.DonorVaultObjects.add(object.getPosition());
					c.sendMessage("[@red@LOOOT@bla@] @pur@You gather some loot from one of your vault chests!");
					int rng = Misc.random(0, 100);
					GameItem reward;
					if (rng > 75) {
						reward = DonorVault.randomRareChestRewards();
					} else {
						reward = DonorVault.randomChestRewards();
					}

					c.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());

					c.sendMessage("@red@You found " + reward.getDef().getName() + " x" + reward.getAmount() + "!");
				}
				break;
			case 2677:
				if (Boundary.isIn(object, NomadVault.area) && Boundary.isIn(c, NomadVault.area)) {
					if (c.NomadVaultObjects.size() >= 20) {
						c.moveTo(new Position(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0));
						c.sendErrorMessage("You have completed the Nomad Vault!");
						c.NomadVaultObjects.clear();
						return;
					}

					if (c.NomadVaultObjects.contains(object.getPosition())) {
						c.sendMessage("This object has already been looted.");
						return;
					}

					c.NomadVaultObjects.add(object.getPosition());
					c.sendMessage("[@red@LOOOT@bla@] @pur@You gather some loot from one of your vault chests!");
					int rng = Misc.trueRand(1000);
					int amount = 1;
					int itemReward = 696;
					if (rng > 998) {
						itemReward = 21126;
					} else {
						amount = Misc.random(1, 50);
					}

					c.getItems().addItemUnderAnyCircumstance(itemReward, amount);
					c.sendMessage("@red@You found " + ItemDef.forId(itemReward).getName() + " x" + amount + "!");
					if (itemReward == 21126) {
						PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" +
								c.getDisplayName() + "</col> has just obtain Nomad's Ring Of Pursuit! 5% Chance to double nomad! (dissolves for 250m)");
					}
				}
				break;
			case 1533:
				c.getPA().movePlayer(3364, 2999, 0);
				break;

			case 8689:
				if (c.getItems().hasItemOnOrInventory(Items.BUCKET)) {
					c.getItems().deleteItem2(Items.BUCKET, 1);
					c.getItems().addItemUnderAnyCircumstance(Items.BUCKET_OF_MILK, 1);
				} else {
					c.sendMessage("You don't have a bucket!");
				}
				break;
			case GroupIronmanBank.OBJECT_ID:
				Optional<GroupIronmanGroup> group = GroupIronmanRepository.getGroupForOnline(c);
				group.ifPresentOrElse(it -> it.getBank().open(c),
						() -> c.sendMessage("This chest is only for group ironmen!"));
				break;
			case 29064:
				LeaderboardInterface.openInterface(c);
				break;
			case 16664:
				if (object.getX() == 3058 && object.getY() == 3376) {
					c.getPA().movePlayer(3058,9777, 0);
				} else if (object.getX() == 3044 && object.getY() == 10324) {
					c.getPA().movePlayer(3045, 10323, 0);
				}
				break;
			case 23969:
				c.getPA().movePlayer(3061, 3376, 0);
				break;
			case 16665:
				c.getPA().movePlayer(3044, 3927, 0);
				break;
			case 42967:
				if (c.absX == 2908 && c.absY == 5202 || c.absX == 2908 && c.absY == 5203 || c.absX == 2908 && c.absY == 5204) {
					if (!c.NexUnlocked) {
						if (!c.getItems().playerHasItem(26_356)) {
							c.sendMessage("You need a Frozen Key to unlock nex!");
						} else {
							c.sendMessage("Your key fits the gate and you may now enter.");
							c.getItems().deleteItem(26_356, 1);
							c.NexUnlocked = true;
						}
						return;
					}
					c.getPA().movePlayer(2910, 5202, 0);
				}
				if (c.absX == 2910 && c.absY == 5202 || c.absX == 2910 && c.absY == 5203 || c.absX == 2910 && c.absY == 5204) {
					c.getPA().movePlayer(2908, 5203, 0);
				}
				break;
			case 28686:
				c.objectDistance = 3;
				AgilityHandler.delayEmote(c, "CRAWL", 3808, 9744, 1, 2);
				break;
			case 34514:
				c.objectDistance = 3;
				AgilityHandler.delayEmote(c, "CRAWL", 1311, 3806, 0, 2);
				break;


			case 19043:
				if (obX == 3046 && obY == 10327) {
					c.objectDistance = 3;
					AgilityHandler.delayEmote(c, "CRAWL", 3048, 10336, 0, 2);
				} else {
					c.objectDistance = 3;
					AgilityHandler.delayEmote(c, "CRAWL", 3046, 10326, 0, 2);
				}
				break;

			case 12047:
			case 12045:
				if (obX == 1943 && obY == 5358 && c.getX() >= 1943 || obX == 1943 && obY == 5359 && c.getX() >= 1943) {
					if (c.getDonorBossKC() < DonorBoss.getDonorKC(c)) {
						c.getPA().movePlayer(1942,5359);
					} else {
						c.sendMessage("You've already killed the Donor Boss Enough times today!");
					}
				} else if (c.objectX == 1943) {
					c.getPA().movePlayer(1944,5359);
				} else if (c.getX() < obX && obY == 5328 || c.getX() < obX && obY == 5329) {
					if (c.getItems().hasItemOnOrInventory(20608)) {
						c.getItems().deleteItem2(20608,1);
						BloodyMinigame bm = new BloodyMinigame(c);
						bm.enter(c, bm);
					} else {
						c.sendMessage("You need a Bloodier Key to enter the minigame!");
					}
				} else if (c.getX() > obX) {
					BloodyMinigame bm = (BloodyMinigame) c.getInstance();
					if (bm != null) {
						bm.leaveGame(c);
					} else {
						c.getPA().spellTeleport(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0, false);
					}
				}
				break;
			case 34359:
				c.objectDistance = 3;
				AgilityHandler.delayEmote(c, "CRAWL", 1312, 10188, 0, 2);
				break;
			case 4874:
			case 11730:
				c.getThieving().doStealStall(Stall.Crafting, location);
				c.objectDistance = 1;
				break;
			case 8929:
				if (c.getSlayer().getTask().isEmpty()) {
					c.sendMessage("You need a slayer task before you can use this area!");
					return;
				}
				DonoSlayerInstances instanced = new DonoSlayerInstances(c, DonoSlayerInstances.boundary);
				DonoSlayerInstances.enter(c, instanced);
				//c.getDH().sendDialogues(792, 1158);
				break;
			case 21306:
				c.getPA().movePlayer(2317, 3824, 0);
				break;
			case 21307:
				c.getPA().movePlayer(2317, 3831, 0);
				break;
			case 21308:
				c.getPA().movePlayer(2343, 3828, 0);
				break;
			case 3831:
				c.getPA().movePlayer(2899, 4449, 4);
				break;

			case 8880:
				if (c.getItems().freeSlots() < 3) {
					c.sendMessage("You need at least three free slots for these tools.");
				} else {
					c.getItems().addItem(1755, 1);
					c.getItems().addItem(1265, 1);
					c.getItems().addItem(1351, 1);
				}
				break;

			case 7674:
				if (c.getItems().freeSlots() < 1) {
					c.sendMessage("You need at least one free slot to pick these berries.");
				} else {
					c.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.PICK_POSION_BERRY);
					c.getItems().addItem(6018, 1);
				}
				break;
			case 21309:
				c.getPA().movePlayer(2343, 3821, 0);
				break;
			case 14843:
				c.getRooftopCanafis().execute(c, objectType);
				break;
			case 14845:
			case 14848:
			case 14846:
			case 14894:
			case 14847:
			case 14897:
			case 14844:
				c.getRooftopCanafis().execute(c, objectType);
				break;
			case 23555:
			case 23554:
				c.getWildernessAgility().wildernessCourse(c, objectType);
				break;
			case 10060:
			case 10061:
				c.getPA().c.itemAssistant.openUpBank();
				break;
			case 29333:
				if (c.getMode().isIronmanType()) {
					c.sendMessage("@red@You are not permitted to make use of this.");
					return;
				}
				c.getTradePost().openMyOffers();
				break;
			case 20391:
				c.getPA().movePlayer(3284, 2808, 0);
				break;
			case 15477:
				c.sendMessage("The Construction skill is coming Soon.");
				break;
			case 33320:
				c.getPA().startTeleport(2078, 4898, 0, "foundry", false);
				break;
			case 29778:
				if  (Boundary.isIn(c, Boundary.RAIDS_LOBBY)) {
					LobbyManager.get(LobbyType.CHAMBERS_OF_XERIC)
							.ifPresent(lobby -> lobby.attemptLeave(c));
					c.getPA().movePlayer(1234, 3567);
					c.setRaidsInstance(null);
					return;
				}
				break;
			case 31623: //making forocious gloves
				if (c.getItems().playerHasItem(995, 15_000_000) && c.getItems().playerHasItem(22983) && c.getItems().playerHasItem(2347)) {
					c.startAnimation(898);
					c.getItems().deleteItem(22983, 1); //leather
					c.getItems().deleteItem(995, 15_000_000); //coins
					c.getItems().addItem(22981, 1); //ads forocious gloves
					c.sendMessage("@red@You have succesfully created forocious gloves.");
					return;
				}
				c.sendMessage("@red@You need a hammer, Hydra Leather, 15 million coins to do this.");
				break;
			/*case 30107:
				if (c.getItems().freeSlots() < 3) {
					c.getDH().sendStatement("@red@You need at least 3 free slots for safety");
					return;
				}
				if (c.getItems().playerHasItem(Raids.COMMON_KEY, 1)) {
					new RaidsChestCommon().roll(c);
					return;
				}
				if (c.getItems().playerHasItem(Raids.RARE_KEY, 1)) {
					new RaidsChestRare().roll(c);
					return;
				}
				if (c.getItems().playerHasItem(3468, 1)) {
					if (Misc.random(100) < 25) {
						c.getItems().deleteItem2(3468,1);
						c.getItems().addItem(Raids.RARE_KEY, 1);
						new RaidsChestPlus().roll(c);
					} else {
						c.getItems().deleteItem2(3468,1);
						c.getItems().addItem(Raids.COMMON_KEY, 1);
						new RaidsChestCommon().roll(c);
					}
					return;
				}
				if (c.getItems().playerHasItem(25432, 1)) {
					new RaidsChestPlus().roll(c);
					return;
				}
				c.getDH().sendStatement("@red@You need either a rare or common key.");
				break;*/
			case 32508:
				c.objectDistance = 13;
/*				if (!(System.currentTimeMillis() - c.chestDelay > 2000)) {
					c.getDH().sendStatement("Please wait before doing this again.");
					return;
				}*/

				if (c.getItems().freeSlots() < 3) {
					c.getDH().sendStatement("@red@You need at least 3 free slots for safety");
					return;
				}
				if (c.getItems().playerHasItem(23776, 1)) {
					new HunllefChest().roll(c);
					c.chestDelay = System.currentTimeMillis();
					return;
				}
				c.getDH().sendStatement("@red@You need Hunllef's key to unlock this chest.");
				break;
			case 12202:
				if (!c.getItems().playerHasItem(952)) {
					c.sendMessage("You need a spade to dig the whole.");
					return;
				}
				c.getPA().movePlayer(1761, 5186, 0);
				c.sendMessage("You digged a whole and landed underground.");
				break;

			case 3840:
				if (Boundary.isIn(c, Boundary.FALADOR_BOUNDARY)) {
					if (c.getItems().playerHasItem(1925)) {
						int amount = c.getItems().getItemAmount(1925);
						c.getItems().deleteItem2(1925, amount);
						c.getItems().addItem(6032, amount);
						c.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.COMPOST_BUCKET, true, amount);
					}

				}
				break;

			case 190:
				c.canEnterHespori = true;
				c.objectDistance = 3;
				Server.getPlayers().nonNullStream().filter(p -> Boundary.isIn(p, Boundary.HESPORI))
						.forEach(players -> {
							Player p = PlayerHandler.getPlayerByLoginName(players.getLoginName());
							if (p != null && !players.getLoginName().equalsIgnoreCase(c.getLoginName())) {
								if (p.getMacAddress().equalsIgnoreCase(c.getMacAddress())) {
									c.canEnterHespori = false;
								}
							}
						});
				if (!c.canEnterHespori) {
					c.sendMessage("You already have an active account inside Hespori.");
					c.canEnterHespori = true;
					return;
				}
				if (Boundary.isIn(c, Boundary.HESPORI_ENTRANCE)) {
/*					if  (c.playerLevel[19] < 50 || c.playerLevel[8] < 50 || c.playerLevel[14] < 50
							|| c.playerLevel[20] < 50 || c.playerLevel[12] < 50 ) {
						c.sendMessage("You need a level of 50 in Farming, Crafting, Firemaking, Runecrafting, Mining,");
						c.sendMessage("& Woodcutting to participate in this event. Use @red@::hespori @bla@to open the guide.");
						return;
					}*/
					if (HesporiSpawner.isSpawned()) {
						c.canLeaveHespori = false;
						c.sendMessage("@red@Gather tools from the crate box if you are ever missing any!");
						boolean axe = c.getItems().hasItemOnOrInventory(WeaponDataConstants.AXES);
						boolean pickaxe = c.getItems().hasItemOnOrInventory(WeaponDataConstants.PICKAXES);
						boolean chisel = c.getItems().playerHasItem(Items.CHISEL);

						if (!axe) { c.getItems().addItem(Items.BRONZE_AXE, 1); }
						if (!pickaxe) { c.getItems().addItem(Items.BRONZE_PICKAXE, 1); }
						if (!chisel) { c.getItems().addItem(Items.CHISEL, 1); }

						c.getPA().movePlayer(3067, 3499);
						return;
					} else {
						c.sendMessage("The Hespori World Event is currently not active.");
						return;
					}
				} else if (Boundary.isIn(c, Boundary.HESPORI_EXIT)) {
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
					c.getPA().movePlayer(3101, 3497);
					return;
				}
				break;
			case 1967:
			case 1968:
				if (c.absY == 3493) {
					c.getPA().movePlayer(2466, 3491, 0);
				} else if (c.absY == 3491) {
					c.getPA().movePlayer(2466, 3493, 0);
				}
				break;
			case 2884:
			case 16684:
			case 16683:
				if (c.absY == 3494 || c.absY == 3495 || c.absY == 3496) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", c.getX(), c.getY(), c.getHeight() + 1, 2);
				}
				break;
			case 16679:
				if (c.absY == 3494 || c.absY == 3495 || c.absY == 3496) {
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX(), c.getY(), c.getHeight() - 1, 2);
				}
				break;
			case 33311:
				Hespori.burnEssence(c);
				break;
			case 11727:
				if (c.getX() == 3045 && c.getY() == 3956) {
					c.getPA().movePlayer(3044, 3956,0);
				} else if (c.getX() == 3044 && c.getY() == 3956) {
					c.getPA().movePlayer(3045, 3956,0);
				}
				if (c.getX() == 3037 && c.getY() == 3956) {
					c.getPA().movePlayer(3038, 3956,0);
				} else if (c.getX() == 3038 && c.getY() == 3956) {
					c.getPA().movePlayer(3037, 3956,0);
				}
				if (c.getX() == 3041 && c.getY() == 3960) {
					c.getPA().movePlayer(3041, 3959,0);
				} else if (c.getX() == 3041 && c.getY() == 3959) {
					c.getPA().movePlayer(3041, 3960,0);
				}
				break;
/*			case 1521:
			case 1522:
			case 1524:
			case 1525:
			case 1535:
			case 1536:
			case 14751:
			case 14752:
			case 14753:
			case 14754:
			case 14749:
			case 14750:
				io.kyros.objects.DoorHandler.handleDoor(c, object);
				break;*/
			case 14735:
				c.getPA().movePlayer(c.getX(), c.getY(), 1);
				break;
			case 14737:
				c.getPA().movePlayer(c.getX(), c.getY(), c.getHeight() -1);
				break;
			case 36197:
				if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
					c.getPA().spellTeleport(3135, 3628, 0, true);
				} else {
					c.getPA().spellTeleport(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0, true);
				}
				break;
			case 34727:
				if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
					c.getPA().spellTeleport(3135, 3628, 0, true);
				} else {
					c.getPA().spellTeleport(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0, true);
				}
				break;
			case 21600:
				if (c.absY == 3802) {
					c.getPA().movePlayer(2326, 3801, 0);
				} else if (c.absY == 3801) {
					c.getPA().movePlayer(2326, 3802, 0);
				}
				break;
			case 31990:
				if (c.absY == 4054) {
					Vorkath.exit(c);
				} else if (c.absY == 4052) {
					Vorkath.enterInstance(c, 10);
				}
				break;
			case 34548:
				c.getPA().walkTo2(obX, obY - 3);
				c.facePosition(obX, obY);
				AgilityHandler.delayEmote(c, "JUMP", obX, obY, 0, 3);
				c.startAnimation(3067);
				break;


			case 14917:
				if (c.getY() == 3879) {
					c.facePosition(3092, 3880);
					AgilityHandler.delayEmote(c, "JUMP", 3091, 3883, 0, 1);
					c.startAnimation(3067);
				} else {
					c.facePosition(3092, 3880);
					AgilityHandler.delayEmote(c, "JUMP", 3093, 3879, 0, 1);
					c.startAnimation(3067);
				}
				break;
			/*
			 * Cheers, ye boi Tutus <3
			 */
			case 34553:
			case 34554:
				if (!c.getSlayer().getTask().isPresent()) {
					c.sendMessage("You must have an active Hydra task to enter this cave...");
					return;
				}
				if (!c.getSlayer().getTask().get().getPrimaryName().equals("hydra")
						&& !c.getSlayer().getTask().get().getPrimaryName().equals("alchemical hydra")) {
					c.sendMessage("You must have an active Hydra task to enter this cave...");
					return;
				} else {
					new AlchemicalHydra(c);
				}
				break;
			/*
			 * End Tutus
			 */
			case 31561:
				if (c.absY <= obY - 2) {
					if (c.playerLevel[Skill.AGILITY.getId()] < 89) {
						c.sendMessage("You need 89 Agility to do this.");
						return;
					}
					c.getPA().walkTo2(obX, obY - 2);
					c.facePosition(obX, obY);
					AgilityHandler.delayEmote(c, "JUMP", obX, obY, 0, 2);
					c.startAnimation(3067);
					AgilityHandler.delayEmote(c, "JUMP", obX, obY + 2, 0, 4);
				} else if (c.absY >= obY + 2) {
					c.getPA().walkTo2(obX, obY + 2);
					c.facePosition(obX, obY);
					AgilityHandler.delayEmote(c, "JUMP", obX, obY, 0, 2);
					c.startAnimation(3067);
					AgilityHandler.delayEmote(c, "JUMP", obX, obY - 2, 0, 4);
					//east jump west
				} else if (c.absX >= obX + 2) {
					if (c.playerLevel[Skill.AGILITY.getId()] < 65) {
						c.sendMessage("You need 65 Agility to do this.");
						return;
					}
					c.getPA().walkTo2(obX, obX + 2);
					c.facePosition(obX, obY);
					AgilityHandler.delayEmote(c, "JUMP", obX, obY, 0, 2);
					c.startAnimation(3067);
					AgilityHandler.delayEmote(c, "JUMP", obX - 2, obY, 0, 4);
					//west jump east
				} else if (c.absX <= obX - 2) {
					c.getPA().walkTo2(obX, obX - 2);
					c.facePosition(obX, obY);
					AgilityHandler.delayEmote(c, "JUMP", obX, obY, 0, 2);
					c.startAnimation(3067);
					AgilityHandler.delayEmote(c, "JUMP", obX + 2, obY, 0, 4);
				}
				break;
			case 23271:
				if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
					c.sendMessage("You cannot leave the wilderness!");
					return;
				}
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						c.getPA().closeAllWindows();
						if (c.absY == 3520) {
							WildernessDitch.wildernessDitchEnter(c);
							container.stop();
						} else if (c.absY == 3523) {
							WildernessDitch.wildernessDitchLeave(c);
							container.stop();
						}
					}

					@Override
					public void onStopped() {
					}
				}, 1);
				break;

			case 6282:
				c.sendMessage("@red@This Portal isn't Available for now!");
				break;

			case 16680:
				c.getPA().movePlayer(2884, 9798, 0);
				break;


			case 31858:
			case 29150:
				int spellBook = c.playerMagicBook == 0 ? 1 : (c.playerMagicBook == 1 ? 2 : 0);
				int interfaceId = c.playerMagicBook == 0 ? 838 : (c.playerMagicBook == 1 ? 29999 : 938);
				String type = c.playerMagicBook == 0 ? "ancient" : (c.playerMagicBook == 1 ? "lunar" : "normal");

				c.sendMessage("You switch spellbook to " + type + " magic.");
				c.setSidebarInterface(6, interfaceId);
				c.playerMagicBook = spellBook;
				c.autocasting = false;
				c.autocastId = -1;
				c.getPA().resetAutocast();
				break;
			case 29241:
				if (c.amDonated == 0
						&& !c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
					c.sendMessage("@red@You need to be a donator to use this feature.");
					return;
				}

				if (c.specRestore > 0) {
					int seconds = ((int) Math.floor(c.specRestore * 0.6));
					c.sendMessage("You have to wait another " + seconds + " seconds to use this altar.");
					return;
				}

				c.startAnimation(645);
				c.specRestore = 120;
				if (c.getHealth().getStatus() == HealthStatus.POISON || c.getHealth().getStatus() == HealthStatus.VENOM) {
					System.out.println("All health status has been restored.");
				}
				c.healEverything();
				c.getPA().sendSound(169);
				c.sendMessage("You feel rejuvinated.");
				break;

			case 6150:
				if (c.getItems().playerHasItem(barType[0])) {
					c.getSmithingInt().showSmithInterface(barType[0]);
				} else if (c.getItems().playerHasItem(barType[1])) {
					c.getSmithingInt().showSmithInterface(barType[1]);
				} else if (c.getItems().playerHasItem(barType[2])) {
					c.getSmithingInt().showSmithInterface(barType[2]);
				} else if (c.getItems().playerHasItem(barType[3])) {
					c.getSmithingInt().showSmithInterface(barType[3]);
				} else if (c.getItems().playerHasItem(barType[4])) {
					c.getSmithingInt().showSmithInterface(barType[4]);
				} else if (c.getItems().playerHasItem(barType[5])) {
					c.getSmithingInt().showSmithInterface(barType[5]);
				} else {
					c.sendMessage("You don't have any bars.");
				}
				break;
			case 11846:
				if (c.combatLevel >= 100) {
					if (c.getY() > 5175) {
						Highpkarena.addPlayer(c);
					} else {
						Highpkarena.removePlayer(c, false);
					}
				} else if (c.combatLevel >= 80) {
					if (c.getY() > 5175) {
						Lowpkarena.addPlayer(c);
					} else {
						Lowpkarena.removePlayer(c, false);
					}
				} else {
					c.sendMessage("You must be at least level 80 to compete in events.");
				}
				break;
			case 2996:
				new VoteChest().roll(c);
				break;
			case 34660:
			case 34662:
				if (c.getItems().playerHasItem(23083, 1)) {
					c.objectDistance = 3;
					new KonarChest().roll(c);
					return;
				} else {
					c.getDH().sendStatement("@red@You need a Konar slayer key to open this.");				}
				break;
			case 34544: //Karuulm Rocks/Stone Bars (Intro)
				if (c.absX == 1320 && c.absY == 10205 || c.absX == 1320 && c.absY == 10206) {
					AgilityHandler.delayEmote(c, "JUMP", 1322, c.absY, 0, 2);
				} else if (c.absX == 1322 && c.absY == 10205 || c.absX == 1322 && c.absY == 10206) {
					AgilityHandler.delayEmote(c, "JUMP", 1320, c.absY, 0, 2);

				} if (c.absX == 1311 && c.absY == 10214 || c.absX == 1312 && c.absY == 10214) {
				AgilityHandler.delayEmote(c, "JUMP", 1311, 10216, 0, 2);
			} else if (c.absX == 1311 && c.absY == 10216 || c.absX == 1312 && c.absY == 10216) {
				AgilityHandler.delayEmote(c, "JUMP", 1312, 10214, 0, 2);

			} if (c.absX == 1303 && c.absY == 10206 || c.absX == 1303 && c.absY == 10205) {
				AgilityHandler.delayEmote(c, "JUMP", 1301, 10205, 0, 2);
			} else if (c.absX == 1301 && c.absY == 10206 || c.absX == 1301 && c.absY == 10205) {
				AgilityHandler.delayEmote(c, "JUMP", 1303, 10206, 0, 2);
			}
				break;
			case 34530:
				c.getPA().movePlayer(1334, 10205, 1);
				break;
			case 34531:
				c.getPA().movePlayer(1329, 10206, 0);
				break;
			case 12768:
				c.objectDistance = 3;
				c.sendMessage("@blu@Use @red@::mbox @blu@to see possible rewards!");
				if (c.getItems().freeSlots() < 3) {
					c.getDH().sendStatement("@red@You need at least 3 free slot to open this.");
					return;
				}
				if (c.getItems().playerHasItem(Hespori.KEY, 1)) {
					new HesporiChest().roll(c);
					c.getEventCalendar().progress(EventChallenge.OPEN_X_HESPORI_CHESTS);
					return;
				}
				c.getDH().sendStatement("@red@You need a Hespori key to open this.");
				break;
			case 11845:
				if (c.combatLevel >= 100) {
					if (c.getY() < 5169) {
						Highpkarena.removePlayer(c, false);
					}
				} else if (c.combatLevel >= 80) {
					if (c.getY() < 5169) {
						Lowpkarena.removePlayer(c, false);
					}
				} else {
					c.sendMessage("You must be at least level 80 to compete in events.");
				}

				break;

			case 10068:
				if (c.getZulrahEvent().isStarting()) {
					c.sendMessage("Your Zulrah instance is about to start.");
				} else if (c.getZulrahEvent().isActive()) {
					c.getDH().sendStatement("It seems that a zulrah instance for you is already created.", "If you think this is wrong then please re-log.");
					c.nextChat = -1;
				} else {
					c.getZulrahEvent().initialize();
				}
				break;
			case 12941:
				PlayerAssistant.refreshSpecialAndHealth(c);
				break;
			case 40387:
				c.getPA().movePlayer(3126, 3833);
				break;
			case 31556:
				c.getPA().movePlayer(3246, 10215);
				break;
			case 43868:
				c.getPA().movePlayer(3126, 3833);
				break;
			case 31555:
				c.getPA().movePlayer(3217, 10058, 0);
				break;
			case 31558://lower stairs
				c.getPA().movePlayer(3075, 3653, 0);
				break;
			case 31624:

				for (int skill = 0; skill < c.playerLevel.length; skill++) {
					if (skill == 3)
						continue;
					if (c.playerLevel[skill] < c.getLevelForXP(c.playerXP[skill])) {
						c.playerLevel[skill] += 8 + (c.getLevelForXP(c.playerXP[skill]));
						if (SkillcapePerks.PRAYER.isWearing(c) || SkillcapePerks.isWearingMaxCape(c))
							c.playerLevel[skill] += 5;
						if (c.playerLevel[skill] > c.getLevelForXP(c.playerXP[skill])) {
							c.playerLevel[skill] = c.getLevelForXP(c.playerXP[skill]);
						}
						if (Boundary.isIn(c, Boundary.DEMONIC_RUINS_BOUNDARY)) {
							c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.DEMONIC_RUINS);
						}
						c.getPA().refreshSkill(skill);
						c.getPA().setSkillLevel(skill, c.playerLevel[skill], c.playerXP[skill]);
					}
				}
				c.lastHealChest = System.currentTimeMillis();
				c.getPotions().doAllDivine();
				c.healEverything();
				c.getDH().sendItemStatement("Restored your HP, Prayer, Run Energy, Spec, and Divine Boosts!", 23685);
				c.nextChat =  -1;

				break;
			case 40388:
				c.moveTo(new Position(3385, 10052, 0));
				break;
			case 40389:
				c.moveTo(new Position(3259, 3663, 0));
				break;
			case 40390:
				c.moveTo(new Position(3405, 10145, 0));
				break;
			case 40391:
				c.moveTo(new Position(3294, 3749, 0));
				break;
			case 39651:
			case 23709:
				long time;
//				if (c.amDonated >= 1000) {
//					time = 30_000;b
//				} else if (c.amDonated >= 500) {
//					time = 60_000;
//				} else if (c.amDonated >= 300) {
//					time = 90_000;
//				} else if (c.amDonated >= 100) {
//					time = 120_000;
//				} else {

//				}
				time = 20_000L;
				if (System.currentTimeMillis() - c.lastHealChest < time) {
//					if (c.amDonated >= 1000) {
//						c.sendMessage("Your rank may only use this chest every 30 seconds.");
//					} else if (c.amDonated >= 500) {
//						c.sendMessage("Your rank may only use this chest every 1 minute.");
//					} else if (c.amDonated >= 300) {
//						c.sendMessage("Your rank may only use this chest every 1 minute and 30 seconds.");
//					} else if (c.amDonated >= 100) {
//						c.sendMessage("Your rank may only use this chest every 2 minutes.");
//					} else {
					c.sendMessage("You may only use this chest every 20 seconds including after login.");
					//}
					return;
				}
				for (int skill = 0; skill < c.playerLevel.length; skill++) {
					if (skill == 3)
						continue;
					if (c.playerLevel[skill] < c.getLevelForXP(c.playerXP[skill])) {
						c.playerLevel[skill] += 8 + (c.getLevelForXP(c.playerXP[skill]));
						if (SkillcapePerks.PRAYER.isWearing(c) || SkillcapePerks.isWearingMaxCape(c))
							c.playerLevel[skill] += 5;
						if (c.playerLevel[skill] > c.getLevelForXP(c.playerXP[skill])) {
							c.playerLevel[skill] = c.getLevelForXP(c.playerXP[skill]);
						}
						if (Boundary.isIn(c, Boundary.DEMONIC_RUINS_BOUNDARY)) {
							c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.DEMONIC_RUINS);
						}
						c.getPA().refreshSkill(skill);
						c.getPA().setSkillLevel(skill, c.playerLevel[skill], c.playerXP[skill]);
					}
				}
				c.lastHealChest = System.currentTimeMillis();
				c.getPA().sendSound(169);
				c.healEverything();
				c.getDH().sendItemStatement("Restored your HP, Prayer, Run Energy, and Spec", 4049);
				c.nextChat =  -1;
				break;
			case 7811:
				if (!c.getPosition().inClanWarsSafe()) {
					return;
				}
				c.getShops().openShop(116);
				break;
			case 1206:
				if (Hespori.ENOUGH_BURNED) {
					c.sendMessage("Enough essence has already been burned!");
					return;
				}
				c.facePosition(obX, obY);
/*				if (c.getLevelForXP(c.playerXP[19]) < 50) {
					c.sendMessage("You need a Farming level of 50 to pick these.");
					return;
				}*/
				if (c.getItems().freeSlots() < 1) {
					c.sendMessage("You have ran out of inventory space.");
					return;
				}
				c.startAnimation(827);
				c.getItems().addItem(9017, 1);


				c.getPA().addSkillXPMultiplied(10, 19, true);
				break;

			case 23609:
				c.getPA().movePlayer(3507, 9494, 0);
				break;


			case 4150:
				c.getPA().spellTeleport(2855, 3543, 0, false);
				break;
			case 23115:// from bobs
				c.getPA().spellTeleport(1644, 3673, 0, false);
				break;
			case 10251:
				c.getPA().spellTeleport(2525, 4776, 0, false);
				break;
			case 26756:
				break;

			case 27057:
				Overseer.handleBludgeon(c);
				break;

			case 14918:
				if (c.absY > 3808) {
					AgilityHandler.delayEmote(c, "JUMP", 3201, 3807, 0, 2);
				} else {
					AgilityHandler.delayEmote(c, "JUMP", 3201, 3810, 0, 2);
				}
				break;

			case 29728:
				if (c.absY > 3508) {
					AgilityHandler.delayEmote(c, "JUMP", 1722, 3507, 0, 2);
				} else {
					AgilityHandler.delayEmote(c, "JUMP", 1722, 3512, 0, 2);
				}
				break;

			case 28893:
				if (c.playerLevel[16] < 54) {
					c.sendMessage("You need an Agility level of 54 to pass this.");
					return;
				}
				if (c.absY > 10064) {
					AgilityHandler.delayEmote(c, "JUMP", 1610, 10062, 0, 2);
				} else {
					AgilityHandler.delayEmote(c, "JUMP", 1613, 10069, 0, 2);
				}
				break;

			case 27987: // scorpia
				if (c.absX == 1774) {
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1769, 3849, 0, 2);
				} else {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 1774, 3849, 0, 2);
				}
				break;

			case 27988: // scorpia
				if (c.absX == 1774) {
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1769, 3849, 0, 2);
				} else {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 1774, 3849, 0, 2);
				}
				break;

			case 27985:
				if (c.absY > 3872) {
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1761, 3871, 0, 2);
				} else {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 1761, 3874, 0, 2);
				}
				break;

			case 27984:
				if (c.absY > 3872) {
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1761, 3871, 0, 2);
				} else {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 1761, 3874, 0, 2);
				}
				break;

			case 29730:
				if (c.absX > 1604) {
					AgilityHandler.delayEmote(c, "JUMP", 1603, 3571, 0, 2);
				} else {
					AgilityHandler.delayEmote(c, "JUMP", 1607, 3571, 0, 2);
				}
				break;

			case 25014:
				if (Boundary.isIn(c, Boundary.PURO_PURO)) {
					c.getPA().startTeleport(2525, 2916, 0, "puropuro", false);
				} else {
					c.getPA().startTeleport(2592, 4321, 0, "puropuro", false);
				}
				break;

			case 4154:// lizexit
				c.getPA().movePlayer(1465, 3687, 0);
				break;
			case 4311:// Mining Guild Entrance
				if (c.absY == 3697) {
					c.getPA().movePlayer(2681, 3698, 0);
				} else if (c.absY == 3698) {
					c.getPA().movePlayer(2682, 3697, 0);
				}
				break;
			case 30366:// Mining Guild Entrance
				if (c.absX == 3043 && c.absY == 9730) {
					if (c.playerLevel[Player.playerMining] >= 60) {
						c.getPA().movePlayer(3043, 9729, 0);
					} else {
						c.sendMessage("You must have a mining level of 60 to enter.");
					}
				} else if (c.absX == 3043 && c.absY == 9729) {
					c.getPA().movePlayer(3043, 9730, 0);
				}
				break;

			case 30365:// Mining Guild Entrance
				if (c.absX == 3019 && c.absY == 9733) {
					if (c.playerLevel[Player.playerMining] >= 60) {
						c.getPA().movePlayer(3019, 9732, 0);
					} else {
						c.sendMessage("You must have a mining level of 60 to enter.");
					}
				} else if (c.absX == 3019 && c.absY == 9732) {
					c.getPA().movePlayer(3019, 9733, 0);
				}
				break;

			case 8356:
				c.getDH().sendDialogues(55874, 2200);
				break;

			case 31621:
				if (!c.CombatSkillingUnlocked) {
					if (!c.getItems().playerHasItem(28015)) {
						c.sendMessage("You need a T10 Emblem to unlock the bosses!");
					} else {
						c.sendMessage("You have unlocked the combat skilling bosses!");
						c.getItems().deleteItem(28015, 1);
						c.CombatSkillingUnlocked = true;
					}
					return;
				}
				c.getDH().sendDialogues(909, -1);
				break;

			case 27517:
				c.getDH().sendDialogues(910, -1);
				break;
			/*
			 * draynor manor doors
			 */
			case 134:
				c.getPA().movePlayer(3108, 3354, 0);
				break;
			case 135:
				c.getPA().movePlayer(3109, 3354, 0);
				break;
			case 21597:
			case 21598:
			case 21599:
				c.getPA().movePlayer(2415, 3827, 0);
				break;

			case 11470:
				if (c.absY == 3357) {
					c.getPA().movePlayer(3109, 3358, 0);
				} else if (c.absY == 3368) {
					c.getPA().movePlayer(3106, 3369, 0);
				} else if (c.absY == 3364) {
					c.getPA().movePlayer(3103, 3363, 0);
				}
				break;
			case 21505:
			case 21507:
				if (c.absX == 2328) {
					c.getPA().movePlayer(2329, 3804, 0);
				} else if (c.absX == 2329) {
					c.getPA().movePlayer(2328, 3804, 0);
				}
				break;
			case 36556:
				if (Boundary.isIn(c, Boundary.ONYX_ZONE)) {
					if ((!c.getSlayer().getTask().isPresent() || !c.getSlayer().getTask().get().getPrimaryName().contains("crystalline")) && !c.getItems().playerHasItem(23951)) {
						c.sendMessage("@red@You must have a crystalline task to go in this cave.");
						c.getPA().closeAllWindows();
						return;
					}
					c.getPA().movePlayer(3225, 12445, 12);
				} else {
					c.start(new CrystalCaveEntryDialogue(c));
				}
				break;
			case 36691:
				c.objectDistance = 3;
				c.getPA().movePlayer(3271, 6051, 0);
				break;
			case 36692:
				c.objectDistance = 3;
				c.getPA().movePlayer(3216, 12441, c.getPosition().getHeight());
				break;
			case 36693:
				c.objectDistance = 3;
				c.getPA().movePlayer(3222, 12441, c.getPosition().getHeight());
				break;
			case 36694:
				c.objectDistance = 3;
				c.getPA().movePlayer(3232, 12420, c.getPosition().getHeight());
				break;
			case 36695:
				c.objectDistance = 3;
				c.getPA().movePlayer(3242, 12420, c.getPosition().getHeight());
				break;
			case 1568:
			case 1569:// edge dung
				if (c.absX == 3075 || c.absX == 3076) {
					if (c.absY == 3868) {
						c.getPA().walkTo(0, -1);
					} else if (c.absY == 3867) {
						c.getPA().walkTo(0, +1);
					}
				}
				if (c.absX == 3145) {//edge dung
					c.getPA().walkTo(+1, 0);
				} else if (c.absX == 3146) {//edge dung
					c.getPA().walkTo(-1, 0);
				}
				if (c.absX == 3103) {//edge dung
					c.getPA().walkTo(+1, 0);
				} else if (c.absX == 3104) {//edge dung
					c.getPA().walkTo(-1, 0);
				}
				if (c.absX == 3022) {//wildy dung
					c.getPA().walkTo(+1, 0);
				} else if (c.absX == 3023) {//wildy dung
					c.getPA().walkTo(-1, 0);
				}
				if (c.absX == 3040) {//wildy dung
					c.getPA().walkTo(+1, 0);
				} else if (c.absX == 3041) {//wildy dung
					c.getPA().walkTo(-1, 0);
				}
				if (c.absX == 3044) {//wildy dung
					c.getPA().walkTo(+1, 0);
				} else if (c.absX == 3045) {//wildy dung
					c.getPA().walkTo(-1, 0);
				}
				if (c.absX == 3103) {//edge dung
					c.getPA().walkTo(+1, 0);
				} else if (c.absX == 3104) {//edge dung
					c.getPA().walkTo(-1, 0);
				}
				if (c.absY == 9944 || c.absY == 9943) {//edge dung
					c.getPA().movePlayer(3106, 9945, 0);
				} else if (c.absY == 9945 || c.absY == 9946) {//edge dung
					c.getPA().movePlayer(3106, 9944, 0);
				}
				break;
			case 14910:
				if (c.absY == 3288) {
					c.getPA().walkTo(0, +1);
				} else if (c.absY == 3289) {
					c.getPA().walkTo(0, -1);
				}
				break;
			case 1727:
			case 1728:
				if (c.absY == 3856 || c.absY == 3857) {
					if (c.absX == 3071) {
						c.getPA().walkTo(+1, 0);
					} else if (c.absX == 3072) {
						c.getPA().walkTo(-1, 0);
					}
				}
				if (c.absX == 3202 || c.absX == 3201) {
					if (c.absY == 3856) {
						c.getPA().walkTo(0,-1);
					} else {
						c.getPA().walkTo(0,+1);
					}
				}
				if (c.absY == 3903) {
					c.getPA().walkTo(0, +1);
				} else if (c.absY == 3904) {
					c.getPA().walkTo(0, -1);
				}
				if (c.absY == 3895) {
					c.getPA().walkTo(0, +1);
				} else if (c.absY == 3896) {
					c.getPA().walkTo(0, -1);
				}
				if (c.absY == 9917) {
					c.getPA().walkTo(0, +1);
				} else if (c.absY == 9918) {
					c.getPA().walkTo(0, -1);
				}
/*				if (c.absY == 3856) {
					c.getPA().walkTo(0, -1);
				} else if (c.absY == 3855) {
					c.getPA().walkTo(0, +1);
				}*/
				if (c.absY == 3182) {
					c.getPA().walkTo(+1, 0);
				} else if (c.absY == 3183) {
					c.getPA().walkTo(-1, 0);
				}
				if (c.absX == 3008) {
					c.getPA().walkTo(-1, 0);
				} else if (c.absX == 3007) {
					c.getPA().walkTo(+1, 0);
				}
				break;

			case 10439:
			case 7814:
				PlayerAssistant.refreshHealthWithoutPenalty(c);
				break;
			case 2670:
				if (!c.getItems().playerHasItem(1925) || !c.getItems().playerHasItem(946)) {
					c.sendMessage("You must have an empty bucket and a knife to do this.");
					return;
				}
				c.getItems().deleteItem(1925, 1);
				c.getItems().addItem(1929, 1);
				c.sendMessage("You cut the cactus and pour some water into the bucket.");
				c.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.CUT_CACTUS);
				break;
			// Carts Start
			case 7029:
				TrainCart.handleInteraction(c);
				break;
			case 28837:
				c.getDH().sendDialogues(193193, -1);
				break;
			// Carts End
			case 10321:
				c.getPA().spellTeleport(1752, 5232, 0, false);
				c.sendMessage("Welcome to the Giant Mole cave, try your luck for a granite maul.");
				break;
			case 1294:
				c.getDH().tree = "stronghold";
				c.getDH().sendDialogues(65, -1);
				break;

			case 1293:
				c.getDH().tree = "village";
				c.getDH().sendDialogues(65, -1);
				break;

			case 1295:
				c.getDH().tree = "grand_exchange";
				c.getDH().sendDialogues(65, -1);
				break;

			case 2073:
				c.getItems().addItem(1963, 1);
				c.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.PICK_BANANAS);
				break;

			case 20877:
				AgilityHandler.delayFade(c, "CRAWL", 2712, 9564, 0, "You crawl into the entrance.",
						"and you end up in a dungeon.", 3);
				break;
			case 20878:
				AgilityHandler.delayFade(c, "CRAWL", 1571, 3659, 0, "You crawl into the entrance.",
						"and you end up in a dungeon.", 3);
				break;
			case 16675:
				AgilityHandler.delayEmote(c, "CLIMB_UP", 2445, 3416, 1, 2);
				break;
			case 16677:
				AgilityHandler.delayEmote(c, "CLIMB_UP", 2445, 3416, 0, 2);
				break;

			case 6434:
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3118, 9644, 0, 2);
				break;

			case 11441:
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2856, 9570, 0, 2);
				break;

			case 18969:
				AgilityHandler.delayEmote(c, "CLIMB_UP", 2857, 3167, 0, 2);
				break;

			case 11835:
				AgilityHandler.delayFade(c, "CRAWL", 2480, 5175, 0, "You crawl into the entrance.",
						"and you end up in Tzhaar City.", 3);
				break;
			case 11836:
				AgilityHandler.delayFade(c, "CRAWL", 1212, 3540, 0, "You crawl into the entrance.",
						"and you end up back on Mt. Quidamortem.", 3);
				break;

			case 155:
			case 156:
				AgilityHandler.delayEmote(c, "BALANCE", 3096, 3359, 0, 2);
				break;
			case 160:
				AgilityHandler.delayEmote(c, 2140, 3098, 3357, 0, 2);
				break;

			case 23568:
				c.getPA().movePlayer(2704, 3205, 0);
				break;

			case 23569:
				c.getPA().movePlayer(2709, 3209, 0);
				break;

			case 17068:
				if (c.playerLevel[Player.playerAgility] < 8 || c.playerLevel[Player.playerStrength] < 19
						|| c.playerLevel[Player.playerRanged] < 37) {
					c.sendMessage(
							"You need an agility level of 8, strength level of 19 and ranged level of 37 to do this.");
					return;
				}
				AgilityHandler.delayEmote(c, "JUMP", 3253, 3180, 0, 2);
				c.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.RIVER_LUM_SHORTCUT);
				break;

			case 16465:
				if (!c.getDiaryManager().getDesertDiary().hasCompletedSome("ELITE")) {
					c.sendMessage("You must have completed all tasks in the desert diary to do this.");
					return;
				}
				if (c.playerLevel[Player.playerAgility] < 82) {
					c.sendMessage("You need an agility level of at least 82 to squeeze through here.");
					return;
				}
				c.sendMessage("You squeeze through the crevice.");
				if (c.absX == 3506 && c.absY == 9505)
					c.getPA().movePlayer(3500, 9510, 2);
				else if (c.absX == 3500 && c.absY == 9510)
					c.getPA().movePlayer(3506, 9505, 2);
				break;

			case 2147:
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3104, 9576, 0, 2);
				break;
			case 2148:
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3105, 3162, 0, 2);
				break;
			case 11668:
			case 1579:
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3097, 9868, 0, 2);
				break;


			case 47140:  //Callisto
				c.getPA().movePlayer(3358,10317,0);
				break;
			case 47122:
				c.getPA().movePlayer(3293, 3848, 0);
				break;

			case 46995:  //Vet'ion
				c.getPA().movePlayer(3295, 10193, 1);
				break;
			case 46925:
				c.getPA().movePlayer(3220,3788,0);
				break;

			case 47077:  //Venanitis
				c.getPA().movePlayer(3423,10184,2);
				break;
			case 47000:
				c.getPA().movePlayer(3320,3796,0);
				break;


			case 10042:
//				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2548, 9951, 0, 2);
				break;
			case 17385:
				if (Boundary.isIn(c, Boundary.EDGE_DUNG_LADDER)) {
					c.sendMessage("This area is currently closed.");
				} else if (Boundary.isIn(c, Boundary.EDGE_DUNG_ENTRANCE_LADDER)){
					AgilityHandler.delayEmote(c, "CLIMB_UP", 3089, 3497, 0, 2);
				}/* else if (Boundary.isIn(c, Boundary.FOE_DUNGEON)) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 3090, 3490, 0, 2);
				}*/
				break;
			case 33318:
				c.start(new FireOfDestructionDialogue(c, -1));
				break;
			case 25938:
			case 11794:
				if (Boundary.isIn(c, Boundary.EDGEVILLE_EXTENDED)) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", c.absX, c.absY, 1, 2);
				}
				break;
			case 25939:
			case 11795:
				if (Boundary.isIn(c, Boundary.EDGEVILLE_EXTENDED)) {
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.absX, c.absY, 0, 2);
				}
				break;

			case 27785:
				c.getDH().sendDialogues(70300, -1);
				break;
			case 43486:
				new AOEChest().roll(c);
				break;
			case 30266:
				if (c.usedFc == true) {
					c.getPA().movePlayer(2495, 5174, 0);
				} else if (c.getItems().playerHasItem(6570, 1)) {
					c.getItems().deleteItem(6570, 1);
					c.usedFc = true;
					c.getPA().movePlayer(2495, 5174, 0);
				} else {
					c.sendMessage("@red@You must sacrifice your firecape at least once.");
					return;
				}
				break;

			case 28894:
			case 28895:
			case 28898:
			case 28897:
			case 28896: // catacomb exits
				c.getPA().movePlayer(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0);
				c.sendMessage("You return to the statue.");
				break;
			case 882:
				c.getPA().movePlayer(2885, 5292, 2);
				c.sendMessage("Welcome to the Godwars Dungeon!.");
				break;
			case 27777:
				c.getPA().movePlayer(1781, 3412, 0);
				c.sendMessage("Welcome to the CrabClaw Isle, try your luck for a tentacle or Trident of the Seas!.");
				break;
			case 3828:
				c.getPA().movePlayer(3484, 9510, 2);
				c.sendMessage("Welcome to the Kalphite Lair, try your luck for a dragon chain or uncut onyx!.");
				break;

			case 3829:
				c.getPA().movePlayer(1845, 3809, 0);
				c.sendMessage("You find the light of day outside of the tunnel!");
				break;
			case 3832:
				c.getPA().movePlayer(3510, 9496, 2);
				break;

			case 4031:
				if (c.absY == 3117) {
					if (EquipmentSet.DESERT_ROBES.isWearing(c)) {
						c.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.PASS_GATE_ROBES);
					} else {
						c.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.PASS_GATE);
					}
					c.getPA().movePlayer(c.absX, 3115);
				} else {
					c.getPA().movePlayer(c.absX, 3117);
				}
				break;

			case 7122:
				if (c.absX == 2564 && c.absY == 3310)
					c.getPA().movePlayer(2563, 3310);
				else if (c.absX == 2563 && c.absY == 3310)
					c.getPA().movePlayer(2564, 3310);
				break;

			case 24958:
				if (c.getDiaryManager().getVarrockDiary().hasCompleted("EASY")) {
					if (c.absX == 3143 && c.absY == 3443)
						c.getPA().movePlayer(3143, 3444);
					else if (c.absX == 3143 && c.absY == 3444)
						c.getPA().movePlayer(3143, 3443);
				} else {
					c.sendMessage("You must have completed all easy tasks in the varrock diary to enter.");
					return;
				}
				break;

			case 10045:
				if (c.getDiaryManager().getVarrockDiary().hasCompleted("EASY")) {
					if (c.absX == 3143 && c.absY == 3452)
						c.getPA().movePlayer(3144, 3452);
					else if (c.absX == 3144 && c.absY == 3452)
						c.getPA().movePlayer(3143, 3452);
				} else {
					c.sendMessage("You must have completed all easy tasks in the varrock diary to enter.");
					return;
				}
				break;

			case 11780:
				if (c.getDiaryManager().getVarrockDiary().hasCompleted("HARD")) {
					if (c.absX == 3255)
						c.getPA().movePlayer(3256, c.absY);
					else
						c.getPA().movePlayer(3255, c.absY);
				} else {
					c.sendMessage("You must have completed all hard tasks in the varrock diary to enter.");
					return;
				}
				break;
			case 1805:
				if (c.getDiaryManager().getVarrockDiary().hasCompleted("EASY")) {
					c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.CHAMPIONS_GUILD);
					if (c.absY == 3362)
						c.getPA().movePlayer(c.absX, 3363);
					else
						c.getPA().movePlayer(c.absX, 3362);
				} else {
					c.sendMessage("You must have completed all easy tasks in the varrock diary to enter.");
					return;
				}
				break;

			case 538:
				c.getPA().movePlayer(2280, 10016, 0);
				break;

			case 537:
				Kraken.init(c);
				break;

			case 6462: // Ice gate
			case 6461:
				c.getPA().movePlayer(2852, 3809, 2);
				break;

			case 6456: // Ice ledge
				c.getPA().movePlayer(2855, c.absY, 1);
				break;

			case 6455: // Ice ledge (Bottom)
				if (c.absY >= 3804)
					c.getPA().movePlayer(2837, 3803, 1);
				else
					c.getPA().movePlayer(2837, 3805, 0);
				break;

			case 677:
				int z = 2;
				if (c.absX == 2974)
					c.getPA().movePlayer(2970, 4384, z);
				else
					c.getPA().movePlayer(2974, 4384, z);
				break;

			case 13641: // Teleportation Device
				c.getDH().sendDialogues(63, -1);
				break;
			case 26741:
			case 26742:
			case 26743:
			case 26744:
			case 26745:
			case 26746:
			case 26747:
			case 26748:
			case 26749:
				c.objectDistance = 13;
				c.objectXOffset = 13;
				c.objectYOffset = 13;
				ViewingOrb.clickObject(c);
				break;
			case 23104:
				if (!(c.getSlayer().getTask().isPresent())) {
					c.sendMessage("You must have an active cerberus or hellhound task to enter this cave...");
					return;
				}
				if (c.getSlayer().getTask().get().getPrimaryName().equals("hellhound") && (c.getKonarSlayerLocation() == "stronghold cave" || c.getKonarSlayerLocation() == "taverly dungeon")) {
					c.sendMessage("Your Konar task does not permit access here.");
					return;
				}
				if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && (!c.getSlayer().getTask().get().getPrimaryName().equals("hellhound") && !c.getSlayer().getTask().get().getPrimaryName().equals("cerberus")))) {
					c.sendMessage("You must have an active cerberus or hellhound task to enter this cave...");
					return;
				}

				if (System.currentTimeMillis() - c.cerbDelay > 5000) {
					if (!c.getSlayer().getTask().isPresent()) {
						c.sendMessage("You must have an active cerberus or hellhound task to enter this cave...");
						return;
					}
					if (!c.getSlayer().isCerberusRoute()) {
						c.sendMessage("You have bought Route into cerberus cave. please wait till you will be teleported.");
						Cerberus.init(c);
						c.cerbDelay = System.currentTimeMillis();
						return;
					}

					if (c.playerLevel[Skill.SLAYER.getId()] < 91) {
						c.sendMessage("You need a slayer level of 91 to enter.");
						return;
					}

					if (Server.getEventHandler().isRunning(c, "cerb")) {
						c.sendMessage("You're about to fight start the fight, please wait.");
						return;
					}

					Cerberus.init(c);

					c.cerbDelay = System.currentTimeMillis();
				} else {
					c.sendMessage("Please wait a few seconds between clicks.");
				}
				break;

			case 21772:
				if (!Boundary.isIn(c, Boundary.CERBERUS_ROOM_WEST)) {
					return;
				}
				c.getPA().movePlayer(1309, 1250, 0);
				break;

			case 28900:
				DarkAltar.handleDarkTeleportInteraction(c);
				break;
			case 28925:
				DarkAltar.handlePortalInteraction(c);
				break;

			case 23105:
				c.appendDamage(5, HitMask.HIT);
				if (c.absY == 1241) {
					c.getPA().walkTo(0, +2);
				} else {
					c.moveTo(Cerberus.EXIT);
				}
				break;

			case 31925:
				c.getPA().startLeverTeleport(3828, 3893, 0);
				break;

			case 4577: // Lighthouse door
				if (c.getY() >= 3636)
					c.getPA().movePlayer(2509, 3635, 0);
				else
					c.getPA().movePlayer(2509, 3636, 0);
				break;

			case 13642: // Lectern
				c.getDH().sendDialogues(10, -1);
				break;

			case 8930:
				c.getPA().movePlayer(1975, 4409, 3);
				break;

			case 10177: // Dagganoth kings ladder
				c.getPA().movePlayer(2900, 4449, 0);
				break;

			case 10193:
				c.getPA().movePlayer(2545, 10143, 0);
				break;

			case 10195:
				c.getPA().movePlayer(1809, 4405, 2);
				break;

			case 10196:
				c.getPA().movePlayer(1807, 4405, 3);
				break;

			case 10197:
				c.getPA().movePlayer(1823, 4404, 2);
				break;

			case 10198:
				c.getPA().movePlayer(1825, 4404, 3);
				break;

			case 10199:
				c.getPA().movePlayer(1834, 4388, 2);
				break;

			case 10200:
				c.getPA().movePlayer(1834, 4390, 3);
				break;

			case 10201:
				c.getPA().movePlayer(1811, 4394, 1);
				break;

			case 10202:
				c.getPA().movePlayer(1812, 4394, 2);
				break;

			case 10203:
				c.getPA().movePlayer(1799, 4386, 2);
				break;

			case 10204:
				c.getPA().movePlayer(1799, 4388, 1);
				break;

			case 10205:
				c.getPA().movePlayer(1796, 4382, 1);
				break;

			case 10206:
				c.getPA().movePlayer(1796, 4382, 2);
				break;

			case 10207:
				c.getPA().movePlayer(1800, 4369, 2);
				break;

			case 10208:
				c.getPA().movePlayer(1802, 4370, 1);
				break;

			case 10209:
				c.getPA().movePlayer(1827, 4362, 1);
				break;

			case 10210:
				c.getPA().movePlayer(1825, 4362, 2);
				break;

			case 10211:
				c.getPA().movePlayer(1863, 4373, 2);
				break;

			case 10212:
				c.getPA().movePlayer(1863, 4371, 1);
				break;

			case 10213:
				c.getPA().movePlayer(1864, 4389, 1);
				break;

			case 10214:
				c.getPA().movePlayer(1864, 4387, 2);
				break;

			case 10215:
				c.getPA().movePlayer(1890, 4407, 0);
				break;

			case 10216:
				c.getPA().movePlayer(1890, 4406, 1);
				break;

			case 10217:
				c.getPA().movePlayer(1957, 4373, 1);
				break;

			case 10218:
				c.getPA().movePlayer(1957, 4371, 0);
				break;

			case 10219:
				c.getPA().movePlayer(1824, 4379, 3);
				break;

			case 10220:
				c.getPA().movePlayer(1824, 4381, 2);
				break;

			case 10221:
				c.getPA().movePlayer(1838, 4375, 2);
				break;

			case 10222:
				c.getPA().movePlayer(1838, 4377, 3);
				break;

			case 10223:
				c.getPA().movePlayer(1850, 4386, 1);
				break;

			case 10224:
				c.getPA().movePlayer(1850, 4387, 2);
				break;

			case 10225:
				c.getPA().movePlayer(1932, 4378, 1);
				break;

			case 10226:
				c.getPA().movePlayer(1932, 4380, 2);
				break;

			case 10227:
				if (c.getX() == 1961 && c.getY() == 4392)
					c.getPA().movePlayer(1961, 4392, 2);
				else
					c.getPA().movePlayer(1932, 4377, 1);
				break;

			case 10228:
				c.getPA().movePlayer(1961, 4393, 3);
				break;

			case 10229:
				c.getPA().movePlayer(1912, 4367, 0);
				break;

			/**
			 * Dagannoth king entrance
			 */
			case 10230:
				if (c.getMode().isIronmanType()) {
					c.getPA().movePlayer(2899, 4449, 4);
				} else {
					c.getPA().movePlayer(2899, 4449, 0);
				}
				break;

			case 8958:
				if (c.getX() <= 2490)
					c.getPA().movePlayer(2492, 10163, 0);
				if (c.getX() >= 2491)
					c.getPA().movePlayer(2490, 10163, 0);
				break;
			case 8959:
				if (c.getX() <= 2490)
					c.getPA().movePlayer(2492, 10147, 0);
				if (c.getX() >= 2491)
					c.getPA().movePlayer(2490, 10147, 0);
				break;
			case 8960:
				if (c.getX() <= 2490)
					c.getPA().movePlayer(2492, 10131, 0);
				if (c.getX() >= 2491)
					c.getPA().movePlayer(2490, 10131, 0);
				break;
			//
			case 26724:
				if (c.playerLevel[Skill.AGILITY.getId()] < 72) {
					c.sendMessage("You need an agility level of 72 to cross over this mud slide.");
					return;
				}
				if (c.getX() == 2427 && c.getY() == 9767) {
					c.getPA().movePlayer(2427, 9762);
				} else if (c.getX() == 2427 && c.getY() == 9762) {
					c.getPA().movePlayer(2427, 9767);
				}
				break;
			case 535:
				if (obX == 3722 && obY == 5798) {
					if (c.getMode().isIronmanType()) {
						c.getPA().movePlayer(3677, 5775, 4);
					} else {
						c.getPA().movePlayer(3677, 5775, 0);
					}
				}
				break;

			case 536:
				if (obX == 3678 && obY == 5775) {
					c.getPA().movePlayer(3723, 5798);
				}
				break;

			case 26720:
				if (obX == 2427 && obY == 9747) {
					if (c.getX() == 2427 && c.getY() == 9748) {
						c.getPA().movePlayer(2427, 9746);
					} else if (c.getX() == 2427 && c.getY() == 9746) {
						c.getPA().movePlayer(2427, 9748);
					}
				} else if (obX == 2420 && obY == 9750) {
					if (c.getX() == 2420 && c.getY() == 9751) {
						c.getPA().movePlayer(2420, 9749);
					} else if (c.getX() == 2420 && c.getY() == 9749) {
						c.getPA().movePlayer(2420, 9751);
					}
				} else if (obX == 2418 && obY == 9742) {
					if (c.getX() == 2418 && c.getY() == 9741) {
						c.getPA().movePlayer(2418, 9743);
					} else if (c.getX() == 2418 && c.getY() == 9743) {
						c.getPA().movePlayer(2418, 9741);
					}
				} else if (obX == 2357 && obY == 9778) {
					if (c.getX() == 2358 && c.getY() == 9778) {
						c.getPA().movePlayer(2356, 9778);
					} else if (c.getX() == 2356 && c.getY() == 9778) {
						c.getPA().movePlayer(2358, 9778);
					}
				} else if (obX == 2388 && obY == 9740) {
					if (c.getX() == 2389 && c.getY() == 9740) {
						c.getPA().movePlayer(2387, 9740);
					} else if (c.getX() == 2387 && c.getY() == 9740) {
						c.getPA().movePlayer(2389, 9740);
					}
				} else if (obX == 2379 && obY == 9738) {
					if (c.getX() == 2380 && c.getY() == 9738) {
						c.getPA().movePlayer(2378, 9738);
					} else if (c.getX() == 2378 && c.getY() == 9738) {
						c.getPA().movePlayer(2380, 9738);
					}
				}
				break;

			case 26721:
				if (obX == 2358 && obY == 9759) {
					if (c.getX() == 2358 && c.getY() == 9758) {
						c.getPA().movePlayer(2358, 9760);
					} else if (c.getX() == 2358 && c.getY() == 9760) {
						c.getPA().movePlayer(2358, 9758);
					}
				}
				if (obX == 2380 && obY == 9750) {
					if (c.getX() == 2381 && c.getY() == 9750) {
						c.getPA().movePlayer(2379, 9750);
					} else if (c.getX() == 2379 && c.getY() == 9750) {
						c.getPA().movePlayer(2381, 9750);
					}
				}
				break;

			case 154:
				if (obX == 2356 && obY == 9783) {
					if (c.playerLevel[Skill.SLAYER.getId()] < 93) {
						c.sendMessage("You need a slayer level of 93 to enter into this crevice.");
						return;
					}
					c.getPA().movePlayer(3748, 5761, 0);
				}
				break;

			case 534:
				if (obX == 3748 && obY == 5760) {
					c.getPA().movePlayer(2356, 9782, 0);
				}
				break;
			case 9706:
				if (obX == 3104 && obY == 3956) {
					c.getPA().startLeverTeleport(3105, 3951, 0);
				}
				break;

			case 9707:
				if (obX == 3105 && obY == 3952) {
					c.getPA().startLeverTeleport(3105, 3956, 0);
				}
				break;
			case 3610:
				if (obX == 3550 && obY == 9695) {
					c.getPA().startTeleport(3565, 3308, 0, "modern", false);
				}
				break;
			case 26561:
				if (obX == 2913 && obY == 5300) {
					c.getPA().movePlayer(2914, 5300, 1);
				}
				break;
			case 26562:
				if (obX == 2920 && obY == 5274) {
					c.getPA().movePlayer(2920, 5274, 0);
				}
				break;
			case 26504:
				if (c.absX == 2909 && c.absY == 5265) {
					SaradominInstance instance = new SaradominInstance(c, Boundary.SARADOMIN_GODWARS);
					SaradominInstance.enter(c, instance);
//					c.getGodwars().enterBossRoom(God.SARADOMIN);
				}
				if (c.absX == 2907 && c.absY == 5265) {
					c.getPA().movePlayer(2909, 5265, 0);
					if (c.getInstance() != null) {
						c.getInstance().dispose();
					}
				}
				break;
			case 26518:
				if (obX == 2885 && obY == 5333) {
					c.getPA().movePlayer(2885, 5344);
				} else if (obX == 2885 && obY == 5344) {
					c.getPA().movePlayer(2885, 5333);
				}
				break;
			case 26505:
				if (c.absX == 2925 &&c.absY == 5333) {
					ZamorakInstance instance = new ZamorakInstance(c, Boundary.ZAMORAK_GODWARS);
					ZamorakInstance.enter(c, instance);
				}
				if (c.absX == 2925 &&c.absY == 5331) {
					c.getPA().movePlayer(2925, 5333, 2);
					if (c.getInstance() != null) {
						c.getInstance().dispose();
					}
				}
				break;
			case 26503:
				if (c.absX == 2862 && c.absY == 5354) {
					BandosInstance instance = new BandosInstance(c, Boundary.BANDOS_GODWARS);
					BandosInstance.enter(c, instance);
//					c.getGodwars().enterBossRoom(God.BANDOS);
				}
				if (c.absX == 2864 && c.absY == 5354) {
					c.getPA().movePlayer(2862, 5354, 2);
					c.getInstance().dispose();
				}
				break;
			case 26380:
				if (obX == 2871 && obY == 5270) {
					if (c.getY() == 5279) {
						c.getPA().movePlayer(2872, 5269);
					} else if (c.getY() == 5269) {
						c.getPA().movePlayer(2872, 5279);
					}
				}
				break;
			case 21578: // Stairs up
			case 10:
				if (!c.getRights().isOrInherits(Right.Extreme_Donator)) {
					c.sendMessage("You must be an Cell Donor to enter the top floor.");
					return;
				}
				if (c.heightLevel == 0) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 3372, 9645, 1, 2);
				} else {
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3372, 9645, 0, 2);
				}
				break;
			case 26502:
				if (c.absX == 2839 && c.absY == 5294) {
					ArmadylInstance instance = new ArmadylInstance(c, Boundary.ARMADYL_GODWARS);
					ArmadylInstance.enter(c, instance);
//					c.getGodwars().enterBossRoom(God.ARMADYL);
				}
				if (c.absX == 2839 && c.absY == 5296) {
					c.getPA().movePlayer(2839, 5294, 2);
					if (c.getInstance() != null) {
						c.getInstance().dispose();
					}
				}
				break;
			case 172:
			case 170:
				c.objectDistance = 3;
				c.objectXOffset = 3;
				c.objectYOffset = 3;
				new CrystalChest().roll(c);
				break;

			case 4873:
			case 26761:
				c.getPA().startLeverTeleport(3153, 3923, 0);
				c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.WILDERNESS_LEVER);
				break;
			case 2492:
			case 15638:
			case 7479:
				c.getPA().startTeleport(3088, 3504, 0, "modern", false);
				break;
			case 11803:
				if (c.getRights().isOrInherits(Right.Major_Donator)) {
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3577, 9927, 0, 2);
					c.sendMessage("@cr4@ Welcome to the donators only slayer cave.");
				}
				break;
			case 17387:
				if (c.getRights().isOrInherits(Right.Major_Donator)) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 2125, 4913, 0, 2);
				}
				break;
			case 25824:
				c.facePosition(obX, obY);
				c.getDH().sendDialogues(40, -1);
				break;

			case 5097:
			case 21725:
				c.getPA().movePlayer(2636, 9510, 2);
				break;
			case 5098:
			case 21726:
				c.getPA().movePlayer(2636, 9517, 0);
				break;
			case 5094:
			case 21722:
				c.getPA().movePlayer(2643, 9594, 2);
				break;
			case 5096:
			case 21724:
				c.getPA().movePlayer(2649, 9591, 0);
				break;
			case 2320:
			case 23566:
				if (c.absY == 9964 || c.absY == 9963) {
					c.getPA().movePlayer(3120, 9970, 0);
				} else if (c.absY == 9969 || c.absY == 9970) {
					c.getPA().movePlayer(3120, 9963, 0);
				}
				break;
			case 26760:
				if (c.absX == 3184 && c.absY == 3945) {
					c.getDH().sendDialogues(631, -1);
				} else if (c.absX == 3184 && c.absY == 3944) {
					c.getPA().movePlayer(3184, 3945, 0);
				}
				break;
			case 19206:
				//	if (c.absX == 1502 && c.absY == 3838) {
				//	c.getDH().sendDialogues(63100, -1);
				//	} else if (c.absX == 1502 && c.absY == 3840) {
				//		c.getPA().movePlayer(1502, 3838, 0);
				//	}
				break;
			case 9326:
				if (c.playerLevel[16] < 62) {
					c.sendMessage("You need an Agility level of 62 to pass this.");
					return;
				}
				if (c.absX < 2769) {
					c.getPA().movePlayer(2775, 10003, 0);
				} else {
					c.getPA().movePlayer(2768, 10002, 0);
				}
				break;
			case 4496:
			case 4494:
				if (c.heightLevel == 2) {
					c.getPA().movePlayer(3412, 3540, 1);
				} else if (c.heightLevel == 1) {
					c.getPA().movePlayer(3418, 3540, 0);
				}
				break;
			case 9319:
				if (c.heightLevel == 0)
					c.getPA().movePlayer(c.absX, c.absY, 1);
				else if (c.heightLevel == 1)
					c.getPA().movePlayer(c.absX, c.absY, 2);
				break;

			case 9320:
				if (c.heightLevel == 1)
					c.getPA().movePlayer(c.absX, c.absY, 0);
				else if (c.heightLevel == 2)
					c.getPA().movePlayer(c.absX, c.absY, 1);
				break;
			case 4493:
				if (c.heightLevel == 0) {
					c.getPA().movePlayer(c.absX - 5, c.absY, 1);
				} else if (c.heightLevel == 1) {
					c.getPA().movePlayer(c.absX + 5, c.absY, 2);
				}
				break;

			case 4495:
				if (c.heightLevel == 1 && c.absY > 3538 && c.absY < 3543) {
					c.getPA().movePlayer(c.absX + 5, c.absY, 2);
				} else {
					c.sendMessage("I can't reach that!");
				}
				break;
			case 2623:
				if (c.absX == 2924 && c.absY == 9803) {
					c.getPA().movePlayer(c.absX - 1, c.absY, 0);
				} else if (c.absX == 2923 && c.absY == 9803) {
					c.getPA().movePlayer(c.absX + 1, c.absY, 0);
				}
				break;
			case 15644:
			case 15641:
			case 24306:
			case 24309:
				if (c.heightLevel == 2) {
					// if(Boundary.isIn(c, WarriorsGuild.WAITING_ROOM_BOUNDARY) &&
					// c.heightLevel == 2) {
					c.getWarriorsGuild().handleDoor();
					return;
					// }
				}
				if (c.heightLevel == 0) {
					if (c.absX == 2855 || c.absX == 2854) {
						if (c.absY == 3546)
							c.getPA().movePlayer(c.absX, c.absY - 1, 0);
						else if (c.absY == 3545)
							c.getPA().movePlayer(c.absX, c.absY + 1, 0);
						c.facePosition(obX, obY);
					}
				}
				break;
			case 15653:
				if (c.absY == 3546) {
					if (c.absX == 2877)
						c.getPA().movePlayer(c.absX - 1, c.absY, 0);
					else if (c.absX == 2876)
						c.getPA().movePlayer(c.absX + 1, c.absY, 0);
					c.facePosition(obX, obY);
				}
				break;

			case 18987: // Kbd ladder
				c.getPA().movePlayer(3069, 10255, 0);
				break;
			case 1817:
				c.getPA().startLeverTeleport(3067, 10253, 0);
				break;

			case 18988:
				c.getPA().movePlayer(3017, 3850, 0);
				break;

			case 24303:
				c.getPA().movePlayer(2840, 3539, 0);
				break;

			case 16671:
				int distanceToPoint = c.distanceToPoint(2840, 3539);
				if (distanceToPoint < 5) {
					c.getPA().movePlayer(2840, 3539, 2);
				}
				break;

			// Jewelery oven
			case 2643:
			case 14888:
				if (!c.getItems().playerHasItem(Items.RING_MOULD) && !c.getItems().playerHasItem(Items.AMULET_MOULD)
						&& !c.getItems().playerHasItem(Items.NECKLACE_MOULD)) {
					if (c.getItems().playerHasItem(Items.BRACELET_MOULD)) {
						BraceletMaking.craftBraceletDialogue(c);
					}
				} else {
					JewelryMaking.mouldInterface(c);
				}
				break;

			case 878:
				c.getDH().sendDialogues(613, -1);
				break;
			case 1733:
				if (c.absY > 3920 && c.getPosition().inWild())
					c.getPA().movePlayer(3045, 10323, 0);
				break;
			case 1734:
				if (c.absY > 9000 && c.getPosition().inWild())
					c.getPA().movePlayer(3044, 3927, 0);
				break;
			case 2466:
				if (c.absY > 3920 && c.getPosition().inWild())
					c.getPA().movePlayer(1622, 3673, 0);
				break;
			case 2467:
				c.getPA().spellTeleport(2604, 3154, 0, false);
				c.sendMessage("This is the dicing area. Place a bet on designated hosts.");
				break;
			case 28851:// wcgate
				if (c.playerLevel[8] < 60) {
					c.sendMessage("You need a Woodcutting level of 60 to enter the Woodcutting Guild.");
					return;
				} else {
					c.getPA().movePlayer(1657, 3505, 0);
				}
				break;
			case 28852:// wcgate
				if (c.playerLevel[8] < 60) {
					c.sendMessage("You need a Woodcutting level of 60 to enter the Woodcutting Guild.");
					return;
				} else {
					c.getPA().movePlayer(1657, 3504, 0);
				}
				break;
			case 2309:
				if (c.getX() == 2998 && c.getY() == 3916) {
					c.getAgility().doWildernessEntrance(c, 2998, 3916, false);
				}
				if (c.absX == 2998 && c.absY == 3917) {
					c.getPA().movePlayer(2998, 3916, 0);
				}
				break;
			case 1766:
				if (c.getPosition().inWild() && c.absX == 3069 && c.absY == 10255) {
					c.getPA().movePlayer(3017, 3850, 0);
				}
				break;
			case 18989:
				if (c.getPosition().inWild()) {
					c.getPA().movePlayer(3017, 10250, 0);
				}
				break;
			case 18990:
				if (c.getPosition().inWild()) {
					c.getPA().movePlayer(3069, 3855, 0);
				}
				break;
			case 14745:
				if (c.getPosition().inWild()) {
					c.getPA().movePlayer(c.getX(), c.getY(), 1);
				}
				break;
			case 14746:
				if (c.getPosition().inWild()) {
					c.getPA().movePlayer(c.getX(), c.getY(), 2);
				}
				break;
			case 1765:
				if (c.getPosition().inWild() && c.absY >= 3847 && c.absY <= 3860) {
					c.getPA().movePlayer(3069, 10255, 0);
				}
				break;

			case 2118:
				if (Boundary.isIn(c, new Boundary(3433, 3536, 3438, 3539))) {
					c.getPA().movePlayer(3438, 3537, 0);
				}
				break;

			case 2114:
				if (Boundary.isIn(c, new Boundary(3433, 3536, 3438, 3539))) {
					c.getPA().movePlayer(3433, 3537, 1);
				}
				break;


			case 7108:
			case 7111:
				if (c.absX == 2907 || c.absX == 2908) {
					if (c.absY == 9698) {
						c.getPA().walkTo(0, -1);
					} else if (c.absY == 9697) {
						c.getPA().walkTo(0, +1);
					}
				}
				break;

			case 2119:
				if (c.heightLevel == 1) {
					if (c.absX == 3412 && (c.absY == 3540 || c.absY == 3541)) {
						c.getPA().movePlayer(3417, c.absY, 2);
					}
				}
				break;

			case 2120:
				if (c.heightLevel == 2) {
					if (c.absX == 3417 && (c.absY == 3540 || c.absY == 3541)) {
						c.getPA().movePlayer(3412, c.absY, 1);
					}
				}
				break;

			case 2102:
			case 2104:
				if (c.heightLevel == 1) {
					if (c.absX == 3426 || c.absX == 3427) {
						if (c.absY == 3556) {
							c.getPA().walkTo(0, -1);
						} else if (c.absY == 3555) {
							c.getPA().walkTo(0, +1);
						}
					}
				}
				break;

			case 1597:
			case 1596:
				// case 7408:
				// case 7407:
				if (c.absY < 9000) {
					if (c.absY > 3903) {
						c.getPA().movePlayer(c.absX, c.absY - 1, 0);
					} else {
						c.getPA().movePlayer(c.absX, c.absY + 1, 0);
					}
				} else if (c.absY > 9917) {
					c.getPA().movePlayer(c.absX, c.absY - 1, 0);
				} else {
					c.getPA().movePlayer(c.absX, c.absY + 1, 0);
				}
				break;

			case 24600:
				c.getDH().sendDialogues(500, -1);
				break;

			case 14315:
				PestControl.addToLobby(c);
				break;

			case 14314:
				PestControl.removeFromLobby(c);
				break;

			case 16105:
				if (c.objectX == 3659) {
					if (c.absY <= 3507) {
						c.getPA().movePlayer(c.absX, 3509, 0);
					} else if (c.absY > 3508) {
						c.getPA().movePlayer(c.absX, 3507, 0);
					}
				} else if (c.objectX == 3652) {
					if (c.absX <= 3652) {
						c.getPA().movePlayer(3653, c.absY, 0);
					} else if (c.absX >= 3653) {
						c.getPA().movePlayer(3651, c.absY, 0);
					}
				} else if (c.objectX == 3669) {
					if (c.absY <= 3453) {
						c.getPA().movePlayer(c.absX, 3454, 0);
					} else if (c.absY > 3453) {
						c.getPA().movePlayer(c.absX, 3452, 0);
					}
				}
				break;
			case 12816:
				if (c.objectX == 3485) {
					if (c.absY <= 3243) {
						c.getPA().movePlayer(c.absX, 3244, 0);
					} else if (c.absY >= 3244) {
						c.getPA().movePlayer(c.absX, 3243, 0);
					}
				}
				break;
			case 14235:
			case 14233:
				if (c.objectX == 2670) {
					if (c.absX <= 2670) {
						c.absX = 2671;
					} else {
						c.absX = 2670;
					}
				}
				if (c.objectX == 2643) {
					if (c.absX >= 2643) {
						c.absX = 2642;
					} else {
						c.absX = 2643;
					}
				}
				if (c.absX <= 2585) {
					c.absY += 1;
				} else {
					c.absY -= 1;
				}
				c.updateController(); // Doing this because above it manually sets x/y coordinate
				c.getPA().movePlayer(c.absX, c.absY, 0);
				break;

			case 245:
				if (c.wildLevel > 0) {
					return;
				}
				c.getPA().movePlayer(c.absX + 2, c.absY, 2);
				break;
			case 246:
				if (c.wildLevel > 0) {
					return;
				}
				c.getPA().movePlayer(c.absX - 2, c.absY, 1);
				break;
			case 272:
				if (c.absY == 3956 || c. absY == 3957) {
					c.getPA().movePlayer(3018, 3958, 1);
					break;
				} else {
					c.getPA().movePlayer(c.absX, c.absY, 1);
				}
				break;
			case 273:
				if (c.absY == 3956 || c. absY == 3957) {
					c.getPA().movePlayer(3018, 3958, 0);
				} else {
					c.getPA().movePlayer(c.absX, c.absY, 0);
				}
				break;
			/* Godwars Door */
			/*
			 * case 26426: // armadyl if (c.absX == 2839 && c.absY == 5295) {
			 * c.getPA().movePlayer(2839, 5296, 2);
			 * c.sendMessage("@blu@May the gods be with you."); } else {
			 * c.getPA().movePlayer(2839, 5295, 2); } break; case 26425: // bandos if
			 * (c.absX == 2863 && c.absY == 5354) { c.getPA().movePlayer(2864, 5354, 2);
			 * c.sendMessage( "@blu@May the gods be with you."); } else {
			 * c.getPA().movePlayer(2863, 5354, 2); } break; case 26428: // bandos if
			 * (c.absX == 2925 && c.absY == 5332) { c.getPA().movePlayer(2925, 5331, 2);
			 * c.sendMessage("@blu@May the gods be with you."); } else {
			 * c.getPA().movePlayer(2925, 5332, 2); } break; case 26427: // bandos if
			 * (c.absX == 2908 && c.absY == 5265) { c.getPA().movePlayer(2907, 5265, 0);
			 * c.sendMessage("@blu@May the gods be with you."); } else {
			 * c.getPA().movePlayer(2908, 5265, 0); } break;
			 */

			case 5960://lever at magebank
				c.getPA().startLeverTeleport(3090, 3956, 0);
				break;
			case 5959:
				if (c.absX != 3089) {
					c.getPA().startLeverTeleport(2539, 4712, 0);
				}
				break;
			case 39653:
			case 39652:
				if (c.getY() == 3629 || c.getY() == 3628) {
					if (c.absX == 3122) {
						PathFinder.getPathFinder().findRoute(c, c.getX() + 1, c.getY(), true, 1, 1, true);
						return;
					}
				}
				if (c.getX() == 3134 || c.getX() == 3135) {
					if (c.getY() == 3616) {
						PathFinder.getPathFinder().findRoute(c,c.getX(),c.getY()+1,true,1,1,true);
						return;
					} else if (c.getY() == 3640) {
						PathFinder.getPathFinder().findRoute(c,c.getX(),c.getY()-1,true,1,1,true);
						return;
					}
				}
				if (c.getY() == 3634 || c.getY() == 3635) {
					if (c.getX() == 3155) {
						PathFinder.getPathFinder().findRoute(c,c.getX()-1,c.getY(),true,1,1,true);
						return;
					}
				}
				WildWarning.sendWildWarning(c, plr -> {
					if (plr.getX() == 3134 || plr.getX() == 3135) {
						if (plr.getY() == 3639) {
							PathFinder.getPathFinder().findRoute(plr,plr.getX(),plr.getY()+1,true,1,1,true);
						} else if (plr.getY() == 3640) {
							PathFinder.getPathFinder().findRoute(plr,plr.getX(),plr.getY()-1,true,1,1,true);
						} else if (plr.getY() == 3604) {
							PathFinder.getPathFinder().findRoute(plr,plr.getX(),plr.getY()-1,true,1,1,true);
						} else if (plr.getY() == 3603) {
							PathFinder.getPathFinder().findRoute(plr,plr.getX(),plr.getY()+1,true,1,1,true);
						}
					}
					if (plr.getY() == 3629 || plr.getY() == 3628) {
						if (plr.getX() == 3121) {
							PathFinder.getPathFinder().findRoute(plr,plr.getX()-1,plr.getY(),true,1,1,true);
						} else if (plr.getX() == 3120) {
							PathFinder.getPathFinder().findRoute(plr,plr.getX()+1,plr.getY(),true,1,1,true);
						}
					}
					if (plr.getY() == 3634 || plr.getY() == 3635) {
						if (plr.getX() == 3155) {
							PathFinder.getPathFinder().findRoute(plr,plr.getX()-1,plr.getY(),true,1,1,true);
						} else if (plr.getX() == 3154) {
							PathFinder.getPathFinder().findRoute(plr,plr.getX()+1,plr.getY(),true,1,1,true);
						}
					}
				});
				break;
			case 1814:
				if (Boundary.isIn(c, Boundary.ARDOUGNE_BOUNDARY)) {
					c.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.WILDERNESS_LEVER);
				}
				c.getPA().startLeverTeleport(3153, 3923, 0);
				break;
			case 14929:
				c.getPA().movePlayer(2712, 3472, 3);
			case 1815:
				if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
					c.sendMessage("You cannot leave the wilderness!");
					return;
				}
				c.getPA().startLeverTeleport(2564, 3310, 0);
				break;
			case 1816:
				c.getPA().startLeverTeleport(2271, 4680, 0);
				c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.KBD_LAIR);
				break;
			/* Start Brimhavem Dungeon */
			case 2879:
				c.getPA().movePlayer(2542, 4718, 0);
				break;
			case 2878:
				c.getPA().movePlayer(2509, 4689, 0);
				break;
			case 5083:
				c.getPA().movePlayer(2713, 9564, 0);
				c.sendMessage("You enter the dungeon.");
				break;

			case 5103:
				if (c.absX == 2691 && c.absY == 9564) {
					c.getPA().movePlayer(2689, 9564, 0);
				} else if (c.absX == 2689 && c.absY == 9564) {
					c.getPA().movePlayer(2691, 9564, 0);
				}
				break;

			case 5106:
			case 21734:
				if (c.absX == 2674 && c.absY == 9479) {
					c.getPA().movePlayer(2676, 9479, 0);
				} else if (c.absX == 2676 && c.absY == 9479) {
					c.getPA().movePlayer(2674, 9479, 0);
				}
				break;
			case 5105:
			case 21733:
				if (c.absX == 2672 && c.absY == 9499) {
					c.getPA().movePlayer(2674, 9499, 0);
				} else if (c.absX == 2674 && c.absY == 9499) {
					c.getPA().movePlayer(2672, 9499, 0);
				}
				break;

			case 5107:
			case 21735:
				if (c.absX == 2693 && c.absY == 9482) {
					c.getPA().movePlayer(2695, 9482, 0);
				} else if (c.absX == 2695 && c.absY == 9482) {
					c.getPA().movePlayer(2693, 9482, 0);
				}
				break;

			case 21731:
				if (c.absX == 2691) {
					c.getPA().movePlayer(2689, 9564, 0);
				} else if (c.absX == 2689) {
					c.getPA().movePlayer(2691, 9564, 0);
				}
				break;

			case 5104:
			case 21732:
				if (c.absX == 2683 && c.absY == 9568) {
					c.getPA().movePlayer(2683, 9570, 0);
				} else if (c.absX == 2683 && c.absY == 9570) {
					c.getPA().movePlayer(2683, 9568, 0);
				}
				break;

			case 5100:
				if (c.absY <= 9567) {
					c.getPA().movePlayer(2655, 9573, 0);
				} else if (c.absY >= 9572) {
					c.getPA().movePlayer(2655, 9566, 0);
				}
				break;
			case 21728:
				if (c.playerLevel[16] < 34) {
					c.sendMessage("You need an Agility level of 34 to pass this.");
					return;
				}
				if (c.absY == 9566) {
					AgilityHandler.delayEmote(c, "CRAWL", 2655, 9573, 0, 2);
				} else {
					AgilityHandler.delayEmote(c, "CRAWL", 2655, 9566, 0, 2);
				}
				break;

			case 5099:
			case 21727:
				if (c.playerLevel[16] < 34) {
					c.sendMessage("You need an Agility level of 34 to pass this.");
					return;
				}
				if (c.objectX == 2698 && c.objectY == 9498) {
					c.getPA().movePlayer(2698, 9492, 0);
				} else if (c.objectX == 2698 && c.objectY == 9493) {
					c.getPA().movePlayer(2698, 9499, 0);
				}
				break;
			case 5088:
			case 20882:
				if (c.playerLevel[16] < 30) {
					c.sendMessage("You need an Agility level of 30 to pass this.");
					return;
				}
				c.getPA().movePlayer(2687, 9506, 0);
				break;

			case 6097:
/*				if (true) {
					c.sendMessage("You need to use FOE Tickets on the well.");
					return;
				}*/
				c.getWogwContributeInterface().open();
				break;
			case 43751:
				c.objectDistance = 10;
				c.facePosition(obX,obY);
				if (c.getRights().isOrInherits(Right.Extreme_Donator)) {
					if (c.getPosition().getY() < obY) {
						c.getPA().movePlayer(1948, 5326, 0);
					} else {
						c.getPA().movePlayer(1948, 5322, 0);
					}
				} else {
					c.sendMessage("You need to have Cell Donor or higher to access this area.");
				}
				break;
			case 43749:
				c.objectDistance = 10;
				c.facePosition(obX,obY);
				if (c.getRights().isOrInherits(Right.Great_Donator)) {
					if (c.getPosition().getY() < obY) {
						c.getPA().movePlayer(1948, 5364, 0);
					} else {
						c.getPA().movePlayer(1948, 5361, 0);
					}
				} else {
					c.sendMessage("You need to have Gohan Donor or higher to access this area.");
				}
				break;
			case 5090:
			case 20884:
				if (c.playerLevel[16] < 30) {
					c.sendMessage("You need an Agility level of 30 to pass this.");
					return;
				}
				c.getPA().movePlayer(2682, 9506, 0);
				break;

			case 16511:
				if (c.playerLevel[16] < 51) {
					c.sendMessage("You need an agility level of at least 51 to squeeze through.");
					return;
				}
				if (c.absX == 3149 && c.absY == 9906) {
					c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.OBSTACLE_PIPE);
					c.getPA().movePlayer(3155, 9906, 0);
				} else if (c.absX == 3155 && c.absY == 9906) {
					c.getPA().movePlayer(3149, 9906, 0);
				}
				break;

			case 5110:
			case 21738:
				if (c.playerLevel[16] < 12) {
					c.sendMessage("You need an Agility level of 12 to pass this.");
					return;
				}
				c.getPA().movePlayer(2647, 9557, 0);
				break;
			case 5111:
			case 21739:
				if (c.playerLevel[16] < 12) {
					c.sendMessage("You need an Agility level of 12 to pass this.");
					return;
				}
				c.getPA().movePlayer(2649, 9562, 0);
				break;
			case 36062:
			case 32996:
				if (Boundary.isIn(c, Boundary.EDGEVILLE_EXTENDED)) {
					TeleportInterface.open(c);
				}
				break;
			case 27362:// lizardmen
				if (c.absY > 3688) {
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1454, 3690, 0, 2);
					c.sendMessage("You climb down into Shayzien Assault.");
				} else
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1477, 3690, 0, 2);
				c.sendMessage("You climb down into Lizardman Camp.");
				break;
			case 4155:// zulrah
				c.getPA().movePlayer(2200, 3055, 0);
				c.sendMessage("You climb down.");
				break;
			case 4152:
				c.start(new SkillingPortalDialogue(c));
				break;
			case 5084:
				c.getPA().movePlayer(2744, 3151, 0);
				c.sendMessage("You exit the dungeon.");
				break;
			/* End Brimhavem Dungeon */
			case 6481:
				c.getPA().movePlayer(3233, 9315, 0);
				break;

			/*
			 * case 17010: if (c.playerMagicBook == 0) {
			 * c.sendMessage("You switch spellbook to lunar magic.");
			 * c.setSidebarInterface(6, 29999); c.playerMagicBook = 2; c.autocasting =
			 * false; c.autocastId = -1; c.getPA().resetAutocast(); break; } if
			 * (c.playerMagicBook == 1) {
			 * c.sendMessage("You switch spellbook to lunar magic.");
			 * c.setSidebarInterface(6, 29999); c.playerMagicBook = 2; c.autocasting =
			 * false; c.autocastId = -1; c.getPA().resetAutocast(); break; } if
			 * (c.playerMagicBook == 2) { c.setSidebarInterface(6, 1151); c.playerMagicBook
			 * = 0; c.autocasting = false;
			 * c.sendMessage("You feel a drain on your memory."); c.autocastId = -1;
			 * c.getPA().resetAutocast(); break; } break;
			 */

			case 1551:
				if (c.absX == 3252 && c.absY == 3266) {
					c.getPA().movePlayer(3253, 3266, 0);
				}
				if (c.absX == 3253 && c.absY == 3266) {
					c.getPA().movePlayer(3252, 3266, 0);
				}
				break;
			case 1553:
				if (c.absX == 3252 && c.absY == 3267) {
					c.getPA().movePlayer(3253, 3266, 0);
				}
				if (c.absX == 3253 && c.absY == 3267) {
					c.getPA().movePlayer(3252, 3266, 0);
				}
				break;
			case 43703:
				c.sendMessage("Coming soon.");
				break;
			case 3044:
			case 24009:
			case 26300:
			case 36555:
			case 16469:
			case 14838:
			case 40949:
			case 2030:
				c.objectDistance = 1;
				if (CannonballSmelting.isSmeltingCannonballs(c)) {
					CannonballSmelting.smelt(c);
				} else {
					c.getSmithing().sendSmelting();
				}
				break;
			/*
			 * case 2030: if (c.absX == 1718 && c.absY == 3468) {
			 * c.getSmithing().sendSmelting(); } else { c.getSmithing().sendSmelting(); }
			 * break;
			 */

			/* AL KHARID */
			case 2883:
			case 2882:
				c.getDH().sendDialogues(1023, 925);
				break;
			// case 2412:
			// Sailing.startTravel(c, 1);
			// break;
			// case 2414:
			// Sailing.startTravel(c, 2);
			// break;
			// case 2083:
			// Sailing.startTravel(c, 5);
			// break;
			// case 2081:
			// Sailing.startTravel(c, 6);
			// break;
			// case 14304:
			// Sailing.startTravel(c, 14);
			// break;
			// case 14306:
			// Sailing.startTravel(c, 15);
			// break;

			case 2213:
			case 24101:
			case 3045:
			case 14367:
			case 3193:
			case 10517:
			case 11402:
			case 26972:
			case 4483:
			case 25808:
			case 11744:
			case 12309:
			case 10058:
			case 2693:
			case 21301:
			case 6943:
			case 3194:
			case 10661:
				c.getPA().c.itemAssistant.openUpBank();
				c.inBank = true;
				break;
			case 13287:
				if (!c.getMode().isBankingPermitted() && (c.getItems().playerHasItem(8868)) && !c.unlockedUltimateChest) {
					c.getItems().deleteItem2(8868, 1);
					c.unlockedUltimateChest = true;
					PlayerSave.saveGame(c);
					c.sendMessage("You have permanently unlocked the UIM storage chest.");
				}
				if (!c.getMode().isBankingPermitted() && (c.getItems().playerHasItem(8866) || c.unlockedUltimateChest)) {
					c.inUimBank = true;
					c.getItems().deleteItem2(8866, 1);
					c.inBank = true;
					c.getPA().c.itemAssistant.openUpBank();
				} else if (!c.getMode().isBankingPermitted() && !c.getItems().playerHasItem(8866)){
					c.sendMessage("You must use a key from the Nomad shop to unlock this chest.");
				} else {
					c.sendMessage("This bank is only for Ultimate Ironman to use.");
				}
				break;
			case 3506:
			case 3507:
				if (c.absY == 3458)
					c.getPA().movePlayer(c.absX, 3457, 0);
				if (c.absY == 3457)
					c.getPA().movePlayer(c.absX, 3458, 0);
				break;

			case 11665:
				if (c.absX == 2658)
					c.getPA().movePlayer(2659, 3437, 0);
				c.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.RANGING_GUILD);
				if (c.absX == 2659)
					c.getPA().movePlayer(2657, 3439, 0);
				break;

			/**
			 * Entering the Fight Caves.
			 */
			case 30386:
			case 11833:
				if (Boundary.getPlayersInBoundary(Boundary.FIGHT_CAVE) >= 50) {
					c.sendMessage("There are too many people using the fight caves at the moment. Please try again later");
					return;
				}
				c.getDH().sendDialogues(633, -1);
				break;

			case 20667:
			case 20668:
			case 20669:
			case 20670:
			case 20671:
			case 20672:
				break;

			/**
			 * Clicking on the Ancient Altar.
			 */
			case 6552:
				if (c.getPosition().inWild()) {
					return;
				}
				c.autocasting = false;
				c.autocastId = -1;
				c.getPA().resetAutocast();
				if (c.absY == 9312) {
				}
				PlayerAssistant.switchSpellBook(c);
				break;

			/**
			 * c.setSidebarInterface(6, 1151); Recharing prayer points.
			 */
			case 20377:
				if (c.getPosition().inWild()) {
					return;
				}
				if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {

					if (c.getPA().getLevelForXP(c.playerXP[5]) > 85 && c.playerLevel[5] < 15) {
						c.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.PRAY_SOPHANEM);
					}
					c.startAnimation(645);
					c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
					c.sendMessage("You recharge your prayer points.");
					c.getPA().refreshSkill(5);
					c.getPA().sendSound(169);
				} else {
					c.sendMessage("You already have full prayer points.");
				}
				break;
			case 61:
				if (c.getPosition().inWild()) {
					return;
				}
				if (c.absY >= 3508 && c.absY <= 3513) {
					if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
						if (Boundary.isIn(c, Boundary.VARROCK_BOUNDARY)
								&& c.getDiaryManager().getVarrockDiary().hasCompleted("HARD")) {
							if (c.prayerActive[25]) {
								c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.PRAY_WITH_PIETY);
							}
						}
						c.startAnimation(645);
						c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
						c.sendMessage("You recharge your prayer points.");
						c.getPA().refreshSkill(5);
						c.getPA().sendSound(169);
					} else {
						c.sendMessage("You already have full prayer points.");
					}
				}
				break;
			case 47347:
				if (!c.inParty(IsleOfTheDamnedParty.TYPE)) {
					c.sendMessage("You must be in a party to start the Isle of the Damned minigame.");
					return;
				}

				if (c.inParty(IsleOfTheDamnedParty.TYPE)) {
					c.getParty().openStartActivityDialogue(c, "Isle of the Damned", Boundary.ISLE_OF_THE_DAMNED_LOBBY::in, list -> new IsleOfTheDamned().start(list));
					return;
				}
				break;
			case 29777:
				if (!c.inParty(CoxParty.TYPE)) {
					c.sendMessage("You must be in a party to start Chambers of Xeric.");
					return;
				}

				if (c.inParty(CoxParty.TYPE)) {
					c.getParty().openStartActivityDialogue(c, "Chambers of Xeric", Boundary.RAIDS_LOBBY_ENTRANCE::in, list -> new Raids().startRaid(list, true));
					return;
				}

				if (Boundary.isIn(c, Boundary.RAIDS_LOBBY_ENTRANCE)) {
					LobbyManager.get(LobbyType.CHAMBERS_OF_XERIC).ifPresent(lobby -> lobby.attemptJoin(c));
					return;
				}

				if  (Boundary.isIn(c, Boundary.RAIDS_LOBBY)) {
					LobbyManager.get(LobbyType.CHAMBERS_OF_XERIC)
							.ifPresent(lobby -> lobby.attemptLeave(c));
					c.getPA().movePlayer(1234, 3567, 0);
					return;
				}
				LobbyManager.get(LobbyType.CHAMBERS_OF_XERIC)
						.ifPresent(lobby -> lobby.attemptJoin(c));
				break;

			case 32541: //Raids Lobbies
				c.sendMessage("Temporarily disabled, new system to arrive soon!");
/*				if (Raids.isMissingRequirements(c)) {
					return;
				}

				for (Player player : Server.getPlayers().toPlayerArray()) {
					if (player.getXeric() != null && Boundary.isIn(player, Boundary.XERIC)) {
						c.sendMessage("A Trial's is already in progress you must wait!");
						return;
					} else if (player.getXeric() != null && !Boundary.isIn(player, Boundary.XERIC)) {
						player.setXeric(null);
					}
				}

				if (Boundary.isIn(c, Boundary.RAIDS_LOBBY_ENTRANCE)) {
					LobbyManager.get(LobbyType.TRIALS_OF_XERIC)
							.ifPresent(lobby -> lobby.attemptJoin(c));
					return;
				}
				if  (Boundary.isIn(c, Boundary.XERIC_LOBBY)) {
					LobbyManager.get(LobbyType.TRIALS_OF_XERIC)
							.ifPresent(lobby -> lobby.attemptLeave(c));
					c.getPA().movePlayer(1234, 3567, 0);
					break;
				}*/
				break;


			case 410:
				if (c.getPosition().inWild()) {
					return;
				}
				if (c.playerLevel[5] == c.getPA().getLevelForXP(c.playerXP[5])) {
					c.sendMessage("You already have full prayer points.");
					return;
				}
				if (Boundary.isIn(c, Boundary.TAVERLY_BOUNDARY)) {
					if (c.getItems().isWearingItem(5574) && c.getItems().isWearingItem(5575)
							&& c.getItems().isWearingItem(5576)) {
						c.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.ALTAR_OF_GUTHIX);
					}
				}
				c.startAnimation(645);
				c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
				c.sendMessage("You recharge your prayer points.");
				c.getPA().sendSound(169);
				c.getPA().refreshSkill(5);
				break;
			case 29941:
			case 27501:
			case 3126:
				if (c.getPosition().inWild()) {
					return;
				}
				if (c.playerLevel[5] == c.getPA().getLevelForXP(c.playerXP[5])) {
					c.sendMessage("You already have full prayer points.");
					return;
				}
				if (Boundary.isIn(c, Boundary.VARROCK_BOUNDARY)) {
					if (c.prayerActive[23]) {
						c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.PRAY_WITH_SMITE);
					}
				}
				if (Boundary.isIn(c, Boundary.ARDOUGNE_BOUNDARY)) {
					if (c.prayerActive[25]) {
						if (!c.getDiaryManager().getArdougneDiary().hasCompleted("MEDIUM")) {
							c.sendMessage("You must have completed all the medium tasks in the ardougne diary to do this.");
							return;
						}
						c.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.PRAY_WITH_CHIVALRY);
					}
				}
				c.startAnimation(645);
				c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
				c.sendMessage("You recharge your prayer points.");
				c.getPA().sendSound(169);
				c.getPA().refreshSkill(5);
				c.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.PRAY_AT_ALTAR);
				break;
			case 18818:
			case 40877:
			case 409:
			case 34855:
			case 4008:
			case 7812:
			case 6817:
			case 14860:
				if (c.getPosition().inWild()) {
					return;
				}
				if (c.playerLevel[5] == c.getPA().getLevelForXP(c.playerXP[5])) {
					c.sendMessage("You already have full prayer points.");
					return;
				}
				if (Boundary.isIn(c, Boundary.VARROCK_BOUNDARY)) {
					if (c.prayerActive[23]) {
						c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.PRAY_WITH_SMITE);
					}
				}
				if (Boundary.isIn(c, Boundary.ARDOUGNE_BOUNDARY)) {
					if (c.prayerActive[25]) {
						if (!c.getDiaryManager().getArdougneDiary().hasCompleted("MEDIUM")) {
							c.sendMessage("You must have completed all the medium tasks in the ardougne diary to do this.");
							return;
						}
						c.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.PRAY_WITH_CHIVALRY);
					}
				}
				c.startAnimation(645);
				c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
				c.sendMessage("You recharge your prayer points.");
				c.getPA().sendSound(169);
				c.getPA().refreshSkill(5);
				break;

			case 411:
				if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
					if (c.getPosition().inWild()) {
						c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.WILDERNESS_ALTAR);
					}
					c.startAnimation(645);
					c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
					c.sendMessage("You recharge your prayer points.");
					c.getPA().sendSound(169);
					c.getPA().refreshSkill(5);
				} else {
					c.sendMessage("You already have full prayer points.");
				}
				break;

			case 14896:
				c.facePosition(obX, obY);
				FlaxPicking.getInstance().pick(c, new Location3D(obX, obY, c.heightLevel));
				break;

			case 412:
				if (c.getPosition().inWild()) {
					return;
				}
				if (c.getMode().isIronmanType()) {
					c.sendMessage("Your game mode prohibits use of this altar.");
					return;
				}
				// if (c.absY >= 3504 && c.absY <= 3507) {
				if (c.specAmount < 10.0) {
					if (c.specRestore > 0) {
						int seconds = ((int) Math.floor(c.specRestore * 0.6));
						c.sendMessage("You have to wait another " + seconds + " seconds to use this altar.");
						return;
					}
					if (c.getRights().isOrInherits(Right.Major_Donator)) {
						c.specRestore = 120;
						c.specAmount = 10.0;
						c.getItems().addSpecialBar(c.playerEquipment[Player.playerWeapon]);
						c.sendMessage("Your special attack has been restored. You can restore it again in 3 minutes.");
					} else {
						c.specRestore = 240;
						c.specAmount = 10.0;
						c.getItems().addSpecialBar(c.playerEquipment[Player.playerWeapon]);
						c.sendMessage("Your special attack has been restored. You can restore it again in 6 minutes.");
					}
				}
				// }
				break;

			case 26366: // Godwars altars
			case 26365:
			case 26364:
			case 26363:
				if (c.getPosition().inWild()) {
					return;
				}
				if (c.gwdAltarTimer > 0) {
					int seconds = ((int) Math.floor(c.gwdAltarTimer * 0.6));
					c.sendMessage("You have to wait another " + seconds + " seconds to use this altar.");
					return;
				}
				if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
					c.startAnimation(645);
					c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
					c.sendMessage("You recharge your prayer points.");
					c.getPA().sendSound(169);
					c.gwdAltarTimer = 600;
					c.getPA().refreshSkill(5);
				} else {
					c.sendMessage("You already have full prayer points.");
				}
				break;

			/**
			 * Aquring god capes.
			 */
			case 2873:
				c.startAnimation(645);
				c.sendMessage("Saradomin blesses you with a cape.");
				c.getItems().addItem(2412, 1);
				break;
			case 2875:
				c.startAnimation(645);
				c.sendMessage("Guthix blesses you with a cape.");
				c.getItems().addItem(2413, 1);
				break;
			case 2874:
				c.startAnimation(645);
				c.sendMessage("Zamorak blesses you with a cape.");
				c.getItems().addItem(2414, 1);
				break;

			/**
			 * Oblisks in the wilderness.
			 */
			case 14829:
			case 14830:
			case 14827:
			case 14828:
			case 14826:
			case 14831:

				break;

			/**
			 * Clicking certain doors.
			 */
			case 1516:
			case 1519:
				if (c.objectY == 9698) {
					if (c.absY >= c.objectY)
						c.getPA().walkTo(0, -1);
					else
						c.getPA().walkTo(0, 1);
					break;
				}

			case 11737:
				if (!c.getRights().isOrInherits(Right.Major_Donator)) {
					return;
				}
				c.getPA().movePlayer(3365, 9641, 0);
				break;


			case 5126:
			case 2100:
				if (c.absY == 3554)
					c.getPA().walkTo(0, 1);
				else
					c.getPA().walkTo(0, -1);
				break;

			case 1759:
				if (c.objectX == 2884 && c.objectY == 3397)
					c.getPA().movePlayer(c.absX, c.absY + 6400, 0);
				break;
			case 1557:
			case 7169:
				if ((c.objectX == 3106 || c.objectX == 3105) && c.objectY == 9944) {
					if (c.getY() > c.objectY)
						c.getPA().walkTo(0, -1);
					else
						c.getPA().walkTo(0, 1);
				} else {
					if (c.getX() > c.objectX)
						c.getPA().walkTo(-1, 0);
					else
						c.getPA().walkTo(1, 0);
				}
				break;
			case 2558:
				c.sendMessage("This door is locked.");
				break;

			case 9294:
				if (c.absX < c.objectX) {
					c.getPA().movePlayer(c.objectX + 1, c.absY, 0);
				} else if (c.absX > c.objectX) {
					c.getPA().movePlayer(c.objectX - 1, c.absY, 0);
				}
				break;

			case 9293:
				if (c.absX < c.objectX) {
					c.getPA().movePlayer(2892, 9799, 0);
				} else {
					c.getPA().movePlayer(2886, 9799, 0);
				}
				break;

			case 10529:
			case 10527:
				if (c.absY <= c.objectY)
					c.getPA().walkTo(0, 1);
				else
					c.getPA().walkTo(0, -1);
				break;
			case 34858:
				c.sendMessage("You manage your way through the web.");
				if (c.absY == 9912)
					c.getPA().walkTo(0, -1);
				else if (c.absY == 9911)
					c.getPA().walkTo(0, 1);
				break;

			case 34898:
				if (c.absY <= c.objectY)
					c.getPA().walkTo(0, 1);
				else
					c.getPA().walkTo(0, -1);
				break;
			case SpiderWeb.OBJECT_ID:
				SpiderWeb.slash(c, object);
				break;

			case 7407:
				GlobalObject gate;
				gate = new GlobalObject(objectType, obX, obY, c.heightLevel, 2, 0, 50, 7407);
				Server.getGlobalObjects().add(gate);
				break;

			case 7408:
				GlobalObject secondGate;
				secondGate = new GlobalObject(objectType, obX, obY, c.heightLevel, 0, 0, 50, 7408);
				Server.getGlobalObjects().add(secondGate);
				break;

			/**
			 * Forfeiting a duel.
			 */
			case 3203:
				DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c,
						MultiplayerSessionType.DUEL);
				if (Objects.isNull(session)) {
					return;
				}
				if (!Boundary.isIn(c, Boundary.DUEL_ARENA)) {
					return;
				}
				if (session.getRules().contains(Rule.FORFEIT)) {
					c.sendMessage("You are not permitted to forfeit the duel.");
					return;
				}
				break;

		}

	}

}