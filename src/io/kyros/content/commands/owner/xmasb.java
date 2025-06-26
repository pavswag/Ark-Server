package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.seasons.ChristmasBoss;
import io.kyros.model.entity.player.Player;

/**
* @project arkcane-server
* @author ArkCane
* @social Discord: ArkCane
* Website: www.arkcane.net
 * @since 26/11/2023
 */
public class xmasb extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        ChristmasBoss.initBoss();
    }
}
