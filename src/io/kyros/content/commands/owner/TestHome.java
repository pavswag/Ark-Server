package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.instance.Pallet;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;

public class TestHome extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Pallet.Companion.copyMap(new Position(1863, 5711)).sendFor(player);
        //do i need to reg this ? nope
    }
}
