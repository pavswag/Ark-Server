package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.npchandling.ForcedChat;
import io.kyros.model.entity.player.Player;

public class crier extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {

        ForcedChat.saveTownCrier(input);
        player.sendErrorMessage("You have adjusted the chat message for the Town Crier!");
        ForcedChat.loadChats();
    }
}
