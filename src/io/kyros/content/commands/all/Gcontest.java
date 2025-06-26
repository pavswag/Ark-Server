package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.group.contest.GroupIronmanContest;

import java.util.Optional;


public class Gcontest extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		GroupIronmanContest.openInterface(c);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens the GIM contest interface.");
	}

}
