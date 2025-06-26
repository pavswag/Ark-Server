package io.kyros.model.entity.player.packets.objectoptions;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.kyros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.kyros.content.afkzone.Afk;
import io.kyros.content.bosses.godwars.impl.ArmadylInstance;
import io.kyros.content.bosses.godwars.impl.BandosInstance;
import io.kyros.content.bosses.godwars.impl.SaradominInstance;
import io.kyros.content.bosses.godwars.impl.ZamorakInstance;
import io.kyros.content.bosses.grotesqueguardians.GrotesqueInstance;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.bosses.wintertodt.WintertodtActions;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.impl.OutlastLeaderboard;
import io.kyros.content.events.monsterhunt.ShootingStars;
import io.kyros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.kyros.content.seasons.Christmas;
import io.kyros.content.skills.FlaxPicking;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.agility.AgilityHandler;
import io.kyros.content.skills.hunter.Hunter;
import io.kyros.content.skills.smithing.CannonballSmelting;
import io.kyros.content.skills.thieving.Thieving.Stall;
import io.kyros.content.wildwarning.WildWarning;
import io.kyros.model.Items;
import io.kyros.model.collisionmap.ObjectDef;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Right;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.entity.player.packets.objectoptions.impl.DarkAltar;
import io.kyros.model.items.GameItem;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.objects.ObjectAction;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;
import io.kyros.util.offlinestorage.ItemCollection;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

/*
 * @author Matt
 * Handles all 2nd options for objects.
 */

public class ObjectOptionTwo {

	public static void handleOption(final Player c, int objectType, int obX, int obY) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickObjectType = 0;
		GlobalObject object = new GlobalObject(objectType, obX, obY, c.heightLevel);
		if (c.getRights().isOrInherits(Right.STAFF_MANAGER) && c.debugMessage)
			c.sendMessage("Clicked Object Option 2:  "+objectType+"");
		Location3D location = new Location3D(obX, obY, c.heightLevel);
		ObjectDef def = ObjectDef.getObjectDef(objectType);

		ObjectAction action = null;
		ObjectAction[] actions = def.defaultActions;
		if(actions != null)
			action = actions[1];
		if(action == null && (actions = def.defaultActions) != null)
			action = actions[1];
		if(action != null) {
			action.handle(c, object);
			return;
		}
		 if ((def != null ? def.name : null) != null && def.name.toLowerCase().contains("bank") && !Boundary.isIn(c, Boundary.OURIANA_ALTAR)) {
			c.getPA().c.itemAssistant.openUpBank();
			c.inBank = true;
			return;
		}

		if (c.getRights().isOrInherits(Right.STAFF_MANAGER) && c.debugMessage)
			c.sendMessage("Clicked Object Option 2:  "+objectType+"");

		if (OutlastLeaderboard.handleInteraction(c, objectType, 2))
			return;
		if (Hespori.clickObject(c, objectType)) {
			return;
		}
		if (WintertodtActions.handleObjects(object, c, 2))
			return;

