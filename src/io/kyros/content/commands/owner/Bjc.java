package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.games.blackjack.BJManager;
import io.kyros.model.entity.player.Player;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 10/04/2024
 */
public class Bjc extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
//        player.queue(() -> {
            player.setBjManager(new BJManager(player));
            player.getBjManager().open();
//        });//just to test
    }
}
