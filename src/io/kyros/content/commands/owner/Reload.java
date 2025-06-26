package io.kyros.content.commands.owner;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.afkzone.AfkBoss;
import io.kyros.content.battlepass.Rewards;
import io.kyros.content.bosses.nightmare.NightmareStatusNPC;
import io.kyros.content.bosses.sarachnis.SarachnisNpc;
import io.kyros.content.bosses.sharathteerk.SharItems;
import io.kyros.content.combat.HitMask;
import io.kyros.content.commands.Command;
import io.kyros.content.commands.all.Centcode;
import io.kyros.content.customs.CustomItemHandler;
import io.kyros.content.dailyrewards.DailyRewardContainer;
import io.kyros.content.deals.BonusItems;
import io.kyros.content.donor.DonorVault;
import io.kyros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.kyros.content.games.goodiebag.CardLoader;
import io.kyros.content.games.goodiebag.GoodieBag;
import io.kyros.content.item.lootable.LootManager;
import io.kyros.content.item.lootable.LootableInterface;
import io.kyros.content.minigames.coinflip.CoinFlip;
import io.kyros.content.minigames.coinflip.CoinFlipJson;
import io.kyros.content.minigames.donationgames.TreasureGameHandler;
import io.kyros.content.npchandling.ForcedChat;
import io.kyros.content.referral.ReferralCode;
import io.kyros.content.skills.Minigame;
import io.kyros.content.skills.runecrafting.ouriana.ZamorakGuardian;
import io.kyros.content.vote_panel.VotePanelManager;
import io.kyros.content.wogw.Wogw;
import io.kyros.content.ytmanager.YTManager;
import io.kyros.model.Npcs;
import io.kyros.model.collisionmap.doors.DoorDefinition;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.definitions.ItemStats;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.definitions.ShopDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.npc.NpcSpawnLoader;
import io.kyros.model.entity.npc.NpcSpawnLoaderOSRS;
import io.kyros.model.entity.npc.actions.CustomActions;
import io.kyros.model.entity.npc.stats.NpcCombatDefinition;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.world.ShopHandler;
import io.kyros.script.PluginManager;
import io.kyros.sql.dailytracker.DailyDataTracker;
import io.kyros.sql.refsystem.RefManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Reloading certain objects by {String input}
 *
 * @author Matt
 */

public class Reload extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		switch (input) {
			case "scripts":
				PluginManager.start();
				break;
			case "scan":
				Server.getNpcs().forEach(npc -> {
					if (npc.getPosition().withinDistance(player.getPosition(), 15)) {
						player.sendErrorMessage("Null npc ? " + npc.getNpcId());
						npc.appendDamage(npc.getHealth().getMaximumHealth(), HitMask.HIT);
						npc.unregisterInstant();
					}
				});
				break;
			case "crier":
				ForcedChat.loadChats();
				player.sendErrorMessage("You have reloaded all forced chats!");
				break;
			case "gencentcodes":
				Centcode.GenCentCode();
				break;
			case "clearcentclaims":
				Centcode.centClaims.clear();
				break;
			case "":
				player.sendMessage("@red@Usage: ::reload doors, drops, items, objects, shops or npcs");
				break;
			case "customactions":
				CustomActions.loadActions();
				break;
			case "bonusitems":
				BonusItems.load();
				player.sendErrorMessage("You have reloaded bonus offers!");
				PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255> New bonus items have been added to ::deals!");
				break;

			case "bpitems":
				Rewards.generateRewards();
				return;

			case "cleartrail":
				AfkBoss.IPAddress.clear();
				AfkBoss.MACAddress.clear();
				player.sendErrorMessage("You have cleared the trail ip logger!");
				break;

			case "boxes":
				LootManager.BoxReloader();
				return;

			case "lootable":
				LootableInterface.lootableReload();
				player.sendErrorMessage("Reloaded lootable view items");
				break;

			case "coinflip":
				try {
					CoinFlipJson.loadJson();
				} catch (IOException e) {
					player.sendMessage("Error loading coinflip data, check the server output!");
					e.printStackTrace();
				}
				break;

			case "dailyrewards":
				try {
					DailyRewardContainer.load();
					player.sendMessage("Loaded daily rewards.");
				} catch (Exception e) {
					player.sendMessage("Error loading daily rewards, check the server output!");
					e.printStackTrace();
				}
				break;

			case "referralcodes":
				try {
//					ReferralCode.load();
					RefManager.loadReferralRewards();
					player.sendMessage("Loaded referral codes.");
				} catch (Exception e) {
					player.sendMessage("Error loading referrals, check the server output!");
					e.printStackTrace();
				}
				break;

			case "store":
//				io.kyros.sql.ingamestore.Configuration.loadConfiguration();
				break;

			case "doors":
				try {
					DoorDefinition.load();
					player.sendMessage("@blu@Reloaded Doors.");
				} catch (IOException e) {
					e.printStackTrace();
					player.sendMessage("@blu@Unable to reload doors, check console.");
				}
				break;

			case "drops":
				try {
					Server.getDropManager().read();
					player.sendMessage("@blu@Reloaded Drops.");
				} catch (Exception e) {
					player.sendMessage("@red@Error reloading drops!");
					e.printStackTrace();
				}

				break;


			case "enableemails":
				DailyDataTracker.ENABLED = true;
				player.sendErrorMessage("Daily DataTracker is " + DailyDataTracker.ENABLED);
				break;

