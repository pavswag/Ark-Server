package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

/**
 * Opens the game rule page in the default web browser.
 * 
 * @author Emiel
 */
public class Grules extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
//		c.getPA().sendFrame126("https://ArkCane.net", 12000);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens a web page with in-game rules");
	}

}
