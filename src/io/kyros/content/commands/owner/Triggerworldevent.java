package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.worldevent.WorldEventContainer;
import io.kyros.model.entity.player.Player;

public class Triggerworldevent extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        WorldEventContainer.getInstance().setTriggerImmediateEvent(true);
        player.sendMessage("Triggering next world event, please allow up to 30 seconds.");
    }
}