			case "items":
				try {
					ItemDef.load();
					ItemStats.load();
					CustomItemHandler.handleCustomItem();
					DonorVault.handleStatics();
					CoinFlip.itemActionHandler();
					Minigame.handleItem();
					SharItems.RegisterItems();
					TreasureGameHandler.handleItemAction();
					player.sendMessage("@blu@Reloaded Items.");
				} catch (Exception e) {
					player.sendMessage("@blu@Unable to reload items, check console.");
					e.printStackTrace();
				}
				break;

			case "wogw":
				Wogw.init();
				break;
			case "objects":
				try {
					Server.getGlobalObjects().reloadObjectFile(player);
					player.sendMessage("@blu@Reloaded Objects.");
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case "goodiebags":
				CardLoader.loadCards();
				player.sendErrorMessage("Reloaded GoodieBags.");
				break;

			case "shops":
				try {
					FireOfExchangeBurnPrice.createBurnPriceShop();
					Server.shopHandler = new ShopHandler();
					ShopDef.load();
					ShopHandler.load();
					player.sendMessage("@blu@Reloaded Shops");
				} catch (Exception e) {
					player.sendMessage("Error occurred, check console.");
					e.printStackTrace();
				}
				break;

			case "combatdata":
				try {
					NpcDef.clearLoadedDefs();
					NpcCombatDefinition.clearLoadedDefs();

					NpcDef.load(); player.sendMessage("@blu@Npc Definition  have been reloaded!");
					NpcCombatDefinition.load(); player.sendMessage("@blu@Npc Combat Definitions have been reloaded!");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				break;

			case "unloadnpcs":
				for (NPC npc : Server.getNpcs()) {
					if (npc == null)
						continue;
					npc.unregisterInstant();
				}
				NpcDef.clearLoadedDefs();
				NpcCombatDefinition.clearLoadedDefs();
				player.sendMessage("@red@All npc's have been unloaded!");
				break;

			case "loadnpcs":
				try {
					NpcDef.load(); player.sendMessage("@blu@Npc Definition  have been reloaded!");

					NpcCombatDefinition.load(); player.sendMessage("@blu@Npc Combat Definitions have been reloaded!");

					NpcSpawnLoader.load(); player.sendMessage("@blu@Old Spawns have been reloaded!");
					NpcSpawnLoaderOSRS.initOsrsSpawns(); player.sendMessage("@blu@Deob Osrs Spawns have been reloaded!");


					NightmareStatusNPC.init(); player.sendMessage("@blu@Nightmares Status npc has been reloaded!");

					ZamorakGuardian.spawn(); player.sendMessage("@blu@Zamorak guardian has been spawned!?");
					new SarachnisNpc(Npcs.SARACHNIS, SarachnisNpc.SPAWN_POSITION); player.sendMessage("@blu@Sarachnis has been spawned!?");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				player.sendMessage("@blu@Reloaded NPCs");
				break;

			case "npcs":
				try {
					for (NPC npc : Server.getNpcs()) {
						if (npc == null)
							continue;
						npc.unregisterInstant();
					}
					NpcDef.clearLoadedDefs();
					NpcCombatDefinition.clearLoadedDefs();
					player.sendMessage("@red@All npc's have been unloaded!");
				} finally {
					try {
						NpcDef.load(); player.sendMessage("@blu@Npc Definition  have been reloaded!");

						NpcCombatDefinition.load(); player.sendMessage("@blu@Npc Combat Definitions have been reloaded!");

						NpcSpawnLoader.load(); player.sendMessage("@blu@Old Spawns have been reloaded!");
						NpcSpawnLoaderOSRS.initOsrsSpawns(); player.sendMessage("@blu@Deob Osrs Spawns have been reloaded!");


						NightmareStatusNPC.init(); player.sendMessage("@blu@Nightmares Status npc has been reloaded!");

						ZamorakGuardian.spawn(); player.sendMessage("@blu@Zamorak guardian has been spawned!?");
						new SarachnisNpc(Npcs.SARACHNIS, SarachnisNpc.SPAWN_POSITION); player.sendMessage("@blu@Sarachnis has been spawned!?");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}

                player.sendMessage("@blu@Reloaded NPCs");
				break;

			case "votes" :
				VotePanelManager.init();
				player.sendMessage("@blu@Reloaded Votes");
				break;

			case "punishments":
				try {
					Server.getPunishments().initialize();
					player.sendMessage("@blu@Reloaded Punishments.");
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case "fixdbfuck":
				Server.loadSqlNetwork();
				break;

			case "smsrefs":
				Server.getServerAttributes().setHashSet("referalls_map", new HashSet<String>());
				Server.getServerAttributes().setList("referalls", new ArrayList());
				try {
					ReferralCode.load();
					player.sendErrorMessage("Reloaded SMS refs!");
				} catch (IOException e) {
					player.sendErrorMessage("Error reloading sms refs");
				}
				break;

			case "ytvideos":
				YTManager.videos.clear();
				break;

			case "centcodes":
				Centcode.init();
				System.out.println("Loaded CentCode: " + Server.CentCode);
				System.out.println("Loaded JrCentCode: " + Server.jrCentCode);
				break;


			case "youtube":
				YTManager.videos.clear();
				break;

			case "looting":
				Configuration.BAG_AND_POUCH_PERMITTED = !Configuration.BAG_AND_POUCH_PERMITTED;
				player.sendMessage(""+(Configuration.BAG_AND_POUCH_PERMITTED ? "Enabled" : "Disabled" +"") + " bag and pouch.");
				break;

			case "tp":

				break;

		}
	}

}
