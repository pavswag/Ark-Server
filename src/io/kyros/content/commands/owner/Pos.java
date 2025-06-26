package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Show the current position.
 * 
 * @author Emiel
 *
 */
public class Pos extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		player.sendMessage("Current coordinates x: " + player.absX + " y:" + player.absY + " h:" + player.heightLevel);
		player.sendMessage("Current local coordinates x:" + player.getPosition().getChunkX() + ", y:" + player.getPosition().getChunkY());
		System.out.println("new Position(" + player.absX + ", " + player.absY + ", " + player.heightLevel + "), ");
	}
}
