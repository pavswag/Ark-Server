package io.kyros.content.commands.all;

import io.kyros.content.teleportv2.inter.TeleportInterface;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class Teleport extends Commands{

    @Override
    public void execute(Player player, String commandName, String input) {
        TeleportInterface.open(player);
        player.getQuesting().handleHelpTabActionButton(668);
    }
    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens Teleport Interface.");
    }

}
