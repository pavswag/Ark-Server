package io.kyros.content.commands.donator;

import io.kyros.Server;
import io.kyros.content.bosses.nightmare.NightmareConstants;
import io.kyros.content.bosses.sharathteerk.SharathteerkInstance;
import io.kyros.content.commands.Command;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;

public class Bank extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.isBoundaryRestricted()) {
            return;
        }



        if (c.teleTimer > 0) {
            return;
        }

        if (c.getY() >= 3520 && c.getY() <= 3525) {
            return;
        }

        if (c.amDonated >= 100 || c.getRights().isOrInherits(Right.MODERATOR)) {
            if (c.wildLevel <= 0) {
                c.getPA().c.itemAssistant.openUpBank();
                c.inBank = true;
            }
        }
    }
}
