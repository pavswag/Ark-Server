package io.kyros.content.commands.helper;

import io.kyros.content.commands.Command;
import io.kyros.content.commands.punishment.PunishmentCommand;
import io.kyros.model.entity.player.Player;

public class Ban extends Command {
	@Override
	public void execute(Player c, String commandName, String input) {
		new PunishmentCommand(commandName, input).parse(c);
	}

	@Override
	public String getFormat() {
		return PunishmentCommand.getFormat(getCommand());
	}
}
