package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * Spawn a specific Object.
 * 
 * @author Emiel
 *
 */
public class Object extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split(" ");
		int objId = Integer.parseInt(args[0]);
		int type = args.length > 1 ? Integer.parseInt(args[1]) : 10;
		int face = args.length > 2 ? Integer.parseInt(args[2]) : 0;
		Server.getPlayers().forEach(p -> {
			p.getPA().object(objId, c.absX, c.absY, face, type, true);
		});
		
		c.sendMessage(String.format("Object spawned [Id: %s] [Type: %s] [Face: %s]", objId, type, face));
	}
}
