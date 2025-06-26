package io.kyros.content.commands.admin;

import io.kyros.content.commands.Command;
import io.kyros.content.hotdrops.HotDrops;
import io.kyros.model.entity.player.Player;

public class StartHotDrop extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split("-");
        if (args.length < 1) {
            player.sendMessage("Syntax: ::starthotdrop-npcid");
            return;
        }
        int npcid = Integer.parseInt(args[0]);
        HotDrops.handleHotDrop(npcid, false);
    }
}
