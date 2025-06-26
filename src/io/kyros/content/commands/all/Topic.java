package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class Topic extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {

        player.getPA().openWebAddress("https://kyros.fandom.com/wiki/Kyros_Wiki");
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Open a forum topic by the id.");
    }
}
