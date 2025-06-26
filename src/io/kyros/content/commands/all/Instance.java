package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Instance extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        //player.getAoeInstanceHandler().open();
        player.sendMessage("New system coming soon! <3");
    }
}
