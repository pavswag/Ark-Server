package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class afkstore extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.setInTradingPost(false);
        player.getShops().openShop(195);
    }
}
