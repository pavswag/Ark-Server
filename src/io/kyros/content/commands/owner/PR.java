package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.games.PartyRoom;
import io.kyros.model.entity.player.Player;

public class PR extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        new PartyRoom().run();
    }
}