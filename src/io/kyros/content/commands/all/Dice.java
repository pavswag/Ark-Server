package io.kyros.content.commands.all;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Teleport the player to the mage bank.
 * 
 * @author Emiel
 */
public class Dice extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}
		if (c.getPosition().inWild()) {
			return;
		}
		c.getPA().spellTeleport(3363, 3294, 0, false);

		List<String> lines = Lists.newArrayList();
		lines.add("Welcome to Gambling on Kyros.");
		lines.add("We would like you warn you that when you start gambling,");
		lines.add("THIS IS AT YOUR OWN RISK!");
		lines.add("Our staff here at ArkCane are not responsible,");
		lines.add("for you losing any items.");
		lines.add("Please be responsible when gambling,");
		lines.add("as we want you to have fun and enjoy your time here.");

		c.getPA().openQuestInterface("FlowerPoker Gambling", lines.stream().limit(149).collect(Collectors.toList()));

	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teles you to gambling area");
	}

}
