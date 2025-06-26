package io.kyros.model.entity.player.packets.npcoptions;

import com.google.common.collect.Lists;
import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.cache.definitions.ItemDefinition;
import io.kyros.content.PetCollector;
import io.kyros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.kyros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.kyros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.kyros.content.bosses.SuperiorTask.SuperiorTaskInstance;
import io.kyros.content.bosses.nightmare.NightmareActionHandler;
import io.kyros.content.dailyrewards.DailyRewardsDialogue;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.dialogue.impl.IronmanNpcDialogue;
import io.kyros.content.dialogue.impl.MacDialogue;
import io.kyros.content.dialogue.impl.MonkChaosAltarDialogue;
import io.kyros.content.dialogue.impl.PineAwayDialogue;
import io.kyros.content.minigames.inferno.Inferno;
import io.kyros.content.minigames.tob.TobConstants;
import io.kyros.content.minigames.wanderingmerchant.Merchant;
import io.kyros.content.miniquests.magearenaii.dialogue.KolodionDialogue;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.content.skills.Fishing;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.crafting.Tanning;
import io.kyros.content.skills.farming.FarmingTeleport;
import io.kyros.content.skills.hunter.impling.Impling;
import io.kyros.content.skills.mining.Mineral;
import io.kyros.content.skills.thieving.Thieving;
import io.kyros.content.teleportv2.inter.TeleportInterface;
import io.kyros.content.votingincentive.VoteEntriesInstance;
import io.kyros.model.Npcs;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCAction;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.npc.pets.Probita;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.Right;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.entity.player.mode.group.GroupIronman;
import io.kyros.model.entity.player.mode.group.GroupIronmanDialogue;
import io.kyros.model.entity.player.mode.group.contest.GroupIronmanContest;
import io.kyros.model.items.GameItem;
import io.kyros.sql.refsystem.RefManager;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;

import static io.kyros.Server.getNpcs;

/*
 * @author Matt //dogshit
 * Handles all first options on non playable characters.
 */

public class NpcOptionOne {

