package io.kyros.content.collection_log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.bosses.nightmare.NightmareConstants;
import io.kyros.content.item.lootable.other.RaidsChestRare;
import io.kyros.content.item.lootable.impl.*;
import io.kyros.content.items.aoeweapons.AoeWeapons;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeConstants;
import io.kyros.content.trails.RewardLevel;
import io.kyros.content.trails.TreasureTrailsRewardItem;
import io.kyros.content.trails.TreasureTrailsRewards;
import io.kyros.content.upgrade.UpgradeMaterials;
import io.kyros.model.Npcs;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.group.GroupIronmanGroup;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * @author Grant_ | www.rune-server.ee/members/grant_ | 10/7/19
 *
 */
public class CollectionLog {

	private static final Logger logger = LoggerFactory.getLogger(CollectionLog.class);

	public static final int PETS_ID = 5;

	/**
	 * Different tabs within interface
	 *
	 */
	public enum CollectionTabType {
		BOSSES, WILDERNESS, RAIDS, MINIGAMES, OTHER
	}

	/* Variables */
	public static HashMap<CollectionTabType, ArrayList<Integer>> collectionNPCS;
	private static final int INTERFACE_ID = 23110;

	private boolean groupIronman;
	private String saveName;
	private CollectionLog linked;

	private HashMap<String, ArrayList<GameItem>> collections;

	public CollectionLog() {
		this.collections = new HashMap<>();
	}

	public String getSaveDirectory() {
		if (isGroupIronman()) {
			return Server.getSaveDirectory() + "/gim/collection_log/";
		}
		return Server.getSaveDirectory() + "/collection_log/";
	}

	public HashMap<String, ArrayList<GameItem>> getCollections() {
		return collections;
	}

	/**
	 * Initializes the default npcs to be collecting for
	 */
	public static void init() {
		try {
			Path path = Paths.get(Server.getDataDirectory() + "/cfg/collection_npcs.json");
			File file = path.toFile();

			JsonParser parser = new JsonParser();
			if (!file.exists()) {
				return;
			}
			Object obj = parser.parse(new FileReader(file));
			JsonObject jsonUpdates = (JsonObject) obj;

			Type listType = new TypeToken<HashMap<CollectionTabType, ArrayList<Integer>>>() {
			}.getType();

			collectionNPCS = new Gson().fromJson(jsonUpdates, listType);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No default NPCs found!");
			collectionNPCS = new HashMap<>();
		}
	}

	/**
	 * Opens the interface for a player
	 */
	public void openInterface(Player c) {
		if (c.isBoundaryRestricted()) {
			return;
		}

		c.setViewingCollectionLog(this);
		resetInterface(c);
		selectTab(c, CollectionTabType.BOSSES);
		//selectCell(0, CollectionTabType.BOSSES);
	}

	public void openInterfaceOther(Player player, Player c2) {
		player.setViewingCollectionLog(c2.getCollectionLog());
		resetInterface(player);
		selectTab(player, CollectionTabType.BOSSES);
		//selectCell(0, CollectionTabType.BOSSES);
	}

	/**
	 * Clears the interface
	 */
	public void resetInterface(Player player) {
		for(int i = 0; i < 50; i++) {
			player.getPA().sendFrame126("", 23123 + (i * 2));
			player.getPA().sendConfig(520 + i, 0);
		}
		player.getPA().sendConfig(519, 0);
		for(int i = 0; i < 3; i++) {
			player.getPA().sendConfig(571 + i, 0);
		}
	}

