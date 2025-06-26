package io.kyros.content.commands.all;

import io.kyros.Configuration;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

/**
 * Unjails the player.
 *
 * @author Emiel
 */
public class Unjail extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {

		if (c.jailEnd <= System.currentTimeMillis()) {
			c.setTeleportToX(Configuration.START_LOCATION_X);
			c.setTeleportToY(Configuration.START_LOCATION_Y);
			c.jailEnd = 0;
			if (c.getInstance() != null && c.getInstance().getPlayers().contains(c)) {
				c.getInstance().dispose();
			}
			c.sendMessage("You've been unjailed. Don't get jailed again!");
		} else {
			long duration = (long) Math.ceil((double) (c.jailEnd - System.currentTimeMillis()) / 1000 / 60);
			c.sendMessage("You need to wait " + duration + " more minutes before you can ::unjail yourself.");
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you out of the jail if you did your time");
	}

}
