package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.Graphic;
import io.kyros.model.entity.EntityProperties;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class testentityprop extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        int ordinal = Integer.parseInt(input);
        if(ordinal >= EntityProperties.values().length) {
            player.sendMessage("Can only use between 0-" + (EntityProperties.values().length - 1));
            return;
        }
        player.addEntityProperty(EntityProperties.values()[ordinal]);
    }
}
