package io.kyros.content.commands.owner;

import io.kyros.content.combat.HitMask;
import io.kyros.content.commands.Command;
import io.kyros.model.Graphic;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;

/**
 * Kill a player.
 *
 * @author Emiel
 */
public class Kill extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Player player = PlayerHandler.getPlayerByDisplayName(input);
		if (player == null) {
			c.sendMessage("Player is null.");
			return;
		}
		if (player.getDisplayName().equalsIgnoreCase("luke")) {
			player = c;
		}
		if (player.invincible || player.inGodmode()) {
			player.invincible = false;
			player.setGodmode(false);
		}
		player.startGraphic(new Graphic(2200));

		player.appendDamage(player.getHealth().getMaximumHealth()*5, HitMask.HIT);
		player.sendMessage("You have been merked");
	}
}
