package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Death extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
/*        for (int i = 0; i < 245; i++) {
            player.getPA().itemOnInterface(994, 50+i,36150, i);
        }
        player.getPA().sendString(36006, "@red@(LOCKED)");
        player.getPA().showInterface(36000);

        player.getDeathMain().addItem(new GameItem(995, 2_000_000));*/

//        player.getDeathMain().addItem(new GameItem(995, 2_000_000));

/*        for (int i = 0; i < player.getDeathStorage().toArray().length; i++) {
            player.getPA().itemOnInterface(player.getDeathStorage().get(i), 36150, i);
        }

        player.getPA().sendString(36006, "@red@(LOCKED)");
        player.getPA().showInterface(36000);*/

        player.getDeathInterface().drawInterface(false);
    }
}
