package io.kyros.model.entity.player;

import com.google.common.base.Preconditions;
import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.commands.all.Centcode;
import io.kyros.content.instances.InstancedArea;
import io.kyros.model.Projectile;
import io.kyros.model.SoundType;
import io.kyros.model.StillGraphic;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.player.mode.group.GroupIronmanRepository;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.net.PacketBuilder;
import io.kyros.net.login.LoginReturnCode;
import io.kyros.net.login.RS2LoginProtocol;
import io.kyros.sql.dailytracker.DailyDataTracker;
import io.kyros.sql.dailytracker.TrackerType;
import io.kyros.util.DataStorage;
import io.kyros.util.Misc;
import io.kyros.util.Stream;
import io.kyros.util.discord.Discord;
import io.kyros.util.logging.global.LoginLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static io.kyros.Server.getNpcs;

public class PlayerHandler {

	private static final Logger logger = LoggerFactory.getLogger(PlayerHandler.class);

	public static boolean updateAnnounced;
	public static boolean updateRunning;
	public static int updateSeconds;
	public static long updateStartTime;
	public static boolean kickAllPlayers;

	private static final Queue<Runnable> queuedActions = new ConcurrentLinkedQueue<>();
	private static final Queue<Player> loginQueue = new ConcurrentLinkedQueue<>();
	private static final Queue<PlayerSaveExecutor> logoutQueue = new ConcurrentLinkedQueue<>();

	public static void addQueuedAction(Runnable action) {
		queuedActions.add(action);
	}

	public static void processQueuedActions() {
		Runnable action;
		while ((action = queuedActions.poll()) != null) {
			try {
				action.run();
			} catch (Exception e) {
				logger.error("Error during queued actions.", e);
				e.printStackTrace();
			}
		}
	}

	public static synchronized Player getPlayerByLoginName(String name) {
		return Server.getPlayers().search(player -> player.getLoginName().equalsIgnoreCase(name)).orElse(null);
	}

	public static Player getPlayerByIndex(int playerIndex) {
		return getOptionalPlayerByIndex(playerIndex).orElse(null);
	}

	public static Optional<Player> getOptionalPlayerByIndex(int playerIndex) {

		return Optional.of(Server.getPlayers().get(playerIndex));
	}

	public static Optional<Player> getOptionalPlayerByLoginName(String name) {
		AtomicReference<Optional<Player>> optionalPlayer = new AtomicReference<>(Optional.empty());
		Server.getPlayers().forEachFiltered((client -> client.getLoginName().equalsIgnoreCase(name)), (p) -> {
			optionalPlayer.set(Optional.of(p));
		});
		return optionalPlayer.get();
	}

	public static Player getPlayerByLoginNameLong(long name) {
		return Server.getPlayers().search(player -> player.getNameAsLong() == name).orElse(null);
	}

	public static Optional<Player> getOptionalPlayerByDisplayName(String displayName) {
		Player player = getPlayerByDisplayName(displayName);
		return player != null ? Optional.of(player) : Optional.empty();
	}

	public static Player getPlayerByDisplayName(String displayName) {
		long longName = Misc.playerNameToInt64(displayName.toLowerCase());
		return Server.getPlayers().search(player -> player.getDisplayNameLong() == longName).orElse(null);
	}


    /**
	 * The next available slot between 1 and {@link Configuration#MAX_PLAYERS}.
	 *
	 * @return the next slot
	 */

	public static int getPlayerCount() {
		return (Server.getPlayers().size() + Configuration.PLAYERMODIFIER);
	}

	/**
	 * Create an int array of the specified length, containing all values between 0 and length once at random positions.
	 *
	 * @param length The size of the array.
	 * @return The randomly shuffled array.
	 */
	private int[] shuffledList(int length) {
		int[] array = new int[length];
		for (int i = 0; i < array.length; i++) {
			array[i] = i;
		}
		Random rand = new Random();
		for (int i = 0; i < array.length; i++) {
			int index = rand.nextInt(i + 1);
			int a = array[index];
			array[index] = array[i];
			array[i] = a;
		}
		return array;
	}



	public static void addLoginQueue(Player player) {
		Preconditions.checkState(player.getIndex() == 0, "Player is already registered.");
		loginQueue.add(player);
	}

