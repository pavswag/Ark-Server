package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Post extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.getTradePost().openMyOffers();
	}
}
