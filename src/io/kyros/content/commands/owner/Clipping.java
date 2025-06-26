package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.entity.player.Player;

/**
 * @author Arthur Behesnilian 7:14 PM
 */
public class Clipping extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        int clipping = player.getRegionProvider().getClipping(player.getX(), player.getY(), player.getHeight()) - RegionProvider.NPC_TILE_FLAG;
        player.sendMessage("Clipping for tile: " + player.getPosition() + "="+ clipping);
    }
}
