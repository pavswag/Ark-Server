package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;

/**
 * Empty the inventory of the player.
 * 
 * @author Emiel
 */
public class Empty extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (Boundary.isIn(c, Boundary.HESPORI)) {
			c.sendMessage("You cannot use that command here.");
			return;
		}
		c.getDH().sendDialogues(450,-1);
	}
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Empty your inventory");
	}
}
