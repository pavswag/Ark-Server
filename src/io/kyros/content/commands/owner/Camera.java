package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Camera extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split(" ");
		
		if (args.length <= 1) {
			c.getPA().resetCamera();
			c.sendMessage("Resetting camera.");
			return;
		}
		c.getPA().stillCamera(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		c.sendMessage("Setting camera to: X: " + Integer.parseInt(args[0]) + ", "
				+ "Y: " + Integer.parseInt(args[1]) + ", "
				+ "Z: " + Integer.parseInt(args[2]) + ", "
				+ "SPEED: " + Integer.parseInt(args[3]) + ", "
				+ "ANGLE: " + Integer.parseInt(args[4]));
	}

}
