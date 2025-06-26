package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class babashop extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getShops().openShop(598);
    }

    public Optional<String> getDescription() {
        return Optional.of("Welcome to the bis shop.");
    }
}

