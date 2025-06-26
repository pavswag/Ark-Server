package io.kyros.content.commands.admin;

import io.kyros.content.activityboss.impl.Groot;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class SetGroot extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] args = input.split("-");
            if (args.length != 1) {
                throw new IllegalArgumentException();
            }
            int points = Integer.parseInt(args[0]);

            if ((Groot.ActivityPoints - points) <= 0) {
                player.sendMessage("You can't do that amount due to it being negative or zero!");
                return;
            }

            Groot.ActivityPoints -= points;
        } catch (Exception e) {
            player.sendMessage("Error. Correct syntax: ::setgroot-amount");
        }
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Prints out combat defence stats while in combat.");
    }
}