	public static void handleOption(Player player, int npcType) {
		if (Server.getMultiplayerSessionListener().inAnySession(player)) {
			return;
		}
		player.clickNpcType = 0;
		player.clickedNpcIndex = player.npcClickIndex;
		player.npcClickIndex = 0;

		/*
		 * if(Fishing.fishingNPC(c, npcType)) { Fishing.fishingNPC(c, 1, npcType);
		 * return; }
		 */



		if (PetHandler.isPet(npcType)) {
			if (PetHandler.talktoPet(player, npcType)) {
				return;
			}
		}

		if (NightmareActionHandler.clickNpc(player, npcType, 1)) {
			return;
		}

        NPC npc = getNpcs().get(player.clickedNpcIndex);
		if (player.debugMessage) {
			player.sendErrorMessage("NPC That was chosen is " + npc.getDefinition().getName() + " " + npc.getNpcId());
		}
		NpcDef npcDef = NpcDef.forId(npcType);
		NPCAction action = null;
		NPCAction[] actions = npc.actions;
		if(actions != null)
			action = actions[0];
		if(action == null && (actions = npcDef.defaultActions) != null)
			action = actions[0];
		if(action != null) {
			action.handle(player, npc);
			return;
		}

		switch (npcType) {
			case 155:
				Merchant.openMerchantShop(player);
				break;
			case 611:
				VoteEntriesInstance.handleGlobalSpawn(player);
				break;
			case 547:
				player.getDH().sendDialogues(7287, 547);
				break;
			case 11675:
				player.getPA().showInterface(24960);
				player.sendErrorMessage("We only accept US Phone Numbers!");
				player.sendErrorMessage("All phone numbers but start with +1!");
				player.sendErrorMessage("Phone Number Example: 123-456-7890 (+11234567890)");
				break;

		case 8184:
			player.moveTo(TobConstants.LOBBY_TELEPORT_POSITION);
			break;
			case 4249:
				if (player.IslandTimer > 0) {
					player.moveTo(new Position(2847,5086,0));
				} else {
					player.start(new DialogueBuilder(player).npc(4249, "You don't have anytime left on the island!"));
				}
				break;
		case Npcs.JAMES:
			player.start(new PineAwayDialogue(player, npc));
			break;
		case FarmingTeleport.NPC:
			player.start(new FarmingTeleport(player, npc));
			break;
		case DailyRewardsDialogue.DAILY_REWARDS_NPC:
			int totalReq = (player.getMode().is5x() ? 100 : 500);
			if (player.totalLevel > totalReq) {
				player.start(new DailyRewardsDialogue(player));
			} else {
				player.sendMessage("@red@ You need a total level of 500 to start collecting your daily reward!");
			}
			break;
			case 10529:
				if (!player.getRecentlyDissolvedItems().isEmpty() && !player.getRecentlyDissolvedPrices().isEmpty()) {
					if (player.getRecentlyDissolvedItems().size() > 6 && player.getRecentlyDissolvedPrices().size() > 6) {
						player.getRecentlyDissolvedItems().remove(0);
						player.getRecentlyDissolvedPrices().remove(0);
					}
					for (int i = 0; i < player.getRecentlyDissolvedItems().size(); i++) {
						player.getPA().itemOnInterface(player.getRecentlyDissolvedItems().get(i), 1, 33422+i, 0);
						player.getPA().sendString(33428+i, String.valueOf(Misc.formatCoins(player.getRecentlyDissolvedPrices().get(i))));
					}
				}
				player.getPA().sendString(33415, String.valueOf(Misc.formatCoins(player.totalEarnedExchangePoints)));
				player.getItems().sendItemContainer(33403, Lists.newArrayList(new GameItem(23, 1)));
				player.getPA().sendInterfaceSet(33400, 33404);
				player.getItems().sendInventoryInterface(33405);
				player.getPA().sendFrame126("@red@0", 33409);
				break;
			case 679:
				player.start(new DialogueBuilder(player).npc(679, "I am Rasolo, keeper of everything bloody","check out my wares for something you may be seeking").option("Have any Volatile minerals to exchange?",
						new DialogueOption("Yes", player1 -> {
							int amt = player1.getItems().getInventoryCount(13186);
							if (amt > 0) {
								player1.getItems().deleteItem2(13186, amt);
								player1.getItems().addItem(13307, (amt * 12));
							}
							player1.getPA().closeAllWindows();
						}), new DialogueOption("No", player1 -> player1.getPA().closeAllWindows())));
				break;
			case 3779:
				player.start(new DialogueBuilder(player).npc(3779, "I am Stan, I have access to the latest AOE Materials!","I sometimes offer some amazing deals.").option("Have any Volatile minerals to exchange?",
						new DialogueOption("Yes", player1 -> {
							int amt = player1.getItems().getInventoryCount(13186);
							if (amt > 0) {
								player1.getItems().deleteItem2(13186, amt);
								player1.getItems().addItem(13307, (amt * 12));
							}
							player1.getPA().closeAllWindows();
						}), new DialogueOption("No", player1 -> player1.getPA().closeAllWindows())));
				break;
		case 3400:
			player.getEventCalendar().openCalendar();
			break;
			case 3201:
				player.start(new DialogueBuilder(player).npc(3201, "Welcome to Bloody Minigame.","Coming soon Difficulty modes!"));
				break;

			case 2306:
				player.start(new DialogueBuilder(player).statement("Hello "+player.getDisplayName()+".",
						"Santa has sent me to ask you for his help!", "If you can help santa he's got some presents waiting for you.")
						.option(new DialogueOption("Teleport to the north pole.",
								player1 -> player1.getPA().startTeleport(1951, 6263, 0, "foundry", false)),

								new DialogueOption("Exchange Anti-Santa for snowballs", p -> {
									int amt1 = p.getItems().getInventoryCount(12892);
									int amt2 = p.getItems().getInventoryCount(12893);
									int amt3 = p.getItems().getInventoryCount(12894);
									int amt4 = p.getItems().getInventoryCount(12895);
									int amt5 = p.getItems().getInventoryCount(12896);

									if (amt1 > 0) {
										p.getItems().deleteItem2(12892, amt1);
										p.getItems().addItemUnderAnyCircumstance(10501, 50*amt1);
									}
									if (amt2 > 0) {
										p.getItems().deleteItem2(12893, amt2);
										p.getItems().addItemUnderAnyCircumstance(10501, 50*amt2);
									}
									if (amt3 > 0) {
										p.getItems().deleteItem2(12894, amt3);
										p.getItems().addItemUnderAnyCircumstance(10501, 50*amt3);
									}
									if (amt4 > 0) {
										p.getItems().deleteItem2(12895, amt4);
										p.getItems().addItemUnderAnyCircumstance(10501, 50*amt4);
									}
									if (amt5 > 0) {
										p.getItems().deleteItem2(12896, amt5);
										p.getItems().addItemUnderAnyCircumstance(10501, 50*amt5);
									}

									if (amt1 == 0 && amt2 == 0 && amt3 == 0 && amt4 == 0 && amt5 == 0) {
										p.sendMessage("@red@You don't have any anti-santa piece to exchange!");
									}
									p.getPA().closeAllWindows();
								}),
								new DialogueOption("No sorry.", player1 -> player1.getPA().closeAllWindows())));
				break;

			case 2309:
				player.start(
						new DialogueBuilder(player).statement(
								"Hello "+player.getDisplayName()+".",
								"I'm the Discord Helper, head over to the discord,",
								"Look for Discord-Link channel and type !connect.").exit(player1 -> player1.getPA().closeAllWindows()));
				break;
			case 3444:
//				BloodExchangeBurnPrice.openExchangeRateShop(player);
				break;
			case 5527:
				if (player.getAchievements().hasCompletedAll()) {
					player.start(new DialogueBuilder(player).setNpcId(5527).option("Would you like an achievement cape?",
							new DialogueOption("Yes", plr -> {
								plr.getItems().addItemUnderAnyCircumstance(13069,1);
								plr.getItems().addItemUnderAnyCircumstance(13070,1);
							}), new DialogueOption("No Thanks", plr -> plr.getPA().closeAllWindows())));
				} else {
					player.start(new DialogueBuilder(player).setNpcId(5527).npc("Looks like you've not completed all the achievements,","Come back and see me when you have!").exit(p -> p.getPA().closeAllWindows()));
				}
				break;
		case 2111:
			player.getShops().openShop(176);
			break;
		case 2850:
			GroupIronmanContest.openInterface(player);
			player.sendMessage("Find out more info about the contest at ::topic 281");
			break;
		case 8686:
			player.getShops().openShop(90);
			break;
		case 5513:
			player.getDH().sendDialogues(80, 5513);
			break;
		case 954:
			if (player.getItems().playerHasItem(4207)) {
				player.getDH().sendDialogues(66, 954);
				return;
			}
			player.getDH().sendDialogues(832, 954);
			break;
			case 2149:
			case 2150:
			case 2151:
			case 2148:
				player.getPA().c.itemAssistant.openUpBank();
				break;
		case 5293:
			player.getShops().openShop(175);
			break;
		case 386:
			player.getDH().sendDialogues(782, 386);
			break;
			case 2183:
			case 2184:
			case 2185:
			case 7677:
				player.getDH().sendDialogues(121314, npcType);
			break;
			case 10621:
				player.getDH().sendDialogues(121315, npcType);
				break;
			case 539:
				player.start(new DialogueBuilder(player).statement("Hello traveller, I am Elfinlock The Great!\\n I run the Seasonal Shop, If you're missing a piece\\n" +
						" or have saved up your points, then check out my wares.").option(new DialogueOption("Access the shop", p -> {
					p.getShops().openShop(196);
				}), new DialogueOption("Nevermind.", p -> p.getPA().closeAllWindows())));

				break;
			case 8208:
			PetCollector.petCollectorDialogue(player);
			break;
		case 2662:
			player.getShops().openShop(131);
			player.sendMessage("You currently have @red@" + player.tournamentPoints + " @bla@Tournament Points!");
			break;
		case 3510:
				if (Boundary.isIn(player, Boundary.OUTLAST_AREA) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
						|| Boundary.isIn(player, Boundary.FOREST_OUTLAST)
						|| Boundary.isIn(player, Boundary.SNOW_OUTLAST)
						|| Boundary.isIn(player, Boundary.ROCK_OUTLAST)
						|| Boundary.isIn(player, Boundary.FALLY_OUTLAST)
						|| Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST)
						|| Boundary.isIn(player, Boundary.SWAMP_OUTLAST)
						|| Boundary.isIn(player, Boundary.WG_Boundary)); {
				player.getShops().openShop(147);
			}
			break;
		case 1013:
			player.getWheelOfFortune().open();
			break;
		case 1306:
			if (player.getItems().isWearingItems()) {
				player.sendMessage("You must remove your equipment before changing your appearance.");
				player.canChangeAppearance = false;
			} else {
				player.getPA().showInterface(3559);
				player.canChangeAppearance = true;
			}
			break;
		case 13: // Referral npc
			RefManager.openInterface(player);
			break;
		case 1603:
			player.start(new KolodionDialogue(player));
			break;
		case 555:
			player.getShops().openShop(122);
			player.sendMessage("@red@ You can sell me anything you want!");
			break;
		case 9011:
			player.getShops().openShop(77);
			player.sendMessage("@red@ Please type in ::vote to go to the site. And do ::voted to receive them!");
			player.sendMessage("@red@ Thank you for supporting " + Configuration.SERVER_NAME + "!");
			break;
		case 1011:
			player.getDH().sendNpcChat1(
					"@or2@Gamble 10 firecapes at a chance of an infernal cape.", 1011,
					"Item Gambler");
		    	break;
		case 9168:
			player.getShops().openShop(123);
			break;
		case 4921:
			player.getShops().openShop(124);
			break;
		case 9120:
			if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
				player.getShops().openShop(112);
			} else {
				player.getShops().openShop(9);
			}
			player.sendMessage("Use @red@::mbox @bla@to see possible rewards from the mystery boxes!");
			player.sendMessage("Use @red@::donationrewards @bla@to see donation bonuses!");
			break;
		
