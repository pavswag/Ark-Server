package io.kyros.content.commands.helper;

import io.kyros.content.commands.Command;
import io.kyros.content.commands.punishment.PunishmentCommand;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

/**
 * Forces a given player to log out.
 * 
 * @author Emiel
 */
public class GambleBan extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		new PunishmentCommand(commandName, input).parse(c);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Ban a player from gambling.");
	}

	@Override
	public String getFormat() {
		return PunishmentCommand.getFormat(getCommand());
	}

}
