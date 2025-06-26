package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;

import java.util.Optional;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 19/03/2024
 */
public class SetCent extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] args = input.split("-");
            String playerName = args[0];
            int rank = Integer.parseInt(args[1]);
            if (rank == 0) {
                rank = -1;
            }

            Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);
            if (optionalPlayer.isPresent()) {
                Player c2 = optionalPlayer.get();
                c2.centurion = rank;
                c2.sendErrorMessage("Make sure to relog!!");
                c2.sendErrorMessage("Make sure to relog!!");
                c2.sendErrorMessage("Make sure to relog!!");
                c2.sendErrorMessage("Make sure to relog!!");
                c2.sendErrorMessage("Make sure to relog!!");
            } else {
                player.sendMessage("Player is not online!");
            }

        } catch (Exception e) {
            player.sendMessage("Error. Correct syntax: ::setcent-player-rankid");
        }
    }
}