		case 1035:
			player.getDH().sendDialogues(300, npcType);
			break;
			case 11247:
				player.start(new DialogueBuilder(player).option("Welcome warrior, what would you like to do?", new DialogueOption("Enter The Inferno.", p -> Inferno.startInferno(p, Inferno.getDefaultWave())),
						new DialogueOption("Gamble Infernal Cape", Inferno::gamble)));
				break;
		case 4407:
			player.getShops().openShop(124);
			break;

		case 5314:
		case 3248:
			TeleportInterface.open(player);
			break;

		case 1143:
			player.getShops().openShop(83);
			break;

		case 1909:
			player.getDH().sendDialogues(900, 1909);
			break;
		case 2989:
			player.start(new DialogueBuilder(player).option("Select an Option",
					new DialogueOption("Open Prestige Manager", p -> {
						p.getPA().closeAllWindows();
						p.getPrestige().openPrestige();
			}), new DialogueOption("Open Prestige Relics", p -> {
						p.getPA().closeAllWindows();
						if (p.prestigeLevel[0] < 1) {
							p.sendMessage("You need to prestige before accessing the relics!");
							return;
						}
						p.getPA().sendString(18913, "Current Tier: " + PrestigePerks.handleTiers(p));
						p.getPA().sendString(18912, "Prestige Points: " + p.prestigePoints);
						p.getPA().showInterface(18910);
					}), new DialogueOption("Open Prestige Shop", p -> {
						p.getPA().closeAllWindows();
						p.getPrestige().openShop();
					})));
			break;
		case 3306:
			player.getDH().sendDialogues(1577, -1);
			break;
		case 7520:
			player.getDH().sendDialogues(850, 7520);
			break;
		/**
		 * Doomsayer
		 */
		case 6773:
			if (!player.pkDistrict) {
				player.sendMessage("You cannot do this right now.");
				return;
			}
			player.getDH().sendDialogues(800, 6773);
			break;
		// Zeah Throw Aways
		case 2200:
			player.getDH().sendDialogues(55873, 2200);
			break;
		case 3189:
			player.getDH().sendDialogues(11929, 3189);
			break;
		case 5998:
			if (player.amDonated <= 1) {
				player.getDH().sendDialogues(5998, 5998);
			} else {
				player.getDH().sendDialogues(5999, 5998);
			}
			break;
		case 4062:
			player.getDH().sendDialogues(55875, 4062);
			break;
		case 4321:
			player.getDH().sendDialogues(145, 4321);
			break;
		case 7041:
			player.getDH().sendDialogues(500, 7041);
			break;
		case 6877:
			player.getDH().sendDialogues(55877, 6877);
			break;
		case 4409:
			player.getDH().sendDialogues(55876, 4409);
			break;
		case 6982:
			player.getDH().sendDialogues(55868, 6982);
			break;
		case 6947:
		case 6948:
		case 6949:
			player.getDH().sendDialogues(55872, 6947);
			break;
		case 6998:
			player.getDH().sendDialogues(55871, 6998);
			break;
		case 7001:
			player.getDH().sendDialogues(55869, 7001);
			break;
		case 6999:
			player.getDH().sendDialogues(55870, 6999);
			break;
		case 6904:
			player.getDH().sendDialogues(55864, 6904);
			break;
		case 6906:
			player.getDH().sendDialogues(55865, 6904);
			break;
		case 6908:
			player.getDH().sendDialogues(55866, 6904);
			break;
		case 6910:
			player.getDH().sendDialogues(55867, 6904);
			break;
		// End Zeah Throw Aways
		case 1503:
			player.getDH().sendDialogues(88393, 1503);
			break;
		case 1504:
			player.getDH().sendDialogues(88394, 1504);
			break;

