package io.kyros.content.commands.admin;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * @author Arthur Behesnilian 2:38 PM
 */
public class SpawnMenu extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {

       player.getPA().showInterface(43214);
        player.sendMessage("You open the spawning menu...");
    }

}
