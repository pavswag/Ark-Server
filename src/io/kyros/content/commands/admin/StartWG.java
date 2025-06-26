package io.kyros.content.commands.admin;

import io.kyros.content.commands.Command;
import io.kyros.content.worldevent.WorldEventContainer;
import io.kyros.content.worldevent.impl.WGWorldEvent;
import io.kyros.model.entity.player.Player;

public class StartWG extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        WorldEventContainer.getInstance().startEvent(new WGWorldEvent());
        player.sendMessage("WeaponGames will start soon.");
    }
}
