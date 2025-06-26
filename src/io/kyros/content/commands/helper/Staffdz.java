package io.kyros.content.commands.helper;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class Staffdz extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.inTrade || c.inDuel || c.getPosition().inWild()) {
            return;
        }

        c.getPA().startTeleport(1903, 5750, 0, "modern", false);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Teleports you to the staff Donor Zone.");
    }

}