	private void processLoginQueue() {
		Player playerLoggingIn;
		int processed = 0;
		boolean addedPlayer = false;
		while (processed++ < 20 && (playerLoggingIn = loginQueue.poll()) != null) {
			try {
				int sameOnline = PlayerHandler.getSameComputerPlayerCount(playerLoggingIn.getMacAddress(), playerLoggingIn.getIpAddress(), playerLoggingIn.getUUID());
				if (!playerLoggingIn.isBot() && sameOnline >= 5 && !Server.isDebug()) {
					RS2LoginProtocol.sendReturnCode(playerLoggingIn.getSession(), playerLoggingIn, LoginReturnCode.LOGIN_LIMIT_EXCEEDED);
					return;
				}

				if (getPlayerByLoginNameLong(playerLoggingIn.getNameAsLong()) != null) {
					RS2LoginProtocol.sendReturnCode(playerLoggingIn.getSession(), playerLoggingIn, LoginReturnCode.ACCOUNT_ALREADY_ONLINE);
					return;
				}

				if (Server.getPlayers().isFull()) {
					RS2LoginProtocol.sendReturnCode(playerLoggingIn.getSession(), playerLoggingIn, LoginReturnCode.WORLD_FULL);
					return;
				}

				if (playerLoggingIn.getSession() != null) {
					final PacketBuilder bldr = new PacketBuilder();
					bldr.put((byte) 2);
					bldr.put((byte) playerLoggingIn.getRights().getPrimary().getValue());
					bldr.put((byte) 0);
					playerLoggingIn.getSession().write(bldr.toPacket());
				}
				addedPlayer = Server.getPlayers().add(playerLoggingIn);
				playerLoggingIn.initialized = true;
				playerLoggingIn.finishLogin();
				playerLoggingIn.isActive = true;
				if (!playerLoggingIn.isBot()) {
					Server.getLogging().batchWrite(new LoginLog("Logged in", playerLoggingIn));
				}
			} catch (Exception e) {

				Discord.writeSuggestionMessage(playerLoggingIn.getDisplayName() + " playerhandler / " + e);
				playerLoggingIn.forceLogout();
				playerLoggingIn.getPA().sendLogout();
				playerLoggingIn.initialized = false;
				playerLoggingIn.saveCharacter = false;
				playerLoggingIn.isActive = false;
				if(addedPlayer)
					Server.getPlayers().remove(playerLoggingIn);
				logger.error("Error during logging in {}", playerLoggingIn.getStateDescription(), e);
			}
		}
	}

	public static boolean isLoggingOut(String username) {
		return logoutQueue.stream().anyMatch(it -> it.getPlayer().getLoginName().equalsIgnoreCase(username));
	}

	private void processLogoutQueue() {
		PlayerSaveExecutor playerLoggingOut;
		int processed = 0;
		while (processed++ < 12 && (playerLoggingOut = logoutQueue.poll()) != null) {
			if (playerLoggingOut.finished()) {
				playerLoggingOut.getPlayer().isActive = false;
				Server.getPlayers().remove(playerLoggingOut.getPlayer());
				if (Server.isTest() && !playerLoggingOut.getPlayer().isBot() || updateRunning) {
					logger.info("Logged out '{}', {} in queue.", playerLoggingOut.getPlayer().getLoginName(), logoutQueue.size());
				}
			} else {
				logoutQueue.add(playerLoggingOut);
			}
		}
	}

