package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Skilling extends Command {

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Links you to the Kyros Skilling and Boosts page.");
    }

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getPA().sendFrame126("https://kyros.fandom.com/wiki/Skilling_%26_Boosts", 12000);
    }
}