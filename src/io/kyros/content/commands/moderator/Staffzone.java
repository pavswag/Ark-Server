package io.kyros.content.commands.moderator;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Teleport the player to the staffzone.
 * 
 * @author Emiel
 */
public class Staffzone extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}
		c.getPA().startTeleport(3164, 3489, 2, "modern", false);
	}
}
