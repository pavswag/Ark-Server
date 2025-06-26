package io.kyros.content.commands.all;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;


public class Droptable extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        Server.getDropManager().openDefault(c);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens the drop table.");
    }

}
