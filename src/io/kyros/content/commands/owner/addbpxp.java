package io.kyros.content.commands.owner;

import io.kyros.content.battlepass.Pass;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;

import java.util.Optional;

public class addbpxp extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split("-");
        String playerName = args[0];
        Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);
        if (optionalPlayer.isPresent()) {
            Player p = optionalPlayer.get();
            Pass.addExperience(p, Integer.parseInt(args[1]));
        } else {
            player.sendMessage(playerName + " is not online.");
        }
    }
}
