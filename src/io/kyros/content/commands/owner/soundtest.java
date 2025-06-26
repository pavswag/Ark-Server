package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.SoundType;
import io.kyros.model.entity.player.Player;

public class soundtest extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");
        player.getPA().sendSound(Integer.parseInt(args[0]), SoundType.SOUND, null);

    }
}
