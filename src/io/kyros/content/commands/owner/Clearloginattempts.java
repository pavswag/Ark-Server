package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.net.login.LoginThrottler;

public class Clearloginattempts extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		LoginThrottler.clear();
		c.sendMessage("Cleared all login attempts.");
	}

}
