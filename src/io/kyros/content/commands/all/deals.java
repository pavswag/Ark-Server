package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.content.deals.AccountBoosts;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class deals  extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        AccountBoosts.openInterface(c);
    }

    public Optional<String> getDescription() { return Optional.of("Welcome to Kyros's Deals."); }
}
