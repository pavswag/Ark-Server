package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.deals.TimeOffers;
import io.kyros.model.entity.player.Player;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 08/03/2024
 */
public class ReloadOffers extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        TimeOffers.forceReloadOffers(player);
    }
}
