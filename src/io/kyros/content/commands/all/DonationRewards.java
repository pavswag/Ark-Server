package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class DonationRewards extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getDonationRewards().openInterface();
    }

    public Optional<String> getDescription() {
        return Optional.of("Opens the Donation rewards interface.");
    }
}
