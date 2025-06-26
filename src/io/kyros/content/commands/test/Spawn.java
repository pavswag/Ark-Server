package io.kyros.content.commands.test;

import java.util.Optional;

import io.kyros.content.ItemSpawner;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Spawn extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        ItemSpawner.open(player);
    }

    public Optional<String> getDescription() {
        return Optional.of("Opens an interface to spawn items.");
    }
}
