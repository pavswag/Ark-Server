package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.content.votemanager.VoteManager;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.Optional;

public class VoteRank extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        VoteManager.open(c);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens the weekly/monthly vote competition.");
    }
}