	/**
	 * Selects a tab within the interface
	 * @param type
	 */
	public void selectTab(Player player, CollectionTabType type) {
		if (collectionNPCS == null || collectionNPCS.isEmpty()) {
			return;
		}

		ArrayList<Integer> npcs = collectionNPCS.get(type);
		if (npcs != null) {
			resetInterface(player);
			player.collectionLogTab = type;
			player.previousSelectedCell = 0;
			player.getPA().sendConfig(player.previousSelectedTab == 0 ? 519 : 570 + player.previousSelectedTab, 0);
			player.previousSelectedTab = type.ordinal();
			player.getPA().sendConfig(type.ordinal() == 0 ? 519 : 570 + type.ordinal(), 1);
			for(int i = 0; i < npcs.size(); i++) {
				boolean found = false;
				if (getCollections().containsKey(npcs.get(i) + "")) {
					ArrayList<GameItem> itemsObtained = getCollections().get(npcs.get(i) + "");
					if (itemsObtained != null) {
						List<GameItem> drops = Server.getDropManager().getNPCdrops(npcs.get(i));
/*						if (npcs.get(i) == 8028) {
							drops = Vorkath.getVeryRareDrops();
						}*/
						if (npcs.get(i) == 7554) {
							drops = RaidsChestRare.getRareDrops();
						} else if (npcs.get(i) >= 1 && npcs.get(i) <= 4) {
							drops = TreasureTrailsRewardItem.toGameItems(TreasureTrailsRewards.getRewardsForType(npcs.get(i)));
						} else if (npcs.get(i) == PETS_ID) {
							drops = PetHandler.getPetIds(true);
						} else if (npcs.get(i) == 6) {
							for (UpgradeMaterials value : UpgradeMaterials.values()) {
								if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.WEAPON)) {
									drops.add(value.getReward());
								}
							}
						} else if (npcs.get(i) == 7) {
							for (UpgradeMaterials value : UpgradeMaterials.values()) {
								if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ARMOUR)) {
									drops.add(value.getReward());
								}
							}
						} else if (npcs.get(i) == 8) {
							for (UpgradeMaterials value : UpgradeMaterials.values()) {
								if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ACCESSORY)) {
									drops.add(value.getReward());
								}
							}
						} else if (npcs.get(i) == 9) {
							for (UpgradeMaterials value : UpgradeMaterials.values()) {
								if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.MISC)) {
									drops.add(value.getReward());
								}
							}
						} else if (npcs.get(i) == 10) {
							for (AoeWeapons value : AoeWeapons.values()) {
								drops.add(new GameItem(value.ID, 1));
							}
						} else if (npcs.get(i) == Npcs.THE_MAIDEN_OF_SUGADINTI) {
							drops = TheatreOfBloodChest.getRareDrops();

						} else if (npcs.get(i) == 1101) {
							drops = ArbograveChestItems.getRareDrops();
							
						} else if (npcs.get(i) == 8583) {
							drops = ShadowCrusadeChestItems.getRareDrops();

						} else if (npcs.get(i) == 13527) {
							drops = ShadowCrusadeChestItems.getRareDrops();

						} else if (npcs.get(i) == 853) {
							drops = DamnedChestItems.getRareDrops();
							drops.addAll(DamnedChestItems.getVeryRareDrops());
						}

						if (drops != null && drops.size() == itemsObtained.size()) {
							found = true;
							boolean hasName = false;

							String name = "";

							if (npcs.get(i) == Npcs.THE_MAIDEN_OF_SUGADINTI) {
								name = "Theatre of Blood";
								hasName = true;
							} else if (npcs.get(i) == 1101) {
								name = "Arbograve Swamp";
								hasName = true;
							} else if (npcs.get(i) == 13527) {
								name = "Shadow Crusade";
								hasName = true;
							} else if (npcs.get(i) == Npcs.DUSK_9) {
								name = "Grotesque Guardians";
								hasName = true;
							} else if (npcs.get(i) == PETS_ID) {
								name = "Pets";
								hasName = true;
							} else if (npcs.get(i) == 6) {
								name = "Weapon Upgrades";
								hasName = true;
							} else if (npcs.get(i) == 7) {
								name = "Armor Upgrades";
								hasName = true;
							} else if (npcs.get(i) == 8) {
								name = "Accessory Upgrades";
								hasName = true;
							} else if (npcs.get(i) == 9) {
								name = "Misc Upgrades";
								hasName = true;
							} else if (npcs.get(i) == 10) {
								name = "Aoe Weapons";
								hasName = true;
							} else if (npcs.get(i) == 1230) {
								name = "Perkfinder Minigame";
								hasName = true;
							} else if (npcs.get(i) == 8583) {
								name = "Hespori";
								hasName = true;
							} else if (npcs.get(i) == 853) {
								name = "Isle of The Damned";
								hasName = true;
							}

							if (!hasName) {
								if (type == CollectionTabType.OTHER && i <= 5) {
									name = Misc.optimizeText(RewardLevel.VALUES.get(npcs.get(i)).name().toLowerCase());
								} else {
									name = Misc.optimizeText(NpcDef.forId(npcs.get(i)).getName());
								}
							}

/*							if (!player.getCollogrewards().containsValue(i)) {
								player.getCollogrewards().put(type, i);
								player.setCollectionPoints(player.getCollectionPoints() + getPoints(type,i));
								player.sendMessage("You have completed a log and earned " + getPoints(type,i) + "collog points.");
							}*/
							player.getPA().sendFrame126("@gre@" + name, 23123 + (i * 2));
						}
					}
				}
				if (!found) {
					if (npcs.get(i) == PETS_ID) {
						player.getPA().sendFrame126("Pets", 23123 + (i * 2));
					} else if (npcs.get(i) == 6) {
						player.getPA().sendFrame126("Weapon Upgrades", 23123 + (i * 2));
					} else if (npcs.get(i) == 7) {
						player.getPA().sendFrame126("Armor Upgrades", 23123 + (i * 2));
					} else if (npcs.get(i) == 8) {
						player.getPA().sendFrame126("Accessory Upgrades", 23123 + (i * 2));
					} else if (npcs.get(i) == 9) {
						player.getPA().sendFrame126("Misc Upgrades", 23123 + (i * 2));
					} else if (npcs.get(i) == 10) {
						player.getPA().sendFrame126("Aoe Weapons", 23123 + (i * 2));
					} else {
						String name = type == CollectionTabType.OTHER ? RewardLevel.VALUES.get(npcs.get(i)).getFormattedName() + " clue scroll"
								: Misc.optimizeText(NpcDef.forId(npcs.get(i)).getName());
						if (npcs.get(i) == Npcs.THE_MAIDEN_OF_SUGADINTI) {
							name = "Theatre of Blood";
						} else if (npcs.get(i) == 1101) {
							name = "Arbograve Swamp";
						} else if (npcs.get(i) == 13527) {
							name = "Shadow Crusade";
						} else if (npcs.get(i) == Npcs.DUSK_9) {
							name = "Grotesque Guardians";
						} else if (npcs.get(i) == 1230) {
							name = "Perkfinder Minigame";
						} else if (npcs.get(i) == 8583) {
							name = "Hespori";
						} else if (npcs.get(i) == 853) {
							name = "Isle Of The Damned";
						}
						player.getPA().sendFrame126(name, 23123 + (i * 2));
					}
				}
			}
			selectCell(player, 0, type);
		} else {
			player.sendMessage("There are no collection logs for this type yet.");
		}
	}

	/**
	 * Selects a cell from a tab type
	 * @param index
	 * @param type
	 */
	public void selectCell(Player player, int index, CollectionTabType type) {
		if (collectionNPCS == null || collectionNPCS.isEmpty()) {
			return;
		}

		ArrayList<Integer> npcs = collectionNPCS.get(type);
		if (npcs != null) {
			if (index >= npcs.size()) {
				return;
			}

			player.getPA().sendConfig(520 + player.previousSelectedCell, 0);
			player.previousSelectedCell = index;
			player.getPA().sendConfig(520 + index , 1);
			player.getPA().resetScrollBar(23121);

			if (npcs.get(index) == PETS_ID) {
				List<GameItem> pets = PetHandler.getPetIds(false);
				for(GameItem petItem : pets) {
					if (player.getItems().getItemCount(petItem.getId(), false) > 0 || (player.hasFollower && player.petSummonId == petItem.getId())) {
						PetHandler.Pets petForItem = PetHandler.forItem(petItem.getId());
						if (petForItem != null) {
							PetHandler.Pets pet = PetHandler.getPetForParentId(petForItem);
							ArrayList<GameItem> petList = getCollections().get("" + 5);
							if (petList == null || petList.stream().noneMatch(item -> item.getId() == pet.getItemId())) {
								player.getCollectionLog().handleDrop(player, 5, pet.getItemId(), 1);
								player.sendMessage("@red@Added missing " + ItemDef.forId(pet.getItemId()).getName() + " to collection log.");
							}
						}
					}
				}
			}
			populateInterface(player, npcs.get(index));
		}
	}

	/**
	 * Populates the interface with data
	 * @param npcId
	 */
	public void populateInterface(Player player, int npcId) {
		if (!getCollections().containsKey("" + npcId)) { //If they've never looked at that NPC before, initialize a blank arraylist
			getCollections().put("" + npcId, new ArrayList<>());
			saveToJSON();
		}

		player.setCollectionLogNPC(npcId);

		String npcName = NpcDef.forId(npcId).getName();
		if (npcId >= 1 && npcId <= 4) {
			npcName = Misc.optimizeText(RewardLevel.VALUES.get(npcId).name().toLowerCase());
		}
		if (npcId == PETS_ID) {
			npcName = "Pets";
		}
		if (npcId == 6) {
			npcName = "Weapon Upgrades";
		}
		if (npcId == 7) {
			npcName = "Armor Upgrades";
		}
		if (npcId == 8) {
			npcName = "Accessory Upgrades";
		}
		if (npcId == 9) {
			npcName = "Misc Upgrades";
		}
		if (npcId == 10) {
			npcName = "Aoe Weapons";
		}
		if (npcId == Npcs.THE_MAIDEN_OF_SUGADINTI) {
			npcName = "Theatre of Blood";
		}
		if (npcId == 1101) {
			npcName = "Arbograve Swamp";
		}
		if (npcId == 13527) {
			npcName = "Shadow Crusade";
		}
		if (npcId == Npcs.DUSK_9) {
			npcName = "Grotesque Guardians";
		}
		if (npcId == 1230) {
			npcName = "Perkfinder Minigame";
		}
		if (npcId == 8583) {
			npcName = "Hespori";
		}
		if (npcId == 853) {
			npcName = "Isle Of The Damned";
		}

		player.getPA().sendFrame126(getSaveName() + "'s Collection Log", 23112);
		player.getPA().sendFrame126(Misc.optimizeText(npcName) /*+ "@gre@("+getPoints(npcId)+" Credits)"*/, 23118);
		player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + player.getNpcDeathTracker().getKc(npcName), 23120);

		//Clear items
		for(int i = 0; i < 198; i++) {
			player.getPA().itemOnInterface(-1, 0, 23231, i);
		}

		for (int i = 0; i < 5; i++) {
			player.getPA().itemOnInterface(new GameItem(-1,0), 23235, i);
		}

		for (int i = 0; i < CollectionRewards.getForNpcID(npcId).size(); i++) {
			player.getPA().itemOnInterface(new GameItem(CollectionRewards.getForNpcID(npcId).get(i).getId(),CollectionRewards.getForNpcID(npcId).get(i).getAmount()), 23235, i);
		}

		ArrayList<GameItem> items = getCollections().get(npcId + "");

		Server.getDropManager().getDrops(player, npcId);