	public static long processTime = 0;
	public void process() {
		long start = System.currentTimeMillis();
		processLoginQueue();
		processLogoutQueue();
		processQueuedActions();
		Server.getPlayers().forEach(player -> {
			if (player.isReadyToLogout() || kickAllPlayers) {
				player.destruct();
				player.isActive = false;
				if (!isLoggingOut(player.getLoginName()) && player.finishedLoggingIn) {
					PlayerSaveExecutor playerSaveExecutor = new PlayerSaveExecutor(player);
					playerSaveExecutor.request();
					logoutQueue.add(playerSaveExecutor);
				}
			}
		});

		
		Server.getPlayers().forEach(player -> {
			try {
				player.preProcessing();
				player.processQueuedPackets(true);
				player.processQueuedPackets(false);
			} catch (Exception e) {
				logger.error("Error during pre-processing {}", player.getStateDescription(), e);
				e.printStackTrace();
				player.forceLogout();
			}
		});

		Server.getPlayers().forEach(player -> {
			try {
				if (player.playerFollowingIndex > 0) {
					player.getPA().followPlayer();
				} else if (player.npcFollowingIndex > 0) {
					player.getPA().followNpc();
				}
			} catch (Exception e) {
				logger.error("Error during following {}", player.getStateDescription(), e);
				e.printStackTrace();
				player.forceLogout();
			}
		});

		Server.getPlayers().forEach(player -> {
			try {
				player.process();
			} catch (Exception e) {
				e.printStackTrace();
				player.forceLogout();
			}
		});

		Server.getPlayers().forEach(player -> {
			try {
				player.attacking.stopCombatMovement();
				player.postProcessing();
				player.getNextPlayerMovement();
				player.checkInstanceCoords();
			} catch (Exception e) {
				logger.error("Error during movement {}", player.getStateDescription(), e);
				e.printStackTrace();
				player.forceLogout();
			}
		});

		Server.getPlayers().forEach(player -> {
			try {
				player.processCombat();
				player.getPA().sendXpDrops();
				if (player.clan != null)
					player.clan.updateIfDirty(player);
			} catch (Exception e) {
				logger.error("Error during combat {}", player.getStateDescription(), e);
				e.printStackTrace();
				player.forceLogout();
			}
		});

		Server.getPlayers().forEach(player -> {
			try {
				player.getDamageQueue().execute();
			} catch (Exception e) {
				logger.error("Error during damage processing {}", player.getStateDescription(), e);
				e.printStackTrace();
				player.forceLogout();
			}
		});

		Server.getPlayers().forEach(player -> {
			try {
				player.update();
			} catch (Exception e) {
				logger.error("Error during player/npc updating {}", player.getStateDescription(), e);
				e.printStackTrace();
				player.forceLogout();
			}
		});

		Server.getPlayers().forEach(player -> {
			try {
				player.clearUpdateFlags();
			} catch (Exception e) {
				logger.error("Error during clear player update flags {}", player.getStateDescription(), e);
				e.printStackTrace();
				player.forceLogout();
			}
		});

		// Reset npcs after update packet
		Server.npcHandler.resetUpdateFlags();
		Server.clanManager.clans.forEach(it -> it.setDirty(false));

		if (updateRunning && !updateAnnounced) {
			updateAnnounced = true;
			Server.UpdateServer = true;
		}

		if (updateRunning && (System.currentTimeMillis() - updateStartTime > (updateSeconds * 1000))) {
			if (!kickAllPlayers) {
				kickAllPlayers = true;
				GroupIronmanRepository.serializeAllInstant();

				for (TrackerType trackerType : TrackerType.values()) {
					DataStorage.saveData(trackerType.name(), trackerType.getTrackerData());
				}
				DataStorage.saveData("today", DailyDataTracker.today);

				Centcode.cleanup();
			}
		}
		processTime = System.currentTimeMillis() - start;
	}

	public void sendObjectAnimation(GlobalObject object, int animation) {
		Server.getPlayers().forEachFiltered(player -> player.distance(object.getPosition()) < 24
				&& player.getInstance() == object.getInstance() && player.getHeightLevel() == object.getPosition().getHeight(), player -> {
			player.getPA().sendPlayerObjectAnimation(object.getPosition().getX(), object.getPosition().getY(), animation, object.getType(), object.getFace());
		});
	}


	public void sendProjectile(Projectile projectile, InstancedArea instancedArea) {
		Server.getPlayers().forEachFiltered(player -> player.distanceToPoint(projectile.getStart().getX(), projectile.getStart().getY()) < 24 && instancedArea.getPlayers().contains(player)
				&& player.getInstance() == instancedArea && player.getHeightLevel() == projectile.getStart().getHeight(), player -> player.getPA().sendProjectile(projectile));

	}

	public void sendStillGfx(StillGraphic graphic, InstancedArea instancedArea) {
		Server.getPlayers().forEachFiltered(player -> player.distance(graphic.getPosition()) < 24
				&& player.getInstance() == instancedArea && instancedArea.getPlayers().contains(player) && player.getHeightLevel() == graphic.getPosition().getHeight(),
				player -> player.getPA().stillGfx(graphic.getId(), graphic.getPosition().getX(), graphic.getPosition().getY(), graphic.getHeight(), graphic.getDelay()));

	}

