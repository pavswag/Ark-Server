package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.npc.drops.DropManager;
import io.kyros.model.entity.player.Player;

public class Droprate extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        double dr = DropManager.getModifier1(player);

        if (dr > 75) {
            player.forcedChat("My drop rate bonus is : 75%.");
        } else {
            player.forcedChat("My drop rate bonus is : " + DropManager.getModifier1(player) + "%.");
        }
    }
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Shows drop rate bonus");
	}
}


