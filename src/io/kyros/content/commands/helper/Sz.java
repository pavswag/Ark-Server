package io.kyros.content.commands.helper;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Teleport the player to the staffzone.
 *
 * @author Emiel
 */
public class Sz extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}
		c.getPA().startTeleport(3612, 3672, 0, "modern", false);
	}
}
