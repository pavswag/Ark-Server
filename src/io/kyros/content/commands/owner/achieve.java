package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class achieve extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
/*        //Name = 54799+7
        //Percent Bar = 54800+7
        //45/100 = 54801+7
        //Claim button = 54802+7
        player.getPA().itemOnInterface(6299,1,54800,0);
        player.getPA().itemOnInterface(6299,1,54800,1);
        player.getPA().itemOnInterface(6299,1,54800,2);
        player.getPA().itemOnInterface(6299,1,54800,3);

        player.getPA().showInterface(54760);*/

        for (int i = 0; i < 10; i++) {
            player.getPA().itemOnInterface(14572, 1, 23004, i);
        }

        player.getPA().showInterface(22999);
    }
}
