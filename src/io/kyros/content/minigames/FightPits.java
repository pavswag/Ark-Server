package io.kyros.content.minigames;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.concurrent.atomic.AtomicInteger;

import static io.kyros.Server.getPlayers;

/**
 * @author Sanity
 */

public class FightPits {

	public int[] playerInPits = new int[200];

	private final int GAME_TIMER = 140;
	private final int GAME_START_TIMER = 40;

	private int gameTime = -1;
	private int gameStartTimer = 30;
	private int properTimer;
	public int playersRemaining;

	public String pitsChampion = "Nobody";

	public void process() {
		if (properTimer > 0) {
			properTimer--;
			return;
		} else {
			properTimer = 4;
		}
		if (gameStartTimer > 0) {
			gameStartTimer--;
			updateWaitRoom();
		}
		if (gameStartTimer == 0) {
			startGame();
		}
		if (gameTime > 0) {
			gameTime--;
			if (playersRemaining == 1)
				endPitsGame(getLastPlayerName());
		} else if (gameTime == 0)
			endPitsGame("Nobody");
	}

	public String getLastPlayerName() {
		for (int j = 0; j < playerInPits.length; j++) {
			if (playerInPits[j] > 0)
				return Server.getPlayers().get(playerInPits[j]).getDisplayName();
		}
		return "Nobody";
	}

	public void updateWaitRoom() {
		getPlayers().forEachFiltered(player -> player.getPA().inPitsWait(), c -> {
			c.getPA().sendFrame126("Next Game Begins In : " + ((gameStartTimer * 3) + (gameTime * 3)) + " seconds.", 6570);
			c.getPA().sendFrame126("Champion: " + pitsChampion, 6572);
			c.getPA().sendFrame126("", 6664);
			c.getPA().walkableInterface(6673);
		});
	}

	public void startGame() {
		if (getWaitAmount() < 2) {
			gameStartTimer = GAME_START_TIMER / 2;
			// System.out.println("Unable to start fight pits game due to lack of players.");
			return;
		}
		getPlayers().forEachFiltered(player -> player.getPA().inPitsWait(), c -> {
			addToPitsGame(c.getIndex());
		});
		System.out.println("Fight Pits game started.");
		gameStartTimer = GAME_START_TIMER + GAME_TIMER;
		gameTime = GAME_TIMER;
	}

	public int getWaitAmount() {
		AtomicInteger count = new AtomicInteger();
		getPlayers().forEachFiltered(player -> player.getPA().inPitsWait(), player -> count.getAndIncrement());
		return count.get();
	}

	public void removePlayerFromPits(int playerId) {
		for (int j = 0; j < playerInPits.length; j++) {
			if (playerInPits[j] == playerId) {
                Player c = getPlayers().get(playerInPits[j]);
				c.getPA().movePlayer(2399, 5173, 0);
				playerInPits[j] = -1;
				playersRemaining--;
				c.inPits = false;
				break;
			}
		}
	}

	public void endPitsGame(String champion) {
		for (int j = 0; j < playerInPits.length; j++) {
			if (playerInPits[j] < 0)
				continue;
			if (getPlayers().get(playerInPits[j]) == null)
				continue;
            Player c = getPlayers().get(playerInPits[j]);
			c.getPA().movePlayer(2399, 5173, 0);
			c.inPits = false;
		}
		playerInPits = new int[200];
		pitsChampion = champion;
		playersRemaining = 0;
		pitsSlot = 0;
		gameStartTimer = GAME_START_TIMER;
		gameTime = -1;
		System.out.println("Fight Pits game ended.");
	}

	private int pitsSlot;

	public void addToPitsGame(int playerId) {
		if (getPlayers().get(playerId) == null)
			return;
		playersRemaining++;
        Player c = getPlayers().get(playerId);
		c.getPA().walkableInterface(-1);
		playerInPits[pitsSlot++] = playerId;
		c.getPA().movePlayer(2392 + Misc.random(12), 5139 + Misc.random(25), 0);
		c.inPits = true;
	}
}