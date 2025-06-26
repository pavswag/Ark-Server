package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.Configuration;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Teleport the player to the mage bank.
 * 
 * @author Emiel
 */
public class Pg extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		 c.getPA().sendFrame126(Configuration.PRICE_GUIDE, 12000);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens the unofficial price guide.");
	}

}
