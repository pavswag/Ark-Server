package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;

import java.util.Optional;

public class newdz extends Command  {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.wildLevel > 20) {
            c.sendMessage("@red@You cannot teleport above 20 wilderness.");
            return;
        }
        if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
            return;
        }
        c.getPA().startTeleport(3797, 2866, 0, "modern", false);
        c.sendMessage("You have teleported to the new donator ares Welcome!.");
    }
    @Override
    public Optional<String> getDescription() {
        return Optional.of("Teleports you to the new donator island.");
    }

}
