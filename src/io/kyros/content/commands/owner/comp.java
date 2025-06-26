package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class comp extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getCompletionistCapeRe().sendColours();
        player.getPA().showInterface(59960);
    }
}
