package io.kyros.content.commands.moderator;

import io.kyros.content.bosses.Durial321;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Durial extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (Durial321.spawned || Durial321.alive) {
            c.sendMessage("You cannot execute this more than once!");
            return;
        }

        Durial321.init();
    }
}
