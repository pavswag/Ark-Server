package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.content.update_polls.UpdatePollManager;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class VotingPolls extends Command {

	@Override
		public void execute(Player c, String commandName, String input) {
		UpdatePollManager.open(c);
	}
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Closes your current interface");
	}

}
