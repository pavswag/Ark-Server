package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class Unblock extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (input.equalsIgnoreCase("list")) {
            int count = 0;
            for (String s : player.getSlayer().getRemoved()) {
                if (s != null && !s.isEmpty()) {
                    player.sendMessage("[SlayerTask] " + s + ", Index = " + count);
                    count++;
                }
            }
            return;
        }

        int index = Integer.parseInt(input);
        String[] removed = player.getSlayer().getRemoved();
        String[] newRemoved = new String[removed.length];
        removed[index] = "";
        int count = 0;

        for (int idx = 0; idx < removed.length; idx++)
            newRemoved[idx] = "";
        for (int idx = 0; idx < removed.length; idx++) {
            if (removed[idx] != null && removed[idx].length() > 0) {
                newRemoved[count++] = removed[idx];
            }
        }

        player.getSlayer().setRemoved(newRemoved);

    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Allows unblocking specific tasks using index numbers.");
    }
}