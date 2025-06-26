package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Show a red skull above the player's head.
 * 
 * @author Emiel
 *
 */
public class Red extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.headIconPk = (1);
		c.getPA().requestUpdates();
	}
}
