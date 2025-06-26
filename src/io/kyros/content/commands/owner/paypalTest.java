package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.sql.ingamestore.StoreInterface;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 12/12/2023
 */
public class paypalTest extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        StoreInterface.openInterface(player);
    }
}
