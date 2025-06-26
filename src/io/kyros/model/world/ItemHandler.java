package io.kyros.model.world;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.bosses.BabaCloneInstance;
import io.kyros.content.bosses.ChaoticClone;
import io.kyros.content.bosses.ChaoticDeathSpawn;
import io.kyros.content.bosses.sharathteerk.SharInstance;
import io.kyros.content.bosses.sol_heredit.SolInstance;
import io.kyros.content.elonmusk.Island;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.lootbag.LootingBag;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.prayer.Bone;
import io.kyros.content.skills.prayer.Prayer;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.*;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.GroundItem;
import io.kyros.model.items.ImmutableItem;
import io.kyros.model.shops.ShopAssistant;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import io.kyros.util.logging.player.ItemPickupLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static io.kyros.Server.getPlayers;

public class ItemHandler {

	private static final Logger logger = LoggerFactory.getLogger(ItemHandler.class);

	/**
	 * A list of all {@link GroundItem}'s in the game.
	 */
	private final List<GroundItem> items = new LinkedList<>();

	/**
	 * The amount of ticks before the game item become visible for everyone (globalisation).
	 */
	public static final int GLOBALISATION_TICKS = Misc.toCycles(1, TimeUnit.MINUTES);

	public int getGlobalisationTicks() {
		return Server.isDebug() ? 10 : GLOBALISATION_TICKS;
	}

	/**
	 * The amount of ticks, after becoming globalised, if applicable, will be visible.
	 * The total time that a ground item will be registered in the game is calculated as
	 * {@link ItemHandler#GLOBALISATION_TICKS} plus this field.
	 */
	public static final int REMOVE_TICKS = Misc.toCycles(1, TimeUnit.MINUTES);

	public ItemHandler() { }

	public GroundItem getGroundItem(Player player, int itemId, int x, int y, int height) {
		Predicate<GroundItem> exists = (GroundItem i) -> i.getId() == itemId && i.getX() == x && i.getY() == y && i.getHeight() == height;
		if (player != null) {
			exists = exists.and((GroundItem i) -> i.getInstance() == player.getInstance() && i.isViewable(player));
		}

		return items.stream().filter(exists).findFirst().orElse(null);
	}

	/**
	 * Item amount
	 **/
	public int itemAmount(String player, int itemId, int itemX, int itemY, int height, InstancedArea instance) {
		for (GroundItem i : items) {
			if (i.globalisationTicks < 1 || player.equalsIgnoreCase(i.getOwnerName())) {
				if (i.getId() == itemId && i.getX() == itemX && i.getY() == itemY && i.getHeight() == height && i.getInstance() == instance) {
					return i.getAmount();
				}
			}
		}
		return 0;
	}

	/**
	 * Item exists
	 **/
	public boolean itemExists(Player player, int itemId, int itemX, int itemY, int height) {
		return getGroundItem(player, itemId, itemX, itemY, height) != null;
	}

	public void reloadItems(Player player) {
		if (player.getMode() == null) {
			return;
		}

		player.getLocalGroundItems().stream().filter(it -> it.inDistance(player) /* optimization because others are removed by client */)
				.forEach(it -> player.getItems().removeGroundItem(it, false));
		player.getLocalGroundItems().clear();

		items.stream().filter(item -> item.isVisible(player)).forEach(item -> player.getItems().createGroundItem(item));
	}
	public static long processTime = 0;
	public void process() {
		long start = System.currentTimeMillis();
		Iterator<GroundItem> it = items.iterator();
		while (it.hasNext()) {
			GroundItem i = it.next();
			if (i == null) {
				it.remove();
				continue;
			}
			if (i.getInstance() != null && i.getInstance().isDisposed()) {
				i.removeTicks = 0;
				sendRemovedGroundItem(i);
				it.remove();
				logger.debug("Removed ground item because instance disposed {}", i);
				continue;
			}
			if (i.globalisationTicks > 0) {
				i.globalisationTicks--;
			}
			if (i.globalisationTicks == 1) {
				i.globalisationTicks = 0;
				if (i.canGlobalise()) {
					Server.getPlayers().nonNullStream().filter(player -> i.isVisible(player) && !i.isOwner(player) /* because owner already sees it */)
							.forEach(plr -> plr.getItems().createGroundItem(i));
					logger.debug("Globalised ground item: " + i);
				} else {
					logger.debug("Ground item can't be globalised: " + i);
				}
				i.removeTicks = REMOVE_TICKS;
			}
			if (i.removeTicks > 0) {
				i.removeTicks--;
			}
			if (i.removeTicks == 1) {
				i.removeTicks = 0;
				sendRemovedGroundItem(i);
				it.remove();
				logger.debug("Removed ground item: " + i);
			}
		}
		processTime = System.currentTimeMillis() - start;
	}

