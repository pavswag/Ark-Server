package io.kyros.content.commands.all;

import io.kyros.Server;
import io.kyros.content.bosses.nightmare.NightmareConstants;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.content.preset.PresetManager;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class LastTele extends Commands {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.isBoundaryRestricted()) {
            return;
        }

        if (Boundary.isIn(c, new Boundary(1664, 4224, 1727, 4287))) {
            return;
        }
        if (c.getInstance() != null) {
            return;
        }

        if (c.teleTimer > 0) {
            return;
        }
        PresetManager.getSingleton().loadLastPreset(c);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Pulls previous preset (Need to rename)");
    }
}
