package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.content.leaderboards.LeaderboardInterface;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class Leaderboards extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		LeaderboardInterface.openInterface(c);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens the leaderboards interface.");
	}
}
