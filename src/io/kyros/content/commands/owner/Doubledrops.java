package io.kyros.content.commands.owner;

import io.kyros.Configuration;
import io.kyros.content.commands.Command;
import io.kyros.content.wogw.Wogw;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Doubledrops extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        int minutes = Integer.parseInt(input);
        if (minutes == 0) {
            Configuration.DOUBLE_DROPS_TIMER = 0;
            player.sendMessage("Double drops have ended.");
            return;
        }

        Configuration.DOUBLE_DROPS_TIMER = Misc.toCycles(minutes, TimeUnit.MINUTES);
        Wogw.sendActivateMessage("double drops");
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Turn on double drops for x minutes, ::doubledrops 60 (60 minutes).");
    }
}