		case 6774:
			player.getDH().sendDialogues(800, 6773);
			break;


		case 822:
			if (player.getDiaryManager().getWildernessDiary().hasDoneEasy()) {
				player.getDH().sendDialogues(702, 822);
			} else {
				if (player.getItems().playerHasItem(11286) && player.getItems().playerHasItem(1540)
						&& player.getItems().playerHasItem(995, 5_000_000)) {
					player.getItems().deleteItem(11286, 1);
					player.getItems().deleteItem(1540, 1);
					player.getItems().deleteItem(995, 500_000);
					player.getItems().addItem(11283, 1);
					player.votePoints -= 5;
					player.getQuestTab().updateInformationTab();
					player.getDH().sendItemStatement("Oziach successfully bound your dragonfire shield.", 11283);
				} else {
					player.getDH().sendNpcChat("Come back with a shield, visage and 5M Gold!");
				}
			}
			break;
		case 306:
			player.getDH().sendDialogues(710, 306);
			break;
		case 2897:
			if (player.getMode().isIronmanType()) {
				player.sendMessage("@red@You are not permitted to make use of this.");	
				return;
			}
			player.getTradePost().openMyOffers();
			break;
		case 3226:
			if (player.playerLevel[Skill.WOODCUTTING.getId()] >= 90) {
				player.getPA().startTeleport(1589, 3483, 0, "modern", false);
				player.sendMessage("@blu@Welcome to the Woodcutting Guild.");
				return;
			}
			player.sendMessage("@red@You need a woodcutting level of 90 to teleport to WC Guild.");