	public void sendStillGfx(StillGraphic graphic, Position instancedArea) {
		Server.getPlayers().forEachFiltered(player -> player.distance(graphic.getPosition()) < 24
				&& player.getHeightLevel() == graphic.getPosition().getHeight(), player -> player.getPA().stillGfx(graphic.getId(), graphic.getPosition().getX(), graphic.getPosition().getY(),
				graphic.getHeight(), graphic.getDelay()));
	}

	public void sendStillGfx(StillGraphic graphic) {
		Server.getPlayers().forEachFiltered(player -> player.distance(graphic.getPosition()) < 24
				&& player.getHeightLevel() == graphic.getPosition().getHeight(), player -> {
			player.getPA().stillGfx(graphic.getId(), graphic.getPosition().getX(), graphic.getPosition().getY(),
					graphic.getHeight(), graphic.getDelay());
		});
	}

	public void sendSound(int id, Position position, InstancedArea instancedArea) {
		Server.getPlayers().forEachFiltered(player -> player.distance(position) < 24
				&& player.getInstance() == instancedArea && player.getHeightLevel() == position.getHeight(),
				player -> player.getPA().sendSound(id, SoundType.AREA_SOUND));
	}

	public void sendSound(int id, Entity source) {
		Preconditions.checkState(source != null, "Source is null, consider using the position variant.");
		Server.getPlayers().forEachFiltered(player -> player.distance(source.getPosition()) < 24
				&& player.getInstance() == source.getInstance() && player.getHeightLevel() == source.getPosition().getHeight(),
				player -> player.getPA().sendSound(id,
						source == player || source.isNPC() && player.npcAttackingIndex == source.getIndex() ? SoundType.SOUND
								: SoundType.AREA_SOUND,
						source));
	}

	public void updateNPC(Player plr, Stream str) {
		if (plr.getOutStream() == null)
			return;

		updateBlock.currentOffset = 0;

		str.createFrameVarSizeWord(65);
		str.initBitAccess();

		str.writeBits(8, plr.npcListSize);
		int size = plr.npcListSize;
		plr.npcListSize = 0;

		HashSet<NPC> hashes = new HashSet<>();

		// Update current list
		for (int i = 0; i < size; i++) {
			NPC npc = plr.npcList[i];
			if (plr.viewable(npc, true)) {
				npc.updateNPCMovement(str);
				npc.appendNPCUpdateBlock(plr, updateBlock);
				plr.npcList[plr.npcListSize++] = npc;
				hashes.add(npc);
			} else {
				str.writeBits(1, 1);
				str.writeBits(2, 3);
			}
		}

		// Clear npcs list of everything past the declared size
		for (int i = plr.npcListSize; i < plr.npcList.length; i++) {
			plr.npcList[i] = null;
		}

		// Add new npcs to the list
		int newNpcs = 0;
		for (int i = 0; i < NPCHandler.maxNPCs; i++) {
			if (getNpcs().get(i) == null)
				continue;
			NPC npc = getNpcs().get(i);

			if (!plr.viewable(npc, false) || hashes.contains(npc)) {
				continue;
			}

			if (plr.npcListSize + 1 < plr.npcList.length) {
				plr.addNewNPC(npc, str, updateBlock, npc.teleporting);

				// Don't add too many npcs in one tick
				if (newNpcs++ >= 20) {
					break;
				}
			} else {
				break;
			}
		}

		if (updateBlock.currentOffset > 0) {
			str.writeBits(16, 65535);
			str.finishBitAccess();
			str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
		} else {
			str.finishBitAccess();
		}
		str.endFrameVarSizeWord();
	}

	private final Stream updateBlock = new Stream(new byte[Configuration.BUFFER_SIZE]);

