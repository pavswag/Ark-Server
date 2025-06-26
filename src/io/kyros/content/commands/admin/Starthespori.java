package io.kyros.content.commands.admin;

import io.kyros.content.commands.Command;
import io.kyros.content.worldevent.WorldEventContainer;
import io.kyros.content.worldevent.impl.HesporiWorldEvent;
import io.kyros.model.entity.player.Player;

public class Starthespori extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        WorldEventContainer.getInstance().startEvent(new HesporiWorldEvent());
        player.sendMessage("Hespori will start soon.");
    }
}
