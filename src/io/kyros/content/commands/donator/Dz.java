package io.kyros.content.commands.donator;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;

/**
 * Teleports the player to the donator zone.
 *
 * @author Emiel
 */
public class Dz extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.inTrade || c.inDuel || c.getPosition().inWild()) {
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@This player is currently at the pk district.");
			return;
		}

		c.start(new DialogueBuilder(c).option("Select the donor zone you wish to enter!",
/*				new DialogueOption("Super DZ", p-> {
					if (c.getStoreDonated() >= 2500) {
//						c.getPA().startTeleport(3038, 2784, 0, "modern", false);
						c.sendErrorMessage("Coming soon.");
						c.getPA().closeAllWindows();
					}
				}),*/
				new DialogueOption("Premium DZ", p -> {
					if (c.getStoreDonated() >= 500) {
						c.getPA().startTeleport(2406, 3803, 0, "modern", false);
					}
				}),
				new DialogueOption("Normal DZ", p -> {
					if (c.getRights().isOrInherits(Right.Donator)) {
						c.getPA().startTeleport(1967, 5365, 0, "modern", false);
					}
				})));
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to donator zone.");
	}

}
