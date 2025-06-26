package io.kyros.content.commands.owner;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;

public class Newhunllef extends Command  {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.wildLevel > 20) {
            c.sendMessage("@red@You cannot teleport above 20 wilderness.");
            return;
        }
        if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
            return;
        }
        c.getPA().startTeleport(2270, 4758, 0, "modern", false);
        c.sendMessage("@red@You have been teleported to New Hunleff area.");
    }
    @Override
    public Optional<String> getDescription() {
        return Optional.of("Teleports you to New Hunleff.");
    }

}
