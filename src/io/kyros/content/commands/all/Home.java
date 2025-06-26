package io.kyros.content.commands.all;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;

import java.util.Optional;

/**
 * Teleport the player to home.
 *
 * @author Emiel
 */
public class Home extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}

		if (c.jailEnd > 1) {
			c.forcedChat("I'm trying to teleport away!");
			c.sendMessage("You are still jailed!");
			return;
		}
		if (c.getPosition().inWild() && c.wildLevel > 20) {
			if (c.getCurrentPet().getNpcId() != 2316) {
				c.sendMessage("You can't use this command in the wilderness.");
				return;
			}
		}

		if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
			c.getPA().spellTeleport(3135, 3628, 0, true);
		} else {
			c.getPA().spellTeleport(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0, true);
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to home area");
	}

}
