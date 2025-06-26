package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class Collect extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getCollectionBox().collect(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Collect items in your collection box.");
    }
}
