package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.fusion.FusionTypes;
import io.kyros.model.entity.player.Player;

public class Fuse extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {

        player.getFusionSystem().openInterface(FusionTypes.WEAPON);

    }
}
