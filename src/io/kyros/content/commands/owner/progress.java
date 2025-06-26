package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class progress extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");

        int ID = Integer.parseInt(args[0]);
        int percent = Integer.parseInt(args[1]);

        player.getPA().sendProgressBar(ID, percent);
        player.getPA().showInterface(59951);
    }
}
