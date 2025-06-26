package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;

import java.util.Optional;

/**
 * Open the forums in the default web browser.
 *
 * @author Emiel
 */
public class Outlast extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
			c.sendMessage("You cannot access this area.");
			return;
		}
		if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
			return;
		}
		c.sendMessage("@red@[OUTLAST]@blu@ Bank your items and enter the portal to join the tournament! Good Luck!");
		c.getPA().spellTeleport(1753, 3606, 0, false);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Quick teleport to outlast teleport.");
	}

}
