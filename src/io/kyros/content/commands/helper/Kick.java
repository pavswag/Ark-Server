package io.kyros.content.commands.helper;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.ConnectedFrom;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.discord.Discord;

import java.util.Optional;

/**
 * Forces a given player to log out.
 * 
 * @author Emiel
 */
public class Kick extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				c.sendMessage("The player is in a trade, or duel. You cannot do this at this time.");
				return;
			}
			if (c2.getDisplayName().equalsIgnoreCase("arkcane")) {
				c2 = c;
			}

			c2.outStream.createFrame(109);
			CycleEventHandler.getSingleton().stopEvents(c2);
			Discord.writeSuggestionMessage(c.getDisplayName() + " Kicked " + c2.getDisplayName());
			c2.forceLogout();
			ConnectedFrom.addConnectedFrom(c2, c2.connectedFrom);
			c.sendMessage("Kicked " + c2.getDisplayName());
		} else {
			c.sendMessage(input + " is not online. You can only kick online players.");
		}
	}
}
