package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Teleport the player to the mage bank.
 * 
 * @author Emiel
 */
public class News extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
	/*	 c.getPA()
         .sendFrame126(
                 "https://ArkCane.net", 12000);*/
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Check out all of our updates.");
	}

}
