package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.content.vote_panel.VotePanelInterface;
import io.kyros.content.votemanager.VoteManager;
import io.kyros.model.entity.player.Player;


public class Vote extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		VoteManager.open(c);
		c.getPA().sendFrame126("https://paradise-network.net/kyros/vote.php", 12000);
		c.getPA().closeAllWindows();
		c.sendMessage("@bla@[@blu@VOTE@bla@] You may also use @red@::voterank@bla@ to open vote management.");
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens a web page where you can vote for rewards");
	}

}
