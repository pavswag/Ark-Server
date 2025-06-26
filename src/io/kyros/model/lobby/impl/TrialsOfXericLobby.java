package io.kyros.model.lobby.impl;

import com.google.common.collect.Maps;
import io.kyros.content.minigames.xeric.XericLobby;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.ClientGameTimer;
import io.kyros.model.entity.player.Player;
import io.kyros.model.lobby.Lobby;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TrialsOfXericLobby extends Lobby {


	@Override
	public void onJoin(Player player) {
		player.getPA().movePlayer(3232, 4832);
		player.sendMessage("Welcome to the Trials of Xeric Lobby.");
		player.sendMessage("The raid will begin in "+ formattedTimeLeft() + "!");
		player.getPA().sendGameTimer(ClientGameTimer.XERIC_TIMER, TimeUnit.SECONDS, 59);
	}

	@Override
	public void onLeave(Player player) {
		player.getPA().movePlayer(1234, 3567);
	}

	@Override
	public boolean canJoin(Player player) {
		if (player.calculateCombatLevel() < 70) {
			player.sendMessage("You need a combat level of 70 to join Trials of Xeric!");
			return false;
		}		
		boolean accountInLobby = getFilteredPlayers()
	            .stream()
	            .anyMatch(lobbyPlr -> lobbyPlr.getMacAddress().equalsIgnoreCase(player.getMacAddress()));
		if (accountInLobby) {
			player.sendMessage("You already have an account in the lobby!");
			return false;
		}
		return true;
	}

	@Override
	public void onTimerFinished(List<Player> lobbyPlayers) {
		Map<String, Player> macFilter = Maps.newConcurrentMap();

		lobbyPlayers.stream().forEach(plr -> macFilter.put(plr.getMacAddress(), plr));

		XericLobby.start(lobbyPlayers);

		lobbyPlayers.stream().filter(plr -> !macFilter.containsValue(plr)).forEach(plr -> {
			plr.sendMessage("You had a different account in this lobby, you will be added to the next one");
			onJoin(plr);
		});
	}

	@Override
	public void onTimerUpdate(Player p1) {
		String timeLeftString = formattedTimeLeft();
		p1.addQueuedAction(player -> {
			player.getPA().sendFrame126("Raid begins in: @gre@" + timeLeftString, 6570);
			player.getPA().sendFrame126("", 6571);
			player.getPA().sendFrame126("", 6572);
			player.getPA().sendFrame126("", 6664);
//			player.getPA().walkableInterface(6673);
		});
	}

	@Override
	public long waitTime() {
		return 60000;
	}

	@Override
	public int capacity() {
		return 99;
	}

	@Override
	public String lobbyFullMessage() {
		// TODO Auto-generated method stub
		return "The lobby is currently full! Please wait for the next game!";
	}

	@Override
	public boolean shouldResetTimer() {
		return this.getWaitingPlayers().isEmpty();
	}

	@Override
	public Boundary getBounds() {
		return Boundary.XERIC_LOBBY;
	}
	
	

}
