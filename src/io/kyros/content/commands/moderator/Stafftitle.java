package io.kyros.content.commands.moderator;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Changes the title of the player to their default staff title.
 * 
 * @author Emiel
 */
public class Stafftitle extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.sendMessage("You will now get your staff title instead. Relog for changes to take effect.");
		c.keepTitle = false;
		c.killTitle = false;
	}
}
