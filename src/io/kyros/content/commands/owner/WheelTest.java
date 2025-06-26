package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class WheelTest extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getWheelOfFortune().open();
    }
}

