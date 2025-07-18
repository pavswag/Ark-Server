package io.kyros.content.items;

import io.kyros.model.entity.player.Player;

import java.util.concurrent.TimeUnit;


public class CluescrollRateIncreaseScroll {

	private static final long TIME = TimeUnit.MINUTES.toMillis(30) / 600;

	public static void openScroll(Player player) {
		if (player.fasterCluesScroll) {
			player.sendMessage("You already have a bonus skill pet rate going.");
			return;
		}

		player.fasterCluesScroll = true;
		player.fasterCluesTicks = TIME;
	}
	

}
	