			break;
		case 7716:
			player.start(new DialogueBuilder(player).setNpcId(7716).option("How can I help you today?",
					new DialogueOption("Open star dust shop.", player1 -> {
						player1.getShops().openShop(191);
			}), new DialogueOption("I would like to go to the amethyst mine", player1 -> {
				if (player1.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player1.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
					player1.getPA().closeAllWindows();
					player1.sendMessage("@red@Wildyman cannot access this area.");
					return;
				}
						if (player.playerLevel[Skill.MINING.getId()] >= 92) {
							player.getPA().startTeleport(3021, 9716, 0, "modern", false);
							player.sendMessage("@blu@Welcome to the Amethyst Mine.");
							player.getPA().closeAllWindows();
							return;
						}
						player.sendMessage("@red@You need a mining level of 92 to teleport to the Amethyst Mine.");
						player.getPA().closeAllWindows();
			})));
			break;
		case 9014:
				player.getPA().startTeleport(3272, 6052, 0, "modern", false);
				player.sendMessage("@blu@Welcome to crystal slayer, use ::crystal for details.");
			break;
		case 7303:
			player.start(new DialogueBuilder(player).npc(npcType,
					"I will trade a full set of clue scrolls for a master one.",
					"Just hand them over."));
			//player.sendMessage("I will trade a full set of clue scrolls with a master one.");
			break;

		case 2914:
			player.getDH().sendNpcChat2("Use Zammy Spear on me to get Hasta, cost 10m", "Use Hasta on me get Zammy Spear, cost 5m", 2914,
					"Otto Godblessed");
			break;

		case 1635:
		case 1636:
		case 1637:
		case 1638:
		case 1639:
		case 1640:
		case 1641:
		case 1642:
		case 1643:
		case 1654:
		case 7302:
			Impling.catchImpling(player, npc);
			break;

