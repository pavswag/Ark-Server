package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.util.Misc;

import java.util.Optional;

public class afk extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.getPosition().inWild()) {
            c.sendMessage("You can only use this command outside the wilderness.");
            return;
        }
        /*if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
            return;
        }*/
/*        if (c.getPA().calculateTotalLevel() < 250) {
            c.sendMessage("You require a total level of 250 to enter the AFK Zone.");
            return;
        }*/
        c.sendMessage("Use command ::afkstore to view the store!", 0x02b3b0);
        c.getPA().startTeleport(2134, 5526, 0, "modern", false);
        c.sendMessage("You have " + Misc.formatCoins(c.getAfkPoints()) + " afk points remaining.", 0x11ff00);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Takes you to the AFK Zone.");
    }
}
