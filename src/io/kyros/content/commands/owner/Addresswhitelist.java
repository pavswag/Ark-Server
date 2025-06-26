package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.net.login.RS2LoginProtocol;

public class Addresswhitelist extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        RS2LoginProtocol.ADDRESS_WHITELIST.add(input);
        player.sendMessage("Add character to address whitelist: " + input);
    }
}