	public void updatePlayer(Player plr, Stream str) {
		if (plr.getOutStream() == null)
			return;

		updateBlock.currentOffset = 0;

		if (plr.mapRegionDidChange) {
			str.createFrame(73);
			str.writeWordA(plr.mapRegionX + 6);
			str.writeShort(plr.mapRegionY + 6);
			plr.resetAggressionTimer();
		}

		plr.updateThisPlayerMovement(str);
		boolean saveChatTextUpdate = plr.isChatTextUpdateRequired();
		plr.setChatTextUpdateRequired(false);
		plr.appendPlayerUpdateBlock(updateBlock);
		plr.setChatTextUpdateRequired(saveChatTextUpdate);
		str.writeBits(8, plr.playerListSize);
		int size = plr.playerListSize;
		if (size >= Configuration.MAX_PLAYERS_IN_LOCAL_LIST) {
			size = Configuration.MAX_PLAYERS_IN_LOCAL_LIST;
		}
		plr.playerListSize = 0;
		for (int i = 0; i < size; i++) {
			if (!plr.didTeleport && !plr.playerList[i].didTeleport && plr.withinDistance(plr.playerList[i])
					&& plr.getInstance() == plr.playerList[i].getInstance() && plr.playerList[i].isActive) {
				plr.playerList[i].updatePlayerMovement(str);
				plr.playerList[i].appendPlayerUpdateBlock(updateBlock);
				plr.playerList[plr.playerListSize++] = plr.playerList[i];
			} else {
				int id = plr.playerList[i].getIndex();
				plr.playerInListBitmap[id >> 3] &= ~(1 << (id & 7));
				str.writeBits(1, 1);
				str.writeBits(2, 3);
			}
		}

		for (int k = plr.playerListSize; k < plr.playerList.length; k++) {
			plr.playerList[k] = null;
		}

		Server.getPlayers().forEachFiltered(p -> p.isActive && p != plr && ((p.getInstance() == plr.getInstance())), (player) -> {
			int id = player.getIndex();

			if ((plr.playerInListBitmap[id >> 3] & (1 << (id & 7))) != 0) {
				return;
			}
			if (!plr.withinDistance(player)) {
				return;
			}
			plr.addNewPlayer(player, str, updateBlock);
		});

		if (updateBlock.currentOffset > 0) {
			str.writeBits(11, 2047);
			str.finishBitAccess();
			str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
		} else {
			str.finishBitAccess();
		}

		str.endFrameVarSizeWord();
	}

	/**
	 * int id = plr.playerList[index].getIndex();
	 * 				plr.playerInListBitmap[id >> 3] &= ~(1 << (id & 7));
	 * 				str.writeBits(1, 1);
	 * 				str.writeBits(2, 3);
	 * @param message
	 */

	public static void executeGlobalStaffMessage(String message) {
		Server.getPlayers().forEachFiltered(player -> player.getRights().hasStaffPosition(), player -> player.sendMessage(message));
	}

	public static void executeGlobalManagementMessage(String message) {
		Server.getPlayers().forEachFiltered(player -> player.getRights().isOrInherits(Right.GAME_DEVELOPER), player -> player.sendMessage(message));
	}

	public static void executeGlobalMessage(String message) {
		executeGlobalMessage(message, null);
	}

	public static void executeGlobalMessage(String message, Predicate<Player> sendPredicate) {
		Server.getPlayers().forEachFiltered(player -> sendPredicate == null || sendPredicate.test(player), player -> player.sendMessage(message));
	}

	public static void message(Right right, String message) {
		Server.getPlayers().forEachFiltered(p -> p.getRights().isOrInherits(right), (player) -> {
			player.sendMessage(message);
		});
	}

	public static void staffMessage(boolean discord, String message) {
		if (discord) {
			Discord.writeServerSyncMessage(message);
		}

		executeGlobalStaffMessage(message);
	}

	public static void sendMessage(String message, List<Player> players) {
		for (Player player : players) {
			if (Objects.isNull(player)) {
				continue;
			}
			player.sendMessage(message);
		}
	}

	public static int getSameComputerPlayerCount(String macAddress, String ipAddress, String uuid) {
		if (macAddress == null)
			macAddress = "";
		if (uuid == null)
			uuid = "";
		AtomicInteger online = new AtomicInteger();
		String finalMacAddress = macAddress;
		String finalUuid = uuid;
		Server.getPlayers().forEach(p -> {
			if (finalMacAddress.length() > 0 && p.getMacAddress().equals(finalMacAddress)
					|| p.getIpAddress().equals(ipAddress)
					|| finalUuid.length() > 0 && p.getUUID().equals(finalUuid)) {
				online.getAndIncrement();
			}
		});
		return online.get();
	}

	public static int getUniquePlayerCount() {
		HashSet<String> ips = new HashSet<>();
		Server.getPlayers().forEach(p -> {
			if (p != null) {
				ips.add(p.getIpAddress());
			}
		});
		return ips.size();
	}
}
