package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class PerkShop extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        c.getPetPerkShop().open(c);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens the pet perk shop");
    }

}
