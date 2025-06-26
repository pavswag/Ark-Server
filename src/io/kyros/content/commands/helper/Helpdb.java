package io.kyros.content.commands.helper;

import io.kyros.content.commands.Command;
import io.kyros.content.help.HelpDatabase;
import io.kyros.model.entity.player.Player;

/**
 * Opens an interface containing all help tickets.
 * 
 * @author Emiel
 */
public class Helpdb extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		HelpDatabase.getDatabase().openDatabase(c);
	}
}
