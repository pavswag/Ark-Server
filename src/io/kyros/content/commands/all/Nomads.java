package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Nomads extends Command {

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Links you to the Kyros Upgrades and Nomad Points page.");
    }

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getPA().sendFrame126("https://kyros.fandom.com/wiki/Upgrades_And_Nomad_Points", 12000);
    }
}