		if (Christmas.handleCollectGift(c, objectType)) {
			return;
		}
		if (Boundary.isIn(c, Boundary.AFK_ZONE) && Afk.handleAFKObjectCheck(c, object)) {
			Afk.Start(c, new Location3D(obX, obY, c.heightLevel), objectType);
			return;
		}
		//if (Halloween.handleCauldron(c, 2, objectType)) {
		//	return;
		//}
		switch (objectType) {
			case 2079:
				ItemCollection.open(c);
				return;
			case 26645:
				WildWarning.sendWildWarning(c, p -> {
					p.getPA().movePlayer(3185, 3935, 0);
				});
				return;
			case 41223:
			case 41224:
			case 41225:
			case 41226:
			case 41227:
			case 41228:
			case 41229:
				ShootingStars.inspect(c);
				break;

			case 7053:
				if (c.amDonated < 100) {
					c.sendMessage("You need 100 or greater total donated.");
					return;
				}
				if (System.currentTimeMillis() - c.infernoLeaveTimer < 1_500L) {
//					c.sendMessage("You cannot leave yet, wait a couple of seconds and try again.");
					return;
				}
				if (c.getItems().freeSlots() == 0) {
					c.sendMessage("You need at least one free slot to steal from this.");
					return;
				}
				if (Misc.random(100) == 0 && c.getInterfaceEvent().isExecutable()) {
					c.getInterfaceEvent().execute();
					return;
				}
				c.facePosition(obX, obY);
				c.startAnimation(881);
				c.getPA().addSkillXPMultiplied(100, Skill.THIEVING.getId(), true);
				List<GameItem> rewards = Lists.newArrayList(
						new GameItem(Items.RANARR_SEED, 1),
						new GameItem(Items.TOADFLAX_SEED, 1),
						new GameItem(Items.IRIT_SEED, 1),
						new GameItem(Items.RANARR_SEED, 1),
						new GameItem(Items.TOADFLAX_SEED, 1),
						new GameItem(Items.IRIT_SEED, 1),
						new GameItem(Items.RANARR_SEED, 1),
						new GameItem(Items.TOADFLAX_SEED, 1),
						new GameItem(Items.IRIT_SEED, 1),
						new GameItem(Items.AVANTOE_SEED, 1),
						new GameItem(Items.KWUARM_SEED, 1),
						new GameItem(Items.SNAPDRAGON_SEED, 1),
						new GameItem(Items.CADANTINE_SEED, 1),
						new GameItem(Items.CADANTINE_SEED, 1),
						new GameItem(Items.CADANTINE_SEED, 1),
						new GameItem(Items.CADANTINE_SEED, 1),
						new GameItem(Items.LANTADYME_SEED, 1),
						new GameItem(Items.DWARF_WEED_SEED, 1),
						new GameItem(Items.TORSTOL_SEED, 1)
				);
				GameItem reward = Misc.getRandomItem(rewards);
				c.getItems().addItem(reward.getId(), Misc.random(1,3), true);
				c.infernoLeaveTimer = System.currentTimeMillis();
				int petRate = c.skillingPetRateScroll ? (int) (2500 * .75) : 2500;
				if (Misc.random(petRate) == 20 && c.getItems().getItemCount(20663, false) == 0 && c.petSummonId != 20663) {
					PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255>" + c.getDisplayName() + "</col> now goes hand in hand with a <col=CC0000>Rocky</col> pet!");

					Achievements.increase(c, AchievementType.THIEV, 1);
					c.getItems().addItemUnderAnyCircumstance(20663, 1);
					c.getCollectionLog().handleDrop(c, 5, 20663, 1);
				}
				break;

			case 14736:
				c.getPA().movePlayer(c.getX(), c.getY(), c.getHeight()+1);
				break;
			case 42967:
				//TODO Instance joining for nex
				break;
			/**
			 * Entering the Fight Caves.
			 */
			case 30386:
				if (Boundary.getPlayersInBoundary(Boundary.FIGHT_CAVE) >= 50) {
					c.sendMessage("There are too many people using the fight caves at the moment. Please try again later");
					return;
				}
				c.getDH().sendDialogues(633, -1);
				break;
			case 28562:
				FireOfExchangeBurnPrice.openExchangeRateShop(c);
				break;
			case 31619:
				c.start(new DialogueBuilder(c).statement("Use item's on the gravestone, to turn your items into bloodmoney!", "Be careful once you use the items on the gravestone there,","Is no turning back."));
				break;
			case 31681:
				if (!c.gargoyleStairsUnlocked) {
					int[] keys = {275 /* old key */, GrotesqueInstance.GROTESQUE_GUARDIANS_KEY};
					OptionalInt heldKey = Arrays.stream(keys).filter(key -> c.getItems().playerHasItem(key)).findFirst();
					heldKey.ifPresentOrElse(key -> {
						c.getItems().deleteItem(key, 1);
						c.gargoyleStairsUnlocked = true;
						c.sendMessage("The gate is now open.");
					}, () -> c.sendMessage("You need a key to go through here."));
					return;
				}

				new GrotesqueInstance().enter(c);
				break;
			case 721:
				Hunter.resetTrap(c, object);
				break;
			case 26505:
				if (c.absX == 2925 &&c.absY == 5333) {
					ZamorakInstance instance = new ZamorakInstance(c, Boundary.ZAMORAK_GODWARS);
					ZamorakInstance.enter(c, instance);
				}
				break;
			case 26504:
				if (c.absX == 2909 && c.absY == 5265) {
					SaradominInstance instance = new SaradominInstance(c, Boundary.SARADOMIN_GODWARS);
					SaradominInstance.enter(c, instance);
//					c.getGodwars().enterBossRoom(God.SARADOMIN);
				}
				break;
			case 26502:
				if (c.absX == 2839 && c.absY == 5294) {
					ArmadylInstance instance = new ArmadylInstance(c, Boundary.ARMADYL_GODWARS);
					ArmadylInstance.enter(c, instance);
//					c.getGodwars().enterBossRoom(God.ARMADYL);
				}
				break;
			case 26503:
				if (c.absX == 2862 && c.absY == 5354) {
					BandosInstance instance = new BandosInstance(c, Boundary.BANDOS_GODWARS);
					BandosInstance.enter(c, instance);
//					c.getGodwars().enterBossRoom(God.BANDOS);
				}
				break;
			case 14747:
				if (c.getPosition().inWild() && c.getHeight() == 1) {
					c.getPA().movePlayer(c.getX(), c.getY(), 2);
					return;
				}
				if (c.getPosition().inWild() && c.getHeight() == 2) {
					c.getPA().movePlayer(c.getX(), c.getY(), 3);
				}
				break;
		case 31858:
		case 29150:
			c.playerMagicBook = 1;
			c.setSidebarInterface(6, 838);
			c.sendMessage("An ancient wisdomin fills your mind.");
			c.getPA().resetAutocast();
			break;
		case 1295:
				c.getPA().movePlayer(3088, 3505, 0);
				c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.GRAND_TREE_TELEPORT);
				break;
		case 34660:
		case 34662:
			c.getPA().movePlayer(1309, 3786, 0);
			break;
			//case 34553:
			//case 34554:
			//c.getDH().sendStatement("Alchemical hydra is in developement.");
			//break;
		case 10060:
		case 10061:
			if (c.getMode().isIronmanType() || c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
				c.sendMessage("@red@You are not permitted to make use of this.");
				return;
			}
			c.getTradePost().openMyOffers();
			break;
		case 2884:
		case 16684:
		case 16683:
			if (c.absY == 3494 || c.absY == 3495 || c.absY == 3496) {
				AgilityHandler.delayEmote(c, "CLIMB_UP", c.getX(), c.getY(), c.getHeight() + 1, 2);
			}
			break;
/*		case 29333:
			if (c.getMode().isIronmanType()) {
				c.sendMessage("@red@You are not permitted to make use of this.");			}
			Listing.collectMoney(c);
			
			break;*/
		case 28900:
			DarkAltar.handleRechargeInteraction(c);
			break;
		case 33311:
			Hespori.burnRunes(c);
			break;
		case 29777:
		case 29734:
		case 10777:
		case 29879:
			c.objectDistance = 4;

