package io.kyros.content.commands.all;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class hween extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        //if (!c.halloweenGlobal) {
        //    c.sendMessage("@red@You've not unlocked the ability to kill this boss yet!!");
        //    c.sendMessage("@red@Kill Flying Pumpkin's for a chance to unlock the global boss!");
        //    return;
        //}
        if (Server.getMultiplayerSessionListener().inAnySession(c)) {
            return;
        }
        if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
            c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
            return;
        }
        if (c.getPosition().inWild()) {
            return;
        }
        c.getPA().spellTeleport(2347, 3687, 0, false);
    }
}