		case 17: // Rug merchant 
			player.getDH().sendDialogues(837, 17);
			break;

			case 5792:
				//Premium DZ
				if (player.getStoreDonated() >= 500) {
					player.getPA().startTeleport(2406, 3803, 0, "modern", false);
				} else {
					player.start(new DialogueBuilder(player).npc(5792, "You've not earned the rights to this special area!"));
				}
				break;
			case 2112:
				//Normal DZ
				if (player.getRights().isOrInherits(Right.Donator)) {
					player.getPA().startTeleport(1967, 5365, 0, "modern", false);
				}
				break;
/*		case 5520:
			player.getDiaryManager().getDesertDiary().claimReward();
			break;
		case 5519:
			player.getDiaryManager().getArdougneDiary().claimReward();
			break;
		case 5790:
			player.getDiaryManager().getKaramjaDiary().claimReward();
			break;
		case 5525:
			player.getDiaryManager().getVarrockDiary().claimReward();
			break;
		case 5523:
			player.getDiaryManager().getLumbridgeDraynorDiary().claimReward();
			break;
		case 5524:
			player.getDiaryManager().getFaladorDiary().claimReward();
			break;
		case 5521:
			player.getDiaryManager().getMorytaniaDiary().claimReward();
			break;
		case 5514:
			player.getDiaryManager().getWildernessDiary().claimReward();
			break;
		case 5517:
			player.getDiaryManager().getKandarinDiary().claimReward();
			break;
		case 5526:
			player.getDiaryManager().getFremennikDiary().claimReward();
			break;
		case 5518:
			player.getDiaryManager().getWesternDiary().claimReward();
			break;*/

		case 3936:
			player.getDH().sendNpcChat1("Right click on me and i will take you on-board.", 3936, "Sailor");
			break;

