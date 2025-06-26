package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.CosmeticOverride;
import io.kyros.model.entity.player.Player;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 15/02/2024
 */
public class cosmetics extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        CosmeticOverride.openInterface(player);
    }
}
