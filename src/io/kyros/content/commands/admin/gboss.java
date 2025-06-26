package io.kyros.content.commands.admin;

import io.kyros.content.activityboss.impl.Groot;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class gboss extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Groot.spawnGroot();
    }
}