	public void createUnownedGroundItem(GameItem gameItem, Position position) {
		createUnownedGroundItem(gameItem.getId(), position.getX(), position.getY(), position.getHeight(), gameItem.getAmount());
	}

	public void createUnownedGroundItem(int id, int x, int y, int height, int amount) {
		if (id > 0 && amount > 0) {
			if (id >= 2412 && id <= 2414) {
				return;
			}
			if (!ItemDef.forId(id).isStackable()) {
				if (amount > 28) {
					amount = 28;
				}
				for (int j = 0; j < amount; j++) {
					GroundItem item = new GroundItem(id, x, y, height, 1, getGlobalisationTicks(), "");
					items.add(item);
//					logger.debug("Added ground item: " + item);
				}
			} else {
				GroundItem item = new GroundItem(id, x, y, height, amount, getGlobalisationTicks(), "");
				items.add(item);
//				logger.debug("Added ground item: " + item);
			}
		}
	}

	public void createGroundItemFromDrop(Player player, int itemId, int itemX, int itemY, int height, int itemAmount, int playerId) {
		boolean newPlayer = player.hasNewPlayerRestriction();
		boolean inWild = Boundary.isIn(player, Boundary.WILDERNESS_PARAMETERS);
		boolean global = !player.hasNewPlayerRestriction();
		createGroundItem(player, itemId, itemX, itemY, height, itemAmount, playerId, global, inWild ? 3 : getGlobalisationTicks(), false);
		if (newPlayer) {
			player.sendMessage("The dropped item won't become global because you haven't played for " + Configuration.NEW_PLAYER_RESTRICT_TIME_MIN + " minutes.");
		}
	}

	public void createGroundItem(Player player, GameItem gameItem, Position position, int hideTicks) {
		createGroundItem(player, gameItem.getId(), position.getX(), position.getY(), position.getHeight(), gameItem.getAmount(), player.getIndex(), true, hideTicks, false);
	}

	public void createGroundItem(Player player, GameItem gameItem, Position position) {
		createGroundItem(player, gameItem.getId(), position.getX(), position.getY(), position.getHeight(), gameItem.getAmount(), player.getIndex(), false);
	}

	public void createGroundItem(Player player, int itemId, int itemX, int itemY, int height, int itemAmount, int playerId, boolean npc) {
		createGroundItem(player, itemId, itemX, itemY, height, itemAmount, playerId, true, getGlobalisationTicks(), npc);
	}

	/**
	 * @param globalise If the item should become globalised after the timer.
	 */
	public void createGroundItem(Player player, int itemId, int itemX, int itemY, int height, int itemAmount, int playerId, boolean globalise, int hideTicks, boolean npc) {
		if (itemId  <= 0) {
			return;
		}
		if (Boundary.isIn(player, ChaoticClone.boundary) && itemId == 9616) {
			return;
		}

		if (Boundary.isIn(player, SolInstance.boundary) && itemId == 9604) {
			return;
		}

		if (Boundary.isIn(player, BabaCloneInstance.boundary) && itemId == 9600) {
			return;
		}

		if (Boundary.isIn(player, Boundary.NEW_INSTANCE_AREA) && itemId == 9608) {
			return;
		}

		if (Boundary.isIn(player, ChaoticDeathSpawn.BOUNDARY)) {
			itemX = player.getPosition().getX();
			itemY = player.getPosition().getY();
			height = player.getPosition().getHeight();
		}

		if(npc) {
			if (Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
				if (player.getCurrentPet().hasPerk("mythical_god_slayer") && player.getCurrentPet().findPetPerk("mythical_god_slayer").isHit()) {
					itemAmount *= 2;
				}
			}

			if (player.getCurrentPet().hasPerk("mythical_the_doubler") && player.getCurrentPet().findPetPerk("mythical_the_doubler").isHit()) {
				itemAmount *= 2;
			}
		}

		if (!ItemDef.forId(itemId).isNoted() && player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33111) && !Boundary.isIn(player, new Boundary(2688, 4736, 2751, 4799)) ) {
			itemId = ItemDef.forId(itemId).getNotedItemIfAvailable();
		}

