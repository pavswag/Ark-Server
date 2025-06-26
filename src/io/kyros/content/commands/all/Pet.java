package io.kyros.content.commands.all;

import io.kyros.Server;
import io.kyros.content.bosses.nightmare.NightmareConstants;
import io.kyros.content.commands.Command;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.pet.PetManager;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class Pet extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {

        PetManager.open(c);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens the pet management interface");
    }

}
