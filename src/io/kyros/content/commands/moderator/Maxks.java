package io.kyros.content.commands.moderator;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Shows the player who has the highest killstreak.
 * 
 * @author Emiel
 */
public class Maxks extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Optional<Player> op = Server.getPlayers().nonNullStream().filter(Objects::nonNull).max(Comparator.comparing(client -> client.getKillstreak().getTotalKillstreak()));
		op.ifPresent(player -> c.sendMessage("Highest killstreak: "+ player.getDisplayName() +" - "+player.getKillstreak().getTotalKillstreak()));
	}
}
