package io.kyros.content.commands.moderator;

import io.kyros.content.commands.Command;
import io.kyros.content.tournaments.TourneyManager;
import io.kyros.content.worldevent.WorldEventContainer;
import io.kyros.content.worldevent.impl.TournamentWorldEvent;
import io.kyros.model.entity.player.Player;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 10/3/19
 *
 */
public class Mstarttourney extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		if (TourneyManager.getSingleton().setNextTourneyType(input)) {
			WorldEventContainer.getInstance().startEvent(new TournamentWorldEvent());
			player.sendMessage("The tournament is about to begin, please allow up to 30 seconds..");
		} else {
			player.sendMessage("The tournament won't start because you entered an invalid tournament type.");
			player.sendMessage("Types: " + "Rune Melee, " + "Pure, "+ "Vesta, "+ "Monk Robes, "+"NH");
			player.sendMessage("Types: " + "Range AGS, "+ "Dharok, "+  "DYOG, "+ "OG dds/whip, "+ "Max Inquisitor");
		}
	}
}