		case 1896:
			if (player.getItems().playerHasItem(995, 30)) {
				player.getItems().deleteItem(995, 30);
				player.getItems().addItem(36, 1);
				player.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.BUY_CANDLE);
			} else {
				player.sendMessage("You need 30 coins to purchase a candle.");
				return;
			}
			break;
			case 9400:
				SuperiorTaskInstance.handleDialogue(player, npc);
				break;
		case 6586:
			player.getDH().sendNpcChat1("No shirt, Sherlock", 6586, "Sherlock");
			player.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.SHERLOCK);
			break;

		case 5036:
			if (player.getItems().playerHasItem(225) || player.getItems().playerHasItem(223)) {
				player.sendMessage("The Apothecary takes your ingredients and creates a strength potion.");
				player.getItems().deleteItem(225, 1);
				player.getItems().deleteItem(223, 1);
				player.getItems().addItem(115, 1);
				player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.APOTHECARY_STRENGTH);
			} else {
				player.sendMessage("You must have limpwurt root and red spiders' eggs to do this.");
				return;
			}
			break;

		case 5906:
			Probita.hasInvalidPet(player);
			break;

		case 3502:
			player.getDH().sendDialogues(64, npcType);
			break;

		case 5870:
			player.getDH().sendDialogues(105, npcType);
			break;

		case 7283:
			player.getDH().sendDialogues(105, npcType);
			break;

		case Npcs.MAC:
			player.start(new MacDialogue(player, true));
			break;

		case 3307: // Combat instructor
			player.getDH().sendDialogues(1390, npcType);
			break;

		case 394:
			player.getDH().sendDialogues(669, npcType);
			break;
		case GroupIronman.GROUP_FORM_NPC:
			player.start(new GroupIronmanDialogue(player));
			break;
		case Npcs.ADAM:
			player.start(new IronmanNpcDialogue(player, npc));
			break;
		case 1159:
			player.start(new MonkChaosAltarDialogue(player, npc));
			break;
		// Noting Npc At Skill Area
		case 905:
			player.talkingNpc = 905;
			player.getDH().sendNpcChat("Hello there, I can note your resources.",
					"I charge @red@25%@bla@ of the yield, this @red@does not apply to donators@bla@.",
					"Use any resource item obtained in this area on me.");
			player.nextChat = -1;
			break;

		case 2040:
			player.getDH().sendDialogues(637, npcType);
			break;

		case 6866:
			player.getShops().openShop(82);
			player.sendMessage("You currently have @red@" + player.getShayPoints() + " @bla@Assault Points!");
			break;

		case 6601:
			NPC golem = npc;
			if (golem != null) {
				player.getMining().mine(golem, Mineral.RUNE,
						new Location3D(golem.getX(), golem.getY(), golem.heightLevel));
			}
			break;
		case 2580:
			player.getDH().sendDialogues(629, npcType);
			break;
		case 4305:
			player.getThieving().doStealNpc(Thieving.Pickpocket.DRUNKEN_DWARF, npc);
			break;
		case 5730:
			if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
				player.getThieving().doStealNpc(Thieving.Pickpocket.FARMER, npc);
				break;
			}
			if (Boundary.isIn(player, Boundary.DRAYNOR_BOUNDARY)) {
				player.getThieving().doStealNpc(Thieving.Pickpocket.FARMER, npc);
				player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.PICKPOCKET_FARMER_DRAY);
				break;
			}
			player.getThieving().doStealNpc(Thieving.Pickpocket.FARMER, npc);
			player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.PICKPOCKET_FARMER_DRAY);
			break;
		case 3894:
			player.getShops().openShop(26);
			break;
		case 3220:
			player.getShops().openShop(25);
			break;
		case 637:
			player.getShops().openShop(6);
			break;
			case 6875:
				player.specRestore = 120;
				player.specAmount = 10.0;
				player.setRunEnergy(100, true);
				player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
				player.playerLevel[5] = player.getPA().getLevelForXP(player.playerXP[5]);
				player.getHealth().removeAllStatuses();
				player.getHealth().reset();
				player.getPA().refreshSkill(5);
				player.getDH().sendItemStatement("Restored your HP, Prayer, Run Energy, and Spec", 4049);
				player.nextChat =  -1;
				break;
			case 732:
			player.getShops().openShop(16);
			break;
		case 8688:
			player.getShops().openShop(113);
			break;
		case 2949:
			player.getPestControlRewards().showInterface();
			break;
		case 2461:
			player.getWarriorsGuild().handleDoor();
			break;
		case 7663:
			player.getDH().sendDialogues(3299, npcType);
			break;
		case 402:// slayer
			if(player.combatLevel<20){
				player.getDH().sendNpcChat2("Do not waste my time peasent.","You need a Combat level of 20.",402,"Mazchna");
				return;
			}
			player.getDH().sendDialogues(3300, npcType);
			break;
		case 401:
			if(player.combatLevel<20){
				player.getDH().sendNpcChat2("Do not waste my time peasent.","You need a Combat level of 20.",402,"Mazchna");
				return;
			}
			player.getDH().sendDialogues(3300, npcType);
			break;
	
		case 405:
			if(player.combatLevel<100){
				player.getDH().sendNpcChat2("Do not waste my time peasent.","You need a Combat level of at least 100.",402,"Duradel");
				return;
			}
			if (player.playerLevel[18] < 50) {
				player.getDH().sendNpcChat1("You must have a slayer level of at least 50 weakling.", 490, "Duradel");
				return;
			}
			player.getDH().sendDialogues(3300, npcType);
			break;
		case 8623:
		case 6797: // Nieve
				player.getDH().sendDialogues(3300, npcType);
			break;
		case 308:
			player.getDH().sendDialogues(550, npcType);
			break;

		case 1308:
			player.getDH().sendDialogues(538, npcType);
			break;
		case Npcs.BOSS_POINT_SHOP:
			player.getShops().openShop(121);
			player.sendMessage("You currently have " + Misc.insertCommas(player.bossPoints) + " Boss points.");
			break;
		case Npcs.PERDU:
			player.getDH().sendDialogues(619, npcType);
			break;
		case 4306:
			player.getShops().openSkillCape();
			break;
		case 1933:
			player.getShops().openMillCape();
			break;
		case 3341:
			player.getDH().sendDialogues(2603, npcType);
			break;
		case 5919:
			if (player.getMode().getType().equals(ModeType.HARDCORE_WILDYMAN) || player.getMode().getType().equals(ModeType.WILDYMAN)) {
				player.sendErrorMessage("Wildymen cannot use Grace!");
				return;
			}
			player.getDH().sendDialogues(14400, npcType);
			break;
		case 603:
			player.getDH().sendDialogues(11955, npcType);
			break;
		case 8761:
			player.getDH().sendDialogues(10955, npcType);
			break;
		case 8605:
			if (player.playerLevel[18] >= 95) {
			player.getDH().sendDialogues(11155, npcType);
			break;
			}
			player.sendMessage("You need a slayer level of 95 to get a task from me.");
			break;
		case 2578:
			player.getDH().sendDialogues(2401, npcType);
			break;
		case 3789:
			player.getShops().openShop(75);
			break;
		// FISHING
			case 7155:
			case 3913: // NET + BAIT
				player.clickNpcType = 1;
				Fishing.attemptdata(player, 1);
				break;
			case 7200: // BASS
				player.clickNpcType = 1;
				Fishing.attemptdata(player, 5);
				break;
		case 3317:
			player.clickNpcType = 1;
			Fishing.attemptdata(player, 14);
			break;
		case 4712:
			player.clickNpcType = 1;
			Fishing.attemptdata(player, 15);
			break;
		case 1524:
			player.clickNpcType = 1;
			Fishing.attemptdata(player, 11);
			break;
		case 3417: // TROUT
			case 7468:
			case 1507:
			case 1506:
			player.clickNpcType = 1;
			Fishing.attemptdata(player, 4);
			break;
		case 3657:
			player.clickNpcType = 1;
			Fishing.attemptdata(player, 8);
			break;
		case 635:
			Fishing.attemptdata(player, 13); // DARK CRAB FISHING
			break;
		case 6825: // Anglerfish
			player.clickNpcType = 1;
			Fishing.attemptdata(player, 16);
			break;
			case 1531: //lava eel
				Fishing.attemptdata(player, 17);
				break;
		case 1520: // LURE
		case 310:
		case 314:
		case 317:
		case 318:
		case 328:
		case 331:
			Fishing.attemptdata(player, 9);
			break;

		case 944:
			player.getDH().sendOption5("Hill Giants", "Hellhounds", "Lesser Demons", "Chaos Dwarf", "-- Next Page --");
			player.teleAction = 7;
			break;

		case 559:
			player.getShops().openShop(16);
			break;
		case 5809:
			Tanning.sendTanningInterface(player);
			break;

		case 2913:
			player.getShops().openShop(22);
			break;
		case 403:
			player.getDH().sendDialogues(2300, npcType);
			break;
		case 1599:
			break;

		case 953: // Banker
		case 2574: // Banker
		case 166: // Gnome Banker
		case 1702: // Ghost Banker
		case 494: // Banker
		case 495: // Banker
		case 496: // Banker
		case 497: // Banker
		case 498: // Banker
		case 499: // Banker
		case 567: // Banker
		case 766: // Banker
		case 1036: // Banker
		case 1360: // Banker
		case 2163: // Banker
		case 2164: // Banker
		case 2354: // Banker
		case 2355: // Banker
		case 2568: // Banker
		case 2569: // Banker
		case 2570: // Banker
			player.getPA().c.itemAssistant.openUpBank();
			break;
		case 1986:
			player.getDH().sendDialogues(2244, player.npcType);
			break;
		case 1576:
			player.getShops().openShop(4);
			break;
		case 1577:
			player.getShops().openShop(8);
			break;
		case 1578:
			player.getShops().openShop(6);
			break;

		case 6747:
			player.getShops().openShop(77);
			break;
		case 1785:
			player.getShops().openShop(8);
			break;

		case 1860:
			player.getShops().openShop(47);
			break;

		case 519:
			player.getShops().openShop(48);
			break;

		case 548:
			player.getDH().sendDialogues(69, player.npcType);
			break;

		case 2258:
			player.getDH().sendOption2("Teleport me to Runecrafting Abyss.", "I want to stay here, thanks.");
			player.dialogueAction = 2258;
			break;

		case 532:
			player.getShops().openShop(47);
			break;


		case 7913:
			if (player.getMode().isIronmanType()
					|| player.getRights().isOrInherits(Right.GAME_DEVELOPER)) {
				player.getShops().openShop(41);
			} else {
				player.sendMessage("You must be an Iron Man to access this shop.");
			}
			break;
		case 7769:
			player.getShops().openShop(2);
			break;

		

		/*
		 * case 198: c.getShops().openSkillCape(); break;
		 */

		/**
		 * Make over mage.
		 */

		}
	}

}
