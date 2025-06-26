package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import org.apache.commons.lang3.text.WordUtils;

import static io.kyros.Server.getPlayers;

/**
 * Sends the player a message containing a list of all online players with a dice bag in their inventory.
 * 
 * @author Emiel
 */
public class Hosts extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		StringBuilder sb = new StringBuilder();
		getPlayers().forEach(c2 -> {
			if (c2.getItems().playerHasItem(15098)) {
				sb.append(c2.getDisplayName()).append(", ");
			}
		});
		if (sb.length() > 0) {
			String result = "@blu@Available hosts@bla@: " + sb.substring(0, sb.length() - 2);
			String[] wrappedLines = WordUtils.wrap(result, 80).split(System.lineSeparator());
			for (String line : wrappedLines) {
				c.sendMessage(line);
			}
		} else {
			c.sendMessage("@blu@No hosts available!");
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Lists all available dice hosts");
	}

}