			break;
		case 30107:
/*			 if (c.getItems().freeSlots() < 3) {
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
			c.getDH().sendStatement("@red@You need either a rare or common key.");*/
			break;
		case 7811:
			if (!c.getPosition().inClanWarsSafe()) {
				return;
			}
			c.getShops().openShop(115);
			break;
		case 47461:
			c.getShops().openShop(596);
			break;
		/**
		 * Iron Winch - peek
		 */
		case 23104:
			c.getDH().sendDialogues(110, 5870);
			break;
			
		case 2118:
			c.getPA().movePlayer(3434, 3537, 0);
			break;

		case 2114:
			c.getPA().movePlayer(3433, 3537, 1);
			break;
		case 25824:
			c.facePosition(obX, obY);
			c.getDH().sendDialogues(40, -1);
			break;
		case 26260:
			c.getDH().sendDialogues(55874, -1);
			break;
		case 14896:
			c.facePosition(obX, obY);
			FlaxPicking.getInstance().pick(c, new Location3D(obX, obY, c.heightLevel));
			break;

			case 4874:
		case 11730:
			c.getThieving().doStealStall(Stall.Crafting, location);
			c.objectDistance = 1;
			break;
		case 4877:
		case 11731:
			if (Boundary.isIn(c, Boundary.FALADOR_BOUNDARY)) {
				c.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.STEAL_GEM_FAL);
				c.getThieving().doStealStall(Stall.Magic, location);
				c.objectDistance = 1;
				return;
			}
			c.getThieving().doStealStall(Stall.Magic, location);
			break;
		case 11729:
			c.getThieving().doStealStall(Stall.Silk, location);
			c.objectDistance = 1;
			break;
		case 4876:
			c.getThieving().doStealStall(Stall.General, location);
			c.objectDistance = 1;
			break;
		case 4878:
			c.getThieving().doStealStall(Stall.Scimitar, location);
			c.objectDistance = 1;
			break;
		case 4875:
			c.getThieving().doStealStall(Stall.Food, location);
			c.objectDistance = 1;
			break;
		case 11734:
			c.getThieving().doStealStall(Stall.Silver, location);
			break;
		case 11732:
			c.getThieving().doStealStall(Stall.Fur, location);
			break;
		case 11733:
			c.getThieving().doStealStall(Stall.Spice, location);
			break;
		case 29165:
			c.getThieving().doStealStall(Stall.Gold, location);
			break;
		case 6162:
			c.getThieving().doStealStall(Stall.LZ_GOLD, location);
			break;
		case 23609:
			c.getPA().movePlayer(3507, 9494, 0);
			break;
			
		case 2558:
		case 8356://streequid
			c.getPA().movePlayer(1255, 3568, 0);
			break;
		case 2557:
			if (System.currentTimeMillis() - c.lastLockPick < 1000 || c.freezeTimer > 0) {
				return;
			}
			c.lastLockPick = System.currentTimeMillis();
			if (c.getItems().playerHasItem(1523, 1)) {

				if (Misc.random(10) <= 2) {
					c.sendMessage("You fail to pick the lock.");
					break;
				}
				if (c.objectX == 3044 && c.objectY == 3956) {
					if (c.absX == 3045) {
						c.getPA().walkTo(-1, 0);
					} else if (c.absX == 3044) {
						c.getPA().walkTo(1, 0);
					}

				} else if (c.objectX == 3038 && c.objectY == 3956) {
					if (c.absX == 3037) {
						c.getPA().walkTo(1, 0);
					} else if (c.absX == 3038) {
						c.getPA().walkTo(-1, 0);
					}
				} else if (c.objectX == 3041 && c.objectY == 3959) {
					if (c.absY == 3960) {
						c.getPA().walkTo(0, -1);
					} else if (c.absY == 3959) {
						c.getPA().walkTo(0, 1);
					}
				} else if (c.objectX == 3191 && c.objectY == 3963) {
					if (c.absY == 3963) {
						c.getPA().walkTo(0, -1);
					} else if (c.absY == 3962) {
						c.getPA().walkTo(0, 1);
					}
				} else if (c.objectX == 3190 && c.objectY == 3957) {
					if (c.absY == 3957) {
						c.getPA().walkTo(0, 1);
					} else if (c.absY == 3958) {
						c.getPA().walkTo(0, -1);
					}
				}
			} else {
				c.sendMessage("I need a lockpick to pick this lock.");
			}
			break;
		case 7814:
			if (c.playerMagicBook == 0) {
				c.playerMagicBook = 1;
				c.setSidebarInterface(6, 838);
				c.sendMessage("An ancient wisdomin fills your mind.");
				c.getPA().resetAutocast();
			} else if (c.playerMagicBook == 1) {
				c.sendMessage("You switch to the lunar spellbook.");
				c.setSidebarInterface(6, 29999);
				c.playerMagicBook = 2;
				c.getPA().resetAutocast();
			} else if (c.playerMagicBook == 2) {
				c.setSidebarInterface(6, 938);
				c.playerMagicBook = 0;
				c.sendMessage("You feel a drain on your memory.");
				c.getPA().resetAutocast();
			}
			break;
		case 17010:
			if (c.playerMagicBook == 0) {
				c.sendMessage("You switch spellbook to lunar magic.");
				c.setSidebarInterface(6, 838);
				c.playerMagicBook = 2;
				c.autocasting = false;
				c.autocastId = -1;
				c.getPA().resetAutocast();
				break;
			}
			if (c.playerMagicBook == 1) {
				c.sendMessage("You switch spellbook to lunar magic.");
				c.setSidebarInterface(6, 29999);
				c.playerMagicBook = 2;
				c.autocasting = false;
				c.autocastId = -1;
				c.getPA().resetAutocast();
				break;
			}
			if (c.playerMagicBook == 2) {
				c.setSidebarInterface(6, 938);
				c.playerMagicBook = 0;
				c.autocasting = false;
				c.sendMessage("You feel a drain on your memory.");
				c.autocastId = -1;
				c.getPA().resetAutocast();
				break;
			}
			break;
		/*
		 * One stall that will give different amount of money depending on your thieving level, also different amount of xp.
		 */
		case 2781:
		case 26814:
		case 11666:
		case 3044:
			case 36555:
		case 16469:
			case 40949:
			case 2030:
		case 24009:
		case 26300:
			c.objectDistance = 1;
			if (CannonballSmelting.isSmeltingCannonballs(c)) {
				CannonballSmelting.smelt(c);
			} else {
				c.getSmithing().sendSmelting();
			}
			break;
			
			
			/**
		 * Opening the bank.
		 */
		case 24101:
		case 14367:
		case 11758:
		case 10517:
		case 26972:
		case 25808:
		case 11744:
		case 11748:
		case 24347:
		case 16700:
			c.inBank = true;
			c.getPA().c.itemAssistant.openUpBank();
			break;

		}
	}
}