		if (!Boundary.isIn(player, Boundary.WILDERNESS_PARAMETERS) &&
				!ItemDef.forId(itemId).isNoted() && player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33085) && Misc.random(0, 100) < 10  &&
				!Boundary.isIn(player, Boundary.OUTLAST) && !Boundary.isIn(player, new Boundary(2688, 4736, 2751, 4799))  && !CastleWarsLobby.isInCw(player)) {
			itemId = ItemDef.forId(itemId).getNotedItemIfAvailable();
		}

		if (Boundary.isIn(player, Boundary.AFK_ZONE)) {
			if (player.getInventory().freeInventorySlots() < 1 && !player.getMode().isUltimateIronman()) {
				player.getInventory().addToBank(new ImmutableItem(itemId,itemAmount));
				/*System.out.println("Sent to bank.");*/
			} else {
				player.getInventory().addOrDrop(new ImmutableItem(itemId, itemAmount));
				/*System.out.println("Sent to inventory.");*/
			}
			logPickup(player, new GroundItem(itemId, itemX, itemY, height, itemAmount, hideTicks, player.getDisplayNameLower()));

			return;
		}

		if (player.getItems().playerHasItem(33159) && itemId == 10501 ||
				(player.hasFollower && (player.petSummonId == 33159)) && itemId == 10501 && !Boundary.isIn(player, new Boundary(2688, 4736, 2751, 4799))) {
			player.getItems().addItemUnderAnyCircumstance(itemId,itemAmount);
			logPickup(player, new GroundItem(itemId, itemX, itemY, height, itemAmount, hideTicks, player.getDisplayNameLower()));
			return;
		}

		if (player.getLoginName().equalsIgnoreCase("zzhz")) {
				if (player.getInventory().freeInventorySlots() < 1 && !player.getMode().isUltimateIronman()) {
					player.getInventory().addToBank(new ImmutableItem(itemId,itemAmount));
					/*System.out.println("Sent to bank.");*/
				} else {
					player.getInventory().addOrDrop(new ImmutableItem(itemId, itemAmount));
					/*System.out.println("Sent to inventory.");*/
				}
			logPickup(player, new GroundItem(itemId, itemX, itemY, height, itemAmount, hideTicks, player.getDisplayNameLower()));
			return;
		}

		if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33110) && !Boundary.isIn(player, Boundary.WILDERNESS_PARAMETERS) && !Boundary.isIn(player, Boundary.OUTLAST)
				&& !Boundary.isIn(player, Boundary.FALLY_OUTLAST)  && !Boundary.isIn(player, new Boundary(2688, 4736, 2751, 4799)) &&
				!Boundary.isIn(player, Boundary.SWAMP_OUTLAST) && !Boundary.isIn(player, Boundary.FOREST_OUTLAST) &&
				!Boundary.isIn(player, Boundary.SNOW_OUTLAST) && !Boundary.isIn(player, Boundary.ROCK_OUTLAST)  &&
				!Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST) && !Boundary.isIn(player, Boundary.WG_Boundary) && !CastleWarsLobby.isInCw(player)) {
			if (player.getMode().isUltimateIronman()) {
				player.getInventory().addOrDrop(new ImmutableItem(itemId,itemAmount));
			} else {
				player.getInventory().addToBank(new ImmutableItem(itemId,itemAmount));
			}
			if (player.CleptNotification) {
				player.sendErrorMessage("[Clepto] @bla@Your clepto picked up " + ItemDef.forId(itemId).getName() + " x " + itemAmount + "!");
			}
			logPickup(player, new GroundItem(itemId, itemX, itemY, height, itemAmount, hideTicks, player.getDisplayNameLower()));


			return;
		}

		if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33083) && !Boundary.isIn(player, Boundary.WILDERNESS_PARAMETERS) && Misc.random(0,100) > 90 && !Boundary.isIn(player, Boundary.OUTLAST)
				&& !Boundary.isIn(player, Boundary.FALLY_OUTLAST) && !Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST) && !Boundary.isIn(player, Boundary.SWAMP_OUTLAST) && !Boundary.isIn(player, new Boundary(2688, 4736, 2751, 4799)) && !CastleWarsLobby.isInCw(player)
				&& !Boundary.isIn(player, Boundary.FOREST_OUTLAST)&& !Boundary.isIn(player, Boundary.SNOW_OUTLAST)&& !Boundary.isIn(player, Boundary.ROCK_OUTLAST) && !Boundary.isIn(player, Boundary.WG_Boundary)
			) {
			if (player.getMode().isUltimateIronman()) {
				player.getInventory().addOrDrop(new ImmutableItem(itemId,itemAmount));
			} else {

				player.getInventory().addToBank(new ImmutableItem(itemId,itemAmount));
			}
			logPickup(player, new GroundItem(itemId, itemX, itemY, height, itemAmount, hideTicks, player.getDisplayNameLower()));
			return;
		}



		if (Boundary.isIn(player, Boundary.REV_CAVE) && player.isSkulled &&
				player.getItems().isWearingItem(22557) &&
				!ItemDef.forId(itemId).isNoted() /*&& !amuletCheck(itemId)*/) {
			itemId = ItemDef.forId(itemId).getNotedItemIfAvailable();
		}

		if (playerId < 0) {
			return;
		}
		Player owner = getPlayers().get(playerId);
		if (owner == null) {
			System.out.println("Could not find owner player using index[" + playerId + "]");
			return;
		}
		if (itemId > 0 && itemAmount > 0) {
			/**
			 * Lootvalue
			 */
			if (player.lootValue > 0) {
				if (ShopAssistant.getItemShopValue(itemId) >= player.lootValue) {
					player.getPA().stillGfx(1177, itemX, itemY, height, 5);
					player.sendMessage("@red@Your lootvalue senses a drop valued at or over "
							+ Misc.getValueWithoutRepresentation(player.lootValue) + " coins.");
				}
			}
			/**
			 * Bone crusher
			 */
			boolean crusher = player.getItems().playerHasItem(13116) || player.playerEquipment[Player.playerAmulet] == 22986 ;

			Optional<Bone> boneOptional = Prayer.isOperableBone(itemId);
			if (crusher && boneOptional.isPresent()) {
				Bone bone = boneOptional.get();

				double experience = player.getRechargeItems().hasItem(13114) ? 0.75 : player.getRechargeItems().hasItem(13115) ? 1 : player.getDiaryManager().getMorytaniaDiary().hasCompleted("ELITE") ?  1 : 0.50 ;
				if (itemId == bone.getItemId()) {
					player.getPrayer().onBonesBuriedOrCrushed(bone, true);
					player.getPA().addSkillXPMultiplied((int) (bone.getExperience() * experience),
							Skill.PRAYER.getId(), true);
					return;
				}
			}

			if (!ItemDef.forId(itemId).isStackable()) {
				if (itemAmount > 28) {
					itemAmount = 28;
				}
				for (int j = 0; j < itemAmount; j++) {
					GroundItem item = new GroundItem(itemId, itemX, itemY, height, 1, hideTicks, owner.getLoginNameLower());
					player.getItems().createGroundItem(item);
					item.setGlobalise(globalise);
					item.setInstance(player.getInstance());
					items.add(item);
//					logger.info("Added ground item: " + item);
				}
			} else {
				GroundItem current = getGroundItem(player, itemId, itemX, itemY, height);
				int existingAmount = 0;
				if (current != null) {
					existingAmount += current.getAmount();
					player.getItems().removeGroundItem(current, true);
					removeGroundItem(player, current, false);
				}
				int newAmount = (int) Math.min(Integer.MAX_VALUE, (long) itemAmount + existingAmount);
				if (newAmount <= 0) {
					return;
				}
				GroundItem item = new GroundItem(itemId, itemX, itemY, height, newAmount, hideTicks, owner.getLoginNameLower());
				player.getItems().createGroundItem(item);
				item.setGlobalise(globalise);
				item.setInstance(player.getInstance());
				items.add(item);
//				logger.debug("Added ground item: " + item);
			}
		}
	}

	public void createGroundItem(Player player, int itemId, int itemX, int itemY, int height, int itemAmount) {
		if (itemId > 0 && itemAmount > 0) {
			if (!ItemDef.forId(itemId).isStackable()) {
				if (itemAmount > 28) {
					itemAmount = 28;
				}
				for (int j = 0; j < itemAmount; j++) {
					GroundItem item = new GroundItem(itemId, itemX, itemY, height, 1, getGlobalisationTicks(), player.getLoginNameLower());
					player.getItems().createGroundItem(item);
					item.setInstance(player.getInstance());
					items.add(item);
//					logger.debug("Added ground item: " + item);
				}
			} else {
				if (itemId == 11849 && Boundary.isIn(player, Boundary.ROOFTOP_COURSES)) {
					player.getItems().addItemUnderAnyCircumstance(11849, 1);
				} else {
					GroundItem current = getGroundItem(player, itemId, itemX, itemY, height);
					int existingAmount = 0;
					if (current != null) {
						existingAmount += current.getAmount();
						player.getItems().removeGroundItem(current, true);
						removeGroundItem(player, current, false);
					}
					int newAmount = (int) Math.min(Integer.MAX_VALUE, (long) itemAmount + existingAmount);
					if (newAmount <= 0) {
						return;
					}
					GroundItem item = new GroundItem(itemId, itemX, itemY, height, newAmount, getGlobalisationTicks(), player.getLoginNameLower());
					player.getItems().createGroundItem(item);
					item.setInstance(player.getInstance());
					items.add(item);
//					logger.debug("Added ground item: " + item);
				}
			}
		}
	}

	/**
	 * Removing the ground item
	 **/

	public void removeGroundItem(Player c, int itemId, int itemX, int itemY, int height, boolean add) {
		removeGroundItem(c, getGroundItem(c, itemId, itemX, itemY, height), add);
	}

	public void removeGroundItem(Player c, GroundItem i, boolean add) {
		if (i == null)
			return;

		if (!add) {
			items.remove(i);
			return;
		}

		if (i.isLocked()) {
			return;
		}

		if(c.itemPickedUpThisTick)
			return;

		if (c.getPosition().inWild() && c.getItems().playerHasItem(LootingBag.LOOTING_BAG_OPEN)
				&& c.getLootingBag().getLootingBagContainer().deposit(i.getId(), i.getAmount(), false)) {
			logPickup(c, i);
			sendRemovedGroundItem(i);
			items.remove(i);
			return;
		}

		if (c.getItems().hasRoomInInventory(i.getId(), i.getAmount()) && itemExists(c, i.getId(), i.getX(), i.getY(), i.getHeight())) {
			c.getItems().addItem(i.getId(), i.getAmount());
			logPickup(c, i);
			sendRemovedGroundItem(i);
			items.remove(i);
		}
	}

	private void logPickup(Player player, GroundItem groundItem) {
		player.itemPickedUpThisTick = true;
		ItemDef def = ItemDef.forId(groundItem.getId());
		Server.getLogging().write(new ItemPickupLog(player, new GameItem(groundItem.getId(), groundItem.getAmount()), groundItem.getPosition(), groundItem.getOwnerName()));
		if (def.getShopValue() > 30_000 || def.getShopValue() < 2) {
			if (player.getRights().isNot(Right.STAFF_MANAGER) && player.getRights().isNot(Right.GAME_DEVELOPER)) {
				if (!player.getDisplayName().equalsIgnoreCase(groundItem.getOwnerName())) {
					Discord.writePickupMessage("[Pickup]: " + player.getDisplayName() +
							" picked up " + def.getName() +
							" x " + groundItem.getAmount() +
							" at " + groundItem.getPosition() +
							" from " + groundItem.getOwnerName());
				}
			}
		}
	}

	private void sendRemovedGroundItem(GroundItem groundItem) {
		Server.getPlayers().nonNullStream().forEach(plr -> {
			if (plr.getLocalGroundItems().contains(groundItem)) {
				plr.getItems().removeGroundItem(groundItem);
			}
		});
	}

	/**
	 * The counterpart of the item whether it is the noted or un noted version
	 *
	 * @param itemId the item id we're finding the counterpart of
	 * @return the note or unnoted version or -1 if none exists
	 */
	public int getCounterpart(int itemId) {
		return ItemDef.forId(itemId).getNoteId() == 0 ? -1 : ItemDef.forId(itemId).getNoteId();
	}

}