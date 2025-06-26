package io.kyros.content.cheatprevention;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

import java.util.concurrent.TimeUnit;

public class RandomEventInterface extends CycleEvent {

	/**
	 * The amount of time the event starts at
	 */
	private static final int MAXIMUM_TIME = 300;

	private static final long EXECUTION_DELAY = TimeUnit.MINUTES.toMillis(15);

	/**
	 * The player this random event was created for
	 */
	private final Player player;

	/**
	 * The combination of items the player would have to choose from
	 */
	private Items combination;

	/**
	 * The correct item the player must select
	 */
	private GameItem correctItem;

	/**
	 * The time remaining for the event
	 */
	private int time;

	/**
	 * Determines if the random event is active
	 */
	private boolean active;

	/**
	 * The time in milliseconds the event was last executed
	 */
	private long lastExecuted;

	/**
	 * Creates a new {@link RandomEventInterface} for the {@link Player}.
	 *
	 * @param player the player this is created for
	 */
	public RandomEventInterface(Player player) {
		this.player = player;
	}

	/**
	 * Creates a new event by randomly selecting a combination of items and choosing the item the player must select. The time is reset and the state of the event is set to active.
	 */
	public void execute() {
		if (player.wildLevel >= 30) {
			return;
		}

		lastExecuted = System.currentTimeMillis();
		active = true;
		time = MAXIMUM_TIME;
		combination = Items.values()[Misc.random(Items.values().length - 1)];
		correctItem = combination.items[Misc.random(combination.items.length - 1)];
		draw();
		CycleEventHandler.getSingleton().stopEvents(this, CycleEventHandler.Event.AFKZone);
		CycleEventHandler.getSingleton().stopEvents(this);
		CycleEventHandler.getSingleton().addEvent(this, this, 1);
	}

	/**
	 * Determines if the event can be executed on the player.
	 *
	 * @return {@code true} if the conditions for execution are met.
	 */
	public boolean isExecutable() {
		if (System.currentTimeMillis() - lastExecuted < EXECUTION_DELAY && !Server.isDebug()) {
			return false;
		}
		if (Boundary.isIn(player, Boundary.HESPORI)) {
			return false;
		}
		if (Boundary.isIn(player, Boundary.AFK_ZONE)) {
			return false;
		}
		if (player.getRights().isOrInherits(Right.HELPER)) {
			return false;
		}
		if (Boundary.isIn(player, new Boundary(2432, 2816, 2495, 2879))) {
			return false;
		}
		if (player.wildLevel > 0) {
			return false;
		}
		if (player.getIpAddress().equalsIgnoreCase("86.12.98.192")) {
			return false;
		}
		if (player.getStoreDonated() >= 500) {
			return false;
		}
		if (Boundary.isIn(player, Boundary.OUTLAST_AREA)
				|| Boundary.isIn(player, Boundary.FOREST_OUTLAST)
				|| Boundary.isIn(player, Boundary.SNOW_OUTLAST)
				|| Boundary.isIn(player, Boundary.ROCK_OUTLAST)
				|| Boundary.isIn(player, Boundary.FALLY_OUTLAST)
				|| Boundary.isIn(player, Boundary.COLOSSEUM)
				|| Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST)
				|| Boundary.isIn(player, Boundary.SWAMP_OUTLAST)
				|| CastleWarsLobby.isInCw(player) || CastleWarsLobby.isInCwWait(player)
				|| Boundary.isIn(player, Boundary.WG_Boundary)) {
			return false;
		}
		if (Boundary.isIn(player, Boundary.RAIDS)) {
			return false;
		}
		if (Boundary.isIn(player, Boundary.RAIDS_LOBBY)) {
			return false;
		}
		if (Boundary.isIn(player, Boundary.OUTLAST_LOBBY)) {
			return false;
		}
		if (Boundary.isIn(player, Boundary.OUTLAST_HUT)) {
			return false;
		}
		if (Boundary.isIn(player, Boundary.FULL_RAIDS)) {
			return false;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(player)) {
			return false;
		}
		return player.playerAttackingIndex <= 0;
	}

	/**
	 * Manages button clicks on the interface
	 *
	 * @param buttonId the button id clicked
	 */
	public void clickButton(int buttonId) {
		if (!(buttonId >= 130032 && buttonId <= 130038)) {
			return;
		}
		int slot = buttonId == 130032 ? 0 : buttonId == 130035 ? 1 : buttonId == 130038 ? 2 : -1;
		if (slot != -1) {
			GameItem item = combination.items[slot];
			if (item.getId() != correctItem.getId()) {
				player.getPA().stopSkilling();
				player.sendMessage("Incorrect, you have been sent home.");
				player.getPA().movePlayer(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0);
			}
			if (item.getId() == correctItem.getId()) {
				player.getItems().addItemToBankOrDrop(7478, 5000);
				player.sendMessage("@blu@Correct! well done.");
				player.sendMessage("@red@5k afk tokens have been added to your bank.");
			}
			active = false;
			player.getPA().removeAllWindows();
			CycleEventHandler.getSingleton().stopEvents(this);

		}
	}

	/**
	 * Draws the information on the interface
	 */
	public void draw() {
		ItemDef definition = ItemDef.forId(correctItem.getId());
		player.getPA().sendFrame126("Click the '" + definition.getName() + "'", 33302);
		int frame = 33311;
		for (GameItem item : combination.items) {
			player.getPA().sendFrame34a(frame, item.getId(), 0, 1);
			frame += 3;
		}
		// int yOffset = -115 + Misc.random(115 * 2);
		// int xOffset = -97 + Misc.random(97 * 2);
		player.getPA().showInterface(33300);
		// player.getPA().sendFrame70(xOffset, yOffset, 33300);
	}

	@Override
	public void execute(CycleEventContainer container) {
		time--;
		long millis = (long) ((time * .6) * 1000L);
		long second = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
		long minute = TimeUnit.MILLISECONDS.toMinutes(millis);
		player.getPA().sendFrame126(String.format("%2d:%02d", minute, second, millis), 33303);
		if (player.getOpenInterface() != 33300) {
			draw();
		}
		player.getPA().stopSkilling();
		if (time <= 0) {
			active = false;
			player.getPA().stopSkilling();
			player.sendMessage("Incorrect, you have been sent to Home.");
			player.getPA().movePlayer(Configuration.EDGEVILLE_X, Configuration.EDGEVILLE_Y, 0);
			container.stop();
		}
	}

	/**
	 * Determines if the random event interface is active
	 *
	 * @return true if the event is active, otherwise false.
	 */
	public boolean isActive() {
		return active;
	}

	private enum Items {
		SCIMITAR(new GameItem(1321), new GameItem(1323), new GameItem(1325)),
		DAGGER(new GameItem(1209), new GameItem(1211), new GameItem(1213)),
		TWO_HANDED(new GameItem(1315), new GameItem(1317), new GameItem(1319)),
		KITESHIELD(new GameItem(2659), new GameItem(2675), new GameItem(2667)),
		DEFENDER(new GameItem(8849), new GameItem(8850), new GameItem(12954));

		/**
		 * An array of items that will be sent to the interface. One item the player must select.
		 */
		private final GameItem[] items;

		/**
		 * Constructs a new Items object containing an array of {@link GameItem} objects
		 *
		 * @param items the array of items
		 */
		Items(GameItem... items) {
			this.items = items;
		}
	}

}
