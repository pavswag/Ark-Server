package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Equipment extends Command {

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Links you to the Kyros Equipment page.");
    }

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getPA().sendFrame126("https://kyros.fandom.com/wiki/Equipment", 12000);
    }
}