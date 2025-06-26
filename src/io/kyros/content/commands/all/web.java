package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class web extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getPA().openWebAddress("https://paradise-network.net/");
    }
}
