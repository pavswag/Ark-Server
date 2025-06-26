package io.kyros.content.commands.admin;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Open the banking interface.
 * 
 * @author Emiel
 */
public class EventManager extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		io.kyros.content.event_manager.EventManager.open(c);
	}
}
