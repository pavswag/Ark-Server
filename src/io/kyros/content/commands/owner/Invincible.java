package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Show the current position.
 * 
 * @author Emiel
 *
 */
public class Invincible extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		if (player.invincible) {
			player.invincible = false;
			player.sendMessage("Invincibility Disabled.");
		} else {
			player.invincible = true;
			player.sendMessage("Invincibility Enabled.");
		}
		
	}
}