/*		if (npcId == 8028) {
			player.dropItems = Vorkath.getVeryRareDrops();
		}*/
		if (npcId == 7554) {
			player.dropItems = RaidsChestRare.getRareDrops();
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + player.raidCount, 23120);
		}
		if (npcId >= 1 && npcId <= 4) {
			player.dropItems = TreasureTrailsRewardItem.toGameItems(TreasureTrailsRewards.getRewardsForType(npcId));
		}
		if (npcId == PETS_ID) {
			player.dropItems = PetHandler.getPetIds(true);
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + items.size(), 23120);
		}
		if (npcId == 6) {
			if (!player.dropItems.isEmpty()) {
				player.dropItems.clear();
			}
			for (UpgradeMaterials value : UpgradeMaterials.values()) {
				if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.WEAPON)) {
					player.dropItems.add(value.getReward());
				}
			}
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + items.size(), 23120);
		}
		if (npcId == 7) {
			if (!player.dropItems.isEmpty()) {
				player.dropItems.clear();
			}
			for (UpgradeMaterials value : UpgradeMaterials.values()) {
				if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ARMOUR)) {
					player.dropItems.add(value.getReward());
				}
			}
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + items.size(), 23120);
		}
		if (npcId == 8) {
			if (!player.dropItems.isEmpty()) {
				player.dropItems.clear();
			}
			for (UpgradeMaterials value : UpgradeMaterials.values()) {
				if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ACCESSORY)) {
					player.dropItems.add(value.getReward());
				}
			}
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + items.size(), 23120);
		}
		if (npcId == 9) {
			if (!player.dropItems.isEmpty()) {
				player.dropItems.clear();
			}
			for (UpgradeMaterials value : UpgradeMaterials.values()) {
				if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.MISC)) {
					player.dropItems.add(value.getReward());
				}
			}
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + items.size(), 23120);
		}

		if (npcId == 10) {
			if (!player.dropItems.isEmpty()) {
				player.dropItems.clear();
			}
			for (AoeWeapons value : AoeWeapons.values()) {
				player.dropItems.add(new GameItem(value.ID,1));
			}
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + items.size(), 23120);
		}
		if (npcId == Npcs.THE_MAIDEN_OF_SUGADINTI) {
			player.dropItems = TheatreOfBloodChest.getRareDrops();
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + player.tobCompletions, 23120);
		}
		if (npcId == 1101) {
			player.dropItems = ArbograveChestItems.getRareDrops();
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + player.arboCompletions, 23120);
		}
		if (npcId == 13527) {
			player.dropItems = ShadowCrusadeChestItems.getRareDrops();
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + player.shadowCrusadeCompletions, 23120);
		}
		if (npcId == 853) {
			player.dropItems = DamnedChestItems.getRareDrops();
            player.dropItems.addAll(DamnedChestItems.getVeryRareDrops());
			player.getPA().sendFrame126(Misc.optimizeText(npcName) + ": @whi@" + player.damnedCompletions, 23120);
		}
		if (npcId == 8583) {
			player.dropItems = HesporiChestItems.getRareDrops();
		}

		int foundCount = 0;
		for(int i = 0; i < player.dropItems.size(); i++) {
			boolean found = false;
			for(int j = 0; j < items.size(); j++) {
				if (items.get(j).getId() == player.dropItems.get(i).getId()) {
					player.getPA().itemOnInterface(items.get(j).getId(),items.get(j).getAmount(),23231,i);
					foundCount++;
					found = true;
					break;
				}
			}
			if (!found) {
				player.getPA().itemOnInterface(player.dropItems.get(i).getId(),0,23231,i);
			}
		}
		player.getPA().sendFrame126("Obtained: " + (foundCount == player.dropItems.size() ? "@gre@" : "@red@") + foundCount + "/" + player.dropItems.size(), 23119);
		player.getPA().showInterface(INTERFACE_ID);
	}

	public void handleDrop(Player player, int npcId, int dropId, int dropAmount) {
		handleDrop(player, npcId, dropId, dropAmount, true);
	}

	/**
	 * Handles and NPC dropping an item
	 * @param npcId
	 * @param dropId
	 * @param dropAmount
	 */
	public void handleDrop(Player player, int npcId, int dropId, int dropAmount, boolean message) {
		if (linked != null) {
			linked.handleDrop(player, npcId, dropId, dropAmount, false);
		}

		if (npcId == 2043 || npcId == 2044) { //All zulrahs
			npcId = 2042;
		}
		if (npcId == 965) {
			npcId = 963;
		}
		if (npcId == 963) {
			npcId = 965;
		}
		if (npcId == 7144  || npcId == 7146) {
			npcId = 7145;
		}

		if (npcId == 8615 || npcId == 8619 || npcId == 8620 || npcId == 8622) {
			npcId = 8621;
		}

		if (npcId == 7851) {
			npcId = 7888;
		}
		if (npcId == 1233 || npcId == 1234 || npcId == 1235 || npcId == 1230
				||npcId == 1231  || npcId == 1232 || npcId == 1227 || npcId == 1228
				||npcId == 1229 ) {
			npcId = 1230;
		}

		//Pets
		if (npcId == PETS_ID) {
			dropId = PetHandler.getPetForParentId(PetHandler.forItem(dropId)).getItemId();
		}

		String npcName = NpcDef.forId(npcId).getName();
		if (npcId >= 1 && npcId <= 4) {
			npcName = Misc.optimizeText(RewardLevel.VALUES.get(npcId).name().toLowerCase());
		}
		if (npcId == PETS_ID) {
			npcName = "Pets";
		}
		if (npcId == 6) {
			npcName = "Weapon Upgrades";
		}
		if (npcId == 7) {
			npcName = "Armor Upgrades";
		}
		if (npcId == 8) {
			npcName = "Accessory Upgrades";
		}
		if (npcId == 9) {
			npcName = "Misc Upgrades";
		}
		if (npcId == 10) {
			npcName = "Aoe Weapons";
		}
		if (npcId == Npcs.THE_MAIDEN_OF_SUGADINTI) {
			npcName = "Theatre of Blood";
		}
		if (npcId == 1101) {
			npcName = "Arbograve Swamp";
		}
		if (npcId == 13527) {
			npcName = "Shadow Crusade";
		}
		if (npcId == Npcs.DUSK_9) {
			npcName = "Grotesque Guardians";
		}
		if (npcId == 1230) {
			npcName = "Perkfinder Minigame";
		}
		if (npcId == 8583) {
			npcName = "Hespori";
		}
		if (npcId == 853) {
			npcName = "Isle Of The Damned";
		}

		if (!isCollectionNPC(npcId)) {
			return;
		}

		ArrayList<GameItem> currentItems = getCollections().get("" + npcId);
		if (currentItems == null) {
			currentItems = new ArrayList<>();
			currentItems.add(new GameItem(dropId, dropAmount));
			if (message) {
				player.sendMessage("You have unlocked another item in your collection log!");
				player.getPA().sendNotification("Collection Log", ItemDef.forId(dropId).getName() + " Unlocked", npcName, dropId);
			}
			Achievements.increase(player, AchievementType.COLLECTOR, 1);

		} else {
			boolean found = false;
			for(int i = 0; i < currentItems.size(); i++) {
				if (currentItems.get(i).getId() == dropId) {
					currentItems.get(i).setAmount(currentItems.get(i).getAmount() + dropAmount);
					found = true;
					break;
				}
			}

			if (!found) {
				currentItems.add(new GameItem(dropId, dropAmount));
				if (message) {
					player.sendMessage("You have unlocked another item in your collection log!");
					player.getPA().sendNotification("Collection Log", ItemDef.forId(dropId).getName() + " Unlocked", npcName, dropId);
					if (!player.getAchievements().isComplete(Achievements.Achievement.Collector)) {
						Achievements.increase(player, AchievementType.COLLECTOR, 1);
					}
				}
				List<GameItem> drops = Server.getDropManager().getNPCdrops(npcId);
				if (currentItems.size() == drops.size()) {
					player.sendMessage("@gre@You have completed a collection log!");
				}

			}
		}
		getCollections().put("" + npcId, currentItems);
		//As soon as it gets a drop it saves Kraken has been getting the most complaints
		saveToJSON();
	}

	/**
	 * Checks if an NPC is in fact a collection NPC
	 * @param npcId
	 * @return
	 */
	public boolean isCollectionNPC(int npcId) {
		for (Map.Entry<CollectionTabType, ArrayList<Integer>> entry : collectionNPCS.entrySet()) {
			for(int i = 0; i < entry.getValue().size(); i++) {
				if (entry.getValue().get(i) == npcId) {
					return true;
				}
			}
		}
		return false;
	}

	public ArrayList<GameItem> getUnlocked(int npcId) {
		return collections.getOrDefault(String.valueOf(npcId), Lists.newArrayList());
	}

	/**
	 * Gets the amount of unique items unlocked.
	 * Doesn't count item amounts or repeat items in different collection log tabs/categories.
	 */
	public int getUniquesUnlocked() {
		HashSet<Integer> uniques = new HashSet<>();

		for (List<GameItem> items : collections.values()) {
			for (GameItem item : items) {
				uniques.add(item.getId());
			}
		}

		return uniques.size();
	}

	/**
	 * Handles all buttons on the interface
	 * @param buttonId
	 * @return
	 */
	public boolean handleActionButtons(Player player, int buttonId) {
		if (buttonId >= 90082 && buttonId <= 90180) {
			int index = (buttonId - 90082) / 2;
			player.getViewingCollectionLog().selectCell(player, index, player.collectionLogTab);
			return true;
		}
		switch(buttonId) {
			case 90076:
				player.getViewingCollectionLog().selectTab(player, CollectionTabType.BOSSES);
				return true;
			case 90182:
				player.getViewingCollectionLog().selectTab(player, CollectionTabType.WILDERNESS);
				return true;
			case 90184:
				player.getViewingCollectionLog().selectTab(player, CollectionTabType.RAIDS);
				return true;
			case 90186:
				player.getViewingCollectionLog().selectTab(player, CollectionTabType.MINIGAMES);
				return true;
			case 90188:
				player.getViewingCollectionLog().selectTab(player, CollectionTabType.OTHER);
				return true;
			case 90073:
				player.getPA().closeAllWindows();
				return true;
		}
		return false;
	}

	/**
	 * Saves users collection to a JSON file
	 */
	public void saveToJSON() {
		if (getSaveName() == null) {
			logger.error("No name set for collection log to save.");
			return;
		}
		Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = prettyGson.toJson(getCollections());
		BufferedWriter bw;
		try {
			if (!new File(getSaveDirectory()).exists()) {
				Preconditions.checkState(new File(getSaveDirectory()).mkdirs());
			}
			bw = new BufferedWriter(new FileWriter(new File(getSaveDirectory() + getSaveName().toLowerCase() + ".json")));
			bw.write(prettyJson);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadForPlayer(Player player) {
		setGroupIronman(false);
		setSaveName(player.getLoginNameLower());
		loadCollections(); // Load collection for non-group ironman players
	}

	public void loadForGroupIronman(GroupIronmanGroup group) {
		setGroupIronman(true);
		setSaveName(group.getName().toLowerCase());
		loadCollections();
	}

	/**
	 * Group ironman was released without a group collection log, and therefore people were filling
	 * up their collection logs individually. We needed to combine team members collection logs together
	 * for the release.
	 * TODO delete on re-release
	 */
	public static void combineForGroupIronman(Player player, GroupIronmanGroup group) {
		if (group.getMergedCollectionLogs().contains(player.getLoginNameLower())) {
			return;
		}

		group.getMergedCollectionLogs().add(player.getLoginNameLower());

		HashMap<String, ArrayList<GameItem>> groupEntries = group.getCollectionLog().getCollections();
		HashMap<String, ArrayList<GameItem>> playerEntries = player.getCollectionLog().getCollections();

		if (!playerEntries.isEmpty()) {
			for (Map.Entry<String, ArrayList<GameItem>> entry : playerEntries.entrySet()) {
				ArrayList<GameItem> groupItems = groupEntries.get(entry.getKey());

				if (groupItems == null) {
					groupEntries.put(entry.getKey(), entry.getValue());
					logger.debug("Putting full entry onto group collection log because it doesn't exist in group collection log {}", entry);
					continue;
				}

				main: for (GameItem playerItem : entry.getValue()) {
					for (GameItem groupItem : groupItems) {
						if (playerItem.getId() == groupItem.getId()) {
							groupItem.setAmount(playerItem.getAmount() + groupItem.getAmount());
							logger.debug("Combined player and group item to create new amount {}, originalGroupItem={}, originalPlayerItem={}", groupItem, groupItem, playerItem);
							continue main;
						}
					}

					groupItems.add(playerItem);
					logger.debug("Added new group item from player collection log {}", playerItem);
				}
			}

			group.getCollectionLog().saveToJSON();
		}
	}

	private Path getPlayerSaveFilePath() {
		return Paths.get(getSaveDirectory() + getSaveName().toLowerCase() + ".json");
	}

	/**
	 * Loads a users collection data
	 */
	public void loadCollections() {
		try {
			File file = getPlayerSaveFilePath().toFile();

			JsonParser parser = new JsonParser();
			if (!file.exists()) {
				return;
			}
			Object obj = parser.parse(new FileReader(file));
			JsonObject jsonUpdates = (JsonObject) obj;

			Type listType = new TypeToken<HashMap<String, ArrayList<GameItem>>>() {
			}.getType();

			collections = new Gson().fromJson(jsonUpdates, listType);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No collections found!");
			collections = new HashMap<>();
		}
	}

	public boolean isGroupIronman() {
		return groupIronman;
	}

	public void setGroupIronman(boolean groupIronman) {
		this.groupIronman = groupIronman;
	}

	public String getSaveName() {
		return saveName;
	}

	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}

	public CollectionLog getLinked() {
		return linked;
	}

	public void setLinked(CollectionLog linked) {
		this.linked = linked;
	}
}
