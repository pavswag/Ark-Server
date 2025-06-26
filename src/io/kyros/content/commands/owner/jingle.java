package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class jingle extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        int id = Integer.parseInt(input);
        player.sendMessage("Sending jingle [" + id + "]");
        player.getPA().sendJingle(id);
    }
}
