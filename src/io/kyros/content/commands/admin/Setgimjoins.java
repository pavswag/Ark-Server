package io.kyros.content.commands.admin;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.mode.group.GroupIronmanGroup;
import io.kyros.model.entity.player.mode.group.GroupIronmanRepository;

public class Setgimjoins extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split("-");
        if (args.length < 1) {
            player.sendMessage("Syntax: ::setgimjoins-player name here-5");
            return;
        }
        String playerName = args[0];
        int count = Integer.parseInt(args[1]);
        if (count < 0)
            count = 0;

        Player p = PlayerHandler.getPlayerByDisplayName(playerName);
        
        if (p != null) {
            GroupIronmanGroup group = GroupIronmanRepository.getGroupForOnline(p).orElse(null);
            if (group == null) {
                player.sendMessage("That player isn't in a group.");
                return;
            }
            group.setJoined(count);
            player.sendMessage("Set group " + group.getName() + " joins to " + count + ".");
        } else {
            player.sendMessage("No such player exists.");
        }
    }

}
