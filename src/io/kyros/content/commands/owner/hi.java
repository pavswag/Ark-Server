package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class hi extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");
        int id = Integer.parseInt(args[0]);
        player.headIcon = id;
        player.getPA().requestUpdates();
        player.sendMessage("HeadIcon: " + id);
    }
}
//						c.headIcon = PRAYER_HEAD_ICONS[i];
//						c.getPA().requestUpdates();