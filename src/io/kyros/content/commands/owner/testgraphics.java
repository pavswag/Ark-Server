package io.kyros.content.commands.owner;

import io.kyros.content.battlepass.Pass;
import io.kyros.content.commands.Command;
import io.kyros.model.Graphic;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.Misc;

import java.util.Optional;

public class testgraphics extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        for(int i = 0; i < 525; i++)
            player.startGraphic(new Graphic(Misc.random(1, 1500)));
    }
}
