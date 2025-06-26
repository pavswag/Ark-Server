package io.kyros.content.commands.admin;

import io.kyros.content.commands.Command;
import io.kyros.content.polls.PollTab;
import io.kyros.model.entity.player.Player;

/**
 * @author Grant_ | www.rune-server.ee/members/grant_ | 2/10/20
 */
public class Endpoll extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        PollTab.endPollManually();
        player.sendMessage("You have ended the current poll.");
        PollTab.updateInterface(player);
    }
}
