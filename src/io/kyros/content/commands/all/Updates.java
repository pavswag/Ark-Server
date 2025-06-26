package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Updates extends Command {

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Links you to the Kyros Updates page.");
    }

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getPA().sendFrame126("https://kyros.fandom.com/wiki/Updates", 12000);
    }
}